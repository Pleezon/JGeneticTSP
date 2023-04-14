package de.jgenetictsp;

import java.util.Arrays;
import java.util.Random;

/**
 * description missing.
 */
public class TSP
{

	public NodeInfo currentBest;
	private int currentGeneration;

	public int getCurrentGeneration()
	{
		return currentGeneration;
	}

	private final TSPNode[] nodes;
	private int[] best;
	private Random r;
	private int[][] distances;
	private int POPULATION_SIZE;
	private int[][] population;
	private double[] roulette;
	private long mutationTimes;
	private double CROSSOVER_PROBABILITY;
	private double MUTATION_PROBABILITY;
	private int UNCHANGED_GENES;
	Integer bestValue;
	int[] values;

	public long getMutationTimes()
	{
		return mutationTimes;
	}

	public int getUNCHANGED_GENES()
	{
		return UNCHANGED_GENES;
	}

	public TSPNode[] getBest()
	{
		return Arrays.stream(best).mapToObj(b -> nodes[b]).toArray(TSPNode[]::new);
	}

	public TSP(TSPNode[] nodes, int POPULATION_SIZE, double CROSSOVER_PROBABILITY, double MUTATION_PROBABILITY)
	{
		this.POPULATION_SIZE = POPULATION_SIZE;
		this.CROSSOVER_PROBABILITY = CROSSOVER_PROBABILITY;
		this.MUTATION_PROBABILITY = MUTATION_PROBABILITY;
		this.nodes = nodes;
		shuffleArray(this.nodes);
		r = new Random();
		UNCHANGED_GENES = 0;
		mutationTimes = 0;
		bestValue = null;
		best = new int[nodes.length];
		currentGeneration = 0;
		currentBest = null;
		population = new int[POPULATION_SIZE][nodes.length];
		values = new int[POPULATION_SIZE];
		roulette = new double[population.length];
		init();
	}

	public TSP(TSPNode[] nodes)
	{
		this(nodes, 30, 0.9, 0.01);
	}

	private static int indexOf(int[] array, int needle, int length)
	{
		for (int i = 0; i < length; i++)
			if (array[i] == needle) return i;
		return -1;
	}

	private static void remove(int[] array, int index, int length)
	{
		System.arraycopy(array, index + 1, array, index, length - 1 - index);
	}

	private void init()
	{
		countDistances();
		for (var i = 0; i < POPULATION_SIZE; i++) {
			population[i] = randomIndividual(nodes.length);
		}
		setBestValue();
	}

	public void nextGeneration()
	{
		currentGeneration++;
		selection();
		crossover();
		mutation();
		setBestValue();
	}

	private void selection()
	{
		var parents = new int[POPULATION_SIZE][nodes.length];
		var initnum = 4;
		parents[0] = population[currentBest.index];
		parents[1] = doMutate(best.clone());
		parents[2] = pushMutate(best.clone());
		parents[3] = best.clone();

		setRoulette();
		for (var i = initnum; i < POPULATION_SIZE; i++) {
			parents[i] = population[wheelOut(r.nextDouble())];
		}
		population = parents;
	}

	private void crossover()
	{
		int len = 0;
		int[] queue = new int[POPULATION_SIZE];
		for (int i = 0; i < POPULATION_SIZE; i++) {
			if (r.nextDouble() < CROSSOVER_PROBABILITY) {
				queue[len] = i;
				len++;
			}
		}
		for (int i = 0; i < len; i += 2) {
			int index = r.nextInt(len);
			int secondIndex = r.nextInt(len - 1);
			if (secondIndex == index) secondIndex++;
			swap(queue, index, len - 1);
			swap(queue, secondIndex, len - 2);
			len -= 2;
			doCrossover(queue[index], queue[secondIndex]);
		}
	}

	private void doCrossover(int x, int y)
	{
		int[] child1 = getNextChild(x, y);
		int[] child2 = getPreviousChild(x, y);
		population[x] = child1;
		population[y] = child2;
	}

	private int[] getPreviousChild(int x, int y)
	{
		int[] solution = new int[population[0].length];
		int[] px = Arrays.copyOf(population[x], population[x].length);
		int[] py = Arrays.copyOf(population[y], population[y].length);
		int dx, dy;
		int pLen = px.length;
		int ix = randomNumber(pLen);
		int c = px[ix];
		int iy = indexOf(py, c, pLen);
		solution[0] = c;
		int index = 1;
		while (pLen > 1) {
			dx = (ix == 0) ? px[pLen - 1] : px[ix - 1];
			dy = (iy == 0) ? py[pLen - 1] : py[iy - 1];
			if (ix < pLen - 1) remove(px, ix, pLen);
			if (iy < pLen - 1) remove(py, iy, pLen);
			if (distances[c][dx] < distances[c][dy]) {
				c = dx;
				if (ix == 0) ix = pLen - 1;
				ix--;
				iy = indexOf(py, c, pLen);
			} else {
				c = dy;
				if (iy == 0) iy = pLen - 1;
				iy--;
				ix = indexOf(px, c, pLen);
			}
			pLen--;
			solution[index] = c;
			index++;
		}
		return solution;
	}

	private int[] getNextChild(int x, int y)
	{
		int[] solution = new int[population[0].length];
		int[] px = Arrays.copyOf(population[x], population[x].length);
		int[] py = Arrays.copyOf(population[y], population[y].length);
		int dx, dy;
		int pLen = px.length;
		int ix = randomNumber(pLen);
		int c = px[ix];
		int iy = indexOf(py, c, pLen);
		solution[0] = c;
		int index = 1;
		while (pLen > 1) {
			dx = (ix + 1 == pLen) ? px[0] : px[ix + 1];
			dy = (iy + 1 == pLen) ? py[0] : py[iy + 1];
			if (ix < pLen - 1) remove(px, ix, pLen);
			if (iy < pLen - 1) remove(py, iy, pLen);
			pLen--;
			if (distances[c][dx] < distances[c][dy]) {
				c = dx;
				if (ix == pLen) ix = 0;
				iy = indexOf(py, c, pLen);
			} else {
				c = dy;
				if (iy == pLen) iy = 0;
				ix = indexOf(px, c, pLen);
			}
			solution[index] = c;
			index++;
		}
		return solution;

	}

	private void mutation()
	{
		for (var i = 0; i < POPULATION_SIZE; i++) {
			if (r.nextDouble() < MUTATION_PROBABILITY) {
				if (r.nextBoolean()) {
					population[i] = pushMutate(population[i]);
				} else {
					population[i] = doMutate(population[i]);
				}
				i--;
			}
		}
	}

	private int randomNumber(int bound)
	{
		return r.nextInt(bound);
	}

	private int[] doMutate(int[] seq)
	{
		mutationTimes++;
		int n = r.nextInt(seq.length - 1) + 1;
		int m = r.nextInt(Math.min(seq.length >> 1, n));
		for (int i = 0, j = (n - m + 1) >> 1; i < j; i++) {
			swap(seq, m + i, n - i);
		}
		return seq;
	}

	private int[] pushMutate(int[] seq)
	{
		int[] seq_ = Arrays.copyOf(seq, seq.length);
		mutationTimes++;
		int n = r.nextInt(seq.length - 1) + 1;
		int m = r.nextInt(Math.min(seq.length >> 1, n));
		int[] s1 = Arrays.copyOf(seq, m);
		System.arraycopy(seq_, m, seq_, 0, n - m);
		System.arraycopy(s1, 0, seq_, n - m, m);
		return seq_;
	}

	private void setBestValue()
	{
		for (var i = 0; i < population.length; i++) {
			values[i] = evaluate(population[i]);
		}
		currentBest = getCurrentBest();
		if (bestValue == null || bestValue > currentBest.value) {
			best = population[currentBest.index].clone();
			bestValue = currentBest.value;
			UNCHANGED_GENES = 0;
		} else {
			UNCHANGED_GENES += 1;
		}
	}

	private NodeInfo getCurrentBest()
	{
		int bestIndex = 0;
		int currentBestValue = values[0];
		for (var i = 1; i < population.length; i++) {
			if (values[i] < currentBestValue) {
				currentBestValue = values[i];
				bestIndex = i;
			}
		}
		return new NodeInfo(bestIndex, currentBestValue);

	}

	private void setRoulette()
	{
		int sum = 0;
		for (int value : values) {
			sum += value;
		}
		roulette[0] = sum / (float) values[0];
		for (var i = 1; i < roulette.length; i++) {
			roulette[i] = sum / (float) values[i];
			roulette[i] += roulette[i - 1];
		}
	}

	private int wheelOut(double rand)
	{
		for (int i = 0; i < roulette.length; i++) {
			if (rand <= roulette[i]) {
				return i;
			}
		}
		throw new RuntimeException("Something went terribly wrong...");
	}

	private int[] randomIndividual(int n)
	{
		int[] ar = new int[n];
		for (var i = 0; i < ar.length; i++) {
			ar[i] = i;
		}
		shuffleArray(ar);
		return ar;
	}

	private void shuffleArray(int[] array)
	{
		int index;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			if (index != i) {
				array[index] ^= array[i];
				array[i] ^= array[index];
				array[index] ^= array[i];
			}
		}
	}

	private <T> void shuffleArray(T[] array)
	{
		int index;
		T temp;
		Random random = new Random();
		for (int i = array.length - 1; i > 0; i--) {
			index = random.nextInt(i + 1);
			temp = array[index];
			array[index] = array[i];
			array[i] = temp;
		}
	}

	private void swap(int[] array, int i, int j)
	{
		array[j] ^= array[i];
		array[i] ^= array[j];
		array[j] ^= array[i];
	}

	public double getCostOfBest()
	{
		return evaluate(best);
	}

	private int evaluate(int[] individual)
	{
		int sum = 0;
		for (int i = 1; i < individual.length; i++) {
			sum += distances[individual[i]][individual[i - 1]];
		}
		return sum;
	}

	private void countDistances()
	{
		int n = nodes.length;
		distances = new int[n][n];
		for (int i = 0; i < n; i++) {
			for (int j = 0; j < n; j++) {
				distances[i][j] = nodes[i].getCost(nodes[j]);
			}
		}
	}

	public record NodeInfo(int index, Integer value)
	{

	}
}

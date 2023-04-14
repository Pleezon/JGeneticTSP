package examples;

import de.jgenetictsp.TSP;
import de.jgenetictsp.TSPNode;
import de.jgenetictsp.presets.EuclideanTSPNode;
import de.jgenetictsp.presets.Pos2D;

/**
 * description missing.
 */
public class Example
{
	public static void main(String[] args)
	{
		EuclideanTSPNode[] nodes = new EuclideanTSPNode[]{
				new EuclideanTSPNode(new Pos2D(0, 50)),
				new EuclideanTSPNode(new Pos2D(32, 30)),
				new EuclideanTSPNode(new Pos2D(1, 52)),
				new EuclideanTSPNode(new Pos2D(-22, 13)),
				new EuclideanTSPNode(new Pos2D(-1, -31)),
		};
		TSP tsp = new TSP(nodes);
		for (int i=0; i<100; i++){
			tsp.nextGeneration();
		}
		TSPNode[] finished = tsp.getBest();
		double cost = tsp.getCostOfBest();
	}
}

# JGeneticTSP
Genetic-Algorithm for solving Travelling Salesman Problems. 

## Usage
```java
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
```

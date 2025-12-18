# JGeneticTSP
Somewhat optimized Genetic-Algorithm for solving Travelling Salesman Problems. 

## Usage
```java
	EuclideanTSPNode[] nodes = new EuclideanTSPNode[]{ // add wanted nodes to list
				new EuclideanTSPNode(new Pos2D(0, 50)),
				new EuclideanTSPNode(new Pos2D(32, 30)),
				new EuclideanTSPNode(new Pos2D(1, 52)),
				new EuclideanTSPNode(new Pos2D(-22, 13)),
				new EuclideanTSPNode(new Pos2D(-1, -31)),
		};
		TSP tsp = new TSP(nodes); // create TSP instance
		for (int i=0; i<100; i++){
			tsp.nextGeneration(); 
// repeat (this is normally done in combination with a check wether or not the current best is in a wanted range)
		}
		TSPNode[] finished = tsp.getBest();  // get result
		double cost = tsp.getCostOfBest(); // get cost of TSP path
```

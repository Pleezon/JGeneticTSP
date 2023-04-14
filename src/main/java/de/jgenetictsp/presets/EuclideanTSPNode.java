package de.jgenetictsp.presets;

import de.jgenetictsp.TSPNode;

/**
 * description missing.
 */
public record EuclideanTSPNode(Pos2D pos) implements TSPNode
{
	public int getCost(Object other)
	{
		if(other instanceof EuclideanTSPNode euclideanTSPNode){
			double d = Math.sqrt(pos.distSquared(euclideanTSPNode.pos));
			return (int) (d * 100.0);
		}
		throw new RuntimeException("Tried to compare nodes of different type.");
	}
}

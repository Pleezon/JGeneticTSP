package de.jgenetictsp.presets;

import jdk.incubator.vector.IntVector;
import jdk.incubator.vector.VectorOperators;

/**
 * description missing.
 */
public record Pos2D(IntVector vector)
{

	public Pos2D(int x, int z)
	{
		this(IntVector.fromArray(IntVector.SPECIES_64, new int[]{x, z}, 0));
	}

	public int getX()
	{
		return vector.lane(0);
	}

	public int getZ()
	{
		return vector.lane(1);
	}

	public Pos2D add(Pos2D other)
	{
		return new Pos2D(vector.add(other.vector));
	}

	public Pos2D sub(Pos2D other)
	{
		return new Pos2D(vector.sub(other.vector));
	}

	public float distSquared(Pos2D other)
	{
		var delta = vector.sub(other.vector);
		return delta.mul(delta).reduceLanes(VectorOperators.ADD);
	}

	public Pos2D maxDist(Pos2D origin, Pos2D other)
	{
		if (origin.distSquared(other) > origin.distSquared(this)) return other;
		return this;
	}

	public Pos2D minDist(Pos2D origin, Pos2D other)
	{
		if (origin.distSquared(other) < origin.distSquared(this)) return other;
		return this;
	}

	@Override
	public int hashCode()
	{
		return vector.hashCode();
	}

	public long asLong()
	{
		return ((long) vector().lane(0) << 32) | (vector.lane(1));
	}
}

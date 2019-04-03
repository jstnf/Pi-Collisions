package com.jstnf.pi;

public class Cube
{
	public double x, y, v, m;
	public int width;

	public Cube(double x, double y, int width, double m, double v)
	{
		this.x = x;
		this.y = y;
		this.width = width;
		this.m = m;
		this.v = v;
	}

	public boolean isColliding(Cube other)
	{
		return other.x + other.width >= x;
	}

	public void move()
	{
		x += v;
	}

	public void reverseDir()
	{
		v *= -1;
	}

	public void doCollide(Cube other)
	{
		double tempThisV = v;
		double tempOtherV = other.v;

		v = ((m - other.m) / (m + other.m)) * tempThisV + ((2 * other.m) / (m + other.m)) * tempOtherV;
		other.v = ((2 * m) / (m + other.m)) * tempThisV + ((other.m - m) / (m + other.m)) * tempOtherV;
	}
}
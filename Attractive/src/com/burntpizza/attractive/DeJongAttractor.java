package com.burntpizza.attractive;
import java.awt.geom.Point2D;

public class DeJongAttractor {
	
	public final double a, b, c, d;
	public double x, y, px, py;
	
	public DeJongAttractor(double a, double b, double c, double d) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
	}
	
	public void update() {
		px = x;
		py = y;
		x = Math.sin(a * y) - Math.cos(b * x);
		y = Math.sin(c * x) - Math.cos(d * y);
	}
	
	public static DeJongAttractor rand() {
		final double range = 4;
		while (true) {
			DeJongAttractor candidate = new DeJongAttractor(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
			Point2D.Double lE = lyapunovExp(candidate);
			if (lE.x > 0 && lE.y > 0)
				return candidate;
		}
	}
	
	private static Point2D.Double lyapunovExp(DeJongAttractor a) {
		Point2D.Double p = new Point2D.Double();
		double x = a.x;
		double y = a.y;
		
		for (int i = 0; i < 1000; i++) {
			a.update();
			p.x += Math.log(Math.abs(a.b * Math.sin(a.b * a.x)));
			p.y += Math.log(Math.abs(a.d * Math.sin(a.d * a.y)));
		}
		
		a.x = x;
		a.y = y;
		p.x /= 1000;
		p.y /= 1000;
		return p;
	}
}
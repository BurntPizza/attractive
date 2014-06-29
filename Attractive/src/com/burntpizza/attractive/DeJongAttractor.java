/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2014
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
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
			final DeJongAttractor candidate = new DeJongAttractor(Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2, Math.random() * range - range / 2);
			final Point2D.Double lE = lyapunovExp(candidate);
			if (lE.x > 0 && lE.y > 0)
				return candidate;
		}
	}
	
	private static Point2D.Double lyapunovExp(DeJongAttractor a) {
		final Point2D.Double p = new Point2D.Double();
		final double x = a.x;
		final double y = a.y;
		
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
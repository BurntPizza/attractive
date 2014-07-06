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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class RenderJob {
	
	private final DeJongAttractor current;
	private final int w, h, iterations, number;
	
	public RenderJob(int width, int height, double a, double b, double c, double d, int iterations, int jobNumber) {
		current = new DeJongAttractor(a, b, c, d);
		w = width;
		h = height;
		this.iterations = iterations;
		number = jobNumber;
	}
	
	public RenderJob(int width, int height, int iterations, DeJongAttractor a, int jobNumber) {
		this(width, height, a.a, a.b, a.c, a.d, iterations, jobNumber);
	}
	
	public Frame render(BufferedImage buffer) {
		final int[] data = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
		
		final int w4 = w / 4;
		final int h4 = h / 4;
		
		for (int i = 0; i < iterations; i++) {
			current.update();
			final double dx = current.x - current.px;
			final double dy = current.y - current.py;
			final int dist = (int) ((dx * dx + dy * dy) * 150) + 50;
			final int rr = Math.min(255, dist);
			final int gr = Math.min(255, dist >> 1);
			final int br = Math.min(255, dist >> 2);
			final int x = (int) Math.round(current.x * (w4 - 10)) + w4 * 2;
			final int y = (int) Math.round(current.y * (h4 - 10)) + h4 * 2;
			final int index = x + y * buffer.getWidth();
			final int prev = data[index];
			final int red = Math.min(255, ((prev & 0xFF0000) >> 16) + (rr >> 4));
			final int green = Math.min(255, ((prev & 0xFF00) >> 8) + (gr >> 4));
			final int blue = Math.min(255, (prev & 0xFF) + (br >> 4));
			data[index] = red << 16 | green << 8 | blue;
		}
		return new Frame(buffer, number);
	}
}
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

import java.util.concurrent.atomic.AtomicInteger;

public class JobFactory {
	
	public final int width, height, iterations;
	private final double[] init, inc;
	private final AtomicInteger counter = new AtomicInteger();
	
	public JobFactory(int imageWidth, int imageHeight, DeJongAttractor dja, int iterations, double ai, double bi, double ci, double di) {
		width = imageWidth;
		height = imageHeight;
		this.iterations = iterations;
		init = new double[4];
		inc = new double[4];
		init[0] = dja.a;
		inc[0] = ai;
		init[1] = dja.b;
		inc[1] = bi;
		init[2] = dja.c;
		inc[2] = ci;
		init[3] = dja.d;
		inc[3] = di;
	}
	
	public void rand() {
		synchronized (init) {
			final DeJongAttractor dja = DeJongAttractor.rand();
			init[0] = dja.a;
			init[1] = dja.b;
			init[2] = dja.c;
			init[3] = dja.d;
		}
	}
	
	public RenderJob getNextJob() {
		synchronized (init) {
			for (int i = 0; i < init.length; i++)
				init[i] += inc[i];
		}
		return new RenderJob(width, height, init[0], init[1], init[2], init[3], iterations, counter.getAndIncrement());
	}
}

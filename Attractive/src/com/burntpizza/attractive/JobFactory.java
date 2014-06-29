package com.burntpizza.attractive;
import java.util.concurrent.atomic.AtomicInteger;

public class JobFactory {
	
	public final int width, height, iterations;
	private double[] init, inc;
	private AtomicInteger counter = new AtomicInteger();
	
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
			DeJongAttractor dja = DeJongAttractor.rand();
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
			return new RenderJob(width, height, init[0], init[1], init[2], init[3], iterations, counter.getAndIncrement());
		}
	}
}

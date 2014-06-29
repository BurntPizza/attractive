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
		int[] data = ((DataBufferInt) buffer.getRaster().getDataBuffer()).getData();
		
		final int w4 = w / 4;
		final int h4 = h / 4;
		
		for (int i = 0; i < iterations; i++) {
			current.update();
			double dx = current.x - current.px;
			double dy = current.y - current.py;
			double dist = dx * dx + dy * dy;
			dist *= 150;
			dist += 50;
			int rr = (int) Math.min(255, dist);
			dist /= 2;
			int gr = (int) Math.min(255, dist);
			dist /= 2;
			int br = (int) Math.min(255, dist);
			final int x = (int) Math.round(current.x * (w4 - 10)) + w4 * 2;
			final int y = (int) Math.round(current.y * (h4 - 10)) + h4 * 2;
			final int index = x + y * buffer.getWidth();
			final int prev = data[index];
			int red = prev & 0xFF0000;
			int green = prev & 0xFF00;
			int blue = prev & 0xFF;
			red >>= 16;
			green >>= 8;
			red = Math.min(255, red + (rr >> 4));
			green = Math.min(255, green + (gr >> 4));
			blue = Math.min(255, blue + (br >> 4));
			data[index] = red << 16 | green << 8 | blue;
		}
		return new Frame(buffer, number);
	}
}
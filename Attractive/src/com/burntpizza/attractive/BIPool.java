package com.burntpizza.attractive;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.Arrays;
import java.util.Stack;

public class BIPool {
	
	private int w, h;
	private Stack<BufferedImage> storage = new Stack<>();
	
	public BIPool(int width, int height) {
		w = width;
		h = height;
	}
	
	public BufferedImage get() {
		if (!storage.isEmpty()) {
			BufferedImage i = storage.pop();
			int[] data = ((DataBufferInt) i.getRaster().getDataBuffer()).getData();
			Arrays.fill(data, Color.BLACK.getRGB());
			return i;
		}
		return new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
	}
	
	public void put(BufferedImage img) {
		storage.push(img);
	}
	
	public int size() {
		return storage.size();
	}
}

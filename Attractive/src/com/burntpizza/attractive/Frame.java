package com.burntpizza.attractive;
import java.awt.image.BufferedImage;

class Frame {
	public final BufferedImage image;
	public final int number;
	
	public Frame(BufferedImage img, int n) {
		image = img;
		number = n;
	}
	
	public static enum Comparator implements java.util.Comparator<Frame> {
		INSTANCE;
		
		@Override
		public int compare(Frame o1, Frame o2) {
			return Integer.compare(o1.number, o2.number);
		}
		
	}
}

package com.burntpizza.attractive;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
	
	public static final int WIDTH = 512, HEIGHT = 512;
	public static final int ITERATIONS_PER_FRAME = 600_000;
	public static final double AIncrement = 0.001;
	public static final double BIncrement = 0.001;
	public static final double CIncrement = 0;
	public static final double DIncrement = 0;
	
	public static void main(String[] a) {
		final JobFactory factory = new JobFactory(WIDTH, HEIGHT, DeJongAttractor.rand(), ITERATIONS_PER_FRAME, AIncrement, BIncrement, CIncrement, DIncrement);
		RenderFrame frame = new RenderFrame(new WorkManager(factory));
		
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// note: it's normal that this takes a second or two to register, that's the change making it's way through the queue
				factory.rand();
			}
		});
	}
}

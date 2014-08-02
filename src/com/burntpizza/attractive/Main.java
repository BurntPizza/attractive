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

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Main {
	
	public static final int WIDTH = 1920, HEIGHT = 1080;
	public static final int ITERATIONS_PER_FRAME = 600_000;
	public static final int FRAMES_PER_SECOND = 30;
	public static final double AIncrement = 0.001;
	public static final double BIncrement = 0.001;
	public static final double CIncrement = 0;
	public static final double DIncrement = 0;
	
	public static void main(String[] a) {
		final JobFactory factory = new JobFactory(WIDTH, HEIGHT, DeJongAttractor.rand(), ITERATIONS_PER_FRAME, AIncrement, BIncrement, CIncrement, DIncrement);
		final RenderFrame frame = new RenderFrame(new WorkManager(factory), FRAMES_PER_SECOND);
		
		frame.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				// note: it's normal that this takes a second or two to register, that's the change making it's way through the queue
				factory.rand();
			}
		});
	}
}

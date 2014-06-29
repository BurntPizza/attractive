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

import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.*;

@SuppressWarnings("serial")
public class RenderFrame extends JFrame implements Runnable {
	
	private final JComponent canvas;
	private BufferedImage currentFrame;
	private final WorkManager manager;
	
	public RenderFrame(final WorkManager manager) {
		this.manager = manager;
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas = new JComponent() {
			{
				setSize(manager.factory.width, manager.factory.height);
				setPreferredSize(getSize());
			}
			
			@Override
			public void paint(Graphics g) {
				if (currentFrame != null)
					g.drawImage(currentFrame, 0, 0, getWidth(), getHeight(), null);
			}
		};
		add(canvas);
		pack();
		final Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(ss.width / 2 - getWidth() / 2, ss.height / 2 - getHeight() / 2);
		setVisible(true);
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		final double sleep = 50 / 1000.0;
		
		while (true) {
			final long time = System.nanoTime();
			currentFrame = manager.getNextFrame();
			canvas.paintImmediately(0, 0, canvas.getWidth(), canvas.getHeight());
			manager.returnImage(currentFrame);
			
			setTitle("Queue: " + manager.getStatus().queueSize + " Sleep: " + sleep);
			try {
				while (System.nanoTime() - time < sleep * 1000000000) {
					Thread.sleep(1);
				}
			} catch (final InterruptedException e) {
			}
		}
	}
}

package com.burntpizza.attractive;
import java.awt.*;
import java.awt.image.BufferedImage;

import javax.swing.JComponent;
import javax.swing.JFrame;

@SuppressWarnings("serial")
public class RenderFrame extends JFrame implements Runnable {
	
	private JComponent canvas;
	private BufferedImage currentFrame;
	private WorkManager manager;
	
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
		Dimension ss = Toolkit.getDefaultToolkit().getScreenSize();
		setLocation(ss.width / 2 - getWidth() / 2, ss.height / 2 - getHeight() / 2);
		setVisible(true);
		
		new Thread(this).start();
	}
	
	@Override
	public void run() {
		double sleep = 50 / 1000.0;
		
		while (true) {
			long time = System.nanoTime();
			currentFrame = manager.getNextFrame();
			canvas.paintImmediately(0, 0, canvas.getWidth(), canvas.getHeight());
			manager.returnImage(currentFrame);
			
			setTitle("Queue: " + manager.getStatus().queueSize + " Sleep: " + sleep);
			try {
				while (System.nanoTime() - time < sleep * 1000000000) {
					Thread.sleep(1);
				}
			} catch (InterruptedException e) {
			}
		}
	}
}

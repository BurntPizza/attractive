package com.burntpizza.attractive;
/*
 * Copyright 2013 HeroesGrave and other Paint.JAVA developers.
 * 
 * This file is part of Paint.JAVA
 * 
 * Paint.JAVA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>
 */

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JDialog;

/**
 * GUI readout for nano-profiling.
 * Create a meter dialog and call tick() in your code to monitor it.
 * A LagMeter should only be used within one thread, multiple LagMeters
 * can be running at a time however.
 * 
 * @author BurntPizza
 * 
 */
@SuppressWarnings("serial")
public class LagMeter {
	
	public static final int DEFAULT_THRESHOLD_LOW = 30;
	public static final int DEFAULT_THRESHOLD_HIGH = 100;
	
	public static int DIALOG_WIDTH = 400;
	
	private JDialog parent;
	private Component component;
	private boolean hasTicked;
	private long time;
	
	private int lowThresh = DEFAULT_THRESHOLD_LOW;
	private int highThresh = DEFAULT_THRESHOLD_HIGH;
	
	/**
	 * Spawns a LagMeter dialog window at location.
	 * Centers the dialog if location is null.
	 */
	public LagMeter(String title, Point location) {
		component = new Component(this);
		parent = new JDialog((Window) null, "Timing diagnostics: " + (title == null ? "" : title));
		parent.add(component);
		parent.pack();
		parent.setAlwaysOnTop(true);
		parent.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		parent.setResizable(false);
		parent.setVisible(true);
		if (location == null)
			parent.setLocationRelativeTo(null);
		else
			parent.setLocation(location);
	}
	
	/**
	 * Every 2nd call to this method will update the meter with the amount of
	 * time between the last two calls. Returns the delta, in milliseconds,
	 * or zero if beginning a measure.
	 * Example:
	 * 
	 * meter.tick();
	 * methodToBeTimed();
	 * int time = meter.tick();
	 * 
	 */
	public int tick() {
		int dt = 0;
		if (hasTicked) {
			dt = (int) ((System.nanoTime() - time) / 1000000);
			if (parent.isVisible()) {
				component.putTime(dt);
				component.repaint();
			}
		} else
			time = System.nanoTime();
		
		hasTicked ^= true;
		return dt;
	}
	
	/**
	 * Sets the low threshold, in milliseconds.
	 * Timing under this threshold will render green,
	 * those between thresholds yellow.
	 * Returns the previous low threshold.
	 */
	public synchronized int setLowThreshold(int threshold) {
		if (threshold >= highThresh)
			throw new IllegalArgumentException("Low threshold must be less than high threshold: " + threshold);
		
		int tmp = lowThresh;
		lowThresh = threshold;
		component._resize();
		return tmp;
	}
	
	/**
	 * Sets the high threshold, in milliseconds.
	 * Timing above this threshold will render red,
	 * those between thresholds yellow.
	 * Returns the previous high threshold.
	 */
	public synchronized int setHighThreshold(int threshold) {
		if (threshold <= lowThresh)
			throw new IllegalArgumentException("High threshold must be greater than low threshold: " + threshold);
		
		int tmp = highThresh;
		highThresh = threshold;
		component._resize();
		return tmp;
	}
	
	/**
	 * Hides/shows the dialog while keeping timing functional.
	 */
	public void setVisble(boolean visible) {
		parent.setVisible(visible);
	}
	
	/**
	 * Closes the meter dialog.
	 */
	public void close() {
		parent.dispose();
	}
	
	private static class Component extends JComponent {
		
		private static int barWidth = 2;
		
		private static Color lowColor = new Color(0, 200, 0);
		private static Color medColor = new Color(220, 220, 0);
		private static Color highColor = new Color(220, 0, 0);
		
		private static Color lowColorBG = new Color(192, 255, 192);
		private static Color medColorBG = new Color(255, 255, 192);
		private static Color highColorBG = new Color(255, 192, 192);
		
		private Image bg;
		private int[] times;
		private int size;
		
		private LagMeter parent;
		
		private Component(LagMeter parent) {
			this.parent = parent;
			_resize();
			
			times = new int[getWidth() / barWidth];
		}
		
		private void putTime(int time) {
			times[size++ % times.length] = time;
		}
		
		@Override
		public void paint(Graphics g) {
			int w = getWidth();
			int h = getHeight();
			
			if (bg == null)
				_resize();
			
			g.drawImage(bg, 0, 0, null);
			
			for (int i = 0; i < times.length; i++) {
				int t = times[i];
				int x = w - (i + 1) * barWidth;
				int y = h - t;
				Color c = t <= parent.lowThresh ? lowColor : t <= parent.highThresh ? medColor : highColor;
				g.setColor(c);
				g.fillRect(x, y, barWidth, t);
				if (size % times.length == i) {
					g.setColor(Color.gray);
					g.drawLine(x, 0, x, h);
				}
			}
		}
		
		private void _resize() {
			setSize(DIALOG_WIDTH, (int) (parent.highThresh * 2.25));
			setPreferredSize(getSize());
			setMinimumSize(getSize());
			setMaximumSize(getSize());
			
			bg = createImage(getWidth(), getHeight());
			if (bg == null)
				return;
			Graphics g = bg.getGraphics();
			int w = getWidth();
			int h = getHeight();
			
			int lt = DEFAULT_THRESHOLD_LOW;
			int ht = DEFAULT_THRESHOLD_HIGH;
			
			synchronized (parent) {
				lt = parent.lowThresh;
				ht = parent.highThresh;
			}
			
			int yh = h - ht;
			int yl = h - lt;
			
			g.setColor(highColorBG);
			g.fillRect(0, 0, w, yh);
			
			g.setColor(medColorBG);
			g.fillRect(0, yh, w, yl - yh);
			
			g.setColor(lowColorBG);
			g.fillRect(0, yl, w, h - yl);
			
			g.dispose();
			
			parent.parent.pack();
		}
	}
	
	/**
	 * Just for testing ;)
	 */
	public static void main(String[] a) throws InterruptedException {
		LagMeter meter = new LagMeter("testing", null);
		
		int c = 0;
		
		while (c++ < 600) {
			meter.tick();
			Thread.sleep(5 + (long) (Math.random() * 25) + (c & 5));
			//System.out.println(meter.tick());
			meter.tick();
		}
		meter.close();
	}
}
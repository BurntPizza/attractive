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

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.atomic.AtomicInteger;

public class Pool {
	
	private final int w, h;
	private final int[] blank;
	private final Deque<BufferedImage> storage = new ArrayDeque<>(64);
	
	public Pool(int width, int height) {
		w = width;
		h = height;
		blank = new int[w * h];
	}
	
	public BufferedImage get() {
		
		BufferedImage image = null;
		boolean wasEmpty = false;
		
		synchronized (storage) {
			if (storage.isEmpty())
				wasEmpty = true;
			else
				image = storage.pop();
		}
		
		if (wasEmpty)
			image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		else {
			final int[] data = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
			System.arraycopy(blank, 0, data, 0, blank.length);
		}
		
		return image;
	}
	
	public void put(BufferedImage img) {
		synchronized (storage) {
			storage.push(img);
		}
	}
	
	public int size() {
		return storage.size();
	}
}

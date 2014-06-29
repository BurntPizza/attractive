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

import java.awt.Color;
import java.awt.image.*;
import java.util.*;

public class BIPool {
	
	private final int w, h;
	private final Stack<BufferedImage> storage = new Stack<>();
	
	public BIPool(int width, int height) {
		w = width;
		h = height;
	}
	
	public BufferedImage get() {
		if (!storage.isEmpty()) {
			final BufferedImage i = storage.pop();
			final int[] data = ((DataBufferInt) i.getRaster().getDataBuffer()).getData();
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

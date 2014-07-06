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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkManager {
	
	public static final int MIN_QUEUED_JOBS = 16;
	public static final int MAX_QUEUED_JOBS = 28;
	
	private final BIPool pool;
	JobFactory factory;
	private final PriorityBlockingQueue<Frame> finishedFrames;
	private final ExecutorService exec;
	
	private final AtomicInteger finishedJobCount;
	private final AtomicInteger queuedJobCount;
	
	public WorkManager(JobFactory factory) {
		pool = new BIPool(factory.width, factory.height);
		this.factory = factory;
		finishedFrames = new PriorityBlockingQueue<>(MAX_QUEUED_JOBS, Frame.Comparator.INSTANCE);
		exec = Executors.newFixedThreadPool(Math.max(1, Runtime.getRuntime().availableProcessors() - 1));
		finishedJobCount = new AtomicInteger();
		queuedJobCount = new AtomicInteger();
	}
	
	public BufferedImage getNextFrame() {
		if (finishedJobCount.get() < MIN_QUEUED_JOBS) {
			for (int i = 0; i < MAX_QUEUED_JOBS - queuedJobCount.get(); i++, queuedJobCount.getAndIncrement())
				exec.submit(new Worker(factory.getNextJob()));
		}
		try {
			synchronized (finishedFrames) {
				final Frame f = finishedFrames.take();
				final BufferedImage i = f.image;
				finishedJobCount.getAndDecrement();
				finishedFrames.notify();
				return i;
			}
		} catch (final InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void returnImage(BufferedImage img) {
		pool.put(img);
	}
	
	public Status getStatus() {
		return new Status(finishedJobCount.get());
	}
	
	public static class Status {
		public final int queueSize;
		
		private Status(int queueSize) {
			this.queueSize = queueSize;
		}
	}
	
	private class Worker implements Runnable {
		private final RenderJob job;
		
		Worker(RenderJob job) {
			this.job = job;
		}
		
		@Override
		public void run() {
			while (finishedJobCount.get() > MAX_QUEUED_JOBS) {
				try {
					finishedFrames.wait();
				} catch (final InterruptedException e) {
					e.printStackTrace();
				}
			}
			queuedJobCount.getAndDecrement();
			finishedFrames.add(job.render(pool.get()));
			finishedJobCount.getAndIncrement();
		}
	}
}

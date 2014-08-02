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
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class WorkManager {
	
	public final int MIN_QUEUED_JOBS;
	public final int MAX_QUEUED_JOBS;
	
	private final Pool pool;
	JobFactory factory;
	private final PriorityBlockingQueue<Frame> finishedFrames;
	private final ExecutorService exec;
	
	private final AtomicInteger finishedJobCount;
	private final AtomicInteger queuedJobCount;
	
	private final Lock lock;
	private final Condition queueFull;
	
	public WorkManager(JobFactory factory) {
		final int numThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
		MIN_QUEUED_JOBS = numThreads + 1;
		MAX_QUEUED_JOBS = MIN_QUEUED_JOBS * 2;
		
		finishedFrames = new PriorityBlockingQueue<>(MAX_QUEUED_JOBS, Frame.Comparator.INSTANCE);
		exec = Executors.newFixedThreadPool(numThreads);
		pool = new Pool(factory.width, factory.height);
		finishedJobCount = new AtomicInteger();
		queuedJobCount = new AtomicInteger();
		this.factory = factory;
		lock = new ReentrantLock();
		queueFull = lock.newCondition();
	}
	
	public BufferedImage getNextFrame() {
		
		while (queuedJobCount.get() < MIN_QUEUED_JOBS) {
			exec.execute(new Worker(factory.getNextJob()));
			queuedJobCount.getAndIncrement();
		}
		
		try {
			final BufferedImage i = finishedFrames.take().image;
			finishedJobCount.getAndDecrement();
			
			lock.lock();
			try {
				queueFull.signalAll();
			} finally {
				lock.unlock();
			}
			
			return i;
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}
	
	public void returnImage(BufferedImage img) {
		pool.put(img);
	}
	
	public Status getStatus() {
		return new Status(finishedJobCount.get(), queuedJobCount.get());
	}
	
	public static class Status {
		public final int numFinished;
		public final int numQueued;
		
		private Status(int numFinished, int numQueued) {
			this.numFinished = numFinished;
			this.numQueued = numQueued;
		}
	}
	
	private class Worker implements Runnable {
		private final RenderJob job;
		
		Worker(RenderJob job) {
			this.job = job;
		}
		
		@Override
		public void run() {
			
			lock.lock();
			try {
				while (finishedJobCount.get() > MAX_QUEUED_JOBS) {
					queueFull.awaitUninterruptibly();
				}
			} finally {
				lock.unlock();
			}
			
			queuedJobCount.getAndDecrement();
			finishedFrames.add(job.render(pool.get()));
			finishedJobCount.getAndIncrement();
		}
	}
}

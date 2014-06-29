package com.burntpizza.attractive;
import java.awt.image.BufferedImage;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class WorkManager {
	
	public static final int MIN_QUEUED_JOBS = 16;
	public static final int MAX_QUEUED_JOBS = 28;
	
	private BIPool pool;
	JobFactory factory;
	private PriorityBlockingQueue<Frame> finishedFrames;
	private ExecutorService exec;
	
	private AtomicInteger finishedJobCount;
	private AtomicInteger queuedJobCount;
	
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
				Frame f = finishedFrames.take();
				BufferedImage i = f.image;
				finishedJobCount.getAndDecrement();
				finishedFrames.notify();
				return i;
			}
		} catch (InterruptedException e) {
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
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			queuedJobCount.getAndDecrement();
			finishedFrames.add(job.render(pool.get()));
			finishedJobCount.getAndIncrement();
		}
	}
}

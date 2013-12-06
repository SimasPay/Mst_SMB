package com.mfino.mce.iso.jpos.unused.task.exec;

import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.hibernate.Timestamp;
import com.mfino.mce.iso.jpos.unused.definedtasks.DeclaredTasks;
import com.mfino.mce.iso.jpos.unused.task.NetworkTask;

class TaskQueue {

	TaskQueue() {
	}

	private static Logger	                 log	  = LoggerFactory.getLogger(TaskQueue.class);

	private LinkedBlockingQueue<NetworkTask>	queue	= new LinkedBlockingQueue<NetworkTask>();

	ReentrantLock	                         lock	  = new ReentrantLock(true);

	public void addTask(NetworkTask task) {

		try {
			lock.lockInterruptibly();
		}
		catch (InterruptedException ex) {
			log.warn("could not add a task as the current thread was interrupted while waiting for the lock");
			return;
		}

		log.info("adding task " + task + " to queue");
		queue.add(task);

		lock.unlock();

	}

	/**
	 * Deletes all the tasks from the queue.
	 * 
	 * @param time
	 */
	public void clearTasks() {

		try {
			lock.lockInterruptibly();
		}
		catch (InterruptedException ex) {
			log.warn("could not clear tasks as the current thread was interrupted while waiting for the lock");
			return;
		}

		log.info("clearing all tasks from the task holder");
		queue.clear();

		lock.unlock();
	}

	/**
	 * Deletes all the tasks that were created before {@code time}.
	 * 
	 * @param time
	 */
	public void clearTasks(Timestamp time, NetworkTask task) {

		try {
			lock.lockInterruptibly();
		}
		catch (InterruptedException ex) {
			log.warn("could not clear tasks as the current thread was interrupted while waiting for the lock");
			return;
		}

		log.info("clearing all tasks that are added before the giventime={}" + time);
		for (NetworkTask nt : queue) {
			if (nt.getCreatedTime().compareTo(time) < 0 && nt.isSimilarTo(task)) {
				queue.remove(nt);
			}
		}

		lock.unlock();
	}

	/**
	 * Deletes all the tasks but the last one
	 * 
	 * @param time
	 */
	public void clearAllButOneTask(NetworkTask task) {

		try {
			lock.lockInterruptibly();
		}
		catch (InterruptedException ex) {
			log.warn("could not clear any task as the current thread was interrupted while waiting for the lock");
			return;
		}

		// log.info("clearing all tasks but one");
		// for (NetworkTask nt : queue) {
		// if (nt.isSimilarTo(task) ) {
		// queue.remove(nt);
		// }
		// }

		lock.unlock();

	}

	public NetworkTask getNextTask() {
		NetworkTask task = null;

		try {
			lock.lockInterruptibly();
		}
		catch (InterruptedException ex) {
			log.warn("could not get the next task as the current thread was interrupted while waiting for the lock");
			return DeclaredTasks.EmptyTask;
		}

		task = queue.poll();

		lock.unlock();

		if (task == null)
			task = DeclaredTasks.EmptyTask;

		return task;
	}

	@Override
	public String toString() {
		return "TaskQueue";
	}

	public void clearExecutedTask() {

		lock.lock();

		this.queue.poll();

		this.lock.unlock();

	}

	public static void main(String[] args) throws InterruptedException {

		final SyncClass sc = new SyncClass();

		ExecutorService es = Executors.newFixedThreadPool(4);
		es.submit(sc);
		es.submit(new Runnable() {
			@Override
			public void run() {
				sc.normalMethod();
			}
		});
		es.submit(sc);
		es.submit(sc);

		TimeUnit.SECONDS.sleep(30);
		es.shutdown();

	}

	static class SyncClass implements Runnable {

		NormalClass	nc	= new NormalClass();

		public synchronized void syncMethod() {

			System.out.println(Thread.currentThread() + " in syncMethod");
			normalMethod();
		}

		public void normalMethod() {
			System.out.println(Thread.currentThread() + "in normalMethod");
			wasteTime();
			System.out.println(Thread.currentThread() + "woke up from sleep");
		}

		void wasteTime() {

			float a = 4567654321234f, b = 487654567887654f;
			for (long i = 0; i < 999999999999999999l; i++) {
				a = a + b;
				b = a + b;
			}

		}

		@Override
		public void run() {
			syncMethod();
		}

	}

	static class NormalClass {

		public void normalMethod() {

			System.out.println(Thread.currentThread() + "in normalMethod");
			Random rand = new Random();

			wasteTime();

			System.out.println(Thread.currentThread() + "woke up from sleep");

		}

		void wasteTime() {

			float a = 4567654321234f, b = 487654567887654f;
			for (int i = 0; i < 999999999; i++) {
				a = a + b;
				b = a + b;
			}

		}

	}

}
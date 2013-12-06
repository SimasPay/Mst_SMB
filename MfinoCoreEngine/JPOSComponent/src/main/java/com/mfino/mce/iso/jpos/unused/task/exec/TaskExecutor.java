package com.mfino.mce.iso.jpos.unused.task.exec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.mce.iso.jpos.unused.definedtasks.DeclaredTasks;
import com.mfino.mce.iso.jpos.unused.task.NetworkTask;
import com.mfino.mce.iso.jpos.unused.task.NetworkTaskResult;

public class TaskExecutor implements Runnable {

	private static Logger	log	= LoggerFactory.getLogger(TaskExecutor.class);

	private TaskQueue	  taskQueue;

	public void submitTask(NetworkTask task) {
		this.taskQueue.addTask(task);
	}

	public void clearAllTasks() {
		this.taskQueue.clearTasks();
	}

	public TaskQueue getTaskQueue() {
		return taskQueue;
	}

	public void setTaskQueue(TaskQueue taskQueue) {
		this.taskQueue = taskQueue;
	}

	private boolean	stopExecution;

	public boolean isExecutionStopped() {
		return stopExecution;
	}

	public void stop() {
		this.stopExecution =true;
	}

	public TaskExecutor() {
		this.taskQueue = new TaskQueue();
	}

	private long	delayBetweenTasks;

	public void setDelayBetweenTasks(long delayBetweenTasks) {
		this.delayBetweenTasks = delayBetweenTasks;
	}

	@Override
	public void run() {

		while (!stopExecution && !Thread.interrupted()) {

			NetworkTask task = taskQueue.getNextTask();

			if (task.equals(DeclaredTasks.EmptyTask)) {

				log.info("got an EmptyTask from the holder.Going to sleep for " + delayBetweenTasks);
				try {
					Thread.sleep(delayBetweenTasks);
				}
				catch (InterruptedException ex) {
					log.warn("Interrupted while sleeping after getting an empty task");
				}
				continue;
			}

			log.info("executing the task {}" + task.getDescription());
			NetworkTaskResult taskResult = task.run();

			if (taskResult.equals(NetworkTaskResult.Successful)) {
//				log.info("clearing all tasks that were added before this task=" + task);
//				taskQueue.clearTasks(task.getExecutionEndedAt(),task);
			}
			else {
//				taskQueue.clearAllButOneTask();
			}
			try {
				Thread.sleep(delayBetweenTasks);
			}
			catch (InterruptedException ex) {
				log.warn("TaskExecutor thread interrupted while sleeping.Breaking out of execution", ex);
			}

		}

		log.info("TaskExecutor stopped!Clearing all the tasks form the queue");
		taskQueue.clearTasks();

	}

}
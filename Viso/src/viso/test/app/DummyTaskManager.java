package viso.test.app;

import viso.app.PeriodicTaskHandle;
import viso.app.Task;
import viso.app.TaskManager;

public class DummyTaskManager implements TaskManager {

	@Override
	public PeriodicTaskHandle schedulePeriodicTask(Task task, long delay,
			long period) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void scheduleTask(Task task) {
		// TODO Auto-generated method stub

	}

	@Override
	public void scheduleTask(Task task, long delay) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean shouldContinue() {
		// TODO Auto-generated method stub
		return false;
	}

}

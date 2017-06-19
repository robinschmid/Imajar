package net.rs.lamsi.utils.threads;

public abstract class DelayedProgressUpdateTask extends ProgressUpdateTask  {
	
	// timer
	private Thread t;
	private StoppableRunnable r;
	private long delayToStart;
	
	
	/**
	 * Task is started after a set delay
	 * 
	 * @param steps
	 * @param delayToStart
	 */
	public DelayedProgressUpdateTask(int steps, long delayToStart) {
		super(steps);
		this.delayToStart = delayToStart;
	}
	
	public void startDelayed() {
		// stop?
		if(r!=null)
			stop();
		
		// start
		final DelayedProgressUpdateTask thistask = this;
		t = new Thread(r = new StoppableRunnable() {
			
			long startTime = -1;
			@Override
			public void run() {
				while(!isStopped()) {
					if(startTime==-1)
						startTime = System.currentTimeMillis();
					
					if(startTime+delayToStart<=System.currentTimeMillis()) {
						// execute task
						thistask.execute();
						this.stop();
						break;
					}
					try {
						Thread.currentThread().sleep(80);
					} catch (InterruptedException e) { 
						e.printStackTrace();
					}
				} 
			}
		});
		t.start();
	}

	public void stop() {
		if(r!=null) {
			r.stop();
			r = null;
			t = null;
			// interrupt task if already started
			if(this.isStarted()) {
				this.cancel(true);
			}
		}
	}
	
}

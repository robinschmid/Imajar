package net.rs.lamsi.utils.threads;

public class EasyTask extends ProgressUpdateTask<Void> {


  private Runnable runner;

  public EasyTask(Runnable runner) {
    this(1, 1000, runner);
  }

  public EasyTask(int steps, Runnable runner) {
    this(steps, 1000, runner);
  }

  public EasyTask(int steps, long millisToPopUp, Runnable runner) {
    super(steps, millisToPopUp);
    this.runner = runner;
  }

  public EasyTask(String name, Runnable runner) {
    this(name, 1, 1000, runner);
  }

  public EasyTask(String name, int steps, Runnable runner) {
    this(name, steps, 1000, runner);
  }

  public EasyTask(String name, int steps, long millisToPopUp, Runnable runner) {
    super(name, steps, millisToPopUp);
    this.runner = runner;
  }

  protected Void doInBackground() throws Exception {
    wasStarted();
    Thread.currentThread().setName(threadName);
    runner.run();
    return null;
  }

  @Override
  protected Void doInBackground2() throws Exception {
    return null;
  }

}

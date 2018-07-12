package net.rs.lamsi.utils.threads;

import java.util.function.Supplier;

public class EasySupplierTask<T> extends ProgressUpdateTask<T> {

  private Supplier<T> supplier;

  public EasySupplierTask(Supplier<T> supplier) {
    this(1, 1000, supplier);
  }

  public EasySupplierTask(int steps, Supplier<T> supplier) {
    this(steps, 1000, supplier);
  }

  public EasySupplierTask(int steps, long millisToPopUp, Supplier<T> supplier) {
    super(steps, millisToPopUp);
    this.supplier = supplier;
  }

  public EasySupplierTask(String name, Supplier<T> supplier) {
    this(name, 1, 1000, supplier);
  }

  public EasySupplierTask(String name, int steps, Supplier<T> supplier) {
    this(name, steps, 1000, supplier);
  }

  public EasySupplierTask(String name, int steps, long millisToPopUp, Supplier<T> supplier) {
    super(name, steps, millisToPopUp);
    this.supplier = supplier;
  }

  protected T doInBackground() throws Exception {
    wasStarted();
    Thread.currentThread().setName(threadName);
    return supplier.get();
  }

  @Override
  protected T doInBackground2() throws Exception {
    return null;
  }
}

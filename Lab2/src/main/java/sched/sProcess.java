package sched;


public class sProcess {
  public int cputime;
  public int ioblocking;
  public int blockingTime;
  public int arrivedTime;
  public int cpudone;
  public int ionext;
  public int numblocked;
  public int priority;
  public boolean isBlocked = false;
  public double timeEstimate = 0;
  public double lastExecTime = 0;

  public sProcess (int cputime, int ioblocking, int blockingTime, int arrivedTime, int cpudone, int ionext, int numblocked, int priority) {
    this.cputime = cputime;
    this.ioblocking = ioblocking;
    this.blockingTime = blockingTime;
    this.arrivedTime = arrivedTime;
    this.cpudone = cpudone;
    this.ionext = ionext;
    this.numblocked = numblocked;
    this.priority = priority;
  }

  @Override
  public String toString() {
    return "sProcess{" +
            "cputime=" + cputime +
            ", ioblocking=" + ioblocking +
            ", blockingTime" + blockingTime +
            ", arrivedTime" + arrivedTime +
            ", cpudone=" + cpudone +
            ", ionext=" + ionext +
            ", numblocked=" + numblocked +
            ", priority=" + priority +
            '}';
  }
}

package sched;


public class sProcess {
  public int cputime;
  public int ioblocking;
  public int cpudone;
  public int ionext;
  public int numblocked;
  public int priority;

  public sProcess (int cputime, int ioblocking, int cpudone, int ionext, int numblocked, int priority) {
    this.cputime = cputime;
    this.ioblocking = ioblocking;
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
            ", cpudone=" + cpudone +
            ", ionext=" + ionext +
            ", numblocked=" + numblocked +
            ", priority=" + priority +
            '}';
  }
}

package org.ironriders.lib.data;

/**
 * Generic class to store PID values. I'm honestly not sure it ever gets used, but i'm just
 * documenting
 */
public class Pid {
  /**
   * Make one with just p.
   *
   * @param p the p
   */
  public Pid(double p) {
    this.proportional = p;
  }

  /**
   * With the p and the i.
   *
   * @param p the p
   * @param i the i
   */
  public Pid(double p, double i) {
    this.proportional = p;
    this.integral = i;
  }

  /**
   * Give it everything.
   *
   * @param p the p
   * @param i the i
   * @param d the d
   */
  public Pid(double p, double i, double d) {
    this.proportional = p;
    this.integral = i;
    this.derivative = d;
  }

  public double proportional;
  public double integral;
  public double derivative;
}

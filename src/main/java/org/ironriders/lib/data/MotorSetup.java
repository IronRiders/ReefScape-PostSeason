package org.ironriders.lib.data;

public class MotorSetup {
  /**
   * Initalizer.
   *
   * @param motorId the can id of the motor
   * @param inversionStatus should it be inverted?
   */
  public MotorSetup(int motorId, boolean inversionStatus) {
    this.motorId = motorId;
    this.inversionStatus = inversionStatus;
  }

  public int getId() {
    return motorId;
  }

  public boolean getInversionStatus() {
    return inversionStatus;
  }

  public int motorId;
  public boolean inversionStatus;
}

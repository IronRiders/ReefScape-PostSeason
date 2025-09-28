package org.ironriders.lib.data;

public class MotorSetup {

  public MotorSetup(int motorId, boolean InversionStatus) {
    this.motorId = motorId;
    this.inversionStatus = InversionStatus;
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

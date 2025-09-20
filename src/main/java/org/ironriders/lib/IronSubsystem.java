package org.ironriders.lib;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.util.sendable.Sendable;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.SubsystemBase;

/**
 * Common base for 4180 subsystems.
 */
public abstract class IronSubsystem extends SubsystemBase {

  private final String diagnosticName = this.getClass().getSimpleName().replaceAll("Subsystem$", "");
  private final String dashboardPrefix = "Subsystems/" + diagnosticName + "/";
  private final String messagePrefix = diagnosticName + ": ";

  private final long startupTime;

  public IronSubsystem() {
    startupTime = System.nanoTime();
  }

  private String addThreadTime() {
    return "["
        + Objects.toString(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startupTime, TimeUnit.NANOSECONDS), null)
        + "] ";
  }

  public Command logMessage(String msg) {
    return Commands.runOnce(() -> System.out.println(addThreadTime() + messagePrefix + msg));
  }

  public double getDiagnostic(String name, double defaultValue) {
    return SmartDashboard.getNumber(name, defaultValue);
  }

  public void publish(String name, boolean value) {
    SmartDashboard.putBoolean(dashboardPrefix + name, value);
  }

  public void publish(String name, double value) {
    SmartDashboard.putNumber(dashboardPrefix + name, value);
  }

  public void publish(String name, String value) {
    SmartDashboard.putString(dashboardPrefix + name, value);
  }

  public void publish(String name, Sendable value) {
    SmartDashboard.putData(dashboardPrefix + name, value);
    if (value instanceof Command) {
      NamedCommands.registerCommand(name, (Command) value);
    }
  }

  public void reportError(String message) {
    DriverStation.reportError(addThreadTime() + messagePrefix + message, false);
  }

  public void reportWarning(String message) {
    DriverStation.reportWarning(addThreadTime() + messagePrefix + message, false);
  }
}

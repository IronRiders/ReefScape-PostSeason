package org.ironriders.lib;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.ironriders.lib.Elastic.Notification;
import org.ironriders.lib.Elastic.NotificationLevel;

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
    String str = Objects
        .toString(TimeUnit.MILLISECONDS.convert(System.nanoTime() - startupTime, TimeUnit.NANOSECONDS) / 1000d, 0);

    return "["
        + str
        + "] ";
  }

  /**
   * Send a generic elastic notification with no edits.
   * 
   * @param notif The elastic notification to send.
   */
  public void putNotifcation(Notification notif) {
    Elastic.sendNotification(notif);
  }

  /**
   * Send a elstaic notification with level WARNING.
   * This will also apend your title to "warning in (your subsystem): and set that
   * as the title".
   * You should be careful to put as little information in the title as possible
   * so it doesn't overflow.
   * This notification will last 10 seconds
   * @param notif The elastic notification to send.
   */
  public void notifyWarning(Notification notif) {
    notif.setLevel(NotificationLevel.WARNING);
    String title = notif.getTitle();
    title = "Warning in " + messagePrefix + ":" + title;
    notif.setTitle(title);
    notif.setDisplayTimeSeconds(10);
    putNotifcation(notif);
  }

  /**
   * Send a elstaic notification with level info.
   * This will also apend your title to "Message from (your subsystem): and set
   * that as the title".
   * You should be careful to put as little information in the title as possible
   * so it doesn't overflow.
   * This notification will last 5 seconds
   * @param notif The elastic notification to send.
   */
  public void notify(Notification notif) {
    notif.setLevel(NotificationLevel.INFO);
    String title = notif.getTitle();
    title = "Message from " + messagePrefix + ":" + title;
    notif.setTitle(title);
    notif.setDisplayTimeSeconds(5);
    putNotifcation(notif);
  }

  /**
   * Send a elstaic notification with level ERROR.
   * This will also apend your title to "ERROR in (your subsystem): and set that
   * as the title".
   * You should be careful to put as little information in the title as possible
   * so it doesn't overflow.
   * This notification will last 30 seconds
   * @param notif The elastic notification to send.
   */
  public void notifyError(Notification notif) {
    notif.setLevel(NotificationLevel.ERROR);
    String title = notif.getTitle();
    title = "ERROR in " + messagePrefix + ":" + title;
    notif.setTitle(title);
    notif.setDisplayTimeSeconds(30);
    putNotifcation(notif);
  }

  public void putTitleTextNotifcation(String title, String text) {
    Elastic.sendNotification(new Notification().withTitle(title).withDescription(text));
  }

  public Command logMessage(String msg) {
    putTitleTextNotifcation(addThreadTime() + messagePrefix, msg);
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
    Elastic.sendNotification(
        new Notification().withLevel(NotificationLevel.ERROR).withTitle("ERROR").withDescription(message));
  }

  public void reportWarning(String message) {
    DriverStation.reportWarning(addThreadTime() + messagePrefix + message, false);
    Elastic.sendNotification(
        new Notification().withLevel(NotificationLevel.WARNING).withTitle("WARNING").withDescription(message));
  }
}

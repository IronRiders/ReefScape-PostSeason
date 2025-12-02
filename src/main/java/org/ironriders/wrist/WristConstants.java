package org.ironriders.wrist;

import static org.ironriders.wrist.WristConstants.DERIVATIVE;
import static org.ironriders.wrist.WristConstants.INTEGRAL;
import static org.ironriders.wrist.WristConstants.PROPORTIONAL;

import org.ironriders.lib.data.PID;

public class WristConstants {
    public enum WristState {
      STOWED(0),
      INTAKING(-85),
      L2L3(40),
      L4(15);
  
    
        public final double rotation;
    
        WristState(double rotation) {
          this.rotation = rotation;
        }
    }

    
    static final int PRIMARY_MOTOR_ID = 1;
    static final int FOLLOWER_MOTOR_ID= 2;

    static final double ABSOLUTE_ENCODER_OFFSET = 0;
    static final double FRAME_OF_REFERENCE_OFFSET = 0;

    static final double WRIST_TOLERANCE = 0.5;

    static final double PROPORTIONAL = 0.001;
    static final double INTEGRAL = 0.0;
    static final double DERIVATIVE = 0.0;
    static final PID PID_COEFFIECENTS = new PID(PROPORTIONAL,INTEGRAL,DERIVATIVE);

    static final double MAX_ACCERLATION = 0.0;
    static final double MAX_VELOCITY = 0.0;
   
}

package org.usfirst.frc.team2383.robot;


import edu.wpi.first.wpilibj.SampleRobot;
import edu.wpi.first.wpilibj.RobotDrive;

import java.util.function.DoubleUnaryOperator;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;


//2383
public class Robot extends SampleRobot {
    RobotDrive myRobot;  // class that handles basic drive operations
    Joystick leftStick;  // set to ID 1 in DriverStation
    Joystick rightStick; // set to ID 2 in DriverStation
    Joystick controller;
    Joystick operatorStick;
    CANTalon lift;
    VictorSP conveyor;
    DoubleSolenoid clamp1;
    DoubleSolenoid clamp2;
    
    
    public Robot() {
        myRobot = new RobotDrive(9,8,1,0);
        myRobot.setMaxOutput(0.4);         // *****
        myRobot.setExpiration(0.1);
        myRobot.setMaxOutput(0.85);
        leftStick = new Joystick(1);
        rightStick = new Joystick(0);
        controller = new Joystick(3);
        operatorStick = new Joystick(2);
        lift = new CANTalon(0); // Talon SRX for lift
        conveyor = new VictorSP(7); // Victor Motor for conveyor
        clamp1 = new DoubleSolenoid(0,1);
        clamp2 = new DoubleSolenoid(2,3);
      
        
    }
    
    private static class Constants {
    	public static double inputExpo = 0.32;
    	public static double inputDeadband = 0.05;
    }
    
    private static DoubleUnaryOperator inputExpo = (x) -> {
		return Constants.inputExpo * Math.pow(x, 3) + (1 - Constants.inputExpo) * x;
	};
	private static DoubleUnaryOperator deadband = (x) -> {
		return Math.abs(x) > Constants.inputDeadband ? x : 0;
	};

    
    
    /**
     * Runs the motors with tank steering.
     */
    public void operatorControl() {
        
        myRobot.setSafetyEnabled(true);
        while (isOperatorControl() && isEnabled()) {
        	myRobot.tankDrive(leftStick, rightStick);
        	//myRobot.tankDrive(deadband.applyAsDouble(controller.getRawAxis(5)), deadband.applyAsDouble(controller.getRawAxis(1)));
        	
            Timer.delay(0.005);		// wait for a motor update time
           
           // lift
            boolean liftUp = operatorStick.getRawButton(5);
            boolean liftDown = operatorStick.getRawButton(3); // move lift down 
            
           if(liftUp && (liftDown == false)){
        	   lift.changeControlMode(TalonControlMode.PercentVbus);
        	   lift.set(-0.7);
           }
           else if((liftUp == false) && (liftDown == false)){
        	   lift.changeControlMode(TalonControlMode.PercentVbus);
        	   lift.set(0.0);
           }
           
           
           if(liftDown && (liftUp == false)){
        	   lift.changeControlMode(TalonControlMode.PercentVbus);
        	   lift.set(0.7);
           }
           
           
           // conveyor
           
           boolean conveyorForward = operatorStick.getRawButton(4); // move conveyor forward
           boolean conveyorBackward = operatorStick.getRawButton(6); // move conveyor backward
           
           if(conveyorForward && (conveyorBackward == false)){
        	   conveyor.set(1.0);
           }
           else if((conveyorForward == false) && (conveyorBackward == false)){
        	   conveyor.set(0.0);
           }
           else if((conveyorForward == false) && (conveyorBackward)){
        	   conveyor.set(-1.0);
           }
           
           
           // guard
           
           boolean Clamp = operatorStick.getRawButton(7);
           boolean unClamp = operatorStick.getRawButton(8);
           
           if(Clamp && (unClamp == false)){
        	   clamp1.set(Value.kForward);
        	   clamp2.set(Value.kForward);
        	   
           }
           else if((Clamp == false) && (unClamp == false)){
        	   clamp1.set(Value.kOff);
        	   clamp2.set(Value.kOff);
           }
           else if((Clamp == false) && (unClamp)){
        	   clamp1.set(Value.kReverse);
        	   clamp2.set(Value.kReverse);
           }
        	   
        }
    }

}

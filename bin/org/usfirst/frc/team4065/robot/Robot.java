/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team4065.robot;


import java.sql.Time;

import com.kauailabs.navx.frc.AHRS;
//import java.util.concurrent.TimeUnit;
import com.kauailabs.navx.frc.AHRS.SerialDataType;
import com.ctre.phoenix.motorcontrol.ControlMode;

//import edu.wpi.first.wpilibj.buttons.Button;
//import edu.wpi.first.wpilibj.buttons.JoystickButton;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
//import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.cscore.UsbCamera;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.networktables.NetworkTableEntry;

import edu.wpi.first.networktables.NetworkTableInstance;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.DigitalInput;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.*;
import com.mindsensors.CANLight;
public class Robot extends IterativeRobot {

public TalonAutoDrive driveAuto;
	
boolean toggleState = false;


//JOYSTICK
private Joystick _joystick = new Joystick(0);
	
//DRIVE	
private TalonSRX _TopL = new TalonSRX(3);
private TalonSRX _BtmL = new TalonSRX(2);
private TalonSRX _TopR = new TalonSRX(13);
private TalonSRX _BtmR = new TalonSRX(12);
private TalonSRX lift = new TalonSRX(32);

private TalonSRX intakeL  = new TalonSRX(22);
private TalonSRX intakeR  = new TalonSRX(23);
	
private DoubleSolenoid solenoid = new DoubleSolenoid(4,5);

// L E D  L I G H T S
//CANLight lights;

private AHRS mNavX;

double leftStickValue;
double rightStickValue;
	
int timer = 0;

private Compressor compressor = new Compressor();

final String baseline = "Baseline";

final String leftAuto = "Left Auto";

final String rightAuto = "Right Auto";

final String centerAuto = "Center Auto";

String autoSelected;

SendableChooser<String> chooser = new SendableChooser<>();


int STAHP = 0;

int Target = 0;

//==========================================================
	
	@Override
	public void robotInit() {
		
		_TopL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

		_TopR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

		CameraServer server = CameraServer.getInstance();

		server.startAutomaticCapture("cam0", 0);
		
	//mNavX = new AHRS();
		
	_BtmL.follow(_TopL);
	_BtmR.follow(_TopR);
		
	leftStickValue = 0.0;
	rightStickValue = 0.0;
		
	compressor.start();
	compressor.setClosedLoopControl(true);   
	
	chooser.addDefault("Baseline", baseline);

	chooser.addObject("Center Auto", centerAuto);

	chooser.addObject("Left Auto", leftAuto);

	chooser.addObject("Right Auto", rightAuto);

	SmartDashboard.putData("Auto choices", chooser);

	//lights = new CANLight(1);
	//lights.showRGB(0, 255, 0);

	}		
//===========================================================
	
	
	@Override
	public void autonomousInit() {
		autoSelected = chooser.getSelected();

		System.out.println("Auto selected: " + autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		
	//	String gameData;
		
		//gameData = DriverStation.getInstance().getGameSpecificMessage();
	/*	
		switch (autoSelected) {

		case leftAuto:

	        if(gameData.length() > 0)

	                { 

			  if(gameData.charAt(0) == 'L')
			  {
				  	driveAuto.drive(0.3);//drives straight
				  	Timer.delay(7);
				  	driveAuto.drive(0.0);//stop drive				  	
				  	driveAuto.closeSolenoid(true);//clamp the cube			  	
				  	Timer.delay(0.3);			  	
				  	driveAuto.intake(0.3);//suck the cube				  	
				  	Timer.delay(1);			  	
				  	driveAuto.intake(0.0);//stops sucking the cube			  	
				  	driveAuto.closeSolenoid(false);//turn off air				  	
				  	driveAuto.offSolenoid(true);//make sure air is off				  	
				  	driveAuto.turnR(0.3);//turns right				  	
				  	Timer.delay(1);				  	
				  	driveAuto.turnR(0.0);//stops turning			  	
				  	driveAuto.lift(0.4);//arm moves up
				  	Timer.delay(2);
				  	driveAuto.lift(0.0);//stops lift
				  	Timer.delay(0.1);
				  	driveAuto.drive(0.2);//DRIVE TO THE scale a little 
				  	Timer.delay(2);
				  	driveAuto.intake(-0.9);//spit out the cube
				  	Timer.delay(2);
				  	driveAuto.intake(0.0);//stops the intake motors				  	
				  	driveAuto.drive(-0.3);//drive backwards to be ready for the match

			  } else if(gameData.charAt(0) == 'R') { // right  
				 
				  driveAuto.drive(0.5);
				  Timer.delay(7);
				  driveAuto.drive(0.0);
			  }
				break;
}
			case centerAuto: 

				  if(gameData.length() > 0)
	             {
			  if(gameData.charAt(0) == 'L')
			  {
					driveAuto.drive(0.3);//drive
					Timer.delay(2);
					driveAuto.drive(0.0);//stop drive
					driveAuto.turnL(0.3);//turn left
					Timer.delay(1);
					driveAuto.turnL(0.0);//stop turning left
					driveAuto.drive(0.4);//drive
					Timer.delay(2);
					driveAuto.drive(0.0);//stop drive
					driveAuto.turnR(0.3);//start turning right
					Timer.delay(1);
					driveAuto.turnR(0.0);//stop turning right
					driveAuto.drive(0.3);//drive
					Timer.delay(1);
					driveAuto.turnR(0.3);
					Timer.delay(1);
					driveAuto.closeSolenoid(true);//clamp the cube
					Timer.delay(0.3);
				  	driveAuto.intake(0.3);//suck the cube				  	
				  	Timer.delay(1);				  	
				  	driveAuto.intake(0.0);//stops sucking the cube
				  	driveAuto.closeSolenoid(false);//turn off air				  	
				  	driveAuto.offSolenoid(true);//make sure air is off			  	
				  	driveAuto.turnR(0.3);//turns right			  	
				  	Timer.delay(1);				  	
					driveAuto.lift(0.4);//arm moves up
				  	Timer.delay(2);
				  	driveAuto.lift(0.0);//stops lift
				  	Timer.delay(0.1);
				  	driveAuto.drive(0.2);//DRIVE TO THE scale a little 
				  	Timer.delay(2);
				  	driveAuto.intake(-0.9);//spit out the cube
				  	Timer.delay(2);
				  	driveAuto.intake(0.0);//stops the intake motors
				  	
				  	driveAuto.drive(-0.3);//drive backwards to be ready for the match

			 } else if(gameData.charAt(0) == 'R') {
				
				 driveAuto.drive(0.3);//drive
					Timer.delay(2);
					driveAuto.drive(0.0);//stop drive
					driveAuto.turnR(0.3);//turn right
					Timer.delay(1);
					driveAuto.turnR(0.0);//stop turning right
					driveAuto.drive(0.4);//drive
					Timer.delay(2);
					driveAuto.drive(0.0);//stop drive
					driveAuto.turnL(0.3);//start turning left
					Timer.delay(1);
					driveAuto.turnL(0.0);//stop turning left
					driveAuto.drive(0.3);//drive
					Timer.delay(1);
					driveAuto.turnL(0.3);
					Timer.delay(1);
					driveAuto.closeSolenoid(true);//clamp the cube
					Timer.delay(0.3);
				  	driveAuto.intake(0.3);//suck the cube				  	
				  	Timer.delay(1);				  	
				  	driveAuto.intake(0.0);//stops sucking the cube
				  	driveAuto.closeSolenoid(false);//turn off air				  	
				  	driveAuto.offSolenoid(true);//make sure air is off			  	
				  	driveAuto.turnL(0.3);//turns left			  	
				  	Timer.delay(1);				  	
					driveAuto.lift(0.4);//arm moves up
				  	Timer.delay(2);
				  	driveAuto.lift(0.0);//stops lift
				  	Timer.delay(0.1);
				  	driveAuto.drive(0.2);//DRIVE TO THE scale a little 
				  	Timer.delay(2);
				  	driveAuto.intake(-0.9);//spit out the cube
				  	Timer.delay(2);
				  	driveAuto.intake(0.0);//stops the intake motors
				  	driveAuto.drive(-0.3);//drive backwards to be ready for the match
					}
				break;
	              }
			case rightAuto: 
				   if(gameData.length() > 0)
	               {   
					   if(gameData.charAt(0) == 'L')
			  {
				   	driveAuto.drive(0.5);
					Timer.delay(7);
					driveAuto.drive(0.0);
			  } else if(gameData.charAt(0) == 'R')  {

				  	driveAuto.drive(0.3);//drives straight
				  	Timer.delay(7);
				  	driveAuto.drive(0.0);//stop drive
				  	driveAuto.closeSolenoid(true);//clamp the cube
				  	Timer.delay(0.3);
				  	driveAuto.intake(0.3);//suck the cube
				  	Timer.delay(1);
				  	driveAuto.intake(0.0);//stops sucking the cube
				  	driveAuto.closeSolenoid(false);//turn off air
				  	driveAuto.offSolenoid(true);//make sure air is off
				  	driveAuto.turnL(0.3);//turns LEFT
				  	Timer.delay(1);
				  	driveAuto.turnL(0.0);//stops turning
				  	driveAuto.lift(0.4);//arm moves up
				  	Timer.delay(2);
				  	driveAuto.lift(0.0);//stops lift
				  	Timer.delay(0.1);
				  	driveAuto.drive(0.2);//DRIVE TO THE scale a little
				  	Timer.delay(2);
				  	driveAuto.intake(-0.9);//spit out the cube
				  	Timer.delay(2);
				  	driveAuto.intake(0.0);//stops the intake motors
				  	driveAuto.drive(-0.3);//drive backwards to be ready for the match
					}
			break;
	             }
			case baseline:
		default:
				driveAuto.drive(0.5);;
				Timer.delay(10);
				driveAuto.drive(0.0);
				break;
	              }
	              */
	}
	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {

//  L I M E  L I G H T

		//NetworkTable table = NetworkTableInstance.getDefault().getTable("limelight");

		//NetworkTableEntry tx = table.getEntry("tx");

		//NetworkTableEntry ty = table.getEntry("ty");

	//	NetworkTableEntry ta = table.getEntry("ta");


//		double x = tx.getDouble(0);

		//double y = ty.getDouble(0);

	//	double area = ta.getDouble(0);  

//		System.out.println("teleop");

	//	if (_joystick.getRawButton(0)) { //chase toggle ON

	//		System.out.println("Button 3");

	//		System.out.println("Entering Target Chase");

	//		Target = 1;		

	//	}

	//	if(_joystick.getRawButton(0)) { //chase toggle OFF
//
		//	System.out.println("Button 4");

		//	System.out.println("Exiting Target Chase");
//
	//		Target = 0;

	//	}

	//	if (Target == 1) {

		//	System.out.println(area);

		//	if(area >= 12.5) {

		//		driveAuto.drive(0);
///
	//			STAHP = 1; //stops the robot
//
	//		}

	//		else if (area <= 12.5) {

		//		STAHP = 0;

	//		}

	//		if(STAHP == 0) {
//
	//			driveAuto.drive(0.6); //sets motors to .6 speed in target mod

	//			if(x < -10) {

	//				driveAuto.turnL(0.6); // checks for Target being to the left, if so, turns robot left

	//			}

	//			else if(x > 10) {

	//				driveAuto.turnR(0.6); //checks for Target being to the right, if so, turns robot right
//
		//		}

		//	}

	//	}			
//===========================================================================
//J O Y S T I C K

 leftStickValue = _joystick.getRawAxis(2);
 rightStickValue = _joystick.getRawAxis(3);
	 
 if(Math.abs(leftStickValue) > 0.1 || Math.abs(rightStickValue) > 0.1) 
 {
	 double right = rightStickValue * rightStickValue;
	 double left = leftStickValue * leftStickValue;
		 
	 if(rightStickValue > 0) 
	 {
		 right *= -1;
	 }
	 if(leftStickValue < 0)
	 {
		 left *= -1;
	 }
		 
		 _TopL.set(ControlMode.PercentOutput, left);
		_TopR.set(ControlMode.PercentOutput, right);
			

	}else{
		_TopL.set(ControlMode.PercentOutput, 0);
		_TopR.set(ControlMode.PercentOutput, 0);
}

//===========================================================================
// L I F T 
 if(_joystick.getRawButton(1))// && !limitSwitch.get()) 
 {
	 lift.set(ControlMode.PercentOutput, 0.7);
}else if(_joystick.getRawButton(4))//&& limitSwitch.get())
	{
	 lift.set(ControlMode.PercentOutput, -0.7);

 }else {
	 lift.set(ControlMode.PercentOutput, 0);
			
}
 //=====================================================================
//I N T A K E  M O T O R S
 if (_joystick.getRawButton(2)) {
	intakeL.set(ControlMode.PercentOutput, 0.8);
	intakeR.set(ControlMode.PercentOutput, 0.8);
 }else if(_joystick.getRawButton(3))
	 {
	intakeL.set(ControlMode.PercentOutput, -0.2);
 	intakeR.set(ControlMode.PercentOutput, -0.2);
}else{
	 intakeL.set(ControlMode.PercentOutput, 0);
	 intakeR.set(ControlMode.PercentOutput, 0);
}
//========================================================================
 // I N T A K E   G R A B B E R
 
 if(_joystick.getRawButton(5))
	 
 {
	 solenoid.set(DoubleSolenoid.Value.kForward);
 }else if(_joystick.getRawButton(6))
 {
	 solenoid.set(DoubleSolenoid.Value.kReverse);
	 
 }else{
	 solenoid.set(DoubleSolenoid.Value.kOff);
 }
//===========================================================================

	}
	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}
}

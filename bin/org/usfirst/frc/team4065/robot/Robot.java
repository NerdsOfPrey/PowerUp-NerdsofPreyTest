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

//create driveAuto object after you declare talonSrx objects and plug them into the contructor in the order you specified.

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

//now your driveAuto should work
public TalonAutoDrive driveAuto = new TalonAutoDrive(_TopL, _BtmL, _TopR, _BtmR, intakeL, intakeR, solenoid);

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

final double driveWheelCircumference = 6 * Math.PI;//6 is the diameter of the drivetrain wheel in inches

String autoSelected;

SendableChooser<String> chooser = new SendableChooser<>();


int STAHP = 0;

int Target = 0;

//==========================================================

	@Override
	public void robotInit() {

	_TopL.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);
	//you would need to invert one of these since one of them will give negative distances because it is rotating opposite of the other one.
	_TopR.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, 0, 10);

	CameraServer server = CameraServer.getInstance();

	server.startAutomaticCapture("cam0", 0);

	mNavX = new AHRS(SPI.Port.kMXP);//spi port is the port when mounting the board directly on roborio, in the middle

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

	/**
		This function converts the position read by mag encoder on the talon given in the argument into inches traveled.
		You have the mag encoders set to relative, so whatever the starting position when the robot starts of the encoder is 0
	*/
	public double getEncoderDistanceInches(TalonSRX* tal)
	{
			return ( driveWheelCircumference * tal.getSelectedSensorPosition(0) ) / 4096;//driveWheelCircumference is defined above, make sure to fix it if its wrong.
	}

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

	  String gameData;

	  gameData = DriverStation.getInstance().getGameSpecificMessage();//gets position of our alliance colors on switches and scale
		//it contains 3 letters, each letter is either L for our color being on the left side when facing it or R if it will be on the right side when facing from DS side.
		//we want only the switch color for our switch right in front of us for auto, so we need the first letter.

		switch (autoSelected) {//use mNavX.getAngle() to get angle of robot, and getEncoderDistanceInches(tal) to get the encoder distance in inches from encoder in tal.

		case leftAuto:

	        if(gameData.length() > 0)
					{

			  		if(gameData.charAt(0) == 'L')
			  		{
				  			driveAuto.drive(0.3);//drives straight
								double endDistanceL = getEncoderDistanceInches(_TopL) + 60;//we want the robot to drive forward 60 inches, so we only need to measure the change in distance, not absolute distance traveled of the shaft.
								//so, the end distance will be the 60 inches plus the current distance the encoder is reading. so then it will move to current distance to current distance + 60
								double endDistanceR = getEncoderDistanceInches(_TopR) + 60;
				  			while(getEncoderDistanceInches(_TopL) < endDistanceL && getEncoderDistanceInches(_TopR) < endDistanceR)
								{
									//if you have trouble with the robot not driving straight, you can add an if statement that checks the difference in distance from both encoders. if it gets too big you can slow down one side and speed up the other to keep it straight.
								}
						  	driveAuto.drive(0.0);//stop drive
						  	driveAuto.closeSolenoid();//clamp the cube
						  	Timer.delay(0.3);
						  	driveAuto.intake(0.3);//suck the cube
						  	Timer.delay(1);
						  	driveAuto.intake(0.0);//stops sucking the cube
						  	driveAuto.closeSolenoid();//turn off air
						  	driveAuto.offSolenoid();//make sure air is off
						  	driveAuto.turnR(0.3);//turns right
								int endAngle = mNavX.getAngle() + 90;//need to make sure turning right increases angle, if it decreases angle then you need to subtract 90
						  	while(mNavX.getAngle() < endAngle)
								{

								}
						  	driveAuto.turnR(0.0);//stops turning
						  	driveAuto.lift(0.4);//arm moves up
						  	Timer.delay(2);//if there is encoder on lift, use it to convert the position its at to an angle. do something like getSelectedSensorPosition(0) * (360/4096) should give the angle its at. then use a while loop
						  	driveAuto.lift(0.0);//stops lift
						  	Timer.delay(0.1);
						  	driveAuto.drive(0.2);//DRIVE TO THE scale a little
						  	Timer.delay(2);
						  	driveAuto.intake(-0.9);//spit out the cube
						  	Timer.delay(2);
						  	driveAuto.intake(0.0);//stops the intake motors
						  	driveAuto.drive(-0.3);//drive backwards to be ready for the match
			  		}
						else if(gameData.charAt(0) == 'R') { // right

							driveAuto.drive(0.3);//drives straight
							double endDistanceL = getEncoderDistanceInches(_TopL) + 80;
							double endDistanceR = getEncoderDistanceInches(_TopR) + 80;
							while(getEncoderDistanceInches(_TopL) < endDistanceL && getEncoderDistanceInches(_TopR) < endDistanceR)
							{
								//if you have trouble with the robot not driving straight, you can add an if statement that checks the difference in distance from both encoders. if it gets too big you can slow down one side and speed up the other to keep it straight.
							}
							driveAuto.drive(0);
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

			 }
			 else if(gameData.charAt(0) == 'R')
			 {

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

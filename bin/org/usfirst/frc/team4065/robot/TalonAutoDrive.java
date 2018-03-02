package org.usfirst.frc.team4065.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DoubleSolenoid;



public class TalonAutoDrive {
	//DRIVE	
	private TalonSRX _TopL = new TalonSRX(3);
	private TalonSRX _BtmL = new TalonSRX(2);
	private TalonSRX _TopR = new TalonSRX(13);
	private TalonSRX _BtmR = new TalonSRX(12);
	private TalonSRX lift = new TalonSRX(32);

	private TalonSRX intakeL  = new TalonSRX(22);
	private TalonSRX intakeR  = new TalonSRX(23);
			
		
	private DoubleSolenoid Solenoid = new DoubleSolenoid(4,5);
	

	public TalonAutoDrive(TalonSRX _TopL, TalonSRX _BtmL, TalonSRX _TopR, TalonSRX _BtmR, TalonSRX intakeL, TalonSRX intakeR) {

		
		this._TopL = _TopL;

		this._BtmL = _BtmL;

		this._TopR = _TopR;

		this._BtmR = _BtmR;

		_BtmL.set(ControlMode.Follower, _TopL.getDeviceID());

		_BtmR.set(ControlMode.Follower, _TopR.getDeviceID());
	

	}

	

	public void drive(double speed) {

		_TopL.set(ControlMode.PercentOutput, speed);

		_TopR.set(ControlMode.PercentOutput, speed * -1);

	}
	public void lift(double speed)
	{
		lift.set(ControlMode.PercentOutput, speed);
	}
	public void intake(double speed)
	{
		intakeL.set(ControlMode.PercentOutput, speed);
		intakeR.set(ControlMode.PercentOutput, speed);
	}
	public void turnR(double speed)
	{
		_TopL.set(ControlMode.PercentOutput, speed);
		_TopR.set(ControlMode.PercentOutput, speed);
	}
	public void turnL(double speed)
	{
		_TopL.set(ControlMode.PercentOutput, speed * -1);
		_TopR.set(ControlMode.PercentOutput, speed * -1);
	}
	public void closeSolenoid(boolean close)
		{
		
		Solenoid.set(DoubleSolenoid.Value.kForward);
	
		
	}
	public void openSolenoid(boolean open)
	{
		Solenoid.set(DoubleSolenoid.Value.kReverse);
		
	}
	public void offSolenoid(boolean off)
	{
		Solenoid.set(DoubleSolenoid.Value.kOff);
	}
	
}

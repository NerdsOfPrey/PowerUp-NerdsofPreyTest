package org.usfirst.frc.team4065.robot;
import com.ctre.phoenix.motorcontrol.ControlMode;
import edu.wpi.first.wpilibj.Timer;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import edu.wpi.first.wpilibj.DoubleSolenoid;



public class TalonAutoDrive {
	//DRIVE
	// You do not need to redefine the talonsrx here, your constructor will set the values when you create the object
	private TalonSRX _TopL;
	private TalonSRX _BtmL;
	private TalonSRX _TopR;
	private TalonSRX _BtmR;
	private TalonSRX lift;

	private TalonSRX intakeL;
	private TalonSRX intakeR;


	private DoubleSolenoid solenoid;


	public TalonAutoDrive(TalonSRX _TopL, TalonSRX _BtmL, TalonSRX _TopR, TalonSRX _BtmR, TalonSRX intakeL, TalonSRX intakeR, DoubleSolenoid sol) {


		this._TopL = _TopL;

		this._BtmL = _BtmL;

		this._TopR = _TopR;

		this._BtmR = _BtmR;
		// You also need to set the intake variables too.
		this.intakeL = intakeL;

		this.intakeR = intakeR;

		_BtmL.set(ControlMode.Follower, _TopL.getDeviceID());

		_BtmR.set(ControlMode.Follower, _TopR.getDeviceID());

		this.solenoid = sol;


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

	public void closeSolenoid()//idk why you put boolean as arguments, you dont need it
	{
		solenoid.set(DoubleSolenoid.Value.kForward);
	}
	public void openSolenoid()
	{
		solenoid.set(DoubleSolenoid.Value.kReverse);

	}
	public void offSolenoid()
	{
		solenoid.set(DoubleSolenoid.Value.kOff);
	}


}

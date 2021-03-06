/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plasmarobotics.jim;

import edu.wpi.first.wpilibj.CounterBase;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.Gyro;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.RobotDrive;
import java.util.Random;
import org.plasmarobotics.jim.controls.PlasmaGamepad;

/**
 *This class controls the drive system 
 * @author Jim
 */

public class FroboDrive {
    
    private Joystick leftJoystick,
            rightJoystick;
   
    private RobotDrive chassis;
    
    private Encoder LeftEncoder,
            RightEncoder;
    
    private Gyro gyro;
    
    private PlasmaGamepad gamepad;
    
    private boolean resetNeeded;
    
    /**
     * Used to create a FroboDrive object to control all driving controls
     * 
     * @param leftJoystick Left joystick for tank drive
     * @param rightJoystick Right joystick for tank drive
     * @param frobo Instance of the main class
     */
    public FroboDrive(Joystick leftJoystick, Joystick rightJoystick, Frobo frobo){
        //Binds the joysticks...
        this.leftJoystick = frobo.getLeftJoystick();
        this.rightJoystick = frobo.getRightJoystick();
        
        //Creates a RobotDrive...
        chassis = new RobotDrive(Constants.FRONT_LEFT_DRIVE_CHANNEL, 
                Constants.BACK_LEFT_DRIVE_CHANNEL, 
                Constants.FRONT_RIGHT_DRIVE_CHANNEL, 
                Constants.BACK_RIGHT_DRIVE_CHANNEL);
        
        gamepad = new PlasmaGamepad(3);
        //setting up encoders
        LeftEncoder = new Encoder(Constants.LEFT_ENCODER_A_CHANNEL, Constants.LEFT_ENCODER_B_CHANNEL, false, CounterBase.EncodingType.k4X); //normal direciton
        LeftEncoder.setDistancePerPulse(Constants.ENCODER_DISTANCE_PER_PULSE);
        LeftEncoder.start();
        
        RightEncoder = new Encoder(Constants.RIGHT_ENCODER_A_CHANNEL, Constants.RIGHT_ENCODER_B_CHANNEL, true, CounterBase.EncodingType.k4X); //reverse    
        RightEncoder.setDistancePerPulse(Constants.ENCODER_DISTANCE_PER_PULSE);
        RightEncoder.start();
        
        //gyro
        gyro = new Gyro(Constants.GYRO_CHANNEL);
        gyro.setSensitivity(Constants.GYRO_SENSITIVITY);
        gyro.reset();
        
        resetNeeded = true;
    }
    
    /**
     * Meant to be run once before teleop. 
     * -sets all motor direction
     */
    public void setupTeleop(){
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontRight,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearRight,false);
    }
    
    /**
     * Meant to be run once before autonomous
     * -sets all motor direction
     * resets the encoders and gyro
     */
    public void setupAutonomous(){
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontLeft,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearLeft,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kFrontRight,false);
        chassis.setInvertedMotor(RobotDrive.MotorType.kRearRight,false);
        reset();
    }
    /*
     * Method called in teleop loop to provide tank drive for the robot
     */
    boolean lawl;
    Random numbers = new Random(100);
    public void updateTeleop(){
//        double sweg = numbers.nextDouble();
//        if(sweg < .0001){
//            lawl = !lawl;
//            System.out.println("Lawl flipped");
//        }
//      
//            
            
//            if(lawl)
//                chassis.arcadeDrive(-gamepad.getRightJoystickXAxis(), gamepad.getRightJoystickYAxis());
//            else
                chassis.arcadeDrive(-gamepad.getLeftJoystickYAxis(), -gamepad.getRightJoystickXAxis());
    }
    
    
    /**
     * The robot will drive in a straight line for a given distance
     * @param distance Distance (in inches) to drive
     * @param speed Speed of motors (-1 to 1 scale)
     */
    

    public boolean drive(double speed, double distance){
        if(resetNeeded){
            reset();
            resetNeeded = false;
        }
        double angle = gyro.getAngle();
        if(((LeftEncoder.getDistance() + RightEncoder.getDistance())/2) < distance){
            chassis.drive(speed, angle * .03);//stay on track with .03 curve
            return false;
        } else{
            resetNeeded = true;
            chassis.drive(0, 0);//stop robot
            return true;
           
        }
        
            
    }
    
    
    /**
     * Turns the robot
     * @param degrees angle in degrees to turn (clockwise is +)
     * @return true when action is completed
     */
    public boolean turn(float degrees){
        if(resetNeeded){
            reset();
            resetNeeded = false;
        }
        degrees = -degrees;
        
        double difference = degrees - gyro.getAngle();
        
//        System.out.println("Gyro: " + gyro.getAngle());
        
        System.out.println(gyro.getAngle());
        
        if(Math.abs(difference) > 5){
            if(difference > 0){
                chassis.drive(.3, -1);//turn left
                System.out.println("Turning left");
                return false;
            } else {
                chassis.drive(.3, 1);//turn right
                System.out.println("Turning right");
                return false;
            } 

        } else {
            chassis.drive(0,0); //stop the robot
            resetNeeded = true;
            System.out.println("Stoping turn");
            return true;
        }           

    }
    
    /**
     * Resets the sensors to perform a new action
     */
    public void reset(){
        LeftEncoder.reset();
        RightEncoder.reset();
        gyro.reset();
    }
    
    /**
     * Stops all motors on the robot
     */
    public void stop(){
        chassis.drive(0, 0);
    }
}
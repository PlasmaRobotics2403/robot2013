
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.plasmarobotics.jim;

import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Joystick;
import org.plasmarobotics.jim.controls.ControlPack;
import org.plasmarobotics.jim.controls.ToggleableButton;

/**
 *Class to manage shooting functionality of the robot
 * @author Jim
 */
public class FroboShoot {
    
    private static Victor frontShootVictor,
            backShootVictor;
    
    private static DoubleSolenoid shootSolenoid;
    
    private boolean motorsSpinning;
    
    private boolean motorSpeed;
    
    private Joystick rightJoystick;
    private ToggleableButton shootBtn,
            motorToggle,
            high_motor_toggle;
    
    /**
     * Sets up a froboShoot object
     * @param rightStick Needed to bind the JoystickButton to shoot
     * @param frobo an instance of the main frobo class
     */
    public FroboShoot(Joystick rightStick, Frobo frobo){
        
        this.rightJoystick = frobo.getRightJoystick();

       
        
        shootBtn = ControlPack.getInstance().getGamepad().getRightBumper();
        
        this.motorToggle = ControlPack.getInstance().getGamepad().getXButton();
        this.high_motor_toggle = ControlPack.getInstance().getGamepad().getAButton();

        
        this.shootSolenoid = new DoubleSolenoid(Constants.SHOOT_KICKER_FORWARD_CHANNEL, Constants.SHOOT_KICKER_REVERSE_CHANNEL);
        
        this.frontShootVictor = new Victor(Constants.FRONT_SHOOT_CHANNEL);
        this.backShootVictor = new Victor(Constants.BACK_SHOOT_CHANNEL);
        
        motorsSpinning = false;
    }
    
    /**
     * Called by teleopPeriodic() in main class
     */
    public void update(){
       if(shootBtn.get()){
           shootSolenoid.set(DoubleSolenoid.Value.kForward);
         
       } else{
           shootSolenoid.set(DoubleSolenoid.Value.kReverse);
//           System.out.println("not");
       }
       
       refreshMotors(motorToggle, high_motor_toggle);
       
    }
  
    /**
     * Toggles whether or not the shoot motors are turning
     */
    private void refreshMotors(ToggleableButton button, ToggleableButton high_button){
        
        if(button.isPressed()){
           motorsSpinning = !motorsSpinning;
           motorSpeed = true;
        }
        
        if(high_button.isPressed()){
           motorsSpinning = !motorsSpinning;
           motorSpeed = false;
        }
        
        if(motorsSpinning){
//            System.out.println("Spinning");
            if(motorSpeed){
                frontShootVictor.set(-Constants.FRONT_SHOOT_MOTOR_SPEED);
                backShootVictor.set(-Constants.BACK_SHOOT_MOTOR_SPEED);
            } else {
                if(Constants.FRONT_SHOOT_MOTOR_SPEED * Constants.HIGH_MULTIPLIER < Constants.HIGH_MAXIMUM){ 
                    frontShootVictor.set(-Constants.FRONT_SHOOT_MOTOR_SPEED * Constants.HIGH_MULTIPLIER);
                } else { 
                    frontShootVictor.set(-Constants.HIGH_MAXIMUM);
                }
                
                if(Constants.BACK_SHOOT_MOTOR_SPEED * Constants.HIGH_MULTIPLIER < Constants.HIGH_MAXIMUM){ 
                    backShootVictor.set(-Constants.BACK_SHOOT_MOTOR_SPEED * Constants.HIGH_MULTIPLIER);
                } else { 
                    backShootVictor.set(-Constants.HIGH_MAXIMUM);
                }
            }
        } else{
            frontShootVictor.set(0);
            backShootVictor.set(0);
        }
    }
    
        
}

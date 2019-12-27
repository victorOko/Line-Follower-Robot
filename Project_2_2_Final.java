
// Oyare Victor Oko
// Java 415 - Proj 2.2

import com.pi4j.io.gpio.*;
import com.pi4j.util.CommandArgumentParser;
import com.pi4j.util.Console;
import com.pi4j.wiringpi.Gpio;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

public class Project_2_2_Final {
    public static void main(String[] args) throws InterruptedException {

        Robot robot = new Robot("both", "reverse", 500, 500);

        robot.navigate(1, 1, 2, 3);
        robot.allStop();
       
    }
}

class Robot {
    GpioController gpio = GpioFactory.getInstance();

    private GpioPinDigitalOutput in1;
    private GpioPinDigitalOutput in2;
    private GpioPinDigitalOutput in3;
    private GpioPinDigitalOutput in4;

    private Pin pin;
    private Pin pin2;

    private GpioPinPwmOutput pwm;
    private GpioPinPwmOutput pwm2;

    private GpioPinDigitalInput leftSensor;
    private GpioPinDigitalInput rightSensor;

    private Console console;

    public Robot(String motor, String dir, int rSpeed, int lSpeed) throws InterruptedException { // Constructor
        // should contain the code required to initialize GPIO and set PWM
        // parameters should be in the constructor.
        // should accept the default speed to apply to each wheel.

        GpioController gpio = GpioFactory.getInstance();
        console = new Console();

        in1 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04);
        in2 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_05);
        in3 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_25);
        in4 = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_27);

        pin = (RaspiPin.GPIO_23);
        pin2 = (RaspiPin.GPIO_01);

        pwm = gpio.provisionPwmOutputPin(pin);
        pwm2 = gpio.provisionPwmOutputPin(pin2);

        // provision gpio pin #02 as an input pin with its internal pull down resistor
        leftSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_16, PinPullResistance.PULL_DOWN); // Left line sensor
        rightSensor = gpio.provisionDigitalInputPin(RaspiPin.GPIO_15, PinPullResistance.PULL_DOWN); // Right line sensor

        leftSensor.setShutdownOptions(true);

        
        leftSensor.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
            }
        });

        rightSensor.addListener(new GpioPinListenerDigital() {
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) 
            }
        });

        com.pi4j.wiringpi.Gpio.pwmSetMode(com.pi4j.wiringpi.Gpio.PWM_MODE_MS);
        com.pi4j.wiringpi.Gpio.pwmSetRange(1000);
        com.pi4j.wiringpi.Gpio.pwmSetClock(500);

        setDirection(motor, dir); // sets direction
        setSpeed(300, 300); // sets speed

    }

    public void navigate(int row, int col, int goalRow, int goalCol) throws InterruptedException {
        if (row == goalRow && col == goalCol) {
            System.out.println("Arrived!");
        } else if (row == goalRow) {
            System.out.println("I'm at the row");
        } else {
            followLine();
            row++;
            System.out.println(row);
        }

        if (col == goalCol) {
            System.out.println("I'm at the right colunm -  at the destination!");
        } else {
            turn("right");
            do {
                followLine();
                col++;
                System.out.println(col);
            } while (col == (goalCol+1);

        }
    }

    public void setDirection(String motor, String direction) throws InterruptedException {
        // Set the direction of the wheels

        if (motor.equals("left")) {
            if (direction.equals("forward")) {

                in1.high();
                in2.low();

            } else {
                in1.low();
                in2.high();
            }

        } else if (motor.equals("right")) {

            if (direction.equals("forward")) {
                in3.high();
                in4.low();

            } else {
                in3.low();
                in4.high();
            }
        }

        else {

            if (direction.equals("forward")) {
                in1.high();
                in2.low();

                in3.high();
                in4.low();
            } else {
                in1.low();
                in2.high();
                in3.low();
                in4.high();
            }
        }
    }

    public void setSpeed(int rSpeed, int lSpeed) throws InterruptedException {
        // Set the speed at which the wheels move in the given direction
        pwm.setPwm(rSpeed);
        pwm2.setPwm(lSpeed);

    }

    public void followLine() throws InterruptedException {
        // should accept a time out value
        // Will move the vehicle along the direction of the line and stop when
        // the vehicle reaches a T-junction (or later an intersection)
        // handle all the logic to follow the line.

        loop: while (true) {

            String Rstate;
            String off = "ON";
            String on = "OFF";

            if (rightSensor.getState() == PinState.HIGH) {
                Rstate = off;
            } else {
                Rstate = on;
            }

            String Lstate;
            if (leftSensor.getState() == PinState.HIGH) {
                Lstate = off;
            } else {
                Lstate = on;
            }

            int rVal = Rstate.compareTo(off);
            int lVal = Lstate.compareTo(off);
            // off is on the line
            // o is on the line

            if ((rVal == 0) && (lVal != 0)) { // right goes on line
                System.out.println("Car is moving LEFT");

                setSpeed(250, 400);
                console.println("PWM rate is: " + pwm.getPwm());
                console.println("PWM rate is: " + pwm2.getPwm());

            } else if ((lVal == 0) && (rVal != 0)) {
                System.out.println("Car is moving RIGHT");

                setSpeed(400, 250);
                console.println("PWM rate is: " + pwm.getPwm());
                console.println("PWM rate is: " + pwm2.getPwm());

                // System.out.println("LeftCounter: " + leftCounter + "+");

            } else if ((rVal != 0) && (lVal != 0)) {
                System.out.println("Car is moving SRAIGHT");

                setSpeed(360, 360);
                console.println("PWM rate is: " + pwm.getPwm());
                console.println("PWM rate is: " + pwm2.getPwm());

            } else if ((rVal == 0) && (lVal == 0)) {
                System.out.println("\nReached End of Line!");
                setSpeed(0, 0);
                break loop;

            }

            Thread.sleep(20);

        }

    }

    public void turn(String dir) throws InterruptedException {

        int direction;
        direction = dir.compareTo("left");

        if (direction == 0) { // if direction == left
            do {
                setSpeed(0, 400);
                System.out.println("Turning Left>>>>>>");
                Thread.sleep(1200);
            } while ((leftSensor.getState() == PinState.HIGH) && (rightSensor.getState() == PinState.HIGH));
            setSpeed(0, 0);
            System.out.println("line reached");
        } else {
            do {
                setSpeed(400, 0);
                System.out.println("Turning Right>>>>>>");
                Thread.sleep(1300);
            } while ((rightSensor.getState() == PinState.HIGH) && (leftSensor.getState() == PinState.HIGH));
            setSpeed(0, 0);
            System.out.println("line reached");
        }
        System.out.println("Done Turning");
    }

    public void allStop() throws InterruptedException { // code required to stop GPIO activity
        setSpeed(0, 0);
        gpio.shutdown();
        gpio.unprovisionPin(pwm);
        gpio.unprovisionPin(pwm2);
        gpio.unprovisionPin(in1);
        gpio.unprovisionPin(in2);
        gpio.unprovisionPin(in3);
        gpio.unprovisionPin(in4);
    }

}

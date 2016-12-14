package org.firstinspires.ftc.teamcode;

import android.util.Log;

import com.qualcomm.hardware.adafruit.AdafruitBNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.exception.RobotCoreException;

import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;

/**
 * Created by Owner on 8/31/2015.
 */
@Autonomous(name="Compass2", group="Compass")
public class IMUTest extends LinearOpMode {

    AdafruitIMU boschBNO055;

    //The following arrays contain both the Euler angles reported by the IMU (indices = 0) AND the
    // Tait-Bryan angles calculated from the 4 components of the quaternion vector (indices = 1)
    volatile double[] rollAngle = new double[2], pitchAngle = new double[2], yawAngle = new double[2];

    long systemTime;//Relevant values of System.nanoTime

    /************************************************************************************************
     * The following method was introduced in the 3 August 2015 FTC SDK beta release and it runs
     * before "start" runs.
     */
    @Override
    public void runOpMode() throws InterruptedException {

        telemetry.addData("Point", "5");
        telemetry.update();
        systemTime = System.nanoTime();
        telemetry.addData("Point", "6");
        telemetry.update();
        //Set up the IMU as needed for a continual stream of I2C reads.
        telemetry.addData("Point", "7");
        telemetry.update();
        telemetry.addData("FtcRobotController", "IMU Start method finished in: "
                + (-(systemTime - (systemTime = System.nanoTime()))) + " ns.");
        telemetry.addData("Point", "8");
        telemetry.update();


        telemetry.addData("Point", "1");
        telemetry.update();
        systemTime = System.nanoTime();
        telemetry.addData("Point", "2");
        telemetry.update();
        Log.i("testing", "hi");
        try {
            telemetry.addData("Point", "3");
            telemetry.update();
            boschBNO055 = new AdafruitIMU(hardwareMap, "bno055"

                    //The following was required when the definition of the "I2cDevice" class was incomplete.
                    //, "cdim", 5

                    , (byte)(AdafruitIMU.BNO055_ADDRESS_A * 2)//By convention the FTC SDK always does 8-bit I2C bus]

                    //addressing
                    , (byte)AdafruitIMU.OPERATION_MODE_IMU);
            telemetry.addData("Point", "4");
            telemetry.update();
        } catch (RobotCoreException e){
            telemetry.addData("FtcRobotController", "Exception: " + e.getMessage());
            telemetry.update();
        }
        telemetry.addData("FtcRobotController", "IMU Init method finished in: "
                + (-(systemTime - (systemTime = System.nanoTime()))) + "ms.");
        telemetry.update();

        boschBNO055.startIMU();
        //ADDRESS_B is the "standard" I2C bus address for the Bosch BNO055 (IMU data sheet, p. 90).
        //BUT DAVID PIERCE, MENTOR OF TEAM 8886, HAS EXAMINED THE SCHEMATIC FOR THE ADAFRUIT BOARD ON
        //WHICH THE IMU CHIP IS MOUNTED. SINCE THE SCHEMATIC SHOWS THAT THE COM3 PIN IS PULLED LOW,
        //ADDRESS_A IS THE IMU'S OPERATIVE I2C BUS ADDRESS
        //IMU is an appropriate operational mode for FTC competitions. (See the IMU datasheet, Table
        // 3-3, p.20 and Table 3-5, p.21.)
        waitForStart();
        telemetry.addData("BEGINNING", "True");
        telemetry.update();
        while(opModeIsActive()) {
            boschBNO055.getIMUGyroAngles(rollAngle, pitchAngle, yawAngle);

		/*
		 * Send whatever telemetry data you want back to driver station.
		 */
            //telemetry.addData("Text", "*** Robot Data***");
            telemetry.addData("Headings(yaw): ",
                    String.format("Euler= %4.5f, Quaternion calculated= %4.5f", yawAngle[0], yawAngle[1]));

            telemetry.addData("Pitches: ",
                    String.format("Euler= %4.5f, Quaternion calculated= %4.5f", pitchAngle[0], pitchAngle[1]));
            telemetry.addData("Max I2C read interval: ",
                    String.format("%4.4f ms. Average interval: %4.4f ms.", boschBNO055.maxReadInterval
                            , boschBNO055.avgReadInterval));
            telemetry.update();
        }

    }

    /************************************************************************************************
     * Code to run when the op mode is first enabled goes here
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#start()
     */


    /***********************************************************************************************
     * This method will be called repeatedly in a loop
     * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#loop()
     * NOTE: BECAUSE THIS "loop" METHOD IS PART OF THE OVERALL OpMode/EventLoop/ReadWriteRunnable
     * MECHANISM, ALL THAT THIS METHOD WILL BE USED FOR, IN AUTONOMOUS MODE, IS TO:
     * 1. READ SENSORS AND ENCODERS AND STORE THEIR VALUES IN SHARED VARIABLES
     * 2. WRITE MOTOR POWER AND CONTROL VALUES STORED IN SHARED VARIABLES BY "WORKER" THREADS, AND
     * 3. SEND TELELMETRY DATA TO THE DRIVER STATION
     * THIS "loop" METHOD IS THE ONLY ONE THAT "TOUCHES" ANY SENSOR OR MOTOR HARDWARE.
     */


    /*
    * Code to run when the op mode is first disabled goes here
    * @see com.qualcomm.robotcore.eventloop.opmode.OpMode#stop()
    */

}

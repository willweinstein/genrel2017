package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.adafruit.BNO055IMU;
import com.qualcomm.hardware.adafruit.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.navigation.*;
import org.firstinspires.ftc.robotcore.internal.VuforiaTrackablesImpl;

/**
 * This is NOT an opmode.
 *
 * This class can be used to define all the specific hardware for a single robot.
 * This hardware class assumes the following device names have been configured on the robot:
 *
 * Motor channel:  Left  drive motor:        "leftFront"
 * Motor channel:  Right drive motor:        "rightFront"
 * Motor channel:  Left  drive motor:        "leftBack"
 * Motor channel:  Right drive motor:        "rightBack"
 *
 */
public class HardwareBot1
{
    public enum direction {
        RIGHT, LEFT
    }

    public double[] speeds = new double[4];

    /* Public OpMode members. */
    public DcMotor leftFront = null;
    public DcMotor leftBack = null;
    public DcMotor rightFront = null;

    public DcMotor rightBack = null;
    public BNO055IMU compass = null;


    /* Local OpMode members. */
    HardwareMap hwMap  = null;
    Telemetry tele = null;
    private ElapsedTime period  = new ElapsedTime();

    /* Constructor */
    public HardwareBot1() {
    }

    private void powers(double[] pows) {
        tele.addData("speeds", pows[0] + " " + pows[1] + " " + pows[2] + " " + pows[3]);
        speeds = scalePowers(pows);
        this.rightFront.setPower(speeds[0]);
        this.rightBack.setPower(speeds[1]);
        this.leftFront.setPower(speeds[2]);
        this.leftBack.setPower(speeds[3]);
        tele.addData("speeds", speeds[0] + " " + speeds[1] + " " + speeds[2] + " " + speeds[3]);

    }

    private double[] getTurnPowers(float target) {
        double[] returns = {0, 0, 0, 0};
        if(getCompass() - target < -1) {
            returns = new double[] {-1, -1, 1, 1};
        } else if(getCompass() - target > 1) {
            returns = new double[] {1, 1, -1, -1};
        } else {
            returns = new double[] {0, 0, 0, 0};
        }
        return returns;
    }
    public void stop() {
        double[] power = {0, 0, 0, 0};
        powers(power);
    }

    public float getCompass() {
        Orientation angles = compass.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX);
        return AngleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle);
    }


    private double[] scalePowers(double[] pows) {
        double highest = 0;
        double[] output = new double[4];
        HardwareBot1[] hi = new HardwareBot1[2];
        for(double pow : pows) {
            if(Math.abs(pow) > Math.abs(highest)) {
                highest = pow;
            }
        }
        if(Math.abs(highest) > 1) {
            for (int i = 0; i < pows.length; i++) {
                output[i] = pows[i] / Math.abs(highest);
            }
        } else {
            output = pows;
        }
        return output;
    }

    public void diagonalForward(direction dir, double power) {
        double[] setPowers = new double[4];
        switch (dir) {
            case RIGHT:
                setPowers = new double[] {power, 0, 0, power};
                break;
            case LEFT:
                setPowers = new double[] {0, power, power, 0};
                break;
            default:
                setPowers = new double[] {0, 0, 0, 0};
                break;
        }
        powers(setPowers);
    }

    public void moveForward(double power) {
        double[] setPowers = {power, power, power, power};
        powers(setPowers);
    }
    public void moveBackward(double power) {
        double[] setPowers = {-power, -power, -power, -power};
        powers(setPowers);
    }

    public void moveForward(double power, float comp) {
        double[] setPowers = {power, power, power, power};
        double[] turnPowers = multArray(getTurnPowers(comp), -0.4 * power);
        powers(avgArrays(turnPowers, setPowers));
    }

    public void moveSide(HardwareBot1.direction dir, double power) {
        double[] setPowers = new double[4];
        switch (dir) {
            case RIGHT:
                setPowers = new double[] {power, -power, -power, power};
                break;
            case LEFT:
                setPowers = new double[] {-power, power, power, -power};
                break;
            default:
                setPowers = new double[] {0, 0, 0, 0};
                break;
        }
        powers(setPowers);
    }
    public void turn(HardwareBot1.direction dir, double power) {
        double[] setPowers = new double[4];
        switch (dir) {
            case LEFT:
                setPowers = new double[] {-power, -power, power, power};
                break;
            case RIGHT:
                setPowers = new double[] {power, power, -power, -power};
                break;
            default:
                setPowers = new double[] {0, 0, 0, 0};
                break;
        }
        powers(setPowers);
    }
    public void turnComp(float tar, double power) {
        powers(multArray(getTurnPowers(tar), power));
    }


    public void moveSide(HardwareBot1.direction dir, double power, float comp) {
        double[] setPowers = new double[4];
        switch (dir) {
            case RIGHT:
                setPowers = new double[] {power , -power, -power, power};
                break;
            case LEFT:
                setPowers = new double[] {-power, power, power, -power};
                break;
            default:
                setPowers = new double[] {0, 0, 0, 0};
                break;
        }

        double[] turnPowers = multArray(getTurnPowers(comp), -0.5 * power);
        powers(avgArrays(turnPowers, setPowers));
    }

    public void moveAng(double x, double y, double speed) {
        double rf = 0;
        double rb = 0;
        double lf = 0;
        double lb = 0;

        if(Math.abs(x) < 0.1) {
            x = 0;
        }

        if(Math.abs(y) < 0.1) {
            y = 0;
        }

        rf += 0.5 * y;
        rb += 0.5 * y;
        lf += 0.5 * y;
        lb += 0.5 * y;

        rf += -0.5 * x;
        rb += 0.5 * x;
        lf += 0.5 * x;
        lb += -0.5 * x;
        double[] returns = {rf, rb, lf, lb};
        powers(scaleArrayToVal(returns, speed));
    }

    private double[] sumArrays(double[] ar1, double[] ar2) {
        if(ar1.length != ar2.length) {
            return null;
        }
        double[] returnArray = new double[ar1.length];
        for(int i = 0; i < ar1.length; i++) {
            returnArray[i] = ar1[i] + ar2[i];
        }
        return returnArray;
    }

    public double[] scaleArrayToVal(double[] ar, double tar) {
        double highest = 0;
        for(int i = 0; i < ar.length; i++) {
            if(Math.abs(ar[i]) > highest) {
                highest = Math.abs(ar[i]);
            }
        }
        if(highest == 0) {
            return ar;
        }
        return multArray(ar, tar/highest);
    }

    public double[] scaleArrayToMax(double[] ar) {
        double highest = 0;
        for(int i = 0; i < ar.length; i++) {
            if(ar[i] > highest) {
                highest = ar[i];
            }
        }
        return multArray(ar, 1/highest);
    }


    private double[] avgArrays(double[] ar1, double[] ar2) {
        if(ar1.length != ar2.length) {
            return null;
        }
        double[] returnArray = new double[ar1.length];
        for(int i = 0; i < ar1.length; i++) {
            returnArray[i] = (ar1[i] + ar2[i])/2;
        }
        return returnArray;
    }

    private double[] multArray(double[] ar1, double ar2) {
        double[] returnArray = new double[ar1.length];
        for(int i = 0; i < ar1.length; i++) {
            returnArray[i] = ar1[i] * ar2;
        }
        return returnArray;
    }


    /* Initialize standard Hardware interfaces */
    public void init(HardwareMap ahwMap, Telemetry tel) {
        // save reference to HW Map
        hwMap = ahwMap;
        this.tele = tel;
        //set up compass
        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu"
        compass = hwMap.get(BNO055IMU.class, "imu");
        compass.initialize(parameters);

        // Define and Initialize Motors
        rightFront   = hwMap.dcMotor.get("frontRight");
        rightBack  = hwMap.dcMotor.get("backRight");
        leftFront   = hwMap.dcMotor.get("frontLeft");
        leftBack = hwMap.dcMotor.get("backLeft");
        leftBack.setDirection(DcMotor.Direction.REVERSE);
        leftFront.setDirection(DcMotor.Direction.REVERSE);

        // Set all motors to zero power
        leftBack.setPower(0);
        leftFront.setPower(0);
        rightBack.setPower(0);
        rightFront.setPower(0);
        // Set all motors to run without encoders.
        // May want to use RUN_USING_ENCODERS if encoders are installed.
        leftBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightBack.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightFront.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        // Define and initialize ALL installed servos.

    }

    /***
     *
     * waitForTick implements a periodic delay. However, this acts like a metronome with a regular
     * periodic tick.  This is used to compensate for varying processing times for each cycle.
     * The function looks at the elapsed cycle time, and sleeps for the remaining time interval.
     *
     * @param periodMs  Length of wait cycle in mSec.
     * @throws InterruptedException
     */
    public void waitForTick(long periodMs)  throws InterruptedException {
        long  remaining = periodMs - (long)period.milliseconds();

        // sleep for the remaining portion of the regular cycle period.
        if (remaining > 0)
            Thread.sleep(remaining);

        // Reset the cycle clock for the next pass.
        period.reset();
    }
    public long time() {
        return (long)period.milliseconds();
    }
}

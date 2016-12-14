package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.adafruit.BNO055IMU;
import com.qualcomm.hardware.adafruit.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by student on 9/22/16.
 */
@Autonomous(name = "Auto1", group="Vuforia")
public class TestAuto extends LinearOpMode {
    enum phase {
        DRIVING, TURNING, ALIGNING, PUSHING, REALIGNING
    }
    BNO055IMU compaz;
    Orientation angles;
    HardwareBot1 bot = new HardwareBot1();
    @Override
    public void runOpMode() throws InterruptedException {
        String target;
        float deg;
        float dist = 0;
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);
        VuforiaLocalizer.Parameters params = new VuforiaLocalizer.Parameters(R.id.cameraMonitorViewId);
        params.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;
        params.vuforiaLicenseKey = "AQCPMDz/////AAAAGc0kTf6MsEGliq5btBScuZ1PxNDf9hmIoT9QS7RpEP0NNm1rMJ4/sfbvHtjqYRdwOAUBKv6sWLLtNYIVswhcwhF6oBQ2FuYMFHfD48+NNaVy2WFxmnCwvzb4yE2rEkFBe/lfkL2xT19SOOJzVOw9tpGdUjkyUlEbpzkoFQyhN4T7QE1UfYBp3/u2Qdq7JC96D63wmHtORDk6sSwtpPj6V8XZ2YRZJMg1KoSsNvzcFZ618iLzIqdrkOX193TU1G/qvHbqhzy6+M2YlYonEiJXxbOGHHRKxNj+znqKK88GxQ31PYdn2qCGXb1R0FCSpigIvT5debYFpzweYOV336Bv/Q3AbZyWWv5DbX6TSMBtHQET";
        params.cameraMonitorFeedback = VuforiaLocalizer.Parameters.CameraMonitorFeedback.TEAPOT;
        VuforiaLocalizer vuforia = ClassFactory.createVuforiaLocalizer(params);
        Vuforia.setHint(HINT.HINT_MAX_SIMULTANEOUS_IMAGE_TARGETS, 4);

        VuforiaTrackables beacons = vuforia.loadTrackablesFromAsset("FTC_2016-17");
        beacons.get(0).setName("Wheels");
        beacons.get(1).setName("Tools");
        beacons.get(2).setName("Legos");
        beacons.get(3).setName("Gears");


        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();
        parameters.angleUnit           = BNO055IMU.AngleUnit.DEGREES;
        parameters.accelUnit           = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;
        parameters.calibrationDataFile = "AdafruitIMUCalibration.json"; // see the calibration sample opmode
        parameters.loggingEnabled      = true;
        parameters.loggingTag          = "IMU";
        parameters.accelerationIntegrationAlgorithm = new JustLoggingAccelerationIntegrator();

        // Retrieve and initialize the IMU. We expect the IMU to be attached to an I2C port
        // on a Core Device Interface Module, configured to be a sensor of type "AdaFruit IMU",
        // and named "imu".
        compaz = hardwareMap.get(BNO055IMU.class, "imu");
        compaz.initialize(parameters);

        waitForStart();

        beacons.activate();
        target = "Gears";
        deg = 0;
        double power = -0.3;
        VectorF tar = VectorF.length(6);
        phase curPhase = phase.DRIVING;
        dist = 13;
        float compazReading;
        boolean found = false;
        double alignDeg = 0;
        String com ="";
        String targ = "";
        bot.rightFront.setPower(power);
        bot.rightBack.setPower(power);
        bot.leftFront.setPower(power);
        bot.leftBack.setPower(power);
        bot.waitForTick(3000);
        angles = compaz.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX);
        compazReading = AngleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle);
        while(compazReading - 45 < -1) {
            angles = compaz.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX);
            compazReading = AngleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle);
            bot.rightFront.setPower(power/3);
            bot.rightBack.setPower(power/3);
            bot.leftFront.setPower(-power/3);
            bot.leftBack.setPower(-power/3);
            telemetry.addData("Compass Reading", compazReading);
            telemetry.update();
            idle();
        }
        bot.rightFront.setPower(0);
        bot.rightBack.setPower(0);
        bot.leftFront.setPower(0);
        bot.leftBack.setPower(0);

        while (opModeIsActive())  {
            angles = compaz.getAngularOrientation().toAxesReference(AxesReference.INTRINSIC).toAxesOrder(AxesOrder.ZYX);
            compazReading = AngleUnit.DEGREES.fromUnit(angles.angleUnit, angles.firstAngle);
            for (VuforiaTrackable beacon : beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beacon.getListener()).getPose();
                //((VuforiaTrackableDefaultListener) beacon.getListener())
                if(pose != null) {
                    VectorF translation = pose.getTranslation();
                    translation = toInches(translation);
                    telemetry.addData(beacon.getName(), translation);
                    // java is a meme

                    float degreesToTurn = 90 + Float.valueOf(String.valueOf(Math.toDegrees(Math.atan2(translation.get(2), -translation.get(0)))));


                    if (beacon.getName().equals(target)) {

                        float[] data = pose.getData();
                        float[][] rotation = {{data[0], data[1], data[2]},
                                {data[4], data[5], data[6]},
                                {data[8], data[9], data[10]}};

                        double thetaX = Math.atan2(rotation[2][1], rotation[2][2]); //idk
                        double thetaY = Math.atan2(-rotation[2][0], Math.sqrt(rotation[2][1] * rotation[2][1] + rotation[2][2] * rotation[2][2])); //yaw??
                        double thetaZ = Math.atan2(rotation[1][0], rotation[0][0]); //idk
                        telemetry.addData("yaw", thetaY);
                        alignDeg = compazReading - thetaY * 100;
                        dist = translation.get(2);
                        tar = translation;
                        deg = compazReading + degreesToTurn;
                        if(!found) {
                            found = true;
                        }
                    }
                }
            }

            switch (curPhase) {
                case DRIVING:
                    if(!found) {
                        bot.rightFront.setPower(0.5 * power);
                        bot.rightBack.setPower(0.5 * power);
                        bot.leftFront.setPower(0.5 * power);
                        bot.leftBack.setPower(0.5 * power);
                    } else if(Math.abs(deg - compazReading) >= 10 && Math.abs(dist) > 16) {
                        if(deg - compazReading > 0) {
                            bot.rightFront.setPower(power/3);
                            bot.rightBack.setPower(power/3);
                            bot.leftFront.setPower(-power/3);
                            bot.leftBack.setPower(-power/3);
                            telemetry.addData("drive", "turnoneway");
                        } else {
                            bot.rightFront.setPower(-power/3);
                            bot.rightBack.setPower(-power/3);
                            bot.leftFront.setPower(power/3);
                            bot.leftBack.setPower(power/3);
                            telemetry.addData("drive", "turnotherway");
                        }
                    } else if(Math.abs(dist) > 16) {
                        bot.rightFront.setPower(power/2);
                        bot.rightBack.setPower(power/2);
                        bot.leftFront.setPower(power/2);
                        bot.leftBack.setPower(power/2);
                        telemetry.addData("drive", "forward");
                    } else {
                        bot.rightFront.setPower(0);
                        bot.rightBack.setPower(0);
                        bot.leftFront.setPower(0);
                        bot.leftBack.setPower(0);
                        telemetry.addData("drive", "stop");
                        com = String.valueOf(compazReading);
                        targ = String.valueOf(alignDeg);
                        curPhase = phase.TURNING;
                    }
                    break;
                case TURNING:
                    telemetry.addData("turning", "true");
                    telemetry.addData("start Compass", com);
                    telemetry.addData("start target compass", targ);
                    telemetry.addData("cur compass", compazReading);
                    telemetry.addData("curTarget", alignDeg);
                    if(compazReading - 90 < -1) {
                        bot.rightFront.setPower(power/3);
                        bot.rightBack.setPower(power/3);
                        bot.leftFront.setPower(-power/3);
                        bot.leftBack.setPower(-power/3);

                    } else {
                        bot.rightFront.setPower(0);
                        bot.rightBack.setPower(0);
                        bot.leftFront.setPower(0);
                        bot.leftBack.setPower(0);
                        curPhase = phase.ALIGNING;
                    }
                    /*bot.rightFront.setPower(0);
                    bot.rightBack.setPower(0);
                    bot.leftFront.setPower(0);
                    bot.leftBack.setPower(0);*/

                    break;
                case ALIGNING:
                    if(tar.get(0) > 0.5) {
                        telemetry.addData("aligning", "true");
                        bot.rightFront.setPower(-power/2);
                        bot.rightBack.setPower(power/2);
                        bot.leftFront.setPower(power/2);
                        bot.leftBack.setPower(-power/2);
                    } else if(tar.get(0) < -0.5) {
                        telemetry.addData("aligning", "true");
                        bot.rightFront.setPower(power/2);
                        bot.rightBack.setPower(-power/2);
                        bot.leftFront.setPower(-power/2);
                        bot.leftBack.setPower(power/2);
                    } else {
                        telemetry.addData("aligning", "aligned");
                        bot.rightFront.setPower(0);
                        bot.rightBack.setPower(0);
                        bot.leftFront.setPower(0);
                        bot.leftBack.setPower(0);
                    }
                    break;
                default:
                    telemetry.addData("confused", "true");
                    break;
            }


            telemetry.update();
        }

    }
    private float getInchDistance(float dist) {
        return dist/25.4F;
    }
    private VectorF toInches(VectorF t) {
       for (int i = 0; i < t.length(); i++) {
            t.put(i, getInchDistance(t.get(i)));
       }
       return t;
    }
}

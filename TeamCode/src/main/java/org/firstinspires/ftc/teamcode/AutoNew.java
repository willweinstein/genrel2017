package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by student on 12/15/16.
 */
@Autonomous(name="autonew1")
public class AutoNew extends LinearOpMode {

    enum phase {
        DRIVING, TURNING, ALIGNING, FIXALIGN, PUSHING1, PUSHING2, PUSHING3, REALIGNING, END
    }
    HardwareBot1 bot = new HardwareBot1();
    ColorSensor sensorRGB;
    ElapsedTime clock = new ElapsedTime();
    Servo pusher;
    @Override
    public void runOpMode() throws InterruptedException {
        String target;
        float deg;
        float dist;
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);
        sensorRGB = hardwareMap.colorSensor.get("color");
        pusher = hardwareMap.servo.get("pusher");
        pusher.setPosition(1);
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


        beacons.activate();

        waitForStart();
        pusher.setPosition(0);


        target = "Gears";
        deg = 0;
        double power = -0.2;
        VectorF tar = VectorF.length(6);
        phase curPhase = phase.DRIVING;
        dist = 21;
        float compazReading;
        boolean found = false;
        String bColor = "";
        double alignDeg = 0;
        int beaconNum = 1;
        String com ="";
        String targ = "";
        String col = "";
        boolean slept = false;
        boolean read = false;
        double starTime = clock.milliseconds();
        boolean timeSaved = false;
        double timeS = 0;
        compazReading = bot.getCompass();
        while(clock.milliseconds() - starTime < 2400) {
            bot.moveForward(power);
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 1000) {
            bot.stop();
            idle();
        }
        while(compazReading - 45 < -1) {
            compazReading = bot.getCompass();
            bot.turn(HardwareBot1.direction.LEFT, power);
            telemetry.addData("Compass Reading", compazReading);
            telemetry.update();
            idle();
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 1000) {
            bot.stop();
            idle();
        }
        while(compazReading - 90 < -1) {
            compazReading = bot.getCompass();
            bot.turn(HardwareBot1.direction.LEFT, power);
            telemetry.addData("Compass Reading", compazReading);
            telemetry.update();
            idle();
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 10000) {
            bot.stop();
            idle();
        }
        bot.stop();
        String argh = "";
        while (dist > 20)  {
            compazReading = bot.getCompass();
            telemetry.addData("compass", compazReading);
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
                        alignDeg = compazReading - thetaY * 100;
                        dist = Math.abs(translation.get(2));
                        tar = translation;
                        deg = compazReading + degreesToTurn;
                        found = true;
                        telemetry.addData("I see", "gears");
                    } else {
                        telemetry.addData("not a gear!", beacon.getName());
                    }
                } else if(beacon.getName().equals(target)) {
                    found = false;
                }
            }

            bot.moveForward(power);


            telemetry.update();
            idle();
        }
        bot.stop();
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 500) {
            idle();
        }
        float hsvValues[] = {0F, 0F, 0F};
        Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);
        if (hsvValues[0] > 140 && hsvValues[0] < 310) {
            col = "blue " + String.valueOf(hsvValues[0]);
            read = true;
            bColor = "blue";
            pusher.setPosition(1);
        } else {
            col = "red " + String.valueOf(hsvValues[0]);
            read = true;
            bColor = "red";
            pusher.setPosition(0);
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 500) {
            idle();
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 2000) {
            bot.moveForward(power);
        }
        timeS = clock.milliseconds();
        while (clock.milliseconds() - timeS < 1000) {
            bot.moveBackward(power);
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

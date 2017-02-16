package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.vuforia.CameraDevice;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;
import org.firstinspires.ftc.robotcore.internal.VuforiaLocalizerImpl;

/**
 * Created by student on 9/22/16.
 */
@Autonomous(name = "Vuforia1", group="Vuforia")
public class TestVuforia extends LinearOpMode {
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

        waitForStart();

        beacons.activate();
        target = "Gears";
        deg = 0;
        double power = 0;
        while (opModeIsActive())  {
            for (VuforiaTrackable beacon : beacons) {
                OpenGLMatrix pose = ((VuforiaTrackableDefaultListener) beacon.getListener()).getPose();
                //((VuforiaTrackableDefaultListener) beacon.getListener())
                if(pose != null) {
                    VectorF translation = pose.getTranslation();
                    translation = toInches(translation);

                    telemetry.addData(beacon.getName(), translation);
                    // java is a meme

                    float degreesToTurn = (float) Math.toDegrees(Math.atan2(translation.get(1), -translation.get(2)));



                    if(beacon.getName().equals(target)) {
                        deg = degreesToTurn;
                        dist = translation.get(2);
                    }
                } else if(beacon.getName().equals(target)) {
                    deg = 0;
                }
            }

            telemetry.addData("deg", deg);

            if(Math.abs(deg) >= 10 && Math.abs(dist) > 12) {
                if(deg > 0) {
                    bot.rightFront.setPower(power);
                    bot.rightBack.setPower(power);
                    bot.leftFront.setPower(-power);
                    bot.leftBack.setPower(-power);
                } else {
                    bot.rightFront.setPower(-power);
                    bot.rightBack.setPower(-power);
                    bot.leftFront.setPower(power);
                    bot.leftBack.setPower(power);
                }
            } else if(Math.abs(dist) > 12) {
                bot.rightFront.setPower(-power);
                bot.rightBack.setPower(-power);
                bot.leftFront.setPower(-power);
                bot.leftBack.setPower(-power);
            } else {
                bot.rightFront.setPower(0);
                bot.rightBack.setPower(0);
                bot.leftFront.setPower(0);
                bot.leftBack.setPower(0);
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

package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
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

/**
 * Created by student on 9/22/16.
 */
@Autonomous(name = "BlueAuto", group="Vuforia")
public class AutoWithBlue extends LinearOpMode {
    enum phase {
        DRIVING, TURNING, ALIGNING, FIXALIGN, PUSHING1, PUSHING2, PUSHING3, REALIGNING, END, SALIGN
    }
    HardwareBot1 bot = new HardwareBot1();
    ColorSensor sensorRGB;
    ElapsedTime clock = new ElapsedTime();
    Servo pusherL;
    Servo pusherR;
    DcMotor shooter;
    @Override
    public void runOpMode() throws InterruptedException {
        double alignTime = 0;
        boolean alignSaved = false;
        double timeAc = 0;
        String target;
        float deg;
        float dist;
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);
        sensorRGB = hardwareMap.colorSensor.get("buttonSensor");
        pusherL = hardwareMap.servo.get("leftPusher");
        pusherR = hardwareMap.servo.get("rightPusher");
        shooter = hardwareMap.dcMotor.get("shooter");
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

        pusherL.setPosition(1);
        pusherR.setPosition(0);

        beacons.activate();
        telemetry.addData("READY!", "HIT PLAY!");
        telemetry.update();
        CameraDevice.getInstance().setFlashTorchMode(true);
        waitForStart();



        target = "Wheels";
        deg = 0;
        double power = -0.7;
        VectorF tar = VectorF.length(6);
        phase curPhase = phase.DRIVING;
        dist = 13;
        float compazReading;
        boolean found = false;
        String bColor = "";
        double alignDeg = 0;
        int beaconNum = 1;
        double initialCompassReading = bot.getCompass();
        String com ="";
        String targ = "";
        String col = "";
        boolean slept = false;
        boolean read = false;
        double starTime = clock.milliseconds();
        boolean timeSaved = false;
        double timeS = 0;
        compazReading = bot.getCompass();
        while(clock.milliseconds() - starTime < 200) {
            bot.moveForward(power);
        }


        double endTurn = clock.milliseconds();
        while(clock.milliseconds() - endTurn < 300) {
            bot.stop();
        }
        while(clock.milliseconds() - endTurn < 2500) {
            shooter.setPower(0.5);
        }
        shooter.setPower(0);
        while(compazReading + 47 > 1) {
            compazReading = bot.getCompass();
            bot.turn(HardwareBot1.direction.LEFT, power/4); //fixed
            telemetry.addData("Compass Reading", compazReading);
            telemetry.update();
            idle();
        }
        String argh = "";
        while (opModeIsActive()) {
            telemetry.addData("phase", "" + curPhase.name());
            compazReading = bot.getCompass();
            //telemetry.addData("compass", compazReading);
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
                        if(!found) {
                            found = true;
                            telemetry.addData("I see", "gears");
                        } else {
                            // telemetry.addData("I see", "no gears");
                        }
                    } else {
                        //telemetry.addData("not a gear!", beacon.getName());
                    }
                } else if(beacon.getName().equals(target)) {
                    found = false;
                }
            }

            switch (curPhase) {
                case DRIVING:
                    if(!found) {
                        bot.moveForward(power/3);
                    } else if(Math.abs(deg - compazReading) >= 10 && dist > 34) {
                        bot.turnComp(deg, power/3);
                        telemetry.addData("drive", "face");
                    } else if(dist > 34) {
                        bot.moveForward(power/3);
                        telemetry.addData("drive", "forward");
                    } else {
                        bot.stop();
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
                    if(compazReading + 84 > 1) {
                        bot.turn(HardwareBot1.direction.LEFT, power/3); //fixed
                    } else {
                        bot.stop();
                        curPhase = phase.ALIGNING;

                    }
	/*bot.rightFront.setPower(0);
	 bot.rightBack.setPower(0);
	 bot.leftFront.setPower(0);
	 bot.leftBack.setPower(0);*/

                    break;
                case ALIGNING:
                    if(!alignSaved) {
                        alignSaved = true;
                        alignTime = clock.milliseconds();
                    }
                    if(clock.milliseconds() - alignTime > 3500) {
                        curPhase = phase.END;
                        bot.stop();
                        break;
                    }
                    telemetry.addData("0", tar.get(0));
                    telemetry.addData("1", tar.get(1));
                    telemetry.addData("2", tar.get(2));
                    if(!found) {
                        telemetry.addData("aligning", "not found yet :(");
                        bot.moveSide(HardwareBot1.direction.RIGHT, power / 2);
                    }
                    if(tar.get(1) > -0.5) {
                        telemetry.addData("aligning", "right");
                        bot.moveSide(HardwareBot1.direction.LEFT, power / 2); //fixed
                    } else if(tar.get(1) < -1.5) {
                        telemetry.addData("aligning", "left");
                        bot.moveSide(HardwareBot1.direction.RIGHT, power / 2); //fixed
                    } else {
                        telemetry.addData("aligning", "aligned");
                        curPhase = phase.FIXALIGN;
                        alignSaved = false;
                        argh += tar.get(1);
                        bot.stop();
                    }
                    break;
                case FIXALIGN:
                    telemetry.addData("fixing", "true");
                    telemetry.addData("start Compass", com);
                    telemetry.addData("start target compass", targ);
                    telemetry.addData("cur compass", compazReading);
                    telemetry.addData("curTarget", alignDeg);
                    if(compazReading + 82 > 1) {
                        bot.turn(HardwareBot1.direction.LEFT, power/3); //fixed
                    } else {
                        bot.stop();
                        curPhase = phase.PUSHING1;

                    }
	/*bot.rightFront.setPower(0);
	 bot.rightBack.setPower(0);
	 bot.leftFront.setPower(0);
	 bot.leftBack.setPower(0);*/

                    break;
                case SALIGN:
                    if(clock.milliseconds() - timeAc < 200) {
                        bot.stop();
                        break;
                    }
                    telemetry.addData("fixing", "true");
                    telemetry.addData("start Compass", com);
                    telemetry.addData("start target compass", targ);
                    telemetry.addData("cur compass", compazReading);
                    telemetry.addData("curTarget", alignDeg);
                    if(compazReading + 87 > 1) {
                        bot.turn(HardwareBot1.direction.LEFT, power/3); //fixed
                    } else if(compazReading + 87 < -10) {
                        bot.turn(HardwareBot1.direction.RIGHT, power/3);
                    } else {
                        curPhase = phase.ALIGNING;
                        bot.stop();
                    }
	/*bot.rightFront.setPower(0);
	 bot.rightBack.setPower(0);
	 bot.leftFront.setPower(0);
	 bot.leftBack.setPower(0);*/

                    break;
                case PUSHING1:
                    telemetry.addData("phase", "pushing1");

                    if(found && dist > 10) {
                        bot.moveForward(power /2, -90);
                    } else {
                        bot.stop();
                        if(!slept) {
                            Thread.sleep(600);
                            slept = true;
                        }

                        float hsvValues[] = {0F, 0F, 0F};
                        Color.RGBToHSV((sensorRGB.red() * 255) / 800, (sensorRGB.green() * 255) / 800, (sensorRGB.blue() * 255) / 800, hsvValues);

                        if (!read) {
                            if (hsvValues[0] != 0) {
                                if (hsvValues[0] > 140 && hsvValues[0] < 340) {
                                    col = "blue " + String.valueOf(hsvValues[0]);
                                    read = true;
                                    bColor = "blue";
                                  //  if (beaconNum == 1) {
                                    pusherL.setPosition(0);
                                    pusherR.setPosition(0);

                                } else {
                                    col = "red " + String.valueOf(hsvValues[0]);
                                    read = true;
                                    bColor = "red";
                                    pusherL.setPosition(1);
                                    pusherR.setPosition(1);
                                }
                            }
                            telemetry.addData("color", col);
                        } else if (pusherL.getPosition() == 1 || pusherL.getPosition() == 0) {
                            curPhase = phase.PUSHING2;
                            slept = true;
                        }
                    }
                    break;
                case PUSHING2:
                    if(!timeSaved) {
                        timeS = clock.seconds();
                        timeSaved = true;
                    }
                    if(clock.seconds() - timeS < 1) {
                        bot.stop();
                        break;
                    }
                    if(clock.seconds() - timeS > 3) {
                        bot.stop();
                        curPhase = phase.PUSHING3;
                        timeS = clock.seconds();
                    }
                    telemetry.addData("phase", "pushing2");
                    telemetry.addData("color", col);
                    bot.moveForward(power / 4);
                    break;
                case PUSHING3:
                    telemetry.addData("phase", "pushing3");
                    if(beaconNum == 3) {
                        if(clock.seconds() - timeS < 1) {
                            bot.moveSide(HardwareBot1.direction.LEFT, power/2);
                        } else if(clock.seconds() - timeS < 2) {
                            bot.moveBackward(power/3);
                        } else {
                            bot.stop();
                            curPhase = phase.END;
                            break;
                        }
                    } else if(clock.seconds() - timeS > 1.2) {
                        bot.stop();
                        found = false;
                        target = "Legos";
                        if(beaconNum == 1) {
                            beaconNum = 2;
                        }
                        curPhase = phase.REALIGNING;
                        pusherL.setPosition(1);
                        pusherR.setPosition(0);
                        read = false;
                        slept = false;
                        timeSaved = false;
                    } else {
                        bot.moveBackward(power/3);
                    }
                    break;
                case REALIGNING:
                    telemetry.addData("Phase", "Realigning");
                    if(!found) {
                        telemetry.addData("move?", "yes");
                        bot.moveSide(HardwareBot1.direction.RIGHT, -0.35 * 3, -90); //fixed
                    } else if(beaconNum == 2) {
                        telemetry.addData("move?", "align pls");
                        curPhase = phase.SALIGN;
                        timeAc = clock.milliseconds();
                        timeSaved = false;
                        beaconNum = 3;
                    } else {
                        telemetry.addData("move?", "end me");
                        bot.stop();
                        curPhase = phase.END;
                    }
                    break;
                default:
                    telemetry.addData("color", col);
                    telemetry.addData("confused", "true");
                    bot.stop();
                    break;
            }


            telemetry.update();
            idle();
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

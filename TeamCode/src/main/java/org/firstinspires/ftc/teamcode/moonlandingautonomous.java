package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.hardware.adafruit.BNO055IMU;
import com.qualcomm.hardware.adafruit.JustLoggingAccelerationIntegrator;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.I2cDeviceSynch;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.vuforia.CameraDevice;
import com.vuforia.HINT;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.matrices.OpenGLMatrix;
import org.firstinspires.ftc.robotcore.external.matrices.VectorF;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;
import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;
import org.firstinspires.ftc.robotcore.external.navigation.Orientation;
import android.graphics.Color;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;
import com.qualcomm.robotcore.hardware.ColorSensor;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackables;

/**
 * Created by student on 9/22/16.
 */
@Autonomous(name = "moonlandingwasntreallol", group="Autonomous")
public class moonlandingautonomous extends LinearOpMode {
    enum phase {
        DRIVING, ZALIGN, TURNING, ALIGNING, GOTOBUTTON, PUSHING, SECONDBEACON, XALIGN, REALIGNING, END, FIRSTALIGN
    }
    HardwareBot1 bot = new HardwareBot1();
    ColorSensor csensor;
    ColorSensor backSensor;
    boolean secondBeacon = false;
  //  ColorSensor frontSensor;
    ElapsedTime clock = new ElapsedTime();
    Servo pusher;
    @Override
    public void runOpMode() throws InterruptedException {
        String target;
        float deg;
        float dist;
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);
        csensor = hardwareMap.colorSensor.get("buttonSensor");
 //       backSensor = hardwareMap.colorSensor.get("backSensor");
    //    backSensor.setI2cAddress(I2cAddr.create8bit(0x4c));
     //   frontSensor = hardwareMap.colorSensor.get("frontSensor");
    //    frontSensor.setI2cAddress(I2cAddr.create8bit(0x5c));
     //   frontSensor.enableLed(true);
     //   backSensor.enableLed(true);
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
        target = "Gears";
        deg = 0;
        double power = -0.7;
        VectorF tar = VectorF.length(6);
        phase curPhase = phase.DRIVING;
        dist = 13;
        float startCompassReading;
        float currentCompassReading;
        boolean found = false;
        String bColor = "";
        double alignDeg = 0;
        int beaconNum = 1;
        String com ="";
        String targ = "";
        String col = "";
        boolean slept = false;
        boolean read = false;
        double startTime = clock.milliseconds();
        boolean timeSaved = false;
        double timeS = 0;
        startCompassReading = bot.getCompass();
        while (opModeIsActive())  {
            CameraDevice.getInstance().setFlashTorchMode(false);
            //telemetry.addData("please", "work");
            currentCompassReading = bot.getCompass();
            telemetry.addData("compass", currentCompassReading);
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


                        dist = Math.abs(translation.get(2));
                        tar = translation;
                        if(!found) {
                            found = true;
                            telemetry.addData("I see", "gears");
                        } else {
                            telemetry.addData("I see", "no gears");
                        }
                    } else {
                        telemetry.addData("not a gear!", beacon.getName());
                    }
                } else if(beacon.getName().equals(target)) {
                    found = false;
                }
            }
            telemetry.addData("tar", tar.get(0));
            telemetry.addData("phase", ""+curPhase.name());
            if(!found) {
                telemetry.addData("beacon image", "not found");
            } else {
                telemetry.addData("beacon image", "found");
            }
            switch (curPhase) {
                case DRIVING:
                    if(!found) {
                        if(clock.milliseconds() - startTime < 1000) {
                            bot.moveSide(HardwareBot1.direction.RIGHT, power);
                        } else if(clock.milliseconds() - startTime < 1350) {
                            bot.moveForward(power/1.5);
                        } else  if(clock.milliseconds() - startTime < 2350) {
                            bot.moveSide(HardwareBot1.direction.RIGHT, power);
                        } else {
                            telemetry.addData("lower", "power");
                            bot.moveSide(HardwareBot1.direction.RIGHT, power*.6);
                        }
                    } else {
                        if(secondBeacon) {
                            curPhase = phase.REALIGNING;
                        }
                        curPhase = phase.XALIGN;
                        bot.stop();
                    }
                    /*
                    if(Math.abs(deg - compazReading) >= 10 && dist > 25) {
                        bot.turnComp(deg, power/3);
                        telemetry.addData("drive", "face");
                    } else if(dist > 25) {
                        bot.moveForward(power/2);
                        telemetry.addData("drive", "forward");
                    } else {
                        bot.stop();
                        telemetry.addData("drive", "stop");
                        com = String.valueOf(compazReading);
                        targ = String.valueOf(alignDeg);
                        curPhase = phase.TURNING;
                    }*/
                    break;
                case XALIGN:
                    if(!found) {
                        bot.moveSide(HardwareBot1.direction.RIGHT, power/2);
                    } else if(tar.get(0) > 1.65) {
                        bot.moveSide(HardwareBot1.direction.LEFT, power/2);
                    }  else if(tar.get(0) < -1.35) {
                        bot.moveSide(HardwareBot1.direction.RIGHT, power/2);
                    } else {
                        bot.stop();
                        curPhase = phase.ZALIGN;
                    }
                    break;
                case FIRSTALIGN:
                    if(currentCompassReading + 4.0 < startCompassReading) {
                        bot.turn(HardwareBot1.direction.RIGHT, power/2);
                    } else if(currentCompassReading - 4.0 > startCompassReading) {
                        bot.turn(HardwareBot1.direction.LEFT, power/2);
                    } else {
                        bot.stop();
                        curPhase = phase.GOTOBUTTON;
                        startTime = clock.milliseconds();
                        // }
                    }

                    break;
                case ZALIGN:
                    if (dist > 12) {
                        if(clock.milliseconds() - startTime < 1000) {
                            bot.moveForward(power / 2.2);
                        } else {
                            bot.moveForward(power / 2.6);
                        }
                        telemetry.addData("aligning", "moving forward");
                    } else {
                        bot.stop();
                        telemetry.addData("driving", "stopped driving");
                        curPhase = phase.FIRSTALIGN;
                     //   curPhase = phase.PUSHING1;
                    }
                    break;
                case ALIGNING:
                    telemetry.addData("aligning", "unaligned");
                    if(currentCompassReading - startCompassReading > 5) {
                        if(currentCompassReading + 2.5 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power/1.9);
                        } else if(currentCompassReading - 2.5 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power/2.1);
                        } else {
                            bot.stop();
                            curPhase = phase.DRIVING;
                            startTime = clock.seconds();
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.DRIVING;
                        startTime = clock.seconds();
                        // }
                    }
                    break;
                case GOTOBUTTON:
                    float red = csensor.red();
                    float blue = csensor.blue();
                    if(red > 600 || blue > 600) {
                        if(red - blue > 300) {
                            bot.stop();
                            telemetry.addData("color", "red");
                            startTime = clock.milliseconds();
                            curPhase = phase.PUSHING;
                        } else if (blue - red > 300) {
                            telemetry.addData("color", "blue");
                            bot.moveSide(HardwareBot1.direction.LEFT, power/2.4);
                        } else {
                            telemetry.addData("color", "none");
                            bot.moveSide(HardwareBot1.direction.LEFT, power/2.4);
                        }
                    } else {
                        telemetry.addData("color", "none");
                        bot.moveSide(HardwareBot1.direction.LEFT, power/2.4);
                    }
                    telemetry.addData("red", red);
                    telemetry.addData("blue", blue);
                    telemetry.update();
                    break;
                case PUSHING:
             //       telemetry.addData("phase", "pushing1");
                    if(clock.milliseconds() - startTime < 250) {
                        bot.stop();
                    }
                    if(clock.milliseconds() - startTime < 250 + 750) {
                        bot.moveForward(power*.5);
                    } else if(clock.milliseconds() - startTime < 250 + 750 + 200) {
                        bot.moveBackward(power*.5);
                    } else if(clock.milliseconds() - startTime < 250 + 750 + 200 + 300) {
                        bot.moveSide(HardwareBot1.direction.LEFT, power/2);
                    } else if(clock.milliseconds() - startTime < 250 + 750 + 200 + 300 + 500) {
                        bot.moveForward(power*.5);
                    } else if(clock.milliseconds() - startTime < 250 + 750 + 200 + 300 + 500 + 500) {
                        bot.moveBackward(power*.5);
                    } else if(!secondBeacon) {
                        secondBeacon = true;
                        target = "Legos";
                        curPhase = phase.DRIVING;
                        bot.stop();
                        startTime = clock.seconds();
                        curPhase = phase.ALIGNING;
                    } else {
                        bot.stop();
                    }
                    break;
                case REALIGNING:
                    telemetry.addData("aligning", "unaligned");
                    if(currentCompassReading - startCompassReading > 5) {
                        if(currentCompassReading + 2.5 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power/1.9);
                        } else if(currentCompassReading - 2.5 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power/2.1);
                        } else {
                            bot.stop();
                            curPhase = phase.XALIGN;
                            startTime = clock.seconds();
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.XALIGN;
                        startTime = clock.seconds();
                        // }
                    }
                    break;
                default:
                    telemetry.addData("error occured", "default break happened");
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

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
@Autonomous(name = "straferedauto3", group="Autonomous")
public class strafered extends LinearOpMode {
    enum phase {
        DRIVING, ZALIGN, TURNING, ALIGNING, GOTOBUTTON, PUSHING, SECONDBEACON, XALIGN, DRIVINGALIGN, END, FIRSTALIGN, SECONDALIGNING, STRAFETOBEACON, REALIGNING
    }
    HardwareBot1 bot = new HardwareBot1();
    ColorSensor csensor;
    float lastBlue = 0;
    float lastRed = 0;
    ColorSensor backSensor;
    boolean secondBeacon = false;
    float red = 0;
    float blue = 0;
    float redRatio = 0;
    float blueRatio = 0;
    HardwareBot1.direction strafeDirection = HardwareBot1.direction.LEFT;
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
        boolean checkedColor = false;
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
                        if(clock.milliseconds() - startTime < 3000) {
                            bot.diagonalForward(HardwareBot1.direction.RIGHT, power);
                        }  else {
                            curPhase = phase.DRIVINGALIGN;
                            bot.stop();
                        }
                    } else {
                        if(secondBeacon) {
                            curPhase = phase.REALIGNING;
                        } else {
                            curPhase = phase.FIRSTALIGN;
                            bot.stop();
                        }
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
                        bot.moveSide(HardwareBot1.direction.RIGHT, power/1.6);
                    } else if(tar.get(0) > -3.5) {
                        startTime = clock.milliseconds();
                        bot.moveSide(HardwareBot1.direction.LEFT, power/2.3);
                    }  else if(tar.get(0) < -6) {
                        startTime = clock.milliseconds();
                        bot.moveSide(HardwareBot1.direction.RIGHT, power/2.3);
                    } else {
                        bot.stop();
                        curPhase = phase.ZALIGN;
                    }
                    break;
                case DRIVINGALIGN:
                    if(Math.abs(currentCompassReading - startCompassReading) >= .25) {
                        if(currentCompassReading + .25 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power*.23);
                        } else if(currentCompassReading - .25 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power*.23);
                        } else {
                            bot.stop();
                            curPhase = phase.STRAFETOBEACON;
                            startTime = clock.milliseconds();
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.STRAFETOBEACON;
                        startTime = clock.milliseconds();
                        // }
                    }
                    break;
                case STRAFETOBEACON:
                    if(!found) {
                        bot.moveSide(HardwareBot1.direction.RIGHT, power*.5);
                    } else {
                        bot.stop();
                        curPhase = phase.FIRSTALIGN;
                    }
                    break;
                case FIRSTALIGN:
                    if(Math.abs(currentCompassReading - startCompassReading) >= .25) {
                        if(currentCompassReading + .25 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power*.21);
                        } else if(currentCompassReading - .25 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power*.21);
                        } else {
                            bot.stop();
                            curPhase = phase.XALIGN;
                            startTime = clock.milliseconds();
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.XALIGN;
                        startTime = clock.milliseconds();
                        // }
                    }
                    break;
                case SECONDALIGNING:
                    if(Math.abs(currentCompassReading - startCompassReading) >= 4.0) {
                        if(currentCompassReading + 4.0 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power/2.3);
                        } else if(currentCompassReading - 4.0 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power/2.3);
                        } else {
                            bot.stop();
                            curPhase = phase.GOTOBUTTON;
                            startTime = clock.milliseconds();
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.SECONDBEACON;
                        startTime = clock.milliseconds();
                        // }
                    }
                    break;
                case ZALIGN:
                    if (dist > 10) {
                        bot.moveForward(power / 3.5);
                        telemetry.addData("aligning", "moving forward");
                    } else {
                        bot.stop();
                        startTime = clock.milliseconds();
                        telemetry.addData("driving", "stopped driving");
                        //float blue  = csensor.blue();
                      //  float red  = csensor.red();
                        curPhase = phase.GOTOBUTTON;
                        red = csensor.red();
                        blue = csensor.blue();
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
                    telemetry.addData("red", csensor.red());
                    telemetry.addData("blue", csensor.blue());
                    if(clock.milliseconds() - startTime < 300) {
                        red = csensor.red();
                        blue = csensor.blue();
                        bot.stop();
                    } else if(red > blue && !checkedColor) {
                        if(clock.milliseconds() - startTime < 0) {
                            bot.moveSide(strafeDirection, power);
                        } else {
                          //  if(csensor.red() > csensor.blue()) {
                                curPhase = phase.PUSHING;
                                bot.stop();
                        }
                        checkedColor = true;
                    } else if(clock.milliseconds() - startTime < 1000) {
                        bot.moveSide(strafeDirection, power*.6);
                    } else if(clock.milliseconds() - startTime < 1250){
                        red = csensor.red();
                        blue = csensor.blue();
                        bot.stop();
                    } else if(clock.milliseconds() - startTime < 1500) {
                       if(red > blue) {
                           bot.stop();
                           curPhase = phase.PUSHING;
                       }
                   } else {
                        red = csensor.red();
                        blue = csensor.blue();
                       startTime = clock.milliseconds();
                       strafeDirection = strafeDirection == HardwareBot1.direction.LEFT ? HardwareBot1.direction.RIGHT : HardwareBot1.direction.LEFT;
                   }
                    telemetry.update();
                    break;
                case PUSHING:
                    if(clock.milliseconds() - startTime < 250) {
                        bot.stop();
                    } else if(clock.milliseconds() - startTime < 250 + 750) {
                        bot.moveForward(power*.5);
                    } else if(clock.milliseconds() - startTime < 250 + 750 + 500) {
                        bot.moveBackward(power*.5);
                    } else if(!secondBeacon) {
                        secondBeacon = true;
                        target = "Legos";
                        bot.stop();;
                        startTime = clock.milliseconds();
                        /* SECOND BEACON AUTO */
                        curPhase = phase.SECONDBEACON;
                    } else {
                        bot.stop();
                    }
                    break;
                case SECONDBEACON:
                    if(!found) {
                        if(clock.milliseconds() - startTime < 2000) {
                            bot.moveSide(HardwareBot1.direction.RIGHT, power * .9);
                        } else {
                            bot.moveSide(HardwareBot1.direction.RIGHT, power * .45);
                        }
                    } else {
                        bot.stop();
                        startTime = clock.milliseconds();
                        curPhase = phase.XALIGN;
                    }
                    break;
                case REALIGNING:
                    telemetry.addData("aligning", "unaligned");
                    if(currentCompassReading - startCompassReading > 5) {
                        if(currentCompassReading + 2.5 < startCompassReading) {
                            bot.turn(HardwareBot1.direction.RIGHT, power/1.9);
                        } else if(currentCompassReading - 2.5 > startCompassReading) {
                            bot.turn(HardwareBot1.direction.LEFT, power/1.9);
                        } else {
                            bot.stop();
                            curPhase = phase.SECONDBEACON;
                            //     startTime = clock.;
                            // }
                        }
                    } else {
                        bot.stop();
                        curPhase = phase.SECONDBEACON;
                        // startTime = clock.seconds();
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

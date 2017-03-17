package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.I2cAddr;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by student on 9/29/16.
 */
@TeleOp(name="MecTeleReal", group="Teleops")
public class MecTeleRunning extends LinearOpMode {
    DcMotor shoot;
    DcMotor nom;
    Servo pusherL;
    Servo pusherR;
    Servo opener;
    //DcMotor convey;
    HardwareBot1 bot = new HardwareBot1();
    Boolean strafe = true;
    Boolean canChangeShooting = true;
    float stopperPower = 0;
    ColorSensor csensor;
    ColorSensor backSensor;
    ColorSensor frontSensor;
    Boolean canDPad = true;
    //   Double conveySpeed = 0.0;
    @Override
    public void runOpMode() throws InterruptedException {
        boolean canPush = true;
        double pushes = 0;
        csensor = hardwareMap.colorSensor.get("buttonSensor");
        telemetry.addData("Welcome", "version1");
        telemetry.update();
        pusherL = hardwareMap.servo.get("leftPusher");
        pusherR = hardwareMap.servo.get("rightPusher");
        opener = hardwareMap.servo.get("opener");
        shoot = hardwareMap.dcMotor.get("shooter");
        nom = hardwareMap.dcMotor.get("pickup");
        bot.init(hardwareMap, telemetry);
        pusherR.setPosition(0);
        pusherL.setPosition(2);
        waitForStart();
        double shootPower = 0;
        double nomPower = .5;
        double pusherPosition = 0.5;
        double speed = 0.8;
        while (opModeIsActive()) {
            telemetry.addData("pusherL", pusherL.getPosition());
            telemetry.addData("pusherR", pusherR.getPosition());
            float buttonSensorValues[] = {0F, 0F, 0F};
            Color.RGBToHSV((csensor.red() * 255) / 800, (csensor.green() * 255) / 800, (csensor.blue() * 255) / 800, buttonSensorValues);
            telemetry.addData("hue", buttonSensorValues[0]);
            if (buttonSensorValues[0] > 140 && buttonSensorValues[0] < 310) {
                telemetry.addData("color", "red");
            } else {
                telemetry.addData("color", "blue");
            }

            nom.setPower(nomPower);
            shoot.setPower(-shootPower);
            if (gamepad2.left_trigger > 0) {
                shootPower = -.3;
            } else {
                shootPower = 0;
            }
            if (Math.abs(gamepad1.left_stick_x) > 0.15 || Math.abs(gamepad1.left_stick_y) > 0.15) {
                if (Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) {
                    if (gamepad1.left_stick_x > 0.15) {
                        bot.turn(HardwareBot1.direction.RIGHT, speed);
                    } else {
                        bot.turn(HardwareBot1.direction.LEFT, speed);
                    }
                } else {
                    if (gamepad1.left_stick_y > 0) {
                        bot.moveForward(gamepad1.left_stick_y);
                    } else {
                        bot.moveBackward(-gamepad1.left_stick_y);
                    }
                }
            } else if (Math.abs(gamepad1.right_stick_x) > 0.15) {
                if (gamepad1.right_stick_x > 0) {
                    bot.moveSide(HardwareBot1.direction.LEFT, -gamepad1.right_stick_x);
                } else {
                    bot.moveSide(HardwareBot1.direction.RIGHT, gamepad1.right_stick_x);
                }
            } else {
                bot.stop();
                telemetry.addData("stopped", "stop");
            }
            if (gamepad1.left_bumper) {
                if(canPush) {
                    pushes += 1;
                    if (pusherL.getPosition() == 1) {
                        pusherL.setPosition(0);
                        pusherR.setPosition(0);
                    } else if (pusherL.getPosition() == 0) {
                        pusherL.setPosition(1);
                        pusherR.setPosition(1);
                    }
                    canPush = false;
                }
            } else if(gamepad1.right_bumper) {
                if(canPush) {
                    pushes += 1;
                    if (pusherR.getPosition() == 0) {
                        pusherL.setPosition(1);
                        pusherR.setPosition(1);
                    } else if (pusherR.getPosition() == 1) {
                        pusherL.setPosition(0);
                        pusherR.setPosition(0);
                    }
                    canPush = false;
                }
            } else {
                canPush = true;
            }
            telemetry.addData("pushes", pushes);
            if (gamepad1.left_trigger > 0) {
                nomPower = -gamepad1.left_trigger;
            } else if (gamepad1.right_trigger > 0) {
                nomPower = gamepad1.right_trigger;
            } else {
                nomPower = 0;
            }
            if (gamepad2.x) {
                stopperPower = 1;
            } else if (gamepad2.b) {
                stopperPower = -1;
            } else {
                stopperPower = 0;
            }
            opener.setPosition(0.6 - gamepad2.right_trigger/2);

            /*if(gamepad2.dpad_up) {
                if(canDPad) {
                    canDPad = false;
                    conveySpeed += 0.1;
                }
            } else if(gamepad2.dpad_down) {
                if(canDPad) {
                    canDPad = false;
                    conveySpeed -= 0.1;
                }
            } else {
                canDPad = true;
            }
            if(conveySpeed > 1) {
                conveySpeed = 1.0;
            } else if(conveySpeed < -1) {
                conveySpeed = -1.0;
            }
            convey.setPower(conveySpeed);*/
            telemetry.update();

        }
    }
}
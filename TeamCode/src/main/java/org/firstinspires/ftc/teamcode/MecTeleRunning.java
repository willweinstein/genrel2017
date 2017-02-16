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
    Servo pusher;
    //DcMotor convey;
    HardwareBot1 bot = new HardwareBot1();
    Boolean strafe = true;
    DcMotor stopper;
    Boolean canChangeShooting = true;
    float stopperPower = 0;
    ColorSensor csensor;
    ColorSensor backSensor;
    ColorSensor frontSensor;
    Boolean canDPad = true;
    //   Double conveySpeed = 0.0;
    @Override
    public void runOpMode() throws InterruptedException {
        csensor = hardwareMap.colorSensor.get("buttonSensor");
        telemetry.addData("Welcome", "version1");
        telemetry.update();
        pusher = hardwareMap.servo.get("pusher");
        shoot = hardwareMap.dcMotor.get("wheelMotor");
        stopper = hardwareMap.dcMotor.get("stopper");
        nom = hardwareMap.dcMotor.get("nom");
        bot.init(hardwareMap, telemetry);
        waitForStart();
        double shootPower = 0;
        double nomPower = .5;
        double pusherPosition = 0.5;
        double speed = 0.8;
        while (opModeIsActive()) {
            float buttonSensorValues[] = {0F, 0F, 0F};
            Color.RGBToHSV((csensor.red() * 255) / 800, (csensor.green() * 255) / 800, (csensor.blue() * 255) / 800, buttonSensorValues);
            telemetry.addData("hue", buttonSensorValues[0]);
            if (buttonSensorValues[0] > 140 && buttonSensorValues[0] < 310) {
                telemetry.addData("color", "red");
            } else {
                telemetry.addData("color", "blue");
            }
            telemetry.update();
            stopper.setPower(stopperPower);
            nom.setPower(nomPower);
            shoot.setPower(-shootPower);
            if (gamepad1.y) {
                if (canChangeShooting) {
                    canChangeShooting = false;
                    if (shootPower == 0) {
                        shootPower = 1;
                    } else {
                        shootPower = 0;
                    }
                }
            } else {
                canChangeShooting = true;
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
                    bot.moveSide(HardwareBot1.direction.LEFT, gamepad1.right_stick_x);
                } else {
                    bot.moveSide(HardwareBot1.direction.RIGHT, -gamepad1.right_stick_x);
                }
            } else {
                bot.stop();
                telemetry.addData("stopped", "stop");
            }
            if (gamepad2.left_bumper) {
                if(pusher.getPosition() != 1) {
                    pusher.setPosition(1);
                }
            }
            if (gamepad2.right_bumper) {
                if(pusher.getPosition() != 0) {
                    pusher.setPosition(0);
                }
            }
            if (gamepad2.left_trigger > 0) {
                nomPower = -gamepad2.left_trigger;
            } else if (gamepad2.right_trigger > 0) {
                nomPower = gamepad2.right_trigger;
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
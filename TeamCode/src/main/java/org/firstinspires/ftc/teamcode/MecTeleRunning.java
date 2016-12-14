package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by student on 9/29/16.
 */
@TeleOp(name="MecTeleReal", group="Teleops")
public class MecTeleRunning extends LinearOpMode {
    DcMotor shoot;
    DcMotor nom;
    //DcMotor convey;
    HardwareBot1 bot = new HardwareBot1();
    Servo pusher;
    Servo loader;
    Boolean strafe = true;
    Boolean canChangeShooting = true;
    Boolean canDPad = true;
    //   Double conveySpeed = 0.0;
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Welcome", "version1");
        telemetry.update();
        pusher = hardwareMap.servo.get("pusher");
        shoot = hardwareMap.dcMotor.get("shooter");
        nom = hardwareMap.dcMotor.get("nom");
        loader = hardwareMap.servo.get("loader");
        bot.init(hardwareMap, telemetry);
        waitForStart();
        double shootPower = 0;
        double nomPower = 0;
        double speed = 0.8;
        while(opModeIsActive()) {
            shoot.setPower(shootPower);
            if(gamepad2.dpad_up) {
                loader.setPosition(1);
            } else if(gamepad2.dpad_down) {
                loader.setPosition(0);
            }
            if(gamepad1.y) {
                if(canChangeShooting) {
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
            if(Math.abs(gamepad1.left_stick_x) > 0.15 || Math.abs(gamepad1.left_stick_y) > 0.15) {
                if(Math.abs(gamepad1.left_stick_x) > Math.abs(gamepad1.left_stick_y)) {
                    if(gamepad1.left_stick_x > 0.15) {
                        bot.turn(HardwareBot1.direction.RIGHT, speed);
                    } else {
                        bot.turn(HardwareBot1.direction.LEFT, speed);
                    }
                } else {
                    if(gamepad1.left_stick_y > 0) {
                        bot.moveForward(gamepad1.left_stick_y);
                    } else {
                        bot.moveBackward(-gamepad1.left_stick_y);
                    }
                }
            } else if (strafe && Math.abs(gamepad1.right_stick_x) > 0.15){
                if(gamepad1.right_stick_x > 0) {
                    bot.moveSide(HardwareBot1.direction.RIGHT, gamepad1.right_stick_x);
                } else {
                    bot.moveSide(HardwareBot1.direction.LEFT, -gamepad1.right_stick_x);
                }

            } else if (!strafe && (Math.abs(gamepad1.right_stick_y) > 0.15 || Math.abs(gamepad1.right_stick_x) > 0.15)) {
                bot.moveAng(gamepad1.right_stick_x, gamepad1.right_stick_y, 1);
            } else {
                bot.stop();
                telemetry.addData("stopped", "stop");


            }
            if(gamepad1.x) {
                strafe = false;
            }
            if(gamepad1.b) {
                strafe = true;
            }
            if(gamepad2.a) {
                pusher.setPosition(0.5);
            } else if(gamepad2.left_bumper) {
                pusher.setPosition(1);
            } else if(gamepad2.right_bumper) {
                pusher.setPosition(0);
            }

            if(Math.abs(gamepad2.right_stick_y) > 0.15) {
                //nom.setPower(gamepad2.right_stick_y);
                nom.setPower(1);
            } else {
                nom.setPower(0);
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
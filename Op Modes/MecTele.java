package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by student on 9/29/16.
 */
@TeleOp(name="Mec2", group="Teleops")
public class MecTele extends LinearOpMode {
    HardwareBot1 bot = new HardwareBot1();
    Servo pusher;
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Welcome", "version1");
        telemetry.update();
        pusher = hardwareMap.servo.get("pusher");
        bot.init(hardwareMap);

        waitForStart();
        double speed = 0.8;
        while(opModeIsActive()) {
            telemetry.addData("y", gamepad1.right_stick_y);
            telemetry.addData("x", gamepad1.right_stick_x);
            if(Math.abs(gamepad1.right_stick_x) > 0.1 || Math.abs(gamepad1.right_stick_y) > 1) {
                bot.moveAng(gamepad1.right_stick_x, gamepad1.right_stick_y, speed);
            } else if(Math.abs(gamepad1.left_stick_x) > 0.1){
                if(gamepad1.left_stick_x > 0.1) {
                    bot.turn(HardwareBot1.direction.RIGHT, speed);
                } else {
                    bot.turn(HardwareBot1.direction.LEFT, speed);
                }
            } else {
                bot.stop();
                telemetry.addData("stopped", "stop");
            }
            if(gamepad1.a) {
                pusher.setPosition(0.5);
            } else if(gamepad1.x) {
                pusher.setPosition(1);
            } else if(gamepad1.y) {
                pusher.setPosition(0);
            }
            telemetry.update();

        }
    }
}

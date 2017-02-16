package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Servo;

/**
 * Created by student on 9/29/16.
 */
@TeleOp(name="TestTele", group="Teleops")
public class TestTele extends LinearOpMode {
    HardwareBot1 bot = new HardwareBot1();
    Servo pusher;
    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Welcome", "version1");
        telemetry.update();
        pusher = hardwareMap.servo.get("pusher");
        bot.init(hardwareMap, telemetry);


        waitForStart();
        double speed = 2;
        while(opModeIsActive()) {
            if(gamepad1.x) {
                bot.moveSide(HardwareBot1.direction.LEFT, speed, 0);
            } else if(gamepad1.b) {
                bot.moveSide(HardwareBot1.direction.RIGHT, speed, 0);
            } else {
                bot.stop();
            }
            telemetry.update();
        }

    }
}

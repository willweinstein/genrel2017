package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

/**
 * Created by student on 12/11/16.
 */
@Autonomous(name="CAPAUTONODELAY")
public class CapBall2 extends LinearOpMode {
    HardwareBot1 bot = new HardwareBot1();
    @Override
    public void runOpMode() throws InterruptedException {
        bot.init(hardwareMap, telemetry);
        ElapsedTime clock = new ElapsedTime();

        waitForStart();

        double timeStart = clock.milliseconds();

        while(opModeIsActive()) {
            if(clock.milliseconds() - timeStart < 3000) {
                bot.moveForward(0.7);
            } else if (clock.milliseconds() - timeStart > 30000) {
                bot.stop();

                break;
            } else {
                bot.stop();
            }
        }
    }
}

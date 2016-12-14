package org.firstinspires.ftc.teamcode;

import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;
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
@Autonomous(name = "Square", group="Vuforia")
public class SquareAuto extends LinearOpMode {
    HardwareBot1 bot = new HardwareBot1();
    ElapsedTime clock = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);


        waitForStart();

        int phase = 0;
        while (opModeIsActive())  {
            if(clock.seconds() % 5 < 1) {
                bot.moveForward(.7);
            } else if(clock.seconds() % 5 < 1.25) {
                bot.stop();
            } else if(clock.seconds() % 5 < 2.5) {
                bot.moveSide(HardwareBot1.direction.RIGHT, .7);
            } else if(clock.seconds() % 5 < 2.75) {
                bot.stop();
            }else if(clock.seconds() % 5 < 3.75) {
                bot.moveBackward(.7);
            } else if(clock.seconds() % 5 < 4) {
                bot.stop();
            } else {
                bot.moveSide(HardwareBot1.direction.LEFT, .7);
            }

            telemetry.update();
            idle();
        }

    }

}

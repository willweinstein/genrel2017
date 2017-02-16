package org.firstinspires.ftc.teamcode;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.I2cAddr;
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
@Autonomous(name = "csensortest", group="Vuforia")
public class SquareAuto extends LinearOpMode {
    HardwareBot1 bot = new HardwareBot1();
    ElapsedTime clock = new ElapsedTime();
    @Override
    public void runOpMode() throws InterruptedException {
        HardwareMap awh = hardwareMap;
        bot.init(awh, telemetry);
        ColorSensor csensor = hardwareMap.colorSensor.get("buttonSensor");
    //    csensor = hardwareMap.colorSensor.get("buttonSensor");
    //   ColorSensor backSensor = hardwareMap.colorSensor.get("backSensor");
      //  ColorSensor frontSensor = hardwareMap.colorSensor.get("frontSensor");
      //  frontSensor.setI2cAddress(I2cAddr.create8bit(0x5c));
        //frontSensor.enableLed(true);
      //  backSensor.enableLed(true);
        //csensor.enableLed(true);
        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            float hsvValues[] = {0F, 0F, 0F};
            Color.RGBToHSV((csensor.red() * 255) / 800, (csensor.green() * 255) / 800, (csensor.blue() * 255) / 800, hsvValues);
            int red = csensor.red();
            int blue = csensor.blue();
            if(red - blue > 220) {
                telemetry.addData("color", "red");
            } else if (blue - red > 220) {
                telemetry.addData("color", "blue");
            } else {
                telemetry.addData("color", "none");
            }
            telemetry.addData("red", csensor.red());
            telemetry.addData("blue", csensor.blue());
            telemetry.addData("green", csensor.green());
            telemetry.addData("alpha", csensor.alpha());
            telemetry.addData("hue", hsvValues[0]);
            telemetry.addData("compass", bot.getCompass());
          //  telemetry.addData("red", frontSensor.red());
          //  telemetry.addData("blue", frontSensor.blue());
          //  telemetry.addData("green", frontSensor.green());
          //  telemetry.addData("alpha", frontSensor.alpha());
            telemetry.update();
            idle();
        }

    }

}

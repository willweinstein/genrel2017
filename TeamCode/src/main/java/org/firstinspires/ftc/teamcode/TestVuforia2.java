/*
Copyright (c) 2016 Robert Atkinson

All rights reserved.

Redistribution and use in source and binary forms, with or without modification,
are permitted (subject to the limitations in the disclaimer below) provided that
the following conditions are met:

Redistributions of source code must retain the above copyright notice, this list
of conditions and the following disclaimer.

Redistributions in binary form must reproduce the above copyright notice, this
list of conditions and the following disclaimer in the documentation and/or
other materials provided with the distribution.

Neither the name of Robert Atkinson nor the names of his contributors may be used to
endorse or promote products derived from this software without specific prior
written permission.

NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
"AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESSFOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR
TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF
THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.firstinspires.ftc.teamcode;

import android.app.Activity;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.vuforia.Vuforia;

import org.firstinspires.ftc.robotcontroller.internal.FTCVuforia;
import org.firstinspires.ftc.robotcontroller.internal.FtcRobotControllerActivity;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackableDefaultListener;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaTrackable;

import java.util.HashMap;


@Autonomous(name="vuf", group="Teleops")
@Disabled //only uncomment this if you don't want to see the opmode
public class TestVuforia2 extends LinearOpMode {

    /* Declare OpMode members. */
    FTCVuforia vuforia;

    @Override
    public void runOpMode() throws InterruptedException {
        telemetry.addData("Status", "Hello Driver");    //
        telemetry.update();
        vuforia = FtcRobotControllerActivity.getVuforia();
        vuforia.addTrackables("fieldelements.xml");
        vuforia.initVuforia();

        //initialize robot with the robot hardware class


        // Send telemetry message to signify robot waiting;
        telemetry.addData("Say", "Hello Driver");    //
        telemetry.update();

        // Wait for the game to start (driver presses PLAY)
        waitForStart();

        // run until the end of the match (driver presses STOP)
        while (opModeIsActive()) {
            // Run wheels in tank mode (note: The joystick goes negative when pushed forwards, so negate it)


            HashMap<String, double[]> data = vuforia.getVuforiaData();
            if(data.containsKey("tools")) {
                if(data.get("tools") != null) {
                    if (data.get("tools").length == 7) {
                        try {

                            telemetry.addData("x", data.get("tools")[3]);
                            telemetry.addData("y", data.get("tools")[4]);
                            telemetry.addData("z", data.get("tools")[5]);
                        } catch (Exception e) {
                            telemetry.addData("error", e);
                        }
                        telemetry.addData("Running", "found");
                        telemetry.update();
                    }
                }
            } else {
                telemetry.addData("Running", "not found");
                telemetry.update();
            }

            // Use gamepad Y & A raise and lower the arm
            /*if (gamepad1.a) {
                telemetry.addData("Controllers", gamepad1.right_stick_x);
            } else if (gamepad1.y) {

            }


            // Use gamepad X & B to open and close the claw
            if (gamepad1.x) {

            } else if (gamepad1.b) {

            }
            */



            // Send telemetry message to signify robot running;

            idle(); // Always call idle() at the bottom of your while(opModeIsActive()) loop
        }
    }
}

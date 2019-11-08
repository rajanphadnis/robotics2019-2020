package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import java.io.File;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.ServoImpl;
import com.qualcomm.robotcore.hardware.ServoImplEx;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.ftccommon.SoundPlayer;
import android.content.Context;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.internal.system.Deadline;

import java.util.concurrent.TimeUnit;

@TeleOp(name="Teleop", group="Pushbot")
public class Teleop extends OpMode
{
    String  sounds[] =  {"ss_alarm", "ss_bb8_down", "ss_bb8_up", "ss_darth_vader", "ss_fly_by",
            "ss_mf_fail", "ss_laser", "ss_laser_burst", "ss_light_saber", "ss_light_saber_long", "ss_light_saber_short",
            "ss_light_speed", "ss_mine", "ss_power_up", "ss_r2d2_up", "ss_roger_roger", "ss_siren", "ss_wookie" };
    boolean soundPlaying = false;
    int     soundID         = -1;
    public DcMotor fr = null;
    public DcMotor fl = null;
    public DcMotor br = null;
    public DcMotor bl = null;
    
    public CRServo iR = null;
    public CRServo iL = null;
    
    public DcMotor liftR = null;
    public DcMotor liftL = null;
    
    public double slowDown = 0.5;
    public double slowDown25 = 0.35;
    public double deadZoneHigh = 0.5;
    public double deadZoneLow= -0.5;
    
    // Context myApp = hardwareMap.appContext;
    
    // private String soundPath = "/FIRST/blocks";
    // private File goldFile   = new File("/sdcard" + soundPath + "/boston.mp3");
    
    //Change the pattern every 10 seconds in AUTO mode.
    private final static int LED_PERIOD = 10;

    RevBlinkinLedDriver blinkinLedDriver;
    RevBlinkinLedDriver.BlinkinPattern pattern;

    Telemetry.Item patternName;
    Telemetry.Item display;
    DisplayKind displayKind;
    Deadline ledCycleDeadline;
   

    protected enum DisplayKind {
        MANUAL,
        AUTO
    }
    
    @Override
    public void init()
    {
        fr = hardwareMap.dcMotor.get("fr");
        fl = hardwareMap.dcMotor.get("fl");
        br = hardwareMap.dcMotor.get("br");
        bl = hardwareMap.dcMotor.get("bl");
        
        iL = hardwareMap.crservo.get("iL");
        iR = hardwareMap.crservo.get("iR");
        
        liftR = hardwareMap.dcMotor.get("liftR");
        liftL = hardwareMap.dcMotor.get("liftL");

        fr.setPower(0);
        fl.setPower(0);
        br.setPower(0);
        bl.setPower(0);
        liftR.setPower(0);
        liftL.setPower(0);
        

        fr.setDirection(DcMotorSimple.Direction.FORWARD);
        fl.setDirection(DcMotorSimple.Direction.REVERSE);
        br.setDirection(DcMotorSimple.Direction.FORWARD);
        bl.setDirection(DcMotorSimple.Direction.REVERSE);
        liftR.setDirection(DcMotorSimple.Direction.REVERSE);
        liftL.setDirection(DcMotorSimple.Direction.REVERSE);
        
        fr.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        fl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        br.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        bl.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftR.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftL.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        fr.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        fl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        br.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        bl.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        
        iL.setPower(0);
        iR.setPower(0);
        
        
        // SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, goldFile);
        
    }
    @Override
    public void init_loop()
    {
        
    }

    @Override
    public void start()
    {
    }
    
    public void mecanumDrive_Cartesian(double x, double y, double rotation)
    {
        double wheelSpeeds[] = new double[4];

        wheelSpeeds[0] = x + y + rotation;
        wheelSpeeds[1] = -x + y - rotation;
        wheelSpeeds[2] = -x + y + rotation;
        wheelSpeeds[3] = x + y - rotation;
        // 
        normalize(wheelSpeeds);

        fl.setPower(wheelSpeeds[0]);
        fr.setPower(wheelSpeeds[1]);
        bl.setPower(wheelSpeeds[2]);
        br.setPower(wheelSpeeds[3]);
    }   //mecanumDrive_Cartesian



    private void normalize(double[] wheelSpeeds)

    {

        double maxMagnitude = Math.abs(wheelSpeeds[0]);



        for (int i = 1; i < wheelSpeeds.length; i++)

        {

            double magnitude = Math.abs(wheelSpeeds[i]);



            if (magnitude > maxMagnitude)

            {

                 maxMagnitude = magnitude;

            }

        }



        if (maxMagnitude > 1.0)

        {

            for (int i = 0; i < wheelSpeeds.length; i++)

            {

                wheelSpeeds[i] /= maxMagnitude;

            }

        }

    }   //normalize
    @Override
    public void loop()
    {
        iL.setPower(0);
        iR.setPower(0);
        SoundPlayer.PlaySoundParams params = new SoundPlayer.PlaySoundParams();
        params.loopControl = 0;
        params.waitForNonLoopingSoundsToFinish = false;
        
        // displayKind = DisplayKind.AUTO;
        // blinkinLedDriver = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        // //change colors here (palette guide: http://www.revrobotics.com/content/docs/REV-11-1105-UM.pdf)
        // pattern = RevBlinkinLedDriver.BlinkinPattern.SINELON_OCEAN_PALETTE; 
        // blinkinLedDriver.setPattern(pattern);

        // display = telemetry.addData("Display Kind: ", displayKind.toString());
        // patternName = telemetry.addData("Pattern: ", pattern.toString());

        // ledCycleDeadline = new Deadline(LED_PERIOD, TimeUnit.SECONDS);
        
        // blinkinLedDriver.setPattern(pattern);
        // patternName.setValue(pattern.toString());
        
        //SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, goldFile);
        // drive code
        float leftY = -2 * gamepad1.left_stick_y;

        float leftX = -2 * gamepad1.left_stick_x;

        float turn = 2 * gamepad1.right_stick_x;
        
        liftR.setPower(gamepad2.left_stick_y*0.6);
        liftL.setPower(gamepad2.right_stick_y*0.6);
        
        // telemetry.addData("GamePad Data: ", "G1LY: " + leftY + "G1LX: " + leftX + "G1Turn: " + turn);
        // telemetry.addData("leftWrist Position, rWrist Position", lwrist.getPosition() + " and " + rwrist.getPosition());
        
        /*
        Controller layout and control feature switches
        1: intake in and out should be right stick button
        2: 
        
        */
        if(gamepad1.left_bumper) {
           // mecanumDrive_Cartesian(slowDown25 * leftX, slowDown25 * leftY,slowDown25 *  turn);
            iL.setPower(-0.8);
            iR.setPower(-0.8);
        }
        else if (gamepad1.right_bumper){
            iL.setPower(0.8);
            iR.setPower(0.8);
            //mecanumDrive_Cartesian(slowDown * leftX, slowDown * leftY,slowDown *  turn);
        }
        else if (gamepad2.dpad_up){
            liftL.setPower(1);
            liftR.setPower(1);
        }
        else if (gamepad2.dpad_down){
            liftL.setPower(-1);
            liftR.setPower(-1);
        }
        else if (gamepad2.a) {
            soundID = hardwareMap.appContext.getResources().getIdentifier(sounds[1], "raw", hardwareMap.appContext.getPackageName());
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID, params, null,
                new Runnable() {
                    public void run() {
                        soundPlaying = false;
                    }
                    
                } );
        }
        else if (gamepad2.b) {
            soundID = hardwareMap.appContext.getResources().getIdentifier(sounds[2], "raw", hardwareMap.appContext.getPackageName());
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID, params, null,
                new Runnable() {
                    public void run() {
                        soundPlaying = false;
                    }
                    
                } );
        }
        else if (gamepad2.x) {
            soundID = hardwareMap.appContext.getResources().getIdentifier(sounds[6], "raw", hardwareMap.appContext.getPackageName());
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID, params, null,
                new Runnable() {
                    public void run() {
                        soundPlaying = false;
                    }
                    
                } );
        }
        else if (gamepad2.left_trigger >= 0.5) {
            soundID = hardwareMap.appContext.getResources().getIdentifier(sounds[7], "raw", hardwareMap.appContext.getPackageName());
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID, params, null,
                new Runnable() {
                    public void run() {
                        soundPlaying = false;
                    }
                    
                } );
        }
        
        else if (gamepad2.right_trigger >= 0.5) {
            soundID = hardwareMap.appContext.getResources().getIdentifier(sounds[8], "raw", hardwareMap.appContext.getPackageName());
            soundPlaying = true;
            SoundPlayer.getInstance().startPlaying(hardwareMap.appContext, soundID, params, null,
                new Runnable() {
                    public void run() {
                        soundPlaying = false;
                    }
                    
                } );
        }mecanumDrive_Cartesian(leftX, leftY, turn);
        telemetry.update();
    }
    protected void setDisplayKind(DisplayKind displayKind)
    {
        this.displayKind = displayKind;
        display.setValue(displayKind.toString());
    }
    protected void doAutoDisplay()
    {
        if (ledCycleDeadline.hasExpired()) {
            pattern = pattern.next();
            displayPattern();
            ledCycleDeadline.reset();
        }
    }
    protected void displayPattern()
    {
        blinkinLedDriver.setPattern(pattern);
        patternName.setValue(pattern.toString());
    }
    
}

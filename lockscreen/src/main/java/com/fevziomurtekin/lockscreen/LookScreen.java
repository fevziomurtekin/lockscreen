package com.fevziomurtekin.lockscreen;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import in.arjsna.passcodeview.PassCodeView;

import static android.content.Context.FINGERPRINT_SERVICE;
import static android.content.Context.KEYGUARD_SERVICE;
import static java.security.AccessController.getContext;

public class LookScreen extends AppCompatActivity implements IAuthenticateListener, PassCodeView.TextChangeListener, View.OnClickListener {


    private boolean useFingerprint=false; /*If you have the FingerPrint sensor, we ask if you want to use it.*/

    private FingerprintHandler fingerprintHandler; /*FingerPrintHandler,*/

    private int digitCount=4; /*default four digit count in Pincode*/

    private PassCodeView passCodeView; /*Passcode*/

    private TextView textTitle,textMessage,textError; /*Creating title,message and error in layout.*/


    private float titleSize=15.0f,messageSize=14.0f,errorSize=14.0f; /*textSize*/

    private int titleColor,messageColor,errorColor; /*text color*/

    private String title="Enter your password.";

    private String message="Log in with your password or fingerprint reader.";

    private String error="You entered an incorrect password. Please try again.";

    private String pass="1234"; /*Default passcode.*/

    private FingerprintManager.CryptoObject cryptoObject;

    private FingerprintManager fingerprintManager;

    private KeyguardManager keyguardManager;

    private FingerprintHandler mFingerprintHandler;

    private IAuthenticateListener ıAuthenticateListener;

    private boolean isFingerPrint=false; /*Default fingerPrint*/

    private RelativeLayout relativeLayout;

    private Intent intent;

    private View.OnClickListener listener;

    private Context context=this;

    private Dialog dialog=null;

    private boolean onStart=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.security_layout);
        ıAuthenticateListener =this;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Get an instance of KeyguardManager and FingerprintManager//
            keyguardManager =
                    (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
            fingerprintManager =
                    (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
            if (fingerprintManager.isHardwareDetected())
                isFingerPrint = true;
        }

        relativeLayout  =new RelativeLayout(this);

        titleColor      = getResources().getColor(R.color.black);
        messageColor    = getResources().getColor(R.color.black);
        errorColor      = getResources().getColor(R.color.red);

        /*textTitle is created*/
        textTitle       =findViewById(R.id.txtTitle);
        textTitle       .setTextSize(titleSize);
        textTitle       .setTextColor(titleColor);
        textTitle       .setText(title);

        /*textMessage is created.*/
        textMessage     =findViewById(R.id.txtMessage);
        textMessage     .setTextSize(messageSize);
        textMessage     .setTextColor(messageColor);
        textMessage     .setText(message);

        /*textError is created.*/
        textError       =findViewById(R.id.txtError);
        textError       .setTextSize(errorSize);
        textError       .setTextColor(errorColor);
        textError       .setText(error);
        textError       .setVisibility(View.GONE);   /*Default error.*/


        passCodeView    =findViewById(R.id.pass);
        passCodeView    .setDigitLength(digitCount);
        passCodeView    .setKeyTextColor(Color.BLACK);
        passCodeView    .setEmptyDrawable(R.drawable.empty);
        passCodeView    .setFilledDrawable(R.drawable.fill);
        passCodeView    .setOnTextChangeListener(this); /*Passcode created.*/

        listener    =this;

        if( isFingerPrint) {
            fingerprintdialog(relativeLayout,listener);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                fingerprint();
            }
        }


    }


    @Override
    protected void onStart() {
        super.onStart();
        try{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Util.checkSensorState(this)) {
                    FingerprintManager.CryptoObject cryptoObject = Util.getCryptoObject();
                    if (cryptoObject != null) {
                        FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                        Dialog dialog = null;
                        mFingerprintHandler = new FingerprintHandler(this ,ıAuthenticateListener,dialog,pass);
                        mFingerprintHandler.startAuth(fingerprintManager, cryptoObject);
                    }
                }
            }
        }catch (Exception e){}
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mFingerprintHandler != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mFingerprintHandler.cancel();
            }
        }

    }

    private String generatedDefaultPass(int digitCount) {

        return null;
    }

    @Override
    public void onAuthenticate(String decryptPassword) {
        passCodeView.setPassCode(decryptPassword);
        Log.e("LookScreen", "fingerprint: " + decryptPassword);
        startActivity(intent);

    }

    @Override
    public void onError() {
        if(onStart)
            Toast.makeText(this,"Incorrect fingerprint.",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTextChanged(String text) {

        if(text.length() == digitCount){

            if(text.equals(pass)){
                try { if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { mFingerprintHandler.cancel(); } } catch (Exception e){}
               startActivity(intent);
            }else{
                textError   .setVisibility(View.VISIBLE);
                passCodeView.reset();
            }
        }

    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessageColor(int messageColor) {
        this.messageColor = messageColor;
    }

    @Override
    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }


    public void setTitle(String title) {
        this.title = title;
    }

    public void setDigitCount(int digitCount) {
        this.digitCount = digitCount;
    }

    public void setTitleSize(float titleSize) {
        this.titleSize = titleSize;
    }

    public void setErrorSize(float errorSize) {
        this.errorSize = errorSize;
    }

    public void setMessageSize(float messageSize) {
        this.messageSize = messageSize;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setErrorColor(int errorColor) {
        this.errorColor = errorColor;
    }

    @Override
    public void setIntent(Intent intent) {
        this.intent = intent;
    }
    public void setPass(String pass) {
        this.pass = pass;
    }

 /*   public void setUseFingerprint(boolean useFingerprint) {
        this.useFingerprint = useFingerprint;
    }*/

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btnPass) {
            Dialog dialog= ((Dialog)view.getTag());
            try{ if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { mFingerprintHandler.cancel(); } }catch (Exception e){}
            dialog.dismiss();
        }

    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void fingerprint() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            // If your app doesn't have this permission, then display the following text//
            Toast.makeText(this, getString(R.string.permissionfinger), Toast.LENGTH_LONG).show();
        }

        if (Util.checkSensorState(this)) {
            FingerprintManager.CryptoObject cryptoObject = Util.getCryptoObject();
            if (cryptoObject != null) {
                FingerprintManager fingerprintManager = (FingerprintManager) getSystemService(FINGERPRINT_SERVICE);
                Dialog dialog = null;
                onStart=true;
                mFingerprintHandler = new FingerprintHandler(this ,ıAuthenticateListener,dialog,pass);
                mFingerprintHandler.startAuth(fingerprintManager, cryptoObject);
            }
        }

    }

    private void fingerprintdialog(RelativeLayout relativeLayout, View.OnClickListener listener) {
        final Dialog dialog = new Dialog(context);

        RelativeLayout rlFingerprint;
        TextView txtFingerPrint,txtMessage,touchSensor;
        ImageView imgFingerPrint;
        Button btnPass;
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialogsecurity);
        dialog.setTitle("");
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        Window window = this.getWindow();
        window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.setCanceledOnTouchOutside(false);


        txtFingerPrint  = dialog.findViewById(R.id.txtFingerPrint);
        txtFingerPrint  .setText("To open the security lock, please read your fingerprint or enter your PIN code.");

        txtMessage      = dialog.findViewById(R.id.txtTouchSensor);
        txtMessage      .setText("Touch to Finger Print");

        btnPass         = dialog.findViewById(R.id.btnPass);
        btnPass         .setOnClickListener(listener);
        btnPass         .setTag(dialog);


        imgFingerPrint      = dialog.findViewById(R.id.imgFingerPrint);
        imgFingerPrint      .setImageDrawable(getResources().getDrawable(R.drawable.fingerprint));

        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}

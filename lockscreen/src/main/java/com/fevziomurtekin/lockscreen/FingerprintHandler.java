package com.fevziomurtekin.lockscreen;

import android.app.Dialog;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.CancellationSignal;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.widget.Toast;

import javax.crypto.Cipher;

@RequiresApi(api = Build.VERSION_CODES.M)
public class FingerprintHandler extends FingerprintManager.AuthenticationCallback {
    private Context mContext;
    private CancellationSignal mCancellationSignal;
    private IAuthenticateListener mListener;
    private Dialog dialog;

    public FingerprintHandler(Context context, IAuthenticateListener listener, Dialog dialog,String password) {
        mContext    = context;
        mListener   = listener;
        this.dialog = dialog;
        mCancellationSignal = new CancellationSignal();
    }

    public void startAuth(FingerprintManager fingerprintManager, FingerprintManager.CryptoObject cryptoObject) {
        fingerprintManager.authenticate(cryptoObject, mCancellationSignal, 0, this, null);
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if(errorCode!=5)
            Toast.makeText(mContext, errString, Toast.LENGTH_SHORT).show();
        mListener.onError();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(mContext, helpString, Toast.LENGTH_SHORT).show();
        mListener.onError();
    }

    @Override
    public void onAuthenticationSucceeded(FingerprintManager.AuthenticationResult result) {

        Cipher cipher = result.getCryptoObject().getCipher();
        String encoded = PreferenceManager.getDefaultSharedPreferences(mContext).getString("Pass", "");
        mListener.onAuthenticate(encoded);
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(mContext, "onAuthenticationFailed", Toast.LENGTH_SHORT).show();
        mListener.onError();
    }

    public void cancel() {
        if (mCancellationSignal != null) mCancellationSignal.cancel();
    }
}



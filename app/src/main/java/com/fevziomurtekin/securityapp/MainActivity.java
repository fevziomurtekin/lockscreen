package com.fevziomurtekin.securityapp;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.os.Bundle;

import com.fevziomurtekin.lockscreen.LookScreen;

public class MainActivity extends LookScreen {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setPass("1234");
        this.setDigitCount(4);
        this.setIntent(new Intent(this,SuccesActivity.class));
        this.setDigitCount(4);
        this.setTitle("YOUR TITLE MESSAGE");
        this.setTitleSize(15f);
        this.setTitleColor(getResources().getColor(R.color.black));
        this.setMessage("YOUR MESSAGE");
        this.setMessageSize(14f);
        this.setMessageColor(getResources().getColor(R.color.black));
        this.setError("YOUR ERROR MESSAGE");
        this.setErrorSize(14f);
        this.setErrorColor(getResources().getColor(R.color.red));
    }
}

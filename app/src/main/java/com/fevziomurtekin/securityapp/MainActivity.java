package com.fevziomurtekin.securityapp;

import android.content.Intent;
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
    }
}

package com.fevziomurtekin.lockscreen;

public interface IAuthenticateListener {

    void onAuthenticate(String decryptPassword);

    void onError();

}

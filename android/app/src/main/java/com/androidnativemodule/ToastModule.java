// ToastModule.java

package com.androidnativemodule;

import android.widget.Toast;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;

import java.util.Map;
import java.util.HashMap;

public class ToastModule extends ReactContextBaseJavaModule {
  private static ReactApplicationContext reactContext;
  public Integer result = 0;


  private static final String DURATION_SHORT_KEY = "SHORT";
  private static final String DURATION_LONG_KEY = "LONG";

  ToastModule(ReactApplicationContext context) {
    super(context);
    reactContext = context;
  }
    @Override
  public String getName() {
    return "ToastExample";
  }

    @ReactMethod
    public void show(String message, Callback errorCallback,Callback successCallback) {
        
        successCallback.invoke("Android native moduleden merhaba" + message);

    }
    @ReactMethod
  public  void addition (Integer firstElement , Integer secondElement , Callback errorCallback , Callback successCalback){

    result = firstElement + secondElement;
    successCalback.invoke("Sonuç :" + result);

  }
}
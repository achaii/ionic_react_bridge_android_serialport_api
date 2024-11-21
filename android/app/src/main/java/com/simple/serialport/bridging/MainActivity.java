package com.simple.serialport.bridging;

import android.os.Bundle;
import com.getcapacitor.BridgeActivity;
import com.simple.serialport.bridging.SerialPortBridgePlugin;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        registerPlugin(SerialPortBridgePlugin.class);
        super.onCreate(savedInstanceState);
    }
}

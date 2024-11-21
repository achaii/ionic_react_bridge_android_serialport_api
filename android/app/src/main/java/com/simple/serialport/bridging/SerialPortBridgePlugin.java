package com.simple.serialport.bridging;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import android_serialport_api.SerialPortConfiguration;
import android_serialport_api.PowerGPIO;
import android_serialport_api.OnDataReceiverListener;
import android_serialport_api.SerialPortFinder;
import android_serialport_api.SerialPort;

import java.util.Arrays;

/**
 * Capacitor plugin for managing serial port communication.
 */
@CapacitorPlugin(name = "SerialPortBridge")
public class SerialPortBridgePlugin extends Plugin {

    private int temInt;
    private SerialPortConfiguration device;
    private SerialPortFinder serialPortFinder;

    private SerialPort serialPort;
    private boolean openDevice;
    private SoundPool soundPool;
    private int soundId;

    /**
     * Retrieves a list of all available serial port devices.
     *
     * @param call The PluginCall object containing the request details.
     */
    @PluginMethod
    public void getDevices(PluginCall call) {
        try {
            JSObject response = new JSObject();

            String[] devices = new String[]{Arrays.toString(serialPortFinder.getAllDevicesPath())};

            response.put("devices", devices);

            call.resolve(response);
        } catch (Exception e) {
            JSObject response = new JSObject();

            response.put("devices", "");

            call.resolve(response);
        }
    }

    /**
     * Opens a specified serial port for communication.
     *
     * @param call The PluginCall object containing the device path and baud rate.
     */
    @PluginMethod
    public void openSerialPort(PluginCall call) {
        try {
            // Enable GPIO power.
            PowerGPIO.power("1");

            String path = call.getString("device");
            String baudRate = call.getString("baudrate");

            device = new SerialPortConfiguration(path, Integer.parseInt(baudRate, 16));
            openDevice = device.openDevice();

            // Load a sound effect for feedback.
            soundPool = new SoundPool(5, AudioManager.STREAM_MUSIC, 0);
            soundId = soundPool.load(getContext(), R.raw.rfid, 1);

            JSObject response = new JSObject();

            response.put("serial_port", "open");

            call.resolve(response);
        } catch (Exception e) {
            Log.e("ERR", String.valueOf(e));
            JSObject response = new JSObject();

            response.put("serial_port", "close");

            call.resolve(response);
        }
    }

    /**
     * Closes the currently open serial port and disables GPIO power.
     *
     * @param call The PluginCall object containing the request details.
     */
    @PluginMethod
    public void closeSerialPort(PluginCall call) {
        try {
            device.closeDevice();

            // Disable GPIO power.
            PowerGPIO.power("0");

            JSObject response = new JSObject();

            response.put("serial_port", "close");

            call.resolve(response);
        } catch (Exception e) {
            JSObject response = new JSObject();

            response.put("serial_port", "error");

            call.resolve(response);
        }
    }

    /**
     * Reads data from the serial port and processes the scanned result.
     *
     * @param call The PluginCall object containing the request details.
     */
    @PluginMethod
    public void getScanResult(PluginCall call) {
        if (openDevice) {
            device.setOnDataReceiverListener(new OnDataReceiverListener() {
                @Override
                public void onDataReceiver(byte[] buffer, int size) {
                    if (size > 0) {
                        byte[] tempBuf = new byte[size - 2];
                        System.arraycopy(buffer, 2, tempBuf, 0, size - 2);
                        Log.e("RFID", "in byte: " + tempBuf);
                        String temStr = new String(tempBuf);
                        Log.e("RFID", "in string: " + temStr);
                        temInt = Integer.parseInt(temStr, 16);
                        soundPool.play(soundId, 1, 1, 0, 0, 1);
                    } else {
                        temInt = 0;
                    }

                    JSObject response = new JSObject();

                    String result = String.valueOf(temInt);

                    response.put("result", result);

                    call.resolve(response);
                }
            });

            // Sends an empty message to trigger the listener.
            device.sendMessage(new byte[0x00]);
        } else {
            Log.i("RFID", "Error: Serial Port Is Not Running");
        }
    }
}

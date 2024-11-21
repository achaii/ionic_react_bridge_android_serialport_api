package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPortConfiguration {

    private static final String TAG = "Utils_SerialPortManager";
    private final SerialPortFinder serialPortFinder;
    private final String deviceName;
    private final int baudRate;
    private SerialPort uSerialPort;
    private OutputStream uOutputStream;
    private InputStream uInputStream;
    private ReadDeviceThread uReadDeviceThread;
    private OnDataReceiverListener onDataReceiverListener;

    /**
     * Constructor to initialize SerialPortConfiguration.
     *
     * @param Device   The serial port device name (e.g., "/dev/ttyS3").
     * @param Baudrate The baud rate for communication (e.g., 9600).
     */
    public SerialPortConfiguration(String Device, int Baudrate) {
        serialPortFinder = new SerialPortFinder();
        deviceName = Device;
        baudRate = Baudrate;
        uSerialPort = null;
    }

    /**
     * Retrieves a list of all available serial port device paths.
     *
     * @return An array of strings containing device paths.
     */
    private String[] getDevicesList() {
        return serialPortFinder.getAllDevicesPath();
    }

    /**
     * Opens the serial port with the specified device and baud rate.
     *
     * @return True if the serial port was opened successfully, false otherwise.
     */
    public boolean openDevice() {
        String[] comList = getDevicesList();
        for (String comName : comList) {
            Log.i(TAG, "Device names：" + comName);
        }
        if (uSerialPort == null) {
            try {
                uSerialPort = new SerialPort(new File(deviceName), baudRate, 0);
                uOutputStream = uSerialPort.getOutputStream();
                uInputStream = uSerialPort.getInputStream();
                // Start the thread to read data from the serial port.
                uReadDeviceThread = new ReadDeviceThread();
                uReadDeviceThread.start();
                return true;
            } catch (Exception e) {
                Log.i(TAG, e.getMessage());
                uSerialPort = null;
            }
        }
        return false;
    }

    /**
     * Closes the currently open serial port.
     */
    public void closeDevice() {
        if (uSerialPort != null) {
            uReadDeviceThread.interrupt();
            uSerialPort.closeInputOutputStream();
            uSerialPort.close();
            uSerialPort = null;
        }
    }

    /**
     * Sends data through the serial port.
     *
     * @param data The data to be sent as a byte array.
     * @return True if the data was sent successfully, false otherwise.
     */
    public boolean sendMessage(byte[] data) {
        try {
            if (uOutputStream != null) {
                uOutputStream.write(data);
                uOutputStream.flush();
            } else {
                return false;
            }
        } catch (IOException e) {
            Log.i(TAG, e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * Thread to continuously read data from the serial port.
     */
    private class ReadDeviceThread extends Thread {

        @Override
        public void run() {
            while (!isInterrupted()) {
                try {
                    byte[] buffer = new byte[24];
                    if (uInputStream == null) {
                        break;
                    }
                    int size = uInputStream.read(buffer);
                    if (size > 0) {
                        Thread.sleep(500); // Delay to process the received data.
                        onDataReceiverListener.onDataReceiver(buffer, size);
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    break;
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
    }

    /**
     * Sets a listener for receiving data from the serial port.
     *
     * @param onDataReceiverListener The callback to handle received data.
     */
    public void setOnDataReceiverListener(OnDataReceiverListener onDataReceiverListener) {
        this.onDataReceiverListener = onDataReceiverListener;
    }
}

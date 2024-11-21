package android_serialport_api;

import android.util.Log;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SerialPort {

    private static final String TAG = "Utils_SerialPort";

    static {
        System.loadLibrary("serial_port");
    }

    private FileInputStream fileInputStream;

    private FileOutputStream fileOutputStream;

    /**
     * Native method to open a serial port.
     *
     * @param path      The path of the serial device.
     * @param baudRate  The baud rate for communication.
     * @param flags     Flags for additional configurations.
     * @return A FileDescriptor for the opened serial port.
     */
    private native static FileDescriptor open(String path, int baudRate, int flags);

    /**
     * Native method to close the serial port.
     */
    public native void close();

    /**
     * Constructor to initialize and open a serial port.
     *
     * @param device                The serial device file.
     * @param baudRate              The baud rate for communication.
     * @param flags                 Flags for additional configurations.
     * @throws SecurityException    If the application lacks permission to access the device.
     * @throws IOException          If an I/O error occurs during initialization.
     */
    public SerialPort(File device, int baudRate, int flags) throws SecurityException, IOException {
        if (!device.canRead() || !device.canWrite()) {
            try {
                // Attempt to gain read/write permissions for the device using superuser privileges.
                Process su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 777 " + device.getAbsolutePath() + "\n" + "exit\n";
                su.getOutputStream().write(cmd.getBytes());

                if ((su.waitFor() != 0) || !device.canRead() || !device.canWrite()) {
                    throw new SecurityException();
                }

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, e.getMessage());
                throw new SecurityException();
            }
        }

        // Open the serial port.
        FileDescriptor mFd = open(device.getAbsolutePath(), baudRate, flags);

        if (mFd == null) {
            Log.i(TAG, "open method and returned null");
            throw new IOException();
        }

        // Initialize input and output streams.
        fileInputStream = new FileInputStream(mFd);
        fileOutputStream = new FileOutputStream(mFd);
    }

    /**
     * Closes the input and output streams associated with the serial port.
     */
    public void closeInputOutputStream() {
        try {
            if (fileInputStream != null) {
                fileInputStream.close();
            }
            if (fileOutputStream != null) {
                fileOutputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(TAG, e.getMessage());
        } finally {
            fileInputStream = null;
            fileOutputStream = null;
        }
    }

    /**
     * Gets the input stream for reading data from the serial port.
     *
     * @return An InputStream for the serial port.
     */
    public InputStream getInputStream() {
        return fileInputStream;
    }

    /**
     * Gets the output stream for writing data to the serial port.
     *
     * @return An OutputStream for the serial port.
     */
    public OutputStream getOutputStream() {
        return fileOutputStream;
    }
}

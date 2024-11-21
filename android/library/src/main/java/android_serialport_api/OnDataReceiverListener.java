package android_serialport_api;

/**
 * Interface for handling data received from the serial port.
 */
public interface OnDataReceiverListener {

    /**
     * Callback method triggered when data is received from the serial port.
     *
     * @param buffer The byte array containing the received data.
     * @param size   The size of the received data in bytes.
     */
    void onDataReceiver(byte[] buffer, int size);
}

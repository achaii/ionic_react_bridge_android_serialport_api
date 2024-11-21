package android_serialport_api;

import android.util.Log;
import java.io.File;
import java.io.FileWriter;

/**
 * Utility class for controlling GPIO (General Purpose Input/Output) power using a specified ID.
 */
public class PowerGPIO {

    // Path to the file controlling GPIO power some android model not support
    private static final String SAM = "/proc/gpiocontrol/set_id";

    private static final String TAG = "PowerGPIO";

    /**
     * Sends a power command to the GPIO controller using a specified ID.
     *
     * @param id The ID string to be written to the GPIO control file.
     */
    public static void power(String id) {
        try {
            File file = new File(SAM);
            Log.d(TAG, "Power: " + file.getPath());
            // Open the file for writing
            FileWriter localFileWriterOn = new FileWriter(new File(SAM));
            // Write the ID to the file
            localFileWriterOn.write(id);
            // Close the file after writing
            localFileWriterOn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

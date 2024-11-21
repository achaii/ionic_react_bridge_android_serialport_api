package android_serialport_api;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.Iterator;
import java.util.Vector;

public class SerialPortFinder {

    private static final String TAG = "UtilsSerialPort";

    private Vector<Driver> deviceDriver;

    /**
     * Retrieves a list of drivers associated with serial devices.
     * Reads the file `/proc/tty/drivers` to identify drivers handling serial devices.
     *
     * @return A vector of `Driver` objects representing available serial device drivers.
     * @throws IOException If an error occurs while reading the drivers file.
     */
    private Vector<Driver> getDrivers() throws IOException {
        if (deviceDriver == null) {
            deviceDriver = new Vector<>();
            LineNumberReader r = new LineNumberReader(new FileReader("/proc/tty/drivers"));
            String line;
            while ((line = r.readLine()) != null) {
                String driverName = line.substring(0, 0x15).trim();
                String[] w = line.split(" +");
                if ((w.length >= 5) && ("serial".equals(w[w.length - 1]))) {
                    deviceDriver.add(new Driver(driverName, w[w.length - 4]));
                }
            }
            r.close();
        }
        return deviceDriver;
    }

    /**
     * Retrieves the names of all available serial devices.
     *
     * @return An array of strings representing the names and associated drivers of the devices.
     */
    public String[] getAllDevices() {
        Vector<String> devices = new Vector<>();
        try {
            Iterator<Driver> iteratorDriver = getDrivers().iterator();
            while (iteratorDriver.hasNext()) {
                Driver driver = iteratorDriver.next();
                Iterator<File> iteratorDevices = driver.getDevices().iterator();
                while (iteratorDevices.hasNext()) {
                    String device = iteratorDevices.next().getName();
                    String value = String.format("%s (%s)", device, driver.getName());
                    devices.add(value);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * Retrieves the paths of all available serial devices.
     *
     * @return An array of strings representing the file paths of the devices.
     */
    public String[] getAllDevicesPath() {
        Vector<String> devices = new Vector<>();
        try {
            Iterator<Driver> iteratorDriver = getDrivers().iterator();
            while (iteratorDriver.hasNext()) {
                Driver driver = iteratorDriver.next();
                Iterator<File> iteratorDevices = driver.getDevices().iterator();
                while (iteratorDevices.hasNext()) {
                    String device = iteratorDevices.next().getAbsolutePath();
                    devices.add(device);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return devices.toArray(new String[devices.size()]);
    }

    /**
     * Inner class representing a driver associated with serial devices.
     */
    public class Driver {

        /**
         * Constructor to initialize a Driver instance.
         *
         * @param name The name of the driver.
         * @param root The root directory where the devices for this driver are located.
         */
        public Driver(String name, String root) {
            deviceDriverName = name;
            deviceRoot = root;
        }

        private final String deviceDriverName;

        private final String deviceRoot;

        Vector<File> mDevices = null;

        /**
         * Retrieves a list of device files associated with this driver.
         *
         * @return A vector of `File` objects representing the device files.
         */
        public Vector<File> getDevices() {
            if (mDevices == null) {
                mDevices = new Vector<>();
                File dev = new File("/dev");
                File[] files = dev.listFiles();
                assert files != null;
                for (File file : files) {
                    if (file.getAbsolutePath().startsWith(deviceRoot)) {
                        mDevices.add(file);
                    }
                }
            }
            return mDevices;
        }

        /**
         * Retrieves the name of the driver.
         *
         * @return A string representing the driver's name.
         */
        public String getName() {
            return deviceDriverName;
        }
    }
}

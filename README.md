# Ionic 8 React Bridge Android Serial Port API

Accessing data communication via the serial port on the Android PDA U9000 family, with the default port configured for using the LF (Low Frequency) RFID Reader on port "/dev/ttyS3" and a baud rate of "9600".

On this PDA, it is necessary to activate GPIO Control at the root "/proc/gpiocontrol/set_id", where setting 1 enables and 0 disables the control. This directly affects the LF RFID Reader.

### API Bridge

The usage guidelines can be seen in the code block below:
```TSX
import { registerPlugin } from "@capacitor/core";

interface SerialPortBridgePlugin {
    getDevices(): Promise<{ devices: string }>;
    openSerialPort(options: {device: string, baudrate: string}): Promise<{ serial_port: string }>;
    closeSerialPort(): Promise<{ serial_port: string }>;
    getScanResult(): Promise<{ result: string }>;

}

const SerialPortBridge = registerPlugin<SerialPortBridgePlugin>('SerialPortBridge');

export default SerialPortBridge;
```

### Developing
Testing is ongoing to identify potential bugs. However, it has been successfully applied in production, and so far, I have experienced no issues.

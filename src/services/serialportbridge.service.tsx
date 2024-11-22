import { registerPlugin } from "@capacitor/core";

interface SerialPortBridgePlugin {
    getDevices(): Promise<{ devices: string }>;
    openSerialPort(options: {port: string, baudrate: string}): Promise<{ message: string }>;
    closeSerialPort(): Promise<{ message: string }>;
    getScanResult(): Promise<{ result: string }>;
}

const SerialPortBridge = registerPlugin<SerialPortBridgePlugin>('SerialPortBridge');

export default SerialPortBridge;
import { registerPlugin } from "@capacitor/core";

interface SerialPortBridgePlugin {
    getDevices(): Promise<{ devices: string }>;
    openSerialPort(options: {device: string, baudrate: string}): Promise<{ serial_port: string }>;
    closeSerialPort(): Promise<{ serial_port: string }>;
    getScanResult(): Promise<{ result: string }>;
}

const SerialPortBridge = registerPlugin<SerialPortBridgePlugin>('SerialPortBridge');

export default SerialPortBridge;
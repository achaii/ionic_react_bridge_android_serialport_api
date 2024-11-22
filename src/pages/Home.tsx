import { IonContent, IonHeader, IonPage, IonTitle, IonToolbar, IonList, IonButton } from '@ionic/react';
import SerialPortBridge from '../services/serialportbridge.service';
import React, { useState, useEffect } from 'react';
import './Home.css';

const Home: React.FC = () => {
  const [devices, setDevices] = useState('');
  const [devicesOn, setDevicesOn] = useState('');
  const [scan, setScan] = useState('');

  useEffect(() => {
    devicesOnPort();
    devicesLoad();
  });

  useEffect(() => {
    const interval = setInterval(async () => {
        await deviceScan();
    }, 1000);

    return () => clearInterval(interval);
}, []);

  const devicesLoad = async () => {
    try{
      const serial = await SerialPortBridge.getDevices();

      if(serial.devices.length > 0){
        setDevices(serial.devices);
      }else{
        setDevices(serial.devices);
      }
    }catch{
      console.log("error: devices not found");
    }
  };

  const devicesOnPort = async () => {
    try{
      const serial = await SerialPortBridge.openSerialPort({
        port : '/dev/ttyS3', 
        baudrate: '9600'
      });

      if(serial.message.length > 0){
        setDevicesOn(serial.message);
      }else{
        setDevicesOn(serial.message);
      }
    }catch{
      console.log("error: device not open");
    }
  };

  const devicesOffPort = async () => {
    try{
      const serial = await SerialPortBridge.closeSerialPort();

      if(serial.message.length > 0){
        setDevicesOn(serial.message);
      }else{
        setDevicesOn(serial.message);
      }
    }catch{
      console.log("error: serial port close");
    }
  };

  const deviceScan = async () => {
    try{
      const serial = await SerialPortBridge.getScanResult();

      if(serial && serial.result){
        setScan(serial.result);
      }
    }catch{
      console.log("error: serial port close");
    }
  };

  return (
    <IonPage>
      <IonHeader>
        <IonToolbar>
          <IonTitle>Serial Port Bridging</IonTitle>
        </IonToolbar>
      </IonHeader>
      <IonContent fullscreen className="ion-padding">
        <IonList>
          <h2>
            Serial Port Devices
          </h2>
          <p>{ devices }</p>
        </IonList>
        <IonList>
          <h2>
            Serial Port Used
          </h2>
          <p>{ devicesOn }</p>
        </IonList>
        <IonList>
          <h2>
            Serial Port Close
          </h2>
          <IonButton fill="solid" expand="full" onClick={devicesOffPort}>
              Close
          </IonButton>
        </IonList>
        <IonList>
          <h2>
            Serial Port Scan
          </h2>
          <p>
            { scan }
          </p>
        </IonList>
      </IonContent>
    </IonPage>
  );
};

export default Home;

package sistema;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;

import java.util.ArrayList;
import java.util.Set;


public class Bluetooth {
    protected static BluetoothAdapter 	bluetooth   = BluetoothAdapter.getDefaultAdapter();
    protected static Bluetooth          instance    = null;


    public static Bluetooth getInstance(){
        if(instance == null){
            instance = new Bluetooth();
        }
        return instance;
    }


    private Bluetooth(){
    }


    /*
    Funcion para detectar si el equipo tiene bluetooth
     */
    public boolean ExistBluetooth(){
        boolean result = false;
        if(bluetooth != null){
            EnabledBluetoth();
            result = true;
        }
        return result;
    }


    /*
        Funcion para habilitar el bluetooth, si esta inhabiltado lo habilita
     */
    public boolean EnabledBluetoth(){
        if(!bluetooth.isEnabled()){
           //bluetooth.enable();
        }
        return bluetooth.isEnabled();
    }

    /*
    Metodo para capturar el nombre del bluetooth del dispositivo que estamos manejando
     */
    public String GetOurDeviceByName(){
        String status;
        if (ExistBluetooth()) {
            status = bluetooth.getName().toString();
        }else{
            status = "Bluetooth is not Enabled.";
        }
       return status;
    }

    /*
    Metodo para capturar la MAC del bluetooth del dispositivo que estamos manejando
     */
    public String GetOurDeviceByAddress(){
        String status;
        if (ExistBluetooth()) {
            status = bluetooth.getAddress().toString();
        }else{
            status = "Bluetooth is not Enabled.";
        }
        return status;
    }


    public ArrayList<String> GetDeviceBluetoothByAddress(){
        ArrayList<String> _lstDevice= new ArrayList<String>();
        _lstDevice.add("");
        try{
            Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
            if(pairedDevices.size() > 0){
                for(BluetoothDevice device : pairedDevices){
                   _lstDevice.add(device.getAddress());
                }
            }
        }catch(Exception e){
        }
        return _lstDevice;
    }


    public ArrayList<String> GetDeviceBluetoothByName(){
        ArrayList<String> _lstDevice= new ArrayList<String>();
        _lstDevice.add("");
        try{
            Set<BluetoothDevice> pairedDevices = bluetooth.getBondedDevices();
            if(pairedDevices.size() > 0){
                for(BluetoothDevice device : pairedDevices){
                    _lstDevice.add(device.getName());
                }
            }
        }catch(Exception e){
        }
        return _lstDevice;
    }
}



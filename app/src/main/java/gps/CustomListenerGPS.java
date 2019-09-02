package gps;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Created by JULIANEDUARDO on 14/04/2015.
 */

public class CustomListenerGPS implements LocationListener{

    public static final int GPS_INTERVAL_SECONDS = 0;
    public static final int GPS_INTERVAL_METERS = 0;

    private static CustomListenerGPS instanceGPS;
    private PointGPS myPointGPS = PointGPS.getInstance();
    private Context ctx;


    public static CustomListenerGPS getInstance(Context _ctx) {
        if(instanceGPS == null){
            instanceGPS = new CustomListenerGPS(_ctx);
        }
        return instanceGPS;
     }


    private CustomListenerGPS(Context _ctx){
        myPointGPS.setEstadoGPS(false);
        myPointGPS.setLatitudGPS("0.0");
        myPointGPS.setLongitudGPS("0.0");
        this.ctx    = _ctx;
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub

        if(myPointGPS.equals("0.0")|| myPointGPS.equals("0.0"))
        {
            Toast.makeText(this.ctx, "Ubicacion GPS Detectada.", Toast.LENGTH_LONG).show();
        }

        if(!myPointGPS.getLatitudGPS().equals(String.valueOf(location.getLatitude())) ||
                !myPointGPS.getLongitudGPS().equals(String.valueOf(location.getLongitude())))
        {
            myPointGPS.setLatitudGPS(String.valueOf(location.getLatitude()));
            myPointGPS.setLongitudGPS(String.valueOf(location.getLongitude()));
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onProviderEnabled(String provider) {
        myPointGPS.setEstadoGPS(true);
    }

    @Override
    public void onProviderDisabled(String provider) {
        myPointGPS.setEstadoGPS(false);
    }

}
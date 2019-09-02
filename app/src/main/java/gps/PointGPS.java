package gps;

import java.util.Observable;

/**
 * Created by Julian Poveda on 30/08/2016.
 */
public class PointGPS extends Observable {

    private static PointGPS myPoint;

    private boolean estadoGPS;
    private String 	latitudGPS;
    private String 	longitudGPS;

    public static PointGPS getInstance()
    {
        if (myPoint == null)
        {
            myPoint = new PointGPS();
        }
        return myPoint;
    }


    private PointGPS(){
        this.estadoGPS = false;
        this.latitudGPS = null;
        this.longitudGPS = null;
    }


    public boolean isEstadoGPS() {
        return estadoGPS;
    }

    protected void setEstadoGPS(boolean estadoGPS) {
        this.estadoGPS = estadoGPS;
        setChanged();
        notifyObservers();
    }

    public String getLatitudGPS() {
        return latitudGPS;
    }

    protected void setLatitudGPS(String latitudGPS) {
        this.latitudGPS = latitudGPS;

        setChanged();
        notifyObservers();
    }

    public String getLongitudGPS() {
        return longitudGPS;
    }

    protected void setLongitudGPS(String longitudGPS) {
        this.longitudGPS = longitudGPS;

        setChanged();
        notifyObservers();
    }
}

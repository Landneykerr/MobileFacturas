package Adapter;

/**
 * Created by SypelcDesarrollo on 04/02/2015.
 */
public class RutasData {

    private String _codigoRuta;
    private String _totalRutas;
    private String _totalLeidas;
    private String _totalPendientes;

    public RutasData(String codigoRuta, String totalPendientes, String totalLeidas, String totalRuta){
            this._codigoRuta = codigoRuta;
            this._totalRutas = totalRuta;
            this._totalLeidas = totalLeidas;
            this._totalPendientes = totalPendientes;
    }

    public String getCodigoRuta() {
        return _codigoRuta;
    }

    public void setCodigoRuta(String codigoRuta) {
        this._codigoRuta = codigoRuta;
    }

    public String getTotalRutas() {
        return _totalRutas;
    }

    public void setTotalRutas(String totalRutas) {
        this._totalRutas = totalRutas;
    }

    public String getTotalLeidas() {
        return _totalLeidas;
    }

    public void setTotalLeidas(String totalLeidas) {
        this._totalLeidas = totalLeidas;
    }

    public String getTotalPendientes() {
        return _totalPendientes;
    }

    public void setTotalPendientes(String totalPendientes) {
        this._totalPendientes = totalPendientes;
    }
}

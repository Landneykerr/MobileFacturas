package async_task;

import android.content.ContentValues;
import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Observable;

import sistema.SQLite;

/**
 * Created by JULIANEDUARDO on 03/02/2015.
 */
public class FlujoInformacion extends Observable {
    private Context             context;
    private String              _campos[];
    private ContentValues       _tempRegistro;
    private ArrayList<ContentValues> _tempTabla;

    private static SQLite FcnSQL;
    private String titulo;
    private String mensaje;
    private String progreso;
    private String total;
    private int totalRutas;

    public FlujoInformacion(Context _ctx){
        this.context        = _ctx;
        this._tempRegistro  = new ContentValues();
        this.FcnSQL         = new SQLite(this.context);
        this.mensaje = null;
        this.progreso = "0";
    }


    public void updateInspectores(JSONArray _inspectores) throws JSONException{
        this.setTitulo("CARGANDO INSPECTORES...");
        this.setTotal(_inspectores.length()+"");
        this.FcnSQL.DeleteRegistro("param_usuarios", "tipo<>0");
        for(int i=0;i<_inspectores.length();i++) {
            this._tempRegistro.clear();
            this._tempRegistro.put("id_inspector", _inspectores.getJSONObject(i).getString("id_inspector"));
            this._tempRegistro.put("nombre", _inspectores.getJSONObject(i).getString("nombre"));
            this._tempRegistro.put("cedula", _inspectores.getJSONObject(i).getString("cedula"));
            this._tempRegistro.put("tipo", 1);
            this.FcnSQL.InsertRegistro("param_usuarios", this._tempRegistro);

            this.setMensaje(_inspectores.getJSONObject(i).getString("nombre"));
            this.setProgreso(i+"");
            setChanged();
            notifyObservers();
        }
    }

    public void updateMensajes(JSONArray _mensajes) throws JSONException{
        this.setTitulo("CARGANDO MENSAJES...");
        this.setTotal(_mensajes.length()+"");
        this.FcnSQL.DeleteRegistro("parametros_mensajes", "codigo IS NOT NULL");
        for(int i=0;i<_mensajes.length();i++) {
            this._tempRegistro.clear();
            this._tempRegistro.put("codigo", _mensajes.getJSONObject(i).getString("codigo"));
            this._tempRegistro.put("mensaje", _mensajes.getJSONObject(i).getString("mensaje"));
            this.FcnSQL.InsertRegistro("parametros_mensajes", this._tempRegistro);

            this.setMensaje(_mensajes.getJSONObject(i).getString("codigo"));
            this.setProgreso(i+"");
            setChanged();
            notifyObservers();
        }
    }


    public void updateDistancia(JSONArray _distancia) throws JSONException{
        this.setTitulo("CARGANDO DISTANCIA...");
        this.setTotal(_distancia.length()+"");
        this.FcnSQL.DeleteRegistro("parametros_distancia", "id IS NOT NULL");
        for(int i=0;i<_distancia.length();i++) {
            this._tempRegistro.clear();
            this._tempRegistro.put("id", _distancia.getJSONObject(i).getString("id_serial"));
            this._tempRegistro.put("distancia", _distancia.getJSONObject(i).getString("distancia"));
            this.FcnSQL.InsertRegistro("parametros_distancia", this._tempRegistro);

            this.setMensaje(_distancia.getJSONObject(i).getString("distancia"));
            this.setProgreso(i+"");
            setChanged();
            notifyObservers();
        }
    }



    public void eraseTrabajo(JSONArray _rutas, String _tipo)throws JSONException{
        for(int i=0; i<_rutas.length();i++){
            this._tempTabla = this.FcnSQL.SelectData("maestro_clientes",
                    "cuenta",
                    "id_programacion="+_rutas.getJSONObject(i).get("id_programacion"));

            for(int j=0; j<this._tempTabla.size();j++){
                this.FcnSQL.DeleteRegistro("entrega_factura",
                        "cuenta=" + this._tempTabla.get(j).getAsInteger("cuenta"));
            }
            this.FcnSQL.DeleteRegistro("maestro_clientes","id_programacion="+_rutas.getJSONObject(i).get("id_programacion"));
            this.FcnSQL.DeleteRegistro("maestro_rutas","id_programacion="+_rutas.getJSONObject(i).get("id_programacion"));
        }
    }


    public void loadTrabajo(JSONArray _rutas)throws JSONException{
        for(int i=0;i<_rutas.length();i++) {
            this.totalRutas=_rutas.length();
            this.setTitulo("RUTA " + _rutas.getJSONObject(i).getString("ciclo") + "-" + _rutas.getJSONObject(i).getString("municipio") + "-" + _rutas.getJSONObject(i).getString("ruta")+ " ("+(i+1)+" DE "+_rutas.length()+")");
            this._tempRegistro.clear();
            this._tempRegistro.put("id_programacion", _rutas.getJSONObject(i).getString("id_programacion"));
            this._tempRegistro.put("id_inspector", _rutas.getJSONObject(i).getString("id_inspector"));
            this._tempRegistro.put("ciclo", _rutas.getJSONObject(i).getString("ciclo"));
            this._tempRegistro.put("municipio", _rutas.getJSONObject(i).getString("municipio"));
            this._tempRegistro.put("mes", _rutas.getJSONObject(i).getString("mes"));
            this._tempRegistro.put("anno", _rutas.getJSONObject(i).getString("anno"));
            this._tempRegistro.put("ruta", _rutas.getJSONObject(i).getString("ruta"));
            this.FcnSQL.InsertRegistro("maestro_rutas", this._tempRegistro);


            JSONArray _cuentas = _rutas.getJSONObject(i).getJSONArray("cuentas");
            JSONArray _gps     = _rutas.getJSONObject(i).getJSONArray("gps");
            this.setTotal(_cuentas.length() + "");
            for(int j=0; j<_cuentas.length();j++){
                this._tempRegistro.clear();
                this._tempRegistro.put("id_programacion", _rutas.getJSONObject(i).getInt("id_programacion"));
                this._tempRegistro.put("id_secuencia", _cuentas.getJSONObject(j).getInt("id"));
                this._tempRegistro.put("cuenta", _cuentas.getJSONObject(j).getString("cuenta"));
                this._tempRegistro.put("nombre", _cuentas.getJSONObject(j).getString("nombre"));
                this._tempRegistro.put("direccion", _cuentas.getJSONObject(j).getString("direccion"));
                this._tempRegistro.put("marca_contador", _cuentas.getJSONObject(j).getString("marca_con"));
                this._tempRegistro.put("serie_contador", _cuentas.getJSONObject(j).getString("numero_con"));
                this._tempRegistro.put("secuencia_imp", _cuentas.getJSONObject(j).getString("sec_imp"));
                this._tempRegistro.put("secuencia_ruta", _cuentas.getJSONObject(j).getString("sec_ruta"));
                this._tempRegistro.put("estado", _cuentas.getJSONObject(j).getString("estado"));
                this._tempRegistro.put("codigo_ruta", _cuentas.getJSONObject(j).getString("codigo_ruta"));
                this._tempRegistro.put("latitud", _gps.getJSONObject(j).getString("latitud"));
                this._tempRegistro.put("longitud", _gps.getJSONObject(j).getString("longitud"));
                this.FcnSQL.InsertRegistro("maestro_clientes", this._tempRegistro);

                this.setMensaje(_cuentas.getJSONObject(j).getString("cuenta")+" "+_cuentas.getJSONObject(j).getString("nombre"));
                this.setProgreso(j+1+"");
                setChanged();
                notifyObservers();
            }
        }
    }

    public String getTrabajoCargado(){
        String _retorno = "-1,";
        this._tempTabla = this.FcnSQL.SelectData("maestro_rutas","id_programacion" );

        for(int i=0; i<this._tempTabla.size();i++){
            _retorno += this._tempTabla.get(i).getAsInteger("id_programacion")+",";
        }
        if(!_retorno.isEmpty()){
            _retorno = _retorno.substring(0,_retorno.length()-1);
        }
        return _retorno;
    }

    public void deleteRegistroFoto(String _nombreFoto){
        this.FcnSQL.DeleteRegistro("registro_fotos","nombre_foto='"+_nombreFoto+"'");
    }

    public String getTitulo() {
        return titulo;
    }

    private void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensaje() {
        return mensaje;
    }

    private void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public String getProgreso() {
        return progreso;
    }

    private void setProgreso(String progreso) {
        this.progreso = progreso;
    }

    public String getTotal() {
        return total;
    }

    private void setTotal(String total) {
        this.total = total;
    }
}

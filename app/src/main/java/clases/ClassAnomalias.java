package clases;

import android.content.ContentValues;
import android.content.Context;

import java.util.ArrayList;

import sistema.SQLite;

/**
 * Created by JULIANEDUARDO on 26/02/2015.
 */
public class ClassAnomalias {
    private static  ClassAnomalias ourInstance;
    private static  ArrayList<String>           _lstAnomalias;
    private static  ArrayList<String>           _lstMensajes;
    private static  SQLite                      FcnSQL;
    private static  ContentValues               _tempRegistro;
    private static  ArrayList<ContentValues>    _tempTabla;
    private static  Context                     ctx;


    public static ClassAnomalias getInstance(Context _ctx){
        if(ourInstance == null){
            ourInstance = new ClassAnomalias(_ctx);
            _tempRegistro  = new ContentValues();
        }else{
            ctx = _ctx;
        }
        return ourInstance;
    }


    public static ClassAnomalias getInstance(){
        return ourInstance;
    }


    private ClassAnomalias(Context _ctx) {
        this.ctx            = _ctx;
        this._tempRegistro  = new ContentValues();
        this._lstAnomalias  = new ArrayList<String>();
        this.FcnSQL         = new SQLite(this.ctx);
        this._lstMensajes  = new ArrayList<String>();
    }


    /**
     *
     * @param   _tipo_uso se usa para filtrar si la anomalia aplica a residenciales o no resienciales
     * @return  un arrayList<String> con los codigos de las anomalias segun sea residencial o no residencial
     */
    public ArrayList<String> getAnomalias(String _tipo_uso) {
        this._lstAnomalias.clear();
        if(_tipo_uso.equals("RS")){
            this._tempTabla = this.FcnSQL.SelectData(   "param_anomalias",
                                                        "id_anomalia, descripcion",
                                                        "aplica_residencial='t'");
        }else{
            this._tempTabla = this.FcnSQL.SelectData(   "param_anomalias",
                                                        "id_anomalia, descripcion",
                                                        "aplica_no_residencial='t'");
        }

        for(int i=0;i<this._tempTabla.size();i++){
            this._tempRegistro = this._tempTabla.get(i);
            this._lstAnomalias.add(this._tempRegistro.getAsString("id_anomalia")+"-"+this._tempRegistro.getAsString("descripcion"));
        }
        return this._lstAnomalias;
    }

    public ArrayList<String> getMensaje(){
        this._lstMensajes.clear();
        this._tempTabla = this.FcnSQL.SelectData("parametros_mensajes",
                "mensaje",
                "codigo IS NOT NULL");

        for(int i=0;i<this._tempTabla.size();i++){
            this._tempRegistro = this._tempTabla.get(i);
            this._lstMensajes.add(this._tempRegistro.getAsString("mensaje"));
        }

        return this._lstMensajes;
    }

    public int getDistancia(){
        this._tempTabla = this.FcnSQL.SelectData("parametros_distancia",
                "distancia",
                "id IS NOT NULL LIMIT 1");
        this._tempRegistro = this._tempTabla.get(0);
        return this._tempRegistro.getAsInteger("distancia");
    }


    public boolean needFoto(int _anomalia){
        return this.FcnSQL.ExistRegistros("param_anomalias","id_anomalia="+_anomalia+" AND foto='t'");
    }

    public boolean needLectura(int _anomalia){
        return this.FcnSQL.ExistRegistros("param_anomalias","id_anomalia="+_anomalia+" AND lectura='t'");
    }

    public boolean needMensaje(int _anomalia){
        return this.FcnSQL.ExistRegistros("param_anomalias","id_anomalia="+_anomalia+" AND mensaje='S'");
    }
}

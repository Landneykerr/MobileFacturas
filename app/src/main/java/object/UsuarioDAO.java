package object;

import android.content.ContentValues;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;

import global.global_var;
import sistema.Archivos;
import sistema.SQLite;

/**
 * Created by JULIANEDUARDO on 20/02/2015.
 */
public class UsuarioDAO implements global_var {
    private Context                     ctx;
    private SQLite                      fcnSQL;
    private ContentValues               _tempRegistro;
    private ContentValues               _tempRegistro1;

    private ArrayList<ContentValues>    _tempTabla;
    //private UsuarioLeido                usuario;

    private Archivos                    fcnArchivos;

    String _id_programacion;


    public UsuarioDAO(Context _ctx){
        ctx     = _ctx;
        fcnSQL  = new SQLite(ctx);
        fcnArchivos = new Archivos(ctx, PATH_FILES_APP, 10);
    }

    public UsuarioLeido getPrimerUsuario(int _ciclo, String _municipio, String _ruta){
        return this.getInfUsuario(_ciclo, _municipio, _ruta, 0, true, "P");
    }

    public UsuarioLeido getSiguienteUsuario(UsuarioLeido _usuario){

        UsuarioLeido newUsuario = this.getInfUsuario(_usuario.getCiclo(), _usuario.getMunicipio(), _usuario.getRuta(),
                _usuario.getSecuenciaRuta(), true, "P");

        if(newUsuario == null)
        {
            return _usuario;
        }else{
            return newUsuario;
        }
    }


    public UsuarioLeido getAnteriorUsuario(UsuarioLeido _usuario){

        UsuarioLeido newUsuario = this.getInfUsuario(_usuario.getCiclo(), _usuario.getMunicipio(), _usuario.getRuta(),
                _usuario.getSecuenciaRuta(), false, "P");

        if(newUsuario == null)
        {
            return _usuario;
        }else{
            return newUsuario;
        }
    }

    //Se cambia a public para acceder desde FormEntregarFactura
    //private UsuarioLeido getInfUsuario(int _ciclo, String _municipio, String _ruta, int _secuencia, boolean _ascendente, String _estado)
    public UsuarioLeido getInfUsuario(int _ciclo, String _municipio, String _ruta, int _secuencia, boolean _ascendente, String _estado)
    {
        ContentValues ruta = new ContentValues();
        ContentValues infUsuario = new ContentValues();
        UsuarioLeido usuario = null;

        ruta = fcnSQL.SelectDataRegistro("maestro_rutas", "id_programacion",
                "ciclo =" + _ciclo + " AND ruta = '" + _ruta + "' AND municipio='" + _municipio + "'");

        if(_ascendente)
        {
            infUsuario = fcnSQL.SelectDataRegistro("maestro_clientes",
                    "id_serial, id_programacion, id_secuencia, cuenta, nombre, direccion, marca_contador, serie_contador, secuencia_imp, secuencia_ruta, codigo_ruta, estado, latitud, longitud",
                    "id_programacion = " + ruta.getAsInteger("id_programacion") + " AND secuencia_ruta > " + _secuencia + " AND estado = '" +_estado + "' ORDER BY secuencia_ruta ASC");
        }else
        {
            infUsuario = fcnSQL.SelectDataRegistro("maestro_clientes",
                    "id_serial, id_programacion, id_secuencia, cuenta, nombre, direccion, marca_contador, serie_contador, secuencia_imp, secuencia_ruta, codigo_ruta, estado, latitud, longitud",
                    "id_programacion = " + ruta.getAsInteger("id_programacion") + " AND secuencia_ruta < " + _secuencia + " AND estado = '" +_estado + "' ORDER BY secuencia_ruta DESC");
        }


        if(infUsuario.size() > 0)
        {
            usuario = new UsuarioLeido(_ciclo, _municipio, _ruta, infUsuario.getAsInteger("id_serial"),
                    infUsuario.getAsInteger("id_programacion"), infUsuario.getAsInteger("id_secuencia"),
                    infUsuario.getAsString("cuenta"), infUsuario.getAsString("nombre"), infUsuario.getAsString("direccion"),
                    infUsuario.getAsString("marca_contador"), infUsuario.getAsString("serie_contador"), infUsuario.getAsInteger("secuencia_imp"),
                    infUsuario.getAsInteger("secuencia_ruta"), infUsuario.getAsString("estado"), infUsuario.getAsString("codigo_ruta"), infUsuario.getAsString("latitud"), infUsuario.getAsString("longitud"));
        }


        return usuario;
    }


    public boolean checkCodeBarras(String _codigoBarras, String _cuenta){
            return _codigoBarras.contains(_cuenta);
    }


    public int getNumeroFotos(String _cuenta, String _directorio){
        return fcnArchivos.numArchivosInFolderBeginByName( SUB_PATH_PICTURES + File.separator + _directorio, _cuenta, true);
    };


    public boolean guardarLectura(int _idProgramacion, String _cuenta, String _longitud, String _latitud,
                                  String _mensaje, int _idInspector, double _distancia){
        boolean _retorno = false;
        ContentValues tempRegistro = new ContentValues();

        tempRegistro.clear();
        tempRegistro.put("id_programacion",_idProgramacion);
        tempRegistro.put("cuenta", _cuenta);
        tempRegistro.put("longitud", _longitud);
        tempRegistro.put("latitud", _latitud);
        tempRegistro.put("mensaje", _mensaje);
        tempRegistro.put("id_inspector", _idInspector);
        tempRegistro.put("distancia", _distancia);

        _retorno =  fcnSQL.InsertRegistro("entrega_factura",tempRegistro);

        if(_retorno)
        {
            tempRegistro.clear();
            tempRegistro.put("estado", "T");

            fcnSQL.UpdateRegistro("maestro_clientes", tempRegistro,
                    "id_programacion = " + _idProgramacion + " AND cuenta = '" + _cuenta +"' AND estado = 'P'");
        }

        return _retorno;
    }

    public ArrayList<ContentValues> ListaClientes(String _ruta, boolean _filtro){

        _id_programacion = this.fcnSQL.StrSelectShieldWhere("maestro_rutas",
                "id_programacion",
                "ruta='"+_ruta+"'");

        if(_filtro){
            this._tempTabla = this.fcnSQL.SelectData(   "maestro_clientes",
                    "cuenta,serie_contador,nombre,direccion",
                    "id_programacion="+_id_programacion);
        }else{
            this._tempTabla = this.fcnSQL.SelectData(   "maestro_clientes",
                    "cuenta,serie_contador,nombre,direccion",
                    "id_serial IS NOT NULL");
        }
        return this._tempTabla;
    }


    public boolean getDatosUsuario(boolean _next){
        boolean _retorno  = false;
        /*if(this.ObjUsuario.getId_consecutivo() == -1){
            this._tempRegistro = this.FcnSQL.SelectDataRegistro("maestro_clientes",
                                                                "ruta, id_serial,id_secuencia, cuenta,marca_medidor,serie_medidor,nombre,direccion,tipo_uso,factor_multiplicacion,id_serial_1,lectura_anterior_1,tipo_energia_1,promedio_1,id_serial_2,lectura_anterior_2,tipo_energia_2,promedio_2,id_serial_3,lectura_anterior_3,tipo_energia_3,promedio_3,estado,id_municipio,anomalia_anterior_1,longitud,latitud,digitos",
                                                                "id_municipio = "+this.ObjUsuario.getId_municipio()+" AND ruta='"+this.ObjUsuario.getRuta()+"' AND estado='P' ORDER BY id_secuencia ASC");
        }else if(_next){
            this._tempRegistro = this.FcnSQL.SelectDataRegistro("maestro_clientes",
                                                                "ruta, id_serial,id_secuencia, cuenta,marca_medidor,serie_medidor,nombre,direccion,tipo_uso,factor_multiplicacion,id_serial_1,lectura_anterior_1,tipo_energia_1,promedio_1,id_serial_2,lectura_anterior_2,tipo_energia_2,promedio_2,id_serial_3,lectura_anterior_3,tipo_energia_3,promedio_3,estado,id_municipio,anomalia_anterior_1,longitud,latitud,digitos",
                                                                "id_municipio = "+this.ObjUsuario.getId_municipio()+" AND ruta='"+this.ObjUsuario.getRuta()+"' AND id_secuencia>"+this.ObjUsuario.getId_consecutivo()+" AND estado='P' ORDER BY id_secuencia ASC");
        }else{
            this._tempRegistro = this.FcnSQL.SelectDataRegistro("maestro_clientes",
                                                                "ruta, id_serial,id_secuencia, cuenta,marca_medidor,serie_medidor,nombre,direccion,tipo_uso,factor_multiplicacion,id_serial_1,lectura_anterior_1,tipo_energia_1,promedio_1,id_serial_2,lectura_anterior_2,tipo_energia_2,promedio_2,id_serial_3,lectura_anterior_3,tipo_energia_3,promedio_3,estado,id_municipio,anomalia_anterior_1,longitud,latitud,digitos",
                                                                "id_municipio = "+this.ObjUsuario.getId_municipio()+" AND ruta='"+this.ObjUsuario.getRuta()+"' AND id_secuencia<"+this.ObjUsuario.getId_consecutivo()+" AND estado='P' ORDER BY id_secuencia DESC");
        }

        if(this._tempRegistro.size()>0){
            _retorno = true;
            this.setInfUsuario();
        }*/
        return _retorno;
    }


    public UsuarioLeido getSearchDatosUsuario(String _cuenta, String _medidor){
        UsuarioLeido usuario = null;
        boolean _retorno    = false;
        this._tempRegistro  = this.fcnSQL.SelectDataRegistro("maestro_clientes",
                              "id_serial,id_programacion,id_secuencia,cuenta,nombre,direccion,marca_contador,serie_contador,secuencia_imp,secuencia_ruta,codigo_ruta,estado,latitud,longitud",
                              "cuenta="+_cuenta+" AND serie_contador ='"+_medidor+"' ORDER BY id_secuencia ASC");

        if(this._tempRegistro.size()>0){
            _retorno = true;
            //this.setInfUsuario();

            this._tempRegistro1 = this.fcnSQL.SelectDataRegistro("maestro_rutas",
                                                                "ciclo,municipio,ruta",
                                                                "id_programacion="+this._tempRegistro.getAsInteger("id_programacion"));

            usuario = new UsuarioLeido(this._tempRegistro1.getAsInteger("ciclo"), this._tempRegistro1.getAsString("municipio"), this._tempRegistro1.getAsString("ruta"), this._tempRegistro.getAsInteger("id_serial"),
                    this._tempRegistro.getAsInteger("id_programacion"), this._tempRegistro.getAsInteger("id_secuencia"),
                    this._tempRegistro.getAsString("cuenta"), this._tempRegistro.getAsString("nombre"), this._tempRegistro.getAsString("direccion"),
                    this._tempRegistro.getAsString("marca_contador"), this._tempRegistro.getAsString("serie_contador"), this._tempRegistro.getAsInteger("secuencia_imp"),
                    this._tempRegistro.getAsInteger("secuencia_ruta"), this._tempRegistro.getAsString("estado"), this._tempRegistro.getAsString("codigo_ruta"), this._tempRegistro.getAsString("latitud"), this._tempRegistro.getAsString("longitud"));
        }
        return usuario;
    }


    private void setInfUsuario(){
        /*this.ObjUsuario.setRuta(this._tempRegistro.getAsString("ruta"));
        this.ObjUsuario.setId_serial(this._tempRegistro.getAsInteger("id_serial"));
        this.ObjUsuario.setId_consecutivo(this._tempRegistro.getAsInteger("id_secuencia"));
        this.ObjUsuario.setCuenta(this._tempRegistro.getAsInteger("cuenta"));
        this.ObjUsuario.setMarca_medidor(this._tempRegistro.getAsString("marca_medidor"));
        this.ObjUsuario.setSerie_medidor(this._tempRegistro.getAsString("serie_medidor"));
        this.ObjUsuario.setNombre(this._tempRegistro.getAsString("nombre"));
        this.ObjUsuario.setDireccion(this._tempRegistro.getAsString("direccion"));
        this.ObjUsuario.setFactor_multiplicacion(this._tempRegistro.getAsInteger("factor_multiplicacion"));
        this.ObjUsuario.setTipo_uso(this._tempRegistro.getAsString("tipo_uso"));
        this.ObjUsuario.setMunicipio(this.FcnSQL.StrSelectShieldWhere("param_municipios", "municipio", "id_municipio=" + this._tempRegistro.getAsString("id_municipio")));

        this.ObjUsuario.setId_serial1(this._tempRegistro.getAsInteger("id_serial_1"));
        this.ObjUsuario.setLectura_anterior1(this._tempRegistro.getAsInteger("lectura_anterior_1"));
        this.ObjUsuario.setTipo_energia1(this._tempRegistro.getAsString("tipo_energia_1"));
        this.ObjUsuario.setPromedio1(this._tempRegistro.getAsInteger("promedio_1"));

        this.ObjUsuario.setId_serial2(this._tempRegistro.getAsInteger("id_serial_2"));
        this.ObjUsuario.setLectura_anterior2(this._tempRegistro.getAsInteger("lectura_anterior_2"));
        this.ObjUsuario.setTipo_energia2(this._tempRegistro.getAsString("tipo_energia_2"));
        this.ObjUsuario.setPromedio2(this._tempRegistro.getAsInteger("promedio_2"));

        this.ObjUsuario.setId_serial3(this._tempRegistro.getAsInteger("id_serial_3"));
        this.ObjUsuario.setLectura_anterior3(this._tempRegistro.getAsInteger("lectura_anterior_3"));
        this.ObjUsuario.setTipo_energia3(this._tempRegistro.getAsString("tipo_energia_3"));
        this.ObjUsuario.setPromedio3(this._tempRegistro.getAsInteger("promedio_3"));

        this.ObjUsuario.setLeido(!this._tempRegistro.getAsString("estado").equals("P"));
        this.ObjUsuario.setDoneCodeBar(!this._tempRegistro.getAsString("estado").equals("P"));
        this.ObjUsuario.setDoneCodeQR(!this._tempRegistro.getAsString("estado").equals("P"));


        this.ObjUsuario.setAnomalia_anterior(this._tempRegistro.getAsInteger("anomalia_anterior_1"));

        this.ObjUsuario.setLatitudCuenta(this._tempRegistro.getAsString("latitud"));
        this.ObjUsuario.setLongitudCuenta(this._tempRegistro.getAsString("longitud"));
        this.ObjUsuario.setDigitosMedidor(this._tempRegistro.getAsInteger("digitos"));

        this.getNumeroFotos();
        this.getNumIntentos();*/
    }


    /*public UsuarioLeido getInfUsuario() {
        return this.ObjUsuario;
    }*/


    /*public void getNumIntentos(){
        this.ObjUsuario.setIntentos(this.FcnSQL.CountRegistrosWhere("toma_lectura",
                                    "id_serial1="+this.ObjUsuario.getId_serial1()+" AND id_serial2="+this.ObjUsuario.getId_serial2()+" AND id_serial3="+this.ObjUsuario.getId_serial3()));
    }*/


    /*private void setEstado(String _estado){
        this._tempRegistro.clear();
        this._tempRegistro.put("estado",_estado);
        this.FcnSQL.UpdateRegistro( "maestro_clientes",
                                    this._tempRegistro,
                                    "id_serial="+this.ObjUsuario.getId_serial());
    }*/





    /*public boolean checkCodeQR(String _cuenta){
        this.getInfUsuario().setDoneCodeQR(_cuenta.contains(this.getInfUsuario().getCuenta()+""));
        return this.getInfUsuario().isDoneCodeQR();
    }*/
}

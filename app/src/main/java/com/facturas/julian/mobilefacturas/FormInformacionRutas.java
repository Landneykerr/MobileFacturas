package com.facturas.julian.mobilefacturas;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.io.File;
import java.util.ArrayList;

import Adapter.RutasAdapter;
import Adapter.RutasData;
import async_task.UpLoadFoto;
import async_task.UploadFacturas;
import clases.ClassConfiguracion;
import clases.ClassSession;
import dialogos.ShowDialog;
import global.global_var;
import object.UsuarioDAO;
import object.UsuarioLeido;
import sistema.Archivos;
import sistema.Bluetooth;
import sistema.GPS;
import sistema.SQLite;


/**
 * Created by SypelcDesarrollo on 04/02/2015.
 */
public class FormInformacionRutas extends Activity implements global_var{
    private String  FolderAplicacion;
    private String  ruta_seleccionada;
    private int     pend_ruta_seleccionada;


    private static int    	CONFIRMACION_RUTA = 1;

    private Intent              dialogConfirmacion;
    private Intent              new_form;
    private ListView            listadoRutas;
    private SQLite              sqlConsulta;
    private ClassSession        FcnSession;
    private ClassConfiguracion  FcnCfg;
    private Bluetooth           FcnBluetooth;
    private Archivos            FcnArchivos;
    private UsuarioDAO          FcnUsuario;             //Add
    private UsuarioLeido        usuario;                       //Add
    private GPS                 pointGPS;                          //Add
    private RutasAdapter        listadoRutasAdapter;

    private ArrayList<RutasData> arrayListadoRutas      = new ArrayList<>();
    private ArrayList<ContentValues> _tempTabla         = new ArrayList<ContentValues>();
    private ArrayList<ContentValues> _terminarRutaTabla = new ArrayList<>();        //Add
    private ContentValues _tempRegistro 		        = new ContentValues();
    private ContentValues _terminarRutaRegistro 	    = new ContentValues();      //Add
    private String _consultaIdProgramacion = new String();                               //Add

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informacion_rutas);


        Bundle bundle = getIntent().getExtras();

        this.FcnSession     = ClassSession.getInstance(this);
        this.FcnCfg         = ClassConfiguracion.getInstance(this);
        this.FcnBluetooth   = Bluetooth.getInstance();
        this.FcnArchivos    = new Archivos(this,PATH_FILES_APP,10);
        this.FcnUsuario     = new UsuarioDAO(this);                     //Add
        this.pointGPS       = GPS.getInstance(this);

        //usuario     = FcnUsuario.getPrimerUsuario();     //Add

        sqlConsulta = new SQLite(this);
        listadoRutasAdapter = new RutasAdapter(FormInformacionRutas.this, arrayListadoRutas);

        listadoRutas = (ListView)findViewById(R.id.InfoListRutas);
        listadoRutas.setAdapter(listadoRutasAdapter);

        arrayListadoRutas.clear();

        this._tempTabla = sqlConsulta.SelectData("maestro_rutas",
                "ciclo, municipio, ruta, id_programacion",
                "id_inspector="+this.FcnSession.getCodigo());

        for(int i=0;i<this._tempTabla.size();i++){
            this._tempRegistro = this._tempTabla.get(i);

            Integer totalR = sqlConsulta.CountRegistrosWhere("maestro_clientes",
                    "id_programacion="+_tempRegistro.getAsInteger("id_programacion"));

            Integer totalP = sqlConsulta.CountRegistrosWhere("maestro_clientes",
                    "id_programacion="+_tempRegistro.getAsInteger("id_programacion") + " AND estado = 'P'");

            Integer totalL = totalR - totalP;

            arrayListadoRutas.add(new RutasData(this._tempRegistro.getAsString("ciclo")+"-"+this._tempRegistro.getAsString("municipio")+"-"+this._tempRegistro.getAsString("ruta"),
                                                String.valueOf(totalP),
                                                String.valueOf(totalL),
                                                String.valueOf(totalR)));
        }
        listadoRutasAdapter.notifyDataSetChanged();
        registerForContextMenu(this.listadoRutas);
    }

    /**Funciones para el control del menu contextual del listview que muestra las ordenes de trabajo**/
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;
        this.ruta_seleccionada      = arrayListadoRutas.get(info.position).getCodigoRuta();
        this.pend_ruta_seleccionada = Integer.parseInt(arrayListadoRutas.get(info.position).getTotalPendientes());
        switch(v.getId()){
            case R.id.InfoListRutas:
                menu.setHeaderTitle("Ruta " + this.ruta_seleccionada);
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_lista_rutas, menu);
                break;
        }
    }



    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.RutasMenuIniciar:
                if (this.pend_ruta_seleccionada == 0) {
                    String DatosArray[] = this.ruta_seleccionada.split("-");
                    int cicloRuta = Integer.parseInt(DatosArray[0]);

                    this._consultaIdProgramacion = sqlConsulta.StrSelectShieldWhere("maestro_rutas",
                            "id_programacion",
                            "ciclo="+cicloRuta+" AND municipio='"+DatosArray[1]+"' AND ruta='"+DatosArray[2]+"'");

                    new ShowDialog().showLoginDialog(this, Integer.parseInt(this._consultaIdProgramacion));
                }else{
                    this.new_form = new Intent(this, FormEntregarFactura.class);
                    this.new_form.putExtra("Ruta", this.ruta_seleccionada);
                    startActivity(this.new_form);
                }

                return true;

            case R.id.RutasMenuSincronizar:
                new UploadFacturas(this).execute(this.ruta_seleccionada);
                return true;

            case R.id.RutasMenuSincronizarFotos:
                /*File f = new File(PATH_FILES_APP+File.separator+SUB_PATH_PICTURES);
                File[] fotos = f.listFiles();
                for (int i=0;i<fotos.length;i++){
                    if(!fotos[i].isDirectory()){
                        String extension = getFileExtension(fotos[i]);
                        if(extension.equals("jpeg")){
                            String[] _foto = fotos[i].getName().split("_");
                            String id_serial = this.sqlConsulta.StrSelectShieldWhere("maestro_clientes", "id_serial_1", "cuenta=" + _foto[0]);
                            new UpLoadFoto(this).execute(_foto[0],id_serial,fotos[i].toString(),fotos[i].getName());
                        }
                    }
                }*/

                    new UpLoadFoto(this).execute();
                    //Toast.makeText(this,"Sincronizando fotos", Toast.LENGTH_LONG).show();

                return true;

            case R.id.RutasMenuTerminarRuta:

                dialogConfirmacion = new Intent(this,DialogoConfirmacion.class);
                dialogConfirmacion.putExtra("informacion","   Desea terminar ruta?");
                startActivityForResult(dialogConfirmacion, CONFIRMACION_RUTA);
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

        if(resultCode == RESULT_OK && requestCode == CONFIRMACION_RUTA){

            if(data.getExtras().getBoolean("accion")){
                String DatosArray[] = this.ruta_seleccionada.split("-");
                int cicloRuta = Integer.parseInt(DatosArray[0]);            //[0]=ciclo  [1]=municipio   [2]=ruta

                this._consultaIdProgramacion = sqlConsulta.StrSelectShieldWhere("maestro_rutas",
                        "id_programacion",
                        "ciclo="+cicloRuta+" AND municipio='"+DatosArray[1]+"' AND ruta='"+DatosArray[2]+"'");

                this._terminarRutaTabla = sqlConsulta.SelectData("maestro_clientes",
                        "id_serial, id_programacion, cuenta, nombre, codigo_ruta",
                        "estado='P' AND id_programacion="+Integer.parseInt(this._consultaIdProgramacion));

                for(int i=0;i<this._terminarRutaTabla.size();i++){
                    this._terminarRutaRegistro = this._terminarRutaTabla.get(i);

                    FcnUsuario.guardarLectura(_terminarRutaRegistro.getAsInteger("id_programacion"), _terminarRutaRegistro.getAsString("cuenta"),
                            pointGPS.getLongitudGPS(), pointGPS.getLatitudGPS(), "Terminada",
                            Integer.parseInt(this.FcnSession.getCodigo()), 0.0);
                }

            }else{}
        }
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
}

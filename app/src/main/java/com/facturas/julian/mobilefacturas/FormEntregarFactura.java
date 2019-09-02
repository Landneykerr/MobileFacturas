package com.facturas.julian.mobilefacturas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import clases.ClassAnomalias;
import clases.ClassConfiguracion;
import clases.ClassSession;
import clases.ManagerImageResize;
import dialogos.CustomToastInfo;
import dialogos.ShowDialog;
import dialogos.ShowDialogBox;
import global.global_var;
import gps.CustomListenerGPS;
import gps.PointGPS;
import object.UsuarioDAO;
import object.UsuarioLeido;
import sistema.DateTime;
import sistema.GPS;
import sistema.GeoConvert;


public class FormEntregarFactura extends ActionBarActivity implements OnClickListener, OnItemSelectedListener, global_var, Observer {
    static int 				    INICIAR_CAMARA			= 1;
    static int                  FROM_BUSCAR             = 2;
    static int                  FINAL_RUTA              = 3;
    static int                  UBICACION_TERRENO       = 4;
    static int                  GET_CODE_BARRAS         = 5;

    DecimalFormat df = new DecimalFormat("#.##");

    private Intent 			    IniciarCamara;
    private Intent              new_form;
    private ManagerImageResize  fcnImage;

    private CustomToastInfo     toastInfo;
    private UsuarioDAO          fcnUsuario;
    private UsuarioLeido        usuario;
    private ClassAnomalias      clsAnomalias;

    private ClassSession        FcnSession;
    private ClassConfiguracion  fcnCfg;

    private DateTime            FcnTime;

    private TextView    _lblCuenta, _lblNombre, _lblDireccion, _lblRuta, _lblMedidor, _lblImp;
    private TextView    _lblDistancia;
    private EditText    _txtMensaje;
    private Button      _btnCodigoBar, _btnSiguiente, _btnAnterior, _btnGuardar;
    private Spinner     _spinMensaje;

    private LocationManager     managerLocation;
    private CustomListenerGPS   listenerGPS;
    private PointGPS            pointGPS;
    private GPS                 FcnGPS;

    private ArrayAdapter<String> listadoMsjCodificados;
    private ArrayList<String>    arrayMensajes = new ArrayList<>();

    public String                directorioCiclo = "";
    int distanciaMin;
    int usuarioOK = 0;
    int siguienteUsuario=0;
    int getNumFotos=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tomar_lectura);

        Bundle bundle   = getIntent().getExtras();
        String ruta[]   = bundle.getString("Ruta").split("\\-");

        df.setRoundingMode(RoundingMode.CEILING);

        fcnUsuario  = new UsuarioDAO(this);
        usuario     = fcnUsuario.getPrimerUsuario(Integer.parseInt(ruta[0]), ruta[1], ruta[2]);

        this.IniciarCamara	= new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        fcnImage           = ManagerImageResize.getInstance();
        fcnCfg             = ClassConfiguracion.getInstance(this);

        this.FcnGPS             = GPS.getInstance(this);
        this.FcnSession         = ClassSession.getInstance(this);
        this.FcnTime            = DateTime.getInstance();
        this.clsAnomalias       = ClassAnomalias.getInstance(this);

        this.listenerGPS    = CustomListenerGPS.getInstance(this);

        this.toastInfo      = new CustomToastInfo(this,Toast.LENGTH_LONG);
        this._lblCuenta     = (TextView) findViewById(R.id.LecturaTxtCuenta);
        this._lblNombre     = (TextView) findViewById(R.id.LecturaTxtNombre);
        this._lblDireccion  = (TextView) findViewById(R.id.LecturaTxtDireccion);
        this._lblRuta       = (TextView) findViewById(R.id.LecturaTxtRuta);
        this._lblMedidor    = (TextView) findViewById(R.id.LecturaTxtMedidor);
        this._lblImp        = (TextView) findViewById(R.id.LecturaTxtImp);
        this._lblDistancia  = (TextView) findViewById(R.id.LecturaTxtDistancia);

        this._txtMensaje    = (EditText) findViewById(R.id.LecturaEditMensaje);

        this._btnCodigoBar  = (Button) findViewById(R.id.LecturasBtnCodeBar);
        this._btnGuardar     = (Button) findViewById(R.id.LecturasBtnGuardar);
        this._btnSiguiente  = (Button) findViewById(R.id.LecturaBtnSiguiente);
        this._btnAnterior   = (Button) findViewById(R.id.LecturaBtnAnterior);
        this._spinMensaje   = (Spinner) findViewById(R.id.spinMensaje);

        this.distanciaMin  = this.clsAnomalias.getDistancia();
        this.arrayMensajes.clear();
        this.arrayMensajes = this.clsAnomalias.getMensaje();
        this.listadoMsjCodificados  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayMensajes);
        this._spinMensaje.setAdapter(listadoMsjCodificados);

        this._btnCodigoBar.setOnClickListener(this);
        this._btnGuardar.setOnClickListener(this);
        this._btnAnterior.setOnClickListener(this);
        this._btnSiguiente.setOnClickListener(this);
        this._spinMensaje.setOnItemSelectedListener(this);
    }

    @Override
    protected void onResume(){
        super.onResume();

        /**         * Listener para detectar cambios en el gps, tanto en el funcionamiento como en la localizacion         */
        try {
            pointGPS = PointGPS.getInstance();
            this.managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            this.managerLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    CustomListenerGPS.GPS_INTERVAL_SECONDS, CustomListenerGPS.GPS_INTERVAL_METERS, listenerGPS);

            pointGPS.addObserver(this);
        }catch(SecurityException se){

        }catch(Exception ex){
            Log.i("Error GPS", ex.toString());
        }
        _btnCodigoBar.setEnabled(pointGPS.isEstadoGPS());
        _btnGuardar.setEnabled(pointGPS.isEstadoGPS());
        this.mostrarInformacionBasica();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_tomar_lectura, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.LecturaMenuBuscar:
                this.new_form = new Intent(this, FormBuscar.class);
                this.new_form.putExtra("Ruta", this.usuario.getRuta());
                startActivityForResult(this.new_form, FROM_BUSCAR);
                break;

            case R.id.LecturaMenuFoto:
                if(pointGPS.isEstadoGPS() && usuario.getDistancia()!=0.0) {
                    if (usuario.getDistancia() > this.distanciaMin && !this._txtMensaje.getText().toString().equals("")) {
                        this.getFoto();
                    } else if (usuario.getDistancia() > this.distanciaMin && this._txtMensaje.getText().toString().equals("")) {
                        Toast.makeText(this, "Ingrese mensaje", Toast.LENGTH_LONG).show();
                    }
                    if (usuario.getDistancia() <= this.distanciaMin) {
                        this.getFoto();
                    }
                }
                if(usuario.getDistancia()==0.0){
                    Toast.makeText(this, "Reinicie GPS", Toast.LENGTH_LONG).show();
                }
                break;

            case R.id.LecturaMenuRendimiento:
                new ShowDialog().showLoginDialog(this, usuario.getIdProgramacion());
                break;

            case R.id.LecturaMenuUbicacion:
                this.new_form =new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:<"+usuario.getLatitudCuenta()+">,<"+usuario.getLongitudCuenta()+">?q=<"+usuario.getLatitudCuenta()+">,<"+usuario.getLongitudCuenta()+">(Cuenta "+usuario.getCuenta()+")"));

                startActivityForResult(this.new_form, UBICACION_TERRENO);
                break;
        }
        return super.onOptionsItemSelected(item);
    }



    private void mostrarInformacionBasica(){
        this._lblRuta.setText(usuario.getCodigoRuta());
        this._lblImp.setText(usuario.getSecuenciaImp()+"");     //getSecuenciaImp->int necesario concatenar "" para mostrar.

        this._lblCuenta.setText(usuario.getCuenta());
        this._lblMedidor.setText(usuario.getMarcaContador() + " " + usuario.getSerieContador());
        this._lblNombre.setText(usuario.getNombre());
        this._lblDireccion.setText(usuario.getDireccion());

        if(pointGPS.isEstadoGPS()) {
            this._btnCodigoBar.setEnabled(!usuario.isLeido());
            this._txtMensaje.setEnabled(!usuario.isLeido());
            this._spinMensaje.setEnabled(!usuario.isLeido());
        }else{
            this._btnCodigoBar.setEnabled(false);
            this._txtMensaje.setEnabled(false);
            this._spinMensaje.setEnabled(false);
            this._lblDistancia.setText("");
        }

        if(usuario.isDoneCodeBar()){
            this._btnCodigoBar.setBackgroundColor(getResources().getColor(R.color.btn_leido));
            this._btnCodigoBar.setTextColor(getResources().getColor(R.color.lbl_leido));
            this._btnCodigoBar.setEnabled(false);
        }else{
            //this._btnCodigoBar.setBackgroundColor(getResources().getColor(R.color.btn_sin_leer));
            //this._btnCodigoBar.setTextColor(getResources().getColor(R.color.lbl_sin_leer));
        }
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LecturaBtnAnterior:
                siguienteUsuario=0;
                usuario = fcnUsuario.getAnteriorUsuario(usuario);
                mostrarInformacionBasica();
                validarUsuarioLeido();
                this._txtMensaje.setText("");
                if(!pointGPS.isEstadoGPS()) {
                    this._btnCodigoBar.setEnabled(false);
                    this._txtMensaje.setEnabled(false);
                    this._spinMensaje.setEnabled(false);
                    this._lblDistancia.setText("");
                }
                break;

            case R.id.LecturaBtnSiguiente:
                usuario = fcnUsuario.getSiguienteUsuario(usuario);
                mostrarInformacionBasica();
                validarUsuarioLeido();
                this._txtMensaje.setText("");
                siguienteUsuario=0;
                if(!pointGPS.isEstadoGPS()) {
                    this._btnCodigoBar.setEnabled(false);
                    this._txtMensaje.setEnabled(false);
                    this._spinMensaje.setEnabled(false);
                    this._lblDistancia.setText("");
                }
                break;

            case R.id.LecturasBtnCodeBar:
                Intent intent1 = new Intent(FormEntregarFactura.this,ValidarCuenta.class);
                startActivityForResult(intent1, GET_CODE_BARRAS);
                break;

            case R.id.LecturasBtnGuardar:
                siguienteUsuario=1;
                if(usuario.getEstado().equals("E") || usuario.getEstado().equals("T") || usuario.isLeido()){
                    _txtMensaje.setText("");
                    usuario = fcnUsuario.getSiguienteUsuario(usuario);
                    mostrarInformacionBasica();
                    validarUsuarioLeido();
                    toastInfo.show("Registro Enviado Correctamente");
                }
                usuarioOK=0;

                break;


            default:
                break;
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        try{
            if(resultCode == RESULT_OK && requestCode == FROM_BUSCAR){
                if(data.getExtras().getBoolean("response")){
                    usuario = this.fcnUsuario.getSearchDatosUsuario(data.getExtras().getString("cuenta"),data.getExtras().getString("medidor"));
                    this.mostrarInformacionBasica();
                    validarUsuarioLeido();
                }
            }else if (resultCode == RESULT_OK && requestCode == GET_CODE_BARRAS){
                if(data.getExtras().getBoolean("response")){
                    usuario.setDoneCodeBar(fcnUsuario.checkCodeBarras(data.getExtras().getString("cuenta"), usuario.getCuenta()));
                }

                if(usuario.isDoneCodeBar())
                {
                    this._btnCodigoBar.setBackgroundColor(getResources().getColor(R.color.btn_leido));
                    this._btnCodigoBar.setEnabled(false);
                    this.getFoto();
                    usuarioOK = 1;
                }else{
                    new ShowDialogBox().showDialogBox(this, ShowDialogBox.DIALOG_ERROR,"CODIGO BARRAS",
                            "El codigo de barras leido no coincide con la cuenta "+usuario.getCuenta()+".");
                }

            }else if(resultCode == RESULT_OK && requestCode == INICIAR_CAMARA){
                fcnImage.resizeImage(directorioCiclo+File.separator+usuario.getCuenta()+"_"+getNumFotos+".JPEG",_WIDTH_PICTURE, _HEIGHT_PICTURE);
                usuario.setCountFotos(fcnUsuario.getNumeroFotos(usuario.getCuenta(), directorioCiclo));

                if(fcnUsuario.guardarLectura(usuario.getIdProgramacion(), usuario.getCuenta(),
                        pointGPS.getLongitudGPS(), pointGPS.getLatitudGPS(), _txtMensaje.getText().toString(),
                        Integer.parseInt(this.FcnSession.getCodigo()), usuario.getDistancia())
                        && usuario.getCountFotos() > 0){

                    usuario.setLeido(true);
                    usuarioOK=1;
                    if(siguienteUsuario==1) {
                        usuario = fcnUsuario.getSiguienteUsuario(usuario);
                        toastInfo.show("Registro Enviado Correctamente");
                    }
                    this.mostrarInformacionBasica();
                    validarUsuarioLeido();
                }else{
                    new ShowDialogBox().showDialogBox(this, ShowDialogBox.DIALOG_ERROR,"GUARDAR DATOS",
                             "Error al registrar la informacion relacionada con la cuenta "+ usuario.getCuenta()+".");
                }
            }else if(resultCode == RESULT_OK && requestCode == FINAL_RUTA){
                this.finish();
            }else if (resultCode == RESULT_CANCELED) {
                // Handle cancel
            }
        }catch(Exception e){
            Log.i("Error", e.toString());

        }
        siguienteUsuario=0;
    }

    private void getFoto(){
        directorioCiclo     = usuario.getCiclo()+"";
        getNumFotos         = fcnUsuario.getNumeroFotos(usuario.getCuenta(), directorioCiclo)+1;
        File imagesFolder   = new File(PATH_FILES_APP, SUB_PATH_PICTURES+File.separator+directorioCiclo);
        File image          = new File( imagesFolder, usuario.getCuenta()+"_"+getNumFotos+".JPEG");
        Uri uriSavedImage   = Uri.fromFile(image);
        this.IniciarCamara.putExtra(MediaStore.EXTRA_OUTPUT, uriSavedImage);
        startActivityForResult(IniciarCamara, INICIAR_CAMARA);
        siguienteUsuario=0;
    }

    private void validarUsuarioLeido(){
        usuario.getEstado();
        if(usuario.getEstado().equals("E") || usuario.getEstado().equals("T") || usuario.isLeido()){
            this._btnCodigoBar.setBackgroundColor(getResources().getColor(R.color.btn_leido));
            this._btnGuardar.setBackgroundColor(getResources().getColor(R.color.btn_guardar));
            this._btnCodigoBar.setTextColor(getResources().getColor(R.color.lbl_leido));
            this._btnGuardar.setTextColor(getResources().getColor(R.color.lbl_leido));
            this._btnCodigoBar.setEnabled(false);
            this._txtMensaje.setEnabled(false);
            this._btnGuardar.setEnabled(true);
        }
        else{
            this._btnCodigoBar.setBackgroundColor(getResources().getColor(R.color.btn_sin_leer));
            this._btnGuardar.setBackgroundColor(getResources().getColor(R.color.btn_guardar));
            this._btnCodigoBar.setTextColor(getResources().getColor(R.color.lbl_sin_leer));
            this._btnGuardar.setTextColor(getResources().getColor(R.color.lbl_sin_leer));
            this._btnCodigoBar.setEnabled(true);
            this._btnGuardar.setEnabled(false);
            this._txtMensaje.setEnabled(true);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        if (!pointGPS.isEstadoGPS()){
            if(!((Activity) this).isFinishing()){
                new ShowDialogBox().showDialogBox(this, ShowDialogBox.DIALOG_ERROR, "ERROR RED Y/O GPS",
                        "Verifique su conexion a internet y GPS.");
            }
            _btnCodigoBar.setEnabled(false);
        }else{
            _btnCodigoBar.setEnabled(true);
            _btnGuardar.setEnabled(true);

            //String distancia = df.format(GeoConvert.distance2Points(4.134827508883343,-73.62806754090668, Double.parseDouble(FcnGPS.getLatitudGPS()), Double.parseDouble(FcnGPS.getLongitudGPS())));
            //String distancia = df.format(GeoConvert.distance2Points(4.1342766,-73.6281208, Double.parseDouble(FcnGPS.getLatitudGPS()), Double.parseDouble(FcnGPS.getLongitudGPS())));
            String distancia = df.format(GeoConvert.distance2Points(Double.parseDouble(usuario.getLatitudCuenta()),Double.parseDouble(usuario.getLongitudCuenta()), Double.parseDouble(FcnGPS.getLatitudGPS()), Double.parseDouble(FcnGPS.getLongitudGPS())));

            usuario.setDistancia(Double.parseDouble(distancia));

            this._lblDistancia.setText("Distancia: "+distancia+" mts");
            /*usuario.setDistancia(GeoConvert.distance2Points(Double.parseDouble(usuario.getLatitudCuenta()), Double.parseDouble(usuario.getLongitudCuenta()),
                    Double.parseDouble(pointGPS.getLatitudGPS()), Double.parseDouble(pointGPS.getLongitudGPS())));*/

            /*if(fcnCfg.isLimiteDistancia()){
                if(!usuario.isCerca()){
                    _btnCodigoBar.setEnabled(false);
                    //_btnGuardar.setEnabled(false);
                }else{
                    _btnCodigoBar.setEnabled(true);
                    _btnGuardar.setEnabled(true);
                }
            }*/

            if(fcnCfg.isLimiteDistancia()){
                if(usuario.getDistancia()>this.distanciaMin){       //!usuario.isCerca()
                    _btnCodigoBar.setEnabled(false);
                    //_btnGuardar.setEnabled(false);
                }else{
                    _btnCodigoBar.setEnabled(true);
                    _btnGuardar.setEnabled(true);
                }
            }

            /*if(usuario.isCerca()){
                this._lblDistancia.setTextColor(this.getResources().getColor(R.color.verde));
            }*/
            if(usuario.getDistancia()<=this.distanciaMin){  //usuario.isCerca()
                this._lblDistancia.setTextColor(this.getResources().getColor(R.color.verde));
            }else{
                this._lblDistancia.setTextColor(this.getResources().getColor(R.color.rojo));
            }
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch(adapterView.getId()) {
            case R.id.spinMensaje:
                if (!_spinMensaje.getSelectedItem().toString().equals("...")) {
                    this._txtMensaje.append("(" + this._spinMensaje.getSelectedItem().toString() + ")");
                }
                break;

            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}

package com.facturas.julian.mobilefacturas;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import async_task.DownLoadParametros;
import async_task.DownLoadTrabajo;
import async_task.FlujoInformacion;
import beacon.TimerCountDown;
import clases.ClassConfiguracion;
import clases.ClassSession;
import dialogos.ShowDialogBox;
import sistema.Bluetooth;
import sistema.DateTime;
import sistema.GPS;


public class Loggin extends AppCompatActivity implements OnClickListener {
    private Intent new_form;
    TimerCountDown envioActas;


    private GPS FcnGPS;
    private ClassSession FcnSession;
    private ClassConfiguracion FcnCfg;
    private FlujoInformacion FcnInf;
    private DateTime FcnTime;

    private LocationManager managerLocation;

    private Button _btnLoggin;
    private EditText _txtCodigo;
    private TextView _lblNombre, _lblVersionSoft, _lblVersionBD, _lblMacBluetooth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loggin);

        this.FcnSession = ClassSession.getInstance(this);
        this.FcnCfg = ClassConfiguracion.getInstance(this);
        this.FcnInf = new FlujoInformacion(this);
        this.FcnTime = DateTime.getInstance();
        this.FcnGPS = GPS.getInstance(this);

        this._btnLoggin = (Button) findViewById(R.id.LoginBtnIngresar);
        this._txtCodigo = (EditText) findViewById(R.id.LoginEditTextCodigo);
        this._lblNombre = (TextView) findViewById(R.id.LoginTxtNombre);
        this._lblVersionBD = (TextView) findViewById(R.id.LoginTxtVersionBD);
        this._lblVersionSoft = (TextView) findViewById(R.id.LoginTxtVersionSoft);
        this._lblMacBluetooth = (TextView) findViewById(R.id.LoginTxtBluetooth);

        this._lblVersionBD.setText("Version BD " + this.FcnCfg.getVersion_bd());
        this._lblVersionSoft.setText("Version Software " + this.FcnCfg.getVersion_software());
        this._lblMacBluetooth.setText("MAC Bluetooth " + Bluetooth.getInstance().GetOurDeviceByAddress());


        envioActas = new TimerCountDown(this, TimerCountDown.BEACON_TIME_FINISH,
                TimerCountDown.BEACON_TIME);
        envioActas.start();

        invalidateOptionsMenu();
        this._btnLoggin.setOnClickListener(this);

        this.managerLocation = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        this.managerLocation.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.FcnGPS);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_loggin, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.InicioCargarParametros).setEnabled(this.FcnSession.isInicio_sesion());
        menu.findItem(R.id.InicioCargarRuta).setEnabled(this.FcnSession.isInicio_sesion());
        menu.findItem(R.id.InicioVerRutas).setEnabled(this.FcnSession.isInicio_sesion());
        menu.findItem(R.id.InicioConfiguracion).setEnabled(this.FcnSession.isInicio_sesion());

        this._txtCodigo.setEnabled(!this.FcnSession.isInicio_sesion());
        this._btnLoggin.setEnabled(!this.FcnSession.isInicio_sesion());
        this._lblNombre.setText(this.FcnSession.getNombre());

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.InicioCargarParametros:
                new DownLoadParametros(this).execute(this.FcnSession.getCodigo()+"");
                break;

            case R.id.InicioCargarRuta:
                new DownLoadTrabajo(this).execute(this.FcnSession.getCodigo()+"", this.FcnInf.getTrabajoCargado(), this.FcnTime.getDateUTC(), this.FcnTime.getHourUTC());
                break;

            case R.id.InicioVerRutas:
                this.new_form = new Intent(this, FormInformacionRutas.class);
                //this.new_form.putExtra("FolderAplicacion",Environment.getExternalStorageDirectory() + File.separator + "TomaLecturas");
                startActivity(this.new_form);
                break;

            case R.id.InicioConfiguracion:
                this.new_form = new Intent(this, FormConfiguracion.class);
                startActivity(this.new_form);
                break;

            case R.id.InicioMenuSalir:
                this.FcnSession.IniciarSession(null);
                invalidateOptionsMenu();
                finish();
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.LoginBtnIngresar:

                Toast.makeText(this, "Longitud "+FcnGPS.getLongitudGPS()+" - Latitud "+FcnGPS.getLatitudGPS(), Toast.LENGTH_LONG).show();        //Add

                if(!this._txtCodigo.getText().toString().isEmpty()){
                    if(!this.FcnSession.IniciarSession(this._txtCodigo.getText().toString())){
                        new ShowDialogBox().showDialogBox(this, ShowDialogBox.DIALOG_ERROR,
                                "INICIO DE SESION", "Codigo incorrecto.");
                    }
                }
                invalidateOptionsMenu();
                break;
        }
    }
}

package com.facturas.julian.mobilefacturas;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;

import clases.ClassConfiguracion;
import clases.ClassSession;
import sistema.Bluetooth;


public class FormConfiguracion extends ActionBarActivity implements OnClickListener {
    private ClassConfiguracion  FcnCfg;
    private ClassSession FcnUsuario;
    private Bluetooth           FcnBluetooth;

    private ArrayAdapter<String> AdapLstImpresoras;
    private ArrayList<String>   _listaImpresoras = new ArrayList<String>();

    private EditText            _txtServidor, _txtPuerto, _txtModulo, _txtWebService;
    private CheckBox            _chkLimiteDistancia;
    private Button              _btnGuardar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracion);


        this.FcnCfg         = ClassConfiguracion.getInstance(this);
        this.FcnUsuario     = ClassSession.getInstance(this);
        this.FcnBluetooth   = Bluetooth.getInstance();

        this._txtServidor   = (EditText) findViewById(R.id.ConfiguracionTxtServidor);
        this._txtPuerto     = (EditText) findViewById(R.id.ConfiguracionTxtPuerto);
        this._txtModulo     = (EditText) findViewById(R.id.ConfiguracionTxtModulo);
        this._txtWebService = (EditText) findViewById(R.id.ConfiguracionTxtWebService);
        this._btnGuardar    = (Button) findViewById(R.id.ConfiguracionBtnGuardar);

        this._chkLimiteDistancia    = (CheckBox) findViewById(R.id.ConfiguracionChkFotosOnLine);

        this._listaImpresoras   = this.FcnBluetooth.GetDeviceBluetoothByAddress();
        //this.AdapLstImpresoras  = new SpinnerAdapter(this, R.layout.custom_spinner, this._listaImpresoras,"#FF5CBD79","#6B5656");
        this.AdapLstImpresoras  = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,this._listaImpresoras);


        this._txtServidor.setText(this.FcnCfg.getIp_server());
        this._txtPuerto.setText(this.FcnCfg.getPort());
        this._txtModulo.setText(this.FcnCfg.getModule_web_service());
        this._txtWebService.setText(this.FcnCfg.getWeb_service());
        this._chkLimiteDistancia.setChecked(this.FcnCfg.isLimiteDistancia());

        if(this.FcnUsuario.getNivel()==0){
            this._txtServidor.setEnabled(true);
            this._txtPuerto.setEnabled(true);
            this._txtModulo.setEnabled(true);
            this._txtWebService.setEnabled(true);
            this._chkLimiteDistancia.setEnabled(true);
        }else{
            this._txtServidor.setEnabled(false);
            this._txtPuerto.setEnabled(false);
            this._txtModulo.setEnabled(false);
            this._txtWebService.setEnabled(false);
            this._chkLimiteDistancia.setEnabled(false);
        }

        this._btnGuardar.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.ConfiguracionBtnGuardar:
                this.FcnCfg.setIp_server(this._txtServidor.getText().toString());
                this.FcnCfg.setPort(this._txtPuerto.getText().toString());
                this.FcnCfg.setModule_web_service(this._txtModulo.getText().toString());
                this.FcnCfg.setWeb_service(this._txtWebService.getText().toString());
                this.FcnCfg.setLimiteDistancia(this._chkLimiteDistancia.isChecked());
                break;
        }
    }
}

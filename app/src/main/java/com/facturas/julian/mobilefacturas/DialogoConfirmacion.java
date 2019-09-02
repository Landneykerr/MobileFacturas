package com.facturas.julian.mobilefacturas;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DialogoConfirmacion extends AppCompatActivity implements View.OnClickListener {
    TextView    _lblMensaje;
    Button      _btnAceptar, _btnCancelar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialogo_confirmacion);

        Bundle bundle = getIntent().getExtras();

        _lblMensaje = (TextView) findViewById(R.id.dc_Lbl_Mensaje);
        _btnAceptar = (Button)  findViewById(R.id.dc_Btn_Aceptar);
        _btnCancelar = (Button)  findViewById(R.id.dc_Btn_Cancelar);

        _lblMensaje.setText(bundle.getString("informacion"));

        _btnCancelar.setOnClickListener(this);
        _btnAceptar.setOnClickListener(this);
    }
    public void finish(boolean _caso) {
        Intent data = new Intent();
        data.putExtra("accion", _caso);
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.dc_Btn_Aceptar:
                finish(true);
                break;

            case R.id.dc_Btn_Cancelar:
                finish(false);
                break;

            default:
                finish(false);
                break;
        }
    }
}

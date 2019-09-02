package com.facturas.julian.mobilefacturas;

import android.content.ContentValues;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import java.util.ArrayList;

import Adapter.AdaptadorFourItems;
import Adapter.DetalleFourItems;
import object.UsuarioDAO;


public class FormBuscar extends ActionBarActivity implements TextWatcher, OnItemSelectedListener {
    private EditText                    _txtBuscar;
    private Spinner                     _cmbFiltro;
    private ListView                    _lstClientes;
    private ArrayList<String>           ArrayFiltro;
    private ArrayAdapter<String>        AdaptadorFiltro;

    private AdaptadorFourItems          AdaptadorUsuarios;
    private ArrayList<DetalleFourItems> ArrayUsuarios       = new ArrayList<DetalleFourItems>();

    private ArrayList<ContentValues>    _tempTabla;
    private ContentValues               _tempRegistro;

    private UsuarioDAO                  FcnLectura;

    private String                      clienteSeleccionado;
    private String                      _cuenta;
    private String                      _medidor;
    private String                      _nombre;
    private String                      _direccion;
    private String                      _ruta;
    private String                      _codigo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modal_form_buscar);
        Bundle bundle       = getIntent().getExtras();
        this._ruta          = bundle.getString("Ruta");

        this.FcnLectura         = new UsuarioDAO(this); //, this._municipio, this._ruta);

        this._txtBuscar     = (EditText) findViewById(R.id.BuscarTxtBuscar);
        this._cmbFiltro     = (Spinner) findViewById(R.id.BuscarCmbFiltro);
        this._lstClientes   = (ListView) findViewById(R.id.BuscarLstClientes);
        this._tempTabla     = new ArrayList<ContentValues>();
        this.ArrayFiltro    = new ArrayList<String>();

        this.ArrayFiltro.clear();
        this.ArrayFiltro.add("Cuenta");
        this.ArrayFiltro.add("Medidor");
        this.ArrayFiltro.add("Nombre");
        this.ArrayFiltro.add("Direccion");



        this.AdaptadorFiltro    = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,this.ArrayFiltro);
        this._cmbFiltro.setAdapter(this.AdaptadorFiltro);

        this._tempTabla = this.FcnLectura.ListaClientes(this._ruta,false);
        this.ArrayUsuarios.clear();
        for(int i=0;i<this._tempTabla.size();i++){
            this._tempRegistro  = this._tempTabla.get(i);
            ArrayUsuarios.add(new DetalleFourItems( this._tempRegistro.getAsString("cuenta"),
                                                    this._tempRegistro.getAsString("serie_contador"),
                                                    this._tempRegistro.getAsString("nombre"),
                                                    this._tempRegistro.getAsString("direccion")));
        }
        this.AdaptadorUsuarios  = new AdaptadorFourItems(this, ArrayUsuarios);
        this._lstClientes.setAdapter(this.AdaptadorUsuarios);
        registerForContextMenu(this._lstClientes);
      //  _lstClientes.setOnItemClickListener(this);
        this.AdaptadorUsuarios.notifyDataSetChanged();
        this._txtBuscar.addTextChangedListener(this);

        this._cmbFiltro.setOnItemSelectedListener(this);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)menuInfo;

        this._cuenta    = AdaptadorUsuarios.getDataOnFilter(info.position).getItem1();
        this._medidor   = AdaptadorUsuarios.getDataOnFilter(info.position).getItem2();

        //this.clienteSeleccionado = ArrayUsuarios.get(info.position).getItem1();
        /*this._cuenta    = ArrayUsuarios.get(info.position).getItem1();
        this._medidor   = ArrayUsuarios.get(info.position).getItem2();
        this._nombre    = ArrayUsuarios.get(info.position).getItem3();
        this._direccion = ArrayUsuarios.get(info.position).getItem4();*/

        switch(v.getId()){
            case R.id.BuscarLstClientes:
                menu.setHeaderTitle("Cuenta" +" "+this._cuenta);
                super.onCreateContextMenu(menu, v, menuInfo);
                MenuInflater inflater = getMenuInflater();
                inflater.inflate(R.menu.menu_lista_buscar, menu);
                break;
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.BuscarMenuIniciar:
                finish(true, this._cuenta, this._medidor);
                return true;

            case R.id.BuscarMenuSincronizar:

                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }
/*
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,	long id) {
        switch(parent.getId()){
            case R.id.BuscarLstClientes:
                this._cuenta    = ArrayUsuarios.get(position).getItem1();
                this._medidor   = ArrayUsuarios.get(position).getItem2();
                this._nombre    = ArrayUsuarios.get(position).getItem3();
                this._direccion = ArrayUsuarios.get(position).getItem4();

                break;
        }
    }*/

    public void finish(boolean _caso, String cuenta, String medidor) {
        Intent data = new Intent();
        data.putExtra("response", _caso);
        data.putExtra("cuenta", cuenta);
        data.putExtra("medidor", medidor);
        setResult(RESULT_OK, data);
        super.finish();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_form_buscar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void afterTextChanged(Editable s) {
        this.AdaptadorUsuarios.Filtrar(this._cmbFiltro.getSelectedItemPosition(),s.toString());
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        //What you want to do
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.BuscarCmbFiltro:
                this.AdaptadorUsuarios.Filtrar(this._cmbFiltro.getSelectedItemPosition(), this._txtBuscar.getText().toString());
                if(this._cmbFiltro.getSelectedItemPosition() == 0){
                    this._txtBuscar.setInputType(InputType.TYPE_CLASS_NUMBER);
                }else if(this._cmbFiltro.getSelectedItemPosition() == 1){
                    this._txtBuscar.setInputType(InputType.TYPE_CLASS_NUMBER);
                }else if(this._cmbFiltro.getSelectedItemPosition() == 2){
                    this._txtBuscar.setInputType(InputType.TYPE_CLASS_TEXT);
                }else if(this._cmbFiltro.getSelectedItemPosition() == 3){
                    this._txtBuscar.setInputType(InputType.TYPE_CLASS_TEXT);
                }

                break;


            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO Auto-generated method stub
    }
}

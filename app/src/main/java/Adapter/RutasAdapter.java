package Adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.facturas.julian.mobilefacturas.R;

import java.util.ArrayList;
/**
 * Created by SypelcDesarrollo on 04/02/2015.
 */
public class RutasAdapter extends BaseAdapter {

    protected ArrayList<RutasData> listadoRutas;
    protected Activity activity;

    public RutasAdapter(Activity activity, ArrayList<RutasData> _listadoRutas){
            this.activity = activity;
            this.listadoRutas = _listadoRutas;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return listadoRutas.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return listadoRutas.get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(convertView == null){
                LayoutInflater inf = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = inf.inflate(R.layout.activity_lista_informacion_rutas, null);
        }

        RutasData ruta = listadoRutas.get(position);

        TextView codigoRuta 	    = (TextView) v.findViewById(R.id.ListaInfoTxtRutas);
        TextView listaPendientes 	= (TextView) v.findViewById(R.id.ListaInfoTxtPendientes);
        TextView listaLeidas 	    = (TextView) v.findViewById(R.id.ListaInfoTxtLeidas);
        TextView listaTotal 	    = (TextView) v.findViewById(R.id.ListaInfoTxtTotal);

        codigoRuta.setText(ruta.getCodigoRuta());
        listaPendientes.setText(ruta.getTotalPendientes());
        listaLeidas.setText(ruta.getTotalLeidas());
        listaTotal.setText(ruta.getTotalRutas());

        return v;
    }

}

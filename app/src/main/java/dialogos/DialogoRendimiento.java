package dialogos;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.widget.EditText;

import com.facturas.julian.mobilefacturas.R;


/**
 * Created by JULIANEDUARDO on 11/02/2015.
 */
public class DialogoRendimiento extends DialogFragment{
    private EditText    _txtPendientes;
    private EditText    _txtCola;
    private EditText    _txtEnviadas;
    private EditText    _txtTotal;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setTitle(getArguments().getString("Titulo"))
                .setView(inflater.inflate(R.layout.dialog_rendimiento, null))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                });



        //this._txtPendientes = (EditText)  ;

        return builder.create();
    }
}

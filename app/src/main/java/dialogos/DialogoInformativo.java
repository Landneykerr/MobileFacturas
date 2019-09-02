package dialogos;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
//import lecturas.sypelc.mobilelecturas.R;


/**
 * Created by JULIANEDUARDO on 11/02/2015.
 */
public class DialogoInformativo extends DialogFragment{

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(getArguments().getString("Mensaje"))
                .setNeutralButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                    }
                })
                .setTitle(getArguments().getString("Titulo"));
        return builder.create();
    }
}

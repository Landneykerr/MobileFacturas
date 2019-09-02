package dialogos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.facturas.julian.mobilefacturas.R;

/**
 * Created by GrupoDesarrollo on 10/10/2016.
 */
public class CustomToastInfo extends Toast {
    private Context context;

    public CustomToastInfo(Context cont, int duration) {
        super(cont);
        context = cont;
        this.setDuration(duration);
    }

    public void show(CharSequence text) {
        LayoutInflater li = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View vi = (View) li.inflate(R.layout.custom_toast_info, null);
        TextView tv = (TextView) vi.findViewById(R.id.TextToastMensaje);
        this.setView(vi);
        tv.setText(text);
        super.show();
    }
}

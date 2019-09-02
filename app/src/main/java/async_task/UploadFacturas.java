package async_task;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.facturas.julian.mobilefacturas.FormEntregarFactura;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import clases.ClassConfiguracion;
import clases.ClassSession;
import clases.ManagerImageResize;
import global.global_var;
import object.UsuarioDAO;
import sistema.Archivos;
import sistema.Bluetooth;
import sistema.SQLite;

//import clases.ClassAnomaliaOld;

/**
 * Created by SypelcDesarrollo on 12/02/2015.
 */
public class UploadFacturas extends AsyncTask<String, Void, Integer> implements global_var {
    private static final int LIMITE_CUENTAS = 10;


    private Intent              new_form;
    private ClassConfiguracion  fcnCfg;
    private Archivos            FcnArch;
    private SQLite              fcnSQL;
    private ClassSession        Usuario;
    private Bluetooth           fcnBluetooth = Bluetooth.getInstance();

    private Context Context;


    URL                 url;
    HttpURLConnection   conn = null;
    DataOutputStream    dos = null;

    private String NAMESPACE;
    private String Respuesta   = "";
    private String infRuta[];

    private ContentValues               tempMaestroRutas    = new ContentValues();    //Add
    private ContentValues				tempRegistro 	    = new ContentValues();
    private ContentValues				tempRegistro1 	    = new ContentValues();
    private ArrayList<ContentValues>	tempTabla	    	= new ArrayList<ContentValues>();
    private ManagerImageResize          fcnImage;
    public  FormEntregarFactura         fcnNumFotos;
    private UsuarioDAO                  fcnUsuario;

    private static final String METHOD_NAME	= "UploadTrabajoExplicitInspector";
    private static final String SOAP_ACTION	= "UploadTrabajoExplicitInspector";

    private SoapObject so;
    private SoapSerializationEnvelope sse;
    private HttpTransportSE htse;

    SoapPrimitive response = null;
    ProgressDialog _pDialog;


    public UploadFacturas(Context context){
        this.Context    = context;
        fcnImage        = ManagerImageResize.getInstance();
        fcnCfg          = ClassConfiguracion.getInstance(this.Context);
        fcnSQL          = new SQLite(this.Context);
        this.FcnArch	= new Archivos(this.Context, PATH_FILES_APP, 10);
        this.Usuario    = ClassSession.getInstance(this.Context);
        fcnUsuario      = new UsuarioDAO(this.Context);
    }

    protected void onPreExecute() {
    }


    @Override
    protected Integer doInBackground(String... params) {

        JSONObject json = new JSONObject();
        JSONArray facturaArray = new JSONArray();


        try {
            tempTabla = fcnSQL.SelectData("maestro_clientes", "id_programacion, cuenta",
                    "estado='T'", "id_programacion, cuenta", LIMITE_CUENTAS);


            for (ContentValues infUsuario : tempTabla) {
                tempRegistro = fcnSQL.SelectDataRegistro("entrega_factura",
                        "id, mensaje, longitud, latitud, fecha_entrega, id_inspector, distancia",
                        "id_programacion =" + infUsuario.getAsInteger("id_programacion") + " AND cuenta=" + infUsuario.getAsString("cuenta"));


                tempMaestroRutas = fcnSQL.SelectDataRegistro("maestro_rutas",
                        "ciclo, mes, anno",
                        "id_programacion =" + infUsuario.getAsInteger("id_programacion"));

                JSONObject factura = new JSONObject();

                factura.put("id", tempRegistro.getAsInteger("id"));
                factura.put("id_programacion", infUsuario.getAsInteger("id_programacion"));
                factura.put("cuenta", infUsuario.getAsString("cuenta"));
                factura.put("mensaje", remove1(tempRegistro.getAsString("mensaje")));
                factura.put("longitud", tempRegistro.getAsString("longitud"));
                factura.put("latitud", tempRegistro.getAsString("latitud"));
                factura.put("fecha_entrega", tempRegistro.getAsString("fecha_entrega"));
                factura.put("id_inspector", tempRegistro.getAsInteger("id_inspector"));
                factura.put("distancia", tempRegistro.getAsDouble("distancia"));

                facturaArray.put(factura);
            }

            json.put("informacion", facturaArray);
        }catch (JSONException jex){
            Log.i("Error JSON", jex.toString());
        }catch (Exception ex){
            Log.i("Error", ex.toString());
        }

        try{
            url = new URL(fcnCfg.getIp_server() + ":" + fcnCfg.getPort() + "/" + fcnCfg.getModule_web_service() + "/UploadJSON.php");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setInstanceFollowRedirects(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("charset", "utf-8");

            dos = new DataOutputStream(conn.getOutputStream());

            String data = "informacion="+json.toString()+"&Peticion=UploadTrabajo&origen="+fcnBluetooth.GetOurDeviceByAddress();
            dos.writeBytes(data);
            dos.flush();
            dos.close();

            InputStream is = this.conn.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            String line;

            StringBuffer response = new StringBuffer();
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();

            String informacion[] = new String(response.toString()).trim().split("\\|");

            tempRegistro1.clear();
            tempRegistro1.put("estado","E");

            for(String id: informacion) {
                    //Con el id local se consulta los id_seriales originales para poder actualizar el registro general
                if(!id.equals("")){
                    tempRegistro = fcnSQL.SelectDataRegistro("entrega_factura", "id_programacion, cuenta",
                                "id=" + id);
                        //Se hace el cambio de estado de (T)erminado a (E)nviado
                    fcnSQL.UpdateRegistro("maestro_clientes", tempRegistro1,
                            "id_programacion=" + tempRegistro.getAsInteger("id_programacion") + " AND cuenta='" + tempRegistro.getAsString("cuenta") + "'");

                }
            }


        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Upload file", "Exception : " + e.getMessage(), e);
        } finally {
            conn.disconnect();
        }

        return 0;
    }


    @Override
    protected void onPostExecute(Integer rta) {

    }


    public static String remove1(String input) {
        // Cadena de caracteres original a sustituir.
        String original = "áàäéèëíìïóòöúùuñÁÀÄÉÈËÍÌÏÓÒÖÚÙÜÑçÇ";
        // Cadena de caracteres ASCII que reemplazarán los originales.
        String ascii = "aaaeeeiiiooouuunAAAEEEIIIOOOUUUNcC";
        String output = input;
        for (int i=0; i<original.length(); i++) {
            // Reemplazamos los caracteres especiales.
            output = output.replace(original.charAt(i), ascii.charAt(i));
        }//for i
        return output;
    }
}





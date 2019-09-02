package async_task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import clases.ClassSession;
import clases.ManagerImageResize;
import global.global_var;
import sistema.Archivos;
import sistema.SQLite;

//import clases.ClassAnomaliaOld;

/**
 * Created by SypelcDesarrollo on 12/02/2015.
 */
public class UpLoadFoto extends AsyncTask<String,String,Integer> implements global_var {
    private static final int LIMITE_CUENTAS = 10;

    private Context Context;

    URL                 url;
    HttpURLConnection   conn = null;
    DataOutputStream    dos = null;

    private ClassSession    FcnSession;
    ManagerImageResize      fcnImagen;
    private SQLite          fcnSQL;
    private Archivos        FcnArch;

    ProgressDialog _pDialog;
    String ciclo;

    public UpLoadFoto(Context context){
        this.Context    = context;
        this.fcnImagen  = ManagerImageResize.getInstance();
        this.fcnSQL     = new SQLite(this.Context);
        this.FcnSession = ClassSession.getInstance(Context);
        this.FcnArch	= new Archivos(this.Context, PATH_FILES_APP, 10);
    }


    protected void onPreExecute() {
        Toast.makeText(this.Context, "Iniciando conexion con el servidor, por favor espere...", Toast.LENGTH_SHORT).show();
        _pDialog = new ProgressDialog(this.Context);
        _pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        _pDialog.setMessage("Ejecutando operaciones...");
        _pDialog.setCancelable(false);
        _pDialog.setProgress(0);
        _pDialog.setMax(100);
        _pDialog.show();
    }


    @Override
    protected Integer doInBackground(String... params) {
        try{
            File f = new File(PATH_FILES_APP+File.separator+SUB_PATH_PICTURES);
            File[] fotosTotal = f.listFiles();
            //Integer.parseInt(this.FcnSession.getCodigo());
            for (int j=0;j<fotosTotal.length;j++) {
                if (fotosTotal[j].isDirectory()) {
                    File[] fotos = this.FcnArch.ListaFotos(fotosTotal[j]+"", false);
                    String[] idCiclo = String.valueOf(fotosTotal[j]).split("/");

                    for (int i = 0; i < fotos.length; i++) {
                        String extension = getFileExtension(fotos[i]);
                        if (extension.equals("JPEG")) {
                            String[] _foto = fotos[i].getName().split("_");
                            this.ciclo = idCiclo[6];

                            JSONObject json = new JSONObject();
                            JSONArray fotoArray = new JSONArray();

                            JSONObject foto = new JSONObject();

                            foto.put("foto", this.fcnImagen.encodeToBase64(fotos[i].getName(), this.ciclo));
                            foto.put("nombre_foto", fotos[i].getName());
                            DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date dt = new Date(fotos[i].lastModified());
                            String fechaToma = dateFormat.format(dt);
                            String[] fechaLectura = fechaToma.split("/");
                            foto.put("fecha", fechaToma);
                            foto.put("cuenta", _foto[0]);
                            foto.put("ciclo", this.ciclo);
                            foto.put("mes", fechaLectura[1]);
                            foto.put("anno", fechaLectura[0]);
                            //foto.put("id_inspector",datos.getAsInteger("id_inspector"));//Integer.parseInt(this.FcnSession.getCodigo())
                            foto.put("id_inspector", Integer.parseInt(this.FcnSession.getCodigo()));
                            foto.put("fecha_entrega", fechaToma);//fechaToma
                            /*
                            foto.put("fecha","2017/04/26 14:33:25");
                            foto.put("cuenta",_foto[0]);
                            foto.put("ciclo",36);
                            foto.put("mes",4);
                            foto.put("anno",2017);
                            foto.put("id_inspector",2020);
                            foto.put("fecha_entrega","2017/04/26 14:33:25");*/

                            fotoArray.put(foto);
                            json.put("Fotos", fotoArray);


                            url = new URL("http://186.115.150.189/FotosFacturas/WSFotos/UploadJSON.php");
                            conn = (HttpURLConnection) url.openConnection();
                            conn.setDoInput(true);
                            conn.setDoOutput(true);
                            conn.setUseCaches(false);
                            conn.setInstanceFollowRedirects(false);
                            conn.setRequestMethod("POST");
                            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                            conn.setRequestProperty("charset", "utf-8");

                            dos = new DataOutputStream(conn.getOutputStream());

                            String data = "informacion=" + json.toString() + "&Peticion=UploadFoto";
                            dos.writeBytes(data);
                            dos.flush();
                            dos.close();

                            InputStream is = this.conn.getInputStream();
                            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                            String line;

                            StringBuffer response = new StringBuffer();
                            while ((line = rd.readLine()) != null) {
                                response.append(line);
                                response.append('\r');
                            }
                            rd.close();

                            String informacion = new String(response.toString()).trim();
                            this.publishProgress(fotos.length + "", fotos[i].getName(), i + 1 + "");
                            this.fcnImagen.deleteImage(informacion, this.ciclo);
                        }
                    }
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
        _pDialog.dismiss();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        _pDialog.setMessage(values[1]);
        _pDialog.setMax(Integer.valueOf(values[0]));
        _pDialog.setProgress(Integer.valueOf(values[2]));
    }

    private static String getFileExtension(File file) {
        String fileName = file.getName();
        if(fileName.lastIndexOf(".") != -1 && fileName.lastIndexOf(".") != 0)
            return fileName.substring(fileName.lastIndexOf(".")+1);
        else return "";
    }
}





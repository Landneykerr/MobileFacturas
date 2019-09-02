package sistema;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class Archivos {
    public static int MAX_WIDTH 	= 1280;
    public static int MAX_HEIGTH	= 960;

    private Context ctx;
    private String 			        Directory;
    private int 			        SizeBuffer;
    private File[]			        ListaArchivos;
    private java.io.FileFilter 	    OnlyFolders;
    private java.io.FilenameFilter	OnlyPictures;

    private FileInputStream         fis;
    private FileReader              file;

    public Archivos(Context ctx, String CurrentDirectory, int BufferKbytes){
        this.ctx 			= ctx;
        this.Directory 		= CurrentDirectory;
        this.SizeBuffer 	= BufferKbytes;
        this.OnlyFolders	= 	new FileFilter(){
            public boolean accept(File dir){
                return (dir.isDirectory());
            };
        };

        this.OnlyPictures	= 	new FilenameFilter(){
            public boolean accept(File dir, String name){
                return (name.endsWith(".jpg")||name.endsWith(".jpeg")||name.endsWith(".JPEG"));
            };
        };


        if(!ExistFolderOrFile(this.Directory, false)){
            MakeDirectory(this.Directory, false);
        }
    }


    /**
     * Metodo para crear una carpeta
     * @param _new_directory 					-> ruta de la nueva carpeta
     * @param _relativeCurrentDirectory true	-> si la carpeta es relativa al directorio del proyecto
     * 									false	-> si la carpeta es independiente al directorio del proyecto
     * @return 							true	-> si se creo correctamente la carpeta
     * 									false	-> si hubo algun error al crear la carpeta
     */
    public boolean MakeDirectory(String _new_directory, boolean _relativeCurrentDirectory){
        if(_relativeCurrentDirectory){
            _new_directory = this.Directory+File.separator+_new_directory;
        }
        File f = new File(_new_directory);
        if(f.mkdir()){
            //Toast.makeText(this.ctx,"Directorio "+_new_directory+" correctamente.", Toast.LENGTH_SHORT).show();
            return true;
        }else{
            return false;
        }
    }


    /**
     * Metodo para comprobar si existe una carpeta o archivo
     * @param _ruta String con la ruta completa de la carpeta que deseamos saber si existe
     * @return	retorna true si existe la carpeta false en caso contrario
     */
    public boolean ExistFolderOrFile(String _ruta, boolean _relativeCurrentDirectory){
        if(_relativeCurrentDirectory){
            _ruta = this.Directory+File.separator+_ruta;
        }
        File f = new File(_ruta);
        return f.exists();
    }




    public int numArchivosInFolder(String _ruta, boolean _relativeCurrentDirectory){
        if(_relativeCurrentDirectory){
            _ruta = this.Directory+File.separator+_ruta;
        }
        this.ListaArchivos = new File(_ruta).listFiles();
        return this.ListaArchivos.length;
    }

    public int numArchivosInFolderBeginByName(String _ruta, String _name, boolean _relativeCurrentDirectory){
        int _start_same_name = 0;

        if(!this.ExistFolderOrFile(_ruta,  _relativeCurrentDirectory)){
            this.MakeDirectory(_ruta,  _relativeCurrentDirectory);
        }

        if(_relativeCurrentDirectory){
            _ruta = this.Directory+File.separator+_ruta;
        }

        this.ListaArchivos = new File(_ruta).listFiles();
        for(int i=0;i<this.ListaArchivos.length;i++){
            if(!this.ListaArchivos[i].isDirectory()){
                if(this.ListaArchivos[i].getName().toString().startsWith(_name)){
                    _start_same_name++;
                }
            }
        }
        return _start_same_name;
    }



    public File[] ListaDirectorios(String _ruta, boolean _relativeCurrentDirectory){
        if(_relativeCurrentDirectory){
            _ruta = this.Directory+File.separator+_ruta;
        }
        this.ListaArchivos = new File(_ruta).listFiles(OnlyFolders);
        return this.ListaArchivos;
    }



    public File[] ListaFotos(String _ruta, boolean _relativeCurrentDirectory){
        if(_relativeCurrentDirectory){
            _ruta = this.Directory+File.separator+_ruta;
        }
        this.ListaArchivos = new File(_ruta).listFiles(OnlyPictures);
        return this.ListaArchivos;
    }


    public void ResizePicture(String _destino, File _archivo, String _pre){
        Bitmap resizedBitmap;
        Bitmap bimage = BitmapFactory.decodeFile(_archivo.toString());
        //resizedBitmap = Bitmap.createScaledBitmap(bimage, MAX_HEIGTH, MAX_WIDTH, false);
        //this.DeleteFile(_archivo);


        //Redimensionamos
        int width = bimage.getWidth();
        int height = bimage.getHeight();
        float scaleHeight = ((float) MAX_HEIGTH) / height;
        float scaleWidth = ((float) MAX_WIDTH) / width;
        // create a matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(scaleWidth,scaleHeight);
        matrix.postRotate(90);
        // recreate the new Bitmap
        resizedBitmap = Bitmap.createBitmap(bimage, 0, 0, width, height, matrix, false);

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream(_destino+File.separator+_pre+_archivo.getName().toString());
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
        }catch (FileNotFoundException ex){
            ex.printStackTrace();
        }catch (IOException ex){
            ex.printStackTrace();
        }
    }








    /**Metodo par aeliminar un archivo que se encuentre en una carpeta especifica
     *
     * @param Archivo 	-> ruta y archivo que se va a eliminar
     * @return			-> true si se elimino correctamente, false en caso contrario
     */
    public boolean DeleteFile(String Archivo){
        File f = new File(Archivo);
        return f.delete();
    }


    //Metodo para comprobar la exitencia de memoria externa
    public boolean MemoryExt(){
        boolean valorRetorno = false;
        String estado = Environment.getExternalStorageState();
        if (estado.equals(Environment.MEDIA_MOUNTED)){
            valorRetorno = true;			//Montada y disponible para lectura/escritura
        }else if (estado.equals(Environment.MEDIA_MOUNTED_READ_ONLY)){
            valorRetorno = false;			//Montada y disponible solo para lectura
        }
        else{
            valorRetorno = false;			//No se encuentra montada la memoria SD
        }
        return valorRetorno;
    }


    //Metodo para saber las lineas de un archivo
    public int TamanoArchivo(String NombreArchivo){
        int CountBfrArchivo = 0;
        try {
            file = new FileReader(NombreArchivo);
            BufferedReader BfrArchivo = new BufferedReader(file);
            while(BfrArchivo.readLine()!=null){
                CountBfrArchivo++;
            }
        } catch (FileNotFoundException e) {
            CountBfrArchivo = 0;
        }  catch (IOException e) {
            CountBfrArchivo = 0;
        }
        return CountBfrArchivo;
    }


    //Metodo para convertir el contenido de un archivo a un array
    public ArrayList<String> FileToArrayString(String Archivo, boolean ruta_completa){
        File file;
        String queryString;
        String storageState = Environment.getExternalStorageState();
        ArrayList<String> InformacionFile = new ArrayList<String>();

        if (storageState.equals(Environment.MEDIA_MOUNTED)) {
            if(ruta_completa){
                file = new File(Archivo);
            }else{
                file = new File(this.Directory + File.separator +Archivo);
            }

            BufferedReader inputReader2;
            try {
                inputReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while ((queryString = inputReader2.readLine()) != null) {
                    InformacionFile.add(queryString);
                }
                file.delete();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return InformacionFile;
    }



	/*public boolean ExisteArchivo(String NombreArchivo){
		return true;
	}*/



    public boolean DoFile(String _rutaArchivo, String NombreArchivo, String InformacionArchivo){
        boolean Retorno = false;
        File file;
        try {
            if(!_rutaArchivo.isEmpty()){
                if(!ExistFolderOrFile(_rutaArchivo,true)){
                    MakeDirectory(_rutaArchivo, true);
                }
                file = new File(this.Directory + File.separator + _rutaArchivo + File.separator + NombreArchivo);
            }else{
                file = new File(this.Directory + File.separator + NombreArchivo);
            }

            file.createNewFile();
            if (file.exists()&&file.canWrite()){
                FileWriter filewriter = new FileWriter(file,false);
                filewriter.write(InformacionArchivo);
                filewriter.close();
                Retorno = true;
            }
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
            Retorno = false;
        }
        return Retorno;
    }


    public byte[] FileToArrayBytes(String _carpeta, String _nombreArchivo, boolean _relativeDirectory){
        int len = 0;
        InputStream is 	= null;
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * this.SizeBuffer);
        byte[] buffer = new byte[1024*this.SizeBuffer];

        try{
            if (_nombreArchivo != null) {
                if(_relativeDirectory){
                    is = new FileInputStream(this.Directory + File.separator+_carpeta + File.separator + _nombreArchivo);
                }else{
                    is = new FileInputStream(_carpeta + File.separator + _nombreArchivo);
                }

                try {
                    while ((len = is.read(buffer)) >= 0) {
                        os.write(buffer, 0, len);
                    }
                } finally {
                    is.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try {
                throw new IOException("Unable to open R.raw.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return os.toByteArray();
    }

    public byte[] FileToArrayBytesOne(String NombreArchivo){
        int len = 0;
        InputStream is 	= null;
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * this.SizeBuffer);
        byte[] buffer = new byte[1024*this.SizeBuffer];

        try{
            if (NombreArchivo != null) {
                is = new FileInputStream(NombreArchivo);
                try {
                    while ((len = is.read(buffer)) >= 0) {
                        os.write(buffer, 0, len);
                    }
                } finally {
                    is.close();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            try {
                throw new IOException("Unable to open R.raw.");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return os.toByteArray();
    }



    public void ByteArrayToFile(byte[] data, String NombreArchivo) throws IOException{
        FileOutputStream out = new FileOutputStream(this.Directory + File.separator + NombreArchivo);
        out.write(data);
        out.close();
    }




    public boolean CrearArchivo(String NombreArchivo, String Encabezado, ArrayList<ArrayList<String>> Informacion){
        ArrayList<String> Registro = new ArrayList<String>();
        String CadenaArchivo = Encabezado;

        for(int i=0; i<Informacion.size(); i++){
            Registro = Informacion.get(i);
            for(int j=0; j<Registro.size(); j++){
                CadenaArchivo += Registro.get(j)+";";
            }
            CadenaArchivo += "\n";
        }

        try {
            File file = new File(this.Directory + File.separator + NombreArchivo);
            file.createNewFile();
            if (file.exists()&&file.canWrite()){
                FileWriter filewriter = new FileWriter(file,false);
                filewriter.write(CadenaArchivo);
                filewriter.close();
            }
        } catch (IOException e2) {
            // TODO Auto-generated catch block
            e2.printStackTrace();
        }
        return true;
    }


    /*public Bitmap getImage(String _rutaArchivo, String _nombreArchivo){

        return mBitmap;
    }*/
}


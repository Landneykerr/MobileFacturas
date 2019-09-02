package clases;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import global.global_var;


/**
 * Created by julian on 10/08/15.
 */
public class ManagerImageResize implements global_var {
    private static ManagerImageResize ourInstance = null;


    /**
     * Constructor singleton de la clase
     * **/
    public static ManagerImageResize getInstance() {
        if(ourInstance == null){
            ourInstance = new ManagerImageResize();
        }
        return ourInstance;
    }

    private ManagerImageResize(){
    }


    public boolean deleteImage(String _nameFile, String _directorio)
    {
        File fdelete = new File (PATH_FILES_APP + File.separator + SUB_PATH_PICTURES + File.separator + _directorio + File.separator + _nameFile);
        return fdelete.delete();

    }

    public String encodeToBase64(String _nameFile, String _directorio) {
        File imagefile = new File(PATH_FILES_APP + File.separator + SUB_PATH_PICTURES + File.separator + _directorio + File.separator + _nameFile);
        FileInputStream fis = null;
        try{
            fis = new FileInputStream(imagefile);
        }catch(FileNotFoundException e){
            e.printStackTrace();
        }
        if(fis!=null) {
            Bitmap bm = BitmapFactory.decodeStream(fis);
            //Base64.d
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] b = baos.toByteArray();
            String encImage = Base64.encodeToString(b, Base64.DEFAULT);
            return encImage;
        }else{
            String encImage = "";
            return encImage;
        }
    }


    public void resizeImage(String _nameFile, int _width, int _height){
        byte[] _image = this.FileToArrayBytesOne(PATH_FILES_APP + File.separator + SUB_PATH_PICTURES + File.separator + _nameFile);
        Bitmap b = BitmapFactory.decodeByteArray(_image, 0, _image.length);
        Bitmap newImage = Bitmap.createScaledBitmap(b, _width, _height, false);

        File pictureFile = new File(PATH_FILES_APP + File.separator + SUB_PATH_PICTURES + File.separator + _nameFile);
        if (pictureFile == null) {
            //Log.d(TAG,"Error creating media file, check storage permissions: ");// e.getMessage());
            return;
        }
        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            newImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
        } catch (FileNotFoundException e) {
            //Log.d(TAG, "File not found: " + e.getMessage());
        } catch (IOException e) {
            //Log.d(TAG, "Error accessing file: " + e.getMessage());
        }
    }



    private byte[] FileToArrayBytesOne(String NombreArchivo){
        int len = 0;
        InputStream is 	= null;
        ByteArrayOutputStream os = new ByteArrayOutputStream(1024 * _SIZZE_BUFFER);
        byte[] buffer = new byte[1024*_SIZZE_BUFFER];

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


}
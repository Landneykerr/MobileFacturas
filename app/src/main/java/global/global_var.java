package global;

import android.os.Environment;

import java.io.File;

/**
 * Created by julian on 10/07/15.
 */
public interface global_var {
    final String NAME_DATABASE      = "EntregaFacturasBD";
    final String PATH_FILES_APP     = Environment.getExternalStorageDirectory() + File.separator + "EntregaFacturas";
    final String SUB_PATH_PICTURES  = "Fotos"; //PATH_FILES_APP +File.separator + "Fotos";

    /*public static String name_database      = "EntregaFacturasBD";
    public static String path_files_app     = Environment.getExternalStorageDirectory() + File.separator + "EntregaFacturas";
    public static String sub_path_pictures  = "Fotos";*/



    /**
     * Constantes para el manejo de las fotos
     */
    final int   _SIZZE_BUFFER       = 5;
    final int   _WIDTH_PICTURE      = 620;
    final int   _HEIGHT_PICTURE     = 430;

}

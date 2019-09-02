package sistema;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * Created by DesarrolloJulian on 12/06/2015.
 */
public class Utilidades {
    private static Utilidades ourInstance = new Utilidades();

    public static Utilidades getInstance() {
        if(ourInstance == null){
            ourInstance = new Utilidades();
        }
        return ourInstance;
    }

    private Utilidades() {
    }


    public String getMD5(String _data){
        byte[] source;
        try {
            //Get byte according by specified coding.
            source = _data.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            source = _data.getBytes();
        }
        String result = null;
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(source);
            //The result should be one 128 integer
            byte temp[] = md.digest();
            char str[] = new char[16 * 2];
            int k = 0;
            for (int i = 0; i < 16; i++) {
                byte byte0 = temp[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            result = new String(str);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}

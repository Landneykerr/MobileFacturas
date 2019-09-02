package sistema;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by JULIANEDUARDO on 19/02/2015.
 */

public class DateTime {
    protected static String     NameOfMonth[]   = { "Enero",
                                                    "Febrero",
                                                    "Marzo",
                                                    "Abril",
                                                    "Mayo",
                                                    "Junio",
                                                    "Julio",
                                                    "Agosto",
                                                    "Septiembre",
                                                    "Octubre",
                                                    "Noviembre",
                                                    "Diciembre"};

    protected static String     NameOfMonthShort[]   = {"Ene",
                                                        "Feb",
                                                        "Mar",
                                                        "Abr",
                                                        "May",
                                                        "Jun",
                                                        "Jul",
                                                        "Ago",
                                                        "Sept",
                                                        "Oct",
                                                        "Nov",
                                                        "Dic"};
    protected static DateTime   instance        = null;

    public static DateTime getInstance(){
        if(instance == null){
            instance = new DateTime();
        }
        return instance;
    }

    private DateTime(){

    }


    public String GetFecha(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = df1.format(c.getTime());
        return formattedDate;
    }


    public String DateWithNameMonth(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd-MM-yyyy");
        String formattedDate[] = df1.format(c.getTime()).split("-");
        return NameOfMonth[Integer.parseInt(formattedDate[1])]+" "+formattedDate[0]+"-"+formattedDate[2];
    }

    public String DateWithNameMonthShort() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate[] = df1.format(c.getTime()).split("-");
        return NameOfMonthShort[Integer.parseInt(formattedDate[1])-1]+" "+formattedDate[0]+" de "+formattedDate[2];
    }

    public String GetHora(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm:ss");
        String formattedDate = df1.format(c.getTime());
        return formattedDate;
    }

    public String GetHoraMinutos(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("HH:mm");
        String formattedDate = df1.format(c.getTime());
        return formattedDate;
    }

    public String GetDateTimeHora(){
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        String formattedDate = df1.format(c.getTime());
        return formattedDate;
    }

    public String getDateUTC(){
        String _retorno = null;
        try{
            SimpleDateFormat f = new SimpleDateFormat("dd/MM/yyyy");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            _retorno = f.format(new Date());
        }catch(Exception ex){

        }
        return _retorno;
    }

    public String getHourUTC(){
        String _retorno = null;
        try{
            SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss");
            f.setTimeZone(TimeZone.getTimeZone("UTC"));
            _retorno = f.format(new Date());
        }catch(Exception ex){

        }
        return _retorno;
    }
}
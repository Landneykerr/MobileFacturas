package object;

/**
 * Created by JULIANEDUARDO on 20/02/2015.
 */
public class UsuarioEMSA {
    //Datos de referencia en la base de datos
    private int         idProgramacion;
    private int         ciclo;
    private String      municipio;
    private String      ruta;

    //Datos de identificacion del usuario
    private int         id_serial;
    private int         id_secuencia;
    private String      cuenta;
    private String      nombre;
    private String      direccion;
    private String      marcaContador;
    private String      serieContador;
    private int         secuenciaImp;
    private int         secuenciaRuta;
    private String      estado;

    private String      codigoRuta;

    private String      latitudCuenta;
    private String      longitudCuenta;


    public UsuarioEMSA(int _ciclo, String _municipio, String _ruta, int _serial, int _idProgramacion,
                       int _idSecuencia, String _cuenta, String _nombre, String _direccion, String _marcaCon,
                       String _serieCon, int _secImpresion, int _secRuta, String _estado, String _codigoRuta, String _latitudCuenta, String _longitudCuenta)
    {
        idProgramacion = _idProgramacion;
        ciclo           = _ciclo;
        municipio       = _municipio;
        ruta            = _ruta;

        id_serial       = _serial;
        id_secuencia    = _idSecuencia;
        cuenta          = _cuenta;
        nombre          = _nombre;
        direccion       = _direccion;
        marcaContador  = _marcaCon;
        serieContador  = _serieCon;
        secuenciaImp   = _secImpresion;
        secuenciaRuta  = _secRuta;
        estado         = _estado;
        codigoRuta     = _codigoRuta;
        latitudCuenta  = _latitudCuenta;
        longitudCuenta = _longitudCuenta;
    }


    public int getIdProgramacion() {
        return idProgramacion;
    }

    public int getCiclo() {
        return ciclo;
    }

    public String getMunicipio() {
        return municipio;
    }

    public String getRuta() {
        return ruta;
    }

    public int getId_serial() {
        return id_serial;
    }

    public int getId_secuencia() {
        return id_secuencia;
    }

    public String getCuenta() {
        return cuenta;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getMarcaContador() {
        return marcaContador;
    }

    public String getSerieContador() {
        return serieContador;
    }

    public int getSecuenciaImp() {   return secuenciaImp;   }

    public int getSecuenciaRuta() {
        return secuenciaRuta;
    }

    public String getCodigoRuta() {
        return codigoRuta;
    }

    public String getEstado() {
        return estado;
    }

    public String getLatitudCuenta() {
        return latitudCuenta;
    }

    public String getLongitudCuenta() {
        return longitudCuenta;
    }
}

package object;

/**
 * Created by JULIANEDUARDO on 20/02/2015.
 */
public class UsuarioLeido extends UsuarioEMSA {

    private static final int RADIO_CERCANIA = 5;

    private int     countFotos;
    private double  distancia;
    private boolean doneCodeBar;
    private boolean leido;
    private boolean cerca;


    public UsuarioLeido(int _ciclo, String _municipio, String _ruta, int _serial, int _idProgramacion,
                        int _idSecuencia, String _cuenta, String _nombre, String _direccion, String _marcaCon,
                        String _serieCon, int _secImpresion, int _secRuta, String _estado, String _codigoRuta, String _latitudCuenta, String _longitudCuenta){

        super(_ciclo, _municipio, _ruta, _serial, _idProgramacion, _idSecuencia, _cuenta, _nombre,
                _direccion, _marcaCon, _serieCon, _secImpresion, _secRuta, _estado, _codigoRuta, _latitudCuenta, _longitudCuenta);

        leido = _estado == "T";
        cerca = false;
    };



    public int getCountFotos() {
        return countFotos;
    }

    public void setCountFotos(int countFotos) {
        this.countFotos = countFotos;
    }

    public boolean isDoneCodeBar() {
        return doneCodeBar;
    }

    public void setDoneCodeBar(boolean doneCodeBar) {
        this.doneCodeBar = doneCodeBar;
    }

    public boolean isLeido() {
        return leido;
    }

    public void setLeido(boolean leido) {
        this.leido = leido;
    }

    public double getDistancia() { return distancia; }

    public void setDistancia(double distancia) {
        this.distancia = distancia;

        if(this.distancia <= RADIO_CERCANIA)
        {
            this.cerca = true;
        }else{
            this.cerca = false;
        }
    }

    public boolean isCerca() {
        return cerca;
    }
}

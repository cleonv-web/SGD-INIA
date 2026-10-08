package pe.gob.segdi.iotramitesgd.bean;

public class AuditoriaLogin {
    private int auto;
    private int cod_trabajador;
    private String fecha_login;
    private int flag_acc;
    private int segundos;


    public int getAuto() {
        return auto;
    }

    public void setAuto(int auto) {
        this.auto = auto;
    }

    public int getSegundos() {
        return segundos;
    }

    public void setSegundos(int segundos) {
        this.segundos = segundos;
    }

    public int getCod_trabajador() {
        return cod_trabajador;
    }

    public void setCod_trabajador(int cod_trabajador) {
        this.cod_trabajador = cod_trabajador;
    }


    public String getFecha_login() {
        return fecha_login;
    }

    public void setFecha_login(String fecha_login) {
        this.fecha_login = fecha_login;
    }

    public int getFlag_acc() {
        return flag_acc;
    }

    public void setFlag_acc(int flag_acc) {
        this.flag_acc = flag_acc;
    }

}

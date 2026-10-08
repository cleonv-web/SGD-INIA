package pe.gob.segdi.iotramitesgd.bean;

public class DatosCorreo {
    private String host;
    private String puerto;
    private String usuario;
    private String password;
    private String from;
    private String[] sMailTo;
    private String sSubject;
    private String sMailText;
    private String[] archivo;

    public String getHost() {
        return host;
    }

    public String getPuerto() {
        return puerto;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getPassword() {
        return password;
    }

    public String getFrom() {
        return from;
    }

    public String[] getsMailTo() {
        return sMailTo;
    }

    public String getsSubject() {
        return sSubject;
    }

    public String getsMailText() {
        return sMailText;
    }

    public String[] getArchivo() {
        return archivo;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPuerto(String puerto) {
        this.puerto = puerto;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setsMailTo(String[] sMailTo) {
        this.sMailTo = sMailTo;
    }

    public void setsSubject(String sSubject) {
        this.sSubject = sSubject;
    }

    public void setsMailText(String sMailText) {
        this.sMailText = sMailText;
    }

    public void setArchivo(String[] archivo) {
        this.archivo = archivo;
    }

}

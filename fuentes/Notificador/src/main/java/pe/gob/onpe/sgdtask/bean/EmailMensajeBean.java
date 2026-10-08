package pe.gob.onpe.sgdtask.bean;
public class EmailMensajeBean {
    private String deCorreoDestino;/*CORREO*/
    private String deTituloMensaje;/**/
    private String deCuerpoMensaje;


    public String getDeCorreoDestino() {
        return deCorreoDestino;
    }

    public void setDeCorreoDestino(String deCorreoDestino) {
        this.deCorreoDestino = deCorreoDestino;
    }

    public String getDeTituloMensaje() {
        return deTituloMensaje;
    }

    public void setDeTituloMensaje(String deTituloMensaje) {
        this.deTituloMensaje = deTituloMensaje;
    }

    public String getDeCuerpoMensaje() {
        return deCuerpoMensaje;
    }

    public void setDeCuerpoMensaje(String deCuerpoMensaje) {
        this.deCuerpoMensaje = deCuerpoMensaje;
    }        
      
}

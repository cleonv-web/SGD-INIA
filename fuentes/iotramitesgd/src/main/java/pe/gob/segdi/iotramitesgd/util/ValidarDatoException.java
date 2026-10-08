package pe.gob.segdi.iotramitesgd.util;


public class ValidarDatoException extends Exception {

    private static final long serialVersionUID = 1L;
    public String valorMsg;

    public ValidarDatoException(String valMsg) {
        this.valorMsg = Utilitarios.ObtenerDatosProperties("MessageResources", valMsg);
    }
}

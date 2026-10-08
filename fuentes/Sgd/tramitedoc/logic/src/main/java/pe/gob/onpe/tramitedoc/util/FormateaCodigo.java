package pe.gob.onpe.tramitedoc.util;

public class FormateaCodigo 
{
  public static String getCodigo(String mascara, int numero) {
        String snumero = String.valueOf(numero);
        int lnumero = snumero.length();
        String codigo = mascara + snumero;
        return codigo.substring(lnumero);
	}
}
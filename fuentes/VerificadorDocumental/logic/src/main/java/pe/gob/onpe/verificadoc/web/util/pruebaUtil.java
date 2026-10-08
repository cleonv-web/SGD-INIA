/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.web.util;

/**
 *
 * @author RATAYAURI
 */
public class pruebaUtil {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        try {
            System.out.println(Utility.getInstancia().descifrar("B038ABF0E9260107EDA5D74EF5AADC0519959FC36DEEAF84DCC31EA9A4F80D84",Constantes.SGD_SECRET_KEY_PROPERTIES));
            System.out.println(Utility.getInstancia().cifrar("D:\\Temp_verifica",Constantes.SGD_SECRET_KEY_PROPERTIES));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}

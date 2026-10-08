/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.util;

import java.util.Iterator;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;

/**
 *
 * @author wcutipa
 */
public class MenuOpciones {

    public static boolean  verificaOpcion(Usuario usuario, String opMenu)
    {
        boolean vret=false;

        for (Iterator<UsuarioAcceso> iter = usuario.getUsuarioAccesos().iterator(); iter.hasNext(); ) {
            UsuarioAcceso accesoOP = iter.next();
            if ( accesoOP.getCoOpcion().equals(opMenu)){
                vret=true;
                break;
            }            
        }
        
        return vret;
    }
}

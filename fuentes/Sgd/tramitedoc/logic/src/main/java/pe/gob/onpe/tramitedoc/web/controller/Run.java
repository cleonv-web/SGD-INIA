/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;

/**
 *
 * @author ypino
 */
public class Run {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        
        try {
            //System.out.println(Utility.getInstancia().descifrar("10F2852051B7BABD69D6D1C876EE9C6A051ABF082B410FFB79036F15AC90B25F",ConstantesSec.SGD_SECRET_KEY_PROPERTIES));    
            //System.out.println(Utility.getInstancia().cifrar("C:\\tmp",ConstantesSec.SGD_SECRET_KEY_PROPERTIES));    
            System.out.println(Utility.getInstancia().cifrar("10424983883",ConstantesSec.SGD_SECRET_KEY_PROPERTIES));    
        } catch (Exception e) {
        }
        
        
    }
    
}

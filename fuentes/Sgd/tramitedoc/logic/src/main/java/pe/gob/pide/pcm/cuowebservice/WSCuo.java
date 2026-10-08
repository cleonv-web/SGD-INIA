/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.pide.pcm.cuowebservice;

import java.rmi.RemoteException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author adelgado
 */
public class WSCuo {
    public static void main(String[] args) {
        try {
            ApplicationServicesPortTypeProxy proxy = new ApplicationServicesPortTypeProxy();
            String	cuo = proxy.getCUO("190.116.1.56");
            System.out.println("CUO ==>"+cuo);
        } catch (RemoteException ex) {
            Logger.getLogger(WSCuo.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String getCuo(String ip) throws Exception{
        String cuo = "";
        try {
            ApplicationServicesPortTypeProxy proxy = new ApplicationServicesPortTypeProxy();
            return cuo = proxy.getCUO(ip);
        } catch (Exception e) {
            Logger.getLogger(WSCuo.class.getName()).log(Level.SEVERE, null, e);
            return cuo = "0";
        }
       
    }
	
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.CredencialUserDao;
import pe.gob.onpe.tramitedoc.service.CredencialUserService;

/**
 *
 * @author USUARIO
 */
@Service("CredencialUserService")
public class CredencialUserServiceImp implements CredencialUserService {
    
    @Autowired
    public CredencialUserDao credencialUserDao;
 
    @Override
    public List<AdmEmpleadoBean> goListaBusqUsuario(String indicador, String estado) throws Exception {
       try {
             return credencialUserDao.getListaBusqUsuario(indicador, estado);
         } catch (Exception e) {
             throw e;
         }
    }
    
    @Override
    public List<SiElementoBean> goListaBusqCredencialUser(String estado) throws Exception{
       try {
             return credencialUserDao.getListaBusqCredencialUser(estado);
         } catch (Exception e) {
             throw e;
         } 
    }
    
    @Override
    public String updCredencial(SiElementoBean user, String codUsuario){
        String vReturn = "NO_OK";
         try {
             String pass3 = Utility.getInstancia().cifrar(user.getNuClave(), ConstantesSec.SGD_SECRET_KEY_PASSWORD);
             user.setCeleDescor(pass3);
             //System.out.println("passFONDE==>" + pass3);
             vReturn = credencialUserDao.updCredencial(user, codUsuario);
         } catch (Exception e){
             e.printStackTrace();
         }
         return vReturn;
    }
    
    @Override
    public String insCredencial(SiElementoBean user, String codUsuario){
        String vReturn = "NO_OK";
         try {
             String pass3 = Utility.getInstancia().cifrar(user.getNuClave(), ConstantesSec.SGD_SECRET_KEY_PASSWORD);
             user.setCeleDescor(pass3);
             vReturn = credencialUserDao.insCredencial(user, codUsuario);
         } catch (Exception e){
             e.printStackTrace();
         }
         return vReturn;
    }
    
    @Override
     public SiElementoBean getCredencial(){
         SiElementoBean user = null;
         try{
            user =  credencialUserDao.getCredencial();
         } catch(Exception e){
             e.printStackTrace();
         }
         
         return user;
     }
     
     @Override
     public List<SiElementoBean> getUsuario(String usuario){
          List<SiElementoBean> list = null;
          try{
              list = credencialUserDao.getUsuario(usuario);
          } catch (Exception  e){
              e.printStackTrace();
          }
          
          return list;
     }
     
     @Override
      public List<SiElementoBean> getCredencialIni(){
          List<SiElementoBean> list = null;
          try{
              list = credencialUserDao.getCredencialIni();
          } catch (Exception  e){
              e.printStackTrace();
          }
          
          return list;
     }
     
     @Override 
     public String insCredencialUser(SiElementoBean userCredencial, String codUsuario){
          String vReturn = "NO_OK";
          try{
              String pass4 = Utility.getInstancia().cifrar(userCredencial.getNuClave(), ConstantesSec.SGD_SECRET_KEY_PASSWORD);
              userCredencial.setCeleDescor(pass4);
              vReturn = credencialUserDao.insCredencialUser(userCredencial, codUsuario);
          } catch(Exception e){
              e.printStackTrace();
          }
          return vReturn;
     }
     
     @Override
     public String updCredencialUser(SiElementoBean userCredencial, String codUsuario){
         String vReturn = "NO_OK";
         try {
             vReturn = credencialUserDao.updCredencialUser(userCredencial,codUsuario);
         } catch (Exception e){
             e.printStackTrace();
         }
         return vReturn;
     }
     
//    @Override
//     public String insCredencial(Usuario user, String codUsuario){
//         String vReturn = "NO_OK";
//         try {
//             
//         } catch (Exception e){
//             e.printStackTrace();
//         }
//         return null;
//     }
}

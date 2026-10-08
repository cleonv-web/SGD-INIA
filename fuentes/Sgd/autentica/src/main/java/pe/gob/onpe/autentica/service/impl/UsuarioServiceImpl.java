/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.autentica.service.impl;

import pe.gob.onpe.autentica.dao.UsuarioDao;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;
import pe.gob.onpe.autentica.service.UsuarioService;
import pe.gob.onpe.libreria.util.Mensaje;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import pe.gob.onpe.autentica.ldap.ADAuthenticator;
import pe.gob.onpe.autentica.model.DatosUsuario;
import pe.gob.onpe.autentica.model.DatosUsuarioLog;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;

public class UsuarioServiceImpl implements UsuarioService{

    private UsuarioDao usuarioDao;

    @Override
    public void autenticarUsuario(Mensaje msg, Usuario usuario, String coAplicativo, boolean isEncripted,String secureLdap,String domain, String host,String Port, String dn,String ADMinutosRestableceIntento) {
        String inApp="OK";
        try {
            String h="";
            usuarioDao.getRptaIdentificacion(msg, usuario, isEncripted);
            if(msg.getCoRespuesta().equals("2829")){ //Validacion por Active Directory

                    ADAuthenticator adAuthenticator = new ADAuthenticator(secureLdap,domain,host,Port,dn);

                    Map<String,Object> attrs = adAuthenticator.authenticate(usuario.getCoUsuario(),usuario.getDePassword());
                  
                    
                    if (attrs != null) {
                          
                        for (String attrKey : attrs.keySet()) {
                            h=attrs.get(attrKey).toString();
                            if(h.equals("775")){
                             msg.setCoRespuesta("005");
                             msg.setDeRespuesta("Su usuario se encuentra bloqueado, espere "+ADMinutosRestableceIntento+" minutos para intentar nuevamente o contáctese con el administrador de red");      
                            }
                            else{
                            
                            if (attrs.get(attrKey) instanceof String) {
                                msg.setCoRespuesta("00");
                                msg.setDeRespuesta("Usuario Valido");
                                usuario.setEsUsuario("A");
                               // usuarioDao.desbloquearUsuario(usuario.getCoUsuario());
                            } else {
                                msg.setCoRespuesta("005");
                                msg.setDeRespuesta("El Usuario o la Contraseña es incorrecta");       
                              
                               //  usuarioDao.incrementarNumeroIntentos(usuario.getCoUsuario());
                            }
                            }
                        }
                        
                        
                    } else {
                                   msg.setCoRespuesta("005");
                                msg.setDeRespuesta("El Usuario o la Contraseña es incorrecta");      
                      
                        
                    }
                    
 
            }
            
            if(msg.getCoRespuesta().equals("00")){
                usuarioDao.getRptaAplicativo(msg,usuario,coAplicativo);
                if(msg.getCoRespuesta().equals("00")){
                  usuarioDao.getParametrosGlobales(msg, usuario);  
                }
            }else if(msg.getCoRespuesta().equals("009")){//usuario Bloqueado
                inApp = this.desbloquearUsuario(usuario.getFullFechaActual(), usuario.getdFecMod(), usuario.getCoUsuario());
                if(inApp.equals("OK")){
                    this.autenticarUsuario(msg, usuario, coAplicativo, isEncripted,secureLdap,domain,host,Port,dn,ADMinutosRestableceIntento);
                }
            }
            
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void obtienePermisos(Mensaje msg, Usuario usuario, String coAplicativo) {
        try {
             List<UsuarioAcceso> list = usuarioDao.getPermisosUsuario(usuario, coAplicativo);    
        	//Seteando los accesos
             if(list!=null) {
                usuario.setUsuarioAccesos(list);
             }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void buildDefaultAdminAccess(Usuario usuario, String name, String coLocal, String DeLocal){
        usuario.setDePrenombres(name);

        UsuarioAcceso usuarioAcceso = new UsuarioAcceso();
        usuarioAcceso.setCoModulo("00");
        usuarioAcceso.setCoOpcion("00");
        usuarioAcceso.setCoSubopcion("00000000");

        List<UsuarioAcceso> accesoList = new ArrayList<UsuarioAcceso>();
        accesoList.add(usuarioAcceso);
        usuario.setUsuarioAccesos(accesoList);
    }

    @Override
    
    public void cerrarSession(Usuario usuario, String coAplicativo) {
        try {
            usuarioDao.cerrarSesionAplicativo(usuario,coAplicativo);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void cambiarClave(Mensaje msg, Usuario usuario, boolean isEncripted) {
        if(msg==null){
            msg = new Mensaje();
        }
        // Verificamos El usuario que cambia la contraseña
        try {
            usuarioDao.getRptaIdentificacion(msg, usuario, isEncripted);
            if(msg.getCoRespuesta().equals("00")){
                if (usuario.getDePasswordNuevo()!=null/* && !usuario.getDePassword().equals(usuario.getDePasswordNuevo())*/){
                    if (checkClaveFuerte(usuario.getDePasswordNuevo())){
                            try{
                                usuario.setDePasswordNuevo(Utility.getInstancia().cifrar(usuario.getDePasswordNuevo(),ConstantesSec.SGD_SECRET_KEY_PASSWORD));
                                if(verificarClaveDiferente(usuario.getCoUsuario(), usuario.getDePasswordNuevo())){
                                    usuario.setEsUsuario("A");
                                    usuarioDao.getModificaClave(msg, usuario);
                                    if(msg.getCoRespuesta().equals("00")){
                                        DatosUsuarioLog datos=new DatosUsuarioLog();
                                        datos.setNuCorr(usuarioDao.getNroSgtCorrHistorialClave(usuario.getCoUsuario()));
                                        datos.setCodUser(usuario.getCoUsuario());
                                        datos.setvClave(usuario.getDePasswordNuevo());
                                        datos.setCoEmp(usuario.getCempCodemp());
                                        datos.setDeIppc(usuario.getIpPC());
                                        datos.setDeNamepc(usuario.getNombrePC());
                                        datos.setDeUserpc(usuario.getUsuPc());
                                        usuarioDao.insHistorialClave(datos);
                                    }                                    
                                }else{
                                    msg.setCoRespuesta("005");
                                    msg.setDeRespuesta("La contraseña tiene que ser diferente a las 3 últimas.");                                              
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }                            
                    }else{
                        msg.setCoRespuesta("005");
                        msg.setDeRespuesta("La Contraseña no cumple con las Políticas de Seguridad.");                                
                    }
                }else{
                    msg.setCoRespuesta("5003");
                    msg.setDeRespuesta("La nueva contraseña debe ser diferente a la antigua.");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            msg.setCoRespuesta("5010");
            msg.setDeRespuesta("Error General");
        }
        
        //Validamos Datos de la nueva Contraseña
        
    }

    @Override
    public void desencripta(Mensaje msg, Usuario usuario){
        usuarioDao.desencripta(msg, usuario);
    }

//    Setters and Getters
    @Override
    public void setUsuarioDao(UsuarioDao usuarioDao) {
        this.usuarioDao = usuarioDao;
    }

    @Override
    public String encriptaDato(Mensaje msg, String pdato){
        return usuarioDao.encriptaDato(msg, pdato);

    }

    public DatosUsuario getDepUsuario(String coUsuario){
        DatosUsuario depUsuario = null;    
        try {
            depUsuario = usuarioDao.getDepUsuario(coUsuario);    
        	//Seteando los accesos
             if(depUsuario!=null && depUsuario.getCoDep()==null && depUsuario.getCempCodemp()==null) {
                depUsuario = null;
             }
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return depUsuario;
    }

    @Override
    public boolean checkClaveFuerte(String pass) {
        boolean passFuerte = false;
        try {
            if(pass!=null&&pass.trim().length()>7&&Utility.validarContraseniaFuerte(pass)){
                passFuerte = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return passFuerte;
    }    

    /**
     * 
     * @param diasExpira
     * @param feModClave
     * @param feActual
     * @param nroDiasAnteShowExpiraClave
     * @return "EXP" si expiro, "NE" no expira, >0 dias proximos a expirar.
     */
    @Override
    public String verificarClaveExpiro(int diasExpira,Date feModClave, Date feActual, int nroDiasAnteShowExpiraClave) {
        String expiro="EXP";
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
        try {
            Date fechaExpira= new Date(feModClave.getTime()+diasExpira*MILLSECS_PER_DAY);
            if(fechaExpira.after(feActual)){
                long diff_en_ms=fechaExpira.getTime()-feActual.getTime();
                long dias = diff_en_ms / MILLSECS_PER_DAY;
                if(dias<=nroDiasAnteShowExpiraClave){
                    expiro=String.valueOf(dias);                                        
                }else{
                    expiro="NE";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expiro;
    }
    
    @Override
    public int getNroDiasAntesExpiraClave(int diasExpira,Date feModClave, Date feActual){
        int dias=0;
        final long MILLSECS_PER_DAY = 24 * 60 * 60 * 1000;
        try {
            Date fechaExpira= new Date(feModClave.getTime()+diasExpira*MILLSECS_PER_DAY);
            if(fechaExpira.after(feActual)){
                long diff_en_ms=fechaExpira.getTime()-feActual.getTime();
                long lDias = diff_en_ms / MILLSECS_PER_DAY;
                dias=(int)lDias;
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dias;
    }
    
    @Override
    public boolean verificarClaveDiferente(String coUser,String pass){
        boolean diff=true;
        List<DatosUsuarioLog> list=null;
        try {
            list=usuarioDao.getHistorialClave(coUser);
            for (DatosUsuarioLog dato : list) {
                if(dato.getvClave().equals(pass)){
                    diff=false;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return diff;
    }
    
    @Override
    public String registraLogAcceso(Usuario usuario, String success){
        String vReturn = "NO_OK";
        DatosUsuarioLog dataUser= new DatosUsuarioLog();
        dataUser.setCodUser(usuario.getCoUsuario());
        dataUser.setCoEmp(usuario.getCempCodemp());
        dataUser.setDeIppc(usuario.getIpPC());
        dataUser.setDeNamepc(usuario.getNombrePC());
        dataUser.setDeUserpc(usuario.getUsuPc());
        dataUser.setSuccess(success);        
        try {
            vReturn=usuarioDao.insAccesoLog(dataUser);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    private String desbloquearUsuario(Date fecActual, Date fecMod, String coUsuario){
        String vResult = "NO_OK";
        try {
            System.out.println("desbloquearUsuario============>");
            if(fecMod==null){
                System.out.println("fecMod==>null");
            }
            else {
                System.out.println("fecMod==>"+fecMod.toString());
            }
            Date dBloqueo= new Date(fecMod.getTime()+15*60*1000);// 15 minutos de bloqueo
            if(fecActual.after(dBloqueo)){
                vResult = usuarioDao.desbloquearUsuario(coUsuario);
            }            
        } catch (Exception e) {
           e.printStackTrace();
        }
        return vResult;
    }
    
      public String cambiarEstadoUsuario(String coEmpleado,String estado,String coUsuario){
        String vResult = "NO_OK";
        try {             
                vResult = usuarioDao.cambiarEstadoUsuario(coEmpleado,estado,coUsuario);                      
        } catch (Exception e) {
           e.printStackTrace();
        }
        return vResult;
    }
       
    
    @Override
    public String resetPassword(String coUse, String clave, String coUseMod){
        String vResult = "NO_OK";
        try {        
            clave = Utility.getInstancia().cifrar(clave,ConstantesSec.SGD_SECRET_KEY_PASSWORD);
            vResult = usuarioDao.resetPassword(coUse, clave, coUseMod);
        } catch (Exception e) {
           e.printStackTrace();
        }
        return vResult;
    }
    
    @Override
    public String delUser(String coUse){
        String vResult = "NO_OK";
        try {        
            vResult = usuarioDao.delUserAplicaOpc(coUse);
            if(vResult.equals("OK")){
                vResult = usuarioDao.delUserAplica(coUse);
                if(vResult.equals("OK")){
                    vResult = usuarioDao.delUser(coUse);
                    if(vResult.equals("OK")){
                        vResult = usuarioDao.delClaveUserHis(coUse);
                    }
                }
            }
        } catch (Exception e) {
           e.printStackTrace();
        }
        return vResult;
    }    
}




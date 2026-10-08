/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.PdfPKCS7;
import com.lowagie.text.pdf.PdfReader;
import java.security.Provider;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.service.ValidarFirmaService;

/**
 *
 * @author WCutipa
 */
@Service("validarFirmaService")
public class ValidarFirmaServiceImp implements ValidarFirmaService {

    @Override
    public String getValidaCertificadoEmp(String filePdf, String pNuDni, String pNuRucInstitucion) {
        //agregado para soporte de SHA256
        this.setBouncyCastleProvider();
        String vret = "NO_OK";
        PdfReader reader = null;
        try {
            reader = new PdfReader(filePdf);
            AcroFields af = reader.getAcroFields();
            //ArrayList<String> names = af.getSignatureNames();
            List<String> names = af.getSignatureNames();
            if(!names.isEmpty()){
                ArrayList<PdfPKCS7> LsSignature = new ArrayList<PdfPKCS7>();
                for (String name : names) {
                    //LsSignature.add(af.verifySignature(name));
                    LsSignature.add(af.verifySignature(name, "BC"));//agregado para soporte de SHA256
                }
                Collections.sort(LsSignature, new Comparator<PdfPKCS7>(){
                    @Override
                    public int compare(PdfPKCS7 o1, PdfPKCS7 o2){
                        if(o1.getSignDate() == null || o2.getSignDate() == null)
                            return 0;
                        return o1.getSignDate().compareTo(o2.getSignDate()) * -1;
                    }
                });
                for(PdfPKCS7 pk : LsSignature){
                    String dn = PdfPKCS7.getSubjectFields(pk.getSigningCertificate()).toString();
                    //YUAL
                    /*String sn = getValByAttributeTypeFromIssuerDN(dn, "SN");
                    String dni = getValByAttributeTypeFromIssuerSN(sn, "DNI");
                    dni=dni!=null&&dni.trim().length()>8?dni.substring(0, 8):dni;
                    String ruc = getValByAttributeTypeFromIssuerSN(sn, "RUC");

                    if (pNuDni.equals(dni)) {
                        if (ruc != null && ruc.trim().length() > 0) {
                            if (pNuRucInstitucion.equals(ruc)) {
                                vret = "OK";
                            }
                        } else {
                            vret = "OK";
                        }
                    }*/
                    if(dn.contains(pNuDni)){
                        //vret = "OK";
                        if(dn.contains(pNuRucInstitucion)){
                            vret = "OK";
                            break;
                        }
                    }
                    
                    
                    
                }
            }else{
                vret="";//documento sin firmar
            }
        } catch (Exception e) {
              e.printStackTrace();
        }finally{
            if(reader!=null){
                reader.close();                
            }
        }
        return vret;
    }

    @Override
    public String getValidaCertificadoEmp(String filePdf, String pNuDni) {
        //agregado para soporte de SHA256
        this.setBouncyCastleProvider();        
        String vret = "NO_OK";
        PdfReader reader = null;
        try {
            reader = new PdfReader(filePdf);
            AcroFields af = reader.getAcroFields();
            List<String> names = af.getSignatureNames();
            if(!names.isEmpty()){
                ArrayList<PdfPKCS7> LsSignature = new ArrayList<PdfPKCS7>();
                for (String name : names) {
                    //LsSignature.add(af.verifySignature(name));
                    LsSignature.add(af.verifySignature(name, "BC"));//agregado para soporte de SHA256
                }
                Collections.sort(LsSignature, new Comparator<PdfPKCS7>(){
                    @Override
                    public int compare(PdfPKCS7 o1, PdfPKCS7 o2){
                        if(o1.getSignDate() == null || o2.getSignDate() == null)
                            return 0;
                        return o1.getSignDate().compareTo(o2.getSignDate()) * -1;
                    }
                });                
                for(PdfPKCS7 pk : LsSignature){
                    String dn = PdfPKCS7.getSubjectFields(pk.getSigningCertificate()).toString();

                    //yual
                    /*String sn = getValByAttributeTypeFromIssuerDN(dn, "SN");
                    String dni = getValByAttributeTypeFromIssuerSN(sn, "DNI");
                    dni=dni!=null&&dni.trim().length()>8?dni.substring(0, 8):dni;

                    if (pNuDni.equals(dni)) {
                        vret = "OK";
                    }*/
                    if(dn.contains(pNuDni)){
                        vret = "OK";
                         break;
                    }
                   
                }                
            }else{
                vret="";//documento sin firmar
            }
        } catch (Exception e) {
              e.printStackTrace();
        }finally{
            if(reader!=null){
                reader.close();                
            }
        }
        return vret;
    }
//    private String verificaDniRuc(String pNuDni, String pNuRucInstitucion, String dni, String ruc) {
//        String vret = "NO OK";
//        if (pNuDni.equals(dni) && pNuRucInstitucion.equals(ruc)) {
//            vret = "OK";
//        }
//        return vret;
//    }

//    private String verificaDni(String pNuDni, String dni) {
//        String vret = "NO OK";
//        if (pNuDni.equals(dni)) {
//            vret = "OK";
//        }
//        return vret;
//    }
//
    private String getValByAttributeTypeFromIssuerDN(String dn, String attributeType) {
        String[] dnSplits = dn.split(",");
        for (String dnSplit : dnSplits) {
            if (dnSplit.contains(attributeType)) {
                String[] cnSplits = dnSplit.trim().split("=");
                if (cnSplits[1] != null) {
                    return cnSplits[1].trim();
                }
            }
        }
        return "";
    }

    private String getValByAttributeTypeFromIssuerSN(String sn, String attributeType) {
        String[] dnSplits = sn.split("-");
        for (String dnSplit : dnSplits) {
            if (dnSplit.contains(attributeType)) {
                String[] cnSplits = dnSplit.trim().split(":");
                if (cnSplits[1] != null) {
                    return cnSplits[1].trim();
                }
            }
        }
        return "";
    }
    
    private void setBouncyCastleProvider(){
        Provider provider = null;
            try {
                Class c =
                Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
                java.security.Security.insertProviderAt((java.security.Provider)c.newInstance(), 2000);
                //provider = "BC";
                provider = (Provider)c.newInstance();
                
            } catch(Exception e) {
                provider = null;
                   // provider is not available }
            }          
    }
}

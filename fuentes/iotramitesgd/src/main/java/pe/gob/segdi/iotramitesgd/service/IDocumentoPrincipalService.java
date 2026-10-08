package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.IODocumentoPrincipal;


public interface IDocumentoPrincipalService {

    IODocumentoPrincipal getDocumentoPrincipal(String vcuo) throws Exception;


}

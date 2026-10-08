package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.IODocumentoPrincipal;


public interface IDocumentoPrincipalDao {

    public int insDocumentoPrincipal(IODocumentoPrincipal documentoPrincipal) throws Exception;

    IODocumentoPrincipal getDocumentoPrincipal(String vcuo) throws Exception;


}

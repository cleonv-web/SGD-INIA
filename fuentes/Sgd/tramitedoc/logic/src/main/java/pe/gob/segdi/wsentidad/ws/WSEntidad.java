package pe.gob.segdi.wsentidad.ws;

import java.net.URL;
import java.util.List;

import javax.xml.ws.BindingProvider;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;





public class WSEntidad {
    ApplicationProperties pro=new ApplicationProperties();
	public static void main(String[] args)  {
		
	}
	
	public List<EntidadBean> getEntidad(int scodcat,String Url) throws Exception{
            List<EntidadBean> lstentidad = null;
            PcmIMgdEntidad service = new  PcmIMgdEntidad(new URL(Url));
            PcmIMgdEntidadPortType entidad = service.getPcmIMgdEntidadHttpsSoap11Endpoint();
            BindingProvider bindingProvider = (BindingProvider) entidad;
	    bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, Url);
	    lstentidad = entidad.getListaEntidad(scodcat);
            return lstentidad;
	}

}

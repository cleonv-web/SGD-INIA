package pe.gob.segdi.pide.consultas.ws;



import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import pe.gob.onpe.tramitedoc.util.Constantes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
 
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;


//import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.service.DatosPlantillaService;
import pe.gob.segdi.pide.consultas.bean.ConsultaDniBean;

public class WSConsultaDni {



        
public ConsultaDniBean consultaDni(String Url,String nuDniConsulta,String nuDniUsuario,String rucEntidad,String password)
        throws SAXException, IOException {
    

            ConsultaDniBean resultado=new ConsultaDniBean();
            
            String resource = Url+"nuDniConsulta="+nuDniConsulta
                    +"&nuDniUsuario="+nuDniUsuario
                    +"&nuRucUsuario="+rucEntidad
                    +"&password="+password;
            
                  
            
            try {
			
			// create HTTP Client
			HttpClient httpClient = HttpClientBuilder.create().build();
 
			// Create new getRequest with below mentioned URL
			HttpGet getRequest = new HttpGet(resource);
 
			// Add additional header to getRequest which accepts application/xml data
			getRequest.addHeader("accept", "application/xml");
 
			// Execute your request and catch response
			HttpResponse response = httpClient.execute(getRequest);
 
			// Check for HTTP response code: 200 = success
			if (response.getStatusLine().getStatusCode() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
			}
 
			// Get-Capture Complete application/xml body response
			BufferedReader br = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
			String output;
//			System.out.println("============Output:============");
                        String Cadena="";
			// Simply iterate through XML response and show on console.
			while ((output = br.readLine()) != null) {
				Cadena+=output;
			}
//                        System.out.println(Cadena);
                        
                        
                        DOMParser parser = new DOMParser();
                        
                        parser.parse(new InputSource(new java.io.StringReader(Cadena)));


                        Document doc = parser.getDocument();
                        NodeList nodeLst = doc.getElementsByTagName("datosPersona");

                        for (int i = 0; i<nodeLst.getLength(); i++) {
                            Element eleProducto = (Element) nodeLst.item(i);


                            NodeList nlsApPrimer = eleProducto.getElementsByTagName("apPrimer");
                            Element eleAprimer = (Element) nlsApPrimer.item(0);
                            resultado.setApPrimer(eleAprimer.getFirstChild().getNodeValue()); 


                            NodeList nlsApSegundo = eleProducto.getElementsByTagName("apSegundo");
                            Element eleApSegundo = (Element) nlsApSegundo.item(0);
                            resultado.setApSegundo(eleApSegundo.getFirstChild().getNodeValue());


                            NodeList nlsPrenombres = eleProducto.getElementsByTagName("prenombres");
                            Element elePrenombres = (Element) nlsPrenombres.item(0);
                            resultado.setPrenombres(elePrenombres.getFirstChild().getNodeValue());


                            /*NodeList nlsMarca = eleProducto.getElementsByTagName("estadoCivil");
                            Element eleMarca = (Element) nlsMarca.item(0);
                            String strMarca = eleMarca.getFirstChild().getNodeValue();*/
                            
                        }                
                  
                        
		} catch (ClientProtocolException e) {
			e.printStackTrace();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
                catch (SAXException e) {
			e.printStackTrace();
		}
            
            return resultado;
        }
        


}


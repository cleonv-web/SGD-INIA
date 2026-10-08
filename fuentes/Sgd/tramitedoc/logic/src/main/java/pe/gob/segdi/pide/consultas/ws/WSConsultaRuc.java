package pe.gob.segdi.pide.consultas.ws;



import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import pe.gob.onpe.tramitedoc.util.Constantes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
 
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;


//import java.io.IOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.segdi.pide.consultas.bean.ConsultaSunatBean;

public class WSConsultaRuc extends SimpleJdbcDaoBase{

        
    public ConsultaSunatBean consultaSunat(String UrlSunat,String nuruc) throws SAXException, IOException
         {
             
            ConsultaSunatBean resultado=new ConsultaSunatBean();

               String resource = UrlSunat+"numruc="+nuruc;
                        
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

                        String Cadena="";

			while ((output = br.readLine()) != null) {
				Cadena+=output;
			}
//                        System.out.println(Cadena);
                        
                        
                        DOMParser parser = new DOMParser();
                        
                        parser.parse(new InputSource(new java.io.StringReader(Cadena)));


                        Document doc = parser.getDocument();
                        NodeList nodeLst = doc.getElementsByTagName("multiRef");

                        for (int i = 0; i<nodeLst.getLength(); i++) {
                            Element eleSunat = (Element) nodeLst.item(i);

                            NodeList nlsDdp_numruc = eleSunat.getElementsByTagName("ddp_numruc");
                            Element eleDdp_numruc = (Element) nlsDdp_numruc.item(0);
                            resultado.setDdp_numruc(eleDdp_numruc.getFirstChild().getNodeValue()); 


                            NodeList nlsDdp_nombre = eleSunat.getElementsByTagName("ddp_nombre");
                            Element eleDdp_nombre = (Element) nlsDdp_nombre.item(0);
                            resultado.setDdp_nombre(eleDdp_nombre.getFirstChild().getNodeValue().trim());


                            NodeList nlsDesc_tipvia = eleSunat.getElementsByTagName("desc_tipvia");
                            Element eleDesc_tipvia = (Element) nlsDesc_tipvia.item(0);
                            try
                            { resultado.setDesc_tipvia(eleDesc_tipvia.getFirstChild().getNodeValue());
                            }
                            catch(Exception e){resultado.setDesc_tipvia("");}   
                            


                            NodeList nlsDdp_nomvia = eleSunat.getElementsByTagName("ddp_nomvia");
                            Element eleDdp_nomvia = (Element) nlsDdp_nomvia.item(0);
                            resultado.setDdp_nomvia(eleDdp_nomvia.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_numer1 = eleSunat.getElementsByTagName("ddp_numer1");
                            Element eleDdp_numer1 = (Element) nlsDdp_numer1.item(0);
                            resultado.setDdp_numer1(eleDdp_numer1.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_nomzon = eleSunat.getElementsByTagName("ddp_nomzon");
                            Element eleDdp_nomzon = (Element) nlsDdp_nomzon.item(0);
                            resultado.setDdp_nomzon(eleDdp_nomzon.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_ubigeo = eleSunat.getElementsByTagName("ddp_ubigeo");
                            Element eleDdp_ubigeo = (Element) nlsDdp_ubigeo.item(0);
                            resultado.setDdp_ubigeo(eleDdp_ubigeo.getFirstChild().getNodeValue());                            
                            
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

public ArrayList listarSunat(String UrlSunat,String pRazonSocial){
        ArrayList<ProveedorBean> lista = new ArrayList<ProveedorBean>();
        try{

              ConsultaSunatBean resultado=new ConsultaSunatBean();
//            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
//            this.applicationProperties = (ApplicationProperties) applicationContext.getBean("applicationProperties");
//            String resource = applicationProperties.getUrl_sunat_rest()+ "?numruc="+nuruc;
               String resource = UrlSunat+"RSocial="+pRazonSocial.trim().replace(" ", "%20");
                        
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
                        NodeList nodeLst = doc.getElementsByTagName("multiRef");

                        for (int i = 0; i<nodeLst.getLength(); i++) {
                            Element eleSunat = (Element) nodeLst.item(i);
//            System.out.println(proveedor.getDdp_numruc());
//            System.out.println(proveedor.getDdp_nombre().trim());
//            System.out.println(proveedor.getDesc_tipvia().trim());
//            System.out.println(proveedor.getDdp_nomvia().trim());
//            System.out.println(proveedor.getDdp_numer1().trim());
//            System.out.println(proveedor.getDdp_nomzon().trim());
//            System.out.println(proveedor.getDdp_ubigeo());
                            
                            

                            NodeList nlsDdp_numruc = eleSunat.getElementsByTagName("ddp_numruc");
                            Element eleDdp_numruc = (Element) nlsDdp_numruc.item(0);
//                            resultado.setDdp_numruc(eleDdp_numruc.getFirstChild().getNodeValue()); 


                            NodeList nlsDdp_nombre = eleSunat.getElementsByTagName("ddp_nombre");
                            Element eleDdp_nombre = (Element) nlsDdp_nombre.item(0);
                            
                            

                            lista.add(new ProveedorBean(eleDdp_numruc.getFirstChild().getNodeValue(),
                            eleDdp_nombre.getFirstChild().getNodeValue().trim() ));
                            

                          /*  NodeList nlsDesc_tipvia = eleSunat.getElementsByTagName("desc_tipvia");
                            Element eleDesc_tipvia = (Element) nlsDesc_tipvia.item(0);
                            resultado.setDesc_tipvia(eleDesc_tipvia.getFirstChild().getNodeValue());


                            NodeList nlsDdp_nomvia = eleSunat.getElementsByTagName("ddp_nomvia");
                            Element eleDdp_nomvia = (Element) nlsDdp_nomvia.item(0);
                            resultado.setDdp_nomvia(eleDdp_nomvia.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_numer1 = eleSunat.getElementsByTagName("ddp_numer1");
                            Element eleDdp_numer1 = (Element) nlsDdp_numer1.item(0);
                            resultado.setDdp_numer1(eleDdp_numer1.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_nomzon = eleSunat.getElementsByTagName("ddp_nomzon");
                            Element eleDdp_nomzon = (Element) nlsDdp_nomzon.item(0);
                            resultado.setDdp_nomzon(eleDdp_nomzon.getFirstChild().getNodeValue());
                            
                            NodeList nlsDdp_ubigeo = eleSunat.getElementsByTagName("ddp_ubigeo");
                            Element eleDdp_ubigeo = (Element) nlsDdp_ubigeo.item(0);
                            resultado.setDdp_ubigeo(eleDdp_ubigeo.getFirstChild().getNodeValue());     */                       
                            
                        }                
                  
                        
		} catch (ClientProtocolException e) {
			e.printStackTrace();
 
		} catch (IOException e) {
			e.printStackTrace();
		}
                catch (SAXException e) {
			e.printStackTrace();
		}
            

        }catch (Exception e){
//            System.out.print("Error Lista Cliente " + e.getMessage());
        }
 
    return lista;
}
        
//    public List<ConsultaSunatBean> consultaSunat(String nuruc) throws SAXException, IOException
//         {
//             
//            List<ConsultaSunatBean> resultado=new ArrayList<ConsultaSunatBean>();
//            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
//            this.applicationProperties = (ApplicationProperties) applicationContext.getBean("applicationProperties");
////            String resource = applicationProperties.getUrl_sunat_rest()+ "?numruc="+nuruc;
//               String resource = "https://ws3.pide.gob.pe/Rest/Sunat/DatosPrincipales?numruc="+nuruc;
//                        
//            try {
//			
//			// create HTTP Client
//			HttpClient httpClient = HttpClientBuilder.create().build();
// 
//			// Create new getRequest with below mentioned URL
//			HttpGet getRequest = new HttpGet(resource);
// 
//			// Add additional header to getRequest which accepts application/xml data
//			getRequest.addHeader("accept", "application/xml");
// 
//			// Execute your request and catch response
//			HttpResponse response = httpClient.execute(getRequest);
// 
//			// Check for HTTP response code: 200 = success
//			if (response.getStatusLine().getStatusCode() != 200) {
//				throw new RuntimeException("Failed : HTTP error code : " + response.getStatusLine().getStatusCode());
//			}
// 
//			// Get-Capture Complete application/xml body response
//			BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));
//			String output;
////			System.out.println("============Output:============");
//                        String Cadena="";
//			// Simply iterate through XML response and show on console.
//			while ((output = br.readLine()) != null) {
//				Cadena+=output;
//			}
////                        System.out.println(Cadena);
//                        
//                        
//                        DOMParser parser = new DOMParser();
//                        
//                        parser.parse(new InputSource(new java.io.StringReader(Cadena)));
//
//
//                        Document doc = parser.getDocument();
//                        NodeList nodeLst = doc.getElementsByTagName("multiRef");
//
//                        for (int i = 0; i<nodeLst.getLength(); i++) {
//                            Element eleSunat = (Element) nodeLst.item(i);
////            System.out.println(proveedor.getDdp_numruc());
////            System.out.println(proveedor.getDdp_nombre().trim());
////            System.out.println(proveedor.getDesc_tipvia().trim());
////            System.out.println(proveedor.getDdp_nomvia().trim());
////            System.out.println(proveedor.getDdp_numer1().trim());
////            System.out.println(proveedor.getDdp_nomzon().trim());
////            System.out.println(proveedor.getDdp_ubigeo());
//                            
//                            
//
//                            NodeList nlsDdp_numruc = eleSunat.getElementsByTagName("ddp_numruc");
//                            Element eleDdp_numruc = (Element) nlsDdp_numruc.item(0);
//                            resultado.setDdp_numruc(eleDdp_numruc.getFirstChild().getNodeValue()); 
//
//
//                            NodeList nlsDdp_nombre = eleSunat.getElementsByTagName("ddp_nombre");
//                            Element eleDdp_nombre = (Element) nlsDdp_nombre.item(0);
//                            resultado.setDdp_nombre(eleDdp_nombre.getFirstChild().getNodeValue());
//
//
//                            NodeList nlsDesc_tipvia = eleSunat.getElementsByTagName("desc_tipvia");
//                            Element eleDesc_tipvia = (Element) nlsDesc_tipvia.item(0);
//                            resultado.setDesc_tipvia(eleDesc_tipvia.getFirstChild().getNodeValue());
//
//
//                            NodeList nlsDdp_nomvia = eleSunat.getElementsByTagName("ddp_nomvia");
//                            Element eleDdp_nomvia = (Element) nlsDdp_nomvia.item(0);
//                            resultado.setDdp_nomvia(eleDdp_nomvia.getFirstChild().getNodeValue());
//                            
//                            NodeList nlsDdp_numer1 = eleSunat.getElementsByTagName("ddp_numer1");
//                            Element eleDdp_numer1 = (Element) nlsDdp_numer1.item(0);
//                            resultado.setDdp_numer1(eleDdp_numer1.getFirstChild().getNodeValue());
//                            
//                            NodeList nlsDdp_nomzon = eleSunat.getElementsByTagName("ddp_nomzon");
//                            Element eleDdp_nomzon = (Element) nlsDdp_nomzon.item(0);
//                            resultado.setDdp_nomzon(eleDdp_nomzon.getFirstChild().getNodeValue());
//                            
//                            NodeList nlsDdp_ubigeo = eleSunat.getElementsByTagName("ddp_ubigeo");
//                            Element eleDdp_ubigeo = (Element) nlsDdp_ubigeo.item(0);
//                            resultado.setDdp_ubigeo(eleDdp_ubigeo.getFirstChild().getNodeValue());                            
//                            
//                        }                
//                  
//                        
//		} catch (ClientProtocolException e) {
//			e.printStackTrace();
// 
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//                catch (SAXException e) {
//			e.printStackTrace();
//		}
//
//            
//            return resultado;
//        }
//        
//

}


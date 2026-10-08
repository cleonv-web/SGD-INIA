/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.util;

/*import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;*/
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.apache.log4j.Logger;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;

/**
 *
 * @author FSilva
 */
public class RestUtility {
    
    private static Logger logger = Logger.getLogger(RestUtility.class);
    private static boolean instanciated = false;
    private static RestUtility instancia;

    private RestUtility(){
    }

    public static RestUtility getInstancia(){
        if(!RestUtility.instanciated){
            RestUtility.instancia = new RestUtility();
            RestUtility.instanciated = true;
        }
        return RestUtility.instancia;
    }
    public String restAsignarFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean, String urlRest) {
        /*String output = "";
        //DataOutputStream wr = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        try {
            //URL url = new URL("http://localhost:8080/sgdws/rest/documentoSeguimientoRoot");
            URL url = new URL(urlRest+"/asignaFuncionario");
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            //Reemplazar esto por el valor de la imagen en Base 64.pnuAnn, pnuEmi, coDep, coEmpVb

            String pData = "{\"coAsignacion\":\"" + asignacionFuncionarioBean.getCoAsignacionFuncionario() + "\","
                    + "\"coUseCre\":\"" + asignacionFuncionarioBean.getCoUseCre() + "\"}";

            os = conn.getOutputStream();
            os.write(pData.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP CODIGO ERROR : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            output = br.readLine();
            conn.disconnect();

        } catch (MalformedURLException e) {
            logger.error("Error en la URL del SGD Rest", e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Error en input", e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return output;*/
        
        
        
        String output = "";        
        try {
            urlRest = urlRest+"/asignaFuncionario";
            String pData = "{\"coAsignacion\":\"" + asignacionFuncionarioBean.getCoAsignacionFuncionario() + "\","
                    + "\"coUseCre\":\"" + asignacionFuncionarioBean.getCoUseCre() + "\"}";            
            output=this.restService(pData, urlRest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return output;
    }
    
    public String restSendMailVistoBueno(String nuAnn, String nuEmi, String coUser, String urlRest) {
        String output = "";        
      /*  try {
            urlRest = urlRest+"/enviarMailVoBo";// envio de correo mediante el servicio de tareas rest
            String pData = "{\"nuAnn\":\"" + nuAnn + "\","
                    + "\"nuEmi\":\"" + nuEmi + "\"," + "\"coUseCre\":\"" + coUser + "\"}";            
            output=this.restService(pData, urlRest);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return output;
    }    
    
    public String restService(String dataOutput, String urlRest) {
        String output = "";
        //DataOutputStream wr = null;
        HttpURLConnection conn = null;
        OutputStream os = null;
        try {
            URL url = new URL(urlRest);
            conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");

            os = conn.getOutputStream();
            os.write(dataOutput.getBytes());
            os.flush();

            if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new RuntimeException("HTTP CODIGO ERROR : " + conn.getResponseCode());
            }
            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            output = br.readLine();
            conn.disconnect();

        } catch (MalformedURLException e) {
            logger.error("Error en la URL service rest: " + urlRest, e);
            e.printStackTrace();
        } catch (IOException e) {
            logger.error("Error en input", e);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.disconnect();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        return output;
    }    
    
/*public String restAsignarFuncionarioJersey(AsignacionFuncionarioBean asignacionFuncionarioBean, String urlRest){
        String output="";
        try {

            Client client = Client.create();

            WebResource webResource = client
                    .resource(urlRest+"/asignaFuncionario");
            String input = "{\"coAsignacion\":\"" + asignacionFuncionarioBean.getCoAsignacionFuncionario() + "\","
                    + "\"coUseCre\":\"" + asignacionFuncionarioBean.getCoUseCre() + "\"}";

            ClientResponse response = webResource.type("application/json")
                    .post(ClientResponse.class, input);

            if (response.getStatus() != 201) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + response.getStatus());
            }

            System.out.println("Output from Server .... \n");
            output = response.getEntity(String.class);
            System.out.println(output);

        } catch (Exception e) {

            e.printStackTrace();

        }
        return output;
    }*/
    
}

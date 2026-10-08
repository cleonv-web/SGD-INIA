package pe.gob.segdi.wsiotramite.ws;

import java.util.Base64;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.xml.bind.annotation.XmlElement;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import pe.gob.segdi.wsiotramite.bean.CargoTramite;
import pe.gob.segdi.wsiotramite.bean.DocumentoExterno;
import pe.gob.segdi.wsiotramite.bean.DocumentoPrincipal;
import pe.gob.segdi.wsiotramite.bean.EmisionExterna;
import pe.gob.segdi.wsiotramite.bean.RecepcionExterna;
import pe.gob.segdi.wsiotramite.bean.RecepcionTramite;
import pe.gob.segdi.wsiotramite.bean.RespuestaCargoTramite;
import pe.gob.segdi.wsiotramite.bean.RespuestaConsultaTramite;
import pe.gob.segdi.wsiotramite.bean.RespuestaRecepcionTramite;
import pe.gob.segdi.wsiotramite.service.IEmisionExternaService;
import pe.gob.segdi.wsiotramite.service.IRecepcionExternaService;
import pe.gob.segdi.wsiotramite.util.Utilitarios;
/**
 * 
 * Objeto : Tramite.java.
 * Descripción : Clase que publica los servicios a ser consumidos por la PIDE.
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 * 
 */


@WebService(serviceName = "Tramite")
public class Tramite {
	private static Logger depurador = Logger.getLogger(Tramite.class.getName());
	BeanFactory beanFactory;

		
	/**
     * Recibe los datos del documento enviado, a través, de la PIDE.
     * @param request Objeto que contiene los datos del documento, tipo RecepcionTramite.
     * @return response Objeto que contiene datos de rerspuesta, tipo RespuestaRecepcionTramite.
     */
	@WebMethod
	public RespuestaRecepcionTramite recepcionarTramiteResponse(@WebParam(name = "recepcionRequest") RecepcionTramite request) {
		depurador.info("recepcionarTramiteResponse ==>"+request.getVrucentrem());
		RespuestaRecepcionTramite response = null;
		try {
			response = new RespuestaRecepcionTramite();
			
			RecepcionExterna recepcionExterna = new RecepcionExterna();
			recepcionExterna.setVrucentrem(request.getVrucentrem());
			recepcionExterna.setVrucentrec(request.getVrucentrec());
			recepcionExterna.setVcuo(request.getVcuo());
			recepcionExterna.setVcuoref(request.getVcuoref());
			recepcionExterna.setVuniorgrem(request.getVuniorgrem());
			recepcionExterna.setCtipdociderem(request.getCtipdociderem());
			recepcionExterna.setVnumdociderem(request.getVnumdociderem());
			
			DocumentoExterno documentoExterno = new DocumentoExterno();
			documentoExterno.setVnomentemi(request.getVnomentemi());
			documentoExterno.setCcodtipdoc(request.getCcodtipdoc());
			documentoExterno.setVnumdoc(request.getVnumdoc());
			java.sql.Date sqlDate = new java.sql.Date(request.getDfecdoc().getTime());
			documentoExterno.setDfecdoc(sqlDate);
			documentoExterno.setVuniorgdst(request.getVuniorgdst());
			documentoExterno.setVnomdst(request.getVnomdst());
			documentoExterno.setVnomcardst(request.getVnomcardst());
			documentoExterno.setVasu(request.getVasu());
			documentoExterno.setSnumanx(request.getSnumanx());
			documentoExterno.setSnumfol(request.getSnumfol());
			documentoExterno.setVurldocanx(request.getVurldocanx());
						
			DocumentoPrincipal documentoPrincipal = new DocumentoPrincipal();
			documentoPrincipal.setVnomdoc(request.getVnomdoc());
			byte[] decodedBytes = Base64.getDecoder().decode(request.getBpdfdoc());
			documentoPrincipal.setBpdfdoc(decodedBytes);
			
			documentoExterno.setDocumentoPrincipal(documentoPrincipal);
			documentoExterno.setLstDocAnexo(request.getLstanexos());
			recepcionExterna.setDocumentoExterno(documentoExterno);
						
			getRecepcionServiceBean().insRecepcionExterna(recepcionExterna);
			response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_RESPUESTA_TRAMITE"));	
			response.setVdesres("El documento N° CUO "+request.getVcuo()+" se encuentra a disposición para la recepción formal de la entidad destinataria "+Utilitarios.ObtenerDatosProperties("MessageResources", "NOMBRE_ENTIDAD")+" en los horarios de atención de su Mesa de Partes.");	
			

		} catch (Exception e) {
			response.setVcodres("-1");
			depurador.error(null,e);
		}
		return response;
	}
	
	/**
     * Devuelve los datos del cargo del documento enviado, a través, de la PIDE.
     * @param request Objeto que contiene los datos de consulta para la obtencion del cargo del documento, tipo CargoTramite.
     * @return response Objeto que contiene los datos del cargo del documento, tipo RespuestaCargoTramite.
     */
	@WebMethod
	public RespuestaCargoTramite cargoResponse(@XmlElement(required=true)  @WebParam(name = "cargoRequest") CargoTramite request) {
		depurador.info("cargoResponse 01 ==>"+request.getVcuo());
		depurador.info("getVanioregstd ==>"+request.getVanioregstd());
		depurador.info("getVnumregstd ==>"+request.getVnumregstd());
		depurador.info("getVobs ==>"+request.getVobs());
		depurador.info("getVdesanxstdrec ==>"+request.getVdesanxstdrec());
		depurador.info("getDfecregstd ==>"+request.getDfecregstd());
		depurador.info("getVuniorgstd ==>"+request.getVuniorgstd());
		depurador.info("getVusuregstd ==>"+request.getVusuregstd());
		
		RespuestaCargoTramite response = new RespuestaCargoTramite();
		try {
			
			EmisionExterna emisionExterna = new EmisionExterna();
			emisionExterna.setVcuo(request.getVcuo());
			emisionExterna.setVnumregstdrec(request.getVnumregstd());
			emisionExterna.setVanioregstdrec(request.getVanioregstd());
			emisionExterna.setVuniorgstdrec(request.getVuniorgstd());
			emisionExterna.setDfecregstdrec(request.getDfecregstd());
			
			byte[] decodedBytes = Base64.getDecoder().decode(request.getBcarstd());
			emisionExterna.setBcarstdrec(decodedBytes);
			emisionExterna.setVobs(request.getVobs());
			emisionExterna.setVusuregstdrec(request.getVusuregstd());
			emisionExterna.setCflgest(request.getCflgest());
			
			int flagUpd = getEmisionExternaServiceBean().updCargoEmisionExterna(emisionExterna);
			
			if(flagUpd == 0){
				response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_ERROR_TRAMITE"));	
				response.setVdesres(Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_CARGO_TRAMITE"));
			}else if(flagUpd == 1){
				response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_RESPUESTA_TRAMITE"));	
				response.setVdesres(Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_CARGO_TRAMITE2"));
			}else if(flagUpd == 2){
				response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_RESPUESTA_TRAMITE3"));	
				response.setVdesres(Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_CARGO_TRAMITE3"));
			}
			
		} catch (Exception e) {
			response.setVcodres("-1");
			depurador.error(null,e);
		}
		return response;
	}
	
	/**
     * Consulta del estado del documento enviado, a través, de la PIDE.
     * @param vcuo Código único de operación, para la obtencion del esta del documento, tipo String.
     * @return response Objeto que contiene los datos de respuesta del estado del documento, tipo RespuestaConsultaTramite.
     */
	@WebMethod
	public RespuestaConsultaTramite consultarTramiteResponse(@XmlElement(required=true)  @WebParam(name = "request") String vcuo) {
		depurador.info("consultarTramiteResponse ==>"+vcuo);
		RespuestaConsultaTramite response = null;
		try {
			response = getRecepcionServiceBean().consultarRegistroTramite(vcuo);
			
			if(response.getVcodres().equals("0000")){
				if(response.getCflgest().equals("R") || response.getCflgest().equals("O")) {
					if(response.getBcarstd() != null && response.getBcarstd().length > 0) {
						response.setBcarstd(Base64.getEncoder().encode(response.getBcarstd()));
					}
				}
				response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_RESPUESTA_CONSULTA"));
				response.setVdesres(Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_CONSULTA_TRAMITE"));
			}else if(response.getVcodres().equals("0001")){
				response = new RespuestaConsultaTramite();
				response.setVcodres(Utilitarios.ObtenerDatosProperties("MessageResources", "CODIGO_RESPUESTA_TRAMITE2"));
				response.setVdesres(Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_CONSULTA_TRAMITE2"));
			}
			
			
		} catch (Exception e) {
			response.setVcodres("-1");
			depurador.error(null,e);
		}
		return response;
		
	}
	
	@WebMethod(exclude=true)
	public IEmisionExternaService getEmisionExternaServiceBean(){
		try {
			beanFactory = new ClassPathXmlApplicationContext("/applicationContext.xml");
		} catch (Exception e) {
			depurador.error(null,e);
		}
		return (IEmisionExternaService) beanFactory.getBean("iEmisionExternaService");
	}
	
	
	@WebMethod(exclude=true)
	public IRecepcionExternaService getRecepcionServiceBean(){
		try {
			beanFactory = new ClassPathXmlApplicationContext("applicationContext.xml");
		} catch (Exception e) {
			depurador.error(null,e);
		}
		return (IRecepcionExternaService) beanFactory.getBean("iRecepcionExternaService");
	}
	
	public static void main(String[] args)  {
		
		 
	}
}

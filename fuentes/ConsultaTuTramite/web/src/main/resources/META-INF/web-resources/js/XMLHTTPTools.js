    function XMLHTTPTools_DoGet(pisRemotePage){
      var oXMLHTTP;
      var bIsVentanaBase, oWindow, iHuboCambio, sLastHref;
      var sResponse;
      
      window.status = "Procesando ...";
    

      oXMLHTTP = crearXMLHttpRequest();      
      oXMLHTTP.open("GET", pisRemotePage, true); 
      oXMLHTTP.send(null);
      
      oXMLHTTP.onreadystatechange = function() {
      if (oXMLHTTP.readyState == 4) 
      {
              window.status = "Listo"
              if (oXMLHTTP.status == 200) {
                sResponse = oXMLHTTP.responseText              
                return sResponse
              }else{
      
                return "error";      
              }
          }

      }
                
    }
    
    var XML_AJAX;
    var oXMLHTTP;
    var objXML;
    function crearObjetoXML(archivoXML){
        var xmlDoc;

         
       if(window.ActiveXObject){

          xmlDoc = new ActiveXObject("Microsoft.XMLDOM");

          xmlDoc.async = false;
          xmlDoc.loadXML(archivoXML);          
       }else if(document.implementation && document.implementation.createDocument){
          xmlDoc = document.implementation.createDocument("","",null);
          xmlDoc.load(archivoXML);
        
       }else{
          alert ('Su navegador no puede soportar este script');
       }
       return xmlDoc;
    }
    TIPORESPONSE = "xml";
    
    function XMLHTTPTools_DoPost(){

      var bIsVentanaBase, oWindow, iHuboCambio, sLastHref;
      var sResponse;              
      var xmlDoc;

      pisModo = true;
      
      if(arguments.length < 3 ){
       alert("Error al recibir parametros.", function(){ return; });
        
      }
      
      pisParam      = arguments[0];
        pisRemotePage = pRutaContexto+"/"+pAppVersion+"/"+arguments[1];

      pisFuncion       = arguments[2];
      
      if (arguments.length > 3){
        pisModo = arguments[3]
        
        if(pisModo.length) 
           { pisModo = true;}
        
      }

       if (arguments.length > 4){
        TIPORESPONSE = arguments[4];
      }
      document.getElementById("loadding").style.display="";
      window.status = "Procesando ...";

      oXMLHTTP = crearXMLHttpRequest();    
      if (pisModo){
        oXMLHTTP.onreadystatechange = validar;
      }

      oXMLHTTP.open('POST',pisRemotePage, pisModo);       
      oXMLHTTP.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded;charset=UTF-8');
      oXMLHTTP.send(pisParam);
      
      if (!pisModo) {
        window.status = "Listo";
        document.getElementById("loadding").style.display="none";
        if(TIPORESPONSE=="xml"){
          sResponse = oXMLHTTP.responseXML;
            if (!isXML(sResponse)){
                sResponse = oXMLHTTP.responseText;
            }
        }
        else if(TIPORESPONSE=="text"){
         sResponse = oXMLHTTP.responseText;}
      }
      return sResponse;
    }
    
    function validar() {
      if (oXMLHTTP.readyState == 4) {
         document.getElementById("loadding").style.display="none";
         window.status = "Listo";
          if (oXMLHTTP.status == 200) {
             if (TIPORESPONSE=="xml") {
                sResponse = oXMLHTTP.responseXML;
                if (!isXML(sResponse)){
                    sResponse = oXMLHTTP.responseText;
                }

             } else if (TIPORESPONSE=="text") {
                sResponse = oXMLHTTP.responseText;                                        
             }
                      
             XML_AJAX = sResponse;
             eval(pisFuncion); 
          } else {
           
             return "error";
          }
      } else {
          window.status = "Listo";
          document.getElementById("loadding").style.display="none";
      }
    }


function XMLTools_GetAttribute( pioXMLDomDocument,pisPath,pisAttributeName){	
    oElement = pioXMLDomDocument.documentElement.selectSingleNode(pisPath);
    return oElement.getAttribute(pisAttributeName);
}


function XMLTools_GetSingleNodeValue( pioXMLDomDocument, pisPath ){            
    return pioXMLDomDocument.documentElement.selectSingleNode(pisPath).text;
}	

function obtenerParametros() {
    var form = document.forms[0]; 
    var parametros = '';
    for (i = 0; i < form.elements.length; i++) {
        if ('text' == form.elements[i].type) {
            parametros += form.elements[i].name + '=' + escape(form.elements[i].value.toUpperCase()) + '&';
        }
        else if ('select-one' == form.elements[i].type) { 
            var j = form.elements[i].selectedIndex;
            if (j >= 0) {
                parametros += form.elements[i].name + '=' + escape(form.elements[i].options[j].value) + '&';
            }
        }
        else if ('textarea' == form.elements[i].type) {            
                parametros += form.elements[i].name + '=' + escape(form.elements[i].value) + '&';            
        }
        else if ('hidden' == form.elements[i].type) {
            parametros += form.elements[i].name + '=' + escape(form.elements[i].value.toUpperCase()) + '&';
        }
        else if ('checkbox' == form.elements[i].type && form.elements[i].checked ) {
            parametros += form.elements[i].name + '=' + escape(form.elements[i].value.toUpperCase()) + '&';
        }
        else if ('file' == form.elements[i].type) {
            parametros += form.elements[i].name + '=' + escape(form.elements[i].value.toUpperCase()) + '&';
        }
    }
    return parametros;
}

function isXMLError(docXml){
    var objXml, res;            
    if (docXml.documentElement) {
       objXml = docXml;
    } else {
       objXml = crearObjetoXML(docXml);
    }

    if (objXml.documentElement.selectSingleNode("/doc/res")){
       res = XMLTools_GetSingleNodeValue(objXml,"/doc/res");
       if (res == "ERROR") {
         alert("Ha ocurrido un error al procesar su solicitud. Por favor intente de nuevo, \n si el error persiste comuniquese con su administrador.", function(){  return true; });
            
       } else if (res == "LOGOUT") {
         alert("Por su seguridad su sesión ha terminado debe entrar de nuevo al sistema.", function(){  return true; });
         
       } else if (res == "MENSAJE") {
         alert(XMLTools_GetSingleNodeValue(objXml,"/doc/out/param_out/mensaje"), function(){  return true; });
          
       } else {
          return false;
       }}
    }
        
function isResTRUE(docXml) {
    var objXml, res;
    if (docXml.documentElement) {
       objXml = docXml;
    } else {
       objXml = crearObjetoXML(docXml);
    }
    if (objXml.documentElement.selectSingleNode("/doc/res")){
      res = XMLTools_GetSingleNodeValue(objXml,"/doc/res");}
    if (res == "TRUE"){
      return true;    }
    else{
      return false;}
}

    function isXML(data) {
        var res=false;
        var objXml;
        if (data!=null){
            objXml = data.documentElement;
            if(objXml!=null)
            {
                res = true
            }
        }
        return res;
    }

/* global nroPestana */

var _wsocket;
var wsRemite = '/BROWSER';
var wsDestino = 'APPCLIENT';
var appOnpeResponse=null;
var nrOperacion=null;


var accionOnDesktopTramiteDoc = {
    ejecutaFirma: 1,
    abrirDocumento: 2,
    abrirDocumentoPC: 3,
    cargarDocumento: 4,
    generaDocumento: 5,
    verificaSiExisteDoc: 6,
    getDirectorio: 7,
    abrirRutaDocs: 8,
    verificarRutaDirectorio: 9
};

var tipoEjecucionAppDesktop = {
    value: 0,
    noEjecutarAppDesktop: 0,
    ejeAppDesktopModoNormal: 1
};

function getIdChannelWS(){
    return Math.round(Math.random() * 0x1000000);
}

function onMessageReceivedWS(evt, callback) {
    var msg = JSON.parse(evt.data); // native API
    processMessageRecived(msg, callback);
}

function processMessageRecived(msg, callback){
    if(idChannel!==$.cookie('idChannel')){
        try{cerrarConexion_wsocket();}catch(ex){}
    }    
   switch (msg.accion) 
        {
            case "TERMINATE_APP":
                var parm = "{\"retval\": \"OK\"}";
                sendMessageWS_CONTINUE_APP(JSON.stringify(parm),"CONTINUE_APP");
                break;
            case "CONEXION":
                var datosPC = JSON.parse(msg.message);
                if(!!datosPC && msg.nrOperacion===nrOperacion.toString()){
                    callback(datosPC);
                }                
                break;
            case "SELECCIONAR_DIRECTORIO":
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;
            case "VER_DOCUMENTO":  
                var auxMsg = msg;
                if(!!auxMsg && msg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="1"){
                        alert(auxMsg.message);
                    }
                }
                break;        
            case "ABRIR_DOCUMENTO_PC":  
                var auxMsg = msg;
                if(!!auxMsg && msg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert_Danger(" ", auxMsg.message);
                    }
                }
                break;                
            case "CARGAR_DOCUMENTO":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    /*if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert_Danger("Repositorio: ", auxMsg.message);
                        //alert(auxMsg.message);
                    }*/
                    callback(auxMsg);
                }
                break;    
            case "GENERAR_DOCUMENTO":
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;                
            case "VERIFICAR_EXISTE_DOC":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    if(auxMsg.error==="0"){
                        callback(auxMsg.message);
                    }else{
                        alert(auxMsg.message);
                    }
                }
                break;             
            case "EJECUTAR_FIRMA":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    /*if(auxMsg.error==="1"){
                        alert(auxMsg.message);
                    }*/
                    callback(auxMsg);
                }
                break;                
            case "VERIFICAR_DIRECTORIO":  
                var auxMsg = msg;
                if(!!auxMsg && auxMsg.nrOperacion===nrOperacion.toString()){
                    callback(auxMsg);
                }
                break;                
            default:
                alert(msg);
                break;
        }   
}

function sendMessageWS_CONTINUE_APP(msg, accion){
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"'+accion+'" ,"nrOperacion":"'+nrOperacion+'"}';    
//    console.log(msg);
    _wsocket.send(msg);
}

function sendMessageWS(msg, accion){
    nrOperacion = Math.round(Math.random() * 0x1000000);    
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"'+accion+'" ,"nrOperacion":"'+nrOperacion+'"}';    
    console.log('sendMessageWS: '+msg);
    _wsocket.send(msg);
}

function wsVerificarConexion(){
    sendMessageWS(null,"CONEXION");
}

function connectToChatDesktop(){
//    _wsocket = new WebSocket(wsServiceLocation + idChannel +wsRemite);
    _wsocket = new WebSocket(wsServiceLocation + $.cookie('idChannel') +wsRemite);
    _wsocket.onmessage = onMessageReceivedWS;
}

function connectToChatDesktopCallback(callback){
    _wsocket = new WebSocket(wsServiceLocation + $.cookie('idChannel') +wsRemite);
    _wsocket.onmessage = onMessageReceivedWS;
    var auxTime = 0;
    var waitToResponseWS = function() {
        auxTime = auxTime + 5;
        var clearTime=auxTime>1000*15/*(15 segundos)*/;
        if (_wsocket.readyState===_wsocket.OPEN) {
            clearInterval(time);
            callback(false);
        }else if(clearTime){
            clearInterval(time);
        }        
    };
    var time = setInterval(waitToResponseWS, 5);         
}

function runOnDesktop(pAccionOnDesktop, param, callback) {
    var auxEjecucion = tipoEjecucionAppDesktop.value;
    if (auxEjecucion === tipoEjecucionAppDesktop.ejeAppDesktopModoNormal) {
        if(idChannel!==$.cookie('idChannel')){
            try{cerrarConexion_wsocket();}catch(ex){}
            try{
                connectToChatDesktopCallback(function() {
                    fn_runOnDesktop(pAccionOnDesktop, param, function(data){
                        callback(data);
                    }); 
                });
            }catch(ex){}            
            idChannel=$.cookie('idChannel');
        }else{
            fn_runOnDesktop(pAccionOnDesktop, param, function(data){
                callback(data);
            });
        }
    }else{
        switch (pAccionOnDesktop)
        {
            case 2:
                var rpta = fn_abrirPdf(param.urlDoc, param.rutaDoc);
                callback(rpta);
                break;
            case 6:
                //verificar si existe
                var rpta = "No se puede Ejecutar Instale Tramite Documentario";
                alert_Danger("!Error : ", rpta);                
                callback("NO");
                break;                
            default:
                var rpta = "No se puede Ejecutar Instale Tramite Documentario";
                alert_Danger("!Error : ", rpta);
                callback(false);            
        }        
    }    
}

function fn_runOnDesktop(pAccionOnDesktop, param, callback){
        switch (pAccionOnDesktop)
        {
            case 1:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };
                fn_firmarDocDesktop(param.urlDoc, param.rutaDoc, param.tipoFirma);
                break;
            case 2:
                fn_abrirDocDesktop(param.urlDoc, param.rutaDoc);
                callback(false);
                break;
            case 3:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_abrirDocFromPCDesktop(param.rutaDoc);
                break;
            case 4:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_cargarDocumentoDesktop(param.urlDoc, param.rutaDoc);
                break;
            case 5:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                 
                fn_generaDocumentoDesktop(param.urlDoc, param.rutaDoc,param.remplazaArchivo);
                break;
            case 6:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                
                fn_verificaSiExisteDocDesktop(param.rutaDoc);
                break;
            case 7:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                  
                fn_selecDirectorioDesktop();
                break;
            case 8:
                fn_abrirRutaDocsDesktop();
                break;
            case 9:
                _wsocket.onmessage = function (evt){
                    onMessageReceivedWS(evt, callback);
                };                
                fn_verificarRutaDirectorio(param.rutaDir);
                break;
            default:
        }    
}

function fn_abrirDocDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": "+JSON.stringify(purlDoc)+", \"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        //console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"VER_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en abrir el documento");
        //console.log(ex.message);
    }
} 

function fn_cargarDocumentoDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": "+JSON.stringify(purlDoc)+", \"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"CARGAR_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en cargar el documento");
        //console.log(ex.message);
    }
} 

function fn_verificaSiExisteDocDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": "+JSON.stringify(prutaDoc)+",\"verBloqueo\": false}";
    try {
        //console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"VERIFICAR_EXISTE_DOC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
        //console.log(ex.message);
    }
} 
function fn_abrirDocFromPCDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": "+JSON.stringify(prutaDoc)+"}";
    try {
        //console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"ABRIR_DOCUMENTO_PC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
        //console.log(ex.message);
    }
}

function fn_cargarAppOnpePrincipal(){
    appOnpeConexion(function(data) {
            desktopCargadoWS(data);
    });    
}

function appOnpeConexion(callback){
    _wsocket.onmessage = function (evt){
        onMessageReceivedWS(evt, callback);
    };
    appOnpeResponse = "";
    var auxTime = 0;
    var waitToResponseApp = function() {
        auxTime = auxTime + 250;
        var clearTime=auxTime<=1000*30/*(30 segundos)*/;
        if (!!!appOnpeResponse&&clearTime) {
            if(_wsocket.readyState===_wsocket.OPEN){
                wsVerificarConexion();                
            }
            //console.log("esperando");
            //console.log(auxTime);
            //Aqui se puede poner un loading.
        } else {
            clearInterval(time);
            if (!!!appOnpeResponse){
                try{_wsocket!==null?_wsocket.close():'';}catch(ex){}
            }
            //aqui se puede terminar el loading.
        }
    };
    var time = setInterval(waitToResponseApp, 250);         
}

function fn_abrirRutaDocsDesktop(){
    try {
        sendMessageWS(null,"VER_RUTA_PRINCIPAL");
    } catch (ex) {
        alert("Fallo en ruta.");
    }
}

function fn_selecDirectorioDesktop(){
    try {
        sendMessageWS(null,"SELECCIONAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en Seleccionar directorio.");
    }
}

function fn_generaDocumentoDesktop(urlDoc, rutaDoc, remplazaArchivo) {
    try {
        if (!!!remplazaArchivo) {
            remplazaArchivo = false;
        }
        var param = "{\"urlDoc\": "+JSON.stringify(urlDoc)+", \"rutaDoc\": "+JSON.stringify(rutaDoc)+", \"remplazaArchivo\": "+remplazaArchivo+"}";        
        //console.log(JSON.stringify(param));
        sendMessageWS(JSON.stringify(param),"GENERAR_DOCUMENTO");        
    } catch (ex) {
        alert("Fallo en generar el documento");
    }
}

function cerrarConexion_wsocket(){
    sendMessageWS(null,"TERMINATE_APP");
    _wsocket.close();
}

function fn_firmarDocDesktop(urlDoc, rutaDoc, tipoFirma) {
    var param = "{\"urlDoc\": "+JSON.stringify(urlDoc)+", \"rutaDoc\": "+JSON.stringify(rutaDoc)+", \"tipoFirma\": "+JSON.stringify(tipoFirma)+"}";
    try {
        console.log(JSON.stringify(param));
        console.log('JSON.stringify(param)');
        sendMessageWS(JSON.stringify(param),"EJECUTAR_FIRMA");
    } catch (ex) {
        alert("Fallo en abrir el documento");
        //console.log(ex.message);
    }    
}

function desktopCargadoWS(datosPC) {
    //alert(datosPC);

    ajaxCall("/config.do?accion=goDatosPC", datosPC,function(data) {
            if(!!data && data.retval){
                appletCargadoRutaDoc(decodeURI(datosPC.rutaPrincipal));
                $("#appletIco span").attr("class", "glyphicon glyphicon-ok-circle").css({color: '#5CB85C'});
                appOnpeResponse="OK";
                tipoEjecucionAppDesktop.value = tipoEjecucionAppDesktop.ejeAppDesktopModoNormal;
            }
    }, 'json', true, true, 'POST');  
}

/*
function fn_generaDocDesktop(urlDoc, rutaDoc,nuAnn, nuEmi, tieneWord, callback) {
    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
        retval = data;
        var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};
        
        if (retval === "SI") {
            //alert("El documento ya fue creado, se procedera a visualizar.");
            bootbox.alert("<h5>El documento ya fue creado, se procedera a visualizar.</h5>", function() {
                bootbox.hideAll();
                fn_genDocDesktop(param, function(data){
                    callback(data);
                });
            });               
        }else{
            
            if (tieneWord==="SI") {
                
                    var pAbreWord = "&pAbreWord="+tieneWord;
                    var p = new Array();
                    p[0] = "accion=goDocRutaAbrirEmiReporte";        
                    p[1] = "nuAnn=" + nuAnn;
                    p[2] = "nuEmi=" + nuEmi;
                    p[3] = "tiOpe=" + "5";
                    p[4] = "pAbreWord=" + tieneWord;
                    
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {

                        eval("var docs=" + data);

                        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
               
                            if (docs.retval === "OK") {

                                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc, remplazaArchivo: true};
                                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                                    retval = data;
                                });

                            } else {

                                alert_Danger("!Repositorio : ", docs.retval);
                            }
                        }

                    }, 'text', false, false, "POST");                    
                    
                    
                    
                    
            }
            else
            {
                fn_genDocDesktop(param, function(data){
                    callback(data);
                    });   
            }         
        }
    });    
}
*/


function fn_generaDocDesktop(urlDoc, rutaDoc,nuAnn, nuEmi, tieneWord, callback) {
    
       if (tieneWord==="SI") {
                
                    var pAbreWord = "&pAbreWord="+tieneWord;
                    var p = new Array();
                    p[0] = "accion=goDocRutaAbrirEmiReporte";        
                    p[1] = "nuAnn=" + nuAnn;
                    p[2] = "nuEmi=" + nuEmi;
                    p[3] = "tiOpe=" + "5";
                    p[4] = "pAbreWord=" + tieneWord;
                    
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {

                        eval("var docs=" + data);

                        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
               
                            if (docs.retval === "OK") {

                                var param={urlDoc:docs.noUrl+pAbreWord,rutaDoc:docs.noDoc, remplazaArchivo: true};
                                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                                    retval = data;
                                });

                            } else {

                                alert_Danger("!Repositorio : ", docs.retval);
                            }
                        }

                    }, 'text', false, false, "POST");                    
                    
                    
                    
                    
            }
            else
            {
 
                var retval = "";
                var param = {rutaDoc: rutaDoc, verBloqueo: false};
                runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                    retval = data;
                    var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};

                    if (retval === "SI") {
                        //alert("El documento ya fue creado, se procedera a visualizar.");
                        bootbox.alert("<h5>El documento ya fue creado, se procedera a visualizar.</h5>", function() {
                            bootbox.hideAll();
                            fn_genDocDesktop(param, function(data){
                                callback(data);
                            });
                        });               
                    }else{
                    //alert_Danger("!Repositorio : ", docs.retval);
                    fn_genDocDesktop(param, function(data){
                    callback(data);
                    });   
                    
                    }
                }); 
                
             
            }   
   
}

function fn_generaDocDesktopOnly(urlDoc, rutaDoc,  callback) {      
                var retval = "";
                var param = {rutaDoc: rutaDoc, verBloqueo: false};
                runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                    retval = data;
                    var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};

                    if (retval === "SI") {
                        //alert("El documento ya fue creado, se procedera a visualizar.");
                        bootbox.alert("<h5>El documento ya fue creado, se procedera a visualizar.</h5>", function() {
                            bootbox.hideAll();
                            fn_genDocDesktop(param, function(data){
                                callback(data);
                            });
                        });               
                    }else{
                    //alert_Danger("!Repositorio : ", docs.retval);
                    fn_genDocDesktop(param, function(data){
                    callback(data);
                    });   
                    
                    }
                }); 
                 
}


function fn_generaDocDesktopInter(urlDoc, rutaDoc, nuAnn, nuEmi, tieneWord, callback) {
    
    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
        retval = data;
        var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};
        if (retval === "SI") {
            //alert("El documento ya fue creado, se procedera a visualizar.");
            bootbox.alert("<h5>El documento ya fue creado, se procedera a visualizar.</h5>", function() {
                bootbox.hideAll();
                fn_genDocDesktop(param, function(data){
                    callback(data);
                });
            });               
        }else{
            
            if (tieneWord==="SI") {
                
                    var pAbreWord = "&pAbreWord="+tieneWord;
                    var p = new Array();
                    p[0] = "accion=goDocRutaAbrirEmiReporte";        
                    p[1] = "nuAnn=" + nuAnn;
                    p[2] = "nuEmi=" + nuEmi;
                    p[3] = "tiOpe=" + "5";
                    p[4] = "pAbreWord=" + tieneWord;
                    
                    ajaxCall("/srDocObjetoInter.do", p.join("&"), function(data) {

                        eval("var docs=" + data);

                        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
          
                            if (docs.retval === "OK") {

                                var param={urlDoc:docs.noUrl+pAbreWord,rutaDoc:docs.noDoc, remplazaArchivo: true};
                                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                                    retval = data;
                                });
                            } else {

                                alert_Danger("!Repositorio : ", docs.retval);
                            }
                        }

                    }, 'text', false, false, "POST");                    
                    
                    
                    
                    
            }
            else
            {
                fn_genDocDesktop(param, function(data){
                    callback(data);
                    });   
            }

        }
    });    
}

function fn_verificarRutaDirectorio(prutaDir) {
    var param = "{\"rutaDir\": "+JSON.stringify(prutaDir)+"}";
    try {
        sendMessageWS(JSON.stringify(param), "VERIFICAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en verificar ruta Directorio.");
    }
} 

function fn_genDocDesktop(param, callback){
    runOnDesktop(accionOnDesktopTramiteDoc.generaDocumento, param, function(data){    
        var retval = data;
        callback(retval);
    });    
}

function fn_cargaDocDesktop(urlDoc, rutaDoc, callback) {
    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){    
        retval = data;
        if (retval === "SI") {
            var param = {urlDoc: urlDoc, rutaDoc: rutaDoc};
            runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function(data){
                retval = data;
                if(retval.error==="0"){
                  callback(retval.message);  
                }else{
                    bootbox.alert("<h5>"+retval.message+rutaDoc+"</h5>", function() {
                        bootbox.hideAll();
                        callback("ERROR");            
                    });                    
                }
            });
        } else {
            bootbox.alert("<h5>El documento NO existe en: "+rutaDoc+"</h5>", function() {
                bootbox.hideAll();
                callback(retval);            
            });            
        }
    });
}

function fn_selecDirectorioOnDesktop(){
    var dir = "";
    runOnDesktop(accionOnDesktopTramiteDoc.getDirectorio ,null, function(data){
        dir=data;
        $("#txtDirPrincipal").attr("value", dir);        
    });  
}

//siempre al final
var isCallAppDesktop=false;
$(function() {
    var waitToResponseApp = function() {
        if (ispageLoad) {
            if(!isCallAppDesktop&&nroPestana===0){
                isCallAppDesktop=true;
                try{
		    //var url = 'OnpeTradoc:accion=TraDoc?ws='+wsServiceLocation + $.cookie('idChannel') + '/?urlBase='+urlBase+'?rutaPri='+rutaPri;
                    var url = 'Tramitedoc:accion=TraDoc?ws='+wsServiceLocation + $.cookie('idChannel') + '/?urlBase='+urlBase+'?rutaPri='+rutaPri;
                    console.log('urlTramitedoc: '+url);
                    var desktopObj = document.getElementById('idTradocDesktop');
                    desktopObj.href = url;
                    desktopObj.click();                    
                    desktopObj.href = "javascript:void(0);";
/*                    if(is_chrome()){
                        var desktopObj = document.createElement('a');
                        desktopObj.setAttribute('onclick','this.click();');
                        desktopObj.setAttribute('href',url);
                        desktopObj.setAttribute('target','_self');
                        desktopObj.click();
                    }else{    
                        var myventana=window.open(url,'_blank');
                        if(!!myventana){
                            myventana.close();                    
                        }*/
                        /*var desktopObj = document.createElement('a');
                        var url='javascript:window.open(\"'+url+'\").close();';
                        desktopObj.setAttribute('onclick',url);
                        desktopObj.click();*/
                        /*var form = document.createElement("form");
                        form.setAttribute("action", url);
                        form.setAttribute("method", "post");
                        form.setAttribute("target", "myVentanaUrl");
                        document.body.appendChild(form);
                        var myVentanaUrl = window.open("","myVentanaUrl");
                        if (!myVentanaUrl)
                            myVentanaUrl.close();
                        form.submit();  
                        document.body.removeChild(form);*/
                    //}
                }
                catch(ex){}
                /*var form = document.createElement("form");
                form.setAttribute("action", url);
                form.setAttribute("method", "post");
                form.setAttribute("target", "VentanaView");
                document.body.appendChild(form);
                var atributos = "toolbar=no,Location=no,directories=no,channelmode=no,menubar=no,status=yes,scrollbars=yes,resizable=yes,width=10,height=10,fullscreen=no,top=0,left=0";
                myWinAppDesktop = window.open("about:blank", "VentanaView", atributos);
                if (!myWinAppDesktop)
                    return false;
                form.submit();  
                document.body.removeChild(form);
                return false;*/

            }else{
                clearInterval(myTime);
                connectToChatDesktop();
                fn_cargarAppOnpePrincipal();
            }
        }
    };    
    var myTime = setInterval(waitToResponseApp, 500);  
});
ispageLoad = true;

var paramConfAnexosDetalle = {
    "bPaginate": false,
    "bFilter": false,
    "bSort": true,
    "bInfo": true,
    "bAutoWidth": false,
    "sScrollY": "140px",
    "bScrollCollapse": false,
    "oLanguage": {
        "sZeroRecords": "No se encuentran registros.",
        "sInfo": "Registros: _TOTAL_ ",
        "sInfoEmpty": ""
    },
    "aoColumns": [
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": false}
    ]
};
function fn_verDocumentoLista() {
        clearInterval($('#pIDAutoRefresh').val());
    
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var ptiOpe = "0";
    if (pnuAnn) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
}

function fn_firmarDocApplet(urlDoc, rutaDoc, tipoFirma) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        retval = appletObj[0].ejecutaFirma(urlDoc, rutaDoc, tipoFirma);
    } catch (ex) {
       bootbox.alert("Fallo en abrir el documento");
       return;
    }
    return retval;
}

function fn_abrirDocApplet(purlDoc, prutaDoc) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    var urlDoc = purlDoc;
    var rutaDoc = prutaDoc;
    try {
        retval = appletObj[0].abrirDocumento(urlDoc, rutaDoc);
    } catch (ex) {
       bootbox.alert("Fallo en abrir el documento");
       return;
        //console.log(ex.message);
    }
    return retval;
}

function fn_abrirDocFromPCApplet(rutaDoc) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        retval = appletObj[0].abrirDocumentoPC(rutaDoc);
    } catch (ex) {
       bootbox.alert("Fallo en abrir el documento");
       return;
        //console.log(ex.message);
    }
    return retval;
}

//JAZANERO
function fn_generaDocApplet(urlDoc, rutaDoc, nuAnn, nuEmi, tieneWord, callback) {
    /*var retval = "";
     var appletObj = jQuery('#firmarDocumento');
     try{
     retval=appletObj[0].verificaSiExisteDoc(rutaDoc,false);
     if (retval=="SI"){
    bootbox.alert("El documento ya fue creado, se procedera a visualizar.");
     }
     retval=appletObj[0].generaDocumento(urlDoc,rutaDoc,false);
     
     }catch(ex){
    bootbox.alert("Fallo en Generar el documento");
     }
     return retval;*/

    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data) {
        retval = data;
        if (retval === "SI") {
           bootbox.alert("El documento ya fue creado, se procedera a visualizar.",function(){
                var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};
                //runApplet(appletsTramiteDoc.generaDocumento, param, function(data) {
                runOnDesktop(accionOnDesktopTramiteDoc.generaDocumento, param, function(data) {
                    retval = data;
                    if (callback !== undefined) 
                        callback(retval);
                });
           });       
        }else{
            
            //console.log("Colocar logica para que indiar que habra desde Respositorio");
            //INICIO
            if (typeof (tieneWord) !== "undefined" && tieneWord !== "" && tieneWord==="SI") {
//                console.log("Existe un backup en BD, procedo a abrirlo");
                //LOGICA PARA ABRIR DESDE BD
                    var pAbreWord = "&pAbreWord="+tieneWord;
                    var p = new Array();
                    p[0] = "accion=goDocRutaAbrirEmiReporte";        
                    p[1] = "nuAnn=" + nuAnn;
                    p[2] = "nuEmi=" + nuEmi;
                    p[3] = "tiOpe=" + "5";
                    p[4] = "pAbreWord=" + tieneWord;
                    
//                    console.log("pnuEmi->"+nuEmi);
//                    console.log("ptiOpe->5");                    
//                    console.log("/srDocObjeto.do?accion=goDocRutaAbrirEmiReporte");
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        
                        var result;
                        eval("var docs=" + data);
//                        console.log("data->"+data);
                        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
//                            console.log("docs.retval->"+docs.retval);                
                            if (docs.retval === "OK") {
//                                console.log("urlDoc->"+docs.noUrl+pAbreWord);
//                                console.log("rutaDoc->"+docs.noDoc);
                                //result = fn_abrirDocApplet(docs.noUrl, docs.noDoc);
                                var param={urlDoc:docs.noUrl+pAbreWord,rutaDoc:docs.noDoc};
                                //runApplet(appletsTramiteDoc.abrirDocumento,param,function(data){
                                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param,function(data){
                                    //result=data;
                                    retval = data;
                                    if (callback !== undefined) 
                                        callback(retval);
                                });
                            } else {
//                                console.log("alert_Danger1");
                                //Error en Documento
                                alert_Danger("!Repositorio : ", docs.retval);
                            }
                        }

                    }, 'text', false, false, "POST");
                
                //FIN LOGICA
            }else{
                console.log("Abro appletsTramiteDoc.generaDocumento");
                console.log("urlDoc->"+urlDoc);
                console.log("rutaDoc->"+rutaDoc);
                console.log("remplazaArchivo->false");
                var param = {urlDoc: urlDoc, rutaDoc: rutaDoc, remplazaArchivo: false};
                    //runApplet(appletsTramiteDoc.generaDocumento, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.generaDocumento, param, function(data) {
                        retval = data;
                        if (callback !== undefined) 
                            callback(retval);
                    });
            }
            
            //FIN
            
        }
    });

}

function fn_verificaSiExisteDocApplet(rutaDoc) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        //verificaSiExisteDoc
        retval = appletObj[0].verificaSiExisteDoc(rutaDoc, false);
    } catch (ex) {
       bootbox.alert("Fallo en verificar si existe el documento");
       return;
    }
    return retval;
}
function fn_generaDocumentoApplet(urlDoc, rutaDoc, remplazaArchivo) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        if (!!!remplazaArchivo) {
            remplazaArchivo = false;
        }
        retval = appletObj[0].generaDocumento(urlDoc, rutaDoc, remplazaArchivo);
    } catch (ex) {
       bootbox.alert("Fallo en generar el documento");
       return;
    }
    return retval;
}

function fn_cargarDocumentoApplet(urlDoc, rutaDoc) {
    var retval = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        retval = appletObj[0].cargarDocumento(urlDoc, rutaDoc);
    } catch (ex) {
       bootbox.alert("Fallo en cargar el documento");
       return;
    }
    return retval;
}


function fn_abrirRutaDocsApplet() {
    var retval = "NO";
    var appletObj = jQuery('#firmarDocumento');
    try {
        retval = appletObj[0].abrirRutaDocs();
    } catch (ex) {
        retval = "NO";
    }
    return retval;
}


function fn_cargaDocApplet(urlDoc, rutaDoc, callback) {
//        var retval = "";
//        var appletObj = jQuery('#firmarDocumento');
//        try{
//            retval=appletObj[0].verificaSiExisteDoc(rutaDoc,false);
//            if (retval==="SI"){
//                retval=appletObj[0].cargarDocumento(urlDoc,rutaDoc);
//            }else{
//               bootbox.alert("El documento NO existe en: "+rutaDoc);
//            }
//        }catch(ex){
//            bootbox.alert("Fallo en cargar el documento");
//        }
//        return retval;


    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data) {
        retval = data;
        if (retval === "SI") {
            var param = {urlDoc: urlDoc, rutaDoc: rutaDoc};
            //runApplet(appletsTramiteDoc.cargarDocumento, param, function(data) {
            runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function(data) {
                callback(data);
            });
        } else {
           bootbox.alert("El documento NO existe en: " + rutaDoc,function(){
                callback(retval);
           });
           
        }
    });


}


function fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocRutaAbrir";
        //p[0] = "accion=goDocRutaAbrirEmiReporte";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        //
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            //var docs = JSON.parse(data);
            if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                if (docs.retval === "OK") {
                    //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                    var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                    //runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento, param, function(data) {
                        result = data;
                    });
                } else {
                    //Error en Documento
                    alert_Danger("!Repositorio : ", docs.retval);
                }
            }

        }, 'text', false, false, "POST");
    }
    return false;
}

//YUAL

function fn_verDocumentoInicialObj(pnuAnn, pnuEmi,pnuDes) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocInicialRutaAbrir";
        //p[0] = "accion=goDocRutaAbrirEmiReporte";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuDes=" + pnuDes;
        //
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            //var docs = JSON.parse(data);
            if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                if (docs.retval === "OK") {
                    //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                    var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                    //runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento, param, function(data) {
                        result = data;
                    });
                } else {
                    //Error en Documento
                    alert_Danger("!Repositorio : ", docs.retval);
                }
            }

        }, 'text', false, false, "POST");
    }
    return false;
}


function fn_selAccion() 
{

    var accion=jQuery('#ddlAccion').val();   
   

    if(accion === "0")
    {
        $('#divUrgencia').show(); 
        $('#divMensajeria').show(); 
    }
    /*if(accion === "1")
    {
        $('#divUrgencia').hide(); 
        $('#divMensajeria').hide(); 
    }*/
    if(accion === "1")
    {
        $('#divUrgencia').show(); 
        $('#divMensajeria').hide(); 
    }
  
}


function fn_grabarEnvioNotificacionObj(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo,tipoBandeja) {

    var nCodAccion = allTrim($("#ddlAccion").val());
    var nCodUrgencia = allTrim($("#ddlUrgencia").val());
    var nCodDepMsj=allTrim($("#sMensajeria").val());

    var p = new Array();
    //p[0] = "accion=goDocRutaAbrir";
    p[0] = "accion=goUpdEnvioNotificacion";
    p[1] = "nuAnn=" + pnuAnn;
    p[2] = "nuEmi=" + pnuEmi;
    p[3] = "nCodAccion=" + nCodAccion;
    p[4] = "nCodUrgencia=" + nCodUrgencia;
    p[5] = "nCodDepMsj=" + nCodDepMsj;

    //ajaxCallSendJson(url, jsonString, function(data) {
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {

        if (data.substring(0,1) === "1") {
            alert_Sucess("MENSAJE", "Se envió el documento a la mensajería correctamente");
            if(tipoBandeja==="EDIT"){
                editarDocumentoEmiClick(pnuAnn, pnuEmi,pexisteDoc,pexisteAnexo);           
            }
            if(tipoBandeja==="NEW"){
                EnvioOkMensajeria();
            }
            else {
                  ajaxCall("/srDocumentoAdmEmision.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
                        refreshScript("divTablaEmiDocumenAdm", data);
                    }, 'text', false, false, "POST");
           
           }
            removeDomId('windowConsultaAnexo');
            jQuery('#btn-buscar').click();


        } else {
            alert_Danger("ERROR:", data.substring(1,data.length));
        }
    }, 'text', false, false, "POST");    
      

                            
    return;
}

function fn_grabarArchivarDocumento(pnuAnn,pnuEmi) {

    
    var myForm='documentoEmiArBean';

    if(fn_valFormArchivarDoc(myForm)){
        var jsonBody =
                {
                    "nuAnn": pnuAnn, "nuEmi": pnuEmi,
                    "feEmi": $('#'+myForm).find("#itxtFecha").val(), "docObser": $('#'+myForm).find("#itxtObservaciones").val()
                };
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srDocObjeto.do?accion=goUpdArchivarDoc";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);                           
          
                ajaxCall("/srDocumentoAdmEmision.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
                            $('#divNewEmiDocumAdmin').hide();
                            $('#divEmiDocumentoAdmin').show();
                            refreshScript("divTablaEmiDocumenAdm", data);
                        }, 'text', false, false, "POST");
                        
                        
                jQuery('#divNewEmiDocumAdmin').html("");        
                removeDomId('windowConsultaArchivar');
                jQuery('#btn-buscar').click();
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
        /*$('#RutaDocs').html("<strong>" + dirPri + "</strong>");
        setTimeout("fu_activaDependencia_dir(" + codDep + "," + codDep + ");", 500);*/
    }
    return;
}

function fn_valFormArchivarDoc(objForm){
    var vReturn=1;
    var Observaciones=$('#'+objForm).find("#itxtObservaciones").val();
    var Fecha=$('#'+objForm).find("#itxtFecha").val();

    
    if ( document.getElementById( "vacio" )) {
        var vacio=document.getElementById("vacio").value;
        
        if(vacio==="-1")
        {
           vReturn=0;
           alert_Info("Aviso", "Verificar los archivos adjuntos"); 
        }
    }

    
    if (!!Observaciones===false)
    {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar por lo menos una observación");
        $('#'+objForm).find('#itxtObservaciones').focus();
    }
    if (!!Fecha===false)
    {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar Fecha");
        $('#'+objForm).find('#itxtFecha').focus();
    }
    
    if(!!Fecha){
            if(moment(fecha, "DD/MM/YYYY").isValid()){// HH:mm
  
                var fecha=moment(fecha, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(!fecha.isValid()){
                vReturn=0;
                 bootbox.alert("<h5>Fecha Inválida.</h5>", function() {
                     bootbox.hideAll();
                     $('#'+objForm).find('#itxtFecha').focus();
                 });
                                         
                }                
            }     
       
       
    }
    
 
   
   
   
    return vReturn;
}


function fn_verDocumentosFromPC2(pnuAnn, pnuEmi, ptiOpe) {
    if (!!pnuAnn) {
        var p = new Array();
        //p[0] = "accion=goDocRutaAbrir";
        p[0] = "accion=goDocRutaAbrirEmiReporte";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        
       
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                if (docs.retval == "OK") {
                    result = fn_abrirDocFromPCApplet(docs.noDoc);
                } else {
                    //Error en Documento
                    alert_Danger("!Repositorio : ", docs.retval);
                }
            }

        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_verDocumentosFromPC(pnuAnn, pnuEmi, ptiOpe) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goRutaGeneraDocx";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=1";
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                //result = fn_abrirDocFromPCApplet(docs.noDoc);
                    /*if (docs.retval === "OK") {
	                var param = {rutaDoc: docs.noDoc};
	                runApplet(appletsTramiteDoc.abrirDocumentoPC, param, function(data) {
	                    result = data;
	                    if (result != "OK") {
	                        alert_Danger("!Abrir Docx : ", result);
	                    }
	                });				
                    }else{
                        alert_Danger("!Abrir Docx : ", docs.retval);				
                    }*/
                if (docs.retval === "OK") {
//                    var param = {rutaDoc: docs.noDoc};
//                    runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumentoPC, param, function(data) {
//                        result = data;
//                        if (result !== "OK") {
//                            alert_Danger("!Abrir Docx : ", result);
//                        }
//                    });
                    fn_generaDocDesktop(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
                        result = data;
                        if (result !== "OK") {
                           bootbox.alert(result);
                        }
                    });
                                        
                }else{
                    alert_Danger("!Abrir Docx : ", docs.retval);
                }
            }
        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_enviarNotificacionObj(pnuAnn, pnuEmi,ptiEnv,pexisteDoc,pexisteAnexo,tipoBandeja,docEstadoMsj) {

    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocEnviarNotificacion";
       // p[1] = "pkEmi=" + pnuAnn + pnuEmi + pnuDes;
       p[1]="pnuAnn="+pnuAnn;
       p[2]="pnuEmi="+pnuEmi;
       p[3]="ptiEnvMsj="+ptiEnv;
       p[4]="pexisteDoc="+pexisteDoc;
       p[5]="pexisteAnexo="+pexisteAnexo;
       p[6]="tipoBandeja="+tipoBandeja;
       /*interopabilidad*/
       p[7]="docEstadoMsj="+docEstadoMsj;
        /*interopabilidad*/
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");

    }
    return false;
}



function fn_ArchivarDocumentosMsj(pnuAnn, pnuEmi) {

    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goArDocMsjEnviados";
       // p[1] = "pkEmi=" + pnuAnn + pnuEmi + pnuDes;
       p[1]="pnuAnn="+pnuAnn;
       p[2]="pnuEmi="+pnuEmi;
     
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocAnexo";
        p[1] = "pkEmi=" + pnuAnn + pnuEmi + pnuDes;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_cargarDocumentosAnexosObj(pnuAnn, pnuEmi) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goCargarDocumentosAnexos";
        p[1] = "pkEmi=" + pnuAnn + pnuEmi + "N";
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");

    }
    return false;
}


function fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes) {     
    
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocSeguimiento";
        p[1] = "pkEmi=" + pnuAnn + pnuEmi + pnuDes;
        //validando si existe seguimiento
        ajaxCall("/srDocObjeto.do", "accion=goDocSeguimientoRoot&"+p[1], function(data) {
            if (data == null || data==""){
                alert_Info("Seguimiento :", "No se puede dar seguimiento a documentos sin destinatario o anulados.");
                return;
            }else{
                //si es ok 
                ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                    if (data !== null){
                        $("body").append(data);
                    }
                }, 'text', false, false, "POST");
            }
        }, 'text', false, true, "GET");
     
    }
    return false;
}

function fn_anexosJson(
        pnuAnn, pnuEmi) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocAnexoRoot";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
           bootbox.alert(data);
        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_verDetalleDocAnexo(vpkEmiDoc) {
    var p = new Array();
    p[0] = "accion=goDetalleDocAnexo";
    p[1] = "pkEmi=" + vpkEmiDoc;
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        if (data !== null)
        {
            refreshScript("divDetalleDocAnexo", data);
        }
    }, 'text', false, false, "POST");
    return false;
}
function fn_verDetalleSeguimiento(vpkEmiDoc) {
    var p = new Array();
    p[0] = "accion=goDetalleSeguimiento";
    p[1] = "pkEmi=" + vpkEmiDoc;
    p[2] = "pkExp=" + jQuery('#frm_docOrigenBean_pkExp').val();    
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        if (data !== null)
        {
            refreshScript("divDetalleDocSeguimiento", data);
        }
    }, 'text', false, false, "POST");
    return false;
}

function fn_verDocAnexo(pnuAnn, pnuEmi, pnuAne) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocRutaAnexo";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuAne=" + pnuAne;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                /*runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                    result = data;
                });*/
                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data) {
                    result = data;
                });
            }

        }, 'text', false, false, "POST");

    }
    return false;
}

//YUAL
function fn_capturarAnexo(pnuAnn, pnuEmi, pnuAne) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goCopiarAnexo";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuAne=" + pnuAne;
        p[4] = "nuAnnDocProyecto=" + $('#nuAnnDocGenerado').val();
        p[5] = "nuEmiDocProyecto=" + $('#Padre_pkEmiDoc').val();
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
          alert_Sucess("ANEXO", "Se cargó el anexo en el Documento en Proyecto");

        }, 'text', false, false, "POST");

    }
    return false;
}


function fn_verDocAnexoArchivado() {

        var p = new Array();
        p[0] = "accion=goDocRutaAnexoArchivado";
        p[1] = "nuAnn=" + $('#nuAnn').val();
        p[2] = "nuEmi=" + $('#nuEmi').val();
        p[3] = "nuDes=" + $('#nuDes').val();
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                /*runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                    result = data;
                });*/
                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data) {
                    result = data;
                });
            }

        }, 'text', false, false, "POST");

   
    return false;
}



function fn_generaDocx() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    $('#inCargar').val("S");
    var rpta = fu_verificarDestinatario("1");
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        rpta = fu_verificarReferencia();
        nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            rpta = fu_verificarChangeDocumentoEmiAdm();
            nrpta = rpta.substr(0, 1);
            if (nrpta === "1") {
                alert_Warning("Emisión :", "Necesita grabar los cambios");
            } else {
                if (!!pnuAnn) {
                    var p = new Array();
                    p[0] = "accion=goRutaGeneraDocx";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=1";
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data);
                        if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined' && docs.retval !== "") {
                            if (docs.retval === "OK") {
                                //fn_generaDocApplet(docs.noUrl, docs.noDoc, function(data) {
                     
                                fn_generaDocDesktop(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
                                    result = data;
                                    if (result !== "OK") {
                                       bootbox.alert(result);
                                    }
                                });
                            } else {
                                alert_Danger("Generar Docx: ", docs.retval);
                            }
                        }
                    }, 'text', false, false, "POST");
                }
            }
        } else {
            alert_Info("Emisión :", rpta);
        }
    } else {
        alert_Info("Emisión :", rpta);
    }

    return false;
}
function fn_generaDocxInter() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    
//    console.log("fn_generaDocx()");
    var rpta = fu_verificarDestinatarioInter();
    var nrpta = rpta.substr(0, 1);
//    console.log("fu_verificarDestinatario nrpta->"+nrpta);
    if (nrpta === "1") {
        rpta = fu_verificarReferencia();
        nrpta = rpta.substr(0, 1);
//        console.log("fu_verificarReferencia nrpta->"+nrpta);
        if (nrpta === "1") {
            rpta = fu_verificarChangeDocumentoEmiAdmInter();
            nrpta = rpta.substr(0, 1);
//            console.log("fu_verificarChangeDocumentoEmiAdm nrpta->"+nrpta);
            if (nrpta === "1") {
//                console.log("alert_Warning1");
                alert_Warning("Emisión :", "Necesita grabar los cambios");
            } else {
                if (!!pnuAnn) {
                    var p = new Array();
                    p[0] = "accion=goRutaGeneraDocx";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=1";
//                    console.log("/srDocObjeto.do?accion=goRutaGeneraDocx");
//                    console.log("nuEmi->"+pnuEmi);
//                    console.log("tiOpe=1");
                    ajaxCall("/srDocObjetoInter.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data);
//                        console.log("data->"+data);
                        if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined' && docs.retval !== "") {
//                            console.log("docs.retval->"+docs.retval);
                            if (docs.retval === "OK") {
//                                console.log("fn_generaDocApplet()");
//                                console.log("docs.noUrl->"+docs.noUrl);
//                                console.log("docs.noDoc->"+docs.noDoc);
//                                console.log("docs.tienedocx->"+docs.tieneWord);
                                //
//                                fn_generaDocApplet(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
//                                    result = data;
////                                    console.log("result->"+result);
//                                    if (result !== "OK") {
//                                       bootbox.alert(result);
//                                    }
//                                });
                                    //MVALDERA(mvaldera@pcm.gob.pe)  

                                    fn_generaDocDesktopInter(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
                                        result = data;
                                        if (result !== "OK") {
                                           bootbox.alert(result);
                                        }
                                    });
                                        

                            } else {
//                                console.log("alert_Danger2");
                                alert_Danger("Generar Docx: ", docs.retval);
                            }
                        }
                    }, 'text', false, false, "POST");
                }
            }
        } else {
//            console.log("alert_Info1");
            alert_Info("Emisión :", rpta);
        }
    } else {
//        console.log("alert_Info2");
        alert_Info("Emisión :", rpta);
    }

    return false;
}



function fn_generaDocxPersonal() {
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    $('#inCargar').val("S");
    var rpta = fu_verificarDestinatario("1");
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        rpta = fu_verificarReferencia();
        nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            rpta = fu_verificarChangeDocumentoEmiPersonal();
            nrpta = rpta.substr(0, 1);
            if (nrpta === "1") {
                alert_Warning("Emisión :", "Necesita grabar los cambios");
            } else {
                if (!!pnuAnn) {
                    var p = new Array();
                    p[0] = "accion=goRutaGeneraDocx";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=1";
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data);
                        if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined' && docs.retval !== "") {
                            if (docs.retval === "OK") {
                                //fn_generaDocApplet(docs.noUrl, docs.noDoc, function(data) {
                     
                                fn_generaDocDesktop(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
                                    result = data;
                                    if (result !== "OK") {
                                       bootbox.alert(result);
                                    }
                                });
                            } else {
                                alert_Danger("Generar Docx: ", docs.retval);
                            }
                        }
                    }, 'text', false, false, "POST");
                }
            }
        } else {
            alert_Info("Emisión :", rpta);
        }
    } else {
        alert_Info("Emisión :", rpta);
    }

    return false;
}
/*ANTES
function fn_generaDocxPersonal() {
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();

    var rpta = fu_verificarDestinatario("1");
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        rpta = fu_verificarReferencia();
        nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            rpta = fu_verificarChangeDocumentoEmiPersonal();
            nrpta = rpta.substr(0, 1);
            if (nrpta === "1") {
                alert_Warning("Emisión :", "Necesita grabar los cambios");
            } else {
                if (!!pnuAnn) {
                    var p = new Array();
                    p[0] = "accion=goRutaGeneraDocx";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=1";
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data);
                        if (typeof (docs) != "undefined" && typeof (docs.retval) != 'undefined' && docs.retval != "") {
                            if (docs.retval === "OK") {
//                             result = fn_generaDocApplet(docs.noUrl,docs.noDoc);
//                            if (result!="OK"){
//                               bootbox.alert(result);
//                            }
                                //fn_generaDocApplet(docs.noUrl, docs.noDoc, function(data) {
                                fn_generaDocDesktop(docs.noUrl, docs.noDoc, function(data) {
                                    result = data;
                                    if (result !== "OK") {
                                       bootbox.alert(result);
                                    }
                                });


                            } else {
                                alert_Danger("Generar Docx: ", docs.retval);
                            }
                        }
                    }, 'text', false, false, "POST");
                }
            }
        } else {
            alert_Info("Emisión :", rpta);
        }
    } else {
        alert_Info("Emisión :", rpta);
    }
    return false;
}
*/
function fn_cargaObjDoc(pnuAnn, pnuEmi, ptiOpe) {
    var p = new Array();
    p[0] = "accion=goRutaCargaDoc";
    p[1] = "nuAnn=" + pnuAnn;
    p[2] = "nuEmi=" + pnuEmi;
    p[3] = "tiOpe=" + ptiOpe;
    var result = "ERROR";
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        eval("var docs=" + data);
        if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
            //fn_cargaDocApplet(docs.noUrl, docs.noDoc, function(data) {
            fn_cargaDocDesktop(docs.noUrl, docs.noDoc, function(data) {
                result = data;
            });
        }
    }, 'text', false, false, "POST");

    return result;
}

function fn_verificaSiExisteDoc(rutaDoc) {
    /*
     //FUNCION REEMPPLAZADA 
     var retval = "NO";
     var appletObj = jQuery('#firmarDocumento');
     try{
     retval=appletObj[0].verificaSiExisteDoc(rutaDoc,false);
     }catch(ex){
     retval = "NO";
     }
     return retval;
     */
}
function fu_callEventoTablaAnexosdetalle() {
    var idTabla = "tblAnexosDetalle";
    fu_eventoGridTabla(idTabla, paramConfAnexosDetalle);
}


function fn_verRequisitoObj(frm_docOrigenBean_pkExp) {
    if(!!frm_docOrigenBean_pkExp&&allTrim(frm_docOrigenBean_pkExp).length>6){    
        var pnuAnnExp=frm_docOrigenBean_pkExp.substr(0,4);
        var pnuSecExp=frm_docOrigenBean_pkExp.substr(4);
        if (!!pnuAnnExp&&!!pnuSecExp) {
            var p = new Array();
            p[0] = "accion=goRequisitosExpDocExtSeg";
            p[1] = "pnuAnnExp=" + pnuAnnExp;
            p[2] = "pnuSecExp=" + pnuSecExp;
            ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                if (data !== null)
                {
                    $("body").append(data);
                }
            }, 'text', false, false, "POST");

        }        
    }
    return false;
}
/*interoperabilidad*/
function fn_grabarEnvioMesaVirtual(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo,tipoBandeja) {

    var nDeDepDes = allTrim($("#txtDeDepDes").val());
    var nDeNomDes = allTrim($("#txtDeNomDes").val());
    var nDeCarDes=allTrim($("#txtDeCarDes").val());

    var vValidar=fu_validarMesaVirtual();
    
    if (vValidar===1)
    {
        var p = new Array();
        //p[0] = "accion=goDocRutaAbrir";
        p[0] = "accion=goUpdEnvioMesaVirtual";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nDeDepDes=" + nDeDepDes;
        p[4] = "nDeNomDes=" + nDeNomDes;
        p[5] = "nDeCarDes=" + nDeCarDes;

        //ajaxCallSendJson(url, jsonString, function(data) {
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {

            if (data.substring(0,1) === "1") {
                alert_Sucess("MENSAJE", "Se envió el documento a la mensajería correctamente");
                if(tipoBandeja==="EDIT"){
                    editarDocumentoEmiClick(pnuAnn, pnuEmi,pexisteDoc,pexisteAnexo);           
                }
                if(tipoBandeja==="NEW"){
                    EnvioOkMensajeria();
                }
                else {
                      ajaxCall("/srDocumentoAdmEmision.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
                            refreshScript("divTablaEmiDocumenAdm", data);
                        }, 'text', false, false, "POST");

               }
                removeDomId('windowConsultaAnexo');
                jQuery('#btn-buscar').click();



            } else {
                alert_Danger("ERROR:", data.substring(1,data.length));
            }
        }, 'text', false, false, "POST");    


    }

                            
    return;
}

function fu_validarMesaVirtual(){
    var valRetorno=1;//no buscar por referencia
    
    var nDeDepDes = allTrim($("#txtDeDepDes").val());
    var nDeNomDes = allTrim($("#txtDeNomDes").val());
    var nDeCarDes=allTrim($("#txtDeCarDes").val());
    
    if(!(typeof(nDeDepDes)!=="undefined" && nDeDepDes!==null && nDeDepDes!==""))
    {
        valRetorno = 0;
        alert_Info("Aviso", "Debe Ingresar por lo menos una dependencia u oficina");
    }
    
    if(!(typeof(nDeNomDes)!=="undefined" && nDeNomDes!==null && nDeNomDes!==""))
    {
        valRetorno = 0;
        alert_Info("Aviso", "Debe Ingresar por lo menos un Destinatario o Persona");
    }
    
    if(!(typeof(nDeCarDes)!=="undefined" && nDeCarDes!==null && nDeCarDes!==""))
    {
        valRetorno = 0;
        alert_Info("Aviso", "Debe Ingresar por lo menos un cargo del destinatario");
    }
    
   

    return valRetorno;
}

function fn_activarInter(siInter,tipoEnv)
{
    console.log("tipo"+siInter);
     console.log("tipo"+tipoEnv);
    if (tipoEnv==="-1" )
    {
        if (siInter==="S")
         {
             jQuery('#viewFisico').hide();
             jQuery('#viewInter').show();

             var pDesDes=$("#txtDeDepDes").val();
             if (pDesDes!="" && pDesDes!=null)
             {
                 document.getElementById("txtDeDepDes").readOnly = true; 
             }
             
             var pNomDes=$("#txtDeNomDes").val();
             if (pNomDes!="" && pNomDes!=null)
             {
                 document.getElementById("txtDeNomDes").readOnly = true; 
             }

             var pCarDes=$("#txtDeCarDes").val();
             if (pCarDes!="" && pCarDes!=null)
             {
                 document.getElementById("txtDeCarDes").readOnly = true; 
             }






             jQuery('#viewChkInter').show();

             document.getElementById("chkviewInter").checked = true;
             
             jQuery('#viewFisicoF').hide();
             jQuery('#viewInterF').show();
         }
         else
         {
             jQuery('#viewFisico').show();
             jQuery('#viewInter').hide();
             jQuery('#viewChkInter').hide();

             jQuery('#viewFisicoF').show();
             jQuery('#viewInterF').hide();
         }   
    }
    else
    {
        if (tipoEnv==="2")
         {
            jQuery('#viewFisico').hide();
            jQuery('#viewInter').show();
             
            document.getElementById("txtDeDepDes").readOnly = true;
            document.getElementById("txtDeNomDes").readOnly = true;
            document.getElementById("txtDeCarDes").readOnly = true;

             /*jQuery('#viewChkInter').show();

             jQuery("#viewChkInter").prop('checked',true);
             jQuery("#viewChkInter").attr('checked',true);*/

             jQuery('#viewFisicoF').hide();
             jQuery('#viewInterF').show();
         }
         else
         {
             jQuery('#viewFisico').show();
             jQuery('#viewInter').hide();
             jQuery('#viewChkInter').hide();

             jQuery('#viewFisicoF').show();
             jQuery('#viewInterF').hide();
         }
    }
  

}



function fn_changeChkVerInter(chk)
{

    if(chk.checked){    
        jQuery('#viewFisico').hide();
        jQuery('#viewInter').show();     
        
        jQuery('#viewFisicoF').hide();
        jQuery('#viewInterF').show();        

    }
    else
    {
        jQuery('#viewFisico').show();
        jQuery('#viewInter').hide();
        
        jQuery('#viewFisicoF').show();
        jQuery('#viewInterF').hide();
    }
    
}


/*interoperabilidad*/
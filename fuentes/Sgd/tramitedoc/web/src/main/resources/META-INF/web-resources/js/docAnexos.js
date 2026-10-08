function fn_verDetalleCargarAnexo(vpkEmiDoc, divDestino) {
    var p = new Array();
    p[0] = "accion=goDetalleCargarAnexo";
    p[1] = "pkEmi=" + vpkEmiDoc;
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        if (data !== null)
        {
            refreshScript(divDestino, data);
        }
    }, 'text', false, false, "POST");
    return false;
}
function fn_cargarArbolAnexos(pkEmiDoc, divTree, divResizable) {
    jQuery("#" + divResizable).resizable({
        resize: function(event, ui) {
            jQuery(".tree ul.dynatree-container").css('width', '100%').css('width', '-=20px');
            jQuery(".tree ul.dynatree-container").css('height', '100%').css('height', '-=20px');
        }
    });
    var arbol = jQuery("#" + divTree).dynatree({
        title: "Lazy loading",
        fx: {height: "toggle", duration: 200},
        autoFocus: false, // Set focus to first child, when expanding or lazy-loading.
        keyboard: true,
        clickFolderMode: 1,
        initAjax: {
            url: pRutaContexto + "/" + pAppVersion + "/srDocObjeto.do",
            data: {accion: "goDocAnexoRoot", pkEmi: pkEmiDoc}
        },
        onLazyRead: function(node) {
            node.appendAjax({
                url: pRutaContexto + "/" + pAppVersion + "/srDocObjeto.do",
                data: {accion: "goDocAnexoJson", pkEmi: node.data.key},
                success: function(node)
                {
                    jQuery(node).focus();
                }
            });
        }
    });
    jQuery("#" + divTree).dynatree({
        onActivate: function(node) {
            var idpkEmiDoc = node.data.key;
            jQuery("#Actual_pkEmiDoc").attr("value", idpkEmiDoc);
            fn_verDetalleCargarAnexo(idpkEmiDoc, "divDetalleCargarAnexo");
        }
    });
    jQuery("#Actual_pkEmiDoc").attr("value", pkEmiDoc);
    fn_verDetalleCargarAnexo(pkEmiDoc, "divDetalleCargarAnexo");

}
function fn_fileUpload(reemplazarDoc) {
    $("#progress").show();
    jQuery("#divErrorLista").html("");
     jQuery("#divError").hide();
   // var pFileSizeMax = 10000000;
   //YUAL
   var pFileSizeMax=jQuery('#fileSizeMaxCargo').val();
   
//    var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var numFiles = 0;
    var contAux = 0;
    jQuery('#fileupload').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            console.log('fileSize:'+fileSize);
            console.log('fileSizeMax:'+(fileSizeMax*1024*1024));
            if (fileSize <= (fileSizeMax*1024*1024))
            {
                numFiles++;
                if (reemplazarDoc === true) {
                    var nuAnn = $("#oNuAnn").attr("value");
                    var nuEmi = $("#oNuEmi").attr("value");
                    var nuAne = $("#oNuAne").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srDocObjeto.do?accion=goUploadReplace", "&pkEmi=", nuAnn, nuEmi, "&pNuAne=", nuAne);
                    data.url = url;
                }
                data.submit();
            } else
            {
                fileSize = Math.round(fileSize / 1000000) + " MB";
                var row = "";
                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMax +'MB.</strong> ');
                jQuery("#divErrorLista").append('<li>' + row + '</li>');
                jQuery("#divError").show();
                jQuery("#divError").attr('display', 'block');
                return;
            }
        },
        done: function(e, data) {
            console.log('data');
            console.log(data);
            if (data.textStatus === "success")
            {
                var result=JSON.parse(data.result);
                console.log(result[0]);
                if(result!=null && result.length>0 && result[0].mensaje=="OK"){
                    contAux++;
                    if (contAux === numFiles) {
                        if (reemplazarDoc) {
                            var jsonRes = jQuery.parseJSON(data.result);
                            var nombreDoc = jsonRes[0].name;
                            var nuAne = $("#oNuAne").attr("value");
                            $("#Anexo" + nuAne + " input:eq(0)").attr("value", nombreDoc);
                            $("#Anexo" + nuAne + " input:eq(1)").attr("value", nombreDoc);
                            reemplazarDoc = false;
                        } else {
                            actualizarListadoAnexos();
                        }
                        alert_Sucess("MENSAJE", "cambios guardados");
                    }
              }
              else if (result!=null && result.length>0 && result[0].mensaje!="OK")
              {
                  $("#progress").hide();
                  alert_Danger("ERROR",result[0].mensaje);
              }
              else {
                  $("#progress").hide();
                  alert_Danger("ERROR", "Error al cargar el archivo");
              }
            } else {
                $("#progress").hide();
                alert_Danger("ERROR", "Error al cargar el archivo");
            }
        },
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            jQuery('#progress .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progress span').html(progressText);
        }
    });
    jQuery('#fileupload').click();
}
function actualizarCargaAnexosDetalle()
{
    var pkEmiDocActual = jQuery("#Actual_pkEmiDoc").attr("value");
    if (pkEmiDocActual) {
        fn_verDetalleCargarAnexo(pkEmiDocActual);
    }
}
function actualizarListadoAnexos()
{
    var pkEmiDocActual = jQuery("#Actual_pkEmiDoc").attr("value");
    if (pkEmiDocActual) {
        fn_verListadoArchivosAnexos(pkEmiDocActual);
    }
}


function fn_verListadoArchivosAnexos(vpkEmiDoc) {
    var p = new Array();
    var accion;
    if (esElNodoPadre()) {
        accion = "accion=goListadoArchivosAnexos";
    } else {
        accion = "accion=goListadoArchivosAnexosModoLectura";
    }
    p[0] = accion;
    p[1] = "pkEmi=" + vpkEmiDoc;
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        if (data !== null)
        {
            refreshScript("divListadoArchivosAnexos", data);
        }
    }, 'text', false, false, "POST");
    return false;
}

function fn_verListadoMsjAnexos(pann,pemi) {
    var p = new Array();
    var accion;

        accion = "accion=goListadoMsjAnexos";
 
    p[0] = accion;
    p[1] = "nuAnn=" + pann;
    p[2] = "nuEmi=" + pemi;
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        if (data !== null)
        {
            
            refreshScript("divListadoMsjAnexos", data);
        }
    }, 'text', false, false, "POST");
    return false;
}


function bloquearAbrirDocumento(pNodPadre) {
    var pkEmiDocActual = jQuery("#Actual_pkEmiDoc").attr("value");
    if (pkEmiDocActual) {
        if (pkEmiDocActual === pNodPadre)
        {
            jQuery("#btn-abrirDocumento").css("display", "none");
        } else {
            jQuery("#btn-abrirDocumento").css("display", "block");
            jQuery("#divHerramientasAnexar").html("");
        }
    }
}
function fn_cargarAnexos() {
    jQuery('#fileupload').attr("multiple", "");
    jQuery('#progress .progress-bar').css('width', '0%');
    jQuery('#progress span').html('');
    fn_fileUpload(false);
}
function fn_cargarAnexosIE() {
    if (jQuery.browser.msie && parseInt(jQuery.browser.version) <= 9) {
        jQuery("#btn-fileuploadIE").click(function() {
            jQuery('#progress .progress-bar').css('width', 0 + '%');
        });
        jQuery("#btn-fileupload").remove();
        $('#fileuploadIE').fileupload({
            add: function(e, data) {
                var jqXHR = data.submit()
                        .success(function(result, textStatus, jqXHR) {
                            actualizarListadoAnexos();
                            //alert(textStatus);
                        })
                        .error(function(jqXHR, textStatus, errorThrown) {
                           bootbox.alert("ERROR");
                        })
                        .complete(function(result, textStatus, jqXHR) {
                            jQuery('#progress .progress-bar').css('width', 100 + '%');
                            jQuery('#progress span').html("Cargado");
                        });
            }
        });
    } else {
        jQuery("#btn-fileuploadIE").remove();
    }
}
function fn_actualizar(nodoPadrePkEmiDoc) {
    var nodoPadre_pkEmiDoc = nodoPadrePkEmiDoc;
    actualizarCargaAnexosDetalle();
    actualizarListadoAnexos();
    jQuery("#divError").hide();
    jQuery("#divErrorLista").html("");
    arrayDocEliminar = [];
    $("#progress").hide();
}
function fn_DescargarArchivosZip()
{
    var nuAnn = $("#nuAnnDocGenerado").val();
    var nuEmi = $("#nuEmiDocGEnerado").val();
     
    fn_verDocAnexo(nuAnn,nuEmi,'ALL');    
}
function fn_GuardarCambios()
{
    var cambioDetalle = false;
    var indiceCambios = Array();
    var jsonBody = [];
    var nuAnn = $("#oNuAnn").attr("value");
    var nuEmi = $("#oNuEmi").attr("value");
    if ((!!nuAnn && !!nuEmi) === false)
    {
        alert_Warning("ALERTA", "No se registro nuevos cambios.");
        return;
    }

   /* $("#tblDocAnexos tbody tr").each(function(indice, valor) {

        var reqFirma=1*jQuery(jQuery(jQuery(this).parents('tr')).find('[name=reqFirma]')).is(':checked');
        var tiPublic=1*jQuery(jQuery(jQuery(this).parents('tr')).find('[name=tiPublic]')).is(':checked');
        var nuAne = arrayNuAne[indice];
        //var nuevoDetalle = jQuery(jQuery(jQuery(this).parents('tr')).find("[name='txtDetalleAnexo']")).val();
        var nuevoDetalle = $(this).find('input[id=txtDetalleAnexo]').val(); //$("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val();
            console.log("reqFirma"+reqFirma);
            console.log("tiPublic"+tiPublic);
            console.log("nuAne"+nuAne);
            console.log("nuevoDetalle"+nuevoDetalle);
            
        if ([jQuery(this).val(),reqFirma].join() !== arrayDetalles[indice].join()||[jQuery(this).val(),tiPublic].join() !== arrayDetalles[indice].join()||[jQuery(this).val(),nuevoDetalle].join() !== arrayDetalles[indice].join())
        {
            cambioDetalle = true;
            indiceCambios.push(indice);

            jsonBody.push({"nuAnn": nuAnn, "nuEmi": nuEmi, "nuAne": nuAne, "deDet": nuevoDetalle, "tiOpe": "update","reqFirma": reqFirma,"tiPublic": tiPublic});
        }
    });*/
    
        jQuery("[name='txtDetalleAnexo']").each(function(indice, valor) {
        var reqFirma=1*jQuery(jQuery(jQuery(this).parents('tr')).find('[name=reqFirma]')).is(':checked');
        var tiPublic=1*jQuery(jQuery(jQuery(this).parents('tr')).find('[name=tiPublic]')).is(':checked');
        if ([jQuery(this).val(),reqFirma].join() !== arrayDetalles[indice].join()||[jQuery(this).val(),tiPublic].join() !== arrayDetalles[indice].join())
        {
            cambioDetalle = true;
            indiceCambios.push(indice);
            var nuAne = arrayNuAne[indice];
            var nuevoDetalle = jQuery(this).val();
            jsonBody.push({"nuAnn": nuAnn, "nuEmi": nuEmi, "nuAne": nuAne, "deDet": nuevoDetalle, "tiOpe": "update","reqFirma": reqFirma,"tiPublic": tiPublic});
        }
    });
    
    for (var i = 0; i < arrayDocEliminar.length; i++)
    {
        jsonBody.push({"nuAnn": nuAnn, "nuEmi": nuEmi, "nuAne": arrayDocEliminar[i], "tiOpe": "delete"});
    }
    var jsonHead = {"docs": jsonBody};
    var jsonString = JSON.stringify(jsonHead);
    if (cambioDetalle || arrayDocEliminar.length > 0) {
        var rowCount = $('#tblDocAnexos tr').length;
        var url = "/srDocObjeto.do?accion=goActualizaDescripAnex&rowCount="+rowCount;
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "OK") {
                alert_Sucess("MENSAJE", "Datos Guardados");
            } else {
                alert_Danger("ERROR", data);
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Warning("MENSAJE", "No se registro nuevos cambios.");
    }
}
function fn_reemplazarDocumento()
{
    var reemplazarDoc = true;
    jQuery('#progress .progress-bar').css('width', '0%');
    jQuery('#fileupload').removeAttr("multiple");
    fn_fileUpload(reemplazarDoc);
}
function fn_eliminarDocumento()
{
    var nuAnn = $("#oNuAnn").attr("value");
    var nuEmi = $("#oNuEmi").attr("value");
    var nuAne = $("#oNuAne").attr("value");
    var pkEmi = "";
    pkEmi = pkEmi.concat(nuAnn, nuEmi);
    $("tr [nuAne='" + nuAne + "']").parents("tr").remove();
    arrayDocEliminar.push(nuAne);
    var indexNuAne;
    for (var i = 0; i < arrayNuAne.length; i++) {
        if (nuAne === arrayNuAne[i])
        {
            indexNuAne = i;
            break;
        }
    }
    arrayNuAne.splice(indexNuAne, 1);
    arrayDetalles.splice(indexNuAne, 1);
    return;
}

function fn_eventDocAnexos() {
    var indexFilaClick = -1;
    if (jQuery.browser.msie && parseInt(jQuery.browser.version) <= 9) {
        $("#btn-ReemplazarDoc").remove();
    } else {
        $("#btn-ReemplazarDocIE").remove();
    }
    $("#tblDocAnexos tbody tr").click(function() {
        if (jQuery.browser.msie && parseInt(jQuery.browser.version) <= 9) {
            jQuery("#btn-ReemplazarDoc").remove();
        } else {
            jQuery("#btn-ReemplazarDocIE").remove();
        }
        if (indexFilaClick !== -1) {
            if ($("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
                $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
                $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }
        else {
            $(this).addClass('row_selected');
            indexFilaClick = $(this).index();
            var nuAnn = $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") button").attr("nuAnn");
            var nuEmi = $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") button").attr("nuEmi");
            var nuAne = $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") button").attr("nuAne");
            $("#oNuAnn").attr("value", nuAnn);
            $("#oNuEmi").attr("value", nuEmi);
            $("#oNuAne").attr("value", nuAne);
            $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
            $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
            fn_validarShowBtnFirmarAnexo('#tblDocAnexos',indexFilaClick);
            $("#tblDocAnexos tbody tr:eq(" + indexFilaClick + ") div #fileuploadReIE").attr("id", "fileuploadReIE" + nuAne);
            var pkEmi = nuAnn + nuEmi;
            var nuAne = $("#oNuAne").attr("value");
            $('#fileuploadReIE' + nuAne).fileupload({
                add: function(e, data) {
                    jQuery('#progress .progress-bar').css('width', 0 + '%');
                    var rutactx = $("#rutactx").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srDocObjeto.do?accion=goUploadReplaceIE", "&pkEmi=", nuAnn, nuEmi, "&pNuAne=", nuAne);
                    data.url = url;
                    var jqXHR = data.submit()
                            .success(function(result, textStatus, jqXHR) {
                                actualizarListadoAnexos();
                            })
                            .error(function(jqXHR, textStatus, errorThrown) {
                               bootbox.alert("ERROR");
                            })
                            .complete(function(result, textStatus, jqXHR) {
                                jQuery('#progress .progress-bar').css('width', 100 + '%');
                                jQuery('#progress span').html("Cargado");
                            });
                }
            });
        }
        //return false;
    });
    $('#tblDocAnexos tbody tr>td:nth-child(4)').click(function(e) {
        e.stopPropagation();
    });
}
function esElNodoPadre() {
    var rpta = false;
    var nActual = $("#Actual_pkEmiDoc").val();
    var nPadre = $("#Padre_pkEmiDoc").val();
    if (nActual === nPadre) {
        rpta = true;
        return rpta;
    } else {
        return rpta;
    }
}

function fn_validarShowBtnFirmarAnexo(idTbl,indexFila){
    var isNoCheckDocFirmar=jQuery(idTbl+" tbody tr:eq(" + indexFila + ")").find('[name=reqFirma]').is(':not(:checked)');
    if(isNoCheckDocFirmar){
        var btn_firmar=jQuery(idTbl+" tbody tr:eq(" + indexFila + ") div").find('#btn-firmar');
        var isBtn_firmar=!!btn_firmar.length;
        if(isBtn_firmar){
            btn_firmar.remove();
        }
    }
}

function fn_firmarDocumentoAnexo(btnFirmar){
    var nuAnn = $("#oNuAnn").attr("value");
    var nuEmi = $("#oNuEmi").attr("value");
    var nuAne = $("#oNuAne").attr("value");
    //console.log("nuAnn:"+nuAnn+" nuEmi:"+nuEmi+" nuAne:"+nuAne);
    //jQuery(noForm).find("#nuSecuenciaFirma").val("");
    //jQuery("#rutaDocFirma").val("");
    
    var ptiOpe = "5";
    var p = new Array();
    p[0] = "accion=goRutaFirmaDocAnexo";
    p[1] = "nuAnn=" + nuAnn;
    p[2] = "nuEmi=" + nuEmi;
    p[3] = "nuAne=" + nuAne;
    p[4] = "tiOpe=" + ptiOpe;
    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
        var result;
        eval("var docs=" + data);
        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
            if (docs.retval === "OK") {
                jQuery("#nuSecuenciaFirmaAnexo").val(docs.nuSecFirma);
                jQuery("#noPrefijoAnexo").val(docs.noPrefijo);
                jQuery("#rutaDocFirmaAnexo").val(docs.noDoc);
                //jQuery("#inFirmaEmi").val(docs.inFirma);

                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"3"};
                /*runApplet(appletsTramiteDoc.ejecutaFirma,param,function(data){
                    result=data;
                    fn_mostrarCargarAnexoFirmado(btnFirmar);
                });*/
                runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma,param, function(data){
                    result=data;
                    fn_mostrarCargarAnexoFirmado(btnFirmar);
                });
            } else {
                alert_Danger("!Repositorio : ", docs.retval);
            }
        }

    }, 'text', false, false, "POST");            
}

function fn_mostrarCargarAnexoFirmado(btnFirmar){
    jQuery(btnFirmar).attr('onclick','fn_cargarAnexoFirmado();');
    jQuery(btnFirmar).attr('title','Cargar Documento Firmado');
    jQuery(btnFirmar).find('span').attr('class','glyphicon glyphicon-cloud-upload');
    jQuery(btnFirmar).attr('class','btn btn-success btn-xs');
}

function fn_cargarAnexoFirmado(){
    var vnuSecFirma = jQuery("#nuSecuenciaFirmaAnexo").val();
    var vnoDoc="";
    if (!!vnuSecFirma && typeof(vnuSecFirma) !== "undefined" && vnuSecFirma !== "") {
        var rutaDocFirma = jQuery("#rutaDocFirmaAnexo").val();
        /*var vinFirma = jQuery('#inFirmaEmi').val();
        if (vinFirma===null || typeof(vinFirma) === "undefined" || vinFirma === ""){
            vinFirma = "F";
        }*/
        var valDoc = "NO";
        if (!!rutaDocFirma) {
            var vnoPrefijo = jQuery("#noPrefijoAnexo").val();
            if (vnoPrefijo==="[AF]"){
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 4) + vnoPrefijo+".pdf";
            }else{
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) +"N"+ vnoPrefijo+".pdf";
            }
            var param = {rutaDoc: vnoDoc};
            /*runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
                valDoc=data;
                if (valDoc === "SI") {
                    fn_cargarDocAnexoFirmado(valDoc,vnoDoc);
                    return;
                }
            });*/
            runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                valDoc=data;
                if (valDoc === "SI") {
                    fn_cargarDocAnexoFirmado(valDoc,vnoDoc);
                    return;
                }
            });
        }
    } else {
        alert_Danger("Firma!", "Se necesita Firmar Documento.");
    }    
}

function fn_cargarDocAnexoFirmado(valDoc,vnoDoc){
    if (valDoc === "SI") {
        fn_cargaDocAnexoFirmaApplet(vnoDoc, function(data) {
            var resulCarga = data;
            /*if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                jQuery('#nuSecuenciaFirmaAnexo').val(resulCarga);
                var nuAnn = $("#oNuAnn").attr("value");
                var nuEmi = $("#oNuEmi").attr("value");
                var nuAne = $("#oNuAne").attr("value");                
                var nuSec = resulCarga;
                var param={nuAnn:nuAnn,nuEmi:nuEmi,nuAne:nuAne,nuSecFirma:nuSec};
                ajaxCall("/srDocAnexo.do?accion=goCargaDocAnexoFirmado", param, function(data) {
                    fn_rptaCargarDocAnexoFirmado(data);
                },
                        'json', false, false, "POST");
            }*/
            if(resulCarga.error==="0"){
                jQuery('#nuSecuenciaFirmaAnexo').val(resulCarga.message);
                var nuAnn = $("#oNuAnn").attr("value");
                var nuEmi = $("#oNuEmi").attr("value");
                var nuAne = $("#oNuAne").attr("value");                
                var nuSec = resulCarga.message;
                var param={nuAnn:nuAnn,nuEmi:nuEmi,nuAne:nuAne,nuSecFirma:nuSec};
                ajaxCall("/srDocAnexo.do?accion=goCargaDocAnexoFirmado", param, function(data) {
                    fn_rptaCargarDocAnexoFirmado(data);
                },
                        'json', false, false, "POST");
            }
        });
    } else {
        alert_Danger("Firma!", "El Documento no esta Firmado.");
    }    
}

function fn_rptaCargarDocAnexoFirmado(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            alert_Sucess("Éxito!", "Anexo Cargado Correctamente.");
            jQuery("#nuSecuenciaFirmaAnexo").val("");
            //ocultar boton subir docAnexo firmado
        } else {
            alert_Danger("Anexo!", data.deRespuesta);
        }
    }
}

function fn_cargaDocAnexoFirmaApplet(pnoDoc,callback) {
    
    var pnuAnn = $("#oNuAnn").attr("value");
    var pnuEmi = $("#oNuEmi").attr("value");
    var pnuAne = $("#oNuAne").attr("value");
    var pnuSecFirma = jQuery('#nuSecuenciaFirmaAnexo').val();
    var ptiOpe = "6";
    var resulCarga = "ERROR";
    var docs;
    if (!!pnuAnn && !!pnuEmi && !!pnuSecFirma) {
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaFirmaDocAnexo";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuAne=" + pnuAne;
        p[4] = "tiOpe=" + ptiOpe;
        p[5] = "nuSecFirma=" + pnuSecFirma;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            eval("docs=" + data);
            if (typeof(docs) !== "undefined" && typeof(docs.retval) !== 'undefined') {

                if (docs.retval === "OK") {
                    var retval = "";
                    try {
                        //retval = appletObj[0].cargarDocumento(docs.noUrl, pnoDoc);
                        var param = {urlDoc: docs.noUrl, rutaDoc: pnoDoc};
                        /*runApplet(appletsTramiteDoc.cargarDocumento, param, function(data) {
                            retval = data;
                            callback(retval);
                            return;
                        });*/
                        runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function(data){    
                            retval = data;
                            callback(retval);
                            return;
                        });
                    } catch (ex) {
                        alert_Danger("Firma!: ", "Fallo en subir documento al servidor");
                        retval = "ERROR";
                        resulCarga = retval;
                        callback(resulCarga);
                        return;
                    }
                } else {
                    alert_Danger("Firma!: ", docs.retval);
                    resulCarga = "ERROR";
                    callback(resulCarga);
                    return;
                }
            }
        }, 'text', true, false, "POST");
        
    }
    
}
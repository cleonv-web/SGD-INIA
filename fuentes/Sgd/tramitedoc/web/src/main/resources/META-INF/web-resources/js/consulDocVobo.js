function fn_inicializaConsulDocsVoBo(){
    var noForm='#buscarDocumentoVoBoBean';
    jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});        
    jQuery(noForm).find('#nuDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    fn_validarTipoBusquedaConsulDocsVoBo(noForm,'0');
    pnumFilaSelect=0;
}
function fn_validarTipoBusquedaConsulDocsVoBo(noForm,tiBusq){
    var tipoBusq=jQuery(noForm).find('#tipBusqueda').val(tiBusq).val();
    jQuery(noForm).find('#feIni').val(jQuery(noForm).find('#fechaFiltro').attr('fini'));    
    jQuery(noForm).find('#feFin').val(jQuery(noForm).find('#fechaFiltro').attr('ffin'));    
    if(tipoBusq==='1'){//busqueda
        if(validarBusqDocsVoBo(noForm)){
            var pEsIncluyeFiltro = jQuery(noForm).find('#esIncluyeFiltro1').is(':checked');
            if(pEsIncluyeFiltro){//filtro
                if(validarFiltroDocsVoBo(noForm)){
                    fn_submitAjaxFormConsulDocsVoBo(noForm);
                }
            }else{
                fn_submitAjaxFormConsulDocsVoBo(noForm);
            }
        }
    }else if(tipoBusq==='0'){//filtro
        if(validarFiltroDocsVoBo(noForm)){
            fn_submitAjaxFormConsulDocsVoBo(noForm);
        }        
    }    
}

function fn_submitAjaxFormConsulDocsVoBo(noForm){
    ajaxCall("/srConsultaDocVobo.do?accion=goInicio", jQuery(noForm).serialize(), function(data) {
        refreshScript("divTblDocsVoBo", data);
    }, 'text', false, false, "POST");    
}

//function validarBusqDocsVoBo(noForm){
//    var vRetorno=1;
//    var vnroEmi=allTrim(jQuery(noForm).find('#nuCorEmi').val());
//    var vnroDoc=allTrim(jQuery(noForm).find('#nuDoc').val());;
//    var vnroExp=allTrim(jQuery(noForm).find('#nroExp').val());;
//    var vdeAsunto=allTrim(jQuery(noForm).find('#asunto').val());;
//    
//    if(!!vRetorno){
//        if(!(!!vnroEmi||!!vnroDoc||!!vnroExp||!!vdeAsunto)){
//            vRetorno=0;
//            alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda.");
//        }
//    }    
//    if(!!vRetorno){
//        if (!!vnroEmi) {
//            var vValidaNumero=fu_validaNumero(vnroEmi);
//            if (vValidaNumero!=="OK") {
//                vRetorno=0;
//                alert_Warning("Buscar: ","N° de Emisión debe ser solo numeros.");
//            }
//        }        
//    } 
//    if(!!vRetorno){
//        if (!!vnroDoc) {
//            var vValidaNumero=fu_validaNumero(vnroDoc);
//            if (vValidaNumero!=="OK") {
//                vRetorno=0;
//                alert_Warning("Buscar: ","N° de Doc. debe ser solo numeros.");
//            }
//        }        
//    }     
//    if(!!vRetorno){
//        if (!!vnroExp) {
//            var vValidaNumero=fu_validaNumero(vnroExp);
//            if (vValidaNumero!=="OK") {
//                vRetorno=0;
//                alert_Warning("Buscar: ","N° de Expediente debe ser solo numeros.");
//            }
//        }        
//    }
//    if(!!vRetorno){
//        vRetorno=fn_validarFechasBusqDocsVoBo(noForm);
//    }
//    return !!vRetorno;
//}

//function validarFiltroDocsVoBo(noForm){
//    var vRetorno=1; 
//    if(!!vRetorno){
//        vRetorno=fn_validarFechasBusqDocsVoBo(noForm)
//    }
//    return !!vRetorno;   
//}
//
//function fn_validarFechasBusqDocsVoBo(noForm){
//    var vRetorno=1; 
//    var pfeIni=allTrim(jQuery(noForm).find('#feIni').val());
//    var pfeFin=allTrim(jQuery(noForm).find('#feFin').val());
//    if(!!vRetorno){
//        if(!!pfeIni){
//            var fIni=moment(pfeIni, "DD/MM/YYYY");
//            if (!fIni.isValid()) {
//                vRetorno=0;
//                alert_Warning("","Fecha Inicial Inválida.");                 
//            }
//        }else{
//            vRetorno=0;
//           alert_Warning("","Fecha Inicial Inválida."); 
//        }
//    }   
//    if(!!vRetorno){
//        if(!!pfeFin){
//            var fFin=moment(pfeFin, "DD/MM/YYYY");
//            if (!fFin.isValid()) {
//                vRetorno=0;
//                alert_Warning("","Fecha Final Inválida.");                 
//            }            
//        }else{
//            vRetorno=0;
//            alert_Warning("","Fecha Final Inválida."); 
//        }
//    }    
//    if(!!vRetorno){
//        var pannBusq = verificarAñoBusquedaFiltro(pfeIni,pfeFin);
//        if(!!pannBusq){
//            var vValidaNumero=fu_validaNumero(pannBusq);
//            if (vValidaNumero==="OK"&&pannBusq.length===4) {
//                jQuery(noForm).find('#nuAnn').val(pannBusq);
//            }else{
//                jQuery(noForm).find('#nuAnn').val("");
//            }            
//        }else{
//            vRetorno=0;
//            alert_Warning("","Fechas Inválida.");             
//        }
//    }
//    return !!vRetorno;       
//}

//function fu_iniTblDocsVoBo(){
//    var nomTabla='#tblDocsVobo';
//    var indexFilaClick = -1;
//    $(nomTabla+" tbody tr").click(function() {
//        if (indexFilaClick !== -1) {
//            if ($(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
//                $(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
//            }
//        }
//        if ($(this).hasClass('row_selected')) {
//            $(this).removeClass('row_selected');
//        }else {
//            $(this).addClass('row_selected');
//            var pnuAnn=$(this).children('td')[0].innerHTML;
//            var pnuEmi=$(this).children('td')[1].innerHTML;
//            if(!!pnuAnn&&!!pnuEmi){
//                jQuery('#txtpnuAnn').val(pnuAnn);
//                jQuery('#txtpnuEmi').val(pnuEmi);
//                pnumFilaSelect = $(this).index();
//            }
//            indexFilaClick = $(this).index();
//        }
//    });
//    
//    if(jQuery(nomTabla+' >tbody >tr').length > 0){
//        try{
//            if(jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).length === 1){
//                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).trigger("click");
//                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).focus();
//            }else{
//                pnumFilaSelect=0;
//            }
//        }catch(ex){
//            pnumFilaSelect=0;
//        }
//    }    
//    $(nomTabla+" tbody td").hover(
//            function() {
//                $(this).attr('id', 'divtitlemostrar');
//                var index = $(this).index();
//                if (index === 4 || index === 8) {
//                    showdivToolTip($('#divtitlemostrar').position(), $(this).html());
//                }
//            },
//            function() {
//                $('#divtitlemostrar').removeAttr('id');
//                $('#divflotante').hide();
//            }
//    );
//    function showdivToolTip(elemento, text){
//        $('#divflotante').html(text);
//        var x = elemento.left;
//        var y = elemento.top + 24;
//        $("#divflotante").css({left: x, top: y});
//
//        document.getElementById('divflotante').style.display = 'block';
//
//        return;
//    }
//}

function editarConsulDocVoBoButtonToolBar() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    editarConsulDocVoBo(pnuAnn,pnuEmi);
}

function editarConsulDocVoBo(pnuAnn,pnuEmi){
    if (!!pnuAnn&&!!pnuEmi) {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        ajaxCall("/srConsultaDocVobo.do?accion=goEditDocumentoVoBo", p.join("&"), function(data) {
            jQuery('#divBusqDocumentoVoBo').hide();
            jQuery('#divTblDocsVoBo').html("");
            jQuery('#divNewEmiDocumVoBo').show();
            refreshScript("divNewEmiDocumVoBo", data);            
        }, 'text', false, false, "POST");
    } else {
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }    
}

//function editarDocumentoVoBoButtonFila(pnuAnn,pnuEmi) {
//    editarDocumentoVoBo(pnuAnn,pnuEmi);
//}

//function fn_eventConsulEstDocVoBo(noForm) {
    //$("#progressDocVobo").hide();
//    jQuery(noForm).find('#deObs').showBigTextBox({
//        pressAceptarEvent: function(data) {
//            if (data.hasChangedText) {
//                fu_changeObsDocVoboBean();
//                if(jQuery(noForm).find("#txtValChangeDocEsObsOpc").val()==='1'){
//                    fn_grabarDocVoBoObs();
//                    jQuery(noForm).find("#txtValChangeDocEsObsOpc").val('0');
//                }
//            }
//        },maxNumCar:300
//    });
//    jQuery(noForm).find('#deObs').change(function(){
//        fu_changeObsDocVoboBean();
//    });
//    if(!!sEstadoDocAdm&&(sEstadoDocAdm==="2"||sEstadoDocAdm==="7"||sEstadoDocAdm==="0")){
//        jQuery('#deObs').focus();
//    }
//    fn_loadEditionDocVoBo(noForm,sEstadoDocAdm);
//}

//function fu_changeObsDocVoboBean(){
//    jQuery('#envDocumentoVoboBean').val("1");
//}

//function fn_changeToEnviarDocVoBo(noForm){
//    var vnuSecFirma = jQuery(noForm).find("#nuSecuenciaFirma").val();
//    var vnoDoc="";
//    if (!!vnuSecFirma && typeof(vnuSecFirma) !== "undefined" && vnuSecFirma !== "") {
//        var rutaDocFirma = jQuery("#rutaDocFirma").val();
//        var vinFirma = jQuery('#inFirmaEmi').val();
//        if (vinFirma===null || typeof(vinFirma) === "undefined" || vinFirma === ""){
//            vinFirma = "F";
//        }
//        var valDoc = "NO";
//        if (!!rutaDocFirma) {
//            var vnoPrefijo = jQuery("#noPrefijo").val();
//            if (vnoPrefijo==="[VF]"){
//                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 4) + vnoPrefijo+".pdf";
//            }else{
//                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) +"N"+ vnoPrefijo+".pdf";
//            }
//            
//            var param = {rutaDoc: vnoDoc};
//            runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
//                valDoc=data;
//                if (valDoc === "SI") {
//                    fn_grabarEnvioDocVoBo(valDoc,vnoDoc,noForm);
//                    return;
//                }
//                
//                if (vinFirma === "N" && valDoc !== "SI") {
//                    vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) + "N.pdf";
//                    var param = {rutaDoc: vnoDoc};
//                    runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
//                        valDoc = data;
//                        fn_grabarEnvioDocVoBo(valDoc, vnoDoc,noForm);
//                        return;
//                    });
//                }else{
//                    alert_Danger("Firma!", "El Documento no esta Visado.");
//                }
//                
//            });
//        }
//            
//    } else {
//        alert_Danger("Firma!", "Se necesita Visar Documento.");
//    }
//}

//function fn_grabarEnvioDocVoBo(valDoc,vnoDoc,noForm){
//    if (valDoc === "SI") {
//        fn_cargaDocVoBoFirmaApplet(vnoDoc,noForm, function(data) {
//            var resulCarga = data;
//            if (resulCarga !== "ERROR" && resulCarga !== "NO") {
//                jQuery(noForm).find('#nuSecuenciaFirma').val(resulCarga);
//                ajaxCall("/srDocumentoVoBo.do?accion=goChangeToEnviado", $(noForm).serialize(), function(data) {
//                    fn_rptaChangeToEnviadoDocVoBo(data,noForm);
//                },'json', false, false, "POST");
//            }
//        });
//    } else {
//        alert_Danger("Firma!", "El Documento no esta Visado.");
//    }
//}
//
//function fn_rptaChangeToEnviadoDocVoBo(data,noForm) {
//    if (!!data) {
//        if (data.coRespuesta === "1") {
//            jQuery(noForm).find('#esDocEmi').val("1");
//            jQuery(noForm).find('#deObs').val("");
//            fn_loadEditionDocVoBo(noForm,'1');
//            updTblPersonalVoboEdit(data.coDep,data.coEmp,'1','#tblPersVoBoDocAdm');
//            alert_Sucess("Éxito!", "Transacción completada.");
//            jQuery(noForm).find("#nuSecuenciaFirma").val("");
//            fn_cargaToolBarDocVoBo(noForm);
//        } else {
//            alert_Danger("VoBo!",data.deRespuesta);
//        }
//    }
//}
//
//function fn_cargaDocVoBoFirmaApplet(pnoDoc,noForm,callback) {
//    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
//    var pnuSecFirmaEmi = jQuery(noForm).find("#nuSecuenciaFirma").val();
//    var ptiOpe = "6";
//    var resulCarga = "ERROR";
//    var docs;
//    if (!!pnuAnn && !!pnuEmi && !!pnuSecFirmaEmi) {
//        // Obteniendo la ruta de Carga
//        var p = new Array();
//        p[0] = "accion=goRutaCargaVoBoDoc";
//        p[1] = "nuAnn=" + pnuAnn;
//        p[2] = "nuEmi=" + pnuEmi;
//        p[3] = "tiOpe=" + ptiOpe;
//        p[4] = "nuSecFirma=" + pnuSecFirmaEmi;
//        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
//            eval("docs=" + data);
//            if (typeof(docs) !== "undefined" && typeof(docs.retval) !== 'undefined') {
//
//                if (docs.retval === "OK") {
//                    var retval = "";
//                    try {
//                        //retval = appletObj[0].cargarDocumento(docs.noUrl, pnoDoc);
//                        var param = {urlDoc: docs.noUrl, rutaDoc: pnoDoc};
//                        runApplet(appletsTramiteDoc.cargarDocumento, param, function(data) {
//                            retval = data;
//                            callback(retval);
//                            return;
//                        });
//                    } catch (ex) {
//                        alert_Danger("Firma!: ", "Fallo en subir documento al servidor");
//                        retval = "ERROR";
//                        resulCarga = retval;
//                        callback(resulCarga);
//                        return;
//                    }
//                } else {
//                    alert_Danger("Firma!: ", docs.retval);
//                    resulCarga = "ERROR";
//                    callback(resulCarga);
//                    return;
//                }
//            }
//        }, 'text', true, false, "POST");
//    }
//}
//
//function fu_changeEstadoDocVoBo(pEstadoDoc) {
//    var noForm='#documentoVoBoBean';
//    if(fn_verificarCamposDocVoBoObs(noForm)){
//        var cadenaJson=fn_buildSendJsontoServerDocVistoBueno(noForm);
//        if(fn_existeCambioEnDocVoBoBean(new Function('return ' + cadenaJson)())){
//            alert_Warning("Emisión :", "Necesita Grabar los cambios");
//        }else{
//            if(pEstadoDoc==='1'){
//                fn_changeToEnviarDocVoBo(noForm);
//            }else if(pEstadoDoc==='0'){
//                fn_changeToNoEnviarDocVoBo(noForm);
//            }
//        }      
//    }    
//}
//
//function fn_grabarDocVoBoObs(){
//    var noForm='#documentoVoBoBean';
//    if(fn_verificarCamposDocVoBoObs(noForm)){
//        var cadenaJson=fn_buildSendJsontoServerDocVistoBueno(noForm);
//        if(fn_existeCambioEnDocVoBoBean(new Function('return ' + cadenaJson)())){
//            ajaxCallSendJson("/srDocumentoVoBo.do?accion=goGrabarDocVoBo", cadenaJson, function(data) {
//                fn_rptaGrabarDocVoBoObs(data,noForm);
//            },'json', false, false, "POST");
//        }else{
//            alert_Info("", 'NO EXISTEN CAMBIOS.');
//        }      
//    }
//}
//
//function fn_rptaGrabarDocVoBoObs(data,noForm){
//    if(!!data){
//        if (data.coRespuesta === "1") {
//            jQuery('#envDocumentoVoboBean').val("0");
//            alert_Sucess("", "Documento grabado correctamente.");
//            var esDocEmi=jQuery(noForm).find('#esDocEmi').val();
//            if(data.emptyObs==="1"){
//                if(esDocEmi==="2"){
//                    fn_loadEditionDocVoBo(noForm,'0');
//                    jQuery(noForm).find('#esDocEmi').val('0');
//                }
//            }else if(data.emptyObs==="0"){
//                if(esDocEmi==='0'||esDocEmi==='7'){
//                    fn_loadEditionDocVoBo(noForm,'2');
//                    jQuery(noForm).find('#esDocEmi').val('2');
//                }
//            }
//            var esDocEmi=jQuery(noForm).find('#esDocEmi').val();
//            updTblPersonalVoboEdit(data.coDep,data.coEmp,esDocEmi,'#tblPersVoBoDocAdm');
//        }else{
//            alert_Warning("Emisión :", data.deRespuesta);
//        }
//    }
//}
//
//function fn_verificarCamposDocVoBoObs(noForm){
//    var vRetorno=1;
//    var vdeObs=allTrim(jQuery(noForm).find('#deObs').val());
//    var vmaxLengthDeObs=jQuery(noForm).find('#deObs').attr('maxlength');
//    if(!!vRetorno){
//        if(!!vdeObs){
//            if(!!vmaxLengthDeObs){
//                var nrolinesDeAsu = (vdeObs.match(/\n/g) || []).length;
//                if(vdeObs.length+nrolinesDeAsu > vmaxLengthDeObs){
//                    vRetorno=0;
//                    bootbox.alert("<h5>La Observación Excede el límite de "+vmaxLengthDeObs+" caracteres.</h5>", function() {
//                        bootbox.hideAll();
//                        jQuery(noForm).find('#deObs').focus();
//                    });
//                }
//            }              
//        }
//    }
//    if(!!vRetorno){
//        var nuAnn=allTrim(jQuery(noForm).find("#nuAnn").val());
//        var nuEmi=allTrim(jQuery(noForm).find("#nuEmi").val());
//        if(!!nuAnn&&!!nuEmi){
//            if(nuAnn.length<2||nuEmi.length<2){
//               vRetorno=0; 
//                bootbox.alert("<h5>Operación no Permitida.</h5>", function() {
//                    bootbox.hideAll();
//                });                
//            }
//        }else{
//            vRetorno=0;
//            bootbox.alert("<h5>Operación no Permitida.</h5>", function() {
//                bootbox.hideAll();
//            });             
//        }
//    }
//    if(!!vRetorno){
//        var esDocEmi=allTrim(jQuery(noForm).find("#esDocEmi").val());
//        if(!!esDocEmi){
//            if(esDocEmi!=="5"&&esDocEmi!=="7"&&esDocEmi!=="2"&&esDocEmi!=="0"&&esDocEmi!=="1"){
//                vRetorno=0;
//                bootbox.alert("<h5>Operación no Permitida.</h5>", function() {
//                    bootbox.hideAll();
//                });                             
//            }
//        }else{
//            vRetorno=0;
//            bootbox.alert("<h5>Operación no Permitida.</h5>", function() {
//                bootbox.hideAll();
//            });             
//        }
//    }    
//    return !!vRetorno;
//}
//
//function fn_buildSendJsontoServerDocVistoBueno(noForm) {
//    var result = '{';
//    result = result + '"nuAnn":"' + jQuery(noForm).find("#nuAnn").val() + '",';
//    result = result + '"nuEmi":"' + jQuery(noForm).find("#nuEmi").val() + '"';
//    var valEnvio = jQuery('#envDocumentoVoboBean').val();
//    if (valEnvio === "1") {
//        result = result + ',"docVoBoBean":' + JSON.stringify(getJsonFormDocVoBoBean(noForm));
//    }
//    return result + "}";
//}
//
//function fn_existeCambioEnDocVoBoBean(obj) {
//    var vReturn=0;
//    if (!!obj) {
//        if (!!obj.docVoBoBean) {
//            vReturn=1;
//        }
//    }
//    return !!vReturn;
//}
//
//function getJsonFormDocVoBoBean(noForm) {
//    var arrCampoBean = new Array();
//    arrCampoBean[0] = "deObs=deObs";
//    arrCampoBean[1] = "nuAnn=nuAnn";
//    arrCampoBean[2] = "nuEmi=nuEmi";
//    return getJsonFormBean(arrCampoBean,noForm);
//}
//
//function getJsonFormBean(arrCampoBean,noForm){
//    var o = {};
//    var a = jQuery(noForm).serializeArray();
//    jQuery.each(a, function() {
//        for (var i = 0; i < arrCampoBean.length; i++) {
//            var arrcampoBeanAux = arrCampoBean[i].split("=");
//            if (this.name === arrcampoBeanAux[1]) {
//                o[arrcampoBeanAux[0]] = this.value;
//            }
//        }
//    });
//    return o;    
//}
//
//function fn_agregarObsDocVoBo(){
//    var noForm='#documentoVoBoBean';
//    jQuery(noForm).find('#deObs').dblclick();
//    jQuery(noForm).find("#txtValChangeDocEsObsOpc").val("1");
//}

//function fn_verAnexoDocVoboLs(){
//    var pnuAnn = jQuery('#txtpnuAnn').val();
//    var pnuEmi = jQuery('#txtpnuEmi').val();
//    var pnuDes = "N";
//    fn_verAnexoDocVoBo(pnuAnn,pnuEmi,pnuDes);
//}
//
//function fn_verAnexoDocVoboBean(){
//    var noForm='#documentoVoBoBean';
//    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
//    var pnuDes = "N";
//    fn_verAnexoDocVoBo(pnuAnn,pnuEmi,pnuDes);
//}
//
//function fn_verAnexoDocVoBo(pnuAnn,pnuEmi,pnuDes){
//    if (!!pnuAnn&&!!pnuEmi) {
//        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
//    } else {
//        alert_Info("Emisión :", "Seleccione fila.");        
//    }    
//}
//
//function fn_verSeguimientoDocVoboBean() {
//    var noForm='#documentoVoBoBean';
//    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
//    var pnuDes = "N";
//    if (pnuAnn) {
//        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
//    } else {
//        //alert("Seleccione una fila de la lista");
//        alert_Info("Emisión :", "Seleccione una fila de la lista");
//    }
//}
//
//function fn_abrirDocumentoVoboBean(){
//    var noForm='#documentoVoBoBean';
//    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
//    var ptiOpe = "5";
//    if (!!pnuAnn && !!pnuEmi) {
//        var p = new Array();
//        p[0] = "accion=goDocRutaAbrirEmi";
//        p[1] = "nuAnn=" + pnuAnn;
//        p[2] = "nuEmi=" + pnuEmi;
//        p[3] = "tiOpe=" + ptiOpe;
//        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
//            var result;
//            eval("var docs=" + data);
//            if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
//                if (docs.retval === "OK") {
//                    var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
//                    runApplet(appletsTramiteDoc.abrirDocumento,param,function(data){
//                        result=data;
//                    });
//                } else {
//                    alert_Danger("!Repositorio : ", docs.retval);
//                }
//            }
//
//        }, 'text', false, false, "POST");
//    }
//}

function fn_regresarConsulDocsVoBoLs(pclickBtn) {
//    var noForm='#documentoVoBoBean';
    if (pclickBtn === "1") {
      fn_continueRegresarConsulDocVoBoLs();
    }
}

function fn_continueRegresarConsulDocVoBoLs(){
    jQuery('#divNewEmiDocumVoBo').toggle();                                
    jQuery('#divBusqDocumentoVoBo').toggle(); 
    fn_validarTipoBusquedaConsulDocsVoBo('#buscarDocumentoVoBoBean',jQuery('#buscarDocumentoVoBoBean').find('#tipBusqueda').val());
    jQuery('#divNewEmiDocumVoBo').html("");       
}

//function fn_firmarDocumentoVoBoBean() {
//    var noForm='#documentoVoBoBean';
//    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
//    jQuery(noForm).find("#nuSecuenciaFirma").val("");
//    jQuery("#rutaDocFirma").val("");
//    
//    var ptiOpe = "5";
//
//    if(fn_verificarCamposDocVoBoObs(noForm)){
//        var cadenaJson=fn_buildSendJsontoServerDocVistoBueno(noForm);
//        if(fn_existeCambioEnDocVoBoBean(new Function('return ' + cadenaJson)())){
//            alert_Warning("Emisión :", "Necesita Grabar los cambios");
//        }else{
//            var p = new Array();
//            p[0] = "accion=goRutaVoBoDoc";
//            p[1] = "nuAnn=" + pnuAnn;
//            p[2] = "nuEmi=" + pnuEmi;
//            p[3] = "tiOpe=" + ptiOpe;
//            ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
//                var result;
//                eval("var docs=" + data);
//                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
//                    if (docs.retval === "OK") {
//                        jQuery(noForm).find("#nuSecuenciaFirma").val(docs.nuSecFirma);
//                        jQuery("#noPrefijo").val(docs.noPrefijo);
//                        jQuery("#rutaDocFirma").val(docs.noDoc);
//                        jQuery("#inFirmaEmi").val(docs.inFirma);
//
//                        var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"2"};
//                        runApplet(appletsTramiteDoc.ejecutaFirma,param,function(data){
//                            result=data;
//                            fn_mostrarEnviarDocVoBo();
//                        });
//                    } else {
//                        alert_Danger("!Repositorio : ", docs.retval);
//                    }
//                }
//
//            }, 'text', false, false, "POST");            
//        }      
//    } 
//}

//function fn_verDocumentoVoBoBean() {
//    var noForm='#documentoVoBoBean';
//    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
//    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
//    var ptiOpe = "0";
//
//    if (!!pnuAnn&&!!pnuEmi) {
//        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
//    } else {
//        alert_Warning("Emisión :", "Necesita grabar los cambios.");        
//    }
//
//}

//function fn_loadEditionDocVoBo(noForm,esDocEmi){
//    if(!!noForm&&!!esDocEmi){
//        fn_ocultarEnviarDocVoBo();
//        var divEstadoDocVoBo = jQuery(noForm).find('#divEstDocVoBo').find('button').get(0); 
//        var existeImgChk=jQuery(divEstadoDocVoBo).find('span').length;     
//        if(existeImgChk===1){
//            jQuery(divEstadoDocVoBo).find('span').show();
//        }
//        divEstadoDocVoBo.removeAttribute('onclick');
//        jQuery(noForm).find('#divEstDocVoBo').removeClass('btn-group');
//        jQuery(noForm).find('#divEstDocVoBo').find('button').last().hide();
//        var objSpan = jQuery(noForm).find('#divEstDocVoBo').find('button').first().find('span');
//        if(objSpan.hasClass('glyphicon glyphicon-ok')){
//            objSpan.removeClass('glyphicon glyphicon-ok');
//        }        
//        jQuery(noForm).find('#ullsEstDocVoBo').html('');
//        divEstadoDocVoBo.removeAttribute('class');
//        jQuery(divEstadoDocVoBo).attr("class","btn btn-danger");
//        if(esDocEmi==="2"||esDocEmi==="7"||esDocEmi==="0"){
//            jQuery(noForm).find('#deObs').removeAttr('readonly'); 
//            //divEstadoDocVoBo.setAttribute('onclick','fu_changeEstadoDocVoBo(\'1\');');
//            if(esDocEmi==='7'||esDocEmi==='0'){
//                var existeSecondButton=jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).length;
//                if(existeSecondButton===1){
//                   jQuery(noForm).find('#divEstDocVoBo').find('button').get(1).removeAttribute('class');
//                   jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).attr("class","btn btn-danger dropdown-toggle");
//                }
//                jQuery(noForm).find('#divEstDocVoBo').find('button').first().html("<span/>  PARA V.B.");              
//                //(jQuery(noForm).find('#divEstDocVoBo').find('button').first().find('span')).addClass('glyphicon glyphicon-ok');                        
//                jQuery(noForm).find('#divEstDocVoBo').find('button').last().show();
//                jQuery(noForm).find('#ullsEstDocVoBo').append('<li><a href="#" onclick="fn_agregarObsDocVoBo();">OBSERVAR V.B.</a></li>');            
//                if(!jQuery(noForm).find('#divEstDocVoBo').hasClass('btn-group')){
//                  jQuery(noForm).find('#divEstDocVoBo').addClass('btn-group');          
//                }                
//            }else if(esDocEmi==='2'){
//                //jQuery(divEstadoDocVoBo).attr("class","btn btn-warning");
//                jQuery(noForm).find('#divEstDocVoBo').find('button').first().html("<span/>  DOC. OBSERVADO");              
//                //(jQuery(noForm).find('#divEstDocVoBo').find('button').first().find('span')).addClass('glyphicon glyphicon-ok');                        
//                var existeSecondButton=jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).length;
//                if(existeSecondButton===1){
//                   jQuery(noForm).find('#divEstDocVoBo').find('button').get(1).removeAttribute('class');
//                   jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).attr("class","btn btn-danger dropdown-toggle");
//                }                
//            }
//        }else {
//            //jQuery(divEstadoDocVoBo).attr("class","btn btn-success");            
//            jQuery(noForm).find('#divEstDocVoBo').find('button').first().html("<span/>  DOC. ENVIADO");                              
//            jQuery(noForm).find('#deObs').attr('readonly','true');
//            if(existeImgChk===1){
//                jQuery(divEstadoDocVoBo).find('span').hide();
//            }            
//            if(esDocEmi==='1'){
//                var existeSecondButton=jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).length;
//                if(existeSecondButton===1){
//                   jQuery(noForm).find('#divEstDocVoBo').find('button').get(1).removeAttribute('class');
//                   jQuery(jQuery(noForm).find('#divEstDocVoBo').find('button').get(1)).attr("class","btn btn-danger dropdown-toggle");
//                }
//                //(jQuery(noForm).find('#divEstDocVoBo').find('button').first().find('span')).addClass('glyphicon glyphicon-ok');                        
////                jQuery(noForm).find('#divEstDocVoBo').find('button').last().show();
////                jQuery(noForm).find('#ullsEstDocVoBo').append('<li><a href="#" onclick="fu_changeEstadoDocVoBo(\'0\');">SIN V.B.</a></li>');            
////                if(!jQuery(noForm).find('#divEstDocVoBo').hasClass('btn-group')){
////                  jQuery(noForm).find('#divEstDocVoBo').addClass('btn-group');          
////                }                  
//            }
//        }        
//    }
//}

//function fn_showObsEmpVoBo(coDep,coEmp){
//    var noForm='#documentoEmiBean';
//    var nuAnn=jQuery(noForm).find('#nuAnn').val();
//    var nuEmi=jQuery(noForm).find('#nuEmi').val();
//    var p = new Array();
//    p[0] = "accion=goBuscaObsEmpVobo";
//    p[1] = "pnuAnn="+ nuAnn;
//    p[2] = "pnuEmi="+ nuEmi;
//    p[3] = "pcoDep="+ coDep;
//    p[4] = "pcoEmp="+ coEmp;
//    ajaxCall("/srDocumentoVoBo.do", p.join("&"), function(data) {
//        if (!!data) {
//            $("body").append(data);
//        }
//    },'text', false, true, "POST");    
//}
//
//function fn_buscaReferenciaOrigenBusqDocVoBo() {
//    var p = new Array();
//    p[0] = "accion=goBuscaReferenciaOrigen";
//    p[1] = "pnuAnn=" + jQuery('#txtAnnioActual').val();
//    ajaxCall("/srDocumentoVoBo.do", p.join("&"), function(data) {
//        fn_rptaBuscaReferenciaOrigenBusqDocVoBo(data);
//    },'text', false, false, "POST");
//}

//function fn_rptaBuscaReferenciaOrigenBusqDocVoBo(XML_AJAX) {
//    if (XML_AJAX !== null) {
//        $("body").append(XML_AJAX);
//    }
//}

//function fn_iniConsRefOriDocVoboConsul(){
//            var tableaux = $('#tlbReferenOrig');
//        tableaux.find('tr').each(function(index, row) {
//            if(index == 0){
//                $(this).addClass('row_selected');                        
//                return false;
//            }
//        });
//        var searchOnTable = function(evento) {
//                var table = $('#tlbReferenOrig');
//                var value = this.value;
//                //alert(evento.which);
//                var isFirst = false;
//                var indexSelect = -1;
//                table.find('tr').each(function(index, row) {
//                        if ( $(this).hasClass('row_selected') ) {
//                            $(this).removeClass('row_selected');
//                        }
//                        //var allCells = $(row).find('td');
//                        var allCells = $(row).find('td');
//                        if(allCells.length > 0) {
//                                var found = false;
//                                allCells.each(function(index, td) {
//                                        var regExp = new RegExp(value, 'i');
//                                        if(regExp.test($(td).text())) {
//                                                found = true;
//                                                return false;
//                                        }
//                                });
//                                if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
//                                else {$(row).hide();}
//                        }
//                });
//                if(evento.which == 13){
//                    if(isFirst){
//                        var pdesDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
//                        var pcodDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
//                        fu_setDatoReferenOrigenDocVoboConsul(pcodDest,pdesDest);
//                    }
//                }
//        };        
//        $('#txtConsultaFind').keyup(searchOnTable);
//        $("#tlbReferenOrig tbody tr").click(function(e) {
//            var pdesDest= $(this).find("td:eq(1)").html();
//            var pcodDest= $(this).find("td:eq(2)").html();
//            fu_setDatoReferenOrigenDocVoboConsul(pcodDest,pdesDest);            
//        });    
//}

//function fu_setDatoReferenOrigenDocVoboConsul(cod, desc) {
//    var noForm='#buscarDocumentoVoBoBean';
//    jQuery(noForm).find('#txtRefOrigen').val(desc);
//    jQuery(noForm).find('#coRefOri').val(cod);
//    removeDomId('windowConsultaRefOri');
//}

//function fn_buscaElaboradoPorDocVoboConsul() {
//    ajaxCall("/srDocumentoVoBo.do?accion=goBuscaElaboradoPor", null, function(data) {
//        fn_rptaBuscaElaboradoPorDocVoboConsul(data);
//    },'text', false, false, "POST");
//}
//
//function fn_rptaBuscaElaboradoPorDocVoboConsul(XML_AJAX) {
//    if (XML_AJAX !== null) {
//        $("body").append(XML_AJAX);
//    }
//}

//function fn_iniConsDocVoboBusq(){
//    var tableaux = $('#tblElaboradoPor');
//    tableaux.find('tr').each(function(index, row) {
//        if(index === 0){
//            $(this).addClass('row_selected');                        
//            return false;
//        }
//    });
//    var searchOnTable = function(evento) {
//            var table = $('#tblElaboradoPor');
//            var value = this.value;
//            //alert(evento.which);
//            var isFirst = false;
//            var indexSelect = -1;
//            table.find('tr').each(function(index, row) {
//                    if ( $(this).hasClass('row_selected') ) {
//                        $(this).removeClass('row_selected');
//                    }
//                    //var allCells = $(row).find('td');
//                    var allCells = $(row).find('td:eq(0)');
//                    if(allCells.length > 0) {
//                            var found = false;
//                            allCells.each(function(index, td) {
//                                    var regExp = new RegExp(value, 'i');
//                                    if(regExp.test($(td).text())) {
//                                            found = true;
//                                            return false;
//                                    }
//                            });
//                            if (found === true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
//                            else {$(row).hide();}
//                    }
//            });
//            if(evento.which === 13){
//                if(isFirst){
//                    var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
//                    var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
//                    fu_setDatoEmpElaboraVoBoDocBusq(pcodDest,pdesDest);
//                }
//            }
//    };        
//    $('#txtConsultaFind').keyup(searchOnTable);
//    $("#tblElaboradoPor tbody tr").click(function(e) {
//        var pdesDest= $(this).find("td:eq(0)").html();
//        var pcodDest= $(this).find("td:eq(1)").html();
//        fu_setDatoEmpElaboraVoBoDocBusq(pcodDest,pdesDest);            
//    });    
//}

//function fu_setDatoEmpElaboraVoBoDocBusq(cod, desc) {
//    var noForm='#buscarDocumentoVoBoBean';
//    jQuery(noForm).find('#txtElaboradoPor').val(desc);
//    jQuery(noForm).find('#coEmpElabora').val(cod);
//    removeDomId('windowConsultaElaboradoPor');
//}

//function fn_changeToNoEnviarDocVoBo(noForm){
//    bootbox.dialog({
//        message: " <h5>¿ Seguro de Guardar los Cambios ?</h5>",
//        buttons: {
//            SI: {
//                label: "SI",
//                className: "btn-primary",
//                callback: function() {
//                    ajaxCall("/srDocumentoVoBo.do?accion=goChangeToSinVobo", jQuery(noForm).serialize(), function(data) {
//                        fn_rptaChangeToToNoEnviarDocVobo(data,noForm);
//                    },'json', false, false, "POST");                    
//                }                        
//            },
//            NO: {
//                label: "NO",
//                className: "btn-default"
//            }
//        }
//    });   
//    return false;
//}

//function fn_rptaChangeToToNoEnviarDocVobo(data,noForm){
//    if(!!data){
//        if (data.coRespuesta === "1") {
//            jQuery(noForm).find('#esDocEmi').val("0");
//            jQuery(noForm).find('#deObs').val("");
//            fn_loadEditionDocVoBo(noForm,'0');
//            updTblPersonalVoboEdit(data.coDep,data.coEmp,'0','#tblPersVoBoDocAdm');
//            alert_Sucess("Éxito!", "Transacción completada.");
//            jQuery(noForm).find("#nuSecuenciaFirma").val("");
//            fn_cargaToolBarDocVoBo(noForm);
//        } else {
//           bootbox.alert(data.deRespuesta);
//        }        
//    }
//}

//function updTblPersonalVoboEdit(coDep,CoEmp,esDocEmi,nomTbl){
//    if(!!coDep&&!!CoEmp&&!!esDocEmi&&!!nomTbl){
//        jQuery(nomTbl+" tbody tr").each(function(index, row) {
//                var coEmpAux=jQuery(row).find("td:eq(0)").text();
//                var coDepAux=jQuery(row).find("td:eq(1)").text();
//                if(!!coEmpAux&&!!coDepAux&&coEmpAux===CoEmp&&coDepAux===coDep){
//                    if(esDocEmi==='2'){
//                        jQuery(row).find('#spanVbTblEdit').parent().attr('class','label-warning');
//                        jQuery(row).find('#spanVbTblEdit').attr('title','Observado.');
//                    }else if(esDocEmi==='1'){
//                        jQuery(row).find('#spanVbTblEdit').parent().attr('class','label-success');
//                        jQuery(row).find('#spanVbTblEdit').attr('title','Con Visto Bueno.');
//                    }else{
//                        jQuery(row).find('#spanVbTblEdit').parent().attr('class','label-danger');
//                        jQuery(row).find('#spanVbTblEdit').attr('title','Sin Visto Bueno.');
//                    }
//                }
//        });  
//    }
//    return false;
//}

//function fn_mostrarEnviarDocVoBo(){
//    jQuery('#divEnviarDocVoBo').show();
//    var divEstadoDocVoBo = jQuery('#divEnviarDocVoBo').find('button').get(0);
//    if(jQuery(divEstadoDocVoBo).length===1){
//        divEstadoDocVoBo.setAttribute('onclick','fu_changeEstadoDocVoBo(\'1\');');        
//    }
//}
//
//function fn_ocultarEnviarDocVoBo(){
//    jQuery('#divEnviarDocVoBo').hide();
//    var divEstadoDocVoBo = jQuery('#divEnviarDocVoBo').find('button').get(0);
//    if(jQuery(divEstadoDocVoBo).length===1){
//        divEstadoDocVoBo.removeAttribute('onclick');
//    }
//}
/* para habilitar si se desea slider
$('.carousel').carousel();
*/
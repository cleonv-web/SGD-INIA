function fn_iniDocExtRecep(){
    jQuery('#buscarDocumentoExtRecepBean').find('#esFiltroFecha').val("1");//hoy
    jQuery('#buscarDocumentoExtRecepBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    
    //Se comenta para que la caja de texto no genere el autocompletado con cero
//    jQuery('#buscarDocumentoExtRecepBean').find('#busNumDoc').change(function() {
//        $(this).val(replicate($(this).val(), 6));
//    });
    var noForm='buscarDocumentoExtRecepBean';
    
    jQuery('#'+noForm).find("#coTipoPersona").find('option:last').remove();  
    //jQuery('#'+noForm).find("#coTipoPersona").find('option:last').remove();  
    jQuery('#'+noForm).find("#nuCopia").val("1");
        
    jQuery('#coTipoPersona').val("03");
    fn_changeTipoRemiDocExteRecep_(jQuery('#'+noForm).find("#coTipoPersona"),"1");    
    
    jQuery('#'+noForm).find("#coTipoPersona").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
    });
    
    jQuery('#'+noForm).find("#nuDniAux").change(function() {
        fn_getCiudadanoRemDocExtRecBus();
    });
    
    pnumFilaSelect=0;
    changeTipoBusqDocuExtRecep("0");
    
    
  
}

function changeTipoBusqDocuExtRecep(tipo) {
    jQuery('#buscarDocumentoExtRecepBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormBusDocExtRecep(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormBusDocExtRecep(tipo) {
    var validaFiltro = fu_validaFormBusDocExtRecep(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srMesaPartes.do?accion=goInicio", $('#buscarDocumentoExtRecepBean').serialize(), function(data) {
            refreshScript("divTablaDocExtRecep", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_validaFormBusDocExtRecep(tipo) {
    var valRetorno = "1";
    jQuery('#buscarDocumentoExtRecepBean').find('#feEmiIni').val(jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#buscarDocumentoExtRecepBean').find('#feEmiFin').val(jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
//    var pEsIncluyeFiltro = jQuery('#buscarDocumentoExtRecepBean').find('#esIncluyeFiltro1').is(':checked');
    var vFechaActual = jQuery('#txtFechaActual').val();
//    if(tipo==="0"){
//        valRetorno = fu_validaFechasFormBusDocExtRecep(vFechaActual);  
//    }else if(tipo==="1"){
//        valRetorno = fu_validaBusDocExtRecep();
//        if(valRetorno==="1"){
//            if(pEsIncluyeFiltro){
               valRetorno = fu_validaFechasFormBusDocExtRecep(vFechaActual); 
//            }else{
//               valRetorno = setAnnioNoIncludeFiltroDocExtRecep();
//            }
//        }
//    }    
    return valRetorno;
}

function setAnnioNoIncludeFiltroDocExtRecep(){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaMP('buscarDocumentoExtRecepBean');
    var pEsFiltroFecha = jQuery('#buscarDocumentoExtRecepBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery('#buscarDocumentoExtRecepBean').find("#feEmiIni").val();
        var vFeFinal = jQuery('#buscarDocumentoExtRecepBean').find("#feEmiFin").val();

        if(valRetorno==="1"){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fu_validaBusDocExtRecep() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoExtRecepBean();
    
    var vNroDocumento = jQuery('#buscarDocumentoExtRecepBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoExtRecepBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoExtRecepBean').find('#busAsunto').val();
    var vBusquedaTipoPersona = jQuery('#buscarDocumentoExtRecepBean').find('#busResultado').val();
    
    if((typeof(vNroDocumento)==="undefined" || vNroDocumento===null || vNroDocumento==="") &&
       (typeof(vNroExpediente)==="undefined" || vNroExpediente===null || vNroExpediente==="") &&
       (typeof(vAsunto)==="undefined" || vAsunto===null || vAsunto==="") &&
       (typeof(vBusquedaTipoPersona)==="undefined" || vBusquedaTipoPersona===null || vBusquedaTipoPersona===""))
    {
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    return valRetorno;
}

function fu_validaFechasFormBusDocExtRecep(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaMP('buscarDocumentoExtRecepBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoExtRecepBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }   
            
            var vFeInicio = jQuery('#buscarDocumentoExtRecepBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoExtRecepBean').find("#feEmiFin").val();
            if(valRetorno==="1"){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoExtRecepBean').find('#coAnnio').val(pAnnioBusq);                          
                    }                
                }               
            }           
                
           if(pEsFiltroFecha==="1"){
               //VALIDA FECHAS
            
               if (valRetorno==="1") {
                    if (vFeInicio===""){
                       bootbox.alert('Debe ingresar Fecha Del');
                        valRetorno="0";
                    } else {
                        valRetorno=fu_validaFechaConsulta(vFeInicio,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Del : "+ valRetorno);
                            valRetorno="0";
                        }
                    }
               }

                if (valRetorno==="1") {
                    //VALIDA FECHAS
                    if (vFeFinal===""){
                       bootbox.alert('Debe ingresar Fecha Al');
                        valRetorno="0";
                    } else {
                        if(pEsFiltroFecha==="3"){
                            vFechaActual = obtenerFechaUltimoDiaMes(vFechaActual);
                        }
                        valRetorno=fu_validaFechaConsulta(vFeFinal,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Al : "+ valRetorno);
                            valRetorno="0";
                        }
                    }
                }
                //se verifica que fechas DEL sea mayor o igual que fecha AL
                if (valRetorno==="1") {
                    var vCantidadDias =  getNumeroDeDiasDiferencia(vFeInicio,vFeFinal);
                    if (vCantidadDias < 0){
                      bootbox.alert("La Fecha Del debe ser mayor o igual a Fecha Al");
                       valRetorno="0";
                    }   
                }
            }
        }
    }
    return valRetorno;    
}

function fu_cleanBusDocExtRecep(tipo) {
    var noForm='#buscarDocumentoExtRecepBean';
    //if (tipo==="1") {
        jQuery(noForm).find("#busNumExpediente").val("");
        jQuery(noForm).find("#busNumDoc").val("");
        jQuery(noForm).find("#busAsunto").val("");
        jQuery(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        jQuery(noForm).find("#esIncluyeFiltro1").attr('checked',false);
    //} else if(tipo==="0"){
        jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    
        jQuery(noForm).find("#esFiltroFecha").val("1");//hoy
        jQuery(noForm).find("#coAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery(noForm).find("#coEstadoDoc option[value=5]").prop("selected", "selected");        
        jQuery(noForm).find("#coTipoEmisor option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coTipoDoc option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coRemitente").val("");
        jQuery(noForm).find("#txtRemitente").val("[TODOS]");
        jQuery(noForm).find("#coDepDestino").val("");
        jQuery(noForm).find("#txtDestinatario").val("[TODOS]");
        jQuery('#divRemPersonaJuri_').find('input').val("");
        jQuery('#divRemCiudadano_').find('input').val("");
        jQuery('#divRemOtros_').find('input').val("");  
        
        jQuery(noForm).find("#coTipoExp").val("");
        jQuery(noForm).find("#coOriDoc").val("");
        jQuery(noForm).find("#coDepOriRec").val("");
        jQuery(noForm).find("#txtDeDepOriRec").val("[TODOS]");
        jQuery(noForm).find("#busResultado").val("");
        
        
    //}
}

function fn_buscaRemitenteDocExtRecep(){
    
}

function fn_buscaDestinatarioDocExtEmi(){
    var noForm='#buscarDocumentoExtRecepBean';
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery(noForm).find('#coAnnio').val();
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },'text', false, false, "POST");    
}

function fu_iniTblDocExtRecep(){
    var oTable;
    oTable = $('#myTableFixed').dataTable({
        "bPaginate": false,
        /*"bLengthChange": false,*/
        "bFilter": false,
        "bSort": true,
        "bInfo": true,
        "bAutoWidth": true,
        "bDestroy": true,
        "sScrollY": "470px",
        "bScrollCollapse": false,
        "oLanguage": {
            "sZeroRecords": "No se encuentran registros.",
            "sInfo": "Registros: _TOTAL_ ",
            "sInfoEmpty": ""
        },        
        "aoColumns": [
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": true},
            {"sType": "fecha"},
            {"sType": "fecha"},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true}
        ]
    });

    jQuery.fn.dataTableExt.oSort['fecha-asc'] = function(a, b) {
        var ukDatea = a.split('/');
        var ukDateb = b.split('/');

        var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
        var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    };

    jQuery.fn.dataTableExt.oSort['fecha-desc'] = function(a, b) {
        var ukDatea = a.split('/');
        var ukDateb = b.split('/');

        var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
        var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

        return ((x < y) ? 1 : ((x > y) ? -1 : 0));
    };
    function showdivToolTip(elemento, text)
    {
        $('#divflotante').html(text);

        var x = elemento.left;
        var y = elemento.top + 24;
        $("#divflotante").css({left: x, top: y});

        document.getElementById('divflotante').style.display = 'block';
        return;
    }
  /*  $("#myTableFixed tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 5 || index === 6 || index === 8 || index === 9 || index === 10 || index === 11) {
                    showdivToolTip($('#divtitlemostrar').position(), $(this).html());
                }
            },
            function() {
                $('#divtitlemostrar').removeAttr('id');
                $('#divflotante').hide();
            }
    );*/
    $("#myTableFixed tbody tr").click(function(e) {
        if ($(this).hasClass('row_selected')) {
            //$(this).removeClass('row_selected');
            //jQuery('#txtpnuAnn').val("");
        }
        else {
            oTable.$('tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');

            /*obtiene datos*/
            //alert($(this).children('td')[12].innerHTML);
            //jQuery('#txtTextIndexSelect').val("0");
            if (typeof($(this).children('td')[0]) !== "undefined") {
                jQuery('#txtpnuAnn').val($(this).children('td')[0].innerHTML);
                jQuery('#txtpnuEmi').val($(this).children('td')[1].innerHTML);
                pnumFilaSelect = $(this).index();
            }
            //editarDocumentoRecep($(this).children('td')[12].innerHTML,$(this).children('td')[13].innerHTML,$(this).children('td')[15].innerHTML);
            /* comentado xq los botones de ver documento y ver anexos, ya se encuentran el la tabla
             var sData = $(this).find('td');
             passArrayToHtmlTabla("txtTextIndexSelect",sData);*/


        }
    });
    if(jQuery('#myTableFixed >tbody >tr').length > 0){
        //$('#myTableFixed >tbody >tr').eq(0).addClass('row_selected');
        //var pauxNumFilaSelect = typeof(pnumFilaSelect)!=="undefined"? pnumFilaSelect:0;
        try{
            if(jQuery('#myTableFixed >tbody >tr').eq(pnumFilaSelect).length === 1){
                jQuery('#myTableFixed >tbody >tr').eq(pnumFilaSelect).trigger("click");
                jQuery('#myTableFixed >tbody >tr').eq(pnumFilaSelect).focus();
            }else{
                pnumFilaSelect=0;
            }
        }catch(ex){
            pnumFilaSelect=0;
        }
    }
    jQuery('#myTableFixed >tbody >tr').keydown(function(evento){
       if(evento.which===38){//up
            if (jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).prev('tr').length===0){
                pnumFilaSelect=$("#myTableFixed >tbody >tr").length;
            }
            pnumFilaSelect--;
            jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).trigger("click");
            jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).focus();
       }else if(evento.which===40){//down
            if ($("#myTableFixed >tbody >tr").eq(pnumFilaSelect).next('tr').length===0){
                pnumFilaSelect=-1;
            }           
            pnumFilaSelect++;
            jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).trigger("click");               
            jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).focus();           
       }
       evento.preventDefault();
    });    
}

function fu_obtenerEsFiltroFechaMP(nameForm){
    var opt = jQuery('#'+nameForm).find('#coAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function upperCaseBuscarDocumentoExtRecepBean(){
    jQuery('#buscarDocumentoExtRecepBean').find('#busNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoExtRecepBean').find('#busNumDoc').val()));
    jQuery('#buscarDocumentoExtRecepBean').find('#busNumExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoExtRecepBean').find('#busNumExpediente').val()));
    jQuery('#buscarDocumentoExtRecepBean').find('#busAsunto').val(fu_getValorUpperCase(jQuery('#buscarDocumentoExtRecepBean').find('#busAsunto').val()));
}

function fn_iniDocExtRecepMp(){
    var noForm='documentoExtRecepBean';
    $("#progressAlta").hide();
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });    
    //YUAL
    jQuery('#'+noForm).find('#nuCopia').val('1');
     
    jQuery('#'+noForm).find("#tiEmi").find('option:last').remove();  
    fn_changeTipoRemiDocExteRecep(jQuery('#'+noForm).find("#tiEmi"),"1");
    jQuery('#'+noForm).find("#nuCorrExp").change(function() {
        fn_isExpedienteDuplicado(noForm);
    });
    jQuery('#'+noForm).find("#feVence,#coProceso").change(function() {
       jQuery('#envExpedienteEmiBean').val("1"); 
    });
    /*jQuery('#'+noForm).find("#coProceso").change(function() {
       jQuery('#envExpedienteEmiBean').val("1"); 
    });*/
    jQuery('#'+noForm).find("#nuCopia,#nuAnexo,#deDocSig,#nuFolios,#deAsu,#nSobre,#anioSobre,#coOriDoc,#nroDniTramitante,#coTraDest,#deObservacion").change(function() {
       jQuery('#envDocumentoEmiBean').val("1"); 
    });
    jQuery('#'+noForm).find("#sensible").change(function() {
       jQuery('#envDocumentoEmiBean').val("1");  
       var isCheckedSensible = $('#'+noForm).find('#sensible').is(':checked');
       if(isCheckedSensible){
           $('#'+noForm).find('#sensible').val('1');
       }else{
           $('#'+noForm).find('#sensible').val('0');
       }       
    });
    /*jQuery('#'+noForm).find("#nuFolios").change(function() {
       jQuery('#envDocumentoEmiBean').val("1"); 
    });    
    jQuery('#'+noForm).find("#deAsu").change(function() {
       jQuery('#envDocumentoEmiBean').val("1"); 
    });     */
    jQuery('#'+noForm).find("#nuDniAux").change(function() {
        fn_getCiudadanoRemDocExtRec("1");
    });
    jQuery('#'+noForm).find("#nuDniAuxRes").change(function() {
        fn_getCiudadanoRemDocExtRec("2");
    });
        
    jQuery('#'+noForm).find("#nuRucAux").change(function() {
        onclickBuscarProveedorDocExtRecep();
    });
    
    jQuery('#'+noForm).find("#tiEmi,#nuDniRes,#coOtrosRes,#deDireccion,#deCorreo,#telefono,#deCargo,#coTipoInv,#coTipoCongresista,#coComision").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
    });
    jQuery('#'+noForm).find("#notificado").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
       var isCheckedNotificado = $('#'+noForm).find('#notificado').is(':checked');
       if(isCheckedNotificado){
           $('#'+noForm).find('#notificado').val('1');
       }else{
           $('#'+noForm).find('#notificado').val('0');
       }       
    });
    jQuery('#'+noForm).find("#reiterativo").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
       var isCheckedReiterativo = $('#'+noForm).find('#reiterativo').is(':checked');
       if(isCheckedReiterativo){
           $('#'+noForm).find('#reiterativo').val('1');
       }else{
           $('#'+noForm).find('#reiterativo').val('0');
       }       
    });
    if (!!jQuery('#'+noForm).find("#nuEmi").val()) {
        var esDocEmi=jQuery('#'+noForm).find("#coEsDocEmiMp").val();
        if(!!esDocEmi&&esDocEmi==="5"){
            jQuery('#'+noForm).find("#coTipDocAdm").focus();
        }   
        fn_actualizaFormDocExtRecEstado(esDocEmi,noForm);        
    }else{
        jQuery('#'+noForm).find('#ullsEstDocEmiAdm').html('');        
        if(jQuery('#'+noForm).find('#estDocEmiAdm').hasClass('btn-group')){
          jQuery('#'+noForm).find('#estDocEmiAdm').removeClass('btn-group');          
        }
        jQuery('#'+noForm).find('#estDocEmiAdm').find('button').last().hide();
        /*if(jQuery('#'+noForm).find("#coTipDocAdm").val()===""){
            jQuery('#'+noForm).find("#coTipDocAdm").append('<option value="-1" selected="selected"/>');        
        }*/
        //jQuery('#'+noForm).find("#nuCorrExp").focus();        
        jQuery('#'+noForm).find("#coProceso").focus();        
    }    
}

function fn_getCiudadanoRemDocExtRec(tipo){
    var noForm='documentoExtRecepBean';
    var pnuDniAux;
    var vResult;
    if(tipo === "1"){
        pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
        vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    }else{
        pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAuxRes").val());
        vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    }
    //var pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
    //var vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    if(vResult){
    var p = new Array();
        p[0] = "accion=goBuscaCiudadano";
        p[1] = "pnuDoc=" + pnuDniAux;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaGetCiudadanoRemDocExtRec(data,noForm, tipo);
        },'json', false, false, "POST");   
    }    
}

function fn_rptaGetCiudadanoRemDocExtRec(data,noForm, tipo){
    if(data!==null){
        if(data.coRespuesta==="1"){
            var obj = data.ciudadanoBean;
            if(!!obj){
            if(tipo === "1"){
                jQuery('#'+noForm).find("#nuDni").val(obj.nuDoc);
                jQuery('#'+noForm).find("#nuDniAux").val(obj.nuDoc);
                jQuery('#'+noForm).find("#deNuDni").val(obj.nombre);
                
                jQuery('#'+noForm).find("#nuDniRes").val(obj.nuDoc);
                jQuery('#'+noForm).find("#nuDniAuxRes").val(obj.nuDoc);
                jQuery('#'+noForm).find("#deNuDniRes").val(obj.nombre);
                
                cargar_ubigeo(obj.idDepartamento.trim(),obj.idProvincia.trim(),obj.idDistrito.trim());
                jQuery('#'+noForm).find("#deDireccion").val(obj.deDireccion);
                jQuery('#'+noForm).find("#deCorreo").val(obj.deCorreo);
                jQuery('#'+noForm).find("#telefono").val(obj.telefono);
            }else{
                jQuery('#'+noForm).find("#nuDniRes").val(obj.nuDoc);
                jQuery('#'+noForm).find("#nuDniAuxRes").val(obj.nuDoc);
                jQuery('#'+noForm).find("#deNuDniRes").val(obj.nombre);
            }    
            jQuery('#'+noForm).find("#coTipDocAdm").focus(); 
            jQuery('#envRemitenteEmiBean').val("1");
        }else{
                bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuDniAux').focus();
                });                 
            }
        }else{
            bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
                fn_CleanDatosRemitenteDocExtRec('#'+noForm,'#divRemCiudadano');
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuDniAux').focus();
            });            
        }
    }
}

function fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux){
    var valRetorno = true;
    if(!!pnuDniAux){
        var lnuDniAux=pnuDniAux.length;
        var vValidaNumero = fu_validaNumero(pnuDniAux);
        if (vValidaNumero !== "OK") {
            valRetorno = false;
            bootbox.alert("<h5>Número DNI debe ser solo números.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuDniAux').focus();
            }); 
        }
        if(valRetorno){
            if(lnuDniAux!==8){
                valRetorno = false;
                bootbox.alert("<h5>Número DNI Inválido.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuDniAux').focus();
                });             
            }
        }
    }else{
        valRetorno = false;
        bootbox.alert("<h5>Digite Número DNI.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#nuDniAux').focus();
        });        
    }
    return valRetorno;
}
var tipoCiudadanoBus="1";
function onclickBuscarCiudadanoExtRec2(tipo){
    var noForm='buscarDocumentoExtRecepBean';
    var pDesCiudadano='';
    var pnuDniAux='';
    tipoCiudadanoBus = tipo;
    if(tipoCiudadanoBus === "1"){
        pnuDniAux=allTrim(jQuery('#nuDniAux').val());
        pDesCiudadano=allTrim(jQuery('#deNuDni').val());
    }else{
        pDesCiudadano=allTrim(jQuery('#deNuDniRes').val());
        pnuDniAux=allTrim(jQuery('#nuDniAuxRes').val());
    }
    
    if(!!pnuDniAux && pnuDniAux.length > 0){
        fn_getCiudadanoRemDocExtRec(tipo);
    }else{
        buscarCiudadanoEditDocExtRecepBus2(pDesCiudadano);
    }
}

function buscarCiudadanoEditDocExtRecepBus2(pDesCiudadano){
    
    var sDescCiudadano = allTrim(fu_getValorUpperCase(pDesCiudadano));
    //snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));
    sDescCiudadano=fn_getCleanTextExpReg(sDescCiudadano);
    sDescCiudadano=sDescCiudadano.trim();
    
    if(allTrim(sDescCiudadano).length >= 0 && allTrim(sDescCiudadano).length <= 1){  //El texto es entre 1 y 3 caracteres
            bootbox.alert("<h5>Debe ingresar como mínimo 2 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }         
    if (!!sDescCiudadano) {
        var rptValida = validaCaracteres(sDescCiudadano, "2");
        if (rptValida === "OK") {
            // Expresion regular
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(sDescCiudadano.match(re)){      
                var p = new Array();
                p[0] = "accion=goBuscaCiudadanoBusIni";
                p[1] = "sDescCiudadano=" + sDescCiudadano;
                ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                    fn_rptaBuscarCiudadanoEditDocExtRecep(data);
                },'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                  
            }                     
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>");            
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre del ciudadano.</h5>");                    
    }
    return false;
}

function fn_iniConsCiudadanoDest(){
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var nombres= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var dni= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                   
                    var pidDepartamento= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                    var pidProvincia= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(3)").html();
                    var pidDistrito= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(4)").html();
                    var pdeDireccion= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(5)").html();
                    var pdeCorreo= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(6)").html();
                    var ptelefono= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(7)").html();
                
                 fn_setDestinotlbCiudadano(nombres,dni,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);            
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var nombres= $(this).find("td:eq(0)").html();
        var dni= $(this).find("td:eq(1)").html();
        
        var pidDepartamento= $(this).find("td:eq(2)").html();
        var pidProvincia= $(this).find("td:eq(3)").html();
        var pidDistrito= $(this).find("td:eq(4)").html();
        var pdeDireccion= $(this).find("td:eq(5)").html();
        var pdeCorreo= $(this).find("td:eq(6)").html();
        var ptelefono= $(this).find("td:eq(7)").html();        
        fn_setDestinotlbCiudadano(nombres,dni,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);                 
    });
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var nombres= $(this).find("td:eq(0)").html();
            var dni= $(this).find("td:eq(1)").html();
            
            var pidDepartamento= $(this).find("td:eq(2)").html();
            var pidProvincia= $(this).find("td:eq(3)").html();
            var pidDistrito= $(this).find("td:eq(4)").html();
            var pdeDireccion= $(this).find("td:eq(5)").html();
            var pdeCorreo= $(this).find("td:eq(6)").html();
            var ptelefono= $(this).find("td:eq(7)").html();
            fn_setDestinotlbCiudadano(nombres,dni,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);               
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}


function fn_setDestinotlbCiudadano(nombres, dni,idDepartamento, idProvincia, idDistrito, deDireccion,deCorreo,telefono){
    var noForm='documentoExtRecepBean';
    if(tipoCiudadanoBus === "1"){
        jQuery('#'+noForm).find("#nuDni").val(dni);
        jQuery('#'+noForm).find("#nuDniAux").val(dni);
        jQuery('#'+noForm).find("#deNuDni").val(nombres);

        jQuery('#'+noForm).find("#nuDniRes").val(dni);
        jQuery('#'+noForm).find("#nuDniAuxRes").val(dni);
        jQuery('#'+noForm).find("#deNuDniRes").val(nombres);

        cargar_ubigeo(idDepartamento,idProvincia,idDistrito);
        jQuery('#'+noForm).find("#deDireccion").val(deDireccion);
        jQuery('#'+noForm).find("#deCorreo").val(deCorreo);
        jQuery('#'+noForm).find("#telefono").val(telefono);
    }else{
        jQuery('#'+noForm).find("#nuDniRes").val(dni);
        jQuery('#'+noForm).find("#nuDniAuxRes").val(dni);
        jQuery('#'+noForm).find("#deNuDniRes").val(nombres);
    }    
    
    jQuery('#'+noForm).find("#coTipDocAdm").focus();         
    jQuery('#envRemitenteEmiBean').val("1");
    removeDomId('windowConsultaOtroOri');
    
    return false;
    
}

function fn_isExpedienteDuplicado(noForm){
    //var valRetorno = fn_validarForm_isExpDuplicado(noForm);
    //if(valRetorno==="1"){
    var pnuAnnExp=jQuery('#'+noForm).find("#nuAnnExp").val();
    var pnuCorrExp=allTrim(jQuery('#'+noForm).find("#nuCorrExp").val());
    var pfeExp=allTrim(jQuery('#'+noForm).find("#feExp").val());
    if(!!pnuAnnExp&&!!pnuCorrExp&&!!pfeExp){
        var vValidaNumero = fu_validaNumero(pnuCorrExp); 
        if (vValidaNumero !== "OK") {
            bootbox.alert("<h5>Número de Expediente debe ser solo números.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuCorrExp').focus();
            });
            jQuery('#'+noForm).find('#nuCorrExp').val("");
            return false;
        }         
        pnuCorrExp=replicate(pnuCorrExp, 7);
        jQuery('#'+noForm).find("#nuCorrExp").val(pnuCorrExp);        
        var p = new Array();    
        p[0] = "accion=goIsExpedienteDuplicado";
        p[1] = "pnuAnnExp=" + pnuAnnExp;
        p[2] = "pnuCorrExp=" + pnuCorrExp; 
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            if(!!data){
                if(data.coRespuesta==="0"){
                   //jQuery('#'+noForm).find("#nuExpediente").val(jQuery('#'+noForm).find("#deDocSigG").val()+jQuery('#'+noForm).find("#nuAnnExp").val()+pnuCorrExp); 
                   jQuery('#'+noForm).find("#nuExpediente").val(jQuery('#'+noForm).find("#nuAnnExp").val()+"-"+pnuCorrExp); 
                   jQuery('#'+noForm).find("#nuCorrExp").val(pnuCorrExp);
                   if(jQuery('#'+noForm).find("#divRemPersonaJuri").is(':visible')){
                       jQuery('#'+noForm).find("#nuRucAux").focus();
                   }else if(jQuery('#'+noForm).find("#divRemCiudadano").is(':visible')){
                       jQuery('#'+noForm).find("#nuDniAux").focus();
                   }else if(jQuery('#'+noForm).find("#divRemOtros").is(':visible')){
                       jQuery('#'+noForm).find("#deNomOtros").focus();
                   }
                }else{
                   bootbox.alert("<h5>Número de Expediente <strong>\""+pnuCorrExp+"\"</strong> Duplicado.</h5>", function() {
                       bootbox.hideAll();
                       jQuery('#'+noForm).find('#nuCorrExp').val("");
                       jQuery('#'+noForm).find('#nuCorrExp').focus();
                    });      
                }
            }
        }, 'json', false, false, "POST");    
    //}
    }else{
        bootbox.alert("<h5>Indicar Número de Expediente.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#nuCorrExp').focus();
        });        
    }
}

function fn_goNuevoDocExtRecep(){
    ajaxCall("/srMesaPartes.do?accion=goNewDocumentoExternoRecep",'',function(data){
            jQuery('#divBuscarDocExtRecep').hide();
            jQuery('#divDocExtRecep').show();
            refreshScript("divDocExtRecep", data);
            jQuery('#divTablaDocExtRecep').html("");
            jQuery('#envExpedienteEmiBean').val("1");
            jQuery('#envDocumentoEmiBean').val("1");
            jQuery('#envRemitenteEmiBean').val("1");
            fn_cargaToolBarDocExtRecep();
            //INI_DESPACHO_PRESIDENCIAL
            $(".esCongresoDatosCong").hide();                        
            $(".esCongresoTipoInv").hide();
            $(".esCongresoNumSobre").hide();
            $("#emiResp option[value='02']").remove();
            $("#emiResp option[value='']").remove();         
            $("#coOriDoc option[value='03']").attr("selected","selected");
            //FIN_DESPACHO_PRESIDENCIAL
    }, 'text', false, false, "POST");          
}

function fn_goEditarDocExtRecep(pnuAnn,pnuEmi){
    if(jQuery('#divTablaDocExtRecep').length===1){
        if(!!pnuAnn&&!!pnuEmi){
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + pnuEmi;
            ajaxCall("/srMesaPartes.do?accion=goEditDocumentoExtRec", p.join("&"), function(data) {
                jQuery('#divBuscarDocExtRecep').hide();
                jQuery('#divDocExtRecep').show();
                refreshScript("divDocExtRecep", data);
                jQuery('#divTablaDocExtRecep').html("");
                fn_cargaToolBarDocExtRecep();                
                $("#emiResp option[value='02']").remove();
                $("#emiResp option[value='']").remove();
//                if(!!!$("#nuDniRes").val().trim()){
//                    $("#emiResp").val('03');
//                    $("#emiResp").change();
//                }else if(!!!$("#coOtrosRes").val().trim()){
//                    $("#emiResp").val('04');
//                    $("#emiResp").change();
//                }
                //$("#coTipoExp").change();
            }, 'text', false, false, "POST");
        }
    }
}

function fn_preGoEditarDocExtRecep(){
    var pnuAnn = allTrim(jQuery('#txtpnuAnn').val());    
    var pnuEmi = allTrim(jQuery('#txtpnuEmi').val());
    fn_goEditarDocExtRecep(pnuAnn,pnuEmi);
}

function fn_goEditarDocExtRecepClick(pnuAnn,pnuEmi){
    fn_goEditarDocExtRecep(pnuAnn,pnuEmi);
}

function fn_changeTipoRemiDocExteRecep(cmbTipoRemite,esIni){
    var noForm='documentoExtRecepBean';
    var pcoTipoRemi=$(cmbTipoRemite).val();
    if(!!pcoTipoRemi){
        if(esIni==="0"){
            jQuery('#'+noForm).find('#nuCorDoc').val("");
            jQuery('#envRemitenteEmiBean').val("1");
            jQuery('#envDocumentoEmiBean').val("1");
            $("#emiResp").change();
            $(".esCongresoDatosCong").hide();                        
            $(".esCongresoTipoInv").hide();
            $(".esCongresoNumSobre").hide();
            limpiarRemitente();
        }else{
            var tiEmiResp = jQuery('#'+noForm).find('#emiResp').val();
            if(tiEmiResp=="03"){
                $(".tipoEmiRespCiudadano").show();
                $(".tipoEmiRespOtros").hide();
            }else if(tiEmiResp=="04"){        
                $(".tipoEmiRespCiudadano").hide();
                $(".tipoEmiRespOtros").show();
            } 
            fn_iniVerificaCongreso();
        }
        $("#divRemPersonaJuri").hide();
        $("#divRemCiudadano").hide();
        $("#divRemOtros").hide(); 
                
        if(pcoTipoRemi==='02'){//juridica
            $("#divRemPersonaJuri").show();
             jQuery('#divRemCiudadano').find('input').val("");
             jQuery('#divRemOtros').find('input').val("");            
             jQuery('#'+noForm).find('#nuRucAux').focus();
             //INI_DESPACHO_PRESIDENCIAL
             $(".tipoEmiRespCiudadano button").removeAttr('disabled');             
             $("#nuDniAuxRes").removeAttr('readonly');
             $("#deNuDniRes").removeAttr('readonly');
             $(".tipoEmiRespOtros button").removeAttr('disabled');
             $(".tipoEmiRespOtros input").removeAttr('readonly');
             $("#emiResp").removeAttr('disabled');
             //FIN_DESPACHO_PRESIDENCIAL
        }else if(pcoTipoRemi==='03'){//ciudadano
            $("#divRemCiudadano").show();
             jQuery('#divRemPersonaJuri').find('input').val("");
             jQuery('#divRemOtros').find('input').val("");            
             jQuery('#'+noForm).find('#nuDniAux').focus();
             //INI_DESPACHO_PRESIDENCIAL
             $("#emiResp").val("03");
             $(".tipoEmiRespCiudadano button").attr('disabled','disabled'); 
             $("#nuDniAuxRes").attr('readonly','readonly'); 
             $("#deNuDniRes").attr('readonly','readonly'); 
             $("#emiResp").attr('disabled','disabled');
             $(".tipoEmiRespCiudadano").show();
             $(".tipoEmiRespOtros").hide();
            //FIN_DESPACHO_PRESIDENCIAL
        }else if(pcoTipoRemi==='04'){//otros
            $("#divRemOtros").show();
             jQuery('#divRemPersonaJuri').find('input').val("");
             jQuery('#divRemCiudadano').find('input').val("");            
             jQuery('#'+noForm).find('#deNomOtros').focus(); 
             //INI_DESPACHO_PRESIDENCIAL
             $("#emiResp").val("04");
             $(".tipoEmiRespOtros button").attr('disabled','disabled');              
             $(".tipoEmiRespOtros input").attr('readonly','readonly'); 
             $("#emiResp").attr('disabled','disabled');
             $(".tipoEmiRespCiudadano").hide();
             $(".tipoEmiRespOtros").show();             
            //FIN_DESPACHO_PRESIDENCIAL
        }
    }
}

function fn_changeTipoDocExtRecep(cmbTipoDoc){
    var coTiDoc=jQuery(cmbTipoDoc).val();
    var coTiDocLast=jQuery(cmbTipoDoc).find('option:last').val();
    if(coTiDocLast==="-1"){
        jQuery(cmbTipoDoc).find('option:last').remove();
    }
    jQuery('#envDocumentoEmiBean').val("1");
}

function fu_grabarRecepDocExt(){
     var noForm='documentoExtRecepBean';
            debugger;
            var esDocEmi=jQuery('#'+noForm).find("#coEsDocEmiMp").val();
            if(!!esDocEmi&&(esDocEmi==="5"||esDocEmi==="7"||esDocEmi==="8")){
                var valRetorno=validarFormDocExtRecep(noForm);
                if(valRetorno==="1"){
                    var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
                    //verificar si necesita grabar el documento.
                    var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
                    var nrpta = rpta.substr(0,1);
                    if (nrpta === "1") {
                        var pnuEmi=jQuery('#'+noForm).find("#nuEmi").val();
                        var pnuAnn=jQuery('#'+noForm).find("#nuAnn").val();
                        var objSizeTabla = fn_getSizeTblsDocExt(new Function('return ' + cadenaJson)());
                        if(!!pnuEmi){//upd
                            ajaxCallSendJson("/srMesaPartes.do?accion=goGrabarDocumento", cadenaJson, function(data) {
                                    if(jQuery('#'+noForm).find('#coProceso').val()!='0000'){
                                         fn_getVerifReqExpedienteDocExtRec();
                                      }
                                      else{
                                fn_rptaActualizarDocExt(data,noForm,objSizeTabla);
                                
                               
                            }
             
                          
                            },'json', false, false, "POST");          
                        }else{//ins
                            ajaxCallSendJson("/srMesaPartes.do?accion=goGrabarDocumento", cadenaJson, function(data) {
                                fn_rptaRegistrarDocExt(data,noForm,objSizeTabla);
                                if(jQuery('#'+noForm).find('#coProceso').val()!='0000'){
                                         fn_getVerifReqExpedienteDocExtRec();
                                      }
                                      else{
                                            fn_rptaRegistrarDocExt(data,noForm,objSizeTabla);
                                          
                                      }


                            },'json', false, false, "POST");                  
                        }            
                    }else{
                         if(jQuery('#'+noForm).find('#coProceso').val()!='0000'){
                                         fn_getVerifReqExpedienteDocExtRec();
                                      }
                                      else{
                                            alert_Info("", rpta);   
                                      }
                      
                    }         
                }


            }else{
               bootbox.alert('Operación no Permitida.');
            }
    
}

function fn_getSizeTblsDocExt(objTrxDocumentoEmiBean){
    var lref=objTrxDocumentoEmiBean.lstReferencia.length;
    var ldes=objTrxDocumentoEmiBean.lstDestinatario.length;
    return obj = {sizeDest: ldes, sizeRef: lref};
}

function fn_rptaActualizarDocExt(dataJson,noForm,objSizeTabla){
   if(dataJson!==null){
       if(dataJson.coRespuesta==="1"){
            var objDocEmiBean=dataJson.documentoEmiBean;
            
            jQuery('#envExpedienteEmiBean').val("0");
            jQuery('#envDocumentoEmiBean').val("0");
            jQuery('#envRemitenteEmiBean').val("0");         
            fn_actualizarTblDestinoDocExtRecep('tblDestEmiDocAdm',dataJson.lstDestinatario,objSizeTabla.sizeDest);
            fn_actualizarTblReferenciaDocExtRecep('tblRefEmiDocAdm',dataJson.lstReferencia,objSizeTabla.sizeRef);
            if(objDocEmiBean!==null&&!!objDocEmiBean){
                jQuery('#'+noForm).find('#nuCorDoc').val(objDocEmiBean.nuCorDoc);
                jQuery('#'+noForm).find('#coEsDocEmiMp').val(objDocEmiBean.esDocEmi);                    
                fn_actualizaFormDocExtRecEstado(objDocEmiBean.esDocEmi,noForm);
            }    
            alert_Sucess("Éxito!", "Documento grabado correctamente.");           
       }else{
            alert_Danger("Mesa Partes: ",dataJson.deRespuesta);
        }
   } 
}

function fn_rptaRegistrarDocExt(dataJson,noForm,objSizeTabla){
    if(dataJson!==null){
        if(dataJson.coRespuesta==="1"){
            var objDocEmiBean=dataJson.documentoEmiBean;
            if(objDocEmiBean!==null&&!!objDocEmiBean){
                jQuery('#envExpedienteEmiBean').val("0");
                jQuery('#envDocumentoEmiBean').val("0");
                jQuery('#envRemitenteEmiBean').val("0");                   
                jQuery('#'+noForm).find('#nuAnn').val(objDocEmiBean.nuAnn);
                jQuery('#'+noForm).find('#nuEmi').val(objDocEmiBean.nuEmi);
                jQuery('#'+noForm).find('#nuAnnExp').val(objDocEmiBean.nuAnnExp);
                jQuery('#'+noForm).find('#nuSecExp').val(objDocEmiBean.nuSecExp);
                jQuery('#'+noForm).find('#nuCorDoc').val(objDocEmiBean.nuCorDoc);
                jQuery('#'+noForm).find('#coEsDocEmiMp').val(objDocEmiBean.esDocEmi);
                jQuery('#'+noForm).find('#nuCorrExp').val(objDocEmiBean.nuCorrExp);
                jQuery('#'+noForm).find('#nuExpediente').val(objDocEmiBean.nuExpediente);

                fn_actualizarTblDestinoDocExtRecep('tblDestEmiDocAdm',dataJson.lstDestinatario,objSizeTabla.sizeDest);
                fn_actualizarTblReferenciaDocExtRecep('tblRefEmiDocAdm',dataJson.lstReferencia,objSizeTabla.sizeRef);
                fn_actualizaFormDocExtRecEstado(objDocEmiBean.esDocEmi,noForm);
                alert_Sucess("Éxito!", "Documento grabado correctamente.");
            }else{
               bootbox.alert("ERROR REGISTRANDO DOCUMENTO MESA PARTES.");
            }
        }else{
            alert_Danger("Mesa Partes: ",dataJson.deRespuesta);
        }
    }
}

function fn_actualizaFormDocExtRecEstado(esDocEmi,noForm){
   

    jQuery('#divEmitirDoc').hide();
    var btnEmitirDoc = jQuery('#divEmitirDoc').find('button').get(0);
    btnEmitirDoc.removeAttribute('onclick');    
    var btnEstadoDocExtRec = jQuery('#'+noForm).find('#estDocEmiAdm').find('button').get(0);           
    btnEstadoDocExtRec.removeAttribute('onclick');
    jQuery('#'+noForm).find('#estDocEmiAdm').removeClass('btn-group');
    jQuery('#'+noForm).find('#estDocEmiAdm').find('button').last().hide();
    var objSpan = jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().find('span');
    if(objSpan.hasClass('glyphicon glyphicon-ok')){
        objSpan.removeClass('glyphicon glyphicon-ok');
    }
    jQuery('#'+noForm).find('#feExp').prop('readonly','true');
    jQuery('#'+noForm).find('#feExp').attr('readonly','true');
    jQuery('#'+noForm).find('#nuCorrExp').prop('readonly','true');
    jQuery('#'+noForm).find('#nuCorrExp').attr('readonly','true');  
    
    var inEditarExp=jQuery('#inEditarExp').val();
    
    if(esDocEmi==="5" || esDocEmi === "7"|| (esDocEmi === "8" && inEditarExp==="1")){//5 EN REGISTRO
        jQuery('#'+noForm).find('#feVence').removeProp('readonly');
        jQuery('#'+noForm).find('#feVence').removeAttr('readonly');           
        jQuery('#'+noForm).find('#coProceso').removeProp('disabled');
        jQuery('#'+noForm).find('#coProceso').removeAttr('disabled');    
        jQuery('#'+noForm).find('#tiEmi').removeProp('disabled');
        jQuery('#'+noForm).find('#tiEmi').removeAttr('disabled');
        jQuery('#'+noForm).find('#nuRucAux').removeProp('readonly');
        jQuery('#'+noForm).find('#nuRucAux').removeAttr('readonly');  
        jQuery('#'+noForm).find('#deNuRuc').removeProp('readonly');
        jQuery('#'+noForm).find('#deNuRuc').removeAttr('readonly');

        (jQuery('#'+noForm).find('#deNuRuc').parent()).find('button').show();
        if(!jQuery('#'+noForm).find('#deNuRuc').hasClass('inp-xs-grup')){
          jQuery('#'+noForm).find('#deNuRuc').addClass('inp-xs-grup');          
        }        
        jQuery('#'+noForm).find('#nuDniAux').removeProp('readonly');
        jQuery('#'+noForm).find('#nuDniAux').removeAttr('readonly');    

        (jQuery('#'+noForm).find('#nuDniAux').parent()).find('button').show();
        if(!jQuery('#'+noForm).find('#nuDniAux').hasClass('inp-xs-grup')){
          jQuery('#'+noForm).find('#nuDniAux').addClass('inp-xs-grup');          
        } 
        jQuery('#'+noForm).find('#deNomOtros').removeProp('readonly');
        jQuery('#'+noForm).find('#deNomOtros').removeAttr('readonly');

        (jQuery('#'+noForm).find('#deNomOtros').parent()).find('button').show();
        if(!jQuery('#'+noForm).find('#deNomOtros').hasClass('inp-xs-grup')){
          jQuery('#'+noForm).find('#deNomOtros').addClass('inp-xs-grup');          
        } 
        jQuery('#'+noForm).find('#coTipDocAdm').removeProp('disabled');
        jQuery('#'+noForm).find('#coTipDocAdm').removeAttr('disabled');    
        jQuery('#'+noForm).find('#deDocSig').removeProp('readonly');
        jQuery('#'+noForm).find('#deDocSig').removeAttr('readonly');       
        jQuery('#'+noForm).find('#nuFolios').removeProp('readonly');
        jQuery('#'+noForm).find('#nuFolios').removeAttr('readonly');     
        jQuery('#'+noForm).find('#deAsu').removeProp('readonly');
        jQuery('#'+noForm).find('#deAsu').removeAttr('readonly');
        jQuery('#'+noForm).find('#ullsEstDocEmiAdm').html('');
        if(esDocEmi==="5"){
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocExtRec(\'7\');">PARA VERIFICAR</a></li>');
           //yual
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocExtRec(\'8\');">OBSERVADO</a></li>');
            //btnEstadoDocExtRec.setAttribute('onclick','fu_changeEstadoDocExtRec(\'0\');');
            jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().html("<span/>  EN REGISTRO");              
            //(jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().find('span')).addClass('glyphicon glyphicon-ok');                
        }else{
          
            //YUAL
            if(esDocEmi==="8") {
   
            //btnEstadoDocExtRec.setAttribute('onclick','fu_changeEstadoDocExtRec(\'0\');');
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocExtRec(\'5\');">EN REGISTRO</a></li>');
            jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().html("<span/> OBSERVADO"); 
            btnEmitirDoc.removeAttribute('onclick');
            btnEmitirDoc.setAttribute('onclick','fu_changeEstadoDocExtRec(\'0\');');
            jQuery('#divEmitirDoc').show();  
            
            }
            else{
            //btnEstadoDocExtRec.setAttribute('onclick','fu_changeEstadoDocExtRec(\'0\');');
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocExtRec(\'5\');">EN REGISTRO</a></li>');
            jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().html("<span/>  PARA VERIFICAR"); 
            btnEmitirDoc.removeAttribute('onclick');
            btnEmitirDoc.setAttribute('onclick','fu_changeEstadoDocExtRec(\'0\');');
            jQuery('#divEmitirDoc').show(); 
            }
        }
        if(!jQuery('#'+noForm).find('#estDocEmiAdm').hasClass('btn-group')){
            jQuery('#'+noForm).find('#estDocEmiAdm').addClass('btn-group');          
        }
        jQuery('#'+noForm).find('#estDocEmiAdm').find('button').last().show();
        
        jQuery("#tblRefEmiDocAdm").find("input,textarea").removeProp('readonly');
        jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").removeProp('disabled');
        jQuery("#tblRefEmiDocAdm").find("input,textarea").removeAttr('readonly');
        jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").removeAttr('disabled');          
        jQuery("#tblRefEmiDocAdm tbody tr").each(function(index,row){
          if(index > 0){
             $(row).find("button").first().show();
          }
        });
        jQuery('#tblRefEmiDocAdm').parents(':eq(4)').find('td').first().show();

        jQuery("#tblDestEmiDocAdm").find("select,button").removeProp('disabled');
        jQuery("#tblDestEmiDocAdm").find("input,textarea").removeProp('readonly');             
        jQuery("#tblDestEmiDocAdm").find("select,button").removeAttr('disabled');
        jQuery("#tblDestEmiDocAdm").find("input,textarea").removeAttr('readonly');                       
        jQuery('#tblDestEmiDocAdm').parents(':eq(4)').find('td').first().show();         
        
    }else{
        jQuery('#'+noForm).find('#feVence').prop('readonly','true');
        jQuery('#'+noForm).find('#feVence').attr('readonly','true');           
        jQuery('#'+noForm).find('#coProceso').prop('disabled','true');
        jQuery('#'+noForm).find('#coProceso').attr('disabled','true');    
        jQuery('#'+noForm).find('#tiEmi').prop('disabled','true');
        jQuery('#'+noForm).find('#tiEmi').attr('disabled','true');
        jQuery('#'+noForm).find('#nuRucAux').prop('readonly','true');
        jQuery('#'+noForm).find('#nuRucAux').attr('readonly','true');  
        jQuery('#'+noForm).find('#deNuRuc').prop('readonly','true');
        jQuery('#'+noForm).find('#deNuRuc').attr('readonly','true');
        (jQuery('#'+noForm).find('#deNuRuc').parent()).find('button').hide();
        if(jQuery('#'+noForm).find('#deNuRuc').hasClass('inp-xs-grup')){
          jQuery('#deNuRuc').removeClass('inp-xs-grup');          
        }
        jQuery('#'+noForm).find('#nuDniAux').prop('readonly','true');
        jQuery('#'+noForm).find('#nuDniAux').attr('readonly','true');    
        (jQuery('#'+noForm).find('#nuDniAux').parent()).find('button').hide();
        if(jQuery('#'+noForm).find('#nuDniAux').hasClass('inp-xs-grup')){
          jQuery('#'+noForm).find('#nuDniAux').removeClass('inp-xs-grup');          
        }
        jQuery('#'+noForm).find('#deNomOtros').prop('readonly','true');
        jQuery('#'+noForm).find('#deNomOtros').attr('readonly','true');    
        (jQuery('#'+noForm).find('#deNomOtros').parent()).find('button').hide();
        if(jQuery('#'+noForm).find('#deNomOtros').hasClass('inp-xs-grup')){
          jQuery('#deNomOtros').removeClass('inp-xs-grup');          
        }
        jQuery('#'+noForm).find('#coTipDocAdm').prop('disabled','true');
        jQuery('#'+noForm).find('#coTipDocAdm').attr('disabled','true');    
        jQuery('#'+noForm).find('#deDocSig').prop('readonly','true');
        jQuery('#'+noForm).find('#deDocSig').attr('readonly','true');       
        jQuery('#'+noForm).find('#nuFolios').prop('readonly','true');
        jQuery('#'+noForm).find('#nuFolios').attr('readonly','true');     
        jQuery('#'+noForm).find('#deAsu').prop('readonly','true');
        jQuery('#'+noForm).find('#deAsu').attr('readonly','true');       
        jQuery("#tblRefEmiDocAdm").find("input,textarea").prop('readonly','true');
        jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").prop('disabled','true');
        jQuery("#tblRefEmiDocAdm").find("input,textarea").attr('readonly','true');
        jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").attr('disabled','true');          
        jQuery("#tblRefEmiDocAdm tbody tr").each(function(index,row){
          if(index > 0){
             $(row).find("button").first().hide();
          }
        });
        jQuery('#tblRefEmiDocAdm').parents(':eq(4)').find('td').first().hide();

        jQuery("#tblDestEmiDocAdm").find("select,button").prop('disabled','true');
        jQuery("#tblDestEmiDocAdm").find("input,textarea").prop('readonly','true');             
        jQuery("#tblDestEmiDocAdm").find("select,button").attr('disabled','true');
        jQuery("#tblDestEmiDocAdm").find("input,textarea").attr('readonly','true');                       
        jQuery('#tblDestEmiDocAdm').parents(':eq(4)').find('td').first().hide();         
        if(esDocEmi==="0"){//REGISTRADO
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').html('');
            jQuery('#'+noForm).find('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocExtRec(\'5\');">EN REGISTRO</a></li>');
            if(!jQuery('#'+noForm).find('#estDocEmiAdm').hasClass('btn-group')){
              jQuery('#'+noForm).find('#estDocEmiAdm').addClass('btn-group');          
            }
            jQuery('#'+noForm).find('#estDocEmiAdm').find('button').last().show();
            jQuery('#'+noForm).find('#estDocEmiAdm').find('button').first().text('REGISTRADO');            
        }
        
    }
}

function fn_actualizarTblDestinoDocExtRecep(nomTbl,lsObj,destEnv){
   if(lsObj!==null&&lsObj.length===destEnv&&!!lsObj){
       //var indexLsObj=0;
//        $.each(lsObj, function(i, item) {
            $("#"+nomTbl+" tbody tr").each(function(index, row) {            
                if (index > 0/* && !$(row).hasClass('oculto')*/) {
                   if($(row).hasClass('oculto')){
                       $(row).remove();
                   }else{
                       var accionBD=$(row).find('td:eq(11)').html();
                       if(accionBD!=="BD"){
                        var coDependencia=$(row).find('td:eq(1)').html();
                        var coEmpleado=$(row).find('td:eq(5)').html();
                        if(accionBD==="DEL"){
                           $(row).remove(); 
                        }else{
                           $.each(lsObj, function(i, obj) {
                               if(obj.accionBD==="INS"||obj.accionBD==="UPD"){
                                    if(coDependencia===obj.coDependencia&&coEmpleado===obj.coEmpleado){
                                     $(row).find('td:eq(9)').html(obj.nuDes);                        
                                     $(row).find('td:eq(11)').html("BD");
                                    }                                   
                               }
                           });
                        }
                       }
                   }
                }
            });
//        });
   }
}

function fn_actualizarTblReferenciaDocExtRecep(nomTbl,lsObj,refEnv){
   if(lsObj!==null&&lsObj.length===refEnv&&!!lsObj){
       //var indexLsObj=0;
//        $.each(lsObj, function(i, item) {
            $("#"+nomTbl+" tbody tr").each(function(index, row) {            
                if (index > 0/* && !$(row).hasClass('oculto')*/) {
                   if($(row).hasClass('oculto')){
                       $(row).remove();
                   }else{
                       var accionBD=$(row).find('td:eq(9)').html();
                       if(accionBD!=="BD"){
                        var nuAnn=$(row).find('td:eq(6)').html();
                        var nuEmi=$(row).find('td:eq(7)').html();
                        if(accionBD==="DEL"){
                           $(row).remove(); 
                        }else{
                           $.each(lsObj, function(i, obj) {
                               if(obj.accionBD==="INS"||obj.accionBD==="UPD"){
                                    if(nuAnn===obj.nuAnn&&nuEmi===obj.nuEmi){
                                     $(row).find('td:eq(8)').html(obj.coRef);                        
                                     $(row).find('td:eq(9)').html("BD");
                                    }                                   
                               }
                           });                            
                        }
                       }
                   }
                }
            });
//        });
   }    
}

function validarFormDocExtRecep(noForm){
   var valRetorno ="1" ;   
   var vValidaNumero = "";
   var rucCongreso = jQuery("#rucCongreso").val();
   var codInvitacion = jQuery("#codInvitacion").val();
   var codAutografa = jQuery("#codAutografa").val();
   
   var pnuCorrExp=allTrim(jQuery('#'+noForm).find("#nuCorrExp").val());
   var pcoTipDocAdm=jQuery('#'+noForm).find("#coTipDocAdm").val();
   var pdeAsu=allTrim(jQuery('#'+noForm).find("#deAsu").val());
   var maxLengthDeAsu = jQuery('#'+noForm).find('#deAsu').attr('maxlength');
   var ptiEmi=jQuery('#'+noForm).find("#tiEmi").val();
   var pnuDni=allTrim(jQuery('#'+noForm).find("#nuDni").val());
   var pnuRuc=allTrim(jQuery('#'+noForm).find("#nuRuc").val());
   var pcoOtros=allTrim(jQuery('#'+noForm).find("#coOtros").val());   
   var pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
   var pnuRucAux=allTrim(jQuery('#'+noForm).find("#nuRucAux").val());
   var ptxtdeOtros=allTrim(jQuery('#'+noForm).find("#deNomOtros").val());
   var pfeExp=allTrim(jQuery('#'+noForm).find("#feExp").val());
   //var pfeVence=allTrim(jQuery('#'+noForm).find("#feVence").val());
   var pfeVence=null;
   var pnuFolios=allTrim(jQuery('#'+noForm).find("#nuFolios").val());
   var inNumeroMp=jQuery('#'+noForm).find("#inNumeroMp").val();
   var pnuDiaAte=jQuery('#'+noForm).find("#nuDiaAte").val();
   //Inicio nuevo DP
   var pcoTipoExp=allTrim(jQuery('#'+noForm).find("#coTipoExp").val());
   var pnuDniRes=allTrim(jQuery('#'+noForm).find("#nuDniRes").val());
   var pnuDniAuxRes=allTrim(jQuery('#'+noForm).find("#nuDniAuxRes").val());
   var pcoOtrosRes=allTrim(jQuery('#'+noForm).find("#coOtrosRes").val());
   var pdeNomOtrosRes=allTrim(jQuery('#'+noForm).find("#deNomOtrosRes").val());
   var pidDepartamento=allTrim(jQuery('#'+noForm).find("#idDepartamento").val());
   var pidProvincia=allTrim(jQuery('#'+noForm).find("#idProvincia").val());
   var pidDistrito=allTrim(jQuery('#'+noForm).find("#idDistrito").val());
   var pdeCargo=allTrim(jQuery('#'+noForm).find("#deCargo").val());
   var pdeDireccion=allTrim(jQuery('#'+noForm).find("#deDireccion").val());
   var ptelefono=allTrim(jQuery('#'+noForm).find("#telefono").val());
   var pdeCorreo=allTrim(jQuery('#'+noForm).find("#deCorreo").val());
   var pcoComision=allTrim(jQuery('#'+noForm).find("#coComision").val());
   var pcoTipoCongresista=allTrim(jQuery('#'+noForm).find("#coTipoCongresista").val());
   var pcoTipoInv=allTrim(jQuery('#'+noForm).find("#coTipoInv").val());
   var pcoOriDoc=allTrim(jQuery('#'+noForm).find("#coOriDoc").val());
   var pnroDniTramitante=allTrim(jQuery('#'+noForm).find("#nroDniTramitante").val());
   var pcoTraDest=allTrim(jQuery('#'+noForm).find("#coTraDest").val());
   var pnSobre=allTrim(jQuery('#'+noForm).find("#nSobre").val());
   var panioSobre=allTrim(jQuery('#'+noForm).find("#anioSobre").val());
   
   var pcoEsDocEmiMp=allTrim(jQuery('#'+noForm).find("#coEsDocEmiMp").val());
   var pinDniTramitadorMp=allTrim(jQuery("#inDniTramitadorMp").val());
   var pinDestinoTramitanteMp=allTrim(jQuery("#inDestinoTramitanteMp").val());
   
   var pnuAnexo=allTrim(jQuery('#'+noForm).find("#nuAnexo").val());
   var pnuCopia=allTrim(jQuery('#'+noForm).find("#nuCopia").val());
   
//    if(valRetorno==="1"/* && pcoEsDocEmiMp==="7"*/){
//      if(!(!!pcoTipoExp)||pcoTipoExp==="-1"){
//        valRetorno = "0";  
//        bootbox.alert("<h5>Seleccione tipo de Expediente.</h5>", function() {
//            bootbox.hideAll();
//            jQuery('#'+noForm).find('#coTipoExp').focus();
//        });
//      } 
//   }
   
      if(valRetorno==="1"){
     if(!!ptiEmi){
         if(ptiEmi==="02"){
             jQuery('#divRemCiudadano').find('input').val("");
             jQuery('#divRemOtros').find('input').val("");
             if(!(!!pnuRuc)||!(!!pnuRucAux)){
                valRetorno = "0";  
                bootbox.alert("<h5>Digite RUC.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuRucAux').focus();
                });                
             }
            if(valRetorno==="1"){
                var emiResp = jQuery('#'+noForm).find('#emiResp').val();
                if(emiResp==='03'){
                    if(!(!!pnuDniRes)||!(!!pnuDniAuxRes)){
                        valRetorno = "0";  
                        bootbox.alert("<h5>Digite DNI del emisor responsable.</h5>", function() {
                            bootbox.hideAll();
                            jQuery('#'+noForm).find('#nuDniAuxRes').focus();
                        });                
                     }
                }else if(emiResp==='04'){
                    if(!(!!pcoOtrosRes)||!(!!pdeNomOtrosRes)){
                        valRetorno = "0";  
                        bootbox.alert("<h5>Digite OTROS del emisor responsable.</h5>", function() {
                            bootbox.hideAll();
                            jQuery('#'+noForm).find('#deNomOtrosRes').focus();
                        });                
                     }
                }
                
            }
         }else if(ptiEmi==="03"){
             jQuery('#divRemPersonaJuri').find('input').val("");
             jQuery('#divRemOtros').find('input').val("");
             if(!(!!pnuDni)||!(!!pnuDniAux)){
                valRetorno = "0";  
                bootbox.alert("<h5>Digite DNI.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuDniAux').focus();
                });                  
             }             
         }else if(ptiEmi==="04"){
             jQuery('#divRemPersonaJuri').find('input').val("");
             jQuery('#divRemCiudadano').find('input').val("");
             if(!(!!pcoOtros)||!(!!ptxtdeOtros)){
                valRetorno = "0";  
                bootbox.alert("<h5>Digite OTROS.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#deNomOtros').focus();
                });                  
             }               
         }
     }else{
        valRetorno="0";  
        bootbox.alert("<h5>Indicar tipo Remitente.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#tiEmi').focus();
        });
     }  
   }  
   
   if(valRetorno==="1"){
      if((!(!!pidDepartamento)||pidDepartamento==="-1")
              ||(!(!!pidProvincia)||pidProvincia==="-1")
              ||(!(!!pidDistrito)||pidDistrito==="-1")){
        valRetorno = "0";  
        bootbox.alert("<h5>Seleccione el ubigeo ( departamento, provincia, distrito).</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#idDepartamento').focus();
        });
      } 
   }
   
//   if(valRetorno==="1"){
//      if((!(!!pnuDniRes))||(!(!!pcoOtrosRes))){
//        valRetorno = "0";  
//        bootbox.alert("<h5>Ingrese el Emisor responsable.</h5>", function() {
//            bootbox.hideAll();
////            jQuery('#'+noForm).find('#coTipoExp').focus();
//        });
//      } 
//   }
    if(valRetorno==="1"){
      if(!(!!pdeDireccion)){
        valRetorno = "0";  
        bootbox.alert("<h5>Ingrese la dirección.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#deDireccion').focus();
        });
      } 
   }
   
   /*if(valRetorno==="1"){
      if(!(!!ptelefono) || fu_validaNumero(ptelefono)!=="OK"){
        valRetorno = "0";  
        bootbox.alert("<h5>Ingrese el teléfono, solo dígitos numéricos.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#telefono').focus();
        });
      } 
   }
   
    if(valRetorno==="1"){  
        if(!esEmail(pdeCorreo) || !(!!pdeCorreo)){
            valRetorno = "0";  
            bootbox.alert("<h5>Ingrese el correo, debe tener el formato 'aaa@bbb.ccc'.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#deCorreo').focus();
            });
        } 
   }*/
  
   if(valRetorno==="1"){ 
       var isCheckedNotificado = $('#'+noForm).find('#notificado').is(':checked');
       if(isCheckedNotificado){
           if(!esEmail(pdeCorreo) || !(!!pdeCorreo)){
            valRetorno = "0";  
            bootbox.alert("<h5>Ingrese el correo, debe tener el formato 'aaa@bbb.ccc'.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#deCorreo').focus();
            });
           }
       }
   }
   
    if(pcoTipoExp===codInvitacion && pnuRuc===rucCongreso){
      if(valRetorno==="1"){
         if(!(!!pcoComision)||pcoComision==="-1"){
            valRetorno = "0";  
            bootbox.alert("<h5>Seleccione la comisión.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#coComision').focus();
            });
          }
      }
      if(valRetorno==="1"){
          if(!(!!pcoTipoCongresista)||pcoTipoCongresista==="-1"){
            valRetorno = "0";  
            bootbox.alert("<h5>Seleccione el tipo de congresista.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#coTipoCongresista').focus();
            });
          }
      }
      if(valRetorno==="1"){
          if(!(!!pcoTipoInv)||pcoTipoInv==="-1"){
            valRetorno = "0";  
            bootbox.alert("<h5>Seleccione el tipo de invitación.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#coTipoInv').focus();
            });
          }
      }
//      jQuery('#'+noForm).find('#nSobre').val("");
//      jQuery('#'+noForm).find('#anioSobre').val("");
    }else if(pcoTipoExp===codAutografa && pnuRuc===rucCongreso){
        if(valRetorno==="1"){
          if(!(!!pnSobre)){
            valRetorno = "0";  
            bootbox.alert("<h5>Ingrese el  número de sobre.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nSobre').focus();
            });
          }
        }     
            
      if(valRetorno==="1"){
            vValidaNumero=fu_validaNumero(panioSobre);           
             if(panioSobre.length<4 || vValidaNumero!=="OK"){
               valRetorno = "0";  
               bootbox.alert("<h5>Ingrese el año del sobre, formato de 4 dígitos numéricos.</h5>", function() {
                   bootbox.hideAll();
                   jQuery('#'+noForm).find('#anioSobre').focus();
               });
             } 
         
        }
//        $('#'+noForm).find('#reiterativo').removeAttr('checked').val('0');   
//        jQuery('#'+noForm).find('#coComision').val("").change();
//        jQuery('#'+noForm).find('#coTipoCongresista').val("").change();
//        jQuery('#'+noForm).find('#coTipoInv').val("").change();
    }else if(pnuRuc===rucCongreso && pcoTipoExp!==codInvitacion && pcoTipoExp!==codAutografa){
//        $('#'+noForm).find('#reiterativo').removeAttr('checked').val('0');
//        jQuery('#'+noForm).find('#coTipoInv').val("").change();
//        jQuery('#'+noForm).find('#nSobre').val("");
//        jQuery('#'+noForm).find('#anioSobre').val("");
    }else{
//        $('#'+noForm).find('#reiterativo').removeAttr('checked').val('0');   
//        jQuery('#'+noForm).find('#coComision').val("").change();
//        jQuery('#'+noForm).find('#coTipoCongresista').val("").change();
//        jQuery('#'+noForm).find('#coTipoInv').val("").change();
//        jQuery('#'+noForm).find('#nSobre').val("");
//        jQuery('#'+noForm).find('#anioSobre').val("");
    }
   
   
   if(valRetorno==="1"){
      if(!(!!pcoOriDoc)||pcoOriDoc==="-1"){
        valRetorno = "0";  
        bootbox.alert("<h5>Seleccione origen del documento.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#coOriDoc').focus();
        });
      } 
   }
   
   if(valRetorno==="1" && pinDniTramitadorMp==="SI"){
       vValidaNumero=fu_validaNumero(pnroDniTramitante);
        if(!(!!pnroDniTramitante)||pnroDniTramitante.length<8||vValidaNumero!=="OK"){
          valRetorno = "0";  
          bootbox.alert("<h5>Ingrese el DNI del tramitador, debe tener 8 dígitos.</h5>", function() {
              bootbox.hideAll();
              jQuery('#'+noForm).find('#nroDniTramitante').focus();
          });
        }
        if(valRetorno==="1" && !verificaCiudadano(pnroDniTramitante)){ 
          valRetorno = "0";  
          bootbox.alert("<h5>El DNI del tramitador no se encuentra en los registros de ciudadanos.</h5>", function() {
              bootbox.hideAll();
              jQuery('#'+noForm).find('#nroDniTramitante').focus();
          });
        }
   }else if(valRetorno==="1" && pinDniTramitadorMp!=="SI"){
       if(!!pnroDniTramitante){
            vValidaNumero=fu_validaNumero(pnroDniTramitante);
             if(pnroDniTramitante.length<8||vValidaNumero!=="OK"){
               valRetorno = "0";  
               bootbox.alert("<h5>Ingrese el DNI del tramitador, debe tener 8 dígitos.</h5>", function() {
                   bootbox.hideAll();
                   jQuery('#'+noForm).find('#nroDniTramitante').focus();
               });
             }
             if(valRetorno==="1" && !verificaCiudadano(pnroDniTramitante)){ 
                valRetorno = "0";  
                bootbox.alert("<h5>El DNI del tramitador no se encuentra en los registros de ciudadanos.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nroDniTramitante').focus();
                });
              }
        }
   }
   
//   if(valRetorno==="1" /*&& pcoEsDocEmiMp==="7"*/ && pinDestinoTramitanteMp==="SI"){
//      if(!(!!pcoTraDest)||pcoTraDest==="-1"){
//        valRetorno = "0";  
//        bootbox.alert("<h5>Seleccione el destino que indica el tramitador.</h5>", function() {
//            bootbox.hideAll();
//            jQuery('#'+noForm).find('#coTraDest').focus();
//        });
//      } 
//   }
   
   //Fin Nuevo DP
   
   if(!!pnuCorrExp){
        vValidaNumero = fu_validaNumero(pnuCorrExp);   
        if (vValidaNumero !== "OK") {
            valRetorno = "0";
            bootbox.alert("<h5>Número de Expediente debe ser solo números.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuCorrExp').focus();
            });
            jQuery('#'+noForm).find('#nuCorrExp').val("");
        }        
   }else{
        if (!!inNumeroMp && inNumeroMp==="1" ){
             valRetorno = "0";  
             bootbox.alert("<h5>Se necesita número de Expediente.</h5>", function() {
                 bootbox.hideAll();
                 jQuery('#'+noForm).find('#nuCorrExp').focus();
             });
        }        
   }
   if(valRetorno==="1"){
       if(!!pfeExp){
            if(moment(pfeExp, "DD/MM/YYYY HH:mm").isValid()){
//                jQuery('#'+noForm).find("#feExp").val(moment(pfeExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"])
//                    .hour(moment().hour()).minute(moment().minute())
//                    .format("DD/MM/YYYY HH:mm"));
//                pfeExp=jQuery('#'+noForm).find("#feExp").val();
//                var pnuAnn=moment(pfeExp,"DD/MM/YYYY HH:mm").format("YYYY");
//                jQuery('#'+noForm).find("#nuAnn").val(pnuAnn);
//                jQuery('#'+noForm).find("#nuAnnExp").val(pnuAnn);
                
                var fecha=moment(pfeExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(fecha.isValid()){
                    if(fecha.hour()===0){
                        fecha.hour(moment().hour());
                    }
                    if(fecha.minute()===0){
                        fecha.minute(moment().minute());
                    }
                    jQuery('#'+noForm).find("#feExp").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
                    pfeExp=jQuery('#'+noForm).find("#feExp").val();
                    var pnuAnn=moment(pfeExp,"DD/MM/YYYY HH:mm").format("YYYY");
                    jQuery('#'+noForm).find("#nuAnn").val(pnuAnn);
                    jQuery('#'+noForm).find("#nuAnnExp").val(pnuAnn);                    
                }                
            }else{
                valRetorno="0";
                 bootbox.alert("<h5>Fecha expediente Inválida.</h5>", function() {
                     bootbox.hideAll();
                     jQuery('#'+noForm).find('#feExp').focus();
                 });
            }       
       }else{
           valRetorno="0";
            bootbox.alert("<h5>Indicar fecha Expediente.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#feExp').focus();
            });
       }
       
   }
   if(valRetorno==="1"){
       if(!!pfeVence){
            if(moment(pfeVence, "DD/MM/YYYY").isValid()){
//                jQuery('#'+noForm).find("#feVence").val(moment(pfeVence, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"])
//                    .format("DD/MM/YYYY"));
                var fecha=moment(pfeVence, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"]); 
                if(fecha.isValid()){
                    if(fecha.hour()===0){
                        fecha.hour(moment().hour());
                    }
                    if(fecha.minute()===0){
                        fecha.minute(moment().minute());
                    }
                    jQuery('#'+noForm).find("#feVence").val(moment(fecha,"DD/MM/YYYY").format("DD/MM/YYYY"));
                }            
            }else{
                valRetorno="0";
                 bootbox.alert("<h5>Fecha vencimiento expediente Inválida.</h5>", function() {
                     bootbox.hideAll();
                     jQuery('#'+noForm).find('#feVence').focus();
                 });
            }       
       }
   }
   if(valRetorno==="1"){
       if(!!pfeVence&&!!pfeExp){
           var mpfeExp=moment(moment(pfeExp,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY"),"DD/MM/YYYY");
           var mpfeVence=moment(pfeVence,"DD/MM/YYYY");
           if(mpfeVence.isBefore(mpfeExp)){
            valRetorno="0";
             bootbox.alert("<h5>Fecha vencimiento debe ser mayor o igual a la Fecha de Expediente.</h5>", function() {
                 bootbox.hideAll();
                 jQuery('#'+noForm).find('#feVence').focus();
             });               
           }
       }       
   }   
 
   if(valRetorno==="1"){
      if(!(!!pcoTipDocAdm)||pcoTipDocAdm==="-1"){
        valRetorno = "0";  
        bootbox.alert("<h5>Seleccione tipo de Documento.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#coTipDocAdm').focus();
        });
      } 
   }
   if(valRetorno==="1"){
       if(!!pnuFolios){
            vValidaNumero = fu_validaNumero(pnuFolios);
            if (vValidaNumero !== "OK") {
                valRetorno = "0";
                bootbox.alert("<h5>Número de Folios debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuFolios').focus();
                });                
            }           
       }else{
            valRetorno = "0";  
            bootbox.alert("<h5>Especifique número de Folios.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuFolios').focus();
            });
       }
   }
   
   //YUAL
    
       if(!!pnuCopia){
            vValidaNumero = fu_validaNumero(pnuCopia);
            if (vValidaNumero !== "OK") {
                valRetorno = "0";
                bootbox.alert("<h5>Número de Copias debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuCopia').focus();
                });                
            }           
       }
     
   if(valRetorno==="1"){
       if(!!pnuDiaAte){
           vValidaNumero=fu_validaNumero(pnuDiaAte);
           if(vValidaNumero!=="OK"){
                valRetorno = "0";
                bootbox.alert("<h5>Número de Días Atención debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                    //jQuery('#'+noForm).find('#nuDiaAte').focus();
                });                
           }
       }
   }
   if(valRetorno==="1"){
       if(!!pdeAsu){
        if(!!maxLengthDeAsu){
            var nrolinesDeAsu = (pdeAsu.match(/\n/g) || []).length;
            if(pdeAsu.length+nrolinesDeAsu > maxLengthDeAsu){
                valRetorno = "0";
                bootbox.alert("<h5>El Asunto Excede el límite de "+maxLengthDeAsu+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#deAsu').focus();
                });
            }
        }           
       }else{
        valRetorno = "0";  
        bootbox.alert("<h5>Indicar Asunto.</h5>", function() {
            bootbox.hideAll();
            jQuery('#'+noForm).find('#deAsu').focus();
        });
       }
   }
   if(valRetorno==="1"){
       var rpta = fu_verificarDestinatario("1");
       var nrpta = rpta.substr(0,1);       
       if(nrpta!=="1"){
        valRetorno = "0";  
        alert_Info("Recepción :",rpta);
       }
   }
   if(valRetorno==="1"){
        var rpta = fu_verificarReferencia();
        var nrpta = rpta.substr(0,1);                    
        if(nrpta!=="1"){
            valRetorno = "0";  
            alert_Info("Recepción :",rpta);            
        }       
   }
   return valRetorno;
} 

function fn_buildSendJsontoServerDocExtRecep(noForm) {
    
//    var isSensible = jQuery('#' + noForm).find('#sensible').is(':checked');
//    jQuery('#' + noForm).find('#sensible').val(isSensible? '1': '0');
//    var isNotificado = jQuery('#' + noForm).find('#notificado').is(':checked');
//    jQuery('#' + noForm).find('#notificado').val(isNotificado? '1': '0');
//    var isReiterativo = jQuery('#' + noForm).find('#reiterativo').is(':checked');
//    jQuery('#' + noForm).find('#reiterativo').val(isReiterativo? '1': '0');
    var result = "{";
    result = result + '"nuAnn":"' + jQuery('#'+noForm).find("#nuAnn").val() + '",';
    result = result + '"nuEmi":"' + jQuery('#'+noForm).find("#nuEmi").val() + '",';
    result = result + '"nuAnnExp":"' + jQuery('#'+noForm).find("#nuAnnExp").val() + '",';
    result = result + '"nuSecExp":"' + jQuery('#'+noForm).find("#nuSecExp").val() + '",';
    var valEnvio = jQuery('#envDocumentoEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"documentoEmiBean":' + JSON.stringify(getJsonFormDocumentoExtRecep(noForm,getArrCampoBeanDocExtRecep())) + ',';
    }
    valEnvio = jQuery('#envExpedienteEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"expedienteEmiBean":' + JSON.stringify(getJsonFormDocumentoExtRecep(noForm,getArrCampoBeanExpDocExtRecep())) + ',';
    }
    valEnvio = jQuery('#envRemitenteEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"remitenteEmiBean":' + JSON.stringify(getJsonFormDocumentoExtRecep(noForm,getArrCampoBeanRemiDocExtRecep())) + ',';
    }
    result = result + '"lstReferencia":' + fn_tblRefDocExtToJson() + ',';
    result = result + '"lstDestinatario":' + sortDelFirst(fn_tblDestEmiDocExtToJson());
    return result + "}";
}

function getArrCampoBeanDocExtRecep(){
    var arrCampoBean = new Array();
    arrCampoBean[0] = "nuAnn";
    arrCampoBean[1] = "nuEmi";
    arrCampoBean[2] = "coTipDocAdm";
    arrCampoBean[3] = "deDocSig";
    arrCampoBean[4] = "deAsu";
    arrCampoBean[5] = "nuFolios";
    arrCampoBean[6] = "nuCorDoc";
    arrCampoBean[7] = "tiEmi";
    arrCampoBean[8] = "coEsDocEmiMp";
    arrCampoBean[9] = "nuDiaAte";
    
    arrCampoBean[11] = "sensible";
    arrCampoBean[12] = "nSobre";
    arrCampoBean[13] = "anioSobre";
    arrCampoBean[14] = "coOriDoc";
    arrCampoBean[15] = "nroDniTramitante";
    arrCampoBean[16] = "coTraDest";
    arrCampoBean[17] = "deObservacion";
    arrCampoBean[18] = "nuAnexo";
    arrCampoBean[19] = "nuCopia";
   
    return arrCampoBean;
}

function getArrCampoBeanExpDocExtRecep(){
    var arrCampoBean = new Array();
    arrCampoBean[0] = "nuAnnExp";
    arrCampoBean[1] = "nuSecExp";
    arrCampoBean[2] = "nuExpediente";
    arrCampoBean[3] = "feExp";
    arrCampoBean[4] = "feVence";
    arrCampoBean[5] = "nuCorrExp";
    arrCampoBean[6] = "coProceso";
    arrCampoBean[7] = "coDepEmi";
    
    arrCampoBean[8] = "coTipoExp";
    
   
    return arrCampoBean;
}   

function getArrCampoBeanRemiDocExtRecep(){
    var arrCampoBean = new Array();
    arrCampoBean[0] = "coLocEmi";
    arrCampoBean[1] = "coDepEmi";
    arrCampoBean[2] = "coEmpEmi";
    arrCampoBean[3] = "coEmpRes";
    arrCampoBean[4] = "tiEmi";
    arrCampoBean[5] = "nuRuc";
    arrCampoBean[6] = "nuDni";
    arrCampoBean[7] = "coOtros";
    
    arrCampoBean[8] = "idDepartamento";
    arrCampoBean[9] = "idProvincia";
    arrCampoBean[10] = "idDistrito";
    arrCampoBean[11] = "deDireccion";
    arrCampoBean[12] = "notificado";
    arrCampoBean[13] = "deCorreo";
    arrCampoBean[14] = "telefono";
    arrCampoBean[15] = "deCargo";
    arrCampoBean[16] = "nuDniRes";
    arrCampoBean[17] = "coOtrosRes";
    arrCampoBean[18] = "reiterativo";
    arrCampoBean[19] = "coComision";
    arrCampoBean[20] = "coTipoCongresista";
    arrCampoBean[21] = "coTipoInv";
    arrCampoBean[22] = "emiResp";
    
   
    
    return arrCampoBean;
}    

function getJsonFormDocumentoExtRecep(noForm,arrCampoBean) {
    var o = {};
//    var a = $('#'+noForm).serializeArray();
//    $.each(a, function() {
//        for (var i = 0; i < arrCampoBean.length; i++) {
//            if (this.name === arrCampoBean[i]) {
//                if (o[this.name]) {
//                    if (!o[this.name].push) {
//                        o[this.name] = [o[this.name]];
//                    }
//                    o[this.name].push(this.value || '');
//                } else {
//                    o[this.name] = this.value || '';
//                }
//            }
//        }
//    });
    for(var i=0;i < arrCampoBean.length; i++){
        o[arrCampoBean[i]]=$("#"+noForm).find("#"+arrCampoBean[i]).val();
    }
    return o;
}

function fn_tblRefDocExtToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBd=10";
    arrColMostrar[1] = "nuAnn=7";
    arrColMostrar[2] = "nuEmi=8";
    arrColMostrar[3] = "coRef=9";
    arrColMostrar[4] = "nuDes=11";
    return fn_tblRefEmihtml2json('tblRefEmiDocAdm', 1, arrColMostrar, 13, "1", 10, "BD");
}

function fn_tblDestEmiDocExtToJson() {
    var json = '[';
    var itArr = [];
    var tbl1 = fn_tblDestIntituEmiDocAdmToJson();
    tbl1 !== "" ? itArr.push(tbl1) : "";
    json += itArr.join(",") + ']';
    return json;
}

function fn_buscaAccionDocExtDestEmitbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaAccionDestinatario";
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },'text', false, false, "POST");
}

function fn_removeReferenciaEmiDocExt() {
    var indexAccionBD = "9";
    var pindexFilaRefEmiDoc = $('#txtIndexFilaRefEmiDoc').val();
    if (pindexFilaRefEmiDoc !== "-1") {
        var sAccionBD = $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td:eq(" + indexAccionBD + ")").text();
        if ($("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td").last().html() === "1") {
            bootbox.dialog({
                message: " <h5>¿ Esta Seguro de Quitar Referencia ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-primary",
                        callback: function() {
                            if (sAccionBD === "BD" || sAccionBD === "UPD") {
                                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
                                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").addClass('oculto');
                            } else {
                                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").remove();
                            }
                            $('#txtIndexFilaRefEmiDoc').val("-1");
                        }                        
                    },
                    NO: {
                        label: "NO",
                        className: "btn-default"
                    }
                }
            });               
        } else {
            if (sAccionBD === "BD" || sAccionBD === "UPD") {
                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td").last().text("1");
                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").addClass('oculto');
            } else {
                $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").remove();
            }
            $('#txtIndexFilaRefEmiDoc').val("-1");
        }
    } else {
       bootbox.alert("Seleccione Referencia");
    }
}

var $objFilaReferencia;
function fn_searchDocExtReferentbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDocumentotblReferencia";
    (($(cell).parent()).parent()).children().each(function(index) {
        switch (index)
        {
            case 0:
                p[1] = "pnuAnn=" + $(this).find('select').val();
                var valAnio = $(this).find('select').val();
                $(this).find('select option[value="'+valAnio+'"]').attr("selected","selected");
                break;
            case 1:
                p[2] = "ptiDoc=" + $(this).find('select').val();
                var valTiDoc = $(this).find('select').val();
                $(this).find('select option[value="'+valTiDoc+'"]').attr("selected","selected");
                break;
            case 2:
                p[3] = "pnroExpediente=" + $(this).find('input[type=text]').val();
                break;
            default:
                break;
        }
    });
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaDocumentoReferencia(data);
        jQuery('#txtTblRefEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblRefEmiColWhereButton').val(($(cell).parent()).index());
        $objFilaReferencia = ($(cell).parent()).parent().clone();
    },'text', false, false, "POST");
}
function fn_seleccionarFila(objeto,event){
    if (event.target.type !== 'checkbox') {
      $(objeto).find(":checkbox").click();
    }
}
$(document).on("change","#tlbDocumentoRefEmi input[type='checkbox']",function(e){
    if ($(this).is(":checked")) {
        $(this).closest('tr').addClass("row_selected");
    } else {
        $(this).closest('tr').removeClass("row_selected");
    }
});


function fn_agregarReferencias(){
    var count = 0;
    var posFila = parseInt($('#txtTblRefEmiFilaWhereButton').val());
    var filaIni = posFila;
     $('#tlbDocumentoRefEmi .row_selected').each(function(i, obj) {
         var pnuExpediente= $(this).find("td:eq(1)").text();
            var pfeExp= $(this).find("td:eq(3)").text();
            var pnuAnn= $(this).find("td:eq(7)").text();
            var pnuEmi= $(this).find("td:eq(8)").text();
            var pcoTipDocAdm= $(this).find("td:eq(9)").text();
        
        if(count===0){
            fu_setDatoDocumentoExtRef(pnuAnn,pnuEmi,pnuExpediente,pfeExp,pcoTipDocAdm);            
        }else{
//            var maxNum='0';
//            $('#tblRefEmiDocAdm tbody input[type="radio"]').each(function()  { 
//                var rbGrupo = $(this).attr('name');
//                var idGrupo = rbGrupo.replace('group','');            
//                if(idGrupo>maxNum){
//                    maxNum=idGrupo;
//                }
//            });
//            $objFilaReferencia.find('input[type=radio]').attr('name', 'group' + (parseInt(maxNum)+1));
            $("#tblRefEmiDocAdm tbody tr:eq("+(posFila-1)+")").after("<tr>"+$objFilaReferencia.html()+"</tr>"); 
            $('#txtTblRefEmiFilaWhereButton').val(posFila);            
            fu_setDatoDocumentoExtRef(pnuAnn,pnuEmi,pnuExpediente,pfeExp,pcoTipDocAdm);
        }
        count++; 
        posFila++;
     });
     
     $('#tblRefEmiDocAdm tbody tr').each(function(i, obj){
            if(i!="0"&&i!=filaIni){   
               var isOk = $(this).find("td").last().text();
               if(isOk==="0"){
                   $(this).remove();
               }
           }
        });
}
function fu_setDatoDocumentoExtRef(pnuAnn,pnuEmi,pnuExpediente,pfeExp,pcoTipDocAdm) {
    
    debugger;
    var pfila = jQuery('#txtTblRefEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblRefEmiColWhereButton').val();
    //verificar si ya esta referenciado el documento
    var sResult = fn_validaDocumentoRefListaDocExt(pfila*1,pcol, pnuAnn, pnuEmi);
    if (sResult) {
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 1) + ")").find('input[type=text]').val(pfeExp);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 2) + ")").find('input[type=text]').val(pnuExpediente);
        removeDomId('windowConsultaDocumentoRefEmi');
        var p = new Array();
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td").each(function(index) {
            switch (index)
            {
                case 0:
                    p[0] = $(this).find('select').val();
                    break;
                case 1:
                    p[1] = $(this).find('select').val();
                    break;
                default:
                    break;
            }
        });
        p[2] = pnuExpediente;
        p[3] = pfeExp;
        //$("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (1) + ")").text(pcoTipDocAdm);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (1) + ")").find('select').val(pcoTipDocAdm);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(p.join("$#"));
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 2) + ")").text(pnuAnn);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 3) + ")").text(pnuEmi);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 4) + ")").text(pcoTipDocAdm);
        if ($("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 5) + ")").text() === "BD") {
            $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 5) + ")").text("UPD");
        }
        fn_changeReferenciaCorrecta(pfila);
    } else {
        removeDomId('windowConsultaDocumentoRefEmi');
       bootbox.alert("Documento ya se encuentra Referenciado.");
    }
    return false;
}

function fn_validaDocumentoRefListaDocExt(pfila, pcol, pnuAnn, pnuEmi) {
    var countFound = 0;
    $("#tblRefEmiDocAdm tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            if(index !== pfila){
                var pnuAnnRef = $(row).find("td:eq(" + (pcol * 1 + 2) + ")").text();
                var pnuEmiRef = $(row).find("td:eq(" + (pcol * 1 + 3) + ")").text();
                if (pnuAnnRef === pnuAnn && pnuEmiRef === pnuEmi) {
                    countFound++;
                }
            }
        }
    });
    if (countFound > 0) {
        return false;
    } else {
        return true;
    }
}

function fn_evalReferenciaCorrectatblDocExt(indexFila) {
    var arrInputAnt = ($("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ")").children().get(5).innerHTML).split("$#");
    var countFound = 0;
    for (var i = 0; i < arrInputAnt.length; i++)
    {
        $("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ") td").each(function(index) {
            if (index in {0: 1, 1: 1, 2: 1, 3: 1}) {
                var valInput = "";
                if (typeof($(this).find('input[type=text]').val()) !== "undefined") {
                    valInput = allTrim(fu_getValorUpperCase($(this).find('input[type=text]').val()));
                } else if (typeof($(this).find('select').val()) !== "undefined") {
                    valInput = $(this).find('select').val();
                } else if (typeof($(this).find('input[type=radio]').val()) !== "undefined") {
                    valInput = $(this).find('input[type=radio]:checked').val();
                }
                if (arrInputAnt[i] === valInput) {
                    countFound++;
                }
            }
        });
    }
    if (countFound === 4) {
        fn_changeReferenciaCorrecta(indexFila);
    } else {
        fn_changeReferenciaIncorrecta(indexFila);
    }
}

function fu_cambioTxtTblRefEmiDocExt(input) {
    fn_evalReferenciaCorrectatblDocExt((($(input).parent()).parent()).index());
}

function fn_validarFechaExpDocExt(){
    var vResult=false;
    var noForm='documentoExtRecepBean';
    var vfechaExp=allTrim(jQuery('#'+noForm).find('#feExp').val());
    if(!!vfechaExp){
        if(moment(vfechaExp, "DD/MM/YYYY HH:mm").isValid()){
//            jQuery('#'+noForm).find("#feExp").val(moment(vfechaExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"])
//                    .hour(moment().hour()).minute(moment().minute())
//                    .format("DD/MM/YYYY HH:mm"));
            var fecha=moment(vfechaExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
            if(fecha.isValid()){
                if(fecha.hour()===0){
                    fecha.hour(moment().hour());
                }
                if(fecha.minute()===0){
                    fecha.minute(moment().minute());
                }
                jQuery('#'+noForm).find("#feExp").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
                vResult=true;
            }            
        }         
    }
    return vResult;
}

function fn_validaFechaVenceExpDocExt(){
    var vResult=false;
    var noForm='documentoExtRecepBean';
    var vfechaExp=allTrim(jQuery('#'+noForm).find('#feVence').val());
    if(!!vfechaExp){
        if(moment(vfechaExp, "DD/MM/YYYY").isValid()){
//            jQuery('#'+noForm).find("#feVence").val(moment(vfechaExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"])
//                    .format("DD/MM/YYYY"));
            var fecha=moment(vfechaExp, ["DD","DD/MM","DD/MM/YY","DD/MM/YYYY"]); 
            if(fecha.isValid()){
                if(fecha.hour()===0){
                    fecha.hour(moment().hour());
                }
                if(fecha.minute()===0){
                    fecha.minute(moment().minute());
                }
                jQuery('#'+noForm).find("#feVence").val(moment(fecha,"DD/MM/YYYY").format("DD/MM/YYYY"));
                jQuery('#envExpedienteEmiBean').val("1"); 
                vResult=true;
            }            
        }         
    }else{
        vResult=true;
    }
    return vResult;    
}

function afterRegresarLstRecepDocumExt(){
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    
    var esGrabaCambio="0";
    if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
        if(!!pnuAnn&&!!pnuEmi){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                bootbox.dialog({
                    message: " <h5>Existen Cambios en el Documento.\n" +
                            "¿ Desea salir del Documento ?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-default",
                            callback: function() {
                                fn_valRegresarRecepDocExtAtender(); 
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-primary"
                        }
                    }
                });
            }            
        }
    }
    if(esGrabaCambio==="0"){
       fn_valRegresarRecepDocExtAtender(); 
    }
}

function fn_valRegresarRecepDocExtAtender(){
    if (jQuery('#divRecepDocumentoAdmin').length === 0) {
        regresarLstRecepDocumExt();
    }else{
        var pnuAnn = jQuery('#txtpnuAnn').val();
        var pnuEmi = jQuery('#txtpnuEmi').val();
        var pnuDes = jQuery('#txtpnuDes').val();
        if (!!pnuAnn&&pnuEmi&&pnuDes) {
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + pnuEmi;
            p[2] = "pnuDes=" + pnuDes;
            ajaxCall("/srDocumentoAdmRecepcion.do?accion=goEditarDocumento", p.join("&"), function(data) {
                refreshScript("divWorkPlaceRecepDocumAdmin", data);
                fn_cargaToolBarRec();
            }, 'text', false, false, "POST");
        } else {
            alert_Info("Emisión :", "Seleccione una fila de la lista");
        }        
    }    
}

function regresarLstRecepDocumExt(){
    jQuery('#divBuscarDocExtRecep').toggle();                                
    jQuery('#divDocExtRecep').toggle(); 
    submitAjaxFormBusDocExtRecep(jQuery('#buscarDocumentoExtRecepBean').find('#tipoBusqueda').val());
    jQuery('#divDocExtRecep').html("");     
}

function onclickBuscarProveedorDocExtRecep(){
    var noForm='documentoExtRecepBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#deNuRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepJson(pnuRucAux,noForm);
    }else if(!!pnuRucAux && pnuRucAux.length > 0){
        buscarProveedorEditDocExtRecepJson(pnuRucAux,noForm);
    }else{
        buscarProveedorEditDocExtRecep(prazonSocial);
    }
}

function buscarProveedorEditDocExtRecepJson(pnuRuc,noForm){
    if(validarBuscarProveedorEditDocExt(pnuRuc)){
        
        var p = new Array();
        p[0] = "accion=goBuscaProveedorJson";
        p[1] = "pnuRuc=" + pnuRuc;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            if (!!data){
                if(data.coRespuesta==="1"){
                    var obj = data.proveedorBean;
                    if(!!obj){
                    jQuery('#'+noForm).find('#deNuRuc').val(obj.deRuc);
                    jQuery('#'+noForm).find('#nuRuc').val(obj.nuRuc);
                    jQuery('#'+noForm).find('#nuRucAux').val(obj.nuRuc);
                    jQuery('#'+noForm).find('#coTipDocAdm').focus(); 
                    jQuery('#envRemitenteEmiBean').val("1");
                    //INI_DESPACHO_PRESIDENCIAL
                    jQuery('#'+noForm).find("#coTipoExp").change();
                    cargar_ubigeo(obj.idDepartamento,obj.idProvincia,obj.idDistrito);
                    jQuery('#'+noForm).find("#deDireccion").val(obj.deDireccion);
                    jQuery('#'+noForm).find("#deCorreo").val(obj.deCorreo);
                    jQuery('#'+noForm).find("#telefono").val(obj.telefono);
                    //FIN_DESPACHO_PRESIDENCIAL
                    }else{
                        bootbox.alert("<h5>Número RUC Inválido.</h5>", function() {
                            bootbox.hideAll();
                            jQuery('#'+noForm).find('#nuRucAux').focus();
                        });                         
                    }
                }else{
                    bootbox.alert("<h5>Número RUC Inválido.</h5>", function() {
                        fn_CleanDatosRemitenteDocExtRec('#'+noForm,'#divRemPersonaJuri');
                        bootbox.hideAll();
                        jQuery('#'+noForm).find('#nuRucAux').focus();
                    });
                }
            }
        }, 'json', false, false, "POST");        
    }
}

function validarBuscarProveedorEditDocExt(pnuRuc){
    var valRetorno = true;
    var vValidaNumero = fu_validaNumero(pnuRuc);
    if (vValidaNumero !== "OK") {
       bootbox.alert("N° de Ruc debe ser solo números.");
        valRetorno = false;
    }
    return valRetorno;
}

function fn_buscarProveedorEditDocExtRecep(){
    var noForm='documentoExtRecepBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#deNuRuc').val());    
    buscarProveedorEditDocExtRecep(prazonSocial);
}

function buscarProveedorEditDocExtRecep(prazonSocial){
    var snoRazonSocial = allTrim(fu_getValorUpperCase(prazonSocial));
    snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));            
    if(allTrim(snoRazonSocial).length >= 0 && allTrim(snoRazonSocial).length <= 3){  //El texto es entre 1 y 3 caracteres
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }         
    if (!!snoRazonSocial) {
        var rptValida = validaCaracteres(snoRazonSocial, "2");
        if (rptValida === "OK") {
            // Expresion regular
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(snoRazonSocial.match(re)){      
                var p = new Array();
                p[0] = "accion=goBuscaProveedor";
                p[1] = "prazonSocial=" + snoRazonSocial;
                ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                    fn_rptaBuscarProveedorEditDocExtRecep(data);
                },
                'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                  
            }                     
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>");            
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre Persona Jurídica.</h5>");                    
    }
    return false;
}

function fn_rptaBuscarProveedorEditDocExtRecep(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_rptaBuscarCiudadanoEditDocExtRecep(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_iniConsProveedorEditRecDocExt(){
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var prazonSocial= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var pnuRuc= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    
                    var pidDepartamento= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                    var pidProvincia= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(3)").html();
                    var pidDistrito= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(4)").html();
                    var pdeDireccion= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(5)").html();
                    var pdeCorreo= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(6)").html();
                    var ptelefono= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(7)").html();
                
                    fn_setProveedorEditRecDocExt(prazonSocial,pnuRuc,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var prazonSocial= $(this).find("td:eq(0)").html();
        var pnuRuc= $(this).find("td:eq(1)").html();
        
        var pidDepartamento= $(this).find("td:eq(2)").html();
        var pidProvincia= $(this).find("td:eq(3)").html();
        var pidDistrito= $(this).find("td:eq(4)").html();
        var pdeDireccion= $(this).find("td:eq(5)").html();
        var pdeCorreo= $(this).find("td:eq(6)").html();
        var ptelefono= $(this).find("td:eq(7)").html();        
        fn_setProveedorEditRecDocExt(prazonSocial,pnuRuc,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);
    });
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var prazonSocial= $(this).find("td:eq(0)").html();
            var pnuRuc= $(this).find("td:eq(1)").html();
            
            var pidDepartamento= $(this).find("td:eq(2)").html();
            var pidProvincia= $(this).find("td:eq(3)").html();
            var pidDistrito= $(this).find("td:eq(4)").html();
            var pdeDireccion= $(this).find("td:eq(5)").html();
            var pdeCorreo= $(this).find("td:eq(6)").html();
            var ptelefono= $(this).find("td:eq(7)").html();
            
            fn_setProveedorEditRecDocExt(prazonSocial,pnuRuc,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setProveedorEditRecDocExt(prazonSocial,pnuRuc,idDepartamento, idProvincia, idDistrito, deDireccion,deCorreo,telefono){
    var noForm='documentoExtRecepBean';
    jQuery('#'+noForm).find('#deNuRuc').val(prazonSocial);
    jQuery('#'+noForm).find('#nuRuc').val(pnuRuc);
    jQuery('#'+noForm).find('#nuRucAux').val(pnuRuc);
    removeDomId('windowConsultaProveedor');
    jQuery('#'+noForm).find('#deNuRuc').focus();
    
    jQuery('#'+noForm).find("#coTipoExp").change();
    cargar_ubigeo(idDepartamento,idProvincia,idDistrito);
    jQuery('#'+noForm).find('#deDireccion').val(deDireccion);
    jQuery('#'+noForm).find('#deCorreo').val(deCorreo);
    jQuery('#'+noForm).find('#telefono').val(telefono); 
        
    jQuery('#envRemitenteEmiBean').val("1");
    return false;
}

function fn_buscarRemitenteOtroDocExtRec(){
    var noForm='documentoExtRecepBean';
    var nomOtro=allTrim(fu_getValorUpperCase(jQuery('#'+noForm).find('#deNomOtros').val()));
    nomOtro=jQuery('#'+noForm).find('#deNomOtros').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(nomOtro))).val(); 
    $("#otroOrigenActivo").val("1");
    $("#otroOrigenResActivo").val("0");
    fn_validaBuscarRemitenteOtroDocExtRec(nomOtro);
}
function fn_buscarRemitenteOtroResDocExtRec(){
    var noForm='documentoExtRecepBean';
    var nomOtroRes=allTrim(fu_getValorUpperCase(jQuery('#'+noForm).find('#deNomOtrosRes').val()));
    nomOtroRes=jQuery('#'+noForm).find('#deNomOtrosRes').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(nomOtroRes))).val(); 
    $("#otroOrigenActivo").val("0");
    $("#otroOrigenResActivo").val("1");
    fn_validaBuscarRemitenteOtroDocExtRec(nomOtroRes);
}
function fn_validaBuscarRemitenteOtroDocExtRec(pnomOtro){
    if( allTrim(pnomOtro).length >= 0 && allTrim(pnomOtro).length <= 3 ){
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll();
            //jQuery('#'+noForm).find('#deNomOtros').focus();
        }); 
        return;
    }    
        
    if(!!pnomOtro){
        var rptValida = validaCaracteres(pnomOtro, "2");
        if (rptValida === "OK") {
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(pnomOtro.match(re)){
                var p = new Array();
                p[0] = "accion=goBuscaDestOtroOrigen";
                p[1] = "pnoOtroOrigen=" + pnomOtro;
                ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                    fn_rptaBuscarRemitenteOtroDocExtRec(data);
                },'text', false, false, "POST");                
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                  
            }             
        }else{
            bootbox.alert("<h5>"+rptValida+".</h5>");              
        }
    }else{
        bootbox.alert("<h5>Debe ingresar datos en Nombre.</h5>");           
    }
}

function fn_rptaBuscarRemitenteOtroDocExtRec(XML_AJAX){
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }    
}

function fn_iniConsOtroOrigenEditRecDocExt(){
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var pdesDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var ptipDocInden= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    var pnroDocInden= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                    var pcodDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(3)").html();
                    
                    var pidDepartamento= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(4)").html();
                    var pidProvincia= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(5)").html();
                    var pidDistrito= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(6)").html();
                    var pdeDireccion= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(7)").html();
                    var pdeCorreo= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(8)").html();
                    var ptelefono= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(9)").html();
                    fn_setOtroOrigenEditDocExtRec(pdesDest,ptipDocInden,pnroDocInden,pcodDest,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var pdesDest= $(this).find("td:eq(0)").html();
        var ptipDocInden= $(this).find("td:eq(1)").html();
        var pnroDocInden= $(this).find("td:eq(2)").html();
        var pcodDest= $(this).find("td:eq(3)").html();
        
        var pidDepartamento= $(this).find("td:eq(4)").html();
        var pidProvincia= $(this).find("td:eq(5)").html();
        var pidDistrito= $(this).find("td:eq(6)").html();
        var pdeDireccion= $(this).find("td:eq(7)").html();
        var pdeCorreo= $(this).find("td:eq(8)").html();
        var ptelefono= $(this).find("td:eq(9)").html();
        fn_setOtroOrigenEditDocExtRec(pdesDest,ptipDocInden,pnroDocInden,pcodDest,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);
    }); 
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var pdesDest= $(this).find("td:eq(0)").html();
            var ptipDocInden= $(this).find("td:eq(1)").html();
            var pnroDocInden= $(this).find("td:eq(2)").html();
            var pcodDest= $(this).find("td:eq(3)").html();
            
            var pidDepartamento= $(this).find("td:eq(4)").html();
            var pidProvincia= $(this).find("td:eq(5)").html();
            var pidDistrito= $(this).find("td:eq(6)").html();
            var pdeDireccion= $(this).find("td:eq(7)").html();
            var pdeCorreo= $(this).find("td:eq(8)").html();
            var ptelefono= $(this).find("td:eq(9)").html();
            fn_setOtroOrigenEditDocExtRec(pdesDest,ptipDocInden,pnroDocInden,pcodDest,pidDepartamento, pidProvincia, pidDistrito, pdeDireccion,pdeCorreo,ptelefono);    
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setOtroOrigenEditDocExtRec(desDest, tipDocInden, nroDocInden, codDest, idDepartamento, idProvincia, idDistrito, deDireccion,deCorreo,telefono){
    var noForm='documentoExtRecepBean';
    if($("#otroOrigenActivo").val()=="1"){
        //validando si es el otro origen es proveedor
        if(!!!tipDocInden){
            $(".tipoEmiRespCiudadano button").removeAttr('disabled');             
            $("#nuDniAuxRes").removeAttr('readonly');
            $("#deNuDniRes").removeAttr('readonly');
            $(".tipoEmiRespOtros button").removeAttr('disabled');
            $(".tipoEmiRespOtros input").removeAttr('readonly');
            $("#emiResp").removeAttr('disabled');
        }else{
            jQuery('#'+noForm).find('#tiEmi').change();
        }
        jQuery('#'+noForm).find('#deNomOtros').val(desDest);
        jQuery('#'+noForm).find('#coOtros').val(codDest);
        jQuery('#'+noForm).find('#deDocOtros').val(tipDocInden);
        jQuery('#'+noForm).find('#nuDocOtros').val(nroDocInden);
        jQuery('#'+noForm).find('#deNomOtrosRes').val(desDest);
        jQuery('#'+noForm).find('#coOtrosRes').val(codDest);
            
        cargar_ubigeo(idDepartamento,idProvincia,idDistrito);
        jQuery('#'+noForm).find('#deDireccion').val(deDireccion);
        jQuery('#'+noForm).find('#deCorreo').val(deCorreo);
        jQuery('#'+noForm).find('#telefono').val(telefono);         
        
    }else if($("#otroOrigenResActivo").val()=="1"){
        jQuery('#'+noForm).find('#deNomOtrosRes').val(desDest);
        jQuery('#'+noForm).find('#coOtrosRes').val(codDest);
    }
     
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipDocAdm').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    return false;    
}

function fu_changeEstadoDocExtRec(esDocEmi){
    var noForm='documentoExtRecepBean';    
    var valRetorno=validarFormDocExtRecep(noForm);//WECM    
    if(valRetorno==="1"){
        var valRetorno=validarEstadoDocExtRecep(esDocEmi,noForm);
        if(valRetorno==="1"){
                var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
                //verificar si necesita grabar el documento.
                var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
                var nrpta = rpta.substr(0,1);
                if (nrpta === "1") {
                    alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
                }else{
                if(esDocEmi==="5"){//a en registro
                    bootbox.dialog({
                        message: " <h5>¿ Seguro de Guardar los Cambios ?</h5>",
                        buttons: {
                            SI: {
                                label: "SI",
                                className: "btn-primary",
                                callback: function() {
                                    fn_changeToEnRegistroDocExtRecep(noForm);//cambiar en registro
                                }                        
                            },
                            NO: {
                                label: "NO",
                                className: "btn-default"
                            }
                        }
                    });                       
                }else if(esDocEmi==="7"){//para verificar
                    fn_changeToPorVerificarDocExtRecep(noForm);
                }else if(esDocEmi==="8"){//para verificar
                     bootbox.dialog({
                        message: " <h5>¿Observar Documento?</h5>",
                        buttons: {
                            SI: {
                                label: "SI",
                                className: "btn-primary",
                                callback: function() {
                                    fn_changeToObservadoDocExtRecep(noForm);    //cambiar en registro
                                }                        
                            },
                            NO: {
                                label: "NO",
                                className: "btn-default"
                            }
                        }
                    });
                            
                }else if(esDocEmi==="0"){//registrado
                    var valRetorno ="1" ; 
                    var pcoTipoExp=allTrim(jQuery('#'+noForm).find("#coTipoExp").val());
                    var pcoTraDest=allTrim(jQuery('#'+noForm).find("#coTraDest").val());
                    var pinDestinoTramitanteMp=allTrim(jQuery("#inDestinoTramitanteMp").val());
                    if(valRetorno==="1"){
                        if(!(!!pcoTipoExp)||pcoTipoExp==="-1"){
                          valRetorno = "0";  
                          bootbox.alert("<h5>Seleccione tipo de Expediente.</h5>", function() {
                              bootbox.hideAll();
                              jQuery('#'+noForm).find('#coTipoExp').focus();
                          });
                        } 
                     }
                     if(valRetorno==="1" && pinDestinoTramitanteMp==="SI"){
                        if(!(!!pcoTraDest)||pcoTraDest==="-1"){
                          valRetorno = "0";  
                          bootbox.alert("<h5>Seleccione el destino que indica el tramitador.</h5>", function() {
                              bootbox.hideAll();
                              jQuery('#'+noForm).find('#coTraDest').focus();
                          });
                        } 
                     }
                     if(valRetorno==="1"){
                        fn_changeToRegistradoDocExtRecep(noForm);//cambiar a registrado
                     }
                }                    
            }
        }
    }
}


//YUAL Verificar
function fu_changeVerificarDocExtRec(){
    var noForm='documentoExtRecepBean';
    /*var valRetorno=validarFormDocExtRecep(noForm);    
    if(valRetorno==="1"){*/
         //   fn_saveReqExpDocExtRecNoAction();
            var p = new Array();
            var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
            var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
            p[0] = "accion=goVerificaObservado";
            p[1] = "pnuAnn=" + pnuAnn;    
            p[2] = "pnuEmi=" + pnuEmi;    
            ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                if(data.coRespuesta=='0'){
                      bootbox.dialog({
                        message: " <h5>¿Observar Documento?</h5>",
                        buttons: { 
                            SI: {
                                label: "SI",
                                className: "btn-primary",
                                callback: function() {
                                  
                                  //Guardar cambios
                                    var pnuEmi=jQuery('#'+noForm).find("#nuEmi").val();
                                    jQuery('#envDocumentoEmiBean').val("1");
                                     var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
                                    var objSizeTabla = fn_getSizeTblsDocExt(new Function('return ' + cadenaJson)());                        
                                  
                                    ajaxCallSendJson("/srMesaPartes.do?accion=goGrabarDocumento", cadenaJson, function(data) {
                                     fn_changeToObservadoDocExtRecep(noForm);    //cambiar en registro
                            },'json', false, false, "POST"); 
                                   
                                }                        
                            },
                            NO: {
                                label: "NO",
                                className: "btn-default"
                            }
                        }
                    });                  
                }
                else{
                   fn_changeToEnRegistroDocExtRecep(noForm); 
                   
                }               
                
                
            },'json', false, false, "POST");    
                    
             
}



function fn_changeToEnRegistroDocExtRecep(noForm){
    var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "accion=goChangeToEnRegistro";
        p[1] = "pnuAnn=" + pnuAnn;    
        p[2] = "pnuEmi=" + pnuEmi;    
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaChangeToEnRegistroDocExtRecep(data,noForm);
            
            //YUAL Recargar Registro
              var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + pnuEmi;
            ajaxCall("/srMesaPartes.do?accion=goEditDocumentoExtRec", p.join("&"), function(data) {
                jQuery('#divBuscarDocExtRecep').hide();
                jQuery('#divDocExtRecep').show();
                refreshScript("divDocExtRecep", data);
                jQuery('#divTablaDocExtRecep').html("");
                fn_cargaToolBarDocExtRecep();                
                $("#emiResp option[value='02']").remove();
                $("#emiResp option[value='']").remove();

            }, 'text', false, false, "POST");
            
            
        },'json', false, false, "POST");    
    }
}

function fn_rptaChangeToEnRegistroDocExtRecep(data,noForm){
    if(data!==null){
        if (data.coRespuesta==="1") {
            jQuery('#'+noForm).find('#coEsDocEmiMp').val("5");
            jQuery('#envExpedienteEmiBean').val("0");
            jQuery('#envDocumentoEmiBean').val("0");
            jQuery('#envRemitenteEmiBean').val("0"); 
            fn_actualizaFormDocExtRecEstado(jQuery('#'+noForm).find('#coEsDocEmiMp').val(),noForm);
            alert_Sucess("Éxito!", "Transacción completada.");    
            fn_cargaToolBarDocExtRecep();
            removeDomId('windowVerRequisitoDocExterno');
        } else {
            alert_Danger("Mesa Partes :",data.deRespuesta );
        }        
    }
}

function fn_changeToRegistradoDocExtRecep(noForm){
    var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "accion=goChangeToRegistrado";
        p[1] = "pnuAnn=" + pnuAnn;    
        p[2] = "pnuEmi=" + pnuEmi;    
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaChangeToRegistradoDocExtRecep(data,noForm);
            
              //YUAL Recargar Registro
              var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + pnuEmi;
            ajaxCall("/srMesaPartes.do?accion=goEditDocumentoExtRec", p.join("&"), function(data) {
                jQuery('#divBuscarDocExtRecep').hide();
                jQuery('#divDocExtRecep').show();
                refreshScript("divDocExtRecep", data);
                jQuery('#divTablaDocExtRecep').html("");
                fn_cargaToolBarDocExtRecep();                
                $("#emiResp option[value='02']").remove();
                $("#emiResp option[value='']").remove();

            }, 'text', false, false, "POST");
            
            
        },'json', false, false, "POST");    
    }
}

function fn_rptaChangeToRegistradoDocExtRecep(data,noForm){
    if(data!==null){
        if (data.coRespuesta==="1") {
            jQuery('#'+noForm).find('#coEsDocEmiMp').val("0");
            jQuery('#envExpedienteEmiBean').val("0");
            jQuery('#envDocumentoEmiBean').val("0");
            jQuery('#envRemitenteEmiBean').val("0"); 
            fn_actualizaFormDocExtRecEstado(jQuery('#'+noForm).find('#coEsDocEmiMp').val(),noForm);
            alert_Sucess("Éxito!", "Transacción completada.");    
            fn_cargaToolBarDocExtRecep();
        } else {
            alert_Danger("Mesa Partes :",data.deRespuesta );
        }        
    }
}

function validarEstadoDocExtRecep(esDocEmi,noForm){
    var valRetorno="0";
    var esDocEmiReal=jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    if((esDocEmi==="5"&&esDocEmiReal==="0")
        ||(esDocEmi==="7"&&esDocEmiReal==="5")
        ||(esDocEmi==="5"&&esDocEmiReal==="7")
        ||(esDocEmi==="0"&&esDocEmiReal==="7")            
        ||(esDocEmi==="0"&&esDocEmiReal==="8")
        ||(esDocEmi==="5"&&esDocEmiReal==="8")        
            ){
        valRetorno="1";
    }else{
        valRetorno=validarFormDocExtRecep(noForm);
    }
    return valRetorno;
}

function fn_verDocumentoExtRecep(){
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    var ptiOpe = "0";

    if (!!pnuAnn&&!!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);		
	} 
    } else {
        alert_Warning("Mesa Pates :", "Necesita grabar los cambios.");        
    }    
}

function fn_verAnexoDocExtRec(){
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    var pnuDes = "N";
    if (!!pnuAnn && !!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);		
	}         
    } else {
        alert_Info("Mesa Pates :", "Necesita grabar los cambios.");
    }    
}

function fn_verSeguimientoDocExtRec() {
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    var pnuDes = "N";
    if (!!pnuAnn && !!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);		
	}         
    } else {
        alert_Info("Mesa Pates :", "Necesita grabar los cambios.");
    }
}

function fn_cargarDocExteRecep(){
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    if (!!pnuAnn && !!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            var rutactx = pRutaContexto + "/" + pAppVersion;
            var contAux = 0;
            jQuery('#fileuploadAlta').fileupload({
                dataType: 'text',
                add: function(e, data) {
                    if(e.target.id===e.currentTarget.id){
                        loadding(true);
                        var url = "";
                        url = url.concat(rutactx, "/srDocumentoEmisionAlta.do?accion=goUploadEmi", "&pnuAnn=", pnuAnn, "&pnuEmi=", pnuEmi);
                        var goUpload = true;
                        var uploadFile = data.files[0];
                        if (!(/\.(pdf)$/i).test(uploadFile.name)) {
                            alert_Danger("ERROR", " Seleccionar solo pdf.");
                            loadding(false);
                            goUpload = false;
                        }
                        if (goUpload) {
                            data.url = url;
                            data.submit();
                        }
                    }	
                },
                done: function(e, data) {
                    if (data.textStatus === "success"){
                        alert_Sucess("GRABAR", "Documento cargado correctamente");
                    } else {
                        alert_Danger("ERROR", " al cargar el archivo");
                    }
                    $("#progressAlta").hide();
                },
                error: function (e, data) {
                    alert_Danger("ERROR", " al cargar el archivo verifique tamaño");
                    $("#progressAlta").hide();
                    loadding(false);
                },        
                progressall: function(e, data) {
                    var progress = parseInt(data.loaded / data.total * 80, 10);
                    jQuery('#progressAlta .progress-bar').css('width', progress + '%');
                    var progressText = "";
                    if (progress < 30) {
                        progressText = progress + '%';
                    } else {
                        progressText = 'Cargando ' + progress + '%';
                    }
                    jQuery('#progressAlta span').html(progressText);
                }
            });
            jQuery('#progressAlta .progress-bar').css('width', '0%');
            jQuery("#progressAlta").show();
            jQuery('#fileuploadAlta').click();		
	}        
    }else {
        alert_Info("Mesa Pates :", "Necesita grabar los cambios.");
    }
}

function fn_cargarAnexosDocExtRec() {
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();

    if (!!pnuAnn && !!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            fn_cargarDocumentosAnexosObj(pnuAnn, pnuEmi);		
	}          
    } else {
        alert_Info("Mesa Partes :", "Necesita Grabar");
    }
}

function fn_abrirDocumentoExtRecep() {
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    var ptiOpe = "5";

    if (!!pnuAnn && !!pnuEmi) {
        var esGrabaCambio="0";
        if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                alert_Warning("Mesa Partes :", "Necesita grabar los cambios");
            }             
        }
        if(esGrabaCambio==="0"){
            var p = new Array();
            p[0] = "accion=goDocRutaAbrirEmi";
            p[1] = "nuAnn=" + pnuAnn;
            p[2] = "nuEmi=" + pnuEmi;
            p[3] = "tiOpe=" + ptiOpe;
            ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                var result;
                eval("var docs=" + data);
                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                    if (docs.retval === "OK") {
                        var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
                        //runApplet(appletsTramiteDoc.abrirDocumento,param,function(data){
                        runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                            result=data;
                        });
                    } else {
                        alert_Danger("!Repositorio : ", docs.retval);
                    }
                }

            }, 'text', false, false, "POST");
        }
    } else {
        alert_Warning("Mesa Partes :", "Necesita Grabar.");
    }
}

function fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson) {//si es "1" necesita grabar el documento.
    var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
    var rpta = fn_validarEnvioGrabaDocExtRecep(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"||!(!!pnuEmi&&!!pnuAnn)){
        return "1";
    }else{
        return rpta.substr(1);
    }
}

function fn_validarEnvioGrabaDocExtRecep(objTrxDocumentoEmiBean) {
    var vReturn = "0EL DOCUMENTO ES EL MISMO.";
    if (objTrxDocumentoEmiBean !== null && typeof(objTrxDocumentoEmiBean) !== "undefined") {
        if (typeof(objTrxDocumentoEmiBean.documentoEmiBean) !== "undefined" || typeof(objTrxDocumentoEmiBean.expedienteEmiBean) !== "undefined" ||
                typeof(objTrxDocumentoEmiBean.remitenteEmiBean) !== "undefined") {
            vReturn = "1A GRABAR";
        } else if (typeof(objTrxDocumentoEmiBean.lstReferencia) !== "undefined" || typeof(objTrxDocumentoEmiBean.lstDestinatario) !== "undefined") {
            if (objTrxDocumentoEmiBean.lstReferencia.length > 0 || objTrxDocumentoEmiBean.lstDestinatario.length > 0) {
                vReturn = "1A GRABAR";
            }
        }
    }
    return vReturn;
}

function fn_anularDocExtRecep() {
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    if (!!pnuAnn&&!!pnuEmi) {
        var esDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
        if(!!esDocEmi&&esDocEmi==="5"){
            bootbox.dialog({
                message: " <h5>¿ Esta Seguro de Anular Documento ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-primary",
                        callback: function() {
                            var p = new Array();
                            p[0] = "accion=goAnularDocExtRecep";
                            p[1] = "pnuAnn=" + pnuAnn;    
                            p[2] = "pnuEmi=" + pnuEmi;   
                            p[3] = "pesDocEmi=" + esDocEmi;
                            ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                                fn_rptAnularDocExtRecep(data);
                            }, 'json', false, false, "POST");
                        }                        
                    },
                    NO: {
                        label: "NO",
                        className: "btn-default"
                    }
                }
            });             
        }
    }else {
        alert_Info("Mesa Pates :", "Necesita grabar los cambios.");
    }
}

function fn_rptAnularDocExtRecep(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            fn_valRegresarRecepDocExtAtender();
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
}

function afterCerrarPantallaMP(){
    var noForm='documentoExtRecepBean';
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    var pesDocEmi = jQuery('#'+noForm).find('#coEsDocEmiMp').val();
    
    var esGrabaCambio="0";
    if(!!pesDocEmi&&(pesDocEmi==="5"||pesDocEmi==="7")){
        if(!!pnuAnn&&!!pnuEmi){
            var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {
                esGrabaCambio="1";
                bootbox.dialog({
                    message: " <h5>Existen Cambios en el Documento.\n" +
                            "¿ Desea salir del Documento ?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-default",
                            callback: function() {
                                cerrarPantallaMP(); 
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-primary"
                        }
                    }
                });
            }            
        }
    }
    if(esGrabaCambio==="0"){
       cerrarPantallaMP(); 
    }    
}

function fn_verDocDocExtRecep(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    var ptiOpe = "0";
    if(!!pnuAnn&&!!pnuEmi){
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    }
}

function fn_verAnexoDocExtRecep(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    if(!!pnuAnn&&!!pnuEmi){    
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);	
    }
}

function fn_verSeguimientoDocExtRecep(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    if(!!pnuAnn&&!!pnuEmi){    
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);		
    }
}

function fn_verDocumentoRefEmiDocExt(cell) {
    var indexFila = (($(cell).parent()).parent()).index();
    var isOk = $("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ") td").last().html();
    if (isOk === "1") {
        var pnuAnn = ($(cell).parent()).parent().children().get(6).innerHTML;
        var pnuEmi = ($(cell).parent()).parent().children().get(7).innerHTML;
        fn_verDocumentosObj(pnuAnn, pnuEmi, "0");
    } else {
        return;
    }
}

function fn_changeTupaGetDiasAteMp(cmbTupa){
    var noForm='#documentoExtRecepBean';
    var pcoProceso=$(cmbTupa).val();
    if(!!pcoProceso){
        var p = new Array();
        p[0] = "accion=goProcesoExp";
        p[1] = "pcoProceso=" + pcoProceso;    
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaChangeTupaGetDiasAteMp(data,noForm);
        },'json', false, true, "POST");                
    }
}

function fn_rptaChangeTupaGetDiasAteMp(data,noForm){
    if(data!==null){
        if (data.coRespuesta==="1") {
            jQuery(noForm).find('#nuDiaAte').val(!!data.nuDiaAte&&data.nuDiaAte!=="null"?data.nuDiaAte:'0');
            var asunto = jQuery(noForm).find('#deAsu');
            if(!!data.deAsunto&&data.deAsunto!==null&&data.deAsunto!=="null"){
                if(allTrim(asunto.val()).length>0){
                    bootbox.dialog({
                        message: " <h5>¿ Seguro de Reemplazar Asunto ?</h5>",
                        buttons: {
                            SI: {
                                label: "SI",
                                className: "btn-primary",
                                callback: function() {
                                   asunto.val(data.deAsunto); 
                                }                        
                            },
                            NO: {
                                label: "NO",
                                className: "btn-default"
                            }
                        }
                    });                    
                }else{
                    asunto.val(data.deAsunto);
                }
            }
            jQuery('#envDocumentoEmiBean').val("1");
        } else {
            alert_Danger("Mesa Partes :","ERROR EN BASE DE DATOS AL OBTENER DIAS ATENCIÓN." );
        }        
    }    
}

function fn_iniDestDocExtRecMp(){
        var tableaux = $('#tlbDestinoEmi');
        tableaux.find('tr').each(function(index, row) {
            if(index == 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tlbDestinoEmi');
                var value = this.value;
                //alert(evento.which);
                var isFirst = false;
                var indexSelect = -1;
                table.find('tr').each(function(index, row) {
                        if ( $(this).hasClass('row_selected') ) {
                            $(this).removeClass('row_selected');
                        }
                        //var allCells = $(row).find('td');
                        var allCells = $(row).find('td');
                        if(allCells.length > 0) {
                                var found = false;
                                allCells.each(function(index, td) {
                                        var regExp = new RegExp(value, 'i');
                                        if(regExp.test($(td).text())) {
                                                found = true;
                                                return false;
                                        }
                                });
                                if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                                else {$(row).hide();}
                        }
                });
                if(evento.which == 13){
                    if(isFirst){
                        var pdesDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        var pcodDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                        fu_setDatoDestinatarioEmiDocExtRecMp(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioEmiDocExtRecMp(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioEmiDocExtRecMp(cod, desc) {
    var noForm='#buscarDocumentoExtRecepBean';
    jQuery(noForm).find('#txtDestinatario').val(desc);
    jQuery(noForm).find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fn_changeToPorVerificarDocExtRecep(noForm){
    var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "accion=goChangeToParaVerificar";
        p[1] = "pnuAnn=" + pnuAnn;    
        p[2] = "pnuEmi=" + pnuEmi;    
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaChangeToPorVerificarDocExtRecep(data,noForm);
        },'json', false, false, "POST");    
    }    
}

//YUAL
function fn_changeToObservadoDocExtRecep(noForm){
    var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "accion=goChangeToObservado";
        p[1] = "pnuAnn=" + pnuAnn;    
        p[2] = "pnuEmi=" + pnuEmi;    
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaChangeObservadoDocExtRecep(data,noForm);
        },'json', false, false, "POST");    
    }    
}

function fn_rptaChangeToPorVerificarDocExtRecep(data,noForm){
    if(data!==null){
        if (data.coRespuesta==="1") {
            jQuery('#'+noForm).find('#coEsDocEmiMp').val("7");
            jQuery('#envExpedienteEmiBean').val("0");
            jQuery('#envDocumentoEmiBean').val("0");
            jQuery('#envRemitenteEmiBean').val("0"); 
            fn_actualizaFormDocExtRecEstado(jQuery('#'+noForm).find('#coEsDocEmiMp').val(),noForm);
            alert_Sucess("Éxito!", "Transacción completada.");    
            fn_cargaToolBarDocExtRecep();
        } else {
            alert_Danger("Mesa Partes :",data.deRespuesta );
        }        
    }
}

//YUAL
function fn_rptaChangeObservadoDocExtRecep(data,noForm){
    if(data!==null){
        if (data.coRespuesta==="1") {
            jQuery('#'+noForm).find('#coEsDocEmiMp').val("8");
            jQuery('#envExpedienteEmiBean').val("0");
            jQuery('#envDocumentoEmiBean').val("0");
            jQuery('#envRemitenteEmiBean').val("0"); 
            fn_actualizaFormDocExtRecEstado(jQuery('#'+noForm).find('#coEsDocEmiMp').val(),noForm);
            alert_Sucess("Éxito!", "Transacción completada.");    
            fn_cargaToolBarDocExtRecep();
            removeDomId('windowVerRequisitoDocExterno');
        } else {
            alert_Danger("Mesa Partes :",data.deRespuesta );
        }        
    }
}

function fn_buscaLocalEmpleadoEdit(){
    //var noForm='#buscarDocumentoExtConsulBean';
    var p = new Array();
    p[0] = "accion=goBuscaLocalEmpleado";
    //p[1] = "pnuAnn=" + jQuery(noForm).find('#coAnnio').val();
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaLocalRegRecDocExt(data);
    },'text', false, false, "POST");
}
function fn_buscaLocalRegRecDocExt(){
    //var noForm='#buscarDocumentoExtConsulBean';
    var p = new Array();
    p[0] = "accion=goBuscaLocal";
    //p[1] = "pnuAnn=" + jQuery(noForm).find('#coAnnio').val();
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaLocalRegRecDocExt(data);
    },'text', false, false, "POST");
}

function fn_rptaBuscaLocalRegRecDocExt(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_iniRegRecDocExtLocalRecDocExt(){
        var nomTbl='#tlbLocalConsDocExtModal';
        var indexAux=-1;
        var tableaux = $(nomTbl);
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $(nomTbl);
                var value = this.value;
                //alert(evento.which);
                var isFirst = false;
                var indexSelect = -1;
                table.find('tr').each(function(index, row) {
                        if ( $(this).hasClass('row_selected') ) {
                            $(this).removeClass('row_selected');
                        }
                        //var allCells = $(row).find('td');
                        var allCells = $(row).find('td:eq(0)');
                        if(allCells.length > 0) {
                                var found = false;
                                allCells.each(function(index, td) {
                                        var regExp = new RegExp(value, 'i');
                                        if(regExp.test($(td).text())) {
                                                found = true;
                                                return false;
                                        }
                                });
                                if (found) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                                else {$(row).hide();}
                        }
                });
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoLocalRegRecDocExt(pcodDest,pdesDest);
                    }
                }else if(evento.which===38||evento.which===40){
                    //$('#txtConsultaFind').blur();
                    indexAux=-1;
                    $(nomTbl+' >tbody >tr:visible').first().focus(); 
                }
        };        
        
        $('#txtConsultaFind').keyup(searchOnTable);
        $(nomTbl+" tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoLocalRegRecDocExt(pcodDest,pdesDest);            
        });
        $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
            if(evento.which===38){//up
                if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length === 0) {
                    indexAux=$(nomTbl+" tbody tr:visible").length;
                }
                $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
                indexAux--;
                $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
                $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
            }else if(evento.which===40){//down
                if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length === 0){
                    indexAux=-1;
                }
                $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
                indexAux++;
                $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
                $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
            }else if(evento.which===13){
                var pdesDest= $(this).find("td:eq(0)").html();
                var pcodDest= $(this).find("td:eq(1)").html();
                fu_setDatoLocalRegRecDocExt(pcodDest,pdesDest);           
            }
            //console.log(indexAux);
            evento.preventDefault();
        });        
}
function fn_iniRegRecDocExtLocalEmpleadoEdit(){
        var nomTbl='#tlbLocalConsDocExtModal';
        var indexAux=-1;
        var tableaux = $(nomTbl);
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $(nomTbl);
                var value = this.value;
                //alert(evento.which);
                var isFirst = false;
                var indexSelect = -1;
                table.find('tr').each(function(index, row) {
                        if ( $(this).hasClass('row_selected') ) {
                            $(this).removeClass('row_selected');
                        }
                        //var allCells = $(row).find('td');
                        var allCells = $(row).find('td:eq(0)');
                        if(allCells.length > 0) {
                                var found = false;
                                allCells.each(function(index, td) {
                                        var regExp = new RegExp(value, 'i');
                                        if(regExp.test($(td).text())) {
                                                found = true;
                                                return false;
                                        }
                                });
                                if (found) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                                else {$(row).hide();}
                        }
                });
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoLocalRegEmpleadoEditDet(pcodDest,pdesDest);
                    }
                }else if(evento.which===38||evento.which===40){
                    //$('#txtConsultaFind').blur();
                    indexAux=-1;
                    $(nomTbl+' >tbody >tr:visible').first().focus(); 
                }
        };        
        
        $('#txtConsultaFind').keyup(searchOnTable);
        $(nomTbl+" tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoLocalRegEmpleadoEditDet(pcodDest,pdesDest);            
        });
        $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
            if(evento.which===38){//up
                if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length === 0) {
                    indexAux=$(nomTbl+" tbody tr:visible").length;
                }
                $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
                indexAux--;
                $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
                $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
            }else if(evento.which===40){//down
                if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length === 0){
                    indexAux=-1;
                }
                $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
                indexAux++;
                $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
                $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
            }else if(evento.which===13){
                var pdesDest= $(this).find("td:eq(0)").html();
                var pcodDest= $(this).find("td:eq(1)").html();
                fu_setDatoLocalRegEmpleadoEditDet(pcodDest,pdesDest);           
            }
            //console.log(indexAux);
            evento.preventDefault();
        });        
}
function fu_setDatoLocalRegEmpleadoEditDet(cod, desc) {
    jQuery('#noLocal').val(desc);
    jQuery('#coLocal').val(cod);
    removeDomId('windowConsultaLocalDocExt'); 
}

function fu_setDatoLocalRegRecDocExt(cod, desc) {
    jQuery('#buscarDocumentoExtRecepBean').find('#txtLocal').val(desc);
    jQuery('#buscarDocumentoExtRecepBean').find('#coLocEmi').val(cod);
    removeDomId('windowConsultaLocalDocExt');
    //jQuery("#coEmpDestino").val("");
    //jQuery("#txtEmpDestino").val("[TODOS]");    
}

function fn_buscaDepenOrigenRegDocExt() {
    var p = new Array();
    p[0] = "accion=goBuscaAllDependencia";
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        fn_rptaBuscaDepenOrigenRegDocExt(data);
    },'text', false, false, "POST");
}

function fn_rptaBuscaDepenOrigenRegDocExt(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_iniFindAllDepBuscarDocExtMp(){
            var tableaux = $('#tlbReferenOrig');
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tlbReferenOrig');
                var value = this.value;
                //alert(evento.which);
                var isFirst = false;
                var indexSelect = -1;
                table.find('tr').each(function(index, row) {
                        if ( $(this).hasClass('row_selected') ) {
                            $(this).removeClass('row_selected');
                        }
                        //var allCells = $(row).find('td');
                        var allCells = $(row).find('td');
                        if(allCells.length > 0) {
                                var found = false;
                                allCells.each(function(index, td) {
                                        var regExp = new RegExp(value, 'i');
                                        if(regExp.test($(td).text())) {
                                                found = true;
                                                return false;
                                        }
                                });
                if (found === true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
                        }
                });
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        var pcodDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                        fu_setDatoDepBuscarDocExtMp(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDepBuscarDocExtMp(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDepBuscarDocExtMp(cod, desc) {
    var noForm='#buscarDocumentoExtRecepBean';
    jQuery(noForm).find('#txtDeDepOriRec').val(desc);
    jQuery(noForm).find('#coDepOriRec').val(cod);
    removeDomId('windowConsultaRefOri');
}

function fu_generarVoucherExpediente() {
    var noForm = '#documentoExtRecepBean';
    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
    if (!!pnuAnn && !!pnuEmi) {        
        var p = new Array();
        p[0] = "accion=goExportarVoucher";
        p[1] = "nuAnn=" + pnuAnn;    
        p[2] = "nuEmi=" + pnuEmi;   
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {          
            if (data!==null) {
                if (data.coRespuesta==="0") {
                    if(!!data.noUrl&&!!data.noDoc){
                        /*fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK") {
                               bootbox.alert(result);
                            }
                        });*/
                        var param={urlDoc:data.noUrl,rutaDoc:data.noDoc};
                        runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                            var result = data;
                        });
                    }
                } else {
                   bootbox.alert(data.deRespuesta);
                }
            } else {
               bootbox.alert("La respuesta del servidor es nula.");
            }
        }, 'json', false, true, "POST");
    } else {
        alert_Info("Mesa Partes :", "Necesita grabar los cambios.");
    }
}

//YUAL VerificarDOcumentoObservado
function fn_getVerifReqExpedienteDocExtRec(){
    var noForm='documentoExtRecepBean';
    var pnuAnnExp = jQuery('#'+noForm).find('#nuAnnExp').val();
    var pnuSecExp = jQuery('#'+noForm).find('#nuSecExp').val();
    var pcoProceso = jQuery('#'+noForm).find('#coProceso').val();
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
 
           var p = new Array();
            var pnuAnn=jQuery('#'+noForm).find('#nuAnn').val();
            var pnuEmi=jQuery('#'+noForm).find('#nuEmi').val();
            p[0] = "accion=goVerificaObservado";
            p[1] = "pnuAnn=" + pnuAnn;    
            p[2] = "pnuEmi=" + pnuEmi;    
            ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                console.log('data.coRespuesta:'+data.coRespuesta);
                if(data.coRespuesta=='0'){
                          
                        var params = {"accion":"goValidarObservadoExp" ,"nuAnnExp": pnuAnnExp, "nuSecExp": pnuSecExp, "coProceso": pcoProceso,
                                        "pnuAnn":pnuAnn, "pnuEmi":pnuEmi};        
                        ajaxCall("/srMesaPartes.do", params, function(data) {
                            fn_rptaGetRequisitosExpedienteDocExtRec(data);
                        },'text', false, false, "POST");           

                }
                else{
                   fn_changeToEnRegistroDocExtRecep(noForm); 
                }               
                
                
            },'json', false, false, "POST");   
            
        //verificar si necesita grabar el documento.
         
        
     
}


function fn_getRequisitosExpedienteDocExtRec(){
    var noForm='documentoExtRecepBean';
    var pnuAnnExp = jQuery('#'+noForm).find('#nuAnnExp').val();
    var pnuSecExp = jQuery('#'+noForm).find('#nuSecExp').val();
    var pcoProceso = jQuery('#'+noForm).find('#coProceso').val();
    var pnuAnn = jQuery('#'+noForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+noForm).find('#nuEmi').val();
    if (!!pnuAnnExp && !!pnuSecExp && !!pcoProceso && !!pnuAnn && !!pnuEmi) {
        var cadenaJson=fn_buildSendJsontoServerDocExtRecep(noForm);
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeDocumentoExtRecep(noForm,cadenaJson);
        var nrpta = rpta.substr(0,1);
        if (nrpta === "1") {        
            alert_Info("Mesa Partes :", "Necesita grabar los cambios.");
        }else{
            var params = {"accion":"goRequisitosExpDocExtRec" ,"nuAnnExp": pnuAnnExp, "nuSecExp": pnuSecExp, "coProceso": pcoProceso,
                            "pnuAnn":pnuAnn, "pnuEmi":pnuEmi};        
            ajaxCall("/srMesaPartes.do", params, function(data) {
                fn_rptaGetRequisitosExpedienteDocExtRec(data);
            },'text', false, false, "POST");                    
        }
    } else {
        alert_Info("Mesa Partes :", "Necesita grabar los cambios.");
    }    
}

function fn_rptaGetRequisitosExpedienteDocExtRec(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_saveReqExpDocExtRec(){
    var noForm='#documentoExtRecepBean';
    var pnuAnnExp = jQuery(noForm).find('#nuAnnExp').val();
    var pnuSecExp = jQuery(noForm).find('#nuSecExp').val();
    var pcoProceso = jQuery(noForm).find('#coProceso').val();    
    var bandera = jQuery('#txtIndChangeReqRecDocExt').val()==='1';
    if (!!pnuAnnExp && !!pnuSecExp && !!pcoProceso) {
        if(bandera){
            var cadenaJson = fn_tblReqDocExtRecToJson();
            if(fn_verificarLsReqRecDocExtMp(new Function('return ' + cadenaJson)())){
                ajaxCallSendJson("/srMesaPartes.do?accion=goSaveReqExpDocExtRec&nuAnnExp="+pnuAnnExp+
                        "&nuSecExp="+pnuSecExp+"&coProceso="+pcoProceso, cadenaJson, function(data) {    
                    fn_rptaSaveReqExpDocExtRec(data);
                },'json', false, false, "POST");        
            }            
        }else{
           alert_Info("", "EL DOCUMENTO ES EL MISMO.");
        }
    }
}
//YUAL
function fn_saveReqExpDocExtRecNoAction(){
    var noForm='#documentoExtRecepBean';
    var pnuAnnExp = jQuery(noForm).find('#nuAnnExp').val();
    var pnuSecExp = jQuery(noForm).find('#nuSecExp').val();
    var pcoProceso = jQuery(noForm).find('#coProceso').val();    
   
    if (!!pnuAnnExp && !!pnuSecExp && !!pcoProceso) {
    
            var cadenaJson = fn_tblReqDocExtRecToJson();
      
                ajaxCallSendJson("/srMesaPartes.do?accion=goSaveReqExpDocExtRec&nuAnnExp="+pnuAnnExp+
                        "&nuSecExp="+pnuSecExp+"&coProceso="+pcoProceso, cadenaJson, function(data) {    
                   // fn_rptaSaveReqExpDocExtRec(data);
                   fu_changeVerificarDocExtRec();
                   
                },'json', false, false, "POST");        
                  
       
    }
}

function fn_rptaSaveReqExpDocExtRec(data){
    if (data !== null) {
        if (data.coRespuesta==="1") {
            jQuery('#txtIndChangeReqRecDocExt').val('0');
            alert_Sucess("Éxito!", "Grabado correctamente.");
        } else {
            alert_Info("Mesa Partes :", data.deRespuesta);
        }
    }
}

function fn_verificarLsReqRecDocExtMp(LsReq){
    var vReturn=0;
    if(!!LsReq){
        if(LsReq.length>0){
            vReturn=1;
        }
    }
    return !!vReturn;    
}

function fn_tblReqDocExtRecToJson() {
    var noTbl="#tblDetReqDocExt";
    var arrColMostrar = new Array();
    //arrColMostrar[0] = "accionBD=11";
    arrColMostrar[0] = "codRequisito=1";
    arrColMostrar[1] = "nuCorrelativo=2";
    arrColMostrar[2] = "docPresente=4";
    return fn_tblDetReqDocExthtml2json(noTbl, 0, arrColMostrar, null, null, null, null);
}

/**
 * 
 * @param {type} idTable nombre de la tabla
 * @param {type} iniFila a partir de que fila se empieza a contar
 * @param {type} colMostrar columnas a enviar al server con sus respectivos campos
 * @param {type} colEstadoActivo columna que tiene el estado activo
 * @param {type} estadoActivo valor activo
 * @param {type} colAccionBD columna que tiene la accion en bd a no enviar
 * @param {type} accionBD valor de fila a no enviar
 * @returns {String} return json.
 */
function fn_tblDetReqDocExthtml2json(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
    var json = '[';
    var otArr = [];
    var count = 0;
    $(idTable + ' tr').each(function(i) {
        if (count >= iniFila) {
            var x = $(this).children();
            var itArr = [];
            //if ($(this).find("td").eq(colEstadoActivo - 1).text() === estadoActivo && $(this).find("td").eq(colAccionBD - 1).text() !== accionBD) {
                x.each(function(index) {
                    for (var i = 0; i < colMostrar.length; i++) {
                        var auxArrColMostrar = colMostrar[i].split("=");
                        if (auxArrColMostrar[1] * 1 === index + 1) {
                            var campoBean = auxArrColMostrar[0];
                            var valCampoBean = "";
                            if (typeof($(this).find('input[type=text]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=text]').val();
                            } else if (typeof($(this).find('select').val()) !== "undefined") {
                                valCampoBean = $(this).find('select').val();
                            } else if (typeof($(this).find('input[type=checkbox]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=checkbox]').is(':checked');
                            } else {
                                valCampoBean = $(this).text();
                            }
                            itArr.push('"' + campoBean + '":' + JSON.stringify(valCampoBean));
                        }
                    }
                });
                otArr.push('{' + itArr.join(',') + '}');
            //}
        }
        count++;
    });
    json += otArr.join(",") + ']';
    return json;
}

function fn_CleanDatosRemitenteDocExtRec(noForm,idDiv){
    $(noForm).find(idDiv).find('input').val('');
}

function chooseProvincia(cmbDep) {
    //el value del combo departamento
    var idDepartamento = $(cmbDep).val();
    var p = new Array();
    p[0] = "accion=getProvincias";
    p[1] = "idDepartamento=" + idDepartamento;
    ajaxCall("/srMesaPartes.do", p.join("&"), function (data) {
        if (!!data) {
            $("#idProvincia").find('option:not(:first)').remove();
            $("#idDistrito").find('option:not(:first)').remove();
            $.each(data, function (i, item) {
                $("#idProvincia").append('<option value=' + item.coProvincia + '>' + item.noProvincia + '</option>');
            });
            jQuery('#envRemitenteEmiBean').val("1");
        }
    }, 'json', true, true, "POST");

}

function chooseDistrito(cmbProv) {
    //el value del combo departamento
    var idProvincia = $(cmbProv).val();
    var idDepartamento = jQuery('#documentoExtRecepBean').find('#idDepartamento').val();
    var p = new Array();
    p[0] = "accion=getDistrito";
    p[1] = "idDepartamento=" + idDepartamento;
    p[2] = "idProvincia=" + idProvincia;
    ajaxCall("/srMesaPartes.do", p.join("&"), function (data) {
        if (!!data) {
            //$("#idProvincia").find('option:not(:first)').remove();
            $("#idDistrito").find('option:not(:first)').remove();
            $.each(data, function (i, item) {
                $("#idDistrito").append('<option value=' + item.coDistrito + '>' + item.noDistrito + '</option>');
            });
            jQuery('#envRemitenteEmiBean').val("1");
        }
    }, 'json', true, true, "POST");
}

function seleccionarDistrito() {
    //Seleccionamos Distrito
    jQuery('#envRemitenteEmiBean').val("1");
}

//INI_DESPACHO_PRESIDENCIAL
function fn_changeTipoExp(objTipoExp){
    var tipoExp = $(objTipoExp).val();
   var rucCongreso = jQuery("#rucCongreso").val();    
   var codInvitacion = jQuery("#codInvitacion").val();
   var codAutografa = jQuery("#codAutografa").val();
   
    var nuRuc = $("#nuRuc").val();
    var tiEmi = $("#tiEmi").val();
    if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp ==codInvitacion){//invitacion
        $(".esCongresoDatosCong").show();                        
        $(".esCongresoTipoInv").show();
        $(".esCongresoNumSobre").hide();
        
        $("#nSobre").val("");
        $("#anioSobre").val("");
    }else if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp ==codAutografa){//autografa ¿proyectos?
        $(".esCongresoDatosCong").hide();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").show();
        
        $("#reiterativo").removeAttr('checked').val('0');   
        $("#coComision").val("");
        $("#coTipoCongresista").val("");
        $("#coTipoInv").val("");
    }else if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp !=codAutografa && tipoExp !=codInvitacion){
        $(".esCongresoDatosCong").show();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").hide();
        
        $("#reiterativo").removeAttr('checked').val('0');
        $("#coTipoInv").val("");
        $("#nSobre").val("");
        $("#anioSobre").val("");
    }else{
        $(".esCongresoDatosCong").hide();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").hide();
        
        $("#reiterativo").removeAttr('checked').val('0');   
        $("#coComision").val("");
        $("#coTipoCongresista").val("");
        $("#coTipoInv").val("");
        $("#nSobre").val("");
        $("#anioSobre").val("");
    }
    jQuery('#envExpedienteEmiBean').val("1");
}

function fn_iniVerificaCongreso(){
    var noForm='documentoExtRecepBean';
    var tipoExp = jQuery('#'+noForm).find("#coTipoExp").val();
   var rucCongreso = jQuery("#rucCongreso").val();    
   var codInvitacion = jQuery("#codInvitacion").val();
   var codAutografa = jQuery("#codAutografa").val();
   
    var nuRuc = $("#nuRuc").val();
    var tiEmi = $("#tiEmi").val();
    if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp ==codInvitacion){//invitacion
        $(".esCongresoDatosCong").show();                        
        $(".esCongresoTipoInv").show();
        $(".esCongresoNumSobre").hide();
    }else if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp ==codAutografa){//autografa ¿proyectos?
        $(".esCongresoDatosCong").hide();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").show();
    }else if(tiEmi=="02" && nuRuc==rucCongreso && tipoExp !=codAutografa && tipoExp !=codInvitacion){
        $(".esCongresoDatosCong").show();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").hide();
    }else{
        $(".esCongresoDatosCong").hide();                        
        $(".esCongresoTipoInv").hide();
        $(".esCongresoNumSobre").hide();
    }
}

function fn_changeEmiResp(objEmiResp){
    var tiEmiResp = $(objEmiResp).val();
    if(tiEmiResp=="03"){
        $(".tipoEmiRespCiudadano").show();
        $(".tipoEmiRespOtros").hide();
    }else if(tiEmiResp=="04"){        
        $(".tipoEmiRespCiudadano").hide();
        $(".tipoEmiRespOtros").show();
    }    
    //limpiarRemitente();
    jQuery("#envRemitenteEmiBean").val("1");
}
function cargar_ubigeo(codDep,codProv,codDist){    
    $("#idDepartamento").val(codDep);
    chooseProvincia($("#idDepartamento"));
    $("#idProvincia").val(codProv);
    chooseDistrito($("#idProvincia"));
    $("#idDistrito").val(codDist);
}

function limpiarRemitente(){
    $("#nuDniAuxRes").val("");
    $("#deNuDniRes").val("");
    $("#nuDniRes").val("");
    $("#deNomOtrosRes").val("");
    $("#coOtrosRes").val(""); 
    cargar_ubigeo("","","");
    $("#deDireccion").val("");
    $("#deCorreo").val("");
    $("#telefono").val("");
    $("#deCargo").val("");
    $("#notificado").removeAttr('checked').val('0');
}

function verificaCiudadano(pnuDni){
    var valRetorno = true;
    var p = new Array();
    p[0] = "accion=goBuscaCiudadano";
    p[1] = "pnuDoc=" + pnuDni;
    ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
        if(data.coRespuesta==='1'){
            valRetorno = true;
        }else{
            valRetorno = false;
        }
    },'json', true, false, "POST"); 
    
    return valRetorno;
}
//FIN_DESPACHO_PRESIDENCIAL

function fu_generarReporteDocExtRecXLS(){
   fu_generarReporteDocExt('XLS'); 
}

function fu_generarReporteDocExtRecPDF(){
   fu_generarReporteDocExt('PDF');  
}

function fu_generarReporteDocExt(pformatoReporte){
    var noForm='#buscarDocumentoExtRecepBean';
    var validaFiltro = fu_validaFiltroRecepDocExtConsul("0",noForm);
    
   
    if (validaFiltro === "1") {
        // ajaxCall("/srConsultaRecDocExterno.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srMesaPartes.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {    
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        /*fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK") {
                               bootbox.alert(result);
                            }
                        });*/
                        var param={urlDoc:data.noUrl,rutaDoc:data.noDoc};
                        runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                            var result = data;
                        });
                    }
                }else{
                   bootbox.alert(data.deRespuesta);
                }
            }else{
               bootbox.alert("La respuesta del servidor es nula.");
            }
        }, 'json', false, true, "POST");
    }
    return false;
}

function fn_changeTipoRemiDocExteRecep_(cmbTipoRemite,esIni){
    var noForm='buscarDocumentoExtRecepBean';
    jQuery('#'+noForm).find("#busResultado").val("");
    var pcoTipoRemi=$(cmbTipoRemite).val();
    
    if(!!pcoTipoRemi){
        if(esIni==="0"){
            jQuery('#'+noForm).find('#nuCorDoc').val("");
            jQuery('#envRemitenteEmiBean').val("1");
            jQuery('#envDocumentoEmiBean').val("1");
        }
        $("#divRemPersonaJuri_").hide();
        $("#divRemCiudadano_").hide();
        $("#divRemOtros_").hide();        
        if(pcoTipoRemi==='02'){//juridica
            $("#divRemPersonaJuri_").show();
             jQuery('#divRemCiudadano_').find('input').val("");
             jQuery('#divRemOtros_').find('input').val("");            
            jQuery('#'+noForm).find('#nuRucAux').focus();
        }else if(pcoTipoRemi==='03'){//ciudadano
            $("#divRemCiudadano_").show();
             jQuery('#divRemPersonaJuri_').find('input').val("");
             jQuery('#divRemOtros_').find('input').val("");            
             jQuery('#'+noForm).find('#nuDniAux').focus();
        }else if(pcoTipoRemi==='04'){//otros
            $("#divRemOtros_").show();
             jQuery('#divRemPersonaJuri_').find('input').val("");
             jQuery('#divRemCiudadano_').find('input').val("");            
             jQuery('#'+noForm).find('#divRemOtros_').focus();
        }
    }
}

function fn_getCiudadanoRemDocExtRecBus()
{
    var noForm='buscarDocumentoExtRecepBean';
    var pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
    var vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    if(vResult){
    var p = new Array();
        p[0] = "accion=goBuscaCiudadano";
        p[1] = "pnuDoc=" + pnuDniAux;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaGetCiudadanoRemDocExtRecBus(data,noForm);
        },'json', false, false, "POST");   
    }    
    
}

function fn_rptaGetCiudadanoRemDocExtRecBus(data,noForm){
    if(data!==null){
        if(data.coRespuesta==="1"){
            var obj = data.ciudadanoBean;
            if(!!obj){
            jQuery('#'+noForm).find("#busNumDni").val(obj.nuDoc);
            jQuery('#'+noForm).find("#nuDniAux").val(obj.nuDoc);
            jQuery('#'+noForm).find("#busDescDni").val(obj.nombre);
            jQuery('#'+noForm).find("#coTipoPersona").focus();
            jQuery('#envRemitenteEmiBean').val("1");
            
            jQuery('#'+noForm).find("#busResultado").val("1");
            
        }else{
                bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
                    bootbox.hideAll();
                    jQuery('#'+noForm).find('#nuDniAux').focus();
                    jQuery('#'+noForm).find("#busNumDni").val('');
                    jQuery('#'+noForm).find("#busDescDni").val('');
                    
                    jQuery('#'+noForm).find("#busResultado").val("");
                });                 
            }
        }else{
            bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
                //fn_CleanDatosRemitenteDocExtRec('#'+noForm,'#divRemCiudadano');
                jQuery('#'+noForm).find("#busNumDni").val('');
                jQuery('#'+noForm).find("#busDescDni").val('');
                bootbox.hideAll();
                jQuery('#'+noForm).find('#nuDniAux').focus();
                
                jQuery('#'+noForm).find("#busResultado").val("");
            });            
        }
    }
}

function onclickBuscarProveedorDocExtRecepBus(){
    var noForm='buscarDocumentoExtRecepBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusJson(pnuRucAux,noForm);
    }
    /*else{
        buscarProveedorEditDocExtRecepBus(prazonSocial);
    }*/
}

function buscarProveedorEditDocExtRecepBusJson(pnuRuc,noForm){
    if(validarBuscarProveedorEditDocExt(pnuRuc)){
        var p = new Array();
        p[0] = "accion=goBuscaProveedorJson";
        p[1] = "pnuRuc=" + pnuRuc;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            if (!!data){
                if(data.coRespuesta==="1"){
                    var obj = data.proveedorBean;
                    if(!!obj){
                    jQuery('#'+noForm).find('#busDescRuc').val(obj.deRuc);
                    jQuery('#'+noForm).find('#busNumRuc').val(obj.nuRuc);
                    jQuery('#'+noForm).find('#nuRucAux').val(obj.nuRuc);
                    jQuery('#'+noForm).find('#coTipoPersona').focus();
                    jQuery('#envRemitenteEmiBean').val("1");
                    
                    jQuery('#'+noForm).find('#busResultado').val("1");
                    }else{
                        bootbox.alert("<h5>Número de RUC no registrado.</h5>", function() {
                            bootbox.hideAll();
                            jQuery('#'+noForm).find('#nuRucAux').focus();
                            
                            jQuery('#'+noForm).find('#busResultado').val('');
                            jQuery('#'+noForm).find('#busDescRuc').val('');
                        });                         
                    }
                }else{
                    
                    
                    
                    
                    bootbox.alert("<h5>Número de RUC no registrado.</h5>", function() {
                        //Ver el limpiado de datos
                        fn_CleanDatosRemitenteDocExtRec('#'+noForm,'#divRemPersonaJuri');
                        
                        jQuery('#'+noForm).find('#busResultado').val("");
                        jQuery('#'+noForm).find('#busDescRuc').val('');
                        
                        bootbox.hideAll();
                        jQuery('#'+noForm).find('#nuRucAux').focus();
                    });
                    
                    
                    
                    
                }
            }
        }, 'json', false, false, "POST");        
    }
}

function fn_getOtroOrigenRemDocExtRecBus()
{    
    var noForm='buscarDocumentoExtRecepBean';
    var pdesOtroOri=allTrim(jQuery('#'+noForm).find("#busNomOtros").val()).toUpperCase();
    var vResult=validaCaracteres(pdesOtroOri, "2");
    if(pdesOtroOri.length >= 0 && pdesOtroOri.length <= 1){  //El texto es entre 0 y 1 caracteres
        bootbox.alert("<h5>Debe ingresar como mínimo 2 caracteres.</h5>", function() {
            bootbox.hideAll();        
        });
        vResult="NO_OK";
    }
    if(vResult == "OK"){
    var p = new Array();
        p[0] = "accion=goBuscaOtroOrigen";
        p[1] = "pdesOtroOri=" + pdesOtroOri;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaBuscarRemitenteOtroDocExtRec(data);
        },'text', false, false, "POST"); 
    }
    return false;
}

function fn_iniConsOtroOrigenEditRecDocExtBus(){
    jQuery('#busResultado').val(""); 
    jQuery("#busCoOtros").val("");    
    
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var pdesDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    /*var ptipDocInden= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    var pnroDocInden= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();*/
                    var pcodDest= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(3)").html();
                    console.log("First");
                    fn_setOtroOrigenEditDocExtRecBus(pdesDest,pcodDest);
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var pdesDest= $(this).find("td:eq(0)").html();
        /*var ptipDocInden= $(this).find("td:eq(1)").html();
        var pnroDocInden= $(this).find("td:eq(2)").html();*/
        var pcodDest= $(this).find("td:eq(3)").html();        
        fn_setOtroOrigenEditDocExtRecBus(pdesDest,pcodDest);
    }); 
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var pdesDest= $(this).find("td:eq(0)").html();
            /*var ptipDocInden= $(this).find("td:eq(1)").html();
            var pnroDocInden= $(this).find("td:eq(2)").html();*/
            var pcodDest= $(this).find("td:eq(3)").html();
            console.log("Third");
            fn_setOtroOrigenEditDocExtRecBus(pdesDest,pcodDest);    
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setOtroOrigenEditDocExtRecBus(desDest, codDest){
    desDest = desDest.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtRecepBean';
    jQuery('#'+noForm).find('#busNomOtros').val(desDest);
    jQuery('#'+noForm).find('#busCoOtros').val(codDest);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
    return false;    
}

function onclickBuscarCiudadanoExtRec(){
    var noForm='buscarDocumentoExtRecepBean';
    var pDesCiudadano=allTrim(jQuery('#'+noForm).find('#busDescDni').val());
    var pnuDniAux=allTrim(jQuery('#'+noForm).find('#nuDniAux').val());
    
    if(!!pnuDniAux && pnuDniAux.length > 0){
        fn_getCiudadanoRemDocExtRecBus();
    }else{
        buscarCiudadanoEditDocExtRecepBus(pDesCiudadano);
    }
}

function buscarCiudadanoEditDocExtRecepBus(pDesCiudadano){
    
    var sDescCiudadano = allTrim(fu_getValorUpperCase(pDesCiudadano));
    //snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));
    sDescCiudadano=fn_getCleanTextExpReg(sDescCiudadano);
    sDescCiudadano=sDescCiudadano.trim();
    
    if(allTrim(sDescCiudadano).length >= 0 && allTrim(sDescCiudadano).length <= 1){  //El texto es entre 1 y 3 caracteres
            bootbox.alert("<h5>Debe ingresar como mínimo 2 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }         
    if (!!sDescCiudadano) {
        var rptValida = validaCaracteres(sDescCiudadano, "2");
        if (rptValida === "OK") {
            // Expresion regular
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(sDescCiudadano.match(re)){      
                var p = new Array();
                p[0] = "accion=goBuscaCiudadanoBus";
                p[1] = "sDescCiudadano=" + sDescCiudadano;
                ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                    fn_rptaBuscarCiudadanoEditDocExtRecep(data);
                },'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                  
            }                     
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>");            
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre del ciudadano.</h5>");                    
    }
    return false;
}

function onclickBuscarProveedorDocExtRecepBusRS(){
    var noForm='buscarDocumentoExtRecepBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusJson(pnuRucAux,noForm);
    }else if(!!pnuRucAux && pnuRucAux.length > 0){
        buscarProveedorEditDocExtRecepBusJson(pnuRucAux,noForm);
    }else{
        buscarProveedorEditDocExtRecepBus(prazonSocial);
    }
}



function buscarProveedorEditDocExtRecepBus(prazonSocial){
    
    var snoRazonSocial = allTrim(fu_getValorUpperCase(prazonSocial));
    //snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));
    snoRazonSocial=fn_getCleanTextExpReg(snoRazonSocial);
    snoRazonSocial=snoRazonSocial.trim();
    
    if(allTrim(snoRazonSocial).length >= 0 && allTrim(snoRazonSocial).length <= 1){  //El texto es entre 1 y 3 caracteres
            bootbox.alert("<h5>Debe ingresar como mínimo 2 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }         
    if (!!snoRazonSocial) {
        var rptValida = validaCaracteres(snoRazonSocial, "2");
        if (rptValida === "OK") {
            // Expresion regular
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(snoRazonSocial.match(re)){      
                var p = new Array();
                p[0] = "accion=goBuscaProveedorBus";
                p[1] = "prazonSocial=" + snoRazonSocial;
                ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
                    fn_rptaBuscarProveedorEditDocExtRecep(data);
                },'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                  
            }                     
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>");            
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre Persona Jurídica.</h5>");                    
    }
    return false;
}

function fn_iniConsCiudadanoEditRecDocExt(){
    jQuery('#busResultado').val(""); 
    jQuery('#busNumDni').val(""); 
    jQuery('#nuDniAux').val(""); 
    
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var pDesCiudadano= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var pnuDni= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    fn_setCiudadanoEditRecDocExtBus(pDesCiudadano,pnuDni);
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var pDesCiudadano= $(this).find("td:eq(0)").html();
        var pnuDni= $(this).find("td:eq(1)").html();
        fn_setCiudadanoEditRecDocExtBus(pDesCiudadano,pnuDni);
    });
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var pDesCiudadano= $(this).find("td:eq(0)").html();
            var pnuDni= $(this).find("td:eq(1)").html();
            fn_setCiudadanoEditRecDocExtBus(pDesCiudadano,pnuDni);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setCiudadanoEditRecDocExtBus(pDesCiudadano,pnuDni){
    pDesCiudadano = pDesCiudadano.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtRecepBean';
    jQuery('#'+noForm).find('#busDescDni').val(pDesCiudadano);
    jQuery('#'+noForm).find('#busNumDni').val(pnuDni);
    jQuery('#'+noForm).find('#nuDniAux').val(pnuDni);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    jQuery('#busResultado').val("1");
        
    return false;
}

function fn_iniConsProveedorEditRecDocExtBus(){
    jQuery('#busResultado').val(""); 
    jQuery('#busNumRuc').val(""); 
    jQuery('#nuRucAux').val(""); 
    
    var nomTbl='#tblDestinatario';
    var indexAux=-1;
    var tableaux = $(nomTbl);
    tableaux.find('tr').each(function(index, row) {
        if(index == 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $(nomTbl);
            var value = this.value;
            //alert(evento.which);
            var isFirst = false;
            var indexSelect = -1;
            table.find('tr').each(function(index, row) {
                    if ( $(this).hasClass('row_selected') ) {
                        $(this).removeClass('row_selected');
                    }
                    //var allCells = $(row).find('td');
                    var allCells = $(row).find('td:eq(0)');
                    if(allCells.length > 0) {
                            var found = false;
                            allCells.each(function(index, td) {
                                    var regExp = new RegExp(value, 'i');
                                    if(regExp.test($(td).text())) {
                                            found = true;
                                            return false;
                                    }
                            });
                            if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                            else {$(row).hide();}
                    }
            });
            if(evento.which == 13){
                if(isFirst){
                    var prazonSocial= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var pnuRuc= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    fn_setProveedorEditRecDocExtBus(prazonSocial,pnuRuc);
                }
            }else if(evento.which==38||evento.which==40){
                //$('#txtConsultaFind').blur();
                indexAux=-1;
                $(nomTbl+' >tbody >tr:visible').first().focus(); 
            }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $(nomTbl+" tbody tr").click(function(e) {
        var prazonSocial= $(this).find("td:eq(0)").html();
        var pnuRuc= $(this).find("td:eq(1)").html();
        fn_setProveedorEditRecDocExtBus(prazonSocial,pnuRuc);
    });
    $(nomTbl+' >tbody >tr:visible').keydown(function(evento){
        if(evento.which==38){//up
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).prev('tr').length == 0) {
                indexAux=$(nomTbl+" tbody tr:visible").length;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux--;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==40){//down
            if ($(nomTbl+" tbody tr:visible").eq(indexAux).next('tr').length == 0){
                indexAux=-1;
            }
            $(nomTbl+" tbody tr:visible").eq(indexAux).removeClass('row_selected');
            indexAux++;
            $(nomTbl+" tbody tr:visible").eq(indexAux).addClass('row_selected');                
            $(nomTbl+" tbody tr:visible").eq(indexAux).focus();
        }else if(evento.which==13){
            var prazonSocial= $(this).find("td:eq(0)").html();
            var pnuRuc= $(this).find("td:eq(1)").html();
            fn_setProveedorEditRecDocExtBus(prazonSocial,pnuRuc);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setProveedorEditRecDocExtBus(prazonSocial,pnuRuc){
    prazonSocial = prazonSocial.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtRecepBean';
    jQuery('#'+noForm).find('#busDescRuc').val(prazonSocial);
    jQuery('#'+noForm).find('#busNumRuc').val(pnuRuc);
    jQuery('#'+noForm).find('#nuRucAux').val(pnuRuc);
    removeDomId('windowConsultaProveedor');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
        
    return false;
}

function fn_changeTipoRemiDocExteRecepSeg_(cmbTipoRemite,esIni){
    var noForm='buscarDocExtRecSeguiEstado';
    jQuery('#'+noForm).find("#busResultado").val("");
    var pcoTipoRemi=$(cmbTipoRemite).val();
    
    if(!!pcoTipoRemi){
        if(esIni==="0"){
            jQuery('#'+noForm).find('#nuCorDoc').val("");
            jQuery('#envRemitenteEmiBean').val("1");
            jQuery('#envDocumentoEmiBean').val("1");
        }
        $("#divRemPersonaJuri_").hide();
        $("#divRemCiudadano_").hide();
        $("#divRemOtros_").hide();        
        if(pcoTipoRemi==='02'){//juridica
            $("#divRemPersonaJuri_").show();
             jQuery('#divRemCiudadano_').find('input').val("");
             jQuery('#divRemOtros_').find('input').val("");            
            jQuery('#'+noForm).find('#nuRucAux').focus();
        }else if(pcoTipoRemi==='03'){//ciudadano
            $("#divRemCiudadano_").show();
             jQuery('#divRemPersonaJuri_').find('input').val("");
             jQuery('#divRemOtros_').find('input').val("");            
             jQuery('#'+noForm).find('#nuDniAux').focus();
        }else if(pcoTipoRemi==='04'){//otros
            $("#divRemOtros_").show();
             jQuery('#divRemPersonaJuri_').find('input').val("");
             jQuery('#divRemCiudadano_').find('input').val("");            
             jQuery('#'+noForm).find('#divRemOtros_').focus();
        }
    }
}





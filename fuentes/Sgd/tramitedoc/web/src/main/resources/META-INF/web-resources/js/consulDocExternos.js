function fn_iniConsulDocExternos(){
  var noForm='#buscarDocumentoExtConsulBean';  
  jQuery(noForm).find('#esFiltroFecha').val("1");//hoy
  jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    

  pnumFilaSelect=0;
  changeTipoBusqRecepDocExtConsul("0",noForm);
  
  var noForm='buscarDocumentoExtConsulBean';
    
    jQuery('#'+noForm).find("#coTipoPersona").find('option:last').remove();  
    
    jQuery('#coTipoPersona').val("03");
    fn_changeTipoRemiDocExteRecep__(jQuery('#'+noForm).find("#coTipoPersona"),"1");    
    
    jQuery('#'+noForm).find("#coTipoPersona").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
    });
    
    jQuery('#'+noForm).find("#nuDniAux").change(function() {
        fn_getCiudadanoRemDocExtRecBusDE();
    });
    pnumFilaSelect=0;
    changeTipoBusqDocuExtRecep_DE("0");
}

function fn_getCiudadanoRemDocExtRecBusDE()
{
    var noForm='buscarDocumentoExtConsulBean';
    var pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
    var vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    if(vResult){
    var p = new Array();
        p[0] = "accion=goBuscaCiudadano";
        p[1] = "pnuDoc=" + pnuDniAux;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaGetCiudadanoRemDocExtRecBusDE(data,noForm);
        },'json', false, false, "POST");   
    }     
}

function fn_rptaGetCiudadanoRemDocExtRecBusDE(data,noForm){
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

function changeTipoBusqDocuExtRecep_DE(tipo) {
    jQuery('#buscarDocumentoExtRecepBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormBusDocExtRecepDE(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormBusDocExtRecepDE(tipo) {
    var validaFiltro = fu_validaFormBusDocExtRecep(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srMesaPartes.do?accion=goInicio", $('#buscarDocumentoExtRecepBean').serialize(), function(data) {
            refreshScript("divTablaDocExtRecep", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function changeTipoBusqRecepDocExtConsul2(tipo,noForm){
    jQuery(noForm).find('#tipoBusqueda').val(tipo);
    submitAjaxFormRecepDocExtConsul2(tipo,noForm);
    mostrarOcultarDivBusqFiltro2();    
}

function pre_changeTipoBusqRecepDocExtConsul2(tipo){
    var noForm='#buscarDocumentoExtConsulBean';
    changeTipoBusqRecepDocExtConsul2(tipo,noForm);
}

function submitAjaxFormRecepDocExtConsul2(tipo,noForm) {
    var validaFiltro = fu_validaFiltroRecepDocExtConsul2(tipo,noForm);
    if (validaFiltro === "1") {
        ajaxCall("/srConsultaRecDocExterno.do?accion=goInicio", $(noForm).serialize(), function(data) {
            refreshScript("divTblConsulDocumentoExterno", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_validaFiltroRecepDocExtConsul2(tipo,noForm) {
    var valRetorno = "1";
    jQuery(noForm).find('#feEmiIni').val(jQuery(noForm).find('#fechaFiltro').attr('fini'));
    jQuery(noForm).find('#feEmiFin').val(jQuery(noForm).find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery(noForm).find("esIncluyeFiltro1").is(':checked');
    var vFechaActual = jQuery('#txtFechaActual').val();
//    if(tipo==="0"){
//        valRetorno = fu_validaFechasFormRecepDocExtConsul(vFechaActual,noForm);     
//    }else if(tipo==="1"){
//      //verificar si se ingreso datos en los campos de busqueda de referencia
//      valRetorno = fu_validarBusquedaXReferenciaConsul(tipo);  
//      if(valRetorno==="1"){
//        valRetorno = fu_validaFormConfBusRecepDocExtConsul(noForm);  
//        if(valRetorno==="1"){
//           if(pEsIncluyeFiltro){ 
              valRetorno = fu_validaFechasFormRecepDocExtConsul2(vFechaActual,noForm);; 
//           }else{
//              valRetorno = setAnnioNoIncludeFiltroRecepDocExtConsul(noForm); 
//           }
//        }              
//      }
//    }    
    return valRetorno;
}

function setAnnioNoIncludeFiltroRecepDocExtConsul(noForm){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaDocExtRecepConsul(noForm);
    var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery(noForm).find('#coAnnio').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery(noForm).find("#feEmiIni").val();
        var vFeFinal = jQuery(noForm).find("#feEmiFin").val();

        if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery(noForm).find('#coAnnio').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fu_validaFormConfBusRecepDocExtConsul(noForm) {
    var valRetorno = "1";
    
    upperCaseFormConfBusRecepDocExtConsul(noForm);
    
    var vnuEmi = allTrim(jQuery(noForm).find('#busNumEmision').val());
    var vnuDoc = allTrim(jQuery(noForm).find('#busNumDoc').val());
    var vdeExp = allTrim(jQuery(noForm).find('#busNumExpediente').val());
    var vdeAsu = jQuery(noForm).find('#busAsunto').val();
    var vBusquedaTipoPersona = jQuery('#buscarDocumentoExtConsulBean').find('#busResultado').val();
    
    if(!!vdeAsu){
        //validar caracteres especiales
        vdeAsu = jQuery(noForm).find('#busAsunto').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vdeAsu))).val();
        if(allTrim(vdeAsu).length >= 0 && allTrim(vdeAsu).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }        
    }
    
    if(!(!!vnuEmi||!!vnuDoc||!!vdeExp||!!vdeAsu||!!vBusquedaTipoPersona)){
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    if(valRetorno==="1"){
        if (!!vnuEmi) {
            var vValidaNumero = fu_validaNumero(vnuEmi);
            if (vValidaNumero !== "OK") {
                alert_Warning("Buscar: ","N° de Emisión debe ser solo números.");
                valRetorno = "0";
            }
        }
    }
    return valRetorno;
}

function upperCaseFormConfBusRecepDocExtConsul(noForm){
    jQuery(noForm).find('#busNumEmision').val(fu_getValorUpperCase(jQuery(noForm).find('#busNumEmision').val()));
    jQuery(noForm).find('#busNumDoc').val(fu_getValorUpperCase(jQuery(noForm).find('#busNumDoc').val()));
    jQuery(noForm).find('#busNumExpediente').val(fu_getValorUpperCase(jQuery(noForm).find('#busNumExpediente').val()));
    jQuery(noForm).find('#busAsunto').val(fu_getValorUpperCase(jQuery(noForm).find('#busAsunto').val()));
}

function fu_obtenerEsFiltroFechaDocExtRecepConsul(nameForm){
    var opt = jQuery(nameForm).find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery(nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery(nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery(nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function fu_validaFechasFormRecepDocExtConsul2(vFechaActual,noForm){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaDocExtRecepConsul(noForm);
        var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery(noForm).find('#coAnnio').val();
                if(!!pAnnio){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }     
           
            var vFeInicio = jQuery(noForm).find("#feEmiIni").val();
            var vFeFinal = jQuery(noForm).find("#feEmiFin").val();
            
            if(valRetorno==="1"){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(!!pAnnioBusq){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery(noForm).find('#coAnnio').val(pAnnioBusq);                          
                    }                
                }               
            }           
            
           if(pEsFiltroFecha==="1"){
               //VALIDA FECHAS
               if (valRetorno==="1") {
                    if (!!vFeInicio){
                        valRetorno=fu_validaFechaConsulta(vFeInicio,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Del : "+ valRetorno);
                            valRetorno="0";
                        }                        
                    } else {
                       bootbox.alert('Debe ingresar Fecha Del');
                        valRetorno="0";
                    }
               }

                if (valRetorno==="1") {
                    //VALIDA FECHAS
                    if (!!vFeFinal){
                        if(pEsFiltroFecha==="3"){
                            vFechaActual = obtenerFechaUltimoDiaMes(vFechaActual);
                        }
                        valRetorno=fu_validaFechaConsulta(vFeFinal,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Al : "+ valRetorno);
                            valRetorno="0";
                        }
                    } else {
                       bootbox.alert('Debe ingresar Fecha Al');
                        valRetorno="0";                        
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

function fn_buscaDestinatarioDocExtConsul(){
    var noForm='#buscarDocumentoExtConsulBean';
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery(noForm).find('#coAnnio').val();
    ajaxCall("/srConsultaRecDocExterno.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },'text', false, false, "POST");
}

function fn_iniConsDestEmiDocExtConsul(){
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
                        fu_setDatoDestinatarioEmiDocExtConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioEmiDocExtConsul(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioEmiDocExtConsul(cod, desc){
    var noForm='#buscarDocumentoExtConsulBean';
    jQuery(noForm).find('#txtDestinatario').val(desc);
    jQuery(noForm).find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fu_iniTblDocsExternos(){
    var nomTabla='#tblDocExtRecepConsul';
    var indexFilaClick = -1;
    $(nomTabla+" tbody tr").click(function() {
        if (indexFilaClick !== -1) {
            if ($(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }else {
            $(this).addClass('row_selected');
            var pnuAnn=$(this).children('td')[0].innerHTML;
            var pnuEmi=$(this).children('td')[1].innerHTML;
            if(!!pnuAnn&&!!pnuEmi){
                jQuery('#txtpnuAnn').val(pnuAnn);
                jQuery('#txtpnuEmi').val(pnuEmi);
                pnumFilaSelect = $(this).index();
            }
            indexFilaClick = $(this).index();
        }
    });
    
    if(jQuery(nomTabla+' >tbody >tr').length > 0){
        try{
            if(jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).length === 1){
                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).trigger("click");
                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).focus();
            }else{
                pnumFilaSelect=0;
            }
        }catch(ex){
            pnumFilaSelect=0;
        }
    }    
  /*  $(nomTabla+" tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 4 || index === 6 || index === 8 || index === 9 || index === 12 || index === 13) {
                    showdivToolTip($('#divtitlemostrar').position(), $(this).html());
                }
            },
            function() {
                $('#divtitlemostrar').removeAttr('id');
                $('#divflotante').hide();
            }
    );*/
    function showdivToolTip(elemento, text){
        $('#divflotante').html(text);
        var x = elemento.left;
        var y = elemento.top + 24;
        $("#divflotante").css({left: x, top: y});

        document.getElementById('divflotante').style.display = 'block';

        return;
    }
}

function fn_editDocExtConsulRec(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        ajaxCall("/srConsultaRecDocExterno.do?accion=goDetDocumentoExt", p.join("&"), function(data) {
            refreshScript("divDocumExternoConsul", data);
            jQuery('#divRecDocumentoMp').hide();
            jQuery('#divDocumExternoConsul').show();
            jQuery('#divTblConsulDocumentoExterno').html("");
        }, 'text', false, false, "POST");        
    }
}

function regresarLsDocExtRecMp(){
    jQuery('#divRecDocumentoMp').toggle();                                
    jQuery('#divDocumExternoConsul').toggle();    
    var noForm='#buscarDocumentoExtConsulBean';
    submitAjaxFormRecepDocExtConsul2(jQuery(noForm).find('#tipoBusqueda').val(),noForm);
    //changeTipoBusqRecepDocExtConsul(jQuery(noForm).find('#tipoBusqueda').val(),noForm);
    jQuery('#divDocumExternoConsul').html(""); 
}

function fn_verDocumentoExtConsul(pnuAnn,pnuEmi,ptiOpe){
    if(!!pnuAnn&&!!pnuEmi){
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    }else{
        alert_Info("Mesa Partes :", "Seleccione fila.");        
    }
}

function fn_verDocumentoExtBeanConsul(){
    var noForm='#documentoExtConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var ptiOpe = "0";
    fn_verDocumentoExtConsul(pnuAnn,pnuEmi,ptiOpe);
}

function fn_verDocumentoExtLsConsul(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    var ptiOpe = "0";
    fn_verDocumentoExtConsul(pnuAnn,pnuEmi,ptiOpe);
}

function fn_verAnexoDocExtConsul(pnuAnn,pnuEmi,pnuDes){
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Mesa Partes :", "Seleccione fila.");        
    }    
}

function fn_verAnexoDocExtLsConsul(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    fn_verAnexoDocExtConsul(pnuAnn,pnuEmi,pnuDes);
}

function fn_verAnexoDocExtBeanConsul(){
    var noForm='#documentoExtConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var pnuDes = "N";
    fn_verAnexoDocExtConsul(pnuAnn,pnuEmi,pnuDes);
}

function fn_verSeguimientoDocExtConsul(pnuAnn,pnuEmi,pnuDes){
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Mesa Partes :", "Seleccione fila.");        
    }    
}

function fn_verSegumientoDocExtLsConsul(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    fn_verSeguimientoDocExtConsul(pnuAnn,pnuEmi,pnuDes);
}

function fn_verSegumientoDocExtBeanConsul(){
    var noForm='#documentoExtConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var pnuDes = "N";
    fn_verSeguimientoDocExtConsul(pnuAnn,pnuEmi,pnuDes);
}

function fu_cleanDocExtConsul(tipo){
    var noForm='#buscarDocumentoExtConsulBean';
    //if (tipo==="1") {
        jQuery(noForm).find('#busNumEmision').val("");
        jQuery(noForm).find('#busNumDoc').val("");
        jQuery(noForm).find('#busNumExpediente').val("");
        jQuery(noForm).find('#busAsunto').val("");
        jQuery(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        jQuery(noForm).find("#esIncluyeFiltro1").attr('checked',false);
    //} else if(tipo==="0"){
        jQuery(noForm).find("#esFiltroFecha").val("1");//hoy
        jQuery(noForm).find("#coAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});
        jQuery(noForm).find("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coTipoRemite option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#tipoDoc option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coProceso option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coDepDestino").val("");
        jQuery(noForm).find("#txtDestinatario").val(" [TODOS]");
        jQuery("#coExpediente option[value=]").prop("selected", "selected");
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

function fu_generarReporteDocExtConsulRecXLS(){
   fu_generarReporteDocExtConsul('XLS'); 
}

function fu_generarReporteDocExtConsulRecPDF(){
   fu_generarReporteDocExtConsul('PDF');  
}

function fu_generarReporteDocExtConsul(pformatoReporte){
    var noForm='#buscarDocumentoExtConsulBean';
    var validaFiltro = fu_validaFiltroRecepDocExtConsul("0",noForm);
    if (validaFiltro === "1") {
        // ajaxCall("/srConsultaRecDocExterno.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srConsultaRecDocExterno.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {    
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function(data){ 
                        fn_generaDocDesktopOnly(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK"){
                               bootbox.alert(result);
                            }
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

function fn_verDocRefDocExtConsul(cell) {
    var pnuAnn = ($(cell).parent()).parent().children().get(0).innerHTML;
    var pnuEmi = ($(cell).parent()).parent().children().get(1).innerHTML;
    if(!!pnuAnn&!!pnuEmi){
        fn_verDocumentosObj(pnuAnn, pnuEmi, "0");
    }
}

function fn_iniConsLocalRecDocExt(){
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
                        fu_setDatoLocalConsulRecDocExt(pcodDest,pdesDest);
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
            fu_setDatoLocalConsulRecDocExt(pcodDest,pdesDest);            
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
                fu_setDatoLocalConsulRecDocExt(pcodDest,pdesDest);           
            }
            //console.log(indexAux);
            evento.preventDefault();
        });        
}

function fu_setDatoLocalConsulRecDocExt(cod, desc) {
    jQuery('#buscarDocumentoExtConsulBean').find('#txtLocal').val(desc);
    jQuery('#buscarDocumentoExtConsulBean').find('#coLocEmi').val(cod);
    removeDomId('windowConsultaLocalDocExt');
    //jQuery("#coEmpDestino").val("");
    //jQuery("#txtEmpDestino").val("[TODOS]");    
}

function fn_buscaLocalDocExtConsul(){
    //var noForm='#buscarDocumentoExtConsulBean';
    var p = new Array();
    p[0] = "accion=goBuscaLocal";
    //p[1] = "pnuAnn=" + jQuery(noForm).find('#coAnnio').val();
    ajaxCall("/srConsultaRecDocExterno.do", p.join("&"), function(data) {
        fn_rptaBuscaLocalDocExtConsul(data);
    },'text', false, false, "POST");
}

function fn_rptaBuscaLocalDocExtConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_buscaDepenOrigenRegDocExtConsul() {
    var p = new Array();
    p[0] = "accion=goBuscaAllDependencia";
    ajaxCall("/srConsultaRecDocExterno.do", p.join("&"), function(data) {
        fn_rptaBuscaDepenOrigenRegDocExtConsul(data);
    },'text', false, false, "POST");
}

function fn_rptaBuscaDepenOrigenRegDocExtConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_iniFindAllDepBuscarDocExtMpConsul(){
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
                        fu_setDatoDepBuscarDocExtMpConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDepBuscarDocExtMpConsul(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDepBuscarDocExtMpConsul(cod, desc) {
    var noForm='#buscarDocumentoExtConsulBean';
    jQuery(noForm).find('#txtDeDepOriRec').val(desc);
    jQuery(noForm).find('#coDepOriRec').val(cod);
    removeDomId('windowConsultaRefOri');
}

/****************seguimiento**************/
function fn_iniSeguiDocExt(){
  var noForm='#buscarDocExtRecSeguiEstado';  
  jQuery(noForm).find('#esFiltroFecha').val("3");//año mes
  jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 3});    
  jQuery(noForm).find('#auxCoDepEmi').val(jQuery(noForm).find('#coDepEmi').val());
  jQuery(noForm).find('#auxDeDepEmi').val(jQuery(noForm).find('#deDepEmi').val());
  
  jQuery(noForm).find("#coTipoPersona").find('option:last').remove();  
    //jQuery(noForm).find("#coTipoPersona").find('option:last').remove();  
    
    jQuery('#coTipoPersona').val("03");
    fn_changeTipoRemiDocExteRecepSeg_(jQuery(noForm).find("#coTipoPersona"),"1");    

      jQuery(noForm).find("#coTipoPersona").change(function() {
       jQuery('#envRemitenteEmiBean').val("1");  
    });
    
    jQuery(noForm).find("#nuDniAux").change(function() {
        fn_getCiudadanoRemDocExtRecBusSeg();
    });
    
  pnumFilaSelect=0;
 // changeTipoBusqRecepDocExtSegui("0",noForm);
  changeTipoBusqRecepDocExtSegui3("0",noForm);
}

function onclickBuscarCiudadanoExtRecSeg(){
    var noForm='buscarDocExtRecSeguiEstado';
    var pDesCiudadano=allTrim(jQuery('#'+noForm).find('#busDescDni').val());
    var pnuDniAux=allTrim(jQuery('#'+noForm).find('#nuDniAux').val());
    
    if(!!pnuDniAux && pnuDniAux.length > 0){
        fn_getCiudadanoRemDocExtRecBusSeg();
    }else{
        buscarCiudadanoEditDocExtRecepBusSeg(pDesCiudadano);
    }
}

function fn_getCiudadanoRemDocExtRecBusSeg()
{
    var noForm='buscarDocExtRecSeguiEstado';
    var pnuDniAux=allTrim(jQuery('#'+noForm).find("#nuDniAux").val());
    var vResult=fn_validarNuDniRemiDocExtRec(noForm,pnuDniAux);
    if(vResult){
    var p = new Array();
        p[0] = "accion=goBuscaCiudadano";
        p[1] = "pnuDoc=" + pnuDniAux;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaGetCiudadanoRemDocExtRecBusSeg(data,noForm);
        },'json', false, false, "POST");   
    }     
}

function fn_iniConsCiudadanoEditRecDocExtSeg(){
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
                    fn_setCiudadanoEditRecDocExtBusSeg(pDesCiudadano,pnuDni);
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
        fn_setCiudadanoEditRecDocExtBusSeg(pDesCiudadano,pnuDni);
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
            fn_setCiudadanoEditRecDocExtBusSeg(pDesCiudadano,pnuDni);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setCiudadanoEditRecDocExtBusSeg(pDesCiudadano,pnuDni){
    pDesCiudadano = pDesCiudadano.replace(/&amp;/g, "&");
    var noForm='buscarDocExtRecSeguiEstado';
    jQuery('#'+noForm).find('#busDescDni').val(pDesCiudadano);
    jQuery('#'+noForm).find('#busNumDni').val(pnuDni);
    jQuery('#'+noForm).find('#nuDniAux').val(pnuDni);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    jQuery('#busResultado').val("1");
        
    return false;
}

function buscarCiudadanoEditDocExtRecepBusSeg(pDesCiudadano){
    
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
                p[0] = "accion=goBuscaCiudadanoBusSeg";
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
        bootbox.alert("<h5>Debe ingresar datos en Nombre Persona Jurídica.</h5>");                    
    }
    return false;
}

function fn_rptaGetCiudadanoRemDocExtRecBusSeg(data,noForm){
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

function changeTipoBusqRecepDocExtSegui3(tipo,noForm){
    jQuery(noForm).find('#tipoBusqueda').val(tipo);
    submitAjaxFormRecepDocExtSegui3(tipo,noForm);
    mostrarOcultarDivBusqFiltro2();   
}

function pre_changeTipoBusqRecepDocExtSegui3(tipo){
    var noForm='#buscarDocExtRecSeguiEstado';
    changeTipoBusqRecepDocExtSegui3(tipo,noForm);
}

function submitAjaxFormRecepDocExtSegui3(tipo,noForm) {
    var validaFiltro = fu_validaFiltroRecepDocExtSegui22(tipo,noForm);
    if (validaFiltro === "1") {
        ajaxCall("/srSeguiEstRecDocExt.do?accion=goInicio", $(noForm).serialize(), function(data) {
            refreshScript("divTblConsulDocumentoExterno", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_validaFiltroRecepDocExtSegui22(tipo,noForm) {
    var valRetorno = "1";
    jQuery(noForm).find('#feEmiIni').val(jQuery(noForm).find('#fechaFiltro').attr('fini'));
    jQuery(noForm).find('#feEmiFin').val(jQuery(noForm).find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery(noForm).find("esIncluyeFiltro1").is(':checked');
    var vFechaActual = jQuery('#txtFechaActual').val();
//    if(tipo==="0"){
//        valRetorno = fu_validaFechasFormRecepDocExtSegui(vFechaActual,noForm);     
//    }else if(tipo==="1"){
//      //verificar si se ingreso datos en los campos de busqueda de referencia
//      valRetorno = fu_validarBusquedaXReferenciaConsul(tipo);  
//      if(valRetorno==="1"){
//        valRetorno = fu_validaFormConfBusRecepDocExtSegui(noForm);  
//        if(valRetorno==="1"){
//           if(pEsIncluyeFiltro){ 
              valRetorno = fu_validaFechasFormRecepDocExtSegui22(vFechaActual,noForm);; 
//           }else{
//              valRetorno = setAnnioNoIncludeFiltroRecepDocExtSegui(noForm); 
//           }
//        }              
//      }
//    }    
    return valRetorno;
}

function fu_validaFechasFormRecepDocExtSegui22(vFechaActual,noForm){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaDocExtRecepConsul(noForm);
        var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery(noForm).find('#nuAnn').val();
                if(!!pAnnio){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }     
           
            var vFeInicio = jQuery(noForm).find("#feEmiIni").val();
            var vFeFinal = jQuery(noForm).find("#feEmiFin").val();
            
            if(valRetorno==="1"){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(!!pAnnioBusq){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery(noForm).find('#nuAnn').val(pAnnioBusq);                          
                    }                
                }               
            }           
            
           if(pEsFiltroFecha==="1"){
               //VALIDA FECHAS
               if (valRetorno==="1") {
                    if (!!vFeInicio){
                        valRetorno=fu_validaFechaConsulta(vFeInicio,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Del : "+ valRetorno);
                            valRetorno="0";
                        }                        
                    } else {
                       bootbox.alert('Debe ingresar Fecha Del');
                        valRetorno="0";
                    }
               }

                if (valRetorno==="1") {
                    //VALIDA FECHAS
                    if (!!vFeFinal){
                        if(pEsFiltroFecha==="3"){
                            vFechaActual = obtenerFechaUltimoDiaMes(vFechaActual);
                        }
                        valRetorno=fu_validaFechaConsulta(vFeFinal,vFechaActual);
                        if (valRetorno!=="1") {
                           bootbox.alert("Error en Fecha Al : "+ valRetorno);
                            valRetorno="0";
                        }
                    } else {
                       bootbox.alert('Debe ingresar Fecha Al');
                        valRetorno="0";                        
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

function upperCaseFormConfBusRecepDocExtSegui(noForm){
    jQuery(noForm).find('#nuCorEmi').val(fu_getValorUpperCase(jQuery(noForm).find('#nuCorEmi').val()));
    jQuery(noForm).find('#nuDoc').val(fu_getValorUpperCase(jQuery(noForm).find('#nuDoc').val()));
    jQuery(noForm).find('#nuExpediente').val(fu_getValorUpperCase(jQuery(noForm).find('#nuExpediente').val()));
    jQuery(noForm).find('#deAsu').val(fu_getValorUpperCase(jQuery(noForm).find('#deAsu').val()));
}

function fu_validaFormConfBusRecepDocExtSegui(noForm) {
    var valRetorno = "1";
    
    upperCaseFormConfBusRecepDocExtSegui(noForm);
    
    var vnuEmi = allTrim(jQuery(noForm).find('#nuCorEmi').val());
    var vnuDoc = allTrim(jQuery(noForm).find('#nuDoc').val());
    var vdeExp = allTrim(jQuery(noForm).find('#nuExpediente').val());
    var vdeAsu = jQuery(noForm).find('#deAsu').val();
    var vBusquedaTipoPersona = jQuery(noForm).find('#busResultado').val();
    
    if(!!vdeAsu){
        //validar caracteres especiales
        vdeAsu = jQuery(noForm).find('#deAsu').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vdeAsu))).val();
        if(allTrim(vdeAsu).length >= 0 && allTrim(vdeAsu).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }        
    }
    
    if(!(!!vnuEmi||!!vnuDoc||!!vdeExp||!!vdeAsu||!!vBusquedaTipoPersona)){
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    if(valRetorno==="1"){
        if (!!vnuEmi) {
            var vValidaNumero = fu_validaNumero(vnuEmi);
            if (vValidaNumero !== "OK") {
                alert_Warning("Buscar: ","N° de Emisión debe ser solo números.");
                valRetorno = "0";
            }
        }
    }
    return valRetorno;
}

function setAnnioNoIncludeFiltroRecepDocExtSegui(noForm){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaDocExtRecepConsul(noForm);
    var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery(noForm).find('#nuAnn').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery(noForm).find("#feEmiIni").val();
        var vFeFinal = jQuery(noForm).find("#feEmiFin").val();

        if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery(noForm).find('#nuAnn').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fu_iniTblSeguiDocsExternos(objJson){
    if(!!objJson){
        var noForm='#buscarDocExtRecSeguiEstado';
        objJson=new Function('return '+objJson)();
        jQuery('#txtFechaActual').val(!!objJson.fechaActual?objJson.fechaActual:"");
        jQuery('#txtAnnioActual').val(!!objJson.annioActual?objJson.annioActual:"");
        jQuery(noForm).find('#auxCoDepEmi').val(!!objJson.coDepEmi?objJson.coDepEmi:"");
        jQuery(noForm).find('#auxDeDepEmi').val(!!objJson.deDepEmi?objJson.deDepEmi:"");
    }
    var nomTabla='#tblDocExtRecepSegui';
    var indexFilaClick = -1;
    $(nomTabla+" tbody tr").click(function() {
        if (indexFilaClick !== -1) {
            if ($(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $(nomTabla+" tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }else {
            $(this).addClass('row_selected');
            var pnuAnn=$(this).children('td')[0].innerHTML;
            var pnuEmi=$(this).children('td')[1].innerHTML;
            var pnuDes=$(this).children('td')[2].innerHTML;
            if(!!pnuAnn&&!!pnuEmi&&!!pnuDes){
                jQuery('#txtpnuAnn').val(pnuAnn);
                jQuery('#txtpnuEmi').val(pnuEmi);
                jQuery('#txtpnuDes').val(pnuDes);
                pnumFilaSelect = $(this).index();
            }
            indexFilaClick = $(this).index();
        }
    });
    
    if(jQuery(nomTabla+' >tbody >tr').length > 0){
        try{
            if(jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).length === 1){
                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).trigger("click");
                jQuery(nomTabla+' >tbody >tr').eq(pnumFilaSelect).focus();
            }else{
                pnumFilaSelect=0;
            }
        }catch(ex){
            pnumFilaSelect=0;
        }
    }    
    /*$(nomTabla+" tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 6 || index === 11 || index === 12) {
                    showdivToolTip($('#divtitlemostrar').position(), $(this).html());
                }
            },
            function() {
                $('#divtitlemostrar').removeAttr('id');
                $('#divflotante').hide();
            }
    );*/
    function showdivToolTip(elemento, text){
        $('#divflotante').html(text);
        var x = elemento.left;
        var y = elemento.top + 24;
        $("#divflotante").css({left: x, top: y});

        document.getElementById('divflotante').style.display = 'block';

        return;
    }
    jQuery(nomTabla+' >tbody >tr').keydown(function(evento){
       if(evento.which===38){//up
            if (jQuery(nomTabla+" >tbody >tr").eq(pnumFilaSelect).prev('tr').length===0){
                pnumFilaSelect=$(nomTabla+" >tbody >tr").length;
            }
            pnumFilaSelect--;
            jQuery(nomTabla+" >tbody >tr").eq(pnumFilaSelect).trigger("click");
            jQuery(nomTabla+" >tbody >tr").eq(pnumFilaSelect).focus();
       }else if(evento.which===40){//down
            if ($(nomTabla+" >tbody >tr").eq(pnumFilaSelect).next('tr').length===0){
                pnumFilaSelect=-1;
            }           
            pnumFilaSelect++;
            jQuery(nomTabla+" >tbody >tr").eq(pnumFilaSelect).trigger("click");               
            jQuery(nomTabla+" >tbody >tr").eq(pnumFilaSelect).focus();           
       }
       evento.preventDefault();
    });    
}

function fu_cleanDocExtSegui(tipo){
    var noForm='#buscarDocExtRecSeguiEstado';
//    if (tipo==="1") {
        jQuery(noForm).find('#nuCorEmi').val("");
        jQuery(noForm).find('#nuDoc').val("");
        jQuery(noForm).find('#nuExpediente').val("");
        jQuery(noForm).find('#deAsu').val("");
        jQuery(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        jQuery(noForm).find("#esIncluyeFiltro1").attr('checked',false);
//    } else if(tipo==="0"){
        jQuery(noForm).find("#esFiltroFecha").val("3");//año mes
        jQuery(noForm).find("#nuAnn").val(jQuery("#txtAnnioActual").val());
        jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 3});
        jQuery(noForm).find("#coEsDocEmi option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#tiEmi option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coEstVen option[value=3]").prop("selected", "selected");
        jQuery(noForm).find("#coTipDocAdm option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coProceso option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coDepEmi").val(jQuery(noForm).find("#auxCoDepEmi").val());
        jQuery(noForm).find("#deDepEmi").val(jQuery(noForm).find("#auxDeDepEmi").val());
        jQuery(noForm).find("#coDepDes").val("");
        jQuery(noForm).find("#txtDepDestino").val(" [TODOS]");
        jQuery(noForm).find("#coEmpRes").val("");
        jQuery(noForm).find("#txtElaboradoPor").val(" [TODOS]");
        
        jQuery('#divRemPersonaJuri_').find('input').val("");
        jQuery('#divRemCiudadano_').find('input').val("");
        jQuery('#divRemOtros_').find('input').val("");  
        
        jQuery(noForm).find("#coTipoExp").val("");
        jQuery(noForm).find("#coOriDoc").val("");
        jQuery(noForm).find("#busResultado").val("");
//    }
}

function fn_buscaDependenciaRemiteDocExtSegui(){
    var noForm='#buscarDocExtRecSeguiEstado';
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaRemite";
    p[1] = "pcoDepen=" + jQuery(noForm).find('#auxCoDepEmi').val();
    ajaxCall("/srSeguiEstRecDocExt.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenConsul(data);
    },
            'text', false, false, "POST");    
}

function fn_iniConsDepenRemiDocExtSegui(){
	var tableaux = $('#tlbReferenOrig');
        tableaux.find('tr').each(function(index, row) {
            if(index == 0){
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
                                if (found == true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                                else {$(row).hide();}
                        }
                });
                if(evento.which == 13){
                    if(isFirst){
                        var pdesDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        var pcodDest= $("#tlbReferenOrig tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                        fu_setDatoDepenRemiDocExtSegui(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDepenRemiDocExtSegui(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDepenRemiDocExtSegui(cod, desc) {
    var noForm='#buscarDocExtRecSeguiEstado';
    jQuery(noForm).find('#deDepEmi').val(desc);
    jQuery(noForm).find('#coDepEmi').val(cod);
    removeDomId('windowConsultaRefOri');
    jQuery(noForm).find('#coEmpRes').val("");
    jQuery(noForm).find('#txtElaboradoPor').val(" [TODOS]"); 
}

function fn_buscaElaboradoPorSeguiDocExtRec() {
    var noForm='#buscarDocExtRecSeguiEstado';
    var coDepOrigen = jQuery(noForm).find('#coDepEmi').val();
    ajaxCall("/srSeguiEstRecDocExt.do?accion=goBuscaElaboradoPor&pcoDep=" + coDepOrigen, '', function(data) {
        fn_rptaBuscaElaboradoPorConsul(data);
    },
            'text', false, false, "POST");
}

function fn_iniSeguiElaboradoPorDocExt(){
        var tableaux = $('#tblElaboradoPor');
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tblElaboradoPor');
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
                                if (found === true) {$(row).show(); if(!isFirst){$(this).addClass('row_selected'); isFirst = true; indexSelect = index;}}
                                else {$(row).hide();}
                        }
                });
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoElaboradoPorSeguiDocExtRec(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tblElaboradoPor tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoElaboradoPorSeguiDocExtRec(pcodDest,pdesDest);            
        });    
}

function fu_setDatoElaboradoPorSeguiDocExtRec(cod, desc) {
    var noForm='#buscarDocExtRecSeguiEstado';
    jQuery(noForm).find('#txtElaboradoPor').val(desc);
    jQuery(noForm).find('#coEmpRes').val(cod);
    removeDomId('windowConsultaElaboradoPor');
}

function fn_buscaDestinatarioDocExtSegui(){
    var noForm='#buscarDocExtRecSeguiEstado';
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery(noForm).find('#nuAnn').val();
    ajaxCall("/srSeguiEstRecDocExt.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },'text', false, false, "POST");
}

function fn_iniSeguiDestEmiDocExt(){
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
                        fu_setDatoDestinatarioEmiDocExtSegui(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioEmiDocExtSegui(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioEmiDocExtSegui(cod, desc){
    var noForm='#buscarDocExtRecSeguiEstado';
    jQuery(noForm).find('#txtDepDestino').val(desc);
    jQuery(noForm).find('#coDepDes').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fn_detDocExtSeguiRec(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val(); 
    var pnuDes=jQuery('#txtpnuDes').val(); 
    if(!!pnuAnn&&!!pnuEmi&&!!pnuDes){
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        p[2] = "pnuDes=" + pnuDes;
        ajaxCall("/srSeguiEstRecDocExt.do?accion=goDetDocumentoExt", p.join("&"), function(data) {
            refreshScript("divDocumExternoConsul", data);
            jQuery('#divRecDocumentoMp').hide();
            jQuery('#divDocumExternoConsul').show();
            jQuery('#divTblConsulDocumentoExterno').html("");
        }, 'text', false, false, "POST");        
    }    
}

function regresarLsSeguiDocExtRec(){
    jQuery('#divRecDocumentoMp').toggle();                                
    jQuery('#divDocumExternoConsul').toggle();    
    var noForm='#buscarDocExtRecSeguiEstado';
    submitAjaxFormRecepDocExtSegui3(jQuery(noForm).find('#tipoBusqueda').val(),noForm);
    //changeTipoBusqRecepDocExtSegui(jQuery(noForm).find('#tipoBusqueda').val(),noForm);
    jQuery('#divDocumExternoConsul').html(""); 
}

function fn_verDocumentoExtBeanSegui(){
    var noForm='#docExtRecSeguiEstBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var ptiOpe = "0";
    fn_verDocumentoExtConsul(pnuAnn,pnuEmi,ptiOpe);
}

function fn_verAnexoDocExtBeanSegui(){
    var noForm='#docExtRecSeguiEstBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var pnuDes=jQuery(noForm).find('#nuDes').val();
    fn_verAnexoDocExtSegui(pnuAnn,pnuEmi,pnuDes);
}

function fn_verSegumientoDocExtBeanSegui(){
    var noForm='#docExtRecSeguiEstBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();    
    var pnuDes=jQuery(noForm).find('#nuDes').val();
    fn_verSeguimientoDocExtSegui(pnuAnn,pnuEmi,pnuDes);
}

function fn_verDocumentoExtLsSegui(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    var ptiOpe = "0";
    fn_verDocumentoExtConsul(pnuAnn,pnuEmi,ptiOpe);
}

function fn_verAnexoDocExtLsSegui(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = jQuery('#txtpnuDes').val();
    fn_verAnexoDocExtConsul(pnuAnn,pnuEmi,pnuDes);
}

function fn_verAnexoDocExtSegui(pnuAnn,pnuEmi,pnuDes){
    if (!!pnuAnn&&!!pnuEmi&&!!pnuDes) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Mesa Partes :", "Seleccione fila.");        
    }    
}

function fn_verSegumientoDocExtLsSegui(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = jQuery('#txtpnuDes').val();
    fn_verSeguimientoDocExtSegui(pnuAnn,pnuEmi,pnuDes);
}

function fn_verSeguimientoDocExtSegui(pnuAnn,pnuEmi,pnuDes){
    if (!!pnuAnn&&!!pnuEmi&&!!pnuDes) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Mesa Partes :", "Seleccione fila.");        
    }    
}

function fu_generarReporteDocExtSeguiRecXLS(){
   fu_generarReporteDocExtSegui('XLS'); 
}

function fu_generarReporteDocExtSeguiRecPDF(){
   fu_generarReporteDocExtSegui('PDF');  
}

//YUAL

function fu_generarReporteResumenPDF(pformatoReporte){
     loadding(true);
    var noForm='#buscarDocumentoExtConsulBean';
    var validaFiltro = fu_validaFiltroRecepDocExtConsul("0",noForm);
    if (validaFiltro === "1") {
        // ajaxCall("/srConsultaRecDocExterno.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srSeguiEstRecDocExt.do?accion=goExportarResumen&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {    
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                        fn_generaDocDesktopOnly(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK"){
                               bootbox.alert(result);
                            }
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
/*
function fu_generarReporteResumenPDF(){
//    console.log('ddddddddddddddddd');
   loadding(true);
    var noForm='#buscarDocExtRecSeguiEstado';
    var validaFiltro = fu_validaFiltroRecepDocExtSegui("0",noForm);
    if (validaFiltro === "1") {
        var pformatoReporte='PDF'
        // ajaxCall("/srSeguiEstRecDocExt.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srSeguiEstRecDocExt.do?accion=goExportarResumen&pformatRepor", $(noForm).serialize(), function(data) {
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        $('#imloadingReport').hide();
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                        fn_generaDocDesktop(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK"){
                               bootbox.alert(result);
                            }
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
    
}*/


function fu_generarReporteDocExtSegui(pformatoReporte){
   //document.getElementById('loadding').style.display = 'block';
   
    var noForm='#buscarDocExtRecSeguiEstado';
    var validaFiltro = fu_validaFiltroRecepDocExtSegui("0",noForm);
    if (validaFiltro === "1") {
        // ajaxCall("/srSeguiEstRecDocExt.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srSeguiEstRecDocExt.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                        fn_generaDocDesktopOnly(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            loadding(false);
                            if (result!=="OK"){
                               bootbox.alert(result);
                            }
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

function fn_iniConsProveedorEditRecDocExtBusDE(){
    jQuery('#busResultado').val("0"); 
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
                    fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);
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
        fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);
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
            fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_iniConsProveedorEditRecDocExtBusDE(){
    jQuery('#busResultado').val("0"); 
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
                    fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);
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
        fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);
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
            fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setProveedorEditRecDocExtBusDE(prazonSocial,pnuRuc){
    prazonSocial = prazonSocial.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtConsulBean';
    jQuery('#'+noForm).find('#busDescRuc').val(prazonSocial);
    jQuery('#'+noForm).find('#busNumRuc').val(pnuRuc);
    jQuery('#'+noForm).find('#nuRucAux').val(pnuRuc);
    removeDomId('windowConsultaProveedor');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
        
    return false;
}

function fn_iniConsProveedorEditRecDocExtBusSeg(){
    jQuery('#busResultado').val("0"); 
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
                    fn_setProveedorEditRecDocExtBusSeg(prazonSocial,pnuRuc);
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
        fn_setProveedorEditRecDocExtBusSeg(prazonSocial,pnuRuc);
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
            fn_setProveedorEditRecDocExtBusSeg(prazonSocial,pnuRuc);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setProveedorEditRecDocExtBusSeg(prazonSocial,pnuRuc){
    prazonSocial = prazonSocial.replace(/&amp;/g, "&");
    var noForm='buscarDocExtRecSeguiEstado';
    jQuery('#'+noForm).find('#busDescRuc').val(prazonSocial);
    jQuery('#'+noForm).find('#busNumRuc').val(pnuRuc);
    jQuery('#'+noForm).find('#nuRucAux').val(pnuRuc);
    removeDomId('windowConsultaProveedor');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
        
    return false;
}

function fn_iniConsOtroOrigenEditRecDocExtBusDE(){
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
                    fn_setOtroOrigenEditDocExtRecBusDE(pdesDest,pcodDest);
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
        fn_setOtroOrigenEditDocExtRecBusDE(pdesDest,pcodDest);
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
            fn_setOtroOrigenEditDocExtRecBusDE(pdesDest,pcodDest);    
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setOtroOrigenEditDocExtRecBusDE(desDest, codDest){
    desDest = desDest.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtConsulBean';
    jQuery('#'+noForm).find('#busNomOtros').val(desDest);
    jQuery('#'+noForm).find('#busCoOtros').val(codDest);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
    return false;    
}

function fn_iniConsOtroOrigenEditRecDocExtBusSeg(){
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
                    fn_setOtroOrigenEditDocExtRecBusSeg(pdesDest,pcodDest);
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
        fn_setOtroOrigenEditDocExtRecBusSeg(pdesDest,pcodDest);
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
            fn_setOtroOrigenEditDocExtRecBusSeg(pdesDest,pcodDest);    
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setOtroOrigenEditDocExtRecBusSeg(desDest, codDest){
    desDest = desDest.replace(/&amp;/g, "&");
    var noForm='buscarDocExtRecSeguiEstado';
    jQuery('#'+noForm).find('#busNomOtros').val(desDest);
    jQuery('#'+noForm).find('#busCoOtros').val(codDest);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    
    jQuery('#busResultado').val("1");
    return false;    
}

function fn_changeTipoRemiDocExteRecep__(cmbTipoRemite,esIni){
    var noForm='buscarDocumentoExtConsulBean';
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

function onclickBuscarProveedorDocExtRecepBusRSDE(){
    var noForm='buscarDocumentoExtConsulBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusDEJson(pnuRucAux,noForm);
    }else if(!!pnuRucAux && pnuRucAux.length > 0){
        buscarProveedorEditDocExtRecepBusDEJson(pnuRucAux,noForm);
    }else{
        buscarProveedorEditDocExtRecepBusDE(prazonSocial);
    }
}



function buscarProveedorEditDocExtRecepBusDEJson(pnuRuc,noForm){
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
                    //jQuery('#envRemitenteEmiBean').val("1");
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
        }, 'json', false, true, "POST");        
    }
}

function buscarProveedorEditDocExtRecepBusDE(prazonSocial){
    
    var snoRazonSocial = allTrim(fu_getValorUpperCase(prazonSocial));
    //snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));
    snoRazonSocial=fn_getCleanTextExpReg(snoRazonSocial);
    snoRazonSocial=snoRazonSocial.trim();
    
    if(allTrim(snoRazonSocial).length >= 0 && allTrim(snoRazonSocial).length <2){  //El texto es entre 1 y 3 caracteres
            bootbox.alert("<h5>Debe ingresar como nínimo 2 caracteres.</h5>", function() {
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
                p[0] = "accion=goBuscaProveedorBusDE";
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

function fn_getOtroOrigenRemDocExtRecBusDE()
{    
    var noForm='buscarDocumentoExtConsulBean';
    var pdesOtroOri=allTrim(jQuery('#'+noForm).find("#busNomOtros").val()).toUpperCase();
    var vResult=validaCaracteres(pdesOtroOri, "2");
    if(pdesOtroOri.length >= 0 && pdesOtroOri.length <= 1){  //El texto es entre 0 y 1 caracteres
        bootbox.alert("<h5>Debe ingresar como nínimo 2 caracteres.</h5>", function() {
            bootbox.hideAll();        
        });
        vResult="NO_OK";
    }
    if(vResult == "OK"){
    var p = new Array();
        p[0] = "accion=goBuscaOtroOrigenDE";
        p[1] = "pdesOtroOri=" + pdesOtroOri;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaBuscarRemitenteOtroDocExtRec(data);
        },'text', false, false, "POST"); 
    }
    return false;
}

function onclickBuscarProveedorDocExtRecepBusDE(){
    var noForm='buscarDocumentoExtConsulBean';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusDEJson(pnuRucAux,noForm);
    }
    /*else{
        buscarProveedorEditDocExtRecepBus(prazonSocial);
    }*/
}

function onclickBuscarProveedorDocExtRecepBusSeg(){
    var noForm='buscarDocExtRecSeguiEstado';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusSegJson(pnuRucAux,noForm);
    }
    /*else{
        buscarProveedorEditDocExtRecepBus(prazonSocial);
    }*/
}

function onclickBuscarProveedorDocExtRecepBusRSSeg(){
    var noForm='buscarDocExtRecSeguiEstado';
    var prazonSocial=allTrim(jQuery('#'+noForm).find('#busDescRuc').val());
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#nuRucAux').val());
    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusSegJson(pnuRucAux,noForm);
    }else if(!!pnuRucAux && pnuRucAux.length > 0){
        buscarProveedorEditDocExtRecepBusSegJson(pnuRucAux,noForm);
    }else{
        buscarProveedorEditDocExtRecepBusDESeg(prazonSocial);
    }
}

function buscarProveedorEditDocExtRecepBusSegJson(pnuRuc,noForm){
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
                    //jQuery('#envRemitenteEmiBean').val("1");
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
        }, 'json', false, true, "POST");        
    }
}

function buscarProveedorEditDocExtRecepBusDESeg(prazonSocial){
    
    var snoRazonSocial = allTrim(fu_getValorUpperCase(prazonSocial));
    //snoRazonSocial=fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial));
    snoRazonSocial=fn_getCleanTextExpReg(snoRazonSocial);
    snoRazonSocial=snoRazonSocial.trim();
    
    if(allTrim(snoRazonSocial).length >= 0 && allTrim(snoRazonSocial).length <2){  
            bootbox.alert("<h5>Debe ingresar comoo mínimo 2 caracteres.</h5>", function() {
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
                p[0] = "accion=goBuscaProveedorBusSeg";
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

function fn_getOtroOrigenRemDocExtRecBusSeg()
{    
    var noForm='buscarDocExtRecSeguiEstado';
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
        p[0] = "accion=goBuscaOtroOrigenSeg";
        p[1] = "pdesOtroOri=" + pdesOtroOri;
        ajaxCall("/srMesaPartes.do", p.join("&"), function(data) {
            fn_rptaBuscarRemitenteOtroDocExtRec(data);
        },'text', false, false, "POST"); 
    }
    return false;
}

function onclickBuscarCiudadanoExtRecConsulta(){
    var noForm='buscarDocumentoExtConsulBean';
    var pDesCiudadano=allTrim(jQuery('#'+noForm).find('#busDescDni').val());
    var pnuDniAux=allTrim(jQuery('#'+noForm).find('#nuDniAux').val());
    
    if(!!pnuDniAux && pnuDniAux.length > 0){
        fn_getCiudadanoRemDocExtRecBusDE();
    }else{
        buscarCiudadanoEditDocExtRecepBusDE(pDesCiudadano);
    }
}

function buscarCiudadanoEditDocExtRecepBusDE(pDesCiudadano){
    
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
                p[0] = "accion=goBuscaCiudadanoBusDE";
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
        bootbox.alert("<h5>Debe ingresar datos en Nombre Persona Jurídica.</h5>");                    
    }
    return false;
}

function fn_iniConsCiudadanoEditRecDocExtDE(){
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
                    fn_setCiudadanoEditRecDocExtBusDE(pDesCiudadano,pnuDni);
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
        fn_setCiudadanoEditRecDocExtBusDE(pDesCiudadano,pnuDni);
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
            fn_setCiudadanoEditRecDocExtBusDE(pDesCiudadano,pnuDni);      
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_setCiudadanoEditRecDocExtBusDE(pDesCiudadano,pnuDni){
    pDesCiudadano = pDesCiudadano.replace(/&amp;/g, "&");
    var noForm='buscarDocumentoExtConsulBean';
    jQuery('#'+noForm).find('#busDescDni').val(pDesCiudadano);
    jQuery('#'+noForm).find('#busNumDni').val(pnuDni);
    jQuery('#'+noForm).find('#nuDniAux').val(pnuDni);
    removeDomId('windowConsultaOtroOri');
    jQuery('#'+noForm).find('#coTipoPersona').focus();
    jQuery('#envRemitenteEmiBean').val("1");
    jQuery('#busResultado').val("1");
        
    return false;
}
/****************seguimiento**************/
function fn_buscaReferenciaOrigenInter() {
    var p = new Array();
    p[0] = "accion=goBuscaReferenciaOrigen";
    p[1] = "pnuAnn=" + jQuery('#sCoAnnio').val();
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenInter(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaReferenciaOrigenInter(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoReferenOrigenInter(cod, desc) {
    jQuery('#txtRefOrigen').val(desc);
    jQuery('#sRefOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
}

function fn_buscaDestinatarioEmiInter() {
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery('#sCoAnnio').val();
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiInter(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaDestinatarioEmiInter(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoDestinatarioEmiInter(cod, desc) {
    jQuery('#txtDestinatario').val(desc);
    jQuery('#sDestinatario').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fn_buscaElaboradoPorInter() {
    var codDependencia = jQuery('#sCoDependencia').val();
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goBuscaElaboradoPor&pcoDep=" + codDependencia, '', function(data) {
        fn_rptaBuscaElaboradoPorInter(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaElaboradoPorInter(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoElaboradoPorInter(cod, desc) {
    jQuery('#txtElaboradoPor').val(desc);
    jQuery('#sElaboradoPor').val(cod);
    removeDomId('windowConsultaElaboradoPor');
}

function fn_inicializaDocInterEmi(sCoAnnio){
    //countPressBtnChange=0;
    jQuery('#buscarDocumentoEmiBean').find('#esFiltroFecha').val("2");//solo año
    //jQuery("#fechaFiltro").html("Año: "+sCoAnnio);
    /*jQuery("#fechaFiltro").showDatePicker({
        showDia: false,
        selectTodosMeses:true,
        pressAceptarEvent: function(data) {
            if(data.rbOp==="0"){
                jQuery('#buscarDocumentoEmiBean').find('#sCoAnnio').val(data.anio);
                if(data.mes==="" && data.anio!==""){
                   jQuery("#fechaFiltro").html("Año: "+data.anio);
                   jQuery('#buscarDocumentoEmiBean').find('#esFiltroFecha').val("2");//solo año
                }else{
                   jQuery("#fechaFiltro").html("Año: "+data.anio+"  Mes: "+monthYearArray[data.mes * 1]);  
                   jQuery('#buscarDocumentoEmiBean').find('#esFiltroFecha').val("3");//año y mes
                }
            }else if(data.rbOp==="1"){
                jQuery('#buscarDocumentoEmiBean').find('#esFiltroFecha').val("1");//rango fecha
                jQuery("#fechaFiltro").html("Del: "+data.fIni+"  Al: "+data.fFin); 
            }
        }
    });*/
    jQuery('#buscarDocumentoEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});        
    jQuery('#buscarDocumentoEmiBean').find('#sNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });    
    jQuery('#buscarDocumentoEmiBean').find('#sCoAnnioBus').find('option[value=""]').remove();
    pnumFilaSelect=0;
    changeTipoBusqEmiDocuInter("0");
}


function fu_eventoTablaEmiDocInter() {
    
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
            {"bSortable": true},
            {"bSortable": true},
            {"sType": "fecha"},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": false}
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
    //$("#myTableFixed thead tr").addClass("ui-datatable-fixed-scrollable-header ui-state-default")
    //$("#myTableFixed tbody tr").addClass("bx_sb ui-datatable-fixed-scrollable-body ui-datatable-fixed-data");
    function showdivToolTip(elemento, text)
    {
        $('#divflotante').html(text);

        var x = elemento.left;
        var y = elemento.top + 24;
        $("#divflotante").css({left: x, top: y});

        //document.getElementById('divflotante').style.top = elemento.top + 24;
        //document.getElementById('divflotante').style.left = elemento.left;		
        document.getElementById('divflotante').style.display = 'block';

        return;
    }
    /*$("#myTableFixed tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                //console.log($(this).index());
                var index = $(this).index();
                if (index === 3 || index === 7 || index === 8 || index === 9) {
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
            if (typeof($(this).children('td')[13]) !== "undefined") {
                jQuery('#txtpnuAnn').val($(this).children('td')[13].innerHTML);
                jQuery('#txtpnuEmi').val($(this).children('td')[14].innerHTML);
                jQuery('#txtpexisteDoc').val($(this).children('td')[17].innerHTML);
                jQuery('#txtpexisteAnexo').val($(this).children('td')[18].innerHTML);
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

function changeTipoBusqEmiDocuInter2() {
    changeTipoBusqEmiDocuInter('1');
}

function changeTipoBusqEmiDocuInter(tipo) {
    jQuery('#sTipoBusqueda').val(tipo);
    submitAjaxFormEmiDocInter(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormEmiDocInter(tipo) {
//    console.log(tipo);
    var validaFiltro = fu_validaFiltroEmiDocInter(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
            refreshScript("divTablaEmiDocumenAdm", data);
        }, 'text', false, false, "POST");
    }else if(validaFiltro === "2"){//buscar referencia
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoEmiBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferenciaInter(data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fu_rptaBuscaDocumentoEnReferenciaInter(data){
    if(data!==null){
        if(data==="0"){
          alert_Info("Buscar Referencia", "No Existe Documento.");
        }else{
          refreshScript("divTablaEmiDocumenAdm", data);  
        }
    }
}

function fu_validaFiltroEmiDocInter(tipo) {
    var valRetorno = "1";
    jQuery('#sFeEmiIni').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#sFeEmiFin').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    //var pEsIncluyeFiltro = jQuery("esIncluyeFiltro1").is(':checked');
    var pEsIncluyeFiltro = $('#buscarDocumentoEmiBean').find('#esIncluyeFiltro1').is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroEmiDocInterFiltrar(vFechaActual);     
    }else if(tipo==="1"){
      //verificar si se ingreso datos en los campos de busqueda de referencia
      valRetorno = fu_validarBusquedaXReferenciaInter(tipo);  
      if(valRetorno==="1"){
        valRetorno = fu_validaFiltroEmiDocInterBuscar();  
        if(valRetorno==="1"){
            if(pEsIncluyeFiltro){
               valRetorno = fu_validaFiltroEmiDocInterFiltrar(vFechaActual); 
            }else{
               valRetorno = setAnnioNoIncludeFiltroEmiInter();
            }
        }
      }
    }    
    return valRetorno;
}

function fu_validarBusquedaXReferenciaInter(tipo){
    var valRetorno="1";//no buscar por referencia
    if(tipo === "1"){
        var vBuscDestinatario = allTrim(jQuery('#sBuscDestinatario').val());
        var vDeTipoDocInter = allTrim(jQuery('#sDeTipoDocInter').val());
        var vCoAnnioBus = allTrim(jQuery('#sCoAnnioBus').val());
        var vNumDocRef = allTrim(jQuery('#sNumDocRef').val());

        if((typeof(vBuscDestinatario)!=="undefined" && vBuscDestinatario!==null && vBuscDestinatario!=="") &&
           (typeof(vDeTipoDocInter)!=="undefined" && vDeTipoDocInter!==null && vDeTipoDocInter!=="") &&
           (typeof(vCoAnnioBus)!=="undefined" && vCoAnnioBus!==null && vCoAnnioBus!=="") &&
           (typeof(vNumDocRef)!=="undefined" && vNumDocRef!==null && vNumDocRef!=="")){
           valRetorno = "2";//buscar por referencia
        }
        
    if(valRetorno==="2"){
        if (vNumDocRef !== "" && vNumDocRef !== null) {
            var vValidaNumero = fu_validaNumero(vNumDocRef);
            if (vValidaNumero !== "OK") {
               bootbox.alert("N° de Documento en referencia debe ser solo numeros.");
                valRetorno = "0";
            }
        }
    }        
    }
    return valRetorno;
}

function fu_validaFiltroEmiDocInterFiltrar(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFecha('buscarDocumentoEmiBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoEmiBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoEmiBean').find('#sCoAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }   
            
            var vFeInicio = jQuery("#sFeEmiIni").val();
            var vFeFinal = jQuery("#sFeEmiFin").val();
            if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoEmiBean').find('#sCoAnnio').val(pAnnioBusq);                          
                    }                
                }               
            }           
                
           if(pEsFiltroFecha==="1" /*|| pEsFiltroFecha==="3"*/){
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
function upperCaseBuscarDocumentoEmiBeanInter(){
    jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').val()));
    jQuery('#buscarDocumentoEmiBean').find('#sBuscNroExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sBuscNroExpediente').val()));
    jQuery('#buscarDocumentoEmiBean').find('#sDeAsuM').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sDeAsuM').val()));
}
function fu_validaFiltroEmiDocInterBuscar() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoEmiBeanInter();
    
    var vNroEmision = jQuery('#sNumCorEmision').val();
    var vNroDocumento = jQuery('#sNumDoc').val();
    var vNroExpediente = jQuery('#sBuscNroExpediente').val();
    var vAsunto = jQuery('#sDeAsuM').val();
    
    if((typeof(vNroEmision)==="undefined" || vNroEmision===null || vNroEmision==="") &&
       (typeof(vNroDocumento)==="undefined" || vNroDocumento===null || vNroDocumento==="") &&
       (typeof(vNroExpediente)==="undefined" || vNroExpediente===null || vNroExpediente==="") &&
       (typeof(vAsunto)==="undefined" || vAsunto===null || vAsunto==="")){
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    if(valRetorno==="1"){
        if (vNroEmision !== "" && vNroEmision !== null) {
            var vValidaNumero = fu_validaNumero(vNroEmision);
            if (vValidaNumero !== "OK") {
                //alert("N° de Emisión debe ser solo numeros.");
                alert_Warning("Buscar: ","N° de Emisión debe ser solo numeros.");
                valRetorno = "0";
            }
        }
    }
    return valRetorno;
}

function fu_cleanEmiDocInter(tipo) {
    if (tipo==="1") {
        jQuery("#sNumDoc").val("");
        jQuery("#sNumDocRef").val("");
        jQuery("#sBuscNroExpediente").val("");
        jQuery("#sDeAsuM").val("");
        jQuery("#sDeTipoDocInter option[value=]").prop("selected", "selected");
//        jQuery("#sBuscEstado").val("");
        jQuery("#sFeEmiIni").val("");
        jQuery("#sFeEmiFin").val("");
//        jQuery("#sDeEmiReferencia").val("");
        jQuery("#txtDepEmiteBus").val(" [TODOS]");
        jQuery("#sBuscDestinatario").val("");
//        jQuery("#sBuscElaboraradoPor").val("");
        jQuery("#sNumCorEmision").val("");
        jQuery("#esIncluyeFiltro1").prop('checked',false);
        jQuery("#esIncluyeFiltro1").attr('checked',false);
        jQuery("#sCoAnnioBus").find('option:first').prop("selected", "selected");
    } else if(tipo==="0"){
        jQuery("#sEstadoDoc option[value=5]").prop("selected", "selected");
        jQuery("#sPrioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#coTema option[value=]").prop("selected", "selected");
        jQuery("#sTipoDoc option[value=]").prop("selected", "selected");
        jQuery("#sRefOrigen").val("");
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#sDestinatario").val("");
        jQuery("#txtDestinatario").val("[TODOS]");
        jQuery("#sElaboradoPor").val("");
        jQuery("#txtElaboradoPor").val("[TODOS]");
        jQuery("#sExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("2");//solo año
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val());
        jQuery("#sCoAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
    }
}

function fu_goNuevoEmisionDocInter() {
    var validaFiltro = "";
//var tipo = dojo.byId("sTipoControl").value;
//jQuery('#sTipoBusqueda').val("1");
    validaFiltro = "1";/*fu_validaFiltroEmiDocInter(tipo);*/
    if (validaFiltro === "1") {
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goNuevoDocumentoEmi", '', function(data) {
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            refreshScript("divNewEmiDocumAdmin", data);
            
            fn_cargaToolBarEmiInter();
        }, 'text', false, false, "POST");
    }
    return false;
}

function fn_grabarDocumentoEmiAdmInter() {
    var validaFiltro = fu_verificarCamposDocEmiAdmInter('0', '');
    if (validaFiltro === "1") {
        var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        var rpta = fu_verificarDestinatarioInter();
        var nrpta = rpta.substr(0, 1);
        var vResult = "0";
        if (pesDocEmi === "7") {
            if (nrpta === "1") {
                vResult = "1";
            }
        } else {
            if (nrpta === "1" || nrpta === "E") {
                vResult = "1";
            }
        }
        if (vResult === "1") {
            rpta = fu_verificarReferencia();
            nrpta = rpta.substr(0, 1);            
            if(nrpta === "1"){
                rpta = fu_verificarEmpVoBo();//add vobo
                nrpta = rpta.substr(0, 1);            
                if(nrpta === "1"){
                    //verificar si necesita grabar el documento.
                    
                    rpta = fu_verificarChangeDocumentoEmiAdmInter();
                    nrpta = rpta.substr(0, 1);
                    if (nrpta === "1") {
                        fn_goGrabarDocumentoEmiAdmInter();//grabar Documento
                    } else {
                        alert_Info("Emisión :", rpta);
                    }                    
                }else{
                    alert_Info("Emisión :", rpta);
                }
            }else{
                alert_Info("Emisión :", rpta);
            }
        } else {
            alert_Info("Emisión :", rpta);
        }
    }
    return false;
}

function fn_firmarDocumentoEmiInter() {

    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    jQuery("#rutaDocFirma").val("");
    
    var ptiOpe = "5";

    var rpta = fu_verificarChangeDocumentoEmiAdmInter();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    } else {
        if (!!pnuAnn&&!!pnuEmi) {
            /*if(fu_existeVistoBuenoDocAdm()){
                alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
            }else{*/
            var param={pnuAnn:pnuAnn,pnuEmi:pnuEmi};
            ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goVerificarVistoBuenoPendiente", param, function(data) {
                if(data.coRespuesta==="1"){
                    var p = new Array();
                    p[0] = "accion=goRutaFirmaDocReporte";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=" + ptiOpe;
                    ajaxCall("/srDocObjetoInter.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data);
                        if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                            if (docs.retval === "OK") {
                                jQuery('#documentoEmiBean').find("#txtnuDocEmiAn").val(docs.numeroDoc);
                                jQuery('#documentoEmiBean').find("#nuDocEmi").val(docs.numeroDoc);
                                jQuery('#esActualizadoNuDocEmi').val("1");
                                jQuery('#documentoEmiBean').find("#feEmiCorta").val(docs.fechaFirma);
                                jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val(docs.nuSecFirma);
                                jQuery("#noPrefijo").val(docs.noPrefijo);
                                jQuery("#rutaDocFirma").val(docs.noDoc);
                                jQuery("#inFirmaEmi").val(docs.inFirma);
                                showBtnEnviarDocAdmInter();

                                //result = fn_firmarDocApplet(docs.noUrl, docs.noDoc);
                                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1"};
                                //runApplet(appletsTramiteDoc.ejecutaFirma,param,function(data){
                                runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma, param, function(data){
                                    result=data;
                                });
                            } else {
                                //Error en Documento
                                alert_Danger("!Repositorio : ", docs.retval);
                            }
                        }

                    }, 'text', false, false, "POST");                                    
                }else{
                    alert_Danger("!Emisión : ", data.deRespuesta);
                }
            },'json', false, false, "POST");              
            /*}*/
        } else {
           bootbox.alert("Documento no encontrado");
        }
    }
}

function showBtnEnviarDocAdmInter(){
    var btnEmitirDoc = jQuery('#divEmitirDoc').find('button').get(0);
    btnEmitirDoc.removeAttribute('onclick');
    btnEmitirDoc.setAttribute('onclick','fu_changeEstadoDocEmiAdmInter(\'0\');');
    jQuery('#divEmitirDoc').show();
}

function fu_verificarChangeDocumentoEmiAdmInter() {//si es "1" necesita grabar el documento.
    //jQuery('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmiInter();
 
    var rpta = fn_validarEnvioGrabaDocEmiAdm(new Function('return ' + cadenaJson)());

    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"||!(!!jQuery('#documentoEmiBean').find('#nuEmi').val()&&!!jQuery('#documentoEmiBean').find('#nuAnn').val())) {
        return "1";
    } else {
        //alert_Info("",rpta.substr(1));
        return rpta.substr(1);
    }
}

function regresarEmitDocumAdmInter(pclickBtn) {
    if (pclickBtn === "1") {
        var vEsDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        if(vEsDocEmi==="5" || vEsDocEmi==="7"){
            var rpta = fu_verificarChangeDocumentoEmiAdmInter();
            var nrpta = rpta.substr(0, 1);
            if (nrpta === "1") {
                bootbox.dialog({
                  message: " <h5>Existen Cambios en el Documento.\n" +
                            "¿ Desea cerrar el Documento ?</h5>",
                  buttons: {
                    SI: {
                      label: "SI",
                      className: "btn-default",
                      callback: function() {
                        animacionventanaRecepDoc();
                        setTimeout(function(){continueRegresarEmitDocumAdmInter();}, 300);
                      }                      
                    },                          
                    NO: {
                      label: "NO",
                      className: "btn-primary"
                    }
                  }
                });
            }else{
                animacionventanaRecepDoc();
                setTimeout(function(){continueRegresarEmitDocumAdmInter();}, 300);
            }
        }else{
            animacionventanaRecepDoc();
            setTimeout(function(){continueRegresarEmitDocumAdmInter();}, 300);
        }
    }else{
      continueRegresarEmitDocumAdmInter();
    }
}

function continueRegresarEmitDocumAdmInter(){
    if (jQuery('#divRecepDocumentoAdmin').length === 0) {
        jQuery('#divEmiDocumentoAdmin').toggle();                                
        jQuery('#divNewEmiDocumAdmin').toggle(); 
        submitAjaxFormEmiDocInter(jQuery('#buscarDocumentoEmiBean').find('#sTipoBusqueda').val());
        jQuery('#divNewEmiDocumAdmin').html("");        
        //mostrarOcultarDivBusqFiltro2();
    } else {
        var pnuAnn = jQuery('#txtpnuAnn').val();
        if (pnuAnn !== "") {
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
            p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
            ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goEditarDocumento", p.join("&"), function(data) {
                refreshScript("divWorkPlaceRecepDocumAdmin", data);
                fn_cargaToolBarRec();
            }, 'text', false, false, "POST");
        } else {
            alert_Info("Emisión :", "Seleccione una fila de la lista");
        }
    }    
}

function fn_anularDocEmiAdmInter() {

    var rpta = fu_verificarChangeDocumentoEmiAdmInter();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");
    } else {
        if(fu_existeVistoBuenoDocAdm()){ 
            alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");            
        }else{
            var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
            if (pesDocEmi !== null && (pesDocEmi === "0" || pesDocEmi === "5" || pesDocEmi === "7")) {
                bootbox.dialog({
                    message: " <h5>¿ Esta Seguro de Anular Documento ?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-primary",
                            callback: function() {
                                $('#documentoEmiBean').find('select').removeProp('disabled');
                                ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goAnularDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
                                    fn_rptAnularDocEmiAdmInter(data);
                                }, 'json', false, false, "POST");
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-default"
                        }
                    }
                });               
            } else {
                //alert("Documento ya fue leido por los Destinatarios!!");
                alert_Warning("Emisión :", "Documento ya fue leido por los Destinatarios.");
            }            
        }
    }
    return false;
}

function fn_rptAnularDocEmiAdmInter(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            regresarEmitDocumAdmInter('0');
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
}

function fn_eliminarDocEmiAdmInter() {
    var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
    if (pesDocEmi !== null && pesDocEmi === "9") {
//        if (confirm('¿ Esta Seguro de Eliminar ?')) {
//            $('#documentoEmiBean').find('select').removeProp('disabled');
//            ajaxCall("/srDocumentoAdmEmision.do?accion=goEliminarDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
//                fn_rptEliminarDocEmiAdm(data);
//            }, 'json', false, false, "POST");
//        }
        bootbox.dialog({
            message: " <h5>¿ Esta Seguro de Eliminar ?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-primary",
                    callback: function() {
                        $('#documentoEmiBean').find('select').removeProp('disabled');
                        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goEliminarDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
                            fn_rptEliminarDocEmiAdmInter(data);
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
    return false;
}

function fn_rptEliminarDocEmiAdmInter(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            regresarEmitDocumAdmInter('0');
        } else {
            alert_Info("Eliminar :", data.deRespuesta);
        }
    }
}

function fu_verificarCamposDocEmiAdmInter(sOrigenBtn, pEstadoDoc) {
    var noForm='#documentoEmiBean';
    var pnuDiaAte = jQuery(noForm).find('#nuDiaAte').val();
    var pnuFol = jQuery(noForm).find('#nuFol').val();
    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
    jQuery(noForm).find('#deDocSig').val(allTrim(fu_getValorUpperCase(jQuery(noForm).find('#deDocSig').val())));
    var pdeDocSig = jQuery(noForm).find('#deDocSig').val();
    var pfeEmiCorta = jQuery(noForm).find('#feEmiCorta').val();
    var pfeHoraActual = jQuery("#txtfechaHoraActual").val();
    var pfeActual = pfeHoraActual.substr(0, 10);
    var pcoTipDocAdm = jQuery(noForm).find('#coTipDocAdm').val();
    var pfirmadoPor = jQuery(noForm).find('#coEmpEmi').val();
    var pelaboradoPor = jQuery(noForm).find('#coEmpRes').val();
    jQuery(noForm).find('#deAsu').val(allTrim(jQuery(noForm).find('#deAsu').val()));
    var pdeAsu = jQuery(noForm).find('#deAsu').val();
    var maxLengthDeAsu = jQuery(noForm).find('#deAsu').attr('maxlength');
    var pesDocEmi = jQuery(noForm).find('#esDocEmi').val();
    jQuery(noForm).find('#nuDocEmi').val(replicate(allTrim(jQuery(noForm).find('#nuDocEmi').val()), 6));
    var pnuDocEmi=jQuery(noForm).find('#nuDocEmi').val();

    var valRetorno = "1";
    var vValidaNumero = "";
    
    if (!(pfirmadoPor !== null && pfirmadoPor !== "")) {
        valRetorno = "0";
        bootbox.alert("<h5>Indicar Quien Firmará el Documento.</h5>", function() {
            jQuery(noForm).find('#deEmpEmi').focus();
        });
        jQuery(noForm).find('#deEmpEmi').focus();
    }
    
    if (valRetorno === "1") {
        if (!(pelaboradoPor !== null && pelaboradoPor !== "")) {
            valRetorno = "0";
            bootbox.alert("<h5>Indicar Quien Elabora el Documento.</h5>", function() {
                jQuery(noForm).find('#coTipDocAdm').focus();
            });
            jQuery(noForm).find('#coTipDocAdm').focus();
        }    
    }    

    if (valRetorno === "1") {
        if (!(pcoTipDocAdm !== null && pcoTipDocAdm !== "-1")) {
            valRetorno = "0";
            //alert("Elija tipo de documento!!");
            bootbox.alert("<h5>Seleccione tipo de Documento.</h5>", function() {
                jQuery(noForm).find('#coTipDocAdm').focus();
            });
            jQuery(noForm).find('#coTipDocAdm').focus();
        }    
    }

    if (valRetorno === "1") {
        if (!(pdeAsu !== null && pdeAsu !== "")) {
            valRetorno = "0";
            //alert("El asunto no debe ser vacio!!");
            bootbox.alert("<h5>El asunto no debe ser vacio.</h5>", function() {
                jQuery("#deAsu").focus();
            });            
            jQuery("#deAsu").focus();
        }else {
            if(!!maxLengthDeAsu){
                var nrolinesDeAsu = (pdeAsu.match(/\n/g) || []).length;
                if(pdeAsu.length+nrolinesDeAsu > maxLengthDeAsu){
                    valRetorno = "0";
                    bootbox.alert("<h5>El Asunto Excede el límite de "+maxLengthDeAsu+" caracteres.</h5>", function() {
                        jQuery("#deAsu").focus();
                    });
                    jQuery("#deAsu").focus();
                }
            }
        }
    }
    
    if (valRetorno === "1") {
        if (pnuDiaAte !== null && pnuDiaAte !== "") {
            vValidaNumero = fu_validaNumero(pnuDiaAte);
            if (vValidaNumero !== "OK") {
                valRetorno = "0";
                //alert("Días de Atención debe ser solo números.");
                bootbox.alert("<h5>Días de Atención debe ser solo números.</h5>", function() {
                    jQuery("#nuDiaAte").focus();        
                });                
                jQuery("#nuDiaAte").focus();
            }
        } else {
            valRetorno = "0";
            //alert("Especifique días de Atención.");
            bootbox.alert("<h5>Especifique días de Atención.</h5>", function() {
                jQuery("#nuDiaAte").focus();        
            });              
            jQuery("#nuDiaAte").focus();
        }
    }
    
    if (valRetorno === "1") {
        if (pnuFol !== null && pnuFol !== "") {
            vValidaNumero = fu_validaNumero(pnuFol);
            if (vValidaNumero !== "OK") {
                valRetorno = "0";
                //alert("Días de Atención debe ser solo números.");
                bootbox.alert("<h5>Nro de folio debe ser solo números.</h5>", function() {
                    jQuery("#nuFol").focus();        
                });                
                jQuery("#nuFol").focus();
            }
            else
            {
                if (pnuFol<=0)
                {
                    valRetorno = "0";
                    
                    bootbox.alert("<h5>Nro de folio debe ser mayor a cero.</h5>", function() {
                    jQuery("#nuFol").focus();        
                        });                
                    jQuery("#nuFol").focus();
                }
            }
        } else {
            valRetorno = "0";
            //alert("Especifique días de Atención.");
            bootbox.alert("<h5>Especifique Nro de folio.</h5>", function() {
                jQuery("#nuFol").focus();        
            });              
            jQuery("#nuFol").focus();
        }
    }
    

    if (valRetorno === "1") {
        if (pnuAnn !== null && pnuAnn !== "") {
            vValidaNumero = fu_validaNumero(pnuAnn);
            if (vValidaNumero !== "OK") {
                valRetorno = "0";
               bootbox.alert("Año de Documento solo números.");
            }
        } else {
            valRetorno = "0";
           bootbox.alert("Especifique Año de Documento.");
        }
    }
    
    if (valRetorno === "1") {
        if (pdeDocSig !== null && pdeDocSig !== "") {
            var rptValida = validaCaracteres(pdeDocSig, "1");
            if (rptValida !== "OK") {
                valRetorno = "0";
                //alert(rptValida);
                bootbox.alert("<h5>"+rptValida+"</h5>", function() {
                    jQuery("#deDocSig").focus();        
                });                 
                jQuery("#deDocSig").focus();
            }
        } else {
           valRetorno = "0";
            //alert("Especifique siglas del Documento");
           bootbox.alert("<h5>Especifique siglas del Documento.</h5>", function() {
               jQuery("#deDocSig").focus();        
           });            
            jQuery("#deDocSig").focus();
        }
    }
    
    if (valRetorno === "1") {
        //VALIDA FECHAS
        if (pfeEmiCorta === "") {
            valRetorno = "0";
           bootbox.alert('Especifique Fecha de Documento.');
        } else {
            valRetorno = fu_validaFechaConsulta(pfeEmiCorta, pfeActual);
            if (valRetorno !== "1") {
                valRetorno = "0";
               bootbox.alert("Error Fecha De Documento" + valRetorno);
            }
        }
    }
    
    if (valRetorno === "1") {
        if (pesDocEmi !== "5" && pesDocEmi !== "7" && pesDocEmi !== "0") {
            valRetorno = "0";
        } else {
            if (sOrigenBtn === "1") {
                if (pesDocEmi === "0" || pesDocEmi === "5") {
                    if (pEstadoDoc !== "5" && pEstadoDoc !== "7") {
                        valRetorno = "0";
                    }
                }
            } else {
                if (pesDocEmi === "0") {
                    valRetorno = "0";
                }
            }
        }
    }
    
    if(valRetorno==="1"){
        if(!!pnuDocEmi){
          vValidaNumero = fu_validaNumero(pnuDocEmi);  
            if (vValidaNumero!=="OK") {
                valRetorno = "0";
                bootbox.alert("<h5>Nro. Documento debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find('#nuDocEmi').focus();        
                });                
            }          
        }
    }
    
    if(valRetorno === "1"){
        if((pEstadoDoc==="5"&&pesDocEmi==="7")||sOrigenBtn ==="0"){
            if(jQuery('#esActualizadoNuDocEmi').val()==="1"){
                jQuery(noForm).find("#nuDocEmi").trigger("change");
                jQuery('#esActualizadoNuDocEmi').val("0");
            }            
        }
    }
    return valRetorno;
}

function fn_seteaCamposDocumentoEmiInter() {
    jQuery('#envDocumentoEmiBean').val("0");
    jQuery('#envExpedienteEmiBean').val("0");
    jQuery('#envRemitenteEmiBean').val("0");
    var p = new Array();
    p[0] = "accion=goUpdTlbsDestinatario";
    p[1] = "pnuAnn=" + $("#nuAnn").val();
    p[2] = "pnuEmi=" + $("#nuEmi").val();
    //p[3] = "pcoDependencia=" + $("#coDepEmi").val();        
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_updateTblsEmisioDocAdmInter(data);
    },
            'text', false, true, "POST");
}
    
function fn_updateTblsEmisioDocAdmInter(XML_AJAX) {
    if (XML_AJAX !== null) {
        refreshScript("divActualizaTablasDestintario", XML_AJAX);
        fn_changeTipoDestinatarioDocuInter(jQuery("#sTipoDestinatario").val(), jQuery("#esDocEmi").val());
        var p = new Array();
        p[0] = "accion=goUpdTlbReferencia";
        p[1] = "pnuAnn=" + $("#nuAnn").val();
        p[2] = "pnuEmi=" + $("#nuEmi").val();
        p[3] = "pcoDependencia=" + $("#coDepEmi").val();
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
            if (data !== null) {
                refreshScript("divtablaRefEmiDocAdm", data);
                $("#txtIndexFilaRefEmiDoc").val("-1");
                //fu_cargaEdicionDocAdm("00", jQuery('#esDocEmi').val());
            } else {
                regresarEmitDocumAdm('0');
            }
        },
                'text', false, true, "POST");
    } else {
        regresarEmitDocumAdm('0');
    }
}

function fu_verificarDestinatarioInter() {
    var vResult = " destinatario";
    var vtabla = ["tblDestEmiDocAdmInter= destinatario InterInstitucion"];
    //var ltabla = vtabla.length;
    /*if (pverificarRef === "1") {
        vtabla.push("tblRefEmiDocAdm= Referencia");
    }*/
    var countEmpty = 0;
    for (var i = 0; i < vtabla.length; i++) {
        var vauxTabla = vtabla[i].split("=");
        var rpta = fn_verificarExisteDestinatario(vauxTabla[0]);
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "0") {
            vResult = rpta.substr(1) + vauxTabla[1];
            break;
        } else if (nrpta === "2" /*&& vauxTabla[0]!=="tblRefEmiDocAdm"*/) {
            countEmpty++;
            //if (countEmpty === ltabla) {
            if (countEmpty === vtabla.length) {
                vResult = rpta.substr(1) + vResult;
                break;
            }
        } else {
            vResult = rpta.substr(0, 1);
        }
    }
    return vResult;
}

function fn_tblDestEmiDocInterToJson() {
    var json = '[';
    var itArr = [];
    var tbl1 = fn_tblDestInterEmiDocAdmToJson();
    tbl1 !== "" ? itArr.push(tbl1) : "";
    json += itArr.join(",") + ']';
    return json;
}
function fn_tblDestInterEmiDocAdmToJson() {
  
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=7";
    arrColMostrar[1] = "cidCat=1";
    arrColMostrar[2] = "nuRuc=2";
    arrColMostrar[3] = "deDepDes=3";
    arrColMostrar[4] = "deNomDes=4";
    arrColMostrar[5] = "deCarDes=5";
   // arrColMostrar[6] = "nuFol=6";
    arrColMostrar[6] = "coTipoDestino=9";
    arrColMostrar[7] = "nuDes=10";
    return fn_tblDestEmihtml2Interjson('tblDestEmiDocAdmInter', 1, arrColMostrar, 11, "1", 7, "BD");
}

function fn_tblDestEmihtml2Interjson(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
    //var json = '[';
    var otArr = [];
    var count = 0;    

    
    $('#' + idTable + ' tr').each(function(i) {
        if (count >= iniFila) {
            var x = $(this).children();
            var itArr = [];
            
            if ($(this).find("td").eq(colEstadoActivo - 1).text() === estadoActivo && $(this).find("td").eq(colAccionBD - 1).text() !== accionBD) {
                x.each(function(index) {                   
                    for (var i = 0; i < colMostrar.length; i++) {
                       
                        var auxArrColMostrar = colMostrar[i].split("=");
                        if (auxArrColMostrar[1] * 1 === index + 1) {
                            var campoBean = auxArrColMostrar[0];
                            var valCampoBean = "";
                            if (typeof($(this).find('input[type=text]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=text]').val();
                            } else if (typeof($(this).find('textarea').val()) !== "undefined") {
                                valCampoBean = $(this).find('textarea').val();
                            } else if (typeof($(this).find('select').val()) !== "undefined") {
                                valCampoBean = $(this).find('select').val();
                            } else if (typeof($(this).find('input[type=radio]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=radio]:checked').val();
                            } else if (typeof($(this).find('input[type=checkbox]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=checkbox]').is(':checked')?'1':'0';
                            } else {
                                valCampoBean = $(this).text();
                            }
                            if (typeof($(this).find('span').html()) !== "undefined") {
                                valCampoBean = $(this).find('span').html();
                            }
                            if (typeof($(this).find('input[id=txtCodigoUbigeoOtro]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[id=txtCodigoUbigeoOtro]').val();                                  
                            }
                            if (typeof($(this).find('input[id=idCategoria]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[id=idCategoria]').val();                                  
                            }
                            if (typeof($(this).find('input[id=idEntidad]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[id=idEntidad]').val();                                  
                            }
                            itArr.push('"' + campoBean + '":' + JSON.stringify(valCampoBean));
                        }
                    }
                });
                otArr.push('{' + itArr.join(',') + '}');
            }
        }
        count++;
    });
    //json += otArr.join(",") + ']';
    return otArr.join(",");
}


function fn_buildSendJsontoServerDocuEmiInter() {
    var result = "{";
    result = result + '"nuAnn":"' + $("#nuAnn").val() + '",';
    result = result + '"nuEmi":"' + $("#nuEmi").val() + '",';
    //result = result + '"documentoEmiBean":' + JSON.stringify($('#documentoEmiBean').serializeFormJSON()) + ',';
    var valEnvio = jQuery('#envDocumentoEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"documentoEmiBean":' + JSON.stringify(getJsonFormDocumentoEmiBeanInter()) + ',';
    }
    valEnvio = jQuery('#envExpedienteEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"expedienteEmiBean":' + JSON.stringify(getJsonFormExpedienteEmiBean()) + ',';
    }
    valEnvio = jQuery('#envRemitenteEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"remitenteEmiBean":' + JSON.stringify(getJsonFormRemitenteEmiBean()) + ',';
    }
    result = result + '"lstReferencia":' + fn_tblRefEmiDocAdmToJson() + ',';
    result = result + '"lstDestinatario":' + sortDelFirst(fn_tblDestEmiDocInterToJson()) + ',';
//       console.log(fn_tblDestEmiDocInterToJson());
    result = result + '"lstEmpVoBo":' + sortDelFirst(fn_tblEmpVoBoDocAdmToJson());//add vobo
    return result + "}";
    
}

function fn_cargaDocEmiInter() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
    var ptiOpe = "3";
    if (pesDocEmi === "7") {
        ptiOpe = "4";
    }
    var resulCarga = "ERROR";
    var docs;
    if (!!pnuAnn&&!!pnuEmi) {
        if(fu_existeVistoBuenoDocAdm()){ 
            alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");            
        }else{
            /* Obteniendo la ruta de Carga*/
            var p = new Array();
            p[0] = "accion=goRutaCargaDoc";
            p[1] = "nuAnn=" + pnuAnn;
            p[2] = "nuEmi=" + pnuEmi;
            p[3] = "tiOpe=" + ptiOpe;
            ajaxCall("/srDocObjetoInter.do", p.join("&"), function(data) {
                eval("docs=" + data);
                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
//                    console.log(docs.noUrl);
//                    fn_cargaDocApplet(docs.noUrl, docs.noDoc,function(data){
                    fn_cargaDocDesktop(docs.noUrl, docs.noDoc,function(data){
                    resulCarga=data;
                        if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                            var p = new Array();
                            p[0] = "accion=goCargarDocEmi";
                            p[1] = "nuAnn=" + pnuAnn;
                            p[2] = "nuEmi=" + pnuEmi;
                            p[3] = "tiOpe=" + ptiOpe;
                            p[4] = "nuSec=" + resulCarga;
                            p[5] = "noDoc=" + docs.noDoc;
                            ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
                                var rpta;
                                try {
                                    eval("var rptaOb=" + data);
                                    rpta = rptaOb;
                                } catch (e) {
                                   bootbox.alert(data);
                                   return;
                                }
                                if (typeof (rpta) !== "undefined" && typeof (rpta.coRespuesta) !== 'undefined' && rpta.coRespuesta !== "") {
                                    if (rpta.coRespuesta === "1") {
                                        alert_Sucess("Repositorio: ", rpta.deRespuesta);
                                    }
                                    else {
                                        alert_Danger("Repositorio: ", rpta.deRespuesta);
                                    }
                                }
                            }, 'text', false, false, "POST");
                        } else {
                            alert_Danger("Repositorio: ", "Error al cargar documento, intente nuevamente.");
                        }
                    });
                }
            }, 'text', true, false, "POST");
            /* */            
        }
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");
    }
}




function getJsonFormDocumentoEmiBeanInter() {
    var arrCampoBean = new Array();
    arrCampoBean[0] = "nuAnn";
    arrCampoBean[1] = "nuEmi";
    arrCampoBean[2] = "nuCorEmi";
    arrCampoBean[3] = "coTipDocAdm";
    arrCampoBean[4] = "nuDocEmi";
    arrCampoBean[5] = "feEmiCorta";
    arrCampoBean[6] = "nuDiaAte";
    arrCampoBean[7] = "deAsu";
    arrCampoBean[8] = "esDocEmi";
    arrCampoBean[9] = "coDepEmi";
    arrCampoBean[10] = "coEmpEmi";
    arrCampoBean[11] = "coEmpRes";
    arrCampoBean[12] = "coLocEmi";
    arrCampoBean[13] = "tiEmi";
    arrCampoBean[14] = "deDocSig";
    arrCampoBean[15] = "nuAnnExp";
    arrCampoBean[16] = "nuSecExp";
    arrCampoBean[17] = "nuCorDoc";
    arrCampoBean[18] = "nuFol";
    var o = {};
    var a = $('#documentoEmiBean').serializeArray();
    $.each(a, function() {
        for (var i = 0; i < arrCampoBean.length; i++) {
            if (this.name === arrCampoBean[i]) {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
            }
        }
    });
    return o;
}



function fn_goGrabarDocumentoEmiAdmInter() {
//    $('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmiInter();
    //console.log(cadenaJson);
    var pcrearExpediente = "0";
    var pesnuevoDocEmiAdm = jQuery("#txtEsNuevoDocAdm").val();
    if (pesnuevoDocEmiAdm === "1" && jQuery("#nuAnnExp").val() === "" && jQuery("#nuSecExp").val() === "") {
        /*bootbox.dialog({
            message: " <h5>¿ Desea crear expediente para este documento ?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-primary",
                    callback: function() {
                        pcrearExpediente = "1";
                        fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente); 
                    }                        
                },
                NO: {
                    label: "NO",
                    className: "btn-default",
                    callback: function() {
                        fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente); 
                    }                      
                }
            }
        });   */
        var inCreaExpediente = jQuery("#inCreaExpediente").val();
        if(inCreaExpediente==='SI'){
            pcrearExpediente = "1";
            fn_submitGrabarDocumentoEmiAdmInter(cadenaJson,pcrearExpediente); 
    }else{
            pcrearExpediente = "0";
       fn_submitGrabarDocumentoEmiAdmInter(cadenaJson,pcrearExpediente); 
    }
    }else{
       fn_submitGrabarDocumentoEmiAdmInter(cadenaJson,pcrearExpediente); 
}
}

function fn_submitGrabarDocumentoEmiAdmInter(cadenaJson,pcrearExpediente){
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    ajaxCallSendJson("/srDocumentoEmisionInteroperabilidad.do?accion=goGrabaDocumentoEmi&pcrearExpediente=" + pcrearExpediente, cadenaJson, function(data) {
        fn_rptaGrabaDocumEmiAdminInter(data, pcrearExpediente);
    },
    'json', false, false, "POST");    
}

function fn_rptaGrabaDocumEmiAdminInter(data, sCrearExpediente) {
    var strCadena = '';
    if (data !== null) {
        if (data.coRespuesta === "1") {
            if (jQuery('#txtEsNuevoDocAdm').val() === "1") {
                jQuery('#txtEsNuevoDocAdm').val("0");
//                jQuery("#nuEmi").val(data.nuEmi);
//                jQuery("#nuCorEmi").val(data.nuCorEmi);
                if (sCrearExpediente === "1") {
                    jQuery("#nuAnnExp").val(data.nuAnnExp);
                    jQuery("#nuSecExp").val(data.nuSecExp);
                    jQuery("#nuExpediente").val(data.nuExpediente);
                    jQuery("#feExpCorta").val(data.feExp);
                    strCadena = 'con expediente.';
                }else{
                    strCadena = 'sin expediente.';
                }
            }
            jQuery("#nuEmi").val(data.nuEmi);
            if (jQuery("#nuCorEmi").val() === "") {
                jQuery("#nuCorEmi").val(data.nuCorEmi);
            }
            if(!!data.nuDocEmi&&data.nuDocEmi!==null&&data.nuDocEmi!=='null'){
                jQuery('#nuDocEmi').val(data.nuDocEmi);
            }            
            fn_seteaCamposDocumentoEmiInter();            //resetear variables del documento
            fn_setTblPersonalVoBo();//setter tabla personal VB.
            fu_cargaEdicionDocAdm("06", jQuery('#esDocEmi').val());
            //alert("Datos Guardados.");
            alert_Sucess("Éxito!", "Documento grabado correctamente "+strCadena);
        } else {
            alert_Info("Emisión :", data.deRespuesta);
        }
    }
}

function fu_cambioTxtEmiDocInter(input) {
    var p = new Array();
    p[0] = 0;
    fn_evalDestinatarioCorrectotblEmiInter('tblDestEmiDocAdmInter', (($(input).parent()).parent()).index(), 5, p, 1);
}

function fn_evalDestinatarioCorrectotblEmiInter(idTabla, indexFila, indexColDatos, arrIndexCompara, contFound) {
    var arrInputAnt = ($("#" + idTabla + " tbody tr:eq(" + indexFila + ")").children().get(indexColDatos).innerHTML).split("$#");
    var countFound = 0;
    for (var i = 0; i < arrInputAnt.length; i++)
    {
        $("#" + idTabla + " tbody tr:eq(" + indexFila + ") td").each(function(index) {
            for (var j = 0; j < arrIndexCompara.length; j++) {
                if (arrIndexCompara[j] === index) {
                    var valInput = fu_getValorUpperCase($(this).find('input[type=text]').val());
                    if (arrInputAnt[i] === valInput) {
                        countFound++;
                    }
                }
            }
        });
    }
    if (countFound === contFound) {
        fn_changeDestinatarioCorrectoOtro(idTabla, indexFila);
    } else {
        fn_changeDestinatarioIncorrectoOtro(idTabla, indexFila);
    }
}

function fn_validaDestinatarioInterDuplicado(idTabla, arrCamposCompara, bcomFilaActual, indexFilaActual) {
    var countFound = 0;
    var auxArrCamposCompara = arrCamposCompara[0].split("=");
    var indexColDependencia = auxArrCamposCompara[0] * 1;
    var coDepen = auxArrCamposCompara[1];
    auxArrCamposCompara = arrCamposCompara[1].split("=");
    var indexColEmpleado = auxArrCamposCompara[0] * 1;
    var coEmple = auxArrCamposCompara[1];
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            if (bcomFilaActual) {
                if (index !== indexFilaActual) {
                    if ($(row).find("td:eq(" + indexColDependencia + ")").text() === coDepen && $(row).find("td:eq(" + indexColEmpleado + ")").text() === coEmple) {
                        countFound++;
                    }
                }
            } else {
                if ($(row).find("td:eq(" + indexColDependencia + ")").text() === coDepen && $(row).find("td:eq(" + indexColEmpleado + ")").text() === coEmple) {
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



function fn_changeToProyectoDocEmiAdmInter() {
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goChangeToProyecto", $('#documentoEmiBean').serialize(), function(data) {
        fn_rptaChangeToProyectoDocEmiAdmInter(data);
    },
            'json', false, false, "POST");
}

function fn_rptaChangeToProyectoDocEmiAdmInter(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("5");
            fn_seteaCamposDocumentoEmiInter();//resetear variables del documento
            fn_updateTblVoBoDocAdmInter();
            fu_cargaEdicionDocAdm("06", jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmiInter();  
            $('#divEmitirMensajeria').hide();
        } else {
            alert_Danger("Emisión :",data.deRespuesta);
        }
    }
}

function fn_changeTipoDocEmiAdmInter(cmbTipoDoc){
    var coTiDoc=jQuery(cmbTipoDoc).val();
    var coTiDocLast=jQuery(cmbTipoDoc).find('option:last').val();
    var coTiDocFirst=jQuery(cmbTipoDoc).find('option:first').val();
    if(coTiDocLast==="-1"){
        jQuery(cmbTipoDoc).find('option:last').remove();
    }else if(coTiDocFirst==="-1"){
        jQuery(cmbTipoDoc).find('option:first').remove();
    }
    fn_limpiarNumeroDocEmi();

    fu_changeDocumentoEmiBean();
}
function editarDocumentoEmiClickInter(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo) {
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        p[2] = "pexisteDoc=" + pexisteDoc;
        p[3] = "pexisteAnexo=" + pexisteAnexo;
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmiInter();
        }, 'text', false, false, "POST");
    } else {
       bootbox.alert("Seleccione una fila de la lista");
    }
}

function editarDocumentoEmiInter() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    if (pnuAnn !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        p[3] = "pexisteDoc=" + jQuery('#txtpexisteDoc').val();
        p[4] = "pexisteAnexo=" + jQuery('#txtpexisteAnexo').val();
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmiInter();
        }, 'text', false, false, "POST");
    } else {
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

//function changeEstadoDocEmiAdm(component){
//   if($("#"+component).hasClass('open')){
//     $("#"+component).removeClass('open');     
//   }else{
//     $("#"+component).addClass('open');     
//   }
//}

function fn_eventEstDocInter(sTipoDestEmi, sEstadoDocInter) {
//   $("#ullsEstDocEmiAdm").hover(
//       function(){
//        return;
//       },
//       function(){
//        changeEstadoDocEmiAdm('estDocEmiAdm');
//       }
//   );
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });
    $("#txtIndicaciones").showBigTextBox({maxNumCar:600});
    jQuery('#documentoEmiBean').find('#nuDiaAte').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#nuFol').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#deDocSig').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#coDepEmi').change(function() {
        fu_changeRemitenteEmiBean();
    });
    jQuery('#documentoEmiBean').find('#coLocEmi').change(function() {
        fu_changeRemitenteEmiBean();
    });
//    jQuery('#documentoEmiBean').find('#coTipDocInter').change(function() {
//        fu_changeDocumentoEmiBean();
//    });
    jQuery('#documentoEmiBean').find('#deAsu').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#nuDocEmi').change(function() {
	var noForm='#documentoEmiBean';
        var pnuAnn = jQuery(noForm).find('#nuAnn').val();
        var pnuEmi = jQuery(noForm).find('#nuEmi').val();
        var ptiEmi = jQuery(noForm).find('#tiEmi').val();
        var pcoTipDocInter = jQuery(noForm).find('#coTipDocInter').val();
        var pcoDepEmi = jQuery(noForm).find('#coDepEmi').val();
        var pnuDocEmiAnn = jQuery(noForm).find('#txtnuDocEmiAn').val();
        var pnuDocEmi = allTrim(jQuery(noForm).find('#nuDocEmi').val());
        if (!!pnuDocEmi&&!!pnuAnn&&!!pnuEmi){
            var vValidaNumero = fu_validaNumero(pnuDocEmi);
            if (vValidaNumero==="OK") {
                pnuDocEmi = replicate(pnuDocEmi, 6);
                if (pnuDocEmiAnn !== pnuDocEmi || (typeof(pnuDocEmiAnn) !== "undefined" && pnuDocEmiAnn === "")) {
                    fn_jsonVerificarNumeracionDocEmi(pnuDocEmiAnn, pnuAnn, pnuEmi, ptiEmi, pcoTipDocInter, pcoDepEmi, pnuDocEmi);
                } else {
                    jQuery(noForm).find('#nuDocEmi').val(pnuDocEmiAnn);
                    jQuery(noForm).find('#nuCorDoc').val("1");
                }
            }else{
                bootbox.alert("<h5>Nro. Documento debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find('#nuDocEmi').focus();        
                });   
            }            
        }
        fu_changeDocumentoEmiBean();
    });
    if (jQuery("#txtEsNuevoDocInter").val() === '1') {
        jQuery("#coTipDocInter").focus();
    }
    fn_changeTipoDestinatarioDocuInter(sTipoDestEmi, sEstadoDocInter);
    fu_cargaEdicionDocAdm("06", sEstadoDocInter);
//    $('#containerTblVoBo').resizable({
//        handles: 's',
//        minHeight: 100,
//        maxHeight: 200
//    });
}

function fu_changeEstadoDocEmiAdmInter(pEstadoDoc) {
    var validaFiltro = fu_verificarCamposDocEmiAdmInter('1', pEstadoDoc);
    if (validaFiltro === "1") {
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeDocumentoEmiAdmInter();
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            alert_Warning("Emisión :", "Necesita grabar los cambios");
        } else {
            if (pEstadoDoc === "5") {//a proyecto
                bootbox.dialog({
                    message: " <h5>¿ Seguro de Guardar los Cambios ?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-primary",
                            callback: function() {
                                fn_changeToProyectoDocEmiAdmInter();//cambiar a proyecto
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-default"
                        }
                    }
                });                        
            } else if (pEstadoDoc === "7" && jQuery("#txtEsNuevoDocAdm").val() === "0") {//a Despacho
//                if(fu_existeVistoBuenoDocAdm()){ 
//                    alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
//                }else{                      
                    rpta = fu_verificarDestinatarioInter("1");
                    nrpta = rpta.substr(0, 1);
                    if (nrpta === "1") {
                        rpta = fu_verificarReferencia();
                        nrpta = rpta.substr(0, 1);                    
                        if(nrpta === "1"){
                            rpta = fu_verificarEmpVoBo();//add vobo
                            nrpta = rpta.substr(0, 1);            
                            if(nrpta === "1"){
                                fn_changeToDespachoDocEmiAdmInter();//cambiar a Despacho                                           
                            }else{
                                alert_Info("Emisión :", rpta);
                            }                        
                        }else{
                            alert_Info("Emisión :", rpta);
                        }
                    } else {
                        alert_Info("Emisión :", rpta);
                    }
//                }
            } else if (pEstadoDoc === "0") {//Emitir
                rpta = fn_validarEstadoDocEmiAdm(pEstadoDoc);
                if (rpta === "1") {
                    fn_changeToEmitidoDocEmiAdmInter();//cambiar a Emitido
                }
            }                
        }
    }
    return false;
}

function fn_changeToDespachoDocEmiAdmInter() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val("");
    if (!!pnuAnn && !!pnuEmi) {
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=4";
        p[4] = "inFor=1";
        ajaxCall("/srDocObjetoInter.do", p.join("&"), function(dataRuta) {
            var retval = "ERROR";
            var docs;
            eval("docs=" + dataRuta);
            if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined') {
                if (docs.retval === "OK"){
                    //Para el caso de Proveido
                    if (!!docs.coDoc && (docs.coDoc === "232" || docs.coDoc === "304")) {
                        retval = "OK";
                        grabarCambioADespachoInter(retval);
                        return;
                        
                    } else {
                        if (!!docs.noDoc) {
                            var param={rutaDoc:docs.noDoc};
//                            runApplet(appletsTramiteDoc.verificaSiExisteDoc,param,function(data){
                              runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                                retval = data;
                                if (retval === "SI") {
                                    //var appletObj = jQuery('#firmarDocumento');
                                    try {
                                        var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
                                        runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento,param,function(data){
                                            retval = data;
                                            /*if (retval !== "ERROR" && retval !== "NO") {
                                                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(retval);
                                                retval = "OK";
                                            }*/
                                            if(retval.error==="0"){
                                                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(retval.message);
                                                retval = "OK";
                                            }
                                            grabarCambioADespachoInter(retval);
                                            return;
                                        });
                                        
                                    } catch (ex) {
                                        alert_Danger("Para Despacho!: ", "Fallo en subir documento al servidor");
                                        retval = "ERROR";
                                    }
                                } else {
                                    if (!!docs.inFor && docs.inFor === "PDF") {
                                        retval = "OK";
                                    } else {
                                        alert_Danger("Para Despacho!: ", "El Documento tiene que estar en PDF");
                                        retval = "ERROR";
                                    }
                                    grabarCambioADespachoInter(retval);
                                    return;                                    
                                }
                                
                            });
                        }
                        
                    }
                } else {
                    alert_Danger("Para Despacho!: ", docs.retval);
                    retval = "ERROR";
                }
            }

        }, 'text', false, false, "POST");

    }   
}

function grabarCambioADespachoInter(retval){
    if (retval === "OK") {
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goChangeToDespacho", $('#documentoEmiBean').serialize(), function(data) {
            fn_rptaChangeToDespachoDocEmiAdmInter(data);
        },
                'json', false, false, "POST");
    }
}

function fn_rptaChangeToDespachoDocEmiAdmInter(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("7");
            fn_seteaCamposDocumentoEmiInter();//resetear variables del documento
            fn_updateTblVoBoDocAdmInter();
            fu_cargaEdicionDocAdm("06", jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmiInter();
        } else {
            alert_Danger("Para Despacho!: ",data.deRespuesta);
        }
    }
}

function fn_updateTblVoBoDocAdmInter() {
    var p = new Array();
    p[0] = "accion=goUpdTlbVoBo";
    p[1] = "pnuAnn=" + jQuery("#nuAnn").val();
    p[2] = "pnuEmi=" + jQuery("#nuEmi").val();
    //p[3] = "pcoDependencia=" + $("#coDepEmi").val();
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        if (data !== null) {
            refreshScript("divtablaVoBoDocAdm", data);
            jQuery("#txtIndexFilaPersVoBoDocAdm").val("-1");
        } else {
            regresarEmitDocumAdm('0');
        }
    },'text', false, true, "POST");
}

function fn_changeToEmitidoDocEmiAdmInter() {
    var vnuSecFirma = jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val();
    var vnoDoc="";
    if (!!vnuSecFirma && typeof(vnuSecFirma) !== "undefined" && vnuSecFirma !== "") {
        var rutaDocFirma = jQuery("#rutaDocFirma").val();
        var vinFirma = jQuery('#inFirmaEmi').val();
        if (vinFirma===null || typeof(vinFirma) === "undefined" || vinFirma === ""){
            vinFirma = "F";
        }
        var valDoc = "NO";
        if (!!rutaDocFirma) {
            var vnoPrefijo = jQuery("#noPrefijo").val();
            if (vnoPrefijo==="[NF]"){
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 4) + vnoPrefijo+".pdf";
            }else{
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) +"N"+ vnoPrefijo+".pdf";
            }
            var param = {rutaDoc: vnoDoc};
//            runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
              runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                valDoc=data;
//                console.log(valDoc);
                if (valDoc === "SI") {
                    fn_grabarEmisionDocumentoInter(valDoc,vnoDoc);
                    return;
                }
                if (vinFirma === "N" && valDoc !== "SI") {
                    vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) + "N.pdf";
                    //valDoc = fn_verificaSiExisteDoc(vnoDoc);
                    var param = {rutaDoc: vnoDoc};
//                    runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                        valDoc = data;
                        fn_grabarEmisionDocumentoInter(valDoc, vnoDoc);
                        return;
                    });
                }else{
                    alert_Danger("Firma!", "El Documento no esta Firmado.");
                }
            });
        }
    } else {
        alert_Danger("Firma!", "Se necesita Firmar Documento.");
    }
}

function fn_grabarEmisionDocumentoInter(valDoc,vnoDoc){
    
//    if (valDoc === "SI") {
//        fn_cargaDocFirmaAppletInter(vnoDoc, function(data) {
//            var resulCarga = data;
//            console.log(resulCarga);
//            if (resulCarga !== "ERROR" && resulCarga !== "NO") {
//                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(resulCarga);
//             
//                
//                ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goChangeToEmitido", $('#documentoEmiBean').serialize(), function(data) {
//                   fn_rptaChangeToEmitidoDocEmiAdmInter(data);                                       
//                },'json', false, false, "POST");       
//                
//                //add yual      
//                //regresarEmitDocumAdm('1');
//                //$('#sEstadoDoc').val('0');
//                //changeTipoBusqEmiDocuAdm('0');
//
//                
//            }
//        });
//    } else {
//        alert_Danger("Firma!", "El Documento no esta Firmado.");
//    }
        if (valDoc === "SI") {
        fn_cargaDocFirmaAppletInter(vnoDoc, function(data) {
            var resulCarga = data;
            /*if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(resulCarga);
             
                
                ajaxCall("/srDocumentoAdmEmision.do?accion=goChangeToEmitido", $('#documentoEmiBean').serialize(), function(data) {
                   fn_rptaChangeToEmitidoDocEmiAdm(data);                                       
                },'json', false, false, "POST");       
                
                //add yual      
                //regresarEmitDocumAdm('1');
                //$('#sEstadoDoc').val('0');
                //changeTipoBusqEmiDocuAdm('0');

                
            }*/
            if(resulCarga.error==="0"){    
                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(resulCarga.message);
                ajaxCall("/srDocumentoEmisionInteroperabilidad.do?accion=goChangeToEmitido", $('#documentoEmiBean').serialize(), function(data) {
                   fn_rptaChangeToEmitidoDocEmiAdmInter(data);                                       
                },'json', false, false, "POST");
            }
        });
    } else {
        alert_Danger("Firma!", "El Documento no esta Firmado.");
    }
}
function fn_cargaDocFirmaAppletInter(pnoDoc,callback) {
    
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pnuSecFirmaEmi = jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val();
    var ptiOpe = "6";
    var resulCarga = "ERROR";
    var docs;
   /* console.log(pnuAnn);
    console.log(pnuEmi);
    console.log(pnuSecFirmaEmi);*/
    if (!!pnuAnn && !!pnuEmi && !!pnuSecFirmaEmi) {
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaFirmaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        p[4] = "nuSecFirma=" + pnuSecFirmaEmi;
        ajaxCall("/srDocObjetoInter.do", p.join("&"), function(data) {
            eval("docs=" + data);
            if (typeof(docs) !== "undefined" && typeof(docs.retval) !== 'undefined') {

                if (docs.retval === "OK") {
                    var retval = "";
                    try {
                        //console.log(docs.noUrl);
                        //console.log(docs.pnoDoc);
                        //retval = appletObj[0].cargarDocumento(docs.noUrl, pnoDoc);
                        var param = {urlDoc: docs.noUrl, rutaDoc: pnoDoc};
                        //runApplet(appletsTramiteDoc.cargarDocumento, param, function(data) {
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



function fn_rptaChangeToEmitidoDocEmiAdmInter(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("0");
            fn_seteaCamposDocumentoEmiInter();//resetear variables del documento
            fu_cargaEdicionDocAdm("06", jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmiInter();
//            ValidarMensajeria();
        } else {
            alert_Danger("Emisión!",data.deRespuesta);
        }
    }
}

function fn_eventDestInterEmiDoc() {
    fn_eventTblSeleccionGuardaIndexInter("tblDestEmiDocAdmInter", "txtIndexFilaDestInterEmiDoc");
}

function fn_eventTblSeleccionGuardaIndexInter(idTabla, idIndexFilaSelec) {
    var indexFilaClick = -1;
    $("#" + idTabla + " tbody").delegate('tr', 'click', function(e) {
        if (indexFilaClick !== -1) {
            if ($("#" + idTabla + " tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $("#" + idTabla + " tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }
        else {
            $(this).addClass('row_selected');
            indexFilaClick = $(this).index();
            $('#' + idIndexFilaSelec).val(indexFilaClick);
            //alert(indexFilaClick);
        }
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(idIndexFilaSelec).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(idIndexFilaSelec).parent()).index());
        //return false;
    });
}

function fn_changeTipoDestinatarioDocuEmiInter(){
    fn_changeTipoDestinatarioDocuInter();
}

function fn_changeTipoDestinatarioDocuInter() {

    $("#divtablaDestEmiDocAdmInter").show();
    
}


//function fn_removeReferenciaEmi(index){
//    if(index !== -1){
//        $("#tblRefEmiDocInter tbody tr:eq("+index+")").remove();
//    }    
//}

function fn_searchDocReferentblInter(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDocumentotblReferencia";
    (($(cell).parent()).parent()).children().each(function(index) {
        switch (index)
        {
            case 0:
                p[1] = "pannio=" + $(this).find('select').val();
                break;
            case 1:
                p[2] = "ptiDoc=" + $(this).find('select').val();
                break;
            case 2:
                p[3] = "ptiBusqueda=" + $(this).find('input[type=radio]:checked').val();
                break;
            case 3:
                p[4] = "pnuDoc=" + $(this).find('input[type=text]').val();
                break;
            default:
                break;
        }
    });
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaDocumentoReferencia(data);
        jQuery('#txtTblRefEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblRefEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");
}



function fn_buscaDependenciaDestEmitblInter(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaDestinatario";
    p[1] = "pdeDepen=" + fu_getValorUpperCase(($(cell).parent()).find('input[type=text]').val());
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatarioInter(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");
}

function fn_changeReferenciaCorrectaInter(pnroFila) {
    var ultimDiv = $("#tblRefEmiDocInter tbody tr:eq(" + pnroFila + ") td div.ui-state-error-text").last();
    ultimDiv.removeClass('ui-state-error-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-alert');
    ultimDivSpan.addClass('ui-icon-check');
    ultimDivSpan.attr("title", "OK");
    ultimDiv.addClass('ui-state-highlight-text');
    $("#tblRefEmiDocInter tbody tr:eq(" + pnroFila + ") td").last().html("1");
}

function fn_changeReferenciaIncorrecta(pnroFila) {
    var ultimDiv = $("#tblRefEmiDocInter tbody tr:eq(" + pnroFila + ") td div.ui-state-highlight-text").last();
    ultimDiv.removeClass('ui-state-highlight-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-check');
    ultimDivSpan.addClass('ui-icon-alert');
    ultimDivSpan.attr("title", "ERROR");
    ultimDiv.addClass('ui-state-error-text');
    $("#tblRefEmiDocInter tbody tr:eq(" + pnroFila + ") td").last().html("0");
}

function fn_addDestintarioEmiInter() {
    fn_addDestintarioEmiDocInter('tblDestEmiDocAdmInter');
}
function fn_addDestintarioEmiDocInter(idTabla) {
    if ($('#' + idTabla + ' >tbody >tr').length === 1 || $("#" + idTabla + " tbody tr td").last().html() === "1") {
        var found = true;
        $("#" + idTabla + " tbody tr").each(function(index, row) {
            if (index > 0) {
                if ($(row).find('td').last().html() !== "1") {
                    found = false;
                }
            }
        });
        if (found) {
            //var filaInsert = $("#"+idTabla+" tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#"+idTabla+" tbody");
            var indexAux = $("#" + idTabla + " tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#" + idTabla + " tbody").index();
            $("#" + idTabla + " tbody tr:eq(" + indexAux + ")").click();
            ($("#" + idTabla + " tbody tr:eq(" + indexAux + ")").children().first()).children().first().focus();
            $("[bt='true']").showBigTextBox({
                pressAceptarEvent: function(data) {
                    if (data.hasChangedText) {
                        changeAccionBDDocEmiInter(data.currentObject);
                    }
                },maxNumCar:600
            });
        } else {
           bootbox.alert("Corregir Destinatario");
        }
    } else {
       bootbox.alert("Corregir Destinatario");
    }
}

function changeAccionBDDocEmiInter(cmbChange) {
    
    if (($("#tblDestEmiDocAdmInter").parent()).parent().find("td").eq(6).html() === "BD") {
        ($("#tblDestEmiDocAdmInter").parent()).parent().find("td").eq(6).html("UPD");
    }
}


function fn_removeDestintarioEmiInter() {
    fn_removeDestintarioEmiDocInter("tblDestEmiDocAdmInter", 'txtIndexFilaDestInterEmiDoc');
}

function fn_removeDestintarioEmiDocInter( idTabla, indexFilaRemove) {
    var indexAccionBD=6;
    var pindexFilaDesEmiDoc = $('#' + indexFilaRemove).val();
    if (pindexFilaDesEmiDoc !== "-1") {
        var sAccionBD = $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td:eq(" + indexAccionBD + ")").text();
        if ($("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td").last().html() === "1") {
//            if (confirm('¿ Esta Seguro de Quitar Destinatario ?')) {
//                if (sAccionBD === "BD" || sAccionBD === "UPD") {
//                    $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
//                    $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").addClass('oculto');
//                } else {
//                    $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").remove();
//                }
//                $('#' + indexFilaRemove).val("-1");
//            } else {
//                return;
//            }
            
            bootbox.dialog({
                message: " <h5>¿ Esta Seguro de Quitar Destinatario ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-primary",
                        callback: function() {
                            if (sAccionBD === "BD" || sAccionBD === "UPD") {
                                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
                                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").addClass('oculto');
                            } else {
                                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").remove();
                            }
                            $('#' + indexFilaRemove).val("-1");
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
                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ") td").last().text("1");
                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").addClass('oculto');
            } else {
                $("#" + idTabla + " tbody tr:eq(" + pindexFilaDesEmiDoc + ")").remove();
            }
            $('#' + indexFilaRemove).val("-1");
        }
    } else {
       bootbox.alert("Seleccione Destinatario");
    }
}


function fn_buscaCategoriaDestEmitblInter(cell) {
    var p = new Array();
    p[0] = "accion=goBusquedaCategoriaDestinatario";  
    ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        $('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        $('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());      
    }, 'text', false, false, "POST");
    return false;
}

function fn_buscaEntidadDestEmitblInter(cell) {
    /*var p = new Array();
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var dato=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=idCategoria]').val();
    
    p[0] = "accion=goBuscaEntidadDestinatario";
    p[1] = "idCategoria=" + dato;
        
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");*/
    jQuery('#txtTblRefEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
    jQuery('#txtTblRefEmiColWhereButton').val(($(cell).parent()).index());
    var p = new Array();
    

    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    //var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var dato=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(0)").find('input[id=idCategoria]').val();
    
    p[0] = "accion=goBusquedaEntidadDestinatario";  
    p[1] = "idCategoria=" + dato;
 
    if (dato.trim().length>0)
    {
        ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        $('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        $('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());      
        }, 'text', false, false, "POST"); 
    }

    
    return false;
}

function fu_setDatoEntiDestEmi(cod, desc) {
    destinatarioDuplicado = true;
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var idTabla = "tblDestEmiDocAdmInter";

    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtEntidad]').val(desc);
    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=idEntidad]').val(cod);
    fu_validaInter();
    removeDomId('windowConsultaEntiDestEmi');
 }   
function fu_setDatoCateDestEmi(cod, desc) {
    destinatarioDuplicado = true;
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var idTabla = "tblDestEmiDocAdmInter";

    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCategoria]').val(desc);
    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=idCategoria]').val(cod);
    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(1)").find('input[id=txtEntidad]').val("");
    $("#"+idTabla+" tbody tr:eq(" + pfila + ") td:eq(1)").find('input[id=idEntidad]').val("");
    fu_validaInter();
    removeDomId('windowConsultaCateDestEmi');
//    var pcoTipoDoc = jQuery('#documentoEmiBean').find('#coTipDocAdm').length === 0 ? 
//                            jQuery('#documentoPersonalEmiBean').find('#coTipDocAdm').length === 0 ? 
//                                "": 
//                                jQuery('#documentoPersonalEmiBean').find('#coTipDocAdm').val() :
//                            jQuery('#documentoEmiBean').find('#coTipDocAdm').val();
//    var p = new Array();
//    p[0] = "accion=goBuscaEmpleadoLocaltblDestinatario";
//    p[1] = "pcoDepen=" + cod;
//    p[2] = "pcoTipoDoc=" + pcoTipoDoc;
//    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
//        if (data !== null && allTrim(data.coRespuesta) === "1") {
//            var arrCampo = new Array();
//            arrCampo[0] = (pcol * 1 + 1) + "=" + cod;//codDependencia
//            arrCampo[1] = "5=" + data.coEmpleado;//codEmpleado
//            var bResult = fn_validaDestinatarioIntituDuplicado(idTabla, arrCampo, true, pfila*1);
//            var esPrimero = $("#" + idTabla + " tbody tr").siblings().not('.oculto').length;
//            var bDestinOk=true;
//            p = new Array();
//            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
//                switch (index)
//                {
//                    case 2:
//                        $(this).find('input[type=text]').val(data.deLocal);
//                        p[0] = allTrim(desc);
//                        p[1] = allTrim(data.deLocal);
//                        break;
//                    case 3:
//                        $(this).text(data.coLocal);
//                        break;
//                    case 4:
//                        var deEmp=data.deEmpleado;
//                        if(deEmp!==null && deEmp!=="null" && allTrim(deEmp)!==""){
//                            $(this).find('input[type=text]').val(deEmp);
//                            p[2] = allTrim(deEmp);
//                        }else{
//                            bDestinOk=false;
//                        }
////                        $(this).find('input[type=text]').val(data.deEmpleado);
////                        p[2] = allTrim(data.deEmpleado);
//                        break;
//                    case 5:
//                        var coEmp=data.coEmpleado;
//                        if(coEmp!==null && coEmp!=="null" && allTrim(coEmp)!==""){
//                            $(this).text(coEmp);
//                        }
//                        else{
//                            bDestinOk=false;
//                        }                          
////                        $(this).text(data.coEmpleado);
//                        break;
//                    case 6:
//                        if (esPrimero === 1) {
//                            $(this).find('input[type=text]').val(data.deTramiteFirst);
//                            p[3] = allTrim(data.deTramiteFirst);
//                        } else {
//                            $(this).find('input[type=text]').val(data.deTramiteNext);
//                            p[3] = allTrim(data.deTramiteNext);
//                        }
//                        break;
//                    case 7:
//                        if (esPrimero === 1) {
//                            $(this).text(data.coTramiteFirst);
//                        } else {
//                            $(this).text(data.coTramiteNext);
//                        }
//                        break;
//                    default:
//                        break;
//                }
//            });
//            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(12)").text(p.join("$#"));
//            if ($("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(11)").text() === "BD") {
//                $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(11)").text("UPD");
//            }
//            if (bResult) {
//                destinatarioDuplicado=false;
//                if(bDestinOk){
//                    fn_changeDestinatarioCorrecto(pfila);
////                    jQuery("#"+idTabla).parents(':eq(4)').find('td').first().find('button').first().focus();
////                }else{
////                    $("#"+idTabla+" tbody tr:eq("+pfila+") td:visible").first().find('input[type=text]').first().focus();
//                }
////            }else{
////                $("#"+idTabla+" tbody tr:eq("+pfila+") td:visible").first().find('input[type=text]').first().focus();
//            }
////                $("#"+idTabla+" tbody tr:eq("+pfila+") td:eq("+pcol+")").find('input[type=text]').val(desc);
////                $("#"+idTabla+" tbody tr:eq("+pfila+") td:eq("+(pcol * 1 + 1)+")").text(cod);                     
//        } else {
//           bootbox.alert(data.deRespuesta);
//        }
//    },
//            'json', false, false, "POST");
}
function fu_validaInter(cell)
{
    
    
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    
//    console.log(cell);
    jQuery('#txtTblRefEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
    jQuery('#txtTblRefEmiColWhereButton').val(($(cell).parent()).index());
        
    
    var idCategoria=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(0)").find('input[id=idCategoria]').val();
    var idEntidad=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(1)").find('input[id=idEntidad]').val();
//    var Entidad=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(1)").find('input[id=txtEntidad]').val();
    
    var vDependencia=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(2)").find('input[id=txtDependencia]').val();
    var vDestinatario=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(3)").find('input[id=txtDestinatario]').val();
    var vCargo=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(4)").find('input[id=txtCargo]').val();
//    var vFolio=jQuery("#tblDestEmiDocAdmInter tbody tr:eq(" + pfila + ") td:eq(5)").find('input[id=txtFolio]').val();
//    
//    if(!!vFolio){
//        var vValidaNumero = fu_validaNumero(vFolio);
//        if (vValidaNumero !== "OK") {
//          // bootbox.alert("Año debe ser solo números.");
//            vFolio = "0";
//        }                
//    } 



    if (idCategoria.trim().length>0 && idEntidad.trim().length>0 && vDependencia.trim().length>0 && vDestinatario.trim().length>0 && vCargo.trim().length>0  )
    { 
//        var p = new Array();
//        p[0] = "accion=goAgregarProveedorEmi";
//        p[1] = "ruc="+idEntidad ;
//        p[2] = "descripcion="+Entidad ;
//        
//        ajaxCall("/srDocumentoEmisionInteroperabilidad.do", p.join("&"), function(data) {
//            
//            if (data !== null && allTrim(data.coRespuesta) === "1"){//data.equals("Datos guardados.")) {
              fn_changeDestinatarioCorrectoOtro("tblDestEmiDocAdmInter",pfila);  
//            }
//         
//        },'json', false, false, "POST");
    } else
    {
        fn_changeDestinatarioIncorrectoOtro("tblDestEmiDocAdmInter",pfila);
    }
   
}

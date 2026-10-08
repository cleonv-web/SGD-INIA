function fn_buscaReferenciaOrigen() {
    var p = new Array();
    p[0] = "accion=goBuscaReferenciaOrigen";
    p[1] = "pnuAnn=" + jQuery('#sCoAnnio').val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigen(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaReferenciaOrigen(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoReferenOrigen(cod, desc) {
    jQuery('#txtRefOrigen').val(desc);
    jQuery('#sRefOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
}

function fn_buscaDestinatarioEmi() {
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery('#sCoAnnio').val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmi(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaDestinatarioEmi(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoDestinatarioEmi(cod, desc) {
    jQuery('#txtDestinatario').val(desc);
    jQuery('#sDestinatario').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fn_buscaElaboradoPor() {
    var codDependencia = jQuery('#sCoDependencia').val();
    ajaxCall("/srDocumentoAdmEmision.do?accion=goBuscaElaboradoPor&pcoDep=" + codDependencia, '', function(data) {
        fn_rptaBuscaElaboradoPor(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaElaboradoPor(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}


function fn_buscaTrabDestino() {
    var codDependencia = jQuery('#sDestinatario').val();
  
    ajaxCall("/srDocumentoAdmEmision.do?accion=goBuscaTrabDestino&pcoDep=" + codDependencia, '', function(data) {
        fn_rptaBuscaTrabDestino(data);
    },
            'text', false, false, "POST");
}


function fn_rptaBuscaTrabDestino(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}



function fu_setDatoElaboradoPor(cod, desc) {
    jQuery('#txtElaboradoPor').val(desc);
    jQuery('#sElaboradoPor').val(cod);
    removeDomId('windowConsultaElaboradoPor');
}

function fu_eventoTablaEmiDocAdm() {
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
    }/*
    $("#myTableFixed tbody td").hover(
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

function changeTipoBusqEmiDocuAdm2() {
    changeTipoBusqEmiDocuAdm('1');
}

function changeTipoBusqEmiDocuAdm(tipo) {
    jQuery('#sTipoBusqueda').val(tipo);
    submitAjaxFormEmiDocAdm(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormEmiDocAdm(tipo) {
    var validaFiltro = fu_validaFiltroEmiDocAdm(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srDocumentoAdmEmision.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
            refreshScript("divTablaEmiDocumenAdm", data);
        }, 'text', false, false, "POST");
    }else if(validaFiltro === "2"){//buscar referencia
        ajaxCall("/srDocumentoAdmEmision.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoEmiBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferencia(data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fu_rptaBuscaDocumentoEnReferencia(data){
    if(data!==null){
        if(data==="0"){
          alert_Info("Buscar Referencia", "No Existe Documento.");
        }else{
          refreshScript("divTablaEmiDocumenAdm", data);  
        }
    }
}

function fu_validaFiltroEmiDocAdm(tipo) {
    var valRetorno = "1";
    jQuery('#sFeEmiIni').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#sFeEmiFin').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    //var pEsIncluyeFiltro = jQuery("esIncluyeFiltro1").is(':checked');
    var pEsIncluyeFiltro = $('#buscarDocumentoEmiBean').find('#esIncluyeFiltro1').is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroEmiDocAdmFiltrar(vFechaActual);     
    }else if(tipo==="1"){
      //verificar si se ingreso datos en los campos de busqueda de referencia
      valRetorno = fu_validarBusquedaXReferencia(tipo);  
      if(valRetorno==="1"){
        valRetorno = fu_validaFiltroEmiDocAdmBuscar();  
        if(valRetorno==="1"){
            if(pEsIncluyeFiltro){
               valRetorno = fu_validaFiltroEmiDocAdmFiltrar(vFechaActual); 
            }else{
               valRetorno = setAnnioNoIncludeFiltroEmi();
            }
        }
      }
    }    
    return valRetorno;
}

function fu_validarBusquedaXReferencia(tipo){
    var valRetorno="1";//no buscar por referencia
    if(tipo === "1"){
        var vBuscDestinatario = allTrim(jQuery('#sBuscDestinatario').val());
        var vDeTipoDocAdm = allTrim(jQuery('#sDeTipoDocAdm').val());
        var vCoAnnioBus = allTrim(jQuery('#sCoAnnioBus').val());
        var vNumDocRef = allTrim(jQuery('#sNumDocRef').val());

        if((typeof(vBuscDestinatario)!=="undefined" && vBuscDestinatario!==null && vBuscDestinatario!=="") &&
           (typeof(vDeTipoDocAdm)!=="undefined" && vDeTipoDocAdm!==null && vDeTipoDocAdm!=="") &&
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

function fu_validaFiltroEmiDocAdmFiltrar(vFechaActual){
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

function fu_validaFiltroEmiDocAdmBuscar() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoEmiBean();
    
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

function fu_cleanEmiDocAdm(tipo) {
//    if (tipo==="1") {
        jQuery("#sNumDoc").val("");
        jQuery("#sNumDocRef").val("");
        jQuery("#sBuscNroExpediente").val("");
        jQuery("#sDeAsuM").val("");
        jQuery("#sDeTipoDocAdm option[value=]").prop("selected", "selected");
        jQuery("#sParaFirmar option[value=]").prop("selected", "selected");
        jQuery("#sFirmadoPor option[value=]").prop("selected", "selected");
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
//    } else if(tipo==="0"){
        jQuery("#sEstadoDoc option[value=5]").prop("selected", "selected");
        jQuery("#sPrioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#coTema option[value=]").prop("selected", "selected");
        jQuery("#sTipoDoc option[value=]").prop("selected", "selected");
        jQuery("#sRefOrigen").val("");
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#sDestinatario").val("");
        jQuery("#txtDestinatario").val("[TODOS]");
        jQuery("#coEmpRec").val("");
        jQuery("#deEmpRec").val("[TODOS]");
        jQuery("#sElaboradoPor").val("");
        jQuery("#txtElaboradoPor").val("[TODOS]");
        jQuery("#sExpediente option[value=]").prop("selected", "selected");
        jQuery("#coEstMensajeria option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("2");//solo año
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val());
        jQuery("#sCoAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
//    }
}

function fu_cleanDocVoBo(tipo) {
    if (tipo==="1") {
        jQuery("#nuCorEmi").val("");
        jQuery("#nuDoc").val("");
        jQuery("#nroExp").val("");
        jQuery("#asunto").val("");
//        jQuery("#sNumDoc").val("");
        jQuery("#sNumDocRef").val("");
//        jQuery("#sBuscNroExpediente").val("");
//        jQuery("#sDeAsuM").val("");
//        jQuery("#sDeTipoDocAdm option[value=]").prop("selected", "selected");
//        jQuery("#sBuscEstado").val("");
//        jQuery("#sFeEmiIni").val("");
//        jQuery("#sFeEmiFin").val("");
//        jQuery("#sDeEmiReferencia").val("");
//        jQuery("#txtDepEmiteBus").val(" [TODOS]");
//        jQuery("#sBuscDestinatario").val("");
//        jQuery("#sBuscElaboraradoPor").val("");
//        jQuery("#sNumCorEmision").val("");
        jQuery("#esIncluyeFiltro").prop('checked',false);
        jQuery("#esIncluyeFiltro").attr('checked',false);
//        jQuery("#sCoAnnioBus").find('option:first').prop("selected", "selected");
    } else if(tipo==="0"){
        jQuery("#coPrioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#esDoc option[value=]").prop("selected", "selected");
        jQuery("#tipDoc option[value=]").prop("selected", "selected");
        
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#txtElaboradoPor").val("[TODOS]");
        
//        jQuery("#sEstadoDoc option[value=5]").prop("selected", "selected");
//        jQuery("#sPrioridadDoc option[value=]").prop("selected", "selected");
//        jQuery("#coTema option[value=]").prop("selected", "selected");
//        jQuery("#sTipoDoc option[value=]").prop("selected", "selected");
       
//        jQuery("#txtRefOrigen").val("[TODOS]");
//        jQuery("#sDestinatario").val("");
//        jQuery("#txtDestinatario").val("[TODOS]");
//        jQuery("#coEmpRec").val("");
//        jQuery("#deEmpRec").val("[TODOS]");
//        jQuery("#sElaboradoPor").val("");
        
//        jQuery("#sExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("2");//solo año
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val());
        jQuery("#sCoAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
    }
}

function fu_goNuevoEmisionDocAdm() {
        clearInterval($('#pIDAutoRefresh').val());
    
    var validaFiltro = "";
//var tipo = dojo.byId("sTipoControl").value;
//jQuery('#sTipoBusqueda').val("1");
    validaFiltro = "1";/*fu_validaFiltroEmiDocAdm(tipo);*/
    if (validaFiltro === "1") {
        ajaxCall("/srDocumentoAdmEmision.do?accion=goNuevoDocumentoEmi", '', function(data) {
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            refreshScript("divNewEmiDocumAdmin", data);
            fn_cargaToolBarEmi();
        }, 'text', false, false, "POST");
    }
    return false;
}

function editarDocumentoEmiClick(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo) {
     clearInterval($('#pIDAutoRefresh').val());
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        p[2] = "pexisteDoc=" + pexisteDoc;
        p[3] = "pexisteAnexo=" + pexisteAnexo;
        ajaxCall("/srDocumentoAdmEmision.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmi();
        }, 'text', false, false, "POST");
    } else {
       bootbox.alert("Seleccione una fila de la lista");
    }
}

function editarDocumentoEmi() {
        clearInterval($('#pIDAutoRefresh').val());
    
    var pnuAnn = jQuery('#txtpnuAnn').val();
    if (pnuAnn !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        p[3] = "pexisteDoc=" + jQuery('#txtpexisteDoc').val();
        p[4] = "pexisteAnexo=" + jQuery('#txtpexisteAnexo').val();
        ajaxCall("/srDocumentoAdmEmision.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmi();
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

function fn_eventEstDoc(sTipoDestEmi, sEstadoDocAdm) {
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
    jQuery('#documentoEmiBean').find('#deDocSig').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#coDepEmi').change(function() {
        fu_changeRemitenteEmiBean();
    });
    jQuery('#documentoEmiBean').find('#coLocEmi').change(function() {
        fu_changeRemitenteEmiBean();
    });
//    jQuery('#documentoEmiBean').find('#coTipDocAdm').change(function() {
//        fu_changeDocumentoEmiBean();
//    });
    jQuery('#documentoEmiBean').find('#deAsu').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#deObsDoc').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#nuDocEmi').change(function() {
	var noForm='#documentoEmiBean';
        var pnuAnn = jQuery(noForm).find('#nuAnn').val();
        var pnuEmi = jQuery(noForm).find('#nuEmi').val();
        var ptiEmi = jQuery(noForm).find('#tiEmi').val();
        var pcoTipDocAdm = jQuery(noForm).find('#coTipDocAdm').val();
        var pcoDepEmi = jQuery(noForm).find('#coDepEmi').val();
        var pnuDocEmiAnn = jQuery(noForm).find('#txtnuDocEmiAn').val();
        var pnuDocEmi = allTrim(jQuery(noForm).find('#nuDocEmi').val());
        if (!!pnuDocEmi&&!!pnuAnn&&!!pnuEmi){
            var vValidaNumero = fu_validaNumero(pnuDocEmi);
            if (vValidaNumero==="OK") {
                pnuDocEmi = replicate(pnuDocEmi, 6);
                if (pnuDocEmiAnn !== pnuDocEmi || (typeof(pnuDocEmiAnn) !== "undefined" && pnuDocEmiAnn === "")) {
                    fn_jsonVerificarNumeracionDocEmi(pnuDocEmiAnn, pnuAnn, pnuEmi, ptiEmi, pcoTipDocAdm, pcoDepEmi, pnuDocEmi);
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
    if (jQuery("#txtEsNuevoDocAdm").val() === '1') {
        jQuery("#coTipDocAdm").focus();
    }
    fn_changeTipoDestinatarioDocuEmi(sTipoDestEmi, sEstadoDocAdm);
    fu_cargaEdicionDocAdm("00", sEstadoDocAdm);
//    $('#containerTblVoBo').resizable({
//        handles: 's',
//        minHeight: 100,
//        maxHeight: 200
//    });
}

//function fn_removeReferenciaEmi(index){
//    if(index !== -1){
//        $("#tblRefEmiDocAdm tbody tr:eq("+index+")").remove();
//    }    
//}fn_searchDocReferentblPersonal
var $objFilaReferencia;
function fn_searchDocReferentbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDocumentotblReferencia";
    (($(cell).parent()).parent()).children().each(function(index) {
        switch (index)
        {
            case 0:
                p[1] = "pannio=" + $(this).find('select').val();
                var valAnio = $(this).find('select').val();
                $(this).find('select option[value="'+valAnio+'"]').attr("selected","selected");
                break;
            case 1:
                p[2] = "ptiDoc=" + $(this).find('select').val();
                var valTiDoc = $(this).find('select').val();
                $(this).find('select option[value="'+valTiDoc+'"]').attr("selected","selected");
                break;
            case 2:
                p[3] = "ptiBusqueda=" + $(this).find('input[type=radio]:checked').val();
                var valTiBusqueda = $(this).find('input[type=radio]:checked').val();
                $(this).find("input[type=radio]").removeAttr('checked');
                $(this).find("input[type=radio]").removeProp('checked');
                $(this).find("input[type=radio][value='"+valTiBusqueda+"']").attr('checked',true);
                $(this).find("input[type=radio][value='"+valTiBusqueda+"']").prop('checked',true);
                break;
            case 3:
                p[4] = "pnuDoc=" + $(this).find('input[type=text]').val();
                break;
            default:
                break;
        }
    });
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDocumentoReferencia(data);
        jQuery('#txtTblRefEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblRefEmiColWhereButton').val(($(cell).parent()).index());
        $objFilaReferencia = ($(cell).parent()).parent().clone();       
    },'text', false, false, "POST");
}

function fn_rptaBuscaDocumentoReferencia(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_seleccionarFila(objeto, event){
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


function fn_agregarReferenciasEmi(){
    var count = 0;
    var posFila = parseInt($('#txtTblRefEmiFilaWhereButton').val());
    var filaIni = posFila;
     $('#tlbDocumentoRefEmi .row_selected').each(function(i, obj) {
         var pnuAnn= $(this).find("td:eq(8)").text();
        var pnuEmi= $(this).find("td:eq(9)").text();
        var pnuDes= $(this).find("td:eq(10)").text();
        var pnuAnnExp= $(this).find("td:eq(11)").text();
        var pnuSecExp= $(this).find("td:eq(12)").text();
        var pnuExpediente= $(this).find("td:eq(13)").text();
        var pnuDoc= $(this).find("td:eq(2)").text();
        var pfeEmi= $(this).find("td:eq(3)").text();
        
        if(count===0){
            fu_setDatoDocumentoRefEmi(pnuAnn,pnuEmi,pnuDes,pnuDoc,pfeEmi,pnuAnnExp,pnuSecExp,pnuExpediente);
            
        }else{
            var maxNum='0';
            $('#tblRefEmiDocAdm tbody input[type="radio"]').each(function()  { 
                var rbGrupo = $(this).attr('name');
                var idGrupo = rbGrupo.replace('group','');            
                if(idGrupo>maxNum){
                    maxNum=idGrupo;
                }
            });
            $objFilaReferencia.find('input[type=radio]').attr('name', 'group' + (parseInt(maxNum)+1));
            $("#tblRefEmiDocAdm tbody tr:eq("+(posFila-1)+")").after("<tr>"+$objFilaReferencia.html()+"</tr>"); 
            $('#txtTblRefEmiFilaWhereButton').val(posFila);
            fu_setDatoDocumentoRefEmi(pnuAnn,pnuEmi,pnuDes,pnuDoc,pfeEmi,pnuAnnExp,pnuSecExp,pnuExpediente);
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

function fn_buscaDependenciaDestEmitbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaDestinatario";
    p[1] = "pdeDepen=" + fu_getValorUpperCase(($(cell).parent()).find('input[type=text]').val());
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");
}

function fn_changeReferenciaCorrecta(pnroFila) {
    var ultimDiv = $("#tblRefEmiDocAdm tbody tr:eq(" + pnroFila + ") td div.ui-state-error-text").last();
    ultimDiv.removeClass('ui-state-error-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-alert');
    ultimDivSpan.addClass('ui-icon-check');
    ultimDivSpan.attr("title", "OK");
    ultimDiv.addClass('ui-state-highlight-text');
    $("#tblRefEmiDocAdm tbody tr:eq(" + pnroFila + ") td").last().html("1");
}

function fn_changeReferenciaIncorrecta(pnroFila) {
    var ultimDiv = $("#tblRefEmiDocAdm tbody tr:eq(" + pnroFila + ") td div.ui-state-highlight-text").last();
    ultimDiv.removeClass('ui-state-highlight-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-check');
    ultimDivSpan.addClass('ui-icon-alert');
    ultimDivSpan.attr("title", "ERROR");
    ultimDiv.addClass('ui-state-error-text');
    $("#tblRefEmiDocAdm tbody tr:eq(" + pnroFila + ") td").last().html("0");
}

function fn_addDestintarioEmiDoc(idTabla) {
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
                        changeAccionBDDocEmi(data.currentObject);
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

function fn_addDestintarioEmi() {
    fn_addDestintarioEmiDoc('tblDestEmiDocAdm');
    //YUAL
    if($('#cmbPrdAllDestino').val()>1){
    fn_setPrioridadFilaTblDestIntitu('tblDestEmiDocAdm', $('#cmbPrdAllDestino').val())
    }
}

function fn_addDestintarioEmiOtro() {
    fn_addDestintarioEmiDoc('tblDestEmiDocAdmOtro');
}

function fn_addDestintarioEmiCiudadano() {
    fn_addDestintarioEmiDoc('tblDestEmiDocAdmCiudadano');
}

function fn_addDestintarioEmiPersJuri() {
    fn_addDestintarioEmiDoc('tblDestEmiDocAdmPersJuri');
}

function fn_addReferenciaEmi() {
    if ($('#tblRefEmiDocAdm >tbody >tr').length === 1 || $("#tblRefEmiDocAdm tbody tr td").last().html() === "1") {
        var found = true;
        $("#tblRefEmiDocAdm tbody tr").each(function(index, row) {
            if (index > 0) {
                if ($(row).find('td').last().html() !== "1") {
                    found = false;
                }
            }
        });
        if (found) {
            var indexAux = $("#tblRefEmiDocAdm tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#tblRefEmiDocAdm tbody").index();
            $("#tblRefEmiDocAdm tbody tr:eq(" + indexAux + ")").click();
            ($("#tblRefEmiDocAdm tbody tr:eq(" + indexAux + ")").children().first()).children().first().focus();
            var numFilaRefEmi = $("#tblRefEmiDocAdm tbody tr").length - 1;
            $("#tblRefEmiDocAdm tbody tr").last().each(function() {
                $(this).find('input[type=radio]').attr('name', 'group' + numFilaRefEmi);
                $(this).find('input[type=radio]').last().prop('checked', 'true');
            });
        } else {
           bootbox.alert("Corregir Referencia");
        }
    } else {
       bootbox.alert("Corregir Referencia");
    }
}

function fn_removeDestintarioEmiDoc(indexAccionBD, idTabla, indexFilaRemove) {
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

function fn_removeDestintarioEmi() {
    fn_removeDestintarioEmiDoc("11", "tblDestEmiDocAdm", 'txtIndexFilaDestEmiDoc');
}

function fn_removeDestintarioEmiOtro() {
    fn_removeDestintarioEmiDoc("4", "tblDestEmiDocAdmOtro", 'txtIndexFilaDestOtroEmiDoc');
}

function fn_removeDestintarioEmiCiudadano() {
    fn_removeDestintarioEmiDoc("3", "tblDestEmiDocAdmCiudadano", 'txtIndexFilaDestCiudadanoEmiDoc');
}

function fn_removeDestintarioEmiPersJuri() {
    fn_removeDestintarioEmiDoc("3", "tblDestEmiDocAdmPersJuri", 'txtIndexFilaDestPersJuriEmiDoc');
}

function fn_removeReferenciaEmi() {
    var indexAccionBD = "10";
    var pindexFilaRefEmiDoc = $('#txtIndexFilaRefEmiDoc').val();
    if (pindexFilaRefEmiDoc !== "-1") {
        var sAccionBD = $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td:eq(" + indexAccionBD + ")").text();
        if ($("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td").last().html() === "1") {
//            if (confirm('¿ Esta Seguro de Quitar Referencia ?')) {
//                if (sAccionBD === "BD" || sAccionBD === "UPD") {
//                    $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ") td:eq(" + indexAccionBD + ")").text("DEL");
//                    $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").addClass('oculto');
//                } else {
//                    $("#tblRefEmiDocAdm tbody tr:eq(" + pindexFilaRefEmiDoc + ")").remove();
//                }
//                $('#txtIndexFilaRefEmiDoc').val("-1");
//            } else {
//                return;
//            }
            
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

function fn_changeDestinatarioCorrecto(pnroFila) {
    var ultimDiv = $("#tblDestEmiDocAdm tbody tr:eq(" + pnroFila + ") td div.ui-state-error-text").last();
    ultimDiv.removeClass('ui-state-error-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-alert');
    ultimDivSpan.addClass('ui-icon-check');
    ultimDivSpan.attr("title", "OK");
    ultimDiv.addClass('ui-state-highlight-text');
    $("#tblDestEmiDocAdm tbody tr:eq(" + pnroFila + ") td").last().html("1");
}

function fn_changeDestinatarioIncorrecto(pnroFila) {
    var ultimDiv = $("#tblDestEmiDocAdm tbody tr:eq(" + pnroFila + ") td div.ui-state-highlight-text").last();
    ultimDiv.removeClass('ui-state-highlight-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-check');
    ultimDivSpan.addClass('ui-icon-alert');
    ultimDivSpan.attr("title", "ERROR");
    ultimDiv.addClass('ui-state-error-text');
    $("#tblDestEmiDocAdm tbody tr:eq(" + pnroFila + ") td").last().html("0");
}

function fn_eventTblSeleccionGuardaIndex(idTabla, idIndexFilaSelec) {
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
        //return false;
    });
}

function fn_eventDestEmiDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblDestEmiDocAdm", "txtIndexFilaDestEmiDoc");
}

function fn_eventRefEmiDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblRefEmiDocAdm", "txtIndexFilaRefEmiDoc");
}

function fn_eventDestOtroEmiDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblDestEmiDocAdmOtro", "txtIndexFilaDestOtroEmiDoc");
}

function fn_eventDestCiudadanoEmiDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblDestEmiDocAdmCiudadano", "txtIndexFilaDestCiudadanoEmiDoc");
}

function fn_eventDestPersJuriEmiDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblDestEmiDocAdmPersJuri", "txtIndexFilaDestPersJuriEmiDoc");
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
function fn_tblRefEmihtml2json(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
    var json = '[';
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
                            } else if (typeof($(this).find('select').val()) !== "undefined") {
                                valCampoBean = $(this).find('select').val();
                            } else if (typeof($(this).find('input[type=radio]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[type=radio]:checked').val();
                            } else {
                                valCampoBean = $(this).text();
                            }
                            itArr.push('"' + campoBean + '":"' + valCampoBean + '"');
                        }
                    }
                });
                otArr.push('{' + itArr.join(',') + '}');
            }
        }
        count++;
    });
    json += otArr.join(",") + ']';
    return json;
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
function fn_tblDestEmihtml2json(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
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

function fn_tblRefEmiDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=11";
    arrColMostrar[1] = "nuAnn=8";
    arrColMostrar[2] = "nuEmi=9";
    arrColMostrar[3] = "nuDes=10";
    arrColMostrar[4] = "coRef=12";
    arrColMostrar[5] = "coTipDoc=2";
    arrColMostrar[6] = "esDocEmi=3";
    arrColMostrar[7] = "nuDoc=4";
    arrColMostrar[8] = "fechaEmi=5";
    return fn_tblRefEmihtml2json('tblRefEmiDocAdm', 1, arrColMostrar, 14, "1", 11, "BD");
}


function fn_tblDestEmiDocAdmToJson() {
    var json = '[';
    var itArr = [];
    var tbl1 = fn_tblDestIntituEmiDocAdmToJson();
    tbl1 !== "" ? itArr.push(tbl1) : "";
    var tbl2 = fn_tblDestProveedorEmiDocAdmToJson();
    tbl2 !== "" ? itArr.push(tbl2) : "";
    var tbl3 = fn_tblDestCiudadanoEmiDocAdmToJson();
    tbl3 !== "" ? itArr.push(tbl3) : "";
    var tbl4 = fn_tblDestOtroEmiDocAdmToJson();
    tbl4 !== "" ? itArr.push(tbl4) : "";
    json += itArr.join(",") + ']';
    return json;
}

function fn_tblDestIntituEmiDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=12";
    arrColMostrar[1] = "nuDes=10";
    arrColMostrar[2] = "coDependencia=2";
    arrColMostrar[3] = "coLocal=4";
    arrColMostrar[4] = "coEmpleado=6";
    arrColMostrar[5] = "coTramite=8";
    arrColMostrar[6] = "deIndicaciones=9";
    arrColMostrar[7] = "coPrioridad=11";
    arrColMostrar[8] = "coTipoDestino=14";
    return fn_tblDestEmihtml2json('tblDestEmiDocAdm', 1, arrColMostrar, 16, "1", 12, "BD");
}

function fn_tblDestOtroEmiDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=5";
    arrColMostrar[1] = "coTipoDestino=7";
    arrColMostrar[2] = "nuDes=8";
    arrColMostrar[3] = "coOtroOrigen=2";
    arrColMostrar[4] = "coPrioridad=17";
    arrColMostrar[5] = "envMesaPartes=16";
    
    arrColMostrar[6] = "ccodDpto=11";
    arrColMostrar[7] = "cdirRemite=12";
    arrColMostrar[8] = "cexpCorreoe=13";
    arrColMostrar[9] = "cTelefono=14";    
    arrColMostrar[10] = "cargo=15";    
    return fn_tblDestEmihtml2json('tblDestEmiDocAdmOtro', 1, arrColMostrar, 19, "1", 5, "BD");
}

function fn_tblDestCiudadanoEmiDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=4";
    arrColMostrar[1] = "coTipoDestino=6";
    arrColMostrar[2] = "nuDes=2";
    arrColMostrar[3] = "nuDni=1";
    arrColMostrar[4] = "coPrioridad=14";
    arrColMostrar[5] = "envMesaPartes=13";
    
    arrColMostrar[6] = "ccodDpto=9";
    arrColMostrar[7] = "cdirRemite=10";
    arrColMostrar[8] = "cexpCorreoe=11";
    arrColMostrar[9] = "cTelefono=12";    
    return fn_tblDestEmihtml2json('tblDestEmiDocAdmCiudadano', 1, arrColMostrar, 16, "1", 4, "BD");
}

function fn_tblDestProveedorEmiDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=4";
    arrColMostrar[1] = "coTipoDestino=6";
    arrColMostrar[2] = "nuDes=2";
    arrColMostrar[3] = "nuRuc=3";
    arrColMostrar[4] = "coPrioridad=17";
    arrColMostrar[5] = "envMesaPartes=16";
    arrColMostrar[6] = "cdirRemite=11";
    arrColMostrar[7] = "cexpCorreoe=12";
    arrColMostrar[8] = "cTelefono=13";
    arrColMostrar[9] = "ccodDpto=10";
    arrColMostrar[10] = "remiNuDniEmi=14";
    arrColMostrar[11] = "remiTiEmi=14";
    arrColMostrar[12] = "cargo=15";
    return fn_tblDestEmihtml2Proveedorjson('tblDestEmiDocAdmPersJuri', 1, arrColMostrar, 19, "1", 4, "BD");
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
function fn_tblDestEmihtml2Proveedorjson(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
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
                            if (typeof($(this).find('input[id=txtCodigoRemitente]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[id=txtCodigoRemitente]').val();                                  
                            }
                            if (typeof($(this).find('input[id=txtCodigoUbigeoOtro]').val()) !== "undefined") {
                                valCampoBean = $(this).find('input[id=txtCodigoUbigeoOtro]').val();                                  
                            }
                            if (campoBean == "remiTiEmi") {
                                valCampoBean = $(this).find('select').val();
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

//function changeEstadoPrioridad(cmbChange){
//    ($(cmbChange).parent()).parent().find("td").eq(11).html($(cmbChange).val());
//}

function changeAccionBDDocEmi(cmbChange) {
    if (($(cmbChange).parent()).parent().find("td").eq(11).html() === "BD") {
        ($(cmbChange).parent()).parent().find("td").eq(11).html("UPD");
    }
}

function changeAccionBDDocEmiCiudadano(cmbChange) {
    if (($(cmbChange).parent()).parent().find("td").eq(4).html() === "BD") {
        ($(cmbChange).parent()).parent().find("td").eq(4).html("UPD");
    }
}

function fn_buscaPersonalDestEmitbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaPersonalDestinatario";
    p[1] = "pcoDepen=" + (($(cell).parent()).parent()).children().get(1).innerHTML;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");
}

function fn_buscaAccionTipDocDestEmitbl(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaAccionDestinatario";
    p[1] = "pcoTipDoc=" + jQuery("#coTipDocAdm").val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaDestinatario(data);
        jQuery('#txtTblDestEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        jQuery('#txtTblDestEmiColWhereButton').val(($(cell).parent()).index());
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaDependenciaDestinatario(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_verificarCreacionExpediente() {
    if (jQuery("#nuAnnExp").val() === "" && jQuery("#nuSecExp").val() === "" && jQuery("#esDocEmi").val() === "5" && jQuery("#txtEsNuevoDocAdm").val() === '1') {
        bootbox.confirm({
        message: "¿Desea Crear Expediente?",
        buttons: {
            confirm: {
                label: 'Aceptar'
            },
            cancel: {
                label: 'Cancelar'
            }
        },
        callback: function (result) {
                if (result) {
                    fn_grabaDocumEmiAdmin("1");
                } else {
                    fn_grabaDocumEmiAdmin("0");
                }
            }
        });
    } else {
        fn_grabaDocumEmiAdmin("0");
    }
}

function fn_beforeGrabarDocumEmiAdmin(vchangeEsDocEmi) {
    var coTipDocAdm = jQuery('#coTipDocAdm').val();
    var deAsu = jQuery('#deAsu').val();
    if (coTipDocAdm !== "-1") {
        if (deAsu !== "") {
            var validaFiltro = fu_validaFiltroGrabaDocEmiAdm();
            if (validaFiltro === "1") {
                var rpta = fn_validarToDespacho("0");
                var nrpta = rpta.substr(0, 1);
                if (nrpta === "1" || nrpta === "E") {
                     bootbox.confirm({
                        message: "¿Seguro de Guardar los Cambios?",
                        buttons: {
                            confirm: {
                                label: 'Aceptar'
                            },
                            cancel: {
                                label: 'Cancelar'
                            }
                        },
                        callback: function (result) {
                            if (result) {
                                fn_changeEstadoDocumentoEmiAdm(vchangeEsDocEmi);
                            } else {
                                return false;
                            }
                        }
                    });
                } else {
                   bootbox.alert(rpta);
                }
            }
        } else {
           bootbox.alert("EL ASUNTO NO DEBE SER VACIO!!");
            jQuery("#deAsu").focus();
        }
    } else {
       bootbox.alert("EL TIPO DE DOCUMENTO NO DEBE SER VACIO!!");
        jQuery("#coTipDocAdm").focus();
    }
}

function fn_grabaDocumEmiAdmin(sCrearExpediente) {
    $('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmi();
    //console.log(cadenaJson);
    var rpta = fn_validarEnvioGrabaDocEmiAdm(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        ajaxCallSendJson("/srDocumentoAdmEmision.do?accion=goGrabaDocumentoEmi&pcrearExpediente=" + sCrearExpediente, cadenaJson, function(data) {
            fn_rptaGrabaDocumEmiAdmin(data, sCrearExpediente);
        },
                'json', false, false, "POST");
    } else {
        //alert(rpta.substr(1));
        alert_Info("", rpta.substr(1));
    }
}

function fu_validaFiltroGrabaDocEmiAdm() {
    var pnuDiaAte = jQuery("#nuDiaAte").val();
    var pnuAnn = jQuery("#nuAnn").val();
    jQuery("#deDocSig").val(allTrim(fu_getValorUpperCase(jQuery("#deDocSig").val())));
    var pdeDocSig = jQuery("#deDocSig").val();
    var pfeEmiCorta = jQuery("#feEmiCorta").val();
    var pfeHoraActual = jQuery("#txtfechaHoraActual").val();
    var pfeActual = pfeHoraActual.substr(0, 10);


    var valRetorno = "1";
    var vValidaNumero = "";

    if (pnuDiaAte !== null && pnuDiaAte !== "") {
        vValidaNumero = fu_validaNumero(pnuDiaAte);
        if (vValidaNumero !== "OK") {
           bootbox.alert("Días de Atención debe ser solo números.");
            jQuery("#nuDiaAte").focus();
            valRetorno = "0";
        }
    } else {
       bootbox.alert("Especifique días de Atención.");
        jQuery("#nuDiaAte").focus();
        valRetorno = "0";
    }

    if (pnuAnn !== null && pnuAnn !== "") {
        vValidaNumero = fu_validaNumero(pnuAnn);
        if (vValidaNumero !== "OK") {
           bootbox.alert("Año de Documento solo números.");
            valRetorno = "0";
        }
    } else {
       bootbox.alert("Especifique Año de Documento.");
        valRetorno = "0";
    }

    if (pdeDocSig !== null && pdeDocSig !== "") {
        var rptValida = validaCaracteres(pdeDocSig, "1");
        if (rptValida !== "OK") {
           bootbox.alert(rptValida);
            jQuery("#deDocSig").focus();
            valRetorno = "0";
        }
    } else {
       bootbox.alert("Especifique siglas del Documento");
        jQuery("#deDocSig").focus();
        valRetorno = "0";
    }

    if (valRetorno === "1") {
        //VALIDA FECHAS
        if (pfeEmiCorta === "") {
           bootbox.alert('Especifique Fecha de Documento.');
            valRetorno = "0";
            //valRetorno="1";
        } else {
            valRetorno = fu_validaFechaConsulta(pfeEmiCorta, pfeActual);
            if (valRetorno !== "1") {
               bootbox.alert("Error Fecha De Documento" + valRetorno);
                valRetorno = "0";
            }
        }
    }
    //se verifica que diferencia de fechas sea maximo de 5 dias
    /*if (valRetorno=="1") {
     vCantidadDias =  getNumeroDeDiasDiferencia(vFeInicio,vFeFinal);
     if (vCantidadDias > 5 || vCantidadDias < 0){
    bootbox.alert("Diferencia entre fechas debe ser maximo de 5 dias");
     valRetorno="0";
     }
     }*/
    return valRetorno;
}

function fn_rptaGrabaDocumEmiAdmin(data, sCrearExpediente) {
      
    var strCadena = '';
    if (data !== null) {
               //YUAL

        if($('#bFirmaProveido').val()=='1' && !!data.nuEmi && data.nuEmi!==null && data.nuEmi!=='null' ){
                jQuery('#txtNuEmiProveido').val(data.nuEmi);
                if($('#bFirmaProveido').val()=='1')
                 {fn_firmarDocumentoEmiProveido();}
       }   
   
        
        if (data.coRespuesta === "1") {
            if (jQuery('#txtEsNuevoDocAdm').val() === "1") {
                jQuery('#txtEsNuevoDocAdm').val("0");
               
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
            fn_seteaCamposDocumentoEmi();            //resetear variables del documento
            fn_setTblPersonalVoBo();//setter tabla personal VB.
            fu_cargaEdicionDocAdm("00", jQuery('#esDocEmi').val());
            //alert("Datos Guardados.");
            alert_Sucess("Éxito!", "Documento grabado correctamente "+strCadena);
        } else {
            alert_Info("Emisión :", data.deRespuesta);
        }
        
      
    }
}

function fu_setDatoDependenciaDestEmi(cod, desc) {
    destinatarioDuplicado = true;
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var idTabla = "tblDestEmiDocAdm";
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(desc);
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(cod);
    removeDomId('windowConsultaDependenciaDestEmi');
    var pcoTipoDoc = jQuery('#documentoEmiBean').find('#coTipDocAdm').length === 0 ? 
                            jQuery('#documentoPersonalEmiBean').find('#coTipDocAdm').length === 0 ? 
                                "": 
                                jQuery('#documentoPersonalEmiBean').find('#coTipDocAdm').val() :
                            jQuery('#documentoEmiBean').find('#coTipDocAdm').val();
    var p = new Array();
    p[0] = "accion=goBuscaEmpleadoLocaltblDestinatario";
    p[1] = "pcoDepen=" + cod;
    p[2] = "pcoTipoDoc=" + pcoTipoDoc;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null && allTrim(data.coRespuesta) === "1") {
            var arrCampo = new Array();
            arrCampo[0] = (pcol * 1 + 1) + "=" + cod;//codDependencia
            arrCampo[1] = "5=" + data.coEmpleado;//codEmpleado
            var bResult = fn_validaDestinatarioIntituDuplicado(idTabla, arrCampo, true, pfila*1);
            var esPrimero = $("#" + idTabla + " tbody tr").siblings().not('.oculto').length;
            var bDestinOk=true;
            p = new Array();
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
                switch (index)
                {
                    case 2:
                        $(this).find('input[type=text]').val(data.deLocal);
                        p[0] = allTrim(desc);
                        p[1] = allTrim(data.deLocal);
                        break;
                    case 3:
                        $(this).text(data.coLocal);
                        break;
                    case 4:
                        var deEmp=data.deEmpleado;
                        if(deEmp!==null && deEmp!=="null" && allTrim(deEmp)!==""){
                            $(this).find('input[type=text]').val(deEmp);
                            p[2] = allTrim(deEmp);
                        }else{
                            bDestinOk=false;
                        }
//                        $(this).find('input[type=text]').val(data.deEmpleado);
//                        p[2] = allTrim(data.deEmpleado);
                        break;
                    case 5:
                        var coEmp=data.coEmpleado;
                        if(coEmp!==null && coEmp!=="null" && allTrim(coEmp)!==""){
                            $(this).text(coEmp);
                        }
                        else{
                            bDestinOk=false;
                        }                          
//                        $(this).text(data.coEmpleado);
                        break;
                    case 6:
                        if (esPrimero === 1) {
                            $(this).find('input[type=text]').val(data.deTramiteFirst);
                            p[3] = allTrim(data.deTramiteFirst);
                        } else {
                            $(this).find('input[type=text]').val(data.deTramiteNext);
                            p[3] = allTrim(data.deTramiteNext);
                        }
                        break;
                    case 7:
                        if (esPrimero === 1) {
                            $(this).text(data.coTramiteFirst);
                        } else {
                            $(this).text(data.coTramiteNext);
                        }
                        break;
                    default:
                        break;
                }
            });
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(12)").text(p.join("$#"));
            if ($("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(11)").text() === "BD") {
                $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(11)").text("UPD");
            }
            if (bResult) {
                destinatarioDuplicado=false;
                if(bDestinOk){
                    fn_changeDestinatarioCorrecto(pfila);
//                    jQuery("#"+idTabla).parents(':eq(4)').find('td').first().find('button').first().focus();
//                }else{
//                    $("#"+idTabla+" tbody tr:eq("+pfila+") td:visible").first().find('input[type=text]').first().focus();
                }
//            }else{
//                $("#"+idTabla+" tbody tr:eq("+pfila+") td:visible").first().find('input[type=text]').first().focus();
            }
//                $("#"+idTabla+" tbody tr:eq("+pfila+") td:eq("+pcol+")").find('input[type=text]').val(desc);
//                $("#"+idTabla+" tbody tr:eq("+pfila+") td:eq("+(pcol * 1 + 1)+")").text(cod);                     
        } else {
           bootbox.alert(data.deRespuesta);
        }
    },
            'json', false, false, "POST");
}

function fu_cambioTxtEmiDoc(input) {
    var arrCampo = new Array();
    arrCampo[0] = "1=" + $("#tblDestEmiDocAdm tbody tr:eq(" + (($(input).parent()).parent()).index() + ") td:eq(1)").text();//codDependencia
    arrCampo[1] = "5=" + $("#tblDestEmiDocAdm tbody tr:eq(" + (($(input).parent()).parent()).index() + ") td:eq(5)").text();//codEmpleado
    var bResult = fn_validaDestinatarioIntituDuplicado('tblDestEmiDocAdm', arrCampo, true, (($(input).parent()).parent()).index());
    var countFound = fn_evalDestinatarioCorrectotblEmi((($(input).parent()).parent()).index());
    if (countFound === 4 && bResult) {
        fn_changeDestinatarioCorrecto((($(input).parent()).parent()).index());
    } else {
        fn_changeDestinatarioIncorrecto((($(input).parent()).parent()).index());
    }
}

function fu_cambioTxtTblRefEmiDoc(input) {
    fn_evalReferenciaCorrectatblEmi((($(input).parent()).parent()).index());
}

function fu_setEmpleadoDestinoEmitlb(cod, desc) {
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var arrCampo = new Array();
    arrCampo[0] = (pcol * 1 - 3) + "=" + $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 3) + ")").text();//codDependencia
    arrCampo[1] = "5=" + cod;//codEmpleado
//    var bResult = fn_validaDestinatarioIntituDuplicado('tblDestEmiDocAdm', arrCampo, false, '');
    var bResult = fn_validaDestinatarioIntituDuplicado('tblDestEmiDocAdm', arrCampo, true, pfila*1);
    if (bResult) {
        destinatarioDuplicado=false;
        fu_setArrAsignaDestinOK(2, cod, desc);
        removeDomId('windowConsultaEmpl');
    } else {
        destinatarioDuplicado=true;
        removeDomId('windowConsultaEmpl');
       bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
    }
}

function fu_setTramiteDestinoEmitlb(cod, desc) {
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    var arrCampo = new Array();
    arrCampo[0] = (pcol * 1 - 5) + "=" + $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 5) + ")").text();//codDependencia
    arrCampo[1] = "5=" + $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(5)").text();//codEmpleado
//    var bResult = fn_validaDestinatarioIntituDuplicado('tblDestEmiDocAdm', arrCampo, false, '');
    var bResult = fn_validaDestinatarioIntituDuplicado('tblDestEmiDocAdm', arrCampo, true, pfila*1);
    if (bResult) {
        destinatarioDuplicado=false;
    } else {
        destinatarioDuplicado=true;
    }    
    fu_setArrAsignaDestinOK(3, cod, desc);
    removeDomId('windowConsultaMoti');
}

function fu_setArrAsignaDestinOK(index, cod, desc) {
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(desc);
    $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(cod);
    var arrInputAnt = ($("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ")").children().get(12).innerHTML).split("$#");
    for (var i = 0; i < arrInputAnt.length; i++) {
        if (i === index) {
            arrInputAnt[i] = desc;
        }
    }
    $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(12)").text(arrInputAnt.join("$#"));
    if ($("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(11)").text() === "BD") {
        $("#tblDestEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(11)").text("UPD");
    }
    var countFound = fn_evalDestinatarioCorrectotblEmi(pfila);
    if (countFound === 4 && !destinatarioDuplicado) {
        fn_changeDestinatarioCorrecto(pfila);
    } else {
        fn_changeDestinatarioIncorrecto(pfila);
    }
}

function fn_evalDestinatarioCorrectotblEmi(indexFila) {
    var arrInputAnt = ($("#tblDestEmiDocAdm tbody tr:eq(" + indexFila + ")").children().get(12).innerHTML).split("$#");
    var countFound = 0;
    //var arrIndexCompara=[0,2,4,6];
    for (var i = 0; i < arrInputAnt.length; i++)
    {
        var found=true;
        $("#tblDestEmiDocAdm tbody tr:eq(" + indexFila + ") td").each(function(index) {
            if (index in {0: 1, 2: 1, 4: 1, 6: 1}) {
                var valInput = allTrim(fu_getValorUpperCase($(this).find('input[type=text]').val()));
                if (arrInputAnt[i] === valInput) {
                    if(found){
                        found=false;                    
                        countFound++;
                    }
                }
            }
        });
    }
    return countFound;
//    if(countFound === 4){
//        fn_changeDestinatarioCorrecto(indexFila);
//    }else{
//        fn_changeDestinatarioIncorrecto(indexFila);	    
//    }
}

function fn_evalReferenciaCorrectatblEmi(indexFila) {
    var arrInputAnt = ($("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ")").children().get(6).innerHTML).split("$#");
    var countFound = 0;
    for (var i = 0; i < arrInputAnt.length; i++)
    {
        $("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ") td").each(function(index) {
            if (index in {0: 1, 1: 1, 2: 1, 3: 1, 4: 1}) {
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
    if (countFound === 5) {
        fn_changeReferenciaCorrecta(indexFila);
    } else {
        fn_changeReferenciaIncorrecta(indexFila);
    }
}

function fn_buildSendJsontoServerDocuEmi() {
    var result = "{";
    result = result + '"nuAnn":"' + $("#nuAnn").val() + '",';
    result = result + '"nuEmi":"' + $("#nuEmi").val() + '",';
    //result = result + '"documentoEmiBean":' + JSON.stringify($('#documentoEmiBean').serializeFormJSON()) + ',';
    var valEnvio = jQuery('#envDocumentoEmiBean').val();
    if (valEnvio === "1") {
        result = result + '"documentoEmiBean":' + JSON.stringify(getJsonFormDocumentoEmiBean()) + ',';
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
    result = result + '"lstDestinatario":' + sortDelFirst(fn_tblDestEmiDocAdmToJson()) + ',';
    result = result + '"lstEmpVoBo":' + sortDelFirst(fn_tblEmpVoBoDocAdmToJson());//add vobo
    return result + "}";
}

function sortDelFirst(lsDestinoJson){
    var vResult='[]';
    var lsObjDestinos=new Function('return ' + lsDestinoJson)();
    if(!!lsObjDestinos){
        var lsObjDestinosAux=[];
        var lsDestinoDel=[];
        lsObjDestinos.forEach(function(objDes,index) {
            if(!!objDes){
                var accion=objDes.accionBD;
                if(!!accion&&accion==="DEL"){
                    //lsObjDestinos.splice(index,1);
                    lsDestinoDel.push(objDes);
                }else{
                   lsObjDestinosAux.push(objDes);
                }
            }
        });
        vResult=JSON.stringify(lsDestinoDel.concat(lsObjDestinosAux));
    }
    return vResult;
}


(function($) {
    $.fn.serializeFormJSON = function() {
        var o = {};
        var a = this.serializeArray();
        var count = 0;
        $.each(a, function() {
            if (count < 14) {
                if (o[this.name]) {
                    if (!o[this.name].push) {
                        o[this.name] = [o[this.name]];
                    }
                    o[this.name].push(this.value || '');
                } else {
                    o[this.name] = this.value || '';
                }
                count++;
            }
        });
        return o;
    };
})(jQuery);

function fu_setDatoDocumentoRefEmi(pnuAnn, pnuEmi, pnuDes, pnuDoc, pfeEmi, pnuAnnExp, pnuSecExp, pnuExpediente) {
    var pfila = jQuery('#txtTblRefEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblRefEmiColWhereButton').val();
    //verificar si ya esta referenciado el documento
    var sResult = fn_validaDocumentoRefLista(pfila*1,pcol, pnuAnn, pnuEmi, pnuDes);
    if (sResult) {
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 1) + ")").find('input[type=text]').val(pfeEmi);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 - 2) + ")").find('input[type=text]').val(pnuDoc);
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
                case 2:
                    p[2] = $(this).find('input[type=radio]:checked').val();
                    break;
                default:
                    break;
            }
        });
        p[3] = pnuDoc;
        p[4] = pfeEmi;
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(p.join("$#"));
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 2) + ")").text(pnuAnn);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 3) + ")").text(pnuEmi);
        $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 4) + ")").text(pnuDes);
        if ($("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 5) + ")").text() === "BD") {
            $("#tblRefEmiDocAdm tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 5) + ")").text("UPD");
        }
        fn_changeReferenciaCorrecta(pfila);
        var pnuAnnExpBean = allTrim(jQuery('#documentoEmiBean').find('#nuAnnExp').length===0 ? "0NP3":jQuery('#documentoEmiBean').find('#nuAnnExp').val());
        var pnuSecExpBean = allTrim(jQuery('#documentoEmiBean').find('#nuSecExp').length===0 ? "0NP3":jQuery('#documentoEmiBean').find('#nuSecExp').val());
//        if (pnuAnnExpBean === "" && pnuSecExpBean === "") {
            if (pnuAnnExp !== "" && pnuSecExp !== "") {
//                if (confirm('El documento pertenece al Expediente:\n'
//                        + pnuExpediente + '\nAdicionar este documento?')) {
//                    fun_agregarDocumentoAlExpediente(pnuAnnExp, pnuSecExp);
//                }
                bootbox.dialog({
                    message: " <h5>El documento pertenece al Expediente: <strong>" + pnuExpediente + "</strong> Adicionar este documento?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-primary",
                            callback: function() {
                                fun_agregarDocumentoAlExpediente(pnuAnnExp, pnuSecExp);
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-default"
                        }
                    }
                });                   
            }
//        }
    } else {
        removeDomId('windowConsultaDocumentoRefEmi');
       bootbox.alert("DOCUMENTO YA SE ENCUENTRA REFERENCIADO.");
    }
    return false;
}

function fn_verDocumentoRefEmi(cell) {
    var indexFila = (($(cell).parent()).parent()).index();
    var isOk = $("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ") td").last().html();
    if (isOk === "1") {
        var pnuAnn = ($(cell).parent()).parent().children().get(7).innerHTML;
        var pnuEmi = ($(cell).parent()).parent().children().get(8).innerHTML;
        fn_verDocumentosObj(pnuAnn, pnuEmi, "0");
    } else {
        return;
    }
}

function continueRegresarEmitDocumAdm(){
    if (jQuery('#divRecepDocumentoAdmin').length === 0) {
        jQuery('#divEmiDocumentoAdmin').toggle();                                
        jQuery('#divNewEmiDocumAdmin').toggle(); 
        submitAjaxFormEmiDocAdm(jQuery('#buscarDocumentoEmiBean').find('#sTipoBusqueda').val());
        jQuery('#btn-buscar').click();
        jQuery('#divNewEmiDocumAdmin').html("");        
        //mostrarOcultarDivBusqFiltro2();
    } else {
        var pnuAnn = jQuery('#txtpnuAnn').val();
        if (pnuAnn !== "") {
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
            p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
            p[3] = "pcoPri=" + jQuery('#txtpcoPri').val();
            ajaxCall("/srDocumentoAdmRecepcion.do?accion=goEditarDocumento", p.join("&"), function(data) {
                console.log('data nuevo:'+data);
                refreshScript("divWorkPlaceRecepDocumAdmin", data);
                fn_cargaToolBarRec();
            }, 'text', false, false, "POST");
        } else {
            alert_Info("Emisión :", "Seleccione una fila de la lista");
        }
    }    
}

function regresarEmitDocumAdm(pclickBtn) {
    if (pclickBtn === "1") {
        var vEsDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        if(vEsDocEmi==="5" || vEsDocEmi==="7"){
            var rpta = fu_verificarChangeDocumentoEmiAdm();
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
                        setTimeout(function(){continueRegresarEmitDocumAdm();}, 300);
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
                setTimeout(function(){continueRegresarEmitDocumAdm();}, 300);
            }
        }else{
            animacionventanaRecepDoc();
            setTimeout(function(){continueRegresarEmitDocumAdm();}, 300);
        }
    }else{
      continueRegresarEmitDocumAdm();
    }
}

function getJsonFormExpedienteEmiBean() {
    var arrCampoBean = new Array();
    arrCampoBean[0] = "nuAnnExp";
    arrCampoBean[1] = "nuSecExp";
    arrCampoBean[2] = "nuExpediente";
    arrCampoBean[3] = "feExp";
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

function getJsonFormRemitenteEmiBean() {
    var arrCampoBean = new Array();
    arrCampoBean[0] = "coDependencia=coDepEmi";
    arrCampoBean[1] = "coEmpFirma=coEmpEmi";
    arrCampoBean[2] = "coLocal=coLocEmi";
    arrCampoBean[3] = "coEmpElabora=coEmpRes";
    var o = {};
    var a = $('#documentoEmiBean').serializeArray();
    $.each(a, function() {
        for (var i = 0; i < arrCampoBean.length; i++) {
            var arrcampoBeanAux = arrCampoBean[i].split("=");
            if (this.name === arrcampoBeanAux[1]) {
                //        if (o[this.name]) {
                //            if (!o[this.name].push) {
                //                o[this.name] = [o[this.name]];
                //            }
                //            o[this.name].push(this.value || '');
                //        } else {
                o[arrcampoBeanAux[0]] = this.value || '';
                //        }          
            }
        }
    });
    return o;
}

function getJsonFormDocumentoEmiBean() {
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
    arrCampoBean[18] = "deObsDoc";
    //arrCampoBean[19] = "confidencial";
    var o = {}; 
    for(var i=0;i < arrCampoBean.length; i++){        
        //if(arrCampoBean[i]=="confidencial"){
        //    o[arrCampoBean[i]]=$("#documentoEmiBean").find("#"+arrCampoBean[i]).is(':checked')?'1':'0';
        //}
        //else
            o[arrCampoBean[i]]=$("#documentoEmiBean").find("#"+arrCampoBean[i]).val();
    }
     
   /* var a = $('#documentoEmiBean').serializeArray();   
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
    });*/
    //alert('obsejto: '+o);
    return o;
}

function fu_changeDocumentoEmiBean() {
    jQuery('#envDocumentoEmiBean').val("1");
}

function fu_changeExpedienteEmiBean() {
    jQuery('#envExpedienteEmiBean').val("1");
}

function fu_changeRemitenteEmiBean() {
    jQuery('#envRemitenteEmiBean').val("1");
}

function fn_seteaCamposDocumentoEmi() {
    jQuery('#envDocumentoEmiBean').val("0");
    jQuery('#envExpedienteEmiBean').val("0");
    jQuery('#envRemitenteEmiBean').val("0");
    var p = new Array();
    p[0] = "accion=goUpdTlbsDestinatario";
    p[1] = "pnuAnn=" + $("#nuAnn").val();
    p[2] = "pnuEmi=" + $("#nuEmi").val();
    //p[3] = "pcoDependencia=" + $("#coDepEmi").val();        
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_updateTblsEmisioDocAdm(data);
    },
            'text', false, true, "POST");
}
//function fn_buscaFirmadoPorRemitenteEmi(){
//     var p = new Array();    
//     p[0] = "accion=goBuscaDestinatario";	    
//     p[1] = "pnuAnn=" + jQuery('#sCoAnnio').val();    
//     ajaxCall("/srDocumentoAdmRecepcion.do",p.join("&"),function(data){
//            fn_rptaBuscaDestinatario(data); 
//         },
//     'text', false, false, "POST");     
//}

function fn_cambiaComboDepEmi() {
    var p = new Array();
    p[0] = "accion=goCambiaDepEmi";
    p[1] = "pcoDepEmi=" + jQuery("#coDepEmi").val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null && data.retval === "OK") {
            jQuery('#documentoEmiBean').find('#coEmpEmi').val(data.coEmpEmi );
            jQuery('#documentoEmiBean').find('#deEmpEmi').val(data.deEmpEmi );
            jQuery('#documentoEmiBean').find('#deDocSig').val(data.deSigla );
            jQuery('#documentoEmiBean').find('#nuCorEmi').val("");
            jQuery('#documentoEmiBean').find('#nuCorDoc').val("");
            jQuery('#documentoEmiBean').find('#nuDocEmi').val("");
            jQuery('#documentoEmiBean').find("#nuDocEmi").trigger("change");            
        }
    }, 'json', false, true, "POST");
}

function fn_changeTipoDestinatarioDocuEmiAdm(sTipoDestEmi){
    fn_changeTipoDestinatarioDocuEmi(sTipoDestEmi,jQuery('#documentoEmiBean').find('#esDocEmi').val());
}

function fn_changeTipoDestinatarioDocuEmi(sTipoDestEmi, sEstadoDocAdm) {
    $("#divMuestraOpcInstitu").hide();
    $("#divtablaDestEmiDocAdmIntitu").hide();
    $("#divtablaDestEmiDocAdmPersJuri").hide();
    $("#divtablaDestEmiDocAdmCiudadano").hide();
    $("#divtablaDestEmiDocAdmOtro").hide();
    
     
    /*segdi mvaldera*/
    $("#divGrupoSalida").hide();
    if ((sEstadoDocAdm === "5" || sEstadoDocAdm === "7") && (sTipoDestEmi === "02" || sTipoDestEmi === "03")) // || sTipoDestEmi === "04"))
        {
            
            var p = new Array();
            p[0] = "accion=goListaGruposDestinosExter";
            p[1] = "vcoTipo=" + jQuery('#sTipoDestinatario').val();
            
            ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {  
                console.log(data);
                    refreshScript("divDetalleGrupoDestino", data);
                }, 'text', false, false, "POST");
    
           $("#divGrupoSalida").show();
        }    
    
    /*segdi mvaldera*/
    
    if (sTipoDestEmi === "02") {
        $("#divtablaDestEmiDocAdmPersJuri").show();
    } else if (sTipoDestEmi === "03") {
        $("#divtablaDestEmiDocAdmCiudadano").show();
    } else if (sTipoDestEmi === "04") {
        $("#divtablaDestEmiDocAdmOtro").show();   
    } else {
        $("#divtablaDestEmiDocAdmIntitu").show(); 
        $("#divGrupoSalida").hide(); /*segdi mvaldera*/
        if (sEstadoDocAdm === "5" || sEstadoDocAdm === "7") {
            $("#divMuestraOpcInstitu").show();
        }
    }
}

function fn_setDestinoEmitlbOtroOrigen(desDest, tipDocInden, nroDocInden, codDest,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis) {
    var pfila = jQuery('#txtTblDestOtroEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiOtroColWhereButton').val();
    var arrCampo = new Array();
    arrCampo[0] = "1=" + codDest;//codDestOtro
    var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmOtro', arrCampo);
    //YUAL
    
    if (!bResult) {
         bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
    }
        var ubigeo=cubiCoddep+','+cubiCodpro+','+cubiCoddis;
        var nom=noDep+'/'+noPrv+'/'+noDis;
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(desDest);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(codDest);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 2) + ")").find('input[type=text]').val(tipDocInden);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 3) + ")").find('input[type=text]').val(nroDocInden);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(5)").text(desDest);
        
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(10)").find('input[id=txtUbigeoOtro]').val(nom=="//"?"":nom);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(10)").find('input[id=txtCodigoUbigeoOtro]').val(ubigeo==",,"?"":ubigeo);
         
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(11)").find('textarea').val(cproDomicil);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(12)").find('input[type=text]').val(cproEmail);
        $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(13)").find('input[type=text]').val(cproTelefo);
        
        
        if ($("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(4)").text() === "BD") {
            $("#tblDestEmiDocAdmOtro tbody tr:eq(" + pfila + ") td:eq(4)").text("UPD");
        }
        removeDomId('windowConsultaOtroOri');
        fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmOtro', pfila);
 
}

function fn_buscaDestOtroOrigenAgregatbl(cell) {
    var snoOtroOrigen = allTrim(fu_getValorUpperCase(($(cell).parent()).find('input[type=text]').val()));
    snoOtroOrigen=($(cell).parent()).find('input[type=text]').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoOtroOrigen))).val();            
    if(allTrim(snoOtroOrigen).length >= 0 && allTrim(snoOtroOrigen).length <= 3){  //El texto es entre 1 y 3 caracteres
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }    
        
    if (snoOtroOrigen !== "") {
        var rptValida = validaCaracteres(snoOtroOrigen, "2");
        if (rptValida === "OK") {
            // Expresion regular
//            var cadena ="^[a-zA-Z0-9ñÑáéíóúÁÉÍÓÚ\' ]+$";
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(snoOtroOrigen.match(re)){
                var p = new Array();
                p[0] = "accion=goBuscaDestOtroOrigenAgrega";
                p[1] = "pnoOtroOrigen=" + snoOtroOrigen;
                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptaBuscaDestOtroOrigenAgregatbl(data);
                    jQuery('#txtTblDestOtroEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
                    jQuery('#txtTblDestEmiOtroColWhereButton').val(($(cell).parent()).index());
                },
                        'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>", function() {
                    ($(cell).parent()).find('input[type=text]').focus();
                });                
            }        
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>", function() {
                ($(cell).parent()).find('input[type=text]').focus();
            });              
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre Destinatario.</h5>", function() {
            ($(cell).parent()).find('input[type=text]').focus();
        });                      
    }
    return false;
}

function fn_rptaBuscaDestOtroOrigenAgregatbl(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_changeDestinatarioCorrectoOtro(idTabla, pnroFila) {
    var ultimDiv = $("#" + idTabla + " tbody tr:eq(" + pnroFila + ") td div.ui-state-error-text").last();
    ultimDiv.removeClass('ui-state-error-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-alert');
    ultimDivSpan.addClass('ui-icon-check');
    ultimDivSpan.attr("title", "OK");
    ultimDiv.addClass('ui-state-highlight-text');
    $("#" + idTabla + " tbody tr:eq(" + pnroFila + ") td").last().html("1");
}

function fn_changeDestinatarioIncorrectoOtro(idTabla, pnroFila) {
    var ultimDiv = $("#" + idTabla + " tbody tr:eq(" + pnroFila + ") td div.ui-state-highlight-text").last();
    ultimDiv.removeClass('ui-state-highlight-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-check');
    ultimDivSpan.addClass('ui-icon-alert');
    ultimDivSpan.attr("title", "ERROR");
    ultimDiv.addClass('ui-state-error-text');
    $("#" + idTabla + " tbody tr:eq(" + pnroFila + ") td").last().html("0");
}

function fu_cambioTxtEmiDocOtro(input) {
    var p = new Array();
    p[0] = 0;
    fn_evalDestinatarioCorrectotblEmiOtro('tblDestEmiDocAdmOtro', (($(input).parent()).parent()).index(), 5, p, 1);
}

function fn_evalDestinatarioCorrectotblEmiOtro(idTabla, indexFila, indexColDatos, arrIndexCompara, contFound) {
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


function fn_buscaCiudadanoDestNombreEmi(dni, pvalidKeys) {
    var tk = new KeyboardClass(oEvent, pvalidKeys);
    var nuDni = dni.value;
    if (nuDni.length === 8 && tk.isIntro() /*&& nuDni !== jQuery('#txtNroDocumentoCiudadano').val()*/) {
        var arrCampo = new Array();
        arrCampo[0] = "4=" + nuDni;
        var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmCiudadano', arrCampo);
        if (bResult) {
            var p = new Array();
            p[0] = "accion=goBuscaCiudadano";
            p[1] = "pnuDoc=" + nuDni;
            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                fn_rptaBuscaCiudadanoDestEmi(data, dni);
            },
                    'json', false, false, "POST");
        } else {
           bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
        }
    }
    return false;
}
function fn_buscaCiudadanoDestEmi(dni, pvalidKeys) {
    var tk = new KeyboardClass(oEvent, pvalidKeys);
    var nuDni = dni.value;
    if (nuDni.length === 8 && tk.isIntro() /*&& nuDni !== jQuery('#txtNroDocumentoCiudadano').val()*/) {
        var arrCampo = new Array();
        arrCampo[0] = "4=" + nuDni;
        var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmCiudadano', arrCampo);
        if (bResult) {
            var p = new Array();
            p[0] = "accion=goBuscaCiudadano";
            p[1] = "pnuDoc=" + nuDni;
            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                fn_rptaBuscaCiudadanoDestEmi(data, dni);
            },
                    'json', false, false, "POST");
        } else {
           bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
        }
    }
    return false;
}

//YUAL
function fn_buscaCiudadanoNuDestEmi() {

  //     var pfila2 = jQuery('#txtIndexFilaDestCiudadanoEmiDoc').val();
 // $("#tblDestEmiDocAdmCiudadano tbody tr:eq(" + pfila2 + ") td:eq(2)").find('#txtDNICiudadano').click();  

}


function fn_rptaBuscaCiudadanoDestEmi(data, cell) {
    if (data !== null) {
        var coRespuesta = data.coRespuesta;
        var ciudadano = data.ciudadano;
        if (coRespuesta === "1") {
            (($(cell).parent()).parent()).children().each(function(index) {
                switch (index)
                {
                    case 0:
                        $(this).find('input[type=text]').val(ciudadano.nuDoc);
                        jQuery('#txtNroDocumentoCiudadano').val(ciudadano.nuDoc);
                        break;
                    case 2:
                        $(this).find('input[type=text]').val(ciudadano.nombre);
                        break;
                    case 4:
                        $(this).html(ciudadano.nuDoc);
                        break;
                    case 8:
                        $(this).find('input[id=txtUbigeoOtro]').val(ciudadano.ubigeo);
                        $(this).find('input[id=txtCodigoUbigeoOtro]').val(ciudadano.ubdep+','+ciudadano.ubprv+','+ciudadano.ubdis);
                        break;
                    case 9:
                        $(this).find('textarea').val(ciudadano.dedomicil=="null"?"":ciudadano.dedomicil);
                        break;
                    case 10:
                        $(this).find('input[type=text]').val(ciudadano.deemail=="null"?"":ciudadano.deemail);
                        break;
                    case 11:
                        $(this).find('input[type=text]').val(ciudadano.detelefo=="null"?"":ciudadano.detelefo);
                        break;
                    default:
                        break;
                }
            });
            //ubdep,ubprv,ubdis,dedomicil,deemail,detelefo,ubigeo;
            if (($(cell).parent()).parent().children().get(3).innerHTML === "BD") {
                ($(cell).parent()).parent().children().get(3).innerHTML = "UPD";
            }
            fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmCiudadano', (($(cell).parent()).parent()).index());
        } else {
            bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>");                           
        }
    }
}

function fu_cambioTxtEmiDocCiudadano(input) {
    var p = new Array();
    p[0] = 0;
    fn_evalDestinatarioCorrectotblEmiOtro('tblDestEmiDocAdmCiudadano', (($(input).parent()).parent()).index(), 4, p, 1);
}

function fn_buscaDestProveedorAgregatbl(cell) {
    var snoRazonSocial = allTrim(fu_getValorUpperCase(($(cell).parent()).find('input[type=text]').val()));
    //snoRazonSocial=($(cell).parent()).find('input[type=text]').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(snoRazonSocial))).val();            
    if(allTrim(snoRazonSocial).length >= 0 && allTrim(snoRazonSocial).length <= 3){  //El texto es entre 1 y 3 caracteres
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll();
            //($(cell).parent()).find('input[type=text]').focus();
        });  
        return;
    }        
    if (snoRazonSocial !== "") {
        var rptValida = validaCaracteres(snoRazonSocial, "2");
        if (rptValida === "OK") {
            // Expresion regular
            var cadena ="^[A-Z0-9ÑÁÉÍÓÚ ]+$";
            var re = new RegExp(cadena);
            if(snoRazonSocial.match(re)){            
                var p = new Array();
                p[0] = "accion=goBuscaDestProveedorAgrega";
                p[1] = "prazonSocial=" + snoRazonSocial;
                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptaBuscaDestProveedorAgregatbl(data);
                    jQuery('#txtTblDestProveedorEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
                    jQuery('#txtTblDestEmiProveedorColWhereButton').val(($(cell).parent()).index());
                },
                        'text', false, false, "POST");
            }else{
                bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>", function() {
                    ($(cell).parent()).find('input[type=text]').focus();
                });                  
            }                     
        } else {
            bootbox.alert("<h5>"+rptValida+".</h5>", function() {
                ($(cell).parent()).find('input[type=text]').focus();
            });            
        }
    } else {
        bootbox.alert("<h5>Debe ingresar datos en Nombre Destinatario.</h5>", function() {
            ($(cell).parent()).find('input[type=text]').focus();
        });                    
    }
    return false;
}

function fn_rptaBuscaDestProveedorAgregatbl(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fn_setDestinoEmitlbProveedor(prazonSocial, pnuRuc,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis) {
    var arrCampo = new Array();
    arrCampo[0] = "7=" + pnuRuc;
    var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmPersJuri', arrCampo);
    if (bResult) {
        
        var pfila = jQuery('#txtTblDestProveedorEmiFilaWhereButton').val();
        var pcol = jQuery('#txtTblDestEmiProveedorColWhereButton').val();
        var ubigeo=cubiCoddep+','+cubiCodpro+','+cubiCoddis;
        var nom=noDep+'/'+noPrv+'/'+noDis;
        
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(7)").text(pnuRuc);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(prazonSocial);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 2) + ")").find('input[type=text]').val(pnuRuc);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtUbigeoOtro]').val(nom=="//"?"":nom);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtCodigoUbigeoOtro]').val(ubigeo==",,"?"":ubigeo);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(10)").find('textarea').val(cproDomicil);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(11)").find('input[type=text]').val(cproEmail);
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(12)").find('input[type=text]').val(cproTelefo);
        
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(4)").text(prazonSocial);        
        if ($("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text() === "BD") {
            $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text("UPD");
        }
       
        removeDomId('windowConsultaProveedor');
        fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmPersJuri', pfila);
             } else {
        removeDomId('windowConsultaProveedor');
       bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
    }
    
}

function fu_cambioTxtEmiNomProveedor(input) {
    var p = new Array();
    p[0] = 0;
    fn_evalDestinatarioCorrectotblEmiOtro('tblDestEmiDocAdmPersJuri', (($(input).parent()).parent()).index(), 4, p, 1);
}

function fn_updateTblsEmisioDocAdm(XML_AJAX) {
    if (XML_AJAX !== null) {
        refreshScript("divActualizaTablasDestintario", XML_AJAX);
        fn_changeTipoDestinatarioDocuEmi(jQuery("#sTipoDestinatario").val(), jQuery("#esDocEmi").val());
        var p = new Array();
        p[0] = "accion=goUpdTlbReferencia";
        p[1] = "pnuAnn=" + $("#nuAnn").val();
        p[2] = "pnuEmi=" + $("#nuEmi").val();
        p[3] = "pcoDependencia=" + $("#coDepEmi").val();
        ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
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

function fn_validaDocumentoRefLista(pfila,pcol, pnuAnn, pnuEmi, pnuDes) {
    var countFound = 0;
    $("#tblRefEmiDocAdm tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            if(index !== pfila){
                var pnuAnnRef = $(row).find("td:eq(" + (pcol * 1 + 2) + ")").text();
                var pnuEmiRef = $(row).find("td:eq(" + (pcol * 1 + 3) + ")").text();
                //var pnuDesRef = $(row).find("td:eq(" + (pcol * 1 + 4) + ")").text();
                if (pnuAnn === pnuAnnRef && pnuEmi === pnuEmiRef /*&& pnuDes === pnuDesRef*/) {
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

function fn_validaDestinatarioIntituDuplicado(idTabla, arrCamposCompara, bcomFilaActual, indexFilaActual) {
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

function fn_validaDestinatarioOtroDuplicado(idTabla, arrCamposCompara) {
    var countFound = 0;
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            for (var i = 0; i < arrCamposCompara.length; i++) {
                var auxArrCamposCompara = arrCamposCompara[i].split("=");
                var auxIndex = auxArrCamposCompara[0];
                var auxValor = auxArrCamposCompara[1];
                if ($(row).find("td:eq(" + auxIndex + ")").text() === auxValor) {
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

function fn_cargaDocEmi() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
    $('#inCargar').val("N");
   
    /*if (pesDocEmi === "7") {
        ptiOpe = "4";
    }*/
    if (pesDocEmi != "7") {
     //   ptiOpe = "4";
      var ptiOpe = "3";
    
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
            ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                eval("docs=" + data);
                console.log('docs:'+docs);
                console.log('data:'+data);
                
                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                    //fn_cargaDocApplet(docs.noUrl, docs.noDoc,function(data){
                    fn_cargaDocDesktop(docs.noUrl, docs.noDoc,function(data){
                    resulCarga=data;
                    console.log('Cargando WORD'+docs.noDoc);
                    console.log(resulCarga);
                        if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                            var p = new Array();
                            p[0] = "accion=goCargarDocEmi";
                            p[1] = "nuAnn=" + pnuAnn;
                            p[2] = "nuEmi=" + pnuEmi;
                            p[3] = "tiOpe=" + ptiOpe;
                            p[4] = "nuSec=" + resulCarga;
                            p[5] = "noDoc=" + docs.noDoc;
                            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
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
                                        
                                        cargarPDF(pnuAnn,pnuEmi,pesDocEmi);
                                        
                                    }
                                    else {
                                        alert_Danger("Repositorio: ", rpta.deRespuesta);
                                    }
                                }
                            }, 'text', false, false, "POST");
                        } else {
                            alert_Danger("Repositorio: ", "Error al cargar documento WORD, intente nuevamente.");
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
}

function cargarPDF(pnuAnn,pnuEmi,pesDocEmi)
{    var resulCarga = "ERROR";
           var  ptiOpe = "4";
              /* Obteniendo la ruta de Carga*/
            var p = new Array();
                var docs;
            p[0] = "accion=goRutaCargaDoc";
            p[1] = "nuAnn=" + pnuAnn;
            p[2] = "nuEmi=" + pnuEmi;
            p[3] = "tiOpe=" + ptiOpe;
            ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                eval("docs=" + data);
                console.log(docs);
                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                    //fn_cargaDocApplet(docs.noUrl, docs.noDoc,function(data){
                    fn_cargaDocDesktop(docs.noUrl, docs.noDoc,function(data){
                    resulCarga=data;
                    console.log('Cargando WORD'+docs.noDoc);
                    console.log(resulCarga);
                        if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                            var p = new Array();
                            p[0] = "accion=goCargarDocEmi";
                            p[1] = "nuAnn=" + pnuAnn;
                            p[2] = "nuEmi=" + pnuEmi;
                            p[3] = "tiOpe=" + ptiOpe;
                            p[4] = "nuSec=" + resulCarga;
                            p[5] = "noDoc=" + docs.noDoc;
                            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
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
                            alert_Danger("Repositorio: ", "Error al cargar documento PDF, intente nuevamente.");
                        }
                    });
                }
            }, 'text', true, false, "POST");
    
    
}

function fn_cargaDocEmiPase() {
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
            ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                eval("docs=" + data);
                if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                    //fn_cargaDocApplet(docs.noUrl, docs.noDoc,function(data){
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
                            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                                var rpta;
                                try {
                                    eval("var rptaOb=" + data);
                                    rpta = rptaOb;
                                } catch (e) {
                                   bootbox.alert(data);
//                                   return;
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



function fn_eliminarDocEmiAdm() {
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
                        ajaxCall("/srDocumentoAdmEmision.do?accion=goEliminarDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
                            fn_rptEliminarDocEmiAdm(data);
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

function fn_rptEliminarDocEmiAdm(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            regresarEmitDocumAdm('0');
        } else {
            alert_Info("Eliminar :", data.deRespuesta);
        }
    }
}

function fn_anularDocEmiAdm() {

    var rpta = fu_verificarChangeDocumentoEmiAdm();
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
                                ajaxCall("/srDocumentoAdmEmision.do?accion=goAnularDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
                                    fn_rptAnularDocEmiAdm(data);
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

function fn_rptAnularDocEmiAdm(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            regresarEmitDocumAdm('0');
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
}

function fn_validarToDespacho(stipo) {
    var vResult = " destinatario";
    var vtabla = ["tblDestEmiDocAdmOtro= destinatario Otros", "tblDestEmiDocAdmCiudadano= destinatario Ciudadano", "tblDestEmiDocAdmPersJuri= destinatario Persona Jurídica", "tblDestEmiDocAdm= destinatario Institución"];
    if (stipo === "0") {
        vtabla.push("tblRefEmiDocAdm= Referencia");
    }
    var countEmpty = 0;
    for (var i = 0; i < vtabla.length; i++) {
        var vauxTabla = vtabla[i].split("=");
        var rpta = fn_verificarExisteDestinatario(vauxTabla[0]);
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "0") {
            vResult = rpta.substr(1) + vauxTabla[1];
            break;
        } else if (nrpta === "2") {
            countEmpty++;
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

function fn_verificarExisteDestinatario(idTabla) {
    var limCaracter=false;
    var found = false;
    var nroFilas = 0;
    var vResult;
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto') && !found) {
            nroFilas++;
            if ($(row).find('td').last().html() !== "1") {
                found = true;
                return false;
            }
            if(!found){
                $(row).find('textarea').each(function(index,txtArea){
                    if(!found){
                        var maxLengthIndica = $(this).attr('maxlength');
                        if(!!maxLengthIndica){
                          var pdeIndica = $(this).val();  
                          var nrolinesDeIndica = (pdeIndica.match(/\n/g) || []).length;
                          if(pdeIndica.length+nrolinesDeIndica > maxLengthIndica){
                              found = true;
                              limCaracter=true;
                              $(this).focus();
                              return false;
                          }                  
                        }
                    }
                });
            }
        }
    });
    if (found) {
        if(limCaracter){
          vResult = "0La Indicación Excede el límite de 600 caracteres";  
        }else{
            vResult = "0Corregir";
        }
    } else {
        if (nroFilas > 0) {
            vResult = "1OK";
        } else {
            vResult = "2Especifique";
        }
    }
    return vResult;
}

function fn_verificarExisteFila(idTabla) {
   
    var found = false;
    var nroFilas = 0;
    
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto') && !found) {
                nroFilas++;
 
            }
        
    });

    return nroFilas;
}


function fn_validarEnvioGrabaDocEmiAdm(objTrxDocumentoEmiBean) {
    console.log(objTrxDocumentoEmiBean);
    var vReturn = "0EL DOCUMENTO ES EL MISMO.";
    if (objTrxDocumentoEmiBean !== null && typeof(objTrxDocumentoEmiBean) !== "undefined") {
        if (typeof(objTrxDocumentoEmiBean.documentoEmiBean) !== "undefined" || typeof(objTrxDocumentoEmiBean.expedienteEmiBean) !== "undefined" ||
                typeof(objTrxDocumentoEmiBean.remitenteEmiBean) !== "undefined") {
            vReturn = "1A GRABAR";
        } else if (typeof(objTrxDocumentoEmiBean.lstReferencia) !== "undefined" || typeof(objTrxDocumentoEmiBean.lstDestinatario) !== "undefined" ||
                typeof(objTrxDocumentoEmiBean.lstEmpVoBo) !== "undefined") {//add vobo
            if (objTrxDocumentoEmiBean.lstReferencia.length > 0 || objTrxDocumentoEmiBean.lstDestinatario.length > 0 || objTrxDocumentoEmiBean.lstEmpVoBo.length > 0) {
                vReturn = "1A GRABAR";
            }
        }
    }
    return vReturn;
}

function fn_abrirDocumentoEmi() {

    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var ptiOpe = "5";

    if (!!pnuAnn && !!pnuEmi) {
        var p = new Array();
        //p[0] = "accion=goDocRutaAbrirEmi";
        p[0] = "accion=goDocRutaAbrirEmiReporte";        
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof(docs) !== "undefined" && typeof(docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                if (docs.retval === "OK") {
                    //result = fn_abrirDocApplet(docs.noUrl, docs.noDoc);
                    var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
                    //runApplet(appletsTramiteDoc.abrirDocumento,param,function(data){
                    runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                        result=data;
                    });
                } else {
                    //Error en Documento
                    alert_Danger("!Repositorio : ", docs.retval);
                }
            }

        }, 'text', false, false, "POST");
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");
    }
}

function fn_abrirDocumentoFromPC() {

    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var ptiOpe = "3";

    if (!!pnuAnn&&!!pnuEmi) {
        fn_verDocumentosFromPC(pnuAnn, pnuEmi, ptiOpe);
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");        
    }

}


function fn_verDocumentoEmi() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var ptiOpe = "0";

    if (!!pnuAnn&&!!pnuEmi) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");        
    }

}

function fn_agregarDestinatarioIntitucionGrupo() {
    var vcmbGrupo = jQuery("#cmbGrupo").val();
    var vTxtValcmbGrupo = jQuery('#txtValcmbGrupo').val();
    if (vcmbGrupo !== "-1" && vTxtValcmbGrupo !== "-1" && vTxtValcmbGrupo === "1") {
        //if(vTxtValcmbGrupo === "1"){
        var idTabla = 'tblDestEmiDocAdm';
        var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
        if (rpta) {
            jQuery('#txtValcmbGrupo').val("0");
            var p = new Array();
            p[0] = "accion=goAgregarDestinatarioIntitucion";
            p[1] = "pcogrupo=" + vcmbGrupo;
            p[2] = "pcoTipDoc=" + jQuery('#documentoEmiBean').find('#coTipDocAdm').val();
            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                fn_rptagregarDestinatarioIntitucionGrupo(data, idTabla);
                   
                    //YUAL
                    if($('#cmbPrdAllDestino').val()>1){
                    fn_setPrioridadFilaTblDestIntitu('tblDestEmiDocAdm', $('#cmbPrdAllDestino').val())
                    }
            }, 'json', false, false, "POST");
        }
        //}
    }/*else{
    bootbox.alert("Seleccionar Grupo.."); 
     jQuery("#cmbGrupo").focus();
     }*/
 
    
    return false;
}

function fn_rptagregarDestinatarioIntitucionGrupo(data, idTabla) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            $.each(data.lsDestInstitu, function(i, item) {
                var arrCampo = new Array();
                arrCampo[0] = "1=" + item.coDependencia;
                arrCampo[1] = "5=" + item.coEmpleado;
                var bResult = fn_validaDestinatarioIntituDuplicado(idTabla, arrCampo, false, 0);
                if (bResult) {
                    fn_addDestintarioEmiDocGrupo(idTabla, item);
                }
            });
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
    return false;
}

function fn_verificarLstDestinatarioCorrecto(idTabla) {
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
            return found;
        } else {
           bootbox.alert("Corregir Destinatario");
            return false;
        }
    } else {
       bootbox.alert("Corregir Destinatario");
        return false;
    }
}

function fn_addDestintarioEmiDocGrupo(idTabla, objDes) {
    var esPrimero = $("#" + idTabla + " tbody tr").last().hasClass('oculto');
    var pfila = $("#" + idTabla + " tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#" + idTabla + " tbody").index();
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });
    var bDestinOk=true;
    var p = new Array();
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
        switch (index)
        {
            case 0:
                $(this).find('input[type=text]').val(objDes.deDependencia);
                p[0] = allTrim(objDes.deDependencia);
                break;
            case 1:
                $(this).text(objDes.coDependencia);
                break;
            case 2:
                $(this).find('input[type=text]').val(objDes.deLocal);
                p[1] = allTrim(objDes.deLocal);
                break;
            case 3:
                $(this).text(objDes.coLocal);
                break;
            case 4:
                var deEmp=objDes.deEmpleado;
                if(deEmp!==null && allTrim(deEmp)!==""){
                    $(this).find('input[type=text]').val(deEmp);
                    p[2] = allTrim(deEmp);
                }
                else{
                    bDestinOk=false;
                }        
//                $(this).find('input[type=text]').val(objDes.deEmpleado);
//                p[2] = allTrim(objDes.deEmpleado);
                break;
            case 5:
                var coEmp=objDes.coEmpleado;
                if(coEmp!==null && allTrim(coEmp)!==""){
                    $(this).text(coEmp);
                }
                else{
                    bDestinOk=false;
                }                 
                //$(this).text(objDes.coEmpleado);
                break;
            case 6:
                if (esPrimero) {
                    $(this).find('input[type=text]').val(objDes.deTramiteFirst);
                    p[3] = allTrim(objDes.deTramiteFirst);
                } else {
                    $(this).find('input[type=text]').val(objDes.deTramiteNext);
                    p[3] = allTrim(objDes.deTramiteNext);
                }
                break;
            case 7:
                if (esPrimero) {
                    $(this).text(objDes.coTramiteFirst);
                } else {
                    $(this).text(objDes.coTramiteNext);
                }
                break;
            default:
                break;
        }
    });
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(12)").text(p.join("$#"));
    if(bDestinOk){
        fn_changeDestinatarioCorrecto(pfila);
    }
}

function onchangeCmbGrupo() {
    jQuery('#txtValcmbGrupo').val("1");
}

function fn_setTramiteTblDestIntitu() {
    var cotramite = jQuery("#cmbTramiteDocEmi").val();
    var detramite = jQuery('#cmbTramiteDocEmi option:selected').html();
    if (cotramite !== "-1") {
        var idTabla = 'tblDestEmiDocAdm';
        var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
        if (rpta) {
            fn_setTramiteFilaTblDestIntitu(idTabla, cotramite, detramite);
        }
    }
    return false;
}

function fn_setTramiteFilaTblDestIntitu(idTabla, cotramite, detramite) {
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            $(row).children('td').each(function(indexx) {
                switch (indexx)
                {
                    case 6:
                        $(this).find('input[type=text]').val(detramite);
                        break;
                    case 7:
                        $(this).text(cotramite);
                        break;
                    case 11:
                        var vaccionBD = $(this).text();
                        if (vaccionBD === "BD") {
                            $(this).text("UPD");
                        }
                        break;
                    case 12:
                        var arrAux = $(this).text().split("$#");
                        arrAux.pop();
                        arrAux.push(detramite);
                        $(this).text(arrAux.join("$#"));
                        break;
                    default:
                        break;
                }
            });
        }
    });
}

function fn_setIndicacionesTblDestIntitu() {
    var valRetorno="1";
    var sindica = allTrim(jQuery("#txtIndicaciones").val());
    if (!!sindica) {
        var maxLengthDeIndica = jQuery("#txtIndicaciones").attr('maxlength');
        if(!!maxLengthDeIndica){
            var nrolinesDeIndica = (sindica.match(/\n/g) || []).length;
            if(sindica.length+nrolinesDeIndica > maxLengthDeIndica){
                valRetorno="0";
                bootbox.alert("<h5>La Indicación Excede el límite de "+maxLengthDeIndica+" caracteres.</h5>", function() {
                    jQuery("#txtIndicaciones").focus();
                });
                jQuery("#txtIndicaciones").focus();
            }             
        }
        if(valRetorno==="1"){
            var idTabla = 'tblDestEmiDocAdm';
            var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
            if (rpta) {
                $("#" + idTabla + " tbody tr").each(function(index, row) {
                    if (index > 0 && !$(row).hasClass('oculto')) {
                        $(row).children('td').each(function(indexx) {
                            switch (indexx)
                            {
                                case 8:
                                    //$(this).find('input[type=text]').val(sindica);
                                    $(this).find('textarea').val(sindica);
                                    break;
                                case 11:
                                    var vaccionBD = $(this).text();
                                    if (vaccionBD === "BD") {
                                        $(this).text("UPD");
                                    }
                                    break;
                                default:
                                    break;
                            }
                        });
                    }
                });
            }
        }
    }
}

function fn_changeEstadoDocumentoEmiAdm(vchangeEsDocEmi) {
    var pesDocEmi = jQuery('#esDocEmi').val();
    if (vchangeEsDocEmi === "0") {
        if (pesDocEmi === "7") {
            jQuery('#esDocEmi').val("0");
            fu_changeDocumentoEmiBean();
            fn_verificarCreacionExpediente();
        }
    } else if (vchangeEsDocEmi === "5") {
        jQuery('#esDocEmi').val("5");
        fu_changeDocumentoEmiBean();
        fn_verificarCreacionExpediente();
    } else if (vchangeEsDocEmi === "7") {
        jQuery('#esDocEmi').val("7");
        fu_changeDocumentoEmiBean();
        fn_verificarCreacionExpediente();

    } else if (vchangeEsDocEmi === "-1") {//se selecciono guardar
        fn_verificarCreacionExpediente();
    }
}

function fn_verSeguimientoEmi() {
        clearInterval($('#pIDAutoRefresh').val());
    
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function fn_verSeguimientoEmiEdit() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function fn_verAnexoEmi() {
        clearInterval($('#pIDAutoRefresh').val());
    
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function fn_verAnexoEmiEdit() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function fn_cargarDocumentosAnexos() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();

    var rpta = fu_verificarChangeDocumentoEmiAdm();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    } else {
        if (!!pnuAnn&&!!pnuEmi) {
            if(fu_existeVistoBuenoDocAdm()){ 
                fn_verAnexoEmiEdit();
                //alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
            }else{
                fn_cargarDocumentosAnexosObj(pnuAnn, pnuEmi);                                
            }
        } else {
            //alert("Seleccione una fila de la lista");
            alert_Info("Emisión :", "Faltan Datos");
        }
    }
}


function fn_jsonVerificarNumeracionDocEmi(/*ptipoCmb,*/pnuDocEmiAnn, pnuAnn, pnuEmi, ptiEmi, pcoTipDocAdm, pcoDepEmi, pnuDocEmi) {
    var p = new Array();
    p[0] = "accion=goVerificarNumDocEmi";
    p[1] = "nuDoc=" + pnuDocEmiAnn;
    p[2] = "nuAnn=" + pnuAnn;
    p[3] = "nuEmi=" + pnuEmi;
    p[4] = "tiEmi=" + ptiEmi;
    p[5] = "coTipDocAdm=" + pcoTipDocAdm;
    p[6] = "coDepEmi=" + pcoDepEmi;
    p[7] = "nuDocEmi=" + pnuDocEmi;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaJsonVerificarNumeracionDocEmi(data, pnuDocEmiAnn/*,ptipoCmb*/, pcoTipDocAdm);
    }, 'json', false, false, "POST");
}

function fn_rptaJsonVerificarNumeracionDocEmi(JSON_AJAX, pnuDocEmiAnn/*,ptipoCmb*/, pcoTipDocAdm) {
    if (JSON_AJAX !== null) {
        if (JSON_AJAX.coRespuesta === "1") {
            var nuDocEmi = replicate(jQuery('#documentoEmiBean').find('#nuDocEmi').val(), 6);
            jQuery('#documentoEmiBean').find('#nuDocEmi').val(nuDocEmi);
            jQuery('#documentoEmiBean').find('#nuCorDoc').val("1");
            jQuery('#txtnuDocEmiAn').val(nuDocEmi);
        } else {
            alert_Danger('', JSON_AJAX.deRespuesta);
            var pcoTipDocAdmAn = jQuery('#documentoEmiBean').find('#txtcoTipDocAdmAn').val();
            if (pcoTipDocAdm === pcoTipDocAdmAn) {
                jQuery('#documentoEmiBean').find('#nuDocEmi').val(pnuDocEmiAnn);
            } else {
                jQuery('#documentoEmiBean').find('#nuDocEmi').val("");
                jQuery('#documentoEmiBean').find('#txtnuDocEmiAn').val("");
            }
        }
    } else {
        alert_Danger("Error!", "Verificando Numeración del Documento");
        jQuery('#documentoEmiBean').find('#nuDocEmi').val("");
        jQuery('#documentoEmiBean').find('#txtnuDocEmiAn').val("");
    }
}

function fn_buscarFirmadoPor() {
    var p = new Array();
    p[0] = "accion=goBuscaFirmadoPorEdit";
    p[1] = "pcoDep=" + jQuery('#documentoEmiBean').find('#coDepEmi').val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaEmpleado(data);
    },
            'text', false, false, "POST");
}

function fn_buscarElaboradoPorEdit() {
    var p = new Array();
    p[0] = "accion=goBuscaElaboradoPorEdit";
    p[1] = "pcoDep=" + jQuery('#documentoEmiBean').find('#coDepEmi').val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaEmpleado(data);
    },
            'text', false, false, "POST");
}

function fu_setDatoElaboradoPorEdit(cod, desc) {
    jQuery('#documentoEmiBean').find('#deEmpRes').val(desc);
    jQuery('#documentoEmiBean').find('#coEmpRes').val(cod);
    fu_changeRemitenteEmiBean();
    removeDomId('windowConsultaElaboradoPor');
}

function fu_setDatoFirmadoPorEdit(cod, desc) {
    jQuery('#documentoEmiBean').find('#deEmpEmi').val(desc);
    jQuery('#documentoEmiBean').find('#coEmpEmi').val(cod);
    fu_changeRemitenteEmiBean();
    removeDomId('windowConsultaElaboradoPor');
}

function fu_changeEstadoDocEmiAdm(pEstadoDoc) {
    var pcoFirma=jQuery('#documentoEmiBean').find('#coFirma').val();
    
    var grabadoFir="0";
    
    if (pEstadoDoc==="5"&&pcoFirma==="S")
    {
        fn_grabarDocumentoEmiAdm();
        grabadoFir= "1";
    }
    

    var validaFiltro = fu_verificarCamposDocEmiAdm('1', pEstadoDoc);

    
    
    if (validaFiltro === "1") {
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeDocumentoEmiAdm();
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "1"&&grabadoFir!=="1") {
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
                                fn_changeToProyectoDocEmiAdm();//cambiar a proyecto
                                if(grabadoFir==="1")
                                {
                                    regresarEmitDocumAdm('7');
                                }
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
                    rpta = fu_verificarDestinatario("1");
                    nrpta = rpta.substr(0, 1);
                    if (nrpta === "1") {
                        rpta = fu_verificarReferencia();
                        nrpta = rpta.substr(0, 1);                    
                        if(nrpta === "1"){
                            rpta = fu_verificarEmpVoBo();//add vobo
                            nrpta = rpta.substr(0, 1);            
                            if(nrpta === "1"){
                                fn_changeToDespachoDocEmiAdm();//cambiar a Despacho                                           
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
                    fn_changeToEmitidoDocEmiAdm();//cambiar a Emitido
                }
            }           
        }
    }
    else
    {
        alert_Warning("Emisión de Proveido:", "Necesita grabar los cambios");
    }
    return false;
}


function fn_validarEstadoDocEmiAdm(pEstadoDoc) {
    var vResult = "0";
    if (pEstadoDoc === "0") {
        var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        if (pesDocEmi === "7") {
            vResult = "1";
        }
        var pnuDocEmi = allTrim(jQuery('#documentoEmiBean').find('#nuDocEmi').val());
        if (typeof(pnuDocEmi) !== "undefined" && pnuDocEmi !== "") {
            vResult = "1";
        } else {
            vResult = "0";
            alert_Info('', "Documento no esta Numerado.");
            jQuery('#nuDocEmi').focus();
        }
    }
    return vResult;
}

function fn_grabarDocumentoEmiAdm() {
   
    var validaFiltro = fu_verificarCamposDocEmiAdm('0', '');
    if (validaFiltro === "1") {
        var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        var rpta = fu_verificarDestinatario("1");
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
                    rpta = fu_verificarChangeDocumentoEmiAdm();
                    nrpta = rpta.substr(0, 1);
                    if (nrpta === "1") {
                        fn_goGrabarDocumentoEmiAdm();//grabar Documento
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


function fn_grabarDocumentoEmiAdmProveido() {
    
    var validaFiltro = fu_verificarCamposDocEmiAdm('0', '');
    if (validaFiltro === "1") {
              fn_goGrabarDocumentoEmiAdm();//grabar Documento                   
                    
    }
    else
    {
         alert_Info("Emisión Proveido: Campos no verificados ", rpta);
    }
    
    
                    
    return false;
}


function fn_goGrabarDocumentoEmiAdm() {
    $('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmi();
//    console.log(cadenaJson);
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
            fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente); 
    }else{
            pcrearExpediente = "0";
       fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente); 
    }
    }else{
       fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente); 
}
}

function fn_submitGrabarDocumentoEmiAdm(cadenaJson,pcrearExpediente){
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    ajaxCallSendJson("/srDocumentoAdmEmision.do?accion=goGrabaDocumentoEmi&pcrearExpediente=" + pcrearExpediente, cadenaJson, function(data) {
        fn_rptaGrabaDocumEmiAdmin(data, pcrearExpediente);
    },
    'json', false, false, "POST");    
}

function fn_changeToProyectoDocEmiAdm() {
    ajaxCall("/srDocumentoAdmEmision.do?accion=goChangeToProyecto", $('#documentoEmiBean').serialize(), function(data) {
        fn_rptaChangeToProyectoDocEmiAdm(data);
    },
            'json', false, false, "POST");
}
function fn_rptaChangeToProyectoDocEmiAdm(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("5");
            fn_seteaCamposDocumentoEmi();//resetear variables del documento
            fn_updateTblVoBoDocAdm();
            fu_cargaEdicionDocAdm("00", jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmi();  
            $('#divEmitirMensajeria').hide();
        } else {
            alert_Danger("Emisión :",data.deRespuesta);
        }
    }
}

function fn_changeToDespachoDocEmiAdm() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val("");
    
    //yual
    var pCarga="0"; // $('#inCargar').val();
     var r = new Array();
    r[0] = "accion=goVerificaCargaDoc";
    r[1] = "pnuAnn=" + pnuAnn;
    r[2] = "pnuEmi=" + pnuEmi;
    
    ajaxCall("/srDocObjeto.do", r.join("&"), function(data) {
        
       pCarga=data.bCargaDoc;

    
    var pcoDoc=$('#coTipDocAdm').val();
    if (pCarga=="0" && pcoDoc !== "232" && pcoDoc !== "304")
    {
        alert_Warning("Para Despacho!: ", "Necesita volver a cargar el documento");
        retval = "ERROR";
        return;
    }
    if (!!pnuAnn && !!pnuEmi) {
        // Obteniendo la ruta de Carga
        
//        fn_cargaDocEmiPase();
        
//        console.log("carga word");
        var p = new Array();
        p[0] = "accion=goRutaCargaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=4";
        p[4] = "inFor=1";
        ajaxCall("/srDocObjeto.do", p.join("&"), function(dataRuta) {
            var retval = "ERROR";
            var docs;
            eval("docs=" + dataRuta);
            if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined') {
                if (docs.retval === "OK"){
                    //Para el caso de Proveido
                    if (!!docs.coDoc && (docs.coDoc === "232" || docs.coDoc === "304")) {
                        retval = "OK";
                        grabarCambioADespacho(retval);
                        return;
                        
                    } else {
                        if (!!docs.noDoc) {
//                            console.log("AQUI1");
                            var param={rutaDoc:docs.noDoc};
                            //runApplet(appletsTramiteDoc.verificaSiExisteDoc,param,function(data){
                            
                            runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                                retval = data;
                                if (retval === "SI") {
//                                    console.log("AQUI2");
                                    //var appletObj = jQuery('#firmarDocumento');
                                    try {
                                        var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
                                        //runApplet(appletsTramiteDoc.cargarDocumento,param,function(data){
                                        runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function(data){    
                                            retval = data;
                                            /*if (retval !== "ERROR" && retval !== "NO") {
                                                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(retval);
                                                retval = "OK";
                                            }*/
                                            if(retval.error==="0"){
                                                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(retval.message);
                                                retval = "OK";
                                            }
                                            grabarCambioADespacho(retval);
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
//                                    console.log("AQUI3");
                                    grabarCambioADespacho(retval);
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
       
    }, 'json', false, false, "POST");
    
   
}
function grabarCambioADespacho(retval){
    if (retval === "OK") {
        ajaxCall("/srDocumentoAdmEmision.do?accion=goChangeToDespacho", $('#documentoEmiBean').serialize(), function(data) {
            fn_rptaChangeToDespachoDocEmiAdm(data);
        },
                'json', false, false, "POST");
    }
}

/*mvaldera SEGDI 26082018*/
function fn_rptaChangeToDespachoDocEmiAdm(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            var pcoFirma=jQuery('#documentoEmiBean').find('#coFirma').val();
            
            //console.log("dasasdasd1="+pcoFirma);
            if(pcoFirma==="S")
            {
                var pnuAnn=jQuery('#documentoEmiBean').find('#nuAnn').val();
                var pnuEmi=jQuery('#documentoEmiBean').find('#nuEmi').val();
                
                /*console.log("dasasdasd2="+pnuAnn);
                console.log("dasasdasd2="+pnuEmi);*/
                editarDocumentoEmiClick(pnuAnn, pnuEmi,'1','0');
            }
            else
            {
                jQuery('#documentoEmiBean').find('#esDocEmi').val("7");
                fn_seteaCamposDocumentoEmi();//resetear variables del documento
                fn_updateTblVoBoDocAdm();
                fu_cargaEdicionDocAdm("00", jQuery('#documentoEmiBean').find('#esDocEmi').val());
                alert_Sucess("Éxito!", "Transacción completada.");
                jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
                fn_cargaToolBarEmi();
            }

        } else {
            alert_Danger("Para Despacho!: ",data.deRespuesta);
        }
    }
}

function fn_cargaDocFirmaApplet(pnoDoc,callback) {
    
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    var pnuSecFirmaEmi = jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val();
    var ptiOpe = "6";
    var resulCarga = "ERROR";
    var docs;
    if (!!pnuAnn && !!pnuEmi && !!pnuSecFirmaEmi) {
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaFirmaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        p[4] = "nuSecFirma=" + pnuSecFirmaEmi;
        p[5] = "tipoDocumento="+$("#coTipDocAdm").val();
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            eval("docs=" + data);
            if (typeof(docs) !== "undefined" && typeof(docs.retval) !== 'undefined') {
               
                if (docs.retval === "OK") {
                    var retval = "";
                    try {
                        //retval = appletObj[0].cargarDocumento(docs.noUrl, pnoDoc);
                        if($("#txtpdNoFirmaProveido").val()=="1"){pnoDoc=docs.noFirma+".pdf";}
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

function fn_firmarDocumentoEmi() {

  if ($('#EsBtnFirmar').val()=='S'){
             regresarEmitDocumAdm('7');
             fn_firmaDirectaDocumento();
            } else {
               fn_firmarDocumentoEmision();
            }
}

//YUAL
function fn_firmarDocumentoEmision() {

    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    jQuery("#rutaDocFirma").val("");
    
    var ptiOpe = "5";

    var rpta = fu_verificarChangeDocumentoEmiAdm();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    } else {
        if (!!pnuAnn&&!!pnuEmi) {
            /*if(fu_existeVistoBuenoDocAdm()){
                alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
            }else{*/
            var param={pnuAnn:pnuAnn,pnuEmi:pnuEmi};
            ajaxCall("/srDocumentoAdmEmision.do?accion=goVerificarVistoBuenoPendiente", param, function(data) {
                if(data.coRespuesta==="1"){
                    var p = new Array();
                    p[0] = "accion=goRutaFirmaDocReporte";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=" + ptiOpe;
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
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
                                showBtnEnviarDocAdm();

                                //result = fn_firmarDocApplet(docs.noUrl, docs.noDoc);
                                var inTSA = "0";//indicador timestampin(servidor de tiempo) inactivo.
                                var cargoEmp = docs.cargoEmpleado;
                                var cargoEmpInTSA = "$"+inTSA+"$"+cargoEmp;
                                //var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1"};
                                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1" + cargoEmpInTSA};
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



//YUAL
function fn_ImprimirActaNotificacion(tipo) {

     var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
   if(!!pnuAnn&&!!pnuEmi){  
                    var p = new Array();
                    p[0] = "accion=goRutaGeneraActaNotificacion";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe="+tipo;
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        var result;
                        eval("var docs=" + data); 
                        if (typeof (docs) !== "undefined" && typeof (docs.coRespuesta) !== 'undefined' && docs.coRespuesta !== "") {
                        
                            if (docs.coRespuesta === "0") {
                                   
                                 if(!!docs.noUrl&&!!docs.noDoc){  
                                    fn_generaDocDesktop(docs.noUrl,docs.noDoc,null,null,null,function(data){
                                        var result = data;
                                        console.log('3----------->>>>>>>>>>>');
                                        if (result!=="OK"){
                                           bootbox.alert(result);
                                        }
                                    });
                                 }
                     
                                /*fn_generaDocDesktop(docs.noUrl, docs.noDoc, docs.nuAnn, docs.nuEmi, docs.tieneWord, function(data) {
                                    result = data;
                                    if (result !== "OK") {
                                       bootbox.alert(result);
                                    }
                                });*/
                            } else {
                                alert_Danger("Generar Acta de Notificación: ", docs.deRespuesta);
                            }
                        }
                    }, 'text', false, false, "POST");

               
    return false;

    }else{
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }

}



//YUAL
function fn_firmaDirectaDocumento() {

  
    clearInterval($('#pIDAutoRefresh').val());
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
   if(!!pnuAnn&&!!pnuEmi){  
        
   
   
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    jQuery("#rutaDocFirma").val("");
    
    var ptiOpe = "5";

      //  if (!!pnuAnn&&!!pnuEmi) {
            /*if(fu_existeVistoBuenoDocAdm()){
                alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
            }else{*/
            var param={pnuAnn:pnuAnn,pnuEmi:pnuEmi};
            ajaxCall("/srDocumentoAdmEmision.do?accion=goVerificarVistoBuenoPendiente", param, function(data) {
                if(data.coRespuesta==="1"){
                    var p = new Array();
                    p[0] = "accion=goRutaFirmaDocReporte";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=" + ptiOpe;
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
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
                            //    showBtnEnviarDocAdm();

                                //result = fn_firmarDocApplet(docs.noUrl, docs.noDoc);
                                var inTSA = "0";//indicador timestampin(servidor de tiempo) inactivo.
                                var cargoEmp = docs.cargoEmpleado;
                         
                                var cargoEmpInTSA = "$"+inTSA+"$"+cargoEmp+"$"+docs.nuEmi+"$"+docs.nuAnn+"$"+docs.dni+"$"+docs.coUsuario+"$"+docs.coVerExt+"$"+docs.numeroDoc;
                                //var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1"};
                                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1" + cargoEmpInTSA};
                                //runApplet(appletsTramiteDoc.ejecutaFirma,param,function(data){
                                runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma, param, function(data){
                                    result=data;
                                });
                                
                                
                                    var cont = 0;
                                    var rango = document.getElementById('cont');

                                    var id = setInterval(function(){

                                         $('#pIDAutoRefresh').val(id);
                                        if(cont%10==0){
                                         jQuery('#btn-buscar').click();
                                         console.log('ID'+id+' actualiza grilla: '+cont);
                                        }

                                        cont++;
                                        if(cont == 60) 
                                        {
                                            clearInterval(id);
                                        }
                                    }, 1000); 
                                
                                
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
      //  } else {
       //    bootbox.alert("Documento no encontrado");
        //}
    
        
        
        
        
    }else{
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
    
    
    

}


function fn_firmarDocumentoEmiProveido() {
  
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#txtNuEmiProveido').val();
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    jQuery("#rutaDocFirma").val("");
    
    var ptiOpe = "5";
/*
    var rpta = fu_verificarChangeDocumentoEmiAdm();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    } else {*/
        if (!!pnuAnn&&!!pnuEmi) {
            /*if(fu_existeVistoBuenoDocAdm()){
                alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");                
            }else{*/
            var param={pnuAnn:pnuAnn,pnuEmi:pnuEmi};
            ajaxCall("/srDocumentoAdmEmision.do?accion=goVerificarVistoBuenoPendiente", param, function(data) {
                if(data.coRespuesta==="1"){
                    var p = new Array();
                    p[0] = "accion=goRutaFirmaDocReporte";
                    p[1] = "nuAnn=" + pnuAnn;
                    p[2] = "nuEmi=" + pnuEmi;
                    p[3] = "tiOpe=" + ptiOpe;
                    ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
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
                                showBtnEnviarDocAdm();

                                //result = fn_firmarDocApplet(docs.noUrl, docs.noDoc);
                                var inTSA = "0";//indicador timestampin(servidor de tiempo) inactivo.
                                var cargoEmp = docs.cargoEmpleado;
                                var cargoEmpInTSA = "$"+inTSA+"$"+cargoEmp;
                                //var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1"};
                                var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1" + cargoEmpInTSA};
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
    //}
}


function fn_grabarEmisionDocumento(valDoc,vnoDoc){
    
    if (valDoc === "SI") {
        fn_cargaDocFirmaApplet(vnoDoc, function(data) {
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
          
            if(resulCarga.error==="0" ){    
       
                jQuery('#documentoEmiBean').find('#nuSecuenciaFirma').val(resulCarga.message);
                ajaxCall("/srDocumentoAdmEmision.do?accion=goChangeToEmitido", $('#documentoEmiBean').serialize(), function(data) {
                   fn_rptaChangeToEmitidoDocEmiAdm(data);                                       
                },'json', false, false, "POST");
            }
        
        });
    } else {
        alert_Danger("Firma!", "El Documento no esta Firmado.");
    }
}
function fn_changeToEmitidoDocEmiAdm() {
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
            //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
            runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){                
                valDoc=data;
                if($("#txtpdNoFirmaProveido").val()=="1"){
                    valDoc = "SI";
                    fn_grabarEmisionDocumento(valDoc,vnoDoc);
                    return;
                }
                if (valDoc === "SI") {
                    fn_grabarEmisionDocumento(valDoc,vnoDoc);
                    return;
                }
                if (vinFirma === "N" && valDoc !== "SI") {
                    vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) + "N.pdf";
                    //valDoc = fn_verificaSiExisteDoc(vnoDoc);
                    var param = {rutaDoc: vnoDoc};
                    //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                        valDoc = data;
                        fn_grabarEmisionDocumento(valDoc, vnoDoc);
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



function fn_rptaChangeToEmitidoDocEmiAdm(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("0");
            fn_seteaCamposDocumentoEmi();//resetear variables del documento
            fu_cargaEdicionDocAdm("00", jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            
            if($('#bFirmaProveido').val()=='1')
            {
            fn_cargaToolBarEmiProveido();
            }
            else{
                fn_cargaToolBarEmi();
            }
            
            /*
             var pcoFirma=jQuery('#documentoEmiBean').find('#coFirma').val();
             if(pcoFirma!='S'){
              ValidarMensajeria();
                }   */
              ValidarMensajeria();
           //YUAL
            var pcoFirma=jQuery('#documentoEmiBean').find('#coFirma').val();

             if(pcoFirma=='S'){
                  regresarEmitDocumAdm('7');
             }
           
            
        } else {
            
            alert_Danger("Emisión!",data.deRespuesta);
        }
    }
}
function ValidarMensajeria(){
    var tblDestEmiDocAdmPersJuri = $('#tblDestEmiDocAdmPersJuri tr').length;
    var tblDestEmiDocAdmOtro = $('#tblDestEmiDocAdmOtro tr').length;
    var tblDestEmiDocAdmCiudadano = $('#tblDestEmiDocAdmCiudadano tr').length;
   
    if(tblDestEmiDocAdmPersJuri>1 || tblDestEmiDocAdmOtro>1 || tblDestEmiDocAdmCiudadano>1){
        
        var btnEmitirMensajeria = jQuery('#divEmitirMensajeria').find('button').get(0);  
        btnEmitirMensajeria.setAttribute("style","color: #ac2925");
        btnEmitirMensajeria.removeAttribute('onclick');
        btnEmitirMensajeria.setAttribute('onclick','fn_EnviarNotificacion_NEW();');
        $('#divEmitirMensajeria').show();
      //  fn_EnviarNotificacion_NEW();
    }
}
function EnvioOkMensajeria(){     
        var btnEmitirMensajeria = jQuery('#divEmitirMensajeria').find('button').get(0);  
        btnEmitirMensajeria.setAttribute("style","color: #468847");
        btnEmitirMensajeria.removeAttribute('onclick');
        btnEmitirMensajeria.setAttribute('onclick','fn_EnviarNotificacion_NEW();');
        $('#divEmitirMensajeria').show();    
        
 
}
 function fn_EnviarNotificacion_NEW (){
     
             var snuAnn=$("#nuAnn").val()+"";
             var snuEmi=$("#nuEmi").val()+"";
             /*interoperabilidad*/
             var tiEnvMsj=$("#tiEnvMsj").val()+"";
             var docEstadoMsj=$("#docEstadoMsj").val();
             /*interoperabilidad*/
             fn_EnviarNotificacion(snuAnn,snuEmi,tiEnvMsj,'','','NEW',docEstadoMsj);
             return false;
            
         } 
function LoadMensajeria(){
    var btnEmitirMensajeria = jQuery('#divEmitirMensajeria').find('button').get(0);               
            $('#divEmitirMensajeria').hide();
            $("#divEmitirMensajeria").find("button").attr("style","");
            if($("#docEstadoMsj").val()==="-1" && ($("#txtesDocEmi").val()==="0" || $("#txtesDocEmi").val()==="1") && $("#tiDest").val()!="01")
            { 
                 btnEmitirMensajeria.setAttribute("style","color: #ac2925");
                 btnEmitirMensajeria.removeAttribute('onclick');
                 btnEmitirMensajeria.setAttribute('onclick','fn_EnviarNotificacion_edit();');
                $('#divEmitirMensajeria').show();
                
            }
            if($("#docEstadoMsj").val()==="0" && ($("#txtesDocEmi").val()==="0" || $("#txtesDocEmi").val()==="1") && $("#tiDest").val()!="01")
            { 
                 btnEmitirMensajeria.setAttribute("style","color: #468847");
                 btnEmitirMensajeria.removeAttribute('onclick');
                 btnEmitirMensajeria.setAttribute('onclick','fn_EnviarNotificacion_edit();');
                $('#divEmitirMensajeria').show();
                
            }
            return false;
}
 function fn_EnviarNotificacion_edit (){
             var snuAnn=$("#nuAnn").val()+"";
             var snuEmi=$("#nuEmi").val()+"";
             var tiEnvMsj=$("#tiEnvMsj").val()+"";
             var sexisteDoc=$("#sexisteDoc").val()+"";
             var sexisteAnexo=$("#sexisteAnexo").val()+"";
             /*interoperabilidad*/
             var docEstadoMsj=$("#docEstadoMsj").val();
             /*interoperabilidad*/             
             fn_EnviarNotificacion(snuAnn,snuEmi,tiEnvMsj,sexisteDoc,sexisteAnexo,'EDIT',docEstadoMsj);
         }                                                    
         
function fu_verificarChangeDocumentoEmiAdm() {//si es "1" necesita grabar el documento.
    jQuery('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmi();
    var rpta = fn_validarEnvioGrabaDocEmiAdm(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"||!(!!jQuery('#documentoEmiBean').find('#nuEmi').val()&&!!jQuery('#documentoEmiBean').find('#nuAnn').val())) {
        return "1";
    } else {
        //alert_Info("",rpta.substr(1));
        return rpta.substr(1);
    }
}

function fu_verificarDestinatario(pverificarRef) {
    var vResult = " destinatario";
    var vtabla = ["tblDestEmiDocAdmOtro= destinatario Otros", "tblDestEmiDocAdmCiudadano= destinatario Ciudadano", "tblDestEmiDocAdmPersJuri= destinatario Persona Jurídica", "tblDestEmiDocAdm= destinatario Institución"];
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
    
    var nFilasO = fn_verificarExisteFila("tblDestEmiDocAdmOtro");
    var nFilasC = fn_verificarExisteFila("tblDestEmiDocAdmCiudadano");
    var nFilasJ = fn_verificarExisteFila("tblDestEmiDocAdmPersJuri");
    var nFilasI = fn_verificarExisteFila("tblDestEmiDocAdm");
    
    
    if (nFilasO>=1 || nFilasC>=1 || nFilasJ>=1  )
    {
        if (nFilasI>=1)
        {
            vResult = "Quitar destinatario(s) Institución";
        }
    }
  
    return vResult;
}

function fu_verificarReferencia() {
    var vResult = " referencia";   
    var vtabla = ["tblRefEmiDocAdm= Referencia"];
    for (var i = 0; i < vtabla.length; i++) {
        var vauxTabla = vtabla[i].split("=");
        var rpta = fn_verificarExisteDestinatario(vauxTabla[0]);
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "0") {
            vResult = rpta.substr(1) + vauxTabla[1];
            break;
        }else if(nrpta === "2"){
            vResult = "1";
        }else {
            vResult = rpta.substr(0, 1);
        }
    }
   
    return vResult;    
}

function fu_verificarCamposDocEmiAdm(sOrigenBtn, pEstadoDoc) {
    var noForm='#documentoEmiBean';
    var pnuDiaAte = jQuery(noForm).find('#nuDiaAte').val();
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
    jQuery(noForm).find('#deObsDoc').val(allTrim(jQuery(noForm).find('#deObsDoc').val()));
    var pdeObsDoc = jQuery(noForm).find('#deObsDoc').val();
    var maxLengthDeObsDoc = jQuery(noForm).find('#deObsDoc').attr('maxlength');
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
            if(!!maxLengthDeObsDoc){
                var nrolinesDeObsDoc = (pdeObsDoc.match(/\n/g) || []).length;
                if(pdeObsDoc.length+nrolinesDeObsDoc > maxLengthDeObsDoc){
                    valRetorno = "0";
                    bootbox.alert("<h5>La Observación del Documento Excede el límite de "+maxLengthDeObsDoc+" caracteres.</h5>", function() {
                        jQuery("#deObsDoc").focus();
                    });
                    jQuery("#deObsDoc").focus();
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
     
    if(valRetorno === "1"){
         var Detalle=fu_validaPersonaJuridaDetalle("tblDestEmiDocAdmPersJuri","9","10"); 
         if (Detalle!=="OK") {
             valRetorno = "0";            
        }
    }
    if(valRetorno === "1"){
         var Detalle=fu_validaPersonaJuridaDetalle("tblDestEmiDocAdmOtro","10","11"); 
         if (Detalle!=="OK") {
             valRetorno = "0";            
        }
    }
    if(valRetorno === "1"){
         var Detalle=fu_validaPersonaJuridaDetalle("tblDestEmiDocAdmCiudadano","8","9"); 
         if (Detalle!=="OK") {
             valRetorno = "0";            
        }
    }
    
    return valRetorno;
}

function fu_validaPersonaJuridaDetalle(idTabla,indexUbi,indexDir){ 
    var vRetorna="OK";  
    var found = false; 
    var vDatosOblDesJur=$("#DatosOblDesJur").val();/*interoperabilidad*/
    
    $("#"+idTabla+" tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto') && !found) {                      
        var codigo=$("#"+idTabla+" tbody tr:eq(" + index + ") td:eq(" + indexUbi + ")").find('input[id=txtCodigoUbigeoOtro]').val()+"";
        var direccion =$("#"+idTabla+" tbody tr:eq(" + index + ") td:eq(" + indexDir + ")").find('textarea').val()+"";  
            if ( codigo === "" || codigo === ",," || codigo === "null,null,null" ) {
                vRetorna = "NO_OK";
                 alert_Danger("Alerta:", "Debe Seleccionar alguna Ubicacion.");
                found = true;
                return false;
            }
            if ( direccion === "") {
                vRetorna = "NO_OK";
                found = true;
                alert_Danger("Alerta:", "Debe Ingresar la Dirección.");
                return false;
            }  
            /*interoperabilidad*/
        if (vDatosOblDesJur==="S")
            {
                console.log("tipo==>"+vDatosOblDesJur);
                var nombreRemite=$("#"+idTabla+" tbody tr:eq(" + index + ") td:eq(13)").find('input[id=txtNombresPJCiudadano]').val()+"";
                var cargoRemite =$("#"+idTabla+" tbody tr:eq(" + index + ") td:eq(14)").find('input[type=text]').val()+"";
                console.log("nom==>"+nombreRemite);
                console.log("car==>"+cargoRemite);
                if ( nombreRemite === "") {
                    vRetorna = "NO_OK";
                    found = true;
                    alert_Danger("Alerta:", "Debe Ingresar Remitente.");
                    return false;
                }  
                
                if ( cargoRemite === "") {
                    vRetorna = "NO_OK";
                    found = true;
                    alert_Danger("Alerta:", "Debe Ingresar Cargo Remitente.");
                    return false;
                } 
            }
            /*interoperabilidad*/
        }
    });    
    return vRetorna; 
}

function fun_agregarDocumentoAlExpediente(pnuAnnExp, pnuSecExp) {
    var p = new Array();
    p[0] = "accion=goObtenerExpedienteBean";
    p[1] = "pnuAnnExp=" + pnuAnnExp;
    p[2] = "pnuSecExp=" + pnuSecExp;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null) {
            if (data.coRespuesta === "1") {
                jQuery('#documentoEmiBean').find('#nuAnnExp').val(data.nuAnnExp);
                jQuery('#documentoEmiBean').find('#nuSecExp').val(data.nuSecExp);
                jQuery('#documentoEmiBean').find('#nuExpediente').val(data.nuExpediente);
                jQuery('#documentoEmiBean').find('#feExp').val(data.feExp);
                jQuery('#documentoEmiBean').find('#feExpCorta').val(data.feExpCorta);
                jQuery('#documentoEmiBean').find('#coProceso').val(data.coProceso);
                jQuery('#documentoEmiBean').find('#deProceso').val(data.deProceso === "null" ? "" : data.deProceso);
                fu_changeExpedienteEmiBean();
                jQuery("#txtEsNuevoDocAdm").val("0");
            } else {
                alert_Danger("Expediente:", data.deRespuesta);
            }
        }
    }, 'json', false, true, "POST");
}

function cerrarPantallaModuloEmisionDoc() {
    var vEsDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
    if (vEsDocEmi === "5" || vEsDocEmi === "7") {
        var rpta = fu_verificarChangeDocumentoEmiAdm();
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            bootbox.dialog({
                message: " <h5>Existen Cambios en el Documento.\n" +
                        "¿ Desea salir del Documento ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-default",
                        callback: function() {
                            cerrarPantalla();
                        }                        
                    },
                    NO: {
                        label: "NO",
                        className: "btn-primary"
                    }
                }
            });
        } else {
            cerrarPantalla();
        }
    } else {
        cerrarPantalla();
    }
}

function fn_iniUpdTblDestinos() {
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });
}

function fu_setDatoDependenciaEmi(cod, desc) {
    jQuery('#txtDepEmiteBus').val(desc);
    jQuery('#sBuscDestinatario').val(cod);
    jQuery('#sDeTipoDocAdm').focus();
    removeDomId('windowConsultaDestinoEmi');
}

function fn_buscaDependenciaEmi() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaEmite";
    p[1] = jQuery('#txtDepEmiteBus').val() === ' [TODOS]' ? "pdeDepEmite=" : "pdeDepEmite=" + fu_getValorUpperCase(jQuery('#txtDepEmiteBus').val());
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaEmi(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaDependenciaEmi(XML_AJAX) {
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}
        if(obj){
            if(obj.coRespuesta==="1"){
                jQuery('#sBuscDestinatario').val(obj.coDependencia);
                jQuery('#txtDepEmiteBus').val(obj.deDependencia);
                jQuery('#sDeTipoDocAdm').focus();
            }else{
               bootbox.alert(obj.deRespuesta);
            }
        }else{
            $("body").append(XML_AJAX);
        }        
    }
}

function fu_FiltrarTecladoCadenaFunParam(evt, autoSubmit, pvalidKeys/*, sucessGoToId, errorGoToId, callbackFunction*/, input) {
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key === 37)) {
        var tk = new KeyboardClass(oEvent, pvalidKeys);
        if (tk.isIntro()) {
            fn_buscaDependenciaDestEmitbl(input);
        }
        return (((autoSubmit) ? autoSubmit : false) === true) ? tk.isValidKey : (tk.isValidKey && tk.k !== 13);
    }
    return false;
}

function fu_cambiarComponenteFiltro(btnCambiar) {
    if (countPressBtnChange === 0) {
        $("#fechaFiltro").showDatePicker({
            showDia: false,
            selectTodosMeses:true,
            pressAceptarEvent: function(data) {
//                console.log(data.fIni);
//                console.log(data.fFin);
//                console.log(data.rbOp);
                $("#fechaFiltro").val(data.fIni+"[]"+data.fFin);
                
                var numMes=moment(data.fIni, "DD/MM/YYYY").month();
//                console.log(numMes);
//                console.log(monthYearArray[numMes]);
                
                
            }
        });
    }
    var tdPrevBtnCambiar = jQuery(btnCambiar).parent('td').prev();
    if (countPressBtnChange % 2 === 0) {
        $("#sCoAnnio").hide();
        $("#fechaFiltro").show();
        tdPrevBtnCambiar.html("Fecha:");
        $("#esFiltroFecha").val("1");
    } else {
        $("#sCoAnnio").show();
        $("#fechaFiltro").hide();
        tdPrevBtnCambiar.html("Año:");
        $("#esFiltroFecha").val("0");
    }
    countPressBtnChange++;
    return;
}

function fu_FiltrarTecladoCadenaOtroOrigen(evt, autoSubmit, pvalidKeys/*, sucessGoToId, errorGoToId, callbackFunction*/, input) {
    fu_cambioTxtEmiDocOtro(input);
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key === 37)) {
        var tk = new KeyboardClass(oEvent, pvalidKeys);
        if (tk.isIntro()) {
            fn_buscaDestOtroOrigenAgregatbl(input);
        }
        return (((autoSubmit) ? autoSubmit : false) === true) ? tk.isValidKey : (tk.isValidKey && tk.k !== 13);
    }
    return false;
}

function fu_FiltrarTecladoCadenaPersJuri(evt, autoSubmit, pvalidKeys/*, sucessGoToId, errorGoToId, callbackFunction*/, input) {
    fu_cambioTxtEmiNomProveedor(input);
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key === 37)) {
        var tk = new KeyboardClass(oEvent, pvalidKeys);
        if (tk.isIntro()) {
            fn_buscaDestProveedorAgregatbl(input);
        }
        return (((autoSubmit) ? autoSubmit : false) === true) ? tk.isValidKey : (tk.isValidKey && tk.k !== 13);
    }
    return false;
}

function fn_inicializaDocAdmEmi(sCoAnnio){
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
    
    //YUALK
    if(sCoAnnio=="Hoy"){
    jQuery('#buscarDocumentoEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});            
    }
    else{
    jQuery('#buscarDocumentoEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});            
    }
    
    jQuery('#buscarDocumentoEmiBean').find('#sNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });    
    jQuery('#buscarDocumentoEmiBean').find('#sCoAnnioBus').find('option[value=""]').remove();
    pnumFilaSelect=0;
    changeTipoBusqEmiDocuAdm("0");
    
    if(jQuery('#EsBtnFirmar').val()=='S')
    {
        
        
    }
}

function verificarAñoBusquedaFiltro(vFeInicio,vFeFinal){
    try {
        var vReturn="A";
        var vAnioFini = vFeInicio.substr(6);
        var vAnioFfin = vFeFinal.substr(6);
        var lAnioFini = vAnioFini.length;
        var lAnioFfin = vAnioFfin.length;
        if(lAnioFfin===4 && lAnioFini===4){
            if(vAnioFini===vAnioFfin){
                vReturn = vAnioFini; 
            }else{
                vReturn = "0";
            }
        }
    } catch(vReturn) {
    }
    return vReturn;      
}

function upperCaseBuscarDocumentoEmiBean(){
    jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').val()));
    jQuery('#buscarDocumentoEmiBean').find('#sBuscNroExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sBuscNroExpediente').val()));
    jQuery('#buscarDocumentoEmiBean').find('#sDeAsuM').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiBean').find('#sDeAsuM').val()));
}

function removeNotification(){
    jQuery(document).mouseup(function (e)
    {
        var searchcontainer = jQuery("#notificacion_proyect");

        if (!searchcontainer.is(e.target) // if the target of the click isn't the container...
            && searchcontainer.has(e.target).length === 0) // ... nor a descendant of the container
        {
            searchcontainer.fadeOut("slow",function(){
                searchcontainer.remove();
            });
        }
    });
}

function fu_obtenerEsFiltroFecha(nameForm){
    var opt = jQuery('#'+nameForm).find('#sCoAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function fu_FiltrarTecladoCadenaFunParamGeneral(evt, autoSubmit, pvalidKeys, callbackFunction, paramFunction) {
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key === 37)) {
        var tk = new KeyboardClass(oEvent, pvalidKeys);
        if (tk.isIntro()) {
            callbackFunction(paramFunction);
        }
        return (((autoSubmit) ? autoSubmit : false) === true) ? tk.isValidKey : (tk.isValidKey && tk.k !== 13);
    }
    return false;
}

function setAnnioNoIncludeFiltroEmi(){
    var valRetorno = "1";
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
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
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
    }
    return valRetorno;       
}

function obtenerAnioBusqueda(vFeInicio,vFeFinal){
    try {
        var vReturn="A";
        var vAnioFini = vFeInicio.substr(6);
        var vAnioFfin = vFeFinal.substr(6);
        var lAnioFini = vAnioFini.length;
        var lAnioFfin = vAnioFfin.length;
        if(lAnioFfin===4 && lAnioFini===4){
            /*if(vAnioFini===vAnioFfin){*/
                vReturn = vAnioFini; 
            /*}else{
                vReturn = "0";
            }*/
        }
    } catch(vReturn) {
    }
    return vReturn;      
}

function fn_iniConsProveedorDestEmi(){
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
                    var cproDomicil= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                    var cproTelefo= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(3)").html();
                    var cproEmail= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(4)").html();
                    var cubiCoddep= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(5)").html();
                    var cubiCodpro= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(6)").html();
                    var cubiCoddis= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(7)").html();
                    var noDep= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(8)").html();
                    var noPrv= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(9)").html();
                    var noDis= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(10)").html();
                    fn_setDestinoEmitlbProveedor(prazonSocial,pnuRuc,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);
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
        var cproDomicil= $(this).find("td:eq(2)").html();
        var cproTelefo= $(this).find("td:eq(3)").html();
        var cproEmail= $(this).find("td:eq(4)").html();
        var cubiCoddep= $(this).find("td:eq(5)").html();
        var cubiCodpro= $(this).find("td:eq(6)").html();
        var cubiCoddis= $(this).find("td:eq(7)").html();
        var noDep= $(this).find("td:eq(8)").html();
        var noPrv= $(this).find("td:eq(9)").html();
        var noDis= $(this).find("td:eq(10)").html();
                             
        fn_setDestinoEmitlbProveedor(prazonSocial,pnuRuc,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);
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
            var cproDomicil= $(this).find("td:eq(2)").html();
            var cproTelefo= $(this).find("td:eq(3)").html();
            var cproEmail= $(this).find("td:eq(4)").html();
            var cubiCoddep= $(this).find("td:eq(5)").html();
            var cubiCodpro= $(this).find("td:eq(6)").html();
            var cubiCoddis= $(this).find("td:eq(7)").html();
            var noDep= $(this).find("td:eq(8)").html();
            var noPrv= $(this).find("td:eq(9)").html();
            var noDis= $(this).find("td:eq(10)").html();
            fn_setDestinoEmitlbProveedor(prazonSocial,pnuRuc,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);         
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_iniConsOtroOrigenDestEmi(){
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
                    
                    var cproDomicil= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(4)").html();
                    var cproTelefo= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(5)").html();
                    var cproEmail= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(6)").html();
                    var cubiCoddep= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(7)").html();
                    var cubiCodpro= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(8)").html();
                    var cubiCoddis= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(9)").html();
                    var noDep= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(10)").html();
                    var noPrv= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(11)").html();
                    var noDis= $(nomTbl+" tbody tr:eq("+indexSelect+")").find("td:eq(12)").html();   
                    fn_setDestinoEmitlbOtroOrigen(pdesDest,ptipDocInden,pnroDocInden,pcodDest
                           ,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);
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
        
         var cproDomicil= $(this).find("td:eq(4)").html();
         var cproTelefo= $(this).find("td:eq(5)").html();
         var cproEmail= $(this).find("td:eq(6)").html();
         var cubiCoddep= $(this).find("td:eq(7)").html();
         var cubiCodpro= $(this).find("td:eq(8)").html();
         var cubiCoddis= $(this).find("td:eq(9)").html();
         var noDep= $(this).find("td:eq(10)").html();
         var noPrv= $(this).find("td:eq(11)").html();
         var noDis= $(this).find("td:eq(12)").html();
            
        fn_setDestinoEmitlbOtroOrigen(pdesDest,ptipDocInden,pnroDocInden,pcodDest
                ,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);
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
            
            var cproDomicil= $(this).find("td:eq(4)").html();
            var cproTelefo= $(this).find("td:eq(5)").html();
            var cproEmail= $(this).find("td:eq(6)").html();
            var cubiCoddep= $(this).find("td:eq(7)").html();
            var cubiCodpro= $(this).find("td:eq(8)").html();
            var cubiCoddis= $(this).find("td:eq(9)").html();
            var noDep= $(this).find("td:eq(10)").html();
            var noPrv= $(this).find("td:eq(11)").html();
            var noDis= $(this).find("td:eq(12)").html();
         
            fn_setDestinoEmitlbOtroOrigen(pdesDest,ptipDocInden,pnroDocInden,pcodDest
                    ,cproDomicil,cproTelefo,cproEmail,cubiCoddep,cubiCodpro,cubiCoddis,noDep,noPrv,noDis);             
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}

function fn_limpiarNumeroDocEmi() {
    jQuery('#documentoEmiBean').find('#nuCorDoc').val("");
    jQuery('#documentoEmiBean').find('#nuDocEmi').val("");
}

function fn_llenaComboTramiteDocEmi() {
    var p = new Array();
    p[0] = "accion=goBuscaLstMotivoDocEmi";
    p[1] = "pcoTipDoc=" + jQuery("#coTipDocAdm").val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        $("#cmbTramiteDocEmi").find('option:not(:first)').remove();
        if (data !== null && data.coRespuesta === "1") {
            $.each(data.lsMotivo, function(i, item) {
                $("#cmbTramiteDocEmi").append('<option value=' + item.value + '>' + item.label + '</option>');
            });
        }
    }, 'json', false, true, "POST");
}

function fn_changeTipoDocEmiAdm(cmbTipoDoc){
    var coTiDoc=jQuery(cmbTipoDoc).val();
    var coTiDocLast=jQuery(cmbTipoDoc).find('option:last').val();
    var coTiDocFirst=jQuery(cmbTipoDoc).find('option:first').val();
    if(coTiDocLast==="-1"){
        jQuery(cmbTipoDoc).find('option:last').remove();
    }else if(coTiDocFirst==="-1"){
        jQuery(cmbTipoDoc).find('option:first').remove();
    }
    
    
    if(jQuery('#inDepSalEsp').val()=='1' && (coTiDoc=='011' || coTiDoc=='012' || coTiDoc=='250'))
    {
        jQuery('#divOrigenOficio').show();
         jQuery('#ddlOrigenOficio').val("0");     
       //  alert(jQuery('#ddlOrigenOficio').val());
         fn_getEditOrigenOficio();
    }
    else
    {
        jQuery('#divOrigenOficio').hide();        
    }
    
    fn_limpiarNumeroDocEmi();
    fn_llenaComboTramiteDocEmi();
//    fn_getNroDocumentoDocAdm(coTiDoc);
    fu_changeDocumentoEmiBean();
}

//YUAL
function fn_initOrigenEspecial(cmbTipoDoc){
    var coTiDoc=jQuery(cmbTipoDoc).val();
    var coTiDocLast=jQuery(cmbTipoDoc).find('option:last').val();
    var coTiDocFirst=jQuery(cmbTipoDoc).find('option:first').val();
    if(jQuery('#inDepSalEsp').val()=='1' && (coTiDoc=='011' || coTiDoc=='012' || coTiDoc=='250'))
    {
    if(coTiDocLast==="-1"){
        jQuery(cmbTipoDoc).find('option:last').remove();
    }else if(coTiDocFirst==="-1"){
        jQuery(cmbTipoDoc).find('option:first').remove();
    }
    
    
    
        jQuery('#divOrigenOficio').show();
         jQuery('#ddlOrigenOficio').val("0");     
       //  alert(jQuery('#ddlOrigenOficio').val());
         fn_getEditOrigenOficio();
   
    
    fn_limpiarNumeroDocEmi();
    fn_llenaComboTramiteDocEmi();
//    fn_getNroDocumentoDocAdm(coTiDoc);
    fu_changeDocumentoEmiBean();
    
     }
    
}


function fn_getEditOrigenOficio()
{
     //var cmbOrigenOficio=jQuery('#ddlOrigenOficio').val();
     var siglaDocumento=jQuery('#deDocSig').val();
     var siglaOrigen=siglaDocumento.substring(siglaDocumento.indexOf("-")+1,siglaDocumento.length);
     
     if(siglaOrigen!="" && siglaOrigen!="0")
     {
        jQuery('#ddlOrigenOficio').val(siglaOrigen);       
     }
     else
     {
        jQuery('#ddlOrigenOficio').val("0"); 
     }
  /*  var p = new Array();
    p[0] = "accion=goBuscaSIGLADependencia";
    p[1] = "pCodDep=" + cmbOrigenOficio;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        console.log(data);
     
         if (data !== null) {
            
            $('#deDocSig').val(siglaOficina+'-'+data.sigla);
           
        }
        else
        {
            $('#deDocSig').val(siglaOficina);
        }
    }, 'json', false, true, "POST");
*/
    
}

//YUAL
function fn_changeOrigenOficio()
{
    var cmbOrigenOficio=jQuery('#ddlOrigenOficio').val();
     var siglaOficina=jQuery('#siglaDependencia').val();
      if(cmbOrigenOficio.trim()!='' && cmbOrigenOficio.trim()!='0')
      {
       $('#deDocSig').val(siglaOficina+'-'+cmbOrigenOficio);
      }
      else{
           $('#deDocSig').val(siglaOficina);          
      }
    /* var p = new Array();
    p[0] = "accion=goBuscaSIGLADependencia";
    p[1] = "pCodDep=" + cmbOrigenOficio;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        console.log(data);
     
         if (data !== null) {
             $('#deDocSig').val(siglaOficina+'-'+data.sigla);

        }
        else
        {
            $('#deDocSig').val(siglaOficina);
        }
    }, 'json', false, true, "POST");*/
    
}

//YUAL
function fn_getEditOrigenOficio()
{
     //var cmbOrigenOficio=jQuery('#ddlOrigenOficio').val();
     var siglaDocumento=jQuery('#deDocSig').val();
     var siglaOrigen=siglaDocumento.substring(siglaDocumento.indexOf("-")+1,siglaDocumento.length);
     
     if(siglaOrigen!="" && siglaOrigen!="0")
     {
        jQuery('#ddlOrigenOficio').val(siglaOrigen);       
     }
     else
     {
        jQuery('#ddlOrigenOficio').val("0"); 
     }
  /*  var p = new Array();
    p[0] = "accion=goBuscaSIGLADependencia";
    p[1] = "pCodDep=" + cmbOrigenOficio;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        console.log(data);
     
         if (data !== null) {
            
            $('#deDocSig').val(siglaOficina+'-'+data.sigla);
           
        }
        else
        {
            $('#deDocSig').val(siglaOficina);
        }
    }, 'json', false, true, "POST");
*/
    
}

//YUAL
function fn_changeOrigenOficio()
{
    var cmbOrigenOficio=jQuery('#ddlOrigenOficio').val();
     var siglaOficina=jQuery('#siglaDependencia').val();
      if(cmbOrigenOficio.trim()!='' && cmbOrigenOficio.trim()!='0')
      {
       $('#deDocSig').val(siglaOficina+'-'+cmbOrigenOficio);
      }
      else{
           $('#deDocSig').val(siglaOficina);          
      }
    /* var p = new Array();
    p[0] = "accion=goBuscaSIGLADependencia";
    p[1] = "pCodDep=" + cmbOrigenOficio;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        console.log(data);
     
         if (data !== null) {
             $('#deDocSig').val(siglaOficina+'-'+data.sigla);

        }
        else
        {
            $('#deDocSig').val(siglaOficina);
        }
    }, 'json', false, true, "POST");*/
    
}

function fn_changeEnvMpOtros(chkEnvMesaPartes){
    var tblOtros='tblDestEmiDocAdmOtro';
    if (jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(4).innerHTML==="BD") {
        var chkEnvMpBD=jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(9).innerHTML==="1"?true:false;        
        var chkEnvMp=jQuery(chkEnvMesaPartes).is(':checked');
        if(chkEnvMpBD!==chkEnvMp){
            jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(4).innerHTML="UPD";            
        }
    }    
}

function fn_changeEnvMpCiudadano(chkEnvMesaPartes){
    var tblOtros='tblDestEmiDocAdmOtro';
    if (jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(3).innerHTML==="BD") {
        var chkEnvMpBD=jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(7).innerHTML==="1"?true:false;        
        var chkEnvMp=jQuery(chkEnvMesaPartes).is(':checked');
        if(chkEnvMpBD!==chkEnvMp){
            jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(3).innerHTML="UPD";            
        }
    }    
}

function fn_changeEnvMpProveedor(chkEnvMesaPartes){
    var tblOtros='tblDestEmiDocAdmOtro';
    if (jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(3).innerHTML==="BD") {
        var chkEnvMpBD=jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(8).innerHTML==="1"?true:false;        
        var chkEnvMp=jQuery(chkEnvMesaPartes).is(':checked');
        if(chkEnvMpBD!==chkEnvMp){
            jQuery(chkEnvMesaPartes).parents(':eq(1)').children().get(3).innerHTML="UPD";            
        }
    }    
}

function showBtnEnviarDocAdm(){
    var btnEmitirDoc = jQuery('#divEmitirDoc').find('button').get(0);
    btnEmitirDoc.removeAttribute('onclick');
    btnEmitirDoc.setAttribute('onclick','fu_changeEstadoDocEmiAdm(\'0\');');
    jQuery('#divEmitirDoc').show();
    
    //Fir
  //  var btnEmitirDocFri = jQuery('#divEmitirDocFir').val();
   // if (typeof (btnEmitirDocFri) != undefined && typeof (btnEmitirDocFri) != null && typeof (btnEmitirDocFri) != 'undefined') {
        //console.log(btnEmitirDocFri);
   // alert('existe');
    /*btnEmitirDocFri.removeAttribute('onclick');
    btnEmitirDocFri.setAttribute('onclick','fu_changeEstadoDocEmiAdm(\'0\');');*/
    $('#divEmitirDocFir').show();
     //}
    
}

function fn_iniNewEmpVoBoDocAdm(){
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
                    fu_setDatoEmpNewVoBoDocAdm(pcodDest,pdesDest);
                }
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tblElaboradoPor tbody tr").click(function(e) {
        var pdesDest= $(this).find("td:eq(0)").html();
        var pcodDest= $(this).find("td:eq(1)").html();
        fu_setDatoEmpNewVoBoDocAdm(pcodDest,pdesDest);            
    });    
}

function fu_setDatoEmpNewVoBoDocAdm(pcoEmp,pDeEmp){
    var pfila = jQuery('#txtTblPersVoBoFilaWhereButton').val();
    var pcol = jQuery('#txtTblPersVoBoColWhereButton').val();    
    var idTabla = "#tblPersVoBoDocAdm";
    removeDomId('windowConsultaElaboradoPor');
    
    var arrCampo = new Array();
    arrCampo[0] = "1=" + jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(1)").text();//codDependencia
    arrCampo[1] = "3=" + pcoEmp;//codEmpleado
    var bResult = fn_validaDestinatarioIntituDuplicado('tblPersVoBoDocAdm', arrCampo, true, pfila*1);
    if(bResult){
        if (jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(5)").text() === "BD") {
            jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(5)").text("DEL");
            jQuery(idTabla + " tbody tr:eq(" + pfila + ")").clone().addClass('oculto').appendTo(idTabla + " tbody");
            jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(5)").text("INS");
        }
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol +")").find('input[type=text]').val(pDeEmp);
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(3)").text(pcoEmp);            
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(7)").text(pDeEmp);             
        fn_changeDestinatarioCorrectoOtro('tblPersVoBoDocAdm',pfila);
        jQuery(idTabla + " tbody tr:eq(" + pfila + ")").find('#spanObsDocVoboAlert').remove();
    }else{
        bootbox.alert("<h5>Empleado ya esta en lista.</h5>", function() {
            bootbox.hideAll();
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').focus();
        });         
    }
}

function fn_eventPersVoBoDocAdm() {
    fn_eventTblSeleccionGuardaIndex("tblPersVoBoDocAdm", "txtIndexFilaPersVoBoDocAdm");
}

function fn_addEmpVoBoDocAdm() {
    fn_addDestintarioEmiDoc('tblPersVoBoDocAdm');
}

function fn_removeEmpVoBoDocAdm() {
    var noTbl='#tblPersVoBoDocAdm';
    var inVobo=jQuery(noTbl + " tbody tr:eq(" + jQuery('#txtIndexFilaPersVoBoDocAdm').val()*1 + ") td:eq(8)").text();
    if(!!inVobo&&(inVobo==="7"||inVobo==="2"||inVobo==="0")){
        fn_removeTblEmiDocAdm("5", "tblPersVoBoDocAdm", 'txtIndexFilaPersVoBoDocAdm','Empleado Visto Bueno');
    }
}

function fn_buscarEmpleadoVoBoDocAdm(kepPressUser,inputNomb){
    var pnomEmp=jQuery(inputNomb).val(fu_getValorUpperCase(jQuery(inputNomb).val())).val();
    var coDep=(($(inputNomb).parent()).parent()).children().get(1).innerHTML;
    fn_validarBusqEmpleadoVoBoDocAdm(pnomEmp,coDep,inputNomb);
}

function fn_buscarEmpleadoVoBoDocAdmButton(objButton){
    var inputNomb=(jQuery(objButton).parent()).find('input[type=text]');
    var pnomEmp=allTrim(fu_getValorUpperCase(inputNomb.val()));
    var coDep=(($(objButton).parent()).parent()).children().get(1).innerHTML;
    fn_validarBusqEmpleadoVoBoDocAdm(pnomEmp,coDep,inputNomb);
}

function fn_validarBusqEmpleadoVoBoDocAdm(pnomEmp,pcoDep,inputNomb){
    if(!!pcoDep&&allTrim(pcoDep).length>1){
        var p = new Array();
        p[0] = "accion=goBuscaEmpVoBoDocAdm";
        p[1] = "pnomEmp=" /*+ pnomEmp*/;
        p[2] = "pcoDep=" + pcoDep;
        ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
            fn_rptaBuscaDestinatarioFiltroConsulDocRecep(data);
            jQuery('#txtTblPersVoBoFilaWhereButton').val(((jQuery(inputNomb).parent()).parent()).index());
            jQuery('#txtTblPersVoBoColWhereButton').val((jQuery(inputNomb).parent()).index());            
        },'text', false, false, "POST");        
    }else{
        bootbox.alert("<h5>Ingrese Dependencia.</h5>", function() {
            bootbox.hideAll();
//            jQuery(inputNomb).focus();
        });        
    }
    return false;     
}

function fn_accionTblPersVoBoDocAdm(accion,object){
    var nomTbl1='#tblRemitenteNewDocAdm_1';
    var nomTbl2='#tblRemitenteNewDocAdm_2';
    var div1='#divtablaPersVoBoDocAdm';
    var div2='#divTblPersVoBoDocAdm';
    if(accion==="SHOW"){
        jQuery(nomTbl2+' >tbody >tr').eq(0).toggle();
        jQuery(nomTbl2+' >tbody >tr').eq(1).appendTo(nomTbl1+' >tbody');
        jQuery(div1).appendTo(nomTbl2+' >tbody');
        jQuery(object).attr('onclick','fn_accionTblPersVoBoDocAdm(\"HIDE\",this);');
        jQuery(object).find('span').removeClass();
        jQuery(object).find('span').addClass('glyphicon glyphicon-chevron-right');
    }else if(accion==="HIDE"){
        jQuery(nomTbl2+' >tbody >tr').eq(0).toggle();
        jQuery(nomTbl1+' >tbody >tr').eq(2).appendTo(nomTbl2+' >tbody');
        jQuery(div1).appendTo(div2);        
        jQuery(object).attr('onclick','fn_accionTblPersVoBoDocAdm(\"SHOW\",this);');
        jQuery(object).find('span').removeClass();
        jQuery(object).find('span').addClass('glyphicon glyphicon-chevron-left');
    }
}

function fn_tblEmpVoBoDocAdmToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=6";
    arrColMostrar[1] = "coEmpleado=4";
    arrColMostrar[2] = "coDependencia=2";
    return '['+fn_tblDestEmihtml2json('tblPersVoBoDocAdm', 1, arrColMostrar, 11, "1", 6, "BD")+']';
}

function fu_verificarEmpVoBo() {
    var vResult = " Visto Bueno";   
    var vtabla = ["tblPersVoBoDocAdm= Visto Bueno"];
    for (var i = 0; i < vtabla.length; i++) {
        var vauxTabla = vtabla[i].split("=");
        var rpta = fn_verificarExisteDestinatario(vauxTabla[0]);
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "0") {
            vResult = rpta.substr(1) + vauxTabla[1];
            break;
        }else if(nrpta === "2"){
            vResult = "1";
        }else {
            vResult = rpta.substr(0, 1);
        }
    }
    return vResult;    
}

function fn_setTblPersonalVoBo(){
    var nomTbl='#tblPersVoBoDocAdm';
    jQuery(nomTbl+" tbody tr").each(function(index, row) {
        if (index > 0){
            if($(row).hasClass('oculto')){
                $(row).remove();
            }else{
                var accionBD=$(row).find('td:eq(5)').html();
                if(accionBD!=="BD"){
                   if(accionBD==="DEL"){
                       $(row).remove();
                   }else{
                       $(row).find('td:eq(5)').html("BD");
                   } 
                }
            }            
        }
    });
}

function fn_buscarDepVoBoDocAdmButton(objButton){
    var inputNomb=(jQuery(objButton).parent()).find('input[type=text]');
    var pnomDep=allTrim(fu_getValorUpperCase(inputNomb.val()));    
    fn_validarBusqDependenciaVoBoDocAdm(pnomDep,inputNomb);    
}

function fn_buscarDepVoBoDocAdm(kepPressUser,inputNomb){
    var pnomDep=jQuery(inputNomb).val(fu_getValorUpperCase(jQuery(inputNomb).val())).val();
    fn_validarBusqDependenciaVoBoDocAdm(pnomDep,inputNomb);    
}

function fn_validarBusqDependenciaVoBoDocAdm(pnomDep,inputNomb){
//    if(!!pnomDep&&allTrim(pnomDep).length>1){
        var p = new Array();
        p[0] = "accion=goBuscaDepVoBoDocAdm";
        p[1] = "pdeDepen=" + pnomDep;
        ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
            fn_rptaBuscaDestinatarioFiltroConsulDocRecep(data);
            jQuery('#txtTblPersVoBoFilaWhereButton').val(((jQuery(inputNomb).parent()).parent()).index());
            jQuery('#txtTblPersVoBoColWhereButton').val((jQuery(inputNomb).parent()).index());            
        },'text', false, false, "POST");        
//    }else{
//        bootbox.alert("<h5>Ingrese Depedencia.</h5>", function() {
//            bootbox.hideAll();
//            jQuery(inputNomb).focus();
//        });        
//    }
    return false;     
}

function fn_iniDepVoBoDocEmiAdm(){
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
                        var pdesDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                        fu_setDatoDepVoBoDocEmiAdm(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDepVoBoDocEmiAdm(pcodDest,pdesDest);            
        });      
}

function fu_setDatoDepVoBoDocEmiAdm(cod, desc){
    var pfila = jQuery('#txtTblPersVoBoFilaWhereButton').val();
    var pcol = jQuery('#txtTblPersVoBoColWhereButton').val();    
    var idTabla = "#tblPersVoBoDocAdm";
    removeDomId('windowConsultaDestinoEmi');
    var filClone=jQuery(idTabla + " tbody tr:eq(" + pfila + ")").clone();
    jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(desc);
    jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(1)").text(cod);            
    var p = new Array();
    p[0] = "accion=goBuscaEmptblPersonalVoBo";
    p[1] = "pcoDepen=" + cod;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null && allTrim(data.coRespuesta) === "1") {
            var arrCampo = new Array();
            arrCampo[0] = (pcol * 1 + 1) + "=" + cod;//codDependencia
            arrCampo[1] = "3=" + data.coEmpleado;//codEmpleado
            var bResult = fn_validaDestinatarioIntituDuplicado('tblPersVoBoDocAdm', arrCampo, true, pfila*1);
            if (jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(5)").text() === "BD") {
                filClone.find('td:eq(5)').text("DEL");
                filClone.addClass('oculto').appendTo(idTabla + " tbody");
                jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(5)").text("INS");
            }             
            if (bResult) {
                fn_changeDestinatarioCorrectoOtro('tblPersVoBoDocAdm',pfila);
            }else{
                fn_changeDestinatarioIncorrectoOtro('tblPersVoBoDocAdm',pfila);
            }            
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol*1+2 + ")").find('input[type=text]').val(data.deEmpleado);
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(3)").text(data.coEmpleado);            
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(6)").text(desc);
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(7)").text(data.deEmpleado);
            jQuery(idTabla + " tbody tr:eq(" + pfila + ")").find('#spanObsDocVoboAlert').remove();
        }else{
           bootbox.alert(data.deRespuesta);
        }
    },'json', false, false, "POST");
}

function fn_removeTblEmiDocAdm(indexAccionBD, idTabla, indexFilaRemove,msg) {
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
                
                message: " <h5>¿ Esta Seguro de Quitar "+msg+" ?</h5>",
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
       bootbox.alert("Seleccione "+msg+"");
    }
}

function fu_existeVistoBuenoDocAdm(){
    var tblPerVobo='#tblPersVoBoDocAdm';
    var indexCol_inVobo=8;
    var vResult=false;
    jQuery(tblPerVobo+" tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            var in_vb=jQuery(row).find("td:eq(" + indexCol_inVobo + ")").text();
            if(in_vb==='2'||in_vb==='1'){
                vResult=true;
            }
        }
    });
    return vResult;
}

function fn_updateTblVoBoDocAdm() {
    var p = new Array();
    p[0] = "accion=goUpdTlbVoBo";
    p[1] = "pnuAnn=" + jQuery("#nuAnn").val();
    p[2] = "pnuEmi=" + jQuery("#nuEmi").val();
    //p[3] = "pcoDependencia=" + $("#coDepEmi").val();
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null) {
            refreshScript("divtablaVoBoDocAdm", data);
            jQuery("#txtIndexFilaPersVoBoDocAdm").val("-1");
        } else {
            regresarEmitDocumAdm('0');
        }
    },'text', false, true, "POST");
}

function fn_AddPersVoboGrup() {
    var vcmbGrupo = jQuery("#cmbAddPerVoboGrup").val();
    var vTxtValcmbVoboGrupo = jQuery('#txtValcmbVoboGrupo').val();
    if (vcmbGrupo !== "-1" && vTxtValcmbVoboGrupo === "1") {
        var idTabla = 'tblPersVoBoDocAdm';
        var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
        if (rpta) {
            jQuery('#txtValcmbVoboGrupo').val("0");
            var p = new Array();
            p[0] = "accion=goAgregarPersVoboGrupo";
            p[1] = "pcogrupo=" + vcmbGrupo;
            p[2] = "pcoTipDoc=";
            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                fn_rptagregarPersVoBoGrupo(data, idTabla);
            }, 'json', false, false, "POST");
        }
    }
    return false;
}

function fn_rptagregarPersVoBoGrupo(data, idTabla) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            $.each(data.lsDestInstitu, function(i, item) {
                var arrCampo = new Array();
                arrCampo[0] = "1=" + item.coDependencia;
                arrCampo[1] = "3=" + item.coEmpleado;
                var bResult = fn_validaDestinatarioIntituDuplicado(idTabla, arrCampo, false, 0);
                if (bResult) {
                    fn_addPersVoboGrupo(idTabla, item);
                }
            });
            if(!jQuery('#collapseOne').hasClass('in')){
               jQuery('#accordion').find('a.myaccordion-1').click(); 
            }
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
    return false;
}

function fn_addPersVoboGrupo(idTabla, objDes) {
    var pfila = $("#" + idTabla + " tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#" + idTabla + " tbody").index();
    var bDestinOk=true;
    var p = new Array();
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
        switch (index)
        {
            case 0:
                $(this).find('input[type=text]').val(objDes.deDependencia);
                p[0] = allTrim(objDes.deDependencia);
                break;
            case 1:
                $(this).text(objDes.coDependencia);
                break;
            case 2:
                var deEmp=objDes.deEmpleado;
                if(deEmp!==null && !!allTrim(deEmp)){
                    $(this).find('input[type=text]').val(deEmp);
                    p[1] = allTrim(deEmp);
                }
                else{
                    bDestinOk=false;
                }        
                break;
            case 3:
                var coEmp=objDes.coEmpleado;
                if(coEmp!==null && !!allTrim(coEmp)){
                    $(this).text(coEmp);
                }
                else{
                    bDestinOk=false;
                }                 
                break;
            default:
                break;
        }
    });
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(6)").text(p[0]);
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(7)").text(p[1]);
    if(bDestinOk){
        fn_changeDestinatarioCorrectoOtro(idTabla,pfila);
    }
}

function onchangeCmbPerVoBoGrupo() {
    jQuery('#txtValcmbVoboGrupo').val("1");
}

function fn_setPrioridadAllDestinoInstitu(cbmPrd) {
    var coPrioridad = cbmPrd.value;
    var coTiPriLast=jQuery(cbmPrd).find('option:last').val();
    var coTiPriFirst=jQuery(cbmPrd).find('option:first').val();
    if(coTiPriLast==="-1"){
        jQuery(cbmPrd).find('option:last').remove();
    }else if(coTiPriFirst==="-1"){
        jQuery(cbmPrd).find('option:first').remove();
    }    
    if (coPrioridad !== "-1") {
        var idTabla = 'tblDestEmiDocAdm';
        var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
        if (rpta) {
            fn_setPrioridadFilaTblDestIntitu(idTabla, coPrioridad);
        }
    }
    return false;
}


function fn_setPrioridadFilaTblDestIntitu(idTabla, coPrioridad) {
    $("#" + idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            $(row).children('td').each(function(indexx) {
                switch (indexx)
                {
                    case 10:
                        $(this).find('select').val(coPrioridad);
                        break;
                    case 11:
                        var vaccionBD = $(this).text();
                        if (vaccionBD === "BD") {
                            $(this).text("UPD");
                        }
                        break;
                    default:
                        break;
                }
            });
        }
    });
}

/*SEGDI MVALDERA*/
function fn_verDocumentosAnexosRef(cell) {
    var indexFila = (($(cell).parent()).parent()).index();
    var isOk = $("#tblRefEmiDocAdm tbody tr:eq(" + indexFila + ") td").last().html();
    if (isOk === "1") {
        var pnuAnn = ($(cell).parent()).parent().children().get(7).innerHTML;
        var pnuEmi = ($(cell).parent()).parent().children().get(8).innerHTML;
        fn_verAnexosObj(pnuAnn,pnuEmi,"N");
    } else {
        return;
    }
}


function fn_agregarDestinatarioExternoGrupo() {
    var vcmbGrupo = jQuery("#codGrupoDestino").val();
    var ptiDes = jQuery("#sTipoDestinatario").val();
    
    var vTxtValcmbGrupo = jQuery('#txtValcmbGrupo').val();
    console.log("valgrupo"+vTxtValcmbGrupo);
    console.log("ptiDes"+ptiDes);
    console.log("vcmbGrupo"+vcmbGrupo);
    if (vcmbGrupo !== "-1" && vTxtValcmbGrupo !== "-1" && vTxtValcmbGrupo === "1") {
        //if(vTxtValcmbGrupo === "1"){
        
        jQuery('#txtValcmbGrupo').val("0");
        var p = new Array();
        p[0] = "accion=goAgregarDestinatarioExterno";
        p[1] = "pcogrupo=" + vcmbGrupo;
        p[2] = "pcoTipDoc=" + jQuery('#documentoEmiBean').find('#coTipDocAdm').val();
        p[3] = "ptiDes=" + ptiDes;
        
        
        if (ptiDes==="02")
        {
            var idTabla = 'tblDestEmiDocAdmPersJuri';
            var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
            console.log("rpta TIPO 02"+rpta);
            if (rpta) {

                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    console.log("rpta TIPO 02 data::::: -->>"+data);
                    fn_rptagregarDestinatarioExternoGrupo(data, idTabla);
                }, 'json', false, false, "POST");
            }
        }

        if (ptiDes==="03")
        {
            var idTabla = 'tblDestEmiDocAdmCiudadano';
            var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
            if (rpta) {

                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptagregarDestinatarioExternoGrupo(data, idTabla);
                }, 'json', false, false, "POST");
            }
        }

        if (ptiDes==="04")
        {
            var idTabla = 'tblDestEmiDocAdmOtro';
            var rpta = fn_verificarLstDestinatarioCorrecto(idTabla);
            if (rpta) {

                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptagregarDestinatarioExternoGrupo(data, idTabla);
                }, 'json', false, false, "POST");
            }
        }

        //}
    }/*else{
    bootbox.alert("Seleccionar Grupo.."); 
     jQuery("#cmbGrupo").focus();
     }*/
    return false;
}



function fn_rptagregarDestinatarioExternoGrupo(data, idTabla) {
    if (data !== null) {
        console.log('respuetas -->'+data.coRespuesta);
        if (data.coRespuesta === "1") {
            
            $.each(data.lsDestInstitu, function(i, item) {
                var arrCampo = new Array();
                arrCampo[0] = "1=" + item.coDependencia;
                arrCampo[1] = "5=" + item.coEmpleado;
                var bResult  ;
                console.log('idTabla -->'+idTabla);
                if (idTabla==="tblDestEmiDocAdmPersJuri2"){
                //if (idTabla==="tblDestEmiDocAdmPersJuri"){
                    bResult =true;
                }
                else {
                    bResult = fn_validaDestinatarioOtroDuplicado(idTabla, arrCampo, false, 0);
                }
                if (bResult) {
                    fn_addDestintarioExterEmiDocGrupo(idTabla, item);
                }
            });
        } else {
           bootbox.alert(data.deRespuesta);
        }
    }
    return false;
}

function fn_verificarLstDestExternoCorrecto(idTabla) {
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
            return found;
        } else {
           bootbox.alert("Corregir Destinatario");
            return false;
        }
    } else {
       bootbox.alert("Corregir Destinatario");
        return false;
    }
}

function fn_addDestintarioExterEmiDocGrupo(idTabla, objDes) {
    
    
    var esPrimero = $("#" + idTabla + " tbody tr").last().hasClass('oculto');
    var pfila = $("#" + idTabla + " tbody tr:eq(0)").clone().removeClass('oculto').appendTo("#" + idTabla + " tbody").index();
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });
    
    var bDestinOk=true;
    var p = new Array();
    
    if (idTabla==="tblDestEmiDocAdmPersJuri")
        {
 
    
            var ubigeo=objDes.ccodDpto+','+objDes.ccodProv+','+objDes.ccodDist;
            var nom=objDes.ubigeo;

//           if (allTrim(ubigeo).length===0)
//                {
//                    bDestinOk=false;
//                }        

           if (allTrim(objDes.deProveedor).length===0)
                {
                    bDestinOk=false;
                } 

//           if (allTrim(objDes.cdirRemite).length===0)
//                {
//                    bDestinOk=false;
//                }         
        
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(7)").text(objDes.nuRuc);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(0)").find('input[type=text]').val(objDes.deProveedor);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(2)").find('input[type=text]').val(objDes.nuRuc);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtUbigeoOtro]').val(nom=="//"?"":nom);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtCodigoUbigeoOtro]').val(ubigeo==",,"?"":ubigeo);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(10)").find('textarea').val(objDes.cdirRemite);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(11)").find('input[type=text]').val(objDes.cexpCorreoe);
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(12)").find('input[type=text]').val(objDes.cTelefono);

            $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(4)").text(objDes.deProveedor);
            
            if(bDestinOk){
                fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmPersJuri', pfila);
            }
        }
    
    bDestinOk=true;
    

    
    if (idTabla==="tblDestEmiDocAdmCiudadano")
        {

            
            var ubigeo=objDes.ccodDpto+','+objDes.ccodProv+','+objDes.ccodDist;
            var nom=objDes.ubigeo;
            
            if (allTrim(objDes.nuDni).length<8)
            {
                bDestinOk=false;
            }

            if (allTrim(objDes.cdirRemite).length===0)
            {
                bDestinOk=false;
            }            
            
            if (allTrim(ubigeo).length===0)
            {
                bDestinOk=false;
            }            
                        
        
            $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
                switch (index)
                {
                    case 0:
                        $(this).find('input[type=text]').val(objDes.nuDni);
                        jQuery('#txtNroDocumentoCiudadano').val(objDes.nuDni);
                        break;
                    case 2:
                        $(this).find('input[type=text]').val(objDes.deCiudadano);
                        break;
                    case 4:
                        $(this).html(objDes.nuDni);
                        break;
                    case 8:
                        $(this).find('input[id=txtUbigeoOtro]').val(nom);
                        $(this).find('input[id=txtCodigoUbigeoOtro]').val(ubigeo);
                        break;
                    case 9:
                        $(this).find('textarea').val(objDes.cdirRemite=="null"?"":objDes.cdirRemite);
                        break;
                    case 10:
                        $(this).find('input[type=text]').val(objDes.cexpCorreoe=="null"?"":objDes.cexpCorreoe);
                        break;
                    case 11:
                        $(this).find('input[type=text]').val(objDes.cTelefono=="null"?"":objDes.cTelefono);
                        break;
                    default:
                        break;
                }
            });
            
            if(bDestinOk){
                fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmCiudadano', pfila);
            }
    
        }

    
    
//    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td").each(function(index) {
//        if (idTabla==="tblDestEmiDocAdmPersJuri")
//        {
//            switch (index)
//                {
//                    case 0:
//                        $(this).find('input[type=text]').val(objDes.deProveedor);
//                        p[0] = allTrim(objDes.deProveedor);
//                        break;
//                    case 1:
//                        $(this).find('input[type=text]').val(objDes.nuRuc);
//                        p[1] = allTrim(objDes.nuRuc);
//                        break;
//                    case 3:
//                        $(this).find('input[type=text]').val(objDes.ubigeo);
//                        p[2] = allTrim(objDes.nuRuc);
//                        break;                        
//                    case 4:
//                        $(this).find('input[type=text]').val(objDes.cdirRemite);
//                        p[3] = allTrim(objDes.cdirRemite);
//                        break;
////                    case 3:
////                        $(this).text(objDes.coLocal);
////                        break;
//                    default:
//                        break;
//                }        
//                        
//            }            
//                        
//        });

//    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(12)").text(p.join("$#"));

}

/*SEGDI MVALDERA*/
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(7)").text(pnuRuc);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(prazonSocial);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 2) + ")").find('input[type=text]').val(pnuRuc);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtUbigeoOtro]').val(nom=="//"?"":nom);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(9)").find('input[id=txtCodigoUbigeoOtro]').val(ubigeo==",,"?"":ubigeo);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(10)").find('textarea').val(cproDomicil);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(11)").find('input[type=text]').val(cproEmail);
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(12)").find('input[type=text]').val(cproTelefo);
//        
//        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(4)").text(prazonSocial);        
//        if ($("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text() === "BD") {
//            $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text("UPD");
//        }






/*SEGDI MVALDERA*/


function hideTableColumns(idTable, columnsIndexs)
{
	$("#" + idTable + " tr").each(function() {
		for(var i = 0; i < columnsIndexs.length; i++)
		{
			var colIndex = columnsIndexs[i];
			$($(this).find("td")[colIndex]).hide();
		}
	});
}
	function showTableColumns(idTable, columnsIndexs)
{
	$("#" + idTable + " tr").each(function() {
		for(var i = 0; i < columnsIndexs.length; i++)
		{
			var colIndex = columnsIndexs[i];
			$($(this).find("td")[colIndex]).show();
		}
	});
}


function hideAllInfo()
{
    $("#infoObservaciones2").hide();
    $("#infoObservaciones").hide();
}

function showColInfoDetalle()
{
    //yual
    if($("#infoObservaciones").is(':visible')){
            $("#infoObservaciones").hide();
    }       
    else{
        $("#infoObservaciones").show();
    }
        
    
    //$("#infoObservaciones").show();
    $("#infoObservaciones2").hide();
}

function showColInfoObservacion()
{
    $("#infoObservaciones").hide();
    $("#infoObservaciones2").show();
}
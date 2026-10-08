function fn_inicializaConsulEmiDoc(sCoAnnio,fechaActual){
    
    //jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").html("Del: "+fechaActual+" Al: "+fechaActual);
    jQuery('#buscarDocumentoEmiConsulBean').find('#esFiltroFecha').val("1");//rango fecha
    //jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',fechaActual);
    //jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);
    
    /*jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").showDatePicker({
        showDia: false,
        selectTodosMeses:true,
        pressAceptarEvent: function(data) {
            if(data.rbOp==="0"){
                jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val(data.anio);
                if(data.mes==="" && data.anio!==""){
                   jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").html("Año: "+data.anio);
                   jQuery('#buscarDocumentoEmiConsulBean').find('#esFiltroFecha').val("2");//solo año
                }else{
                   jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").html("Año: "+data.anio+"  Mes: "+monthYearArray[data.mes * 1]);  
                   jQuery('#buscarDocumentoEmiConsulBean').find('#esFiltroFecha').val("3");//año y mes
                }
            }else if(data.rbOp==="1"){
                jQuery('#buscarDocumentoEmiConsulBean').find('#esFiltroFecha').val("1");//rango fecha
                jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").html("Del: "+data.fIni+"  Al: "+data.fFin); 
            }
        }
    });*/
    jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    
    jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });    
    jQuery('#buscarDocumentoEmiConsulBean').find('#busCoAnnio').find('option[value=""]').remove();
    pnumFilaSelect=0;
    changeTipoBusqEmiDocuAdmConsul("0");  
}

function changeTipoBusqEmiDocuAdmConsul(tipo) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormEmiDocAdmConsul(tipo);
    //mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormEmiDocAdmConsul(tipo) {
    var validaFiltro = fu_validaFiltroEmiDocAdmConsul(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srConsultaEmisionDoc.do?accion=goInicio", $('#buscarDocumentoEmiConsulBean').serialize(), function(data) {
            refreshScript("divTblConsulDocumentoEmitido", data);
        }, 'text', false, false, "POST");
    }else if(validaFiltro === "2"){//buscar referencia
        ajaxCall("/srConsultaEmisionDoc.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoEmiConsulBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferenciaConsul(data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fu_rptaBuscaDocumentoEnReferenciaConsul(data){
    if(data!==null){
        if(data==="0"){
          alert_Info("Buscar Referencia", "No Existe Documento.");
        }else{
          refreshScript("divTblConsulDocumentoEmitido", data);  
        }
    }
}

function fu_validaFiltroEmiDocAdmConsul(tipo) {
    var valRetorno = "1";
    jQuery('#buscarDocumentoEmiConsulBean').find('#feEmiIni').val(jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#buscarDocumentoEmiConsulBean').find('#feEmiFin').val(jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery('#buscarDocumentoEmiConsulBean').find("esIncluyeFiltro1").is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroEmiDocAdmFiltrarConsul(vFechaActual);     
    }else if(tipo==="1"){
      //verificar si se ingreso datos en los campos de busqueda de referencia
      valRetorno = fu_validarBusquedaXReferenciaConsul(tipo);  
      if(valRetorno==="1"){
        valRetorno = fu_validaFiltroEmiDocAdmBuscarConsul();  
        if(valRetorno==="1"){
           if(pEsIncluyeFiltro){ 
              valRetorno = fu_validaFiltroEmiDocAdmFiltrarConsul(vFechaActual); 
           }else{
              valRetorno = setAnnioNoIncludeFiltroEmiConsul(); 
           }
        }              
      }
    }    
    return valRetorno;
}

function fu_validaFiltroEmiDocAdmBuscarConsul() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoEmiConsulBean();
    
    var vNroEmision = jQuery('#buscarDocumentoEmiConsulBean').find('#busNumEmision').val();
    var vNroDocumento = jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoEmiConsulBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoEmiConsulBean').find('#busAsunto').val();
    var vNumDocRef = allTrim(jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDocRef').val());
    if(!!vAsunto){
        //validar caracteres especiales
        vAsunto=jQuery('#buscarDocumentoEmiConsulBean').find('#busAsunto').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vAsunto))).val();            
        if(allTrim(vAsunto).length >= 0 && allTrim(vAsunto).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }        
    }
    
    if((typeof(vNroEmision)==="undefined" || vNroEmision===null || vNroEmision==="") &&
       (typeof(vNroDocumento)==="undefined" || vNroDocumento===null || vNroDocumento==="") &&
       (typeof(vNroExpediente)==="undefined" || vNroExpediente===null || vNroExpediente==="") &&
       (typeof(vNumDocRef)==="undefined" || vNumDocRef===null || vNumDocRef==="") &&
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

function fu_validarBusquedaXReferenciaConsul(tipo){
    var valRetorno="1";//no buscar por referencia
    if(tipo === "1"){
        var vBuscDestinatario = allTrim(jQuery('#buscarDocumentoEmiConsulBean').find('#busCodDepEmiRef').val());
        var vDeTipoDocAdm = allTrim(jQuery('#buscarDocumentoEmiConsulBean').find('#busCodTipoDocRef').val());
        var vCoAnnioBus = allTrim(jQuery('#buscarDocumentoEmiConsulBean').find('#busCoAnnio').val());
        var vNumDocRef = allTrim(jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDocRef').val());

        if(
                //(typeof(vBuscDestinatario)!=="undefined" && vBuscDestinatario!==null && vBuscDestinatario!=="") &&
          // (typeof(vDeTipoDocAdm)!=="undefined" && vDeTipoDocAdm!==null && vDeTipoDocAdm!=="") &&
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
            else {
                valRetorno = "1";
            }
        }
    }        
    }
    return valRetorno;
}

function fu_validaFiltroEmiDocAdmFiltrarConsul(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaConsul('buscarDocumentoEmiConsulBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoEmiConsulBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }     
           
            var vFeInicio = jQuery('#buscarDocumentoEmiConsulBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoEmiConsulBean').find("#feEmiFin").val();
            
            if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val(pAnnioBusq);                          
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


function fn_buscaDependenciaEmiConsul() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaEmite";
    p[1] = jQuery('#buscarDocumentoEmiConsulBean').find('#txtDepEmiteBus').val() === ' [TODOS]' ? "pdeDepEmite=" : "pdeDepEmite=" + fu_getValorUpperCase(jQuery('#buscarDocumentoEmiConsulBean').find('#txtDepEmiteBus').val());
    ajaxCall("/srConsultaEmisionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaEmiConsul(data);
    },
    'text', false, false, "POST");
}

function fn_rptaBuscaDependenciaEmiConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}
        if(obj){
            if(obj.coRespuesta==="1"){
                jQuery('#buscarDocumentoEmiConsulBean').find('#busCodDepEmiRef').val(obj.coDependencia);
                jQuery('#buscarDocumentoEmiConsulBean').find('#txtDepEmiteBus').val(obj.deDependencia);
                jQuery('#buscarDocumentoEmiConsulBean').find('#busCodTipoDocRef').focus();
            }else{
               bootbox.alert(obj.deRespuesta);
            }
        }else{
            $("body").append(XML_AJAX);
        }        
    }    
}

function fu_setDatoDependenciaEmiConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtDepEmiteBus').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#busCodDepEmiRef').val(cod);
    jQuery('#buscarDocumentoEmiConsulBean').find('#busCodTipoDocRef').focus();
    removeDomId('windowConsultaDestinoEmi');
}

function fn_buscaReferenciaOrigenConsul() {
    var p = new Array();
    p[0] = "accion=goBuscaReferenciaOrigen";
    p[1] = "pnuAnn=" + jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val();
    ajaxCall("/srConsultaEmisionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenConsul(data);
    },
            'text', false, false, "POST");
}

function fn_buscaDependenciaOrigenConsulEmi(){
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaOrigen";
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoEmiConsulBean').find('#coDependencia').val();
    ajaxCall("/srConsultaEmisionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenConsul(data);
    },
            'text', false, false, "POST");    
}

function fn_rptaBuscaReferenciaOrigenConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoReferenOrigenConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtRefOrigen').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#coRefOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
}

function fu_setDatoDependenciaOrigenConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtDepOrigen').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#coDepOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
    jQuery("#coEmpElaboro").val("");
    jQuery("#txtElaboradoPor").val("[TODOS]");    
}

function fn_buscaDestinatarioEmiConsul() {
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val();
    ajaxCall("/srConsultaEmisionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },
            'text', false, false, "POST");
}
function fn_buscaEmpDestino() {
    var p = new Array();
    p[0] = "accion=goBuscaEmpDestino";
    p[1] = "pcoDep=" + jQuery('#buscarDocumentoEmiConsulBean').find('#coDepDestino').val();
    ajaxCall("/srConsultaEmisionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaDestinatarioEmiConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoDestinatarioEmiConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtDestinatario').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDestinoEmi');
    jQuery("#coEmpDestino").val("");
    jQuery("#txtEmpDestino").val("[TODOS]");    
}
function fu_setDatoEmpDestinoConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtEmpDestino').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#coEmpDestino').val(cod);
    removeDomId('windowConsultaEmpDestino');
}

function fn_buscaElaboradoPorConsul() {
    //var codDependencia = jQuery('#buscarDocumentoEmiConsulBean').find('#coDependencia').val();
    var coDepOrigen = jQuery('#buscarDocumentoEmiConsulBean').find('#coDepOrigen').val();
    ajaxCall("/srConsultaEmisionDoc.do?accion=goBuscaElaboradoPor&pcoDep=" + coDepOrigen, '', function(data) {
        fn_rptaBuscaElaboradoPorConsul(data);
    },
            'text', false, false, "POST");
}

function fn_rptaBuscaElaboradoPorConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}

function fu_setDatoElaboradoPorConsul(cod, desc) {
    jQuery('#buscarDocumentoEmiConsulBean').find('#txtElaboradoPor').val(desc);
    jQuery('#buscarDocumentoEmiConsulBean').find('#coEmpElaboro').val(cod);
    removeDomId('windowConsultaElaboradoPor');
}

function fu_eventoTablaEmiDocAdmConsul() {
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
            {"bSortable": true},
            {"bSortable": true},
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
   /* $("#myTableFixed tbody td").hover(
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
                pnumFilaSelect = $(this).index();
            }
            //editarDocumentoRecep($(this).children('td')[12].innerHTML,$(this).children('td')[13].innerHTML,$(this).children('td')[15].innerHTML);
            /* comentado xq los botones de ver documento y ver anexos, ya se encuentran el la tabla
             var sData = $(this).find('td');
             passArrayToHtmlTabla("txtTextIndexSelect",sData);*/


        }
    });
    $("#myTableFixed tbody tr").dblclick(function() {
        //jQuery('#txtTextIndexSelect').val("-1"); 
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

function editarDocumentoEmiConsul() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        ajaxCall("/srConsultaEmisionDoc.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdminConsul", data);
            jQuery('#divEmiDocumentoAdminConsul').hide();
            jQuery('#divNewEmiDocumAdminConsul').show();
            jQuery('#divTblConsulDocumentoEmitido').html("");
        }, 'text', false, false, "POST");
    } else {
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function editarDocumentoEmiConsulClick(pnuAnn,pnuEmi) {
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        ajaxCall("/srConsultaEmisionDoc.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdminConsul", data);
            jQuery('#divEmiDocumentoAdminConsul').hide();
            jQuery('#divNewEmiDocumAdminConsul').show();
            jQuery('#divTblConsulDocumentoEmitido').html("");
        }, 'text', false, false, "POST");
    } else {
       bootbox.alert("Seleccione una fila de la lista");
    }
}

function regresarEmitDocumAdmConsul(){
    jQuery('#divEmiDocumentoAdminConsul').toggle();                                
    jQuery('#divNewEmiDocumAdminConsul').toggle();    
    submitAjaxFormEmiDocAdmConsul(jQuery('#buscarDocumentoEmiConsulBean').find('#tipoBusqueda').val());
    jQuery('#divNewEmiDocumAdminConsul').html("");
}

function fn_verDocumentoEmiConsul(nomForm) {
    var pnuAnn = jQuery('#'+nomForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+nomForm).find('#nuEmi').val();
    var ptiOpe = "0";

    if (!!pnuAnn && !!pnuEmi) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        alert_Info("Emisión :", "Seleccione fila.");
    }

}

function fn_verAnexoEmiConsul(nomForm) {
    var pnuAnn = jQuery('#'+nomForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+nomForm).find('#nuEmi').val();
    var pnuDes = jQuery('#'+nomForm).find('#nuDes').val();
    if (!!pnuAnn && !!pnuEmi) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Emisión :", "Seleccione fila.");
    }
}

function fn_verSeguimientoEmiConsul(nomForm) {
    var pnuAnn = jQuery('#'+nomForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+nomForm).find('#nuEmi').val();
    var pnuDes = jQuery('#'+nomForm).find('#nuDes').val();
    if (!!pnuAnn && !!pnuEmi) {        
            fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione fila.");
    }
}

function upperCaseBuscarDocumentoEmiConsulBean(){
    jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiConsulBean').find('#busNumDoc').val()));
    jQuery('#buscarDocumentoEmiConsulBean').find('#busNumExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiConsulBean').find('#busNumExpediente').val()));
    jQuery('#buscarDocumentoEmiConsulBean').find('#busAsunto').val(fu_getValorUpperCase(jQuery('#buscarDocumentoEmiConsulBean').find('#busAsunto').val()));    
}

function fu_cleanEmiDocAdmConsul(tipo) {
    if (tipo==="1") {
        jQuery("#busNumDoc").val("");
        jQuery("#busNumDocRef").val("");
        jQuery('#buscarDocumentoEmiConsulBean').find("#busNumExpediente").val("");
        jQuery("#busAsunto").val("");
        jQuery("#busCodTipoDocRef option[value=]").prop("selected", "selected");
        jQuery("#txtDepEmiteBus").val(" [TODOS]");
        jQuery("#busCodDepEmiRef").val("");
        jQuery("#busNumEmision").val("");
        jQuery("#esIncluyeFiltro1").prop('checked',false);
        jQuery("#esIncluyeFiltro1").attr('checked',false);
        jQuery("#busCoAnnio").find('option:first').prop("selected", "selected");
    } else if(tipo==="0"){
        jQuery("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery("#prioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#tipoDoc option[value=]").prop("selected", "selected");
//        jQuery("#coRefOrigen").val("");
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#coDepOrigen").val(jQuery('#buscarDocumentoEmiConsulBean').find("#coDependencia").val());
        jQuery("#txtDepOrigen").val(jQuery('#buscarDocumentoEmiConsulBean').find("#deDependencia").val());
        jQuery("#coDepDestino").val("");
        jQuery("#txtDestinatario").val("[TODOS]");
        jQuery("#coEmpElaboro").val("");
        jQuery("#txtElaboradoPor").val("[TODOS]");
        jQuery("#coEmpDestino").val("");
        jQuery("#txtEmpDestino").val("[TODOS]");
        jQuery("#coExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("1");//rango fecha
        //var fechaActual = jQuery("#txtFechaActual").val();
        //jQuery("#fechaFiltro").html("Del: "+fechaActual+" Al: "+fechaActual);
        jQuery("#coAnnio").val(jQuery("#txtAnnioActual").val());
        //jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',fechaActual);
        //jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);
        jQuery('#buscarDocumentoEmiConsulBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});
    }
}

function fu_obtenerEsFiltroFechaConsul(nameForm){
    var opt = jQuery('#'+nameForm).find('#coAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function fu_generarReporteEmiConsul(pformatoReporte){
    var validaFiltro = fu_validaFiltroEmiDocAdmConsul("0");
    if (validaFiltro === "1" || validaFiltro === "2") {
        //ajaxCall("/srConsultaEmisionDoc.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $('#buscarDocumentoEmiConsulBean').serialize(), function(data) {
        ajaxCall("/srConsultaEmisionDoc.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $('#buscarDocumentoEmiConsulBean').serialize(), function(data) {
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

function fu_generarReporteEmiConsulXLS(){
   fu_generarReporteEmiConsul('XLS'); 
}

function fu_generarReporteEmiConsulPDF(){
   fu_generarReporteEmiConsul('PDF');  
}

function setAnnioNoIncludeFiltroEmiConsul(){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaConsul('buscarDocumentoEmiConsulBean');
    var pEsFiltroFecha = jQuery('#buscarDocumentoEmiConsulBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery('#buscarDocumentoEmiConsulBean').find("#feEmiIni").val();
        var vFeFinal = jQuery('#buscarDocumentoEmiConsulBean').find("#feEmiFin").val();

        if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#buscarDocumentoEmiConsulBean').find('#coAnnio').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fn_iniConsDestEmiConsul(){
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
                        fu_setDatoDestinatarioEmiConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioEmiConsul(pcodDest,pdesDest);            
        });    
}

function fn_iniConsRefOriDocEmiConsul(){
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
                        fu_setDatoReferenOrigenConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoReferenOrigenConsul(pcodDest,pdesDest);            
        });
}

function fn_iniConsDepenOrigenEmi(){
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
                        fu_setDatoDependenciaOrigenConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDependenciaOrigenConsul(pcodDest,pdesDest);            
        });    
}

function fn_iniConsDocEmiElaboradoPor(){
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
                        fu_setDatoElaboradoPorConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tblElaboradoPor tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoElaboradoPorConsul(pcodDest,pdesDest);            
        });	    
}
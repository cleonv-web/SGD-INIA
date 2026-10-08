function fn_inicializaConsulRecepDoc(sCoAnnio,fechaActual){
    //jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").html("Año: "+sCoAnnio+"  Mes: "+monthYearArray[obtenerNroMesFecha(fechaActual) * 1]);
    jQuery('#buscarDocumentoRecepConsulBean').find('#esFiltroFecha').val("1");//rango fecha
    //jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',obtenerFechaPrimerDiaMes(fechaActual));
    //jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);
    /*jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").showDatePicker({
        showDia: false,
        selectTodosMeses:true,
        pressAceptarEvent: function(data) {
            if(data.rbOp==="0"){
                jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').val(data.anio);
                if(data.mes==="" && data.anio!==""){
                   jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").html("Año: "+data.anio);
                   jQuery('#buscarDocumentoRecepConsulBean').find('#esFiltroFecha').val("2");//solo año
                }else{
                   jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").html("Año: "+data.anio+"  Mes: "+monthYearArray[data.mes * 1]);  
                   jQuery('#buscarDocumentoRecepConsulBean').find('#esFiltroFecha').val("3");//año y mes
                }
            }else if(data.rbOp==="1"){
                jQuery('#buscarDocumentoRecepConsulBean').find('#esFiltroFecha').val("1");//rango fecha
                jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").html("Del: "+data.fIni+"  Al: "+data.fFin); 
            }
        }
    });*/
    jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});
    jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });    
    jQuery('#buscarDocumentoRecepConsulBean').find('#busCoAnnio').find('option[value=""]').remove();
    pnumFilaSelect=0;
    changeTipoBusqRecepDocuAdmConsul("0");
}

function fu_eventoTablaRecepDocAdmConsul() {
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
            {"bSortable": false},
            {"bSortable": false},
            {"bSortable": true},
            {"sType": "fecha"},                        
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"sType": "fecha"},   
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
    //$("#myTableFixed thead tr").addClass("ui-datatable-fixed-scrollable-header ui-state-default")
    //$("#myTableFixed tbody tr").addClass("bx_sb ui-datatable-fixed-scrollable-body ui-datatable-fixed-data");
    function showdivToolTip(elemento, text)
    {
        $('#divflotante').html(text);
        console.log(elemento);
        var x = elemento.left;
        var y = "42%";//elemento.top + 24;
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
                if (index === 7 || index === 8 || index === 12 || index === 13 || index === 14) {
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
            if (typeof($(this).children('td')[12]) !== "undefined") {
                jQuery('#txtpnuAnn').val($(this).children('td')[1].innerHTML);
                jQuery('#txtpnuEmi').val($(this).children('td')[2].innerHTML);
                jQuery('#txtpnuDes').val($(this).children('td')[3].innerHTML);
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

function changeTipoBusqRecepDocuAdmConsul(tipo){
    jQuery('#buscarDocumentoRecepConsulBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormRecepDocAdmConsul(tipo);
    //mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormRecepDocAdmConsul(tipo){
    var validaFiltro=fu_validaFiltroRecepDocAdmConsul(tipo);
    if (validaFiltro==="1") {
        ajaxCall("/srConsultaRecepcionDoc.do?accion=goInicio",$('#buscarDocumentoRecepConsulBean').serialize(),function(data){
                refreshScript("divTblConsulDocumentoRecepcionado", data);
        }, 'text', false, false, "POST");        
    }else if(validaFiltro === "2"){//buscar referencia
        ajaxCall("/srConsultaRecepcionDoc.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoRecepConsulBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferenciaRecepConsul(data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fu_rptaBuscaDocumentoEnReferenciaRecepConsul(data){
    if(data!==null){
        if(data==="0"){
          alert_Info("Buscar Referencia", "No Existe Documento.");
        }else{
          refreshScript("divTblConsulDocumentoRecepcionado", data);  
        }
    }
}

function fu_validaFiltroRecepDocAdmConsul(tipo){
    var valRetorno="1";
   
    jQuery('#buscarDocumentoRecepConsulBean').find('#feEmiIni').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#buscarDocumentoRecepConsulBean').find('#feEmiFin').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery('#buscarDocumentoRecepConsulBean').find("esIncluyeFiltro1").is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroRecepDocAdmFiltrarConsul(vFechaActual);     
    }else if(tipo==="1"){
      //verificar si se ingreso datos en los campos de busqueda de referencia
      valRetorno = fu_validarBusquedaXReferenciaRecepConsul(tipo);  
      if(valRetorno==="1"){
        valRetorno = fu_validaFiltroRecepDocAdmBuscarConsul();  
        if(valRetorno==="1"){
           if(pEsIncluyeFiltro){ 
              valRetorno = fu_validaFiltroRecepDocAdmFiltrarConsul(vFechaActual); 
           }else{
              valRetorno = setAnnioNoIncludeFiltroRecepConsul();  
           }
        }              
      }        
    }    
    return valRetorno;
}

function fu_validaFiltroRecepDocAdmFiltrarConsul(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaConsul('buscarDocumentoRecepConsulBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoRecepConsulBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }     

            var vFeInicio = jQuery('#buscarDocumentoRecepConsulBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoRecepConsulBean').find("#feEmiFin").val();
            
            if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').val(pAnnioBusq);                          
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

function fu_validarBusquedaXReferenciaRecepConsul(tipo){
    var valRetorno="1";//no buscar por referencia
    if(tipo === "1"){
        var vBuscDestinatario = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busCodDepEmiRef').val());
        var vDeTipoDocAdm = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busCodTipoDocRef').val());
        var vCoAnnioBus = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busCoAnnio').val());
        var vNumDocRef = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDocRef').val());

        if(
                //(typeof(vBuscDestinatario)!=="undefined" && vBuscDestinatario!==null && vBuscDestinatario!=="") &&
           //(typeof(vDeTipoDocAdm)!=="undefined" && vDeTipoDocAdm!==null && vDeTipoDocAdm!=="") &&
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

function fu_validaFiltroRecepDocAdmBuscarConsul() {
    var valRetorno = "1";
    
    upperCaseConsulBuscarRecepDocAdmBean();
    
    var vNroDocumento = jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoRecepConsulBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoRecepConsulBean').find('#busAsunto').val();
    var vRemitente = jQuery('#buscarDocumentoRecepConsulBean').find('#busRemitente').val();
    var vCoAnnioBus = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busCoAnnio').val());
    var vNumDocRef = allTrim(jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDocRef').val());
    var pEsIncluyeFiltro = jQuery('#buscarDocumentoRecepConsulBean').find("esIncluyeFiltro1").is(':checked');
    
    if(!!vAsunto){
        //validar caracteres especiales
        vAsunto=jQuery('#buscarDocumentoRecepConsulBean').find('#busAsunto').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vAsunto))).val();        
        if(allTrim(vAsunto).length >= 0 && allTrim(vAsunto).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }         
    }   
    
    if(!!vRemitente){
        //validar caracteres especiales
        vRemitente=jQuery('#buscarDocumentoRecepConsulBean').find('#busRemitente').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vRemitente))).val();        
        if(allTrim(vRemitente).length >= 0 && allTrim(vRemitente).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el remitente.");
            valRetorno = "0";
            return valRetorno;
        }         
    }
    
    if(!!vNroExpediente){
        //validar caracteres especiales
        vNroExpediente=jQuery('#buscarDocumentoRecepConsulBean').find('#busNumExpediente').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vNroExpediente))).val();        
        if(allTrim(vNroExpediente).length >= 0 && allTrim(vNroExpediente).length <= 1){  //El texto es entre 0 y 1 caracteres
            alert_Warning("Buscar: ","Debe ingresar como mínimo de 2 caracteres en el Nro. de expediente.");
            valRetorno = "0";
            return valRetorno;
        }         
    }
     
    if((typeof(vNroDocumento)==="undefined" || vNroDocumento===null || vNroDocumento==="") &&
       (typeof(vNroExpediente)==="undefined" || vNroExpediente===null || vNroExpediente==="") &&
       (typeof(vAsunto)==="undefined" || vAsunto===null || vAsunto==="")&&
       ((typeof(vNumDocRef)==="undefined" || vNumDocRef===null || vNumDocRef==="") && !pEsIncluyeFiltro   )&&
       (typeof(vRemitente)==="undefined" || vRemitente===null || vRemitente==="")){
        
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    return valRetorno;
}

function editarDocumentoRecepClickConsul(pnuAnn,pnuEmi,pnuDes){
      if(pnuAnn !== "" && pnuEmi !== ""){  
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;	
        p[1] = "pnuEmi=" + pnuEmi;        
        p[2] = "pnuDes=" + pnuDes;
        ajaxCall("/srConsultaRecepcionDoc.do?accion=goDetalleDocumento",p.join("&"),function(data){
                refreshScript("divNewRecepDocumAdminConsul", data);
                jQuery('#divTblConsulDocumentoRecepcionado').html("");
                jQuery('#divEmiDocumentoAdminConsul').hide();
                jQuery('#divNewRecepDocumAdminConsul').show();
                //fn_cargaToolBarRec();
                //var sEstadoDocAdm = jQuery('#documentoRecepBean').find('#esDocRec').val();
                //fu_cargaEdicionDocAdm("01",sEstadoDocAdm);
                //jQuery('#txtpnuAnn').val(pnuAnn);  
        }, 'text', false, false, "POST");              
    }else{
       bootbox.alert("Registro No existe.");
    }
}

function editarDocumentoRecepConsul(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
      if(pnuAnn !== ""){  
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;	
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();        
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        ajaxCall("/srConsultaRecepcionDoc.do?accion=goDetalleDocumento",p.join("&"),function(data){
                refreshScript("divNewRecepDocumAdminConsul", data);
                jQuery('#divTblConsulDocumentoRecepcionado').html("");
                jQuery('#divEmiDocumentoAdminConsul').hide();
                jQuery('#divNewRecepDocumAdminConsul').show();
                //fn_cargaToolBarRec();
                //var sEstadoDocAdm = jQuery('#documentoRecepBean').find('#esDocRec').val();
                //fu_cargaEdicionDocAdm("01",sEstadoDocAdm);
        }, 'text', false, false, "POST");              
    }else{
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
}

function regresarRecepDocumAdmConsul(){
    jQuery('#divNewRecepDocumAdminConsul').toggle();                                
    jQuery('#divEmiDocumentoAdminConsul').toggle();    
    submitAjaxFormRecepDocAdmConsul(jQuery('#buscarDocumentoRecepConsulBean').find('#tipoBusqueda').val());
    jQuery('#divNewRecepDocumAdminConsul').html("");
}

function fn_verSeguimientoConsulRec() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = jQuery('#txtpnuDes').val();
    if (pnuAnn) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
}

function fn_verAnexoConsulRec() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = jQuery('#txtpnuDes').val();
    if (pnuAnn) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
}

function upperCaseConsulBuscarRecepDocAdmBean(){
    jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#busNumDoc').val()));
    jQuery('#buscarDocumentoRecepConsulBean').find('#busNumExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#busNumExpediente').val()));
    jQuery('#buscarDocumentoRecepConsulBean').find('#busAsunto').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#busAsunto').val()));
    jQuery('#buscarDocumentoRecepConsulBean').find('#busRemitente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#busRemitente').val()));
    
}

function fu_setDatoDependenciaRecepConsul(cod, desc) {
    jQuery('#buscarDocumentoRecepConsulBean').find('#txtDepEmiteBus').val(desc);
    jQuery('#buscarDocumentoRecepConsulBean').find('#busCodDepEmiRef').val(cod);
    jQuery('#buscarDocumentoRecepConsulBean').find('#busCodTipoDocRef').focus();
    removeDomId('windowConsultaDestinoEmi');
}

function fn_buscaDependenciaRecepConsul() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaEmite";
    p[1] = jQuery('#buscarDocumentoRecepConsulBean').find('#txtDepEmiteBus').val() === ' [TODOS]' ? "pdeDepEmite=" : "pdeDepEmite=" + fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#txtDepEmiteBus').val());
    ajaxCall("/srConsultaRecepcionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaRecepConsul(data);
    },
    'text', false, false, "POST");
}

function fn_rptaBuscaDependenciaRecepConsul(XML_AJAX) {
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}
        if(obj){
            if(obj.coRespuesta==="1"){
                jQuery('#buscarDocumentoRecepConsulBean').find('#busCodDepEmiRef').val(obj.coDependencia);
                jQuery('#buscarDocumentoRecepConsulBean').find('#txtDepEmiteBus').val(obj.deDependencia);
                jQuery('#buscarDocumentoRecepConsulBean').find('#busCodTipoDocRef').focus();
            }else{
               bootbox.alert(obj.deRespuesta);
            }
        }else{
            $("body").append(XML_AJAX);
        }        
    }      
}

function fu_cleanConsulRecepDocAdm(tipo){
    if(tipo==="1"){
        jQuery("#busNumDoc").val("");
        jQuery('#buscarDocumentoRecepConsulBean').find("#busNumExpediente").val("");
        jQuery("#busAsunto").val("");
        jQuery("#busRemitente").val("");
        jQuery("#busCodTipoDocRef option[value=]").prop("selected", "selected");
        jQuery("#feEmiIni").val("");
        jQuery("#feEmiFin").val("");
        jQuery("#busNumDocRef").val("");
        jQuery("#busCodDepEmiRef").val("");
        jQuery("#txtDepEmiteBus").val(" [TODOS]");
        jQuery("#esIncluyeFiltro1").prop('checked',false);
        jQuery("#esIncluyeFiltro1").attr('checked',false);                
        jQuery("#busCoAnnio").find('option:first').prop("selected", "selected");
    }else{
        jQuery("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery("#prioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#coTipoDocAdm option[value=]").prop("selected", "selected");
        jQuery("#coDepRemite").val("");
        jQuery("#txtRemitente").val("[TODOS]");
        jQuery("#coDepDestino").val(jQuery('#buscarDocumentoRecepConsulBean').find("#coDependencia").val());
        jQuery("#txtDestinatario").val(jQuery('#buscarDocumentoRecepConsulBean').find("#deDependencia").val());
        jQuery("#coExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("1");
        //var fechaActual = jQuery("#txtFechaActual").val();
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val()+"  Mes: "+monthYearArray[obtenerNroMesFecha(fechaActual) * 1]);
        //jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',obtenerFechaPrimerDiaMes(fechaActual));
        //jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);        
        jQuery("#coAnnio").val(jQuery("#txtAnnioActual").val());          
        jQuery('#buscarDocumentoRecepConsulBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});        
    }
    
}

function fn_rptaBuscaRemitenteFiltroConsulDocRecep(XML_AJAX){
   if(XML_AJAX !== null){
       $("body").append(XML_AJAX);
   }    
}

function fn_buscaRemitenteFiltroConsulDocRecep(){
    var p = new Array();
    p[0] = "accion=goBuscaRemitente";	    
    p[1] = "pnuAnn=" + jQuery('#coAnnio').val();	    
    ajaxCall("/srConsultaRecepcionDoc.do",p.join("&"),function(data){
           fn_rptaBuscaRemitenteFiltroConsulDocRecep(data); 
        },
    'text', false, false, "POST");       
}

function fu_setDatoRemitenteFiltroConsulDocRecep(cod,desc){
    jQuery('#txtRemitente').val(desc);
    jQuery('#coDepRemite').val(cod);
    removeDomId('windowConsultaRem');
}

function fn_buscaDestinatarioFiltroConsulDocRecep(){
    var p = new Array();    
    p[0] = "accion=goBuscaDestinatario";	    
    //p[1] = "pnuAnn=" + jQuery('#coAnnio').val();    
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoRecepConsulBean').find('#coDependencia').val();
    ajaxCall("/srConsultaRecepcionDoc.do",p.join("&"),function(data){
           fn_rptaBuscaDestinatarioFiltroConsulDocRecep(data); 
        },
    'text', false, false, "POST");       
}
function fn_buscaDestinatarioFiltroConsulCodEmpDest() {
    var p = new Array();
    p[0] = "accion=goBuscaEmpDestino";
    //p[1] = "pcoDepen=" + jQuery('#buscarDocumentoRecepConsulBean').find('#coDepDestino').val();
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoRecepConsulBean').find('#coDepDestino').val();
    ajaxCall("/srConsultaRecepcionDoc.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioFiltroConsulDocRecep(data);
    },
            'text', false, false, "POST");
}
function fu_setDatoEmpDestinoConsulRecep(cod, desc) {
    jQuery('#buscarDocumentoRecepConsulBean').find('#txtEmpDestino').val(desc);
    jQuery('#buscarDocumentoRecepConsulBean').find('#coEmpDestino').val(cod);
    removeDomId('windowConsultaEmpDestino');
}

function fn_rptaBuscaDestinatarioFiltroConsulDocRecep(XML_AJAX){
   if(XML_AJAX !== null){
       $("body").append(XML_AJAX);
   }    
}

function fu_setDatoDestinatarioFiltroConsulDocRecep(cod,desc){
    jQuery('#buscarDocumentoRecepConsulBean').find('#txtDestinatario').val(desc);
    jQuery('#buscarDocumentoRecepConsulBean').find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDest');
}

function fu_generarReporteRecepConsul(pformatoReporte){
    var validaFiltro = fu_validaFiltroRecepDocAdmConsul("0");
    if (validaFiltro === "1" || validaFiltro === "2") {
        //ajaxCall("/srConsultaRecepcionDoc.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $('#buscarDocumentoRecepConsulBean').serialize(), function(data) {
        ajaxCall("/srConsultaRecepcionDoc.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $('#buscarDocumentoRecepConsulBean').serialize(), function(data) {
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function (data){
                        fn_generaDocDesktopOnly(data.noUrl,data.noDoc,function (data){
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

function fu_generarReporteRecepConsulXLS(){
   fu_generarReporteRecepConsul('XLS'); 
}

function fu_generarReporteRecepConsulPDF(){
   fu_generarReporteRecepConsul('PDF');  
}

function setAnnioNoIncludeFiltroRecepConsul(){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaConsul('buscarDocumentoRecepConsulBean');
    var pEsFiltroFecha = jQuery('#buscarDocumentoRecepConsulBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery('#buscarDocumentoRecepConsulBean').find("#feEmiIni").val();
        var vFeFinal = jQuery('#buscarDocumentoRecepConsulBean').find("#feEmiFin").val();

        if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#buscarDocumentoRecepConsulBean').find('#coAnnio').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}
function fn_iniDocMensajeria(){
    $('#buscarDocumentoRecepMensajeriaBean').find('#esFiltroFecha').val("2");//año
    $('#buscarDocumentoRecepMensajeriaBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
    
    $('#buscarDocumentoRecepMensajeriaBean').find('#esFiltroFechaEnvMsj').val("1");//hoy
    $('#buscarDocumentoRecepMensajeriaBean').find("#fechaFiltroEnvMsj").showDatePicker({defaultOpcionSelected: 1});
    pnumFilaSelect=0;
    changeTipoBusqMensajeriaRecep("0");
}
function changeTipoBusqMensajeriaRecep(tipo) {
    $('#buscarDocumentoRecepMensajeriaBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormBusDocMensajeriaRecep(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormBusDocMensajeriaRecep(tipo) {
    var validaFiltro = fu_validaFormBusDocMensajeria(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srMensajeriaGestion.do?accion=goInicio", $('#buscarDocumentoRecepMensajeriaBean').serialize(), function(data) {
            refreshScript("divTablaDocMensajeriaRecep", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_cleanBusDocMensajeria(tipo) {
    var noForm='#buscarDocumentoRecepMensajeriaBean';
    if (tipo==="1") {
        
        $(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        $(noForm).find("#esIncluyeFiltro1").attr('checked',false);
    } else if(tipo==="0"){
        $(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    
        $(noForm).find("#esFiltroFecha").val("1");//hoy
        $(noForm).find("#coAnnio").val(jQuery("#txtAnnioActual").val());
        $(noForm).find("#coEstadoDoc option[value=0]").prop("selected", "selected");        
        $(noForm).find("#coTipoEmisor option[value=]").prop("selected", "selected");
        $(noForm).find("#coTipoDoc option[value=]").prop("selected", "selected");
        $(noForm).find("#coRemitente").val("");
        $(noForm).find("#txtRemitente").val("[TODOS]");
        $(noForm).find("#coDepDestino").val("");
        $(noForm).find("#txtDestinatario").val("[TODOS]");  
        
        $(noForm).find("#busDesti").val("");
        $(noForm).find("#busNumExpediente").val("");
        $(noForm).find("#busNumDoc").val("");
        $(noForm).find("#busAsunto").val("");
        
    }
}

function fu_validaFormBusDocMensajeria(tipo) {
    var valRetorno = "1";
    $('#buscarDocumentoRecepMensajeriaBean').find('#feEmiIni').val($('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    $('#buscarDocumentoRecepMensajeriaBean').find('#feEmiFin').val($('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    $('#buscarDocumentoRecepMensajeriaBean').find('#feEmiIniEnvMSJ').val($('#buscarDocumentoRecepMensajeriaBean').find('#coAnnioEnvio').parent('td').find('#fechaFiltroEnvMsj').attr('fini'));
    $('#buscarDocumentoRecepMensajeriaBean').find('#feEmiFinEnvMSJ').val($('#buscarDocumentoRecepMensajeriaBean').find('#coAnnioEnvio').parent('td').find('#fechaFiltroEnvMsj').attr('ffin')); 
    
    var pEsIncluyeFiltro = $('#buscarDocumentoRecepMensajeriaBean').find('#esIncluyeFiltro1').is(':checked');
    var vFechaActual = $('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFechasFormBusDocMensajeria(vFechaActual);  
    }else if(tipo==="1"){
        valRetorno = fu_validaBusDocMensajeria();
        if(valRetorno==="1"){
            if(pEsIncluyeFiltro){
               valRetorno = fu_validaFechasFormBusDocMensajeria(vFechaActual); 
            }else{
               valRetorno = setAnnioNoIncludeFiltroDocMensajeria();
            }
        }
    }    
    return valRetorno;
}

function setAnnioNoIncludeFiltroDocMensajeria(){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaMP('buscarDocumentoRecepMensajeriaBean');
    var pEsFiltroFecha = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#feEmiIni").val();
        var vFeFinal = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#feEmiFin").val();

        if(valRetorno==="1"){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fu_validaBusDocMensajeria() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoMensajeria();
    
    var vNroDocumento = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busAsunto').val();
    var vBusquedaTipoPersona = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busResultado').val();
    
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

function upperCaseBuscarDocumentoMensajeria(){
    jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumDoc').val()));
    jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busNumExpediente').val()));
    jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busAsunto').val(fu_getValorUpperCase(jQuery('#buscarDocumentoRecepMensajeriaBean').find('#busAsunto').val()));
}

function fu_validaFechasFormBusDocMensajeria(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaMensajeria('buscarDocumentoRecepMensajeriaBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }   
            
            var vFeInicio = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoRecepMensajeriaBean').find("#feEmiFin").val();
            if(valRetorno==="1"){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoRecepMensajeriaBean').find('#coAnnio').val(pAnnioBusq);                          
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

function fu_obtenerEsFiltroFechaMensajeria(nameForm){
    var opt = jQuery('#'+nameForm).find('#coAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function fu_iniTblDocMensajeriaRecep(){
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
            {"bSortable": true},
                    
           
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},    
            {"bSortable": true},
             {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
           /* {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": false}*/
        ]
    }); 
    $.fn.dataTableExt.oSort['fecha-asc'] = function(a, b) {
        var ukDatea = a.split('/');
        var ukDateb = b.split('/');

        var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
        var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

        return ((x < y) ? -1 : ((x > y) ? 1 : 0));
    };

    $.fn.dataTableExt.oSort['fecha-desc'] = function(a, b) {
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
   /* $("#myTableFixed tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if ((index >= 3 && index <= 14 )|| index === 17 || index === 18  ) {
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
                $('#txtpnuAnn').val($(this).children('td')[0].innerHTML);
                $('#txtpnuEmi').val($(this).children('td')[1].innerHTML);
                pnumFilaSelect = $(this).index();
            }
            //editarDocumentoRecep($(this).children('td')[12].innerHTML,$(this).children('td')[13].innerHTML,$(this).children('td')[15].innerHTML);
            /* comentado xq los botones de ver documento y ver anexos, ya se encuentran el la tabla
             var sData = $(this).find('td');
             passArrayToHtmlTabla("txtTextIndexSelect",sData);*/


        }
    });
    if($('#myTableFixed >tbody >tr').length > 0){
        //$('#myTableFixed >tbody >tr').eq(0).addClass('row_selected');
        //var pauxNumFilaSelect = typeof(pnumFilaSelect)!=="undefined"? pnumFilaSelect:0;
        try{
            if($('#myTableFixed >tbody >tr').eq(pnumFilaSelect).length === 1){
                $('#myTableFixed >tbody >tr').eq(pnumFilaSelect).trigger("click");
                $('#myTableFixed >tbody >tr').eq(pnumFilaSelect).focus();
            }else{
                pnumFilaSelect=0;
            }
        }catch(ex){
            pnumFilaSelect=0;
        }
    }
    $('#myTableFixed >tbody >tr').keydown(function(evento){
       if(evento.which===38){//up
            if (jQuery("#myTableFixed >tbody >tr").eq(pnumFilaSelect).prev('tr').length===0){
                pnumFilaSelect=$("#myTableFixed >tbody >tr").length;
            }
            pnumFilaSelect--;
            $("#myTableFixed >tbody >tr").eq(pnumFilaSelect).trigger("click");
            $("#myTableFixed >tbody >tr").eq(pnumFilaSelect).focus();
       }else if(evento.which===40){//down
            if ($("#myTableFixed >tbody >tr").eq(pnumFilaSelect).next('tr').length===0){
                pnumFilaSelect=-1;
            }           
            pnumFilaSelect++;
            $("#myTableFixed >tbody >tr").eq(pnumFilaSelect).trigger("click");               
            $("#myTableFixed >tbody >tr").eq(pnumFilaSelect).focus();           
       }
       evento.preventDefault();
    });    
}
function fn_verDocMensajeriaRecep(){
    var pnuAnn=$('#txtpnuAnn').val();
    var pnuEmi=$('#txtpnuEmi').val();
    var ptiOpe = "0";
    if(!!pnuAnn&&!!pnuEmi){
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    }
}
function fn_verAnexoMensajeriaRecep(){
    var pnuAnn=$('#txtpnuAnn').val();
    var pnuEmi=$('#txtpnuEmi').val();
    var pnuDes = "N";
    if(!!pnuAnn&&!!pnuEmi){    
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);	
    }
}
function fn_verSeguimientoMensajeriaRecep(){
    var pnuAnn=$('#txtpnuAnn').val();
    var pnuEmi=$('#txtpnuEmi').val();
    var pnuDes = "N";
    if(!!pnuAnn&&!!pnuEmi){    
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);		
    }
}
function fn_EnviarMensajeriaRecep(){
    var pnuAnn = allTrim($('#txtpnuAnn').val());    
    var pnuEmi = allTrim($('#txtpnuEmi').val());
    var validar=$('#txtSelecctionOptionValidar').val(); 
    if(validar!=""){
        if(validar.indexOf("0")>-1){
            bootbox.alert('Verificar por favor.!</br>El documento no puede enviar por que está en estado PENDIENTE de recepción.');
        }
        else {
             fn_goEnviarMensajeriaRecep(pnuAnn,pnuEmi);
        }
    }
}

function fn_goEnviarMensajeriaRecep(pnuAnn,pnuEmi){
    if($('#divTablaDocMensajeriaRecep').length===1){
        if(!!pnuAnn&&!!pnuEmi){
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;
            p[1] = "pnuEmi=" + pnuEmi;
            p[2] = "codigos=" + $('#txtSelecctionOption').val();
            ajaxCall("/srMensajeriaGestion.do?accion=goEnviarDocMensajeria", p.join("&"), function(data) {
                $('#divBuscarDocExtRecep').hide();
                $('#divDocExtRecep').show();
                refreshScript("divDocExtRecep", data);
                $('#divTablaDocExtRecep').html("");
                fn_cargaToolBarDocExtRecep();
            }, 'text', false, false, "POST");
        }
    }
}
function fn_optionSelecction(){
    var codigo="";
    var validar="";
    $('.esOptionCheck input').each(function() {  
        if($(this).is(':checked')){
        codigo+=$(this).attr("nuAnn")+$(this).attr("nuEmi")+",";
        validar+=$(this).attr("docEstadoMsj");
        }
    });
    $('#txtSelecctionOption').val(codigo);
    $('#txtSelecctionOptionValidar').val(validar);
}

function fn_optionSelecctionDetalleMensajDest(){
    var codigo="";
    var ambito="";
    $('.esOptionCheckDetalle input').each(function() {  
        if($(this).is(':checked')){
            codigo+=$(this).attr("nuAnn")+$(this).attr("nuEmi")+$(this).attr("nuDes")+",";
            if(ambito.indexOf($(this).attr("ambito"))<0){
                ambito+=$(this).attr("ambito");
            }
        }
    });
    $('#codigo').val(codigo);
    $('#txtDetalleDestiMensAmbitos').val(ambito);
    
}
function fn_optionSelecctionDetalleMensajDestAllCheckedFalse(){
    $('.esOptionCheckDetalle input').each(function() {                   
            $(this).prop( "checked", false);         
    }); 
}
function fn_optionSelecctionDetalleMensajDestAll(thiss){
    $('.esOptionCheckDetalle input').each(function() {          
        if($(thiss).is(':checked')){
            $(this).prop( "checked", true);
        }
        else {
            $(this).prop( "checked", false);
        }
    });
    fn_optionSelecctionDetalleMensajDest();
}
function fn_iniDocEditMensajeria(){
    return false;
    var noForm='documentoRecepMensajeriaBean';
    $("#progressAlta").hide();
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });    
    $('#'+noForm).find("#tiEmi").find('option:last').remove();  
    fn_changeTipoRemiDocExteRecep($('#'+noForm).find("#tiEmi"),"1");
    $('#'+noForm).find("#nuCorrExp").change(function() {
        fn_isExpedienteDuplicado(noForm);
    });
    $('#'+noForm).find("#feVence").change(function() {
       $('#envExpedienteEmiBean').val("1"); 
    });
    $('#'+noForm).find("#coProceso").change(function() {
       $('#envExpedienteEmiBean').val("1"); 
    });
    $('#'+noForm).find("#deDocSig").change(function() {
       $('#envDocumentoEmiBean').val("1"); 
    });
    $('#'+noForm).find("#nuFolios").change(function() {
       $('#envDocumentoEmiBean').val("1"); 
    });    
    $('#'+noForm).find("#deAsu").change(function() {
       $('#envDocumentoEmiBean').val("1"); 
    });     
    $('#'+noForm).find("#nuDniAux").change(function() {
        fn_getCiudadanoRemDocExtRec();
    });
    $('#'+noForm).find("#tiEmi").change(function() {
       $('#envRemitenteEmiBean').val("1");  
    });    
    if (!!$('#'+noForm).find("#nuEmi").val()) {
        var esDocEmi=$('#'+noForm).find("#coEsDocEmiMp").val();
        if(!!esDocEmi&&esDocEmi==="5"){
            $('#'+noForm).find("#coTipDocAdm").focus();
        }   
        fn_actualizaFormDocExtRecEstado(esDocEmi,noForm);        
    }else{
        $('#'+noForm).find('#ullsEstDocEmiAdm').html('');        
        if($('#'+noForm).find('#estDocEmiAdm').hasClass('btn-group')){
          $('#'+noForm).find('#estDocEmiAdm').removeClass('btn-group');          
        }
        $('#'+noForm).find('#estDocEmiAdm').find('button').last().hide();
        /*if(jQuery('#'+noForm).find("#coTipDocAdm").val()===""){
            jQuery('#'+noForm).find("#coTipDocAdm").append('<option value="-1" selected="selected"/>');        
        }*/
        //jQuery('#'+noForm).find("#nuCorrExp").focus();        
        $('#'+noForm).find("#coProceso").focus();        
    }    
}
function fn_eventdocMensajeria() {
    fn_eventTblSeleccionGuardaIndex("tblDocMensajeria", "txtIndexFilaDocMensajeria");
}


function regresarDocMensajeBusq(pclickBtn) {
    if (pclickBtn === "1") {
        var vEsDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        if(vEsDocEmi==="5" || vEsDocEmi==="7"){
            var rpta = fu_verificarChangeDocEnviarMensajeria();
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
                        continueRegresarBuscarDocMensajeria();
                      }                      
                    },                          
                    NO: {
                      label: "NO",
                      className: "btn-primary"
                    }
                  }
                });
            }else{
                continueRegresarBuscarDocMensajeria();
            }
        }else{
            continueRegresarBuscarDocMensajeria();
        }
    }else{
      continueRegresarBuscarDocMensajeria();
    }
}

function fu_verificarChangeDocEnviarMensajeria() {//si es "1" necesita grabar el documento.
    jQuery('#documentoRecepMensajeriaBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmi();
    var rpta = fn_validarEnvioGrabaDocEmiAdm(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"||!(!!jQuery('#documentoRecepMensajeriaBean').find('#nuEmi').val()&&!!jQuery('#documentoRecepMensajeriaBean').find('#nuAnn').val())) {
        return "1";
    } else {
        //alert_Info("",rpta.substr(1));
        return rpta.substr(1);
    }
}
function continueRegresarBuscarDocMensajeria(){
    jQuery('#divEmiDocumentoAdmin').toggle();                                
    jQuery('#divNewEmiDocumAdmin').toggle(); 
    submitAjaxFormEmiDocMensajeria(jQuery('#buscarDocumentoRecepMensajeriaBean').find('#sTipoBusqueda').val());
    jQuery('#divDocExtRecep').html("");        
    //mostrarOcultarDivBusqFiltro2();
}
function submitAjaxFormEmiDocMensajeria(tipo) {
    ajaxCall("/srMensajeriaGestion.do?accion=goInicio", $('#buscarDocumentoRecepMensajeriaBean').serialize(), function(data) {
         $('#divBuscarDocExtRecep').show();
                $('#divDocExtRecep').hide();
        refreshScript("divTablaDocMensajeriaRecep", data);
    }, 'text', false, false, "POST");
    return false;
}

function fn_tipomensajerochange() {
    var noForm='documentoRecepMensajeriaBean';
    $('#'+noForm).find('#feplamsj').val('');
    var p = new Array();
    p[0] = "accion=goListaDocResposable";
    p[1] = "vcoTipMensajero=" + $('#detipmsj').val();
    p[2] = "vcoTipoAmbito=" + $('#deambito').val();
    p[3] = "vcoTipoEnvio=" + $('#detipenv').val();
    ajaxCall("/srMensajeriaGestion.do", p.join("&"), function(data) {
        refreshScript("divDetalleResponsableMensajeria", data);
    }, 'text', false, false, "POST");
}


function fu_grabarMensajeriaDocumento(){    
    var noForm='documentoRecepMensajeriaBean';
    var codigosDestinos=$('#'+noForm).find("#codigo").val();  
    if(codigosDestinos != null && codigosDestinos!=""  ){
        var valRetorno=validarFormDocumentoMensaje(noForm);  
        if(valRetorno==="1"){
            var cadenaJson=fn_buildSendJsontoServerDocMensajeria(noForm);
            
            //verificar si necesita grabar el documento.
            var rpta = fu_verificarChangeDocumentoMensajeria(noForm,cadenaJson);
            var nrpta = rpta.substr(0,1);
            if (nrpta === "1") {                
                ajaxCallSendJson("/srMensajeriaGestion.do?accion=goGrabarDocumentoMensajeria", cadenaJson, function(data) {
                        fn_rptaRegistrarDocMensajeEnvio(data,noForm);
                    },'json', false, false, "POST");                  
                            
            }else{
                alert_Info("", rpta);
            }         
        }        
    }else{
       bootbox.alert('Debe Seleccionar por lo menos un Destino.');
    }
}

function fn_rptaRegistrarDocMensajeEnvio(dataJson,noForm){ 
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){
            var item=dataJson; 
            if(item!==null&&!!item){   
                $('#'+noForm).find('#numsj').val(item.deNro);
                $('#'+noForm).find('#feregmsj').val(item.fecha);
                $('#'+noForm).find('#cousecre').val(item.usuario); 
                $("#btnGrabarMensajeria").prop('disabled', true);
                alert_Sucess("Éxito!", "Documento grabado correctamente.");
            }else{
               bootbox.alert("ERROR REGISTRANDO DOCUMENTO MESA PARTES.");
            }
        }else{
            alert_Danger("Mensajeria de Documentos: ",dataJson.deRespuesta);
        }
    }
}
 
function fu_verificarChangeDocumentoMensajeria(noForm,cadenaJson) {//si es "1" necesita grabar el documento.   
    var rpta = fn_validarEnvioGrabaDocMensajeria(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"){
        return "1";
    }else{
        return rpta.substr(1);
    }
}
function fn_validarEnvioGrabaDocMensajeria(objTrxMensajeBean) {
    var vReturn = "0EL DOCUMENTO ES EL MISMO."; 
    if (objTrxMensajeBean !== null && typeof(objTrxMensajeBean) !== "undefined"  ) {
        if (typeof(objTrxMensajeBean.deambito) !== "undefined" || 
                typeof(objTrxMensajeBean.detipmsj) !== "undefined" ||
                typeof(objTrxMensajeBean.reenvmsj) !== "undefined" || 
                typeof(objTrxMensajeBean.detipenv) !== "undefined" ||
                typeof(objTrxMensajeBean.codigo) !== "undefined" ) {
            vReturn = "1A GRABAR";
        }
    }
    return vReturn;
}
function fn_buildSendJsontoServerDocMensajeria(noForm) {
    var result = "{";
    result += '"deambito":"' + $('#'+noForm).find("#deambito").val() + '",';
    result += '"detipmsj":"' + $('#'+noForm).find("#detipmsj").val() + '",';
    result += '"reenvmsj":"' + $('#'+noForm).find("#slcResponsableMensajeria").val() + '",';
    result += '"detipenv":"' + $('#'+noForm).find("#detipenv").val() + '",';
    result += '"nusermsj":"' + $('#'+noForm).find("#nusermsj").val() + '",';
    result += '"ansermsj":"' + $('#'+noForm).find("#ansermsj").val() + '",';
    result += '"fecenviomsj":"' + $('#'+noForm).find("#fecenviomsj").val() + ' ' + $('#'+noForm).find("#hoenvmsj").val() + ':00",';
    result += '"hoenvmsj":"' + $('#'+noForm).find("#fecenviomsj").val() + ' ' + $('#'+noForm).find("#hoenvmsj").val() + ':00",'; 
    result += '"feplamsj":"' + $('#'+noForm).find("#feplamsj").val() + ' ' + $('#'+noForm).find("#hoplamsj").val() + ':00",';
    result += '"hoplamsj":"' + $('#'+noForm).find("#feplamsj").val() + ' ' + $('#'+noForm).find("#hoplamsj").val() + ':00",';    
    result += '"numsj":"' + $('#'+noForm).find("#numsj").val() + '",';     
    result += '"diasEntrega":"' + $('#'+noForm).find("#txtdiasEntrega").val() + '",'; 
    result += '"diasDevoluvion":"' + $('#'+noForm).find("#txtdiasDevolucion").val() + '",';   
    result += '"calculaPenalizacion":"' + $('#'+noForm).find("#txtcalcularPenalizacion").val() + '",';
//    result += '"diasDevoluvion":"' + $('#'+noForm).find("#txtdiasDevolucion").val() + '",';     
    
    result += '"codigo":"' + $('#'+noForm).find("#codigo").val() + '",';  
    if($('#envioSedeLocal').prop('checked'))
    {
    result += '"in_env_sede_local":"1",'; 
    }
    else
    {
    result += '"in_env_sede_local":"0",';         
    }
    result += '"de_obs_msj":"' + $("#txtObsDireccion").val() + '"'; 
    
    //result += '"lstDestinatario":' + sortDelFirst(fn_tblDestallePersonason()) + ',';
    
    return result + "}";
    
     
} 
function fn_tblDestallePersonason() {
    var json = '[';
    var itArr = []; 
    var tbl4 = fn_tblDestProveedorJson();
    tbl4 !== "" ? itArr.push(tbl4) : "";
    json += itArr.join(",") + ']';
    return json;
}

function fn_tblDestProveedorJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=5"; 
    arrColMostrar[1] = "nuDes=8";  
    arrColMostrar[2] = "ccodDpto=11";
    arrColMostrar[3] = "cdirRemite=12";   
    return fn_tblDestEmihtmlMensajeriajson('tblDocMensajeria', 0, arrColMostrar, 7, "1", 5, "BD");
}
function fn_tblDestEmihtmlMensajeriajson(idTable, iniFila, colMostrar, colEstadoActivo, estadoActivo, colAccionBD, accionBD) {
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

function validarFormDocumentoMensaje(noForm){
   var valRetorno ="1" ;
   var detipmsj=allTrim($('#'+noForm).find("#detipmsj").val());
   var deambito=$('#'+noForm).find("#deambito").val();
   var detipenv=allTrim(jQuery('#'+noForm).find("#detipenv").val()); 
   var slcResponsableMensajeria=jQuery('#'+noForm).find("#slcResponsableMensajeria").val();
   var nusermsj=allTrim(jQuery('#'+noForm).find("#nusermsj").val());
   var ansermsj=allTrim(jQuery('#'+noForm).find("#ansermsj").val());
   var fecenviomsj=allTrim(jQuery('#'+noForm).find("#fecenviomsj").val());   
   var hoenvmsj=allTrim(jQuery('#'+noForm).find("#hoenvmsj").val());
   var feplamsj=allTrim(jQuery('#'+noForm).find("#feplamsj").val());   
   var hoplamsj=allTrim(jQuery('#'+noForm).find("#hoplamsj").val());  
   var detalleambito = $('#txtDetalleDestiMensAmbitos').val();
  
   
   var vValidaNumero = ""; 
  /*if("NACIONAL"==deambito && detipmsj=="MOTORIZADO"){
       valRetorno = "0"; 
        alert_Info("Aviso", "El tipo de ambito "+deambito+" NO puede enviar un Motorizado.");
        $('#'+noForm).find('#deambito').focus();
  }
  else if("INTERNACIONAL"==deambito && detipmsj=="MOTORIZADO"){
       valRetorno = "0"; 
        alert_Info("Aviso", "El tipo de ambito "+deambito+" NO puede enviar un Motorizado.");
        $('#'+noForm).find('#deambito').focus();
  }
   else*/ 
    
    
  if(detalleambito!=deambito && !$('#envioSedeLocal').prop('checked')){
        valRetorno = "0"; 
        alert_Info("Aviso", "El tipo de ambito "+deambito+" NO corresponde al departamento Destino.");
        $('#'+noForm).find('#deambito').focus();
   }
     else if($('#envioSedeLocal').prop('checked') && $('#txtObsDireccion').val().trim()==''){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Indicar la Obsservación / Dirección sede local del destinatario ");
        $('#'+noForm).find('#deambito').focus();
   }
  else if(!!deambito==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Seleccionar un Ambito");
        $('#'+noForm).find('#deambito').focus();
   }
   else if(!!detipmsj==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Seleccionar un Tipo de Mensajero");
        $('#'+noForm).find('#detipmsj').focus();
            
   }
   else if(!!detipenv==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Seleccionar un Tipo de Envío");
        $('#'+noForm).find('#detipenv').focus();
   }
   else if(!!slcResponsableMensajeria==false){
        valRetorno = "0";
        alert_Info("Aviso", "Debe Seleccionar un Responsable");
        $('#'+noForm).find('#slcResponsableMensajeria').focus();
   }
   else if(slcResponsableMensajeria=="-1"){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Seleccionar un Responsable");
        $('#'+noForm).find('#slcResponsableMensajeria').focus();
   }
   else if(detipmsj=="COURRIER" && !!nusermsj==false){
        valRetorno = "0";
        alert_Info("Aviso", "Debe Ingresar Nro de Guía del Documento");
        $('#'+noForm).find('#nusermsj').focus();
   }
   else if(detipmsj=="COURRIER" && !!ansermsj==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Ingresar Año de Guía del Documento");
        $('#'+noForm).find('#ansermsj').focus();
   }
   else if(!!fecenviomsj==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Ingresar Fecha de Envío del Documento");
        $('#'+noForm).find('#fecenviomsj').focus();
   }
   else if(!!hoenvmsj==false){
        valRetorno = "0"; 
        alert_Info("Aviso", "Debe Ingresar Hora de Envío del Documento");
        $('#'+noForm).find('#hoenvmsj').focus();
   }
   if(valRetorno==="1"){
       if(!!fecenviomsj){
            if(moment(fecenviomsj, "DD/MM/YYYY").isValid()){// HH:mm
  
                var fecha=moment(fecenviomsj, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(fecha.isValid()){
                    //if(fecha.hour()===0){
                    //    fecha.hour(moment().hour());
                   // }
                    //if(fecha.minute()===0){
                    //    fecha.minute(moment().minute());
                    //}
                    //$('#'+noForm).find("#fecenviomsj").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
                                         
                }                
            }else{
                valRetorno="0";
                 bootbox.alert("<h5>Fecha de envío Inválida.</h5>", function() {
                     bootbox.hideAll();
                     $('#'+noForm).find('#fecenviomsj').focus();
                 });
            }       
       }else{
           valRetorno="0";
            bootbox.alert("<h5>Indicar fecha de envío.</h5>", function() {
                bootbox.hideAll();
                jQuery('#'+noForm).find('#fecenviomsj').focus();
            });
       }
       
   }
    
   return valRetorno;
} 

function fu_recibirMensajeriaDocumento(){ 
    var codigosDestinos=$("#txtSelecctionOption").val(); 
     var validar=$('#txtSelecctionOptionValidar').val(); 
     if(validar!=""){
        if(validar.indexOf("1")>-1 || validar.indexOf("3")>-1 || validar.indexOf("4")>-1 ){
             if( validar.indexOf("4")>-1 )
                 bootbox.alert('Verificar por favor.!</br>Hay documentos seleccionados que están en ENTREGA PARCIAL.');            
             else
                bootbox.alert('Verificar por favor.!</br>Hay documentos seleccionados que están RECIBIDOS.');        
        }
        else {
             if(codigosDestinos != null && codigosDestinos!=""  ){                      
                    bootbox.dialog({
                    message: " <h5>¿ Esta Seguro de Recibir los Documentos Seleccionados?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-primary",
                            callback: function() {                                    
                                    ajaxCallSendJson("/srMensajeriaGestion.do?accion=goRecibirDocumentoMensajeria", codigosDestinos, function(data) {
                                        fn_rptaRecibirDocMensajeEnvio(data);
                                    },'json', false, false, "POST"); 
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-default"
                        }
                    }
                });

        }else{
           bootbox.alert('Debe Seleccionar por lo menos un Destino.');
        }

        }
    }
}
function fn_rptaRecibirDocMensajeEnvio(dataJson){ 
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){   
                //$("#btnGrabarMensajeria").prop('disabled', true);
                changeTipoBusqMensajeriaRecep('0');
                alert_Sucess("Éxito!", "Documento es recibido correctamente.");             
        }else{
            alert_Danger("Mensajeria de Documentos: ",dataJson.deRespuesta);
        }
    }

            
            
}

function fn_DevolverMensajeriaRecep(){ 
    var codigosDestinos=$("#txtSelecctionOption").val(); 
     var validar=$('#txtSelecctionOptionValidar').val();
     if(validar!=""){
        if(codigosDestinos != null && codigosDestinos!=""  ){  
                    if(validar.indexOf("0")>-1 || validar.indexOf("1")>-1  ){
                        bootbox.dialog({
                        message: " <h5>¿ Esta Seguro que desea devolver el Documento a la Oficina Origen?</h5>",
                        buttons: {
                            SI: {
                                label: "SI",
                                className: "btn-primary",
                                callback: function() {                                    
                                        ajaxCallSendJson("/srMensajeriaGestion.do?accion=goDevolverDocumentoMensajeria", codigosDestinos, function(data) {
                                            fn_rptaDevolverDocMensajeEnvio(data);
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
                   else {
                        bootbox.alert('Verificar por favor.!</br>Solo se Pueden Devolver Documentos con Estado PENDIENTE.');    

                   }               
        }else{
                bootbox.alert('Debe Seleccionar por lo menos un Registro.');
        }
    }
}
function fn_rptaDevolverDocMensajeEnvio(dataJson){ 
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){   
                //$("#btnGrabarMensajeria").prop('disabled', true);
                changeTipoBusqMensajeriaRecep('0');
                alert_Sucess("Éxito!", "Documento es recibido correctamente.");             
        }else{
            alert_Danger("Mensajeria de Documentos: ",dataJson.deRespuesta);
        }
    }

            
            
}




function fu_calcularFechaPlazo(){
    var noForm='documentoRecepMensajeriaBean';
    var deambito=$('#'+noForm).find("#deambito").val();  
    var deNuRuc=$('#'+noForm).find("#slcResponsableMensajeria").val(); 
    var detipmsj=$('#'+noForm).find("#detipmsj").val(); 
    var detipenv=$('#'+noForm).find("#detipenv").val(); 
    //if(detipmsj ==="MOTORIZADO"){
    //  $('#'+noForm).find('#feplamsj').val($('#'+noForm).find("#fecenviomsj").val());
    //  $('#'+noForm).find('#txtdiasEntrega').val('0'); 
    //  $('#'+noForm).find('#txtdiasDevolucion').val('0')
    //}
    if(deNuRuc !== "undefined" || deNuRuc == "-1") {
      $('#'+noForm).find('#feplamsj').val('');  
    }//detipmsj !="MOTORIZADO" &&
    if( deNuRuc != "undefined" && deNuRuc != "-1"    ){
        if(deambito != null && deambito!=""   ){  
                var fecenviomsj = $('#'+noForm).find("#fecenviomsj").val(); 
                if (fecenviomsj != null && fecenviomsj!="") {
                    var json = "{"; 
                    json += '"deambito":"' + deambito +'",'; 
                    json += '"fecenviomsj":"' + fecenviomsj +'",';
                    json += '"deNuRuc":"' + deNuRuc +'",';
                    json += '"detipmsj":"' + detipmsj +'",';
                    json += '"detipenv":"' + detipenv +'"';
                    json += "}";
                    ajaxCallSendJson("/srMensajeriaGestion.do?accion=goCalcularFechaPlazo", json, function(data) {
                            fn_rptaFechaPlazo(data,noForm);
                        },'json', false, false, "POST");                  

                }else{
                    alert_Info("", rpta);
                }         

        }else{
           bootbox.alert('Debe Seleccionar Ámbito.');
       }
    }
}

function fn_rptaFechaPlazo(dataJson,noForm){  
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){  
                $('#'+noForm).find('#feplamsj').val(dataJson.deRespuesta); 
                $('#'+noForm).find('#txtdiasEntrega').val(dataJson.diasEntrega); 
                $('#'+noForm).find('#txtdiasDevolucion').val(dataJson.diasDevolucion);                  
                
//                $('#'+noForm).find('#txtdiasDevolucion').val(dataJson.diasDevolucion);  
                $('#'+noForm).find('#txtcalcularPenalizacion').val(dataJson.getCalculaPenalizacion);  
                //alert_Sucess("Éxito!", "Documento grabado correctamente.");            
        }else{
            alert_Danger("Calcular Fecha Plazo: ",dataJson.deRespuesta);
        }
    }
}
function fu_calcularHora(){
    var noForm='documentoRecepMensajeriaBean';
    $('#'+noForm).find('#hoplamsj').val($('#'+noForm).find('#hoenvmsj').val());    
}
 
 function fu_ImprimirMensajeriaDocumento(tipo){ 
    var documento = $('#documentoRecepMensajeriaBean').find('#numsj').val();  
    var respEnvio = $('#documentoRecepMensajeriaBean').find('#slcResponsableMensajeria option:selected').text();  
    if (documento !== ""  ) { 
        ajaxCall("/srMensajeriaGestion.do?accion=goImprimirDocumento&pformatRepor="+tipo+"&nrodocumento="+documento+"&responsable="+respEnvio, $('#documentoRecepMensajeriaBean').serialize(), function(data) {
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        //fn_generaDocApplet(data.noUrl,data.noDoc,function (data){
                        fn_generaDocDesktop(data.noUrl, data.noDoc, function(data) {
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
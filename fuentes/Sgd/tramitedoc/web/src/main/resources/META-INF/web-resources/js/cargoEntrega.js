function fn_iniCargoGenerado(){
    var noForm='buscarCargoEntregaBean';
    jQuery('#'+noForm).find('#esFiltroFecha').val("1");//hoy
    jQuery('#'+noForm).find('#auxCoLocDes').val(jQuery('#'+noForm).find('#coLocDes').val());
    jQuery('#'+noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});        
    pnumFilaSelect=0;
    submitAjaxFormBusCarGenerado(noForm);  
}

function fn_submitAjaxFormBusCarGenerado(){
    var noForm='buscarCargoEntregaBean';
    return submitAjaxFormBusCarGenerado(noForm);
}


function submitAjaxFormBusCarGenerado(noForm){
    var validaFiltro = fu_validaFormBusCargoGen(noForm);
    if (validaFiltro === "1") {
        ajaxCall("/srCargoEntrega.do?accion=goInicioCargo", $('#'+noForm).serialize(), function(data) {
            refreshScript("divTablaCargosEntrega", data);
        }, 'text', false, false, "POST");
    }
    return false;    
}

function fu_validaFormBusCargoGen(noForm) {
    var valRetorno = "0";
    jQuery('#'+noForm).find('#feGuiaIni').val(jQuery('#'+noForm).find('#fechaFiltro').attr('fini'));
    jQuery('#'+noForm).find('#feGuiaFin').val(jQuery('#'+noForm).find('#fechaFiltro').attr('ffin'));    
    
    
    var vFechaActual = jQuery('#txtFechaActual').val();
    valRetorno = fu_validaFechasFormBusCargoEntrega(vFechaActual,noForm);  

    return valRetorno;
}

function fu_validaFechasFormBusCargoEntrega(vFechaActual,noForm){
    var valRetorno='1';
    fu_obtenerEsFiltroFechaCargoEntrega(noForm);
    var pEsFiltroFecha = jQuery('#'+noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#'+noForm).find('#nuAnnGuia').val();
            if(!!pAnnio){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }   

        var vFeInicio = jQuery('#'+noForm).find("#feGuiaIni").val();
        var vFeFinal = jQuery('#'+noForm).find("#feGuiaFin").val();
        if(valRetorno==="1"){
          var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
            if(!!pAnnioBusq){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#'+noForm).find('#nuAnnGuia').val(pAnnioBusq);                          
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
                        var msg_fec = 'Error en Fecha Desde ';
                        msg_fec = msg_fec + vFeInicio;
                        msg_fec = msg_fec + ' Hasta ' + vFeFinal + ' : ';
                        bootbox.alert(msg_fec + valRetorno);
                        //bootbox.alert("Error en Fecha Del : "+ valRetorno);
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
    return valRetorno;    
}

function fu_obtenerEsFiltroFechaCargoEntrega(nameForm){
    debugger;
    var opt = jQuery('#'+nameForm).find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}

function fu_obtenerFechaCalendarDocPendEntrega(nameForm){
    var opt = jQuery('#'+nameForm).find('#fechaCalendar').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#filtroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#filtroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#filtroFecha").val("3"); 
    }
}

function fu_cleanFiltroCarGenerado(){
    var noForm='#buscarCargoEntregaBean';
    jQuery(noForm).find('#estadoGuiaMp option[value=0]').prop("selected", "selected");
    jQuery(noForm).find("#coLocDes option[value=001]").prop("selected", "selected");    
    jQuery(noForm).find("#coDepDes").val("");
    jQuery(noForm).find("#txtDependencia").val(" [TODOS]");
    jQuery(noForm).find("#esFiltroFecha").val("1");//hoy
    jQuery(noForm).find("#nuAnnGuia").val(jQuery("#txtAnnioActual").val());
    jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 1});    
}

function fu_iniTblCargoEntrega(){
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
            {"sType": "fecha"},
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
    /*$("#myTableFixed tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 5){
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

            if (typeof($(this).children('td')[0]) !== "undefined") {
                jQuery('#txtpnuAnnGuia').val($(this).children('td')[0].innerHTML);
                jQuery('#txtpnuGuia').val($(this).children('td')[1].innerHTML);
                pnumFilaSelect = $(this).index();
            }
        }
    });
    if(jQuery('#myTableFixed >tbody >tr').length > 0){
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
}

function fn_goNuevoCargoEntrega(){
    var noForm='buscarCargoEntregaBean';
    var p = new Array();    
    p[0] = "accion=goInicioNewDocPendienteEntrega";	    
    p[1] = "coLoc=" + jQuery('#'+noForm).find("#auxCoLocDes").val();   
    p[2] = "coDep=" + jQuery('#'+noForm).find("#coDepOri").val();   
    ajaxCall("/srCargoEntrega.do",p.join("&"),function(data){                
        if(data!==null){
            refreshScript("divDocsPedienteEntrega", data);
            jQuery('#divDocsPedienteEntrega').toggle();
            jQuery('#divTablaCargosEntrega').html("");
            jQuery('#divBuscarCargoEntrega').toggle();
        }
    },'text', false, true, "GET");    
}

function regresarLsCargosGenerados(){
    fn_submitAjaxFormBusCarGenerado();
    jQuery('#divBuscarDocPendEntrega').toggle();
    jQuery('#divDocsPedienteEntrega').toggle();
    jQuery('#divTablaDocPendEntrega').html("");
    jQuery('#divBuscarCargoEntrega').toggle();
}

function fn_iniBusDocPendEntrega(){
    var noForm='buscarDocPendienteEntregaBean';
    jQuery('#'+noForm).find('#filtroFecha').val("1");//hoy
    jQuery('#'+noForm).find("#fechaCalendar").showDatePicker({defaultOpcionSelected: 1});        
    pnumFilaSelect2=0;
    changeTipoBusqDocPendEntrega(noForm,'0');      
}

function changeTipoBusqDocPendEntrega(noForm,tipo) {
    jQuery('#'+noForm).find('#tipoBusqueda').val(tipo);
    submitAjaxFormBusqDocPendEntrega(noForm,tipo);
    mostrarOcultarDivBusqFiltro2();
}

function fn_PrebuscarDocPendEntrega(tipo){
    var noForm='buscarDocPendienteEntregaBean';
    changeTipoBusqDocPendEntrega(noForm,tipo);
}

function regresarLsDocPendEntregaOfNewCargoEntrega(){
    var noForm='#buscarDocPendienteEntregaBean';
    fn_PrebuscarDocPendEntrega(jQuery(noForm).find('#tipoBusqueda').val());
    jQuery('#divNewCargoEntrega').toggle();
    jQuery('#divTablaDocPendEntrega').html("");
    jQuery('#divNewCargoEntrega').html("");
    jQuery('#divDocsPedienteEntrega').toggle();
}

function submitAjaxFormBusqDocPendEntrega(noForm,tipo) {
    var validaFiltro = fu_validaFormBusqDocPendEntrega(noForm,tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srCargoEntrega.do?accion=goInicioNewDocPendienteEntrega", $('#'+noForm).serialize(), function(data) {
            refreshScript("divTablaDocPendEntrega", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_validaFormBusqDocPendEntrega(noForm,tipo) {
    var valRetorno = "1";
    jQuery('#'+noForm).find('#feEmiIni').val(jQuery('#'+noForm).find('#fechaCalendar').attr('fini'));
    jQuery('#'+noForm).find('#feEmiFin').val(jQuery('#'+noForm).find('#fechaCalendar').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = $('#'+noForm).find('#esIncluyeFiltro1').is(':checked');
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFechasFormBusDocPendEntrega(vFechaActual,noForm);     
    }else if(tipo==="1"){
        valRetorno = fu_validaFormBusConfBusDocPendEntrega(noForm);  
        if(valRetorno==="1"){
            if(pEsIncluyeFiltro){
               valRetorno = fu_validaFechasFormBusDocPendEntrega(vFechaActual,noForm); 
            }else{
               valRetorno = setAnnioNoIncludeFiltroDocPendEntrega(noForm);
            }
        }
    }    
    return valRetorno;
}

function setAnnioNoIncludeFiltroDocPendEntrega(noForm){
    var valRetorno = "1";
    fu_obtenerFechaCalendarDocPendEntrega(noForm);
    var pEsFiltroFecha = jQuery('#'+noForm).find("#filtroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#'+noForm).find('#nuAnn').val();
            if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery('#'+noForm).find('#feEmiIni').val();
        var vFeFinal = jQuery('#'+noForm).find('#feEmiFin').val();

        if(valRetorno==="1" /*&& pEsFiltroFecha==="1"*/){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#'+noForm).find('#nuAnn').val(pAnnioBusq);                          
                }                
            }               
        }
    }
    return valRetorno;       
}

function fu_validaFormBusConfBusDocPendEntrega(noForm) {
    var valRetorno = "1";
    
    upperCaseFormConfigBusDocPendEntrega(noForm);
    
    var vnuCorEmi = allTrim(jQuery('#'+noForm).find('#nuCorEmi').val());
    var vnuDoc = allTrim(jQuery('#'+noForm).find('#nuDoc').val());
    var vdeAsu = allTrim(jQuery('#'+noForm).find('#deAsu').val());
    
    if(!(!!vnuCorEmi||!!vnuDoc||!!vdeAsu)){
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    if(valRetorno==="1"){
        if (!!vnuCorEmi) {
            var vValidaNumero = fu_validaNumero(vnuCorEmi);
            if (vValidaNumero !== "OK") {
                alert_Warning("Buscar: ","N° de Registro debe ser solo números.");
                valRetorno = "0";
            }
        }
    }
    return valRetorno;
}

function fu_validaFechasFormBusDocPendEntrega(vFechaActual,noForm){
    var valRetorno='1';
    fu_obtenerFechaCalendarDocPendEntrega(noForm);
    var pEsFiltroFecha = jQuery('#'+noForm).find("#filtroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery('#'+noForm).find('#nuAnn').val();
            if(!!pAnnio){
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }   

        var vFeInicio = jQuery('#'+noForm).find("#feEmiIni").val();
        var vFeFinal = jQuery('#'+noForm).find("#feEmiFin").val();
        if(valRetorno==="1"){
          var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
            if(!!pAnnioBusq){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }else if(vValidaNumero ==="OK"){
                    jQuery('#'+noForm).find('#nuAnn').val(pAnnioBusq);                          
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
                        var msg_fec = 'Error en Fecha Desde ';
                        msg_fec = msg_fec + vFeInicio;
                        msg_fec = msg_fec + ' Hasta ' + vFeFinal + ' : ';
                        bootbox.alert(msg_fec + valRetorno);                        
                        // bootbox.alert("Error en Fecha Del : "+ valRetorno);
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
    return valRetorno;    
}

function upperCaseFormConfigBusDocPendEntrega(noForm){
    jQuery('#'+noForm).find('#nuCorEmi').val(fu_getValorUpperCase(jQuery('#'+noForm).find('#nuCorEmi').val()));
    jQuery('#'+noForm).find('#nuDoc').val(fu_getValorUpperCase(jQuery('#'+noForm).find('#nuDoc').val()));
    jQuery('#'+noForm).find('#deAsu').val(fu_getValorUpperCase(jQuery('#'+noForm).find('#deAsu').val()));
}

function fu_iniTblDocPendEntrega(){
    var nomTabla='#tblDocPendEntrega';
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
            indexFilaClick = $(this).index();
        }
    });
   /* $(nomTabla+" tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 4 || index === 8 || index === 9) {
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

function fu_cleanBusDecPendEntrega(tipo){
    var noForm='#buscarDocPendienteEntregaBean';
    if (tipo==="1") {
        jQuery(noForm).find('#nuCorEmi').val("");
        jQuery(noForm).find('#nuDoc').val("");
        jQuery(noForm).find('#deAsu').val("");
        jQuery(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        jQuery(noForm).find("#esIncluyeFiltro1").attr('checked',false);        
        jQuery(noForm).find("#feEmiIni").val("");
        jQuery(noForm).find("#feEmiFin").val("");
    } else if(tipo==="0"){
        jQuery(noForm).find("#coTipDocAdm option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coLocDes option[value=001]").prop("selected", "selected");
        jQuery(noForm).find('#coDepDes').val("");
        jQuery(noForm).find('#txtDependenciaDes').val(" [TODOS]");
        jQuery(noForm).find("#filtroFecha").val("1");//hoy
        jQuery(noForm).find("#nuAnn").val(jQuery("#txtAnnioActual").val());
        jQuery(noForm).find("#fechaCalendar").showDatePicker({defaultOpcionSelected: 1});
    }    
}

function fn_changeChkIncluirDocPedEntrega(chk){
    var nomTabla='#tblDocPendEntrega';
    if(chk.checked){
        $(nomTabla + " tbody tr").each(function(index, row) {
            $(row).find('#chkTblIncluir').attr('checked',true);
            $(row).find('#chkTblIncluir').prop('checked',true);
        });        
    }else{
        $(nomTabla + " tbody tr").each(function(index, row) {
            $(row).find('#chkTblIncluir').attr('checked',false);
            $(row).find('#chkTblIncluir').prop('checked',false);
        });         
    }
}

function fn_submitAjaxGenerarCargo(){
    var json=fn_preObtenerJsontblDocPendEntrega();
    var vResult=fn_verificarLsDocPendEntregaSelec(new Function('return ' + json)());
    if(vResult==='1'){
        //var noForm='#buscarDocPendienteEntregaBean';
        //var pcoDepDes="null";
        //var coDepDes=jQuery(noForm).find('#coDepDes').val();
        //var deDepDes=jQuery(noForm).find('#txtDependenciaDes').val();
        //if(jQuery(noForm).find('#tipoBusqueda').val()==="0"&&!!coDepDes){
            //pcoDepDes=coDepDes;
        //}
        ajaxCallSendJson("/srCargoEntrega.do?accion=goGeneraCargoEntrega"/*&pcoDepDes="+pcoDepDes+"&pdeDepDes="+deDepDes*/, json, function(data) {
            fn_preRptaGenerarCargo(data);
        },'text', false, false, "POST");        
    }else{
        alert_Info("Cargo :", "Seleccione filas.");
    }
}

function fn_preRptaGenerarCargo(XML_AJAX){
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}        
        if(obj){
            if(obj.coRespuesta==="0"){
                alert_Info("Cargo :", "Seleccionar registros de una misma dependencia.");
            }
        }else{
            fn_rptaGenerarCargo(XML_AJAX);
        }         
    }
}

function fn_rptaGenerarCargo(data){
    if(!!data){
        refreshScript("divNewCargoEntrega", data);
        jQuery('#divNewCargoEntrega').toggle();
        jQuery('#divTablaDocPendEntrega').html("");
        jQuery('#divDocsPedienteEntrega').toggle();        
    }
}

function fn_verificarLsDocPendEntregaSelec(LsDoc){
    var vReturn='0';
    if(!!LsDoc){
        if(LsDoc.length>0){
            vReturn='1';
        }
    }
    return vReturn;
}

function fn_preObtenerJsontblDocPendEntrega(){
    //Observaciones RBN
    //El armado de Json se maneja posicion por posicion, por tanto hay que tener mucho cuidado
    //al momento de incluir nuevos campos, se sugiere ponerlos al final, para evitar mover los
    //campos ya dados y evitar errores
    //OJO nuCorEmi ya se encontraba comentado
    var nomTabla='#tblDocPendEntrega';
    var nomChk='#chkTblIncluir';
    var arrColEnviar = new Array();
    arrColEnviar[0] = "nuAnn=1";
    arrColEnviar[1] = "nuEmi=2";
    arrColEnviar[2] = "nuDes=3";
    //arrColEnviar[3] = "nuCorEmi=4";
    arrColEnviar[3] = "deOriEmi=5";
    arrColEnviar[4] = "feEmiCorta=6";
    arrColEnviar[5] = "deTipDocAdm=7";
    arrColEnviar[6] = "nuDoc=8";
    arrColEnviar[7] = "deDepDes=9";
    return fn_obtenerJsontblDocPendEntrega(nomTabla,arrColEnviar,nomChk);
}

function fn_obtenerJsontblDocPendEntrega(nomTabla,colMostrar,nomChk){
    var otArr = [];
    $(nomTabla + " tbody tr").each(function(index, row) {
        var itArr = [];
        var isRowCheck=$(row).find(nomChk).is(':checked');
        if(isRowCheck){
            var x = $(this).children();
            x.each(function(index) {
             for (var i = 0; i < colMostrar.length; i++) {
                var auxArrColMostrar = colMostrar[i].split("=");
                if (auxArrColMostrar[1] * 1 === index + 1) {
                    var campoBean = auxArrColMostrar[0];
                    var valCampoBean = $(this).text();
                    itArr.push('"' + campoBean + '":' + JSON.stringify(valCampoBean));
                }
             }   
            });
            otArr.push('{' + itArr.join(',') + '}');
        }
    });
    return '[' + otArr.join(",") + ']';    
}

function fn_iniCargoEntrega(){
    var noForm='#guiaMesaPartesBean';
    jQuery(noForm).find("#feGuiMp").change(function() {
       var vResult=fn_changeFeGuiMpCargoEntrega(noForm);
       if(vResult==="1"){
        jQuery(noForm).find('#envGuiaMp').val("1");           
       }
    });
    jQuery(noForm).find("#deObs").change(function() {
        jQuery(noForm).find('#envGuiaMp').val("1");
    });
    var pnuAnnGuia=jQuery(noForm).find("#nuAnn").val();
    var pnuGuia=jQuery(noForm).find("#nuGuia").val();
    if(!(!!pnuAnnGuia)||!(!!pnuGuia)){
        jQuery(noForm).find('#envGuiaMp').val("1");        
    }
}

function fu_iniTblGuiaMesaPartes(){
    var nomTabla='#tblDetGuiaMP';
    /*$(nomTabla+" tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 7 || index === 8) {
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

function fn_changeFeGuiMpCargoEntrega(noForm){
    var valRetorno="0";
    var pFeGuia=jQuery(noForm).find("#feGuiMp").val();
    if(!!pFeGuia){
        if(moment(pFeGuia, "DD/MM/YYYY HH:mm").isValid()){
            var fecha=moment(pFeGuia, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
            if(fecha.isValid()){
                if(fecha.hour()===0){
                    fecha.hour(moment().hour());
                }
                if(fecha.minute()===0){
                    fecha.minute(moment().minute());
                }
                jQuery(noForm).find("#feGuiMp").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
                valRetorno="1";
            }            
        }else{
             bootbox.alert("<h5>Fecha Inválida.</h5>", function() {
                 bootbox.hideAll();
                 jQuery(noForm).find('#feGuiMp').focus();
             });
        }
    }else{
        bootbox.alert("<h5>Indicar fecha.</h5>", function() {
            bootbox.hideAll();
            jQuery(noForm).find('#feGuiMp').focus();
        });        
    }
    return valRetorno;
}

function fn_goGrabarCargoEntrega(){
    var noForm='#guiaMesaPartesBean';
    if(fn_verificarFormCargoEntrega(noForm)==="1"){
        var json=fn_buildSendJsontoServerGuiaMp(noForm);
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeCargoEntrega(noForm,json);
        var nrpta = rpta.substr(0,1);
        if (nrpta === "1") {    
            ajaxCallSendJson("/srCargoEntrega.do?accion=goGrabarGuiaMp", json, function(data) {
                fn_rptaGrabarCargoEntrega(data,noForm);
            },'json', false, false, "POST");           
        }else{
            alert_Info("", rpta);
        } 
    }
}

function fn_verificarFormCargoEntrega(noForm){
//    var tblDetGuia='tblDetGuiaMP';
    var pcoLocOri=allTrim(jQuery(noForm).find('#coLocOri').val());
    var pcoDepOri=allTrim(jQuery(noForm).find('#coDepOri').val());
    var pcoLocDes=allTrim(jQuery(noForm).find('#coLocDes').val());
    var pnuAnn=allTrim(jQuery(noForm).find('#nuAnn').val());
//    var pnuGuia=allTrim(jQuery(noForm).find('#nuGuia').val());
    var pfeGuiMp=allTrim(jQuery(noForm).find('#feGuiMp').val());
    var pdeObs=allTrim(jQuery(noForm).find('#deObs').val());
    var maxLengthDeObs=jQuery(noForm).find('#deObs').attr('maxlength');
    
    var valRetorno = "0";
    var vValidaNumero = "";
    
    if(!!pcoLocOri&&pcoLocOri.length>1){
        valRetorno="1";
        jQuery(noForm).find('#coLocOri').val(pcoLocOri);
    }else{
        bootbox.alert("<h5>Indicar Local Origen.</h5>", function() {
            bootbox.hideAll();
            //jQuery(noForm).find('#coLocOri').focus();
        });        
    }
    
    if(valRetorno==="1"){
       if(!!pcoDepOri&&pcoDepOri.length>1){
           jQuery(noForm).find('#coDepOri').val(pcoDepOri);
       }else{
           valRetorno="0";
           bootbox.alert("<h5>Indicar Dependencia Origen.</h5>", function() {
               bootbox.hideAll();
               //jQuery(noForm).find('#coDepOri').focus();
           });            
       } 
    }
    
    if(valRetorno==="1"){
       if(!!pcoLocDes&&pcoLocDes.length>1){
           jQuery(noForm).find('#coLocDes').val(pcoLocDes);
       }else{
           valRetorno="0";
           bootbox.alert("<h5>Indicar Local Destino.</h5>", function() {
               bootbox.hideAll();
               //jQuery(noForm).find('#coLocDes').focus();
           });            
       }         
    }
    
    if(valRetorno==="1"){
       if(!!pnuAnn&&pnuAnn.length>1){
           valRetorno = "0";           
           vValidaNumero = fu_validaNumero(pnuAnn);
            if (vValidaNumero === "OK"){
                valRetorno = "1";           
                jQuery(noForm).find('#nuAnn').val(pnuAnn);                    
            }else{
                bootbox.alert("<h5>Número Año debe ser solo números.</h5>", function() {
                    bootbox.hideAll();
                });            
            }  
       }else{
           valRetorno="0";
           bootbox.alert("<h5>Indicar Año.</h5>", function() {
               bootbox.hideAll();
               //jQuery(noForm).find('#coLocDes').focus();
           });            
       }         
    }
    
//    if(valRetorno==="1"){
//        if(!!pnuAnn&&!!pnuGuia){
//          //implementar  
//        }else{
//            
//        }
//    }
    
   if(valRetorno==="1"){
       if(!!pfeGuiMp){
           valRetorno="0";
            if(moment(pfeGuiMp, "DD/MM/YYYY HH:mm").isValid()){
                var fecha=moment(pfeGuiMp, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(fecha.isValid()){
                    if(fecha.hour()===0){
                        fecha.hour(moment().hour());
                    }
                    if(fecha.minute()===0){
                        fecha.minute(moment().minute());
                    }
                    jQuery(noForm).find("#feGuiMp").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
                    valRetorno="1";
                }                
            }else{
                valRetorno="0";
                 bootbox.alert("<h5>Fecha Inválida.</h5>", function() {
                     bootbox.hideAll();
                     jQuery(noForm).find('#feGuiMp').focus();
                 });
            }       
       }else{
           valRetorno="0";
            bootbox.alert("<h5>Indicar fecha.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find('#feGuiMp').focus();
            });
       }
   }
   
   if(valRetorno==="1"){
       if(!!pdeObs){
        if(!!maxLengthDeObs){
            var nrolinesDeAsu = (pdeObs.match(/\n/g) || []).length;
            if(pdeObs.length+nrolinesDeAsu > maxLengthDeObs){
                valRetorno = "0";
                bootbox.alert("<h5>La Observación Excede el límite de "+maxLengthDeObs+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find('#deObs').focus();
                });
            }
        }           
       }
   }   
   return valRetorno;    
}

function fu_verificarChangeCargoEntrega(noForm,cadenaJson) {//si es "1" necesita grabar el documento.
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuGuia=jQuery(noForm).find('#nuGuia').val();
    var rpta = fn_validarEnvioGrabaCargoEntrega(new Function('return ' + cadenaJson)());
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1"||!(!!pnuGuia&&!!pnuAnn)){
        return "1";
    }else{
        return rpta.substr(1);
    }
}

function fn_validarEnvioGrabaCargoEntrega(objTrxDocumentoEmiBean) {
    var vReturn = "0EL DOCUMENTO ES EL MISMO.";
    if (objTrxDocumentoEmiBean !== null && typeof(objTrxDocumentoEmiBean) !== "undefined") {
        if (typeof(objTrxDocumentoEmiBean.guia) !== "undefined") {
            vReturn = "1A GRABAR";
        } else if (typeof(objTrxDocumentoEmiBean.lsDetGuia) !== "undefined") {
            if (objTrxDocumentoEmiBean.lsDetGuia.length > 0) {
                vReturn = "1A GRABAR";
            }
        }
    }
    return vReturn;
}

function fn_rptaGrabarCargoEntrega(Obj,noForm){
    if(!!Obj){
        var vRespuesta=Obj.coRespuesta;
        if(!!vRespuesta){
            if(vRespuesta==="1"){
                var accion=Obj.accionBd;
                if(accion==="INS"){
                    var guia=Obj.guiaBean;
                    if(!!guia){
                        jQuery(noForm).find('#nuGuia').val(guia.nuGuia);
                        jQuery(noForm).find('#nuCorGui').val(guia.nuCorGuia);
                        jQuery(noForm).find('#nuAnn').val(guia.nuAnn);
                        var lsDetGuia=Obj.lstDetGuia;
                        if(!!lsDetGuia){
                            fn_actualizarTblDetalleGuia(lsDetGuia);
                        }
                        jQuery(noForm).find('#envGuiaMp').val("0");
                        alert_Sucess("Éxito!", "Documento grabado correctamente.");           
                    }
                }else if(accion==="UPD"){
                    jQuery(noForm).find('#envGuiaMp').val("0");
                    alert_Sucess("Éxito!", "Documento grabado correctamente.");           
                }
            }else{
                alert_Danger("Cargo Entrega: ",Obj.deRespuesta);
            }
        }
    }
}

function fn_actualizarTblDetalleGuia(lsDetGuia){
    var noTblDetGuia='#tblDetGuiaMP';
    $(noTblDetGuia+" tbody tr").each(function(index, row) {
        $.each(lsDetGuia, function(i, obj) {
           var pnuAnn=$(row).find('td:eq(0)').html();
           var pnuEmi=$(row).find('td:eq(1)').html();
           var pnuDes=$(row).find('td:eq(2)').html();          
           if(pnuAnn===obj.nuAnn&&pnuEmi===obj.nuEmi
                   &&pnuDes===obj.nuDes){
               $(row).find('td:eq(3)').html(obj.nuCorr);
           }
        });
    });
}

function fn_buildSendJsontoServerGuiaMp(noForm) {
    var result = "{";
    result = result + '"nuAnnGuia":"' + jQuery(noForm).find('#nuAnn').val() + '",';
    result = result + '"nuGuia":"' + jQuery(noForm).find('#nuGuia').val() + '",';
    var valEnvio = jQuery(noForm).find('#envGuiaMp').val();
    if (valEnvio === "1") {
        result = result + '"guia":' + JSON.stringify(getJsonFormGeneralCargoEntregaBean(noForm,getArrCampoBeanGuiaMp())) + ',';
    }
    result = result + '"lsDetGuia":[' + fn_tblDetCargoEmihtml2json('#tblDetGuiaMP',0,fn_getArrCampoBeanDetGuiaMp(),4) + ']';
    return result + "}";
}

function fn_getArrCampoBeanDetGuiaMp() {
    var arrColEnviar = new Array();
    arrColEnviar[0] = "nuAnn=1";
    arrColEnviar[1] = "nuEmi=2";
    arrColEnviar[2] = "nuDes=3";
    arrColEnviar[3] = "nuCor=4";
    return arrColEnviar;
}

function getArrCampoBeanGuiaMp(){
    var arrCampoBean = new Array();
    arrCampoBean[0] = "nuAnn";
    arrCampoBean[1] = "nuGuia";
    arrCampoBean[2] = "coLocOri";
    arrCampoBean[3] = "coDepOri";
    arrCampoBean[4] = "coLocDes";
    arrCampoBean[5] = "coDepDes";
    arrCampoBean[6] = "feGuiMp";
    arrCampoBean[7] = "deObs";
    return arrCampoBean;
}

/**
 * 
 * @param {type} idTable nombre de la tabla
 * @param {type} iniFila a partir de que fila se empieza a contar
 * @param {type} colEnviar columnas a enviar al server con sus respectivos campos
 * @param {type} colNroCorr columna de numero correlativo de acuerdo a esto se envia las filas al server.
 * @returns {String} return json.
 */
function fn_tblDetCargoEmihtml2json(idTable, iniFila, colEnviar,colNroCorr) {
    //var json = '[';
    var otArr = [];
    var count = 0;
    $(idTable + ' tr').each(function(i) {
        if (count >= iniFila) {
            var x = $(this).children();
            var itArr = [];
            if (!(!!($(this).find("td").eq(colNroCorr - 1).text()))) {
                x.each(function(index) {
                    for (var i = 0; i < colEnviar.length; i++) {
                        var auxArrColMostrar = colEnviar[i].split("=");
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
                            } else {
                                valCampoBean = $(this).text();
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

function getJsonFormGeneralCargoEntregaBean(noForm,arrCampoBean) {
    var o = {};
    var a = $(noForm).serializeArray();
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

function fn_goEditarCargoEntrega(){
    var pnuAnnGuia=jQuery('#txtpnuAnnGuia').val();
    var pnuGuia=jQuery('#txtpnuGuia').val();
    if(!!pnuAnnGuia&&pnuGuia){
        var p = new Array();    
        p[0] = "accion=goEditCargoEntrega";	    
        p[1] = "pnuAnnGuia=" + pnuAnnGuia;   
        p[2] = "pnuGuia=" + pnuGuia;           
        ajaxCall("/srCargoEntrega.do?accion=goEditCargoEntrega", p.join("&"), function(data) {
            fn_rptaEditarCargoEntrega(data);
        }, 'text', false, false, "POST");        
    }
}

function fn_rptaEditarCargoEntrega(data){
    if(!!data){
        refreshScript("divNewCargoEntrega", data);
        jQuery('#divNewCargoEntrega').toggle();
        jQuery('#divTablaCargosEntrega').html("");
        jQuery('#divBuscarCargoEntrega').toggle();        
    }
}

function fn_backCargoEntrega(){
    var noForm='#guiaMesaPartesBean';
    var whoCalled=jQuery(noForm).find('#whoCalled').val();
    if(!!whoCalled){
        if(whoCalled==="1"){//editar cargo
            regresarLsCargosGeneradosOfNewCargoEntrega();
        }else if(whoCalled==="0"){//nuevo cargo
            regresarLsDocPendEntregaOfNewCargoEntrega();
        }
    }
}

function regresarLsCargosGeneradosOfNewCargoEntrega(){
    fn_submitAjaxFormBusCarGenerado();
    jQuery('#divNewCargoEntrega').toggle();
    jQuery('#divTablaCargosEntrega').html("");
    jQuery('#divNewCargoEntrega').html("");
    jQuery('#divBuscarCargoEntrega').toggle();
}

function fn_anularCargoEntregaMp(){
    var noForm='#guiaMesaPartesBean';
    var pnuAnnGuia=jQuery(noForm).find('#nuAnn').val();
    var pnuGuia=jQuery(noForm).find('#nuGuia').val();
    if (!!pnuAnnGuia&&!!pnuGuia) {
        var estadoGuia=jQuery(noForm).find('#estadoGuia').val();
        if(!!estadoGuia&&estadoGuia==="0"){
            bootbox.dialog({
                message: " <h5>¿ Esta Seguro de Anular Cargo ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-primary",
                        callback: function() {
                            var p = new Array();
                            p[0] = "accion=goAnularGuiaMp";
                            p[1] = "nuAnn=" + pnuAnnGuia;    
                            p[2] = "nuGuia=" + pnuGuia;   
                            ajaxCall("/srCargoEntrega.do", p.join("&"), function(data) {
                                fn_rptAnularCargoEntregaMp(data);
                            }, 'json', false, false, "POST"); 
                        }                        
                    },
                    NO: {
                        label: "NO",
                        className: "btn-default"
                    }
                }
            });   
        }else{
          alert_Warning("Cargo Entrega :", "No se puede anular el cargo porque el destino ya recibió el documento.");  
        }        
    }else {
        alert_Info("Cargo Entrega :", "Necesita grabar los cambios.");
    }
}

function fn_rptAnularCargoEntregaMp(data){
    if(!!data){
       if(data.coRespuesta==="1"){
           fn_backCargoEntrega();
       }else{
          alert_Warning("Cargo Entrega :", data.deRespuesta);   
       } 
    }
}

function fn_imprimirCargoEntrega(){
    var noForm='#guiaMesaPartesBean';
    var pnuAnnGuia=jQuery(noForm).find('#nuAnn').val();
    var pnuGuia=jQuery(noForm).find('#nuGuia').val();
    if (!!pnuAnnGuia&&!!pnuGuia) {
        var p = new Array();
        p[0] = "accion=goExportarArchivoLista";
        p[1] = "pnuAnnGuia=" + pnuAnnGuia;    
        p[2] = "pnuGuia=" + pnuGuia;   
        ajaxCall("/srCargoEntrega.do", p.join("&"), function(data) {          
            if (data!==null) {
                if(data.coRespuesta==="0"){
                    if(!!data.noUrl&&!!data.noDoc){
                        /*fn_generaDocApplet(data.noUrl,data.noDoc,function(data){
                            var result = data;
                            if (result!=="OK"){
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
        }, 'json', false, false, "POST");
    } else {
        alert_Info("Cargo Entrega :", "Necesita grabar los cambios.");
    }
}

function fn_iniConsDestDocPendEntrega(){
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
                        fu_setDatoDestinatarioDocPendEntrega(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioDocPendEntrega(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioDocPendEntrega(cod, desc){
    var noForm='#buscarDocPendienteEntregaBean';
    jQuery(noForm).find('#txtDependenciaDes').val(desc);
    jQuery(noForm).find('#coDepDes').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fu_buscaDependenciaDestinoDocPendEntrega(){
    var noForm='#buscarDocPendienteEntregaBean';
    var p = new Array();
    p[0] = "accion=goBuscaDependencia";
    p[1] = "pcoLocDes=" + jQuery(noForm).find('#coLocDes').val();
    ajaxCall("/srCargoEntrega.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigen(data);
    },'text', false, false, "POST");    
}

function fn_changeLocalFiltroDocPendEnrtega(){
    var noForm='#buscarDocPendienteEntregaBean';
    jQuery(noForm).find('#coDepDes').val("");
    jQuery(noForm).find('#txtDependenciaDes').val(" [TODOS]");
}

function fn_buscaDepDestCargoEntrega(){
    var noForm='#buscarCargoEntregaBean';
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaCargoEntrega";
    p[1] = "pcoLocDes=" + jQuery(noForm).find('#coLocDes').val();
    ajaxCall("/srCargoEntrega.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigen(data);
    },'text', false, false, "POST");      
}

function fn_changeLocalFiltroCargoGenerados(){
    var noForm='#buscarCargoEntregaBean';
    jQuery(noForm).find('#coDepDes').val("");
    jQuery(noForm).find('#txtDependencia').val(" [TODOS]");    
}

function fn_iniConsDestCargosGenerados(){
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
                        fu_setDatoDestinatarioCargosGenerados(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioCargosGenerados(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioCargosGenerados(cod, desc){
    var noForm='#buscarCargoEntregaBean';
    jQuery(noForm).find('#txtDependencia').val(desc);
    jQuery(noForm).find('#coDepDes').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}

function fu_iniTblDetGuiaMpEdit(){
    var nomTabla='#tblDetGuiaMP';
    /*$(nomTabla+" tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 8 || index === 9) {
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

function fn_verRegistrarAvance(){ 
    var pnuAnn = $('#txtpnuAnnAvance').val();
    var pnuEmi = $('#txtpnuEmiAvance').val();
    var pnuDes = $('#txtpnuDesAvance').val(); 
    var tipAvance = $('#tipAvance').val(); 
    var avance = $('#txtAvanceDescripcion').val(); 
    if(pnuAnn != null && pnuAnn!=""  ){   
        /* var p = new Array();
        p[0] = "accion=goGrabarAvance";
        p[1] = "pnuAnn=" + pnuAnn  ; 
        p[2] = "pnuEmi=" + pnuEmi  ; 
        p[3] = "pnuDes=" + pnuDes;  
        p[4] = "tipAvance=" + tipAvance;  
        p[5] = "desAvance=" + avance;  
        */
       if(avance.trim().length>0)
       {
        var o={};
        o["nuAnn"]=pnuAnn;  
        o["nuEmi"]=pnuEmi; 
        o["nuDes"]=pnuDes; 
        o["tiAvance"]=tipAvance;  
        o["deAvance"]=avance;  
        var cadena=JSON.stringify(o); 
        console.log(cadena);
       ajaxCallSendJson("/srDocObjeto.do?accion=goGrabarAvance",cadena , function(data) {//p.join("&")
                        fn_rptaegistrarAvance(data);
                    },'json', false, false, "POST");    
        }else{
          bootbox.alert('Debe ingresar la descripción del avance');
       }
    }else{
       bootbox.alert('Debe Seleccionar por lo menos un Destino.');
    }
}
function fn_rptaegistrarAvance(dataJson){ 
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){     
           alert_Sucess("Éxito!", "Avance Registrado Correctamente.");
           removeDomId('divOrigenMain');             
        }else{
            alert_Danger("Avance: ",dataJson.deRespuesta);
        }
    }
    else{
        bootbox.alert("ERROR EN REGISTRAR AVANCE.");
    }
}
 
function fn_verAvanceConsultaObj(pnuAnn, pnuEmi, pnuDes) {         
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goVerAvanceConsulta";
        p[1] = "pnuAnn=" + pnuAnn  ; 
        p[2] = "pnuEmi=" + pnuEmi  ; 
        p[3] = "pnuDes=" + pnuDes; 
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                    if (data !== null){
                        $("body").append(data);
                    }
                }, 'text', false, false, "POST");
             
     
    }
    return false;
}

function fn_verAvanceRecepcion() {
    var pnuAnn = $('#txtpnuAnn').val();
    var pnuEmi = $('#txtpnuEmi').val();
    var pnuDes = $('#txtpnuDes').val(); 
    if (pnuAnn){
        fn_verAvanceRecepcionObj(pnuAnn, pnuEmi,pnuDes);
    } else{
        alert_Info("Recepción :", "Seleccione una fila de la lista.");
    }
}
function fn_verAvanceRecepcionObj(pnuAnn, pnuEmi, pnuDes) {         
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goVerAvance";
        p[1] = "pnuAnn=" + pnuAnn  ; 
        p[2] = "pnuEmi=" + pnuEmi  ; 
        p[3] = "pnuDes=" + pnuDes; 
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                    if (data !== null){
                        $("body").append(data);
                    }
                }, 'text', false, false, "POST");
             
     
    }
    return false;
}

function fu_grabarTema(){  
    var codigosDestinos=$("#codigoEmision").val();  
    if(codigosDestinos != null && codigosDestinos!=""  ){   
         var p = new Array();
        p[0] = "accion=goGrabarTema";
        p[1] = "codigoEmision=" +$("#codigoEmision").val();  
        p[2] = "codTipo="+$("#codTipo").val();  
        p[3] = "coTema="+$("#divOrigenMain").find("select").val();  
       ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                        fn_rptagrabarTema(data);
                    },'json', false, false, "POST");                                     
    }else{
       bootbox.alert('Debe Seleccionar por lo menos un Destino.');
    }
}

function fn_rptagrabarTema(dataJson){ 
    if(dataJson!==null){ 
        if(dataJson.coRespuesta==="1"){     
           alert_Sucess("Éxito!", "Documento Vinculado Tema Correctamente.");
           removeDomId('divOrigenMain');             
        }else{
            alert_Danger("Vincular Tema: ",dataJson.deRespuesta);
        }
    }
    else{
        bootbox.alert("ERROR EN VINCULAR TEMA.");
    }
}


function fn_Vincular_TemaRecepcion() {
    var pnuAnn = $('#txtpnuAnn').val();
    var pnuEmi = $('#txtpnuEmi').val();
    var pnuDes = $('#txtpnuDes').val(); 
    if (pnuAnn) {
        fn_verVincularTemaObj(pnuAnn, pnuEmi, pnuDes,"RECEPCION");
    } else {
        alert_Info("Recepción :", "Seleccione una fila de la lista.");
    }
}
function fn_Vincular_TemaEmision() {
    var pnuAnn = $('#txtpnuAnn').val();
    var pnuEmi = $('#txtpnuEmi').val(); 
    if (pnuAnn){
        fn_verVincularTemaObj(pnuAnn, pnuEmi,"","EMISION");
    } else{
        alert_Info("Recepción :", "Seleccione una fila de la lista.");
    }
}
function fn_verVincularTemaObj(pnuAnn, pnuEmi, pnuDes, tipo) {         
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goVincularTema";
        p[1] = "pkEmi=" + pnuAnn + pnuEmi + pnuDes;
        p[2] = "tipoRecep="+tipo;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                    if (data !== null){
                        $("body").append(data);
                    }
                }, 'text', false, false, "POST");
             
     
    }
    return false;
}



function fn_buscarRemitenteCiudadanoEmision(cell){
     
     
    var pfila = (($(cell).parent()).parent()).index();
    var pcol = ($(cell).parent()).index();
    var tablaId = (((($(cell).parent()).parent()).parent()).parent()).attr("id");
    var valor = $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val();   
    var pnomOtro=allTrim(fu_getValorUpperCase(valor)); 
    pnomOtro=$("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(pnomOtro))).val();    
    
    if( allTrim(pnomOtro).length >= 0 && allTrim(pnomOtro).length <= 3 ){
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll(); 
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
                
                p[0] = "accion=goBuscaDestCiudadano";
                p[1] = "pnoCiudadano=" + pnomOtro;                
                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptaBuscarRemitenteOtroDocExtRec(data);
                    
                     $('#txtTblDestProveedorEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
                     $('#txtTblDestEmiProveedorColWhereButton').val(($(cell).parent()).index());
                     $('#txtTblDestEmiProveedorTablaWhereButton').val((((($(cell).parent()).parent()).parent()).parent()).attr("id"));   

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


function fn_iniConsCiudadanoDestEmi(){
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
                   
                 fn_setDestinoEmitlbCiudadano(nombres,dni);             
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
        
            fn_setDestinoEmitlbCiudadano(nombres,dni);                  
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
            
            fn_setDestinoEmitlbCiudadano(nombres,dni);             
        }
        //console.log(indexAux);
        evento.preventDefault();
    });    
}


function fn_setDestinoEmitlbCiudadano(nombres, dni) {
    var pfila = jQuery('#txtTblDestProveedorEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiProveedorColWhereButton').val(); 
    
    //var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmOtro', arrCampo);
    //if (bResult) { 
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('#txtDniPJCiudadano').val(dni);  
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('#txtCodigoRemitente').val(dni);    
        $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('#txtNombresPJCiudadano').val(nombres);         
        if ($("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text() === "BD") {
            $("#tblDestEmiDocAdmPersJuri tbody tr:eq(" + pfila + ") td:eq(3)").text("UPD");
        }
        removeDomId('windowConsultaOtroOri');
        //fn_changeDestinatarioCorrectoOtro('tblDestEmiDocAdmOtro', pfila);
    //} else {
    //    removeDomId('windowConsultaOtroOri');
    //   bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
    //}
}
 

function fn_buscaPJCiudadanoResp(dni, pvalidKeys) {
    var tk = new KeyboardClass(oEvent, pvalidKeys);
    var nuDni = dni.value;
    if (nuDni.length === 8 && tk.isIntro() /*&& nuDni !== jQuery('#txtNroDocumentoCiudadano').val()*/) {
        var arrCampo = new Array();
        arrCampo[0] = "4=" + nuDni;
        //var bResult = fn_validaDestinatarioOtroDuplicado('tblDestEmiDocAdmCiudadano', arrCampo);
        if (true) {
            var p = new Array();
            p[0] = "accion=goBuscaCiudadano";
            p[1] = "pnuDoc=" + nuDni;
            ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                fn_rptaBuscaPJCiudadanoResp(data, dni);
            },
                    'json', false, false, "POST");
        } else {
           bootbox.alert("DESTINATARIO YA SE ENCUENTRA EN LISTA..");
        }
    }
    return false;
}
function fn_rptaBuscaPJCiudadanoResp(data, cell) {
    if (data !== null) {
        var coRespuesta = data.coRespuesta;
        var ciudadano = data.ciudadano;
        if (coRespuesta === "1") {  
            $($(cell).parent()).find('input[id=txtCodigoRemitente]').val(ciudadano.nuDoc);
            $($(cell).parent()).find('input[id=txtNombresPJCiudadano]').val(ciudadano.nombre);
            $($(cell).parent()).find('input[id=txtDniPJCiudadano]').val(ciudadano.nuDoc);
             
        } else {
            bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>");                           
        }
    }
}


function changeAccionBDForUDP_Cell(cmbChange,celda) {
    if (($(cmbChange).parent()).parent().find("td").eq(celda).html() === "BD") {
        ($(cmbChange).parent()).parent().find("td").eq(celda).html("UPD");
    }
}
function fn_buscaUbigeoEmi() {
    var nomDepCon = $("#txtNomDepCon").val();
    var nomProPa = $("#txtNomProPa").val();
    var nomDisDep = $("#txtNomDisDep").val();
    var codDepCon = $("#txtCodDepCon").val();
    var codProPa = $("#txtCodProPa").val();
    var codDisDep = $("#txtCodDisDep").val();

    var numCarSend = nomDepCon.length + nomProPa.length + nomDisDep.length;
    if (numCarSend <= 2) {
        alert_Danger("Error", "Ingresar 3 caracteres como minimo.");
        return;
    }
    var jsonBody =
            {
                "noDep": nomDepCon.toUpperCase(), "ubDep": codDepCon,
                "noPrv": nomProPa.toUpperCase(), "ubPrv": codProPa,
                "noDis": nomDisDep.toUpperCase(), "ubDis": codDisDep
            };
    var jsonString = JSON.stringify(jsonBody);
    ajaxCallSendJson("/srTablaConfiguracion.do?accion=goListaBusqUbigeoEmi", jsonString, function(data) {
        refreshScript("divUbigeoDetEmi", data);
        var c = $("#tblUbigeoDetalleEmi tr").length;
        if (c === 0) {
            alert_Danger("ATENCIÓN:", "No se encontraron coincidencias");
        }
        
    }, 'text', false, false, "POST");
    return;
}
function fn_limpiarUbigeoEmi() {
    $("#txtNomDepCon").attr("value", "").val("");
    $("#txtNomProPa").attr("value", "").val("");
    $("#txtNomDisDep").attr("value", "").val("");
    $("#txtCodDepCon").attr("value", "").val("");
    $("#txtCodProPa").attr("value", "").val("");
    $("#txtCodDisDep").attr("value", "").val("");
    /** LAAH 28092015- MODIFICACION SIGUIENDO RECOMENDACION 03 BUG #3405
     **/
    $('#tblUbigeoDetalleEmi > tbody').html('');
    $('#tblUbigeoDetalleEmi_info').hide();

}
function fn_seleccionarUbigeoEmision(cell) {
    var p = new Array();
    p[0] = "accion=goCargarUbigeoEmi";    
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null)
        {
            $("body").append(data);        
        }
        $('#txtTblUbigeoEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
        $('#txtTblUbigeoEmiColWhereButton').val(($(cell).parent()).index());
        $('#txtTblUbigeoEmiOrigenTablaWhereButton').val((((($(cell).parent()).parent()).parent()).parent()).attr("id"));        
    }, 'text', false, false, "POST");
    return false;
}
function changeTipoEmision_cell(cell) {     
     var pfila = (($(cell).parent()).parent()).index();
     var pcol = ($(cell).parent()).index();
     var tablaId = (((($(cell).parent()).parent()).parent()).parent()).attr("id");
     $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val('');
     $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtDniPJCiudadano]').val('');
     $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCodigoRemitente]').val(''); 
     if($(cell).val()=="03"){
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtDniPJCiudadano]').show();
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('button[id=btnOtroCiudadano]').hide();
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('button[id=btnCiudadano]').show();     
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').css("width","185px");   
    }
    if($(cell).val()=="04"){
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtDniPJCiudadano]').hide();
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('button[id=btnOtroCiudadano]').show();
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('button[id=btnCiudadano]').hide();    
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').css("width","250px");  
    }
    
}
function fu_callEventoTablaUbigeoEmi() {
    var idTablaUbi = "tblUbigeoDetalleEmi";
    fu_eventoGridTabla(idTablaUbi, paramConfUbigeoEmi);
    fn_eventProvedoresEmi(idTablaUbi);
}
function fn_eventProvedoresEmi(pIdTablaUbi) {
    var idTablaUbi = pIdTablaUbi;
    $("#" + idTablaUbi + " tbody").on("click", "tr", function() {
        var index = $(this).index();
        var noDep = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(0)").text();
        var noPrv = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(1)").text();
        var noDis = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(2)").text();
        var ubDep = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(3)").text();
        var ubPrv = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(4)").text();
        var ubDis = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(5)").text();
        var ambito = $("#tblUbigeoDetalleEmi tbody tr:eq(" + index + ") td:eq(6)").text();
        
        var ubi = noDep + "-" + noPrv + "-" + noDis;
        var ubiCod = ubDep + "," + ubPrv + "," + ubDis; 
        var pfila = $('#txtTblUbigeoEmiFilaWhereButton').val();
        var pcol = $('#txtTblUbigeoEmiColWhereButton').val();
        var tablaId = $('#txtTblUbigeoEmiOrigenTablaWhereButton').val();
        
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtUbigeoOtro]').val(ubi);
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCodigoUbigeoOtro]').val(ubiCod);
        $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCodigoUbigeoAmbito]').val(ambito);
        
         console.log($("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtUbigeoOtro]').val());
         console.log($("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCodigoUbigeoOtro]').val());
        removeDomId('windowConsultaUbigeoProveedorEmi');
    });
}

function fu_onKeyPressFiltrar_buscarNombress(evt, cell) {
    var pfila = (($(cell).parent()).parent()).index();
    var pcol = ($(cell).parent()).index();
    var tablaId = (((($(cell).parent()).parent()).parent()).parent()).attr("id");
    var valor = $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('select').val();  
    
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;     
    if ((key==13)) {
        if(valor=="04"){
            fn_buscarRemitenteOtroDocExtRecEmision(cell);
        }
        if(valor=="03"){
            fn_buscarRemitenteCiudadanoEmision(cell);
        }
    } 
}

function fn_buscarRemitenteOtroDocExtRecEmision(cell){
     
     
    var pfila = (($(cell).parent()).parent()).index();
    var pcol = ($(cell).parent()).index();
    var tablaId = (((($(cell).parent()).parent()).parent()).parent()).attr("id");
    var valor = $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val();   
    var pnomOtro=allTrim(fu_getValorUpperCase(valor)); 
    pnomOtro=$("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(pnomOtro))).val();    
    
    if( allTrim(pnomOtro).length >= 0 && allTrim(pnomOtro).length <= 3 ){
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll(); 
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
                ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                    fn_rptaBuscarRemitenteOtroDocExtRec(data);
                    
                     $('#txtTblDestProveedorEmiFilaWhereButton').val((($(cell).parent()).parent()).index());
                     $('#txtTblDestEmiProveedorColWhereButton').val(($(cell).parent()).index());
                     $('#txtTblDestEmiProveedorTablaWhereButton').val((((($(cell).parent()).parent()).parent()).parent()).attr("id"));   

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



function fn_iniConsOtroOrigenEmisor(){
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
                    fn_setOtroOrigenEditDocEmision(pdesDest,ptipDocInden,pnroDocInden,pcodDest);
                }
            }else if(evento.which==38||evento.which==40){ 
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
        fn_setOtroOrigenEditDocEmision(pdesDest,ptipDocInden,pnroDocInden,pcodDest);
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
            fn_setOtroOrigenEditDocEmision(pdesDest,ptipDocInden,pnroDocInden,pcodDest);    
        } 
        evento.preventDefault();
    });    
}
function fn_setOtroOrigenEditDocEmision(pdesDest,ptipDocInden,pnroDocInden,pcodDest) {
        var pfila = $('#txtTblDestProveedorEmiFilaWhereButton').val();
        var pcol = $('#txtTblDestEmiProveedorColWhereButton').val();
        var tablaId = $('#txtTblDestEmiProveedorTablaWhereButton').val();
         
       $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtNombresPJCiudadano]').val(pdesDest);
       $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtDniPJCiudadano]').val(pnroDocInden);
       $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[id=txtCodigoRemitente]').val(pcodDest);
 
       if ($("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(3)").text() === "BD") {
            $("#"+tablaId+" tbody tr:eq(" + pfila + ") td:eq(3)").text("UPD");
        }
        
        removeDomId('windowConsultaOtroOri');
    
}


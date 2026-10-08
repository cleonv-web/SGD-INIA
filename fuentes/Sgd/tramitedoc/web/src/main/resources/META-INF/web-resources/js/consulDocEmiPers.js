function fn_inicializaConsulEmiDocPersonal(){
    var noForm='#buscarConsulDocPersEmiBean';
    jQuery(noForm).find('#esFiltroFecha').val("3");//annio mes
    jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 3});    
    jQuery(noForm).find('#nuDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    pnumFilaSelect=0;
    changeTipoBusqEmiDocPersConsul("0",noForm);      
}
         
function changeTipoBusqEmiDocPersConsul(tipo,noForm) {
    jQuery(noForm).find('#tipoBusqueda').val(tipo);
    submitAjaxFormDocEmiPersConsul(tipo,noForm);
    mostrarOcultarDivBusqFiltro2();
}

function fn_preChangeTipoBusqEmiDocPersConsul(tipo){
    var noForm='#buscarConsulDocPersEmiBean';
    changeTipoBusqEmiDocPersConsul(tipo,noForm);
}

function submitAjaxFormDocEmiPersConsul(tipo,noForm) {
    var validaFiltro = fu_validarBusqFormEmiDocPersonalConsul(tipo,noForm);
    if (validaFiltro === "1") {
        ajaxCall("/srConsultaEmiDocPersonal.do?accion=goInicio", $(noForm).serialize(), function(data) {
            refreshScript("divTblConsulDocumentoEmitido", data);
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_validarBusqFormEmiDocPersonalConsul(tipo,noForm) {
    var valRetorno = "1";
    jQuery(noForm).find('#feEmiIni').val(jQuery(noForm).find('#fechaFiltro').attr('fini'));
    jQuery(noForm).find('#feEmiFin').val(jQuery(noForm).find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery(noForm).find("esIncluyeFiltro1").is(':checked');
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroEmiDocPersonalConsul(vFechaActual,noForm);     
    }else if(tipo==="1"){
        valRetorno = fu_validaEmiDocPersonalBuscarConsul(noForm);  
        if(valRetorno==="1"){
           if(pEsIncluyeFiltro){ 
              valRetorno = fu_validaFiltroEmiDocPersonalConsul(vFechaActual,noForm); 
           }else{
              valRetorno = setAnnioNoIncludeFiltroEmiDocPersonalConsul(noForm); 
           }
        }              
    }    
    return valRetorno;
}

function setAnnioNoIncludeFiltroEmiDocPersonalConsul(noForm){
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaConsulEmiDocPers(noForm);
    var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
       if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
            var pAnnio = jQuery(noForm).find('#nuAnn').val();
            if(!!pAnnio){
                var vValidaNumero=fu_validaNumero(pAnnio);
                if (vValidaNumero!=="OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }                
            }               
       }     

        var vFeInicio = jQuery(noForm).find("#feEmiIni").val();
        var vFeFinal = jQuery(noForm).find("#feEmiFin").val();

        if(valRetorno==="1"){
          var pAnnioBusq = obtenerAnioBusqueda(vFeInicio,vFeFinal);
            if(!!pAnnioBusq){
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero === "OK") {
                    jQuery(noForm).find('#nuAnn').val(pAnnioBusq);                          
                }else{
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";                    
                }                
            }               
        }
    }
    return valRetorno;       
}

function upperCaseBuscarDocumentoPersonalEmiConsulBean(noForm){
    jQuery(noForm).find('#nuCorEmi').val(fu_getValorUpperCase(jQuery(noForm).find('#nuCorEmi').val()));
    jQuery(noForm).find('#nuDoc').val(fu_getValorUpperCase(jQuery(noForm).find('#nuDoc').val()));
    jQuery(noForm).find('#deAsu').val(fu_getValorUpperCase(jQuery(noForm).find('#deAsu').val()));    
}

function fu_validaEmiDocPersonalBuscarConsul(noForm) {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoPersonalEmiConsulBean(noForm);
    
    var vNroEmision = jQuery(noForm).find('#nuCorEmi').val();
    var vNroDocumento = jQuery(noForm).find('#nuDoc').val();
    var vAsunto = jQuery(noForm).find('#deAsu').val();
    if(!!vAsunto){
        //validar caracteres especiales
        vAsunto = jQuery(noForm).find('#deAsu').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vAsunto))).val();
        if(allTrim(vAsunto).length >= 0 && allTrim(vAsunto).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }         
    }
    if(!(!!vNroEmision||!!vNroDocumento||!!vAsunto)){
       alert_Warning("Buscar: ","Ingresar Algún parámetro de Búsqueda");
       valRetorno = "0";
    }
    
    if(valRetorno==="1"){
        if (!!vNroEmision) {
            var vValidaNumero = fu_validaNumero(vNroEmision);
            if (vValidaNumero !== "OK") {
                alert_Warning("Buscar: ","N° de Emisión debe ser solo números.");
                valRetorno = "0";
            }
        }
    }
    return valRetorno;
}

function fu_obtenerEsFiltroFechaConsulEmiDocPers(nameForm){
    var opt = jQuery(nameForm).find('#fechaFiltro').attr('optselected');
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery(nameForm).find("#esFiltroFecha").val("1");//"1" rango fechas 
    }else if(opt==="5"||opt==="6"){
       jQuery(nameForm).find("#esFiltroFecha").val("2");//"2" solo año
    }else if(opt==="3"||opt==="7"){
       jQuery(nameForm).find("#esFiltroFecha").val("3");//"3" año mes
    }
}

function fu_validaFiltroEmiDocPersonalConsul(vFechaActual,noForm){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFechaConsulEmiDocPers(noForm);
        var pEsFiltroFecha = jQuery(noForm).find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery(noForm).find('#nuAnn').val();
                if(!!pAnnio){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero!=="OK") {
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
                    var vValidaNumero=fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero==="OK") {
                        jQuery(noForm).find('#nuAnn').val(pAnnioBusq);                          
                    }else{
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";                        
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

function fu_iniTblDocPersEmiConsul(){
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
                if (index === 5 || index === 8 || index === 9 || index === 10) {
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
            var pnuAnn=$(this).children('td')[0].innerHTML;
            var pnuEmi=$(this).children('td')[1].innerHTML;
            if (!!pnuAnn&&!!pnuEmi) {
                jQuery('#txtpnuAnn').val(pnuAnn);
                jQuery('#txtpnuEmi').val(pnuEmi);
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

function fn_detDocEmiPersConsul(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    if(!!pnuAnn&&!!pnuEmi){
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        ajaxCall("/srConsultaEmiDocPersonal.do?accion=goDetDocumentoPers", p.join("&"), function(data) {
            refreshScript("divDetEmiDocumPersConsul", data);
            jQuery('#divDocEmiPersonalConsul').hide();
            jQuery('#divDetEmiDocumPersConsul').show();
            jQuery('#divTblConsulDocumentoEmitido').html("");
        }, 'text', false, false, "POST");          
    }
}

function fn_verDocReferenciaDocPersConsul(objBtn){
    var pnuAnn=($(objBtn).parent()).parent().children().get(0).innerHTML;
    var pnuEmi=($(objBtn).parent()).parent().children().get(1).innerHTML;
    fn_verDocEmiPersConsul(pnuAnn,pnuEmi);
}

function fn_verDocEmiPersConsul(pnuAnn,pnuEmi){
    var ptiOpe = "0";
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    }   
}

function fn_verDocPersEmiDetConsul(){
    var noForm='#documentoEmiPersConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();
    fn_verDocEmiPersConsul(pnuAnn,pnuEmi);
}

function fn_verAnexoDocEmiPersConsul(pnuAnn,pnuEmi){
    var pnuDes = "N";
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    }    
}

function fn_verAnexoDocPersEmiDetConsul(){
    var noForm='#documentoEmiPersConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();
    fn_verAnexoDocEmiPersConsul(pnuAnn,pnuEmi);
}

function fn_verSeguimientoDocEmiPersConsul(pnuAnn,pnuEmi){
    var pnuDes = "N";
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    }    
}

function fn_verSeguimientoDocPersEmiDetConsul(){
    var noForm='#documentoEmiPersConsulBean';
    var pnuAnn=jQuery(noForm).find('#nuAnn').val();
    var pnuEmi=jQuery(noForm).find('#nuEmi').val();
    fn_verSeguimientoDocEmiPersConsul(pnuAnn,pnuEmi);    
}

function regresarEmiDocumPerConsul(){
    jQuery('#divDetEmiDocumPersConsul').toggle();                                
    jQuery('#divDocEmiPersonalConsul').toggle();    
    var noForm='#buscarConsulDocPersEmiBean';
    changeTipoBusqEmiDocPersConsul(jQuery(noForm).find('#tipoBusqueda').val(),noForm);
    jQuery('#divDetEmiDocumPersConsul').html(""); 
}

function fn_verDocEmiPersLsConsul(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    fn_verDocEmiPersConsul(pnuAnn,pnuEmi);
}

function fn_verAnexoDocEmiPerLsConsul(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();
    fn_verAnexoDocEmiPersConsul(pnuAnn,pnuEmi);
}

function fn_verSeguimientoDocEmiPerLsConsul(){
    var pnuAnn=jQuery('#txtpnuAnn').val();
    var pnuEmi=jQuery('#txtpnuEmi').val();    
    fn_verSeguimientoDocEmiPersConsul(pnuAnn,pnuEmi);    
}

function fn_iniConsDestDocEmiPers(){
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
                        fu_setDatoDestinatarioDocEmiPersConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDestinatarioDocEmiPersConsul(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDestinatarioDocEmiPersConsul(pcodDest,pdesDest){
    var noForm='#buscarConsulDocPersEmiBean';
    jQuery(noForm).find('#txtDependenciaDes').val(pdesDest);
    jQuery(noForm).find('#coDepDestino').val(pcodDest);
    removeDomId('windowConsultaDestinoEmi');    
}

function fn_buscaDestinatarioDocEmiPersConsul(){
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    ajaxCall("/srConsultaEmiDocPersonal.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },'text', false, false, "POST");
}

function fn_buscaReferenciaOrigenDocEmiPersConsul(){
    var p = new Array();    
    p[0] = "accion=goBuscaReferenciaOrigen";	    
    ajaxCall("/srConsultaEmiDocPersonal.do",p.join("&"),function(data){
           fn_rptaBuscaReferenciaOrigenPersonal(data); 
        },
    'text', false, false, "POST");       
}

function fn_iniConsRefOriDocEmiPersConsul(){
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
                        fu_setDatoReferenOrigenDocEmiPersConsul(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbReferenOrig tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoReferenOrigenDocEmiPersConsul(pcodDest,pdesDest);            
        });    
}

function fu_setDatoReferenOrigenDocEmiPersConsul(cod, desc) {
    var noForm='#buscarConsulDocPersEmiBean';
    jQuery(noForm).find('#txtRefOrigen').val(desc);
    jQuery(noForm).find('#coDepRef').val(cod);
    removeDomId('windowConsultaRefOri');
}

function fu_cleanEmiDocPersConsul(tipo){
    var noForm='#buscarConsulDocPersEmiBean';
    if(tipo==="1"){
        jQuery(noForm).find("#nuCorEmi").val("");
        jQuery(noForm).find("#nuDoc").val("");
        jQuery(noForm).find("#deAsu").val("");
        jQuery(noForm).find("#esIncluyeFiltro1").prop('checked',false);
        jQuery(noForm).find("#esIncluyeFiltro1").attr('checked',false);        
        jQuery(noForm).find("#feEmiIni").val("");
        jQuery(noForm).find("#feEmiFin").val("");
    }else{
        jQuery(noForm).find("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#tipoDoc option[value=]").prop("selected", "selected");
        //jQuery("#sPrioridadDoc option[value=]").prop("selected", "selected");
        jQuery(noForm).find("#coDepRef").val("");
        jQuery(noForm).find("#txtRefOrigen").val(" [TODOS]");
        jQuery(noForm).find("#coDepDestino").val("");
        jQuery(noForm).find("#txtDependenciaDes").val(" [TODOS]");        
        jQuery(noForm).find("#esFiltroFecha").val("3");
        jQuery(noForm).find("#nuAnn").val(jQuery("#txtAnnioActual").val());        
        jQuery("#fechaFiltro").showDatePicker({defaultOpcionSelected: 3});
    }    
}

function fu_generarReporteEmiDocPerConsulXLS(){
    fu_generarReporteDocEmiPersConsul('XLS');
}

function fu_generarReporteEmiDocPerConsulPDF(){
    fu_generarReporteDocEmiPersConsul('PDF')
}

function fu_generarReporteDocEmiPersConsul(pformatoReporte){
    var noForm='#buscarConsulDocPersEmiBean';
    var validaFiltro = fu_validarBusqFormEmiDocPersonalConsul("0",noForm);
    if (validaFiltro === "1") {
        //ajaxCall("/srConsultaEmiDocPersonal.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
        ajaxCall("/srConsultaEmiDocPersonal.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $(noForm).serialize(), function(data) {
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
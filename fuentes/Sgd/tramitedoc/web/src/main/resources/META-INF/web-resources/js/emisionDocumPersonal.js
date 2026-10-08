
var $objFilaReferenciaPers;
function fn_searchDocReferentblPersonal(cell) {
    var p = new Array();
    p[0] = "accion=goBuscaDocumentotblReferenciaPerso";
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
        $objFilaReferenciaPers = ($(cell).parent()).parent().clone();       
    },'text', false, false, "POST");
}


function fn_agregarReferenciasEmiPersonal(){
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
            fu_setDatoDocumentoRefEmiPersonal(pnuAnn,pnuEmi,pnuDes,pnuDoc,pfeEmi,pnuAnnExp,pnuSecExp,pnuExpediente);            
        }else{
            var maxNum='0';
            $('#tblRefEmiDocAdm tbody input[type="radio"]').each(function()  { 
                var rbGrupo = $(this).attr('name');
                var idGrupo = rbGrupo.replace('group','');            
                if(idGrupo>maxNum){
                    maxNum=idGrupo;
                }
            });
            $objFilaReferenciaPers.find('input[type=radio]').attr('name', 'group' + (parseInt(maxNum)+1));
            $("#tblRefEmiDocAdm tbody tr:eq("+(posFila-1)+")").after("<tr>"+$objFilaReferencia.html()+"</tr>"); 
            $('#txtTblRefEmiFilaWhereButton').val(posFila);
            fu_setDatoDocumentoRefEmiPersonal(pnuAnn,pnuEmi,pnuDes,pnuDoc,pfeEmi,pnuAnnExp,pnuSecExp,pnuExpediente);
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
function fu_setDatoDocumentoRefEmiPersonal(pnuAnn, pnuEmi, pnuDes, pnuDoc, pfeEmi, pnuAnnExp, pnuSecExp, pnuExpediente) {
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
       
    } else {
        removeDomId('windowConsultaDocumentoRefEmi');
       bootbox.alert("DOCUMENTO YA SE ENCUENTRA REFERENCIADO.");
    }
    return false;
}


function fn_inicializaDocPersonalEmi(sCoAnnio){
    //jQuery("#fechaFiltro").html("Año: "+sCoAnnio);
    jQuery('#buscarDocumentoPersonalEmiBean').find('#esFiltroFecha').val("2");//solo año
    /*jQuery("#fechaFiltro").showDatePicker({
        showDia: false,
        selectTodosMeses:true,
        pressAceptarEvent: function(data) {
            if(data.rbOp==="0"){
                jQuery('#buscarDocumentoPersonalEmiBean').find('#sCoAnnio').val(data.anio);
                if(data.mes==="" && data.anio!==""){
                   jQuery("#fechaFiltro").html("Año: "+data.anio);
                   jQuery('#buscarDocumentoPersonalEmiBean').find('#esFiltroFecha').val("2");//solo año
                }else{
                   jQuery("#fechaFiltro").html("Año: "+data.anio+"  Mes: "+monthYearArray[data.mes * 1]);  
                   jQuery('#buscarDocumentoPersonalEmiBean').find('#esFiltroFecha').val("3");//año y mes
                }
            }else if(data.rbOp==="1"){
                jQuery('#buscarDocumentoPersonalEmiBean').find('#esFiltroFecha').val("1");//rango fecha
                jQuery("#fechaFiltro").html("Del: "+data.fIni+"  Al: "+data.fFin); 
            }
        }
    });*/
    jQuery('#buscarDocumentoPersonalEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});    
    jQuery('#buscarDocumentoPersonalEmiBean').find('#sNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });     
    pnumFilaSelect=0;
    changeTipoBusqEmiDocuPersonal("0");    
}

function changeTipoBusqEmiDocuPersonal(tipo){
    jQuery('#sTipoBusqueda').val(tipo);
    submitAjaxFormEmiDocPersonal(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormEmiDocPersonal(tipo){
var validaFiltro=fu_validaFiltroEmiDocPersonal(tipo);
    if (validaFiltro==="1") {
        ajaxCall("/srDocumentoEmisionPersonal.do?accion=goInicio",$('#buscarDocumentoPersonalEmiBean').serialize(),function(data){
                refreshScript("divTablaEmiDocumenAdm", data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fu_validaFiltroEmiDocPersonal(tipo) {
    var valRetorno = "1";
    jQuery('#sFeEmiIni').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#sFeEmiFin').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = jQuery("#esIncluyeFiltro1").is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if(tipo==="0"){
        valRetorno = fu_validaFiltroEmiDocPersonalFiltrar(vFechaActual);     
    }else if(tipo==="1"){
      valRetorno = fu_validaFiltroEmiDocPersonalBuscar();  
      if(pEsIncluyeFiltro&&valRetorno==="1"){
         valRetorno = fu_validaFiltroEmiDocPersonalFiltrar(vFechaActual); 
      }
    }    
    return valRetorno;
}

function fu_validaFiltroEmiDocPersonalBuscar() {
    var valRetorno = "1";
    
    upperCaseBuscarDocumentoPersonalBean();
    
    var vNroEmision = jQuery('#sNumCorEmision').val();
    var vNroDocumento = jQuery('#sNumDoc').val();
    var vAsunto = jQuery('#sDeAsuM').val();
    
    if((typeof(vNroEmision)==="undefined" || vNroEmision===null || vNroEmision==="") &&
       (typeof(vNroDocumento)==="undefined" || vNroDocumento===null || vNroDocumento==="") &&
       (typeof(vAsunto)==="undefined" || vAsunto===null || vAsunto==="")){
       //alert("Ingresar Algún parámetro de Búsqueda");
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

function fu_validaFiltroEmiDocPersonalFiltrar(vFechaActual){
    var valRetorno = "1";
    if (valRetorno==="1") {
        fu_obtenerEsFiltroFecha('buscarDocumentoPersonalEmiBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoPersonalEmiBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoPersonalEmiBean').find('#sCoAnnio').val();
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
                        jQuery('#buscarDocumentoPersonalEmiBean').find('#sCoAnnio').val(pAnnioBusq);                          
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

function fu_cleanEmiDocPersonal(tipo){
    if(tipo==="1"){
        jQuery("#sNumDoc").val("");
        jQuery("#sNumDocRef").val("");
//        jQuery("#sBuscNroExpediente").val("");
        jQuery("#sDeAsuM").val("");
        jQuery("#sDeTipoDocAdm option[value=]").prop("selected", "selected");
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
    }else{
        jQuery("#sEstadoDoc option[value=5]").prop("selected", "selected");
        jQuery("#sPrioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#sTipoDoc option[value=]").prop("selected", "selected");
        jQuery("#sRefOrigen").val("");
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#sDestinatario").val("");
        jQuery("#txtDestinatario").val("[TODOS]");
        jQuery("#esFiltroFecha").val("2");
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val());
        jQuery("#sCoAnnio").val(jQuery("#txtAnnioActual").val());        
        jQuery("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
    }
}

function fn_buscaReferenciaOrigenPersonal(){
    var p = new Array();    
    p[0] = "accion=goBuscaReferenciaOrigen";	    
    ajaxCall("/srDocumentoEmisionPersonal.do",p.join("&"),function(data){
           fn_rptaBuscaReferenciaOrigenPersonal(data); 
        },
    'text', false, false, "POST");       
}

function fn_rptaBuscaReferenciaOrigenPersonal(XML_AJAX){
   if(XML_AJAX !== null){
       $("body").append(XML_AJAX);
   }    
}

function fn_buscaDestinatarioEmiPersonal(){
    var p = new Array();    
    p[0] = "accion=goBuscaDestinatario";	    
    ajaxCall("/srDocumentoEmisionPersonal.do",p.join("&"),function(data){
           fn_rptaBuscaDestinatarioEmiPersonal(data); 
        },
    'text', false, false, "POST");       
}

function fn_rptaBuscaDestinatarioEmiPersonal(XML_AJAX){
   if(XML_AJAX !== null){
       $("body").append(XML_AJAX);
   }    
}

function fu_eventoTablaEmiDocPersonal(){
var oTable;
    oTable = $('#myTableFixed').dataTable( {
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
                                {"bSortable": false},
                                {"bSortable": false},                                            
                                {"bSortable": false},                                
                                {"bSortable": false}
                        ]         
    } );
    
        jQuery.fn.dataTableExt.oSort['fecha-asc']  = function(a,b) {
            var ukDatea = a.split('/');
            var ukDateb = b.split('/');

            var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
            var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

            return ((x < y) ? -1 : ((x > y) ?  1 : 0));
        };

        jQuery.fn.dataTableExt.oSort['fecha-desc'] = function(a,b) {
            var ukDatea = a.split('/');
            var ukDateb = b.split('/');

            var x = (ukDatea[2] + ukDatea[1] + ukDatea[0]) * 1;
            var y = (ukDateb[2] + ukDateb[1] + ukDateb[0]) * 1;

            return ((x < y) ? 1 : ((x > y) ?  -1 : 0));
        };    
    //$("#myTableFixed thead tr").addClass("ui-datatable-fixed-scrollable-header ui-state-default")
    //$("#myTableFixed tbody tr").addClass("bx_sb ui-datatable-fixed-scrollable-body ui-datatable-fixed-data");
    function showdivToolTip(elemento,text)
    {
        $('#divflotante').html(text);

        var x=elemento.left;
        var y=elemento.top + 24;
        $("#divflotante").css({left:x,top:y});

        //document.getElementById('divflotante').style.top = elemento.top + 24;
        //document.getElementById('divflotante').style.left = elemento.left;		
        document.getElementById('divflotante').style.display='block';

        return;
    }
   /* YUAL
     $("#myTableFixed tbody td").hover(
        function(){    
            $(this).attr('id','divtitlemostrar');
            //console.log($(this).index());
            var index = $(this).index();
            if(index === 3 || index === 7 || index === 8){
                showdivToolTip($('#divtitlemostrar').position(),$(this).html());
            }
        },  
        function(){
            $('#divtitlemostrar').removeAttr('id');
            $('#divflotante').hide();
        }			
    );*/
    $("#myTableFixed tbody tr").click( function( e ) {
            if ( $(this).hasClass('row_selected') ) {
                    //$(this).removeClass('row_selected');
                    //jQuery('#txtpnuAnn').val("");
            }
            else {
                    oTable.$('tr.row_selected').removeClass('row_selected');
                    $(this).addClass('row_selected');

                    /*obtiene datos*/
                    //alert($(this).children('td')[12].innerHTML);
                    //jQuery('#txtTextIndexSelect').val("0");
                    if(typeof($(this).children('td')[10]) !== "undefined"){
                        jQuery('#txtpnuAnn').val($(this).children('td')[10].innerHTML);
                        jQuery('#txtpnuEmi').val($(this).children('td')[11].innerHTML);
                        jQuery('#txtpexisteDoc').val($(this).children('td')[12].innerHTML);
                        jQuery('#txtpexisteAnexo').val($(this).children('td')[13].innerHTML);
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

function fn_verSeguimientoEmiPersonal() {
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

function fn_verAnexoEmiPersonal() {
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

function fn_verDocumentoEmiPersonal(){

    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var ptiOpe = "0";
    
    if (pnuAnn) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        //alert("Error en Datos, Ingrese al documento nuevamente.");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
    
}

function editarDocumentoEmiPersonalClick(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo){
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;	
        p[1] = "pnuEmi=" + pnuEmi;        
        p[2] = "pexisteDoc=" + pexisteDoc;
        p[3] = "pexisteAnexo=" + pexisteAnexo;
        ajaxCall("/srDocumentoEmisionPersonal.do?accion=goEditDocumentoEmi",p.join("&"),function(data){
                refreshScript("divNewEmiDocumAdmin", data);
                jQuery('#divEmiDocumentoAdmin').hide();
                jQuery('#divNewEmiDocumAdmin').show();
                jQuery('#divTablaEmiDocumenAdm').html("");
                fn_cargaToolBarEmiPersonal();
        }, 'text', false, false, "POST");              
    }else{
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }    
}

function editarDocumentoEmiPersonal(){
    var pnuAnn = jQuery('#txtpnuAnn').val();
      if(pnuAnn !== ""){  
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;	
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();        
        p[2] = "pexisteDoc=" + jQuery('#txtpexisteDoc').val();
        p[3] = "pexisteAnexo=" + jQuery('#txtpexisteAnexo').val();
        ajaxCall("/srDocumentoEmisionPersonal.do?accion=goEditDocumentoEmi",p.join("&"),function(data){
                refreshScript("divNewEmiDocumAdmin", data);
                jQuery('#divEmiDocumentoAdmin').hide();
                jQuery('#divNewEmiDocumAdmin').show();
                jQuery('#divTablaEmiDocumenAdm').html("");
                fn_cargaToolBarEmiPersonal();
        }, 'text', false, false, "POST");              
    }else{
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }    
}

//YUAL
function notificarVistoBueno(){
    var p = new Array();
    p[0] = "pnuAnn=" + jQuery('#nuAnn').val(); 
    p[1] = "pnuEmi=" + jQuery('#nuEmi').val();        
    ajaxCall("/srDocumentoEmisionPersonal.do?accion=goNotificarVistoBueno",p.join("&"),function(data){
       alert_Sucess("VISTO BUENO:","Notificación Emitida");
    }, 'text', false, false, "POST");              
   
}


function fn_inicializaEditEmiDocPersonal(sTipoDestEmi,sEstadoDocAdm){
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if(data.hasChangedText){
                changeAccionBDDocEmi(data.currentObject);
            }
        },maxNumCar:600
    });
    jQuery('#documentoPersonalEmiBean').find('#nuDiaAte').change(function(){
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoPersonalEmiBean').find('#deDocSig').change(function(){
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoPersonalEmiBean').find('#coDepEmi').change(function(){
        fu_changeRemitenteEmiBean();
    });
    jQuery('#documentoPersonalEmiBean').find('#coLocEmi').change(function(){
        fu_changeRemitenteEmiBean();
    });    
//    jQuery('#documentoPersonalEmiBean').find('#coTipDocAdm').change(function(){
//        fu_changeDocumentoEmiBean();
//    });
    jQuery('#documentoPersonalEmiBean').find('#deAsu').change(function(){
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').change(function(){
        var noForm='#documentoPersonalEmiBean';
	var pnuAnn = jQuery(noForm).find('#nuAnn').val();
        var pnuEmi = jQuery(noForm).find('#nuEmi').val();
        var ptiEmi = jQuery(noForm).find('#tiEmi').val();
        var pcoTipDocAdm = jQuery(noForm).find('#coTipDocAdm').val();
        var pcoDepEmi = jQuery(noForm).find('#coDepEmi').val();
        var pcoEmpEmi = jQuery(noForm).find('#coEmpEmi').val();
        var pnuDocEmiAnn = jQuery(noForm).find('#txtnuDocEmiAn').val();
        var pnuDocEmi = allTrim(jQuery(noForm).find('#nuDocEmi').val());
        if(!!pnuDocEmi&&!!pnuAnn&&!!pnuEmi){
            var vValidaNumero = fu_validaNumero(pnuDocEmi);
            if (vValidaNumero==="OK") {
                pnuDocEmi = replicate(pnuDocEmi,6);
                if(pnuDocEmiAnn !== pnuDocEmi || (typeof(pnuDocEmiAnn) !== "undefined" && pnuDocEmiAnn === "")){
                    fn_jsonVerificarNumeracionDocPersonalEmi(pnuDocEmiAnn,pnuAnn,pnuEmi,ptiEmi,pcoTipDocAdm,pcoDepEmi,pnuDocEmi,pcoEmpEmi);
                }else{
                   jQuery('#nuDocEmi').val(pnuDocEmiAnn);  
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
   //fn_changeTipoDestinatarioDocuEmi(sTipoDestEmi,sEstadoDocAdm);    
   fu_cargaEdicionDocAdm("02",sEstadoDocAdm); 
   if(jQuery("#txtEsNuevoDocAdm").val() === '1'){
       jQuery("#coTipDocAdm").focus();
   }   
}

function fn_jsonVerificarNumeracionDocPersonalEmi(/*ptipoCmb,*/pnuDocEmiAnn,pnuAnn,pnuEmi,ptiEmi,pcoTipDocAdm,pcoDepEmi,pnuDocEmi,pcoEmpEmi){
    var p = new Array();    
    p[0] = "accion=goVerificarNumDocEmi";	    
    p[1] = "nuDoc=" + pnuDocEmiAnn;
    p[2] = "nuAnn=" + pnuAnn; 
    p[3] = "nuEmi=" + pnuEmi; 
    p[4] = "tiEmi=" + ptiEmi; 
    p[5] = "coTipDocAdm=" + pcoTipDocAdm; 
    p[6] = "coDepEmi=" + pcoDepEmi; 
    p[7] = "nuDocEmi=" + pnuDocEmi; 
    p[8] = "coEmpEmi=" + pcoEmpEmi; 
    ajaxCall("/srDocumentoEmisionPersonal.do",p.join("&"),function(data){
         fn_rptaJsonVerificarNumeracionDocEmiPersonal(data,pnuDocEmiAnn/*,ptipoCmb*/,pcoTipDocAdm);
     },'json', false, false, "POST");   
}

function fn_rptaJsonVerificarNumeracionDocEmiPersonal(JSON_AJAX,pnuDocEmiAnn/*,ptipoCmb*/,pcoTipDocAdm){
    if(JSON_AJAX !== null){
        if(JSON_AJAX.coRespuesta==="1"){
            var nuDocEmi = replicate(jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val(),6);
            jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val(nuDocEmi);            
            jQuery('#documentoPersonalEmiBean').find('#nuCorDoc').val("1");            
            jQuery('#txtnuDocEmiAn').val(nuDocEmi);
        }else{
            alert_Danger('',JSON_AJAX.deRespuesta);
            var pcoTipDocAdmAn = jQuery('#documentoPersonalEmiBean').find('#txtcoTipDocAdmAn').val();
            if(pcoTipDocAdm === pcoTipDocAdmAn){
              jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val(pnuDocEmiAnn);
            }else{
              jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val("");
              jQuery('#documentoPersonalEmiBean').find('#txtnuDocEmiAn').val("");
            }
        }
    }else{
        alert_Danger("Error!","Verificando Numeración del Documento");
        jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val("");
        jQuery('#documentoPersonalEmiBean').find('#txtnuDocEmiAn').val("");
    }
}

function fu_goNuevoEmisionDocPersonal(){
    var validaFiltro="1";/*fu_validaFiltroEmiDocAdm(tipo);*/
    if (validaFiltro==="1") {
        ajaxCall("/srDocumentoEmisionPersonal.do?accion=goNuevoDocumentoEmi",'',function(data){
                jQuery('#divEmiDocumentoAdmin').hide();
                jQuery('#divNewEmiDocumAdmin').show();            
                refreshScript("divNewEmiDocumAdmin", data);
                fn_cargaToolBarEmiPersonal();
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fn_buscarFirmadoPorPersonal(){
    var p = new Array();    
    p[0] = "accion=goBuscaFirmadoPorEdit";	    
    p[1] = "pcoDep=" + jQuery('#documentoPersonalEmiBean').find('#coDepEmi').val();
    ajaxCall("/srDocumentoAdmEmision.do",p.join("&"),function(data){
           fn_rptaBuscaEmpleado(data); 
        },
    'text', false, false, "POST");               
}

function continueBackLsDocEmiPersonal(){
    if(jQuery('#divRecepDocumentoAdmin').length===0){
        jQuery('#divEmiDocumentoAdmin').toggle();                                
        jQuery('#divNewEmiDocumAdmin').toggle(); 
        submitAjaxFormEmiDocPersonal(jQuery('#buscarDocumentoPersonalEmiBean').find('#sTipoBusqueda').val());
        jQuery('#divNewEmiDocumAdmin').html("");      
        //mostrarOcultarDivBusqFiltro2();               
    }else{
        var pnuAnn = jQuery('#txtpnuAnn').val();
          if(pnuAnn !== ""){  
            var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;	
            p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();        
            p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
            p[3] = "pcoPri=" + jQuery('#txtpcoPri').val();
            ajaxCall("/srDocumentoAdmRecepcion.do?accion=goEditarDocumento",p.join("&"),function(data){
                    refreshScript("divWorkPlaceRecepDocumAdmin", data);
                    fn_cargaToolBarRec();
            }, 'text', false, false, "POST");              
        }else{
           bootbox.alert("Seleccione una fila de la lista");
        }        
    }    
}

function backLsDocEmiPersonal(pclickBtn){
    if (pclickBtn === "1") {
        var vEsDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
        if (vEsDocEmi === "5" || vEsDocEmi === "7") {
            var rpta = fu_verificarChangeDocumentoEmiPersonal();
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
                                continueBackLsDocEmiPersonal();
                            }                            
                        },
                        NO: {
                            label: "NO",
                            className: "btn-primary"
                        }
                    }
                });
            } else {
                continueBackLsDocEmiPersonal();
            }
        } else {
            continueBackLsDocEmiPersonal();
        }
    } else {
        continueBackLsDocEmiPersonal();
    }
}

function cerrarPantallaModuloEmisionDocPersonal(){
    var vEsDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
    if(vEsDocEmi==="5" || vEsDocEmi==="7"){    
        var rpta = fu_verificarChangeDocumentoEmiPersonal();
        var nrpta = rpta.substr(0,1);
        if(nrpta === "1"){
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
        }else{
            cerrarPantalla();                
        }
    }else{
        cerrarPantalla();            
    }
    
}

function fn_abrirDocumentoPersonal() {

    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();

    var ptiOpe = "5";

    if (!!pnuAnn && !!pnuEmi) {
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
                    //result = fn_abrirDocApplet(docs.noUrl, docs.noDoc);
                    var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
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

function fu_verificarChangeDocumentoEmiPersonal(){//si es "1" necesita grabar el documento.
    //jQuery('#documentoPersonalEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmiPersonal();
    var rpta = fn_validarEnvioGrabaDocEmiPersonal(new Function('return '+cadenaJson)());
    var nrpta = rpta.substr(0,1);
    if(nrpta === "1"||!(!!jQuery('#documentoPersonalEmiBean').find('#nuEmi').val()&&!!jQuery('#documentoPersonalEmiBean').find('#nuAnn').val())){
		return "1";
	}else{
        //alert_Info("",rpta.substr(1));
		return rpta.substr(1);
    }	
}

function fn_buildSendJsontoServerDocuEmiPersonal(){
    var result="{";
    result = result + '"nuAnn":"' + $("#nuAnn").val() + '",';
    result = result + '"nuEmi":"' + $("#nuEmi").val() + '",';
    //result = result + '"documentoEmiBean":' + JSON.stringify($('#documentoEmiBean').serializeFormJSON()) + ',';
    var valEnvio = jQuery('#envDocumentoEmiBean').val();
    if(valEnvio === "1"){
        result = result + '"documentoEmiBean":' + JSON.stringify(getJsonFormDocPersonalEmiBean()) + ',';
    }
//    valEnvio = jQuery('#envExpedienteEmiBean').val();
//    if(valEnvio === "1"){
//        result = result + '"expedienteEmiBean":' + JSON.stringify(getJsonFormExpedienteEmiBean()) + ',';
//    }
    valEnvio = jQuery('#envRemitenteEmiBean').val();
    if(valEnvio === "1"){
        result = result + '"remitenteEmiBean":' + JSON.stringify(getJsonFormPersRemitenteEmiBean()) + ',';
    }
    result= result + '"lstReferencia":' + fn_tblRefEmiDocAdmToJson() + ',';
    result= result + '"lstDestinatario":' + sortDelFirst(fn_tblDestEmiDocPersonalToJson()) + ',';
    result = result + '"lstEmpVoBo":' + sortDelFirst(fn_tblEmpVoBoDocAdmToJson());//add vobo
    return result + "}";
}

function getJsonFormDocPersonalEmiBean(){
    var arrCampoBean = new Array();  
    arrCampoBean[0] = "nuAnn";
    arrCampoBean[1] = "nuEmi";
    arrCampoBean[2] = "coTipDocAdm";
    arrCampoBean[3] = "nuDocEmi";
    arrCampoBean[4] = "feEmiCorta";
    arrCampoBean[5] = "nuDiaAte";
    arrCampoBean[6] = "deAsu";
    arrCampoBean[7] = "esDocEmi";
    arrCampoBean[8] = "coDepEmi";
    arrCampoBean[9] = "coEmpEmi";
    arrCampoBean[10] = "coLocEmi";
    arrCampoBean[11] = "tiEmi";
    arrCampoBean[12] = "deDocSig";
    arrCampoBean[13] = "nuCorDoc";
 
    var noForm='#documentoPersonalEmiBean';
    var o = {};
    arrCampoBean.forEach(function(campo) {
        var valCampo=jQuery(noForm).find('#'+campo).val();
        if(!!valCampo){
            o[campo]=valCampo;
        }else{
            o[campo]=null;
        }
    });
    console.log(o);
    return o;     
}

function getJsonFormPersRemitenteEmiBean(){
  var arrCampoBean = new Array();  
  arrCampoBean[0] = "coDependencia=coDepEmi";
  arrCampoBean[1] = "coEmpFirma=coEmpEmi";
  arrCampoBean[2] = "coLocal=coLocEmi";
  var noForm='#documentoPersonalEmiBean';
  var o = {};
  arrCampoBean.forEach(function(campo) {
	var campoAux = campo.split("=");
	var valCampo=jQuery(noForm).find('#'+campoAux[1]).val();
	if(!!valCampo){
		o[campoAux[0]]=valCampo;
	}else{
		o[campoAux[0]]=null;
	}  
  });
  return o;      
}

function fn_tblDestEmiDocPersonalToJson(){
    var json = '[';
    var itArr = [];
    var tbl1 = fn_tblDestIntituEmiDocAdmToJson();
    tbl1 !== "" ? itArr.push(tbl1) : "";
//    var tbl2 = fn_tblDestProveedorEmiDocAdmToJson();
//    tbl2 !== "" ? itArr.push(tbl2) : "";
//    var tbl3 =fn_tblDestCiudadanoEmiDocAdmToJson();
//    tbl3 !== "" ? itArr.push(tbl3) : "";    
//    var tbl4 =fn_tblDestOtroEmiDocAdmToJson();
//    tbl4 !== "" ? itArr.push(tbl4) : "";    
    json += itArr.join(",") + ']';
    return json;
}

function fn_grabarDocumentoEmiPersonal(){
  
             
    //Yual
    var intVobo=jQuery('#tblPersVoBoDocAdm tr:last').index();

   // alert(intVobo);
    if(intVobo>0){
       $('#divEnviarVistoBueno').show();
    }
    else{$('#divEnviarVistoBueno').hide();}
    //end yual
                        
	var validaFiltro = fu_verificarCamposDocEmiPersonal('0','');
	if(validaFiltro === "1"){
                var pesDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
		var rpta = fu_verificarDestinatario("1");
		var nrpta = rpta.substr(0,1);
                var vResult="0";
                if(pesDocEmi==="7"){
                    if(nrpta === "1"){
                        vResult="1";
                    }
                }else{
                    if(nrpta === "1" || nrpta === "E"){
                        vResult="1";
                    }
                }
                if(vResult==="1"){
                    rpta = fu_verificarReferencia();
                    nrpta = rpta.substr(0,1);                    
                    if(nrpta === "1"){
                        rpta = fu_verificarEmpVoBo();//add vobo
                       
                        nrpta = rpta.substr(0, 1);
                        
                        if(nrpta === "1"){                
                            //verificar si necesita grabar el documento.
                            rpta = fu_verificarChangeDocumentoEmiPersonal();
                            nrpta = rpta.substr(0,1);
                            if(nrpta === "1"){			
                                fn_goGrabarDocumentoEmiPersonal();//grabar Documento
                                
                            
                            }else{
                                alert_Info("Emisión :",rpta);
                            }   
                        
                        }else{
                            alert_Info("Emisión :", rpta);
                        }                        
                        
                        
                    }else{
                        alert_Info("Emisión :",rpta);
                    }
                }else{
                    alert_Info("Emisión :",rpta);
                }
	}
             
            
              
	return false;
        
}

function fu_verificarCamposDocEmiPersonal(sOrigenBtn,pEstadoDoc){
    var noForm='#documentoPersonalEmiBean';
    var pnuDiaAte = jQuery(noForm).find('#nuDiaAte').val();
    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
    jQuery(noForm).find('#deDocSig').val(allTrim(fu_getValorUpperCase(jQuery(noForm).find('#deDocSig').val())));
    var pdeDocSig = jQuery(noForm).find('#deDocSig').val();
    var pfeEmiCorta = jQuery(noForm).find('#feEmiCorta').val();
    var pfeHoraActual = jQuery("#txtfechaHoraActual").val();
    var pfeActual = pfeHoraActual.substr(0,10);
    var pcoTipDocAdm = jQuery(noForm).find('#coTipDocAdm').val();
    var pcoEmpEmi = jQuery(noForm).find('#coEmpEmi').val();  
    jQuery(noForm).find('#deAsu').val(allTrim(jQuery(noForm).find('#deAsu').val()));
    var pdeAsu = jQuery(noForm).find('#deAsu').val();
    var maxLengthDeAsu = jQuery(noForm).find('#deAsu').attr('maxlength');
    var pesDocEmi = jQuery(noForm).find('#esDocEmi').val();
    jQuery(noForm).find('#nuDocEmi').val(replicate(allTrim(jQuery(noForm).find('#nuDocEmi').val()),6));
    var pnuDocEmi=jQuery(noForm).find('#nuDocEmi').val();
    
    var valRetorno="1";
    var vValidaNumero="";

        if(!(pcoEmpEmi !== null && pcoEmpEmi !== "")){
                valRetorno="0";
                bootbox.alert("<h5>Indicar Quien Firmará el Documento.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#deEmpEmi").focus();		
                });                    
        }
            
        if (valRetorno === "1") {
            if(!(pcoTipDocAdm !== null && pcoTipDocAdm !== "-1")){
                    valRetorno="0";
                    bootbox.alert("<h5>Seleccione tipo de Documento.</h5>", function() {
                        bootbox.hideAll();
                        jQuery(noForm).find('#coTipDocAdm').focus();			
                    });
            }        
        }
        
        if (valRetorno === "1") {
            if(!(pdeAsu !== null && pdeAsu !== "")){
                    valRetorno="0";
                    bootbox.alert("<h5>El asunto no debe ser vacio.</h5>", function() {
                        bootbox.hideAll();
                        jQuery(noForm).find("#deAsu").focus();		
                    });                    
            }else{
                if(!!maxLengthDeAsu){
                    var nrolinesDeAsu = (pdeAsu.match(/\n/g) || []).length;
                    if(pdeAsu.length+nrolinesDeAsu > maxLengthDeAsu){
                        valRetorno = "0";
                        bootbox.alert("<h5>El Asunto Excede el límite de "+maxLengthDeAsu+" caracteres.</h5>", function() {
                            bootbox.hideAll();
                            jQuery(noForm).find("#deAsu").focus();
                        });
                    }
                }                
            }
        }
        
        if (valRetorno === "1") {
            if (pnuDiaAte !== null && pnuDiaAte !== "") {
                vValidaNumero=fu_validaNumero(pnuDiaAte);
                if (vValidaNumero!=="OK") {
                    valRetorno="0";
                    bootbox.alert("<h5>Días de Atención debe ser solo números.</h5>", function() {
                        bootbox.hideAll();
                        jQuery(noForm).find("#nuDiaAte").focus();        
                    });                      
                }
            }else{
                valRetorno="0"; 
                bootbox.alert("<h5>Especifique días de Atención.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#nuDiaAte").focus();        
                });                  
            }
        }
    if (valRetorno === "1") {    
        if (pnuAnn !== null && pnuAnn !== "") {
            vValidaNumero=fu_validaNumero(pnuAnn);
            if (vValidaNumero!=="OK") {
                valRetorno="0";
               bootbox.alert("Año de Documento solo números.");
            }
        }else{
            valRetorno="0";        
           bootbox.alert("Especifique Año de Documento.");
        }
    }
    
    if(valRetorno==="1"){
        if(!!pnuAnn&&!!pnuEmi){
            if(!(!!pnuDocEmi)){
                valRetorno="0";
                bootbox.alert("<h5>Especifique Número de Documento.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#nuDocEmi").focus();
                });                
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
    
    if (valRetorno === "1") {
        if(pdeDocSig !== null && pdeDocSig !== ""){
            var rptValida = validaCaracteres(pdeDocSig,"1");
            if(rptValida !== "OK"){
                valRetorno="0";            
                bootbox.alert("<h5>"+rptValida+"</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#deDocSig").focus();        
                });                  
            }        
        }else{
           valRetorno="0"; 
           bootbox.alert("<h5>Especifique siglas del Documento.</h5>", function() {
               bootbox.hideAll();
               jQuery(noForm).find("#deDocSig").focus();        
           });             
        }
    }
    
    if (valRetorno==="1") {
        //VALIDA FECHAS
        if (pfeEmiCorta===""){
            valRetorno="0";
           bootbox.alert('Especifique Fecha de Documento.');
        } else {
            valRetorno=fu_validaFechaConsulta(pfeEmiCorta,pfeActual);
            if (valRetorno!=="1") {
                valRetorno="0";
               bootbox.alert("Error Fecha De Documento"+ valRetorno);
            }
        }
    }
    
    if (valRetorno === "1") {
        if(pesDocEmi!=="5"&&pesDocEmi!=="7"&&pesDocEmi!=="0"){
           valRetorno="0"; 
        }else{
            if(sOrigenBtn==="1"){
                if(pesDocEmi==="0"||pesDocEmi==="5"){
                    if(pEstadoDoc!=="5"&&pEstadoDoc!=="0"){
                        valRetorno="0";
                    }
                }
            }else{
                if(pesDocEmi==="0"){
                    valRetorno="0";
                }
            }
        }
    }
    return valRetorno;    
}

function fn_goGrabarDocumentoEmiPersonal(){
    //$('#documentoPersonalEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmiPersonal();
//    var pcrearExpediente="0";
//    var pesnuevoDocEmiAdm=jQuery("#txtEsNuevoDocAdm").val();
//    if(pesnuevoDocEmiAdm==="1"){
//        if (confirm('¿ Desea Crear Expediente ?')){
//            pcrearExpediente="1";
//        }        
//    }

//YUAL
   var pnuAnnExp= $("#nuAnnExp").val();
   var pnuSecExp= $("#nuSecExp").val();
   var pnuExpediente= $("#nuExpediente").val();

    jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val("");
    ajaxCallSendJson("/srDocumentoEmisionPersonal.do?accion=goGrabaDocumentoEmi&pnuAnnExp="+pnuAnnExp+"&pnuSecExp="+pnuSecExp+"&pnuExpediente="+pnuExpediente/*&pcrearExpediente="+pcrearExpediente*/,cadenaJson,function(data){
               fn_rptaGrabaDocPersonalEmi(data/*,pcrearExpediente*/); 
            },
    'json', false, false, "POST");   
 }
 
 
 
 function fn_rptaGrabaDocPersonalEmi(data/*,sCrearExpediente*/){
     //console.log('AKIIIIIIIIII--->>>0000000000');
    if(data !== null){
        if(data.coRespuesta==="1"){
//            console.log(jQuery('#txtEsNuevoDocAdm').val());
//            console.log('txtEsNuevoDocAdm');
       
             if(jQuery('#txtEsNuevoDocAdm').val()==="1"){
       
                jQuery('#txtEsNuevoDocAdm').val("0");
                jQuery('#documentoPersonalEmiBean').find("#nuEmi").val(data.nuEmi);
                jQuery('#documentoPersonalEmiBean').find("#nuCorEmi").val(data.nuCorEmi);                                
                jQuery('#documentoPersonalEmiBean').find("#nuDocEmi").val(data.nuDocEmi);                                
                jQuery('#documentoPersonalEmiBean').find("#nuDoc").val(data.nuDoc);
                
     
                //console.log(data.nuEmi);
//                jQuery("#nuEmi").val(data.nuEmi);
//                jQuery("#nuCorEmi").val(data.nuCorEmi);
//                if(sCrearExpediente === "1"){
//                    jQuery("#nuAnnExp").val(data.nuAnnExp);
//                    jQuery("#nuSecExp").val(data.nuSecExp);
//                    jQuery("#nuExpediente").val(data.nuExpediente); 
//                    jQuery("#feExpCorta").val(data.feExp);                     
//                }
                            //resetear variables del documento
            }
            /*else {
                jQuery('#documentoPersonalEmiBean').find("#nuEmi").val(data.nuEmi);
                jQuery('#documentoPersonalEmiBean').find("#nuCorEmi").val(data.nuCorEmi);                                
                jQuery('#documentoPersonalEmiBean').find("#nuDocEmi").val(data.nuDocEmi);                                
                jQuery('#documentoPersonalEmiBean').find("#nuDoc").val(data.nuDoc);
            }*/
            fn_seteaCamposDocPersonalEmi();
            fn_setTblPersonalVoBo();
            fu_cargaEdicionDocAdm("02",jQuery('#esDocEmi').val());            
            
             
                
                  
            //alert("Datos Guardados.");
            alert_Sucess("Éxito!","Documento grabado correctamente.");
        }else{
            alert_Info("Emisión :",data.deRespuesta);
        }        
    }
}

function fn_seteaCamposDocPersonalEmi(){
    jQuery('#envDocumentoEmiBean').val("0");
    jQuery('#envExpedienteEmiBean').val("0");
    jQuery('#envRemitenteEmiBean').val("0");
     var p = new Array();    
     p[0] = "accion=goUpdTlbsDestinatario";	    
     p[1] = "pnuAnn=" + $("#nuAnn").val();        
     p[2] = "pnuEmi=" + $("#nuEmi").val();        
     //p[3] = "pcoDependencia=" + $("#coDepEmi").val();        
    ajaxCall("/srDocumentoAdmEmision.do",p.join("&"),function(data){
           fn_updateTblsEmisioDocPersonal(data);
        },
    'text', false, true, "POST");          
}

function fn_updateTblsEmisioDocPersonal(XML_AJAX){
   if(XML_AJAX !== null){
       refreshScript("divActualizaTablasDestintario", XML_AJAX);
       fn_changeTipoDestinatarioDocuEmi('01','');
        var p = new Array();    
        p[0] = "accion=goUpdTlbReferencia";	    
        p[1] = "pnuAnn=" + $("#nuAnn").val();        
        p[2] = "pnuEmi=" + $("#nuEmi").val();        
        p[3] = "pcoDependencia=" + $("#coDepEmi").val();        
       ajaxCall("/srDocumentoAdmEmision.do",p.join("&"),function(data){
              if(data !== null){
                  refreshScript("divtablaRefEmiDocAdm", data);
                  $("#txtIndexFilaRefEmiDoc").val("-1");
                  fu_cargaEdicionDocAdm("02",jQuery('#esDocEmi').val()); 
              }else{
                  backLsDocEmiPersonal('0');
              }
           },
       'text', false, true, "POST");            
   }else{
       backLsDocEmiPersonal('0');
   } 
}

function fu_changeEstadoDocPersonalEmi(pEstadoDoc) {
    var validaFiltro = fu_verificarCamposDocEmiPersonal('1', pEstadoDoc);
    if (validaFiltro === "1") {
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeDocumentoEmiPersonal();
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            alert_Warning("Emisión :", "Necesita grabar los cambios");
        } else {
            if (pEstadoDoc === "5") {//Proyecto
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
                            fn_changeToProyectoDocEmiPersonal();//cambiar a proyecto
                        }
                    }
                });
            } /*else if (pEstadoDoc === "7" && jQuery("#txtEsNuevoDocAdm").val() === "0") {//Despacho
                rpta = fu_verificarDestinatario("1");
                nrpta = rpta.substr(0, 1);
                if (nrpta === "1") {
                    fn_changeToDespachoDocEmiAdm();//cambiar a Despacho
                } else {
                    alert_Info("Emisión :", rpta);
                }
            }*/ else if (pEstadoDoc === "0") {//Emitido
                 rpta = fn_validarEstadoDocEmiPersonal(pEstadoDoc);
                if (rpta === "1") {
                    rpta = fu_verificarDestinatario("1");
                    nrpta = rpta.substr(0, 1);
                    if (nrpta === "1") {
                        rpta = fu_verificarReferencia();
                        nrpta = rpta.substr(0, 1);                        
                        if(nrpta === "1"){
                            fn_changeToEmitidoDocEmiPersonal();//cambiar a Emitido
                        }else{
                            alert_Info("Emisión :", rpta);
                        }
                    } else {
                        alert_Info("Emisión :", rpta);
                    }                    
                }
            }
        }

    }
    return false;
}

function fn_changeToProyectoDocEmiPersonal(){
	ajaxCall("/srDocumentoEmisionPersonal.do?accion=goChangeToProyecto",$('#documentoPersonalEmiBean').serialize(),function(data){
		   fn_rptaChangeToProyectoDocEmiPersonal(data); 
           fn_cargaToolBarEmiPersonal();
		},
	'json', false, false, "POST");   
}
function fn_rptaChangeToProyectoDocEmiPersonal(data){
    if(data !== null){
        if(data.coRespuesta==="1"){
            jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val("5");
            fn_seteaCamposDocPersonalEmi();//resetear variables del documento
            fn_updateTblVoBoDocAdm();
            fu_cargaEdicionDocAdm("02","5");            
            alert_Sucess("Éxito!","Transacción completada.");
            jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val("");  
        }else{
           bootbox.alert(data.deRespuesta);
        }        
    }
}

function fn_validarEstadoDocEmiPersonal(pEstadoDoc){
    var vResult = "0";
    if(jQuery('#txtEsNuevoDocAdm').val()==="0"){
        if(pEstadoDoc==="0"){
            var pesDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
            if(pesDocEmi === "7"){        
               vResult="1"; 
            }
            var pnuDocEmi = allTrim(jQuery('#documentoPersonalEmiBean').find('#nuDocEmi').val());
            if(typeof(pnuDocEmi) !== "undefined" && pnuDocEmi!==""){
               vResult="1";  
            }else{
                vResult = "0";
                alert_Info('',"Documento no esta Numerado.");
                jQuery('#nuDocEmi').focus();
            }          
        }
    }else{
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    }
    return vResult;
}

function fn_changeToEmitidoDocEmiPersonal(){
    
    var vnuSecFirma = jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val();
    var vnoDoc="";
    
    if( !!vnuSecFirma && typeof(vnuSecFirma) !== "undefined" && vnuSecFirma!==""){
        var rutaDocFirma = jQuery("#rutaDocFirma").val();
        var vinFirma = jQuery('#inFirmaEmi').val();
        if (vnuSecFirma===null || typeof(vnuSecFirma) === "undefined" || vnuSecFirma === ""){
            vinFirma = "F";
        }        
        
        var valDoc = "NO";
        if (!!rutaDocFirma) {
            var vnoPrefijo = jQuery("#noPrefijo").val();
            var vnoDoc ="";
            if (vnoPrefijo==="[NF]"){
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 4) + vnoPrefijo+".pdf";
            }else{
                vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) +"N"+ vnoPrefijo+".pdf";
            }
            var param = {rutaDoc: vnoDoc};
            //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
            runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                valDoc = data;
                if (valDoc==="SI") {
                    fn_grabarEmisionDocumentoPersonal(valDoc, vnoDoc);
                    return;
                }
                if (vinFirma === "N" && valDoc !== "SI") {
                    vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 5) + "N.pdf";
                    //valDoc = fn_verificaSiExisteDoc(vnoDoc);
                    var param = {rutaDoc: vnoDoc};
                    //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
                    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                        valDoc = data;
                        fn_grabarEmisionDocumentoPersonal(valDoc, vnoDoc);
                        return;
                    });
                }else{
                    alert_Danger("Firma!", "El Documento no esta Firmado.");
                }
                
            });

        }

    }else{
        alert_Danger("Firma!","Se necesita Firmar Documento.");
    }
    
}
function fn_grabarEmisionDocumentoPersonal(valDoc,vnoDoc){
    
    if (valDoc === "SI") {
        //var resulCarga = fn_cargaDocPersonalFirmaApplet(vnoDoc);
        fn_cargaDocPersonalFirmaApplet(vnoDoc, function(data) {
            var resulCarga = data;
            //if (resulCarga !== "ERROR" && resulCarga !== "NO") {
            if(resulCarga.error==="0"){
                //jQuery('#documentoPersonalEmiBean').find('#nuSecuenciaFirma').val(resulCarga);
                jQuery('#documentoPersonalEmiBean').find('#nuSecuenciaFirma').val(resulCarga.message);
                ajaxCall("/srDocumentoEmisionPersonal.do?accion=goChangeToEmitido", $('#documentoPersonalEmiBean').serialize(), function(data) {
                    fn_rptaChangeToEmitidoDocEmiPersonal(data);
                },
                        'json', false, false, "POST");
            }
        });
    } else {
        alert_Danger("Firma!", "El Documento no esta Firmado.");
    }
}

function fn_rptaChangeToEmitidoDocEmiPersonal(data){
    if(data !== null){
        if(data.coRespuesta==="1"){
            jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val("0");
            fn_seteaCamposDocPersonalEmi();//resetear variables del documento
            fu_cargaEdicionDocAdm("02","0");            
            alert_Sucess("Éxito!","Transacción completada.");
            jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val("");  
            fn_cargaToolBarEmiPersonal();
        }else{
            alert_Danger("Personal!",data.deRespuesta);
        }        
    }
}

function fn_verAnexoEmiPersonalEdit() {
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
       bootbox.alert("Seleccione una fila de la lista");
    }
}

function fn_cargarDocumentosAnexosPersonal() {
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();

    var rpta = fu_verificarChangeDocumentoEmiPersonal();
    var nrpta = rpta.substr(0, 1);
    if (nrpta === "1") {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    } else {
        if (pnuAnn && pnuEmi) {
            fn_cargarDocumentosAnexosObj(pnuAnn, pnuEmi);
        } else {
            //alert("Seleccione una fila de la lista");
            alert_Info("Emisión :", "Faltan Datos");
        }
    }
}


function fn_verSeguimientoEmiPersonalEdit() {
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var pnuDes = "N";
    if (pnuAnn) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function fn_firmarDocumentoEmiPersonal(){

    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val("");
    var ptiOpe = "5";

    var rpta = fu_verificarChangeDocumentoEmiPersonal();
    var nrpta = rpta.substr(0,1);
    if(nrpta === "1"){
         alert_Warning("Emisión :","Necesita grabar los cambios");
    }else{
        if (pnuAnn) {
            
            var param={pnuAnn:pnuAnn,pnuEmi:pnuEmi};
            ajaxCall("/srDocumentoAdmEmision.do?accion=goVerificarVistoBuenoPendiente", param, function(data) {
                if(data.coRespuesta==="1"){ 
                    var p = new Array();
                    p[0] = "accion=goRutaFirmaDoc";	
                    p[1] = "nuAnn="+pnuAnn;	
                    p[2] = "nuEmi="+pnuEmi;	
                    p[3] = "tiOpe="+ptiOpe;	
                        ajaxCall("/srDocObjeto.do",p.join("&"),function(data){
                            var result;
                            eval("var docs="+data);
                            if(typeof(docs)!=="undefined" && typeof(docs.nuAnn)!=='undefined' && docs.nuAnn!==""){
                                if(docs.retval ==="OK"){
                                    jQuery('#documentoPersonalEmiBean').find("#txtnuDocEmiAn").val(docs.numeroDoc);
                                    jQuery('#documentoPersonalEmiBean').find("#nuDocEmi").val(docs.numeroDoc);
                                    jQuery('#documentoPersonalEmiBean').find("#feEmiCorta").val(docs.fechaFirma);
                                    jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val(docs.nuSecFirma);
                                    jQuery("#noPrefijo").val(docs.noPrefijo);
                                    jQuery("#rutaDocFirma").val(docs.noDoc);
                                    jQuery("#inFirmaEmi").val("N");
                                    showBtnEnviarDocPersonal();
                                    //result = fn_firmarDocApplet(docs.noUrl,docs.noDoc);
                                    var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc,tipoFirma:"1"};
                                    //runApplet(appletsTramiteDoc.ejecutaFirma,param,function(data){
                                    runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma, param, function(data){
                                        result=data;
                                    });
                                }else{
                                       var p = new Array();
                                        p[0] = "accion=goRutaGeneraDocx";
                                        p[1] = "nuAnn=" + pnuAnn;
                                        p[2] = "nuEmi=" + pnuEmi;
                                        p[3] = "tiOpe=1";
                                        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
                                         //var result;
                                         eval("var docs=" + data);
                                        if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined' && docs.retval !== "") {
                                            if (docs.retval === "OK") {
                                            var retval1 = "";
                                            var param = {rutaDoc:docs.noDoc, verBloqueo: false};
                                            //runApplet(appletsTramiteDoc.verificaSiExisteDoc, param, function(data) {
                                            runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function(data){
                                            retval1 = data;
                                                if (retval1 != "SI") {
                                                    docs.retval1 =  'No se realizó la carga del documento';
                                                    alert_Danger("!Repositorio : ",docs.retval1);
                                                }
                                            });
                                        }
                                    }
                                }, 'text',true, false, "POST");

                                    alert_Danger("!Repositorio : ",docs.retval);
                                }
                            }

                        }, 'text', false, false, "POST");  
                    }
                else
                    {
                        alert_Danger("!Emisión : ", data.deRespuesta);
                    }
            },'json', false, false, "POST");
        } else {
           bootbox.alert("Documento no encontrado");
        }
   }     
}

function fn_anularDocPersonalEmi(){
    
    var rpta = fu_verificarChangeDocumentoEmiPersonal();
    var nrpta = rpta.substr(0,1);
    if(nrpta === "1"){
         alert_Warning("Emisión :","Necesita grabar los cambios");
    }else{
        var pesDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
        if(pesDocEmi !== null && (pesDocEmi === "0" || pesDocEmi === "5" || pesDocEmi === "7")){
            bootbox.dialog({
                message: " <h5>¿ Esta Seguro de Anular Documento ?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
                        className: "btn-primary",
                        callback: function() {
                            if(!!jQuery('#documentoPersonalEmiBean').find('#nuAnn').val() && !!jQuery('#documentoPersonalEmiBean').find('#nuEmi').val()){
                                //$('#documentoPersonalEmiBean').find('select').removeProp('disabled');
                                ajaxCall("/srDocumentoEmisionPersonal.do?accion=goAnularDocumento",$('#documentoPersonalEmiBean').serialize(),function(data){
                                    fn_rptAnularDocPersonalEmi(data);
                                }, 'json', false, false, "POST");                        
                            }else{
                               backLsDocEmiPersonal('0'); 
                            }
                        }                        
                    },
                    NO: {
                        label: "NO",
                        className: "btn-default"
                    }
                }
            });                
        }else{
            //alert("Documento ya fue leido por los Destinatarios!!");
            alert_Warning("Emisión :","Documento ya fue leido por los Destinatarios.");
        }
    }
    return false;
}

function fn_rptAnularDocPersonalEmi(data){
    if(data!==null){
        if(data.coRespuesta === "1"){
            backLsDocEmiPersonal('0');
        }else{
           bootbox.alert(data.deRespuesta);
        }
    }
}

function fn_abrirDocumentoEmiPersonal(){

    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var ptiOpe = "5";
    
    if (!!pnuAnn && !!pnuEmi) {
        var p = new Array();
        p[0] = "accion=goDocRutaAbrirEmi";	
        p[1] = "nuAnn="+pnuAnn;	
        p[2] = "nuEmi="+pnuEmi;	
        p[3] = "tiOpe="+ptiOpe;	
        ajaxCall("/srDocObjeto.do",p.join("&"),function(data){
            var result;
            eval("var docs="+data);
            if(typeof(docs)!="undefined" && typeof(docs.nuAnn)!='undefined' && docs.nuAnn!=""){
                if(docs.retval =="OK"){
                    //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                    var param={urlDoc:docs.noUrl,rutaDoc:docs.noDoc};
                    //runApplet(appletsTramiteDoc.abrirDocumento,param,function(data){
                    runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento, param, function(data){
                        result=data;
                    });
                }else{
                    //Error en Documento
                    alert_Danger("!Repositorio : ",docs.retval);
                }
            }

        }, 'text', false, false, "POST");       
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");        
    }
}

function fn_abrirDocumentoPersonalFromPC(){

    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var ptiOpe = "3";
    
    if (!!pnuAnn&&!!pnuEmi) {
        fn_verDocumentosFromPC(pnuAnn, pnuEmi, ptiOpe);
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios.");        
    }
    
}

function fn_cargaDocPersonalFirmaApplet(pnoDoc,callback){
    /*var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var pnuSecFirmaEmi = jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val();
    var ptiOpe = "6";
    var resulCarga="ERROR";
    var docs;
    if(!!pnuAnn && !!pnuEmi && !!pnuSecFirmaEmi ){
        // Obteniendo la ruta de Carga
             var p = new Array();
             p[0] = "accion=goRutaCargaFirmaDoc";	
             p[1] = "nuAnn="+pnuAnn;
             p[2] = "nuEmi="+pnuEmi;
             p[3] = "tiOpe="+ptiOpe;
             p[4] = "nuSecFirma="+pnuSecFirmaEmi;
             ajaxCall("/srDocObjeto.do",p.join("&"),function(data){
                 eval("docs="+data);
                 if(typeof(docs)!=="undefined" && typeof(docs.retval)!=='undefined'){
                     if(docs.retval==="OK"){
                        var retval = "";
                        var appletObj = jQuery('#firmarDocumento');
                        
                        
                        try{
                           retval=appletObj[0].cargarDocumento(docs.noUrl,pnoDoc);
                        }catch(ex){
                            alert_Danger("Firma!: ","Fallo en subir documento al servidor");
                            retval = "ERROR";
                        }
                        resulCarga = retval;
                    }else{
                        alert_Danger("Firma!: ",docs.retval);
                        resulCarga = "ERROR";
                    }
                }
             },'text', true, false, "POST");   
        
    }   
    return(resulCarga);*/
    //////////////////////////////////////////
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var pnuSecFirmaEmi = jQuery('#documentoPersonalEmiBean').find("#nuSecuenciaFirma").val();
    var ptiOpe = "6";
    var resulCarga="ERROR";
    var docs;
    if(!!pnuAnn && !!pnuEmi && !!pnuSecFirmaEmi ){
        // Obteniendo la ruta de Carga
             var p = new Array();
             p[0] = "accion=goRutaCargaFirmaDoc";	
             p[1] = "nuAnn="+pnuAnn;
             p[2] = "nuEmi="+pnuEmi;
             p[3] = "tiOpe="+ptiOpe;
             p[4] = "nuSecFirma="+pnuSecFirmaEmi;
             ajaxCall("/srDocObjeto.do",p.join("&"),function(data){
                 eval("docs="+data);
                 if(typeof(docs)!=="undefined" && typeof(docs.retval)!=='undefined'){
                     if(docs.retval==="OK"){
                        var retval = "";
                        //var appletObj = jQuery('#firmarDocumento');
                        try{
                           //retval=appletObj[0].cargarDocumento(docs.noUrl,pnoDoc);
                           var param = {urlDoc: docs.noUrl, rutaDoc: pnoDoc};
                            //runApplet(appletsTramiteDoc.cargarDocumento, param, function(data) {
                            runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function(data){
                                retval = data;
                                callback(retval);
                                return;
                            });
                        }catch(ex){
                            alert_Danger("Firma!: ","Fallo en subir documento al servidor");
                            retval = "ERROR";
                            resulCarga = retval;
                            callback(resulCarga);
                            return;
                        }
                        resulCarga = retval;
                    }else{
                        alert_Danger("Firma!: ",docs.retval);
                        resulCarga = "ERROR";
                        callback(resulCarga);
                        return;
                    }
                }
             },'text', true, false, "POST");   
        
    }   
    //return(resulCarga);
}

function fn_cargaDocEmiPersonal() {
   
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    //var pesDocEmi = jQuery('#documentoPersonalEmiBean').find('#esDocEmi').val();
    //var ptiOpe = "3";
    //if (pesDocEmi === "7") {
      //  ptiOpe = "4";
    //}
    var ptiOpe = "3";
    var resulCarga = "ERROR";
    var docs;
    if (!!pnuAnn && !!pnuEmi) {
        if(fu_existeVistoBuenoDocAdm()){ 
            alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");            
        }else{
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            eval("docs=" + data);
            if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                //resulCarga = fn_cargaDocApplet(docs.noUrl, docs.noDoc);
                //fn_cargaDocApplet(docs.noUrl, docs.noDoc, function(data) {
                fn_cargaDocDesktop(docs.noUrl, docs.noDoc, function(data) {
                    resulCarga = data;
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
                                    cargarPDF(pnuAnn,pnuEmi);
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
      }
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    }
    
    
}

function cargarPDF(pnuAnn,pnuEmi)
{
    
   var ptiOpe = "4";
    var resulCarga = "ERROR";
    var resulCarga = "ERROR";
    var docs;
    
    if (!!pnuAnn && !!pnuEmi) {
        if(fu_existeVistoBuenoDocAdm()){ 
            alert_Warning("Emisión : ", "Doc. con Visto Bueno u Observado.");            
        }else{
        // Obteniendo la ruta de Carga
        var p = new Array();
        p[0] = "accion=goRutaCargaDoc";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "tiOpe=" + ptiOpe;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            eval("docs=" + data);
            if (typeof (docs) !== "undefined" && typeof (docs.nuAnn) !== 'undefined' && docs.nuAnn !== "") {
                //resulCarga = fn_cargaDocApplet(docs.noUrl, docs.noDoc);
                //fn_cargaDocApplet(docs.noUrl, docs.noDoc, function(data) {
                fn_cargaDocDesktop(docs.noUrl, docs.noDoc, function(data) {
                    resulCarga = data;
                    if (resulCarga !== "ERROR" && resulCarga !== "NO") {
                        var p = new Array();
                        p[0] = "accion=goCargarDocEmi";
                        p[1] = "nuAnn=" + pnuAnn;
                        p[2] = "nuEmi=" + pnuEmi;
                        p[3] = "tiOpe=" + ptiOpe;
                        p[4] = "nuSec=" + resulCarga;
                        p[5] = "noDoc=" + docs.noDoc;
                        ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
                           // var rpta;
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
    } else {
        alert_Warning("Emisión :", "Necesita grabar los cambios");
    }
    
}
function upperCaseBuscarDocumentoPersonalBean(){
    jQuery('#buscarDocumentoPersonalEmiBean').find('#sNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoPersonalEmiBean').find('#sNumDoc').val()));
    jQuery('#buscarDocumentoPersonalEmiBean').find('#sDeAsuM').val(fu_getValorUpperCase(jQuery('#buscarDocumentoPersonalEmiBean').find('#sDeAsuM').val()));
}

function fn_verDocumentoEmiPersonalEdit(){
    var pnuAnn = jQuery('#documentoPersonalEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoPersonalEmiBean').find('#nuEmi').val();
    var ptiOpe = "0";
    
    if (pnuAnn) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        //alert("Error en Datos, Ingrese al documento nuevamente.");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
    
}

function fn_changeTipoDocEmiPers(cmbTipoDoc){
    var coTiDoc=jQuery(cmbTipoDoc).val();
    var coTiDocLast=jQuery(cmbTipoDoc).find('option:last').val();
    var coTiDocFirst=jQuery(cmbTipoDoc).find('option:first').val();
    if(coTiDocLast==="-1"){
        jQuery(cmbTipoDoc).find('option:last').remove();
    }else if(coTiDocFirst==="-1"){
        jQuery(cmbTipoDoc).find('option:first').remove();
    }
    fu_changeDocumentoEmiBean();
    
    // Validacion de numero de documento
    fn_valTipDocEmiPers(coTiDoc);
}

function fn_valTipDocEmiPers(pcoTipDocAdm) {
    var noForm='#documentoPersonalEmiBean';
    var pnuAnn = jQuery(noForm).find('#nuAnn').val();
    var pnuEmi = jQuery(noForm).find('#nuEmi').val();
    var ptiEmi = jQuery(noForm).find('#tiEmi').val();
    var pcoDepEmi = jQuery(noForm).find('#coDepEmi').val();
    var pcoEmpEmi = jQuery(noForm).find('#coEmpEmi').val();
    var pnuDocEmiAnn = jQuery(noForm).find('#txtnuDocEmiAn').val();
    var pnuDocEmi = allTrim(jQuery(noForm).find('#nuDocEmi').val());
    if(!!pnuDocEmi&&!!pnuAnn&&!!pnuEmi){
        var vValidaNumero = fu_validaNumero(pnuDocEmi);
        if (vValidaNumero==="OK") {
            pnuDocEmi = replicate(pnuDocEmi, 6);
        //    if(pnuDocEmiAnn !== pnuDocEmi || (typeof(pnuDocEmiAnn) !== "undefined" && pnuDocEmiAnn === "")){
                fn_jsonVerificarNumeracionDocPersonalEmi(pnuDocEmiAnn,pnuAnn,pnuEmi,ptiEmi,pcoTipDocAdm,pcoDepEmi,pnuDocEmi,pcoEmpEmi);
        //    }else{
        //       jQuery('#nuDocEmi').val(pnuDocEmiAnn);  
        //    }                
        }           
    }
}

function showBtnEnviarDocPersonal(){
    var btnEmitirDoc = jQuery('#divEmitirDocPersonal').find('button').get(0);
    btnEmitirDoc.removeAttribute('onclick');    
    btnEmitirDoc.setAttribute('onclick','fu_changeEstadoDocPersonalEmi(\'0\');');       
    jQuery('#divEmitirDocPersonal').show();
}



function fn_validarEnvioGrabaDocEmiPersonal(objTrxDocumentoEmiBean) {
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
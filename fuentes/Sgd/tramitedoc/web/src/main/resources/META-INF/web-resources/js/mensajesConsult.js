function fn_iniMensajes(){
    jQuery('#buscarDocumentoCargaMsjBean').find('#esFiltroFecha').val("4");//ultimos 30 dias
    jQuery('#buscarDocumentoCargaMsjBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});    
   
    pnumFilaSelect=0;
    changeBusqMensajes("1");
    
}
function changeBusqMensajes(tipo) {
    
    jQuery('#buscarDocumentoCargaMsjBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormBusDocMensajes(tipo);
    
}

function submitAjaxFormBusDocMensajes(tipo) {
    
    var validaFiltro = fu_validaFormBusqMensajes(tipo);
    
    if (validaFiltro === "1") {
        ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
            refreshScript("divTablaMensajes", data);
        }, 'text', false, false, "POST");
    }
    return false;
}


function fu_validaFormBusqMensajes(tipo) {
    var valRetorno = "1";
    $('#buscarDocumentoCargaMsjBean').find('#feEmiIni').val($('#buscarDocumentoCargaMsjBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    $('#buscarDocumentoCargaMsjBean').find('#feEmiFin').val($('#buscarDocumentoCargaMsjBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    
    
    
    var pEsIncluyeFiltro = $('#buscarDocumentoCargaMsjBean').find('#esIncluyeFiltro').is(':checked');
    var vFechaActual = $('#txtFechaActual').val();
    if(tipo==="1"){
         
        valRetorno = fu_validaFechasFormBusqMensaje(vFechaActual);  
    }else if(tipo==="0"){
        //valRetorno = fu_validaBusDocExtRecep();
        if(valRetorno==="1"){
            if(pEsIncluyeFiltro===false){
               valRetorno = fu_validaFechasFormBusqMensaje(vFechaActual); 
            }else{
               valRetorno = setAnnioNoIncludeFiltroDocExtRecep();
            }
        }
    }    
    
    return valRetorno;
}


function fu_validaFechasFormBusqMensaje(vFechaActual){
    var valRetorno = "1";
    
    if (valRetorno==="1") {
        
        fu_obtenerEsFiltroFechaMensaje('buscarDocumentoCargaMsjBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoCargaMsjBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if(pEsFiltroFecha==="1" || pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
           if(pEsFiltroFecha==="2" || pEsFiltroFecha==="3"){
                var pAnnio = jQuery('#buscarDocumentoCargaMsjBean').find('#coAnnio').val();
                if(pAnnio!==null && pAnnio!=="null" && typeof(pAnnio)!=="undefined" && pAnnio!==""){
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }                
                }               
           }   
            
            var vFeInicio = jQuery('#buscarDocumentoCargaMsjBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoCargaMsjBean').find("#feEmiFin").val();
            if(valRetorno==="1"){
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                        jQuery('#buscarDocumentoCargaMsjBean').find('#coAnnio').val(pAnnioBusq);                          
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

function fu_obtenerEsFiltroFechaMensaje(nameForm){
    var opt = jQuery('#'+nameForm).find('#coAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    
    if(opt==="1"||opt==="2"||opt==="4"||opt==="8"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("1"); 
    }else if(opt==="5"||opt==="6"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("2"); 
    }else if(opt==="3"||opt==="7"){
       jQuery('#'+nameForm).find("#esFiltroFecha").val("3"); 
    }
}


function fn_cargarResponsable() 
{
    var p = new Array();
    p[0] = "accion=goListaResponsable";
    p[1] = "vCodResposable=" + $('#coTipoMsj').val();
    p[2] = "vCodAmbito=" + $('#coAmbitoMsj').val();
    ajaxCall("/srGestionMensajes.do", p.join("&"), function(data) {
    refreshScript("divResponsableMensajeria", data);
    }, 'text', false, false, "POST");
}

function fn_verMotivo() 
{
   /* var p = new Array();
    p[0] = "accion=goListaResponsable";
    p[1] = "vCodResposable=" + jQuery('#coTipoMsj').val();
    ajaxCall("/srGestionMensajes.do", p.join("&"), function(data) {
    refreshScript("divResponsableMensajeria", data);
    }, 'text', false, false, "POST");*/
    var estado=jQuery('#co_EstadoDoc').val();
    calPenDev();

    if(estado === "4")
    {   
         $('#divMotivo').show(); 
//          document.getElementById("LblFechaEnv").innerHTML = "Fecha Primera Visita:" ;
//          document.getElementById("LblFechaDev").innerHTML = "Fecha de Devolución:" ;
//          
   }else
   {
      $('#divMotivo').hide();        
//       document.getElementById("LblFechaEnv").innerHTML = "Fecha de Entrega:" ; 
//       document.getElementById("LblFechaDev").innerHTML = "Fecha de Devolución:" ; 
//       
  }
  
}



function fn_buildSendJsontoServerDocMensaj(noForm) {
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
    result += '"codigo":"' + $('#'+noForm).find("#codigo").val() + '"'; 
    return result + "}";
} 

function fu_cleanBusqMensajes(tipo) {
    var noForm='#buscarDocumentoCargaMsjBean';
    if (tipo==="0") {
        $(noForm).find("#busNuSerMsj").val("");
        $(noForm).find("#busAnSerMsj").val("");
        $(noForm).find("#busNuMsj").val("");

    } else if(tipo==="1"){
        $(noForm).find("#esIncluyeFiltro").prop('checked',false);
        $(noForm).find("#esIncluyeFiltro").attr('checked',false);
        $(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});    
        $(noForm).find("#esFiltroFecha").val("4");//ultimos 30 dias
        $(noForm).find("#coAnnio").val(jQuery("#txtAnnioActual").val());
        $(noForm).find("#coEstadoDoc").val("2");        
        $(noForm).find("#coTipoEnvMsj").val(".: TODOS :.");
        $(noForm).find("#coTipoMsj").val(".: TODOS :.");
        $(noForm).find("#coAmbitoMsj").val(".: TODOS :.");
        
        $(noForm).find("#coOficina").val("");
        
        $(noForm).find("#busNuSerMsj").val("");
        $(noForm).find("#busAnSerMsj").val("");
        $(noForm).find("#busNuMsj").val("");
        $(noForm).find("#busNuDoc").val("");
        $(noForm).find("#busDesti").val("");
    }
    
    
    changeBusqMensajes('0');
}

function fu_eventoTablaMensajes() {
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
        }
    
    
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
                if (index >= 3 && index <= 13) {
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

function fn_grabarDescargaMsj() {


    var myForm='descargaMensajeBean';
    var PrimeraVisita=$("#txt_fe_Acta_Vis1").val();
    var SegundaVisita=$("#txt_fe_Acta_Vis2").val();
    var valido=true;

    if($('#co_EstadoDoc').val()=="3" && $('#txtFechaEnt').val().trim()==""){valido=false; bootbox.alert("Debe Ingresar la fecha de la Entrega");}
    if($('#co_EstadoDoc').val()=="4" && $('#txtFechaDev').val().trim()==""){valido=false; bootbox.alert("Debe Ingresar la fecha de la Devolución");}
    if($('#co_EstadoDoc').val()=="2" && PrimeraVisita.trim()=="" && SegundaVisita.trim()==""){valido=false; bootbox.alert("Debe Ingresar la fecha de la Primera Visita");}
    if($('#co_EstadoDoc').val()=="2" &&SegundaVisita.trim()!="" && PrimeraVisita>=SegundaVisita){valido=false; bootbox.alert("La fecha de la segunda visita debe ser mayor a la fecha de la primera visita");}
    
    var horaEntrega="";
    var horaDevolucion="";
      if($('#txtFechaEnt').val().trim()!=""){horaEntrega=$('#'+myForm).find("#txtFechaEnt").val()+ ' ' + $('#'+myForm).find("#txtHoraEnt").val() + ':00'}
      if($('#txtFechaDev').val().trim()!=""){horaDevolucion=$('#'+myForm).find("#txtFechaDev").val()+ ' ' + $('#'+myForm).find("#txtHoraDev").val() + ':00'}
      
    if(valido){     
    if(fn_valFormDescargaMsj(myForm)){
        
        var arrayDeCadenas = $("#coMotivo").val().split("|");
  
        var jsonBody =
                {
                    "nu_msj": $('#'+myForm).find("#txtNu_Msj").val(), "nu_des": $('#'+myForm).find("#txtNu_Des").val(),
                    "co_EstadoDoc": $('#'+myForm).find("#co_EstadoDoc").val(),"nro_guia_devolucion": $('#'+myForm).find("#txtNroGuiaDevolucion").val(), "nu_ann": $('#'+myForm).find("#txtNu_Ann").val(),
                    "nu_emi": $('#'+myForm).find("#txtNu_Emi").val(), "ob_msj": allTrim($('#'+myForm).find("#txtObservaciones").val()),
                    "mo_msj_dev": arrayDeCadenas[0], "es_pen_msj": allTrim($('#'+myForm).find("#txtes_pen_msj").val()),"es_pen_dev": allTrim($('#'+myForm).find("#txtes_pen_dev").val()),
                    "fe_ent_msj": $('#'+myForm).find("#txtFechaEnt").val(),"ho_ent_msj":horaEntrega,
                    "fe_dev_msj": $('#'+myForm).find("#txtFechaDev").val(),"ho_dev_msj":horaDevolucion,
                    "fe_pla_dev":$('#'+myForm).find("#txtFe_Pla_Dev").val(),"pe_env_msj":$('#'+myForm).find("#txtPeEnvMsj").val(),
                    "nu_Acta_Vis1":$('#txt_pnu_Acta_Vis1').val(),"fe_Acta_Vis1":$('#txt_fe_Acta_Vis1').val(),"es_Acta1_msj":$('#es_Acta1_msj').val(),
                    "nu_Acta_Vis2":$('#txt_pnu_Acta_Vis2').val(),"fe_Acta_Vis2":$('#txt_fe_Acta_Vis2').val(),"es_Acta2_msj":$('#es_Acta2_msj').val()
                    
                };
               
        var jsonString = JSON.stringify(jsonBody);
        console.log('jsonString:'+jsonString);
        var url = "/srGestionMensajes.do?accion=goUpdDescargaMsj";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);
                
        
            
                    ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                $('#divBuscarMensajes').show();
                                $('#divDescargaMsj').hide();
                                refreshScript("divTablaMensajes", data);
                                }, 'text', false, false, "POST");
                
                              
                jQuery('#divDescargaMsj').html(""); 
                removeDomId('divOrigenMain');
            } else {
                alert_Danger("ERROR:", data);
            }
        }, false, false, false, "POST");
        /*$('#RutaDocs').html("<strong>" + dirPri + "</strong>");
        setTimeout("fu_activaDependencia_dir(" + codDep + "," + codDep + ");", 500);*/
    }
    }
    return;
}

function fn_grabarDescarga() {
    var archivo=document.getElementById("files").files;//$("#fileupload").files();
    
    //var archivo = $("input[type=file name='archivo']").val().split('\\').pop();
    /*var nu_msj = $("#txtNu_Msj").val();
    var nu_des = $("#txtNu_Des").val();
    var esDoc = $("#coEstadoDoc").val();
    var nu_ann=$("#txtNu_Ann").val();
    var nu_emi=$("#txtNu_Emi").val();
    var Observaciones=allTrim($("#txtObservaciones").val());*/

    var myForm='descargaMensajeBean';
   /* var piePag = $("#txtPiePag").val();
    var rbAcceso = $("input[name='rbAcceso']:checked").val();
    var rbCarDoc = $("input[name='rbCarDoc']:checked").val();
    var rbFirDoc = $("input[name='rbFirDoc']:checked").val();
    var rbDocDef = $("input[name='rbDocDef']:checked").val();*/
    //myForm={nu_ann:nu_ann,nu_emi:nu_emi,nu_msj:nu_msj, nu_des:nu_des, esDoc:esDoc, Observaciones:Observaciones};
    
           /* var jsonBody =
                {
                    "nu_msj": $('#'+myForm).find("#txtNu_Msj").val(), "nu_des": $('#'+myForm).find("#txtNu_Des").val(),
                    "co_EstadoDoc": $('#'+myForm).find("#co_EstadoDoc").val(), "nu_ann": $('#'+myForm).find("#txtNu_Ann").val(),
                    "nu_emi": $('#'+myForm).find("#txtNu_Emi").val(), "ob_msj": allTrim($('#'+myForm).find("#txtObservaciones").val()),
                    "mo_msj_dev": $('#'+myForm).find("#coMotivo").val(), "es_pen_msj": allTrim($('#'+myForm).find("#txtes_pen_msj").val()),
                    "fe_ent_msj": $('#'+myForm).find("#txtFecha").val(),"archivo": archivo
                };*/
                
    if(fn_valFormDescargaMsj(myForm)){
        /*jQuery('#fileupload').attr("multiple", "");         
        jQuery('#fileupload').click(); */
       /* var jsonBody =
                {
                    "nu_msj": $('#'+myForm).find("#txtNu_Msj").val(), "nu_des": $('#'+myForm).find("#txtNu_Des").val(),
                    "co_EstadoDoc": $('#'+myForm).find("#co_EstadoDoc").val(), "nu_ann": $('#'+myForm).find("#txtNu_Ann").val(),
                    "nu_emi": $('#'+myForm).find("#txtNu_Emi").val(), "ob_msj": allTrim($('#'+myForm).find("#txtObservaciones").val()),
                    "mo_msj_dev": $('#'+myForm).find("#coMotivo").val(), "es_pen_msj": allTrim($('#'+myForm).find("#txtes_pen_msj").val()),
                    "fe_ent_msj": $('#'+myForm).find("#txtFecha").val()
                };
        var jsonString = JSON.stringify(jsonBody);*/
        var url = "/srGestionMensajes.do?accion=goUpload";
       /* ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);
                
        
            
                    ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                $('#divBuscarMensajes').show();
                                $('#divDescargaMsj').hide();
                                refreshScript("divTablaMensajes", data);
                                }, 'text', false, false, "POST");
                
                              
                jQuery('#divDescargaMsj').html(""); 
                removeDomId('divOrigenMain');
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");*/
        
          var oMyForm = new FormData(myForm);
          /*oMyForm.append("NuEmi", $('#'+myForm).find("#txtNu_Emi").val());
          oMyForm.append("NuDes", $('#'+myForm).find("#txtNu_Des").val());
          oMyForm.append("NuAnn", $('#'+myForm).find("#txtNu_Ann").val());
          oMyForm.append("file", files.files[0]);*/

          /*  $.ajax({
              url:url,// 'http://localhost:8080/spring-mvc-file-upload/rest/cont/upload',
              data: oMyForm,
              dataType: 'text',
              processData: false,
              contentType: false,
              type: 'POST',
              success: function(data){
                if (data === "Datos guardados.") {
                                alert_Sucess("MENSAJE", data);



                                    ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                                $('#divBuscarMensajes').show();
                                                $('#divDescargaMsj').hide();
                                                refreshScript("divTablaMensajes", data);
                                                }, 'text', false, false, "POST");


                                jQuery('#divDescargaMsj').html(""); 
                                removeDomId('divOrigenMain');
                            } else {
                                alert_Danger("ERROR:", data);
                            }
              }
            });*/
        
        ajaxCall(url, oMyForm, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);
                
        
            
                    ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                $('#divBuscarMensajes').show();
                                $('#divDescargaMsj').hide();
                                refreshScript("divTablaMensajes", data);
                                }, 'text', false, false, "POST");
                
                              
                jQuery('#divDescargaMsj').html(""); 
                removeDomId('divOrigenMain');
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'json', false, false, "POST");
        
        
        /*$('#RutaDocs').html("<strong>" + dirPri + "</strong>");
        setTimeout("fu_activaDependencia_dir(" + codDep + "," + codDep + ");", 500);*/
    }
    return;
}

function fn_grabarDescarga2() {
    var rutactx = pRutaContexto + "/" + pAppVersion;
   /* var url = "";
    var data=document.getElementById("files");//$("#fileupload").files();
    url = url.concat(rutactx, "/srGestionMensajes.do?accion=goUpload", "&pkEmi=");
    data.url = url;
    alert_Danger(""+data.url);
    data.submit();
    $("#descargaMensajeBean").submit();
    jQuery('#files').click();*/
    
    
    //var url = "/srGestionMensajes.do?accion=goUpload";
   // var myForm='descargaMensajeBean';
   // var data = new FormData(myForm); //Creamos los datos a enviar con el formulario
  $.ajax({
        url: rutactx+"/srGestionMensajes.do?accion=goUpload", //URL destino
        data: $('#descargaMensajeBean').serialize(),
        processData: false, //Evitamos que JQuery procese los datos, daría error
        contentType: false, //No especificamos ningún tipo de dato
        type: 'POST',
        success: function (resultado) {
            alert(resultado);
        }
    });
    
           /* ajaxCall("/srGestionMensajes.do?accion=goUpload", $('#descargaMensajeBean').serialize(), function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);
                
        
            
                    ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                $('#divBuscarMensajes').show();
                                $('#divDescargaMsj').hide();
                                refreshScript("divTablaMensajes", data);
                                }, 'text', false, false, "POST");
                
                              
                jQuery('#divDescargaMsj').html(""); 
                removeDomId('divOrigenMain');
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'multipart/form-data', false, false, "POST");*/
    

}

function fn_valFormDescargaMsj(objForm){
    var vReturn=1;
//    var Observaciones=$('#'+objForm).find("#txtObservaciones").val();
    var FechaEnt=$('#'+objForm).find("#txtFechaEnt").val();
    var HoraEnt=$('#'+objForm).find("#txtHoraEnt").val();
    var FechaDev=$('#'+objForm).find("#txtFechaDev").val();
    var HoraDev=$('#'+objForm).find("#txtHoraDev").val();
    var archivo=$('#'+objForm).find("#txtNombreAnexo").val();
    
//    if (!!Observaciones===false)
//    {
//        vReturn=0;
//        alert_Info("Aviso", "Debe Ingresar por lo menos una observación");
//        $('#'+objForm).find('#txtObservaciones').focus();
//    }
  
    
    if (!!FechaEnt===false && $('#co_EstadoDoc').val()=="3")
    {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar Fecha");
        $('#'+objForm).find('#FechaEnt').focus();
    }
    
    if(!!FechaEnt && $('#co_EstadoDoc').val()=="3"){
            if(moment(fecha, "DD/MM/YYYY").isValid()){// HH:mm
  
                var fecha=moment(fecha, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(!fecha.isValid()){
                vReturn=0;
                 bootbox.alert("<h5>Fecha Inválida.</h5>", function() {
                     bootbox.hideAll();
                     $('#'+objForm).find('#txtFechaEnt').focus();
                 });
                                         
                }                
            }     
       
       
    }
    
    if (!!FechaDev===false && $('#co_EstadoDoc').val()=="4")
    {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar Fecha");
        $('#'+objForm).find('#txtFecha').focus();
    }
    
    if(!!FechaDev && $('#co_EstadoDoc').val()=="4"){
            if(moment(fecha, "DD/MM/YYYY").isValid()){// HH:mm
  
                var fecha=moment(fecha, ["DD","DD/MM","DD/MM/YY","DD/MM/YY HH:mm","DD/MM/YYYY","DD/MM/YYYY HH:mm"]); 
                if(!fecha.isValid()){
                vReturn=0;
                 bootbox.alert("<h5>Fecha Inválida.</h5>", function() {
                     bootbox.hideAll();
                     $('#'+objForm).find('#txtFechaDev').focus();
                 });
                                         
                }                
            }     
       
       
    }
    
   if (!!HoraEnt===false && $('#co_EstadoDoc').val()=="3") {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar Hora");
        $('#'+objForm).find('#txtHoraEnt').focus(); 
   }
   else {
        
        if (HoraEnt.substr(2,1)==":" && $('#co_EstadoDoc').val()=="3") {
            
            for (i=0; i<5; i++) {

                    if ((HoraEnt.substr(i,1)<"0" || HoraEnt.substr(i,1)>"9") && i!==2) {
                        vReturn = 0; 
                        alert_Info("Aviso", "Debe Ingresar Hora Válida");
                        $('#'+objForm).find('#txtHoraEnt').focus();
                    }
                
                
            }
            
            var h = HoraEnt.substr(0,2);
            var m = HoraEnt.substr(3,2);
            var s = HoraEnt.substr(6,2);
            
            if (h>23 || m>59 || s>59) {
                vReturn = 0; 
                alert_Info("Aviso", "Debe Ingresar Hora Válida");
                $('#'+objForm).find('#txtHoraEnt').focus();
            }
        } else {
            if($('#co_EstadoDoc').val()=="3"){
            vReturn = 0; 
            alert_Info("Aviso", "Debe Verificar Formato de Hora");
            $('#'+objForm).find('#txtHoraEnt').focus();}
        }
    }
   
   if (!!HoraDev===false && $('#co_EstadoDoc').val()=="4") {
        vReturn=0;
        alert_Info("Aviso", "Debe Ingresar Hora");
        $('#'+objForm).find('#txtHoraDev').focus(); 
   }
   else {
        
        if (HoraDev.substr(2,1)==":" && $('#co_EstadoDoc').val()=="4") {
            
            for (i=0; i<5; i++) {

                    if ((HoraDev.substr(i,1)<"0" || HoraDev.substr(i,1)>"9") && i!==2) {
                        vReturn = 0; 
                        alert_Info("Aviso", "Debe Ingresar Hora Válida");
                        $('#'+objForm).find('#txtHoraDev').focus();
                    }
                
                
            }
            
            var h = HoraDev.substr(0,2);
            var m = HoraDev.substr(3,2);
            var s = HoraDev.substr(6,2);
            
            if (h>23 || m>59 || s>59) {
                vReturn = 0; 
                alert_Info("Aviso", "Debe Ingresar Hora Válida");
                $('#'+objForm).find('#txtHoraDev').focus();
            }
        } else {
            if( $('#co_EstadoDoc').val()=="4"){
            vReturn = 0; 
            alert_Info("Aviso", "Debe Verificar Formato de Hora");
            $('#'+objForm).find('#txtHoraDev').focus();}
        }
    }
      
   
   
    if (!!archivo===false && $("#txtAnexarCargo").val()=="SI")
    {
        vReturn=0;
        alert_Info("Aviso", "Debe seleccionar un archivo");
        $('#'+objForm).find('#archivo').focus();
    }
   
   
   
    return vReturn;
}

function fn_eliminarMsj(vnu_ann,vnu_emi,vnu_des,vnu_msj) {
   // var pesDocEmi = jQuery('#buscarDocumentoCargaMsjBean').find('#esDocEmi').val();
    if (vnu_ann !== null && vnu_emi !== null) {
//        if (confirm('¿ Esta Seguro de Eliminar ?')) {
//            $('#documentoEmiBean').find('select').removeProp('disabled');
//            ajaxCall("/srDocumentoAdmEmision.do?accion=goEliminarDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
//                fn_rptEliminarDocEmiAdm(data);
//            }, 'json', false, false, "POST");
//        }
        bootbox.dialog({
            message: " <h5>¿Esta seguro de retornar este registro?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-primary",
                    callback: function() {
                       // $('#documentoEmiBean').find('select').removeProp('disabled');
                        ajaxCallSendJson("/srGestionMensajes.do?accion=goEliminaMensaje&nu_ann="+vnu_ann+"&nu_emi="+vnu_emi+"&nu_des="+vnu_des+"&nu_msj="+vnu_msj, "", function(data) {
                           if (data === "Datos guardados.") {
                                alert_Sucess("MENSAJE", "Registro Retornado");                                          
            
                                ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                        $('#divBuscarMensajes').show();
                                        $('#divDescargaMsj').hide();
                                        refreshScript("divTablaMensajes", data);
                                        }, 'text', false, false, "POST");
                
                              
                                    jQuery('#divDescargaMsj').html(""); 
                                    removeDomId('divOrigenMain');
                            } else {
                                alert_Danger("ERROR:", data);
                            }
                        }, 'text', false, false, "POST");
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

function change(obj) {


    var selectBox = obj;
    var selected = selectBox.options[selectBox.selectedIndex].value;
    var textarea = document.getElementById("coMotivo");

    if(selected === '1'){
        textarea.style.display = "block";
    }
    else{
        textarea.style.display = "none";
    }
}


function fu_generarReporteMsjConsulPDF(){
   fu_generarReporteMsjConsul('PDF');  
}

function fu_generarReporteMsjConsulXLS(){
   fu_generarReporteMsjConsul('XLS');  
}

function fu_generarReporteMsjConsul(pformatoReporte){
    var validaFiltro = fu_validaFiltroRecepDocAdmConsul("0");
    if (validaFiltro === "1" || validaFiltro === "2") {
        //ajaxCall("/srConsultaRecepcionDoc.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $('#buscarDocumentoRecepConsulBean').serialize(), function(data) {
        ajaxCall("/srGestionMensajes.do?accion=goExportarArchivoLista&pformatRepor="+pformatoReporte, $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
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

function fn_anexoDocumento()
{
    var reemplazarDoc = true;
    jQuery('#progress .progress-bar').css('width', '0%');
    jQuery('#fileupload').removeAttr("multiple");
    fn_fileUploadMsj(reemplazarDoc);
}


function fn_anexoDocumentoActa1()
{
    var reemplazarDoc = true;
    jQuery('#progressActa1 .progress-bar').css('width', '0%');
    jQuery('#fileuploadActa1').removeAttr("multiple");
    fn_fileUploadMsj1(reemplazarDoc);
}


function fn_anexoDocumentoActa2()
{
    var reemplazarDoc = true;
    jQuery('#progressActa2 .progress-bar').css('width', '0%');
    jQuery('#fileuploadActa2').removeAttr("multiple");
    fn_fileUploadMsj2(reemplazarDoc);
}

function fn_fileUploadMsj1(reemplazarDoc) {
    $("#progress").show();
     jQuery("#divErrorLista").html("");
     jQuery("#divError").hide();
  //  var pFileSizeMax = 10000000;
    var pFileSizeMax=jQuery('#fileSizeMaxCargo').val();
//    var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var numFiles = 0;
    var contAux = 0;
    jQuery('#fileuploadActa1').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            if (fileSize <= (fileSizeMax*1024*1024))
            {
                numFiles++;
                if (reemplazarDoc === true) {
                    var nu_Acta_Vis = $("#txt_pnu_Acta_Vis1").attr("value");
                    var nuAnn = $("#txtNu_Ann").attr("value");
                    var nuEmi = $("#txtNu_Emi").attr("value");
                    var nuDes = $("#txtNu_Des").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srGestionMensajes.do?accion=goUploadActa", "&NuAnn=", nuAnn,"&NuEmi=", nuEmi, "&NuDes=", nuDes, "&nu_Acta_Vis=", nu_Acta_Vis);
                    data.url = url;
                }
                data.submit();
            } else
            {
                fileSize = Math.round(fileSize / 1024) + " Kb";
                var row = "";
                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMax +'MB.</strong> ');
                jQuery("#divErrorLista").append('<li>' + row + '</li>');
                jQuery("#divError").show();
                jQuery("#divError").attr('display', 'block');
                return;
            }
        },
        done: function(e, data) {
            if (data.textStatus === "success")
            {
                contAux++;
                if (contAux === numFiles) {
                    if (reemplazarDoc) {
                        var jsonRes = jQuery.parseJSON(data.result);
                        var nombreDoc = jsonRes[0].name;
                        var nuAne = $("#oNuAne").attr("value");
                        $("#Anexo" + nuAne + " input:eq(0)").attr("value", nombreDoc);
                        $("#Anexo" + nuAne + " input:eq(1)").attr("value", nombreDoc);
                        reemplazarDoc = false;
                    } else {
                        actualizarListadoAnexos();
                    }
                    alert_Sucess("MENSAJE", "archivo adjuntado");
                    $("#txtNombreActa1").attr("value",nombreDoc);
                }
            } else {
                alert_Danger("ERROR", "Error al cargar el archivo");
            }
        },
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            jQuery('#progressActa1 .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progressActa1 span').html(progressText);
        }
    });
    jQuery('#fileuploadActa1').click();
}


function fn_fileUploadMsj2(reemplazarDoc) {
    $("#progress").show();
     jQuery("#divErrorLista").html("");
     jQuery("#divError").hide();
  //  var pFileSizeMax = 10000000;
    var pFileSizeMax=jQuery('#fileSizeMaxCargo').val();
//    var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var numFiles = 0;
    var contAux = 0;
    jQuery('#fileuploadActa2').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            if (fileSize <= (fileSizeMax*1024*1024))
            {
                numFiles++;
                if (reemplazarDoc === true) {
                    var nu_Acta_Vis = $("#txt_pnu_Acta_Vis2").attr("value");
                    var nuAnn = $("#txtNu_Ann").attr("value");
                    var nuEmi = $("#txtNu_Emi").attr("value");
                    var nuDes = $("#txtNu_Des").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srGestionMensajes.do?accion=goUploadActa", "&NuAnn=", nuAnn,"&NuEmi=", nuEmi, "&NuDes=", nuDes, "&nu_Acta_Vis=", nu_Acta_Vis);
                    data.url = url;
                }
                data.submit();
            } else
            {
                fileSize = Math.round(fileSize / 1024) + " Kb";
                var row = "";
                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMax +'MB.</strong> ');
                jQuery("#divErrorLista").append('<li>' + row + '</li>');
                jQuery("#divError").show();
                jQuery("#divError").attr('display', 'block');
                return;
            }
        },
        done: function(e, data) {
            if (data.textStatus === "success")
            {
                contAux++;
                if (contAux === numFiles) {
                    if (reemplazarDoc) {
                        var jsonRes = jQuery.parseJSON(data.result);
                        var nombreDoc = jsonRes[0].name;
                        var nuAne = $("#oNuAne").attr("value");
                        $("#Anexo" + nuAne + " input:eq(0)").attr("value", nombreDoc);
                        $("#Anexo" + nuAne + " input:eq(1)").attr("value", nombreDoc);
                        reemplazarDoc = false;
                    } else {
                        actualizarListadoAnexos();
                    }
                    alert_Sucess("MENSAJE", "archivo adjuntado");
                    $("#txtNombreActa2").attr("value",nombreDoc);
                }
            } else {
                alert_Danger("ERROR", "Error al cargar el archivo");
            }
        },
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            jQuery('#progressActa2 .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progressActa2 span').html(progressText);
        }
    });
    jQuery('#fileuploadActa2').click();
}


function fn_fileUploadMsj(reemplazarDoc) {
    $("#progress").show();
     jQuery("#divErrorLista").html("");
     jQuery("#divError").hide();
  //  var pFileSizeMax = 10000000;
    var pFileSizeMax=jQuery('#fileSizeMaxCargo').val();
//    var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var numFiles = 0;
    var contAux = 0;
    jQuery('#fileupload').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            if (fileSize <= (fileSizeMax*1024*1024))
            {
                numFiles++;
                if (reemplazarDoc === true) {
                    var nuAnn = $("#txtNu_Ann").attr("value");
                    var nuEmi = $("#txtNu_Emi").attr("value");
                    var nuDes = $("#txtNu_Des").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srGestionMensajes.do?accion=goUpload", "&NuAnn=", nuAnn,"&NuEmi=", nuEmi, "&NuDes=", nuDes);
                    data.url = url;
                }
                data.submit();
            } else
            {
                fileSize = Math.round(fileSize / 1024) + " Kb";
                var row = "";
                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMax +'MB.</strong> ');
                jQuery("#divErrorLista").append('<li>' + row + '</li>');
                jQuery("#divError").show();
                jQuery("#divError").attr('display', 'block');
                return;
            }
        },
        done: function(e, data) {
            if (data.textStatus === "success")
            {
                contAux++;
                if (contAux === numFiles) {
                    if (reemplazarDoc) {
                        var jsonRes = jQuery.parseJSON(data.result);
                        var nombreDoc = jsonRes[0].name;
                        var nuAne = $("#oNuAne").attr("value");
                        $("#Anexo" + nuAne + " input:eq(0)").attr("value", nombreDoc);
                        $("#Anexo" + nuAne + " input:eq(1)").attr("value", nombreDoc);
                        reemplazarDoc = false;
                    } else {
                        actualizarListadoAnexos();
                    }
                    alert_Sucess("MENSAJE", "archivo adjuntado");
                    $("#txtNombreAnexo").attr("value",nombreDoc);
                }
            } else {
                alert_Danger("ERROR", "Error al cargar el archivo");
            }
        },
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            jQuery('#progress .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progress span').html(progressText);
        }
    });
    jQuery('#fileupload').click();
}


function fn_anexoDocumentoArc(pAnn,pEmi,pDes)
{
    var reemplazarDoc = true;
    jQuery('#progress .progress-bar').css('width', '0%');
    jQuery('#fileupload').removeAttr("multiple");
    fn_fileUploadMsjArc(reemplazarDoc,pAnn,pEmi,pDes);
}

function fn_fileUploadMsjArc(reemplazarDoc,pAnn,pEmi,pDes) {
    $("#progress").show();
    var pFileSizeMax = 10000000;
//    var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var numFiles = 0;
    var contAux = 0;
    jQuery('#fileupload').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            if (fileSize <= fileSizeMax)
            {
                numFiles++;
                if (reemplazarDoc === true) {
                    var nuAnn=pAnn;// $("#txtNu_Ann").attr("value");
                    var nuEmi=pEmi ;//$("#txtNu_Emi").attr("value");
                    var nuDes=pDes ;//$("#txtNu_Des").attr("value");
                    var url = "";
                    url = url.concat(rutactx, "/srGestionMensajes.do?accion=goUpload", "&NuAnn=", nuAnn,"&NuEmi=", nuEmi, "&NuDes=", nuDes);
                    data.url = url;
                }
                data.submit();
                
            } else
            {
                fileSize = Math.round(fileSize / 1024) + " Kb";
                var row = "";
                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido.');
                jQuery("#divErrorLista").append('<li>' + row + '</li>');
                jQuery("#divError").show();
                jQuery("#divError").attr('display', 'block');
                return;
            }
        },
        done: function(e, data) {
            if (data.textStatus === "success")
            {
                contAux++;
                if (contAux === numFiles) {
                    if (reemplazarDoc) {
                        var jsonRes = jQuery.parseJSON(data.result);
                        var nombreDoc = jsonRes[0].name;
                        var nuAne = $("#oNuAne").attr("value");
                        $("#Anexo" + nuAne + " input:eq(0)").attr("value", nombreDoc);
                        $("#Anexo" + nuAne + " input:eq(1)").attr("value", nombreDoc);
                        reemplazarDoc = false;
                    } else {
                        actualizarListadoAnexos();
                    }
                    alert_Sucess("MENSAJE", "archivo adjuntado");
                    fn_verListadoMsjAnexos(pAnn,pEmi);
                    $("#txtNombreAnexo").attr("value",nombreDoc);
                }
            } else {
                alert_Danger("ERROR", "Error al cargar el archivo");
            }
        },
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 100, 10);
            jQuery('#progress .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progress span').html(progressText);
        }
    });
    jQuery('#fileupload').click();
    
}


function fn_verAnexoMsj(pnuAnn, pnuEmi, pnuDes) {
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goDocRutaAnexo";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuDes=" + pnuDes;
        ajaxCall("/srDocObjeto.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                //runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento,param, function(data){
                    result = data;
                });
            }

        }, 'text', false, false, "POST");

    }
    return false;
}

function fn_eliminarArchivoMsj(pNuAnn,pNuEmi,pNuDes)
{

    var p = new Array();
    var accion;

    accion = "accion=goDelete";
 
    p[0] = accion;
    p[1] = "nu_ann=" + pNuAnn;
    p[2] = "nu_emi=" + pNuEmi;
    p[3] = "nu_des=" + pNuDes;
    ajaxCall("/srGestionMensajes.do", p.join("&"), function(data) {
        if (data !== null)
        {
            alert_Sucess("MENSAJE", "archivo eleminado");
            fn_verListadoMsjAnexos(pNuAnn,pNuEmi);
        }
    }, 'text', false, false, "POST");
    return false;
}



function calPenEnv()
{
   var TipoMsj=$("#txtPeEnvMsj").val();
    var vBloDias=getNumeroDeDiasDiferencia($("#txtFe_Act").val(),$("#txtFechaEnt").val());
    var vDias=getNumeroDeDiasDiferencia($("#txtFe_Pla").val(),$("#txtFechaEnt").val());
    
    if (vDifDias>0)
    {
        $("#txtFechaDev").val($("#txtFechaEnt").val());
        

    }
   
   $("#txtFechaDev").datepicker("change", { minDate: vBloDias }); 
    
   var fila1 = document.getElementById("rowPen1"); 
   var fila2 = document.getElementById("rowPen2"); 
   
   if (TipoMsj !=="0")
   {
    
  
    fila1.style.display = ""; //ocultar fila 
    fila2.style.display = "";
    
    var vDifDias=getNumeroDeDiasDiferencia($("#txtFechaDev").val(),$("#txtFechaEnt").val());
 
    if (vDias>0)
      {
       $('#txtes_pen').text('SI');
        obj = document.getElementById('txtes_pen');
        obj.style.backgroundColor = (obj.style.backgroundColor = '#ac2925') ? 'none' : '#ac2925';
       $('#txtes_pen_msj').val('S');
      }
   else
      {
       $('#txtes_pen').text('NO');
       obj = document.getElementById('txtes_pen');
      obj.style.backgroundColor = (obj.style.backgroundColor = '#468847') ? 'none' : '#468847';
       $('#txtes_pen_msj').val('N');
      }
    calPenDev();
   }
  else
  {
      fila1.style.display = "none"; //ocultar fila 
      fila2.style.display = "none";
  }
    
    
}

function calPenDev()
{ 
   var TipoMsj=$("#txtPeEnvMsj").val();
    
   var fila1 = document.getElementById("rowPen1"); 
   var fila2 = document.getElementById("rowPen2"); 
    $("#txtDia_Pla_Dev").val(jQuery('#txtDia_Pen_Dev').val());
   if (TipoMsj !=="0")
   {
    
    
    fila1.style.display = ""; //ocultar fila 
    fila2.style.display = "";
    
    var estado=jQuery('#co_EstadoDoc').val();
    
    var arrayDeCadenas = $("#coMotivo").val().split("|");
    var pAmbito=jQuery('#txtAmbito').val();
    var pTipoEnv=jQuery('#txtTipoEnv').val();
    
    
     
    if(estado === "4")
    {
   
        if (pAmbito.toUpperCase()==="LOCAL" && arrayDeCadenas[1]!=="0")
        {
            $("#txtDia_Pla_Dev").val(arrayDeCadenas[1]);
             var fe=getSumarDias($("#txtFechaEnt").val(),arrayDeCadenas[1]);
            console.log(fe);
            $("#txtFe_Pla_Dev").val(""+fe);
        }
        else
        {
            if (pAmbito.toUpperCase()==="NACIONAL" && arrayDeCadenas[2]!=="0")
            {
                $("#txtDia_Pla_Dev").val(arrayDeCadenas[2]);
                 var fe=getSumarDias($("#txtFechaEnt").val(),arrayDeCadenas[2]);
                console.log(fe);
                jQuery("#txtFe_Pla_Dev").val(""+fe);
            } 
            else
            {
                if (pTipoEnv.toUpperCase()==="OVERNIGTH" && arrayDeCadenas[3]!=="0")
                {
                    $("#txtDia_Pla_Dev").val(arrayDeCadenas[3]);
                    var fe=getSumarDias($("#txtFechaEnt").val(),arrayDeCadenas[3]);
                    console.log(fe);
                    jQuery("#txtFe_Pla_Dev").val(""+fe);
                }
                else
                {
                    fu_calFechaPlazo();
                }
            }
 
        }
            
        

        
    }
    else
    {
       fu_calFechaPlazo(); 
    }
   
    
    var vDias=getNumeroDeDiasDiferencia($("#txtFe_Pla_Dev").val(),$("#txtFechaDev").val());
     if (vDias>0 && $("#txtFechaDev").val()!==$("#txtFechaEnt").val())
       {
        $('#txtes_dev').text('SI');
       obj = document.getElementById('txtes_dev');
       obj.style.backgroundColor = (obj.style.backgroundColor = '#ac2925') ? 'none' : '#ac2925';
        $('#txtes_pen_dev').val('S');
       }
    else
       {
        $('#txtes_dev').text('NO');
        obj = document.getElementById('txtes_dev');
        obj.style.backgroundColor = (obj.style.backgroundColor = '#468847') ? 'none' : '#468847';
        $('#txtes_pen_dev').val('N');
       }       
            
        
     //var vPla =$("#txtDia_Pla_Dev").val();
       // alert_Danger("dias=>>>",vDias);


   }
   else
   {
      fila1.style.display = "none"; //ocultar fila 
      fila2.style.display = "none";
   }
    
    

           
}

function fn_verDocMsj(pnuAnn, pnuEmi, pnuDes,nuActa,nombreArchivo) {
    
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goObtieneDocumentoMsj";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuDes=" + pnuDes;
        p[4] = "nuActa=" + pnuActa;
        p[5] = "nombreArchivo=" + pnombreArchivo;
       
        ajaxCall("/srGestionMensajes.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                //runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento, param, function(data){
                    result = data;
                });
            }

        }, 'text', false, false, "POST");

    }
    return false;
}



function fn_verDocActa(pnuAnn, pnuEmi, pnuDes,pnuActa,pnombreArchivo) {
    
    if (!!pnuAnn) {
        var p = new Array();
        p[0] = "accion=goObtieneActaMsj";
        p[1] = "nuAnn=" + pnuAnn;
        p[2] = "nuEmi=" + pnuEmi;
        p[3] = "nuDes=" + pnuDes;
        p[4] = "nuActa=" + pnuActa;
        p[5] = "nombreArchivo=" + pnombreArchivo;
       
        ajaxCall("/srGestionMensajes.do", p.join("&"), function(data) {
            var result;
            eval("var docs=" + data);
            if (typeof (docs) != "undefined" && typeof (docs.nuAnn) != 'undefined' && docs.nuAnn != "") {
                //result = fn_abrirDocApplet(docs.noUrl,docs.noDoc);
                var param = {urlDoc: docs.noUrl, rutaDoc: docs.noDoc};
                //runApplet(appletsTramiteDoc.abrirDocumento, param, function(data) {
                runOnDesktop(accionOnDesktopTramiteDoc.abrirDocumento, param, function(data){
                    result = data;
                });
            }

        }, 'text', false, false, "POST");

    }
    return false;
}


function fn_revertirMsj(vnu_ann,vnu_emi,vnu_des,vnu_msj) {
   // var pesDocEmi = jQuery('#buscarDocumentoCargaMsjBean').find('#esDocEmi').val();
    if (vnu_ann !== null && vnu_emi !== null) {
//        if (confirm('¿ Esta Seguro de Eliminar ?')) {
//            $('#documentoEmiBean').find('select').removeProp('disabled');
//            ajaxCall("/srDocumentoAdmEmision.do?accion=goEliminarDocEmiAdm", $('#documentoEmiBean').serialize(), function(data) {
//                fn_rptEliminarDocEmiAdm(data);
//            }, 'json', false, false, "POST");
//        }
        bootbox.dialog({
            message: " <h5>¿Esta seguro de revertir este registro?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-primary",
                    callback: function() {
                       // $('#documentoEmiBean').find('select').removeProp('disabled');
                        ajaxCallSendJson("/srGestionMensajes.do?accion=goRevertirMensaje&nu_ann="+vnu_ann+"&nu_emi="+vnu_emi+"&nu_des="+vnu_des+"&nu_msj="+vnu_msj, "", function(data) {
                           if (data === "Datos guardados.") {
                                alert_Sucess("MENSAJE", "Registro Revertido");                                          
            
                                ajaxCall("/srGestionMensajes.do?accion=goInicio", $('#buscarDocumentoCargaMsjBean').serialize(), function(data) {
                                        $('#divBuscarMensajes').show();
                                        $('#divDescargaMsj').hide();
                                        refreshScript("divTablaMensajes", data);
                                        }, 'text', false, false, "POST");
                
                              
                                    jQuery('#divDescargaMsj').html(""); 
                                    removeDomId('divOrigenMain');
                            } else {
                                alert_Danger("ERROR:", data);
                            }
                        }, 'text', false, false, "POST");
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

function fu_calFechaPlazo(){
   
    var p = new Array();

    p[0] = "pfechaent=" + $("#txtFechaEnt").val();
    p[1] = "pdipladev=" + $("#txtDia_Pla_Dev").val();

    ajaxCall("/srGestionMensajes.do?accion=goCalFechaPlazo",  p.join("&"), function(data) {
                if(data!==null){ 
                    if(data.coRespuesta==="1"){ 

                            $("#txtFe_Pla_Dev").val(data.deFechaDev);  

                    }else{
                        alert_Danger("Calcular Fecha Plazo: ",data.deRespuesta);
                    }
                }
        },'json', false, false, "POST");                  


}

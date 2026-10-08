function fn_inicializaDocEmiAlta(sCoAnnio){
    //countPressBtnChange=0;
    changeTipoBusqEmiDocuAlta("0");  
    jQuery("#fechaFiltro").html("Año: "+sCoAnnio);
    jQuery("#fechaFiltro").showDatePicker({
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
    });
    jQuery('#buscarDocumentoEmiBean').find('#sNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoEmiBean').find('#sNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });    
    jQuery('#buscarDocumentoEmiBean').find('#sCoAnnioBus').find('option[value=""]').remove();
}

function fn_eventEstDocAlta(sTipoDestEmi, sEstadoDocAdm) {
    $("#progressAlta").hide();
    $("[bt='true']").showBigTextBox({
        pressAceptarEvent: function(data) {
            if (data.hasChangedText) {
                changeAccionBDDocEmi(data.currentObject);
            }
        }
    });
    $("#txtIndicaciones").showBigTextBox();
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
    jQuery('#documentoEmiBean').find('#coTipDocAdm').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#deAsu').change(function() {
        fu_changeDocumentoEmiBean();
    });
    jQuery('#documentoEmiBean').find('#nuDocEmi').change(function() {
        var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
        var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
        var ptiEmi = jQuery('#documentoEmiBean').find('#tiEmi').val();
        var pcoTipDocAdm = jQuery('#documentoEmiBean').find('#coTipDocAdm').val();
        var pcoDepEmi = jQuery('#documentoEmiBean').find('#coDepEmi').val();
        var pnuDocEmiAnn = jQuery('#documentoEmiBean').find('#txtnuDocEmiAn').val();
        var pnuDocEmi = allTrim(jQuery('#documentoEmiBean').find('#nuDocEmi').val());
        if (typeof(pnuDocEmi) !== "undefined" && pnuDocEmi !== "") {
            pnuDocEmi = replicate(pnuDocEmi, 6);
            if (pnuDocEmiAnn !== pnuDocEmi || (typeof(pnuDocEmiAnn) !== "undefined" && pnuDocEmiAnn === "")) {
                fn_jsonVerificarNumeracionDocEmi(pnuDocEmiAnn, pnuAnn, pnuEmi, ptiEmi, pcoTipDocAdm, pcoDepEmi, pnuDocEmi);
            } else {
                jQuery('#nuDocEmi').val(pnuDocEmiAnn);
            }
        }
        fu_changeDocumentoEmiBean();
    });
    if (jQuery("#txtEsNuevoDocAdm").val() === '1') {
        jQuery("#coTipDocAdm").focus();
    }
    fn_changeTipoDestinatarioDocuEmi(sTipoDestEmi, sEstadoDocAdm);
    fu_cargaEdicionDocAlta(sEstadoDocAdm);
}

function changeTipoBusqEmiDocuAlta(tipo) {
    jQuery('#sTipoBusqueda').val(tipo);
    submitAjaxFormEmiDocAlta(tipo);
    mostrarOcultarDivBusqFiltro2();
}

function submitAjaxFormEmiDocAlta(tipo) {
    ajaxCall("/srDocumentoEmisionAlta.do?accion=goInicio", $('#buscarDocumentoEmiBean').serialize(), function(data) {
        refreshScript("divTablaEmiDocumenAdm", data);
    }, 'text', false, false, "POST");
    return false;
}


function fu_goNuevoEmisionDocAlta() {
    var validaFiltro = "";
    validaFiltro = "1";
    if (validaFiltro === "1") {
        ajaxCall("/srDocumentoEmisionAlta.do?accion=goNuevoDocumentoEmi", '', function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            fn_cargaToolBarEmiAlta();
        }, 'text', false, false, "POST");
    }
    return false;
}

function fu_goEditarEmisionDocAlta() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    if (pnuAnn !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        p[3] = "pexisteDoc=" + jQuery('#txtpexisteDoc').val();
        p[4] = "pexisteAnexo=" + jQuery('#txtpexisteAnexo').val();
        ajaxCall("/srDocumentoEmisionAlta.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmiAlta();
        }, 'text', false, false, "POST");
    } else {
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}

function editarDocumentoAltaClick(pnuAnn,pnuEmi,pexisteDoc,pexisteAnexo) {
    if (pnuAnn !== "" && pnuEmi !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + pnuEmi;
        p[2] = "pexisteDoc=" + pexisteDoc;
        p[3] = "pexisteAnexo=" + pexisteAnexo;
        ajaxCall("/srDocumentoEmisionAlta.do?accion=goEditDocumentoEmi", p.join("&"), function(data) {
            refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
            fn_cargaToolBarEmiAlta();
        }, 'text', false, false, "POST");
    } else {
       bootbox.alert("Seleccione una fila de la lista");
    }
}

/**
 * funcion que habilita o deshabilita las propiedades del Documento 
 * de acuerdo a su estado.
 * @param {type} tModulo --> emision 00, recepcion 01
 * @param {type} estadoDocAdm --> estado del documento
 * @returns void
 */
function fu_cargaEdicionDocAlta(estadoDocAdm){
        var btnAux = jQuery('#estDocEmiAdm').find('button').get(0);
        btnAux.removeAttribute('onclick');
        jQuery('#estDocEmiAdm').removeClass('btn-group');
        jQuery('#estDocEmiAdm').find('button').last().hide();
        var objSpan = jQuery('#estDocEmiAdm').find('button').first().find('span');
        if(objSpan.hasClass('glyphicon glyphicon-ok')){
            objSpan.removeClass('glyphicon glyphicon-ok');
        }
        if(estadoDocAdm === "5" || estadoDocAdm === "7" ){//en proyecto o para despacho
          jQuery('#coTipDocAdm').removeProp('disabled');
          jQuery('#deAsu').removeProp('readonly');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').first().removeProp('readonly');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').last().removeProp('readonly');
         jQuery('#coTipDocAdm').removeAttr('disabled');
          jQuery('#deAsu').removeAttr('readonly');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').first().removeAttr('readonly');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').last().removeAttr('readonly');    
          
              jQuery('#ullsEstDocEmiAdm').html('');
              btnAux.setAttribute('onclick','fu_changeEstadoDocEmiAdmAlta(\'0\');');
              jQuery('#nuDocEmi').removeProp('readonly');
              jQuery('#nuDocEmi').removeAttr('readonly');
              jQuery('#estDocEmiAdm').find('button').first().html("<span/>  EN PROYECTO");              
              (jQuery('#estDocEmiAdm').find('button').first().find('span')).addClass('glyphicon glyphicon-ok');
              
          jQuery('#nuDiaAte').removeProp('readonly');
          jQuery('#nuDiaAte').removeAttr('readonly');
          jQuery("#tblRefEmiDocAdm").find("input,textarea").removeProp("readonly");
          jQuery("#tblRefEmiDocAdm").find("input,textarea").removeAttr("readonly");
          jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").removeProp("disabled");
          jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").removeAttr("disabled");
          jQuery("#tblRefEmiDocAdm tbody tr").each(function(index,row){
            if(index > 0){
               $(row).find("button").first().show();
            }
          });
          jQuery('#btnGrabaDocEmi').show();
          jQuery('#btnAnulaDocEmi').show();
          
          jQuery('#tblRefEmiDocAdm').parents(':eq(4)').find('td').first().show();
          if(jQuery("#sTipoDestinatario").val()==="01"){
            jQuery("#divMuestraOpcInstitu").show();
          }
          jQuery("#tblDestEmiDocAdm").find("select,button").removeProp("disabled");
          jQuery("#tblDestEmiDocAdm").find("select,button").removeAttr("disabled");
          jQuery("#tblDestEmiDocAdm").find("input,textarea").removeProp("readonly");          
          jQuery("#tblDestEmiDocAdm").find("input,textarea").removeAttr("readonly");          
          
          jQuery("#tblDestEmiDocAdmOtro").find("select,button").first().removeProp("disabled");
          jQuery("#tblDestEmiDocAdmOtro").find("input,textarea").first().removeProp("readonly");     
          
          jQuery("#tblDestEmiDocAdmCiudadano").find("select,button").first().removeProp("disabled");
          jQuery("#tblDestEmiDocAdmCiudadano").find("input,textarea").first().removeProp("readonly");     
          
          jQuery("#tblDestEmiDocAdmPersJuri").find("select,button").first().removeProp("disabled");
          jQuery("#tblDestEmiDocAdmPersJuri").find("input,textarea").first().removeProp("readonly");    
          
          jQuery("#tblDestEmiDocAdmOtro").find("select,button").first().removeAttr("disabled");
          jQuery("#tblDestEmiDocAdmOtro").find("input,textarea").first().removeAttr("readonly");     
          
          jQuery("#tblDestEmiDocAdmCiudadano").find("select,button").first().removeAttr("disabled");
          jQuery("#tblDestEmiDocAdmCiudadano").find("input,textarea").first().removeAttr("readonly");     
          
          jQuery("#tblDestEmiDocAdmPersJuri").find("select,button").first().removeAttr("disabled");
          jQuery("#tblDestEmiDocAdmPersJuri").find("input,textarea").first().removeAttr("readonly");           
          
          jQuery('#tblDestEmiDocAdm').parents(':eq(4)').find('td').first().show();
          jQuery('#tblDestEmiDocAdmOtro').parents(':eq(4)').find('td').first().show();
          jQuery('#tblDestEmiDocAdmCiudadano').parents(':eq(4)').find('td').first().show();
          jQuery('#tblDestEmiDocAdmPersJuri').parents(':eq(4)').find('td').first().show();
          
          (jQuery('#deEmpRes').parent()).find('button').show();
          if(!jQuery('#deEmpRes').hasClass('inp-xs-grup')){
            jQuery('#deEmpRes').addClass('inp-xs-grup');          
          }
         
          (jQuery('#deEmpEmi').parent()).find('button').show();
          if(!jQuery('#deEmpEmi').hasClass('inp-xs-grup')){
            jQuery('#deEmpEmi').addClass('inp-xs-grup');          
          }
          
        }else{
//          jQuery('#coDepEmi').prop('disabled','true');
//          jQuery('#coLocEmi').prop('disabled','true');
          jQuery('#coTipDocAdm').prop('disabled','true');
          jQuery('#deAsu').prop('readonly','true');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').first().prop('readonly','true');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').last().prop('readonly','true');
          
          jQuery('#coTipDocAdm').attr('disabled','true');
          jQuery('#deAsu').attr('readonly','true');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').first().attr('readonly','true');
          (jQuery('#nuDocEmi').parent()).find('input[type=text]').last().attr('readonly','true');          
          
          if(estadoDocAdm === "0"){
//              jQuery('#btnAnulaDocEmi').show();
              jQuery('#ullsEstDocEmiAdm').html('');
              jQuery('#ullsEstDocEmiAdm').append('<li><a href="#" onclick="fu_changeEstadoDocEmiAdmAlta(\'5\');">EN PROYECTO</a></li>');
              if(!jQuery('#estDocEmiAdm').hasClass('btn-group')){
                jQuery('#estDocEmiAdm').addClass('btn-group');          
              }
              jQuery('#estDocEmiAdm').find('button').last().show();
              jQuery('#estDocEmiAdm').find('button').first().text('EMITIDO');
          }
//          jQuery('#feEmiCorta').prop('readonly','true');
          jQuery('#nuDiaAte').prop('readonly','true');
          jQuery('#nuDiaAte').attr('readonly','true');
          
          jQuery("#tblRefEmiDocAdm").find("input,textarea").prop('readonly','true');
          jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").prop('disabled','true');
          
          jQuery("#tblRefEmiDocAdm").find("input,textarea").attr('readonly','true');
          jQuery("#tblRefEmiDocAdm").find("select,input[type=radio]").attr('disabled','true');          
          jQuery("#tblRefEmiDocAdm tbody tr").each(function(index,row){
            if(index > 0){
               $(row).find("button").first().hide();
            }
          });
          jQuery('#btnGrabaDocEmi').hide();
          
          jQuery('#tblRefEmiDocAdm').parents(':eq(4)').find('td').first().hide();
          jQuery("#divMuestraOpcInstitu").hide();
          
          jQuery("#tblDestEmiDocAdm").find("select,button").prop('disabled','true');
          jQuery("#tblDestEmiDocAdm").find("input,textarea").prop('readonly','true');             
          
          jQuery("#tblDestEmiDocAdmOtro").find("select,button").prop('disabled','true');
          jQuery("#tblDestEmiDocAdmOtro").find("input,textarea").prop('readonly','true');             
          
          jQuery("#tblDestEmiDocAdmCiudadano").find("select,button").prop('disabled','true');
          jQuery("#tblDestEmiDocAdmCiudadano").find("input,textarea").prop('readonly','true');  
          
          jQuery("#tblDestEmiDocAdmPersJuri").find("select,button").prop('disabled','true');
          jQuery("#tblDestEmiDocAdmPersJuri").find("input,textarea").prop('readonly','true');      
          
          jQuery("#tblDestEmiDocAdm").find("select,button").attr('disabled','true');
          jQuery("#tblDestEmiDocAdm").find("input,textarea").attr('readonly','true');             
          
          jQuery("#tblDestEmiDocAdmOtro").find("select,button").attr('disabled','true');
          jQuery("#tblDestEmiDocAdmOtro").find("input,textarea").attr('readonly','true');             
          
          jQuery("#tblDestEmiDocAdmCiudadano").find("select,button").attr('disabled','true');
          jQuery("#tblDestEmiDocAdmCiudadano").find("input,textarea").attr('readonly','true');  
          
          jQuery("#tblDestEmiDocAdmPersJuri").find("select,button").attr('disabled','true');
          jQuery("#tblDestEmiDocAdmPersJuri").find("input,textarea").attr('readonly','true');             
          
          jQuery('#tblDestEmiDocAdm').parents(':eq(4)').find('td').first().hide();
          jQuery('#tblDestEmiDocAdmOtro').parents(':eq(4)').find('td').first().hide();
          jQuery('#tblDestEmiDocAdmCiudadano').parents(':eq(4)').find('td').first().hide();
          jQuery('#tblDestEmiDocAdmPersJuri').parents(':eq(4)').find('td').first().hide();
          
          (jQuery('#deEmpRes').parent()).find('button').hide();
          if(jQuery('#deEmpRes').hasClass('inp-xs-grup')){
            jQuery('#deEmpRes').removeClass('inp-xs-grup');          
          }
         
          (jQuery('#deEmpEmi').parent()).find('button').hide();
          if(jQuery('#deEmpEmi').hasClass('inp-xs-grup')){
            jQuery('#deEmpEmi').removeClass('inp-xs-grup');          
          }            
        }        
}

function regresarEmitDocumAlta(pclickBtn) {
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
                        continueRegresarEmitDocumAlta();
                      }                      
                    },                          
                    NO: {
                      label: "NO",
                      className: "btn-primary"
                    }
                  }
                });
            }else{
                continueRegresarEmitDocumAlta();
            }
        }else{
            continueRegresarEmitDocumAlta();
        }
    }else{
      continueRegresarEmitDocumAlta();
    }
}

function continueRegresarEmitDocumAlta(){
    jQuery('#divEmiDocumentoAdmin').toggle();                                
    jQuery('#divNewEmiDocumAdmin').toggle(); 
    submitAjaxFormEmiDocAlta(jQuery('#buscarDocumentoEmiBean').find('#sTipoBusqueda').val());
    jQuery('#divNewEmiDocumAdmin').html("");        
    //mostrarOcultarDivBusqFiltro2();
}

function fu_changeEstadoDocEmiAdmAlta(pEstadoDoc) {
    var validaFiltro = fu_verificarCamposDocEmiAdm('1', '7');
    if (validaFiltro === "1") {
        //verificar si necesita grabar el documento.
        var rpta = fu_verificarChangeDocumentoEmiAdm();
        var nrpta = rpta.substr(0, 1);
        if (nrpta === "1") {
            alert_Warning("Emisión :", "Necesita grabar los cambios");
        } else {
            if (pEstadoDoc === "5" ) {//a proyecto
                bootbox.dialog({
                    message: " <h5>¿ Seguro de Guardar los Cambios ?</h5>",
                    buttons: {
                        SI: {
                            label: "SI",
                            className: "btn-primary",
                            callback: function() {
                                fn_changeToProyectoDocEmiAdmAlta();//cambiar a proyecto
                            }                        
                        },
                        NO: {
                            label: "NO",
                            className: "btn-default"
                        }
                    }
                });                   
            } else if (pEstadoDoc === "0") {//Emitir
                rpta = fn_validarEstadoDocEmiAdmAlta(pEstadoDoc);
                if (rpta === "1") {
                    fn_changeToEmitidoDocEmiAdmAlta();//cambiar a Emitido
                }
            }
        }

    }
    return false;
}

function fn_validarEstadoDocEmiAdmAlta(pEstadoDoc) {
    var vResult = "0";
    if (pEstadoDoc === "0") {
        var pesDocEmi = jQuery('#documentoEmiBean').find('#esDocEmi').val();
        if (pesDocEmi === "7" || pesDocEmi === "5" ) {
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

function fn_changeToEmitidoDocEmiAdmAlta() {
    ajaxCall("/srDocumentoAdmEmision.do?accion=goChangeToEmitidoAlta", $('#documentoEmiBean').serialize(), function(data) {
        fn_rptaChangeToEmitidoDocEmiAlta(data);
    },
            'json', false, false, "POST");
}

function fn_rptaChangeToEmitidoDocEmiAlta(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("0");
            //fn_seteaCamposDocumentoEmi();//resetear variables del documento
            fu_cargaEdicionDocAlta(jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmiAlta();
        } else {
            alert_Danger("Emisión :",data.deRespuesta );
        }
    }
}

function fn_grabarDocumentoEmiAdmAlta() {
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
                //verificar si necesita grabar el documento.
                rpta = fu_verificarChangeDocumentoEmiAdm();
                nrpta = rpta.substr(0, 1);
                if (nrpta === "1") {
                    fn_goGrabarDocumentoEmiAdmAlta();//grabar Documento
                } else {
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

function fn_goGrabarDocumentoEmiAdmAlta() {
    $('#documentoEmiBean').find('select').removeProp('disabled');
    var cadenaJson = fn_buildSendJsontoServerDocuEmi();
    //console.log(cadenaJson);
    var pcrearExpediente = "0";
    var pesnuevoDocEmiAdm = jQuery("#txtEsNuevoDocAdm").val();
    if (pesnuevoDocEmiAdm === "1" && jQuery("#nuAnnExp").val() === "" && jQuery("#nuSecExp").val() === "") {
        bootbox.dialog({
            message: " <h5>¿ Desea crear expediente para este documento ?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-primary",
                    callback: function() {
                        pcrearExpediente = "1";
                        fn_submitGrabarDocumentoEmiAdmAlta(cadenaJson,pcrearExpediente); 
                    }                        
                },
                NO: {
                    label: "NO",
                    className: "btn-default",
                    callback: function() {
                        fn_submitGrabarDocumentoEmiAdmAlta(cadenaJson,pcrearExpediente); 
                    }                      
                }
            }
        });         
    }else{
       fn_submitGrabarDocumentoEmiAdmAlta(cadenaJson,pcrearExpediente); 
    }
}

function fn_submitGrabarDocumentoEmiAdmAlta(cadenaJson,pcrearExpediente){
    jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
    ajaxCallSendJson("/srDocumentoAdmEmision.do?accion=goGrabaDocumentoEmi&pcrearExpediente=" + pcrearExpediente, cadenaJson, function(data) {
        fn_rptaGrabaDocumEmiAlta(data, pcrearExpediente);
    },
    'json', false, false, "POST");    
}

function fn_rptaGrabaDocumEmiAlta(data, sCrearExpediente) {
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
            fn_seteaCamposDocumentoEmi();            //resetear variables del documento
            fu_cargaEdicionDocAlta(jQuery('#esDocEmi').val());
            //alert("Datos Guardados.");
            alert_Sucess("Éxito!", "Documento grabado correctamente "+strCadena);
        } else {
            alert_Info("Emisión :", data.deRespuesta);
        }
    }
}


function fn_changeToProyectoDocEmiAdmAlta() {
    ajaxCall("/srDocumentoEmisionAlta.do?accion=goChangeToProyecto", $('#documentoEmiBean').serialize(), function(data) {
        fn_rptaChangeToProyectoDocEmiAlta(data);
    },
            'json', false, false, "POST");
}
function fn_rptaChangeToProyectoDocEmiAlta(data) {
    if (data !== null) {
        if (data.coRespuesta === "1") {
            jQuery('#documentoEmiBean').find('#esDocEmi').val("5");
            fn_seteaCamposDocumentoEmi();//resetear variables del documento
            fu_cargaEdicionDocAlta(jQuery('#documentoEmiBean').find('#esDocEmi').val());
            alert_Sucess("Éxito!", "Transacción completada.");
            jQuery('#documentoEmiBean').find("#nuSecuenciaFirma").val("");
            fn_cargaToolBarEmiAlta();            
        } else {
            alert_Danger("Emisión :",data.deRespuesta);
        }
    }
}

function fn_cargaDocEmiAlta() {
    var pnuAnn = jQuery('#documentoEmiBean').find('#nuAnn').val();
    var pnuEmi = jQuery('#documentoEmiBean').find('#nuEmi').val();
    //var rutactx = $("#rutactx").attr("value");
    var rutactx = pRutaContexto + "/" + pAppVersion;
    var contAux = 0;
    jQuery('#fileuploadAlta').fileupload({
        dataType: 'text',
        add: function(e, data) {
                var url = "";
                url = url.concat(rutactx, "/srDocumentoEmisionAlta.do?accion=goUploadEmi", "&pnuAnn=", pnuAnn, "&pnuEmi=", pnuEmi);
                data.url = url;
                data.submit();
        },
        done: function(e, data) {
            if (data.textStatus === "success"){
                alert_Sucess("GRABAR", "Documento cargado correctamente");
            } else {
                alert_Danger("ERROR", " al cargar el archivo");
            }
            $("#progressAlta").hide();
        },
        error: function (e, data) {
            alert_Danger("ERROR", " al cargar el archivo verifique tamaño");
            $("#progressAlta").hide();
        },        
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 80, 10);
            jQuery('#progressAlta .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#progressAlta span').html(progressText);
        }
    });
    jQuery('#progressAlta .progress-bar').css('width', '0%');
    jQuery("#progressAlta").show();
    jQuery('#fileuploadAlta').click();
    
}

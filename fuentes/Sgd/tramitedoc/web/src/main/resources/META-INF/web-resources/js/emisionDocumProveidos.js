/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function ejecutaOpcionModalProveido(thiss) {
    var lisSelect=[];
    
    lisSelect= Gob.Pcm.SGD.Presentacion.RecepDocumAdm.Index.Vista.Control.GrdResultado.GetSelectedData();
    
    
    if(lisSelect.length==0)
    {
           alert_Info("Recepción :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
    
    var pnuAnn = jQuery('#txtpnuAnn').val();
     if(pnuAnn !== ""){  
        var p = new Array();
            p[0] = "pnuAnn=" + pnuAnn;	
            p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();        
            p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
            p[3] = "pDepen=" + $(thiss).attr("codigo");
            ajaxCall('/srDocumentoAdmEmision.do?accion=goEditarDocumentoProveido', p.join("&"), function(data){     
               refreshScript("divNewEmiDocumAdmin", data);
            jQuery('#divEmiDocumentoAdmin').hide();
            jQuery('#divNewEmiDocumAdmin').show();
            jQuery('#divTablaEmiDocumenAdm').html("");
         
              refreshScript("divWorkPlaceRecepDocumAdmin", data);
                jQuery('#divRecepDocumentoAdmin').hide();
                jQuery('#divWorkPlaceRecepDocumAdmin').show();
//                fn_cargaToolBarRec();
                jQuery('#documentoEmiBean').find('#esDocEmi').val("7");
                   fn_cargaToolBarEmiProveido();
                var sEstadoDocAdm = jQuery('#documentoRecepBean').find('#esDocRec').val();
                fu_cargaEdicionDocAdm("01",sEstadoDocAdm);
            }, 'text', false, false, "POST");
     }
     else{
           bootbox.alert("Seleccione una fila de la lista");
        }  
}


function fn_preload()
{
    jQuery('#coTipDocAdm').val('232');
    jQuery('#coEmpEmi').val(jQuery('#coEmpRes').val());
    jQuery('#deEmpEmi').val(jQuery('#deEmpRes').val());
    //cargar destinatario
  //  fn_addDestintarioEmi();
     //fu_setProveedorDestEmi(jQuery('#txtpOficinaDestino').val(),jQuery('#txtpDesOficinaDestino').val());
    
}


function fu_setProveedorDestEmi(cod, desc) {
   // destinatarioDuplicado = true;
    var pfila = jQuery('#txtTblDestEmiFilaWhereButton').val();
    var pcol = jQuery('#txtTblDestEmiColWhereButton').val();
    console.log(pfila);
    console.log(pcol);
    var idTabla = "tblDestEmiDocAdm";
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(desc);
    $("#" + idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + (pcol * 1 + 1) + ")").text(cod);
    //removeDomId('windowConsultaDependenciaDestEmi');
    var pcoTipoDoc = jQuery('#coTipDocAdm').val();
    var p = new Array();
    p[0] = "accion=goBuscaEmpleadoLocaltblDestinatario";
    p[1] = "pcoDepen=" + cod;
    p[2] = "pcoTipoDoc=" + pcoTipoDoc;
    ajaxCall("/srDocumentoAdmEmision.do", p.join("&"), function(data) {
        if (data !== null && allTrim(data.coRespuesta) === "1") {
            
            var arrCampo = new Array();
            arrCampo[0] = (pcol * 1 + 1) + "=" + cod;//codDependencia
            arrCampo[1] = "5=" + data.coEmpleado;//codEmpleado
           // var bResult = fn_validaDestinatarioIntituDuplicado(idTabla, arrCampo, true, pfila*1);
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

                        break;
                    case 5:
                        var coEmp=data.coEmpleado;
                        if(coEmp!==null && coEmp!=="null" && allTrim(coEmp)!==""){
                            $(this).text(coEmp);
                        }
                        else{
                            bDestinOk=false;
                        }                          

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
          //  if (bResult) {
               // destinatarioDuplicado=false;
          //      if(bDestinOk){
          // //         fn_changeDestinatarioCorrecto(pfila);

           //     }

            //}
               
        } else {
           bootbox.alert(data.deRespuesta);
        }
    },
            'json', false, false, "POST");
}

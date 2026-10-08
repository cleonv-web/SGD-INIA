function fn_inicializaSeguiEmiDoc(sCoAnnio, fechaActual) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#esFiltroFecha').val("1");//rango fecha
    jQuery('#buscarDocumentoSeguiEstEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCoAnnio').find('option[value=""]').remove();
    pnumFilaSelect = 0;
    changeTipoBusqEmiDocuAdmSegui("0");
}
function changeTipoBusqEmiDocuAdmSegui(tipo) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormEmiDocAdmSegui(tipo);
    //mostrarOcultarDivBusqFiltro2SeguiEmi();
}
////Arriba ya cambio
function submitAjaxFormEmiDocAdmSegui(tipo) {
    var validaFiltro = fu_validaFiltroEmiDocAdmSeguiEst(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srSeguiEstEmi.do?accion=goInicio", $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
            refreshScript("divTblConsulDocumentoEmitido", data);
        }, 'text', false, false, "POST");
    } else if (validaFiltro === "2") {//buscar referencia
        ajaxCall("/srSeguiEstEmi.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferenciaRecepConsulEmi(data);
        }, 'text', false, false, "POST");
    }
    return false;
}
function fu_rptaBuscaDocumentoEnReferenciaRecepConsulEmi(data) {
    if (data !== null) {
        if (data === "0") {
            alert_Info("Buscar Referencia", "No Existe Documento.");
        } else {
            refreshScript("divTblConsulDocumentoEmitido", data);
        }
    }
}
function fu_validaFiltroEmiDocAdmSeguiEst(tipo) {
    var valRetorno = "1";

    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#feEmiIni').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#feEmiFin').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));


    var pEsIncluyeFiltro = jQuery('#buscarDocumentoSeguiEstEmiBean').find("esIncluyeFiltro1").is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if (tipo === "0") {
        valRetorno = fu_validaFiltroEmiDocAdmFiltrarSeguiEst(vFechaActual);
    } else if (tipo === "1") {
        //verificar si se ingreso datos en los campos de busqueda de referencia
        valRetorno = fu_validarBusquedaXReferenciaRecepSeguiEstEmi(tipo);
        if (valRetorno === "1") {
            valRetorno = fu_validaFiltroRecepDocAdmBuscarSeguiEstEmi();
            if (valRetorno === "1") {
                if (pEsIncluyeFiltro) {
                    valRetorno = fu_validaFiltroEmiDocAdmFiltrarSeguiEst(vFechaActual);
                } else {
                    valRetorno = setAnnioNoIncludeFiltroRecepSegui();
                }
            }
        }
    }
    return valRetorno;
}
function fu_validaFiltroRecepDocAdmBuscarSeguiEstEmi() {
    var valRetorno = "1";

    upperCaseSeguiBuscarEmiDocAdmBean();

    var vNroDocumento = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busAsunto').val();
    var vNumDocRef = allTrim(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDocRef').val());
    
    if(!!vAsunto){
        //validar caracteres especiales
        vAsunto=jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busAsunto').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vAsunto))).val();    
        if(allTrim(vAsunto).length >= 0 && allTrim(vAsunto).length <= 3){  //El texto es entre 1 y 3 caracteres
            alert_Warning("Buscar: ","Debe ingresar solo palabras con un mínimo de 4 caracteres en el asunto.");
            valRetorno = "0";
            return valRetorno;
        }         
    }
    
    if ((typeof (vNroDocumento) === "undefined" || vNroDocumento === null || vNroDocumento === "") &&
            (typeof (vNroExpediente) === "undefined" || vNroExpediente === null || vNroExpediente === "") &&
            (typeof (vNumDocRef) === "undefined" || vNumDocRef === null || vNumDocRef === "") &&
            (typeof (vAsunto) === "undefined" || vAsunto === null || vAsunto === "")) {
        //alert("Ingresar Algún parámetro de Búsqueda");
        alert_Warning("Buscar: ", "Ingresar Algún parámetro de Búsqueda");
        valRetorno = "0";
    }

    return valRetorno;
}
function upperCaseSeguiBuscarEmiDocAdmBean(){
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDoc').val(fu_getValorUpperCase(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDoc').val()));
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumExpediente').val(fu_getValorUpperCase(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumExpediente').val()));
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busAsunto').val(fu_getValorUpperCase(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busAsunto').val()));
}
function fu_validarBusquedaXReferenciaRecepSeguiEstEmi(tipo) {
    var valRetorno = "1";//no buscar por referencia
    if (tipo === "1") {
        var vBuscDestinatario = allTrim(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodDepEmiRef').val());
        var vDeTipoDocAdm = allTrim(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodTipoDocRef').val());
        var vCoAnnioBus = allTrim(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCoAnnio').val());
        var vNumDocRef = allTrim(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busNumDocRef').val());

        if ((typeof (vBuscDestinatario) !== "undefined" && vBuscDestinatario !== null && vBuscDestinatario !== "") &&
                (typeof (vDeTipoDocAdm) !== "undefined" && vDeTipoDocAdm !== null && vDeTipoDocAdm !== "") &&
                (typeof (vCoAnnioBus) !== "undefined" && vCoAnnioBus !== null && vCoAnnioBus !== "") &&
                (typeof (vNumDocRef) !== "undefined" && vNumDocRef !== null && vNumDocRef !== "")) {
            valRetorno = "1";//buscar por referencia
        }

        if (valRetorno === "1") {
            if (vNumDocRef !== "" && vNumDocRef !== null) {
                var vValidaNumero = fu_validaNumero(vNumDocRef);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("N° de Documento en referencia debe ser solo numeros.");
                    valRetorno = "0";
                }
            }
            else {
                valRetorno = "1";
            }
        }
    }
    return valRetorno;
}

function fu_obtenerEsFiltroFechaConsulSeguiEstEmi(nameForm) {
    var opt = jQuery('#' + nameForm).find('#coAnnio').parent('td').find('#fechaFiltro').attr('optselected');
    if (opt === "1" || opt === "2" || opt === "4" || opt === "8") {
        jQuery('#' + nameForm).find("#esFiltroFecha").val("1");
    } else if (opt === "5" || opt === "6") {
        jQuery('#' + nameForm).find("#esFiltroFecha").val("2");
    } else if (opt === "3" || opt === "7") {
        jQuery('#' + nameForm).find("#esFiltroFecha").val("3");
    }

}

function fu_validaFiltroEmiDocAdmFiltrarSeguiEst(vFechaActual) {
    var valRetorno = "1";
    if (valRetorno === "1") {
        fu_obtenerEsFiltroFechaConsulSeguiEstEmi('buscarDocumentoSeguiEstEmiBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoSeguiEstEmiBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if (pEsFiltroFecha === "1" || pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
            if (pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
                var pAnnio = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').val();
                if (pAnnio !== null && pAnnio !== "null" && typeof (pAnnio) !== "undefined" && pAnnio !== "") {
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }
                }
            }

            var vFeInicio = jQuery('#buscarDocumentoSeguiEstEmiBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoSeguiEstEmiBean').find("#feEmiFin").val();

            if (valRetorno === "1" /*&& pEsFiltroFecha==="1"*/) {
                var pAnnioBusq = verificarAñoBusquedaFiltroSeguiEmi(vFeInicio, vFeFinal);
                if (pAnnioBusq !== null && pAnnioBusq !== "null" && typeof (pAnnioBusq) !== "undefined" && pAnnioBusq !== "") {
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    } else if (vValidaNumero === "OK") {
                        jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').val(pAnnioBusq);
                    }
                }
            }

            if (pEsFiltroFecha === "1" /*|| pEsFiltroFecha==="3"*/) {
                //VALIDA FECHAS
                if (valRetorno === "1") {
                    if (vFeInicio === "") {
                       bootbox.alert('Debe ingresar Fecha Del');
                        valRetorno = "0";
                    } else {
                        valRetorno = fu_validaFechaConsulta(vFeInicio, vFechaActual);
                        if (valRetorno !== "1") {
                           bootbox.alert("Error en Fecha Del : " + valRetorno);
                            valRetorno = "0";
                        }
                    }
                }

                if (valRetorno === "1") {
                    //VALIDA FECHAS
                    if (vFeFinal === "") {
                       bootbox.alert('Debe ingresar Fecha Al');
                        valRetorno = "0";
                    } else {
                        if (pEsFiltroFecha === "3") {
                            vFechaActual = obtenerFechaUltimoDiaMes(vFechaActual);
                        }
                        valRetorno = fu_validaFechaConsulta(vFeFinal, vFechaActual);
                        if (valRetorno !== "1") {
                           bootbox.alert("Error en Fecha Al : " + valRetorno);
                            valRetorno = "0";
                        }
                    }
                }
                //se verifica que fechas DEL sea mayor o igual que fecha AL
                if (valRetorno === "1") {
                    var vCantidadDias = getNumeroDeDiasDiferencia(vFeInicio, vFeFinal);
                    if (vCantidadDias < 0) {
                       bootbox.alert("La Fecha Del debe ser mayor o igual a Fecha Al");
                        valRetorno = "0";
                    }
                }
            }
        }
    }
    return valRetorno;
}
function fu_cleanSeguiEmiDocAdm(tipo) {
    if (tipo === "1") {
        jQuery("#busNumDoc").val("");
        jQuery('#buscarDocumentoSeguiEstEmiBean').find("#busNumExpediente").val("");
        jQuery("#busAsunto").val("");
        jQuery("#busCodTipoDocRef option[value=]").prop("selected", "selected");
        jQuery("#feEmiIni").val("");
        jQuery("#feEmiFin").val("");
        jQuery("#busNumDocRef").val("");
        jQuery("#busCodDepEmiRef").val("");
        jQuery("#txtDepEmiteBus").val(" [TODOS]");
        jQuery("#esIncluyeFiltro1").prop('checked', false);
        jQuery("#esIncluyeFiltro1").attr('checked', false);
        jQuery("#busCoAnnio").find('option:first').prop("selected", "selected");
    } else {
        jQuery("#coRefOrigen").val("");
        jQuery("#txtRefOrigen").val("[TODOS]");
        jQuery("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery("#coVencimiento option[value=]").prop("selected", "selected");
        jQuery("#prioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#coTipoDocAdm option[value=]").prop("selected", "selected");
        jQuery("#coDepRemite").val("");
        jQuery("#txtRemitente").val("[TODOS]");
        jQuery("#coEmpDestino").val("");
        jQuery("#txtEmpDestino").val("[TODOS]");
        jQuery("#coEmpElaboro").val("");
        jQuery("#txtElaboradoPor").val("[TODOS]");
        jQuery("#coDepDestino").val("");
        jQuery("#txtDestinatario").val("[TODOS]");
//        jQuery("#coDepDestino").val(jQuery('#buscarDocumentoSeguiEstEmiBean').find("#coDependencia").val());
//        jQuery("#txtDestinatario").val(jQuery('#buscarDocumentoSeguiEstEmiBean').find("#deDependencia").val());
        jQuery("#coDepOrigen").val(jQuery('#buscarDocumentoSeguiEstEmiBean').find("#coDependencia").val());
        jQuery("#txtDepOrigen").val(jQuery('#buscarDocumentoSeguiEstEmiBean').find("#deDependencia").val());

        jQuery("#coExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("1");
        //var fechaActual = jQuery("#txtFechaActual").val();
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val()+"  Mes: "+monthYearArray[obtenerNroMesFecha(fechaActual) * 1]);
        //jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',obtenerFechaPrimerDiaMes(fechaActual));
        //jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);        
        jQuery("#coAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery('#buscarDocumentoSeguiEstEmiBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});
    }

}

function fnVerLeyendaVencimientoEmi() {
    var divLeyenda;
    $("#btnShowLeyendaVen")
            .mouseenter(function() {
                var pos = $(this).position();
                var zindex = $(this).zIndex();
                divLeyenda = $("#divLeyendaVen").clone().insertAfter(this).css({
                    position: "absolute",
                    top: (pos.top + 37) + "px",
                    left: (pos.left + 50) + "px",
                    "z-index": zindex + 1
                });

                divLeyenda.show();
            })
            .mouseleave(function() {
                if (!!divLeyenda) {
                    divLeyenda.remove();
                }
            });
}

function fn_construyeTablaVencimientoDetalleEmi() {
    len = function(obj) {
        var L = 0;
        $.each(obj, function(i, elem) {
            L++;
        });
        return L;
    }
    var orden = [5, 0, 4, 1, 3, 2];
    for (var j = 0; j < len(coloresVencimiento); j++)
    {
        var label = coloresVencimiento[orden[j]].text;
        var color = coloresVencimiento[orden[j]].color;
        var descrip = coloresVencimiento[orden[j]].descrip;
        var clase = coloresVencimiento[orden[j]].cssClass;
        var filaAux = Array();
        filaAux.push("<tr><td><div class='redondito " + clase + "'></div></td><td>");
        filaAux.push("<h4 style='margin-top: 3px; margin-bottom: 0px;'>" + label + "</h4>");
        filaAux.push("<p>" + descrip + "</p>");
        filaAux.push("</td></tr>");
        var fila = "";
        for (var i = 0; i < filaAux.length; i++) {
            fila = fila + filaAux[i];
        }
        $("#tablaDetVencimientoCirculos").append(fila);
        fila = [];
    }
}
function fn_construyeTablaVencimientoDetalleCuadradosEmi() {
    len = function(obj) {
        var L = 0;
        $.each(obj, function(i, elem) {
            L++;
        });
        return L;
    }
    var orden = [5, 0, 4, 1, 3, 2];
    for (var j = 0; j < len(coloresVencimiento); j++)
    {
        var label = coloresVencimiento[orden[j]].text;
        var color = coloresVencimiento[orden[j]].color;
        var descrip = coloresVencimiento[orden[j]].descrip;
        var clase = coloresVencimiento[orden[j]].cssClass;
        var filaAux = Array();
        filaAux.push("<tr><td><div class='cuadradoVencimiento " + clase + "'>" + label + "</div></td>");
        filaAux.push("<td>" + descrip + "</td>");
        filaAux.push("</tr>");
        var fila = "";
        for (var i = 0; i < filaAux.length; i++) {
            fila = fila + filaAux[i];
        }
        $("#tablaDetVencimientoCuadrados").append(fila);
        fila = [];
    }
}
////function fn_buscaDestinatarioFiltroConsulDocSeguiEmi() {
////    var p = new Array();
////    p[0] = "accion=goBuscaDestinatario";
////    //p[1] = "pnuAnn=" + jQuery('#coAnnio').val();    
////    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDependencia').val();
////    ajaxCall("/srSeguiEstRecep.do", p.join("&"), function(data) {
////        fn_rptaBuscaDestinatarioFiltroConsulDocSeguiEmi(data);
////    }, 'text', false, false, "POST");
////}
////function fn_rptaBuscaDestinatarioFiltroConsulDocSeguiEmi(XML_AJAX) {
////    if (XML_AJAX !== null) {
////        $("body").append(XML_AJAX);
////    }
////}
//function fu_setDatoDestinatarioFiltroSeguiDocEmi(cod, desc) {
//    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDestinatario').val(desc);
//    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepDestino').val(cod);
//    removeDomId('windowConsultaDest');
//    jQuery("#coEmpDestino").val("");
//    jQuery("#txtEmpDestino").val("[TODOS]");
//
//}
////function fn_buscaDestinatarioFiltroSeguiCodEmpDestEmi() {
////    var p = new Array();
////    p[0] = "accion=goBuscaEmpDestino";
////    //p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepDestino').val();
////    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepDestino').val();
////    ajaxCall("/srSeguiEstRecep.do", p.join("&"), function(data) {
////        fn_rptaBuscaDestinatarioFiltroSeguiDocEmi(data);
////    },
////            'text', false, false, "POST");
////}
////function fn_rptaBuscaDestinatarioFiltroSeguiDocEmi(XML_AJAX) {
////    if (XML_AJAX !== null) {
////        $("body").append(XML_AJAX);
////    }
////}
//function fu_setDatoEmpDestinoSeguiRecep(cod, desc) {
//    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtEmpDestino').val(desc);
//    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coEmpDestino').val(cod);
//    removeDomId('windowConsultaEmpDestino');
//}
//function fu_generarReporteEmiSeguiPDF() {
//    fu_generarReporteEmiSegui('PDF');
//}
///*function fu_generarReporteEmiSegui(pformatoReporte){
// var validaFiltro = fu_validaFiltroRecepDocAdmConsul("0");
// if (validaFiltro === "1" || validaFiltro === "2") {
// ajaxCall("/srSeguiEstRecep.do?accion=goRutaReporte&pformatRepor="+pformatoReporte, $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
// if (data!==null) {
// if(data.coRespuesta==="0"){
// if(!!data.noUrl&&!!data.noDoc){
// fn_generaDocApplet(data.noUrl,data.noDoc,function (data){
// var result = data;
// if (result!=="OK"){
//bootbox.alert(result);
// }                            
// });
// }
// }else{
//bootbox.alert(data.deRespuesta);
// }
// }else{
//bootbox.alert("La respuesta del servidor es nula.");
// }
// }, 'json', false, true, "POST");
// }
// return false;
// }*/
//function fu_generarReporteEmiSeguiXLS() {
//    fu_generarReporteEmiSegui('XLS');
//}
//function fu_generarReporteEmiSegui(pformatoReporte) {
////   bootbox.alert("falta implementar");
////    return;
//    var validaFiltro = fu_validaFiltroEmiDocAdmSeguiEst("0");
//    if (validaFiltro === "1") {
//        ajaxCall("/srSeguiEstRecep.do?accion=goRutaReporte&pformatRepor=" + pformatoReporte, $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
//            if (data !== null) {
//                if (data.coRespuesta === "0") {
//                    if (!!data.noUrl && !!data.noDoc) {
//
////                        var param = {urlDoc: data.noUrl, rutaDoc: data.noDoc, remplazaArchivo: true};
////                        runApplet(appletsTramiteDoc.generaDocumento, param, function(data) {
////                            if (data !== "OK") {
////                               bootbox.alert(data);
////                            }
////                        });
//                        fn_generaDocApplet(data.noUrl, data.noDoc, function(data) {
//                            var result = data;
//                            if (result !== "OK") {
//                               bootbox.alert(result);
//                            }
//                        });
//
//                    }
//                } else {
//                   bootbox.alert(data.deRespuesta);
//                }
//            } else {
//               bootbox.alert("La respuesta del servidor es nula.");
//            }
//        }, 'json', false, true, "POST");
//    }
//    return false;
//}
function mostrarOcultarDivBusqFiltroEmiSeguiEst(divMostrar) {
    if (divMostrar === "1") {
        if (jQuery('#divConfigFiltro').is(':visible')) {
            jQuery('#divConfigFiltro').hide();
            jQuery('#spanDivFiltro').removeClass("glyphicon-collapse-up");
            jQuery('#spanDivFiltro').addClass("glyphicon-collapse-down");
        } else {
            jQuery('#divConfigFiltro').show();
            jQuery('#spanDivFiltro').removeClass("glyphicon-collapse-down");
            jQuery('#spanDivFiltro').addClass("glyphicon-collapse-up");
        }
    } else if (divMostrar === "2") {
        if (jQuery('#divConfigBusqueda').is(':visible')) {
            jQuery('#divConfigBusqueda').hide();
//           jQuery('#spanDivBusqueda').removeClass("ui-icon-circle-arrow-n");
//           jQuery('#spanDivBusqueda').addClass("ui-icon-circle-arrow-s");           
            jQuery('#spanDivBusqueda').removeClass("glyphicon-collapse-up");
            jQuery('#spanDivBusqueda').addClass("glyphicon-collapse-down");
        } else {
            jQuery('#divConfigBusqueda').show();
            jQuery('#spanDivBusqueda').removeClass("glyphicon-collapse-down");
            jQuery('#spanDivBusqueda').addClass("glyphicon-collapse-up");
        }
    }
}
function mostrarOcultarDivBusqFiltro2SeguiEmi() {

    if (jQuery('#divConfigFiltro').is(':visible')) {
        jQuery('#divConfigFiltro').hide();
        jQuery('#spanDivFiltro').removeClass("ui-icon-circle-arrow-n");
        jQuery('#spanDivFiltro').addClass("ui-icon-circle-arrow-s");
    }
    if (jQuery('#divConfigBusqueda').is(':visible')) {
        jQuery('#divConfigBusqueda').hide();
        jQuery('#spanDivBusqueda').removeClass("ui-icon-circle-arrow-n");
        jQuery('#spanDivBusqueda').addClass("ui-icon-circle-arrow-s");
    }
}
function verificarAñoBusquedaFiltroSeguiEmi(vFeInicio, vFeFinal) {
    try {
        var vReturn = "A";
        var vAnioFini = vFeInicio.substr(6);
        var vAnioFfin = vFeFinal.substr(6);
        var lAnioFini = vAnioFini.length;
        var lAnioFfin = vAnioFfin.length;
        if (lAnioFfin === 4 && lAnioFini === 4) {
            if (vAnioFini === vAnioFfin) {
                vReturn = vAnioFini;
            } else {
                vReturn = "0";
            }
        }
    } catch (vReturn) {
    }
    return vReturn;
}
function pintarEstadosVencimientoSeguiEstEmi() {
    var col = 5;
    $("#myTableFixed tbody tr").each(function() {
        var codVencimiento = $(this).find("td:eq(" + col + ")").text();
        if (!!codVencimiento) {
            codVencimiento = parseInt(codVencimiento);
            var text = coloresVencimiento[codVencimiento].text;
            var color = coloresVencimiento[codVencimiento].color;
            var clase = coloresVencimiento[codVencimiento].cssClass;
            $(this).find("td:eq(" + col + ")").html(text);
            //$(this).find("td:eq(" + col + ")").css({"background-color": color, "color": "white"});
            $(this).find("td:eq(" + col + ")").attr("class", clase);
            var auxCol = col - 1;
            $(this).find("td:eq(" + auxCol + ")").attr("class", clase);
        } else {
            //seguramente no hay filas en la tabla no hay nada que pintar
        }
    });
}
function fn_buscaReferenciaOrigenConsulEmiSegui() {
    var p = new Array();
    p[0] = "accion=goBuscaReferenciaOrigen";
    p[1] = "pnuAnn=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').val();
    ajaxCall("/srSeguiEstEmi.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenConsulEmiSegui(data);
    },
            'text', false, false, "POST");
}
function fn_rptaBuscaReferenciaOrigenConsulEmiSegui(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}
function fn_iniConsRefOriDocEmiConsulSeguiEstEmi() {
    var tableaux = $('#tlbReferenOrig');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbReferenOrig tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbReferenOrig tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoReferenOrigenConsulSeguiEstEmi(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbReferenOrig tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoReferenOrigenConsulSeguiEstEmi(pcodDest, pdesDest);
    });
}
function fu_setDatoReferenOrigenConsulSeguiEstEmi(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtRefOrigen').val(desc);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coRefOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
}
function fn_buscaElaboradoPorConsulSeguiEstEmi() {
    var coDepOrigen = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepOrigen').val();
    ajaxCall("/srSeguiEstEmi.do?accion=goBuscaElaboradoPor&pcoDep=" + coDepOrigen, '', function(data) {
        fn_rptaBuscaElaboradoPorConsul(data);
    },
            'text', false, false, "POST");
}
function fu_setDatoElaboradoPorConsulSeguiEstEmi(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtElaboradoPor').val(desc);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coEmpElaboro').val(cod);
    removeDomId('windowConsultaElaboradoPor');
}
function fn_buscaDependenciaOrigenConsulEmiSeguiEst() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaOrigen";
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDependencia').val();
    ajaxCall("/srSeguiEstEmi.do", p.join("&"), function(data) {
        fn_rptaBuscaReferenciaOrigenConsulSeguiEstEmi(data);
    },
            'text', false, false, "POST");
}
function fn_rptaBuscaReferenciaOrigenConsulSeguiEstEmi(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}
function fu_setDatoDependenciaOrigenConsulSeguiEstEmi(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDepOrigen').val(desc);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepOrigen').val(cod);
    removeDomId('windowConsultaRefOri');
    jQuery("#coEmpElaboro").val("");
    jQuery("#txtElaboradoPor").val("[TODOS]");
}
function fn_buscaDestinatarioEmiConsulSeguiEst() {
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    p[1] = "pnuAnn=" + jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coAnnio').val();
    ajaxCall("/srSeguiEstEmi.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioEmiConsul(data);
    },
            'text', false, false, "POST");
}
function fn_iniConsDestEmiConsulSeguiEst() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDestinatarioEmiConsulSeguiEst(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoDestinatarioEmiConsulSeguiEst(pcodDest, pdesDest);
    });
}
function fu_setDatoDestinatarioEmiConsulSeguiEst(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDestinatario').val(desc);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDestinoEmi');
    jQuery("#coEmpDestino").val("");
    jQuery("#txtEmpDestino").val("[TODOS]");
}
function fn_iniConsDestEmiDocExtConsulSeguiEst() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDestinatarioEmiDocExtConsulSeguiEst(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoDestinatarioEmiDocExtConsulSeguiEst(pcodDest, pdesDest);
    });
}
function fu_setDatoDestinatarioEmiDocExtConsulSeguiEst(cod, desc) {
    var noForm = '#buscarDocumentoSeguiEstEmiBean';
    jQuery(noForm).find('#txtDestinatario').val(desc);
    jQuery(noForm).find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}
function fn_iniConsDestDocPendEntregaSeguiEstEmi() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDestinatarioDocPendEntregaSeguiEstEmi(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoDestinatarioDocPendEntregaSeguiEstEmi(pcodDest, pdesDest);
    });
}
function fu_setDatoDestinatarioDocPendEntregaSeguiEstEmi(cod, desc) {
    var noForm = '#buscarDocumentoSeguiEstEmiBean';
    jQuery(noForm).find('#txtDependenciaDes').val(desc);
    jQuery(noForm).find('#coDepDes').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}
function fn_iniConsDestCargosGeneradosSeguiEstEmi() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDestinatarioCargosGeneradosSeguiEstEmi(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoDestinatarioCargosGeneradosSeguiEstEmi(pcodDest, pdesDest);
    });
}
function fu_setDatoDestinatarioCargosGeneradosSeguiEstEmi(cod, desc) {
    var noForm = '#buscarDocumentoSeguiEstEmiBean';
    jQuery(noForm).find('#txtDependencia').val(desc);
    jQuery(noForm).find('#coDepDes').val(cod);
    removeDomId('windowConsultaDestinoEmi');
}
function fn_iniConsDestDocEmiPersSeguiEst() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function(index, row) {
        if (index == 0) {
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
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function(index, td) {
                    var regExp = new RegExp(value, 'i');
                    if (regExp.test($(td).text())) {
                        found = true;
                        return false;
                    }
                });
                if (found == true) {
                    $(row).show();
                    if (!isFirst) {
                        $(this).addClass('row_selected');
                        isFirst = true;
                        indexSelect = index;
                    }
                }
                else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDestinatarioDocEmiPersConsulSeguiEst(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function(e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        fu_setDatoDestinatarioDocEmiPersConsulSeguiEst(pcodDest, pdesDest);
    });
}
function fu_setDatoDestinatarioDocEmiPersConsulSeguiEst(pcodDest, pdesDest) {
    var noForm = '#buscarDocumentoSeguiEstEmiBean';
    jQuery(noForm).find('#txtDependenciaDes').val(pdesDest);
    jQuery(noForm).find('#coDepDestino').val(pcodDest);
    removeDomId('windowConsultaDestinoEmi');
}
function fu_eventoTablaRecepDocAdmSeguiEstEmi() {
    var oTable;
    oTable = $('#myTableFixed').dataTable({
        "bPaginate": false,
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
            {"bSortable": false},
            {"bSortable": true},
            {"sType": "fecha"},
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
                if (index === 6 || index === 7 || index === 12 || index === 13) {
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
        }
        else {
            oTable.$('tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');

            /*obtiene datos*/
            if (typeof ($(this).children('td')[12]) !== "undefined") {
                jQuery('#txtpnuAnn').val($(this).children('td')[1].innerHTML);
                jQuery('#txtpnuEmi').val($(this).children('td')[2].innerHTML);
                jQuery('#txtpnuDes').val($(this).children('td')[3].innerHTML);
                pnumFilaSelect = $(this).index();
            }
        }
    });
    $("#myTableFixed tbody tr").dblclick(function() {
        //jQuery('#txtTextIndexSelect').val("-1"); 
    });
}
function fn_buscaDependenciaEmiSeguiEst() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaEmite";
    p[1] = jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDepEmiteBus').val() === ' [TODOS]' ? "pdeDepEmite=" : "pdeDepEmite=" + fu_getValorUpperCase(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDepEmiteBus').val());
    ajaxCall("/srSeguiEstEmi.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaEmiSeguiEst(data);
    },
    'text', false, false, "POST");
}
function fn_rptaBuscaDependenciaEmiSeguiEst(XML_AJAX) {
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}
        if(obj){
            if(obj.coRespuesta==="1"){
                jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodDepEmiRef').val(obj.coDependencia);
                jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDepEmiteBus').val(obj.deDependencia);
                jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodTipoDocRef').focus();
            }else{
               bootbox.alert(obj.deRespuesta);
            }
        }else{
            $("body").append(XML_AJAX);
        }        
    }    
}
function editarDocumentoEmiSeguiEst() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    if (pnuAnn !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        ajaxCall("/srSeguiEstEmi.do?accion=goDetalleDocumento", p.join("&"), function(data) {
            refreshScript("divNewRecepDocumAdminConsul", data);
            jQuery('#divTblConsulDocumentoRecepcionado').html("");
            jQuery('#divEmiDocumentoAdminConsul').hide();
            jQuery('#divNewRecepDocumAdminConsul').show();
            //fn_cargaToolBarRec();
            //var sEstadoDocAdm = jQuery('#documentoRecepBean').find('#esDocRec').val();
            //fu_cargaEdicionDocAdm("01",sEstadoDocAdm);
        }, 'text', false, false, "POST");
    } else {
        alert_Info("Recepción :", "Seleccione una fila de la lista");
    }
}
function fn_verAnexoEmiSeguiEst(nomForm) {
    var pnuAnn = jQuery('#'+nomForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+nomForm).find('#nuEmi').val();
    var pnuDes = jQuery('#'+nomForm).find('#nuDes').val();
    if (!!pnuAnn && !!pnuEmi) {
        fn_verAnexosObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        alert_Info("Emisión :", "Seleccione fila.");
    }
}
function fn_verSeguimientoEmiSeguiEst(nomForm) {
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
function fn_verDocumentoEmiSeguiEst(nomForm) {
    var pnuAnn = jQuery('#'+nomForm).find('#nuAnn').val();
    var pnuEmi = jQuery('#'+nomForm).find('#nuEmi').val();
    var ptiOpe = "0";

    if (!!pnuAnn && !!pnuEmi) {
        fn_verDocumentosObj(pnuAnn, pnuEmi, ptiOpe);
    } else {
        alert_Info("Emisión :", "Seleccione fila.");
    }

}
function fu_generarReporteEmiSeguiEstPDF() {
    fu_generarReporteEmiSeguiEst('PDF');
}
function fu_generarReporteEmiSeguiEstXLS() {
    fu_generarReporteEmiSeguiEst('XLS');
}
function fu_generarReporteEmiSeguiEst(pformatoReporte) {

    var validaFiltro = fu_validaFiltroEmiDocAdmSeguiEst("0");
    if (validaFiltro === "1") {
        //ajaxCall("/srSeguiEstEmi.do?accion=goRutaReporte&pformatRepor=" + pformatoReporte, $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
        ajaxCall("/srSeguiEstEmi.do?accion=goExportarArchivoLista&pformatRepor=" + pformatoReporte, $('#buscarDocumentoSeguiEstEmiBean').serialize(), function(data) {
            if (data !== null) {
                if (data.coRespuesta === "0") {
                    if (!!data.noUrl && !!data.noDoc) {

                        //fn_generaDocApplet(data.noUrl, data.noDoc, function(data) {
                        fn_generaDocDesktop(data.noUrl, data.noDoc, function(data) {
                            var result = data;
                            if (result !== "OK") {
                               bootbox.alert(result);
                            }
                        });

                    }
                } else {
                   bootbox.alert(data.deRespuesta);
                }
            } else {
               bootbox.alert("La respuesta del servidor es nula.");
            }
        }, 'json', false, true, "POST");
    }
    return false;
}
function regresarEmiDocumAdmSeguiEst() {
    jQuery('#divNewRecepDocumAdminConsul').toggle();
    jQuery('#divEmiDocumentoAdminConsul').toggle();
    submitAjaxFormEmiDocAdmSegui(jQuery('#buscarDocumentoSeguiEstEmiBean').find('#tipoBusqueda').val());
    jQuery('#divNewRecepDocumAdminConsul').html("");
}
function fn_verSeguimientoEstEmi() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    var pnuEmi = jQuery('#txtpnuEmi').val();
    var pnuDes = jQuery('#txtpnuDes').val();
    if (pnuAnn) {
        fn_verSeguimientoObj(pnuAnn, pnuEmi, pnuDes);
    } else {
        //alert("Seleccione una fila de la lista");
        alert_Info("Emisión :", "Seleccione una fila de la lista");
    }
}
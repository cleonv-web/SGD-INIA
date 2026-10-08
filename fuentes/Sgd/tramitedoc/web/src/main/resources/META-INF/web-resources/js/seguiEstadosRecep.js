function fn_inicializaSeguiRecepDoc(sCoAnnio, fechaActual) {
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#esFiltroFecha').val("1");//rango fecha
    jQuery('#buscarDocumentoSeguiEstRecBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumDocRef').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumDoc').change(function() {
        $(this).val(replicate($(this).val(), 6));
    });
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCoAnnio').find('option[value=""]').remove();
    pnumFilaSelect = 0;
    changeTipoBusqRecepDocuAdmSegui("0");
}
function changeTipoBusqRecepDocuAdmSegui(tipo) {
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#tipoBusqueda').val(tipo);
    submitAjaxFormRecepDocAdmSegui(tipo);
    //mostrarOcultarDivBusqFiltro2();
}
function submitAjaxFormRecepDocAdmSegui(tipo) {
    var validaFiltro = fu_validaFiltroRecepDocAdmSegui(tipo);
    if (validaFiltro === "1") {
        ajaxCall("/srSeguiEstRecep.do?accion=goInicio", $('#buscarDocumentoSeguiEstRecBean').serialize(), function(data) {
            refreshScript("divTblConsulDocumentoRecepcionado", data);
        }, 'text', false, false, "POST");
    } else if (validaFiltro === "2") {//buscar referencia
        ajaxCall("/srSeguiEstRecep.do?accion=goBuscaDocumentoEnReferencia", $('#buscarDocumentoSeguiEstRecBean').serialize(), function(data) {
            fu_rptaBuscaDocumentoEnReferenciaRecepConsul(data);
        }, 'text', false, false, "POST");
    }
    return false;
}
function fu_validaFiltroRecepDocAdmSegui(tipo) {
    var valRetorno = "1";

    jQuery('#buscarDocumentoSeguiEstRecBean').find('#feEmiIni').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('fini'));
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#feEmiFin').val(jQuery('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin'));


    var pEsIncluyeFiltro = jQuery('#buscarDocumentoSeguiEstRecBean').find("esIncluyeFiltro1").is(':checked');
    //var pEsFiltroFecha = jQuery("#esFiltroFecha").val();
    var vFechaActual = jQuery('#txtFechaActual').val();
    if (tipo === "0") {
        valRetorno = fu_validaFiltroRecepDocAdmFiltrarSegui(vFechaActual);
    } else if (tipo === "1") {
        //verificar si se ingreso datos en los campos de busqueda de referencia
        valRetorno = fu_validarBusquedaXReferenciaRecepSegui(tipo);
        if (valRetorno === "1") {
            valRetorno = fu_validaFiltroRecepDocAdmBuscarSegui();
            if (valRetorno === "1") {
                if (pEsIncluyeFiltro) {
                    valRetorno = fu_validaFiltroRecepDocAdmFiltrarSegui(vFechaActual);
                } else {
                    valRetorno = setAnnioNoIncludeFiltroRecepSegui();
                }
            }
        }
    }
    return valRetorno;
}
function fu_validaFiltroRecepDocAdmFiltrarSegui(vFechaActual) {
    var valRetorno = "1";
    if (valRetorno === "1") {
        fu_obtenerEsFiltroFechaConsul('buscarDocumentoSeguiEstRecBean');
        var pEsFiltroFecha = jQuery('#buscarDocumentoSeguiEstRecBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
        if (pEsFiltroFecha === "1" || pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
            if (pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
                var pAnnio = jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').val();
                if (pAnnio !== null && pAnnio !== "null" && typeof (pAnnio) !== "undefined" && pAnnio !== "") {
                    var vValidaNumero = fu_validaNumero(pAnnio);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }
                }
            }

            var vFeInicio = jQuery('#buscarDocumentoSeguiEstRecBean').find("#feEmiIni").val();
            var vFeFinal = jQuery('#buscarDocumentoSeguiEstRecBean').find("#feEmiFin").val();

            if (valRetorno === "1" /*&& pEsFiltroFecha==="1"*/) {
                var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio, vFeFinal);
                if (pAnnioBusq !== null && pAnnioBusq !== "null" && typeof (pAnnioBusq) !== "undefined" && pAnnioBusq !== "") {
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    } else if (vValidaNumero === "OK") {
                        jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').val(pAnnioBusq);
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
function fu_validarBusquedaXReferenciaRecepSegui(tipo) {
    var valRetorno = "1";//no buscar por referencia
    if (tipo === "1") {
        var vBuscDestinatario = allTrim(jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodDepEmiRef').val());
        var vDeTipoDocAdm = allTrim(jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodTipoDocRef').val());
        var vCoAnnioBus = allTrim(jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCoAnnio').val());
        var vNumDocRef = allTrim(jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumDocRef').val());

        if ((typeof (vBuscDestinatario) !== "undefined" && vBuscDestinatario !== null && vBuscDestinatario !== "") &&
                (typeof (vDeTipoDocAdm) !== "undefined" && vDeTipoDocAdm !== null && vDeTipoDocAdm !== "") &&
                (typeof (vCoAnnioBus) !== "undefined" && vCoAnnioBus !== null && vCoAnnioBus !== "") &&
                (typeof (vNumDocRef) !== "undefined" && vNumDocRef !== null && vNumDocRef !== "")) {
            valRetorno = "2";//buscar por referencia
        }

        if (valRetorno === "2") {
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
function fu_validaFiltroRecepDocAdmBuscarSegui() {
    var valRetorno = "1";

    upperCaseConsulBuscarRecepDocAdmBean();

    var vNroDocumento = jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumDoc').val();
    var vNroExpediente = jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumExpediente').val();
    var vAsunto = jQuery('#buscarDocumentoSeguiEstRecBean').find('#busAsunto').val();
    var vNumDocRef = allTrim(jQuery('#buscarDocumentoSeguiEstRecBean').find('#busNumDocRef').val());
    if(!!vAsunto){
        //validar caracteres especiales
        vAsunto=jQuery('#buscarDocumentoSeguiEstRecBean').find('#busAsunto').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg(vAsunto))).val();    
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
function setAnnioNoIncludeFiltroRecepSegui() {
    var valRetorno = "1";
    fu_obtenerEsFiltroFechaConsul('buscarDocumentoSeguiEstRecBean');
    var pEsFiltroFecha = jQuery('#buscarDocumentoSeguiEstRecBean').find("#esFiltroFecha").val();//"2" solo año ,"3" año mes,"1" rango fechas
    if (pEsFiltroFecha === "1" || pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
        if (pEsFiltroFecha === "2" || pEsFiltroFecha === "3") {
            var pAnnio = jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').val();
            if (pAnnio !== null && pAnnio !== "null" && typeof (pAnnio) !== "undefined" && pAnnio !== "") {
                var vValidaNumero = fu_validaNumero(pAnnio);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                }
            }
        }

        var vFeInicio = jQuery('#buscarDocumentoSeguiEstRecBean').find("#feEmiIni").val();
        var vFeFinal = jQuery('#buscarDocumentoSeguiEstRecBean').find("#feEmiFin").val();

        if (valRetorno === "1" /*&& pEsFiltroFecha==="1"*/) {
            var pAnnioBusq = obtenerAnioBusqueda(vFeInicio, vFeFinal);
            if (pAnnioBusq !== null && pAnnioBusq !== "null" && typeof (pAnnioBusq) !== "undefined" && pAnnioBusq !== "") {
                var vValidaNumero = fu_validaNumero(pAnnioBusq);
                if (vValidaNumero !== "OK") {
                   bootbox.alert("Año debe ser solo números.");
                    valRetorno = "0";
                } else if (vValidaNumero === "OK") {
                    jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').val(pAnnioBusq);
                }
            }
        }
    }
    return valRetorno;
}
function fu_cleanSeguiRecepDocAdm(tipo) {
    if (tipo === "1") {
        jQuery("#busNumDoc").val("");
        jQuery('#buscarDocumentoSeguiEstRecBean').find("#busNumExpediente").val("");
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
        jQuery("#estadoDoc option[value=]").prop("selected", "selected");
        jQuery("#coVencimiento option[value=]").prop("selected", "selected");
        jQuery("#prioridadDoc option[value=]").prop("selected", "selected");
        jQuery("#coTipoDocAdm option[value=]").prop("selected", "selected");
        jQuery("#coDepRemite").val("");
        jQuery("#txtRemitente").val("[TODOS]");
        jQuery("#coEmpDestino").val("");
        jQuery("#txtEmpDestino").val("[TODOS]");
        jQuery("#coDepDestino").val(jQuery('#buscarDocumentoSeguiEstRecBean').find("#coDependencia").val());
        jQuery("#txtDestinatario").val(jQuery('#buscarDocumentoSeguiEstRecBean').find("#deDependencia").val());
        jQuery("#coExpediente option[value=]").prop("selected", "selected");
        jQuery("#esFiltroFecha").val("1");
        //var fechaActual = jQuery("#txtFechaActual").val();
        //jQuery("#fechaFiltro").html("Año: "+jQuery("#txtAnnioActual").val()+"  Mes: "+monthYearArray[obtenerNroMesFecha(fechaActual) * 1]);
        //jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('fini',obtenerFechaPrimerDiaMes(fechaActual));
        //jQuery('#buscarDocumentoSeguiEstRecBean').find('#coAnnio').parent('td').find('#fechaFiltro').attr('ffin',fechaActual);        
        jQuery("#coAnnio").val(jQuery("#txtAnnioActual").val());
        jQuery('#buscarDocumentoSeguiEstRecBean').find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 4});
    }

}
function editarDocumentoRecepSegui() {
    var pnuAnn = jQuery('#txtpnuAnn').val();
    if (pnuAnn !== "") {
        var p = new Array();
        p[0] = "pnuAnn=" + pnuAnn;
        p[1] = "pnuEmi=" + jQuery('#txtpnuEmi').val();
        p[2] = "pnuDes=" + jQuery('#txtpnuDes').val();
        ajaxCall("/srSeguiEstRecep.do?accion=goDetalleDocumento", p.join("&"), function(data) {
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
function regresarRecepDocumAdmSegui() {
    jQuery('#divNewRecepDocumAdminConsul').toggle();
    jQuery('#divEmiDocumentoAdminConsul').toggle();
    submitAjaxFormRecepDocAdmSegui(jQuery('#buscarDocumentoSeguiEstRecBean').find('#tipoBusqueda').val());
    jQuery('#divNewRecepDocumAdminConsul').html("");
}
function fu_eventoTablaRecepDocAdmSegui() {
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
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
            {"bSortable": true},
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
    
    /* YUAL
    $("#myTableFixed tbody td").hover(
            function() {
                $(this).attr('id', 'divtitlemostrar');
                var index = $(this).index();
                if (index === 6 || index === 11 || index === 12) {
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
function pintarEstadosVencimiento() {
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
function fnVerLeyendaVencimiento() {
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

function fn_construyeTablaVencimientoDetalle() {
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
function fn_construyeTablaVencimientoDetalleCuadrados() {
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
function fn_buscaDestinatarioFiltroConsulDocSegui() {
    var p = new Array();
    p[0] = "accion=goBuscaDestinatario";
    //p[1] = "pnuAnn=" + jQuery('#coAnnio').val();    
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstRecBean').find('#coDependencia').val();
    ajaxCall("/srSeguiEstRecep.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioFiltroConsulDocSegui(data);
    }, 'text', false, false, "POST");
}
function fn_rptaBuscaDestinatarioFiltroConsulDocSegui(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}
function fu_setDatoDestinatarioFiltroSeguiDocRecep(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#txtDestinatario').val(desc);
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#coDepDestino').val(cod);
    removeDomId('windowConsultaDest');
    jQuery("#coEmpDestino").val("");
    jQuery("#txtEmpDestino").val("[TODOS]");

}
function fn_buscaDestinatarioFiltroSeguiCodEmpDest() {
    var p = new Array();
    p[0] = "accion=goBuscaEmpDestino";
    //p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstRecBean').find('#coDepDestino').val();
    p[1] = "pcoDepen=" + jQuery('#buscarDocumentoSeguiEstRecBean').find('#coDepDestino').val();
    ajaxCall("/srSeguiEstRecep.do", p.join("&"), function(data) {
        fn_rptaBuscaDestinatarioFiltroSeguiDocRecep(data);
    },
            'text', false, false, "POST");
}
function fn_rptaBuscaDestinatarioFiltroSeguiDocRecep(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}
function fu_setDatoEmpDestinoSeguiRecep(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#txtEmpDestino').val(desc);
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#coEmpDestino').val(cod);
    removeDomId('windowConsultaEmpDestino');
}
function fu_generarReporteRecepSeguiPDF() {
    fu_generarReporteRecepSegui('PDF');
}

function fu_generarReporteRecepSeguiXLS() {
    fu_generarReporteRecepSegui('XLS');
}
function fu_generarReporteRecepSegui(pformatoReporte) {

    var validaFiltro = fu_validaFiltroRecepDocAdmSegui("0");
    if (validaFiltro === "1") {
        //ajaxCall("/srSeguiEstRecep.do?accion=goRutaReporte&pformatRepor=" + pformatoReporte, $('#buscarDocumentoSeguiEstRecBean').serialize(), function(data) {
        ajaxCall("/srSeguiEstRecep.do?accion=goExportarArchivoLista&pformatRepor=" + pformatoReporte, $('#buscarDocumentoSeguiEstRecBean').serialize(), function(data) {
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
function fu_setDatoDependenciaEmiSeguiEst(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#txtDepEmiteBus').val(desc);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodDepEmiRef').val(cod);
    jQuery('#buscarDocumentoSeguiEstEmiBean').find('#busCodTipoDocRef').focus();
    removeDomId('windowConsultaDestinoEmi');
}
function fn_buscaDependenciaRecepSeguiEst() {
    var p = new Array();
    p[0] = "accion=goBuscaDependenciaEmite";
    p[1] = jQuery('#buscarDocumentoSeguiEstRecBean').find('#txtDepEmiteBus').val() === ' [TODOS]' ? "pdeDepEmite=" : "pdeDepEmite=" + fu_getValorUpperCase(jQuery('#buscarDocumentoRecepConsulBean').find('#txtDepEmiteBus').val());
    ajaxCall("/srSeguiEstRecep.do", p.join("&"), function(data) {
        fn_rptaBuscaDependenciaRecepSeguiEst(data);
    },
    'text', false, false, "POST");
}
function fn_rptaBuscaDependenciaRecepSeguiEst(XML_AJAX) {
    if (XML_AJAX !== null) {
        var obj = null;
        try{obj = jQuery.parseJSON(XML_AJAX);}catch(e){}
        if(obj){
            if(obj.coRespuesta==="1"){
                jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodDepEmiRef').val(obj.coDependencia);
                jQuery('#buscarDocumentoSeguiEstRecBean').find('#txtDepEmiteBus').val(obj.deDependencia);
                jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodTipoDocRef').focus();
            }else{
               bootbox.alert(obj.deRespuesta);
            }
        }else{
            $("body").append(XML_AJAX);
        }        
    }      
}
function fu_setDatoDependenciaRecepSeguiEst(cod, desc) {
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#txtDepEmiteBus').val(desc);
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodDepEmiRef').val(cod);
    jQuery('#buscarDocumentoSeguiEstRecBean').find('#busCodTipoDocRef').focus();
    removeDomId('windowConsultaDestinoEmi');
}
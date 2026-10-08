/*Grupos destinos*/
var reemDoc = false;
var reemMot = false;
var jsonDetGrupo = [];
var GDindexCurrentSelectedCell;
var nuevoGrupo = false;
var cambioDatos = false;
/*Ubigeo*/
var paramConfUbigeo = {
    "bPaginate": false,
    "bFilter": false,
    "bSort": true,
    "bInfo": true,
    "bAutoWidth": false,
    "sScrollY": "300px",
    "bScrollCollapse": false,
    "oLanguage": {
        "sZeroRecords": "No se encuentran registros.",
        "sInfo": "Registros: _TOTAL_ ",
        "sInfoEmpty": ""
    },
    "aoColumns": [
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true}
    ]
};
var paramConfUbigeoEmi = {
    "bPaginate": false,
    "bFilter": false,
    "bSort": true,
    "bInfo": true,
    "bAutoWidth": false,
    "sScrollY": "300px",
    "bScrollCollapse": false,
    "oLanguage": {
        "sZeroRecords": "No se encuentran registros.",
        "sInfo": "Registros: _TOTAL_ ",
        "sInfoEmpty": ""
    },
    "aoColumns": [
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true},
        {"bSortable": true}
    ]
};
function fn_cargarDocumentosDependencia() {
    var p = new Array();
    p[0] = "accion=goListaDocsDependencia";
    p[1] = "vCodDependencia=" + jQuery('#codDepencia').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleDocsDependencia", data);
    }, 'text', false, false, "POST");
}

//YUAL
function fn_cargarDetalleTablaMaestra() {
    var p = new Array();
    p[0] = "accion=goListaDetalleTablaMaestra";
    p[1] = "vTabla=" + jQuery('#lsTablaMaestra').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleTablaMaestra", data);
    }, 'text', false, false, "POST");
}

function fn_eventDocDependencia() {
    var indexFilaClick = -1;
    $("#tblDocDepen tbody tr").click(function() {
        if (indexFilaClick !== -1) {
            if ($("#tblDocDepen tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
                $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
                $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }
        else {
            $(this).addClass('row_selected');
            indexFilaClick = $(this).index();
            var codDoc = $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") button").attr("codDoc");
            $("#codDoc").attr("value", codDoc);

            $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
            $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
            $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            $("#tblDocDepen tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
        }
        return false;
    });
}
function fn_cambiarDoc(codDoc) {
    reemDoc = true;
    $("#codDocReempl").attr("value", codDoc);
    fn_cargarNuevoDocDepen();

}
function fn_eliminarDocDepen() {
    var codDoc = $("#codDoc").attr("value");
    
    bootbox.confirm({
        message: "¿Eliminar documento?",
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
                var jsonBody = {"coDep": jQuery('#codDepencia').val(), "coTipDoc": codDoc};
                var jsonString = JSON.stringify(jsonBody);
                var url = "/srTablaConfiguracion.do?accion=goEliDocDependencia";
                ajaxCallSendJson(url, jsonString, function(data) {
                    if (data === "Datos eliminados.") {
                        alert_Sucess("MENSAJE:", data);
                        fn_cargarDocumentosDependencia();
                    } else {
                        alert_Danger("ERROR:", data);
                    }
                }, 'text', false, false, "POST");
            }else{
                
            }
        }
    });
    
    return;
}
function fn_cargarNuevoDocDepen() {
    var p = new Array();
    p[0] = "accion=goCargarDocumentoDep";
    p[1] = "vCodDependencia=" + jQuery('#codDepencia').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null)
        {
            $("body").append(data);
        }
    }, 'text', false, false, "POST");
    return false;
}
function fn_agregarDocumentoDepen(codDoc) {
    if (!!codDoc) {
        if (reemDoc === false) {
            var jsonBody = {"coDep": jQuery('#codDepencia').val(), "coTipDoc": codDoc};
            var jsonString = JSON.stringify(jsonBody);
            var url = "/srTablaConfiguracion.do?accion=goAgregarDocsDependencia";
            ajaxCallSendJson(url, jsonString, function(data) {
                if (data === "Datos guardados.") {
                    removeDomId('windowConsultaDocumentoDependencia');
                    fn_cargarDocumentosDependencia();
                } else {
                    alert_Danger("ERROR:", data);
                }
            }, 'text', false, false, "POST");
        } else {
            var codDocReempl = $("#codDocReempl").attr("value");
            reemDoc = false;
            var jsonBody = {"coDep": jQuery('#codDepencia').val(), "coTipDoc": codDoc};
            var jsonString = JSON.stringify(jsonBody);
            var url = "/srTablaConfiguracion.do?accion=goUpdDocsDependencia&codDocReempl=" + codDocReempl;
            ajaxCallSendJson(url, jsonString, function(data) {
                if (data === "Datos guardados.") {
                    alert_Sucess("MENSAJE:", data);
                    removeDomId('windowConsultaDocumentoDependencia');
                    fn_cargarDocumentosDependencia();
                } else {
                    alert_Danger("ERROR:", data);
                }
            }, 'text', false, false, "POST");
        }
    } else {
        alert_Warning("MENSAJE", "No selecciono ningun elemento.");
    }
}
//function mostrarListaDocDependencia() {
//    var validaFiltro = "1";
//    if (validaFiltro === "1") {
//        var p = new Array();
//        p[0] = "accion=goDocuDependencia";
//        p[1] = "vCodDependencia=" + jQuery('#codDepencia').val();
//        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
//            refreshScript("divTablaListDocDependencia", data);
//        }, 'text', false, false, "POST");
//    }
//    return false;
//}
//function mostrarPaginacionDocDependencia(accion) {
//    var validaFiltro = "";
//    validaFiltro = fu_validadFiltroPaginacionDocDepen(accion);
//    if (validaFiltro === "1") {
//        var p = new Array();
//        p[0] = "accion=goDocuDependencia";
//        p[1] = "vCodDependencia=" + jQuery('#codDepencia').val();
//        p[2] = "hdnOperacionPaginacion=" + accion;
//        p[3] = "txtNumeroPaginaActual=" + jQuery('#txtNumeroPaginaActual').val();
//        p[4] = "hdnNumeroTotalDePaginas=" + jQuery('#hdnNumeroTotalDePaginas').val();
//        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
//            refreshScript("divTablaListDocDependencia", data);
//        }, 'text', false, false, "POST");
//    }
//    return false;
//}
function fu_validadFiltroPaginacionDocDepen(accion) {
    var nroPagActual = Number(jQuery('#txtNumeroPaginaActual').val());
    var nroTotalPaginas = Number(jQuery('#hdnNumeroTotalDePaginas').val());
    if (accion === 'P_PRIMERO') {
        if (nroPagActual <= 1) {
            return "0";
        }
    } else if (accion === 'P_ATRAS') {
        if (nroPagActual <= 1 || nroPagActual > nroTotalPaginas) {
            return "0";
        }
    } else if (accion === 'P_SIGUIENTE') {
        if (nroPagActual >= nroTotalPaginas) {
            return "0";
        }
    } else if (accion === 'P_ULTIMO') {
        if (nroPagActual === nroTotalPaginas) {
            return "0";
        }
    } else if (accion === 'P_ACTUALIZAR') {
        if (nroPagActual > nroTotalPaginas || nroPagActual <= 0) {
            return "0";
        }
    }
    return "1";
}
function fu_editarDocXDependencia(codTipoDoc, accion) {
    if (accion === '1') {
        var p = new Array();
        p[0] = "accion=goUpdDocuDependencia";
        p[1] = "vAccion=1";//traer Datos    
        p[2] = "vCodTipoDoc=" + codTipoDoc;
        p[3] = "vCodDependencia=" + jQuery('#codDepencia').val();
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            fn_rptaeditarDocXDependencia(data);
        }, 'text', false, false, "POST");
    } else {
        ajaxCall("/srTablaConfiguracion.do?accion=goUpdDocuDependencia&vAccion=2", $('#documentoDependenciaBean').serialize()/*p.join("&")*/, function(data) {
            fn_rptaeditarDocXDependencia(data);
        }, 'text', false, false, "POST");
    }
}
function fn_rptaeditarDocXDependencia(XML_AJAX) {
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }
}
function fu_setDatoDocumentoDepenUpd(cod, desc) {
    jQuery('#tiDescrip').val(desc);
    jQuery('#coTipDoc').val(cod);
    if (!jQuery('#bEsOblFirma1:checked').val()) {
        jQuery('#bEsOblFirma1').prop('checked', true);
        jQuery('#esOblFirma').val("1");
    }
    jQuery('#bInGeneOfic1').removeAttr('checked');
    jQuery('#inGeneOfic').val("0");
}
function f_habDesDocDepenUpd() {
    if (jQuery('#bEsOblFirma1:checked').val()) {
        jQuery('#esOblFirma').val("1");
    } else {
        jQuery('#esOblFirma').val("0");
    }
    if (jQuery('#bInGeneOfic1:checked').val()) {
        jQuery('#inGeneOfic').val("1");
    } else {
        jQuery('#inGeneOfic').val("0");
    }
}
function passArrayToHtmlTablaTdDocDepenUpd(data) {
    var p = new Array();
    $.each(data, function(index, item) {
        p[index] = item.innerHTML;
    });
    fu_setDatoDocumentoDepenUpd(p[1], p[0]);
}
function fn_cargarDocMotivosDependencia() {
    var p = new Array();
    p[0] = "accion=goListaDocMotDependencia";
    p[1] = "vCodDependencia=" + jQuery('#codDepencia').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleDocMotDependencia", data);
    }, 'text', false, false, "POST");
}
function fn_listaMotDependencia() {
    var codDepencia = $("#codDepencia").val();
    var codDoc = $("#codDoc").val();
    var p = new Array();
    p[0] = "accion=goListaMotDependencia";
    p[1] = "vCodDependencia=" + codDepencia;
    p[2] = "vCodDoc=" + codDoc;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleMotDependencia", data);
    }, 'text', false, false, "POST");
}

function fn_eliminarMotDocDep() {
    var codDoc = $("#codDoc").val();
    var codMot = $("#codMot").attr("value");
    var codDepencia = $("#codDepencia").val();
    
    bootbox.confirm({
        message: "¿Eliminar MOTIVO?",
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
                var jsonBody = {"coDep": codDepencia, "coTipDoc": codDoc, "coMot": codMot};
                var jsonString = JSON.stringify(jsonBody);
                var url = "/srTablaConfiguracion.do?accion=goEliMotDocDependencia";
                ajaxCallSendJson(url, jsonString, function(data) {
                    if (data === "Datos eliminados.") {
                        alert_Sucess("MENSAJE:", data);
                        fn_listaMotDependencia();
                    } else {
                        alert_Danger("ERROR:", data);
                    }
                }, 'text', false, false, "POST");
            } else {
            }
        }
    });
    
    return;
}
function fn_eventMotDependencia() {
    var indexFilaClick = -1;
    $("#tblMotDepen tbody tr").click(function() {
        if (indexFilaClick !== -1) {
            if ($("#tblMotDepen tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
                $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
                $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }
        else {
            $(this).addClass('row_selected');
            indexFilaClick = $(this).index();
            var codMot = $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") button").attr("codMot");
            $("#codMot").attr("value", codMot);

            $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
            $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
            $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            $("#tblMotDepen tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
        }
        return false;
    });
}
function fn_cargarNuevoMotDepen() {
    var codDepencia = $("#codDepencia").val();
    var codDoc = $("#codDoc").val();
    if (!!codDepencia && !!codDoc && codDoc !== "-1")
    {
        var p = new Array();
        p[0] = "accion=goCargarMotDep";
        p[1] = "vCodDependencia=" + codDepencia;
        p[2] = "vCodDoc=" + codDoc;
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Warning("MENSAJE", "Debe seleccionar una DEPENDECIA con un DOCUMENTO.");
    }
    return false;
}
function fn_agregarMotivoDepen(codMot) {
    var codDepencia = $("#codDepencia").val();
    var codDoc = $("#codDoc").val();
    if (!!codMot && !!codDepencia && !!codDoc && codDoc !== "-1") {
        if (reemMot === false) {
            var jsonBody = {"coDep": codDepencia, "coTipDoc": codDoc, "coMot": codMot};
            var jsonString = JSON.stringify(jsonBody);
            var url = "/srTablaConfiguracion.do?accion=goAgregarMotiDocDependencia";
            ajaxCallSendJson(url, jsonString, function(data) {
                if (data === "Datos guardados.") {
                    alert_Sucess("MENSAJE:", data);
                    removeDomId('windowConsultaMotDependencia');
                    fn_listaMotDependencia();
                } else {
                    alert_Danger("ERROR:", data);
                }
            }, 'text', false, false, "POST");
        } else {
            var jsonBody = {"coDep": codDepencia, "coTipDoc": codDoc, "coMot": codMot};
            var jsonString = JSON.stringify(jsonBody);
            var codMotReempl = $("#codMotReempl").attr("value");
            reemMot = false;
            var url = "/srTablaConfiguracion.do?accion=goCambiarMotiDocDependencia&codMotReempl=" + codMotReempl;
            ajaxCallSendJson(url, jsonString, function(data) {
                if (data === "Datos guardados.") {
                    alert_Sucess("MENSAJE:", data);
                    removeDomId('windowConsultaMotDependencia');
                    fn_listaMotDependencia();
                } else {
                    alert_Danger("ERROR:", data);
                }
            }, 'text', false, false, "POST");
        }
    } else {
        alert_Warning("MENSAJE", "Error al agregar el motivo.");
    }
}
function fn_cambiarMotDoc(codMot) {
    reemMot = true;
    $("#codMotReempl").attr("value", codMot);
    fn_cargarNuevoMotDepen();
}
function fn_cargarNuevoGrupoDestino() {
    var ocultaGrupos = "#tablaDependencia tr:eq(1)";
    $(ocultaGrupos).hide();
    $("#btn-cerrar").show();
    $("#txtNombreGrupo").focus();
    $("#txtNombreGrupo").prop("readonly", false);
    $("#txtNombreGrupo").val("");
    $("#btn-nuevo").hide();
    $("#btn-eliminar").hide();
    $("#txtNombreGrupo").val("");
    
    $("#txtDesDependenciaTemp").val($("#busDependenciaGD input").val());
    $("#txtDesDependenciaTemp").show();    
    $("#busDependenciaGD").hide();
    
    nuevoGrupo = true;
    jsonDetGrupo = [];
    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        $(this).remove();
    });
    return;
    $("#tblNuevoGrupo").show();
}

function fn_listaGrupoDetalle() {
    var codGrupoDestino = $("#codGrupoDestino").val();
    var p = new Array();
    p[0] = "accion=goListaGrupoDestinoDet";
    p[1] = "vCodGrupoDestino=" + codGrupoDestino;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaGrupoDestinoDet", data);
        if (!!codGrupoDestino) {
            $("#txtNombreGrupo").val($("#codGrupoDestino option:selected").text());
            $("#txtNombreGrupo").prop("readonly", false);
        } else {
            $("#txtNombreGrupo").prop("readonly", true);
        }
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}
function fn_eventGrupoDestino() {
    $("#tblDestDetalle tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
    });
}
function fn_cargarDepenDestino(cell) {
    GDindexCurrentSelectedCell = $(cell).parents("tr").index();
    //var codGrupoDest = $("#codGrupoDestino").val();
    var codDepen = $("#codDependencia").val();
    //if (!!codGrupoDest && !!codDepen) {
    if (!!codDepen) {
        var p = new Array();
        p[0] = "accion=goCargarDependencias";
        //p[1] = "codGrupoDest=" + codGrupoDest;
        p[1] = "codDepen=" + codDepen;
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");
        return false;
    } else {
        alert_Warning("MENSAJE", "Error no se selecciono una dependencia.");
    }
}


function fn_NuevoGrupoDestino() {
    var codDepencia = $("#codDepencia").val();
    var nomGrupo = $("#txtNombreGrupo").val();
    if (!!nomGrupo === false) {
        alert_Danger("ERROR:", "No se puede crear grupo con un nombre vacio.");
        return;
    }
    if (!!codDepencia)
    {
        var jsonBody = {"deGruDes": nomGrupo, "coDep": codDepencia};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goNuevoGrupo";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "NO_OK") {
                alert_Danger("ERROR:", "Error al crear el grupo");
            } else {
                var codGrupoCreado = data;
                fn_cargarGruposDestino();
                $("#codGrupoDestino option[value='" + codGrupoCreado + "']").attr("selected", "selected");
                fn_listaGrupoDetalle();
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Warning("MENSAJE", "Debe seleccionar una DEPENDECIA");

    }
    return false;
}
function fn_eliminarGrupo() {
    var codGrupo = $("#codGrupoDestino").val();
    if (!!codGrupo && codGrupo !== "-1") {
        bootbox.confirm({
        message: "¿Está seguro de eliminar este grupo?",
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
                    var jsonBody = {"coGruDes": codGrupo};
                    var jsonString = JSON.stringify(jsonBody);
                    var url = "/srTablaConfiguracion.do?accion=goEliGrupoDestino";
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos eliminados.") {
                            alert_Sucess("MENSAJE", data);
                            fn_cargarGruposDestino();
                        } else {
                            alert_Danger("ERROR:", "Error al crear el grupo");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    } else {
        alert_Warning("MENSAJE", "Tiene que seleccionar un grupo para eliminarlo.");
    }
}
function fn_grabarConfPersonal() {
    var codEmp = $("#codEmp").val();
    var codDep = $("#codDep").val();
    var dirPri = allTrim($("#txtDirPrincipal").val());
    var piePag = $("#txtPiePag").val();
    var rbAcceso = $("input[name='rbAcceso']:checked").val();
    var rbCarDoc = $("input[name='rbCarDoc']:checked").val();
    var rbFirDoc = $("input[name='rbFirDoc']:checked").val();
    var rbDocDef = $("input[name='rbDocDef']:checked").val();
    var rinEmailDeriv = $("#lstTipoEnvioCorreo").val();
    var myForm={codEmp:codEmp, codDep:codDep, dirPri:dirPri, piePag:piePag, rbAcceso:rbAcceso, rbCarDoc:rbCarDoc, rbFirDoc:rbFirDoc, rbDocDef:rbDocDef,rinEmailDeriv:rinEmailDeriv};
    /*if(fn_valFormConfPersonal(myForm)){
        var jsonBody =
                {
                    "cempCodemp": codEmp, "coDep": codDep,
                    "deDirEmi": dirPri, "dePiePagina": piePag,
                    "tiAcceso": rbAcceso, "inCargaDocMesaPartes": rbCarDoc,
                    "inFirma": rbFirDoc, "inTipoDoc": rbDocDef
                };
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goUpdConfigPersonal";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE", data);
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
        $('#RutaDocs').html("<strong>" + dirPri + "</strong>");
        setTimeout("fu_activaDependencia_dir(" + codDep + "," + codDep + ");", 500);
    }*/
    $("#applicationPanel").show();
    fn_valFormConfPersonal(myForm, function(data) {
        var retval = data;
        if(retval){
            var jsonBody =
                    {
                        "cempCodemp": codEmp, "coDep": codDep,
                        "deDirEmi": dirPri, "dePiePagina": piePag,
                        "tiAcceso": rbAcceso, "inCargaDocMesaPartes": rbCarDoc,
                        "inFirma": rbFirDoc, "inTipoDoc": rbDocDef,"inEmailDeriv":rinEmailDeriv
                    };
            var jsonString = JSON.stringify(jsonBody);
            var url = "/srTablaConfiguracion.do?accion=goUpdConfigPersonal";
            ajaxCallSendJson(url, jsonString, function(data) {
                if (data === "Datos guardados.") {
                    alert_Sucess("MENSAJE", data);
                } else {
                    alert_Danger("ERROR:", data);
                }
            }, 'text', false, false, "POST");
            $('#RutaDocs').html("<strong>" + dirPri + "</strong>");
            setTimeout("fu_activaDependencia_dir(" + codDep + "," + codDep + ");", 500);             
        }
    });
    return;
}
function fn_selecDirectorio() {
//    var dir = "";
//    var appletObj = jQuery('#firmarDocumento');
//    try {
//        dir = appletObj[0].getDirectorio();
//        if (!!dir) {
//            $("#txtDirPrincipal").attr("value", dir);
//        }
//    } catch (ex) {
//        alert_Warning("MENSAJE", "Fallo en abrir el direcotrio");
//    }
    var dir = "";
    var param={};
    //runApplet(appletsTramiteDoc.getDirectorio,param,function(data){
    runOnDesktop(accionOnDesktopTramiteDoc.getDirectorio,param,function(data){    
        dir=data;
        $("#txtDirPrincipal").attr("value", dir);
        
    });
    
}
function fn_selecDirectorioApplet() {
    var dir = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        dir = appletObj[0].getDirectorio();
        if (!!dir) {
            $("#txtDirPrincipal").attr("value", dir);
        }
    } catch (ex) {
        alert_Warning("MENSAJE", "Fallo en abrir el direcotrio");
    }
}
function fn_verificaSiExisteDirApplet() {
    var dir = "";
    var appletObj = jQuery('#firmarDocumento');
    try {
        dir = appletObj[0].verificarSiExisteDir(jQuery('#txtDirPrincipal').val());
    } catch (ex) {
        //alert_Warning("MENSAJE", "Fallo en verificar si existe el directorio.");
    }
    return dir;
}
function fn_cargarProveedores() {
    var p = new Array();
    p[0] = "accion=goListaRegProveedores";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaProveedores", data);
    }, 'text', false, false, "POST");
}
function fn_cargarTupa() {
    var p = new Array();
    p[0] = "accion=goListaRegTupa";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaTupa", data);
    }, 'text', false, false, "POST");
}
function fn_cargarCiudadano() {
    var p = new Array();
    p[0] = "accion=goListaRegCiudadano";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaCiudadano", data);
    }, 'text', false, false, "POST");
}
function fn_buscarProveedores(e, textInput) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqProveedores();
    }
}
function fn_buscarTupa(e, textInput) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqTupa();
    }
}
function fn_buscarCiudadano(e, textInput) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqCiudadano();
    }
}
function fn_cargarBusqProveedores() {
    var busNroRuc       = allTrim(  $("#txtBusNroRuc").val()        );
    var busRazonSocial = jQuery('#txtBusRazonSocial').val();
    if(!!busRazonSocial){
        busRazonSocial=jQuery('#txtBusRazonSocial').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg($("#txtBusRazonSocial").val()))).val();        
        if(allTrim(busRazonSocial).length >= 0 && allTrim(busRazonSocial).length <= 3){  //El texto es entre 1 y 3 caracteres
            //bootbox.alert("<h5>Ingrese Razon social de mas de 3 digitos.</h5>", function() {
            bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtBusRazonSocial").focus();
            });  
            return;
        }        
    }else{
        busRazonSocial="";
    }
    /*
    if (!!!busRazonSocial && !!!busNroRuc)
    {
        alert_Danger("ERROR", "  Ingrese un texto ó Nro RUC");
        return;
    }*/
    
    if(allTrim(busNroRuc).length >= 1 && allTrim(busNroRuc).length <= 3){  //El texto es entre 1 y 3 caracteres
        bootbox.alert("<h5>Debe ingresar como mínimo 4 caracteres.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtBusNroRuc").focus();
        });
        return;
    }
    
    var p = new Array();
    p[0] = "accion=goListaBusqProveedores";
    p[1] = "busNroRuc=" + busNroRuc;
    p[2] = "busRazonSocial=" + busRazonSocial.toUpperCase();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaProveedores", data);
        loadding(false);
        var c = $("#tblProveedorDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}
function fn_cargarBusqTupa() {
    var busTupaCorto        = allTrim(  $("#txtTupaCorto").val()       );
    var busTupaDescripcion  = allTrim(  $("#txtTupaDescripcion").val() );
    /*
    if ( busTupaCorto.length<1 && busTupaDescripcion.length < 1)
    {
        alert_Danger("ERROR", "  Ingrese dependencia, Descripcion corta ó Descripcion");
        return;
    }*/
    var p = new Array();
    p[0] = "accion=goListaBusqTupa";
    p[1] = "busTupaCorto="       + busTupaCorto.toUpperCase();
    p[2] = "busTupaDescripcion=" + busTupaDescripcion.toUpperCase();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaTupa", data);
        loadding(false);
        var c = $("#tblTupaDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}

function fn_cargarBusqCiudadano() {
    var busCiudDocumento  = allTrim(  $("#txtCiudDocumento").val() );
    var busCiudApPaterno  = allTrim(  $("#txtCiudApPaterno").val() );
    var busCiudApMaterno  = allTrim(  $("#txtCiudApMaterno").val() );
    var busCiudNombres    = allTrim(  $("#txtCiudNombres").val()   );
    /*
    if (busCiudDocumento.length<1 && busCiudApPaterno.length<1 && busCiudApMaterno.length < 1 && busCiudNombres.length < 1)
    {
        alert_Danger("ERROR", "  Ingrese Documento, Apellido Paterno, Apellido Materno o Nombres del ciudadano");
        return;
    }*/
    if(!!busCiudDocumento&&busCiudDocumento.length>0){
        var lnuDniAux=busCiudDocumento.length;
        var vValidaNumero = fu_validaNumero(busCiudDocumento);
        if (vValidaNumero !== "OK") {
            bootbox.alert("<h5>Documento debe ser solo números.</h5>", function() {
                bootbox.hideAll();
            }); 
            return;
        }else{
            if(lnuDniAux!==8){
                bootbox.alert("<h5>Número de documento debe tener 8 caracteres.</h5>", function() {
                    bootbox.hideAll();
                });
                return;
            }
        }
    }    
    
    var p = new Array();
    p[0] = "accion=goListaBusqCiudadano";
    p[1] = "busCiudDocumento=" + busCiudDocumento;
    p[2] = "busCiudApPaterno=" + busCiudApPaterno.toUpperCase();
    p[3] = "busCiudApMaterno=" + busCiudApMaterno.toUpperCase();
    p[4] = "busCiudNombres="   + busCiudNombres.toUpperCase();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaCiudadano", data);
        loadding(false);
        var c = $("#tblCiudadanoDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}
function fn_eventProveedores() {
    $("#tblProveedorDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codProveedor").val($(this).find("[name='hid_NuRuc']").val());
    });
}
function fn_eventTupa() {
    $("#tblTupaDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codTupa").val($(this).find("[name='hid_CoProceso']").val());
    });
}
function fn_eventCiudadano() {
    $("#tblCiudadanoDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codCiudadano").val($(this).find("[name='hid_IdCiudadano']").val());
    });
}

function fu_goNuevoProveedor() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoProveedor", '', function(data) {
        jQuery('#divProveedorBody').hide();
        jQuery('#divProveedorEdit').show();
        refreshScript("divProveedorEdit", data);
        loadding(false);
    }, 'text', false, false, "POST");
    return false;
}
function fu_goEditarProveedor() {
    var codProveedor = $("#codProveedor").val();
    if (!!codProveedor) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoProveedor&vcodProveedor=" + codProveedor, '', function(data) {
            jQuery('#divProveedorBody').hide();
            jQuery('#divProveedorEdit').show();
            refreshScript("divProveedorEdit", data);
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}
function fu_goNuevoTupa() {
    
    
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoTupa", '', function(data) {
        jQuery('#divTupaBody').hide();
        jQuery('#divTupaEdit').show();
        refreshScript("divTupaEdit", data);
        loadding(false);
        jQuery('#fielDetalleTupaRequisitos').hide();
    }, 'text', false, false, "POST");
    return false;
}
function fu_goEditarTupa() {
    var codTupa = $("#codTupa").val();
    if (!!codTupa) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoTupa&vcodTupa=" + codTupa, '', function(data) {
            jQuery('#divTupaBody').hide();
            jQuery('#divTupaEdit').show();
            refreshScript("divTupaEdit", data);
            fn_cargarTupaRequisito();
            jQuery('#fielDetalleTupaRequisitos').show();
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}
function fu_goNuevoCiudadano() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoCiudadano", '', function(data) {
        jQuery('#divCiudadanoBody').hide();
        jQuery('#divCiudadanoEdit').show();
        refreshScript("divCiudadanoEdit", data);
        loadding(false);
    }, 'text', false, false, "POST");
    return false;
}
function fu_goEditarCiudadano() {
    var codCiudadano = $("#codCiudadano").val();
    if (!!codCiudadano) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoCiudadano&vcodCiudadano=" + codCiudadano, '', function(data) {
            jQuery('#divCiudadanoBody').hide();
            jQuery('#divCiudadanoEdit').show();
            refreshScript("divCiudadanoEdit", data);
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}
function fn_seleccionarUbigeo() {
    var p = new Array();
    p[0] = "accion=goCargarUbigeo";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null)
        {
            $("body").append(data);
        }
    }, 'text', false, false, "POST");
    return false;
}
function fn_buscaUbigeo() {
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
    ajaxCallSendJson("/srTablaConfiguracion.do?accion=goListaBusqUbigeo", jsonString, function(data) {
        refreshScript("divUbigeoDet", data);
        var c = $("#tblUbigeoDetalle tr").length;
        if (c === 0) {
            alert_Danger("ATENCIÓN:", "No se encontraron coincidencias");
        }
    }, 'text', false, false, "POST");
    return;
}
//Ubigeo
function fu_callEventoTablaUbigeo() {
    var idTablaUbi = "tblUbigeoDetalle";
    fu_eventoGridTabla(idTablaUbi, paramConfUbigeo);
    fn_eventProvedores(idTablaUbi);
}
function fn_eventProvedores(pIdTablaUbi) {
    var idTablaUbi = pIdTablaUbi;
    $("#" + idTablaUbi + " tbody").on("click", "tr", function() {
        var index = $(this).index();
        var noDep = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(0)").text();
        var noPrv = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(1)").text();
        var noDis = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(2)").text();
        var ubDep = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(3)").text();
        var ubPrv = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(4)").text();
        var ubDis = $("#tblUbigeoDetalle tbody tr:eq(" + index + ") td:eq(5)").text();
        $("#hNomDepCon").val(noDep);
        $("#hNomProPa").val(noPrv);
        $("#hNomDisDep").val(noDis);
        $("#hCodDepCon").val(ubDep);
        $("#hCodProPa").val(ubPrv);
        $("#hCodDisDep").val(ubDis);
        var ubi = noDep + "-" + noPrv + "-" + noDis;
        $("#txtUbiDomicilio").val(ubi);
        removeDomId('windowConsultaUbigeoProveedor');
    });
}
function fn_limpiarUbigeo() {
    $("#txtNomDepCon").attr("value", "").val("");
    $("#txtNomProPa").attr("value", "").val("");
    $("#txtNomDisDep").attr("value", "").val("");
    $("#txtCodDepCon").attr("value", "").val("");
    $("#txtCodProPa").attr("value", "").val("");
    $("#txtCodDisDep").attr("value", "").val("");
    /** LAAH 28092015- MODIFICACION SIGUIENDO RECOMENDACION 03 BUG #3405
     **/
    $('#tblUbigeoDetalle > tbody').html('');
    $('#tblUbigeoDetalle_info').hide();

}
function fn_grabarNueProveedor() {
    var nruc = allTrim($("#hidN_Ruc").val());
    var ruc = allTrim($("#txtRuc").val());
    var razonSocial = allTrim($("#txtRazonSocial").val());
    var dir = allTrim($("#txtDireccion").val());
    var telefono = allTrim($("#txtTelefono").val());
    var cmail = allTrim($("#txtEmail").val());
    var noDep = allTrim($("#hNomDepCon").val());
    var noPrv = allTrim($("#hNomProPa").val());
    var noDis = allTrim($("#hNomDisDep").val());
    var ubDep = allTrim($("#hCodDepCon").val());
    var ubPrv = allTrim($("#hCodProPa").val());
    var ubDis = allTrim($("#hCodDisDep").val());
    $("#applicationPanel").show();
    if (valFormProveedorConfigGeneral()) {
        var url = "";
        if (!!nruc) {
            var mensaje = "<h5>¿Desea Editar Persona Jurídica?</h5>";
            url = "/srTablaConfiguracion.do?accion=goUpdProveedor";
        } else {
            var mensaje = "<h5>¿Desea Insertar Nueva Persona Jurídica?</h5>";
            var nruc = allTrim($("#txtRuc").val());
            url = "/srTablaConfiguracion.do?accion=goAgregarProveedor";
        }

        bootbox.confirm(mensaje, function (result) {
            if (result) {
                var jsonBody =
                        {
                            "nuRuc": ruc, "descripcion": razonSocial,
                            "cproDomicil": dir, "cubiCoddep": ubDep, "cubiCodpro": ubPrv,
                            "cubiCoddis": ubDis, "noDep": noDep,
                            "noPrv": noPrv, "noDis": noDis, "cproTelefo": telefono,"cproEmail":cmail
                        };
                var jsonString = JSON.stringify(jsonBody);
                //var url = "/srTablaConfiguracion.do?accion=goAgregarProveedor";
                ajaxCallSendJson(url, jsonString, function (data) {
                    if (data === "Datos guardados.") {
                        alert_Sucess("MENSAJE:", data);
                        fn_cargarListaProveedores();
                        //fn_cargarBusqProveedores();
                        $("#btn-buscar").click();
                        $("#codProveedor").val(''); 
                    } else {
                        alert_Danger("ERROR", "No se pudo guardar, POSIBLES DATOS EXISTENTES.");
                    }
                }, 'text', false, false, "POST");
            }
        });


    }
}
function fn_grabarNuevoTupa() {
    var valRetorno      = '0';
    
    var CoProceso       = allTrim(  $("#hidN_coProceso").val()  );
    var DeNombre        = allTrim(  $("#txtDeNombre").val()     );
    var DeDescripcion   = allTrim(  $("#txtDeDescripcion").val()); 
    var NuPlazo         = allTrim(  $("#txtNuPlazo").val()      );
    var isChecked       = $("#chbxEstado").is(':checked');
    var coEstado = (isChecked?'1':'0');
    
    
    if(allTrim(DeNombre).length<1){
        bootbox.alert("<h5>Ingrese descripción corta de Tupa, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtDeNombre").focus();
        });  
    }else if(allTrim(NuPlazo).length<1){
        bootbox.alert("<h5>Ingrese PLAZO de atencion en días, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtNuPlazo").focus();
        });  
    }else if(fu_validaNumero(NuPlazo)!=="OK"){
        bootbox.alert("<h5>PLAZO de atencion en días, debe ser solo números.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtNuPlazo").focus();
        });         
    }else{
        valRetorno = '1';
    }
    if(valRetorno === '1'){
        bootbox.confirm({
        message: "¿Guardar Tupa?",
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
                    var jsonBody =
                            {
                                 "coProceso": CoProceso, "deNombre": DeNombre, "deDescripcion": DeDescripcion, "nuPlazo": NuPlazo , "esEstado":coEstado
                            };
                    var jsonString = JSON.stringify(jsonBody);

                    if (!!CoProceso) {
                        url = "/srTablaConfiguracion.do?accion=goUpdTupa";
                    } else {
                        url = "/srTablaConfiguracion.do?accion=goAgregarTupa";
                    }

                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            fn_cargarListaTupa();
                            fn_cargarBusqTupa();
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    }
}

function cargarListCredecialUser() {
    var estado  = 1; // Habilitados
    var p = new Array();
    p[0] = "accion=goListaBusqCredencialUser";
    p[1] = "estado=" + estado;
     ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleRegistroCredenciales", data);
    }, 'text', false, false, "POST");
    return false;
}


function fn_grabarCredencialUser() {
 
    //var CoProceso       = allTrim(  $("#hidN_coProceso").val()  );
    var DeDni        = allTrim(  $("#txtDNI").val());
    var DeClave   = allTrim(  $("#txtClave").val()); 
   
    if(fn_validarCredencialUser() === '1'){
        bootbox.confirm({
        message: "¿Guardar Credencial?",
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
                    var jsonBody =
                            {
                                 "nuDni": DeDni, "nuClave": DeClave
                            };
                    var jsonString = JSON.stringify(jsonBody);

                    if (!!DeDni && !!DeClave) {
                        url = "/srTablaConfiguracion.do?accion=goUpdCredencial";
                    }// else {
//                        url = "/srTablaConfiguracion.do?accion=goUpdCredencial";
//                    }

                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            //fn_cargarListaTupa();
                            //fn_cargarBusqTupa();
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    }
}

function fn_agregarCredencialUser(usuario) {
    //var codProceso = $("#hidN_coProceso").val();
    var nuDNI = $("#txtDNI").val();
    var nuClave = $("#txtClave").val();
    if(fn_validarCredencialUser() === '1'){
        var jsonBody = { "deUser": usuario, "nuDni": nuDNI , "nuClave": nuClave};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goAgregarCredecialUser";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE:", data);
                removeDomId('windowConsultaCredencialUser');
                cargarListCredecialUser();
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
    }
}

function fn_eliminarCredencialUser(codigo,usuario) {
    var pcodiogo = codigo;
    var pusuario = usuario;
    bootbox.confirm({
        message: "¿Eliminar REQUISITO?",
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
                var jsonBody = {"celeCodele": pcodiogo, "deUser": pusuario};
                var jsonString = JSON.stringify(jsonBody);
                var url = "/srTablaConfiguracion.do?accion=goEliCredencialUser";
                ajaxCallSendJson(url, jsonString, function(data) {
                    if (data === "Datos eliminados.") {                       
                        alert_Sucess("MENSAJE:", data);  
                        cargarListCredecialUser();
                    } else {
                        alert_Danger("ERROR:", data);
                    }
                }, 'text', false, false, "POST");
            } else {
            }
        }
    });
    
    return;
}

function fn_validarCredencialUser(){
    var valRetorno ='1';
    var DeDni        = allTrim(  $("#txtDNI").val());
    var DeClave   = allTrim(  $("#txtClave").val()); 
    var maxCarateresDni=8;
    
    if(!!DeDni){
        var lnuDniAux=DeDni.length;
        var vValidaNumero = fu_validaNumero(DeDni);
        if (vValidaNumero !== "OK") {
            valRetorno = '0';
            bootbox.alert("<h5>Número de documento debe ser solo números.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtDNI").focus();
            }); 
        }
        if(valRetorno==='1'){
            if(lnuDniAux!==8){
                valRetorno = '0';
                bootbox.alert("<h5>Número de documento debe tener "+maxCarateresDni+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtDNI").focus();
                });                
            }
        }
    }else{
        valRetorno = '0';
        bootbox.alert("<h5>Digite Número de documento.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtDNI").focus();
        });        
    }
    
    if(allTrim(DeClave).length<1){
        valRetorno ='0';
        bootbox.alert("<h5>Ingrese clave , campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtClave").focus();          
        });  
    }
    
    return valRetorno;
}

function fn_grabarNuevoCiudadano() {
    var nuLem = allTrim($("#hidN_nuLem").val());
    var deApp = allTrim($("#txtDeApp").val());
    var deApm = allTrim($("#txtDeApm").val());
    var deNom = allTrim($("#txtDeNom").val());
    
    //Estos 3 de aca no se usan
    var noDep = allTrim($("#hNomDepCon").val());
    var noPrv = allTrim($("#hNomProPa").val());
    var noDis = allTrim($("#hNomDisDep").val());

    var ubDep = allTrim($("#hCodDepCon").val());
    var ubPrv = allTrim($("#hCodProPa").val());
    var ubDis = allTrim($("#hCodDisDep").val());
    
    var deDomicil = allTrim($("#txtDeDomicil").val());
    var deEmail = allTrim($("#txtDeEmail").val());
    var deTelefo = allTrim($("#txtDeTelefo").val());
    if(deEmail!="" &&  !fu_IsValidaCorreo(deEmail)){
        return false;
    }
    
    if (!!valFormCiudadano()) {
         bootbox.confirm({
        message: "¿Guardar Cambios?",
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
                    var url = "";
                    if (!!nuLem) {
                        url = "/srTablaConfiguracion.do?accion=goUpdCiudadano";
                    } else {
                        nuLem = allTrim( $("#txtNuLem").val() );
                        url = "/srTablaConfiguracion.do?accion=goAgregarCiudadano";
                    }

                    var jsonBody =
                    {
                        "nuLem": nuLem, 
                        "ubDep": ubDep, "ubPrv": ubPrv, "ubDis": ubDis,
                        "deApp": deApp, "deApm": deApm, "deNom": deNom,
                        "deDomicil": deDomicil, "deTelefo": deTelefo, "deEmail": deEmail

                    };

                    var jsonString = JSON.stringify(jsonBody);
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            fn_cerrarCiudadano();
                            $('#btn-buscar').click();
                             $("#codCiudadano").val(''); 
                            //fn_cargarBusqCiudadano();
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");        
                }
            }
         });
    }
}
function fn_cargarListaProveedores() {
    jQuery('#divProveedorBody').toggle();                                
    jQuery('#divProveedorEdit').html("");
    
}
function fn_cargarListaTupa() {
    jQuery('#divTupaBody').toggle();
    jQuery('#divDetalleTupaRequisitos').html("");
    jQuery('#divTupaEdit').html("");
}
function fn_cerrarCiudadano() {
    jQuery('#divCiudadanoBody').toggle();                                
    jQuery('#divCiudadanoEdit').html("");   
}
function fu_goNuevoOrigen() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoOrigen", '', function(data) {
        jQuery('#divOrigenBody').hide();
        jQuery('#divOrigenEdit').show();
        refreshScript("divOrigenEdit", data);
    }, 'text', false, false, "POST");
    return false;
}
function fn_cargarListaOtrosOrigenes() {
    var p = new Array();
    p[0] = "accion=goListaOtrosOrigenes";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaOtrosOrigenes", data);
    }, 'text', false, false, "POST");
}
function fn_buscarOtrosOrigenes(e, inputText) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqOtrosOrigenes();
    }
}

function fn_cargarBusqOtrosOrigenes() {
    var deRazSocOtr=jQuery('#txtBusRazonSocial').val();
    if(!!deRazSocOtr){
        //validar caracteres especiales
        deRazSocOtr=jQuery('#txtBusRazonSocial').val(fn_getCleanTextLenGreathree(fn_getCleanTextExpReg($("#txtBusRazonSocial").val()))).val();        

        if( allTrim(deRazSocOtr).length >= 0 && allTrim(deRazSocOtr).length <= 1 ){
            //bootbox.alert("<h5>Ingrese campo de Búsqueda de mas de 3 digitos .</h5>", function() {
            bootbox.alert("<h5>Debe ingresar como mínimo 2 caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtBusRazonSocial").focus();
            }); 
            return;
        }        
    }else{
        deRazSocOtr="";
    }
    
    var jsonBody =
            {
                "deRazSocOtr": deRazSocOtr
            };
    var jsonString = JSON.stringify(jsonBody);
    var url = "/srTablaConfiguracion.do?accion=goListaBusqOtrosOrigenes";
    ajaxCallSendJson(url, jsonString, function(data) {
        refreshScript("divListaOtrosOrigenes", data);
        var c = $("#tblOtroOrigenDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}
function fn_grabarOtroOrigen() {
    var idOtroOri = $("#idOtroOri").val();
    var codTipoDoc = $("#cbTipoDoc").val();
    var txtNumeroDoc = $("#txtNumeroDoc").val();
    var txtPaterno = $("#txtPaterno").val();
    var txtMaterno = $("#txtMaterno").val();
    var txtNombres = $("#txtNombres").val();
    var txtRazonSocial = $("#txtRazonSocial").val();
    var txtDireccion = $("#txtDireccion").val();
    var txtEmail = $("#txtEmail").val();
    var txtTelefo = $("#txtTelefo").val();

    var noDep = $("#hNomDepCon").val();
    var noPrv = $("#hNomProPa").val();
    var noDis = $("#hNomDisDep").val();
    var ubDep = $("#hCodDepCon").val();
    var ubPrv = $("#hCodProPa").val();
    var ubDis = $("#hCodDisDep").val();
    if(txtEmail!="" &&  !fu_IsValidaCorreo(txtEmail)){
        return false;
    }
    
    if(valFormOtroOrigenConfigGeneral()){
        bootbox.confirm({
        message: "¿Guardar Cambios?",
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
                    var jsonBody =
                            {
                                "coOtrOri": idOtroOri, "coTipOtrOri": codTipoDoc, "nuDocOtrOri": txtNumeroDoc,
                                "deApePatOtr": txtPaterno, "deApeMatOtr": txtMaterno, "deNomOtr": txtNombres,
                                "deRazSocOtr": txtRazonSocial, "deDirOtroOri": txtDireccion,
                                "ubDep": ubDep, "ubPro": ubPrv, "ubDis": ubDis, "deEmail": txtEmail, "deTelefo": txtTelefo
                            };

                    var jsonString = JSON.stringify(jsonBody);

                    var url = "";
                    if (!!idOtroOri) {
                        url = "/srTablaConfiguracion.do?accion=goUpdOtroOrigen";
                    } else {
                        url = "/srTablaConfiguracion.do?accion=goAgregarOtroOrigen";
                    }
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            fn_cerrarOtrosOrigenes();
                            //fn_cargarBusqOtrosOrigenes();
                            $("#btn-buscar").click();
                            $("#codOrigen").val('');
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");        
                }
            }
        });
    }
}
function fn_cerrarOtrosOrigenes() {
    jQuery('#divOrigenBody').toggle();                                
    jQuery('#divOrigenEdit').html("");
}


function fu_goEditarOrigen() {
    var codOrigen = $("#codOrigen").val();
    if (!!codOrigen) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoOrigen&vcodOrigen=" + codOrigen, '', function(data) {
            jQuery('#divOrigenBody').hide();
            jQuery('#divOrigenEdit').show();
            refreshScript("divOrigenEdit", data);
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

function fn_eventOtrosOrigenes() {
    $("#tblOtroOrigenDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codOrigen").val($(this).find("[name='idOtroOri']").val());
    });
}

function fn_eventDepDetalle() {
    $("#tblDepDet tbody tr").click(function(e) {
        if ($(this).hasClass('row_selected')) {
            //$(this).removeClass('row_selected');
            //jQuery('#txtpnuAnn').val("");
        }
        else {
            $('#tblDepDet tbody tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');

            /*obtiene datos*/
            //alert($(this).children('td')[12].innerHTML);
            //jQuery('#txtTextIndexSelect').val("0");
            if (!!$(this).children('td')[0].innerHTML) {
                jQuery('#pcoDep').val($(this).children('td')[0].innerHTML);
            }
            //editarDocumentoRecep($(this).children('td')[12].innerHTML,$(this).children('td')[13].innerHTML,$(this).children('td')[15].innerHTML);
            /* comentado xq los botones de ver documento y ver anexos, ya se encuentran el la tabla
             var sData = $(this).find('td');
             passArrayToHtmlTabla("txtTextIndexSelect",sData);*/


        }
    });    
}

function fn_selecionarDependencia() {
    var p = new Array();
    p[0] = "accion=goSelDependencia";
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null){
            $("body").append(data);
        }
    }, 'text', false, false, "POST");
    return false;
}
function fn_cambiarDependencia(codDepen, deDepen) {
      var esActivoPermiso = $("#tablaDependencia").data("esactivopermiso");
    
    if (esActivoPermiso){
        $("#tablaDependenciaPermiso tr:eq(1)").show();
        $("#codDependencia").val(codDepen);
        $("#txtDepEmiteBus").val(deDepen);
        removeDomId('windowSelDependencia');
        fn_listaPermisoDetalle();
    }else{
        $("#tablaDependencia tr:eq(1)").show();
        $("#codDependencia").val(codDepen);
        $("#txtDepEmiteBus").val(deDepen);
        removeDomId('windowSelDependencia');
        resetValoresGrupoDestino();
        fn_cargarGruposDestino();
    }
}
//js de Grupo destino.
function fn_guardarGrupoDest() {
    fn_grabarGrupoDestino();
}
function fn_grabarGrupoDestino() {
    jsonDetGrupo = [];
    var existeDuplicado = false;
    var depenVacia = false;
    var nombreGrupo = allTrim($("#txtNombreGrupo").val());    
    var codGrupo = $("#codGrupoDestino").val();
    if (!nuevoGrupo && cambioDatos == false && !!!nombreGrupo) {
        alert_Warning("ALERTA", "Debe crear o seleccionar un grupo para guardar cambios");
        return;
    }
    if (!!!nombreGrupo) {
        alert_Danger("ERROR", "Ingrese un nombre al grupo");
        $("#txtNombreGrupo").focus();
        return;
    }

    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        var aCoDep = $(this).find("#btn-cargar-dep").attr("coDep");
        var aCoEmp = $(this).find("#btn-cargar-emp").attr("coEmp");
        if (!!!aCoDep) {
            depenVacia = true;
        } else {
            if (!fn_buscaDetGrupo(aCoDep, aCoEmp)) {
                jsonDetGrupo.push({"co_dep": aCoDep, "co_emp": aCoEmp});
            } else {
                existeDuplicado = true;
            }
        }
    });
//    console.log(existeDuplicado);
//    return;

    if (existeDuplicado) {
        alert_Danger("ERROR", "Filas duplicadas corregir.");
    } else {
        if (depenVacia) {
            alert_Danger("ERROR", "Dependencia vacia, corregir.");
        } else {
            if (jsonDetGrupo.length >= 1) {
                var codDependencia = $("#codDependencia").val();//controlar si no existe este codigo.
                var jsonBody = {"coGruDes": codGrupo, "coDep": codDependencia, "deGruDes": nombreGrupo, "grupoDestinoDetalle": jsonDetGrupo};
                var jsonString = JSON.stringify(jsonBody);
                var url;
                if (nuevoGrupo) {
                    url = "/srTablaConfiguracion.do?accion=goNuevoGrupo";
                    fn_cargarGruposDestino();
                    fn_cancelarGuardarGD();
                    //return;
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "OK") {
                            alert_Sucess("OK", "Grabó correctamente.");
                            resetValoresGrupoDestino();
                            fn_cargarGruposDestino();
                            var restablecerVista = fn_cancelarGuardarGD();
                        } else {
                            alert_Danger("ERROR:", data);
                        }
                    }, 'text', false, false, "POST");
                } else {
                    if (cambioDatos) {
                        if (!!!codGrupo) {
                            alert_Danger("ERROR", "no se selecciono un grupo");
                            return;
                        } else {
                            url = "/srTablaConfiguracion.do?accion=goUpdateGrupo";
                            ajaxCallSendJson(url, jsonString, function(data) {
                                if (data === "OK") {
                                    alert_Sucess("OK", "Grabó correctamente.");
                                    var nuevoNombreGrupo = $("#txtNombreGrupo").val();
                                    $("#codGrupoDestino option:selected").text(nuevoNombreGrupo);
                                    resetValoresGrupoDestino();
                                } else {
                                    alert_Danger("ERROR:", data);
                                }
                            }, 'text', false, false, "POST");
                        }
                    } else {
                        alert_Warning("ALERTA", "No se detecto cambios que guardar.");
                    }
                }
            } else {
                alert_Danger("ERROR", "Detalle del grupo vacio.");
            }
        }
    }
}
//function fn_insertNuevoGrupoDestino() {
//    jsonDetGrupo = [];
//    var existeDuplicado = false;
//    var depenVacia = false;
//    var nombreGrupo = $("#txtNombreGrupo").val();
//    if (!!!nombreGrupo) {
//        alert_Danger("ERROR", "Ingrese un nombre al grupo");
//        $("#txtNombreGrupo").focus();
//        return;
//    }
//    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
//        var aCoDep = $(this).find("#btn-cargar-dep").attr("coDep");
//        var aCoEmp = $(this).find("#btn-cargar-emp").attr("coEmp");
//        if (!!!aCoDep) {
//            depenVacia = true;
//        } else {
//            if (!fn_buscaDetGrupo(aCoDep, aCoEmp)) {
//                jsonDetGrupo.push({"co_dep": aCoDep, "co_emp": aCoEmp});
//            } else {
//                existeDuplicado = true;
//            }
//        }
//    });
//    if (existeDuplicado) {
//        alert_Danger("ERROR", "Filas duplicadas duplicados corregir.");
//    } else {
//        if (depenVacia) {
//            alert_Danger("ERROR", "Dependencia vacia, corregir.");
//        } else {
//            if (jsonDetGrupo.length >= 1) {
//
////                var url = "/srTablaConfiguracion.do?accion=goInsGrupoDestino";
////                ajaxCallSendJson(url, jsonString, function(data) {
////                   bootbox.alert(data);
////                }, 'text', false, false, "POST");
//                var codDependencia = $("#codDependencia").val();//controlar si no existe este codigo.
//                var jsonBody = {"coDep": codDependencia, "deGruDes": nombreGrupo, "grupoDestinoDetalle": jsonDetGrupo};
//                //var jsonBody = {"coGruDes":"Algo"};
//                var jsonString = JSON.stringify(jsonBody);
//                var url = "/srTablaConfiguracion.do?accion=goNuevoGrupo";
//                ajaxCallSendJson(url, jsonString, function(data) {
//                    if (data === "NO_OK") {
//                        alert_Danger("ERROR:", "Error al crear el grupo");
//                    } else {
//                        alert_Sucess("OK", "Grabo correctamente.");
//                    }
//                }, 'text', false, false, "POST");
//                //nuevoGrupo=false;
//            } else {
//                alert_Danger("ERROR", "Detalle del grupo vacio.");
//            }
//        }
//    }
//}
function fn_buscaDetGrupo(coDep, coEmp) {
    for (i = 0; i < jsonDetGrupo.length; i++) {
        if (coDep === jsonDetGrupo[i].co_dep && coEmp === jsonDetGrupo[i].co_emp) {
            return true;
        }
    }
    return false;
}
function fn_eliminarDetalleGrupo() {

    var coDepTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
    $("#tblDestDetalle tbody").find(".row_selected").remove();
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;


    var codGrupoDestino = $("#codGrupoDestino").val();
    var coDepTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
    if (!!coDepTabla) {
        bootbox.confirm({
        message: "¿Eliminar fila?",
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
                    var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": coDepTabla, "co_emp": coEmpTabla};
                    var jsonString = JSON.stringify(jsonBody);
                    var url = "/srTablaConfiguracion.do?accion=goEliDetalleGrupo";
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos eliminados.") {
                            alert_Sucess("MENSAJE", data);
                            fn_listaGrupoDetalle();
                        } else {
                            alert_Danger("ERROR:", data);
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    } else {
        $("#tblDestDetalle tbody").find(".row_selected").remove();
    }
    return;
}
function fn_agregarDetalleGrupoDepen() {

    var coGrupo = $("#codGrupoDestino").val();
    var codDepencia = $("#codDependencia").val();
    var cf = 0;
    if (nuevoGrupo === true || !!coGrupo)
    {
        $("#tblDestDetalle tbody [id='btn-cargar-dep']").each(function() {
            if (!!$(this).attr("coDep") === false) {
                cf++;
            }
        });
        if (cf === 2) {
            alert_Danger("ERROR:", "Corregir dependencia");
            return;
        }
        if (!!codDepencia) {
            var fila = "<tr>" + $("#tblDestDetalle tr:eq(0)").html() + "</tr>";
            $("#tblDestDetalle tbody").append(fila);
        } else {
            alert_Warning("MENSAJE", "No se selecciono una dependencia con un grupo");
        }
    } else {
        alert_Warning("MENSAJE", "Tiene que crear un nuevo grupo o seleccionar un grupo");
    }

}
function fn_agregarDependenciaGrupo(codDepen, deDepen) {
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtCoDepen']").val(codDepen);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtDesDepen']").val(deDepen);

    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-dep").attr("coDep", codDepen);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coDep", codDepen);


    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-msg span").attr("class", "glyphicon glyphicon-ok").css({color: "green"});


    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtEmpleado']").val("");
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coDep", codDepen);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coEmp", "");


    removeDomId('windowConsultaDependencias');
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;
    var codGrupoDestino = $("#codGrupoDestino").val();
    if (!!codDepen && !!codGrupoDestino) {
        var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": codDepen};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goAgregarDependenciaDestino";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                removeDomId('windowConsultaDependencias');
                fn_listaGrupoDetalle();
            } else {
                alert_Danger("ERROR:", "EXISTE UNA FILA CON LOS MISMOS DATOS.");
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Danger("ERROR:", "ocurrio un error cons la dependencia");
    }
}
function fn_agregarEmpleadoGrupo(codEmp, nombre) {
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coEmp", codEmp);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtEmpleado']").val(nombre);
    removeDomId('windowConsultaEmpleados');
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;
    var codGrupoDestino = $("#codGrupoDestino").val();
    var coDepTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
//    if (!!codEmp && !!codGrupoDestino) {
    if (!!codEmp) {
        var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": coDepTabla, "co_emp": codEmp};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goAgregarEmpleadoDestino&codEmpActual=" + coEmpTabla;
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                removeDomId('windowConsultaEmpleados');
                fn_listaGrupoDetalle();
            } else {
                alert_Danger("ERROR:", "EXISTE UNA FILA CON LOS MISMOS DATOS.");
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Warning("MENSAJE", "ocurrio un error con la dependencia");
    }
}
function fn_cambiarEmpleado(cell) {
    GDindexCurrentSelectedCell = $(cell).parents("tr").index();

    //var codDepen = $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtCoDepen']").val();
    var codDepen = $(cell).attr("codep");

    //var deDepen = $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtDesDepen']").val();
//    return;
//    var coDepTabla = $(nodo).attr("codep");
//    var codGrupoDest = $("#codGrupoDestino").val();
//    if (!!coDepTabla) {
    if (!!codDepen) {
        var p = new Array();
        p[0] = "accion=goCargarEmpleados";
        p[1] = "codDepen=" + codDepen;
        //p[2] = "codGrupoDest=" + codGrupoDest;
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");
        return false;
    } else {
        alert_Warning("MENSAJE", "Primero seleccione una dependencia.");
    }
}
function fn_cargarGruposDestino() {
    var p = new Array();
    p[0] = "accion=goListaGruposDestinos";
    p[1] = "vCodDependencia=" + jQuery('#codDependencia').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleGrupoDestino", data);
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}
function resetValoresGrupoDestino() {
    reemDoc = false;
    reemMot = false;
    jsonDetGrupo = [];
    GDindexCurrentSelectedCell;
    nuevoGrupo = false;
    cambioDatos = false;
    $("#btn-cerrar").hide();
}
function fn_cambioNombreGD() {
    cambioDatos = true;
}
function fn_cancelarGuardarGD() {
    $("#btn-nuevo").show();
    $("#btn-cerrar").hide();
    $("#btn-eliminar").show();
    
    $("#txtDesDependenciaTemp").hide();    
    $("#busDependenciaGD").show();
    $("#txtNombreGrupo").prop("readonly", true);
    
    var muestraGrupos = "#tablaDependencia tr:eq(1)";
    $(muestraGrupos).show();
}

function fn_admBuscarDep() {
    var busDep = $("#txtBusDependencia").val();

    /**
     LAAH - Modificacion 21092015- Recomendacion 01 Y 02 : Falta de sintaxis
    if (!!!busDep)
    {
     //alert_Danger("ERROR", "  Ingrese un datos de busqueda");
     alert_Danger("ERROR", "  Ingrese datos de búsqueda");
        return;
    }
     */

    var p = new Array();
    p[0] = "accion=goBuscaAdminUO";
    p[2] = "busDep=" + busDep.toUpperCase();
    p[3] = "busTipo="+$('#cbBusTipo').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDependenciaDetalle", data);
        loadding(false);
        var c = $("#tblDepDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}

function fu_goEditarAdmUUOO() {
    var vcoDep = $("#pcoDep").val();
    if (!!vcoDep) {
        ajaxCall("/srTablaConfiguracion.do?accion=goEditAdminUO&pcoDep=" + vcoDep, '', function(data) {
            jQuery('#divDependenciaBody').hide();
            jQuery('#divDependenciaEdit').show();
            refreshScript("divDependenciaEdit", data);
            jQuery('#divDependenciaDetalle').html("");
        }, 'text', false, false, "GET");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

function fu_goNewAdmUUOO() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNewAdminUO", '', function(data) {
        jQuery('#divDependenciaBody').hide();
        jQuery('#divDependenciaEdit').show();
        refreshScript("divDependenciaEdit", data);
        jQuery('#divDependenciaDetalle').html("");
    }, 'text', false, false, "GET");
}

function fn_eventAdmEmpleado(){
    /**
     * Evento para mostrar formulario y agregar nuevo empleado
     */
    $('#divAdmEmpleado #btn_nuevo').click(function(){
        fu_goNuevoAdmEmpleado();
    });
    
    /**
     * Evento para editar empleado
     */
    $('#divAdmEmpleado #btn_editar').click(function(){
        fu_goEditarAdmEmpleado();
    });
    
    /**
     * Evento para abandonar el panel de administración de empleados
     */
    $('#divAdmEmpleado #btn_salir').click(function(){
        cerrarPantallaConfigTabla();
    });
    
    /**
     * Evento seleccionar un empleado para luego editar
     */
    $(document).on('click','#divAdmEmpleado .row_emp',function(){
        if (!$(this).hasClass('row_selected')){
            $('#tblLsAdmEmpleado tbody tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');
            if (!!$(this).children('td')[0].innerHTML) {
                jQuery('#pCodEmp').val($(this).children('td')[0].innerHTML);
            }
        }
    });
    
    /**
     * Event submit para el formulario de búsqueda
     */
    $('#form_buscar_empleado .btn_bsq').click(function(e){
        fn_busquedaEmpleado();
        return false;
    });
    
    /**
     * Cuando se desea agregar nuevo empleado, estará disponible el evento que identifica
     * a la persona mediante su DNI para luego ser incorporado como nuevo empleado
     */
//    $(document).on('change','#formEditPersonal #dni',function(){
//        fn_getPersonaDesdeDni();
//    });
    
    /**
     * Ejecuta el cambio de estado del empleado
     */
    $(document).on('click','#formEditPersonal .cambiar_estado',function(e){
        fn_cambiarEstadoEmpleado(e.currentTarget);
    });
    $(document).on('click','#formEditPersonal .cambiar_estado_usuario',function(e){
        fn_cambiarEstadoUsuario(e.currentTarget);
    });
    /**
     * Evento grabar datos del empleado
     */
//    $(document).on('click','#formEditPersonal #btn_grabar',function(){
//        fn_grabarDatosPersonal();
//        return false;
//    });
    
    /**
     * Evento salir del formulario de nuevo y editar datos del personal
     */
    $(document).on('click','#formEditPersonal #btn_salir',function(){
        removeDomId('divOrigenMain');
    });
    
//    /**
//     * Muestra formulario (modal) para realizar búsqueda de dependencia para el empleado
//     */
//    $(document).on('click','#formEditPersonal #bsq_dependencia',function(){
//        fu_goAdmEmpleadoDependencia();
//    });
    
    /**
     * Cierra el formulario (modal) para realizar búsqueda de dependencia para el empleado
     */
    $(document).on('click','#windowConsultaDependencia #btn_salir',function(){
        removeDomId('windowConsultaDependencia');
    });
    
    /**
     * Crear usuario para el empleado
     */
    $(document).on('click','#formEditPersonal #user_add',function(){
        $('#user_btns').html('<button type="button" class="btn btn-default" id="user_cancel"><span class="glyphicon glyphicon-remove"></span> Cancelar</button>');
        var nom = $('#nombres').val();
        nom = nom.substring(0, 1);
        var pat = $('#apPaterno').val();
        pat = pat.replace(/[áÁ]/g,'A')  /*Reemplazar los caracteres especiales*/
                 .replace(/[éÉ]/g,'E')
                 .replace(/[íÍ]/g,'I')
                 .replace(/[óÓ]/g,'O')
                 .replace(/[úÚ]/g,'U')
                 .replace(/[ñÑ]/g,'N')
                 .replace(/[^a-zA-Z]/g,'') ; 
        pat = (pat.length>20?pat.substring(0, 19):pat);
        var item = '<tr><td style="width: 30%">Nombre usuario</td>'
            +'<td style="width:70%">'
                +'<div class="input-group">'
                    +'<input type="text" maxlength="20" class="form-control" id="username" name="username" value="'+nom+""+pat+'" style="padding-left:2px" />'
                    +'<a href="javascript:;" title="Verificar disponibilidad"  id="user_verify" class="input-group-addon" style="padding:3px 7px;color:#26A0F0;"><span class="glyphicon glyphicon-search"></span></a>'
                +'</div>'
            +'<td></tr><tr style="height:20px;"></tr>'
        +'<tr><td>Autenticacion :</td><td><select id="sInAD" class="btn btn-info dropdown-toggle btn-sm" ><option value="0" class="cambiar_estado">Constraseña del SGD </option>'    
        +'<option value="1" class="cambiar_estado">Active Directory</option></select></td></tr>';
        $('#user_form').html(item);
    });
    
    /**
     * Elimina el usuario del Empleado
     */
//    $(document).on('click','#formEditPersonal #user_delete',function(){
//        var answer = confirm('¿Desea eliminar al usuario?');
//        if(answer){
//            var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val()});
//            ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoEliminarAcceso', jsonString, function(data) {
//                if(data.exito){
//                    $('#user_btns').html('<button type="button" class="btn btn-default" id="user_add"><span class="glyphicon glyphicon-plus"></span> Nuevo usuario</button>');
//                    $('#user_form').html('<td><span style="color:red;">'+data.mensaje+'</span></td>');
//                    $('#div_user_verify').html('');
//                }else{
//                    alert_Danger("ERROR:", data.mensaje);
//                }
//            }, 'json', false, false, 'POST');
//        }
//    });
    
    /**
     * Reestablece su contraseña
     */
//    $(document).on('click','#formEditPersonal #user_reestablish',function(){
//        var answer = confirm('La contraseña se va a restablecer \n ¿Desea continuar?');
//        if(answer){
//            var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val()});
//            ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoRestablecerAcceso', jsonString, function(data) {
//                if(data.exito){
//                    //$('#user_btns').html('<button type="button" class="btn btn-default" id="user_add"><span class="glyphicon glyphicon-plus"></span> Nuevo usuario</button>');
//                    //$('#user_form').append('<span style="color:red;">'+data.mensaje+'</span>');
//                    $('#div_user_verify').html('<span style="color:red;">'+data.mensaje+'</span>');
//                }else{
//                    alert_Danger("ERROR:", data.mensaje);
//                }
//            }, 'json', false, false, 'POST');
//        }
//    });
    
    /**
     * Cancela la creación de USUARIO 
     */
    $(document).on('click','#formEditPersonal #user_cancel',function(){
        $('#user_btns').html('<button type="button" class="btn btn-default" id="user_add"><span class="glyphicon glyphicon-plus"></span> Nuevo usuario</button>');
        $('#user_form').html('');
        $('#div_user_verify').html('');
    });
    
    /**
     * Realiza verficación del USERNAME del empleado
     */
    $(document).on('click','#formEditPersonal #user_verify',function(){
        var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val(),"coUsuario":$('#username').val()});
            ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoVerificarUsername', jsonString, function(data) {
                if(data.exito){
                    if(data.list.length>0){
                        $('#div_user_verify').html(fu_muestraLsNombreUsuario(data.list));
                    } else {
                        $('#div_user_verify').html('<br /><h4 style="color:green;">Nombre de usuario disponible</h4>');
                    }
                }else{
                    alert_Danger("ERROR:", data.mensaje);
                }
            }, 'json', false, false, 'POST');
    });
}

function fu_muestraLsNombreUsuario(list){
    var item = '<br />'
            +'<h4 style="color:red;">Usuarios existentes</h4>'
            +'<table class="ui-state-default" style="width:100%;">'
                +'<thead><tr><th style="width:30%">Usuario</th><th style="width:70%">Nombre Empleado</th></tr></thead>'
            +'</table>'
            +'<table class="ui-datatable-data" style="width:100%;">'
                +'<tbody>';
                    $.each(list,function(i,v){
                        item+='<tr class="ui-datatable-odd">'
                            +'<td style="width:30%">'+v.coUsuario+'</td><td style="width:70%">'+v.deUsuario+'</td>'
                        +'</tr>';
                    });
                item+='</tbody>'
            +'</table>';
    return item;
}

/**
 * Muestra formulario para agregar nuevo empleado
 * @returns
 */
function fu_goNuevoAdmEmpleado() {
    ajaxCall('/srTablaConfiguracion.do?accion=goNewAdmEmpleado', '', function(data) {
        $('body').append(data);
    }, 'text', false, false, 'GET');
}

/**
 * Muestra formulario para editar empleado
 * @returns
 */
function fu_goEditarAdmEmpleado() {
    var vcodEmp = $("#pCodEmp").val();
    if (!!vcodEmp) {
        ajaxCall("/srTablaConfiguracion.do?accion=goEditAdmEmpleado&codEmp="+vcodEmp, '', function(data) {
            $("body").append(data);
        }, 'text', false, false, "GET");
    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

/**
 * Función que realiza búsqueda de empleado de acuerdo a los
 * criterios del formulario de búsqueda
 * @returns 
 */
function fn_busquedaEmpleado() {
    var url = "/srTablaConfiguracion.do?accion=goBsqAdmEmpleado";
    ajaxCall(url, $('#form_buscar_empleado').serialize(), function(data) {
        $('#divListaEmpleados').html(data);
    }, 'text', false, false, "POST");
    return false;
}

/**
 * Obtiene datos de una persona mediante su DNI, para luego agregarla como nuevo empleado
 * @returns
 */
function fn_getPersonaDesdeDni(objEvent,currentObj){
    var vReturn=0;
    var url = "/srTablaConfiguracion.do?accion=goGetPersonaDesdeDni";
    if($('#dni').val().length===8&&fu_esNumerico($('#dni').val())==='SI'&&$('#dni').val()!==jQuery('#dni_old').val()){
        jQuery('#dni_old').val(jQuery('#dni').val());
        ajaxCall(url,{dni: $('#dni').val()}, function(data) {
            if(data.exito){
                if(!data.si_existe && data.disponible){
                    $('#apPaterno').val(data.apPaterno.trim());
                    $('#apMaterno').val(data.apMaterno.trim());
                    $('#nombres').val(data.nombres.trim());
                    $('#fechaNacimiento').val(data.fechaNacimiento.trim());
                    if(data.sexo==1){
                        $('#sexo2').removeAttr('checked');
                        $('#sexo1').prop('checked',true);
                    } else if(data.sexo==2) {
                        $('#sexo1').removeAttr('checked');
                        $('#sexo2').prop('checked',true);
                    }
                    vReturn=1;
                } else {
                    fn_cleanPersonaDesdeDni();
                    alert_Warning("ATENCIÓN",data.mensaje);
                }
            }else{
                fn_cleanPersonaDesdeDni();
                alert_Danger("ERROR:", data.mensaje);
            }
        }, 'json', false, false, "POST");
    }
    return !!vReturn;
}  

/**
 * Ejecuta el cambio de estado del empleado
 */
function fn_cambiarEstadoEmpleado(target){
    var valor = $(target).attr('data-value');
    $('#estado').val(valor);
    if(valor==1){
        $('#divEstado0').hide();
        $('#divEstado1').show();
    } else {
        $('#divEstado0').show();
        $('#divEstado1').hide();
    }
    return false;
}
    function fn_cambiarEstadoUsuario(target){
    var valor = $(target).attr('data-value');
    /*console.log(valor);*/
    $('#estadousuario').val(valor);
    $('#divEstadoA').hide();
    $('#divEstadoI').hide();
    $('#divEstadoN').hide();
    $('#divEstadoB').hide();
    $('#divEstadoX').hide();
    $('#divEstadoM').hide();
    $('#divEstado'+valor).show();
     user_cambiarEstado(valor);
    return false;
}

/**
 * Función que permite guardar datos del empleado
 * @returns 
 */
function fn_grabarDatosPersonal() {
    var coEmpleado = $('#codEmp').val();
    var dni = $('#dni').val();
    var apPaterno = $('#apPaterno').val();
    var apMaterno = $('#apMaterno').val();
    var nombres = $('#nombres').val();
    var fechaNacimiento = $('#fechaNacimiento').val();
    var sexo = $('input[name="sexo"]:checked').val();
    var coDependencia = $('#coDependencia').val();
    var coCargo = $('#coCargo').val();
    var coCategoria = $('#coCategoria').val();
    var coObservacion = $('#coObservacion').val();
    var coLocal = $('#coLocal').val();
    var username = '';
    if($('#username').length>0){
        username = $('#username').val();
    }
    if(dni!="" && dni.length!=8){
        bootbox.alert('Ingrese DNI válido.');
        return false;
    }
    if($('#email').val()!="" &&  !fu_IsValidaCorreo($('#email').val())){
        return false;
    }
    
    var inAD = $('#sInAD').val();
//    if(!!dni && !!apPaterno && !!apMaterno && !!nombres && !!coDependencia && !!sexo){
    if(!!dni && (!!apPaterno || !!apMaterno) && !!nombres && !!coDependencia && !!coLocal){        
        var jsonBody ={
                "empleado" :{
                    "coEmpleado": coEmpleado, "dni": dni, "apPaterno": apPaterno,"apMaterno": apMaterno, 
                    "nombres": nombres,"fechaNacimiento":fechaNacimiento,"sexo":sexo,"coDependencia": coDependencia,
                    "email":$('#email').val(),"estado":$('#estado').val(),
                    "coCargo":coCargo,"coCategoria":coCategoria,"coObservacion":coObservacion,"coLocal":coLocal
                },
                "acceso":{
                    "coEmpleado" : coEmpleado, "coUsuario":username,"inAD":inAD
                }
            };
        var jsonString = JSON.stringify(jsonBody);
        var url = '/srTablaConfiguracion.do?accion=goUpdAdmEmpleado';
        ajaxCallSendJson(url, jsonString, function(data) {
            if(data.exito){
                if(typeof data.list !== 'undefined'){
                    alert_Warning("MENSAJE",data.mensaje);
                    $('#div_user_verify').html(fu_muestraLsNombreUsuario(data.list));
                }else{
                    $('#formEditPersonal #btn_salir').trigger('click');
                    var item = '<td>'+data.coEmpleado+'</td><td>'+data.dni+'</td><td>'+data.apPaterno+'</td>'
                                +'<td>'+data.apMaterno+'</td><td>'+data.nombres+'</td><td>'+data.deDependencia+'</td><td>'+data.deCargo+'</td>'
                                +'<td>'+((data.estado==1) ? '<span class="label label-success">Activo</span>':'<span class="label label-danger">Baja</span>')+'</td>'
                                +'<td>'+ (typeof data.usuario !== 'undefined'? data.usuario:'')+'</td>' 
                               +'<td>'+((data.estadousuario=='Activo') ? '<span class="label label-success">Activo</span>':'<span class="label label-danger">'+data.estadousuario+'</span>')+'</td>'
                               +'<td>'+((data.inAD=='0') ? '<span class="label label-info">SGD</span>':'<span class="label label-info">Act. Directory</span>')+'</td>';
                    if($('#tr_'+data.coEmpleado).length>0){ //Editar empleado
                        $('#tr_'+data.coEmpleado).html(item);
                    }else{//nuevo empleado
                        $('#tblLsAdmEmpleado tbody').prepend('<tr id="tr_'+data.coEmpleado+'" class="row_emp">'+item+'</tr>');
                    }
                    alert_Sucess("MENSAJE:", data.mensaje);
                }
            }else{
                alert_Danger("ERROR:", data.mensaje);
            }
        }, 'json', false, false, 'POST');
    } else {
        alert_Warning('MENSAJE', 'No se completó todos los datos requeridos');
    }
    return false;
}

/**
 * Muestra formulario (modal) para realizar búsqueda de dependencia para el empleado
 * @returns 
 */
function fu_goAdmEmpleadoDependencia() {
    ajaxCall("/srTablaConfiguracion.do?accion=goAdmEmpleadoDependencia",{"criterio":$('#deDependencia').val()}, function(data) {
        $("body").append(data);
    }, 'text', false, false, "POST");
}

function valFormProveedorConfigGeneral(){
    var valRetorno = '1';
    var pruc = $("#txtRuc").val();
    var prazonSocial = $("#txtRazonSocial").val();
    var pdir = $("#txtDireccion").val();
    var ptelefono = $("#txtTelefono").val();  
    
    if(!!pruc){
        /*LAAH 21092015 - variable verifica la expresion regular solo numeros */
        var rx = new RegExp(/^\d+$/);
        var nrolinespRuc = (pruc.match(/\n/g) || []).length;
        var maxCarateres=11;
        var rucLength=pruc.length+nrolinespRuc;
        if(rucLength>maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Número de RUC excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtRuc").focus();
            });
        }
        /*LAAH 22092015 - Se agrego condicional - verifica si el campo coincide con la expresion regular */
        else if (!rx.test(pruc)) {
            valRetorno = '0';
            bootbox.alert("<h5>Número del RUC inválido.</h5>", function () {
                bootbox.hideAll();
                jQuery("#txtRuc").focus();
            });
        }else{
          if(rucLength<maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Número de RUC inválido.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtRuc").focus();
            });              
          }  
        }
    }
    /*LAAH 22092015 - Se agrego condicional - verifica que el campo ruc no sea vacio */
    else {
        valRetorno = '0';
        bootbox.alert("<h5>Ingrese RUC, es un campo obligatorio.</h5>", function () {
            bootbox.hideAll();
            jQuery("#txtRazonSocial").focus();
        });
    }
    if(valRetorno==='1'){
        if(!!prazonSocial&&allTrim(prazonSocial).length>1){
            /*LAAH 21092015 - variable verifica la expresion regular permita solo algunos caracteres */
            var rx = new RegExp(/^[a-zA-Z0-9üéáíóúñÁÉÍÓÚÜÑ\-\s\'\´&*)(+=._-]*$/);
            var nrolinespRazonSocial = (prazonSocial.match(/\n/g) || []).length;
            var maxCarateres=100;
            if(prazonSocial.length+nrolinespRazonSocial > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Razón Social excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
            }            
            /*LAAH 22092015 - Se agrego condicional - verifica si el campo coincide con la expresion regular */
            else if (!rx.test(prazonSocial)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar razón social, existe caracter no permitido.</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
            }
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese razón social, es un campo obligatorio.</h5>", function () {
                bootbox.hideAll();
                jQuery("#txtRazonSocial").focus();
            });            
        }
    }
    
    if(valRetorno==='1'){
        if(!!pdir){
            /*LAAH 21092015 - variable verifica la expresion regular permita solo algunos caracteres */
            var rx = new RegExp(/^[a-zA-Z0-9üéáíóúñÁÉÍÓÚÜÑ#°\-\s\'\´&*)(+=._-]*$/);
            var nrolinespDir = (pdir.match(/\n/g) || []).length;
            var maxCarateres=100;
            if(pdir.length+nrolinespDir > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Dirección excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtDireccion").focus();
                });
            }
            /*LAAH 22092015 - Se agrego condicional - verifica si el campo coincide con la expresion regular */
            else if (!rx.test(pdir)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar dirección, existe caracter no permitido.</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtDireccion").focus();
                });
            }
        }        
    }
    
    if(valRetorno==='1'){
        if(!!ptelefono){
            /*LAAH 22092015 - variable verifica la expresion regular valide telefono y permita guiones y espacios */
            var rx = new RegExp(/^[0-9\-().\s]{3,20}$/);
            var nrolinespTele = (ptelefono.match(/\n/g) || []).length;
            var maxCarateres=30;
            if(ptelefono.length+nrolinespTele > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Teléfono excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtTelefono").focus();
                });
            }
            /*LAAH 22092015 - Se agrego condicional - verifica si el campo coincide con la expresion regular */
            else if (!rx.test(ptelefono)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar teléfono, existe caracter no permitido.</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtTelefono").focus();
                });
            }
        }        
    }    
    return valRetorno==='1';
}
function valFormTupaConfigGeneral(){
    var valRetorno      = '1';
    var pDeNombre       = $("#txtDeNombre").val();      //Descripcion corta de tupa
    var pDeDescripcion  = $("#txtDeDescripcion").val(); //Descripcion larga de tupa
    var pNuPlazo        = $("#txtNuPlazo").val();       //Plazo atención en dias
    
    //bootbox.alert("pDeNombre**"+pDeNombre +"***pNuPlazo***"+pNuPlazo+"****");
    
    if(allTrim(pDeNombre).length<1){
        valRetorno = '0';
        bootbox.alert("<h5>Ingrese descripción corta de Tupa, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtDeNombre").focus();
        });  
    }
    if(allTrim(pNuPlazo).length<1){
        valRetorno = '0';
        bootbox.alert("<h5>Ingrese Plazo de atencion en dias, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtNuPlazo").focus();
        });  
    }
  
    return valRetorno;
}
function valFormOtroOrigenConfigGeneral(){
    var valRetorno = '1';
    //var pidOtroOri = $("#idOtroOri").val();
    //var pcodTipoDoc = $("#cbTipoDoc").val();
    var pNumeroDoc = $("#txtNumeroDoc").val();
    var pPaterno = $("#txtPaterno").val();
    var pMaterno = $("#txtMaterno").val();
    var pNombres = $("#txtNombres").val();
    var pRazonSocial = $("#txtRazonSocial").val();
    var pDireccion = $("#txtDireccion").val();  
    
    if(!!pNumeroDoc){
        var nrolinespNumeroDoc = (pNumeroDoc.match(/\n/g) || []).length;
        var maxCarateres=10;
        var numeroDocLength=pNumeroDoc.length+nrolinespNumeroDoc;
        if(numeroDocLength>maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Número de documento excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtNumeroDoc").focus();
            });
        }
    }
    if(valRetorno==='1'){
        if(!!pPaterno){
            var nrolinespPaterno = (pPaterno.match(/\n/g) || []).length;
            var maxCarateres=50;
            var paternoLength=pPaterno.length+nrolinespPaterno;
            if(paternoLength>maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Apellido Paterno excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtPaterno").focus();
                });
            }
        }        
    }
    if(valRetorno==='1'){
        if(!!pMaterno){
            var nrolinespMaterno = (pMaterno.match(/\n/g) || []).length;
            var maxCarateres=50;
            var maternoLength=pMaterno.length+nrolinespMaterno;
            if(maternoLength>maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Apellido Materno excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtMaterno").focus();
                });
            }
        }        
    }    
    if(valRetorno==='1'){
        if(!!pNombres){
            var nrolinespNombres = (pNombres.match(/\n/g) || []).length;
            var maxCarateres=100;
            var nombreLength=pNombres.length+nrolinespNombres;
            if(nombreLength>maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Nombres excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtNombres").focus();
                });
            }
        }        
    }    
    if(valRetorno==='1'){
        if(!!pRazonSocial&&allTrim(pRazonSocial).length>1){
            var nrolinespRazonSocial = (pRazonSocial.match(/\n/g) || []).length;
            var maxCarateres=100;
            if(pRazonSocial.length+nrolinespRazonSocial > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Razón Social excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
            }            
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Razón Social/Nombre Completo campo obligatorio.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtRazonSocial").focus();
            });            
        }
    }
    
    if(valRetorno==='1'){
        if(!!pDireccion){
            var nrolinespDir = (pDireccion.match(/\n/g) || []).length;
            var maxCarateres=100;
            if(pDireccion.length+nrolinespDir > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Dirección excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtDireccion").focus();
                });
            }
        }        
    }
    return valRetorno==='1';
}
function valFormCiudadano(){
    var valRetorno = '1';

    var pNumLem   = allTrim($("#txtNuLem").val());
    var pDeApp    = allTrim(fu_getValorUpperCase($("#txtDeApp").val()));
    var pDeApm    = allTrim(fu_getValorUpperCase($("#txtDeApm").val()));
    var pDeNom    = allTrim(fu_getValorUpperCase($("#txtDeNom").val())); 
    var maxCarateresDni=8;
    
    /*if(pNumLem.length === 0){
        var nrolines_pNumLem = (pNumLem.match(/\n/g) || []).length;
        var maxCarateres=8;
        var numeroDocLength= pNumLem.length;
        //var numeroDocLength=pNumLem.length+nrolines_pNumLem;
        if(numeroDocLength<maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Número de documento debe tener "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtNumeroDoc").focus();
            });
        }
        if(numeroDocLength>maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Número de documento debe tener "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtNumeroDoc").focus();
            });
        }
    }*/
    if(!!pNumLem){
        var lnuDniAux=pNumLem.length;
        var vValidaNumero = fu_validaNumero(pNumLem);
        if (vValidaNumero !== "OK") {
            valRetorno = '0';
            bootbox.alert("<h5>Número de documento debe ser solo números.</h5>", function() {
                bootbox.hideAll();
                jQuery("#txtNumeroDoc").focus();
            }); 
        }
        if(valRetorno==='1'){
            if(lnuDniAux!==8){
                valRetorno = '0';
                bootbox.alert("<h5>Número de documento debe tener "+maxCarateresDni+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery("#txtNumeroDoc").focus();
                });                
            }
        }
    }else{
        valRetorno = '0';
        bootbox.alert("<h5>Digite Número de documento.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtNumeroDoc").focus();
        });        
    }
    
    if(valRetorno==='1'){
     if(!pDeApp && !pDeApm){
         valRetorno = '0';
         bootbox.alert("<h5>Debe ingresar por lo menos un Apellido Paterno o Materno.</h5>"); 
     }
    }
    
    if(valRetorno==='1'){
        if(!!pDeApp){
            var rptValida = validaCaracteres(pDeApp, "2");
            if(rptValida === "OK"){
                // Expresion regular
                var cadena ="^[a-zA-Z0-9ÑÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕñäëïöüáéíóúàèìòùâêîôûãõ' .-]+$";
//                var cadena = "^["+public_apenom+"]+$";
                var re = new RegExp(cadena);
                if(!pDeApp.match(re)){ 
                    valRetorno = '0';
                    bootbox.alert("<h5>Apellido Paterno<br>Los caracteres ingresados deben contener solo números y letras.</br></h5>");                                    
                }
            }else{
                valRetorno = '0';
                rptValida=rptValida.substr(2);
                bootbox.alert("<h5>Apellido Paterno<br>"+rptValida+"</br></h5>");               
            }
        }else{
//            valRetorno = '0';
//            bootbox.alert("<h5>Debe ingresar Apellido Paterno del Ciudadano.</h5>"); 
            $("#txtDeApp").val("");
        }
    }
    if(valRetorno==='1'){
        if(!!pDeApm){
            var rptValida = validaCaracteres(pDeApm, "2");
            if(rptValida === "OK"){
                // Expresion regular
                var cadena ="^[a-zA-Z0-9ÑÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕñäëïöüáéíóúàèìòùâêîôûãõ' .-]+$";
//                var cadena = "^["+public_apenom+"]+$";
                var re = new RegExp(cadena);
                if(!pDeApm.match(re)){ 
                    valRetorno = '0';
                    bootbox.alert("<h5>Apellido Materno<br>Los caracteres ingresados deben contener solo números y letras.</br></h5>");                                    
                }
            }else{
                valRetorno = '0';
                rptValida=rptValida.substr(2);
                bootbox.alert("<h5>Apellido Materno<br>"+rptValida+"</br></h5>");            
            }
        }else{
//            valRetorno = '0';
//            bootbox.alert("<h5>Debe ingresar Apellido Materno del Ciudadano.</h5>"); 
            $("#txtDeApm").val("");
        }
    }    
    
    if(valRetorno==='1'){
        if(!!pDeNom){
            var rptValida = validaCaracteres(pDeNom, "2");
            if(rptValida === "OK"){
                // Expresion regular
                var cadena ="^[a-zA-Z0-9ÑÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕñäëïöüáéíóúàèìòùâêîôûãõ' .-]+$";
//                var cadena = "^["+public_apenom+"]+$";
                var re = new RegExp(cadena);
                if(!pDeNom.match(re)){ 
                    valRetorno = '0';
                    bootbox.alert("<h5>Los caracteres ingresados deben contener solo números y letras.</h5>");                                    
                }
            }else{
                valRetorno = '0';
                rptValida=rptValida.substr(2);
                bootbox.alert("<h5>Nombre<br>"+rptValida+"</br></h5>");            
            }
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Debe ingresar Nombre del Ciudadano.</h5>");  
        }
    }     
       
    return valRetorno==='1';
}


function fn_cerrarEditDepAdm() {
    jQuery('#divDependenciaEdit').toggle();
    jQuery('#divDependenciaEdit').html("");
    jQuery('#divDependenciaBody').toggle();
    var busDep = $("#txtBusDependencia").val();
    if(!!busDep){
        fn_admBuscarDep();
    }else{
        ajaxCall("/srTablaConfiguracion.do?accion=goAdminUO", '', function(data) {
            refreshScript("divDependenciaDetalle", data);
        }, 'text', false, false, "POST");        
    }
    return false;
}

function fn_grabarDependenciaAdm(){
    var noForm='#dependenciaBean';
    var idTblEmp='#tblIntegrantesUUOO';
    var msg='Corregir Integrante';
    var pTipoAnt = $('#pTipoAnt').val();
    fn_delFilaTblDepenAdmUo(idTblEmp);
    //if (valFormDependenciaConfigGeneral(noForm) && fn_verificarLstIntegrantesDepUOCorrecto(idTblEmp, msg)) {
    if(valFormDependenciaConfigGeneral(noForm)&&fn_verificarLstIntegrantesDepUOCorrecto(idTblEmp,msg)){
        var objTrxJson=fn_getObjTrxJsonDepenAdmUO(noForm);
        var rpta=fn_verificarChangeAdmDepenUO(new Function('return '+objTrxJson)());        
        var nrpta=rpta.substr(0, 1);
        if(nrpta === "1"){
            ajaxCallActionFormSendJson(jQuery(noForm).attr('action')+"&pTipoAnt="+$("#pTipoAnt").val()+"&coLocal="+jQuery(noForm).find('#coLocal').val(),objTrxJson, function(data) {
                fn_rptaGrabaDependenciaAdm(data,noForm,idTblEmp);
            },'json', false, false, "POST");    
        }else{
            alert_Info("", rpta.substr(1));
        }
    }
}

function fn_delFilaTblDepenAdmUo(idTblEmp){
   jQuery(idTblEmp+' tbody tr').each(function (index){
       if(index>0){
           var ptr=jQuery(this);
           if(ptr.hasClass('oculto')||ptr.find('td').eq('1').text()==="DEL"){
               ptr.remove();
           }
       }
   });    
}

function fn_verificarChangeAdmDepenUO(objTrxDependencia){
    var vReturn = "0LA UNIDAD ORGANICA ES LA MISMA.";    
    if (!!objTrxDependencia) {
        if (!!objTrxDependencia.dependencia) {
            vReturn = "1A GRABAR";
        } else if (!!objTrxDependencia.lstIntegrante) {
            if (objTrxDependencia.lstIntegrante.length > 0) {
                vReturn = "1A GRABAR";
            }
        }
    }
    return vReturn;
}

function fn_getObjTrxJsonDepenAdmUO(noForm){
    var result="{";
    result = result + '"coDep":"' + jQuery(noForm).find("#coDependencia").val() + '",';
    
    //result = result + '"nuEmi":"' + $("#nuEmi").val() + '",';
    var valEnvio = jQuery(noForm).find('#envDependenciaBean').val();
    if(valEnvio==="1"){
        result = result + '"dependencia":' + JSON.stringify(getJsonFormDependenciaConfGenBean(noForm)) + ',';
    }
    result= result + '"lstIntegrante":' + sortDelFirst(fn_tblEmpDepenUnOrgToJson());
    return result + "}";
}

function getJsonFormDependenciaConfGenBean(noForm){
    var arrCampoBean = new Array();  
    arrCampoBean[0] = "coDependencia";
    arrCampoBean[1] = "deDependencia";
    arrCampoBean[2] = "tituloDep";
    arrCampoBean[3] = "coDepenPadre";
    arrCampoBean[4] = "coEmpleado";
    arrCampoBean[5] = "coCargo";
    arrCampoBean[6] = "deCargoCompleto";
    arrCampoBean[7] = "coNivel";
    arrCampoBean[8] = "deSigla";
    arrCampoBean[9] = "deCortaDepen";
    arrCampoBean[10] = "inBaja";
    arrCampoBean[11] = "coTipoEncargatura";
    arrCampoBean[12] = "tiDependencia";
    arrCampoBean[13] = "coObservacion";
    var o = {};
    arrCampoBean.forEach(function(campo) {
        var valCampo=jQuery(noForm).find('#'+campo).val();
        if(!!valCampo){
            o[campo]=valCampo;
        }else{
            o[campo]=null;
        }
    });
    return o;     
}

function fn_tblEmpDepenUnOrgToJson() {
    var arrColMostrar = new Array();
    arrColMostrar[0] = "accionBD=2";
    arrColMostrar[1] = "cempCodemp=3";
    return '['+fn_tblDestEmihtml2json('tblIntegrantesUUOO', 1, arrColMostrar, 7, "1", 1, "BD")+']';
}

function fn_rptaGrabaDependenciaAdm(data,noForm,idTblEmp){
    if(!!data){
        if(data.coRespuesta==="1"){
            jQuery(noForm).find('#envDependenciaBean').val("0");
            var action=data.action;
            if(!!action){
                var p = new Array();
                p[0] = action;
                p[1] = "pcoDep=" + data.coDep;
                p[2] = "pTipoAnt=" + data.coDep;
                var actionForm=jQuery('#dependenciaBean').attr('action');
                var n = actionForm.search("srTablaConfiguracion");
                var actionForm = actionForm.substr(0,n)+p.join("&");
                jQuery(noForm).attr('action',actionForm);
            }
            fn_actualizarTblIntegranteUO(idTblEmp);
            alert_Sucess("Dependencia :", "Datos Guardados.");
        }else{
            alert_Info("Dependencia :", data.deRespuesta);
        }
    }
}

function fn_actualizarTblIntegranteUO(idTblEmp){
   jQuery(idTblEmp+' tbody tr').each(function (index){
       if(index>0){
           var ptr=jQuery(this);
           if(ptr.hasClass('oculto')){
               ptr.remove();
           }else{
               ptr.find('td').eq('1').text("BD");
           }
       }
   });
}

function fn_buscaEmpleadoEncargadoAdm(){
    ajaxCall("/srTablaConfiguracion.do?accion=goListaEmpEncargado", '', function(data) {
        fn_rptaBuscaEmpleadoEncargadoAdm(data);
    },'text', false, false, "POST");        
}

function fn_rptaBuscaEmpleadoEncargadoAdm(XML_AJAX){
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }    
}

function fn_buscaCargoEmpleadoAdm(idCodigo,idDescripcion){
    //var coDepOrigen = jQuery('#buscarDocumentoEmiConsulBean').find('#coDepOrigen').val();
    //alert("codigo: "+idCodigo+" , des: "+idDescripcion);
    ajaxCall("/srTablaConfiguracion.do?accion=goListaEmpleo", {"idCodigo":idCodigo,"idDescripcion":idDescripcion}, function(data) {
        fn_rptaBuscaCargoEmpleadoAdm(data);
    },'text', false, false, "POST");    
}

function fn_rptaBuscaCargoEmpleadoAdm(XML_AJAX){
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }    
}

function fu_setDatoCargoNewDependencia(idCodigo,idDescripcion,codSelec,desSelec){
    //alert("codigo: "+idCodigo+" , des: "+idDescripcion);
//    var noForm='#dependenciaBean';
//    jQuery(noForm).find('#'+idDescripcion).val(desSelec);
//    jQuery(noForm).find('#'+idCodigo).val(codSelec);
//    jQuery(noForm).find('#envDependenciaBean').val("1"); 
    
    jQuery('#'+idDescripcion).val(desSelec);
    jQuery('#'+idCodigo).val(codSelec);
    jQuery('#envDependenciaBean').val("1"); 
    removeDomId('windowConsultaElaboradoPor');
}

function fn_iniConsCargoEmpNewDepen(idCodigo,idDescripcion){
    //alert("codigo: "+idCodigo+" , des: "+idDescripcion);
        var tableaux = $('#tblElaboradoPor');
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tblElaboradoPor');
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
                if (found === true) {
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
                if(evento.which === 13){
                    if(isFirst){
                        var pdesSelec= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodSelec= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoCargoNewDependencia(idCodigo,idDescripcion,pcodSelec,pdesSelec);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tblElaboradoPor tbody tr").click(function(e) {
            var pdesSelec= $(this).find("td:eq(0)").html();
            var pcodSelec= $(this).find("td:eq(1)").html();
            fu_setDatoCargoNewDependencia(idCodigo,idDescripcion,pcodSelec,pdesSelec);            
        });
}

function fn_iniNewDependenciaEmpEncarg(){
        var tableaux = $('#tblElaboradoPor');
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tblElaboradoPor');
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
                if (found === true) {
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
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoEmpEncargaturaNewDep(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tblElaboradoPor tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoEmpEncargaturaNewDep(pcodDest,pdesDest);            
        });     
}

function fu_setDatoEmpEncargaturaNewDep(cod, desc) {
    var noForm='#dependenciaBean';
    jQuery(noForm).find('#deEmpleado').val(desc);
    jQuery(noForm).find('#coEmpleado').val(cod);
    jQuery(noForm).find('#envDependenciaBean').val("1"); 
    removeDomId('windowConsultaElaboradoPor');
}

function fn_buscaDependenciaPadreUOAdm(){
    ajaxCall("/srTablaConfiguracion.do?accion=goBuscaDepPadre", '', function(data) {
        fn_rptaBuscaDependenciaPadreUOAdm(data);
    },'text', false, false, "POST");       
}

function fn_rptaBuscaDependenciaPadreUOAdm(XML_AJAX){
    if (XML_AJAX !== null) {
        $("body").append(XML_AJAX);
    }    
}

function fn_iniDepPadreNewUOAdm(){
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
                if(evento.which == 13){
                    if(isFirst){
                        var pdesDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        var pcodDest= $("#tlbDestinoEmi tbody tr:eq("+indexSelect+")").find("td:eq(2)").html();
                        fu_setDatoDepPadreUOAdm(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tlbDestinoEmi tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(1)").html();
            var pcodDest= $(this).find("td:eq(2)").html();
            fu_setDatoDepPadreUOAdm(pcodDest,pdesDest);            
        });    
}

function fu_setDatoDepPadreUOAdm(cod, desc) {
    var noForm='#dependenciaBean';
    jQuery(noForm).find('#deDepenPadre').val(desc);
    jQuery(noForm).find('#coDepenPadre').val(cod);
    jQuery(noForm).find('#envDependenciaBean').val("1"); 
    removeDomId('windowConsultaDestinoEmi');
}

function valFormDependenciaConfigGeneral(noForm){
    var valRetorno = '1';
    var pcoDep = jQuery(noForm).find("#coDependencia").val(fu_getValorUpperCase(jQuery(noForm).find("#coDependencia").val())).val();
    var pdeDep = jQuery(noForm).find("#deDependencia").val(fu_getValorUpperCase(jQuery(noForm).find("#deDependencia").val())).val();
    var pcoNivel = jQuery(noForm).find("#coNivel").val(fu_getValorUpperCase(jQuery(noForm).find("#coNivel").val())).val();
    var pdeSigla = jQuery(noForm).find("#deSigla").val(fu_getValorUpperCase(jQuery(noForm).find("#deSigla").val())).val();
    var ptituloDep = jQuery(noForm).find("#tituloDep").val();
    var pdeCargoCompleto = jQuery(noForm).find("#deCargoCompleto").val();
    var pdeCortaDepen = jQuery(noForm).find("#deCortaDepen").val(fu_getValorUpperCase(jQuery(noForm).find("#deCortaDepen").val())).val();
    var pcoDepid = $("#tiDependencia").val();
    if(pcoDep.trim().length>0){
        if(!!pcoDep&&allTrim(pcoDep).length>1){
            var nrolinespCoDep = (pcoDep.match(/\n/g) || []).length;
            var maxCarateres=5;
            if(pcoDep.length+nrolinespCoDep > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Código Dependencia excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#coDependencia").focus();
                });
            }            
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Código Dependencia Válido.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#coDependencia").focus();
            });            
        }
    }else if(pcoDepid ==='0' && pcoDep.trim().length < 1){
        valRetorno = '0';
            bootbox.alert("<h5>Ingrese Código Dependencia campo obligatorio.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#coDependencia").focus();
            });            
    }
    
    if(valRetorno==="1"){
        if(!!pdeDep&&allTrim(pdeDep).length>1){
            var nrolinespDeDep = (pdeDep.match(/\n/g) || []).length;
            var maxCarateres=200;
            var rx = new RegExp(/^[a-zA-Z0-9üéáíóúñÁÉÍÓÚÜÑ\-\s\'\´&*)(+=._,-]*$/);/*se añadio el caracter coma*/
            if(pdeDep.length+nrolinespDeDep > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Descripción Dependencia excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#deDependencia").focus();
                });
            } else if (!rx.test(pdeDep)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar Descripción Dependencia, existe caracter no permitido.</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
            }            
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Descripción Dependencia campo obligatorio.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#deDependencia").focus();
            });            
        }        
    }
    
    if(valRetorno==="1"){
        if(!!pcoNivel&&allTrim(pcoNivel).length>0){
            var nrolinespCoNivel = (pcoNivel.match(/\n/g) || []).length;
            var maxCarateres=1;
            var rx = new RegExp(/^[1-6]+$/);
            if(pcoNivel.length+nrolinespCoNivel > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Nivel Dependencia excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#coNivel").focus();
                });
            } else if (!rx.test(pcoNivel)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar Nivel Dependencia, ingresar números entre 1 - 6 .</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
            }            
        }else{
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Nivel Dependencia campo obligatorio.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#coNivel").focus();
            });            
        }        
    }
    
    if(valRetorno==="1"){
        if(!!pdeSigla&&allTrim(pdeSigla).length>1){
            var nrolinespDeSigla = (pdeSigla.match(/\n/g) || []).length;
            var maxCarateres=50;
            if(pdeSigla.length+nrolinespDeSigla > maxCarateres){
                valRetorno = '0';
                bootbox.alert("<h5>Sigla de Dependencia excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                    bootbox.hideAll();
                    jQuery(noForm).find("#deSigla").focus();
                });
            }            
        } else {
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Sigla de Dependencia campo obligatorio, mínimo dos caracteres</h5>", function () {
                bootbox.hideAll();
                jQuery(noForm).find("#deSigla").focus();
            });
        }       
    }
    
    if(valRetorno==="1"){
      if(!!ptituloDep&&allTrim(ptituloDep).length>1){
        var nrolinespDeTitulo = (ptituloDep.match(/\n/g) || []).length;
        var maxCarateres=200;
            var rx = new RegExp(/^[a-zA-Z0-9üéáíóúñÁÉÍÓÚÜÑ\-\s\'\´&*)(+=._,-]*$/);
        if(ptituloDep.length+nrolinespDeTitulo > maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Título de Dependencia excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#tituloDep").focus();
            });
            } else if (!rx.test(ptituloDep)) {
                valRetorno = '0';
                bootbox.alert("<h5>Verificar Título de Dependencia, existe caracter no permitido.</h5>", function () {
                    bootbox.hideAll();
                    jQuery("#txtRazonSocial").focus();
                });
        }             
      }
    }
    
    if(valRetorno==="1"){
      if(!!pdeCargoCompleto&&allTrim(pdeCargoCompleto).length>1){
        var nrolinespDeCargoC = (pdeCargoCompleto.match(/\n/g) || []).length;
        var maxCarateres=200;
        if(pdeCargoCompleto.length+nrolinespDeCargoC > maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Descripción de Cargo excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#deCargoCompleto").focus();
            });
        }             
      }
    }    
    
    if(valRetorno==="1"){
      if(!!pdeCortaDepen&&allTrim(pdeCortaDepen).length>1){
        var nrolinespDeCortaDep = (pdeCortaDepen.match(/\n/g) || []).length;
        var maxCarateres=20;
        if(pdeCortaDepen.length+nrolinespDeCortaDep > maxCarateres){
            valRetorno = '0';
            bootbox.alert("<h5>Sigla Corta excede el límite de "+maxCarateres+" caracteres.</h5>", function() {
                bootbox.hideAll();
                jQuery(noForm).find("#deCortaDepen").focus();
            });
        }             
        } else {
            valRetorno = '0';
            bootbox.alert("<h5>Ingrese Sigla Corta campo obligatorio, mínimo dos caracteres.</h5>", function () {
                bootbox.hideAll();
                jQuery(noForm).find("#deCortaDepen").focus();
            });
      }
    }    
    return valRetorno==='1';    
}

function fu_iniDepAdmUO(){
    var noForm='#dependenciaBean';
    jQuery(noForm).change(function() {
       jQuery(noForm).find('#envDependenciaBean').val("1"); 
    });
}
function fu_iniTipoUO(){
    $('#tiDependencia').change(function(e){
        var v = $('#tiDependencia').val();
        if (v == 1) {
            $('#rowCoDependencia').hide();
            $('#coDependencia').removeAttr('value');
            $('#tblIntegrantes').show();
        }
        else {
            $('#rowCoDependencia').show();
            $('#coDependencia').removeAttr('value');
            $('#tblIntegrantes').hide();
        }
    });
}

function fn_addEmpleadoDepUUOO() {
    fn_addDestintarioEmiDoc('tblIntegrantesUUOO');
}

function fn_removeEmpleadoDepUUOO() {
    fn_removeDestintarioEmiDoc("1", "tblIntegrantesUUOO", 'txtIndexFilaIntegDepUo');
}

function fn_buscarEmpleadoEditUUOO(kepPressUser,inputNomb){
    var pnomEmp=jQuery(inputNomb).val(fu_getValorUpperCase(jQuery(inputNomb).val())).val();
    fn_validarBusqEmpleadoEditUUOO(pnomEmp,inputNomb);
}

function fn_validarBusqEmpleadoEditUUOO(pnomEmp,inputNomb){
    if(!!pnomEmp&&allTrim(pnomEmp).length>1){
        var p = new Array();
        p[0] = "accion=goBuscaEmpUUOO";
        p[1] = "pnomEmp=" + pnomEmp;
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            fn_rptaBuscaDestinatarioFiltroConsulDocRecep(data);
            jQuery('#txtTblIntegranteDepUUOOFilaWhereButton').val(((jQuery(inputNomb).parent()).parent()).index());
            jQuery('#txtTblIntegranteDepUUOOColWhereButton').val((jQuery(inputNomb).parent()).index());            
        },'text', false, false, "POST");        
    }else{
        bootbox.alert("<h5>Ingrese Empleado.</h5>", function() {
            bootbox.hideAll();
            jQuery(inputNomb).focus();
        });        
    }
    return false;    
}

function fn_buscarEmpleadoEditUUOOButton(objButton){
    var inputNomb=(jQuery(objButton).parent()).find('input[type=text]');
    var pnomEmp=allTrim(fu_getValorUpperCase(inputNomb.val()));    
    fn_validarBusqEmpleadoEditUUOO(pnomEmp,inputNomb);
}

function fn_iniNewEmpDependenciaUUOO(){
        var tableaux = $('#tblElaboradoPor');
        tableaux.find('tr').each(function(index, row) {
            if(index === 0){
                $(this).addClass('row_selected');                        
                return false;
            }
        });
        var searchOnTable = function(evento) {
                var table = $('#tblElaboradoPor');
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
                if (found === true) {
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
                if(evento.which === 13){
                    if(isFirst){
                        var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                        var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                        fu_setDatoEmpNewDepenUUOO(pcodDest,pdesDest);
                    }
                }
        };        
        $('#txtConsultaFind').keyup(searchOnTable);
        $("#tblElaboradoPor tbody tr").click(function(e) {
            var pdesDest= $(this).find("td:eq(0)").html();
            var pcodDest= $(this).find("td:eq(1)").html();
            fu_setDatoEmpNewDepenUUOO(pcodDest,pdesDest);            
        });    
}

function fu_setDatoEmpNewDepenUUOO(pcoEmp,pDeEmp){
    var pfila = jQuery('#txtTblIntegranteDepUUOOFilaWhereButton').val();
    var pcol = jQuery('#txtTblIntegranteDepUUOOColWhereButton').val();    
    var idTabla = "#tblIntegrantesUUOO";
    removeDomId('windowConsultaElaboradoPor'); 
    var arrCampo = new Array();
    arrCampo[0] = "2=" + pcoEmp;
    var bResult = fn_validaEmpleadoDepUODuplicado(idTabla, arrCampo);
    if(bResult){
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').val(pDeEmp);
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(2)").text(pcoEmp);
        jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(3)").text(pDeEmp);        
        if (jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(1)").text() === "BD") {
            jQuery(idTabla+" tbody tr:eq(" + pfila + ") td:eq(1)").text("UPD");
        }
        fn_changeEmpleadoDepUoCorrecto(idTabla, pfila);
    }else{
        bootbox.alert("<h5>Empleado ya esta en lista.</h5>", function() {
            bootbox.hideAll();
            jQuery(idTabla + " tbody tr:eq(" + pfila + ") td:eq(" + pcol + ")").find('input[type=text]').focus();
        });         
    }
}

function fn_eventTblIntegrantesUUOO() {
    fn_eventTblSeleccionGuardaIndex("tblIntegrantesUUOO", "txtIndexFilaIntegDepUo");
}

function fn_validaEmpleadoDepUODuplicado(idTabla, arrCamposCompara) {
    var countFound = 0;
    jQuery(idTabla + " tbody tr").each(function(index, row) {
        if (index > 0 && !$(row).hasClass('oculto')) {
            for (var i = 0; i < arrCamposCompara.length; i++) {
                var auxArrCamposCompara = arrCamposCompara[i].split("=");
                var auxIndex = auxArrCamposCompara[0];
                var auxValor = auxArrCamposCompara[1];
                if ($(row).find("td:eq(" + auxIndex + ")").text() === auxValor) {
                    countFound++;
                }
            }
        }
    });
    if (countFound > 0) {
        return false;
    } else {
        return true;
    }
}

function fn_changeEmpleadoDepUoCorrecto(idTabla, pnroFila) {
    var ultimDiv = jQuery(idTabla + " tbody tr:eq(" + pnroFila + ") td div.ui-state-error-text").last();
    ultimDiv.removeClass('ui-state-error-text');
    var ultimDivSpan = ultimDiv.find('span');
    ultimDivSpan.removeClass('ui-icon-alert');
    ultimDivSpan.addClass('ui-icon-check');
    ultimDivSpan.attr("title", "OK");
    ultimDiv.addClass('ui-state-highlight-text');
    jQuery(idTabla + " tbody tr:eq(" + pnroFila + ") td").last().html("1");
}

function fn_verificarLstIntegrantesDepUOCorrecto(idTabla,msg) {

    //if ($(idTabla + ' >tbody >tr').length === 1 || $(idTabla + " tbody tr td").last().html() === "1") {
    if ($(idTabla + ' >tbody >tr').length === 1 || $(idTabla + " tbody tr td").last().html() === "1" || $(idTabla + ' >tbody >tr').length === 0) {
        var found = true;
        $(idTabla + " tbody tr").each(function(index, row) {
            if (index > 0) {
                if ($(row).find('td').last().html() !== "1") {
                    found = false;
                }
            }
        });
        //if (found) {
        if ((found)) {
            return found;
        } else {
            bootbox.alert("<h5>"+msg+".</h5>", function() {
                bootbox.hideAll();
            });            
            return false;                
        }
    } else {
        bootbox.alert("<h5>"+msg+".</h5>", function() {
            bootbox.hideAll();
        });            
        return false;                
    }
}

function fn_rptaEmptyListTupaExpediente(data){
    if(!!data){
        if (data.coRespuesta==="1") {
            alert_Sucess("","OK");
        }else{
            alert_Info("Vaciar Lista :", data.deRespuesta);
        }
    }
}

function user_delete_admEmpleado(){
    bootbox.dialog({
        message: " <h5>¿Desea eliminar al usuario?</h5>",
        buttons: {
            SI: {
                label: "SI",
                className: "btn-primary",
                callback: function() {
                    var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val()});
                    ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoEliminarAcceso', jsonString, function(data) {
                        if(data.exito){
                            $('#user_btns').html('<button type="button" class="btn btn-default" id="user_add"><span class="glyphicon glyphicon-plus"></span> Nuevo usuario</button>');
                            $('#user_form').html('<td><span style="color:red;">'+data.mensaje+'</span></td>');
                            $('#div_user_verify').html('');
                        }else{
                            alert_Danger("ERROR:", data.mensaje);
                        }
                    }, 'json', false, false, 'POST');
                }                        
            },
            NO: {
                label: "NO",
                className: "btn-default"
            }
        }
    });
    return false;
}

function user_reestablish_pass(){
    bootbox.dialog({
       message: " <h5>La contraseña se va a restablecer \n ¿Desea continuar?</h5>",
       buttons: {
           SI: {
               label: "SI",
               className: "btn-primary",
               callback: function() {
                   var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val()});
                   ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoRestablecerAcceso', jsonString, function(data) {
                       if(data.exito){
                           $('#div_user_verify').html('<span style="color:red;">'+data.mensaje+'</span>');
                       }else{
                           alert_Danger("ERROR:", data.mensaje);
                       }
                   }, 'json', false, false, 'POST');
               }                        
           },
           NO: {
               label: "NO",
               className: "btn-default" 
           }
       }
   });    
   return false;
}


function user_cambiarEstado(estado){
     var jsonString = JSON.stringify({"coEmpleado":$('#codEmp').val(),"esUsuario":estado});
                   ajaxCallSendJson('/srTablaConfiguracion.do?accion=goAdmEmpleadoVerificarUsernameCambiarEstado', jsonString, function(data) {
                       if(data.exito){
                           $('#div_user_verify').html('<span style="color:red;">'+data.mensaje+'</span>');
                            removeDomId('divOrigenMain');
                            fn_busquedaEmpleado();
                       }else{
                           alert_Danger("ERROR:", data.mensaje);
                       }
                   }, 'json', false, false, 'POST');
   return false;
}


function fn_cleanPersonaDesdeDni(){
    $('#apPaterno').val('');
    $('#apMaterno').val('');
    $('#nombres').val('');
    $('#fechaNacimiento').val('');
    $('#sexo1').removeAttr('checked');
    $('#sexo2').removeAttr('checked');
}

function fn_iniRegistroCiudadanoNuevo(vnuLem){
    if(!!vnuLem){
        if(vnuLem!==null&&vnuLem!=='null'&&allTrim(vnuLem).length>0){
            jQuery('#txtNuLem').prop('readonly','true');
            jQuery('#txtNuLem').attr('readonly','true');                    
        }
    }
}

function fn_cargarImgPortadaPrincipal(){
    var rutactx = pRutaContexto + "/" + pAppVersion;
    jQuery('#imgPortada_fileuploadAlta').fileupload({
        dataType: 'text',
        add: function(e, data) {
            if(e.target.id===e.currentTarget.id){
                loadding(true);
                var url = "";
                url = url.concat(rutactx, "/srTablaConfiguracion.do?accion=goUploadImg");
                var goUpload = true;
                /*var uploadFile = data.files[0];
                if (!(/\.(pdf)$/i).test(uploadFile.name)) {
                    alert_Danger("ERROR", " Seleccionar solo pdf.");
                    loadding(false);
                    goUpload = false;
                }*/
                if (goUpload) {
                    data.url = url;
                    data.submit();
                }
            }	
        },
        done: function(e, data) {
            if (data.textStatus === "success"){
                var jsonRes = jQuery.parseJSON(data.result);
                var nombreDoc = jsonRes[0].name;
                $("#imgPortada_name").val(nombreDoc);
                alert_Sucess("GRABAR", "Imagen cargada correctamente");
            } else {
                alert_Danger("ERROR", " al cargar el archivo");
            }
            $("#imgPortada_progressAlta").hide();
        },
        error: function (e, data) {
            alert_Danger("ERROR", " al cargar el archivo verifique tamaño");
            $("#imgPortada_progressAlta").hide();
            loadding(false);
        },        
        progressall: function(e, data) {
            var progress = parseInt(data.loaded / data.total * 80, 10);
            jQuery('#imgPortada_progressAlta .progress-bar').css('width', progress + '%');
            var progressText = "";
            if (progress < 30) {
                progressText = progress + '%';
            } else {
                progressText = 'Cargando ' + progress + '%';
            }
            jQuery('#imgPortada_progressAlta span').html(progressText);
        }
    });
    jQuery('#imgPortada_progressAlta .progress-bar').css('width', '0%');
    jQuery("#imgPortada_progressAlta").show();
    jQuery('#imgPortada_fileuploadAlta').click();    
}

//Asignación de funcionarios
function fu_goDialogAsignacionFuncionario() {
    ajaxCall("/srAsignacionFuncionario.do?accion=goDialogAsignacion", '', function(data) {        
        jQuery('#divDependenciaBody').hide();
        jQuery('#divDependenciaEdit').show();
        refreshScript("divDependenciaEdit", data);
    }, 'text', false, false, "POST");
}
//llamar al dialog
function fn_iniDepAsignacionFuncionario() {
    var tableaux = $('#tlbDestinoEmi');
    tableaux.find('tr').each(function (index, row) {
        if (index == 0) {
            $(this).addClass('row_selected');
            return false;
        }
    });
    var searchOnTable = function (evento) {
        var table = $('#tlbDestinoEmi');
        var value = this.value;
        //alert(evento.which);
        var isFirst = false;
        var indexSelect = -1;
        table.find('tr').each(function (index, row) {
            if ($(this).hasClass('row_selected')) {
                $(this).removeClass('row_selected');
            }
            //var allCells = $(row).find('td');
            var allCells = $(row).find('td');
            if (allCells.length > 0) {
                var found = false;
                allCells.each(function (index, td) {
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
                } else {
                    $(row).hide();
                }
            }
        });
        if (evento.which == 13) {
            if (isFirst) {
                var pdesDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(1)").html();
                var pcodDest = $("#tlbDestinoEmi tbody tr:eq(" + indexSelect + ")").find("td:eq(2)").html();
                fu_setDatoDepPadreUOAdm(pcodDest, pdesDest);
            }
        }
    };
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tlbDestinoEmi tbody tr").click(function (e) {
        var pdesDest = $(this).find("td:eq(1)").html();
        var pcodDest = $(this).find("td:eq(2)").html();
        $('#deDependencia').val(pdesDest);
        $('#coDependencia').val(pcodDest);
        fu_setDatosDependencia(pcodDest, pdesDest);
    });
}
function fn_buscaDependenciaAsignacion() {
    ajaxCall("/srAsignacionFuncionario.do?accion=goDlgBuscarDependencia", '', function (data) {
        fn_rptaBuscaDependenciaPadreUOAdm(data);
    }, 'text', false, false, "POST");
}
//Cuando se selecciona la dependencia
function fu_setDatosDependencia(cod, desc) {
    var noForm='#frmDatosDependencia';
    jQuery(noForm).find('#deDependencia').val(desc);
    jQuery("#frmAsginar").find('#coDependencia').val(cod);
    var p={coDependencia:cod};
    ajaxCall("/srAsignacionFuncionario.do?accion=goFuncionarioAsignado", p, function (data) {        
        if(data.coRespuesta==="1" || data.coRespuesta==="2"){
            jQuery(noForm).find('#deEncargado').val(data.deEmpleado);
            jQuery(noForm).find('#deTipoEncargatura').val(data.coTipoEncargo);
        }
        if(data.coRespuesta==="2"){
            alert_Danger("Importante", data.deRespuesta);
        }
    }, 'json', false, false, "POST");
    removeDomId('windowConsultaDestinoEmi');
}
function fn_buscaEmpleadoAsignacion() {
    ajaxCall("/srAsignacionFuncionario.do?accion=goDlgListaEmpleado", '', function (data) {
        fn_rptaBuscaDependenciaPadreUOAdm(data);
    }, 'text', false, false, "POST");
}

function fn_iniConsEmpleadosAsignacion(){
    var tableaux = $('#tblElaboradoPor');
    tableaux.find('tr').each(function(index, row) {
        if(index === 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $('#tblElaboradoPor');
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
                if (found === true) {
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
            if(evento.which === 13){
                if(isFirst){
                    var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    fu_setDatoEmpAsignar(pcodDest,pdesDest);
                }
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tblElaboradoPor tbody tr").click(function(e) {
        var pdesDest= $(this).find("td:eq(0)").html();
        var pcodDest= $(this).find("td:eq(1)").html();
        fu_setDatoEmpAsignar(pcodDest,pdesDest);            
    });    
}
function fu_setDatoEmpAsignar(cod, desc) {
    var noForm='#frmAsginar';
    jQuery(noForm).find('#coEmpleado').val(cod);
    jQuery(noForm).find('#deEmpleado').val(desc);
    var p={coEmpleado:cod};
    //Validar si empleado tiene asignación
    ajaxCall("/srAsignacionFuncionario.do?accion=goValidaEmpleadoAsignacion", p, function (data) {
        if (data.coRespuesta === "1") {
            alert_Danger("Importante", data.deRespuesta);
        }
    }, 'json', false, false, "POST");
    removeDomId('windowConsultaElaboradoPor');
}
//Función que valida la fecha cuando se esta digitando
//Llamado desde el input dónde se ingresa la fecha
function fn_fecha(key,inputType){       
    //Declara variable para obtener el valor
    var feInscripcion=allTrim(inputType.value);
    //llama a la funcion de validacion de fecha
    return fn_validaFecha(feInscripcion,inputType);
}

//Valida que las fechas sean validas
function fn_validaFecha(feInscripcion,inputType){
    var result = false;
    if(!!feInscripcion){
        if(moment(feInscripcion, "DD/MM/YYYY").isValid()){
            var fecha=moment(feInscripcion, ["DD","DD/MM","DD/MM/YY","DD/MM/YY","DD/MM/YYYY"]);
            //jQuery(noForm).find("#feGuiMp").val(moment(fecha,"DD/MM/YYYY HH:mm").format("DD/MM/YYYY HH:mm"));
            
            inputType.value=moment(fecha,"DD/MM/YYYY").format("DD/MM/YYYY");  
            var str=inputType.value;
            var anio=parseInt(str.substring(6,10));
            if(anio<1900){
                result = false;
                inputType.value='';
            }else{
                result = true;
            }
            
        }else{
            inputType.value='';
        }        
    }else{
        inputType.value='';
    }    
    return result;
}
function fn_registrarAsignacionFuncionario(noForm){     
    var msgRptaValidacion = validarFrmRegistrarAsignacionFuncionario(noForm);
    if (!!msgRptaValidacion) {//Si la Validación fue correcta ejecutar ajax
        bootbox.dialog({
            message: " <h5>¿ Confirma la Asignación del Funcionario ?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-default",
                    callback: function() {
                        setTimeout(function() {
                            ajaxCall("/srAsignacionFuncionario.do?accion=goRegistrarAsignacionFuncionario", jQuery(noForm).serialize(), function(data) {
                                if(data.coRespuesta==='01'){
                                    alert_Sucess("Mensaje",data.deRespuesta);
                                    fn_cerrarNuevaAsignacion();
                                }else{
                                    alert_Danger("Error",data.deRespuesta);
                                }                                                                                                   
                            }, 'json', true, false, "POST");
                        }, 300);
                    }
                },
                NO: {
                    label: "NO",
                    className: "btn-primary"
                }
            }
        });
    } else {
        //Mensajes de Validacion;
    }
}

function validarFrmRegistrarAsignacionFuncionario(noForm) {
    var vRetorno = 1;
    var vcoDependencia = jQuery(noForm).find('#coDependencia').val();
    var vcoEmpleado = jQuery(noForm).find('#coEmpleado').val();
    var vcoTipoEncargo = jQuery(noForm).find('#coTipoEncargo').val();
    var vfeInicio = jQuery(noForm).find('#feInicio').val();
    var vfeFin = jQuery(noForm).find('#feFin').val();
    var vdeDocumentoAsignacion = jQuery(noForm).find('#deDocAsigna').val();
    var hoy=fechaActual();
    //VALIDACIÓN GENERAL
    if (vRetorno) {
        if (!(!!vcoDependencia)) {
            vRetorno = 0;
            alert_Danger("Requerido:", "Debe seleccionar una dependencia.");
            jQuery(noForm).find('#coDependencia').focus();
        }
    }
    if (vRetorno) {
        if (!(!!vcoEmpleado)) {
            vRetorno = 0;
            alert_Danger("Requerido:", "Debe seleccionar el empleado a asignar.");
            jQuery(noForm).find('#coEmpleado').focus();
        }
    }

    if (vRetorno) {
        if (!(!!vcoTipoEncargo)) {
            vRetorno = 0;
            alert_Danger("Requerido:", "Debe seleccionar el tipo de encargatura a asignar.");
            jQuery(noForm).find('#coTipoEncargo').focus();
        }
    }
    if (vRetorno) {
        if (!(!!vfeInicio)) {
            vRetorno = 0;
            alert_Danger("Requerido", "Debe ingresar una fecha de inicio.");
            jQuery(noForm).find('#feInicio').focus();
        }
    }
    if (vRetorno) {
        if (vcoTipoEncargo !== "1") {
            if (!(!!vfeFin)) {
                vRetorno = 0;
                alert_Danger("Requerido", "Debe ingresar una fecha de fin.");
                jQuery(noForm).find('#feFin').focus();
            }
        }
    }
    if (vRetorno) {
        if (!(!!vdeDocumentoAsignacion)) {
            vRetorno = 0;
            alert_Danger("Requerido", "Debe ingresar un documento de asignación.");
            jQuery(noForm).find('#deDocAsigna').focus();
        }
    }
    
//    if (!!vRetorno) {
//        var v1 = comparaFechas(vfeInicio, hoy);
//        if (v1 === "-1") {
//            vRetorno = 0;
//            alert_Danger("Mensaje", "La fecha de inicio no debe ser menor que la fecha actual.");
//            jQuery(noForm).find('#feInicio').focus();
//        }
//    }
     if (!!vRetorno) {
        if (vcoTipoEncargo!=="1") {
            var v1 = comparaFechas(vfeFin, vfeInicio);
            if (v1 === "-1") {
                vRetorno = 0;
                alert_Danger("Mensaje", "La fecha de inicio no debe ser mayor que la fecha de fin.");
            }
        }else{
            jQuery(noForm).find('#feFin').val(jQuery(noForm).find('#feInicio').val());
        }
    }
    if (!!vRetorno) {
        if (vcoTipoEncargo!=="1") {
            var v1 = comparaFechas(vfeFin, hoy);
            if (v1 === "-1") {
                vRetorno = 0;
                alert_Danger("Mensaje", "La fecha de fin no debe ser menor que la fecha actual.");
                jQuery(noForm).find('#feFin').focus();
            }
        }
    }
    return vRetorno;
}

function fn_cerrarNuevaAsignacion() {
    jQuery('#divDependenciaEdit').toggle();
    jQuery('#divDependenciaEdit').html("");
    jQuery('#divDependenciaBody').toggle();
    ajaxCall("/srAsignacionFuncionario.do?accion=goAsignacionFuncionarios", '', function (data) {
        refreshScript("divDependenciaDetalle", data);
    }, 'text', false, false, "POST");
    return false;
}

function fn_inicializaAsignacionFuncionarios(noForm){
    jQuery(noForm).find("#fechaFiltro").showDatePicker({defaultOpcionSelected: 5});
    
}
function fn_filtrarAsignacionFuncionario(noForm) {
    jQuery(noForm).find('#feRegistraInicio').val(jQuery(noForm).find('#fechaFiltro').attr('fini'));
    jQuery(noForm).find('#feRegistraFin').val(jQuery(noForm).find('#fechaFiltro').attr('ffin'));    
    ajaxCall("/srAsignacionFuncionario.do?accion=goAsignacionFuncionarios", jQuery(noForm).serialize(), function (data) {
        refreshScript("divDependenciaDetalle", data);
    }, 'text', false, false, "POST");
    return false;
}

function fn_limpiarFormAsignacion(noForm){
    jQuery(noForm).find('#deDependencia').val('');
    jQuery(noForm).find('#feRegistraInicio').val('');
    jQuery(noForm).find('#feRegistraFin').val('');
    jQuery(noForm).find('#coDependencia').val('');
}
function fu_iniNuevaAsignacion(noForm){
    //$('#coTipoEncargo').change(function(){
        var coTipoEncargatura=jQuery(noForm).find('#coTipoEncargo').val();
        if(coTipoEncargatura==="1"){
            jQuery(noForm).find('#feFin').hide();
            jQuery(noForm).find('#lblFeFin').hide();
            jQuery(noForm).find('#feFin').val('');
            
        }else{
            jQuery(noForm).find('#feFin').show();
            jQuery(noForm).find('#lblFeFin').show();
            jQuery(noForm).find('#feFin').val('');
        }
    //});
}
function fu_eliminaAsignacionEmpleado(coAsignacion) {
    var p={coAsignacion:coAsignacion};    
    bootbox.dialog({
        message: " <h5>¿ Confirma la eliminación de la programación de asignación?</h5>",
        buttons: {
            SI: {
                label: "SI",
                className: "btn-default",
                callback: function () {
                    setTimeout(function () {
                        ajaxCall("/srAsignacionFuncionario.do?accion=goEliminaAsignacionEmpleado", p, function (data) {
                            if (data.coRespuesta === "1") {
                                alert_Sucess("Mensaje", data.deRespuesta);
                                fn_cerrarNuevaAsignacion();
                            } else {
                                alert_Danger("Error", data.deRespuesta);
                            }
                        }, 'json', false, false, "POST");
                    }, 300);
                }
            },
            NO: {
                label: "NO",
                className: "btn-primary"
            }
        }
    });
}
function fu_goDialogEditarAsignacionFuncionario(coAsignacion) {
    var p={coAsignacion:coAsignacion};    
    ajaxCall("/srAsignacionFuncionario.do?accion=goDialogEditarAsignacion", p, function(data) {        
        jQuery('#divDependenciaBody').hide();
        jQuery('#divDependenciaEdit').show();
        refreshScript("divDependenciaEdit", data);
    }, 'text', false, false, "POST");
}

function fn_actualizarAsignacionFuncionario(noForm){     
    var msgRptaValidacion = validarFrmActualizarAsignacionFuncionario(noForm);
    if (!!msgRptaValidacion) {//Si la Validación fue correcta ejecutar ajax
        bootbox.dialog({
            message: " <h5>¿ Confirma las modificaciones en la Asignación del Funcionario ?</h5>",
            buttons: {
                SI: {
                    label: "SI",
                    className: "btn-default",
                    callback: function() {
                        setTimeout(function() {
                            ajaxCall("/srAsignacionFuncionario.do?accion=goActualizarAsignacionFuncionario", jQuery(noForm).serialize(), function(data) {
                                if(data.coRespuesta==='1'){
                                    alert_Sucess("Mensaje",data.deRespuesta);
                                    fn_cerrarNuevaAsignacion();
                                }else{
                                    alert_Danger("Error",data.deRespuesta);
                                }                                                                                                   
                            }, 'json', true, false, "POST");
                        }, 300);
                    }
                },
                NO: {
                    label: "NO",
                    className: "btn-primary"
                }
            }
        });
    } else {
        //Mensajes de Validacion;
    }
}

function validarFrmActualizarAsignacionFuncionario(noForm) {
    var vRetorno = 1;    
    var vcoAsignacionFuncionario = jQuery(noForm).find('#coAsignacionFuncionario').val();
    var vcoTipoEncargo = jQuery(noForm).find('#coTipoEncargo').val();
    var vfeInicio = jQuery(noForm).find('#feInicio').val();
    var vfeFin = jQuery(noForm).find('#feFin').val();
    var vdeDocumentoAsignacion = jQuery(noForm).find('#deDocAsigna').val();
    var hoy=fechaActual();

    //VALIDACIÓN GENERAL
    if (!!vRetorno) {
        if (!(!!vcoAsignacionFuncionario)) {
            vRetorno = 0;
            alert_Danger("Requerido:", "El código de asignación no es valida.");
        }
    }
    
    if (!!vRetorno) {
        if (!(!!vcoTipoEncargo)) {
            vRetorno = 0;
            alert_Danger("Requerido:", "Debe seleccionar el tipo de encargatura a asignar.");
            jQuery(noForm).find('#coTipoEncargo').focus();
        }
    }
    if (!!vRetorno) {
        if (!(!!vfeInicio)) {
            vRetorno = 0;
            alert_Danger("Requerido", "Debe ingresar una fecha de inicio.");
            jQuery(noForm).find('#feInicio').focus();
        }
    }
    if (!!vRetorno) {
        if (vcoTipoEncargo !== "1") {
            if (!(!!vfeFin)) {
                vRetorno = 0;
                alert_Danger("Requerido", "Debe ingresar una fecha de fin.");
                jQuery(noForm).find('#feFin').focus();
            }
        }
    }
    if (!!vRetorno) {
        if (!(!!vdeDocumentoAsignacion)) {
            vRetorno = 0;
            alert_Danger("Requerido", "Debe ingresar un documento de asignación.");
            jQuery(noForm).find('#feFin').focus();
        }
    }
   
//    if (!!vRetorno) {
//        var v1 = comparaFechas(vfeInicio, hoy);
//        if (v1 === "-1") {
//            vRetorno = 0;
//            alert_Danger("Mensaje", "La fecha de inicio no debe ser menor que la fecha actual.");
//            jQuery(noForm).find('#feInicio').focus();
//        }
//    }
     if (!!vRetorno) {
        if (vcoTipoEncargo!=="1") {
            var v1 = comparaFechas(vfeFin, vfeInicio);
            if (v1 === "-1") {
                vRetorno = 0;
                alert_Danger("Mensaje", "La fecha de inicio no debe ser mayor que la fecha de fin.");
            }
        }else{
            jQuery(noForm).find('#feFin').val(jQuery(noForm).find('#feInicio').val());
        }
    }
    if (!!vRetorno) {
        if (vcoTipoEncargo!=="1") {
            var v1 = comparaFechas(vfeFin, hoy);
            if (v1 === "-1") {
                vRetorno = 0;
                alert_Danger("Mensaje", "La fecha de fin no debe ser menor que la fecha actual.");
                jQuery(noForm).find('#feFin').focus();
            }
        }
    }
   
    return !!vRetorno;
}

function fu_llamarEditar(){
    var coAsignacionFuncionario=jQuery('#divListaAsignaciones').find('#idCoAsignacionFuncionario').val();
    if (!!coAsignacionFuncionario) {
        fu_goDialogEditarAsignacionFuncionario(coAsignacionFuncionario);
    }else{
        alert_Danger("Mensaje","Debe seleccionar un registro de la lista");
    }
}

function fn_eventAsignacionDetalle() {
    $("#tblDepDet tbody tr").click(function (e) {
        if ($(this).hasClass('row_selected')) {
            //$(this).removeClass('row_selected');
            //jQuery('#txtpnuAnn').val("");
        } else {
            $('#tblDepDet tbody tr.row_selected').removeClass('row_selected');
            $(this).addClass('row_selected');

            /*obtiene datos*/
            //jQuery('#txtTextIndexSelect').val("0");
            if (!!$(this).children('td')[0].innerHTML) {
                jQuery('#divListaAsignaciones').find('#idCoAsignacionFuncionario').val($(this).children('td')[0].innerHTML);                
            }
        }
    });
}

function fn_buscarRequisito(e, textInput) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqRequisito();
    }
}

function fn_cargarBusqRequisito() {
    var busRequisitoDescripcion  = allTrim($("#txtRequisitoDescripcion").val());
    var indicador  = 0; // Indicador
    var p = new Array();
    p[0] = "accion=goListaBusqRequisito";
    p[1] = "busRequisitoDescripcion=" + busRequisitoDescripcion.toUpperCase();
    p[2] = "indicador=" + indicador;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaRequisito", data);
        loadding(false);
        var c = $("#tblRequisitoDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}

function fn_eventRequisito() {
    $("#tblRequisitoDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codRequisito").val($(this).find("[name='hid_CoRequisito']").val());
    });
}

function fu_goNuevoRequisito() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoRequisito", '', function(data) {
        jQuery('#divRequisitoBody').hide();
        jQuery('#divRequisitoEdit').show();
        refreshScript("divRequisitoEdit", data);
        loadding(false);
    }, 'text', false, false, "POST");
    return false;
}

function fu_goEditarRequisito() {
    var codRequisito = $("#codRequisito").val();
    if (!!codRequisito) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoRequisito&vcodRequisito=" + codRequisito, '', function(data) {
            jQuery('#divRequisitoBody').hide();
            jQuery('#divRequisitoEdit').show();
            refreshScript("divRequisitoEdit", data);
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

function fn_grabarNuevoRequisito() {
    var valRetorno      = '0';
    
    var CoRequisito     = allTrim($("#hidN_codRequisito").val());
    var DeDescripcion   = allTrim($("#txtDeDescripcion").val()); 
    var isChecked       = $("#chbxEstado").is(':checked');
    var coEstado = (isChecked ? '1' : '0');    
    
    if (allTrim(DeDescripcion).length<1) {
        bootbox.alert("<h5>Ingrese descripción del Requisito, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtDeNombre").focus();
        });           
    } else {
        valRetorno = '1';
    }
    
    if (valRetorno === '1') {
        bootbox.confirm({
        message: "¿Guardar Requisito?",
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
                    var jsonBody =
                            {
                                 "codRequisito": CoRequisito, "descripcion": DeDescripcion, "esEstado":coEstado
                            };
                    var jsonString = JSON.stringify(jsonBody);

                    if (!!CoRequisito) {
                        url = "/srTablaConfiguracion.do?accion=goUpdRequisito";
                    } else {
                        url = "/srTablaConfiguracion.do?accion=goAgregarRequisito";
                    }

                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            fn_cargarListaRequisito();
                            fn_cargarBusqRequisito();
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    }

}
    
function fn_cargarListaRequisito() {
    jQuery('#divRequisitoBody').toggle();                                
    jQuery('#divRequisitoEdit').html("");   
}   

//YUAL

function fn_buscarCargo(e, textInput) {
    var code = (e.keyCode ? e.keyCode : e.which);
    if (code === 13) {
        fn_cargarBusqCargo();
    }
}

function fn_cargarBusqCargo() {
    var busCargoDescripcion  = allTrim($("#txtCargoDescripcion").val());
    var indicador  = 0; // Indicador
 
    var p = new Array();
    p[0] = "accion=goListaBusqCargo";
    p[1] = "busCargoDescripcion=" + busCargoDescripcion.toUpperCase()    
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaCargo", data);
        loadding(false);
        var c = $("#tblCargoDet tr").length;
        if (c === 0) {
            alert_Warning("MENSAJE", "No se encontraron coincidencias.");
        }
    }, 'text', false, false, "POST");
}


function fn_eventCargo() {
    $("#tblCargoDet tbody").on("click", "tr", function() {
        $(this).parent().find('.row_selected').removeClass("row_selected");
        $(this).toggleClass("row_selected");
        $("#codCargo").val($(this).find("[name='hid_CoCargo']").val());
    });
}

function fu_goNuevoCargo() {
    ajaxCall("/srTablaConfiguracion.do?accion=goNuevoCargo", '', function(data) {
        jQuery('#divCargoBody').hide();
        jQuery('#divCargoEdit').show();
        refreshScript("divCargoEdit", data);
        loadding(false);
    }, 'text', false, false, "POST");
    return false;
}

function fu_goEditarCargo() {
    var codRequisito = $("#codCargo").val();
    if (!!codRequisito) {

        ajaxCall("/srTablaConfiguracion.do?accion=goNuevoCargo&vcodCargo=" + codRequisito, '', function(data) {
            jQuery('#divCargoBody').hide();
            jQuery('#divCargoEdit').show();
            refreshScript("divCargoEdit", data);
        }, 'text', false, false, "POST");

    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

function fn_grabarNuevoCargo() {
    var valRetorno      = '0';
    
    var CoRequisito     = allTrim($("#hidN_codCargo").val());
    var DeDescripcion   = allTrim($("#txtDeDescripcion").val()); 
    var isChecked       = $("#chbxEstado").is(':checked');
    var coEstado = (isChecked ? '1' : '0');    
    
    if (allTrim(DeDescripcion).length<1) {
        bootbox.alert("<h5>Ingrese descripción del Cargo, campo obligatorio.</h5>", function() {
            bootbox.hideAll();
            jQuery("#txtDeCargo").focus();
        });           
    } else {
        valRetorno = '1';
    }
    
    if (valRetorno === '1') {
        bootbox.confirm({
        message: "¿Guardar Cargo?",
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
                    var jsonBody =
                            {
                                 "codigo": CoRequisito, "descripcion": DeDescripcion, "estado":coEstado
                            };
                    var jsonString = JSON.stringify(jsonBody);

                    if (!!CoRequisito) {
                        url = "/srTablaConfiguracion.do?accion=goUpdCargo";
                    } else {
                        url = "/srTablaConfiguracion.do?accion=goAgregarCargo";
                    }

                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos guardados.") {
                            alert_Sucess("MENSAJE:", data);
                            fn_cargarListaCargo();
                            fn_cargarBusqCargo();
                        } else {
                            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    }

}
    
function fn_cargarListaCargo() {
    jQuery('#divCargoBody').toggle();                                
    jQuery('#divCargoEdit').html("");   
}   


//FIN YUAL

// agregar credencial x usuario
function fn_adicionarUsuario() {
    //reemMot = false;
    fn_cargarNuevoCredencialUsuario();
}

function fn_cargarNuevoCredencialUsuario() {
    var estado  = 1; // Habilitados
    var indicador = $("#hidN_coProceso").val(); // Indicador
    var p = new Array();
    p[0] = "accion=goListaBusqUsuario";
    p[1] = "estado=" + estado;
    p[2] = "indicador=" + indicador;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null) {
            $("body").append(data);
        }
    }, 'text', false, false, "POST");
    return false;
}



// Tupa x Requisitos

function fn_adicionarTupaRequisito() {
    reemMot = false;
    fn_cargarNuevoTupaRequisito();
}

function fn_cambiarTupaRequisito(codMot) {
    reemMot = true;
    $("#codReqReempl").attr("value", codMot);
    fn_cargarNuevoTupaRequisito();
}

function fn_cargarNuevoTupaRequisito() {
    var estado  = 1; // Habilitados
    var indicador = $("#hidN_coProceso").val(); // Indicador
    var p = new Array();
    p[0] = "accion=goListaBusqRequisito";
    p[1] = "estado=" + estado;
    p[2] = "indicador=" + indicador;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        if (data !== null) {
            $("body").append(data);
        }
    }, 'text', false, false, "POST");
    return false;
}

function fn_agregarTupaRequisito(codRequisito) {
    var codProceso = $("#hidN_coProceso").val();
    if (reemMot === false) {
        var jsonBody = {"codProceso": codProceso, "codRequisito": codRequisito};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goAgregarTupaRequisito";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE:", data);
                removeDomId('windowConsultaTupaRequisito');
                fn_cargarTupaRequisito();
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
    } else {
        var codReqReempl = $("#codReqReempl").attr("value");
        var jsonBody = {"codProceso": codProceso, "codRequisito": codRequisito, "codRem": codReqReempl};
        var jsonString = JSON.stringify(jsonBody);            
        reemMot = false;
        var url = "/srTablaConfiguracion.do?accion=goCambiarTupaRequisito";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE:", data);
                removeDomId('windowConsultaTupaRequisito');
                fn_cargarTupaRequisito();
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
    }
}

function fn_cargarTupaRequisito() {
    var p = new Array();
    p[0] = "accion=goListaTupaRequisito";
    p[1] = "codProceso=" + jQuery('#hidN_coProceso').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleTupaRequisitos", data);
    }, 'text', false, false, "POST");
}

function fn_eliminarTupaRequisito() {
    var codProceso = $("#hidN_coProceso").val();
    var codRequisito = $("#codReqReempl").val();
    
    bootbox.confirm({
        message: "¿Eliminar REQUISITO?",
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
                var jsonBody = {"codProceso": codProceso, "codRequisito": codRequisito};
                var jsonString = JSON.stringify(jsonBody);
                var url = "/srTablaConfiguracion.do?accion=goEliTupaRequisito";
                ajaxCallSendJson(url, jsonString, function(data) {
                    if (data === "Datos eliminados.") {
                        alert_Sucess("MENSAJE:", data);
                        fn_cargarTupaRequisito();
                    } else {
                        alert_Danger("ERROR:", data);
                    }
                }, 'text', false, false, "POST");
            } else {
            }
        }
    });
    
    return;
}

function fn_eventTupaRequisito() {
    var indexFilaClick = -1;
    $("#tblTupaRequisito tbody tr").click(function() {
        if (indexFilaClick !== -1) {
            if ($("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ")").hasClass('row_selected')) {
                $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ")").removeClass('row_selected');
                $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
                $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            }
        }
        if ($(this).hasClass('row_selected')) {
            $(this).removeClass('row_selected');
        }
        else {
           
            $(this).addClass('row_selected');
            indexFilaClick = $(this).index();
            var codMot = $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") button").attr("codMot");
            $("#codReqReempl").attr("value", codMot);
            
            var indRegistro = $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") button").attr("indicadorReg");
            $("#indRegistro").attr("value", indRegistro);            

            $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
            $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div button:gt(0)").remove();
            $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div > span").remove();
            $("#tblTupaRequisito tbody tr:eq(" + indexFilaClick + ") div").append($("#btn-defecto").html());
        }
        return false;
    });
}

function fn_cambiarEstadoObligatorio() {
    
    var codRequisito = $("#codReqReempl").attr("value");
    var indRegistro = $("#indRegistro").attr("value");
    var codProceso = $("#hidN_coProceso").val();
    if (!!codRequisito) {
        var indicador = "";
        if (indRegistro === '1') {
            indicador = '0';
        } else {
            indicador = '1';
        }
        var jsonBody = {"codProceso": codProceso, "codRequisito": codRequisito, "indicador": indicador};
        var jsonString = JSON.stringify(jsonBody);            
        reemMot = false;
        var url = "/srTablaConfiguracion.do?accion=goCambiarTupaRequisito";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                alert_Sucess("MENSAJE:", data);
                fn_cargarTupaRequisito();
            } else {
                alert_Danger("ERROR:", data);
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Warning("ATENCIÓN", "seleccione una fila");
    }
}

function fn_valFormConfPersonal(objForm, callback){
    var vReturn=0;
    if (!!objForm.codEmp && !!objForm.codDep && !!objForm.rbAcceso && !!objForm.rbCarDoc && !!objForm.rbFirDoc && !!objForm.rbDocDef) {
        if(!!objForm.dirPri&&objForm.dirPri.length>0){
            /*var vRpta=fn_verificaSiExisteDirApplet(objForm.dirPri);
            if(vRpta==="SI"){
                vReturn=1;
            }else{
                alert_Warning("MENSAJE", "No se encuentra la ruta del directorio principal.");
            }*/
            var param = {rutaDir: objForm.dirPri};
            runOnDesktop(accionOnDesktopTramiteDoc.verificarRutaDirectorio, param, function(data){
                var objRpta=data;
                if(!!objRpta){
                    if(objRpta.error==="0"&&objRpta.message==="SI"){
                        vReturn=1;
                    }else{
                        alert_Warning("MENSAJE", "No se encuentra la ruta del directorio principal.");                        
                    }
                }
                if(vReturn==0){
                        alert_Warning("MENSAJE", "No es válido la ruta del directorio principal");
                   }
                callback(vReturn); 
            });
        }else{
            vReturn=1;
        }
    } else {
        alert_Warning("MENSAJE", "No se completo todos los datos requeridos");
    }
    
    callback(vReturn);
}
/*SEGDI*/
function fn_cargarGruposDestinoVar() {
    var p = new Array();
    p[0] = "accion=goListaGruposDestinosVar";
    p[1] = "vCodDependencia=" + jQuery('#codDependencia').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleGrupoDestino", data);
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}

function fn_listaGrupoDetalleVar() {
    var codGrupoDestino = $("#codGrupoDestino").val();
    var codTipoDestino = $("#coTipo").val();
   //console.log("codigo==>"+codGrupoDestino);
    var p = new Array();
    p[0] = "accion=goListaGrupoDestinoVarDet";
    p[1] = "vCodGrupoDestino=" + codGrupoDestino;
    p[2] = "vcoTipo=" + codTipoDestino;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaGrupoDestinoDet", data);
        if (!!codGrupoDestino) {
            $("#txtNombreGrupo").val($("#codGrupoDestino option:selected").text());
            $("#txtNombreGrupo").prop("readonly", false);
        } else {
            $("#txtNombreGrupo").prop("readonly", true);
        }
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}


function fn_changeTipoDest()
{
    fn_iniGrupoDesVar();
}


function fn_cargarNuevoGrupoDestinoVar() {
    var ocultaGrupos = "#tablaDependencia tr:eq(1)";
    $(ocultaGrupos).hide();
    $("#btn-cerrar").show();
    $("#txtNombreGrupo").focus();
    $("#txtNombreGrupo").prop("readonly", false);
    $("#txtNombreGrupo").val("");
    $("#btn-nuevo").hide();
    $("#btn-eliminar").hide();
    $("#txtNombreGrupo").val("");
    
    
    $("#sTipoDestinatario").attr("disabled", "disabled");
    
//    $("#txtDesDependenciaTemp").val($("#busDependenciaGD input").val());
//    $("#txtDesDependenciaTemp").show();    
//    $("#busDependenciaGD").hide();
    
    
    
    nuevoGrupo = true;
    jsonDetGrupo = [];
    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        $(this).remove();
    });
    return;
    $("#tblNuevoGrupo").show();
}




function fn_guardarGrupoDestVar() {
    fn_grabarGrupoDestinoVar();
}

function fn_grabarGrupoDestinoVar() {
    jsonDetGrupo = [];
    var existeDuplicado = false;
    var depenVacia = false;
    var nombreGrupo = allTrim($("#txtNombreGrupo").val());    
    var codGrupo = $("#codGrupoDestino").val();
    
    if (!nuevoGrupo && cambioDatos == false && !!!nombreGrupo) {
        alert_Warning("ALERTA", "Debe crear o seleccionar un grupo para guardar cambios");
        return;
    }
    
    if (!!!nombreGrupo) {
        alert_Danger("ERROR", "Ingrese un nombre al grupo");
        $("#txtNombreGrupo").focus();
        return;
    }

    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        var aCoEmp = $(this).find("#btn-cargar-dep").attr("coDep");
        
        if (!!!aCoEmp) {
            depenVacia = true;
        } else {
            if (!fn_buscaDetGrupoVar(aCoEmp)) {
                jsonDetGrupo.push({"co_emp": aCoEmp});
            } else {
                existeDuplicado = true;
            }
        }
    });
//    console.log(existeDuplicado);
//    return;

    if (existeDuplicado) {
        alert_Danger("ERROR", "Filas duplicadas corregir.");
    } else {
        if (depenVacia) {
            alert_Danger("ERROR", "Destinatario vacio, corregir.");
        } else {
            if (jsonDetGrupo.length >= 1) {
                var coTipo = $("#coTipo").val();//controlar si no existe este codigo.
                var jsonBody = {"coGruDes": codGrupo, "coDep": coTipo, "deGruDes": nombreGrupo, "grupoDestinoDetalle": jsonDetGrupo};
                var jsonString = JSON.stringify(jsonBody);
                var url;
                if (nuevoGrupo) {
                    url = "/srTablaConfiguracion.do?accion=goNuevoGrupoVar";
                    /*fn_cargarGruposDestinoVar();
                    fn_cancelarGuardarGDVar();*/
                    //return;
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "OK") {
                            alert_Sucess("OK", "Grabó correctamente.");
                            //resetValoresGrupoDestino(); 
                            fn_cargarGruposDestinoVar();
                            fn_cancelarGuardarGDVar();
                            
                        } else {
                            alert_Danger("ERROR:", data);
                        }
                    }, 'text', false, false, "POST");
                } else {
                    if (cambioDatos) {
                        if (!!!codGrupo) {
                            alert_Danger("ERROR", "no se selecciono un grupo");
                            return;
                        } else {
                            url = "/srTablaConfiguracion.do?accion=goUpdateGrupoVar";
                            ajaxCallSendJson(url, jsonString, function(data) { 
                                if (data === "OK") { 
                                    alert_Sucess("OK", "Grabó correctamente.");
                                    var nuevoNombreGrupo = $("#txtNombreGrupo").val();
                                    $("#codGrupoDestino option:selected").text(nuevoNombreGrupo);
                                    resetValoresGrupoDestino();
                                } else {
                                    alert_Danger("ERROR:", data);
                                }
                            }, 'text', false, false, "POST");
                        }
                    } else {
                        alert_Warning("ALERTA", "No se detecto cambios que guardar.");
                    }
                }
            } else {
                alert_Danger("ERROR", "Detalle del grupo vacio.");
            }
        }
    }
}
function fn_iniGrupoDesVar()
{
    $("#coTipo").val($("#sTipoDestinatario").val());
    fn_cargarGruposDestinoVar();    
}

function fn_cancelarGuardarGDVar() {
    $("#btn-nuevo").show();
    $("#btn-cerrar").hide();
    $("#btn-eliminar").show();
    
    /*$("#txtDesDependenciaTemp").hide();    
    $("#busDependenciaGD").show();*/
    $("#sTipoDestinatario").removeAttr('disabled');
    
    $("#txtNombreGrupo").prop("readonly", true);
    
    var muestraGrupos = "#tablaDependencia tr:eq(1)";
    $(muestraGrupos).show();
}


function fn_cargarGruposDestinoVar() {
    var p = new Array();
    p[0] = "accion=goListaGruposDestinosVar";
    p[1] = "vcoTipo=" + jQuery('#coTipo').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        
        refreshScript("divDetalleGrupoDestino", data);
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}


function fn_agregarDetalleGrupoDepenVar() {

    var coGrupo = $("#codGrupoDestino").val();
    var codTipo = $("#coTipo").val();
    var cf = 0;
    if (nuevoGrupo === true || !!coGrupo)
    {
        $("#tblDestDetalle tbody [id='btn-cargar-dep']").each(function() {
            if (!!$(this).attr("coDep") === false) {
                cf++;
            }
        });
        if (cf === 2) {
            alert_Danger("ERROR:", "Corregir dependencia");
            return;
        }
        if (!!codTipo) {
            var fila = "<tr>" + $("#tblDestDetalle tr:eq(0)").html() + "</tr>";
            $("#tblDestDetalle tbody").append(fila);
        } else {
            alert_Warning("MENSAJE", "No se selecciono una dependencia con un grupo");
        }
    } else {
        alert_Warning("MENSAJE", "Tiene que crear un nuevo grupo o seleccionar un grupo");
    }

}

function fn_agregarDependenciaGrupoVar(codDepen, deDepen) {
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtCoDepen']").val(codDepen);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtDesDepen']").val(deDepen);

    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-dep").attr("coDep", codDepen);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coDep", codDepen);


    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-msg span").attr("class", "glyphicon glyphicon-ok").css({color: "green"});


    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtEmpleado']").val("");
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coDep", codDepen);

    removeDomId('windowConsultaDependencias');
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;
    var codGrupoDestino = $("#codGrupoDestino").val();
    if (!!codDepen && !!codGrupoDestino) {
        var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": codDepen};
        var jsonString = JSON.stringify(jsonBody);
        var url = "/srTablaConfiguracion.do?accion=goAgregarDependenciaDestino";
        ajaxCallSendJson(url, jsonString, function(data) {
            if (data === "Datos guardados.") {
                removeDomId('windowConsultaDependencias');
                fn_listaGrupoDetalle();
            } else {
                alert_Danger("ERROR:", "EXISTE UNA FILA CON LOS MISMOS DATOS.");
            }
        }, 'text', false, false, "POST");
    } else {
        alert_Danger("ERROR:", "ocurrio un error cons la dependencia");
    }
}

function fn_cargarDestinoVar(cell) {
    GDindexCurrentSelectedCell = $(cell).parents("tr").index();

    var coTipo = $("#coTipo").val();

    if (!!coTipo) {
        var p = new Array();
        p[0] = "accion=goCargarDestinatarios";
        //p[1] = "codGrupoDest=" + codGrupoDest;
        p[1] = "coTipo=" + coTipo;
        ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");
        return false;
    } else {
        alert_Warning("MENSAJE", "Error no se selecciono una tipo destinatario.");
    }
    
}    
    
    function fn_buscaDetGrupo(coDep, coEmp) {
    for (i = 0; i < jsonDetGrupo.length; i++) {
        if (coDep === jsonDetGrupo[i].co_dep && coEmp === jsonDetGrupo[i].co_emp) {
            return true;
        }
    }
    return false;
}

function fn_eliminarDetalleGrupoVar() {

    var coDepTabla = $("#coTipo").val();
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    $("#tblDestDetalle tbody").find(".row_selected").remove();
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;


    var codGrupoDestino = $("#codGrupoDestino").val();
    var coDepTabla = $("#coTipo").val();
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
    if (!!coDepTabla) {
        bootbox.confirm({
        message: "¿Eliminar fila?",
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
                    var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": coDepTabla, "co_emp": coEmpTabla};
                    var jsonString = JSON.stringify(jsonBody);
                    var url = "/srTablaConfiguracion.do?accion=goEliDetalleGrupoVar";
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos eliminados.") {
                            alert_Sucess("MENSAJE", data);
                            fn_listaGrupoDetalle();
                        } else {
                            alert_Danger("ERROR:", data);
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    } else {
        $("#tblDestDetalle tbody").find(".row_selected").remove();
    }
    return;
}



function fn_buscaDetGrupoVar(coDes) {
    for (i = 0; i < jsonDetGrupo.length; i++) {
        if (coDes === jsonDetGrupo[i].co_des) {
            return true;
        }
    }
    return false;
}

function fn_eliminarDetalleGrupo() {

    var coDepTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
    $("#tblDestDetalle tbody").find(".row_selected").remove();
    if (!!!nuevoGrupo) {
        cambioDatos = true;
    }
    return;


    var codGrupoDestino = $("#codGrupoDestino").val();
    var coDepTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coDep");
    var coEmpTabla = $("#tblDestDetalle tbody").find(".row_selected").find("#btn-cargar-dep").attr("coEmp");
    if (!!coDepTabla) {
        bootbox.confirm({
        message: "¿Eliminar fila?",
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
                    var jsonBody = {"co_gru_des": codGrupoDestino, "co_dep": coDepTabla, "co_emp": coEmpTabla};
                    var jsonString = JSON.stringify(jsonBody);
                    var url = "/srTablaConfiguracion.do?accion=goEliDetalleGrupo";
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos eliminados.") {
                            alert_Sucess("MENSAJE", data);
                            fn_listaGrupoDetalle();
                        } else {
                            alert_Danger("ERROR:", data);
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    } else {
        $("#tblDestDetalle tbody").find(".row_selected").remove();
    }
    return;
}

function fn_eliminarGrupoVar() {
    var codGrupo = $("#codGrupoDestino").val();
    if (!!codGrupo && codGrupo !== "-1") {
        bootbox.confirm({
        message: "¿Está seguro de eliminar este grupo?",
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
                    var jsonBody = {"coGruDes": codGrupo};
                    var jsonString = JSON.stringify(jsonBody);
                    var url = "/srTablaConfiguracion.do?accion=goEliGrupoDestinoVar";
                    ajaxCallSendJson(url, jsonString, function(data) {
                        if (data === "Datos eliminados.") {
                            alert_Sucess("MENSAJE", data);
                            fn_cargarGruposDestinoVar();
                        } else {
                            alert_Danger("ERROR:", "Error al crear el grupo");
                        }
                    }, 'text', false, false, "POST");
                }
            }
        });
    } else {
        alert_Warning("MENSAJE", "Tiene que seleccionar un grupo para eliminarlo.");
    }
}

/*SEGDI*/

//YUAL

function fn_guardarPermisoUsuarios() {
    jsonDetGrupo = [];
    var existeDuplicado = false;
    var depenVacia = false;

    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        var aCoDep = $("#codDependencia").val();
        var aCoEmp = $(this).find("#btn-cargar-emp").attr("coEmp");
        var esAct = $(this).find("[name='txtActivo']").is(':checked');
        esAct = esAct?'0':'1';
        if (!!!aCoDep) {
            depenVacia = true;
        } else {
            if (!fn_buscaPermiso(aCoDep, aCoEmp)) {
                jsonDetGrupo.push({"coDep": aCoDep, "coUse": aCoEmp, "esAct": esAct});
            } else {
                existeDuplicado = true;
            }
        }
    });
    if (existeDuplicado) {
        alert_Danger("ERROR", "Filas duplicadas corregir.");
    } else {
        if (depenVacia) {
            alert_Danger("ERROR", "Dependencia vacia, corregir.");
        } else {
            if (jsonDetGrupo.length >= 1) {
                var codDependencia = $("#codDependencia").val();//controlar si no existe este codigo.
                var jsonBody = {"coDep": codDependencia, "permisoDetalle": jsonDetGrupo};
                var jsonString = JSON.stringify(jsonBody);
                var url;      
                url = "/srTablaConfiguracion.do?accion=goUpdatePermisoUsuario";
                ajaxCallSendJson(url, jsonString, function(data) {
                    if (data === "OK") {
                        alert_Sucess("OK", "Grabó correctamente.");    
                    }
                }, 'text', false, false, "POST"); 
            } else {
                alert_Danger("ERROR", "La dependencia no tiene asignado un empleado.");
            }
        }
    }
}


function fn_listaPermisoDetalle() {
    var codDependencia = $("#codDependencia").val();
    var p = new Array();
    p[0] = "accion=goListaAdmPermisosDet";
    p[1] = "vCodDependencia=" + codDependencia;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaGrupoDestinoDet", data);
        if (!!codDependencia) {
            $("#txtNombreGrupo").val($("#codDependencia option:selected").text());
            $("#txtNombreGrupo").prop("readonly", false);
        } else {
            $("#txtNombreGrupo").prop("readonly", true);
        }
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}


function fn_agregarDetallePermiso(){

    var codDepencia = $("#codDependencia").val();
    var cf = 0;
  
        $("#tblDestDetalle tbody [id='btn-cargar-dep']").each(function() {
            if (!!$(this).attr("coDep") === false) {
                cf++;
            }
        });
        if (cf === 2) {
            alert_Danger("ERROR:", "Corregir dependencia");
            return;
        }
        if (!!codDepencia) {
            var fila = "<tr>" + $("#tblDestDetalle tr:eq(0)").html() + "</tr>";
            $("#tblDestDetalle tbody").append(fila);
        } else {
            alert_Warning("MENSAJE", "No se selecciono una dependencia con un grupo");
        }
    

}


function fn_guardarTipoPermisosUsuarios() {
    var codEmpleado = $("#codEmpleado").val();
    var codDependencia = $("#codDependencia").val();
    var tipoAcceso;
    var tipoConsulta;
    $("#tblDestDetalle tbody tr:gt(0)").each(function() {
        tipoAcceso = $("input[name='rbAcceso']:checked").val();
        tipoConsulta = $("input[name='rbConsulta']:checked").val();
    });
    if (codEmpleado === '') {
        alert_Danger("ERROR", "Seleccione un empleado.");
    } else {
        if (codDependencia === '') {
            alert_Danger("ERROR", "Seleccione una dependencia.");
        } else {
                
                var p = new Array();
                p[0] = "accion=goUpdateTipoPermisoUsuario";
                p[1] = "vCodEmpleado=" + codEmpleado;
                p[2] = "vCodDependencia=" + codDependencia;
                p[3] = "vTipoAcceso=" + tipoAcceso;
                p[4] = "vTipoConsulta=" + tipoConsulta;
                ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
                    if (data === "OK") {
                        alert_Sucess("OK", "Grabó correctamente.");    
                    }
                }, 'text', false, false, "POST"); 
            
        }
    }
}

function fn_buscaEmpleadoTipoPermisos() {
    ajaxCall("/srTablaConfiguracion.do?accion=goDlgListaEmpleadoTipoPermiso", '', function (data) {
        fn_rptaBuscaDependenciaPadreUOAdm(data);
    }, 'text', false, false, "POST");
}

function fn_cargarDependenciasEmpleado() {
    var p = new Array();
    p[0] = "accion=goListaDependenciasEmpleado";
    p[1] = "vCodEmpleado=" + jQuery('#codEmpleado').val();
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divDetalleGrupoDestino", data);
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}


function fn_listaTipoPermisoDetalle() {
    var codEmpleado = $("#codEmpleado").val();
    var codDependencia = $("#codDependencia").val();
    var p = new Array();
    p[0] = "accion=goListaAdmTipoPermisoDet";
    p[1] = "vCodEmpleado=" + codEmpleado;
    p[2] = "vCodDependencia=" + codDependencia;
    ajaxCall("/srTablaConfiguracion.do", p.join("&"), function(data) {
        refreshScript("divListaGrupoDestinoDet", data);
        if (!!codDependencia) {
            $("#txtNombreGrupo").val($("#codGrupoDestino option:selected").text());
            $("#txtNombreGrupo").prop("readonly", false);
        } else {
            $("#txtNombreGrupo").prop("readonly", true);
        }
        resetValoresGrupoDestino();
    }, 'text', false, false, "POST");
}

function fn_buscaPermiso(coDep, coEmp) {
    for (i = 0; i < jsonDetGrupo.length; i++) {
        if (coDep === jsonDetGrupo[i].coDep && coEmp === jsonDetGrupo[i].coUse) {
            return true;
        }
    }
    return false;
}


function fn_cambiarUsuario(cell) {
    GDindexCurrentSelectedCell = $(cell).parents("tr").index();

        var p = new Array();
        ajaxCall("/srTablaConfiguracion.do?accion=goCargarUsuarios", '', function(data) {
            if (data !== null)
            {
                $("body").append(data);
            }
        }, 'text', false, false, "POST");
        return false;

}


function fn_agregarUsuario(codUser, nombre){
    
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-emp").attr("coEmp", codUser);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtEmpleado']").val(nombre);
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("#btn-cargar-act").attr("esAct", '0');
    $("#tblDestDetalle tbody tr:eq(" + GDindexCurrentSelectedCell + ")").find("input[name='txtActivo']").val('0');
    removeDomId('windowConsultaEmpleados'); 
}


function fn_iniConsEmpleadoDependencias(){
    var tableaux = $('#tblElaboradoPor');
    tableaux.find('tr').each(function(index, row) {
        if(index === 0){
            $(this).addClass('row_selected');                        
            return false;
        }
    });
    var searchOnTable = function(evento) {
            var table = $('#tblElaboradoPor');
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
                if (found === true) {
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
            if(evento.which === 13){
                if(isFirst){
                    var pdesDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(0)").html();
                    var pcodDest= $("#tblElaboradoPor tbody tr:eq("+indexSelect+")").find("td:eq(1)").html();
                    fu_setDatoEmpDependencia(pcodDest,pdesDest);
                }
            }
    };        
    $('#txtConsultaFind').keyup(searchOnTable);
    $("#tblElaboradoPor tbody tr").click(function(e) {
        var pdesDest= $(this).find("td:eq(0)").html();
        var pcodDest= $(this).find("td:eq(1)").html();
        fu_setDatoEmpDependencia(pcodDest,pdesDest);            
    });    
}


function fu_setDatoEmpDependencia(cod, desc){
    $("#tablaEmpleado tr:eq(1)").show();
    $("#codEmpleado").val(cod);
    $("#txtEmpleado").val(desc);
    removeDomId('windowConsultaElaboradoPor');
    fn_cargarDependenciasEmpleado();
}

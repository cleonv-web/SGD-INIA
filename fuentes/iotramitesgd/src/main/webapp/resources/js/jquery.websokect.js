//var wsServiceLocation = 'wss://sgd.pcm.gob.pe:8181/wstradoc/chat/';
var _wsocket = null;
var wsRemite = '/BROWSER';
var wsDestino = 'APPCLIENT';
var appOnpeResponse = null;
var nrOperacion = null;

var _call_link_req = 0;
var nroPestana = 0;
var auxBrowVersion = '1.1';
//var urlAplication =  'iotramitesgd-0.0.1-SNAPSHOT';

var wsServiceLocation = "";
var urlAplication = "";
var urlBase = "";
$(function () {
    wsServiceLocation = $("#wsServiceLocation").val();
    urlAplication = $("#urlAplication").val();
    urlBase = document.location.protocol + '//' + document.location.host + '//' + urlAplication;
});


var idCokieSession = new Date().valueOf();
var accionOnDesktopTramiteDoc = {
    ejecutaFirma: 1,
    abrirDocumento: 2,
    abrirDocumentoPC: 3,
    cargarDocumento: 4,
    generaDocumento: 5,
    verificaSiExisteDoc: 6,
    getDirectorio: 7,
    abrirRutaDocs: 8,
    verificarRutaDirectorio: 9
};

var tipoEjecucionAppDesktop = {
    value: 0,
    noEjecutarAppDesktop: 0,
    ejeAppDesktopModoNormal: 1
};

function getIdChannelWS() {
    return Math.round(Math.random() * 0x1000000);
}

function onMessageReceivedWS(evt, callback) {
    var msg = JSON.parse(evt.data); // native API
    processMessageRecived(msg, callback);
}

function processMessageRecived(msg, callback) {
    //if (idChannel !== $.cookie('idChannel')) {
    var idChannel = idCokieSession;//VALIDACION DE SESSION
    if (idChannel !== idCokieSession) {
        try {
            cerrarConexion_wsocket();
        } catch (ex) {
        }
    }
    switch (msg.accion) {
        case "TERMINATE_APP":
            var parm = "{\"retval\": \"OK\"}";
            sendMessageWS_CONTINUE_APP(JSON.stringify(parm), "CONTINUE_APP");
            break;
        case "CONEXION":
            var datosPC = JSON.parse(msg.message);
            if (!!datosPC && msg.nrOperacion === nrOperacion.toString()) {
                callback(datosPC);
            }
            break;
        case "SELECCIONAR_DIRECTORIO":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                if (auxMsg.error === "0") {
                    callback(auxMsg.message);
                } else {
                    alert(auxMsg.message);
                }
            }
            break;
        case "VER_DOCUMENTO":
            var auxMsg = msg;
            if (!!auxMsg && msg.nrOperacion === nrOperacion.toString()) {
                if (auxMsg.error === "1") {
                    alert(auxMsg.message);
                }
            }
            break;
        case "ABRIR_DOCUMENTO_PC":
            var auxMsg = msg;
            if (!!auxMsg && msg.nrOperacion === nrOperacion.toString()) {
                if (auxMsg.error === "0") {
                    callback(auxMsg.message);
                } else {
                    alert_Danger(" ", auxMsg.message);
                }
            }
            break;
        case "CARGAR_DOCUMENTO":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                /*if(auxMsg.error==="0"){
                    callback(auxMsg.message);
                }else{
                    alert_Danger("Repositorio: ", auxMsg.message);
                    //alert(auxMsg.message);
                }*/
                callback(auxMsg);
            }
            break;
        case "GENERAR_DOCUMENTO":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                if (auxMsg.error === "0") {
                    callback(auxMsg.message);
                } else {
                    alert(auxMsg.message);
                }
            }
            break;
        case "VERIFICAR_EXISTE_DOC":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                if (auxMsg.error === "0") {
                    callback(auxMsg.message);
                } else {
                    alert(auxMsg.message);
                }
            }
            break;
        case "EJECUTAR_FIRMA":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                /*if(auxMsg.error==="1"){
                    alert(auxMsg.message);
                }*/
                callback(auxMsg);
            }
            break;
        case "VERIFICAR_DIRECTORIO":
            var auxMsg = msg;
            if (!!auxMsg && auxMsg.nrOperacion === nrOperacion.toString()) {
                callback(auxMsg);
            }
            break;
        default:
            alert(msg);
            break;
    }
}

function sendMessageWS_CONTINUE_APP(msg, accion) {
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"' + accion + '" ,"nrOperacion":"' + nrOperacion + '"}';
    _wsocket.send(msg);
}

function sendMessageWS(msg, accion) {
    nrOperacion = Math.round(Math.random() * 0x1000000);
    var msg = '{"message":' + msg + ', "sender":"", "destination":"' + wsDestino + '" ,"accion":"' + accion + '" ,"nrOperacion":"' + nrOperacion + '"}';
    console.log(msg);
    _wsocket.send(msg);
}

function wsVerificarConexion() {
    sendMessageWS(null, "CONEXION");
}

function connectToChatDesktop() {
    _wsocket = new WebSocket(wsServiceLocation + idCokieSession + wsRemite);
    _wsocket.onmessage = onMessageReceivedWS;
}

function connectToChatDesktopCallback(callback) {
    _wsocket = new WebSocket(wsServiceLocation + idCokieSession + wsRemite);
    _wsocket.onmessage = onMessageReceivedWS;
    var auxTime = 0;
    var waitToResponseWS = function () {
        auxTime = auxTime + 5;
        var clearTime = auxTime > 1000 * 15/*(15 segundos)*/;
        if (_wsocket.readyState === _wsocket.OPEN) {
            clearInterval(time);
            callback(false);
        } else if (clearTime) {
            clearInterval(time);
        }
    };
    var time = setInterval(waitToResponseWS, 5);
}

function runOnDesktop(pAccionOnDesktop, param, callback) {
    var auxEjecucion = tipoEjecucionAppDesktop.value;
    if (auxEjecucion === tipoEjecucionAppDesktop.ejeAppDesktopModoNormal) {
        var idChannel = idCokieSession;
        //if (idChannel !== $.cookie('idChannel')) {
        if (idChannel !== idCokieSession) {
            try {
                cerrarConexion_wsocket();
            } catch (ex) {
            }
            try {
                connectToChatDesktopCallback(function () {
                    fn_runOnDesktop(pAccionOnDesktop, param, function (data) {
                        callback(data);
                    });
                });
            } catch (ex) {
            }
            //idChannel = $.cookie('idChannel');
        } else {
            fn_runOnDesktop(pAccionOnDesktop, param, function (data) {
                callback(data);
            });
        }
    } else {
        switch (pAccionOnDesktop) {
            case 2:
                var rpta = fn_abrirPdf(param.urlDoc, param.rutaDoc);
                callback(rpta);
                break;
            case 6:
                //verificar si existe
                var rpta = "No se puede Ejecutar Instale Tramite Documentario";
                alert_Danger("!Error : ", rpta);
                callback("NO");
                break;
            default:
                var rpta = "No se puede Ejecutar Instale Tramite Documentario";
                alert_Danger("!Error : ", rpta);
                callback(false);
        }
    }
}

function fn_runOnDesktop(pAccionOnDesktop, param, callback) {
    switch (pAccionOnDesktop) {
        case 1:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_firmarDocDesktop(param.urlDoc, param.rutaDoc, param.tipoFirma);
            break;
        case 2:
            fn_abrirDocDesktop(param.urlDoc, param.rutaDoc);
            callback(false);
            break;
        case 3:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_abrirDocFromPCDesktop(param.rutaDoc);
            break;
        case 4:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_cargarDocumentoDesktop(param.urlDoc, param.rutaDoc);
            break;
        case 5:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_generaDocumentoDesktop(param.urlDoc, param.rutaDoc, param.remplazaArchivo);
            break;
        case 6:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_verificaSiExisteDocDesktop(param.rutaDoc);
            break;
        case 7:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_selecDirectorioDesktop();
            break;
        case 8:
            fn_abrirRutaDocsDesktop();
            break;
        case 9:
            _wsocket.onmessage = function (evt) {
                onMessageReceivedWS(evt, callback);
            };
            fn_verificarRutaDirectorio(param.rutaDir);
            break;
        default:
    }
}

function fn_abrirDocDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": " + JSON.stringify(purlDoc) + ", \"rutaDoc\": " + JSON.stringify(prutaDoc) + "}";
    try {
        sendMessageWS(JSON.stringify(param), "VER_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
}

function fn_cargarDocumentoDesktop(purlDoc, prutaDoc) {
    var param = "{\"urlDoc\": " + JSON.stringify(purlDoc) + ", \"rutaDoc\": " + JSON.stringify(prutaDoc) + "}";
    try {
        sendMessageWS(JSON.stringify(param), "CARGAR_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en cargar el documento");
    }
}

function fn_verificaSiExisteDocDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": " + JSON.stringify(prutaDoc) + ",\"verBloqueo\": false}";
    try {
        sendMessageWS(JSON.stringify(param), "VERIFICAR_EXISTE_DOC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
}

function fn_abrirDocFromPCDesktop(prutaDoc) {
    var param = "{\"rutaDoc\": " + JSON.stringify(prutaDoc) + "}";
    try {
        sendMessageWS(JSON.stringify(param), "ABRIR_DOCUMENTO_PC");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
}

function fn_cargarAppOnpePrincipal() {
    appOnpeConexion(function (data) {
        desktopCargadoWS(data);
    });
}

function appOnpeConexion(callback) {
    _wsocket.onmessage = function (evt) {
        onMessageReceivedWS(evt, callback);
    };
    appOnpeResponse = "";
    var auxTime = 0;
    var waitToResponseApp = function () {
        auxTime = auxTime + 250;
        var clearTime = auxTime <= 1000 * 30/*(30 segundos)*/;
        if (!!!appOnpeResponse && clearTime) {
            if (_wsocket.readyState === _wsocket.OPEN) {
                wsVerificarConexion();
            }
            //Aqui se puede poner un loading.
        } else {
            clearInterval(time);
            if (!!!appOnpeResponse) {
                try {
                    _wsocket !== null ? _wsocket.close() : '';
                } catch (ex) {
                }
            }
            //aqui se puede terminar el loading.
        }
    };
    var time = setInterval(waitToResponseApp, 250);
}

function fn_abrirRutaDocsDesktop() {
    try {
        sendMessageWS(null, "VER_RUTA_PRINCIPAL");
    } catch (ex) {
        alert("Fallo en ruta.");
    }
}

function fn_selecDirectorioDesktop() {
    try {
        sendMessageWS(null, "SELECCIONAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en Seleccionar directorio.");
    }
}

function fn_generaDocumentoDesktop(urlDoc, rutaDoc, remplazaArchivo) {
    try {
        if (!!!remplazaArchivo) {
            remplazaArchivo = false;
        }
        var param = "{\"urlDoc\": " + JSON.stringify(urlDoc) + ", \"rutaDoc\": " + JSON.stringify(rutaDoc) + ", \"remplazaArchivo\": " + remplazaArchivo + "}";
        sendMessageWS(JSON.stringify(param), "GENERAR_DOCUMENTO");
    } catch (ex) {
        alert("Fallo en generar el documento");
    }
}

function cerrarConexion_wsocket() {
    sendMessageWS(null, "TERMINATE_APP");
    _wsocket.close();
}

function fn_firmarDocDesktop(urlDoc, rutaDoc, tipoFirma) {
    var param = "{\"urlDoc\": " + JSON.stringify(urlDoc) + ", \"rutaDoc\": " + JSON.stringify(rutaDoc) + ", \"tipoFirma\": " + JSON.stringify(tipoFirma) + "}";
    try {
        sendMessageWS(JSON.stringify(param), "EJECUTAR_FIRMA");
    } catch (ex) {
        alert("Fallo en abrir el documento");
    }
}

function fn_abrirRutaDocs() {
    var retval = "NO";
    runOnDesktop(accionOnDesktopTramiteDoc.abrirRutaDocs, null);

}


function appletCargadoRutaDoc(datosPC) {
    jQuery("#RutaDocs").html("<strong>" + datosPC + "</strong>");
}

function desktopCargadoWS(datosPC) {
    appletCargadoRutaDoc(decodeURI(datosPC.rutaPrincipal));
    $("#appletIco span").attr("class", "glyphicon glyphicon-ok-circle").css({color: '#5CB85C'});
    appOnpeResponse = "OK";
    tipoEjecucionAppDesktop.value = tipoEjecucionAppDesktop.ejeAppDesktopModoNormal;
}


function fn_verificarRutaDirectorio(prutaDir) {
    var param = "{\"rutaDir\": " + JSON.stringify(prutaDir) + "}";
    try {
        sendMessageWS(JSON.stringify(param), "VERIFICAR_DIRECTORIO");
    } catch (ex) {
        alert("Fallo en verificar ruta Directorio.");
    }
}

function fn_genDocDesktop(param, callback) {
    runOnDesktop(accionOnDesktopTramiteDoc.generaDocumento, param, function (data) {
        var retval = data;
        callback(retval);
    });
}

function fn_cargaDocDesktop(urlDoc, rutaDoc, callback) {
    var retval = "";
    var param = {rutaDoc: rutaDoc, verBloqueo: false};
    runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function (data) {
        retval = data;
        if (retval === "SI") {
            var param = {urlDoc: urlDoc, rutaDoc: rutaDoc};
            runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function (data) {
                retval = data;
                if (retval.error === "0") {
                    callback(retval.message);
                } else {
                    bootbox.alert("<h5>" + retval.message + rutaDoc + "</h5>", function () {
                        bootbox.hideAll();
                        callback("ERROR");
                    });
                }
            });
        } else {
            bootbox.alert("<h5>El documento NO existe en: " + rutaDoc + "</h5>", function () {
                bootbox.hideAll();
                callback(retval);
            });
        }
    });
}

function fn_selecDirectorioOnDesktop() {
    var dir = "";
    runOnDesktop(accionOnDesktopTramiteDoc.getDirectorio, null, function (data) {
        dir = data;
        $("#txtDirPrincipal").attr("value", dir);
    });
}

function fn_FirmarDocumento() {
    var p = new Array();
    p[0] = "accion=cargaRutadoc";
    p[1] = "codigo=" + $("#CodigoRecepcion").val();
    ajaxCall("/" + urlAplication + "/DataServlet", p.join("&"), function (data) {
        eval("var docs=" + data);
        $("#hiddenNameDocfirms").val(docs.rutaDocName);
        var param = {urlDoc: docs.rutaDocUrl, rutaDoc: docs.rutaDocName, tipoFirma: "0$0"};
        runOnDesktop(accionOnDesktopTramiteDoc.ejecutaFirma, param, function (data) {
            $("#btnEnviarFirmaCargo").show();
        });

    }, 'text', false, false, "POST");
}

function cargaDocFirma(pnoDoc, callback) {
    var CodigoRecepcion = $("#CodigoRecepcion").val();
    var docs;
    if (!!CodigoRecepcion) {
        var p = new Array(
            "accion=rutaCargaFirmaDoc",
            "codigo=" + $("#CodigoRecepcion").val()
        );
        ajaxCall("/" + urlAplication + "/DataServlet", p.join("&"), function (data) {
            eval("docs=" + data);
            if (typeof (docs) !== "undefined" && typeof (docs.retval) !== 'undefined') {
                if (docs.retval === "OK") {
                    var retval = "";
                    try {
                        var param = {urlDoc: docs.noUrl, rutaDoc: pnoDoc};
                        runOnDesktop(accionOnDesktopTramiteDoc.cargarDocumento, param, function (data) {
                            retval = data;
                            callback(retval);
                            return;
                        });
                    } catch (ex) {
                        alert_Danger("Firma!: ", "Fallo en subir documento al servidor");
                        retval = "ERROR";
                        resulCarga = retval;
                        callback(resulCarga);
                        return;
                    }
                } else {
                    alert_Danger("Firma!: ", docs.retval);
                    resulCarga = "ERROR";
                    callback(resulCarga);
                    return;
                }
            }
        }, 'text', true, false, "POST");
    }
}

function activate_recepcion(valDoc, vnoDoc) {
    if (valDoc === "SI") {
        cargaDocFirma(vnoDoc, function (data) {
            var resulCarga = data;
            if (resulCarga.error === "0") {
                alert_Sucess("Aviso!", "El documento se cargó correctamente en la ruta temporal.");
                $("#divRecepcionarDoc").show();
                /*var p = new Array();
                p[0] = "accion=enviarDocRecp";
                p[1] = "codigo="+$("#CodigoRecepcion").val();
                ajaxCall("/"+urlAplication+"/DataServlet", p.join("&"), function (data) {
                    if (data !== null) {
                           if (data.coRespuesta === "1") {

                           } else {
                               alert_Danger("Emisión!",data.deRespuesta);
                           }
                       }
               }, 'json', false, false, "POST");*/
            } else {
                alert_Danger("Aviso!", "Error al cargar al servidor el archivo PDF.");
            }
        });
    } else {
        alert_Danger("Firma!", "El Documento no esta Firmado.");
    }
}

function EmitirDocumentoClick() {
    var vnoDoc = "";
    var rutaDocFirma = $("#hiddenNameDocfirms").val();
    if (!!rutaDocFirma) {
        vnoDoc = rutaDocFirma.substring(0, rutaDocFirma.length - 4) + "[F].pdf";
        var param = {rutaDoc: vnoDoc};
        runOnDesktop(accionOnDesktopTramiteDoc.verificaSiExisteDoc, param, function (data) {
            valDoc = data;
            if (valDoc === "SI") {
                activate_recepcion(valDoc, vnoDoc);
                return;
            } else {
                alert_Danger("Firma!", "El Documento no está Firmado.");
            }
        });


    }
}


function ajaxCall(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = url;
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function (request, status, error) {
            //alert(request.responseText);
            console.log(request.responseText);
        },
        success: function (result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }
    };
    if (typeof (params) == 'object') {
        objAjax.data = params;
    } else if (typeof (params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function alert_Sucess(titulo, msg) {
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;top: 5px;left: 5px;">')
        .appendTo("#frmCargoTramite")
        .html(msgHtml)
        .addClass("notification alert alert-success");

    $('#notificacion_proyect').fadeIn("slow", function () {
        removeNotification();
    });
}

function alert_Danger(titulo, msg) {
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;top: 5px;left: 5px;">')
        .appendTo("#frmCargoTramite")
        .html(msgHtml)
        .addClass("notification alert alert-danger");
    $('#notificacion_proyect').fadeIn("slow", function () {
        removeNotification();
    });
}

function alert_Warning(titulo, msg) {
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;top: 5px;left: 5px;">')
        .appendTo("#frmCargoTramite")
        .html(msgHtml)
        .addClass("notification alert alert-warning");
    $('#notificacion_proyect').fadeIn("slow", function () {
        removeNotification();
    });
}

function alert_Info(titulo, msg) {
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;top: 5px;left: 5px;">')
        .appendTo("#frmCargoTramite")
        .html(msgHtml)
        .addClass("notification alert alert-info");

    $('#notificacion_proyect').fadeIn("slow", function () {
        removeNotification();
    });
}

function removeNotification() {
    jQuery(document).mouseup(function (e) {
        var searchcontainer = $("#notificacion_proyect");
        if (!searchcontainer.is(e.target)
            && searchcontainer.has(e.target).length === 0) {
            searchcontainer.fadeOut("slow", function () {
                searchcontainer.remove();
            });
        }
    });
}

var isCallAppDesktop = false;
$(function () {
    var waitToResponseApp = function () {
        if (ispageLoad) {
            if (!isCallAppDesktop && nroPestana === 0) {
                isCallAppDesktop = true;

                try {
                    var rutaPri = '';
                    var url = 'Tramitedoc:accion=TraDoc?ws=' + wsServiceLocation + idCokieSession + '/?urlBase=' + urlBase + '?rutaPri=' + rutaPri;
                    var desktopObj = document.getElementById('idTradocDesktop');
                    desktopObj.href = url;
                    desktopObj.click();
                    desktopObj.href = "javascript:void(0);";

                } catch (ex) {
                    console.log(ex);
                }


            } else {
                clearInterval(myTime);
                connectToChatDesktop();
                fn_cargarAppOnpePrincipal();
            }
        }
    };
    var myTime = setInterval(waitToResponseApp, 500);
});
ispageLoad = true;

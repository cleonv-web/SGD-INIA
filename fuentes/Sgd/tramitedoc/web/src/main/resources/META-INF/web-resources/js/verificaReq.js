//var configJava = {
//    minVersionJava: "1.7.0",
//    minRevisionJava: "51"
//};
function fn_mostrar_req_so() {
    var p = new Array();
    p[0] = "accion=goVerRequisitos";
    ajaxCall("/srDepAcceso.do", p.join("&"), function(data) {
        if (data !== null) {
            jQuery("body").append(data);
        }
    }, 'text', false, false, "POST");
}
function liberaApplet() {
    $("#verificaPreRequisitos").remove();
}

//function loadAppletVerificaReqPc() {
//    try {
//        var auxCode = "000" + Math.floor((Math.random() * 100) + 1);
//        var parameters = {
//            testParameter: auxCode
//        };
//        var configuracionApplet = {
//            id: "exeAppletVeriReqPC",
//            code: "pe.gob.onpe.applet.VerificaPreReqPC.class",
//            archivo: "appletVerificaConfig-1.0.jar",
//            codeBase: "applet/",
//            width: 10,
//            height: 10,
//            parametros: parameters,
//            divDestino: "objApplet"
//        }
//        appletCall(configuracionApplet);
//    } catch (e) {
//       bootbox.alert("No se pudo cargar el applet")
//    }
//}

function fnInfoOfPC(infoPC)
{
    try {
        javaFullVer=infoPC.javaFullVer;
        if (!!javaFullVer) {
            var arrayTemp = javaFullVer.split("_");
            var version = parseFloat(arrayTemp[0]);
            
            if (version === parseFloat(configJava.minVersionJava)) {
                mostrarInfoPC(infoPC);
                return;
            }
            if (version < parseFloat(configJava.minVersionJava)) {
                mostrarInfoPC(infoPC);
                return;
            }
            if (version > parseFloat(configJava.minVersionJava)) {
                mostrarInfoPC(infoPC);
                return;
            }
            if (!!!version) {
                mostrarInfoPC(infoPC);
                return;
            }
           bootbox.alert("Error inesperado.");
        } else {
           bootbox.alert("no se pudo determinar la version de Java instalado.");
        }
    } catch (e) {
       bootbox.alert("Error al cargar la informacion para el applet.");
    }
    return;
}
function mostrarInfoPC(infoPC) {
    try {
        var msgDatosPC=infoPC.pcData;
        var msgJavaVerFull=infoPC.javaFullVer;
        var msgWordVer=infoPC.officeVer;
        var msgAdobeVer=infoPC.adobeVer;
        var msgFirmaVer=infoPC.reFirmaVer;
        var msgFirmasDigitales=infoPC.cerDigi;
        var msgTSLReniec=infoPC.tslReniec;
        var msgTSLIndecopi=infoPC.tslIndecopi;
        var msgCRLReniec=infoPC.crlReniec;
        $("#divNoJava").hide();
        $("#tablaReqDet").css({visibility: "visible"});
        var arrayTemp = msgJavaVerFull.split("_");
        var version = parseFloat(arrayTemp[0]);
        var revision = parseInt(arrayTemp[1]);
        var minRevision = parseInt(configJava.minRevisionJava);

        if (!!msgDatosPC && msgDatosPC !== "error") {
            $("#verPC").append(msgDatosPC);
        } else {
            $("#verPC").append("<span style='color:#FF4500' > ERROR en obtener los datos PC</span>").append($("#icoError").clone());
        }

        if (!!msgFirmasDigitales && msgFirmasDigitales !== "error") {
            $("#veriFirmasDigitales").append(msgFirmasDigitales);
        } else {
            $("#veriFirmasDigitales").append("<span style='color:#FF4500' > No se encontro ningun certificado digital. </span>").append($("#icoError").clone());
        }
        var auxBrowVersion = $.browser.version;
        var ie;
        if (!!$.browser.msie) {
            ie = true;
        } else {
            if ((/Trident\/7\./).test(navigator.userAgent)) {
                ie = true;
            } else {
                ie = false;
            }
        }
        if (ie) {
            if (auxBrowVersion <= 9)
            {
                $("#browser").append("IE version " + auxBrowVersion + "<span style='color:#FFAF6A' >, actualizar IE versión 10 o superior</span>").append($("#icoWarnig").clone()).append("<br/>(Recomendamos usar navegador Chrome)");
            } else {
                $("#browser").append("IE version " + auxBrowVersion).append($("#icoOk").clone()).append("(Recomendamos usar navegador Chrome)");
            }
        } else {
            var auxNavName;
            if ($.browser.mozilla) {
                auxNavName = "Firefox";
                $("#browser").append("Browser " + auxNavName + " ver. " + auxBrowVersion).append($("#icoOk").clone());
            } else {
                if ($.browser.chrome) {
                    auxNavName = "Chrome";
                    $("#browser").append("Browser " + auxNavName + " ver. " + auxBrowVersion).append($("#icoOk").clone());
                } else {
                    $("#browser").append("<span style='color:#FF4500' > Browser Desconocido ver. Instale Chrome</span>").append($("#icoError").clone());
                }
            }
        }

        if (!!version) {
            if (version === parseFloat(configJava.minVersionJava))
            {
                if (!!revision) {
                    if (revision === minRevision)
                    {
                        $("#veriJava").append(msgJavaVerFull).append($("#icoOk").clone());
                    }
                    if (revision < minRevision)
                    {
                        $("#veriJava").append(msgJavaVerFull + "<span style='color:#FF4500' >, actualice su revisión de java </span>").append($("#icoWarnig").clone()).append("<br/><span style='color:#FF4500' >(Versión recomendada " + configJava.minVersionJava + "_" + configJava.minRevisionJava + " 32 bits)<span>");
                    }
                    if (revision > minRevision)
                    {
                        $("#veriJava").append(msgJavaVerFull + "<span style='color:#FFAF6A' >, revisión de java aun no testeada </span>").append($("#icoWarnig").clone());
                    }
                } else {
                    $("#veriJava").append(version + "<span style='color:#FF4500' >, no se detecto la revisión de java actualice </span>").append($("#icoError").clone());
                }
            }
            if (version < parseFloat(configJava.minVersionJava))
            {
                $("#veriJava").append(msgJavaVerFull + "<span style='color:#FF4500' > actualice su version de java </span>").append($("#icoError").clone()).append("<br/>(Versión recomendada " + configJava.minVersionJava + "_" + configJava.minRevisionJava + ")");
            }
            if (version > parseFloat(configJava.minVersionJava))
            {
                $("#veriJava").append(msgJavaVerFull + "<span style='color:#FFAF6A' > Version de java aun no testeada</span>").append($("#icoWarnig").clone()).append("<br/>(Versión recomendada " + configJava.minVersionJava + "_" + configJava.minRevisionJava + ")");
            }
        } else {
            $("#veriJava").append("<span style='color:#FF4500' > No se detecto la version de su java</span>").append($("#icoError").clone()).append("<br/>(Versión recomendada " + configJava.minVersionJava + "_" + configJava.minRevisionJava + ")");
        }

        if (msgTSLIndecopi !== "error") {
            $("#veriTSLIndecopi").append(msgTSLIndecopi).append($("#icoOk").clone());
        } else {
            $("#veriTSLIndecopi").append("<span style='color:#FF4500' > TSL no responde </span>").append($("#icoError").clone());
        }
        if (msgCRLReniec !== "error") {
            $("#veriCRLReniec").append(msgCRLReniec).append($("#icoOk").clone());
        } else {
            $("#veriCRLReniec").append("<span style='color:#FF4500' > CRL no responde </span>").append($("#icoError").clone());
        }        
        if (msgTSLReniec !== "error") {
            $("#veriTSLReniec").append(msgTSLReniec).append($("#icoOk").clone());
        } else {
            $("#veriTSLReniec").append("<span style='color:#FF4500' > TSL no responde </span>").append($("#icoError").clone());
        }
        if (msgFirmaVer !== "error") {
            $("#veriReFirma").append(msgFirmaVer).append($("#icoOk").clone());
        } else {
            $("#veriReFirma").append("<span style='color:#FF4500' > Firma no instalada </span>").append($("#icoError").clone());
        }

        if (msgAdobeVer !== "error") {
            $("#veriAdobe").append(msgAdobeVer).append($("#icoOk").clone());
        } else {
            $("#veriAdobe").append("<span style='color:#FF4500' > Adobe R. no instalado </span>").append($("#icoError").clone());
        }

        if (msgWordVer !== "error") {

            var auxWarning = "<span style='color:#FFAF6A' > ,Office antiguo actualizar Office</span>";
            switch (msgWordVer)
            {
                case "Office 98":
                    $("#veriWord").append(msgWordVer + auxWarning).append($("#icoWarnig").clone());
                    break;
                case "Office 2000":
                    $("#veriWord").append(msgWordVer + auxWarning).append($("#icoWarnig").clone());
                    break;
                case "Office XP":
                    $("#veriWord").append(msgWordVer + auxWarning).append($("#icoWarnig").clone());
                    break;
                case "Office 2003":
                    $("#veriWord").append(msgWordVer + auxWarning).append($("#icoWarnig").clone());
                    break;
                case "Office 2007":
                    $("#veriWord").append(msgWordVer + auxWarning).append($("#icoWarnig").clone());
                    break;
                case "Office 2010":
                    $("#veriWord").append(msgWordVer).append($("#icoOk").clone());
                    break;
                case "Office 2013":
                    $("#veriWord").append(msgWordVer).append($("#icoOk").clone());
                    break;
                case "Version no reconocida":
                    break;
            }

        } else {
            $("#veriWord").append("<span style='color:#FF4500' > Office no instalado </span>").append($("#icoError").clone());
        }
        loadding(false);
        return;
    } catch (ex) {
        loadding(false);
        $("#divNoJava").hide();
        $("#tablaReqDet").css({visibility: "visible"});
        $("#veriJava").append(msgJavaVerFull + "<span style='color:#FF4500' > error critico actualizar versión de JAVA</span>").append($("#icoError").clone()).append("<br/>(Versión recomendada " + configJava.minVersionJava + "_" + configJava.minRevisionJava + ")");
        //console.log(ex.message);
        return;
    }
}






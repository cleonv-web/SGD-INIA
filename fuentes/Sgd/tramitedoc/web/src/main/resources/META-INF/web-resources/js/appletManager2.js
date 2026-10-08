
var javaApplet = {
    value: 0,
    noJavaDetected: 0,
    oldJava: 1,
    updatedJava: 2,
    futureJava: 3
};

var soportaCallFunApplet = false;
var configJava = {
    minVersionJava: "",
    minRevisionJava: "",
    pRutaEmi: ""
};
setValuesConfigJava();
function setValuesConfigJava(){
    var aux=versionJRE.split('_');
    configJava.minVersionJava=aux[0];
    configJava.minRevisionJava=aux[1];
}
var tipoEjecucionApplet = {
    value: 0,
    noEjecutarApplet: 0,
    ejeAppletModoNormal: 1,
    ejeAppletModoAuxilar: 2
};

function configAppletAuxiliar(className, parameters) {
    var configuracionApplet = {
        id: "appletAux",
        code: "pe.gob.onpe.applet." + className + ".class",
        archivo: "appletTramiteDoc-1.0.jar",
        codeBase: pRutaContexto + "/applet",
        width: 16,
        height: 16,
        parametros: parameters,
        divDestino: "appletAux"
    };
    return configuracionApplet;
}

function fn_AppletEjecutaFirma(a, b, callback) {
    var auxEjecucion = tipoEjecucionApplet.value;
    if (auxEjecucion === tipoEjecucionApplet.ejeAppletModoNormal) {
        var rpta = fn_abrirDocAppletModoNormal(a, b);
        callback(rpta);
    }
    if (auxEjecucion === tipoEjecucionApplet.ejeAppletModoAuxilar) {
        var confApplet = configAppletAuxiliar("AppletAbrirDocumento", {a: a, b: b});
        appletCall(confApplet, function(data) {
            if (data.value !== "error") {
                callbak(data.value);
               bootbox.alert(data.value);
            } else {
               bootbox.alert(data.message);
            }
        });
    }
    if (auxEjecucion === tipoEjecucionApplet.noEjecutarApplet) {
        callback(data);
    }
}

var appletsTramiteDoc = {
    ejecutaFirma: 1,
    abrirDocumento: 2,
    abrirDocumentoPC: 3,
    cargarDocumento: 4,
    generaDocumento: 5,
    verificaSiExisteDoc: 6,
    getDirectorio: 7,
    abrirRutaDocs: 8
};

function runApplet(applet, param, callback) {
    var rpta = "error";
    var auxEjecucion = tipoEjecucionApplet.value;
    if (auxEjecucion === tipoEjecucionApplet.ejeAppletModoNormal) {

        switch (applet)
        {
            case 1:
                rpta = fn_firmarDocApplet(param.urlDoc, param.rutaDoc, param.tipoFirma);
                callback(rpta);
                return;
                break;
            case 2:
                rpta = fn_abrirDocApplet(param.urlDoc, param.rutaDoc);
                callback(rpta);
                return;
                break;
            case 3:
                rpta = fn_abrirDocFromPCApplet(param.rutaDoc);
                callback(rpta);
                return;
                break;
            case 4:
                rpta = fn_cargarDocumentoApplet(param.urlDoc, param.rutaDoc);
                callback(rpta);
                return;
                break;
            case 5:
                rpta = fn_generaDocumentoApplet(param.urlDoc, param.rutaDoc,param.remplazaArchivo);
                callback(rpta);
                return;
                break;
            case 6:
                rpta = fn_verificaSiExisteDocApplet(param.rutaDoc);
                callback(rpta);
                return;
                break;
            case 7:
                rpta = fn_selecDirectorioApplet();
                callback(rpta);
                return;
                break;
            case 8:
                rpta = fn_abrirRutaDocsApplet();
                callback(rpta);
                return;
                break;
            default:
        }

    }
    //alert("Entra a applet auxiliar");
    if (auxEjecucion === tipoEjecucionApplet.ejeAppletModoAuxilar) {
        var confApplet = "";
        param.pRuta = configJava.pRutaEmi.replace("|", "/");
        switch (applet)
        {
            case 1:
                confApplet = configAppletAuxiliar("AppletEjecutaFirma", param);
                break;
            case 2:
                confApplet = configAppletAuxiliar("AppletAbrirDocumento", param);
                break;
            case 3:
                confApplet = configAppletAuxiliar("AppletAbrirDocFromPC", param);
                break;
            case 4:
                confApplet = configAppletAuxiliar("AppletCargarDocumento", param);
                break;
            case 5:
                confApplet = configAppletAuxiliar("AppletGeneraDocumento", param);
                break;
            case 6:
                confApplet = configAppletAuxiliar("AppletVerificaSiExisteDoc", param);
                break;
            case 7:
                confApplet = configAppletAuxiliar("AppletGetDirectorio", param);
                break;
            case 8:
                confApplet = configAppletAuxiliar("AppletAbrirRutaDocs", param);
                break;
            default:
        }

        appletCall(confApplet, function(data) {
            if (data.value !== "error") {
                callback(data.value);
//               bootbox.alert(data.value);
            } else {
               bootbox.alert(data.message, function(){ callback(data.value); });//AQUI TENER EN CUENTA QUE HAY QUE MOSTRAR UN ERROR.
                
            }
        });
    }
    if (auxEjecucion === tipoEjecucionApplet.noEjecutarApplet) {

        switch (applet)
        {
            case 2:
                rpta = fn_abrirPdf(param.urlDoc, param.rutaDoc);
                callback(rpta);
                break;
            default:
                var rpta = "No se puede Ejecutar requiere Java";
                alert_Danger("!Error : ", rpta);
                callback(rpta);
        }


    }


}

function fn_cargarAppletPincipal(codeBase, pRutaEmi) {
    configJava.pRutaEmi = pRutaEmi;
    var configuracionApplet = {
        id: "firmarDocumento",
        code: "pe.gob.onpe.applet.FirmarDocumentoGeneral.class",
        archivo: "appletTramiteDoc-1.0.jar",
        codeBase: codeBase,
        width: 32,
        height: 32,
        parametros: {pRuta: configJava.pRutaEmi},
        divDestino: "appletDoc"
    };
    appletCall(configuracionApplet, function(data) {
        if (data.value !== "error") {
            soportaCallFuncionesApplet();
            setTipoEjecucionApplet();
            //tipoEjecucionApplet.value=tipoEjecucionApplet.ejeAppletModoAuxilar;//Convertir a ejecución modo auxiliar
            if (tipoEjecucionApplet.value === tipoEjecucionApplet.ejeAppletModoNormal) {
                $("#appletIco span").attr("class", "glyphicon glyphicon-ok-circle").css({color: '#5CB85C'});
            }
            if (tipoEjecucionApplet.value === tipoEjecucionApplet.ejeAppletModoAuxilar) {
                $("#appletIco span").attr("class", "glyphicon glyphicon-exclamation-sign").css({color: 'orange'});
            }
//           bootbox.alert(data.value);
        } else {
           bootbox.alert(data.message);
        }
    });
}
function fn_cargarAppletVerificaConfig() {
    var configuracionApplet = {
        id: "exeAppletVeriReqPC",
        code: "pe.gob.onpe.applet.VerificaPreReqPC.class",
        archivo: "appletVerificaConfig-1.0.jar",
        codeBase: "applet/",
        width: 10,
        height: 10,
        parametros: {},
        divDestino: "appletDoc"
    };
    appletCall(configuracionApplet, function(data) {
        if (data.value !== "error") {
            var rpta = data.value.split(",");
            var infoPC = {
                pcData: rpta[0],
                javaFullVer: rpta[1],
                officeVer: rpta[2],
                adobeVer: rpta[3],
                reFirmaVer: rpta[4],
                cerDigi: rpta[5],
                tslReniec: rpta[6],
                tslIndecopi: rpta[7],
                crlReniec: rpta[8]
            };
            fnInfoOfPC(infoPC);
        } else {
           bootbox.alert(data.message);
        }
    });
}
function appletCall(configuracionApplet, callback) {
    document.appletResponseONPE = "";
    var data = {
        value: "",
        message: ""
    };

    var id = configuracionApplet.id;
    var code = configuracionApplet.code;
    var archivo = configuracionApplet.archivo;
    var codeBase = configuracionApplet.codeBase;
    var width = configuracionApplet.width;
    var height = configuracionApplet.height;
    var parametros = configuracionApplet.parametros;
    var divDestino = configuracionApplet.divDestino;
    if (!!!id) {
        id = "appletGenerico";
    }
    if (!!!width) {
        width = 10;
    }
    if (!!!height) {
        height = 10;
    }
    if (!!!parametros) {
        parametros = {};
    }
    if (!!code && !!archivo && !!codeBase && !!divDestino) {
        var attributes = {
            id: id,
            code: code,
            archive: archivo,
            codeBase: codeBase,
            width: width,
            height: height
        };
        var parameters = parametros;
        var intercepted = '';
        var nativeCode = document.write;
        document.write = function(arg) {
            intercepted += arg;
        };
        try {
            if (deployJava.getJREs().length === 0) {
                javaApplet.value = javaApplet.noJavaDetected;//no java
                data.value = "error";
                data.message = "NO_JAVA_INSTALLED";
                callback(data);
            } else {
                deployJava.runApplet(attributes, parameters, '1.6');
                document.write = nativeCode;
                $("#" + divDestino).html(intercepted);
                var auxTime = 0;
                var waitToResponseApplet = function() {
                    auxTime = auxTime + 50;
                    if (!!!document.appletResponseONPE) {
                        //console.log("esperando");
                        //console.log(auxTime);
                        //Aqui se puede poner un loading.
                    } else {
                        clearInterval(time);
                        data.value = document.appletResponseONPE;
                        data.message = "ok";
                        callback(data);
                        //aqui se puede terminar el loading.
                    }
                };
                var time = setInterval(waitToResponseApplet, 50);
            }
        } catch (err) {
            var msg="Error al intentar Ejecutar el Applet";
           bootbox.alert(msg, function(){
            data.value = "error";
            data.message = msg;
            console.log(err.message);
            callback(data);
            });
        }
    } else {
        data.value = "error";
        data.message = "No se puede ejecutar el applet falta parametros";
        callback(data);
    }
}
function soportaCallFuncionesApplet() {
    var rpta = false;
    try {
        var param = moment().format("dddd, MMMM Do YYYY, h:mm:ss a");
        var response = firmarDocumento.llamadoPrueba(param);
        if (param === response) {
            soportaCallFunApplet = true;
            rpta = true;
        }
    } catch (e) {
        soportaCallFunApplet = false;
        rpta = false;
    }
    return rpta;
}
function setJavaApplet() {
    if (deployJava.getJREs().length < 1)
    {
        javaApplet.value = javaApplet.noJavaDetected;
        return;
    } else {
        var currentVersionJRE = deployJava.getJREs()[0].toString();
        var arrayTemp = currentVersionJRE.split("_");
        var version = parseFloat(arrayTemp[0]);
        var revision = parseInt(arrayTemp[1]);
        var minRevision = parseInt(configJava.minRevisionJava);
        if (version === parseFloat(configJava.minVersionJava)) {
            if (!!revision) {
                if (revision === minRevision)
                {
                    javaApplet.value = javaApplet.updatedJava;//tiene minima version de java.
                    return;
                }
                if (revision < minRevision)
                {
                    javaApplet.value = javaApplet.oldJava;//1 tiene java version antigua.
                    return;
                }
                if (revision > minRevision)
                {
                    javaApplet.value = javaApplet.futureJava;//3 tiene version no probada.
                    return;
                }
                javaApplet.value = javaApplet.oldJava;//1 tiene java version antigua.
                return;
            } else {
                javaApplet.value = javaApplet.noJavaDetected;
                return;
            }
        }
        if (version < parseFloat(configJava.minVersionJava)) {
            javaApplet.value = javaApplet.oldJava;//tiene java version antigua
            return;
        }
        if (version > parseFloat(configJava.minVersionJava)) {
            javaApplet.value = javaApplet.futureJava;//tiene version no probada.
            return;
        }
        if (!!!version) {
           bootbox.alert("No se puede determinar la version de java", function(){return;});
           
        }
    }
}
function setTipoEjecucionApplet() {
    if (javaApplet.value === javaApplet.updatedJava && soportaCallFunApplet) {
        tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoNormal;
    } else {
        if (javaApplet.value === javaApplet.oldJava && soportaCallFunApplet) {
            tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoNormal;
        }
        if (javaApplet.value === javaApplet.oldJava && !soportaCallFunApplet){
            tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoAuxilar;
        }
        if (javaApplet.value === javaApplet.updatedJava && soportaCallFunApplet) {
            tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoNormal;
        }
        if (javaApplet.value === javaApplet.updatedJava && !soportaCallFunApplet){
            tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoAuxilar;
        }
        if (javaApplet.value === javaApplet.noJavaDetected) {
            tipoEjecucionApplet.value = tipoEjecucionApplet.noEjecutarApplet;
           bootbox.alert("Java JRE no detectado en su dispositivo", function(){return;});
            
        }
        if (javaApplet.value === javaApplet.futureJava) {
            if (soportaCallFunApplet) {
                tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoNormal;
            } else {
                tipoEjecucionApplet.value = tipoEjecucionApplet.ejeAppletModoAuxilar;
            }
        }
    }
}

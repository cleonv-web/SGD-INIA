monthYearArray = new Array("","Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");

function timeRunn() {
    var mydate = new Date();
    var year = mydate.getYear();
    if (year<1000) {year += 1900;}
    var day = mydate.getDay();
    var month = mydate.getMonth();
    var daym = mydate.getDate();
    if (daym<10) {daym = "0" + daym;}
    var dayarray = new Array("Domingo","Lunes","Martes","Miercoles","Jueves","Viernes","Sábado");
    var montharray = new Array("Enero","Febrero","Marzo","Abril","Mayo","Junio","Julio","Agosto","Septiembre","Octubre","Noviembre","Diciembre");
    momentoActual = new Date();
    hora = momentoActual.getHours();
    minuto = momentoActual.getMinutes(); 
    segundo = momentoActual.getSeconds(); 
    if (segundo<10) {segundo = "0" + segundo;}
    if (minuto<10) {minuto = "0" + minuto;}
    document.getElementById("timeRun").innerHTML = dayarray[day] + " " + daym + " de " + montharray[month] + " de " + year  +"  - "+hora + ":" + minuto + ":" + segundo;
    setTimeout("timeRunn()",1000); 
} 

function fu_OnKeyPressAll(txtValida,evt) {
    eventEvaluate(evt);
	if (oEvent.keyCode==13) {
        if (txtValida!='') {
            document.getElementById(txtValida).focus();
        }
	}
}

function fu_ValidaNumero(txtValida,sizeTxt,goTxt,evt) {
    eventEvaluate(evt);
	if (oEvent.keyCode==13) {
        if (sizeTxt!="") {
            if (document.getElementById(txtValida).value.length==parseInt(sizeTxt)) {
                if (goTxt!='') {
                    document.getElementById(goTxt).focus();
                }
            }
        } else {
            document.getElementById(goTxt).focus();
        }
	} else {
		numerico(evt);
	}
}

function fu_Exit(){
    //window.location.href="srMenu?accion=goInicio";
    document.getElementById("applicationPanel").innerHTML = "";
    inicioMenuSio();
}

function fu_showCapa(divNameCapaFather,divNameCapa,divNameChild,divNameClose) {
    if (divNameCapaFather!="") {
        $("#"+divNameCapaFather+" select").hide();
        $("#"+divNameCapaFather+" input").attr('disabled','true');
    }
    if (divNameCapa!="") {$("#"+divNameCapa).show();}
    if (divNameChild!="") {$("#"+divNameChild).css({display:''});}
    if (divNameClose!="") {$("#"+divNameClose).css({display:'none'});}
}

function fu_hideCapa(divNameCapaFather,divNameCapa,divNameChild,divNameClose) {
    if (divNameCapaFather!="") {
        $("#"+divNameCapaFather+" select").show();
        $("#"+divNameCapaFather+" input").removeAttr('disabled','true');
    }
    if (divNameCapa!="") {$("#"+divNameCapa).hide();}
    if (divNameChild!="") {$("#"+divNameChild).css({display:'none'});}
    if (divNameClose!="") {$("#"+divNameClose).css({display:''});}
}

function fu_evalChildNode(objXML, nameElement) {
    var mensaje = "";
    var obj = objXML.getElementsByTagName(nameElement)[0].childNodes[0];
    if (obj==null) {
        return mensaje;
    } else {
        if (obj.nodeValue=="null") {
            return mensaje;
        } else {
            mensaje = obj.nodeValue;
        }
    }
    return mensaje;
}

function fu_muestraTr(trName) {
    document.getElementById(trName).style.display = '';
}

function fu_ocultaTr(trName) {
    dojo.byId(trName).style.display = 'none';
}

function fu_ocultaTrCapa2(trName){
    dojo.byId(trName).style.display = 'none';
}

function fu_innerHTML(divName,xmlHtml) {
    document.getElementById(divName).innerHTML = xmlHtml;
}

function fu_pintaColor(idObjeto,pColor) {
    document.getElementById(idObjeto).style.backgroundColor = pColor;
}

function fu_setDatosListasGen(pCodigo, pDescripcion, idObjetoCodigo, idObjetoDescripcion, idObjetoCodigoAdi, nameFuOculta, nameDivOcultar, nameFuncion) {
    document.getElementById(idObjetoCodigo).value = pCodigo;
    document.getElementById(idObjetoDescripcion).value = pDescripcion;
    if (idObjetoCodigoAdi!="") {
        document.getElementById(idObjetoCodigoAdi).value = pCodigo;
    }
    fu_showSelectDiv(nameFuOculta,nameDivOcultar);
    if (nameFuncion!="" && nameFuncion!=null) {
        eval(nameFuncion);
    }
}

function fu_setDatosListas(pCodigo, pDescripcion, idObjetoCodigo, idObjetoDescripcion) {
    document.getElementById(idObjetoCodigo).value = pCodigo;
    document.getElementById(idObjetoDescripcion).value = pDescripcion;
}

function fu_setDatosList(pCodigo, pDescripcion, idObjetoCodigo, idObjetoDescripcion, idObjetoCodigoAdi) {
    fu_setDatosListas(pCodigo, pDescripcion, idObjetoCodigo, idObjetoDescripcion);
    if (idObjetoCodigoAdi!="") {
        document.getElementById(idObjetoCodigoAdi).value = pCodigo;
    }
}


function numerico(evt) {
    var oEvent = (window.event) ? window.event : evt;
    var k =   oEvent.keyCode||oEvent.which;
    if (k == 8 || k ==46) {
        return true;
    }
    if (k >= 58 || k <=47) {
        return false;
    }
}

function numeroLetra(evt) {
    if (!((oEvent.keyCode >= 48 && oEvent.keyCode <=57) || (oEvent.keyCode >= 65 && oEvent.keyCode <=90) ||
        (oEvent.keyCode >= 97 && oEvent.keyCode <=122))) {
        oEvent.keyCode=0;
    }
}

function teclaNumerica(evt) {
    if ((evt.keyCode >= 48 && evt.keyCode <=57) || (evt.keyCode >= 96 && evt.keyCode <=105)) {
        return true;
    }
    return false;
}

function teclaNumeroLetra() {
    return true;
}

function validaSexo() {
    if (event.keyCode== 49 || event.keyCode ==50) {
    } else {
        event.keyCode=0;
    }
}

function valoresFecha(evt) {
    eventEvaluate(evt);
    if (oEvent.keyCode>=58 || oEvent.keyCode<=46) {
        return true;
    }
    else{return false;}
}

function valoresHora(evt) {
    eventEvaluate(evt);
    if (oEvent.keyCode>=59 || oEvent.keyCode<=47) {
    }
}

function valoresImporte(evt) {
    eventEvaluate(evt);
    if (oEvent.keyCode>=58 || oEvent.keyCode<=45 || oEvent.keyCode==47) {
    }
}

function getDescripcionCombo(nameIdObj) {
    try {
        var pdescri = "";
        pdescri = document.getElementById(nameIdObj).options[document.getElementById(nameIdObj).selectedIndex].text;
    } catch(pdescri) {
    }
    return pdescri;
}

function esBisiesto(anio) {
	if (anio%4==0) {
        if (anio%100==0) {
            if (anio%400==0) {
                return true;
            } else {
                return false;
            }
		} else {
            return true;
        }
	} else {
		return false;
    }
}

function valFecha(oTxt) {
    if (oTxt!="") {
        if (oTxt.substr(2,1)=="/" && oTxt.substr(5,1)=="/") {
            for (i=0; i<10; i++) {
                if ((oTxt.substr(i,1)<"0" || oTxt.substr(i,1)>"9") && i!=2 && i!=5) {
                    return false;
                }
            }
            var a = oTxt.substr(6,4);
            var m = oTxt.substr(3,2);
            var d = oTxt.substr(0,2);
            if ( (a<1888 || m<1 || m>12 || d<1 || d>31) ||
                ((m==4 || m==6 || m==9 || m==11) && d>30) ||
                (m==2 && (d>29 || (d==29 && esBisiesto(a)==false))) ) {
                return false;
            }
        } else {
            return false;
        }
    }
    return true;
}

function valHora(oTxt) {
    if (oTxt!="") {
        if (oTxt.substr(2,1)==":" && oTxt.substr(5,1)==":") {
            for (i=0; i<8; i++) {
                if ((oTxt.substr(i,1)<"0" || oTxt.substr(i,1)>"9") && i!=2 && i!=5) {
                    return false;
                }
            }
            var h = oTxt.substr(0,2);
            var m = oTxt.substr(3,2);
            var s = oTxt.substr(6,2);
            if (h>23 || m>59 || s>59) {
                return false;
            }
        } else {
            return false;
        }
    }
    return true;
}

function fu_validaCaracter(pCadenaTex,pCampo){
    var pCarValidoNombres = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ-' .0123456789/";
    var pCarValidoDireccion = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ.0123456789-' :/(),"+'"';
    var pCarValidoEmail = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ0123456789_@.-";
    var pCarValidoTelefono = "0123456789";
    var p1CarValidoNombres = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ"; //PARA EL PRIMER CARACTER
    var p1CarValidoDireccion = "ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕ.0123456789"; //PARA EL PRIMER CARACTER
    var pCaracter = "";
    var inicio = "OK";
    if (pCampo=="1") {pCaracter = pCarValidoNombres;}
    if (pCampo=="2") {pCaracter = pCarValidoDireccion;}
    if (pCampo=="3") {pCaracter = pCarValidoEmail;}
    if (pCampo=="4") {pCaracter = pCarValidoTelefono;}
    if (pCampo=="5") {pCaracter = p1CarValidoNombres;}
    if (pCampo=="6") {pCaracter = p1CarValidoDireccion;}
    for (i=0; i<=pCadenaTex.length-1; i++) {
        car = pCadenaTex.substring(i,i+1);
        var pos = pCaracter.indexOf((car));
        if (pos==-1) {
            return car;
            break;
        }
    }
    return inicio;
}

function fu_validaCaracterGrupo(pCadenaText) {
    var inicio = "OK";
    var pCarGrupo = "  |AAA|BBB|CCC|DDD|EEE|FFF|GGG|HHH|III|JJ|KK|LLL|MMM|NNN|OOO|PPP|QQ|RRR|SSS|TTT|UUU|VV|WW|XX|YY|ZZZ|ÄÄ|ËË|ÑÑ|ÖÖ|ÜÜ|";
    var result = new Array();
    var p = new Array();
    p = pCarGrupo.split("|");
    for (i=0; i<p.length -1; i++) {
        result = pCadenaText.match((p[i]));
        if (result!=null) {
            return result;
            break;
        }
    }
    return inicio;
}

function validaCaracteres(pCadenaTex,pCampo) {
    var rpta = fu_validaCaracter(pCadenaTex,pCampo);
    if (rpta!="OK") {
        rpta = 'R-contiene caracteres no permitidos: '+rpta;
        return rpta;
    }
    if (pCampo=="1") {
        if (pCadenaTex.substr(0,1)==" ") {
            rpta = 'R-No puede tener como primer caracter: Un espacio en blanco';
            return rpta;
        } else {
            rpta = fu_validaCaracter(pCadenaTex.substr(0,1),"5");
            if (rpta!="OK") {
                rpta = 'R-No puede tener como primer caracter: '+rpta;
                return rpta;
            }
        }
    }
    if (pCampo=="2") {
        if (pCadenaTex.substr(0,1)==" ") {
            rpta = 'R-No puede tener como primer caracter: Un espacio en blanco';
            return rpta;
        } else {
            rpta = fu_validaCaracter(pCadenaTex.substr(0,1),"6");
            if (rpta!="OK") {
                rpta = 'R-No puede tener como primer caracter: '+rpta;
                return rpta;
            }
        }
    }
    if (pCadenaTex.substr(pCadenaTex.length-1,1)==" ") {
        rpta = 'R-No puede tener como último caracter: Un espacio en blanco';
        return rpta;
    }
    /*
    rpta = fu_validaCaracterGrupo(pCadenaTex);
    if (rpta=="  ") {
        rpta = 'R-No puede tener más de un espacio en blanco entre los caracteres';
    } else if (rpta!="OK") {
        rpta = 'A-contiene grupo de caracteres no permitidos: '+rpta;
    }
    */
    return rpta;
}

function fu_esNumerico(pCadenaTex) {
    var pCaracter = "0123456789";
    var car = "";
    var pos = "";
    for (i=0; i<=pCadenaTex.length-1; i++) {
        car = pCadenaTex.substring(i,i+1);
        pos = pCaracter.indexOf((car));
        if (pos==-1) {
            return "NO";
        }
    }
    return "SI";
}

function fu_getValueObj(pIdObjeto) {
    if      (document.getElementById(pIdObjeto).value==null)   { return false;}
    else if (document.getElementById(pIdObjeto).value=="null") { return false;}
    else if (document.getElementById(pIdObjeto).value=="")     { return false;}
    else if (document.getElementById(pIdObjeto).value=="-1")   { return false;}
    return true;
}

function fu_getValueVar(cadena) {
    if      (cadena==null)   {return false;}
    else if (cadena=="null") {return false;}
    else if (cadena=="")     {return false;}
    else if (cadena=="-1")   {return false;}
    return true;
}

function fu_getValorUpperCase(cadena) {
    if      (cadena==null)   {return "";}
    else if (cadena=="null") {return "";}
    else if (cadena=="")     {return "";}
    else if (cadena=="-1")   {return "";}
    return cadena.toUpperCase();
}
function fu_getText(pIdObjeto) {
    var cadena = document.getElementById(pIdObjeto).value;
    return cadena;
}

function fu_setText(pIdObjeto,valor) {
    document.getElementById(pIdObjeto).value = valor;
}

function esEmail(string) {
    if (string.search(/^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z0-9]+$/) != -1)
        {return true;}
    else
        {return false;}
}

function fu_getMascaraCodigo(mascara,pCodigo) {
    var codigoMascara = pCodigo;
    if (isNaN(pCodigo)==false) {
        var lnumero = pCodigo.length;
        var codigo  = mascara + "" + pCodigo;
        codigoMascara = codigo.substring(lnumero);
    }
    return codigoMascara;
}

function fu_setDisabled(itemName, pValor) {
    document.getElementById(itemName).disabled = pValor;
}

function fu_setReadOnly(itemName, pValor) {
    document.getElementById(itemName).readOnly = pValor;
}

function comparaFechas(fechaCompara, fechaBase) {
    var feCompara = fechaCompara.substr(6,4)+fechaCompara.substr(3,2)+fechaCompara.substr(0,2);
    var feBase = fechaBase.substr(6,4)+fechaBase.substr(3,2)+fechaBase.substr(0,2);
    if      (feCompara==feBase) {return "0";}
    else if (feCompara>feBase)  {return "1";}
    else                        {return "-1";}
}

function comparaFechasMenor(fechaCompara, fechaBase) {
    var feCompara = fechaCompara.substr(6,4)+fechaCompara.substr(3,2)+fechaCompara.substr(0,2);
    var feBase = fechaBase.substr(6,4)+fechaBase.substr(3,2)+fechaBase.substr(0,2);
    if      (feCompara==feBase) {return "0";}
    else if (feCompara<feBase)  {return "1";}
    else                        {return "-1";}
}



function goError() {
    window.location.href = "/CapturaTramiteDNI/srLogin?accion=goInicio";
}

function maximize() {
    if (window.screen) {
        var aw = screen.availWidth;
        var ah = screen.availHeight;
        window.moveTo(0, 0);
        window.resizeTo(aw, ah);
    }
}

function fu_validarFechaAll(fechaIniFin,mensaje) {
    var varr = valFecha(document.getElementById(fechaIniFin).value);
    if (varr==false) {
       bootbox.alert(mensaje);
        document.getElementById(fechaIniFin).focus();
        return true;
    }
    return false;
}

function f_click_derecho() {
    document.onmousedown = click;
}

function click() {
    if (event.button==2) {
    }
}

function f_press_boton() {
    document.onkeydown = function(e) {
        if (e) {
            document.onkeypress = function() {
                return true;
            }
        }
        var evt = e?e:event;
        if (evt.keyCode==116) {
            if (e) {
                document.onkeypress = function() {
                    return false;
                }
            } else {
                evt.keyCode = 0;
                evt.returnValue = false;
            }
        }
    }
}

function getHoraRun() {
    var momentoActual = new Date();
    hora = momentoActual.getHours();
    minuto = momentoActual.getMinutes();
    segundo = momentoActual.getSeconds();
    if (segundo<10) {segundo="0"+segundo;}
    if (minuto<10) {minuto="0"+minuto;}
    return hora + ":" + minuto + ":" + segundo
}

function fu_onKeyPressFiltrarTecladoNumero(evt,autoSubmit,pvalidKeys,sucessGoToId,errorGoToId, callbackFunction){
    eventEvaluate(evt);
	var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key==37 || key==46 || key==39)) {
        return filtrarTeclado(oEvent, autoSubmit, pvalidKeys, sucessGoToId,errorGoToId, callbackFunction);
    }
    return false;
}
function fu_IsValidaCorreo(correo) { 
    var re = /([A-Z0-9a-z_-][^@])+?@[^$#<>?]+?\.[\w]{2,4}/.test(correo);
    if(!re) {
        bootbox.alert('Ingrese un correo electrónico válido.');
        return false;
    } else {
        return true;
    }
    
}

function fu_onKeyPressFiltrarTecladoCadena(evt, autoSubmit, pvalidKeys, sucessGoToId, errorGoToId, callbackFunction) {
    eventEvaluate(evt);
    var key = window.event ? oEvent.keyCode : oEvent.which;
    if (!(key==37)) {
        return filtrarTeclado(oEvent, autoSubmit, pvalidKeys, sucessGoToId, errorGoToId, callbackFunction);
    }
    return false;
}

function fu_bs(evt) {
    eventEvaluate(evt);
    if (oEvent.keyCode==8) {
        oEvent.keyCode = 0;
        return oEvent.keyCode;
    }
}

//-------------------------------------------------------------------------------------------//
//-- Funcion    : fu_validaComparaFechaConsulta
//-- Funcion que compara fechas (segunda fecha es del sistema o fecha actual)
//-------------------------------------------------------------------------------------------//
function fu_validaComparaFechaConsulta(datoFecha,datoFechaActual){
var vRetorna="1";
    vComparaFechas = comparaFechas(datoFecha, datoFechaActual);
    if (vComparaFechas=="1") { 
       vRetorna="Fecha ingresada es mayor que la fecha del Sistema.";
    }
    return vRetorna;
}

//
//-------------------------------------------------------------------------------------------//
//-- Funcion    : fu_validaFechaConsulta
//-- Funcion que verifica formato de fechas ingresadas y compara fechas
//-------------------------------------------------------------------------------------------//
function fu_validaFechaConsulta(datoFecha,datoFechaActual){
var vRetorna="1";
var lonFecha = datoFecha.length;
    if (lonFecha!="10") {
        vRetorna="Fecha no válida";
    }else{
        if(lonFecha=="10"){
            if(valFecha(datoFecha)){
                vRetorna="1";
            }else{
		vRetorna="Fecha no válida";
            }
        }
    }
    if (vRetorna=="1") {
       vRetorna = fu_validaComparaFechaConsulta(datoFecha,datoFechaActual);
    }
    return vRetorna;
}

function getNumeroDeDiasDiferencia(feInicio,feFin){
    var dat1 = new Date(parseFloat(feInicio.substring(6,10)), parseFloat(feInicio.substring(3,5)), parseFloat(feInicio.substring(0,2)));
    var dat2 = new Date(parseFloat(feFin.substring(6,10)), parseFloat(feFin.substring(3,5)), parseFloat(feFin.substring(0,2)));

    var fin = dat2.getTime() - dat1.getTime();
    var dias = Math.floor(fin / (1000 * 60 * 60 * 24))

    return dias;
}

function getDiasMin(feInicio){
   // console.log(feInicio);
    var dia=parseFloat(feInicio.substring(0,2))+1;
    var dat1 = feInicio.substring(6,10)+'-'+feInicio.substring(3,5)+'-'+ dia;
   // var dat2 = new Date(parseFloat(feFin.substring(6,10)), parseFloat(feFin.substring(3,5)), parseFloat(feFin.substring(0,2)));
//console.log(dat1);
    //var fin = dat2.getTime() - dat1.getTime();
   // var dias = Math.floor(fin / (1000 * 60 * 60 * 24))

    return dat1;
}

function getSumarDias(fecha, dias){
 
  fecha=new Date(fecha.substring(6,10)+"-"+fecha.substring(3,5)+"-"+ fecha.substring(0,2));
//  console.log(fecha);
  fecha.setDate(fecha.getDate() + parseInt(dias)+1);
//  console.log(fecha);
  fecha = moment(fecha).format('DD/MM/YYYY');
  console.log(fecha);
  return fecha;
}
//-------------------------------------------------------------------------------------------//
//-- Funcion    : fu_validaNumero
//-- Parametros : pCadenaTex (Texto a validar)
//-- Si retorna -1 significa que no hay caracteres invalidos 
//-------------------------------------------------------------------------------------------//
function fu_validaNumero(pCadenaTex){
  var pCarValidoNumero="0123456789";
  for (i=0; i<=pCadenaTex.length-1; i++){
    car=pCadenaTex.substring(i,i+1);
    var pos=pCarValidoNumero.indexOf((car));
    if(pos==-1){
      return "KO";
      break;
    }
  }
  return "OK";
}
/**
 * 
 * @param {type} pFechaActual en formato "DD/MM/YYYY"
 * @returns {undefined} ejemplo pFechaActual 10/01/2014 return 31/01/2014
 */
function obtenerFechaUltimoDiaMes(pFechaActual){
    try {
        var vReturn="";
        var ultDiaMes = moment("pFechaActual", "DD/MM/YYYY").daysInMonth();
        vReturn = ultDiaMes + pFechaActual.substr(2);
    } catch(vReturn) {
    }
    return vReturn;   
}

function obtenerFechaPrimerDiaMes(pFechaActual){
    try {
        var vReturn="";
        vReturn = "01" + pFechaActual.substr(2);
    } catch(vReturn) {
    }
    return vReturn;   
}

function obtenerNroMesFecha(pFechaActual){
    try {
        var vReturn="";
        vReturn = pFechaActual.substr(3,2);
    } catch(vReturn) {
    }
    return vReturn;       
}

function fn_getCleanTextExpReg(text){
    if(!!text&&text.length>0){
        text=allTrim(text).replace(/[^a-zA-Z 0-9ÑñäëïöüÄËÏÖÜÁÉÍÓÚáéíóú]+/g, '').replace(/\s+/gi,' ');
    }
    return text;
}

function fn_getCleanTextLenGreathree(text){
    if(!!text&&text.length>0){
        var arrCadenaAux=new Array();
        var arrCadena=text.split(' ');
        arrCadena.forEach(function(value){
            if(allTrim(value).length>1){
                arrCadenaAux.push(value);
            }          
        });
        text=arrCadenaAux.join(' ');
    }
    return text;
}
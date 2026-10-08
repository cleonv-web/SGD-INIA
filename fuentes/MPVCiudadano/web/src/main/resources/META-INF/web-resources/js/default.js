bootbox.addLocale("sgd", {
    OK : 'Aceptar',
    CANCEL : 'Cancelar',
    CONFIRM : 'Aceptar'
});

bootbox.setLocale("sgd");

var gridModel = [{valor: 'CODIGO', width: 60, align: 'center'},
    {valor: 'DESCRIPCION', width: 560, align: 'left'}
];

var gridModel3 = [{valor: 'CODIGO', width: 60, align: 'center'},
    {valor: 'DESCRIPCION', width: 560, align: 'left'},
    {valor: 'CODIGO2', width: 10, align: 'center'}
];

var icoEtiqueta={
    0:"glyphicon glyphicon-tag",
    1:"glyphicon glyphicon-time",
    2:"glyphicon glyphicon-flash",
    3:"glyphicon glyphicon-transfer"
};
var coloresVencimiento = {
    0: {"text": "Normal", "color": "#009BE6", "descrip": "Documentos sin días límites de atención.", "cssClass": "estadoVenNormal"},
    1: {"text": "Proximo a vencer", "color": "#E97200", "descrip": "Tiempo de vencimiento igual a 1 o 2 días.", "cssClass": "estadoVenProximoVencer"},
    2: {"text": "Vencido", "color": "#222222", "descrip": "Fecha límite anterior a la fecha actual.", "cssClass": "estadoVenVencido"},
    3: {"text": "Vence hoy", "color": "#D00000", "descrip": "Fecha límite igual a la fecha actual.", "cssClass": "estadoVenVenceHoy"},
    4: {"text": "Por vencer", "color": "#D9D900", "descrip": "Tiempo de vencimiento mayor a 2 días.", "cssClass": "estadoVenPorVencer"},
    5: {"text": "Atendido", "color": "#009F01", "descrip": "Documentos con fecha de atención o archivado.", "cssClass": "estadoVenAtendido"}
};
function setButtonEvents(parentFormId) {
    var container;
    if (parentFormId) {
        container = jQuery('#' + parentFormId);
    }
    else {
        container = document;
    }
    //dojo.query("input, button", container).forEach(setEventButton);
    jQuery("input, button", container).each(setEventButton);
    //container.find('input,button').each(setEventButton);
}

function setEvents(parentFormId) {
    var container;
    if (parentFormId) {
        for (var j = 0; j < document.forms.length; j++) {
            if (parentFormId == document.forms[j].id) {
                container = document.forms[j];
                break;
            }
        }
    }
    else {
        container = document;
    }
    if (typeof(container) != 'object') {
        container = jQuery(parentFormId);
    }
    jQuery("input, button", container).each(setEventButton);
}

function setEventButton(obj, index, arr) {
    if (obj.type == 'submit' || obj.type == 'reset' || obj.type == 'button') {
        if (!obj.onmouseover) {
            jQuery(obj).onmouseover(function(e) {
                jQuery(obj).addClass('ui-state-hover');
            });
        }
        if (!obj.onmouseout) {
            jQuery(obj).onmouseout(function(e) {
                jQuery(obj).removeClass('ui-state-hover');
            })
        }
        if (!obj.onfocus) {
            jQuery(obj).onfocus(function(e) {
                jQuery(obj).addClass('ui-state-hover');
            })
        }
        if (!obj.onblur) {
            jQuery(obj).onblur(function(e) {
                jQuery(obj).removeClass('ui-state-hover');
            })
        }
    }
}

function setFirstElement(oForm) {
    /*for (var i=0; i < oForm.elements.length; i++) {
     var oField = oForm.elements[i];
     if ((oField.type=='text'||oField.type=='password'||oField.type=='select-one'||oField.type=='textarea') && typeof(oField.tabIndex)!='undefined' && oField.tabIndex==1){
     try{
     oField.focus();
     }catch(e){}
     break;
     }
     }*/

    for (var i = 0; i < oForm.elements.length; i++) {
        var oField = oForm.elements[i];
        if ((oField.type == 'text' || oField.type == 'password' || oField.type == 'select-one' || oField.type == 'textarea')) {
            if (typeof(oField.tabIndex) != 'undefined' && oField.tabIndex == 1) {
                try {
                    oField.focus();
                } catch (e) {
                }
            }
            jQuery(oField).bind('paste', function(e) {
                return false;
            });
        }
        //break;
    }
}

function pushFocusEvent(setFocusOnLoad, parentFormId, callbackFunction) {
    setEvents(parentFormId);
    if (typeof(setFocusOnLoad) == 'undefined' || setFocusOnLoad == true || setFocusOnLoad == 'firstElement') {
        if (parentFormId) {
            for (var j = 0; j < document.forms.length; j++) {
                if (parentFormId == document.forms[j].id) {
                    setFirstElement(document.forms[j]);
                    break;
                }
            }
        }
        else {
            if (document.forms.length > 0) {
                setFirstElement(document.forms[0]);
            }
        }
    }
    else {
        if (setFocusOnLoad != false) {
            if (parentFormId) {
                try {
                    eval("document." + parentFormId + "." + setFocusOnLoad + ".focus()");
                } catch (e) {
                }
            }
        }
    }
    if (callbackFunction) {
        callbackFunction();
    }
}

function filtrarTeclado(pEvent, autoSubmit, pvalidKeys, sucessGoToId, errorGoToId, callbackFunction) {
    var tk = new KeyboardClass(pEvent, pvalidKeys);
    if (tk.isIntro()) {
        if (callbackFunction) {
            (callbackFunction(tk.objEvent, tk.currentObj)) ? mueveFoco(tk.currentObj, sucessGoToId) : mueveFoco(tk.currentObj, errorGoToId);
        }
        else {
            mueveFoco(tk.currentObj, sucessGoToId);
        }
    }
    return (((autoSubmit) ? autoSubmit : false) == true) ? tk.isValidKey : (tk.isValidKey && tk.k != 13 && tk.k != 37);
}

function filtrarAlphaNumeric(event) {
    var regex = new RegExp('^[a-zA-Z0-9]+$');
    var charCode = (typeof event.which == 'number') ? event.which:event.keyCode;
    var key = String.fromCharCode(charCode);
    if (!(charCode == 8 || charCode == 0)) {
        if (!regex.test(key)) {
            event.preventDefault();
            return false;
        }
    }
}

function filtrarAlphaNumericEnhes(event) {
    var regex = new RegExp('^[a-zA-Z0-9ñÑ]+$');
    var charCode = (typeof event.which == 'number') ? event.which:event.keyCode;
    var key = String.fromCharCode(charCode);
    if (!(charCode == 8 || charCode == 0)) {
        if (!regex.test(key)) {
            event.preventDefault();
            return false;
        }
    }
}



function mueveFoco(obj, goTo) {
    var to;
    if (obj.type != 'textarea') {
        if (goTo) {
            to = document.getElementById(goTo);
        }
        else {
            var ltabindex = obj.tabIndex + 1;
            to = jQuery("*[tabindex=" + ltabindex + "]")[0];
        }
        if (to != null && typeof(obj) != 'undefined') {
            jQuery(obj).removeClass('activado_confoco').addClass('normal');
            jQuery(to).removeClass('normal').addClass('activado_confoco');
            try {
                to.focus();
                to.select();
            } catch (e) {
            }
        }
    }
}

function salir(url) {
    var params = {accion: 'salir'};
    ajaxCall("/logout.do", params, function(data) {
        jQuery("#body").hide()/*style("display","none")*/;
        $.cookie("State", null, {expires: -1});
        $.cookie("Acceso", null, {expires: -1});
        $.cookie("Usuario", null, {expires: -1});
        $.cookie("JSESSIONID", null, {expires: -1});
        $.cookie("SaveStateCookie", null, {expires: -1});
        var state = $.cookie("State");
        if (state != 'undefined' && state != null) {
        } else{
            try{cerrarConexion_wsocket();}catch(ex){}
            if (data == "inicio") {
                window.location.replace("/inicio/login.do");
            } else {
                window.location.replace(url);
            }
        }
    }, 'text', false, true, 'POST');

}

function fastLogout(msg) {
    dojo.cookie("JSESSIONID", null, {expires: -1});
    if (msg) {
       bootbox.alert("se ha terminado la session, vuelva a ingresar",function(){
           window.location = pRutaContexto + "/login.do";
       });
    }else{
        window.location = pRutaContexto + "/login.do";
    }
    
}

function loadding(onOf) {
    if (onOf) {
        var div="<div id='loadding' class='box'><div class='image'><img align='absmiddle' src='"+resourceURL+"/images/loading.gif'></div><div class='line1'>PROCESANDO</div><div class='line2'>Ejecutando petición, por favor espere...</div></div>";
        jQuery.blockUI({
            message: div,
            css: {
                border: 'none',
                padding: '0px',
                backgroundColor: ''
            },
            overlayCSS: {
                backgroundColor: 'black',
                opacity: 0.10
            }
        });        
    }
    else {
        //jQuery("#loadding").hide();
        jQuery.unblockUI();
    }
}
function refreshScript(refreshDiv, data) {
    jQuery('#' + refreshDiv).html(data);
}

function refreshAppBody(data) {
    loadding(false);
    alert_Danger(data);
    refreshScript("applicationPanel", data);
    jQuery('#applicationPanel').show();
}

function releaseDojoObj(id) {
    var obj = dijit.byId(id);
    if (obj != null) {
        obj.destroyRecursive();
    }
}
function buildDojoWridget(id) {
    var lst = dojo.query("input[data-dojo-type]", id);
    for (i = 0; i < lst.length; i++) {
        releaseDojoObj(lst[i].id);
    }
    dojo.parser.parse(dojo.byId(id));
}


function buscaOpMenu(valOp) {
    var dataSource = public_menuItem;
    var vret = "0";

    for (i = 0; i < dataSource.rows.length; i++)
    {
        var row = dataSource.rows[i];
        if (row.cell[0] == valOp) {
            vret = row.cell[1];
            return vret;
        }
    }
    return vret;
}

function ejecutaOpcion(idOpc, url, tipo) {
    if (!!tipo === false) {
        tipo = "GET";
    }
    if (jQuery(idOpc).hasClass('menu_lista')) {
        ajaxCall(url, null, refreshAppBody, 'text', false, false, tipo);
        hideMenuSio();
    } else {
        return false;
    }

}

function ejecutaOpcionModal(idOpc, url, tipo) {
    if (!!tipo === false) {
        tipo = "GET";
    }
    ajaxCallModal(url, null, function(data){ 
        if (data !== null) {
            $("body").append(data);
        }
    }, 'text', false, false, tipo);
}

function ejecutaOpcionJson(idOpc, url, tipo, mngr){
    if (!!tipo === false) {
        tipo = "GET";
    }
    if (jQuery(idOpc).hasClass('menu_lista')) {
        ajaxCall(url, null, function(data){
            if (mngr) {
                mngr(data);
            }              
        }, 'json', false, false, tipo);
    } else {
        return false;
    }  
}

function ejecutaOpcionJsonConfirm(idOpc, url, tipo, mngr){//ecueva
    if (!!tipo === false) {
        tipo = "GET";
    }
    if (jQuery(idOpc).hasClass('menu_lista')) {
            bootbox.dialog({
                message: "<p>Al confirmar esta opción se actualizará los cambios realizados en las siguientes listas del sistema, permitiendo visualizar los cambios a todos los usuarios :</p>"+
                          "<ul><li>Tupa</li>"+
                          "<li>Locales y Dependencias</li>"+
                          "<li>Tipos de Destinos</li>"+
                          "<li>Tipos de Encargatura</li>"+
                          "<li>Tipos de Emisores Externos</li>"+
                          "<li>Plantillas de Documentos</li>"+
                          "<h5>¿Desea confirmar?</h5>",
                buttons: {
                    SI: {
                        label: "Aceptar",
                        className: "btn-primary",
                        callback: function() {
                            ajaxCall(url, null, function(data){
                                if (mngr) {
                                    mngr(data);
                                }              
                            }, 'json', false, false, tipo);
                        }                        
                    },
                    NO: {
                        label: "Cancelar",
                        className: "btn-default"
                    }
                }
            });          
    } else {
        return false;
    }  
}

function mostrarMenu() {
    var w = document.getElementById("leftPane").style.width;
    var width = w.substring(0, w.indexOf("px"));
    var lpane = dijit.byId('leftPane');
    if (width < 26) {
        lpane.toggle();
    }
}

function ocultarMenu() {
    var lpane = dijit.byId('leftPane');
    lpane.toggle();
}

function ajaxCallModal(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = pRutaContexto + "/" + pAppVersion + url;
    if (!silent) {
        loadding(true);
    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function(request, status, error) {
            refreshAppBody(request.responseText);
            hideMenuSio();
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function ajaxCallSinLoading(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = pRutaContexto + "/" + pAppVersion + url;
    
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function(request, status, error) {
            //alert(request.responseText);
            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function ajaxCall(url, params, mngr, type, sync, silent, tipo) {
//    var urlFullPath = pRutaContexto + "/" + pAppVersion + url;
    var urlFullPath = pRutaContexto + url; 
    if (!silent) {
        loadding(true);
    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function(request, status, error) {
            //alert(request.responseText);
            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    
    jQuery.ajax(objAjax);

}

function ajaxCallActionForm(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = url;
    if (!silent) {
        loadding(true);
    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        error: function(request, status, error) {
            //alert(request.responseText);
            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function ajaxCallActionFormSendJson(url, params, mngr, type, sync, silent, tipo) {
    var urlFullPath = url;
    if (!silent) {
        loadding(true);
    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        contentType: 'application/json',
        error: function(request, status, error) {
            //alert(request.responseText);
            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function ajaxCallSendJson(url, params, mngr, type, sync, silent, tipo) {
    //var urlFullPath = pRutaContexto + "/" + pAppVersion + url;
    var urlFullPath = pRutaContexto  + url;
    if (!silent) {
        loadding(true);
    }
    var objAjax = {
        url: urlFullPath,
        type: (tipo == "GET" || tipo == "get") ? 'get' : 'post',
        dataType: (type) ? type : 'text',
        async: (sync) ? false : true,
        contentType: 'application/json',
        error: function(request, status, error) {
            //alert(request.responseText);
            refreshAppBody(request.responseText);
        },
        success: function(result, textStatus, xhr) {
            if (result == null) {
                return;
            }
            if (mngr) {
                mngr(result);
            }
        }};
    if (typeof(params) == 'object') {
        objAjax.data = params;
    } else if (typeof(params) == "string") {
        objAjax.data = params;
    }
    jQuery.ajax(objAjax);

}

function xhrResponseManager(respuesta, fnCallBack, tipo) {
    loadding(false);
    if (typeof(tipo) != 'undefined' && tipo == 'json') {
        var oJson = jsonFormatValide(respuesta);
        if (oJson == null) {

            return false;
        }
        else {
            if (fnCallBack) {
                fnCallBack(oJson);
            }
        }
    }
    else {
        if (fnCallBack) {
            fnCallBack(respuesta);
        }
    }
}

function jsonFormatValide(result) {
    try {
        eval("var v=" + result);
        if (typeof(v) != "undefined" && typeof(v.coRespuesta) != 'undefined' && v.coRespuesta != "") {
            return v;
        }
    } catch (e) {
    }
    return null;
}

function bindTableEvents(tableId, colNum) {
    dojo.query(tableId).onclick(function(e) {
        obj = (e.target || e.srcElement);
        if (obj.tagName == 'INPUT') {
            var tr = obj.parentNode.parentNode;
            toogleRowSelected(tr, obj, false);
        }
        else {
            var tr = obj.parentNode;
            if (tr.tagName != "TR") {
                tr = obj.parentNode.parentNode;
            }
            td = tr.cells[colNum];
            input = dojo.query("input", td)[0];
            if (input != 'undefined') {
                toogleRowSelected(tr, input, true);
            }
        }
    })
}

function toogleRowSelected(tableRow, cBoxObj, state) {
    if (state) {
        checked = cBoxObj.checked;
    } else {
        checked = !cBoxObj.checked;
    }
    if (checked) {
        cBoxObj.checked = false;
        deseleccionaFila(tableRow);
    }
    else {
        cBoxObj.checked = true;
        seleccionaFila(tableRow);
    }
}

function seleccionaFila(tr) {
    dojo.query(tr).addClass('ui-state-highlight');
}
function deseleccionaFila(tr) {
    dojo.query(tr).removeClass('ui-state-highlight');
}

function navegador() {
    if (navigator.userAgent.indexOf('Chrome') == -1) {

        null;
    }
}

function showDown(evt) {
    evt = (evt) ? evt : ((event) ? event : null);
    if (evt) {
        if (evt.ctrlKey && (evt.keyCode == 86 || evt.keyCode == 118)) {
            cancelKey(evt);
        }
        else if (evt.keyCode == 8) {

            var node = (evt.target) ? evt.target : ((evt.srcElement) ? evt.srcElement : null);
            if (node != null && node.type != "text" && node.type != "password" && node.type != "textarea") {
                cancelKey(evt);
            }
        }
        else if (evt.keyCode == 116) {

            cancelKey(evt);
        }
        else if (evt.ctrlKey && (evt.keyCode == 78 || evt.keyCode == 82)) {

            cancelKey(evt);
        }
        else if (evt.ctrlKey && (evt.keyCode == 65 || evt.keyCode == 85)) {

            cancelKey(evt);
        }
        else if (evt.altKey && (evt.keyCode == 37 || evt.keyCode == 39)) {

            return false;
        }
    }
}

function cancelKey(evt) {
    if (evt.preventDefault) {
        evt.preventDefault();
        return false;
    }
    else {
        evt.keyCode = 0;
        evt.returnValue = false;
    }
}

document.onkeydown = showDown;

function loadObjectsMenu(nver)
{
    loadSuccess = false;
    //console.log("inicia LoadMenu");
    ajaxCall("/recursos", {accion: 'opcionesMenu', version: nver},
    function(data) {
        //console.log("fin loadMenu");
        if (data.retmenu)
        {
            //console.log(data.retmenu);
            public_menuItem = (data.menuItem) ? eval(data.menuItem) : 0;
            loadSuccess = true;
        }
        else {

        }
    }, 'json', true, true, 'GET');
}

function loadObjects(nver)
{
    loadSuccess = false;
    ajaxCall("/recursos", {accion: 'loadObjs', version: nver},
    function(data) {
        if (data.retval)
        {
            //console.log(data.retval);
            public_subTipoTramite = (data.subtipotramite) ? eval(data.subtipotramite) : 0;
            public_domRestriccion = (data.domRestriccion) ? eval(data.domRestriccion) : 0;
            public_domDiscapacidad = (data.domDiscapacidad) ? eval(data.domDiscapacidad) : 0;
            public_domInterdiccion = (data.domInterdiccion) ? eval(data.domInterdiccion) : 0;
            public_nivelEduca = (data.niveleduca) ? eval(data.niveleduca) : 0;
            public_estadoCivil = (data.estadocivil) ? eval(data.estadocivil) : 0;
            public_genero = {'rows': [{'cell': ['1', 'MASCULINO']}, {'cell': ['2', 'FEMENINO']}]};
            public_observacion = (data.observacion) ? eval(data.observacion) : 0;
            public_tipoPagoConsulado = {'rows': [{'cell': ['1', 'TIMBRE CONSULAR']}, {'cell': ['32', 'RJ 183-2006/003-2010 - TRAMITE GRATUITO DISCAPACIDAD']}]};
            public_diagnostico = (data.diagnostico) ? eval(data.diagnostico) : 0;
            loadSuccess = true;
        }
        else {

        }
    }, 'json', false, true, 'GET');
}


search = function(titulo, db, modelo, posx, posy, fncallbak, objSource, idDetalle, idToSkip, idDetalle2, valToSkip)
{
    p = {
        id: "Window_Advanced_Search",
        titulo: titulo,
        dataSource: db,
        colHead: modelo,
        beginSearch: 0
    };
    var g = {
        doFilter: function(e) {
            var k = e.charCode || e.keyCode || e.which;
            if (e.ctrlKey || e.altKey) {
                return true;
            } else if ((k >= 41 && k <= 122) || k == 32 || k > 186 || k == 8 || k == 13) {
                var obj = "";
                if (k == 13 || k == 9)
                {
                    obj = $(e.target || e.srcElement).val();
                    $('tr', g.bTable).unbind();
                    $(g.bTable).empty();
                    g.refeshTable(obj, true);
                }
            }
        },
        refeshTable: function(valx, filtrar) {
            var entra = false;
            if (valx.length > 0) {
                valx = valx.toUpperCase();
                var ttidx = 0;
                for (i = 0; i < p.dataSource.rows.length; i++)
                {
                    var row = p.dataSource.rows[i];
                    entra = false;
                    posicion = 0;
                    var tr = document.createElement('tr');
                    for (j = 0; j < p.colHead.length; j++) {
                        var cm = p.colHead[j];
                        var td = document.createElement('td');
                        td.innerHTML = row.cell[j];
                        $(td).attr('width', cm.width);
                        if (cm.hide)
                            $(td).css('display', 'none');
                        $(tr).append(td);
                        td = null;
                        if (filtrar) {
                            var posicion = row.cell[j].toUpperCase().indexOf(valx);
                            if (posicion > -1) {
                                entra = true;
                            }
                        } else {
                            entra = true;
                        }
                    }
                    if (entra) {
                        ttidx++;
                        if ((ttidx % 2) == 0) {
                            var rowStyle = "ui-datatable-even";
                        } else {
                            var rowStyle = "ui-datatable-odd";
                        }
                        tr.className = rowStyle;
                        $(g.bTable).append(tr);
                    }
                    tr = null;
                }
            }
            ;

            var dtarget = $(g.bTable);
            setTableEvents(dtarget, function(fila) {
                g.doClose();
                //var fields = $("td", fila).length;
                objSource.value = $("td", fila)[0].innerHTML;
                if (idDetalle) {
                    document.getElementById(idDetalle).value = $("td", fila)[1].innerHTML;
                }
                if (idDetalle2) {
                    document.getElementById(idDetalle2).value = $("td", fila)[2].innerHTML;
                }
                if (idToSkip) {
                    document.getElementById(idToSkip).focus();
                    $(objSource).removeClass('activado_confoco');
                }
                if (valToSkip) {
                    document.getElementById(idToSkip).value = valToSkip;
                }
                if (fncallbak) {
                    fncallbak(fila, objSource, idToSkip);
                }
            }, 0);

        },
        doClose: function() {
            $(g.priDiv).remove();
        }
    };

    thead = document.createElement('thead');
    tbody = document.createElement('tbody');
    tr = document.createElement('tr');

    var tmpwidth = 0;
    for (i = 0; i < p.colHead.length; i++) {
        var cm = p.colHead[i];
        var th = document.createElement('th');
        th.innerHTML = cm.valor;
        th.align = (cm.align) ? cm.align : 'left';
        $(th).attr('width', cm.width);
        if (cm.hide) {
            $(th).css('display', 'none');
        }
        if ((cm.width) && !(cm.hide)) {
            tmpwidth += cm.width;
        }
        $(tr).append(th);
    }
    $(thead).append(tr);

    var vmaxZindex = maxZindex();

    g.priDiv = document.createElement("div");
    g.gDiv = document.createElement("div");
    g.bloqueaDiv = document.createElement("div");
    g.tDiv = document.createElement("div");
    g.sDiv = document.createElement("div");
    g.mDiv = document.createElement("div");
    g.hDiv = document.createElement("div");
    g.bDiv = document.createElement("div");
    g.hTable = document.createElement("table");
    g.bTable = document.createElement("table");

    g.priDiv.id = p.id;

    g.bloqueaDiv.className = "bloquea";
    $(g.bloqueaDiv).css({zIndex: vmaxZindex + 2000});

    g.gDiv.id = "divGlobal";
    g.gDiv.className = "cuadrow ui-panel ui-corner-all";
    $(g.gDiv).css({zIndex: vmaxZindex + 2001, width: tmpwidth + 20 + 'px', top: posx, left: posy});
    $(g.gDiv).append("<DIV><div id='cerrar' class='icon_close' style='width: 16px; height: 16px; cursor: pointer; float: right; margin-top: 3px; margin-right: 5px;'/></DIV>");

    g.tDiv.id = "div_titulo";
    g.tDiv.className = "ui-panel-titlebar ui-widget-header ui-corner-all";
    $(g.tDiv).append("<span>" + p.titulo + "</span>");

    g.sDiv.className = "bx_sb";
    $(g.sDiv).css({margin: '5px', padding: '4px'});
    $(g.sDiv).append("<strong> Buscar :</strong>");
    $(g.sDiv).append("<input type='text' name='txtcadena' size=50 />");

    g.mDiv.id = "grid_filtro";
    g.mDiv.className = "ui-datatable ui-datatable-scrollable";
    $(g.mDiv).css({width: tmpwidth, height: '300px', padding: '6px'});

    g.hDiv.id = 'thdiv';
    g.hDiv.className = 'ui-datatable-scrollable-header';
    $(g.hDiv).css({
        paddingLeft: '1px'
    });

    g.bDiv.id = 'tbdiv';
    g.bDiv.className = 'bx_sb ui-datatable-scrollable-body ';
    $(g.bDiv).css({
        height: '274px', overflow: 'auto'
    });

    g.hTable.className = "ui-state-default";
    g.bTable.className = "ui-datatable-data";
    $(g.bTable).append(tbody);
    $(g.hTable).append(thead);

    $(g.bDiv).append(g.bTable);
    $(g.hDiv).append(g.hTable);

    $(g.mDiv).append(g.hDiv);
    $(g.mDiv).append(g.bDiv);

    $(g.gDiv).append(g.tDiv);
    $(g.gDiv).append(g.sDiv);
    $(g.gDiv).append(g.mDiv);

    $(g.priDiv).append(g.gDiv);
    $(g.priDiv).append(g.bloqueaDiv);

    $("body").append(g.priDiv);

    $('input[name=txtcadena]', g.sDiv).keydown(function(e) {
        g.doFilter(e)
    });
    $('#cerrar', g.priDiv).click(function() {
        g.doClose()
    })
    $('input[name=txtcadena]', g.sDiv).addClass('activado_confoco');
    $('input[name=txtcadena]', g.sDiv).focus();

    document.onkeypress = function(elevento) {
        var levento = (window.event) ? window.event : elevento;
        var lcodigo = levento.keyCode || levento.charCode;
        if ((levento.keyCode == 27))
        {
            g.doClose();
            if (fncallbak) {
                fncallbak(0, objSource, idToSkip);
            }
        }
    };
    g.refeshTable('0', false);
}

function setTableEvents(target, click, dlbclick)
{
    $("tbody tr", target).each(function(e) {
        setTableRowEvents(this, click, dlbclick);
    });
}

function setTableRowEvents(fila, clickCallback, dlbclickCallback)
{
    $(fila)
            .click(
            function() {
                $(fila).parent().children("tr").removeClass('seleccionado');
                $(fila).addClass('seleccionado');
                clickCallback ? clickCallback(fila) : 0;
            })
            .dblclick(
            function() {
                dlbclickCallback ? dlbclickCallback(fila) : 0;
            })
            .hover(
            function() {
                $(fila).addClass('over');
            },
            function() {
                $(fila).removeClass('over');
            }
    );
}

showDetailByCod = function(pEvent, dataSource, wind, strIdDetalle, strIdSaltar, flagShowGridonF1, psearchbyDetail, fundCallBack, listValidKeys)
{
    var tecla = new KeyboardClass(pEvent, listValidKeys);
    tecla.currentObj.firstval = '';

    if (tecla.isIntro()) {
        if (dataSource) {
            var valx = tecla.currentObj.value.toUpperCase();
            var flag = false;
            for (i = 0; i < dataSource.rows.length; i++) {
                var row = dataSource.rows[i];
                var col = (row.cell.length == 3) ? row.cell[1] : row.cell[0];
                if (col == valx) {
                    if (row.cell.length == 3) {
                        tecla.currentObj.firstval = row.cell[0];
                    }
                    tecla.currentObj.value = col;
                    if (strIdDetalle) {
                        document.getElementById(strIdDetalle).value = (row.cell.length == 3) ? row.cell[2] : row.cell[1];
                    }
                    if (fundCallBack) {
                        fundCallBack(row, tecla.currentObj, strIdSaltar, wind);
                    } else {
                        saltar(tecla.currentObj, strIdSaltar, wind)
                    }
                    ;
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if (psearchbyDetail) {
                    saltar(tecla.currentObj, strIdDetalle, wind);
                }
                else {
                    (fundCallBack) ? fundCallBack(0, tecla.currentObj, strIdSaltar) : 0;
                }
            }
        }
    }
    else {
        if (tecla.isHelp()) {
            if (flagShowGridonF1) {
                search('BUSQUEDAS', dataSource, gridModel, 150, 250, fundCallBack, tecla.currentObj, strIdDetalle, strIdSaltar);
            }
        }
    }
    return tecla.isValidKey;
}

function saltar(objetoactual, ir_a, en) {
    switch (ir_a.substring(0, 3)) {
        case 'txt':
            var to = $('input[name=' + ir_a + ']', en);
            break;
        case 'txa':
            var to = $('textarea[name=' + ir_a + ']', en);
            break;
    }
    if (to != null) {
        to.focus();
    }
}

function regresarLimpiar() {
    dojo.byId("applicationPanel").innerHTML = "";
    inicioMenuStd();
}

function autoCloseModalW(obj) {
    var ventana;
    if (typeof(obj) == 'object') {
        ventana = obj.parentNode.parentNode.parentNode.parentNode;
    }
    else {
        ventana = jQuery('#' + obj);
    }
    //var cmdsalir = dojo.query("input[type=button][value=Salir]", ventana)[0];
    var cmdsalir = jQuery("button[type=button][value=Salir]", ventana)[0];
    if (typeof(cmdsalir) != 'undefined') {
        cmdsalir.click();
    }
    else {
        removeDomId(ventana);
    }
}

function removeDomId(obj) {
    if (typeof(obj) == "object") {
        obj.parentNode.removeChild(obj);
    }
    else {
        //var ventana = dojo.byId(obj);
        var ventana = jQuery('#' + obj);
        //ventana.removeChild(ventana);
        ventana.remove();
    }
}

function removeDomIdBus(obj) {
    if (typeof(obj) == "object") {
        obj.parentNode.removeChild(obj);
    }
    else {
        //var ventana = dojo.byId(obj);
        var ventana = jQuery('#' + obj);
        //ventana.removeChild(ventana);
        ventana.remove();
    }
}

function waitForFunction(){
    if(typeof tamanoVentanaNavegador !== "undefined" && $.isFunction(tamanoVentanaNavegador)){
        return tamanoVentanaNavegador();
    }
    else{
        setTimeout(waitForFunction, 100);
    }
}

function setZindex(id) {
    if(typeof tamanoVentanaNavegador !== "undefined" && $.isFunction(tamanoVentanaNavegador)){
        var nZindex = maxZindex();
        nZindex = (nZindex > 999) ? nZindex : 999;
        var div = document.getElementById(id);
        var divBlock = document.createElement("div");
        divBlock.className = 'bloquea';
        divBlock.style.zIndex = nZindex;
        jQuery(div).append(divBlock);


        //dims = waitForFunction();        
        dims = tamanoVentanaNavegador();        
        var w = div.childNodes[0].style.width;
        var h = div.childNodes[0].style.height;
        var t = div.childNodes[0].style.top;
        var l = div.childNodes[0].style.left;

        if (!l) {
            l = 0;
        } else {
            l = l.substring(0, l.indexOf("px"));
        }
        if (!t) {
            t = 0;

        } else {
            t = t.substring(0, t.indexOf("px"));
        }

        if (l > 0)
        {
            x = l;
        } else {
            var width = w.substring(0, w.indexOf("px"));
            x = Math.round((dims.ancho / 2 - width / 2) * Math.pow(10, 0)) / Math.pow(10, 0);
            if(x < 20){
                x = 20;
            }
        }
        if (t > 0)
        {
            y = t;
        } else {
            var height = h.substring(0, h.indexOf("px"));
            y = Math.round((dims.alto / 2 - height / 2) * Math.pow(10, 0)) / Math.pow(10, 0);
            if(y < 20){
                y = 20;
            }
        }
        jQuery(div.childNodes[0]).css({zIndex: ++nZindex, top: y + 'px', left: x + 'px', height: 'auto'});
    }
    else{
        setTimeout(function(){setZindex(id); }, 100);
    }


}


function maxZindex() {
    var tCol = $("div .cuadrow");

    var z = 0;
    for (var i = 0; i < tCol.length; i++) {
        if (tCol[i].style.zIndex != 'undefined') {
            if (tCol[i].style.zIndex > z) {
                z = tCol[i].style.zIndex;
            }
        }
    }
    return ++z;
}

function openWindow(url, params) {
    var URL = pRutaContexto + "/" + pAppVersion + url + "?" + params;
    var atributos = "toolbar=no,Location=no,directories=no,channelmode=no,menubar=no,status=yes,scrollbars=yes,resizable=yes,width=900,height=600,fullscreen=no,top=100,left=100";
    window.open(URL, "Ventana", atributos);
}

function showChangeDialog() {
    jQuery("#dlglogin").hide();
    jQuery("#dlgchange").show();
    jQuery("#txtUsuario").focus();
    jQuery("#txtUsuario").val(jQuery("#coUsuario").val());
}

function cancelChangePwd() {
    /*
    jQuery("#dlglogin").show();
    var dlg = jQuery("#dlgchange");
    dlg.hide();
    jQuery(':input', dlg).each(function() {
        var type = this.type;
        if (type == 'text' || type == 'password') {
            this.value = "";
        }
    });
    jQuery("#coUsuario").focus();
    jQuery("#msgChange").hide();
    jQuery("#errorChange").html("");
    */
   removeDomId('windowChangePwd');
   
}

function goChangePwd() {
    var resultado = $("#form-cambiar-contrasena").valid();
    console.log(resultado);
    if(resultado !== true) return;
    console.log(resultado);
    /*if(!$("#form-cambiar-contrasena").validate()){
        return;
    }*/
    $("#msgChange").hide();
    jQuery('#txtUsuario').val(fu_getTextUpperCase("txtUsuario"));
    jQuery('#txtClaveOriginal').val(fu_getTextCadenaLogin("txtClaveOriginal"));
    jQuery('#txtClaveNew1').val(fu_getTextCadenaLogin("txtClaveNew1"));
    jQuery('#txtClaveNew2').val(fu_getTextCadenaLogin("txtClaveNew2"));    

    var nuDni = window.btoa(unescape(encodeURIComponent($("#txtUsuario").val())));
    var clave = window.btoa(unescape(encodeURIComponent($("#txtClaveOriginal").val())));
    var pwd1 = window.btoa(unescape(encodeURIComponent($("#txtClaveNew1").val())));
    var pwd2 = window.btoa(unescape(encodeURIComponent($("#txtClaveNew2").val())));

    if (nuDni == "" || clave == "" || pwd1 == "" || pwd2 == "") {                      
        bootbox.alert("<h5>La información de los campos (*) es obligatoria.", function() {
            bootbox.hideAll();
            jQuery('#txtUsuario').focus();
        });        
        //alert("todos los campos (*) son requeridos");
        return;
    };
    
    if (pwd1 == pwd2) {
        var params = {accion: 'changepwd', nuDni: nuDni, dePassword: clave, dePasswordNuevo: pwd1, dePasswordRepeat: pwd2};
        ajaxCall("/logout.do", params, function(data) {
            if (data.coRespuesta == "1") {
                bootbox.alert({
                    message: "<h5>"+data.deRespuesta, 
                    buttons: {
                        ok: {
                            label: 'Aceptar'                            
                        }
                    },
                    callback: function() {
                        bootbox.hideAll();
                        $("#dePass").val("");
                        $("#filaCaptcha").hide();
                        cancelChangePwd();
                    }
                });                  
//               bootbox.alert(data.deRespuesta);
//                cancelChangePwd();
            }
            else {
                jQuery("#msgChange").show();
                jQuery("#errorChange").html(data.deRespuesta);
            }
        }, 'json', false, true, 'POST');
    }
    else {
       bootbox.alert({
            message: "Las nuevas contraseñas no coinciden.",
            buttons: {
                ok: {
                    label: 'Aceptar'                            
                }
            }
       });
    }
}
function login() {
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    jQuery('#dePass').val(fu_getTextCadenaLogin("dePass"));

    var formdata = $(document.forms[0]).serializeArray();
    $.each(formdata, function (index, campo) {
        if("coUsuario" === campo.name){
            formdata[index].value = window.btoa(unescape(encodeURIComponent( jQuery('#coUsuario').val() )));
        }
        if("dePass" === campo.name){
            formdata[index].value = window.btoa(unescape(encodeURIComponent( jQuery('#dePass').val() )));
        }
    });
    formUtil.post($(document.forms[0]).attr("action"),formdata);
}

function loginSession() {
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    jQuery('#dePass').val(fu_getTextCadenaLogin("dePass"));    
    //document.forms[0].submit();
    return false;
}

function submitLogin() {
    jQuery('#coUsuario').val("ETERRY");
    jQuery('#dePass').val("E");
    jQuery('#coDep').val("1200");
//    jQuery('#coUsuario').val("WCUTIPA");
//    jQuery('#dePass').val("WCUTIPA");
//    jQuery('#coDep').val("1000");
    document.forms[0].submit();
}

function expira_inicio(/*url*/) {
    document.formExpira.submit();
    //window.location.replace(url);
        
}

function btnEnable(obj) {
    obj.removeAttribute("disabled");
}
function btnDisable(obj) {
    obj.disabled = "true";
}
function hideMenuTimeOut() {
    setTimeout("ocultarMenu()", 1000);
}

function hideMenuSio() {
    //$('#menuSio').tabs('select', -1);
    //$('#menu').collapseAll;
    //$( ".selector" ).menu( "collapseAll", null, true );
    jQuery('#divBandejaEntrada').hide();
    $(".selector").blur();
    $(".selector").focusout();
    $("#menu").blur();
}

function inicioMenuStd() {
    //$('#menuSio').tabs('select', 0);
    jQuery('#divBandejaEntrada').show();
}

function openWindow_post(purl, pparam) {

    var URL = pRutaContexto + "/" + pAppVersion + purl
    var pparameters = split_param(pparam);

//    pparameters = (typeof pparameters == 'undefined') ? {}: pparameters;

    var form = document.createElement("form");
    jQuery(form).attr("id", "reg-form").attr("name", "reg-form").attr("action", URL).attr("method", "post");
    jQuery(form).attr("target", "VentanaReporte");

    jQuery.each(pparameters,
            function(key) {
                jQuery(form).append('<input type="text" name="' + key + '" value="' + this + '" />');
            });

    document.body.appendChild(form);

    var atributos = "toolbar=no,Location=no,directories=no,channelmode=no,menubar=no,status=yes,scrollbars=yes,resizable=yes,width=900,height=600,fullscreen=no,top=100,left=100";
    if (typeof ventanaReporte == "undefined")
        ventanaReporte = null;

//     if(ventanaReporte!=null)  ventanaReporte.close();

    ventanaReporte = window.open("about:blank", "VentanaReporte", atributos);
    if (!ventanaReporte)
        return false;
    form.submit();
    document.body.removeChild(form);
    ventanaReporte.focus();
    return false;
}

function split_param(param) {
    var queryString = {};
    param.replace(/([^?=&]+)(=([^&]*))?/g, function($0, $1, $2, $3) {
        queryString[$1] = $3;
    });
    return queryString;
}

function cerrarPantalla() {
    refrescarBandejaEntrada();
    //dojo.byId("applicationPanel").innerHTML=" ";
    jQuery("#applicationPanel").html(" ");
    inicioMenuStd();
}

function cerrarPantallaMP() {
    //dojo.byId("applicationPanel").innerHTML=" ";
    jQuery("#applicationPanel").html(" ");
    inicioMenuStd();
}

function cerrarPantallaConfigTabla() {
    //dojo.byId("applicationPanel").innerHTML=" ";
    jQuery("#applicationPanel").html(" ");
    inicioMenuStd();
}

function refrescarBandejaEntrada() {
    var p = new Array();
    p[0] = "accion=goInicio";
    var nameDiv = "divTablaBandejaEntrada";
    var div = "#" + nameDiv;
    jQuery(div).fadeOut(250, function() {
        jQuery(this).show().css({visibility: "hidden"});
    });
    ajaxCall("/srBandejaEntrada.do", p.join("&"), function(data) {
        jQuery(div).fadeIn(500, function() {
            jQuery(this).show().css({visibility: "visible"});
            refreshScript(nameDiv, data);
        });
    }, 'text', false, false, "POST");
}

function appletCargado(datosPC) {
    //alert(datosPC);

    ajaxCall("/config.do", "accion=goDatosPC&" + datosPC,
            function(data) {
                null;
            }, 'text', true, true, 'POST');

}

function appletCargadoRutaDoc(datosPC) {
    jQuery("#RutaDocs").html("<strong>"+datosPC+"</strong>");

}


/**
 * Mostrar notificaciones
 * @param {type} titulo
 * @param {type} msg
 * @returns {undefined}
 */
/*function alert_Sucess(titulo, msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            //.appendTo("#applicationPanel")
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-success").css({right: '1em',top: '10%'});
    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });    
}

function alert_Danger(titulo, msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            //.appendTo("#applicationPanel")
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-danger").css({right: '1em',top: '10%'});

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });        
}

function alert_Warning(titulo, msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-warning").css({right: '1em',top: '10%'});
            

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });     
}

function alert_Info(titulo, msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-info").css({right: '1em',top: '10%'});

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });
}*/
// FERNANDO TICONA: ALERTS CON TITULO UNICO, ESTANDARIZACIÓN
function alert_Sucess(msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>¡Éxito!</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-success").css({right: '1em',top: '10%'});
    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });    
}

function alert_Danger( msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>¡Error!</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-danger").css({right: '1em',top: '10%'});

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });        
}

function alert_Warning(msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>¡Cuidado!</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-warning").css({right: '1em',top: '10%'});
            

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });     
}

function alert_Info(msg) {
    var TagId = '#bodyMain';
    var msgHtml = "<strong>¡Atención!</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-info").css({right: '1em',top: '10%'});

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });
}
function removeNotification(){
    jQuery(document).mouseup(function (e)
    {
        var searchcontainer = jQuery("#notificacion_proyect");

        if (!searchcontainer.is(e.target) // if the target of the click isn't the container...
            && searchcontainer.has(e.target).length === 0) // ... nor a descendant of the container
        {
            searchcontainer.fadeOut("slow",function(){
                searchcontainer.remove();
            });
        }
    });
}
//FIN FERNANDO TICONA: ALERTS
function fn_abrirRutaDocs(){
    runOnDesktop(accionOnDesktopTramiteDoc.abrirRutaDocs,null); 
}

function fn_selDependenciaAcceso(codDepen) {
    if (!!codDepen) {
        jQuery("#coDepSeleccionaAcceso").val(codDepen);
    } else {
        alert_Danger("Ocurrio un error cons la dependencia");
    }
}

function fn_updDependenciaUsuario() {
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    var pcoUsuario = jQuery('#coUsuario').val();
    jQuery('#coDep').val("");
    jQuery('#deDependencia').val("");
    
    if (!!pcoUsuario) {
        var p = new Array();
        p[0] = "accion=goSeleccionaDep";	
        p[1] = "pcoUsuario="+pcoUsuario;	
        ajaxCall("/srDepAcceso.do",p.join("&"),function(data){
            var result;
            eval("var docs="+data);
            if(typeof(docs)!="undefined" && typeof(docs.retval)!='undefined' && docs.retval!=""){
                if(docs.retval =="OK"){
                    jQuery('#cempCodemp').val(docs.coEmp);
                    jQuery('#coDep').val(docs.coDep);
                    jQuery('#deDependencia').val(docs.deDep);
                    if(docs.nuInt != null && docs.nuIntMax != null && docs.nuInt >= docs.nuIntMax && docs.nuInt <= docs.nuIntMaxCapt){
                        fn_mostrarFilaCaptcha('1');
                    }
                    else{
                        fn_mostrarFilaCaptcha('0');                        
                    }
                    jQuery("#msgs-content").empty();
                }
                else{                    
                    if(docs.retval=="false"){
                        console.log(docs.mensajeError);
                        $("#msgs-content").empty();
                        $("#msgs-content").html(
                            '<div id="msgsDep" class="ui-messages ui-widget">'+'<div class="ui-messages-warn ui-corner-all">'+'<ul>'+                       
                                '<li>'+'<span class="ui-messages-warn-detail">'+docs.mensajeError+'</span>'+'</li>'+
                            '</ul>'+'</div>'+'</div>'
                        );
                        fn_mostrarFilaCaptcha('0');
                        jQuery("#dePass").focus();
                        jQuery("#coUsuario").focus();
                    }
                }
            }
        }, 'text', true, true, "POST");       
    }

}

function fn_mostrarFilaCaptcha(mostrar){
    if(mostrar=='1'){
        jQuery('#filaCaptcha').show();
        jQuery('#textoCaptcha').prop('required', true);
    }
    else{
        jQuery('#textoCaptcha').prop('required', false);
        jQuery('#filaCaptcha').hide();        
    }   
}

function fu_setDependenciaAcceso(pcoDep,pdeDep) {
    jQuery('#coDep').val(pcoDep);
    jQuery('#deDependencia').val(pdeDep);
    removeDomId('windowDependenciaAcceso');
}

function fn_cambiaPwd(){    
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));    
    var pcoUsuario = jQuery('#coUsuario').val();

    var p = new Array();    
    p[0] = "accion=goCambioPwd";	    
    if (!!pcoUsuario) {
        p[1] = "pcoUsuario=" + pcoUsuario;    
    }
    ajaxCall("/srDepAcceso.do",p.join("&"),function(data){
            if(data !== null){
                jQuery("body").append(data);
                jQuery('#txtUsuario').val(pcoUsuario);
            }    
        },
    'text', false, false, "POST");       
    
}

function fn_inicializaVentanaModal(divId){
    jQuery("#"+divId).draggable({handle: "#dragmodal", containment: "window", disabled: true});
    jQuery("#"+divId).keyup(function(event){
        if(event.which===27)
        {
            removeDomId(divId);
        }
    });    
}
function fu_eventoGridTabla(pIdSelector, pParamConf) {
    var idSelector = "#" + pIdSelector;
    var oTable;
    oTable = $(idSelector).dataTable(pParamConf);
}


function fn_cambiaDependenciaLogin(){

    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    
    var pcoUsuario = jQuery('#coUsuario').val();
    var pcoEmp = jQuery('#cempCodemp').val();

    if (!!pcoUsuario && !!pcoEmp) {
            var p = new Array();    
            p[0] = "accion=goListaDependencias";	    
            p[1] = "pcoUsuario=" + pcoUsuario;    
            p[2] = "pcoEmp=" + pcoEmp;    
            ajaxCall("/srDepAcceso.do",p.join("&"),function(data){
                    if(data !== null){
                        jQuery("body").append(data);
                        jQuery("#buscaDestiDetalle").focus();
                        
                        var inputs = $(document).find("legend, button").filter(function(){
                            return $(this).attr('tabindexmodal') == "modal-dep";
                        });
                        console.log(inputs);
                        var firstInput = inputs.first();
                        var lastInput = inputs.last();

                        /*set focus on first input*/
                        firstInput.focus();

                        /*redirect last tab to first input*/
                        lastInput.on('keydown', function (e) {
                           if (e.which === 9 && !e.shiftKey) {
                               e.preventDefault();
                               firstInput.focus();
                           }
                        });

                    /*redirect first shift+tab to last input*/
                    firstInput.on('keydown', function (e) {
                        if ((e.which === 9 && e.shiftKey)) {
                            e.preventDefault();
                            lastInput.focus();
                        }
                    });
                    }    
                },
            'text', false, false, "POST");       
    }
    
}

function fn_cambiaDependencia(){
            ajaxCall("/cambiaDep.do","",function(data){
                    if(data !== null){
                        jQuery("body").append(data);
                    }    
                },
            'text', false, false, "POST");       
    }

function fu_activaDependencia(pcoDep, pdeDep) {
    loadding(true);
    try { cerrarConexion_wsocket(); }catch(ex) { }    
    setTimeout( function() {
        var noForm = '#depForm';
        jQuery(noForm).find('#txtCoDep').val(pcoDep);
        jQuery(noForm).submit();
        // event.stopPropagation();
        loadding(false);
    }, 1000);
}

function fu_activaDependencia_dir(pcoDep, pdeDep) {
    try { cerrarConexion_wsocket(); }catch(ex) { }
    var noForm = '#depFormDir';
    jQuery(noForm).find('#txtCoDep').val(pcoDep);
    jQuery(noForm).submit();    
}

function fn_mostrar_manual(){
    
    var windowTarget = "_blank" ;
    var path = '../Manual_SGD.pdf';
    window.open(path,"location=no,hidden=no");  
    return false;
}


function cleanString (st)
{
        var ltr = ['[àáâãä]','[èéêë]','[ìíîï]','[òóôõö]','[ùúûü]','ñ','ç','[ýÿ]','\\s|\\W|_'];
        var rpl = ['a','e','i','o','u','n','c','y',''];
        var str = String(st.toLowerCase());
        
        for (var i = 0, c = ltr.length; i < c; i++)
        {
        	var rgx = new RegExp(ltr[i],'g');
        	str = str.replace(rgx,rpl[i]);
        };
        
        return str;
}

function fn_abrirPdf(pUrl,pDoc){
        var windowTarget = "_system" ;
        var path = pUrl;
        window.open(path,"location=yes,hidden=no");
        return false;
/*    
    window.open(pUrl,'_blank','left=0px,top=0px,width=800,height=670');
    return false;
    */
    
}

function fn_abrirPdf2(pUrl,pDoc){
    var vpos = -1 
    var noDoc = pDoc;
    try{
        vpos = pDoc.lastIndexOf('|'); 
        noDoc = pDoc.substring(vpos+1);
    }catch(ev){
        noDoc = "documento.pdf";
    }
    //Usaremos un link para iniciar la descarga 
    var save = document.createElement('a');
    //save.href = "javascript:window.open('"+pUrl+" ','_blank');" ;
    save.href = pUrl;
    save.target = '_blank';
    //save.download = noDoc;
    //save.onclick = function(){window.open(pUrl,'_blank','left=0px,top=0px,width=800,height=670');return false;};
    
    var clickEvent = document.createEvent ("MouseEvent");    
    clickEvent.initMouseEvent ("click", true, true, window, 1, 
                                0, 0, 0, 0, 
                                false, false, false, false, 
                                0, null);

    //Simulamos un clic del usuario
    //no es necesario agregar el link al DOM.
    save.dispatchEvent(clickEvent);
    //Y liberamos recursos...
    (window.URL || window.webkitURL).revokeObjectURL(save.href);
    
}
function getIcoEtiqueta(id) {
    try {
        var id = parseInt(id);
        if (id < Object.keys(icoEtiqueta).length && id >= 0) {
            return icoEtiqueta[id];
        } else {
            return "";
        }
    } catch (e) {
        return "";
    }
}
function mostrarEtiquetas() {
    $("input[name='icoEtiqueta']").each(function() {
        var id = $(this).val();
        $(this).parent().attr("class", getIcoEtiqueta(id));
    });
}
function setEtiquetasListaDoc(idTable,col) {
    
    $("#"+idTable+" tbody tr").each(function() {
        var aux = $(this).find("td:eq("+col+")").text();
        var a = aux.split("_");
        var id = a[0];
        var label = a[1];
        var clase = getIcoEtiqueta(id);
        var ico = "<span class='" + clase + "'></span> ";
        if (parseInt(id) === 0) {
            $(this).find("td:eq("+col+")").html(label);
        } else {
            $(this).find("td:eq("+col+")").html(ico + label);
        }
    });
}

function verificarClaveFuerte(inClaveFuerte){
    if(inClaveFuerte==='EXP'||inClaveFuerte==='NF'||inClaveFuerte>0){
        //jQuery('#myModal').modal();
        var p = new Array();    
        p[0] = "accion=msgCambioPwd";	    
        p[1] = "tipoMsg="+inClaveFuerte;	    
        ajaxCall("/srDepAcceso.do",p.join("&"),function(data){
                if(data !== null){
                    jQuery("body").append(data);
                }    
            },
        'text', false, false, "POST");               
    }
}


function fn_PrecambiaPwd(){
    removeDomId('windowAlertChangePwd');
    fn_cambiaPwd();
}
function fn_password() {
    $('[type=password]').keypress(function (e) {
        var $password = $(this),
                tooltipVisible = $('.tooltip').is(':visible'),
                s = String.fromCharCode(e.which);

        //Check if capslock is on. No easy way to test for this
        //Tests if letter is upper case and the shift key is NOT pressed.
        if (s.toUpperCase() === s && s.toLowerCase() !== s && !e.shiftKey) {
            if (!tooltipVisible)
                $password.tooltip('show');
        } else {
            if (tooltipVisible)
                $password.tooltip('hide');
        }

        //Hide the tooltip when moving away from the password field
        $password.blur(function (e) {
            $password.tooltip('hide');
        });
    });
}

/**
* Para Web Sockets Login Ver Requisitos 07/07/2017
* author: FTICONA Y DBARRIOS
*/

var _ws_req = null;
var _onpetradoc_install = 0;
var _ws_url_req = null;
var _ws_url_server_req = null;
var _call_link_req = 0;

function fn_mostrar_req_so(hpLink) {
    if(!_call_link_req){
        _call_link_req = 1;
        _onpetradoc_install = 0;

        _ws_url_req = _ws_url_server_req;

	//var auxUrl="OnpeTradoc:accion=VerifConf?browser=";
        var auxUrl="Tramitedoc:accion=VerifConf?browser=";
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
                //$("#browser").append("IE version " + auxBrowVersion + "<span style='color:#FFAF6A' >, actualizar IE versión 10 o superior</span>").append($("#icoWarnig").clone()).append("<br/>(Recomendamos usar navegador Chrome)");
                auxUrl=auxUrl+"IE version " + auxBrowVersion + " (Recomendamos usar navegador Chrome)";
            } else {
                //$("#browser").append("IE version " + auxBrowVersion).append($("#icoOk").clone()).append("(Recomendamos usar navegador Chrome)");
                auxUrl=auxUrl+"IE version " + auxBrowVersion + " (Recomendamos usar navegador Chrome)";
            }
        } else {
            var auxNavName;
            if ($.browser.mozilla) {
                auxNavName = "Firefox";
                //$("#browser").append("Browser " + auxNavName + " ver. " + auxBrowVersion).append($("#icoOk").clone());
                auxUrl=auxUrl+"Browser " + auxNavName + " ver. " + auxBrowVersion;
            } else {
                if ($.browser.chrome) {
                    auxNavName = "Chrome";
                    //$("#browser").append("Browser " + auxNavName + " ver. " + auxBrowVersion).append($("#icoOk").clone());
                    auxUrl=auxUrl+"Browser " + auxNavName + " ver. " + auxBrowVersion;
                } else {
                    //$("#browser").append("<span style='color:#FF4500' > Browser Desconocido ver. Instale Chrome</span>").append($("#icoError").clone());
                    auxUrl=auxUrl+"Browser Desconocido ver. Instale Chrome";
                }
            }
        }
        _ws_url_req+= new Date().valueOf()+'/';
        hpLink.setAttribute('href',auxUrl+'?ws='+_ws_url_req);    

        var conex_ws = 0;
        var waitToConexionWS = function() {
            hpLink.href = "javascript:void(0);";
            if(!conex_ws){
                conex_ws=1;
                var p = new Array();
                p[0] = "accion=goVerRequisitos";
                ajaxCall("/srDepAcceso.do", p.join("&"), function(data) {
                    if (data !== null) {
                        jQuery("body").append(data);
                    }
                }, 'text', false, false, "POST");
                _call_link_req = 0;
            }

            clearInterval(timeConex);
        };
        var timeConex = setInterval(waitToConexionWS, 1000*.25); //1/4 de segundo   

        return true;
    }else{
        return false;
    }
}

function fn_close_ws_req(){
    if(_ws_req!==null)
    {
        try{
        _ws_req.close();
        }catch(e){}
    }       
}

function fn_req_so_new(data){
    if(data!==null){
        if(data.error==="0"){
            if (_ws_req.readyState===_ws_req.OPEN){
                var msg = '{"message":' + null + ', "sender":"", "destination":"' + "APPCLIENT" + '" ,"accion":"'+"TERMINATE_APP"+'" ,"nrOperacion":"'+null+'"}';    
                _ws_req.send(msg);              
            }
        }/*else{
            bootbox.alert("<h5>Por favor instale Tr&#225;mite Documentario.</h5>", function() {
                bootbox.hideAll();
            });             
        }*/
    }
}


function fn_onMessageReceivedWS(evt){
    var msg = JSON.parse(evt.data); // native API
    fn_processMessageRecived(msg);    
}

function fn_processMessageRecived(msg){
   switch (msg.accion) 
        {
            case "APP_INSTALL":
                _onpetradoc_install=1;
                fn_req_so_new(msg);
                break;
            default:
                fn_req_so_new(null);
                break;
        }
}


function ini_verReqSO(){
    var elem = document.getElementById("myProgressBarReq"); 

    var width = 0;
    var time_re_call = 1000;//1 segundo
    var conex_ws = 0;

    if(!conex_ws){
        conex_ws=1;
        _ws_req = new WebSocket(_ws_url_req +'BROWSER');
        _ws_req.onmessage = function (evt){
            fn_onMessageReceivedWS(evt);
        };
    }                    

    //var send_ws = 0;
    //var ini = new Date();
    var waitToResponseWS = function() {
        if (_ws_req.readyState===_ws_req.OPEN){
            if(/*!send_ws&&*/!_onpetradoc_install){
                //send_ws=1;
                var msg = '{"message":' + null + ', "sender":"", "destination":"' + "APPCLIENT" + '" ,"accion":"'+"APP_INSTALL"+'" ,"nrOperacion":"'+null+'"}';
                _ws_req.send(msg);
            }
        }

        width=Math.round(width+7.3);
        elem.style.width = width + '%'; 
        elem.innerHTML = '';//width * 1 + '%';
        elem.setAttribute('aria-valuenow', width * 1);                        

        if (width >= 100) {
            //console.log('INI: '+ini+" FIN: "+ new Date());
            clearInterval(time);
            fn_close_ws_req();
            if(!_onpetradoc_install){
                jQuery('#myDivProgress').hide();
                jQuery('#myDivInstall').show();
            }                             
        }

        if(_onpetradoc_install){
            //console.log('INI: '+ini+" FIN: "+ new Date());
            clearInterval(time);                
            fn_close_ws_req();
            removeDomId('windowVerificaRequisitosSO');
        }                        
    };
    var time = setInterval(waitToResponseWS, time_re_call);
}

/**
 * Fin de Web Sockets
 */

function recargarCaptcha(){
//    console.log("invocado")
    $("#imagenCaptcha").removeAttr( "src" );
    $("#imagenCaptcha").attr("src", "jcaptcha?"+new Date().getTime());
    $("#textoCaptcha").val("");
}

function dimensionarImagenInicio(){
    var anchoPantalla = $(window).width();
    var altoPantalla = $(window).height();   
    var tdBandejaAncho = $('#td-bandeja-entrada').width();
    
    if(anchoPantalla<1280){
        anchoPantalla = 1280;
    }
    if(altoPantalla<600){
        altoPantalla = 600;
    }
    
    var tdImagen = anchoPantalla - tdBandejaAncho;
    var anchoImagen = 0;
    var altoImagen = 0;
    anchoImagen = tdImagen - 20;
    if(tdImagen < 800){         
        $("#td-img-imagen-inicio").width(anchoImagen);
        $("#td-img-imagen-inicio").height(anchoImagen/2);        
    } 
    else{
        altoPantalla = altoPantalla - 210;
        if(anchoImagen > 0.70*anchoPantalla) anchoImagen = 0.69*anchoPantalla;
        if(altoPantalla*2 < anchoImagen){
            $("#td-img-imagen-inicio").width(altoPantalla*2);
            $("#td-img-imagen-inicio").height(altoPantalla);  
        }
        else{
            anchoImagen = 0.75*anchoImagen;
            $("#td-bandeja-entrada").width(400);
            $("#td-img-imagen-inicio").width(anchoImagen);
            $("#td-img-imagen-inicio").height(anchoImagen/2);
        }
    }         
    
}

var resize_function = function () {};

jQuery(document).ready(function(){
    $(window).resize(function() {
        if (resize_function && typeof(resize_function) === "function") {
            resize_function();
        }
    });
});

function corregir_dimensiones(panel_selector, div_tbl, datatable_selector){
    var oTable = $(datatable_selector).dataTable();
    if ( $.fn.DataTable.isDataTable(datatable_selector ) ) {
        $(panel_selector+" "+div_tbl).width( Math.max($(panel_selector).width() - 40, $(window).width() - 50) );
        oTable.fnAdjustColumnSizing();
    }
    resize_function = function () {
        if($(panel_selector).length > 0){
            clearTimeout(window.refresh_size);
            window.refresh_size = setTimeout(function() {
                if ( $.fn.DataTable.isDataTable(datatable_selector ) ) {
                    $(panel_selector+" "+div_tbl).width( Math.max($(panel_selector).width() - 35, $(window).width() - 40) );
                    oTable.fnAdjustColumnSizing();
                }
            }, 100 );
        }
    };
}

function esnulo(campo) {
    return (campo == null || campo == '' || campo == 'null');
}

function validarnulo(campo) {
    if (esnulo(campo)) {
        return null;
    }
    return campo;
}

function verificaParametro(campo) {
    if (esnulo(campo)) {
        return '_';
    }
    return campo;
}

function verificaParametroInt(campo) {
    if (esnulo(campo)) {
        return 0;
    }
    return campo;
}

function obtieneParametro(campo) {
    if (esnulo(campo)) {
        return '';
    }
    return campo;
}

function verificarListaJson(lista) {
    if (typeof lista === 'undefined') {
        return new Object();
    }
    return lista;
}

var formUtil = {};
formUtil.post = function (url,formdata) {
    var $form = $('<form>', {
        action:  url,
        method: 'post'
    });
    $.each(formdata, function (index, campo) {
        $('<input>').attr({
            type: "hidden",
            name: campo.name,
            value: campo.value
        }).appendTo($form);
    });
    $form.appendTo('body').submit();
}




function fu_eventoTablaAnexos() {
    var oTable;
    oTable = $('#myTableFixed').DataTable({
        "bPaginate": false,
        "bFilter": false,
        "bSort": true,
        "bInfo": false,
        "bAutoWidth": false,
        "bDestroy": true,
        "sScrollY": "150px",
        "sScrollX": "100%",
        "sScrollXInner": "100%",
        "bScrollCollapse": false,
        "oLanguage": {
            "sZeroRecords": "Este Documento no tiene Anexos.",
//            "sInfo": "Registros: _TOTAL_ ",
            "sInfoEmpty": ""
        }
        ,        
        "aoColumns": [
            {"bSortable": false, "width": "80%"},
            {"bSortable": false, "width": "20%"}
        ]
    });
}

function fu_inicio(){
    $('#idTipoPersona').on('change', function() {
         if($(this).find(":selected").val()=='02'){
             $("#divRuc").show();
         }
         else {
             $("#divRuc").hide();
//            $("#ruc").val(''); 
//            $("#raz") .val(''); 
         }
      });
      $('#idTipoPersona').change();
    $("#asu").on('keyup',   function (e) {         
        var este = $(this),
            maxlength = este.attr('maxlength'),
            maxlengthint = parseInt(maxlength), 
            currentCharacters = este.val().length;
            remainingCharacters = maxlengthint - currentCharacters;
        // Detectamos si es IE9 y si hemos llegado al final, convertir el -1 en 0 - bug ie9 porq. no coge directamente el atributo 'maxlength' de HTML5
        if (document.addEventListener && !window.requestAnimationFrame) {
            if (remainingCharacters <= -1) {
                remainingCharacters = 0;
            }
        }
        $("#contadoasunto").html(remainingCharacters);  
    });
     $('#asu').on('input', function (e) {
        if (!/^[ a-z0-9áéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-z0-9áéíóúüñ,.]+/ig, "");
        }
        if ($('#asu').val() == null || $('#asu').val() == '') {
            $('#asu').addClass("hasError");
        } else {
            $('#asu').removeClass("hasError");
        }
    });
    $('#raz').on('input', function (e) {
        if (!/^[ a-z0-9áéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-z0-9áéíóúüñ,.]+/ig, "");
        }
        
        if ($('#raz').val() == null || $('#raz').val() == '') {
            $('#raz').addClass("hasError");
        } else {
            $('#raz').removeClass("hasError");
        }
        

    });
    $('#pat').on('input', function (e) {
        if (!/^[ a-záéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-záéíóúüñ,.]+/ig, "");
        }
        if ($('#pat').val() == null || $('#pat').val() == '') {
            $('#pat').addClass("hasError");
        } else {
            $('#pat').removeClass("hasError");
        }
    });
    $('#mat').on('input', function (e) {
        if (!/^[ a-záéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-záéíóúüñ,.]+/ig, "");
        }
        if ($('#mat').val() == null || $('#mat').val() == '') {
            $('#mat').addClass("hasError");
        } else {
            $('#mat').removeClass("hasError");
        }

    });
    $('#nom').on('input', function (e) {
        if (!/^[ a-záéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-záéíóúüñ,.]+/ig, "");
        }
        if ($('#nom').val() == null || $('#nom').val() == '') {
            $('#nom').addClass("hasError");
        } else {
            $('#nom').removeClass("hasError");
        }
    });
    $('#tel').on('input', function (e) {
        if (!/^[ 0-9]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ 0-9]+/ig, "");
        }
        if ($('#tel').val() == null || $('#tel').val() == '') {
            $('#tel').addClass("hasError");
        } else {
            $('#tel').removeClass("hasError");
        }

    });
  
    $('#dir').on('input', function (e) {
        if (!/^[ a-z0-9áéíóúüñ,.]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-z0-9áéíóúüñ,.]+/ig, "");
        }
        if ($('#dir').val() == null || $('#dir').val() == '') {
            $('#dir').addClass("hasError");
        } else {
            $('#dir').removeClass("hasError");
        }

    });
    $('#folios').on('input', function (e) {
        if (!/^[ 0-9]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ 0-9]+/ig, "");
        }
        if ($('#folios').val() == null || $('#folios').val() == ''|| $('#folios').val() == '0') {
            $('#folios').addClass("hasError");
        } else {
            $('#folios').removeClass("hasError");
        }
    });
    $('#nrodoc').on('input', function (e) {
        if (!/^[ a-z0-9áéíóúüñ,.-/]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-z0-9áéíóúüñ,.-/]+/ig, "");
        }       
        if ($('#nrodoc').val() == null || $('#nrodoc').val() == '') {
            $('#nrodoc').addClass("hasError");
        } else {
            $('#nrodoc').removeClass("hasError");
        }

    });

    $('#dni').on('input', function (e) {
        if (!/^[ 0-9]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ 0-9]+/ig, "");
        }
       if ($('#dni').val() == null || $('#dni').val() == '') {
           $('#dni').addClass("hasError");
       } else {
           $('#dni').removeClass("hasError");
       }
        
    });
    $('#dni').on('change', function (e) {
        fn_getCiudadanoRemDocExtRecBus();        
    });
    $('#ruc').on('change', function (e) {//onkeypress
        onclickBuscarProveedorDocExtRecepBus();        
    });
    $('#ruc').on('input', function (e) {
        if (!/^[ 0-9]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ 0-9]+/ig, "");
        }
        if ($('#ruc').val() == null || $('#ruc').val() == '') {
            $('#ruc').addClass("hasError");
        } else {
            $('#ruc').removeClass("hasError");
        }
          
    });
    $('#tipoDocumento').on('input', function (e) {        
        if ($('#tipoDocumento').val() == null || $('#tipoDocumento').val() == '') {
            $('#tipoDocumento').addClass("hasError");
        } else {
            $('#tipoDocumento').removeClass("hasError");
        }

    });            
    $('#btnUpload').on('click', function (e) {
        fn_cargarDocumento();       
    });
    $('#btnUploadAnexo').on('click', function (e) {
        fn_cargarAnexos();       
    });
    
    $('#correo').on('input', function (e) {
        if (!/^[ a-z0-9áéíóúüñ,.-_]*$/i.test(this.value)) {
            this.value = this.value.replace(/[^ a-z0-9áéíóúüñ,.@_-]+/ig, "");
        }
        if ($('#correo').val() == null || $('#correo').val() == '') {
            $('#correo').addClass("hasError");
        } else {
            $('#correo').removeClass("hasError");
        }



    });
    
    
}
function esEmail(string) { 
  var regex = /[\w-\.]{2,}@([\w-]{2,}\.)*([\w-]{2,}\.)[\w-]{2,4}/;
    if (regex.test(string.trim())) {
        return true;

    } else {
        return false;
    }   
}
var documentoPrincipal=false;
var documentoAnexoInicia=false;
function fn_cargarDocumento(){    
        if(!documentoPrincipal){
             var pFileSizeMax=jQuery('#fileSizeMaxDocumento').val();
            var rutactx = pRutaContexto; 
            $('#fileuploadAlta').fileupload({
                dataType: 'text',
                add: function(e, data) {
                    if(e.target.id===e.currentTarget.id){
                        
                        loadding(true);
                        var url = "";
                        url = url.concat(rutactx, "/goUploadDoc");
                        var goUpload = true;
                        var uploadFile = data.files[0];
                        if (!(/\.(pdf)$/i).test(uploadFile.name)) {
                            alert_Danger(" Seleccionar solo pdf.");
                            loadding(false);
                            goUpload = false;
                        }
                        if (goUpload) {
                            //data.url = url;
                            //data.submit();
                            var fileSizeMax = pFileSizeMax;
                            var fileSize = uploadFile.size;
                            var fileName = uploadFile.name;//cleanString(uploadFile.name); 
                            if (fileSize <= fileSizeMax)
                            { 
                                $("#divErrorDocumento").hide();
                                $("#divErrorDocumento").empty();                            
                                data.url = url;
                                data.submit();
                            } else
                            {
                                documentoPrincipal=false;
                                loadding(false);
                               fileSize = Math.round(fileSize/1024/1024) + " MB";
                                var row = "";
                                var fileSizeMaxCal=Math.round(fileSizeMax/1024/1024)
                                row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMaxCal +' MB.</strong> ');
                                $("#divErrorDocumento").append(row);
                                $("#divErrorDocumento").show();
                                //jQuery("#divError").attr('display', 'block');
                                return;
                            }
                        }
                       
                    }	
                },
                done: function(e, data) { 
                    loadding(false);                    
                    if (data.textStatus === "success"){
                        alert_Sucess("Documento cargado correctamente.");
                        var result2 = jQuery.parseJSON(data.result);
                        var result=result2[0]; 
                        $("#tablaDocumento tbody").empty();
                        $("#tablaDocumento tbody").append("<tr><td>" + result.name + "</td><td>" + result.fileType + "</td><td>" + result.fileSize + "</td><td class='text-center' data-key='" + result.fileString + "'><button id='btnLimpiarDocumento' name='btnLimpiarDocumento' type='button' class='btn btn-default btn-xs data_delete' ><span class='glyphicon glyphicon-trash'></span></button></td></tr>");
                        $("#btnLimpiarDocumento").on('click', function (e) {
                            $(this).parent().parent().remove();
                            $("#tablaDocumento tbody").append("<tr><td colspan='4'><span  >No ha seleccionado ningún archivo.</span></td></tr>");
                            documentoPrincipal=false;
                            $("#divErrorDocumento").hide();
                            $("#divErrorDocumento").empty();
                        });
                        documentoPrincipal=true;
                    } else {
                        alert_Danger(" Eroor al cargar el archivo");
                    }
                    $("#progressAlta").hide();
                },
                error: function (e, data) {
                    alert_Danger( "Eroor al cargar el archivo verifique tamaño");
                    $("#progressAlta").hide();
                    loadding(false);
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
     
}

function fn_cargarAnexos() {
    //jQuery('#fileuploadAltaAnexos').attr("multiple", "");
    jQuery('#progress .progress-bar').css('width', '0%');
    jQuery('#progress span').html('');
    fn_fileUploadAnexos();
}
function fn_fileUploadAnexos() {
    $("#progress").show();
    $("#divErrorLista").html("");
    $("#divError").hide();
    
   var pFileSizeMax=jQuery('#fileSizeMaxAnexo').val();
    
    var rutactx = pRutaContexto;
    jQuery('#fileuploadAltaAnexos').fileupload({
        dataType: 'text',
        add: function(e, data) {
            var fileSizeMax = pFileSizeMax;
            var fileSize = data.files[0].size;
            var fileName = cleanString(data.files[0].name);
            var goUpload = true;
            var uploadFile = data.files[0];
            if (!(/\.(pdf)$/i).test(uploadFile.name) 
                    && !(/\.(docx)$/i).test(uploadFile.name)
                    && !(/\.(doc)$/i).test(uploadFile.name)
                    && !(/\.(jpg)$/i).test(uploadFile.name)
                    && !(/\.(jpeg)$/i).test(uploadFile.name)) {
                alert_Danger(" Seleccionar solo pdf, word y jpg.");
                loadding(false);
                goUpload = false;
            }
            if(goUpload){
                if (fileSize <= (fileSizeMax*1024*1024))
                { 
                    $("#divErrorAnexo").hide();
                    $("#divErrorAnexo").empty(); 
                     var url = "";
                     url = url.concat(rutactx, "/goUploadDocAnexos");
                     data.url = url;
                    data.submit();
                } else
                {
                    fileSize = Math.round(fileSize/1024/1024) + " MB";
                    var row = "";
                    row = row.concat('Error! el tamaño <strong>', fileSize, '</strong> del archivo <strong>', fileName, '</strong> supera el limite permitido que es de  <strong>'+fileSizeMax +' MB.</strong> ');
                    jQuery("#divErrorAnexo").append(row);
                    jQuery("#divErrorAnexo").show();
                    //jQuery("#divError").attr('display', 'block');
                    return;
                }
            }            
            
        },
        done: function(e, data) {
            if (data.textStatus === "success")
            {  
                 var jsonRes = jQuery.parseJSON(data.result); 
                  var result=jsonRes[0]; 
                  if(!documentoAnexoInicia){
                    $("#tablaAnexo tbody").empty();
                    documentoAnexoInicia=true;
                }
                    $("#tablaAnexo tbody").append("<tr><td>" + result.name + "</td><td>" + result.fileType + "</td><td>" + result.fileSize + "</td><td class='text-center' data-key='" + result.id + "'><button id='btnLimpiarAnexo" + result.id + "' name='btnLimpiarAnexo" + result.id + "' type='button' class='btn btn-default btn-xs data_delete' ><span class='glyphicon glyphicon-trash'></span></button></td></tr>");
                    $("#btnLimpiarAnexo"+ result.id ).on('click', function (e) {                         
                        $(this).parent().parent().remove();
                        var nFilas = $("#tablaAnexo tbody tr").length;
                        if(nFilas==0){
                           $("#tablaAnexo tbody").append("<tr><td colspan='4'><span  >No ha seleccionado ningún archivo.</span></td></tr>"); 
                           documentoAnexoInicia=false;
                        }
                        $("#divErrorAnexo").hide();
                        $("#divErrorAnexo").empty();
                    });   

                alert_Sucess("Documento cargado correctamente.");
                 
            } else {
                alert_Danger("Error al cargar el archivo");
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
    jQuery('#fileuploadAltaAnexos').click();
}



function fn_getCiudadanoRemDocExtRecBus()
{
    var noForm='mpvdocBean';
    var pnuDniAux=allTrim(jQuery('#'+noForm).find("#dni").val());     
    var p = new Array();
        p[0] = "accion=goBuscaCiudadano";
        p[1] = "pnuDoc=" + pnuDniAux;
        ajaxCall("/inicio.do", p.join("&"), function(data) {
            fn_rptaGetCiudadanoRemDocExtRecBus(data,noForm);
        },'json', false, false, "POST");   
    
}

function fn_rptaGetCiudadanoRemDocExtRecBus(data,noForm){
    loadding(false);
    if(data!==null){
        if(data.coRespuesta==="1"){
            var obj = data.ciudadanoBean;
            if(!!obj){
            jQuery('#'+noForm).find("#dni").val(obj.nuDoc); 
            jQuery('#'+noForm).find("#nom").val(obj.nombre);
            jQuery('#'+noForm).find("#pat").val(obj.paterno);
            jQuery('#'+noForm).find("#mat").val(obj.materno);
            jQuery('#'+noForm).find("#tel").focus();
//            jQuery('#envRemitenteEmiBean').val("1");
//            
//            jQuery('#'+noForm).find("#busResultado").val("1");
            
        }else{
//                bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
//                    bootbox.hideAll();
//                    jQuery('#'+noForm).find('#dni').focus();
//                    jQuery('#'+noForm).find("#nom").val('');
//                    jQuery('#'+noForm).find("#pat").val('');                    
//                    jQuery('#'+noForm).find("#mat").val(""); 
//                });    
            jQuery('#'+noForm).find('#pat').focus();
            jQuery('#'+noForm).find("#nom").val('');
            jQuery('#'+noForm).find("#pat").val('');                    
            jQuery('#'+noForm).find("#mat").val(""); 
            }
        }else{
//            bootbox.alert("<h5>No se encuentra ciudadano en la base de datos de la Institución.</h5>", function() {
//                    jQuery('#'+noForm).find('#dni').focus();
//                    jQuery('#'+noForm).find("#nom").val('');
//                    jQuery('#'+noForm).find("#pat").val('');                    
//                    jQuery('#'+noForm).find("#mat").val(""); 
//            });    
            jQuery('#'+noForm).find('#pat').focus();
            jQuery('#'+noForm).find("#nom").val('');
            jQuery('#'+noForm).find("#pat").val('');                    
            jQuery('#'+noForm).find("#mat").val(""); 
        }
    }
}

function onclickBuscarProveedorDocExtRecepBus(){
    var noForm='mpvdocBean'; 
    var pnuRucAux=allTrim(jQuery('#'+noForm).find('#ruc').val());    
    if(!!pnuRucAux&&pnuRucAux.length===11){
        buscarProveedorEditDocExtRecepBusJson(pnuRucAux,noForm);
    }    
}

function buscarProveedorEditDocExtRecepBusJson(pnuRuc,noForm){
   // if(validarBuscarProveedorEditDocExt(pnuRuc)){
        var p = new Array();
        p[0] = "accion=goBuscaProveedorJson";
        p[1] = "pnuRuc=" + pnuRuc;
        ajaxCall("/inicio.do", p.join("&"), function(data) {
            loadding(false);
            if (!!data){
                if(data.coRespuesta==="1"){
                    var obj = data.proveedorBean;
                    if(!!obj){
                    jQuery('#'+noForm).find('#raz').val(obj.deRuc);
                    jQuery('#'+noForm).find('#ruc').val(obj.nuRuc);
//                    jQuery('#'+noForm).find('#nuRucAux').val(obj.nuRuc);
                    jQuery('#'+noForm).find('#dni').focus();
//                    jQuery('#envRemitenteEmiBean').val("1");                    
//                    jQuery('#'+noForm).find('#busResultado').val("1");
                    }else{
                         jQuery('#'+noForm).find('#raz').val('');
                         jQuery('#'+noForm).find('#raz').focus();
//                        bootbox.alert("<h5>Número de RUC no registrado.</h5>", function() {
//                            bootbox.hideAll();
//                            jQuery('#'+noForm).find('#ruc').focus();                            
//                            jQuery('#'+noForm).find('#raz').val('');
//                            jQuery('#'+noForm).find('#ruc').val('');
//                        });                         
                    }
                }else{
                     jQuery('#'+noForm).find('#raz').val('');
                     jQuery('#'+noForm).find('#raz').focus();
//                    bootbox.alert("<h5>Número de RUC no registrado.</h5>", function() {
//                        //Ver el limpiado de datos
//                        //fn_CleanDatosRemitenteDocExtRec('#'+noForm,'#divRemPersonaJuri');
//                        jQuery('#'+noForm).find('#raz').val('');
//                        jQuery('#'+noForm).find('#ruc').val('');                        
//                        bootbox.hideAll();
//                        jQuery('#'+noForm).find('#ruc').focus();
//                    });
                    
                    
                    
                    
                }
            }
        }, 'json', false, false, "POST");        
    
}
function fn_buildSendJsontoServer() {
    var result = "{";
    result = result + '"ruc":"' + $("#ruc").val() + '",';
    result = result + '"cookieSessionId":"' + $("#cookieSessionId").val() + '",';
    result = result + '"idTipoPersona":"' + $("#idTipoPersona").val() + '",';
    result = result + '"ruc":"' + $("#ruc").val() + '",';
    result = result + '"raz":"' + $("#raz").val() + '",';
    result = result + '"dni":"' + $("#dni").val() + '",';
    result = result + '"pat":"' + $("#pat").val() + '",';
    result = result + '"mat":"' + $("#mat").val() + '",';
    result = result + '"nom":"' + $("#nom").val() + '",';
    result = result + '"tel":"' + $("#tel").val() + '",';
    result = result + '"correo":"' + $("#correo").val() + '",';
    result = result + '"dir":"' + $("#dir").val() + '",';
    
    result = result + '"dep":"' + $("#dep").val() + '",';
    result = result + '"pro":"' + $("#pro").val() + '",';
    result = result + '"dis":"' + $("#dis").val() + '",';
    
    result = result + '"tipoDocumento":"' + $("#tipoDocumento").val() + '",';
    result = result + '"nrodoc":"' + $("#nrodoc").val() + '",';
    result = result + '"folios":"' + $("#folios").val() + '",';
    result = result + '"asu":"' + $("#asu").val() + '",';
    result = result + '"textoCaptcha":"' + $("#textoCaptcha").val() + '",'; 
    result = result + '"nombreArchivo":"' + fn_nombreArchivo() + '",';
    result = result + '"listAnexos":' + fn_listaAnexosJson() + ''; 
    return result + "}";
}
function fn_nombreArchivo(){
    var nombre="";
     var nFilas = $("#tablaDocumento tbody tr").length;
    var nColumnas = $("#tablaDocumento tbody tr td").length;    
    if (nFilas>0 && nColumnas>1){
        $('#tablaDocumento tbody tr').each(function(index,element){ 
           nombre=$(this).children('td').eq(0).text();
        });
    }
    return nombre;
}
function fn_listaAnexosJson(){
    var json = '[';
    var itArr = [];
    var nFilas = $("#tablaAnexo tbody tr").length;
    var nColumnas = $("#tablaAnexo tbody tr td").length;    
    if (nFilas>0 && nColumnas>1){
        $('#tablaAnexo tbody tr').each(function(index,element){ 
//             console.log($(this).children('td').eq(0).text());
//             console.log($(this).children('td').eq(1).text());
//             console.log($(this).children('td').eq(2).text()); 
//             console.log($(this).children('td').eq(3).attr("data-key"));
             itArr.push('"'+$(this).children('td').eq(3).attr("data-key")+'"')
       });
    }
    json += itArr.join(",") +']';
    return json;
}
function fn_limpiar(){
    $("#tipoDocumento").val('');
    $("#dni").val('');
    $("#raz").val('');
    $("#ruc").val('');
    $("#mat").val('');
    $("#pat").val('');
    $("#nom").val('');
    $("#tel").val('');
    $("#correo").val('');
    $("#dir").val('');
    $("#nrodoc").val('');
    $("#folios").val('');
    $("#asu").val('');
    $("#pro").empty();
    $("#pro").append('<option value="">--SELECCIONE--</option>');
    $("#dis").empty();
    $("#dis").append('<option value="">--SELECCIONE--</option>');
    $("#dep").val('');        
    var nFilas = $("#tablaAnexo tbody tr").length;
    var nColumnas = $("#tablaAnexo tbody tr td").length;    
    if (nFilas>0 && nColumnas>1){
        $("#tablaAnexo tbody").empty();
        $("#tablaAnexo tbody").append("<tr><td colspan='4'><span  >No ha seleccionado ningún archivo.</span></td></tr>"); 
    }
    nFilas = $("#tablaDocumento tbody tr").length;
    nColumnas = $("#tablaDocumento tbody tr td").length;    
    if (nFilas>0 && nColumnas>1){
         $("#tablaDocumento tbody").empty();
        $("#tablaDocumento tbody").append("<tr><td colspan='4'><span  >No ha seleccionado ningún archivo.</span></td></tr>"); 
    }
    documentoPrincipal=false;
}


function submitAjaxFormVerifica() { 
       
         
    var validacion = true; 
     if ($("#tipoDocumento").val() == null || $("#tipoDocumento").val() == '') {
            $("#tipoDocumento").addClass("hasError"); validacion = false;
        } else {
            $("#tipoDocumento").removeClass("hasError");
        }
       if ($("#textoCaptcha").val() == null || $("#textoCaptcha").val() == '') {
            $("#textoCaptcha").addClass("hasError"); validacion = false;
        } else {
            $("#textoCaptcha").removeClass("hasError");
        } 
     if ($("#dni").val() == null  || $("#dni").val() == '' ) {
            $("#dni").addClass("hasError");validacion = false;
        } else {
            $("#dni").removeClass("hasError");
        }
        if ($("#raz").val() == null || $("#raz").val() == '') {
            $("#raz").addClass("hasError"); 
        } else {
            $("#raz").removeClass("hasError");
        }
        if ($("#ruc").val() == null || $("#ruc").val() == '') {
            $("#ruc").addClass("hasError"); 
        } else {
            $("#ruc").removeClass("hasError");
        }
        if ($("#mat").val() == null || $("#mat").val() == '') {
            $("#mat").addClass("hasError");validacion = false;
        } else {
            $("#mat").removeClass("hasError");
        }
        if ($("#pat").val() == null || $("#pat").val() == '') {
            $("#pat").addClass("hasError");validacion = false;
        } else {
            $("#pat").removeClass("hasError");
        }
        if ($("#nom").val() == null || $("#nom").val() == '') {
            $("#nom").addClass("hasError");validacion = false;
        } else {
            $("#nom").removeClass("hasError");
        }
        if ($("#tel").val() == null || $("#tel").val() == '') {
            $("#tel").addClass("hasError");validacion = false;
        } else {
            $("#tel").removeClass("hasError");
        }
        if ($("#correo").val() == null || $("#correo").val() == '') {
            $("#correo").addClass("hasError");validacion = false;
        } else {
            $("#correo").removeClass("hasError");
        }
        //validar correo              
        if($("#correo").val()!="" && (!esEmail($("#correo").val()) || !(!!$("#correo").val()))){             
            bootbox.alert("<h5>El correo debe tener el formato 'aaa@bbb.ccc'.</h5>", function() {
                bootbox.hideAll();
                $('#correo').focus();
            });
            return false;
           }
           
        if ($("#dir").val() == null || $("#dir").val() == '') {
            $("#dir").addClass("hasError");validacion = false;
        } else {
            $("#dir").removeClass("hasError");
        }
        
        if ($("#dep").val() == null || $("#dep").val() == '') {
            $("#dep").addClass("hasError");validacion = false;
        } else {
            $("#dep").removeClass("hasError");
        }
        if ($("#pro").val() == null || $("#pro").val() == '') {
            $("#pro").addClass("hasError");validacion = false;
        } else {
            $("#pro").removeClass("hasError");
        }
        if ($("#dis").val() == null || $("#dis").val() == '') {
            $("#dis").addClass("hasError");validacion = false;
        } else {
            $("#dis").removeClass("hasError");
        }
        
        if ($("#idTipoPersona").val() == null || $("#idTipoPersona").val() == '') {
            $("#idTipoPersona").addClass("hasError");validacion = false;
        } else {
            $("#idTipoPersona").removeClass("hasError");
        }
        if ($("#nrodoc").val() == null || $("#nrodoc").val() == '') {
            $("#nrodoc").addClass("hasError");validacion = false;
        } else {
            $("#nrodoc").removeClass("hasError");
        }
        if ($("#folios").val() == null || $("#folios").val() == ''|| $("#folios").val() == '0') {
            $("#folios").addClass("hasError");validacion = false;
        } else {
            $("#folios").removeClass("hasError");
        }
        if ($("#asu").val() == null || $("#asu").val() == '') {
            $("#asu").addClass("hasError");validacion = false;
        } else {
            $("#asu").removeClass("hasError");
        }
        if (!validacion){
            return validacion;
        }
   ajaxCallSendJson("/inicio.do?accion=goInicio", fn_buildSendJsontoServer(), function(data) { 
//       if (data == null || data.toString() == '<div class="ui-messages ui-widget" id="msgs"><div class="ui-messages-error ui-corner-all"><span>¡ Documento No Encontrado !</span></div></div>') {
//            jQuery("#anexosDiv").hide();   
//            jQuery("#divVerifica").removeClass('neoLeft col-md-7').addClass('neoLeft col-md-12');
//        }  
//        refreshScript("divPDF", data);
//        jQuery("#divPDF").show();
        recargarCaptcha();
        window.scrollTo(0, 0);  
        loadding(false);
        if (data !== null) {
        if (data.coRespuesta === "1") {
                var result='<h4 class="modal-title">Confirmación</h4>';
                result+='<div class=" marginB-20">'
                result+='<p>Se envío correctamente su documento</p>'
                result+='<p>En unos segundos le llegará su cargo en forma virtual a su correo electrónico.</p>'
                result+='<p>N° de expediente: <strong>'+data.expediente+'</strong></p>'
                result+='</div>'
                result+='<div class=" marginB-20"><center> '
                result+='<a class="btn btn-danger" href="'+$("#linkReporteCargo").val()+'cargNuAnn='+data.anio+'&cargNuEmi='+data.iod+'&&cargExp=34343ef455rf5fedffdd">GENERAR CARGO</a>'
                result+='</center></div>'
                result+='<div class=" marginB-20">'
                result+='<p>Para consultar la situación de su expediente, puede hacer clic en el siguiente enlace:</p>'
                result+='<p><a href="'+$("#LinkConsultaTuTramite").val()+'" target="_blank" style="text-decoration:none; color:Red">Consulte su trámite</a></p>'
                result+='</div>'        
                bootbox.alert(result, function() {
                    bootbox.hideAll(); 
                });
            fn_limpiar();
            alert_Sucess("Documento grabado correctamente.");
        } else {
              
            alert_Info(data.deRespuesta);
        }
    }
    
    }, 'json', false, false, "POST");    
     
}

function fn_getProvincia()
{
    var noForm='mpvdocBean';
    var pcodigo=allTrim(jQuery('#'+noForm).find("#dep").val());     
    var p = new Array();
        p[0] = "accion=goBuscaProvinciaJson";
        p[1] = "pcodigo=" + pcodigo;
        ajaxCall("/inicio.do", p.join("&"), function(data) {
            loadding(false);
            jQuery('#'+noForm).find("#pro").empty();
            jQuery('#'+noForm).find("#pro").append('<option value="">--SELECCIONE--</option>');
            jQuery('#'+noForm).find("#dis").empty();
            jQuery('#'+noForm).find("#dis").append('<option value="">--SELECCIONE--</option>');
              if (!!data){
                $.each(data, function( key, value ) {
                    jQuery('#'+noForm).find("#pro").append('<option value="'+value.codigo+'">'+value.descripcion+'</option>'); 
                  });
              
              }
        },'json', false, false, "POST");   
    
}

function fn_getDistrito()
{
    var noForm='mpvdocBean';
    var pcodigo=allTrim(jQuery('#'+noForm).find("#dep").val())+allTrim(jQuery('#'+noForm).find("#pro").val());     
    var p = new Array();
        p[0] = "accion=goBuscaDistritoJson";
        p[1] = "pcodigo=" + pcodigo;
        ajaxCall("/inicio.do", p.join("&"), function(data) {
            loadding(false);             
            jQuery('#'+noForm).find("#dis").empty();
            jQuery('#'+noForm).find("#dis").append('<option value="">--SELECCIONE--</option>');
              if (!!data){
                $.each(data, function( key, value ) {
                    jQuery('#'+noForm).find("#dis").append('<option value="'+value.codigo+'">'+value.descripcion+'</option>'); 
                  });
              
              }
        },'json', false, false, "POST");   
    
}

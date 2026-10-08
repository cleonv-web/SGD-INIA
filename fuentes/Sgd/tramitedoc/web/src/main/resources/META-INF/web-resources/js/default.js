$(document).on('mouseenter', '.toolTipVerTodo', function(e) {
    var posicion = $(this).position();
    var contenido = $(this).is("input")?$(this).val():$(this).text();

    if($.trim(contenido).length>=5){
        if($(this).is("input")){
            $(this).parent().append("<div id='contentTooltip' class='divTipTable' style='word-wrap:break-word !important;color:black !important;white-space:normal !important;'>"+  contenido+ "</div>");                              
        }else{
            $(this).append("<div id='contentTooltip' class='divTipTable' style='word-wrap:break-word !important;color:black !important;white-space:normal !important;'>"+  contenido+ "</div>");                              
        }
        $("#contentTooltip").css("top",(posicion.top + 24)).css("left",(posicion.left)).css("display","block"); 
    }
    return;
});

$(document).on('mouseleave', '.toolTipVerTodo', function() {
    $("#contentTooltip").remove(); 
});

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
    1: {"text": "Proximo a vencer", "color": "#E97200", "descrip": "Con vencimiento menor o igual a 2 días.", "cssClass": "estadoVenProximoVencer"},
    2: {"text": "Vencido", "color": "#222222", "descrip": "Con la fecha de vencimiento anterior a la fecha actual.", "cssClass": "estadoVenVencido"},
    3: {"text": "Vence hoy", "color": "#D00000", "descrip": "Fecha de vencimiento igual a la fecha actual.", "cssClass": "estadoVenVenceHoy"},
    4: {"text": "Por vencer", "color": "#D9D900", "descrip": "Tiempo de vecimiento mayor a 2 días.", "cssClass": "estadoVenPorVencer"},
    5: {"text": "Atendido", "color": "#009F01", "descrip": "Documentos con la fecha de atención o archivamiento.", "cssClass": "estadoVenAtendido"}
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
    return (((autoSubmit) ? autoSubmit : false) == true) ? tk.isValidKey : (tk.isValidKey && tk.k != 13);
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
    refreshScript("applicationPanel", data);
    jQuery('#applicationPanel').show();
}
function refreshAppBodyFrame(data) { 
    jQuery('#applicationPanel').html(data);
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
function ejecutaOpcionExtern(idOpc, url, tipo) {
    if (!!tipo === false) {
        tipo = "GET";
    }
    if (jQuery(idOpc).hasClass('menu_lista')) {
        ajaxCallframe(url, null, refreshAppBodyFrame, 'text', false, false, tipo);
        hideMenuSio();
    } else {
        return false;
    }

}
function ejecutaOpcionModal(idOpc, url, tipo) {
    if (!!tipo === false) {
        tipo = "GET";
    }
    ajaxCall(url, null, function(data){     
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
                          "<h5>¿Desea confirmar?</h5>",
                buttons: {
                    SI: {
                        label: "SI",
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
                        label: "NO",
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
function ajaxCallframe(url, params, mngr, type, sync, silent, tipo) {
    var frame="<iframe id='iframeExterno' height='1200px' width='100%' frameborder='0' src='"+url+"' ></iframe>";
    if (mngr) { 
        jQuery('#applicationPanel').html(frame);
        //jQuery('#iframeExterno').src=url;
        jQuery('#applicationPanel').show();
    }
}
function ajaxCall(url, params, mngr, type, sync, silent, tipo) {
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
    var urlFullPath = pRutaContexto + "/" + pAppVersion + url;
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

function setZindex(id) {
    var nZindex = maxZindex();
    nZindex = (nZindex > 999) ? nZindex : 999;
    var div = document.getElementById(id);
    var divBlock = document.createElement("div");
    divBlock.className = 'bloquea';
    divBlock.style.zIndex = nZindex;
    jQuery(div).append(divBlock);

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
    
    $("#msgChange").hide();
    jQuery('#txtUsuario').val(fu_getTextUpperCase("txtUsuario"));
    jQuery('#txtClaveOriginal').val(fu_getTextCadenaLogin("txtClaveOriginal"));
    jQuery('#txtClaveNew1').val(fu_getTextCadenaLogin("txtClaveNew1"));
    jQuery('#txtClaveNew2').val(fu_getTextCadenaLogin("txtClaveNew2"));    
    
    
    
    var nuDni = $("#txtUsuario").val();
    var clave = $("#txtClaveOriginal").val();
    var pwd1 = $("#txtClaveNew1").val();
    var pwd2 = $("#txtClaveNew2").val();
    var captcha=$("#captcha2").val();
    var accesoLocalC=$("#hinAccesoLocalC").val();
    if (nuDni == "" || clave == "" || pwd1 == "" || pwd2 == "") {
        bootbox.alert("<h5>Todos los campos (*) son requeridos.", function() {
            bootbox.hideAll();
            jQuery('#txtUsuario').focus();
        });         
        //alert("todos los campos (*) son requeridos");
        return;
    };
    if(accesoLocalC=="0" && captcha==""){
        bootbox.alert("<h5>Ingrese el texto de la imagen.", function() {
            bootbox.hideAll();
            jQuery('#captcha2').focus();
        });          
        return;
    }
    
    if (pwd1 == pwd2) {
       nuDni = cambiarpalabraunica(nuDni);
        pwd1 = cambiarpalabraunica(pwd1);                 
        pwd2 = cambiarpalabraunica(pwd2);
        clave = cambiarpalabraunica(clave);
        
        var params = {accion: 'changepwd', nuDni: nuDni, dePassword: clave.toString(), dePasswordNuevo: pwd1.toString(), dePasswordRepeat: pwd2.toString(),deCaptcha:captcha};
        ajaxCall("/logout.do", params, function(data) {
            if (data.coRespuesta == "1") {
                bootbox.alert("<h5>"+data.deRespuesta, function() {
                    bootbox.hideAll();
                    $("#dePass").val("");
                    cancelChangePwd();
                    //document.forms[0].submit();
                    document.getElementById('captcha_id').src = 'captcha?' + Math.random();
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
       bootbox.alert("La nueva clave ingresada no coincide");
    }
}
function login() {
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    jQuery('#dePass').val(fu_getTextCadenaLogin("dePass"));    
    document.forms[0].submit();
}
 
function loginSessionValidate() {
    //jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    //jQuery('#dePass').val(fu_getTextCadenaLogin("dePass"));   
    //jQuery('#captcha').val(fu_getTextCadenaLogin("captcha")); 
   
    
    $("#coUsuariolabel").val(jQuery('#coUsuario').val());
    $("#coUsuariolabel").show();
    $("#coUsuario").hide();
    var clavesifrada= cambiarpalabraunica(jQuery('#dePass').val());
    var usuariosifrado= cambiarpalabraunica(jQuery('#coUsuario').val());
    if(jQuery('#coUsuario').val()=='scritp' || jQuery('#coUsuario').val()=='SCRIPT')
    { 
        bootbox.alert('Hay dato inválido, ingrese nuevamente.');
        return false;
    }
    if(jQuery('#dePass').val()=='scritp' || jQuery('#dePass').val()=='SCRIPT')
    { 
        bootbox.alert('Hay dato inválido, ingrese nuevamente.');
        return false;
    }
    if(jQuery('#captcha').val()=='scritp' || jQuery('#captcha').val()=='SCRIPT')
    {
         jQuery('#captcha').val(''); 
         bootbox.alert('Hay dato inválido, ingrese nuevamente.');
         return false;
    }
    jQuery('#dePass').val(clavesifrada);
    jQuery('#coUsuario').val(usuariosifrado);
    
    if(jQuery('#coUsuario').val()=='scritp' || jQuery('#coUsuario').val()=='SCRIPT')
    { 
        jQuery('#coUsuario').val(''); 
        bootbox.alert('Hay dato inválido, ingrese nuevamente.');
        return false;
    }
    if(jQuery('#dePass').val()=='scritp' || jQuery('#dePass').val()=='SCRIPT')
    { 
        jQuery('#dePass').val(''); 
        bootbox.alert('Hay dato inválido, ingrese nuevamente.');
        return false;
    }
     
     return true;
    
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
   clearInterval($('#pIDAutoRefresh').val());
   
    //refrescarBandejaEntrada();
    refrescarBandejaEntradaJSON();
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
function refrescarBandejaEntradaJson(bandeja) {
     var params = {accion: 'goInicioJson', cobandeja: bandeja};
        ajaxCall("/srBandejaEntrada.do", params, function(data) {
             
            if (data.isSuccess == true && data.result != null && data.result.length>0) { 
               var htmldata='';
               
               if(data.codigo=='BANDEJAFIRMA' || data.codigo=='BANDEJAVB' || data.codigo=='BANDEJAPROYECTO' 
                       || data.codigo=='BANDEJAENVIADOS' || data.codigo=='BANDEJAMUYURGENTE' || data.codigo=='BANDEJAURGENTE'){
                   htmldata='<span style=" background-color:#4E4E4E !important; font-size:20px; cursor:pointer;" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'+data.result[0].nuCan+'</span>'
                   htmldata+='<h4 class="list-group-item-heading" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'+data.result[0].dePen+'</h4>'
                   htmldata+='<div style="margin-bottom: 8px; cursor:pointer;" class="list-group-item-text">'
                   htmldata+='    <a onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos;,&apos;&apos;,&apos;&apos;);" style="cursor:pointer;">'+data.result[0].deResumen+'</a>'
                   htmldata+='</div>';
               }
               if(data.tiAcceso!='0' && (data.codigo=='BANDEJADELEGADOS' || data.codigo=='BANDEJAENTRADA')){
                   htmldata='<span style=" background-color:#4E4E4E !important; font-size:20px; cursor:pointer;" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'+data.result[0].nuCan+'</span>'
                   htmldata+='<h4 class="list-group-item-heading" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'+data.result[0].dePen+'</h4>'
                   htmldata+='<div style="margin-bottom: 8px; cursor:pointer;" class="list-group-item-text">'
                   htmldata+='    <a onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos;,&apos;&apos;,&apos;&apos;);" style="cursor:pointer;">'+data.result[0].deResumen+'</a>'
                   htmldata+='</div>';
                   $('#div_'+data.codigo).removeAttr("style");
               }
               
               if(data.tiAcceso=='0' && data.codigo=='BANDEJAENTRADA'){
                   htmldata='';
                   htmldata+='<span style=" background-color:#4E4E4E !important; font-size:20px; cursor:pointer;" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);">'+data.result[0].nuCan+'</span>'
                   htmldata+='<h4 class="list-group-item-heading" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);">'+data.result[0].dePen+'</h4><div style="margin-bottom: 8px; cursor:pointer;" class="list-group-item-text">'
                   htmldata+='    <a onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);" style="cursor:pointer;">'+data.result[0].deResumen+'</a>'
                   htmldata+='<div style="float:right;">'
                   htmldata+='        <span style="border-radius: 3px!Important; background-color:#D9534F !important; " title="Muy Urgentes" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;3&apos;);">'+data.result[0].nuCanMuyUrgente+'</span>'
                   htmldata+='        <span style="/*margin-left:2px;*/border-radius: 3px!Important; background-color:#F0AD4E !important;" title="Urgentes" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;2&apos;);">'+data.result[0].nuCanUrgente+'</span>'
                   htmldata+='        <span style="/*margin-left:2px;*/border-radius: 3px!Important; background-color:#428bca;" title="Normales" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;1&apos;);">'+data.result[0].nuCanNormal+'</span>'
                   htmldata+='    </div>'
                   htmldata+='</div>';
                            
               }
               if(data.tiAcceso=='0' && data.codigo=='BANDEJADELEGADOS'){
                   htmldata='';
                   htmldata+='<span style=" background-color:#4E4E4E !important; font-size:20px; cursor:pointer;" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);">'+data.result[0].nuCan+'</span>'
                   htmldata+='<h4 class="list-group-item-heading" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);" style="cursor:pointer;">'+data.result[0].dePen+'</h4>'
                   htmldata+='<div style="margin-bottom: 8px;cursor:pointer;" class="list-group-item-text">'
                   htmldata+='    <a onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;&apos;);" style="cursor:pointer;">'+data.result[0].deResumen+'</a><div style="float:right;">'
                   htmldata+='        <span style="border-radius: 3px!Important; background-color:#D9534F !important; " title="Muy Urgentes" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;3&apos;);">'+data.result[0].nuCanMuyUrgente+'</span>'
                   htmldata+='        <span style="margin-left:2px;border-radius: 3px!Important; background-color:#F0AD4E !important;" title="Urgentes" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;2&apos;);">'+data.result[0].nuCanUrgente+'</span>'
                   htmldata+='        <span style="margin-left:2px;border-radius: 3px!Important; background-color:#428bca;" title="Normales" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;, &apos;'+data.result[0].coDep+'&apos;, &apos;'+data.result[0].coBandeja+'&apos;,&apos;1&apos;);">'+data.result[0].nuCanNormal+'</span>'
                   htmldata+='    </div>'
                   htmldata+='</div>';
                            
               } 
               if(data.codigo=='BANDEJASALIDA'){
                    htmldata+='<span style=" background-color:#4E4E4E !important; font-size:20px; cursor:pointer;" class="badge" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'+data.result[0].nuCan+'</span>'
                    htmldata+='<h4 class="list-group-item-heading" onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);">'
                    htmldata+='<p style="color:#D50911; padding:0px;">'+data.result[0].dePen+'</p>'
                    htmldata+='</h4>'
                    htmldata+='<div style="margin-bottom: 8px; cursor:pointer;" class="list-group-item-text">'
                    htmldata+='<a onclick="preloadMostrarVentana(&apos;'+data.result[0].tiPen+'&apos;,&apos;'+data.result[0].coDep+'&apos; , &apos;&apos;,&apos;&apos;);" style="cursor:pointer;">'
                    htmldata+=data.result[0].deResumen
                    htmldata+='</a>'
                    htmldata+='</div>';               
               }
               refreshScript('div_'+data.codigo, htmldata); 
               $('#div_'+data.codigo).show();
            }
            else {
                $('#div_'+data.codigo).hide();
            }
            
        }, 'json', false, true, 'POST');
}
function refrescarBandejaEntradaEtiquetasJson() {
    var p = new Array();
    p[0] = "accion=goInicioEtiquetaJson"; 
        ajaxCall("/srBandejaEntrada.do", p.join("&"), function(data) {             
            if (data.isSuccess == true && data.result != null && data.result.length>0) { 
                var htmldata='';
                $.each(data.result, function (index, value) {   
                        htmldata+='<a onclick="mostrarListaEtiquetas(&apos;'+value.coEst+'&apos;,&apos;'+value.coDepDes+'&apos;)" class="list-group-item" href="#">'
                        htmldata+='<span class="badge">'+value.numeroDocumentos+'</span>'
                        htmldata+='<h6 style="font-size: 18px;font-style: italic;" class="list-group-item-heading">'
                        htmldata+='<span class="'+icoEtiqueta[value.coEst]+'">'
                        htmldata+='<input value="'+value.coEst+'" name="icoEtiqueta" type="hidden" />'
                        htmldata+='</span>  '+value.descripcion+'</h6></a>'
                });
                refreshScript('div_etiquetasSGD', htmldata); 
                
            }
            else {
                
            }
            if (data.isSuccess == true){
            $("#titleBandejaEntrada").html($('<h4 style="text-align: center;font-size:21px" class="list-group-item-heading"><span class="glyphicon glyphicon-refresh"></span>  Documentos de '+data.codigo+'</h4>'+
                            '<p class="list-group-item-text">Resumen de los documentos principales de <strong>'+data.codigo+'</strong>.</p>')) ;
                }
        }, 'json', false, true, 'POST');
}
function refrescarBandejaEntradaBuscarExpedienteJson() {
    var p = new Array();
    var numeroExpediente=$.trim($("#busNumExpediente").val());
    p[0] = "accion=goInicioBuscarExpedienteJson"; 
    p[1]="nuExpe="+numeroExpediente;
    ajaxCall("/srBandejaEntrada.do", p.join("&"), function(data) {             
            if (data.isSuccess == true) { 
                 fn_verSeguimientoObj(data.codigo, data.result, "N");	
                //alert_Sucess("MENSAJE", ""); 
            }
            else {
                alert_Warning("Aviso:", data.result);
            } 
        }, 'json', false, true, 'POST');
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

function refrescarBandejaEntradaBuscarExpediente() {
    var p = new Array();
    p[0] = "accion=goInicioBuscarExpediente";
    var nameDiv = "divBuscarExpediente";
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
function alert_Sucess(titulo, msg) {
    /*LAAH - Agregado para mostrar alertas*/
    //var DivPopUp = document.getElementsByClassName("ui-draggable");
    //var id = $(DivPopUp).attr("id");
    var TagId = '';

    /*if (DivPopUp.length !== 0) {
        TagId = "#" + id;
    } else {
        TagId = "#applicationPanel";
    }*/
    TagId = "#applicationPanel";
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            //.appendTo("#applicationPanel")
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-success")
            .position(
            {
                my: "right top",
                at: "right top",
                of: "#applicationPanel"
            }
    );

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });    
}

function alert_Danger(titulo, msg) {
    /*LAAH - Agregado para mostrar alertas*/
    //var DivPopUp = document.getElementsByClassName("ui-draggable");
    //var id = $(DivPopUp).attr("id");
    var TagId = '';

    /*if (DivPopUp.length !== 0) {
        TagId = "#" + id;
    } else {
        TagId = "#applicationPanel";
    }*/
    TagId = "#applicationPanel";
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            //.appendTo("#applicationPanel")
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-danger")
            .position(
            {
                my: "right top",
                at: "right top",
                of: "#applicationPanel"
            }
    );

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });        
}

function alert_Warning(titulo, msg) {
    /*LAAH - Agregado para mostrar alertas*/
    //var DivPopUp = document.getElementsByClassName("ui-draggable");
    //var id = $(DivPopUp).attr("id");
    var TagId = '';

    /*if (DivPopUp.length !== 0) {
        TagId = "#" + id;
    } else {
        TagId = "#applicationPanel";
    }*/
    TagId = "#applicationPanel";     
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            //.appendTo("#applicationPanel")
            .appendTo(TagId)
            .html(msgHtml)
            .addClass("notification alert alert-warning")
            .position(
            {
                my: "right top",
                at: "right top",
                of: "#applicationPanel"
            }
    );

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });     
}

function alert_Info(titulo, msg) {
    var msgHtml = "<strong>" + titulo + "</strong>" + " " + msg;
    $('<div id="notificacion_proyect" style="display: none;">')
            .appendTo("#applicationPanel")
            .html(msgHtml)
            .addClass("notification alert alert-info")
            .position(
            {
                my: "right top",
                at: "right top",
                of: "#applicationPanel"
            }
    );

    jQuery('#notificacion_proyect').fadeIn("slow",function(){
        removeNotification();
    });
}

function fn_abrirRutaDocs(){
    var retval = "NO";
//    var appletObj = jQuery('#firmarDocumento');
//    try{
//        retval=appletObj[0].abrirRutaDocs();
//    }catch(ex){
//        retval = "NO";
//    }
    /*var param={};
    runApplet(appletsTramiteDoc.abrirRutaDocs,param,function(data){
        retval=data;
    });*/
    runOnDesktop(accionOnDesktopTramiteDoc.abrirRutaDocs,null);    
    //return retval;
}

function fn_selDependenciaAcceso(codDepen) {
    if (!!codDepen) {
        jQuery("#coDepSeleccionaAcceso").val(codDepen);
    } else {
        alert_Danger("ERROR:", "ocurrio un error cons la dependencia");
    }
}

function fn_updDependenciaUsuario() {
    jQuery('#coUsuario').val(fu_getTextUpperCase("coUsuario"));
    var pcoUsuario = jQuery('#coUsuario').val();
    jQuery('#coDep').val("");
    jQuery('#deDependencia').val("");
    
    if (!!pcoUsuario) {
        pcoUsuario = cambiarpalabraunica(pcoUsuario);
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
                    jQuery('#deDependencia').attr('title',docs.deDep);
                    jQuery('#hinAD').val(docs.inAD);
                    
                }
            }

        }, 'text', true, true, "POST");       
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
                document.getElementById('captcha_id2').src = 'captcha?' + Math.random();
            }    
        },
    'text', false, false, "POST");       
    
}

function fn_inicializaVentanaModal(divId){
    jQuery("#"+divId).draggable({handle: "#dragmodal"});
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

function fu_activaDependencia(pcoDep,pdeDep) {
    try{cerrarConexion_wsocket();}catch(ex){}
    var noForm ="#depForm";
    jQuery(noForm).find('#txtCoDep').val(pcoDep);
    jQuery(noForm).submit();
}

function fu_activaDependencia_dir(pcoDep,pdeDep) {
    try{cerrarConexion_wsocket();}catch(ex){}
    var noForm ="#depFormDir";
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

function fn_mostrar_req_so_old(hpLink) {
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
    hpLink.setAttribute('href',auxUrl);
    return true;
}

var _ws_req = null;
var _onpetradoc_install = 0;
var _ws_url_req = null;
var _ws_url_server_req = null;
var _call_link_req = 0;

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

//function fn_llamar_onpetradoc_install(auxUrl,hpLink){
//    try{
//        console.log(auxUrl);
//        jQuery('#idTradocDesktop_req_so').attr('href', auxUrl);
//        jQuery('#idTradocDesktop_req_so').click();
//        console.log("todo bien..");
//        //hpLink.href = "javascript:void(0);";
//        //alert("aqui");
//        /*var f = document.createElement("form");        
//        f.setAttribute('method',"POST");
//        f.setAttribute('action',auxUrl);
//        if(f.submit()){
//            alert('aqui');
//        }else{
//            console.log("no installed..");
//            alert("no installed..");            
//        }*/
//        /*var myventana=window.open(auxUrl,'_blank');
//        if(!!myventana){
//            console.log("cerrando ventana..");
//            myventana.close();                    
//        }*/
//        /*var form = document.createElement("form");
//        jQuery(form).attr("action", auxUrl).attr("method", "post");
//        jQuery(form).attr("target", "ventanaReq_so");        
//        //var atributos = "toolbar=no,Location=no,directories=no,channelmode=no,menubar=no,status=yes,scrollbars=yes,resizable=yes,width=900,height=600,fullscreen=no,top=100,left=100";        
//        ventanaReq_so = window.open("about:blank", "ventanaReq_so");
//        if (!!ventanaReq_so)
//            ventanaReq_so.close();
//        //console.log("OK");
//        form.submit();*/
//    }catch(ex){
//        console.log("no installed..");
//        //alert("no installed..");
//    }     
//}

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

function fn_mostrar_req_so(hpLink) {
    if(!_call_link_req){
        _call_link_req = 1;
        _onpetradoc_install = 0;

        var auxwsServLoc0 = _ws_url_server_req.substring(0, _ws_url_server_req.indexOf('://') + 3);
        var auxwsServLoc1 = _ws_url_server_req.substring(_ws_url_server_req.indexOf('://') + 3);
        auxwsServLoc1 = auxwsServLoc1.substring(auxwsServLoc1.indexOf('/'));
        _ws_url_req = auxwsServLoc0+document.location.host+auxwsServLoc1;    

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
                p[0] = "accion=goVerRequisitos2";
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
        elem.innerHTML = width * 1 + '%';
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

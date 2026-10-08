var cloneCount = 0;
var ID_DIV_ORIGINAL = "divSelectFecha";

var NAME_RADIO_BUTTON_OPCION = "DPOpcionFecha";
var ID_COMBO_ANIO = "DPAnio";
var ID_COMBO_MES = "DPMes";
var ID_COMBO_DIA = "DPDia";

var ID_INPUT_FECHA_INICIAL = "DPfechaInicial";
var ID_INPUT_FECHA_FINAL = "DPfechaFinal";

var ID_BUTTON_ACEPTAR = "DPAceptar";
var ID_BUTTON_CANCELAR = "DPCancelar";

//seguimos con los cambios

var ID_DIV_FECHA_INICIAL = "divFechaInicial";
var ID_DIV_FECHA_FINAL = "divFechaFinal";

var ID_HIDDEN_FI = "anioDataPickerInicial";
var ID_HIDDEN_FF = "anioDataPickerFinal";

/*
 * Nuevas variables,
 * bellOptions=0, Sin seleccion por defecto
 * bellOptions=1, Hoy
 * bellOptions=2, Ayer
 * bellOptions=3, Mes actual
 * bellOptions=4, Mes pasado
 * bellOptions=5, Año 2014
 * bellOptions=6, Año 2013
 * bellOptions=7, Año/mes
 * bellOptions=8, Rango de fechas
 */
var bellOptions = 0;


function fn_cargarParametrosIniciales() {
    var auxAnioIni = $("#anioDataPickerInicial").val();
    var auxAnioFin = $("#anioDataPickerFinal").val();
    if (!!!auxAnioIni && !!!auxAnioFin) {
        $("#anioDataPickerInicial").val(moment().year() - 1);
        $("#anioDataPickerFinal").val(moment().year());
    }
//    var anioIni = $("#anioDataPickerInicial").val();
//    var anioFin = $("#anioDataPickerFinal").val();
}


function getCloneNum() {
    return cloneCount;
}
function AddCloneNumber() {
    return ++cloneCount;
}

function setDatePicker(pSelectorFechaInicial, pSelectorFechaFinal, numClon) {
    console.log('setDatePicker');
    $(pSelectorFechaInicial).datepicker({
        dateFormat: 'dd/mm/yy',
        dayNames: ["Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"],
        dayNamesMin: ["Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"],
        monthNamesShort: ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
        defaultDate: new Date(),
        changeMonth: true,
        numberOfMonths: 1,
        onSelect: function(selectedDate) {
            $(pSelectorFechaFinal).datepicker("option", "minDate", selectedDate);
            var desde = ID_INPUT_FECHA_INICIAL + numClon;
            var hasta = ID_INPUT_FECHA_FINAL + numClon;
            $("#" + desde).val($(pSelectorFechaInicial).datepicker().val());
            $("#" + hasta).val($(pSelectorFechaFinal).datepicker().val());
        }
    });
    $(pSelectorFechaFinal).datepicker({
        dateFormat: 'dd/mm/yy',
        dayNames: ["Domingo", "Lunes", "Martes", "Miercoles", "Jueves", "Viernes", "Sabado"],
        dayNamesMin: ["Do", "Lu", "Ma", "Mi", "Ju", "Vi", "Sa"],
        monthNamesShort: ["Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"],
        defaultDate: new Date(),
        changeMonth: true,
        numberOfMonths: 1,
        onSelect: function(selectedDate) {
            $(pSelectorFechaInicial).datepicker("option", "maxDate", selectedDate);
            var desde = ID_INPUT_FECHA_INICIAL + numClon;
            var hasta = ID_INPUT_FECHA_FINAL + numClon;
            $("#" + desde).val($(pSelectorFechaInicial).datepicker().val());
            $("#" + hasta).val($(pSelectorFechaFinal).datepicker().val());
        }
    });
}

function cargarDatePicker() {
    ajaxCall("/srDatePicker.do?accion=goCargarDatePicker", '', function(data) {
        refreshScript("divSelectFecha", data);
    }, 'text', false, false, "POST");
}

function getMaxYear(numClon) {
    var maxYear = $("#" + ID_COMBO_ANIO + numClon + " option:eq(0)").val();
    if (!!!maxYear) {
        alert_Danger("ERROR:", "Ocurrio un error inesperado al intentar seleccionar el año");
        return;
    }
    $("#" + ID_COMBO_ANIO + numClon + " option").each(function() {
        var aux = $(this).val();
        if (aux >= maxYear) {
            maxYear = aux;
        }
    });
    return maxYear;
}
function getMinYear(numClon) {
    var minYear = $("#" + ID_COMBO_ANIO + numClon + " option:eq(0)").val();
    if (!!!minYear) {
        alert_Danger("ERROR:", "Ocurrio un error inesperado al intentar seleccionar el año");
        return;
    }
    $("#" + ID_COMBO_ANIO + numClon + " option").each(function() {
        var aux = $(this).val();
        if (aux <= minYear) {
            minYear = aux;
        }
    });
    return minYear;
}
function getAnioSeleccionado(numClon) {
    return $("#" + ID_COMBO_ANIO + numClon).val();
}
function getMesSeleccionado(numClon) {
    return $("#" + ID_COMBO_MES + numClon).val();
}
function getDiaSeleccionado(numClon) {
    return $("#" + ID_COMBO_DIA + numClon).val();
}
function getNumeroDiasMes(numClon) {
    return moment(getAnioSeleccionado(numClon) + "-" + getMesSeleccionado(numClon), "YYYY-MM").daysInMonth();
}
function setNumeroDiasYRangoFechas(numClon) {

    $("#" + ID_COMBO_DIA + numClon + " option").each(function() {
        $(this).remove();
    });

    if ($("#" + ID_COMBO_MES + numClon).val()) {
        for (var i = 1; i <= getNumeroDiasMes(numClon); i++) {
            if (i < 10) {
                i = "0" + i;
            }
            $("#" + ID_COMBO_DIA + numClon).append("<option value='" + i + "' style='width:85px;'>" + i + "</option>");
        }
    }
    $("#" + ID_COMBO_DIA + numClon).append("<option selected='selected' value='' style='width:85px;'>.:TODOS:.</option>");

    var aDia = (!!getDiaSeleccionado(numClon)) ? getDiaSeleccionado(numClon) : "01";
    var aMes = (!!getMesSeleccionado(numClon)) ? getMesSeleccionado(numClon) : "01";
    var aAnio = getAnioSeleccionado(numClon);
    var b = "/";
    var fI = aDia + b + aMes + b + aAnio;

    var bDia = (!!getDiaSeleccionado(numClon)) ? getDiaSeleccionado(numClon) : (!!getMesSeleccionado(numClon)) ? getNumeroDiasMes(numClon) : "31";
    var bMes = (!!getMesSeleccionado(numClon)) ? getMesSeleccionado(numClon) : "12";
    var bAnio = getAnioSeleccionado(numClon);
    var fF = bDia + b + bMes + b + bAnio;

    if (moment(fI, "DD/MM/YYYY").isValid() && moment(fF, "DD/MM/YYYY").isValid()) {
        $("#" + ID_INPUT_FECHA_INICIAL + numClon).val(fI);
        $("#" + ID_INPUT_FECHA_FINAL + numClon).val(fF);
    } else {
        alert_Danger("ERROR:", "Ocurrio un error inesperado al seleccionar la fecha");
    }
}

function setDia(numClon) {
    if (!!getAnioSeleccionado(numClon) && !!getMesSeleccionado(numClon)) {

        var aDia = (!!getDiaSeleccionado(numClon)) ? getDiaSeleccionado(numClon) : "01";
        var aMes = getMesSeleccionado(numClon);
        var aAnio = getAnioSeleccionado(numClon);
        var b = "/";
        var fI = aDia + b + aMes + b + aAnio;

        var bDia = (!!getDiaSeleccionado(numClon)) ? getDiaSeleccionado(numClon) : getNumeroDiasMes(numClon);
        var bMes = getMesSeleccionado(numClon);
        var bAnio = getAnioSeleccionado(numClon);
        var fF = bDia + b + bMes + b + bAnio;

        if (moment(fI, "DD/MM/YYYY").isValid() && moment(fF, "DD/MM/YYYY").isValid()) {
            $("#" + ID_INPUT_FECHA_INICIAL + numClon).val(fI);
            $("#" + ID_INPUT_FECHA_FINAL + numClon).val(fF);
        } else {
            alert_Danger("ERROR:", "Ocurrio un error inesperado al seleccionar la fecha");
        }
    } else {
        alert_Danger("ERROR:", "Ocurrio un error inesperado siempre tiene que estar seleccionado un Año y Mes");
        return;
    }
}
function bloquearFecha(numClon) {
    var opcion = $("input[name='" + NAME_RADIO_BUTTON_OPCION + numClon + "']:checked").val();
    if (!!opcion) {
        if (opcion === "0") {
            bloquearFechasAnioMesDia(false, numClon);
            bloquearFechasRango(true, numClon);
            setNumeroDiasYRangoFechas(numClon);
        }
        if (opcion === "1") {
            bloquearFechasAnioMesDia(true, numClon);
            bloquearFechasRango(false, numClon);
        }
    } else {
        alert_Danger("ERROR:", "Ocurrio un error al seleccionar un valor");
    }
}
function bloquearFechasAnioMesDia(comando, numClon) {
    $("#" + ID_COMBO_ANIO + numClon).prop("disabled", comando);
    $("#" + ID_COMBO_MES + numClon).prop("disabled", comando);
    $("#" + ID_COMBO_DIA + numClon).prop("disabled", comando);
}
function bloquearFechasRango(comando, numClon) {
    $("#" + ID_INPUT_FECHA_INICIAL + numClon).prop("disabled", comando);
    $("#" + ID_INPUT_FECHA_FINAL + numClon).prop("disabled", comando);
}

function removeDataPicker(div, elementoPadre) {
    jQuery(document.body).mouseup(function(e)
    {
        var searchcontainer = jQuery(div);

        if (!searchcontainer.is(e.target) // if the target of the click isn't the container...
                && searchcontainer.has(e.target).length === 0) // ... nor a descendant of the container
        {
            searchcontainer.fadeOut("slow", function() {
                $(elementoPadre).removeAttr("isActive");
//                $(elementoPadre).prop("isActive", "false");
                searchcontainer.remove();
            });
        }
    });
}


jQuery.fn.showDatePicker = function(options)
{
    //defaultOpcionSelected= 5 , estoy seleccionando por defecto el año actual
    optionsDefault = {
        defaultOpcionSelected: 5,
        showDia: true,
        selectTodosMeses: false,
        pressAceptarEvent: function() {

        }
    };
    var opciones = jQuery.extend(optionsDefault, options);
    var anioIni = $("#anioDataPickerInicial").val();
    var anioFin = $("#anioDataPickerFinal").val();
    var that = this;


    var escribirAtributosCajaFecha = function(fIni, fFin, option) {
        if (!!!$(that).attr("optSelected")) {
            $("#DPfechaInicial").val(fIni);
            $("#DPfechaFinal").val(fFin);
        }
        $(that).attr("fIni", fIni);
        $(that).attr("fFin", fFin);
        $(that).attr("optSelected", option);
    };

    var darFormatoFechaACadena = function(fIni, fFin, tipo) {
        var salida = "";
        //tipo 1 -> Del: 17/02/2014 Al: 17/02/2014
        //tipo 2 -> Año: 2014 Mes: Febrero
        //tipo 3 -> Año: 2013
        switch (tipo)
        {
            case 1:
                salida = moment(fIni, "DD/MM/YYYY").format(" [Del:] DD/MM/YYYY ");
                salida = salida + moment(fFin, "DD/MM/YYYY").format(" [Al:] DD/MM/YYYY ");
                break;
            case 2:
                salida = moment(fIni, "DD/MM/YYYY").format(" [Año:] YYYY [Mes:] MMMM ");
                break;
            case 3:
                salida = moment(fIni, "DD/MM/YYYY").format(" [Año:] YYYY ");
                break;
            //YUAL
            case 4:
               // salida = moment(fIni, "DD/MM/YYYY").format(" [Del:] DD/MM/YYYY ");
                //salida = salida + moment(fFin, "DD/MM/YYYY").format(" [Al:] DD/MM/YYYY ");
                salida="TODOS";
                break;
            default:
                salida = moment(fIni, "DD/MM/YYYY").format(" [Del:] DD/MM/YYYY ");
                salida = salida + moment(fFin, "DD/MM/YYYY").format(" [Al:] DD/MM/YYYY ");
        }
        return salida;
    };
    var seleccinarOpcion = function(opcion) {
        var textoFinal = "";
        var hoy = moment().format("DD/MM/YYYY");
        var ayer = moment().subtract('days', 1).format("DD/MM/YYYY");
        var mesAnterior = moment().subtract('month', 1).format("DD/MM/YYYY");
        var hace30Dias = moment().subtract('days', 30).format("DD/MM/YYYY");
        var anioPasado = moment().subtract('year', 1).format("DD/MM/YYYY");
        var inicioPasado =("01/01/2017");
        switch (opcion) {
            case 1:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 1);//hoy
                escribirAtributosCajaFecha(hoy, hoy, opcion);
                break;
            case 2:
                textoFinal = darFormatoFechaACadena(ayer, ayer, 1);//ayer
                escribirAtributosCajaFecha(ayer, ayer, opcion);
                break;
            case 3:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 2);//Mes actual
                var starOfMoth = moment().startOf('month').format("DD/MM/YYYY");
                var endOfMoth = moment().endOf('month').format("DD/MM/YYYY");
                escribirAtributosCajaFecha(starOfMoth, endOfMoth, opcion);
                break;
            case 4:
                textoFinal = darFormatoFechaACadena(hace30Dias, hoy, 1);//Hace 30 Dias
                escribirAtributosCajaFecha(hace30Dias, hoy, opcion);
                break;
            case 5:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 3);//Año 2014,current year
                var starOfYear = moment().startOf('year').format("DD/MM/YYYY");
                var endOfYear = moment().endOf('year').format("DD/MM/YYYY");
                escribirAtributosCajaFecha(starOfYear, endOfYear, opcion);
                break;
            case 6:
                textoFinal = darFormatoFechaACadena(anioPasado, anioPasado, 3);//Año 2013,pass year
                var startOfLastYear = moment().startOf('year').subtract('year', 1).format("DD/MM/YYYY");
                var endOfLastYear = moment().endOf('year').subtract('year', 1).format("DD/MM/YYYY");
                escribirAtributosCajaFecha(startOfLastYear, endOfLastYear, opcion);
                break;
            case 7:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 2);
                break;
            case 8:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 1);
                break;
            //YUAL
            case 9:  textoFinal = darFormatoFechaACadena(inicioPasado, hoy, 4);//Año 2014,current year
                var starOfYear = moment().startOf('year').format("DD/MM/YYYY");
                var endOfYear = moment().endOf('year').format("DD/MM/YYYY");
                escribirAtributosCajaFecha(inicioPasado, hoy, opcion);
                break;
            default:
                textoFinal = darFormatoFechaACadena(hoy, hoy, 1);
                escribirAtributosCajaFecha(hoy, hoy, 1);
        }
        $(that).find("[name='labelDate']").text(textoFinal);

    };
    seleccinarOpcion(optionsDefault.defaultOpcionSelected);

    this.bind("click.showDatePicker", function() {
        //alert(e.handled);
        //this.on("click", function() {        
        var aSefaultOpcionSelected;
        if (!!$(this).attr("optSelected"))
        {
            aSefaultOpcionSelected = parseInt($(this).attr("optSelected"));
        } else {
            aSefaultOpcionSelected = parseInt(opciones.defaultOpcionSelected);
        }

        var aShowDia = opciones.showDia;
        var aSelectTodosMeses = opciones.selectTodosMeses;


        //mejorar este codigo para que funcione una sola vez.
        //con el clon del id del div 

        var elementoPadre = this;
        var clonId;
        var numClon;

        var rbOp;
        var fIni;
        var fFin;

        if (!!!$(this).attr("numClon"))
        {
            numClon = AddCloneNumber();
            clonId = ID_DIV_ORIGINAL + numClon;
            $(this).attr("numClon", numClon);
        } else {
            numClon = $(this).attr("numClon");
            clonId = ID_DIV_ORIGINAL + numClon;
        }
//        var esActivo=$(this).attr("isActive");
//        
        if (!!$(this).attr("isActive")) {
            //console.log("is also CREATED");
            return;
        } else {
            $(this).attr("isActive", "true");
        }

        var pos = $(this).position();
        var zindex = $(this).zIndex();

        var nuevoDP = $("#" + ID_DIV_ORIGINAL).clone().attr("id", clonId).insertAfter(this).css({
            position: "absolute",
            top: (pos.top + 32) + "px",
            left: (pos.left) + "px",
            "z-index": zindex + 1
        });
        var nuevoIDFechaIni = ID_INPUT_FECHA_INICIAL + numClon;
        var nuevoIDFechaFin = ID_INPUT_FECHA_FINAL + numClon;

        var nuevoNameRadioButton = NAME_RADIO_BUTTON_OPCION + numClon;
        var nuevoIDComboAnio = ID_COMBO_ANIO + numClon;
        var nuevoIDComboMes = ID_COMBO_MES + numClon;
        var nuevoIDComboDia = ID_COMBO_DIA + numClon;

        var nuevoIDBotonAceptar = ID_BUTTON_ACEPTAR + numClon;
        var nuevoIDBotonCancelar = ID_BUTTON_CANCELAR + numClon;

        var nuevoID_HIDDEN_FI = ID_HIDDEN_FI + numClon;
        var nuevoID_HIDDEN_FF = ID_HIDDEN_FF + numClon;

        var nuevoID_DIV_FECHA_INICIAL = ID_DIV_FECHA_INICIAL + numClon;
        var nuevoID_DIV_FECHA_FINAL = ID_DIV_FECHA_FINAL + numClon;



        nuevoDP.find("[name='" + NAME_RADIO_BUTTON_OPCION + "']").attr("name", nuevoNameRadioButton);
        nuevoDP.find("#" + ID_COMBO_ANIO).attr("id", nuevoIDComboAnio);
        nuevoDP.find("#" + ID_COMBO_MES).attr("id", nuevoIDComboMes);
        nuevoDP.find("#" + ID_COMBO_DIA).attr("id", nuevoIDComboDia);

        nuevoDP.find("#" + ID_INPUT_FECHA_INICIAL).attr("id", nuevoIDFechaIni);
        nuevoDP.find("#" + ID_INPUT_FECHA_FINAL).attr("id", nuevoIDFechaFin);

        nuevoDP.find("#" + ID_BUTTON_ACEPTAR).attr("id", nuevoIDBotonAceptar);
        nuevoDP.find("#" + ID_BUTTON_CANCELAR).attr("id", nuevoIDBotonCancelar);

        nuevoDP.find("[name='" + nuevoNameRadioButton + "']").attr("onclick", "bloquearFecha(" + numClon + ")");
        nuevoDP.find("#" + nuevoIDComboAnio).attr("onchange", "setNumeroDiasYRangoFechas(" + numClon + ")");
        nuevoDP.find("#" + nuevoIDComboMes).attr("onchange", "setNumeroDiasYRangoFechas(" + numClon + ")");
        nuevoDP.find("#" + nuevoIDComboDia).attr("onchange", "setDia(" + numClon + ")");

        nuevoDP.find("#" + ID_HIDDEN_FI).attr("id", nuevoID_HIDDEN_FI);
        nuevoDP.find("#" + ID_HIDDEN_FF).attr("id", nuevoID_HIDDEN_FF);

        nuevoDP.find("#" + ID_DIV_FECHA_INICIAL).attr("id", nuevoID_DIV_FECHA_INICIAL);
        nuevoDP.find("#" + ID_DIV_FECHA_FINAL).attr("id", nuevoID_DIV_FECHA_FINAL);

//        rbOp = $(this).attr("rbOp");
//        fIni = $(this).attr("fIni");
//        fFin = $(this).attr("fFin");


        if (!!fIni && !!fFin) {
            $("#" + nuevoIDFechaIni).val(fIni);
            $("#" + nuevoIDFechaFin).val(fFin);
        }

        if (!aShowDia) {
            $("#" + nuevoIDComboDia).parent().parent().hide();
        }
        if (aSelectTodosMeses) {
            $("#" + nuevoIDComboMes + " option:eq(12)").prop("selected", "selected");
            setNumeroDiasYRangoFechas(numClon);
        }

        if (!!rbOp && !!fIni && !!fFin) {
            if (rbOp === "0") {
                nuevoDP.find("[name='" + nuevoNameRadioButton + "']:eq(0)").prop("checked", true);
                nuevoDP.find("[name='" + nuevoNameRadioButton + "']:eq(0)").attr("checked", true);
            }
            if (rbOp === "1") {
                nuevoDP.find("[name='" + nuevoNameRadioButton + "']:eq(1)").prop("checked", true);
                nuevoDP.find("[name='" + nuevoNameRadioButton + "']:eq(1)").attr("checked", true);
            }
        }
        setDatePicker("#" + nuevoID_DIV_FECHA_INICIAL, "#" + nuevoID_DIV_FECHA_FINAL, numClon);
        //bloquearFecha(numClon);//MODIFICAR ADECUAR


//        nuevoDP.show(function() {
//            removeDataPicker("#" + clonId,elementoPadre);
//        });


        if (!!rbOp && !!fIni && !!fFin) {
            $("#" + nuevoIDFechaIni).attr("value", fIni);
            $("#" + nuevoIDFechaIni).prop("value", fIni);
            $("#" + nuevoIDFechaFin).attr("value", fFin);
            $("#" + nuevoIDFechaFin).prop("value", fFin);
        }
//        var anioMaximo = $("#" + nuevoID_HIDDEN_FF).val();
        var configuarAniosEnListaOpciones = function(opcion) {
            var configurarNombreAniosEnListaOpciones = function() {
                $("#" + clonId + " .selectContent ul li").each(function() {
                    var anioActual = moment().format("YYYY");
                    var anioPasado = anioActual - 1;
                    var index = $(this).index();
                    if (index === 4) {
                        $(this).text("Año " + anioActual);
                    }
                    if (index === 5) {
                        $(this).text("Año " + anioPasado);
                    }
                    if ((opcion - 1) === index) {
                        $(this).addClass("active");
                    }
                });
            };
            configurarNombreAniosEnListaOpciones();
        };
        configuarAniosEnListaOpciones(aSefaultOpcionSelected);
        if (aSefaultOpcionSelected === 7) {
            $("#" + clonId + " div .anioMes").show();
        }
        if (aSefaultOpcionSelected === 8) {
            $("#" + clonId + " div .rangoFechas").show();
        }

        var cerrarDiv = function() {
            $(elementoPadre).removeAttr("isActive");
            nuevoDP.remove();
        };


        var setFechaAlComboBox = function(fecha) {
            var anio = moment(fecha, "DD/MM/YYYY").format("YYYY");
            var mes = moment(fecha, "DD/MM/YYYY").format("MM");
            $("#" + nuevoIDComboAnio + " option[value='" + anio + "']").prop('selected', true);
            $("#" + nuevoIDComboMes + " option[value='" + mes + "']").prop('selected', true);
        };

        var auxOptSelected;
        var setInputsDates = function(ini, fin) {
            $("#" + nuevoIDFechaIni).val(ini);
            $("#" + nuevoIDFechaFin).val(fin);
        };

        var esAnioCompleto = function(ini, fin) {
            var starOfYear = moment(ini, "DD/MM/YYYY").startOf('year').format("DD/MM/YYYY");
            var endOfYear = moment(fin, "DD/MM/YYYY").endOf('year').format("DD/MM/YYYY");
            if (ini === starOfYear && fin === endOfYear) {
                return true;
            }
            return false;
        };

        var configInicialDataPicker = function() {
            var ini = $(elementoPadre).attr("fIni");
            var fin = $(elementoPadre).attr("fFin");
            setInputsDates(ini, fin);
            if (aSefaultOpcionSelected === 8) {
                $("#" + clonId).css({width: "631px"});
                $("#" + nuevoID_DIV_FECHA_INICIAL).datepicker("setDate", ini);
                $("#" + nuevoID_DIV_FECHA_FINAL).datepicker("setDate", fin);
                //alert("s");
            }
            if (aSefaultOpcionSelected === 7) {
                $("#" + clonId).css({width: "338px"});
//                var starOfYear = moment(ini, "DD/MM/YYYY").startOf('year').format("DD/MM/YYYY");
//                var endOfYear = moment(fin, "DD/MM/YYYY").endOf('year').format("DD/MM/YYYY");
                setFechaAlComboBox(ini);
//                if (ini === starOfYear && fin === endOfYear) {
//                    $("#" + nuevoIDComboMes + " option[value='']").prop('selected', true);//.:TODOS:.
//                }
                if (esAnioCompleto(ini, fin)) {
                    $("#" + nuevoIDComboMes + " option[value='']").prop('selected', true);//.:TODOS:.
                }
            }
        };
        configInicialDataPicker();

        var onClickListaOpciones = function() {
            $("#" + clonId + " .selectContent ul li").click(function() {
                $(".selectContent ul li").each(function() {
                    $(this).removeClass("active");
                });
                var currentOption = $(this).index() + 1;
                $(this).addClass("active");
                if (currentOption === 7 || currentOption === 8) {
                    if (currentOption === 7) {
                        //año/mes
                        $("#" + clonId + " div .anioMes").show();
                        $("#" + clonId + " div .rangoFechas").hide();
                        $("#" + clonId).css({width: "338px"});

                        if (aSefaultOpcionSelected === currentOption) {
                            //seleccionar el mes previamente selecciodo
                            //var hoy = moment().format("DD/MM/YYYY");
                            var ini = $(elementoPadre).attr("fIni");
                            setFechaAlComboBox(ini);
                        } else {
                            //no hubo seleccion seleccionar el mes actual y fecha actual
                            //listo para el aceptar
                            var hoy = moment().format("DD/MM/YYYY");
                            var starOfMoth = moment().startOf('month').format("DD/MM/YYYY");
                            var endOfMoth = moment().endOf('month').format("DD/MM/YYYY");
                            setFechaAlComboBox(hoy);
                            setInputsDates(starOfMoth, endOfMoth);
                        }
                    }
                    if (currentOption === 8) {
                        //rango de fechas
                        $("#" + clonId + " div .anioMes").hide();
                        $("#" + clonId + " div .rangoFechas").show();
                        $("#" + clonId).css({width: "631px"});

                        var hoy = moment().format("DD/MM/YYYY");
                        setInputsDates(hoy, hoy);
                    }
                    if (currentOption === 9) {
                        //rango de fechas
                        $("#" + clonId + " div .anioMes").hide();
                        $("#" + clonId + " div .rangoFechas").show();
                        $("#" + clonId).css({width: "631px"});

                        var hoy = moment().format("DD/MM/YYYY");
                        setInputsDates(hoy, hoy);
                    }
                    auxOptSelected = currentOption;
                } else {
                    seleccinarOpcion(currentOption);
                    cerrarDiv();
                }
            });
        };
        onClickListaOpciones();
        $("#" + nuevoIDBotonAceptar).click(function() {
            var auxFi = $("#" + nuevoIDFechaIni).val();
            var auxFf = $("#" + nuevoIDFechaFin).val();
            $(elementoPadre).attr("fIni", auxFi);
            $(elementoPadre).attr("fFin", auxFf);
            if (!!!auxOptSelected) {
                auxOptSelected = aSefaultOpcionSelected;
            }
            escribirAtributosCajaFecha(auxFi, auxFf, auxOptSelected);
            var tipoSalida = 1;
            if (auxOptSelected === 7) {
                if (esAnioCompleto(auxFi, auxFf)) {
                    tipoSalida = 1;
                } else {
                    tipoSalida = 2;
                }
            }
            var cadenaFecha = darFormatoFechaACadena(auxFi, auxFf, tipoSalida);
            $(elementoPadre).find("[name='labelDate']").text(cadenaFecha);
            cerrarDiv();
            return;
        });
        $("#" + nuevoIDBotonCancelar).click(function() {
            cerrarDiv();
        });
        nuevoDP.show(function() {
            removeDataPicker("#" + clonId, elementoPadre);
        });
        return this;
    });
};
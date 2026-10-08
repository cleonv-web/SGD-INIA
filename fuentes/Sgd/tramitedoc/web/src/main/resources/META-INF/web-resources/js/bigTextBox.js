/*
 * Jquery pluggin
 * name:  bigTextBox
 * version:  1.1
 * lastUpdate: 17/12/2013
 */
var cloneBTCount = 0;
var ID_BIG_TEXTAREA = "txtBigText";
var ID_BTN_ACEPTAR = "BTAceptar";
var ID_BTN_CANCELAR = "BTCancelar";
var ID_LABEL_CARACTERES = "BTLabelCaracteres";

function AddCloneBTNumber() {
    return ++cloneBTCount;
}

var ID_DIV_BIG_TEXT = "divCajaTexto";


jQuery.fn.showBigTextBox = function(options)
{
    optionsDefault = {
        maxNumCar: 1000,
        pressAceptarEvent: function() {

        }
    };
    var opciones = jQuery.extend(optionsDefault, options);

    this.bind("dblclick.showBigTextBox", function() {
        var parent = $(this);
        var clonId;
        var numClon;
        var maxNumCar = opciones.maxNumCar;
        var numCarDefaulText;

        if (!!!$(this).attr("numClonBT"))
        {
            numClon = AddCloneBTNumber();
            clonId = ID_DIV_BIG_TEXT + numClon;
            $(this).attr("numClonBT", numClon);
        } else {
            numClon = $(this).attr("numClonBT");
            clonId = ID_DIV_BIG_TEXT + numClon;
        }

        var nuevoBT = $("#" + ID_DIV_BIG_TEXT).clone().attr("id", clonId);
        var nuevoIDTextArea = ID_BIG_TEXTAREA + clonId;
        var nuevoIDBotonAceptar = ID_BTN_ACEPTAR + clonId;
        var nuevoIDBotonCancelar = ID_BTN_CANCELAR + clonId;
        var nuevoIDLabelCaracteres = ID_LABEL_CARACTERES + clonId;

        nuevoBT.find("#" + ID_BIG_TEXTAREA).attr("id", nuevoIDTextArea);
        nuevoBT.find("#" + ID_BTN_ACEPTAR).attr("id", nuevoIDBotonAceptar);
        nuevoBT.find("#" + ID_BTN_CANCELAR).attr("id", nuevoIDBotonCancelar);
        nuevoBT.find("#" + ID_LABEL_CARACTERES).attr("id", nuevoIDLabelCaracteres);

        $.blockUI({
            message: nuevoBT,
            css: {
                border: 'none',
                padding: '0px',
                backgroundColor: '',
                color: '',
                position: 'relative'
            },
            overlayCSS: {
                backgroundColor: 'black',
                opacity: 0.10
            }
        });

        var parentVal = $(this).val();
        if (!!parentVal) {
            $("#" + nuevoIDTextArea).val(parentVal);
        }
        numCarDefaulText = (!!parentVal.length) ? parentVal.length : 0;

        var label = "N° de caracteres " + numCarDefaulText + " de <b>" + maxNumCar + "</b>";
        $("#" + nuevoIDLabelCaracteres).html(label);

        var readOnly = $(this).attr("readonly");
        $("#" + nuevoIDTextArea).keyup(function() {
            if (readOnly) {
                return;
            }
            var auxVal = $("#" + nuevoIDTextArea).val();
            var numCurrentCar = (!!auxVal.length) ? auxVal.length : 0;
            var label;
            if (numCurrentCar <= maxNumCar) {
                label = "N° de caracteres " + numCurrentCar + " de <b>" + maxNumCar + "</b>";

            } else {
                label = "<span style='color:red'>Superó Nro Max de car " + numCurrentCar + " de <b>" + maxNumCar + "</b></span>";
            }
            $("#" + nuevoIDLabelCaracteres).html(label);
        });
        $("#" + nuevoIDTextArea).keypress(function() {
            if (readOnly) {
                return;
            }
            var auxVal = $("#" + nuevoIDTextArea).val();
            var numCurrentCar = (!!auxVal.length) ? auxVal.length : 0;
            var label;
            if (numCurrentCar <= maxNumCar) {
                label = "N° de caracteres " + numCurrentCar + " de <b>" + maxNumCar + "</b>";

            } else {
                label = "<span style='color:red'>Superó Nro Max de car " + numCurrentCar + " de <b>" + maxNumCar + "</b></span>";
            }
            $("#" + nuevoIDLabelCaracteres).html(label);
        });
        if (!!readOnly) {
            readOnly = true;
            $("#" + nuevoIDTextArea).attr("readonly", "readonly");
            $("#" + nuevoIDLabelCaracteres).append("<br/><span style='color:#FF0000'>(texto solo lectura)");
            $("#" + nuevoIDTextArea).css({color:"#7A7A7A"});
        } else {
            readOnly = false;
        }
        if (readOnly === false)
        {
            $("#" + nuevoIDBotonAceptar).click(function() {
                var auxVal = $("#" + nuevoIDTextArea).val();
                if (auxVal.trim() === "") {
                    alert_Danger("!Error","Caracteres Incorrectos");
                    return false;
                }

                var numCurrentCar = (!!auxVal.length) ? auxVal.length : 0;
                if (numCurrentCar <= maxNumCar) {

                    var aceptarParam = {originalText: parentVal, currentText: auxVal, hasChangedText: (parentVal === auxVal) ? false : true, currentObject: parent};
                    $(parent).val(auxVal);
                    opciones.pressAceptarEvent.call(undefined, aceptarParam);

                    $.unblockUI();
                    nuevoBT.remove();
                    return false;
                } else {
                    label = "<span style='color:red'>Superó Nro Max de car " + numCurrentCar + " de <b>" + maxNumCar + "</b></span>";
                    $("#" + nuevoIDLabelCaracteres).html(label);
                }
            });
        }

        $("#" + nuevoIDBotonCancelar).click(function() {
            $.unblockUI();
            nuevoBT.remove();
            return false;
        });
        return this;
    });
}
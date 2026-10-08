/// Copyright (c) 2017.
/// All rights reserved.
/// <summary>
/// Controlador de progreso de carga o peticiones
/// </summary>
/// <remarks>
/// Creacion: 	WECM 14062017 <br />
/// </remarks>
ns('Gob.Pcm.UI.Web.Components.Dialog');
Gob.Pcm.UI.Web.Components.Dialog = function (opts) {
    this.init(opts);
};

Gob.Pcm.UI.Web.Components.Dialog.prototype = {
    idDivDialog: 'divDialog_Pcm',
    noCloseButtonClass: 'no-close-dialog',
    init: function (opts) {
        opts.show = opts.show == undefined ? true : opts.show;
        opts.hide = opts.hide == undefined ? true : opts.hide;
        
        opts.height = opts.height == undefined ? undefined : opts.height;
        opts.minHeight = opts.minHeight == undefined ? undefined : opts.minHeight;
        opts.maxHeight = opts.maxHeight == undefined ? 650 : opts.maxHeight;
        

        opts.width = opts.width == undefined ? '50%' : opts.width;
        opts.minWidth = opts.minWidth == undefined ? 320 : opts.minWidth;
        opts.maxWidth = opts.maxWidth == undefined ? 1024 : opts.maxWidth;

        this._privateFunction.createDialog.apply(this, [opts]);
        if (opts != null && opts.onClose != null && opts.onClose)
            this.onClose = opts.onClose;
        if (opts != null && opts.onDestroy != null && opts.onDestroy)
            this.onDestroy = opts.onDestroy;
    },

    setCloseOnEscape: function (value) {
        if (this.divDialog) {
            this.divDialog.progressbar('option', 'closeOnEscape', value);
        }
    },
    setCloseText: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'closeText', value);
        }
    },
    setDraggable: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'draggable', value);
        }
    },
    setHeight: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'height', value);
        }
    },
    setWidth: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'width', value);
        }
    },
    setMaxHeight: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'maxHeight', value);
        }
    },
    setMaxWidth: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'maxWidth', value);
        }
    },
    setMinHeight: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'minHeight', value);
        }
    },
    setMinWidth: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'minWidth', value);
        }
    },
    setModal: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'modal', value);
        }
    },
    setPosition: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'position', value);
        }
    },
    setResizable: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'resizable', value);
        }
    },
    setTitle: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'title', value);
        }
    },
    setClass: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'dialogClass', value);
        }
    },
    setButtons: function (value) {
        if (this.divDialog) {
            this.divDialog.dialog('option', 'buttons', value);
        }
    },

    getMainContainer: function () {
        return this.divDialog;
    },
    getHeight: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'height');
        }
        return value;
    },
    getWidth: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'width');
        }
        return value;
    },
    getPosition: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'position');
        }
        return value;
    },
    getTitle: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'title');
        }
        return value;
    },
    isModal: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'modal');
        }
        return value;
    },
    isResizable: function (value) {
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('option', 'resizable');
        }
        return value;
    },
    isOpen: function (value) {
        $.datepicker._hideDatepicker();
        var value = undefined;
        if (this.divDialog) {
            value = this.divDialog.dialog('isOpen');
        }
        return value;
    },

    close: function () {
        if (this.divDialog && this.isOpen()) {
            this.divDialog.dialog('close');
            if (this.onClose != null)
                this.onClose();
        }
    },
    open: function () {
        if (this.divDialog && !this.isOpen()) {
            if (navigator.userAgent.indexOf('Chrome') != -1) {
                $(window).scrollTop(0);
                $("body").scrollTop(0);
            }
            this.divDialog.dialog('open');
            $("body").css("overflow", "hidden");
            $('.ui-dialog-titlebar-close').prop('title', '');
            $('.ui-dialog').css('overflow', 'visible');
            $('.ui-dialog').css('z-index', '1000');
        }
    },
    moveToTop: function () {
        if (this.divDialog) {
            this.divDialog.dialog('moveToTop');
        }
    },

    setContent: function (html) {
        if (this.divDialog) {
            this.divDialog.html(html);
        }
    },

    getAjaxContent: function (opts) {
        var me = this;
        var ajaxBuscar = new Gob.Pcm.UI.Web.Components.Ajax({
            action: opts.action,
            dataType: 'html',
            data: opts.data,
            //updatePanel: me.divDialog[0].id,
            onSuccess: function (html) {
                me.setContent(html);
                Gob.Pcm.UI.Web.Components.TextBox.Function.Configure(me.idDivDialog);
                opts.autoOpen = (opts.autoOpen == undefined || opts.autoOpen == null) ? true : opts.autoOpen;
                if (opts.autoOpen) {
                    me.open();
                    if (opts.afterOpen) {
                        opts.afterOpen.apply(opts.scope ? opts.scope : me, [html, opts.customParam]);
                    }
                }
                if (Gob.Pcm.UI.Web.Components.Util.RemoveDrop) {
                    Gob.Pcm.UI.Web.Components.Util.RemoveDrop();
                }
                setTimeout(function () {
                    if (opts.onSuccess) {
                        opts.onSuccess.apply(opts.scope ? opts.scope : me, [html, opts.customParam]);
                    }
                }, 100)
            }
        });
    },

    destroy: function () {
        if (this.divDialog) {
            this.divDialog.dialog('destroy');
            if (this.onDestroy != null)
                this.onDestroy();
            this.divDialog.remove();
        }
    },

    _privateFunction: {
        createDialog: function (opts) {
            if (!opts.target || opts.target == '') {
                this.divDialog = this._privateFunction.implementDialogElement.apply(this, [opts]);
            }
            else {
                this.divDialog = $('#' + opts.target);
            }
            this.divDialog.dialog(this._privateFunction.getConfig.apply(this, [opts]));
            if (opts.maxHeight != undefined || opts.height != undefined) {
                this.divDialog.css('overflow-x', 'hidden');
            }

        },
        implementDialogElement: function (opts) {
            var div = $('<div />').uniqueId();
            this.idDivDialog = this.idDivDialog + div.attr('id');
            div.attr('id', this.idDivDialog);
            $('body').append(div);
            return div;
        },
        getConfig: function (opts) {

            var config = {
                autoOpen: opts.autoOpen ? opts.autoOpen : false,
                modal: opts.modal ? opts.modal : true,
                resizable: opts.resizable ? opts.resizable : false,
                show: { effect: "clip", duration: 400 },
                hide: { effect: "clip", duration: 50 },
                close: function () { $("body").css("overflow", "auto"); }
            };

            if (opts) {
                for (var property in opts) {
                    config[property] = opts[property];
                }
                if (opts.showCloseButton == false) {
                    config.dialogClass = config.dialogClass ? config.dialogClass + ' ' + this.noCloseButtonClass : this.noCloseButtonClass;
                }
                if (opts.close && opts.close != null) {
                    config.close = function () {
                        $("body").css("overflow", "auto");
                        opts.close();
                    };
                }
            }
            return config;
        }
    }
};

ns('Gob.Pcm.UI.Web.Common.Resources')
Gob.Pcm.UI.Web.Common.Resources.LabelInformation = 'Información';
Gob.Pcm.UI.Web.Common.Resources.LabelAceptConfirmation = 'Aceptar';
Gob.Pcm.UI.Web.Common.Resources.LabelCancelConfirmation = 'Cancelar';
Gob.Pcm.UI.Web.Common.Resources.LabelWarning = 'Advertencia';

/// Copyright (c) 2017.
/// All rights reserved.
/// <summary>
/// Controlador de Mensajes
/// </summary>
/// <remarks>
/// Creacion: 	WECM 14092017 <br />
/// </remarks>
ns('Gob.Pcm.UI.Web.Components.Message');
Gob.Pcm.UI.Web.Components.Message = function (opts) {
    this.init(opts);
};

Gob.Pcm.UI.Web.Components.Message.prototype = {
    init: function (opts) {
        this._privateFunction.createMessage.apply(this, [opts]);
        ResultConfirm = false;
    },

    Information: function (opts) {
        opts.dialogClass = 'message-dialog-information';
        opts.position = { my: "right top", at: "right top", of: window };
        opts.title = opts.title ? opts.title : Gob.Pcm.UI.Web.Common.Resources.LabelInformation;
        //opts.title = '<strong>' + opts.title + ' : </strong>'
        opts.title =  opts.title + ' :'
        opts.message = opts.title + opts.message;
        opts.message = '<div class="alert alert-success">' + opts.message + '</div>';
        opts.modal = false;
        opts.minWidth = 550;
        opts.minHeight = 60;
        this._privateFunction.show.apply(this, [opts]);
        if (this.divDialog) {
            var me = this;
            this.informationTimeOut = setTimeout(function () {
                me.divDialog.close();
            }, 2500);
        }
    },

    InformationCustom: function (opts) {
        var me = this;
        opts.dialogClass = 'message-dialog-informationCustom';
        opts.position = { my: "right top", at: "right top", of: window };
        opts.title = opts.title ? opts.title : Gob.Pcm.UI.Web.Common.Resources.LabelInformation;
        opts.message = '<div class="cont-critico"><span class="' + opts.classimage + '"></span><span class="texto-item">' + opts.message + '</span></div>';
        opts.modal = false;
        this._privateFunction.show.apply(this, [opts]);
        if (this.divDialog) {
            var me = this;
            this.informationTimeOut = setTimeout(function () {
                me.divDialog.close();
            }, 2500);
        }
    },

    ResultConfirm: false,

    Confirmation: function (opts) {
        var me = this;
        opts.dialogClass = 'message-dialog-confirmation';
        opts.title = opts.title ? opts.title : Gob.Pcm.UI.Web.Common.Resources.LabelConfirmation;
        opts.buttons = [
                            {
                                text: opts.textConfirmar ? opts.textConfirmar : Gob.Pcm.UI.Web.Common.Resources.LabelAceptConfirmation,
                                'class': 'btn btn-primary',
                                click: function () {
                                    ResultConfirm = true;
                                    if (me.divDialog) {
                                        me.divDialog.close();
                                    }
                                    setTimeout(function () {
                                        if (opts.onConfirm) {
                                            opts.onConfirm(true);
                                        }
                                        if (opts.onAccept) {
                                            opts.onAccept();
                                        }
                                    }, 100);
                                }
                            },
                            {
                                text: opts.textCancelar ? opts.textCancelar : Gob.Pcm.UI.Web.Common.Resources.LabelCancelConfirmation,
                                'class': 'btn btn-info',
                                click: function () {
                                    if (me.divDialog) {
                                        me.divDialog.close();
                                    }
                                    setTimeout(function () {
                                        if (opts.onConfirm) {
                                            opts.onConfirm(false);
                                        }
                                        if (opts.onCancel) {
                                            opts.onCancel();
                                        }
                                    }, 100);

                                }
                            }
        ];
        this._privateFunction.show.apply(this, [opts]);
    },

    ConfirmationCustom: function (opts) {
        var me = this;
        opts.dialogClass = 'message-dialog-confirmation';
        opts.title = opts.title ? opts.title : Gob.Pcm.UI.Web.Common.Resources.LabelConfirmation;
        opts.buttons = [
                            {
                                text: opts.textConfirmar ? opts.textConfirmar : Gob.Pcm.UI.Web.Common.Resources.LabelAceptConfirmation,
                                'class': 'btn btn-primary',
                                click: function () {
                                    ResultConfirm = true;
                                    if (me.divDialog) {
                                        me.divDialog.close();
                                    }
                                    setTimeout(function () {
                                        if (opts.onConfirm) {
                                            opts.onConfirm(true);
                                        }
                                        if (opts.onAccept) {
                                            opts.onAccept();
                                        }
                                    }, 100);
                                }
                            },
                            {
                                text: opts.textCancelar ? opts.textCancelar : Gob.Pcm.UI.Web.Common.Resources.LabelCancelConfirmation,
                                'class': 'btn btn-info',
                                click: function () {
                                    if (me.divDialog) {
                                        me.divDialog.close();
                                    }
                                    setTimeout(function () {
                                        if (opts.onConfirm) {
                                            opts.onConfirm(false);
                                        }
                                        if (opts.onCancel) {
                                            opts.onCancel();
                                        }
                                    }, 100);

                                }
                            }
        ];
        opts.message = '<div class="cont-critico"><span class="' + opts.classimage + '"></span><span class="texto-item">' + opts.message + '</span></div>';
        this._privateFunction.show.apply(this, [opts]);
    },

    Warning: function (opts) {
        opts.dialogClass = 'message-dialog-warning';
        opts.title = opts.title ? opts.title : Gob.Pcm.UI.Web.Common.Resources.LabelWarning;
        opts.message = '<div class="alert alert-warning">' + opts.message + '</div>';
        this._privateFunction.show.apply(this, [opts]);
    },

    Error: function (opts) {
        opts.dialogClass = 'message-dialog-error';
        opts.title = opts.title ? opts.title : 'Error';
        opts.message = '<div class="alert alert-danger">' + opts.message + '</div>';
        this._privateFunction.show.apply(this, [opts]);
    },

    Basic: function (opts) {
        var me = this;
        opts.dialogClass = 'message-dialog';
        opts.title = opts.title ? opts.title : '';
        opts.message = '<div>' + opts.message + '</div>';
        this._privateFunction.show.apply(this, [opts]);
        return me;
    },

    setMessage: function (message) {
        this.divDialog.setContent(message);
    },

    onClose: null,

    destroy: function () {
        if (this.divDialog) {
            this.divDialog.destroy();
        }
    },
    _privateFunction: {
        createMessage: function (opts) {
            var me = this;
            this.divDialog = new Gob.Pcm.UI.Web.Components.Dialog({
                closeOnEscape: false,
                close: function (event, ui) { if (me.onClose && me.onClose != null) { me.onClose(event, ui); } },
                resizable: false,
                closeText: "Close",
                width: 300
            });
            this.divDialog.setClass('message-dialog-confirmation');
        },
        show: function (opts) {
            if (this.divDialog) {
                if (this.informationTimeOut) {
                    clearTimeout(this.informationTimeOut);
                }
                opts.modal = opts.modal == false ? opts.modal : true;
                this.divDialog.close();
                this.onClose = opts.onClose ? opts.onClose : null;
                this.divDialog.setTitle(opts.title);
                this.setMessage(opts.message);
                this.divDialog.setButtons(opts.buttons);
                this.divDialog.setClass(opts.dialogClass);
                this.divDialog.setModal(opts.modal);
                this.divDialog.setMinWidth(opts.minWidth);
                this.divDialog.setMinHeight(opts.minHeight);
                this.divDialog.open();
            }
        }
    }
};

// // Copyright (c) 2017.
// All rights reserved.
/// <summary>
/// Libreria de intregracion de Jquery ajax con el proyecto.
/// </summary>
/// <remarks>
/// Creacion: 	WECM 14092017 <br />
/// </remarks>
ns('Gob.Pcm.UI.Web.Components');
Gob.Pcm.UI.Web.Components.Ajax = function (opts) {
    this.init(opts);
};

Gob.Pcm.UI.Web.Components.Ajax.prototype = {
    form: null,
    content: null,
    action: null,
    data: null,
    customAction: null,
    updatePanel: null,
    updateForm: null,
    objectCall: null,
    showLoading: null,
    locationLoading: null,
    targetLoading: null,
    locationLoading: null,
    autoSubmit: null,
    ajaxRequest: null,
    async: null,
    method: null,
    dataType: null,
    CONTENT_TYPE_POST: 'application/json; charset=utf-8',
    CONTENT_TYPE_GET: 'text/json;',
    CONTENT_TYPE_FORM: 'application/x-www-form-urlencoded',
    message: new Gob.Pcm.UI.Web.Components.Message(),
    processData: null,
    hasFile: null,

    init: function (opts) {
        this.async = this.async == false ? false : true;
        this.method = this.method ? this.method : 'POST';
        this.dataType = this.dataType ? this.dataType : 'json';
        this.showLoading = this.showLoading == false ? false : true;
        this.customAction = { args: null, callBack: null };
        if (opts) {
            this.form = opts.form && opts.form != null ? document.getElementById(opts.form) : null;
            this.content = opts.content ? document.getElementById(opts.content) : null;
            this.action = opts.action && opts.action != null ? opts.action : (this.form != null && this.action == null) ? this.form.action : this.action;
            this.data = opts.data ? opts.data : {};
            this.updatePanel = opts.updatePanel ? $('#' + opts.updatePanel) : null;
            this.dataType = opts.dataType ? opts.dataType : (this.dataType && this.dataType != null) ? this.dataType : 'json';
            this.method = opts.method ? opts.method : 'POST';
            this.async = opts.async == false ? opts.async : true;
            this.processData = opts.processData == false ? opts.processData : true;
            this.hasFile = opts.hasFile == true ? opts.hasFile : false;

            this.onSuccess = opts.onSuccess && this.onSuccess == null ? opts.onSuccess : this.onSuccess;
            this.onError = opts.onError ? opts.onError : this.onError;
            this.autoSubmit = opts.autoSubmit == false ? opts.autoSubmit : true;
            this.showLoading = opts.showLoading == false ? opts.showLoading : true;
            this.locationLoading = opts.locationLoading == undefined ? "body" : "#" + opts.locationLoading;
            this.targetLoading = opts.targetLoading;
            this.updateForm = opts.updateForm && opts.updateForm != null ? document.getElementById(opts.updateForm) : null;
            if (this.autoSubmit) {
                this.submit();
            }
        }
    },

    dataJsonToForm: function (data) {

        if (data && data != null) {
            var valor = null;
            for (var i = 0; i < this.updateForm.elements.length; i++) {
                if (this.updateForm.elements[i].name && this.updateForm.elements[i].name != '') {
                    valor = data[this.updateForm.elements[i].name];

                    if (this.updateForm.elements[i].type.toUpperCase() == 'RADIO' || this.updateForm.elements[i].type.toUpperCase() == 'CHECKBOX') {
                        if (valor == null) {
                            valor = false;
                        }
                        this.updateForm.elements[i].checked = valor;
                    } else {
                        if (valor == null) {
                            valor = '';
                        }
                        this.updateForm.elements[i].value = valor;
                    }
                }
            }
        }

    },

    formToDataJSon: function () {
        this.data = this.data ? this.data : {};
        for (var i = 0; i < this.form.elements.length; i++) {
            if (this.form.elements[i].name && this.form.elements[i].name != '') {

                if (this.form.elements[i].type.toUpperCase() == 'RADIO' || this.form.elements[i].type.toUpperCase() == 'CHECKBOX') {
                    if (this.form.elements[i].checked) {
                        if (this.data[this.form.elements[i].name] != null) {

                            if (typeof this.data[this.form.elements[i].name] == 'string') {
                                var firstValueArray = this.data[this.form.elements[i].name];
                                this.data[this.form.elements[i].name] = null;
                                this.data[this.form.elements[i].name] = new Array();
                                this.data[this.form.elements[i].name][0] = firstValueArray;
                            }
                            this.data[this.form.elements[i].name][this.data[this.form.elements[i].name].length] = this.form.elements[i].value.toString().trim().toUpperCase();
                        }
                        else {
                            this.data[this.form.elements[i].name] = this.form.elements[i].value.toString().trim().toUpperCase();
                        }
                    }
                } else {
                    this.data[this.form.elements[i].name] = this.form.elements[i].value.toString().trim().toUpperCase();
                }
            }
        }
        return this.data;
    },

    contentToDataJSon: function () {
        this.data = this.data ? this.data : {};
        var ele = null;
        var name = '';
        var array = this.content.getElementsByTagName('*');
        for (var i = 0; i < array.length; i++) {
            ele = array[i];

            if ((ele.name && ele.name != '') || (ele.id && ele.id != '')) {

                name = (ele.id && ele.id != '') ? ele.id : '';
                name = (ele.name && ele.name != '') ? ele.name : name;

                if (ele.type.toUpperCase() == 'RADIO' || ele.type.toUpperCase() == 'CHECKBOX') {
                    if (ele.checked) {
                        this.data[name] = ele.value;

                        if (this.data[name] != null) {

                            if (typeof this.data[name] == 'string') {
                                var firstValueArray = this.data[name];
                                this.data[name] = null;
                                this.data[name] = new Array();
                                this.data[name][0] = firstValueArray;
                            }
                            this.data[name][this.data[name].length] = ele.value;
                        }
                        else {
                            this.data[name] = ele.value;
                        }

                    }
                } else {
                    this.data[name] = ele.value;
                }
            }
        }
        return this.data;
    },

    submit: function () {

        if (typeof this.form == 'string') {
            this.form = document.getElementById(this.form);
        }
        if (this.action == null && this.form != null) {
            this.action = this.form.action;
        }
        if (this.ajaxRequest != null) {
            this.abort();
        }

        if (this.form != null)
            this.formToDataJSon();
        if (this.content != null)
            this.contentToDataJSon();

        var me = this;
        this.ajaxRequest = $.ajax({
            type: this.method,
            url: this.action,
            async: this.async,
            cache: false,            
            dataType: this.dataType,
            data: this.data,//(this.method.toUpperCase() == 'GET' || this.hasFile) ? this.data : JSON.stringify(this.data),
            customParams: me.customParams,
            processData: this.processData,
            beforeSend: function (jqXHR, settings) {
                if (me.showLoading == true) {  
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

            }
        });

        this.ajaxRequest.done(function (data) {            
            if ((data && data.isSuccess === true) || me.dataType.toUpperCase() == 'HTML') {
                if (me.updateForm != null) {
                    me.dataJsonToForm(data);
                }
                if (me.updatePanel != null) {
                    me.updatePanel.html(data);
                }
                if (me.onSuccess && me.onSuccess != null) {
                    me.onSuccess(data, me.customAction);
                }
            }
            else {
                /*if (data.Exception && data.Exception.IsCustomException) {
                    me.message.Warning({ message: data.Exception.Message });
                }
                else {*/
                me.message.Error({ message: 'Error al procesar la solicitud, el proceso retorno : IsSuccess="false"; Validar la ejecucion del proceso en el servidor.' });
                //}

            }
            me.data = null;
        });

        this.ajaxRequest.fail(function (jqXHR, textStatus, errorThrown) {
            if (jqXHR.status == 490) {
                window.location.href = jqXHR.statusText;
            }
            else if (!(textStatus == 'abort' && errorThrown == 'abort')) {
                if (me.onError) {
                    me.onError(errorThrown, textStatus, jqXHR, me.customAction);
                }
            }
            me.data = null;
        });
        this.ajaxRequest.always(function (jqXHR, textStatus) { 

            if (jqXHR.TimeOut == 'False') { 
            }
        });
        this.ajaxRequest.complete(function () {
            if (me.showLoading == true) {
               
               jQuery.unblockUI();
        }
        });
    },

    send: function (data, onSuccess, onError) {
        this.data = data && data != null ? data : this.data;
        this.onSuccess = onSuccess ? onSuccess : this.onSuccess;
        this.onError = onError ? onError : this.onError;
        this.submit();
    },

    implementLoading: function () {
        this.loading = new Gob.Pcm.UI.Web.Components.ProgressBar({ renderizarEn: this.targetLoading });
        console.log('this.loading');
        console.log(this.loading);
    },

    abort: function () {
        if (this.ajaxRequest)
            this.ajaxRequest.abort();
    },

    loading: null,
    onSuccess: null,
    onError: null
};

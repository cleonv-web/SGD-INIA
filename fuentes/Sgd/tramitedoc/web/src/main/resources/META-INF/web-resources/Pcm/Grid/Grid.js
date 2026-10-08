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

//
///// Copyright (c) 2018.
/// <summary>
/// Libreria para la creación de Grillas
/// </summary>
/// <remarks>
/// Creacion: 	Pcm 0606018 <br />Gob.Pcm.UI.Web.Components.Grid.prototype
/// </remarks>
ns('Gob.Pcm.UI.Web.Components.Grid'); 
Gob.Pcm.UI.Web.Components.Grid = function (opts) {
    this.Init(opts);
};
Gob.Pcm.UI.Web.Components.Grid.prototype = {
    id: null,
    idFilter: null,
    renderTo: null,
    columns: null,
    columnDefs: null,
    hasPaging: null,
    hasPagingServer: null,
    hasSearching: null,
    hasSelectionRows: null,
    hasSelectionCell: null,
    selectionRowsEvent:null,
    initComplete: null,
    height: null,
    proxy: null,
    core: null,
    table: null,
    events: null,
    scrollY: null,
    scrollCollapse: null,
    data: null,
    dataPagination: null,
    ordering: null,
    Init: function (opts) {

        this.renderTo = $('#' + opts.renderTo);
        if (this.renderTo && this.renderTo.length == 0) {
            alert('ERROR: Not renderTo defined');
            return;
        }
      
        this.columns = opts.columns && opts.columns != null ? opts.columns : null;
        this.columnDefs = opts.columnDefs && opts.columnDefs != null ? opts.columnDefs : null;
        this.order = opts.order && opts.order != null ? opts.order : undefined;
        this.proxy = opts.proxy && opts.proxy != null ? opts.proxy : null;
        this.events = opts.events && opts.events != null ? opts.events : null;
        this.hasPaging = opts.hasPaging != null ? opts.hasPaging : true;
        this.hasPagingServer = opts.hasPagingServer != null ? opts.hasPagingServer : true;
        this.hasSearching = opts.hasSearching != null ? opts.hasSearching : false;
        this.initComplete = opts.initComplete != null ? opts.initComplete : null;
        this.ordering = opts.ordering != null ? opts.ordering : false;
        this.selectionRowsEvent = opts.selectionRowsEvent != null ? opts.selectionRowsEvent : null;
        this.height = opts.height != null ? opts.height : null;
        this.scrollY = opts.scrollY != null ? opts.scrollY : false;
        this.scrollCollapse = opts.scrollCollapse != null ? opts.scrollCollapse : false;
        this.hasSelectionRows = opts.hasSelectionRows != null ? opts.hasSelectionRows : true;
        this.hasSelectionCell = opts.hasSelectionCell != null ? opts.hasSelectionCell : true;
        this.idFilter = opts.idFilter != null ? opts.idFilter : "";

        this.data = opts.data != null ? opts.data : new Array();
        if (this.hasPagingServer) {
            this.dataPagination = {
                NumeroPagina: 0,
                RegistrosPagina: 10
            }
        }
        if (this.columns == null) {
            alert('ERROR: Not columms defined');
            return;
        }
        this.id = opts.id && opts.id != null ? opts.id : 'PCM-Grid-';
        this._privateFunction.ImplementTableElement.apply(this, [this.renderTo]);
        this._privateFunction.ProcessSelectionRows.apply(this);
        this._privateFunction.CreateGrid.apply(this, [this.data]);

    },
    Load: function (params) {
        this.proxy.params = params;
        this._privateFunction.CreateGrid.apply(this);
    },
    ExportToExcel: function (name) {
        this.table.tableExport({ type: 'excel', escape: 'false', tableName: "shieldui-grid", documentName: name });

    },
    DestroyGrid: function () {
        if (this.core != null) {
            this.core.destroy();
            this.core = null;
        }
    },
    GetSelectedData: function () {
        var data = this.core.rows('.selected').data();
        var lista = new Array();
        for (var i = 0; i < data.length; i++) {
            lista.push(data[i]);
        }
        return lista;
    }, 
    _privateFunction: {
        CreateGrid: function () {
            $("#" + $(this)[0].id).data("instancia", this);
             
            if (this.core != null) {
                this.core.destroy();
            }
            if (this.proxy != null) {
                $("#" + $(this)[0].id).data("paginateUrl", this.proxy.url);
                $("#" + $(this)[0].id).data("paginateSource", this.proxy.source);
                if (this.proxy.params != null) {
                    if (this.hasPagingServer) {
                        this.proxy.params.NumeroPagina = this.dataPagination.NumeroPagina;
                        this.proxy.params.RegistrosPagina = this.dataPagination.RegistrosPagina;
                    }
                    $("#" + $(this)[0].id).data("paginateData", this.proxy.params);
                }
            }

             
            this.core = this.table.DataTable({
                columns: this.columns,
                idFilter: this.idFilter,
                autoWidth: true,
                //data: dataSource,            
                paging: this.hasPaging,
                serverSide: this.hasPagingServer,
                ordering: this.ordering,
                searching: this.hasSearching,
                columnDefs: this.columnDefs,
                order: this.order,
                ajax: this._privateFunction.CallProxy,
                //scrollY: this.scrollY,
                //scrollCollapse: this.scrollCollapse,
                initComplete: this.initComplete
                //responsive: true
            });
        },
        ImplementTableElement: function (renderTo) {
            renderTo.addClass('table-responsive');

            this.table = $('<table />').uniqueId();
            this.id = this.id + this.table.attr('id');
            this.table.attr('id', this.id);
            this.table.width('100%');
            this.table.addClass('table table-striped table-bordered table-hover text-center text-middle');

            if (this.height != null) {
                this.table.attr('height', this.height);
            }

            renderTo.append(this.table);


            this._privateFunction.ProcessColumns.apply(this);

            if (this.events != null) {
                var me = this;
                $.each(this.events, function (index, event) {
                    if (event.isRowEvent) {
                        me.table.on(event.type, event.selector, function () {
                            var row = me.core.row($(this).parents('tr'))
                            var data = row.data();
                            event.callBack.apply(this, [row, data]);
                        });
                    }
                    else {
                        me.table.on(event.type, event.selector, event.callBack);
                    }
                });
            }
            return this.table;
        },
        ProcessColumns: function () {
            var me = this;
            this.events = this.events || new Array();
            $.each(this.columns, function (index, column) {
                if (column.actionButtons) {

                    $.each(column.actionButtons, function (index, action) {

                        me.events.push({
                            type: action.event.on,
                            selector: '.' + action.type.Icon + '-' + me.id + '-' + index,
                            callBack: action.event.callBack,
                            isRowEvent: true
                        });

                    });

                    column.mRender = function (data, type, full) {

                        var htmlSource = '';
                        $.each(column.actionButtons, function (index, action) {
                            var renderAction = action.validateRender ? action.validateRender(data, type, full) : true;
                            if (renderAction) {
                                htmlSource += action.type.Source(me.id, action.toolTip, index);
                            }
                        });
                        return htmlSource;
                    };
                };
            });
        },
        ProcessSelectionRows: function () {
            var me = this; 
            var idCheckHeader = 'chkHeader-' + this.id;
            var idCheckRow = 'chkRow-' + this.id;

            if (this.hasSelectionRows) {
                this.columns.splice(0, 0, {
                    data: '', title: '<input class="checkSgd ' + idCheckHeader + '" type="checkbox" >',
                    'mRender': function (data, type, full) {
                        var html = '';
                         var renderAction = me.selectionRowsEvent!= null && me.selectionRowsEvent.validateRender ? me.selectionRowsEvent.validateRender(data, type, full) : true;
                            if (renderAction) {
                                html += '<input class="checkSgd ' + idCheckRow + '" type="checkbox">';
                            }                        
                        return html;
                    }
                });
                this.table.on('click', '.' + idCheckRow, function () {
                var row = $(this).parents('tr');
                var chkHeader = $(me.table.find('.' + idCheckHeader));

                row.toggleClass('selected');
                chkHeader.prop('checked', (me.core.data().length == me.core.rows('.selected').data().length));
            });

            this.table.on('click', '.' + idCheckHeader, function () {
                var chk = $(this);
                var rows = me.table.find('tr');
                rows.removeClass('selected');
                if (chk.is(':checked')) {
                    rows.addClass('selected');
                }
                me.table.find('.' + idCheckRow).prop('checked', chk.is(':checked'));
            });
            }
            else{
                this.table.on('click', 'td', function () {
                var chk = $(this).parents('tr');
                var data = me.core.rows(chk).data();
                var rows = me.table.find('tr');
                rows.removeClass('selected');
                if (chk) {
                    chk.addClass('selected');
                }
                me.table.find(chk); 
                if (me.selectionRowsEvent != null) { 
                    var lista = [];
                    for (var i = 0; i < data.length; i++) {
                        lista.push(data[i]);
                    }
                    me.selectionRowsEvent.callBack.call(this, this, lista);
                }
            });
        }
            
             
            
        },
        
        CallProxy: function (request, callback, settings) {
            var that = this;
            var instancia = $(that).data("instancia");   
            if (instancia.proxy.params != null) {
                if (instancia.searching) {
                    instancia.proxy.params[instancia.searchingText] = request.search.value;
                }
                instancia.proxy.params.NumeroPagina = request.start / request.length;
                instancia.proxy.params.RegistrosPagina = request.length;
                if (request.order != null && request.order.length > 0) {
                    instancia.proxy.params.Orden = request.columns[request.order[0].column].data + '-' + request.order[0].dir.toUpperCase();
                }
                 
                var ajaxBuscar = new Gob.Pcm.UI.Web.Components.Ajax({
                    action: instancia.proxy.url,
                    data: instancia.proxy.params, 
                    //async:false,  
                    onSuccess: function (data) {                        
                        var records = data[instancia.proxy.source];
                        try {
                            callback({ 'data': records, "iTotalRecords": records.length > 0 ? records[0].filasTotal : "0", "iDisplayLength": records.length > 0 ? records[0].NumeroFila : "0", "iTotalDisplayRecords": records.length > 0 ? records[0].filasTotal : "0" });
                        } catch (err) {

                        }
                         
                    }
                });
            } 
        }
    }
};


ns('Gob.Pcm.UI.Web.Components.GridAction');
Gob.Pcm.UI.Web.Components.GridAction = {
    RessetPass: {
        Class: 'fa-icon-color',
        Icon: 'fa-unlock-alt',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Reset Password';
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.RessetPass, tooltip, index);
        }
    },
    ViewFile: {
        Class: 'fa-icon-color',
        Icon: 'fas fa-file',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Ver Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Edit, tooltip, index);
        }
    },
    Edit: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-edit',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Editar Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.Edit, tooltip, index);
        }
    },
     EditGreen: {
        Class: 'btn btn-success btn-xs',
        Icon: 'glyphicon-edit',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Editar Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.EditGreen, tooltip, index);
        }
    },
     EditOrange: {
        Class: 'btn btn-warning btn-xs',
        Icon: 'glyphicon-edit',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Editar Documento Urgente';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.EditOrange, tooltip, index);
        }
    },
     EditRed: {
        Class: 'btn btn-danger btn-xs',
        Icon: 'glyphicon-edit',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Editar Documento Muy Urgente';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.EditRed, tooltip, index);
        }
    },
    EditLightBlue: {
        Class: 'btn btn-info btn-xs',
        Icon: 'glyphicon-edit',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Editar Documento Observado';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.EditLightBlue, tooltip, index);
        }
    },
    Delete: {
        Class: 'delete fa-icon-color',
        Icon: 'fa-trash',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : Gob.Pcm.UI.Web.Components.GenericoResource.EtiquetaEliminar;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Delete, tooltip, index);
        }
    },
    Seleccionar: {
        Class: 'seleccionar',
        Icon: 'fa-share',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Seleccionar';//0Gob.Pcm.UI.Web.Components.GenericoResource.EtiquetaEliminar;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Seleccionar, tooltip, index);
        }
    },
    Detail: {
        Class: 'detail',
        Icon: 'fa-tasks',
        Source: function (id, tooltip, index) {
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Detail, tooltip, index);
        }
    },
    ViewInfo: {
        Class: 'view fa-icon-color',
        Icon: 'fa-eye',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : Gob.Pcm.UI.Web.Components.GenericoResource.VerInformacion;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.ViewInfo, tooltip, index);
        }
    },
    Check: {
        Class: 'print',
        Icon: 'fa-check-square-o',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : Gob.Pcm.UI.Web.Components.GenericoResource.EtiquetaCheck;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Check, tooltip, index);
        }
    },
    Print: {
        Class: 'print',
        Icon: 'fa-print',
        Source: function (id, tooltip, index) {

            tooltip = tooltip ? tooltip : Gob.Pcm.UI.Web.Components.GenericoResource.EtiquetaImprimir;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Print, tooltip, index);
        }
    }, 
    Upload: {
        Class: 'upload',
        Icon: 'fa-upload',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : Gob.Pcm.UI.Web.Components.GenericoResource.EtiquetaCargarDocumento;
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Upload, tooltip, index);
        }
    },

    Download: {
        Class: 'download',
        Icon: 'fa-download',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : "Descargar Documento";
            return Gob.Pcm.UI.Web.Components.GridAction.Source(id, Gob.Pcm.UI.Web.Components.GridAction.Download, tooltip, index);
        }
    },


    PaperClip: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-paperclip',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Documentos Anexos';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.PaperClip, tooltip, index);
        }
    },
    
     PaperBook: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-green-book',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Ver Documento Inicial';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.PaperBook, tooltip, index);
        }
    },
    SendNotificationGreen: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-envelope-green',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Enviar Notificación';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.SendNotificationGreen, tooltip, index);
        }
    },
    SendNotificationDanger: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-envelope-red',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Enviar Notificación';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.SendNotificationDanger, tooltip, index);
        }
    },
    ArchiveDoc: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-save-green',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Archivar Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.ArchiveDoc, tooltip, index);
        }
    },
    File: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-file',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Ver Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.File, tooltip, index);
        }
    },
    DigitalSign: {
        Class: 'btn btn-default btn-xs',
        Icon: 'glyphicon-digital-sign',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Firmar Documento';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.DigitalSign, tooltip, index);
        }
    },
     ActaNotificacion: {
        Class: 'btn btn-default btn-xs alert-warning',
        Icon: 'glyphicon-tag',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Imprimir Acta de Notificación - 1ra Visita';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.ActaNotificacion, tooltip, index);
        }
    },
    ActaNotificacion2: {
        Class: 'btn btn-default btn-xs alert-warning',
        Icon: 'glyphicon-tags',
        Source: function (id, tooltip, index) {
            tooltip = tooltip ? tooltip : 'Imprimir Acta de Notificación - 2da Visita';
            return Gob.Pcm.UI.Web.Components.GridAction.SourceGlyphicon(id, Gob.Pcm.UI.Web.Components.GridAction.ActaNotificacion2, tooltip, index);
        }
    },
    
    Source: function (id, atributos, tooltip, index) {
        var selector = atributos.Icon + '-' + id + '-' + index;
        return Gob.Pe.UI.Web.Components.Util.RenderIcono(atributos.Class, atributos.Icon + ' ' + selector, tooltip);
    },
    SourceGlyphicon: function (id, atributos, tooltip, index) {
        var selector = atributos.Icon + '-' + id + '-' + index;
        return Gob.Pe.UI.Web.Components.Util.RenderIconoGlyphicon(atributos.Class, atributos.Icon + ' ' + selector, tooltip);
    },
     SourceDoubleGlyphicon: function (id, atributos, tooltip, index) {
        var selector = atributos.Icon + '-' + id + '-' + index;
        return Gob.Pe.UI.Web.Components.Util.DoubleRenderIconoGlyphicon(atributos.Class, atributos.Icon + ' ' + selector, tooltip);
    }
};



/// Copyright (c) 2017.
/// All rights reserved.
/// <summary>
/// Controlador de métodos genericos
/// </summary>
/// <remarks>
/// Creacion: WECM 25092017 <br />
/// </remarks>
ns('Gob.Pe.UI.Web.Components.Util');
Gob.Pe.UI.Web.Components.Util.GetKeyCode = function (e) {
    return e.keyCode ? e.keyCode : e.which ? e.which : e.charCode;
}

Gob.Pe.UI.Web.Components.Util.GetValueCopy = function (e) {
    var texto = "";
    if (window.clipboardData) {
        texto = window.clipboardData.getData('Text');
    }
    else {
        texto = e.originalEvent.clipboardData.getData('text/plain');
    }
    return texto;
}

Gob.Pe.UI.Web.Components.Util.ValidateBlurOnlyDate = function (e) {
    var ok = true;
    if (e.value.length < 10) {
        ok = false;
    }
    else {
        try {
            var date = $.datepicker.parseDate(Gob.Pe.UI.Web.Formato.Fecha, e.value);
            ok = (date.getFullYear() >= 1900)
        }
        catch (err) {
            ok = false;
        }
    }

    if (!ok) {
        $('#' + e.id).val('');
    }

    return ok;
}

Gob.Pe.UI.Web.Components.Util.ValidateCopyDate = function validarCopyFecha(e) {
    var texto = GetCopiedValue(e);
    return ValidateCopyDate(texto);
}

Gob.Pe.UI.Web.Components.Util.ValidateCopyOnlyNumeric = function (e) {
    var text = Gob.Pe.UI.Web.Components.Util.GetValueCopy(e);
    return Gob.Pe.UI.Web.Components.Util.ValidateStringOnlyNumeric(text);
}

Gob.Pe.UI.Web.Components.Util.GetCopiedValue = function (e) {
    var text = "";
    if (window.clipboardData) {
        text = window.clipboardData.getData('Text');
    }
    else {
        text = e.originalEvent.clipboardData.getData('text/plain');
    }
    return text;
}

Gob.Pe.UI.Web.Components.Util.ValidateStringOnlyNumeric = function (text) {
    var patron = /^[0-9\r\n]+$/;
    if (!text.search(patron))
        return true;
    else
        return false;
}

Gob.Pe.UI.Web.Components.Util.ValidaCadenaSoloTexto = function (text) {
    var patron = /^[a-zA-Z]*$/;
    if (!text.search(patron))
        return true;
    else
        return false;
}

Gob.Pe.UI.Web.Components.Util.ValidateOnlyNumbers = function (e) {
    /*Validar la existencia del objeto event*/
    e = (e) ? e : event;

    var key = Gob.Pe.TemplateApp.Presentation.Web.Shared.Util.GetKeyCode(e);

    /*Predefinir como invalido*/
    var result = false;

    if (key >= 48 && key <= 57)
    { result = true; }
    if (evento.charCode == 0)/*direccionales*/
    { result = true; }

    if (key == 13)/*enter*/
    { result = true; }

    /*Regresar la result*/
    return result;
}

Gob.Pe.UI.Web.Components.Util.ValidateCopyOnlyAlphanumeric = function (e) {
    var text = Gob.Pe.UI.Web.Components.Util.GetValueCopy(e);
    var patron = /^[\u00F1A-Za-z0-9\-.\s]+$/i;
    var result = patron.test(text);
    return result;
}

Gob.Pe.UI.Web.Components.Util.ValidarCopiarSoloTexto = function (e) {
    var text = Gob.Pe.UI.Web.Components.Util.GetValueCopy(e);
    return Gob.Pe.UI.Web.Components.Util.ValidaCadenaSoloTexto(text);
}

Gob.Pe.UI.Web.Components.Util.ValidarCopiarSoloNumeros = function (e) {
    var text = Gob.Pe.UI.Web.Components.Util.GetValueCopy(e);
    return Gob.Pe.UI.Web.Components.Util.ValidateStringOnlyNumeric(text);
}

Gob.Pe.UI.Web.Components.Util.ValidateCopyDate = function validarCampoFecha(value) {
    var date_regex = /^(0[1-9]|1\d|2\d|3[01])\/(0[1-9]|1[0-2])\/(19|20)\d{2}$/;
    return date_regex.test(value);
};

Gob.Pe.UI.Web.Components.Util.RemoveDrop = function () {
    var controls = $("input[type=text], input[type=password], textarea");
    controls.bind("drop", function () {
        return false;
    });
    controls = undefined;
}
 
Gob.Pe.UI.Web.Components.Util.RenderIndicadorCheck = function (data, type, full) {
    var etiqueta = '';

    if (data === true)
        etiqueta = '<span class="control-table"><i class="fa fa-check-square" style="font-size: 16px"></i></span>'; 
    else if (data === false)
        etiqueta = '<span class="control-table"><i class="fa fa-square-o" style="font-size: 16px"></i></span>'; 

    return etiqueta;
}

Gob.Pe.UI.Web.Components.Util.RenderIcono = function (clase, icono, tooltip) {
    var etiqueta = '';

    if (tooltip)
        etiqueta = 'data-toggle="tooltip" data-placement="top" title="' + tooltip + '"'

    etiqueta = '<span class="control-table ' + clase + '" ' + etiqueta + '><i class="fa ' + icono + '"></i></span>';

    return etiqueta;
}
Gob.Pe.UI.Web.Components.Util.RenderIconoGlyphicon  = function (clase, icono, tooltip) {
    var etiqueta = '';

    if (tooltip)
        etiqueta = 'data-toggle="tooltip" data-placement="top" title="' + tooltip + '"'

    etiqueta = '<span class="control-table ' + clase + '" ' + etiqueta + '><i class="glyphicon  ' + icono + '"></i></span>';

    return etiqueta;
}

Gob.Pe.UI.Web.Components.Util.DoubleRenderIconoGlyphicon  = function (clase, icono, icono2, tooltip) {
    var etiqueta = '';

    if (tooltip)
        etiqueta = 'data-toggle="tooltip" data-placement="top" title="' + tooltip + '"'

    etiqueta = '<span class="control-table ' + clase + '" ' + etiqueta + '><i class="glyphicon  ' + icono + '"></i><i class="glyphicon glyphicon-ok-circle"></i></span>';

    return etiqueta;
}
Gob.Pe.UI.Web.Components.Util.RenderIconoAccion = function (editar, eliminar) {
    var etiqueta = '';

    editar = (editar !== false);
    eliminar = (eliminar !== false);

    if (editar)
        etiqueta += Gob.Pe.UI.Web.Components.Util.RenderIcono('edit', 'fa-edit', Pe.Stracon.Politicas.Presentacion.Base.GenericoResource.EtiquetaEditar);

    if (eliminar)
        etiqueta += Gob.Pe.UI.Web.Components.Util.RenderIcono('delete', 'fa-trash', Pe.Stracon.Politicas.Presentacion.Base.GenericoResource.EtiquetaEliminar);

    return etiqueta;
}


Gob.Pe.UI.Web.Components.Util.RedirectPost = function (location, args) {
    var form = '';
    $.each(args, function (key, value) {
        form += '<input type="hidden" name="' + key + '" value="' + value + '">';
    });
    var submit = $('<form action="' + location + '" method="POST" target="_blank">' + form + '</form>');//_self
    $('body').after(submit);
    submit.submit();
}

Gob.Pe.UI.Web.Components.Util.RedirectReportingPost = function (location, datos, parametros) {
    var form = '';
    $.each(datos, function (key, value) {
        form += '<input type="hidden" name="' + key + '" value="' + value + '">';
    });
    $.each(parametros, function (key, value) {
        form += '<input type="hidden" name="Parametros[' + key + '].Name"  value="' + value.Name + '">';
        form += '<input type="hidden" name="Parametros[' + key + '].Values"  value="' + value.Values + '">';
        form += '<input type="hidden" name="Parametros[' + key + '].Visible"  value="' + value.Visible + '">';
    });
    var submit = $('<form action="' + location + '" method="POST" target="_self">' + form + '</form>');
    $('body').after(submit);
    submit.submit();
}

Gob.Pe.UI.Web.Components.Util.Left = function (cadena, len) {
    if (len <= 0)
        return "";
    else if (len > String(cadena).length)
        return cadena;
    else
        return String(cadena).substring(0, len);
}

Gob.Pe.UI.Web.Components.Util.Right = function (cadena, len) {
    if (len <= 0)
        return "";
    else if (len > String(cadena).length)
        return str;
    else {
        var iLen = String(cadena).length;
        return String(cadena).substring(iLen, iLen - len);
    }
};

Gob.Pe.UI.Web.Components.Util.StringFormat = function () {
    // The string containing the format items (e.g. "{0}")
    // will and always has to be the first argument.
    var theString = arguments[0];

    // start with the second argument (i = 1)
    for (var i = 1; i < arguments.length; i++) {
        // "gm" = RegEx options for Global search (more than one instance)
        // and for Multiline search
        var regEx = new RegExp("\\{" + (i - 1) + "\\}", "gm");
        theString = theString.replace(regEx, arguments[i]);
    }

    return theString;
};

Gob.Pe.UI.Web.Components.Util.SoloLetras = function (e) {
    var tecla = (document.all) ? e.keyCode : e.which;
    patron = /^[a-zA-ZáéíóúàèìòùÀÈÌÒÙÁÉÍÓÚñÑüÜ\s]+$/;
    te = String.fromCharCode(tecla);
    return patron.test(te);

};
Gob.Pe.UI.Web.Components.Util.SoloNumeros = function (e) {
    var tecla = (document.all) ? e.keyCode : e.which;
    patron = /^[0-9]+$/;
    te = String.fromCharCode(tecla);
    return patron.test(te);
};
Gob.Pe.UI.Web.Components.Util.SoloEmail = function (value) {
    patron = /^[A-Z0-9._%+-]+@([A-Z0-9-]+\.)+[A-Z]{2,4}$/i;
    return patron.test(value);
};

Gob.Pe.UI.Web.Components.Util.MergeData = function (temp, dataarry) {
    return temp.replace(/\$\{([\w]+)\}/g, function (s1, s2) { var s = dataarry[s2]; if (typeof (s) != "undefined") { return s; } else { return s1; } });
};

(function ($, undefined) {
    $.fn.getCursorPosition = function () {
        var el = $(this).get(0);
        var pos = 0;
        if ('selectionStart' in el) {
            pos = el.selectionStart;
        } else if ('selection' in document) {
            el.focus();
            var Sel = document.selection.createRange();
            var SelLength = document.selection.createRange().text.length;
            Sel.moveStart('character', -el.value.length);
            pos = Sel.text.length - SelLength;
        }
        return pos;
    }
})(jQuery);

//Giovanni Rivera C.
function getCursorTinyPosicion(editor) {
    //set a bookmark so we can return to the current position after we reset the content later
    var bm = editor.selection.getBookmark(0);
    //select the bookmark element
    var selector = "[data-mce-type=bookmark]";
    var bmElements = editor.dom.select(selector);
    //put the cursor in front of that element
    editor.selection.select(bmElements[0]);
    editor.selection.collapse();
    //add in my special span to get the index...
    //we won't be able to use the bookmark element for this because each browser will put id and class attributes in different orders.
    var elementID = "######cursor######";
    var positionString = '<span id="' + elementID + '"></span>';
    editor.selection.setContent(positionString);
    //get the content with the special span but without the bookmark meta tag
    var content = editor.getContent({ format: "html" });
    //find the index of the span we placed earlier
    var index = content.indexOf(positionString);
    //remove my special span from the content
    editor.dom.remove(elementID, false);
    //move back to the bookmark
    editor.selection.moveToBookmark(bm);
    return index;
};

Gob.Pe.UI.Web.Components.Util.ConvertToDecimal = function (value) {

    var format = Gob.Pe.UI.Web.Formato.Decimal;
    var number = value;

    format = format.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorDecimal, '@');
    format = format.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorMiles, ',');
    format = format.replace('@', '.');

    number = number.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorDecimal, '@');
    number = number.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorMiles, ',');
    number = number.replace('@', '.');

    number = $.parseNumber(number, { format: format });

    return parseFloat(number);
};

Gob.Pe.UI.Web.Components.Util.DecimalConvertToString = function (value) {

    var format = Gob.Pe.UI.Web.Formato.Decimal;
    var number = value.toString();

    format = format.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorDecimal, '@');
    format = format.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorMiles, ',');
    format = format.replace('@', '.');

    if (typeof decimal == 'string') {
        number = number.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorDecimal, '@');
        number = number.replace(Gob.Pe.UI.Web.Formato.DecimalSeparadorMiles, '');
        number = number.replace('@', '.');
    }

    number = $.formatNumber(number, { format: format });

    number = number.replace('.', '@');
    number = number.replace(',', Gob.Pe.UI.Web.Formato.DecimalSeparadorMiles);
    number = number.replace('@', Gob.Pe.UI.Web.Formato.DecimalSeparadorDecimal);

    return number;

};

Gob.Pe.UI.Web.Components.Util.ValidateDateRange = function (txtStart, txtEnd) {

    var isValid = true;

    var valueStart = txtStart.is('input') ? txtStart.val() : txtStart.html();
    var valueEnd = txtEnd.is('input') ? txtEnd.val() : txtEnd.html();

    if (valueStart != '' && valueEnd != '') {
        var dateStart = $.datepicker.parseDate(Gob.Pe.UI.Web.Formato.Fecha, valueStart);
        var dateEnd = $.datepicker.parseDate(Gob.Pe.UI.Web.Formato.Fecha, valueEnd);
        isValid = (dateStart <= dateEnd);
    }
    return isValid;
}
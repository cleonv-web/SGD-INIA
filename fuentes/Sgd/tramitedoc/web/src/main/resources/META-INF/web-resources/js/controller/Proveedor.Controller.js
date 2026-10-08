/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * auto: wcondori
 */
ns('Gob.Pcm.SGD.Presentacion.Proveedor.Index');
Gob.Pcm.SGD.Presentacion.Proveedor.Index.Controller = function () {
var base = this;
base.Ini = function () {
     base.Control.BtnBuscar().off('click');
     base.Control.BtnBuscar().on('click', base.Event.BtnBuscarClick);     
     base.Control.TxtBusNroRuc().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Control.TxtBusRazonSocial().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Function.CrearGrid(); 
     //base.Event.BtnBuscarClick();
     
     };
base.Control = {
     BtnBuscar: function () { return $('#btn-buscar') },
     TxtBusNroRuc: function () { return $('#txtBusNroRuc'); },       
     TxtBusRazonSocial: function () { return $('#txtBusRazonSocial'); }, 
     Filtros:{}
    }; 
base.Event = {
    BtnBuscarClick: function () {
            'use strict' 
            base.Control.Filtros.busNroRuc= allTrim(base.Control.TxtBusNroRuc().val());
            base.Control.Filtros.busRazonSocial= base.Control.TxtBusRazonSocial().val().toUpperCase();
            base.Control.Filtros.accion='goListaBusqProveedoresJson';
            base.Control.GrdResultado.Load(base.Control.Filtros);
        }, 
    BtnGridSelectionRowClick: function (that, row) {            
            $.each(row, function (index, value) {  
                 $("#codProveedor").val(value.nuRuc); 
            });
        },
};
base.Function = { 
    CrearGrid: function () {
            var columns = new Array();
            columns.push({
                data: 'fila',
                title: 'N°',
                width: "3%"
            });
            columns.push({
                data: 'nuRuc',
                title: 'R.U.C.',
                width: "5%"
            });
            columns.push({
                data: 'descripcion',
                title: 'Razon Social',
                width: "20%",
            });
            columns.push({
                data: 'noDep',
                title: 'Dep.',
                width: "10%",
            });

            columns.push({
                data: 'noPrv',
                title: 'Prov.',
                width: "10%",
            });
             
            columns.push({
                data: 'noDis',
                title: 'Dist.',
                width: "10%",
            });

            columns.push({
                data: 'cproDomicil',
                title: 'Direc.',
                width: "15%",
            });
             
            columns.push({
                data: 'cproEmail',
                title: 'Correo',
                width: "5%",
            });
            columns.push({
                data: 'cproTelefo',
                title: 'Telf.',
                width: "5%",
            }); 
            
             

            base.Control.GrdResultado = new Gob.Pcm.UI.Web.Components.Grid({
                renderTo: 'divGrdResult',
                columns: columns,
                hasSelectionRows: false,
                hasSelectionCell:true,
                selectionRowsEvent: { on: 'click', callBack: base.Event.BtnGridSelectionRowClick },  
                ordering: false,                
                proxy: {
                    url: pRutaContexto + "/" + pAppVersion +'/srTablaConfiguracion.do',
                    source: 'result'
                },
                //returnCallbackComplete: base.Event.AjaxGrdResultadoSuccess,
            });
        }   
         };

        
};


ns('Gob.Pcm.SGD.Presentacion.Proveedor.Index');
try {
    $(document).ready(function () {
        'use strict';
        Gob.Pcm.SGD.Presentacion.Proveedor.Index.Vista = new Gob.Pcm.SGD.Presentacion.Proveedor.Index.Controller();
        Gob.Pcm.SGD.Presentacion.Proveedor.Index.Vista.Ini();
    });
} catch (ex) {
    alert(ex);
}




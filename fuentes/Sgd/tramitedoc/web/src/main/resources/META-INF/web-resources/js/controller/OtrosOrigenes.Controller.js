/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
ns('Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index');
Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index.Controller = function () {
var base = this;
base.Ini = function () {
     base.Control.BtnBuscar().off('click');
     base.Control.BtnBuscar().on('click', base.Event.BtnBuscarClick);
     base.Control.TxtBusRazonSocial().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Function.CrearGrid(); 
     //base.Event.BtnBuscarClick();
     
     };
base.Control = {
     BtnBuscar: function () { return $('#btn-buscar') },     
     TxtBusRazonSocial: function () { return $('#txtBusRazonSocial'); }, 
     Filtros:{}
    }; 
base.Event = {
    BtnBuscarClick: function () {
            'use strict' 
            base.Control.Filtros.busRazonSocial= base.Control.TxtBusRazonSocial().val().toUpperCase();
            base.Control.Filtros.accion='goListaBusqOtrosOrigenesJson';
            base.Control.GrdResultado.Load(base.Control.Filtros);
        }, 
    BtnGridSelectionRowClick: function (that, row) {            
            $.each(row, function (index, value) {  
                 $("#codOrigen").val(value.coOtrOri); 
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
                data: 'nuDocOtrOri',
                title: 'N. DOC',
                width: "5%"
            });
            columns.push({
                data: 'deApePatOtr',
                title: 'Ap Paterno',
                width: "10%",
            });
            columns.push({
                data: 'deApeMatOtr',
                title: 'Ap Materno',
                width: "10%",
            });

            columns.push({
                data: 'deNomOtr',
                title: 'Nombres',
                width: "10%",
            });
             
            columns.push({
                data: 'deRazSocOtr',
                title: 'Razón Social',
                width: "10%",
            });

            columns.push({
                data: 'noDep',
                title: 'Dep.',
                width: "5%",
            });
            columns.push({
                data: 'noPro',
                title: 'Prov.',
                width: "5%",
            });
            columns.push({
                data: 'noDis',
                title: 'Dist.',
                width: "5%",
            });
            columns.push({
                data: 'deDirOtroOri',
                title: 'Direc.',
                width: "10%",
            });
            columns.push({
                data: 'deEmail',
                title: 'Correo',
                width: "5%",
            });
            columns.push({
                data: 'deTelefo',
                title: 'Telf.',
                width: "5%",
            }); 
            
             

            base.Control.GrdResultado = new Gob.Pcm.UI.Web.Components.Grid({
                renderTo: 'divGrdResultOtroOrigen',
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


ns('Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index');
try {
    $(document).ready(function () {
        'use strict';
        Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index.Vista = new Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index.Controller();
        Gob.Pcm.SGD.Presentacion.OtrosOrigenes.Index.Vista.Ini();
    });
} catch (ex) {
    alert(ex);
}


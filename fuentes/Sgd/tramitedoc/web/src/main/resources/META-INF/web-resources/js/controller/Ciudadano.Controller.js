/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
ns('Gob.Pcm.SGD.Presentacion.Ciudadano.Index');
Gob.Pcm.SGD.Presentacion.Ciudadano.Index.Controller = function () {
var base = this;
base.Ini = function () {
     base.Control.BtnBuscar().off('click');
     base.Control.BtnBuscar().on('click', base.Event.BtnBuscarClick);     
     base.Control.TxtBusDocumento().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Control.TxtBusApePaterno().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Control.TxtBusApeMaterno().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Control.TxtBusNombre().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Function.CrearGrid(); 
     //base.Event.BtnBuscarClick();
     
     };
base.Control = {
     BtnBuscar: function () { return $('#btn-buscar');},
     TxtBusDocumento: function() {return $('#txtCiudDocumento');},
     TxtBusApePaterno: function(){return $('#txtCiudApPaterno');},
     TxtBusApeMaterno: function () { return $('#txtCiudApMaterno'); },       
     TxtBusNombre: function () { return $('#txtCiudNombres'); }, 
     Filtros:{}
    }; 
base.Event = {
    BtnBuscarClick: function () {
            'use strict' 
            base.Control.Filtros.busCiudDocumento= allTrim(base.Control.TxtBusDocumento().val());
            base.Control.Filtros.busCiudApPaterno= base.Control.TxtBusApePaterno().val().toUpperCase();
            base.Control.Filtros.busCiudApMaterno= base.Control.TxtBusApeMaterno().val().toUpperCase();
            base.Control.Filtros.busCiudNombres= base.Control.TxtBusNombre().val().toUpperCase();
            base.Control.Filtros.accion='goListaBusqCiudadanoJson';
            base.Control.GrdResultado.Load(base.Control.Filtros);
        }, 
    BtnGridSelectionRowClick: function (that, row) {            
            $.each(row, function (index, value) {  
                 $("#codCiudadano").val(value.nuLem); 
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
                data: 'nuLem',
                title: 'N. Doc',
                width: "5%"
            });
            columns.push({
                data: 'deApp',
                title: 'Ap. Paterno',
                width: "20%",
            });
            columns.push({
                data: 'deApm',
                title: 'Ap. Materno',
                width: "10%",
            });

            columns.push({
                data: 'deNom',
                title: 'Nombres',
                width: "10%",
            });
             
            columns.push({
                data: 'noDep',
                title: 'Dep.',
                width: "10%",
            });

            columns.push({
                data: 'noPrv',
                title: 'Prov.',
                width: "15%",
            });
            
            columns.push({
                data: 'noDis',
                title: 'Dist.',
                width: "15%",
            });
            columns.push({
                data: 'deDomicil',
                title: 'Direc.',
                width: "15%",
            });
            
            columns.push({
                data: 'deTelefo',
                title: 'Telf',
                width: "5%",
            });
            columns.push({
                data: 'deEmail',
                title: 'Correo.',
                width: "5%",
            }); 
            
             

            base.Control.GrdResultado = new Gob.Pcm.UI.Web.Components.Grid({
                renderTo: 'divGrdListCiudadano',
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


ns('Gob.Pcm.SGD.Presentacion.Ciudadano.Index');
try {
    $(document).ready(function () {
        'use strict';
        Gob.Pcm.SGD.Presentacion.Ciudadano.Index.Vista = new Gob.Pcm.SGD.Presentacion.Ciudadano.Index.Controller();
        Gob.Pcm.SGD.Presentacion.Ciudadano.Index.Vista.Ini();
    });
} catch (ex) {
    alert(ex);
}


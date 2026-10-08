/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * auto: ypino
 */
ns('Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index');
Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index.Controller = function () {
var base = this;
base.Ini = function () {
     base.Control.BtnBuscar().off('click');
     base.Control.BtnBuscar().on('click', base.Event.BtnBuscarClick);     
     base.Control.BtnFiltroBusqEmiDocuAdm().off('click');
     base.Control.BtnFiltroBusqEmiDocuAdm().on('click', base.Event.BtnBuscarClick);   
   //  base.Control.BtnRecepDocum().off('click');
   //  base.Control.BtnRecepDocum().on('click', base.Event.BtnRecepDocumClick);   
     base.Control.BtnEditarDocumentoRecep().off('click');
     base.Control.BtnEditarDocumentoRecep().on('click', base.Event.BtnEditarDocumentoRecepClick);   
     base.Control.BtnVerDocumento().off('click');
     base.Control.BtnVerDocumento().on('click', base.Event.BtnVerDocumentoClick);   
     base.Control.BtnVerAnexo().off('click');
     base.Control.BtnVerAnexo().on('click', base.Event.BtnVerAnexoClick);   
     base.Control.BtnVerSeguimiento().off('click');
     base.Control.BtnVerSeguimiento().on('click', base.Event.BtnVerSeguimientoClick);  
     base.Control.BtnVincularTema().off('click');
     base.Control.BtnVincularTema().on('click', base.Event.BtnVincularTemaClick);  
     base.Control.BtnVerAvanceRecepcion().off('click');
     base.Control.BtnVerAvanceRecepcion().on('click', base.Event.BtnVerAvanceRecepcionClick); 
     //base.Control.BtnFiltroBusqEmiDocuAdm().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     //base.Control.TxtBusRecepDocumAdm().bind('keypress', function (e) {   if (e.which === 13) { base.Event.BtnBuscarClick(); } });
     base.Function.CrearGrid(); 
     base.Event.BtnBuscarClick();
     
     };
base.Control = {
     BtnBuscar: function () { return $('#btn-buscar'); },
     BtnFiltroBusqEmiDocuAdm: function () { return $('#btnFiltroBusqEmiDocuAdm'); },       
     //BtnRecepDocum: function () { return $('#btnRecDocumentos'); },
     BtnEditarDocumentoRecep: function () { return $('#btnEditarDocumentoRecep'); },         
     BtnVerDocumento: function () { return $('#btnVerDocumento'); },    
     BtnVerAnexo: function () { return $('#btnVerAnexo'); },
     BtnVerSeguimiento: function () { return $('#btnVerSeguimiento'); },
     BtnVincularTema: function () { return $('#btnVincularTema'); }, 
     BtnVerAvanceRecepcion: function () { return $('#btnVerAvanceRecepcion'); }, 

     Filtros:{}
    }; 
base.Event = {
    BtnBuscarClick: function () {
            'use strict' 
          /*  JQuery('#divGrdLstDocRecepResult').html('');
            base.Function.CrearGrid(); */
           
           /* base.Control.Filtros.busNroRuc= allTrim(base.Control.BtnFiltroBusqEmiDocuAdm().val());
            base.Control.Filtros.busRazonSocial= base.Control.TxtBusRecepDocumAdm().val().toUpperCase();
            base.Control.Filtros.accion='goListaBusqDocumRecepAdmJson';*/
        //console.log('buscarDocumentoRecepBean:'+$('#buscarDocumentoRecepBean').serialize());
        
        
            jQuery('#sFeEmiIni').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('fini'));
           jQuery('#sFeEmiFin').val(jQuery('#sCoAnnio').parent('td').find('#fechaFiltro').attr('ffin'));    

            jQuery('#sTipoBusqueda').val('0');
           /*var vFeInicio = jQuery("#sFeEmiIni").val();
            var vFeFinal = jQuery("#sFeEmiFin").val();             
              var pAnnioBusq = verificarAñoBusquedaFiltro(vFeInicio,vFeFinal);
                if(pAnnioBusq!==null && pAnnioBusq!=="null" && typeof(pAnnioBusq)!=="undefined" && pAnnioBusq!==""){
                    var vValidaNumero = fu_validaNumero(pAnnioBusq);
                    if (vValidaNumero !== "OK") {
                       bootbox.alert("Año debe ser solo números.");
                        valRetorno = "0";
                    }else if(vValidaNumero ==="OK"){
                      //  jQuery('#buscarDocumentoRecepBean').find('#sCoAnnio').val(pAnnioBusq);                          
                      jQuery('#sCoAnnio').val(pAnnioBusq);
                      jQuery('#sCoAnnioBus').val(pAnnioBusq);
                    }                
                }               
            */
            fu_validaFiltroEmiDocAdm('0'); 
            
            var unindexed_array = $('#buscarDocumentoEmiBean').serializeArray();
            var indexed_array = {};

            $.map(unindexed_array, function(n, i){
                indexed_array[n['name']] = n['value'];
            });
           // base.Control.Filtros.EsIncluyeOficina= allTrim(base.Control.EsIncluyeOficina().val());
         //   base.Control.Filtros.EsIncluyeProfesional= allTrim(base.Control.EsIncluyeProfesional().val());
            base.Control.Filtros.buscarDocumentoEmiBean=JSON.stringify(indexed_array); 
            //console.log('buscarDocumentoEmiBean='+base.Control.Filtros.buscarDocumentoEmiBean);
           
            base.Control.GrdResultado.Load(base.Control.Filtros);

           if($('#pLenghDatatable').val().length>0){
              $('#divGrdLstDocEmiAdmResult').find('select').val($('#pLenghDatatable').val());                           
            }            
                  
           $('#divGrdLstDocEmiAdmResult').find('select').change( function() { 
                $('#pLenghDatatable').val($('#divGrdLstDocEmiAdmResult').find('select').val());               
            });
            
              
        }, 
    BtnGridSelectionRowClick: function (that, row) {
      
            $.each(row, function (index, value) {  
                        jQuery('#txtpnuAnn').val(value.nuAnn);
                        jQuery('#txtpnuEmi').val(value.nuEmi);
                        jQuery('#txtptiCap').val(value.tiCap);
                        jQuery('#txtpnuDes').val(value.nuDes);
                        jQuery('#txtpcoPri').val(value.coPri);
            });
        },
    BtnGridEditarClick: function (row, data) {      
                        jQuery('#txtpnuAnn').val(data.nuAnn);
                        jQuery('#txtpnuEmi').val(data.nuEmi);
                        jQuery('#txtptiCap').val(data.tiCap);
                        jQuery('#txtpnuDes').val(data.nuDes);
                        jQuery('#txtpcoPri').val(data.coPri);
                        //editarDocumentoRecepClick(data.nuAnn, data.nuEmi,data.nuDes,data.coPri,data.existeDoc,data.existeAnexo); 
                        editarDocumentoEmiClick(data.nuAnn, data.nuEmi,data.existeDoc,data.existeAnexo);
    },
    BtnGridVerArchivoClick: function (row, data) {
      
        fn_verDocumento(data.nuAnn, data.nuEmi);    
    },
    BtnGridVerAnexoClick: function (row, data) { 
                        fn_verAnexo(data.nuAnn, data.nuEmi,data.nuDes);    
    },
    BtnGridVerDocOrigenClick: function (row, data) {
                        fn_verDocumentoInicialObj(data.nuAnn, data.nuEmi,data.nuDes);
    },
    BtnGridEnviarNotificacionClick: function (row, data) {
           fn_EnviarNotificacion(data.nuAnn, data.nuEmi,data.tiEnvMsj,'','','LISTA');
    },    
    BtnGridArchiveDocClick: function (row, data) {
           fn_ArchivarDocumentosMsj(data.nuAnn, data.nuEmi);
    },        
    BtnRecepDocumClick: function(){
        
    var lisSelect=[];
    var DocumRecepBE = new Object();
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }
    var validate=0;
    for (var i = 0; i <lisSelect.length ; i++) {
       if(lisSelect[0].esDocRec!=0){
           validate=1;
        }
    
    }
    if(validate==1)
    {
           alert_Info("Selección no válida :", "los documentos selecionados deben estar en el estado NO LEIDO");
           return false;
    }
     

        
        //console.log('lisSelect:'+lisSelect);
           bootbox.dialog({
        message: " <h5>Recibir Documentos Seleccionados ?</h5>",
        buttons: {
            SI: {
                label: "SI",
                className: "btn-primary",
                callback: function() {
                    
                    var currentdate = new Date(); 
                    var datetime =  ('0' + currentdate.getDate()).slice(-2) + "/"+
                                ('0' + (currentdate.getMonth() + 1)).slice(-2)+ "/"
                               + currentdate.getFullYear() + " "  
                               + currentdate.getHours() + ":"  
                               + currentdate.getMinutes();

                    for (var i = 0; i <lisSelect.length ; i++) {
                        DocumRecepBE.nuEmi = lisSelect[i].nuEmi;
                        DocumRecepBE.nuAnn = lisSelect[i].nuAnn;
                        DocumRecepBE.nuDes = lisSelect[i].nuDes;
                        DocumRecepBE.tiEmi = lisSelect[i].tiEmi;
                        DocumRecepBE.esDocRec = lisSelect[i].esDocRec;
                        DocumRecepBE.nuCorDes = lisSelect[i].nuCorDes;
                        DocumRecepBE.feRecDoc = datetime;
                        DocumRecepBE.feArcDoc = '';
                        DocumRecepBE.feAteDoc = '';
                        DocumRecepBE.deAne = ' ';
                        
                       
                        
                        ajaxCall("/srDocumentoAdmRecepcion.do?accion=goUpdDocumento&vAccion=1&documentoBean=",DocumRecepBE,function(data){
                         
                        }, 'json', true, true, "POST");  
                        
                    }
                
                    alert_Sucess("Recepción: ","Transacción completada con Exito.");    
                     $('#btn-buscar').click(); 
                        
                    
                }                        
            },
            NO: {
                label: "NO",
                className: "btn-default"
            }
        }
    });           
    },
    BtnEditarDocumentoRecepClick: function(){

    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede editar un documento a la vez");
           return false;
    }                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     editarDocumentoRecep();
    
    },
    BtnGridFirmarClick: function (row, data) {      
                        jQuery('#txtpnuAnn').val(data.nuAnn);
                        jQuery('#txtpnuEmi').val(data.nuEmi);
                        jQuery('#txtptiCap').val(data.tiCap);
                        jQuery('#txtpnuDes').val(data.nuDes);
                        jQuery('#txtpcoPri').val(data.coPri);
        fn_firmaDirectaDocumento();
       
        
    },    
    BtnGridImprimirActaClick: function (row, data) {      
                        jQuery('#txtpnuAnn').val(data.nuAnn);
                        jQuery('#txtpnuEmi').val(data.nuEmi);
                        jQuery('#txtptiCap').val(data.tiCap);
                        jQuery('#txtpnuDes').val(data.nuDes);
                        jQuery('#txtpcoPri').val(data.coPri);
        fn_ImprimirActaNotificacion('1');       
        
    },
    BtnGridImprimirActa2Click: function (row, data) {      
                        jQuery('#txtpnuAnn').val(data.nuAnn);
                        jQuery('#txtpnuEmi').val(data.nuEmi);
                        jQuery('#txtptiCap').val(data.tiCap);
                        jQuery('#txtpnuDes').val(data.nuDes);
                        jQuery('#txtpcoPri').val(data.coPri);
        fn_ImprimirActaNotificacion('2');       
        
    },
    BtnVerDocumentoClick: function(){

    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     fn_verDocumentoLista();
    
    },
    BtnVerAnexoClick: function(){
    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     fn_verAnexoRec();    
    },
    BtnVerSeguimientoClick: function(){
    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     fn_verSeguimiento();    
    },
    BtnVincularTemaClick: function(){
    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Emisión :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     fn_Vincular_TemaRecepcion();    
    },
    BtnVerAvanceRecepcionClick: function(){
    var lisSelect=[];
    
    lisSelect=base.Control.GrdResultado.GetSelectedData();
    
    if(lisSelect.length==0)
    {
           alert_Info("Recepción :", "Seleccione una fila de la lista");
           return false;
    }

    if(lisSelect.length>1)
    {
           alert_Info("Selección no válida :", "Solo se puede visualizar un documento a la vez");
           return false;
    }
                       
    jQuery('#txtpnuAnn').val(lisSelect[0].nuAnn);
    jQuery('#txtpnuEmi').val(lisSelect[0].nuEmi);
    jQuery('#txtptiCap').val(lisSelect[0].tiCap);
    jQuery('#txtpnuDes').val(lisSelect[0].nuDes);
    jQuery('#txtpcoPri').val(lisSelect[0].coPri);                                 
      
     fn_verAvanceRecepcion();    
    }  
        
};
base.Function = { 
    validarCkeck: function (data, type, full){
        //alert(full.esDocRec);
       // if(full.esDocRec==1)
       // {
          return true;         
       // }         
       // else
       // {
       //  return false;
       // }    
    },
    ValidarEstBtnEdit: function (data, type, full) {
       
            if ((full.deObsDoc==null||full.deObsDoc== '') && full.esDocEmi!=7) {
                return true;
            } else {
                return false;
            }
        },
    ValidarEstBtnEditGreen: function (data, type, full) {
        
            if (full.coPri==1&& (full.deObsDoc==null||full.deObsDoc== '')&& full.esDocEmi==7) {
                return true;
            } else {
                return false;
            }
        },
    ValidarEstBtnEditOrange: function (data, type, full) {
             if ( full.coPri==2&& (full.deObsDoc==null||full.deObsDoc== '')&& full.esDocEmi==7) {
                return true;
            } else {
                return false;
            }
        },
    ValidarEstBtnEditRed: function (data, type, full) {
             if (full.coPri==3 && (full.deObsDoc==null||full.deObsDoc== '')&& full.esDocEmi==7) {
                return true;
            } else {
                return false;
            }
        },
    ValidarEstBtnEditLightBlue: function (data, type, full) {
        
             if(full.deObsDoc != null&&full.deObsDoc != '') {
                return true;
            } else {
                return false;
            }
     },
     ValidarSendNotificationDanger: function (data, type, full) {
             if (full.docEstadoMsj=='-1'&& (full.esDocEmi==0 || full.esDocEmi==1)&& full.tiDest!='01' && full.tiEnvMsj=='-1') {
                return true;
            } else {
                return false;
            }
     },
     ValidarSendNotificationGreen: function (data, type, full) {
             if ((full.docEstadoMsj==0 && (full.esDocEmi==0 || full.esDocEmi==1)&& full.tiDest!='01')|| (full.tiEnvMsj==2 && full.docEstadoMsj=='-1')) {
                return true;
            } else {
                return false;
            }
     },
      ValidarArchiveDoc: function (data, type, full) {
             if ((full.docEstadoMsj==5 && full.esDocEmi!=4 && full.tiDest!='01') ||
                 (full.docEstadoMsj!='-1' && full.tiEnvMsj==1 && full.esDocEmi!="4" && full.tiDest!="01")) {
                return true;
            } else {
                return false;
            }
     },
     ValidarEstBtnFirmar: function (data, type, full) {

             if ($('#EsBtnFirmar').val()=='S'&& full.esDocEmi==7) {
                return true;
            } else {
                return false;
            }
     },
     ValidarEstBtnActaNotif: function (data, type, full) {
         if ((full.docEstadoMsj==0 && (full.esDocEmi==0 || full.esDocEmi==1)&& full.tiDest!='01')) {
                return true;
            } else {
                return false;
            }
     },
    CrearGrid: function () {
            var columns = new Array();
            
            var listaActionButtons = new Array();
            
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.DigitalSign, event: { on: 'click', callBack: base.Event.BtnGridFirmarClick}, validateRender: base.Function.ValidarEstBtnFirmar  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.Edit, event: { on: 'click', callBack: base.Event.BtnGridEditarClick}, validateRender: base.Function.ValidarEstBtnEdit  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.EditGreen, event: { on: 'click', callBack: base.Event.BtnGridEditarClick}, validateRender: base.Function.ValidarEstBtnEditGreen  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.EditOrange, event: { on: 'click', callBack: base.Event.BtnGridEditarClick}, validateRender: base.Function.ValidarEstBtnEditOrange  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.EditRed, event: { on: 'click', callBack: base.Event.BtnGridEditarClick}, validateRender: base.Function.ValidarEstBtnEditRed  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.EditLightBlue, event: { on: 'click', callBack: base.Event.BtnGridEditarClick}, validateRender: base.Function.ValidarEstBtnEditLightBlue  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.File, event: { on: 'click', callBack: base.Event.BtnGridVerArchivoClick } });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.PaperClip, event: { on: 'click', callBack: base.Event.BtnGridVerAnexoClick } });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.SendNotificationDanger, event: { on: 'click', callBack: base.Event.BtnGridEnviarNotificacionClick}, validateRender: base.Function.ValidarSendNotificationDanger  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.SendNotificationGreen, event: { on: 'click', callBack: base.Event.BtnGridEnviarNotificacionClick}, validateRender: base.Function.ValidarSendNotificationGreen  });
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.ArchiveDoc, event: { on: 'click', callBack: base.Event.BtnGridArchiveDocClick}, validateRender: base.Function.ValidarArchiveDoc});
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.ActaNotificacion, event: { on: 'click', callBack: base.Event.BtnGridImprimirActaClick}, validateRender: base.Function.ValidarEstBtnActaNotif  });    
            listaActionButtons.push({ type: Gob.Pcm.UI.Web.Components.GridAction.ActaNotificacion2, event: { on: 'click', callBack: base.Event.BtnGridImprimirActa2Click}, validateRender: base.Function.ValidarEstBtnActaNotif  }); 
            
            columns.push({
                data: 'fila',
                title: 'ACCIONES',
                class: "RowLeft",
                width: "10%",
                actionButtons: listaActionButtons                
            });

            
            columns.push({
                data: 'fila',
                title: 'N°',
                width: "3%",
                class: 'RowCenter',
                mRender: function (data, type, full) {
                    var fila=''; 
                    if(full.fila==null){
                        fila='';
                    }
                    else
                    {
                         fila=full.fila;
                    }
                   
                        if(full.coPri==3){
                            return '<a title="Muy Urgente"><div style="margin-left:7px; text-align:center; font-size: 1em; color:white; background-color: #D9534F; border-radius: 25px; width:25px; height:25px; line-height:25px;">'+fila+'</div></a>';
                        }
                        if(full.coPri==2){
                            return '<a title="Urgente"><div style="margin-left:7px; text-align:center; font-size: 1em; color:white; background-color: #F0AD4E; border-radius: 25px; width:25px; height:25px; line-height:25px;">'+fila+'</div></a>';                            
                        }
                         if(full.coPri==1){
                            return '<div  style="text-align:center; font-size: 1em;">'+fila+'</div>';
                        }                
                }
            });
            columns.push({
                data: 'fila',
                title: 'V°B°',
                width: "3%",
                class: 'RowCenter',
                mRender: function (data, type, full) {
                     
                        if(full.sFirmaVB==1){
                            return '<a title="Pendiente de Firma VB"><div style="margin-left:7px; text-align:center; font-size: 1em; color:white; background-color: #D9534F; border-radius: 25px; width:25px; height:25px; line-height:25px;">VB</div></a>';
                        }
                        else if(full.sFirmaVB==2){
                            return '<a title="Observado VB"><div style="margin-left:7px; text-align:center; font-size: 1em; color:white; background-color: #F0AD4E; border-radius: 25px; width:25px; height:25px; line-height:25px;">VB</div></a>';
                        }
                        else if(full.sFirmaVB==0){
                            return '<a title="Firmado VB"><div style="margin-left:7px; text-align:center; font-size: 1em; color:white; background-color: #5cb85c; border-radius: 25px; width:25px; height:25px; line-height:25px;">VB</div></a>';                            
                        }
                        else  {
                            return '';
                        }                
                }
            });
            columns.push({
                data: 'deEmiRef',
                title: 'REFERENCIA ORIGEN',
                class: "RowLeft",
                width: "10%"
            });
            
            columns.push({
                data: 'feEmiCorta',
                title: 'FECHA Elabora/Emisión',
                class: "RowCenter",
                width: "4%"
            });
            columns.push({
                data: 'deTipDocAdm',
                title: 'TIPO DOC.',
                width: "6%",
                class:'RowLeft'
            });

            columns.push({
                data: 'nuDoc',
                title: 'N° DOCUMENTO.',
                width: "8%",
                class:'RowLeft'
            });

            columns.push({
                data: 'deAsuM',
                title: 'ASUNTO',
                class: "RowJustify",
                width: "25%"
            });
            
            if($('#sEstadoDoc').val()=='0'){
            columns.push({
                data: 'deEmpPro',
                title: 'ASIGNADO A',
                class: "RowLeft",
                width: "14%"
            });
             }
             else
            {
                columns.push({
                data: 'deEmpPro',
                title: 'DESTINATARIO',
                class: "RowJustify",
                width: "14%"
            }); 
             }
             
            columns.push({
                data: 'deEmpRes',
                title: 'ELABORADO POR',
                class: "RowLeft",
                width: "8%"
            });
             
            columns.push({
                data: 'nuExpediente',
                title: 'NRO EXPEDIENTE',
                width: "7%"
            });
       
           columns.push({
                data: 'deEsDocEmi',
                title: 'ESTADO',
                width: "6%",
                class:'RowCenter'
            }); 
            
            columns.push({
                data: 'recursoenvio',
                title: 'ENVÍO',
                width: "7%",
                class:'RowCenter'
            }); 
             

            base.Control.GrdResultado = new Gob.Pcm.UI.Web.Components.Grid({
                renderTo: 'divGrdLstDocEmiAdmResult',
                columns: columns,
                columnDefs: columns,
                hasSelectionRows: false,
                hasSelectionCell:true,
                selectionRowsEvent: { on: 'click', callBack: base.Event.BtnGridSelectionRowClick},  
                ordering: true,          
                columnDefs: [{ "targets": 0, "orderable": false },{ "targets": 1, "orderable": false },{ "targets": 2, "orderable": false }, { "targets": 11, "orderable": false }],
                proxy: {
                    url: pRutaContexto + "/" + pAppVersion +'/srDocumentoAdmEmision.do?accion=goListaBusqDocumEmiAdmJson',                    
                    source: 'result'
                },
                //returnCallbackComplete: base.Event.AjaxGrdResultadoSuccess,
            });
        }   
         };

        
};


ns('Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index');
try {
    $(document).ready(function () {
        'use strict';
        Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index.Vista = new Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index.Controller();
        Gob.Pcm.SGD.Presentacion.EmiDocumAdm.Index.Vista.Ini();
    });
} catch (ex) {
    alert(ex);
}




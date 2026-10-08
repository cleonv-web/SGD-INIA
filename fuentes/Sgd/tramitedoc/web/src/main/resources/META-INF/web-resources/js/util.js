var public_enteros="0123456789";var public_letras="ABCDEFGHIJKLMNÑÃ‘OPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0123456789_- ";var altf4=false;var isIntroLastKey=false;var mousePoint={clientX:0,clientY:0};if(is_chrome()){document.onmouseout=mousefuera;document.onmousemove=movimientomouse;}
function lTrim(a){while(a.charAt(0)==" ")a=a.substr(1,a.length-1);return a}function rTrim(a){while(a.charAt(a.length-1)==" ")a=a.substr(0,a.length-1);return a}function allTrim(a){return rTrim(lTrim(a))}function tamanoVentanaNavegador(){width=0;height=0;if(typeof(window.innerWidth)=='number'){width=window.innerWidth;height=window.innerHeight}else if(document.documentElement&&(document.documentElement.clientWidth||document.documentElement.clientHeight)){width=document.documentElement.clientWidth;height=document.documentElement.clientHeight}else if(document.body&&(document.body.clientWidth||document.body.clientHeight)){width=document.body.clientWidth;height=document.body.clientHeight}return{ancho:width,alto:height}}
function fechaActual(){var a=new Date();var b=a.getYear();var c=(b<1000)?b+1900:b;a.setHours(0);var d=a.getUTCDate();var e=(d<10)?"0"+d:d;var f=a.getMonth()+1;var g=(f<10)?"0"+f:f;var h=e+"/"+g+"/"+c;return h}
function isValidDate(fecha){var retval=true;var pattern=/([0-9]{2})\/([0-9]{2})\/([0-9]{4})/;var res=pattern.exec(fecha);var fecha_regreso_aux='';var errores=0;if(!res){retval=false}else{var dia=res[1];var mes=res[2];var ano=res[3];if(dia<1||dia>31){retval=false}if(mes<1||mes>12){retval=false}if(ano<2005||ano>3000){retval=false}}return retval}
function redondear(valor){return Math.round(valor*Math.pow(10,2))/Math.pow(10,2)}function replicate(str,len){var x='';if(str){var y=len-str.length;for(i=0;i<y;i++){x+="0"}}return x+str}function nombreMes(codigo){switch(codigo){case"01":return"ENERO";case"02":return"FEBRERO";case"03":return"MARZO";case"04":return"ABRIL";case"05":return"MAYO";case"06":return"JUNIO";case"07":return"JULIO";case"08":return"AGOSTO";case"09":return"SETIEMBRE";case"10":return"OCTUBRE";case"11":return"NOVIEMBRE";case"12":return"DICIEMBRE"}}
function detectVersion(){version=parseInt(navigator.appVersion);return version}function detectOS(){if(navigator.userAgent.indexOf('Win')==-1){OS='Macintosh'}else{OS='Windows'}return OS}function detectBrowser(){if(navigator.appName.indexOf('Netscape')==-1){browser='IE'}else{browser='Netscape'}return browser}function showPopupWindow(a,w,h){var A=(screen.availHeight/2)-(h/2);var L=(screen.availWidth/2)-(w/2);open(a,"","scrollbars=yes, resizable=yes, status=0, toolbar=0, location=0, menubar=0, titlebar=false,width="+w+",height="+h+",top="+A+",left="+L)}
function cerrarventana(evt){var oEvent=(window.event)?window.event:evt;if(!oEvent.clientY){if(altf4==true){jQuery.post(unloadUrl)}else if(is_chrome()&&(mousePoint.clientX==0)&&(mousePoint.clientY==0)&&!isIntroLastKey){jQuery.post(unloadUrl)}else if((oEvent.explicitOriginalTarget.tagName=='undefined')||(oEvent.explicitOriginalTarget.tagName=="HTML")){jQuery.post(unloadUrl)}}else{if(altf4==true){jQuery.post(unloadUrl)}else if((oEvent.clientY<0)&&(!isIntroLastKey)){jQuery.post(unloadUrl)}}}
function presionaTecla(event){var oEvent=(window.event)?window.event:event;var k=oEvent.keyCode||oEvent.which;if(oEvent.altKey==true&&k==115){altf4=true}if(k==13){isIntroLastKey=true;setTimeout("isIntroLastKey=false",1000);}else{isIntroLastKey=false}}
function is_chrome(){return navigator.userAgent.toLowerCase().indexOf('chrome') > -1;}
function movimientomouse(event){var evento=(window.event)?window.event:event;mousePoint.clientX=evento.clientX;mousePoint.clientY=evento.clientY}
function mousefuera(){mousePoint.clientX=0;mousePoint.clientY=0;}
var KeyboardClass=function(pEv,pOkeys){this.anykeys=((typeof(pOkeys)=='string')&&(pOkeys.length>0))?pOkeys:' ./|-()*"0123456789abcdefghijklmnñopqrstuvwxyzABCDEFGHIJKLMNÑOPQRSTUVWXYZ';this.espKeysList=(typeof(public_esp_keys)!='undefined')?public_esp_keys:[8,13,27,37,39,46];this.objEvent=(window.event)?window.event:pEv;this.currentObj=this.objEvent.target||this.objEvent.srcElement;this.k=this.objEvent.keyCode||this.objEvent.which;this.character=String.fromCharCode(this.k);this.isSpecialKey=function(){for(var i in this.espKeysList){if(this.k==this.espKeysList[i]){return true}}return false};this.isIntro=function(){if((this.k==13)||(this.k==9)||(this.k==0)){return true}return false};this.isHelp=function(){if(this.k==113){return true}return false};this.isValidKey=this.anykeys.indexOf(this.character)!=-1||this.isSpecialKey()}
var public_apenom="ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕabcdefghijklmnñopqrstuvwxyzäëïöüáéíóúàèìòùâêîôûãõ-' .0123456789";
var public_direc="ABCDEFGHIJKLMNÑOPQRSTUVWXYZÄËÏÖÜÁÉÍÓÚÀÈÌÒÙÂÊÎÔÛÃÕabcdefghijklmnñopqrstuvwxyzäëïöüáéíóúàèìòùâêîôûãõ.0123456789-' :/(),"+'"';
var public_textual="ABCDEFGHIJKLMNÑOPQRSTUVWXYZÁÉÍÓÚabcdefghijklmnñopqrstuvwxyzáéíóú 0123456789";
var public_textual_date="0123456789/";
var public_textual_hour="0123456789:";
//INICIO FERNANDO TICONA
//Array para dar formato en español
  $.datepicker.regional['es'] = 
  {
  closeText: 'Cerrar', 
  prevText: 'Previo', 
  nextText: 'Próximo',
  
  monthNames: ['Enero','Febrero','Marzo','Abril','Mayo','Junio',
  'Julio','Agosto','Septiembre','Octubre','Noviembre','Diciembre'],
  monthNamesShort: ['Ene','Feb','Mar','Abr','May','Jun',
  'Jul','Ago','Sep','Oct','Nov','Dic'],
  monthStatus: 'Ver otro mes', yearStatus: 'Ver otro año',
  dayNames: ['Domingo','Lunes','Martes','Miércoles','Jueves','Viernes','Sábado'],
  dayNamesShort: ['Dom','Lun','Mar','Mie','Jue','Vie','Sáb'],
  dayNamesMin: ['Do','Lu','Ma','Mi','Ju','Vi','Sá'],
  dateFormat: 'dd/mm/yy', firstDay: 0, 
  initStatus: 'Selecciona la fecha', isRTL: false};
 $.datepicker.setDefaults($.datepicker.regional['es']);
 
 
 
 //FIN FERNANDO TICONA
 
function eventEvaluate(evt){
    oEvent= (evt) ? evt : ((event) ? event : null);
}
function fu_getTextUpperCase(pIdObjeto) {
    var cadena = document.getElementById(pIdObjeto).value;
    if (cadena==null || cadena=="null" || cadena=="" || cadena=="-1") {return "";}
    else {return cadena.toUpperCase();}
}

function fu_getTextCadenaLogin(pIdObjeto){
    var cadena = document.getElementById(pIdObjeto).value;
    if (cadena==null || cadena=="null" || cadena=="" || cadena=="-1") {return "";}
    else {return cadena;}    
}
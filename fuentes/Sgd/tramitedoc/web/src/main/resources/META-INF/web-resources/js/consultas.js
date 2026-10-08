function fn_cargarRecepcionDoc() {
    var p = new Array();
    p[0] = "accion=goCargarRecepcionDoc";
    ajaxCall("/srConsultaRecepcionDoc.do", p.join("&"), function(data) {
        refreshScript("divRecepDocMain", data);
    }, 'text', false, false, "POST");
}
function fn_cargarListaDocumentos() {
    var rbOpcionFecha = $("#fecha").attr("rbop");
    var conAnioList = $("#conAnioList").val();
    var conMesList = $("#conMesList").val();
    var conDiaList = $("#conDiaList").val();
    var conFInicial = $("#fecha").attr("fini");
    var conFFinal = $("#fecha").attr("ffin");

    var conDestinatariosList = $("#conDestinatariosList").val();
    var conTipoRemitenteList = $("#conTipoRemitenteList").val();
    var conExpedienteList = $("#conExpedienteList").val();
    var conRemitenteList = $("#conRemitenteList").val();
    var conEstadoList = $("#conEstadoList").val();
    var conTipoDocumentoList = $("#conTipoDocumentoList").val();
//    "conFInicial": conFInicial,
//                "conFFinal": conFFinal,
    var jsonBody =
            {
                "rbOpcionFecha": rbOpcionFecha,
                "conAnioList": conAnioList,
                "conMesList": conMesList,
                "conDiaList": conDiaList,
                
                "conDestinatariosList": conDestinatariosList,
                "conTipoRemitenteList": conTipoRemitenteList,
                "conExpedienteList": conExpedienteList,
                "conRemitenteList": conRemitenteList,
                "conEstadoList": conEstadoList,
                "conTipoDocumentoList": conTipoDocumentoList
            };
    var jsonString = JSON.stringify(jsonBody);
    return;
    var url = "/srConsultaRecepcionDoc.do?accion=goCargarListaDoc";
    ajaxCallSendJson(url, jsonString, function(data) {
       bootbox.alert(data);
//        if (data === "Datos guardados.") {
//            alert_Sucess("MENSAJE:", data);
//            fn_cargarListaProveedores();
//        } else {
//            alert_Danger("ERROR", "No se pudo guardar, POSIBLE DATOS EXISTENTES.");
//        }
    }, 'text', false, true, "POST");

}
$("#btnPrueba").click(function(){
   bootbox.alert("asd");
});
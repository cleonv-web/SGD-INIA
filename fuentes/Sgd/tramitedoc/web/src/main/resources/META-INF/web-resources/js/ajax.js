function crearXMLHttpRequest() {
    if (window.XMLHttpRequest) { 
        return new XMLHttpRequest();
    }
    else { 
        try {
            return new ActiveXObject("Msxml2.XMLHTTP");
        }
        catch (e) {
            try {
                return new ActiveXObject("Microsoft.XMLHTTP");
            }
            catch (e) {
               alert(e);
               alert("Intente nuevamente...");
            }
        }
    }
    return null;
}
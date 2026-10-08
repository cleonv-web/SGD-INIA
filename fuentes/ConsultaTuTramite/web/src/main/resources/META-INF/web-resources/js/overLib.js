var ns4 = document.layers;
var ie4 = document.all;
var ns6 = document.getElementById && !document.all;
var dragswitch = 0;
var nsx;
var nsy;
var nstemp;
function drag_dropns(name) {
    var tt=name.parentNode.parentNode;
    if (!ns4) {
        return;
    }
    temp = eval(tt);
    temp.captureEvents(Event.MOUSEDOWN | Event.MOUSEUP);
    temp.onmousedown = gons;
    temp.onmousemove = dragns;
    temp.onmouseup = stopns;
}
function gons(e) {
    temp.captureEvents(Event.MOUSEMOVE);
    nsx = e.x;
    nsy = e.y;
}
function dragns(e) {
    if (dragswitch == 1) {
        temp.moveBy(e.x - nsx, e.y - nsy);
        return false;
    }
}
function stopns() {
    temp.releaseEvents(Event.MOUSEMOVE);
}
function drag_drop(e) {
    if (ie4 && dragapproved) {
        crossobj.style.left = tempx + event.clientX - offsetx;
        crossobj.style.top = tempy + event.clientY - offsety;
        return false;
    } else if (ns6 && dragapproved) {
        crossobj.style.left = tempx + e.clientX - offsetx + "px";
        crossobj.style.top = tempy + e.clientY - offsety + "px";
        return false;
    }
}
function initializedrag(e, firedobj) {
    crossobj = firedobj.parentNode.parentNode;
    offsetx = ie4 ? event.clientX : e.clientX;
    offsety = ie4 ? event.clientY : e.clientY;
    tempx = parseInt(crossobj.style.left);
    tempy = parseInt(crossobj.style.top);
    dragapproved = true;
    document.onmousemove = drag_drop;
}
document.onmouseup = new Function("dragapproved=false");
function hidebox(div) {
    crossobj = ns6 ? document.getElementById(div) : eval("document.all." + div);
    if (ie4 || ns6) {
        crossobj.style.display = "none";
    } else if (ns4) {
        eval("document." + div + ".display=none");
    }
}
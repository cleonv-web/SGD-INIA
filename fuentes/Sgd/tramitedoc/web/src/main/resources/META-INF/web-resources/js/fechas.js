function formato_fecha(dator){
var longitud=dator.length;
if(!longitud) {return;}
var modifica=0;
var positor=dator.search('/');
var charcero=dator.charAt(0);
var chartres=dator.charAt(3);
var charcuatro=dator.charAt(4);
var charseis=dator.charAt(6);
var mayorcero=parseInt(charseis);
switch(longitud){
case 2: if(positor == 1) {dator='0'+charcero+'/'; break;}
if(nonumber(dator.charAt(1))) {dator=charcero; break}
dator+='/'; rango_fecha(dator); break;
case 5: if(charcuatro == '/') { dator=dator.substr(0,3)+'0'+chartres+'/'; break;}
if(nonumber(charcuatro)) { modifica=4; break;}
dator+='/'; rango_fecha(dator); break;
case 1: if(charcero > 3) {dator='0'+charcero+'/'; break;}
if(nonumber(charcero)) dator='';
break;
case 4: if(chartres > 1) dator=dator.substr(0,3)+'0'+chartres+'/';
if(nonumber(chartres)) modifica=3;
break;
case 7: if(!mayorcero || (nonumber(charseis))) modifica=6; break;
case 8: if(nonumber(dator.charAt(7))) modifica=7; break;
case 9: if(nonumber(dator.charAt(8))) modifica=8; break;
case 10: if(nonumber(dator.charAt(9))) modifica=9; break
rango_fecha(dator); break;
}
if(modifica) dator=dator.substr(0,modifica);
}

function nonumber(digito){
if(digito < '0' || digito > '9') {return true;}
return false;
}

function rango_fecha(dator){
var dt=dator.length;
var barra_a='/';
var barra_b='/';
if(!dt) {return;}
var st=dator.split('/');
if(dt > 2) {if(st[0] > 31) {st[0]=31;}}
if(dt > 5) {
if(st[1] > 12) {st[1]=12;}
if((st[1] == 4) || (st[1] == 6) || (st[1] == 9) || (st[1] == 11)){
if(st[0] > 30) {st[0]=30;}
}
}
if(dt > 9){
if(st[2] > 2500) {st[2]='2011';}
if(st[1] == 2){
if (!(st[2] % 4) && (st[2] % 100) || !(st[2] % 400)){
if(st[0] > 29) {st[0]=29;}
}
else {
if(st[0] > 28) {st[0]=28;}
}
}
}
if(dt < 7) {st[2]='';}
if(dt < 6) {barra_a='';}
if(dt < 4) {st[1]='';}
if(dt < 3) {barra_b='';}
dator=st[0]+barra_a+st[1]+barra_b+st[2];
return st[0];
}
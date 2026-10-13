/* Mesa de Partes INIA. El código y los archivos se conservan ante errores. */
(function ($) {
    'use strict';
    $(function () {
        var form = document.getElementById('mpvdocBean');
        if (!form) return;
        var context = form.getAttribute('data-context');
        var step = 0, principal = null, anexos = [], uploading = 0, sending = false;
        var captchaToken = '', captchaUrl = '', captchaRequest = null, captchaSequence = 0;
        var maxDoc = Number($('#fileSizeMaxDocumento').val()), maxAnnex = Number($('#fileSizeMaxAnexo').val()) * 1024 * 1024;
        var provinceRequest = null, districtRequest = null;
        function message(text) { $('#portalMessage').text(text).prop('hidden', !text); if (text) document.getElementById('portalMessage').focus(); }
        function size(bytes) { return (bytes / 1024 / 1024).toFixed(bytes >= 1024 * 1024 ? 1 : 2) + ' MB'; }
        $('.max-document,#limitePrincipal').text(size(maxDoc)); $('.max-annex,#limiteAnexo').text(size(maxAnnex));
        function busy() { $('#enviarExpediente,#pasoSiguiente,#btnUpload,#btnUploadAnexo').prop('disabled', sending || uploading > 0); $('#mpvdocBean').attr('aria-busy', sending || uploading > 0 ? 'true' : 'false'); }
        function validatePanel(index) {
            var fields = document.querySelectorAll('[data-panel="' + index + '"] input,[data-panel="' + index + '"] select,[data-panel="' + index + '"] textarea');
            for (var i = 0; i < fields.length; i++) {
                if (fields[i].type === 'file' || fields[i].disabled || fields[i].closest('[hidden]')) continue;
                if (fields[i].required && fields[i].type !== 'checkbox' && !fields[i].value.trim()) {
                    fields[i].setCustomValidity('Completa este campo.');
                } else fields[i].setCustomValidity('');
                if (!fields[i].checkValidity()) { fields[i].reportValidity(); return false; }
            }
            if (index === 0 && (!$('#pro').val() || !$('#dis').val())) { message('Selecciona departamento, provincia y distrito.'); return false; }
            if (index === 1 && !principal) { message('Adjunta el documento principal en PDF antes de continuar.'); return false; }
            return true;
        }
        function summary() {
            var root = $('#resumenEnvio').empty();
            function item(label, value, wide) { var box = $('<div>').toggleClass('wide', !!wide); $('<dt>').text(label).appendTo(box); $('<dd>').text(value).appendTo(box); root.append(box); }
            item('Solicitante', ($('#nom').val() + ' ' + $('#pat').val() + ' ' + $('#mat').val()).trim());
            item('Correo de contacto', $('#correo').val());
            if ($('#idTipoPersona').val() === '02') item('Organización', $('#raz').val(), true);
            item('Documento', $('#tipoDocumento option:selected').text() + ' · ' + $('#nrodoc').val());
            item('Folios', $('#folios').val()); item('Asunto', $('#asu').val(), true);
            item('Archivo principal', principal ? principal.name : '', true); item('Anexos', anexos.length + ' archivo(s)');
        }
        function showStep(index, scroll) {
            step = index; $('[data-panel]').each(function () { $(this).prop('hidden', Number($(this).attr('data-panel')) !== index); });
            $('[data-step]').each(function () { var n = Number($(this).attr('data-step')); $(this).toggleClass('active', n === index).toggleClass('complete', n < index); if (n === index) $(this).attr('aria-current','step'); else $(this).removeAttr('aria-current'); });
            $('#stepCount').text('Paso ' + (index + 1) + ' de 3'); $('#pasoAnterior').prop('hidden', index === 0); $('#pasoSiguiente').prop('hidden', index === 2); $('#enviarExpediente').prop('hidden', index !== 2);
            message(''); if (index === 2) { summary(); if (!captchaToken) refreshCaptcha(); }
            if (scroll) document.getElementById('presentacion').scrollIntoView({block:'start',behavior:window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth'});
        }
        function goStep(next) { if (uploading || sending) return; if (next > step) { for (var i = step; i < next; i++) if (!validatePanel(i)) return; } showStep(next, true); }
        $('[data-step]').on('click', function () { goStep(Number($(this).attr('data-step'))); });
        $('#pasoSiguiente').on('click', function () { goStep(step + 1); }); $('#pasoAnterior').on('click', function () { goStep(step - 1); });
        $('#idTipoPersona').on('change', function () { var legal = this.value === '02'; $('#divRuc').prop('hidden', !legal); $('#ruc,#raz').prop('required', legal).prop('disabled', !legal); }).trigger('change');
        $('#asu').on('input', function () { $('#contadoasunto').text(900 - this.value.length); });
        $('input,textarea,select').on('input change', function () { if (this.setCustomValidity) this.setCustomValidity(''); });
        function emptySelect(id, disabled) { $(id).empty().append($('<option>').val('').text('Selecciona')).prop('disabled', disabled); }
        $('#dep').on('change', function () {
            if (provinceRequest) provinceRequest.abort(); if (districtRequest) districtRequest.abort(); emptySelect('#pro',true); emptySelect('#dis',true);
            var selected = this.value; if (!selected) return;
            provinceRequest = $.ajax({url:context + '/inicio.do',type:'POST',dataType:'json',timeout:20000,data:{accion:'goBuscaProvinciaJson',pcodigo:selected}}).done(function (list) {
                if ($('#dep').val() !== selected) return; $.each(list || [],function (_,row) { $('#pro').append($('<option>').val(row.codigo).text(row.descripcion)); }); $('#pro').prop('disabled',false);
            }).fail(function (_,status) { if (status !== 'abort') message('No se pudieron cargar las provincias. Vuelve a seleccionar el departamento.'); });
        });
        $('#pro').on('change', function () {
            if (districtRequest) districtRequest.abort(); emptySelect('#dis',true); var selected = $('#dep').val() + this.value; if (!this.value) return;
            districtRequest = $.ajax({url:context + '/inicio.do',type:'POST',dataType:'json',timeout:20000,data:{accion:'goBuscaDistritoJson',pcodigo:selected}}).done(function (list) {
                if ($('#dep').val() + $('#pro').val() !== selected) return; $.each(list || [],function (_,row) { $('#dis').append($('<option>').val(row.codigo).text(row.descripcion)); }); $('#dis').prop('disabled',false);
            }).fail(function (_,status) { if (status !== 'abort') message('No se pudieron cargar los distritos. Vuelve a seleccionar la provincia.'); });
        });
        // La consulta de identidad es opcional: no borra nombres ingresados si no encuentra a la persona.
        $('#dni').on('change', function () {
            var dni = this.value; if (!/^\d{8}$/.test(dni)) return;
            $.ajax({url:context + '/inicio.do',type:'POST',dataType:'json',timeout:15000,data:{accion:'goBuscaCiudadano',pnuDoc:dni}}).done(function (data) {
                if ($('#dni').val() !== dni || !data || data.coRespuesta !== '1' || !data.ciudadanoBean) return;
                $.each({nom:'nombre',pat:'paterno',mat:'materno'},function (id,key) { if (!$('#'+id).val()) $('#'+id).val(data.ciudadanoBean[key] || ''); });
            });
        });
        $('#ruc').on('change', function () { var ruc = this.value; if (!/^\d{11}$/.test(ruc)) return;
            $.ajax({url:context + '/inicio.do',type:'POST',dataType:'json',timeout:15000,data:{accion:'goBuscaProveedorJson',pnuRuc:ruc}}).done(function (data) { if ($('#ruc').val() === ruc && !$('#raz').val() && data && data.coRespuesta === '1' && data.proveedorBean) $('#raz').val(data.proveedorBean.deRuc || ''); });
        });
        function renderFiles() {
            $('#documentoListado,#anexosListado').empty();
            function row(file, main) { var box = $('<div class="file-row">'); var name = $('<div class="file-name">').text(file.name); $('<small>').text(file.fileSize).appendTo(name); box.append(name); $('<button type="button" class="remove-file">').text('Quitar').attr('aria-label','Quitar ' + file.name).on('click',function () { if (uploading || sending) return; if (main) principal = null; else { uploading++; busy(); $.ajax({url:context + '/goRemoveAnexo',type:'POST',dataType:'json',data:{id:file.id}}).done(function () { anexos = anexos.filter(function (x) { return x.id !== file.id; }); renderFiles(); }).fail(function () { message('No se pudo quitar el anexo. Intenta nuevamente.'); }).always(function () { uploading--; busy(); }); return; } renderFiles(); }).appendTo(box); $(main ? '#documentoListado' : '#anexosListado').append(box); }
            if (principal) row(principal,true); else $('#documentoListado').append($('<p class="empty-file">').text('Aún no has adjuntado el documento principal.'));
            anexos.forEach(function (file) { row(file,false); });
        }
        function upload(file, main) {
            if (!main && anexos.length >= 20) return $.Deferred().reject('Puedes adjuntar hasta 20 anexos. Quita uno antes de agregar otro.').promise();
            if (!(main ? /\.pdf$/i : /\.(pdf|doc|docx|jpg|jpeg)$/i).test(file.name)) return $.Deferred().reject('Formato no permitido. Usa ' + (main ? 'PDF para el documento principal.' : 'PDF, Word o JPG para los anexos.')).promise();
            if (file.size === 0 || file.size > (main ? maxDoc : maxAnnex)) return $.Deferred().reject('El archivo "' + file.name + '" está vacío o supera el máximo de ' + size(main ? maxDoc : maxAnnex) + '.').promise();
            var data = new FormData(); data.append('files[]',file); uploading++; busy(); $('#uploadStatus').text('Cargando ' + file.name + '…');
            return $.ajax({url:context + (main ? '/goUploadDoc' : '/goUploadDocAnexos'),type:'POST',data:data,processData:false,contentType:false,dataType:'json',timeout:120000}).then(function (result) {
                if (!result || !result[0] || !result[0].name) return $.Deferred().reject('La carga no pudo confirmarse. Vuelve a seleccionar el archivo.').promise();
                if (main) principal = result[0]; else anexos.push(result[0]); renderFiles();
            },function (xhr) { if (xhr.responseJSON && xhr.responseJSON.error) return $.Deferred().reject(xhr.responseJSON.error).promise(); return $.Deferred().reject('No se pudo cargar "' + file.name + '". Revisa el tamaño y la conexión e intenta nuevamente.').promise(); }).always(function () { uploading--; busy(); $('#uploadStatus').text(uploading ? 'Cargando archivos…' : ''); });
        }
        $('#btnUpload').on('click',function () { document.getElementById('fileuploadAlta').click(); }); $('#btnUploadAnexo').on('click',function () { document.getElementById('fileuploadAltaAnexos').click(); });
        $('#fileuploadAlta').on('change',function () { if (!this.files[0]) return; message(''); upload(this.files[0],true).fail(message); this.value = ''; });
        $('#fileuploadAltaAnexos').on('change',function () { var files = Array.prototype.slice.call(this.files); this.value = ''; message(''); var chain = $.Deferred().resolve().promise(); files.forEach(function (file) { chain = chain.then(function () { return upload(file,false); }); }); chain.fail(message); });
        function refreshCaptcha() {
            var sequence = ++captchaSequence; if (captchaRequest) captchaRequest.abort(); captchaToken = ''; $('#textoCaptcha').val(''); $('#refreshCaptcha').prop('disabled',true); $('#imagenCaptcha').prop('hidden',true); $('#captchaStatus').text('Cargando código…').prop('hidden',false);
            var xhr = new XMLHttpRequest(); captchaRequest = xhr; xhr.open('GET',context + '/jcaptcha?t=' + Date.now(),true); xhr.responseType = 'blob'; xhr.timeout = 20000;
            function failed() { if (sequence !== captchaSequence) return; $('#refreshCaptcha').prop('disabled',false); $('#captchaStatus').text('No se pudo cargar la imagen. Solicita otro código.'); }
            xhr.onload = function () {
                if (sequence !== captchaSequence) return; var token = xhr.getResponseHeader('X-Captcha-Token'); if (xhr.status !== 200 || !token || !xhr.response || !/^image\/png(?:\s*;|$)/i.test(xhr.response.type)) { failed(); return; }
                var nextUrl = URL.createObjectURL(xhr.response), image = document.getElementById('imagenCaptcha');
                image.onload = function () { if (sequence !== captchaSequence) { URL.revokeObjectURL(nextUrl); return; } if (captchaUrl) URL.revokeObjectURL(captchaUrl); captchaUrl = nextUrl; captchaToken = token; $('#imagenCaptcha').prop('hidden',false); $('#captchaStatus').prop('hidden',true); $('#refreshCaptcha').prop('disabled',false); };
                image.onerror = function () { URL.revokeObjectURL(nextUrl); failed(); }; image.src = nextUrl;
            }; xhr.onerror = failed; xhr.ontimeout = failed; xhr.send();
        }
        $('#refreshCaptcha').on('click',refreshCaptcha);
        $('#textoCaptcha').on('input',function () { this.value = this.value.toUpperCase().replace(/[^A-Z0-9]/g,''); });
        var dialog = document.getElementById('terminosDialog'); $('#verTerminos').on('click',function () { dialog.showModal(); }); $('#cerrarTerminos,#entendidoTerminos').on('click',function () { dialog.close(); });
        function buildPayload() { var data = {}; ['idTipoPersona','ruc','raz','dni','pat','mat','nom','tel','correo','dir','dep','pro','dis','tipoDocumento','nrodoc','folios','asu','textoCaptcha'].forEach(function (id) { data[id] = ($('#'+id).val() || '').trim(); }); data.captchaToken = captchaToken; data.aceptaTerminos = $('#aceptaTerminos').prop('checked'); data.nombreArchivo = principal ? principal.name : ''; data.listAnexos = anexos.map(function (f) { return f.id; }); return data; }
        $(form).on('submit',function (event) {
            event.preventDefault(); if (step !== 2) { goStep(step + 1); return; } if (sending || uploading) return;
            for (var i = 0; i < 3; i++) { if (i !== step) showStep(i,false); if (!validatePanel(i)) return; }
            showStep(2,false); if (!captchaToken) { message('Espera a que cargue el código de verificación o solicita otro.'); return; }
            sending = true; busy(); $('#enviarExpediente').text('Registrando…'); message('');
            $.ajax({url:context + '/inicio.do?accion=goInicio',type:'POST',contentType:'application/json; charset=UTF-8',dataType:'json',data:JSON.stringify(buildPayload()),timeout:120000}).done(function (data) {
                if (data && data.coRespuesta === '1') {
                    $('#numeroExpediente').text(data.expediente); var cargo = $('#linkReporteCargo').val() + $.param({cargNuAnn:data.anio,cargNuEmi:data.iod}); $('#descargarCargo').attr('href',cargo);
                    $(form).prop('hidden',true); $('.stepper,#stepCount').prop('hidden',true); $('#registroExitoso').prop('hidden',false); document.getElementById('registroExitoso').focus();
                } else { message(data && data.deRespuesta ? data.deRespuesta : 'No se pudo confirmar el registro.'); refreshCaptcha(); }
            }).fail(function () { message('No pudimos confirmar el resultado del envío. Consulta tu trámite antes de volver a enviar para evitar duplicados. Tus datos permanecen en pantalla.'); refreshCaptcha(); }).always(function () { sending = false; busy(); $('#enviarExpediente').text('Enviar documento →'); });
        });
        $('#nuevoTramite').on('click',function () { window.location.href = context + '/inicio.do'; });
        showStep(0,false);
    });
}(jQuery));

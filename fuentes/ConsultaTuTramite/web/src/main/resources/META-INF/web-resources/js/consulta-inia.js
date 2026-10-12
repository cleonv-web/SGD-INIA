(function () {
    'use strict';
    var busy = false, captchaRequest = null, captchaUrl = null, captchaReady = false;
    function el(id) { return document.getElementById(id); }
    function text(id, value) { el(id).textContent = value; }
    function updateButton() { el('searchExpediente').disabled = busy || !captchaReady; }
    function fieldError(id, message, target) {
        el(id).setAttribute('aria-invalid', message ? 'true' : 'false');
        text(target, message);
    }
    window.recargarCaptcha = function () {
        if (!el('imagenCaptcha')) return;
        if (captchaRequest) captchaRequest.abort();
        captchaReady = false;
        el('captchaToken').value = '';
        el('textoCaptcha').value = '';
        el('refreshCaptcha').disabled = true;
        el('imagenCaptcha').removeAttribute('src');
        el('imagenCaptcha').style.visibility = 'hidden';
        text('captcha-status', 'Generando código de seguridad…');
        updateButton();
        var request = new XMLHttpRequest();
        captchaRequest = request;
        request.open('GET', pRutaContexto + '/jcaptcha?t=' + Date.now(), true);
        request.responseType = 'blob';
        request.timeout = 15000;
        function failed() {
            captchaRequest = null;
            el('refreshCaptcha').disabled = false;
            text('captcha-status', 'No se pudo cargar el código. Pulsa ↻ para volver a intentar.');
            updateButton();
        }
        request.onload = function () {
            var token = request.getResponseHeader('X-Captcha-Token');
            if (request.status !== 200 || !token || !/^image\//.test(request.response.type)) { failed(); return; }
            if (captchaUrl) URL.revokeObjectURL(captchaUrl);
            captchaUrl = URL.createObjectURL(request.response);
            el('imagenCaptcha').onload = function () {
                captchaReady = true;
                el('imagenCaptcha').style.visibility = 'visible';
                el('refreshCaptcha').disabled = false;
                text('captcha-status', '');
                updateButton();
            };
            el('imagenCaptcha').onerror = failed;
            el('captchaToken').value = token;
            el('imagenCaptcha').src = captchaUrl;
            captchaRequest = null;
        };
        request.onerror = failed;
        request.ontimeout = failed;
        request.send();
    };
    window.submitAjaxFormVerifica = function () {
        if (busy) return false;
        var expediente = el('claveAccesoDoc').value.trim();
        var code = el('textoCaptcha').value.trim().toUpperCase();
        text('form-alert', '');
        fieldError('claveAccesoDoc', expediente ? '' : 'Ingresa tu número de expediente.', 'expediente-error');
        fieldError('textoCaptcha', code.length === 5 ? '' : 'Escribe los cinco caracteres de la imagen.', 'captcha-error');
        if (!expediente) { el('claveAccesoDoc').focus(); return false; }
        if (code.length !== 5) { el('textoCaptcha').focus(); return false; }
        if (!captchaReady) { text('form-alert', 'Espera a que cargue el código de seguridad o genera uno nuevo.'); return false; }
        el('claveAccesoDoc').value = expediente;
        el('textoCaptcha').value = code;
        busy = true;
        updateButton();
        el('refreshCaptcha').disabled = true;
        el('consultaDocBean').setAttribute('aria-busy', 'true');
        text('search-label', 'Consultando…');
        el('divPDF').innerHTML = '';
        jQuery.ajax({
            url: pRutaContexto + '/inicio.do?accion=goInicio',
            type: 'POST', data: jQuery('#consultaDocBean').serialize(), dataType: 'html', timeout: 30000,
            success: function (data) {
                jQuery('#divPDF').html(data);
                el('divPDF').focus({preventScroll:true});
                el('divPDF').scrollIntoView({behavior: window.matchMedia('(prefers-reduced-motion: reduce)').matches ? 'auto' : 'smooth', block: 'start'});
            },
            error: function () { text('form-alert', 'No pudimos completar la consulta. Vuelve a intentarlo en unos momentos.'); },
            complete: function () {
                busy = false;
                el('consultaDocBean').setAttribute('aria-busy', 'false');
                text('search-label', 'Consultar expediente');
                window.recargarCaptcha();
            }
        });
        return false;
    };
    jQuery(function () {
        if (!el('consultaDocBean')) return;
        el('consultaDocBean').noValidate = true;
        el('claveAccesoDoc').addEventListener('input', function () { fieldError('claveAccesoDoc', '', 'expediente-error'); });
        el('textoCaptcha').addEventListener('input', function () { fieldError('textoCaptcha', '', 'captcha-error'); });
        window.recargarCaptcha();
    });
}());

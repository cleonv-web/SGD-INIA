"""Verificaciones HTTP y lectura SQL; no resuelve captchas ni modifica datos."""
import json
import sys
import urllib.request
import urllib.parse
import http.cookiejar
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd

def verificar():
    base = sgd.C['url_publica'].rstrip('/') + '/consulta'
    jar = http.cookiejar.CookieJar()
    client = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(jar))
    checks = []
    def check(name, passed):
        checks.append({'caso': name, 'correcto': bool(passed)})
        if not passed:
            raise AssertionError(name)
    def get(path):
        with client.open(base + path, timeout=20) as response:
            return response.status, response.headers, response.read()
    def post(values):
        payload = urllib.parse.urlencode(values).encode()
        with client.open(base + '/inicio.do?accion=goInicio', data=payload, timeout=20) as response:
            return response.status, response.read().decode('utf-8')
    status, headers, page = get('/inicio.do')
    check('Página INIA responde y contiene marca institucional', status == 200 and b'Consulta de expediente | INIA' in page)
    for path in ['/resources-4.7/css/consulta-inia.css', '/resources-4.7/js/consulta-inia.js',
                 '/resources-4.7/images/inia-logo.png', '/resources-4.7/images/inia-sede.png']:
        code, _, body = get(path)
        check('Recurso local ' + path.rsplit('/', 1)[-1], code == 200 and len(body) > 100)
    status, headers, image = get('/jcaptcha')
    token = headers.get('X-Captcha-Token')
    check('Captcha PNG y token independiente', status == 200 and image.startswith(b'\x89PNG\r\n\x1a\n') and bool(token))
    check('Captcha sin caché', 'no-store' in headers.get('Cache-Control', ''))
    _, new_headers, _ = get('/jcaptcha')
    check('Renovación del código', new_headers.get('X-Captcha-Token') != token)
    status, body = post({})
    check('Campos ausentes sin error de servidor', status == 200 and 'Campos Obligatorios' in body)
    status, body = post({'nroExpediente': '2026-000001', 'textoCaptcha': 'INVALIDO', 'captchaToken': token})
    check('Código incorrecto rechazado', status == 200 and 'no es válido o ha vencido' in body)
    status, body = post({'nroExpediente': '2026-000001', 'textoCaptcha': 'INVALIDO', 'captchaToken': token})
    check('Desafío consumido rechazado', status == 200 and 'no es válido o ha vencido' in body)
    result = sgd.psql("SELECT count(*) FROM idosgd.pk_sgd_tramite_consulta_tu_expediente('PRUEBA-CONSULTA-INIA');")
    check('Función PostgreSQL de consulta disponible', result.returncode == 0)
    sgd.save(sgd.LOG / 'consulta-inia-2026-10-11/verificacion-http.json', checks)
    print(str(len(checks)) + ' comprobaciones correctas. Sin modificaciones en la base.')

if __name__ == '__main__':
    verificar()

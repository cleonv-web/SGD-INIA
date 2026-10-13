"""Enlaces públicos de MPD derivados de la URL de cada instalación."""
import re
import base64
from pathlib import Path


def configurar_logo_sgd(dominio):
    """Corrige únicamente la identidad de los reportes; conserva las demás opciones."""
    archivo = dominio / 'sgdproperties/application.properties'
    logo = (Path(__file__).resolve().parents[1] / 'fuentes/MPVCiudadano/web/src/main/resources/META-INF/web-resources/images/inia-logo.png').read_bytes()
    valor = 'B64_LOGO_REPORTE=' + base64.b64encode(logo).decode('ascii')
    texto = archivo.read_text(encoding='utf-8')
    patron = r'^B64_LOGO_REPORTE\s*=.*$'
    if re.search(patron, texto, re.M):
        texto = re.sub(patron, lambda _: valor, texto, flags=re.M)
    else:
        texto += '\n' + valor + '\n'
    archivo.write_text(texto, encoding='utf-8')


def configurar_mpd(dominio, url_publica, url_consulta_qr=None):
    archivo = dominio / 'mpvproperties/application.properties'
    if not archivo.is_file():
        raise RuntimeError('Falta application.properties de MPD.')
    base = url_publica.rstrip('/')
    logo = (Path(__file__).resolve().parents[1] / 'fuentes/MPVCiudadano/web/src/main/resources/META-INF/web-resources/images/inia-logo.png').read_bytes()
    cambios = {
        'B64_LOGO_REPORTE': base64.b64encode(logo).decode('ascii'),
        'MESA_PARTES_CIUDADANO': base + '/mpd',
        'CONSULTA_TU_TRAMITE': base + '/consulta/inicio.do',
        'url_consulta_qr': url_consulta_qr or 'https://calidad.inia.gob.pe/consulta/inicio.do',
        'EMAIL_ASUNTO': 'Mesa de Partes Digital INIA',
        'EMAIL_CONTENIDO_II': 'Hemos registrado su presentación ante el Instituto Nacional de Innovación Agraria, con el número de expediente',
        'EMAIL_CONTENIDO_VI': 'Mesa de Partes - INIA',
        'title_mpv': 'Presentación de documentos ante el Instituto Nacional de Innovación Agraria.',
    }
    texto = archivo.read_text(encoding='utf-8')
    for clave, valor in cambios.items():
        # PropertiesFactoryBean usa ISO-8859-1 por defecto; los escapes son portables.
        valor = ''.join(caracter if ord(caracter) < 128 else '\\u%04x' % ord(caracter) for caracter in valor)
        patron = r'^' + re.escape(clave) + r'\s*=.*$'
        if re.search(patron, texto, re.M):
            texto = re.sub(patron, lambda _: clave + '=' + valor, texto, flags=re.M)
        else:
            texto += '\n' + clave + '=' + valor + '\n'
    archivo.write_text(texto, encoding='utf-8')

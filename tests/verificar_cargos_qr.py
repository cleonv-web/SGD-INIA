"""Verifica los QR desde los PDF renderizados, sus enlaces y el formulario desplegado.

Dependencias de revisión: pypdfium2, Pillow, pypdf y zxing-cpp.
Ejemplo: python tests/verificar_cargos_qr.py --pdf-dir logs/cargos-qr/muestras --output logs/cargos-qr/revision
"""
import argparse
import json
import re
from pathlib import Path
from urllib.request import urlopen

import pypdfium2 as pdfium
import zxingcpp
from pypdf import PdfReader


def verificar(pdf_dir, output, base):
    output.mkdir(parents=True, exist_ok=True)
    destino = 'https://calidad.inia.gob.pe/consulta/inicio.do?expediente=2026-0000002'
    resultados = []
    for name in ('TDR15', 'TDR_VOUCHER', 'TDR_VOUCHER2'):
        path = pdf_dir / (name + '-qr-muestra.pdf')
        reader = PdfReader(path)
        assert len(reader.pages) == 1, 'Cargo dividido en varias páginas: ' + name
        enlaces = [a.get_object().get('/A', {}).get('/URI') for a in reader.pages[0].get('/Annots', [])]
        assert destino in enlaces, 'El PDF debe permitir pulsar sobre el QR: ' + name
        documento = pdfium.PdfDocument(str(path))
        lecturas = {}
        for dpi in (150, 300):
            imagen = documento[0].render(scale=dpi / 72).to_pil().convert('RGB')
            imagen.save(output / f'{name}-{dpi}dpi.png')
            codigos = zxingcpp.read_barcodes(imagen)
            assert len(codigos) == 1, 'Debe haber un solo QR sin barras: ' + name
            assert codigos[0].text == destino, 'QR con destino incorrecto: ' + name
            assert codigos[0].format == zxingcpp.BarcodeFormat.QRCode
            lecturas[str(dpi)] = codigos[0].text
        resultados.append({'plantilla': name, 'paginas': 1, 'qr_leido': lecturas, 'enlace_pdf': destino})
    for parametro, esperado in [('2026-0000002', '2026-0000002'), ('2026-0000003', '2026-0000003'),
                               ('2026-2%3Cscript%3E', ''), ('', '')]:
        with urlopen(base + '/consulta/inicio.do?expediente=' + parametro, timeout=30) as respuesta:
            html = respuesta.read().decode('utf-8')
        etiqueta = re.search(r'<input\b[^>]*\bid="claveAccesoDoc"[^>]*>', html).group()
        valor = re.search(r'\bvalue="([^"]*)"', etiqueta)
        assert (valor.group(1) if valor else '') == esperado, 'Autocompletado incorrecto'
        assert 'captchaToken' in html, 'El enlace no debe omitir el código de seguridad'
    evidencia = {'cargo': resultados, 'autocompletado_http': '4 casos correctos', 'captcha': 'conservado'}
    (output / 'verificacion-qr.json').write_text(json.dumps(evidencia, ensure_ascii=False, indent=2), encoding='utf-8')
    print(json.dumps(evidencia, ensure_ascii=False, indent=2))


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--pdf-dir', type=Path, required=True)
    parser.add_argument('--output', type=Path, required=True)
    parser.add_argument('--base', default='http://127.0.0.1:8085')
    args = parser.parse_args()
    verificar(args.pdf_dir, args.output, args.base)

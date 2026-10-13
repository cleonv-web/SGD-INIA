// Prueba real del formulario con una sesión nueva y sin presentar un expediente.
const { chromium } = require('playwright');
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
(async () => {
  const root = path.resolve(__dirname, '..');
  const browser = await chromium.launch({executablePath: process.env.CHROME_PATH || 'C:/Program Files/Google/Chrome/Application/chrome.exe', headless: true});
  try {
    const page = await browser.newPage();
    const errors = [];
    page.on('pageerror', error => errors.push(error.message));
    await page.goto('http://127.0.0.1:8085/mpd/inicio.do', {waitUntil: 'networkidle'});
    const action = await page.locator('#mpvdocBean').getAttribute('action');
    assert.match(action, /;jsessionid=/, 'Debe probar la primera visita con URL de sesión');
    const upload = page.waitForResponse(response => response.url().endsWith('/mpd/goUploadDoc'));
    const fixture = {name: 'prueba-carga.pdf', mimeType: 'application/pdf', buffer: Buffer.from('%PDF-1.4\n% Archivo sintético para comprobar transporte multipart\n%%EOF\n')};
    await page.locator('#fileuploadAlta').setInputFiles(fixture);
    const response = await upload;
    assert.equal(response.status(), 200);
    await page.waitForFunction(() => document.getElementById('documentoListado').textContent.includes('prueba-carga.pdf'));
    const annex = page.waitForResponse(response => response.url().endsWith('/mpd/goUploadDocAnexos'));
    await page.locator('#fileuploadAltaAnexos').setInputFiles(fixture);
    assert.equal((await annex).status(), 200);
    const captcha = await page.request.get('http://127.0.0.1:8085/mpd/jcaptcha');
    assert.equal(captcha.status(), 200);
    assert.match(captcha.headers()['content-type'], /^image\/png(?:;|$)/);
    await page.evaluate(() => document.getElementById('refreshCaptcha').click());
    await page.waitForFunction(() => !document.getElementById('imagenCaptcha').hidden && document.getElementById('imagenCaptcha').naturalWidth > 0);
    assert.equal(errors.length, 0, errors.join('\n'));
    const result = {sesion_nueva_con_url_reescrita: true, carga_principal: 200, carga_anexo: 200, captcha: 200, errores_javascript: errors, expediente_presentado: false};
    fs.mkdirSync(path.join(root, 'logs/revision-mpd-2026-10-13'), {recursive: true});
    fs.writeFileSync(path.join(root, 'logs/revision-mpd-2026-10-13/navegador.json'), JSON.stringify(result, null, 2));
    console.log(JSON.stringify(result));
  } finally {await browser.close();}
})().catch(error => {console.error(error); process.exit(1);});

// Regresión offline del bloqueo de Generar Doc. No usa HTTP, BD ni archivos reales.
// Ejecutar con Node.js: node tests/test_generar_documento.js
const assert = require('node:assert/strict');
const fs = require('node:fs');
const path = require('node:path');
const vm = require('node:vm');
const jsRoot = path.join(__dirname, '../fuentes/Sgd/tramitedoc/web/src/main/resources/META-INF/web-resources/js');

function escenario(opciones = {}) {
    const { conectado = true, socketAbierto = true, tieneWord = 'NO' } = opciones;
    const existe = Object.hasOwn(opciones, 'existe') ? opciones.existe : 'NO';
    const generado = Object.hasOwn(opciones, 'generado') ? opciones.generado : 'OK';
    const plantilla = Object.hasOwn(opciones, 'plantilla') ? opciones.plantilla : { retval: 'OK' };
    const mensajes = [], operaciones = [], esperas = [], validaciones = [];
    const campos = { nuAnn: '2026', nuEmi: 'FICTICIO' };
    const jquery = selector => ({
        find: id => ({ val: () => campos[id.slice(1)] }),
        val: () => undefined
    });
    jquery.cookie = () => 'CANAL_FICTICIO';
    const contexto = vm.createContext({
        console: { log() {} }, $: jquery, jQuery: jquery,
        setInterval() {}, clearInterval() {},
        loadding: value => esperas.push(value),
        bootbox: {
            alert(message, callback) {
                assert.equal(typeof message, 'string', 'Bootbox debe recibir un mensaje de texto');
                assert.ok(message.length, 'Bootbox requiere un mensaje no vacío');
                mensajes.push(message);
                if (callback) callback();
            },
            hideAll() {}
        },
        alert_Danger() {}, alert_Info() {}, alert_Warning() {},
        fu_verificarDestinatario: () => '1', fu_verificarReferencia: () => '1',
        fu_verificarChangeDocumentoEmiAdm: () => '0'
    });
    vm.runInContext(fs.readFileSync(path.join(jsRoot, 'wsTradoc.js'), 'utf8'), contexto);
    vm.runInContext(fs.readFileSync(path.join(jsRoot, 'docObjetos.js'), 'utf8'), contexto);
    contexto.tipoEjecucionAppDesktop.value = conectado ? 1 : 0;
    contexto._wsocket = { OPEN: 1, readyState: socketAbierto ? 1 : 3 };
    const ejecutarReal = contexto.runOnDesktop;
    contexto.runOnDesktop = (accion, param, callback) => {
        if (!conectado) return ejecutarReal(accion, param, callback);
        operaciones.push(accion);
        callback(accion === 6 ? existe : generado);
    };
    contexto.ajaxCall = (url, params, callback) => {
        if (params.includes('accion=goValidarPlantillaDocx')) {
            validaciones.push(params);
            callback(plantilla);
        } else if (params.includes('accion=goRutaGeneraDocx')) {
            callback(JSON.stringify({ retval: 'OK', nuAnn: '2026', nuEmi: 'FICTICIO', noUrl: 'URL_FICTICIA', noDoc: 'RUTA_FICTICIA', tieneWord }));
        } else {
            callback(JSON.stringify({ retval: 'OK', nuAnn: '2026', noUrl: 'URL_FICTICIA', noDoc: 'RUTA_FICTICIA' }));
        }
    };
    contexto.fn_generaDocx();
    return { mensajes, operaciones, esperas, validaciones };
}

let r = escenario({ conectado: false });
assert.deepEqual(r.operaciones, [], 'Sin cliente no se debe verificar ni generar');
assert.match(r.mensajes[0], /Tramitedoc no está conectado/);
assert.equal(r.esperas.at(-1), false, 'El fallo debe cerrar PROCESANDO');

r = escenario({ socketAbierto: false });
assert.deepEqual(r.operaciones, [], 'Un socket cerrado debe detener la generación');
assert.equal(r.esperas.at(-1), false);

for (const existe of [false, undefined, 'ERROR']) {
    r = escenario({ existe });
    assert.deepEqual(r.operaciones, [6], 'Una verificación fallida no debe iniciar la generación');
    assert.match(r.mensajes[0], /No se pudo verificar/);
    assert.equal(r.esperas.at(-1), false);
}

for (const generado of [false, undefined, '']) {
    r = escenario({ generado });
    assert.deepEqual(r.operaciones, [6, 5]);
    assert.match(r.mensajes[0], /no pudo generar/);
    assert.equal(r.esperas.at(-1), false);
}

r = escenario();
assert.deepEqual(r.operaciones, [6, 5], 'Cliente conectado y archivo nuevo mantienen el flujo');
assert.deepEqual(r.mensajes, []);

r = escenario({ existe: 'SI' });
assert.deepEqual(r.operaciones, [6, 5], 'Se conserva la confirmación para un archivo existente');
assert.match(r.mensajes[0], /ya fue creado/);

r = escenario({ tieneWord: 'SI', conectado: false });
assert.deepEqual(r.operaciones, [], 'Un Word del repositorio también requiere el cliente');
assert.match(r.mensajes[0], /Tramitedoc no está conectado/);

r = escenario({ tieneWord: 'SI' });
assert.deepEqual(r.operaciones, [2], 'Se conserva la apertura del Word del repositorio');
for (const plantilla of [{ retval: 'No hay una plantilla DOCX configurada.' }, null, { retval: false }]) {
    r = escenario({ plantilla });
    assert.deepEqual(r.operaciones, [6], 'Sin plantilla comprobada no se debe descargar ni generar');
    assert.equal(r.validaciones.length, 1);
    assert.match(r.mensajes[0], /plantilla DOCX/);
    assert.equal(r.esperas.at(-1), false, 'El error de plantilla debe cerrar PROCESANDO');
}
r = escenario({ existe: 'SI', plantilla: null });
assert.deepEqual(r.operaciones, [6, 5], 'Un archivo local existente no depende de tener plantilla');
assert.deepEqual(r.validaciones, []);
r = escenario({ tieneWord: 'SI', plantilla: null });
assert.deepEqual(r.operaciones, [2], 'Un Word del repositorio no requiere regenerar una plantilla');
assert.deepEqual(r.validaciones, []);
console.log('17 escenarios offline aprobados. Cliente real y ciclo documental: pendientes.');

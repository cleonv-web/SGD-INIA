# Activar el certificado de sgd.inia.gob.pe en Payara

`Reparar-HTTPS.bat` es un archivo unico para ejecutar dentro de la instalacion
del Windows Server que atiende el dominio. Usa Python y Java incluidos en esa
carpeta y el certificado ya importado en su `config/keystore.jks`.

```powershell
.\Reparar-HTTPS.bat
```

Solicita la clave maestra del almacen de Payara. Enter usa `changeit`, la clave
inicial de una instalacion nueva; no solicita ni guarda la clave del PFX.

Antes de cambiar algo, exige URL publica HTTPS del dominio, puerto 443,
archivos de una instalacion existente y Payara iniciado. Comprueba los nombres
SAN, vigencia, clave privada recuperable con la clave maestra y almacen del
listener. Prefiere el alias `sgd-inia`; si no existe, admite un unico certificado
valido que cubra el dominio. Una ambiguedad o certificado invalido bloquea el
cambio.

Respalda el XML y el keystore en una carpeta privada bajo
`logs/https-reparacion-*`. Cambia exclusivamente `cert-nickname` del protocolo
que usa el listener HTTPS en `server-config`. Reinicia solo ese dominio Payara,
con su directorio explicitamente identificado; no ejecuta SQL, no inicia ni
reinicia PostgreSQL y no modifica usuarios, contrasenas SGD ni documentos.

Comprueba que el certificado servido en 443 tenga la huella del certificado
seleccionado. Si falla el arranque o esa huella no coincide, intenta recuperar
el XML anterior y arrancar nuevamente. Si no puede confirmar que Payara esta
detenido, conserva el respaldo y muestra el error sin editar un dominio activo.

Finalmente comprueba el dominio y la confianza usando las autoridades del
sistema operativo. Si el servidor local entrega INIA pero el dominio entrega
otro certificado, indica revisar DNS, proxy o balanceador. Si falla confianza o
resolucion DNS, conserva la configuracion aplicada pero no declara HTTPS
verificado. `resultado.json` registra las comprobaciones completadas. No cambia
la confianza del navegador ni desactiva validaciones de TLS.

Pruebas: nueve comprobaciones aisladas, incluido un keystore temporal generado
y leido con el Java 8 incluido, certificado vencido/futuro/clave no recuperable,
dominios wildcard, ambiguedad de alias, preservacion de otros parametros XML,
recuperacion ante fallo de arranque/huella distinta y ejecucion real del BAT con
un entorno simulado. Las pruebas no detienen el SGD real.

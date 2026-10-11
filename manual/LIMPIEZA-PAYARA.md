# Limpiar archivos regenerables de Payara

El utilitario Windows `Limpiar-Payara.bat` retira archivos que se generan al ejecutar el servidor. Permite reducir el espacio local antes de preparar una instalación y evita acumular archivos del piloto. Las reglas de Git excluyen esa actividad incluso si todavía está en el disco.

Para preparar una subida solo se utilizan los filtros de .gitignore: no hace falta ejecutar este utilitario ni eliminar archivos locales. La limpieza física es una operación opcional distinta, que el operador debe solicitar expresamente. Una instalación nueva se realiza con git clone e Instalar-SGD; los componentes necesarios están versionados.

## Comandos simples

Desde `C:\SGD\SGD-INIA-INSTALL_PG`, mostrar el alcance sin borrar ni detener servicios:

```powershell
.\Limpiar-Payara.bat
```

Aplicar personalmente la limpieza:

```powershell
.\Limpiar-Payara.bat --aplicar
```

El comando comprueba las rutas y el estado del proceso. Si Payara está activo, lo detiene antes de borrar y lo vuelve a iniciar al terminar. Si estaba detenido, queda detenido. La reapertura puede tardar más porque se reconstruyen cachés y clases generadas; los usuarios deben volver a ingresar si perdieron la sesión. No ejecuta `SGD detener` ni `SGD iniciar`, por lo que no administra PostgreSQL ni regenera propiedades de las aplicaciones. El arranque de las aplicaciones mantiene su conexión habitual a la base.

## Archivos que retira

| Carpeta del dominio sgd | Contenido regenerable |
|---|---|
| osgi-cache | Copias y metadatos de los módulos OSGi. |
| generated | Clases, JSP y recursos generados al desplegar y ejecutar. |
| temp | Copias de trabajo y archivos temporales de las aplicaciones. |
| logs | Logs operativos del dominio; se eliminan sus registros anteriores. |

Las cuatro carpetas permanecen vacías después de limpiar con el servidor detenido. No ejecutar durante la elaboración o carga de un documento: los archivos de trabajo de `temp` se retiran. Los documentos guardados oficialmente en `datos/documentos` se conservan.

El utilitario conserva `config`, `domain.xml`, `domain.xml.bak`, certificados, credenciales del dominio, driver JDBC, módulos del motor, propiedades, `applications`, `reportes`, `docroot`, datos, respaldos y cargas de usuarios. No elimina aplicaciones desplegadas ni su historial en `domain.xml`. Si encuentra un PID residual o una ruta con enlace/junction, se detiene sin borrar automáticamente esos elementos. El resultado se registra en `logs/limpieza-payara.json`, fuera de Git.

## Qué se versiona de Payara

El motor está en `payara/payara5`. Se conservan sus binarios, bibliotecas, scripts, plantillas de dominio, licencias y WAR internos necesarios. Se excluyen `glassfish/domains` y `mq/var`, donde se genera actividad local, así como ejemplos, Javadoc y fuentes de ejemplo de H2. La configuración del piloto se conserva separadamente en `payara/configuracion`, con hashes, certificados y propiedades aprobadas. El ZIP de respaldo de Payara permanece local para evitar duplicar el motor en Git.

En una instalación nueva, el instalador crea el dominio `sgd` y configura los puertos, JDBC y propiedades para el ambiente. Los ocho WAR propios se compilan y despliegan desde fuentes. El ejemplo de configuración fija `heap_payara_mb=4096`, para reproducir el ajuste de 4 GB. En instalaciones existentes se conserva su configuración local; no reemplazarla ciegamente.

Una copia de configuración capturada puede contener referencias a aplicaciones del piloto. No usarla como reemplazo directo del dominio recién creado antes de compilar y desplegar; para la instalación nueva seguir el instalador y su configuración del ambiente. La copia sirve para conservar y comparar los ajustes aprobados y para la restauración administrada documentada en `payara/configuracion/README.md`.

## Validación y referencias

Las pruebas ejecutan la limpieza en carpetas temporales y verifican que aplicaciones, configuración, certificados, documentos y cargas queden intactos. También comprueban el bloqueo por PID y junction, el modo de revisión y las reglas de Git. La limpieza del piloto se registra después de aplicar el comando, sin consultas ni scripts PostgreSQL.

La documentación de administración de Payara distingue configuración, archivos generados y cachés; indica conservar `domain.xml.bak`: [Overview of Payara Server Administration](https://docs.payara.fish/community/docs/6.2023.10/Technical%20Documentation/Payara%20Server%20Documentation/General%20Administration/Overview.html). La distribución y su configuración OSGi identifican la reconstrucción al arrancar con una caché vacía. Se conserva la versión instalada 5.2022.5; no se actualiza el motor como parte de la limpieza.

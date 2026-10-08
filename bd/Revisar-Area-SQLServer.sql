/* Revisión por una oficina exacta, sin incorporar automáticamente subáreas.
   Sólo SELECT sobre tablas del sistema origen. No actualiza ni carga datos.
   1) Ejecutar con @Oficina=NULL para elegir el código.
   2) Cambiar NULL por ese código y ejecutar el archivo completo otra vez.
   @SoloEstado1=1 es un filtro provisional: confirmar que 1 significa activo. */
USE [bd_tramite_dev];
SET NOCOUNT ON;
DECLARE @Oficina int = NULL; -- Sustituir NULL por el iCodOficina de UTI u otra área.
DECLARE @SoloEstado1 bit = 1;

IF @Oficina IS NULL
BEGIN
    SELECT o.iCodOficina,o.cNomOficina,o.cSiglaOficina,o.iFlgEstado,o.idPadre,
           p.cNomOficina AS oficina_superior,u.cNomUbicacion,
           (SELECT COUNT(*) FROM dbo.Tra_M_Trabajadores t
            WHERE t.iCodOficina=o.iCodOficina AND (@SoloEstado1=0 OR t.nFlgEstado=1)) AS trabajadores_oficina_principal,
           (SELECT COUNT(DISTINCT a.iCodTrabajador) FROM dbo.Tra_M_Perfil_Ususario a
            JOIN dbo.Tra_M_Trabajadores t ON t.iCodTrabajador=a.iCodTrabajador
            WHERE a.iCodOficina=o.iCodOficina AND (@SoloEstado1=0 OR t.nFlgEstado=1)) AS trabajadores_asignados
    FROM dbo.Tra_M_Oficinas o
    LEFT JOIN dbo.Tra_M_Oficinas p ON p.iCodOficina=o.idPadre
    LEFT JOIN dbo.Tra_M_Ubicacion_Oficina u ON u.iCodUbicacion=o.iCodUbicacion
    WHERE @SoloEstado1=0 OR o.iFlgEstado=1
    ORDER BY o.cNomOficina,o.iCodOficina;
    PRINT 'Elegir iCodOficina, cambiar @Oficina y ejecutar el archivo completo.';
    RETURN;
END;
IF NOT EXISTS(SELECT 1 FROM dbo.Tra_M_Oficinas WHERE iCodOficina=@Oficina)
    THROW 50001,'El codigo de oficina no existe; no continuar.',1;

DECLARE @Personal TABLE(iCodTrabajador int PRIMARY KEY);
INSERT INTO @Personal(iCodTrabajador)
SELECT t.iCodTrabajador FROM dbo.Tra_M_Trabajadores t
WHERE (@SoloEstado1=0 OR t.nFlgEstado=1)
  AND (t.iCodOficina=@Oficina OR EXISTS
       (SELECT 1 FROM dbo.Tra_M_Perfil_Ususario a
        WHERE a.iCodTrabajador=t.iCodTrabajador AND a.iCodOficina=@Oficina));

-- 1. Oficina seleccionada: validar nombre, sigla, estado, padre y sede.
SELECT o.*,p.cNomOficina AS oficina_superior,u.cNomUbicacion,u.cNomDistrito
FROM dbo.Tra_M_Oficinas o
LEFT JOIN dbo.Tra_M_Oficinas p ON p.iCodOficina=o.idPadre
LEFT JOIN dbo.Tra_M_Ubicacion_Oficina u ON u.iCodUbicacion=o.iCodUbicacion
WHERE o.iCodOficina=@Oficina;

-- 2. Personal candidato. No contiene contraseñas, hashes, firmas ni fotos.
SELECT t.iCodTrabajador,t.cNombresTrabajador,t.cApellidosTrabajador,
       t.cNumDocIdentidad,t.cTipoDocIdentidad,t.cUsuario,t.cMailTrabajador,
       t.cCargo,t.nFlgEstado,t.iCodOficina AS oficina_principal_origen,t.iCodPerfil AS perfil_principal_origen,
       CASE WHEN t.iCodOficina=@Oficina THEN 'OFICINA PRINCIPAL' ELSE 'ASIGNACION ADICIONAL' END AS motivo_inclusion
FROM dbo.Tra_M_Trabajadores t JOIN @Personal e ON e.iCodTrabajador=t.iCodTrabajador
ORDER BY t.iCodTrabajador;

-- 3. Asignaciones del área, incluso huérfanas: se conservan para revisión.
SELECT a.iCodPerfilUsuario,a.iCodTrabajador,a.iCodOficina,a.iCodPerfil,
       RTRIM(p.cDescPerfil) AS perfil,t.nFlgEstado,
       CASE WHEN t.iCodTrabajador IS NULL THEN 'TRABAJADOR INEXISTENTE'
            WHEN p.iCodPerfil IS NULL THEN 'PERFIL INEXISTENTE'
            WHEN @SoloEstado1=1 AND ISNULL(t.nFlgEstado,-1)<>1 THEN 'FUERA DEL FILTRO ESTADO 1'
            ELSE 'REVISAR EQUIVALENCIA DE PERMISOS' END AS revision
FROM dbo.Tra_M_Perfil_Ususario a
LEFT JOIN dbo.Tra_M_Trabajadores t ON t.iCodTrabajador=a.iCodTrabajador
LEFT JOIN dbo.Tra_M_Perfil p ON p.iCodPerfil=a.iCodPerfil
WHERE a.iCodOficina=@Oficina ORDER BY a.iCodTrabajador,a.iCodPerfil,a.iCodPerfilUsuario;

-- 4. Incidencias por persona. Una persona puede tener varias incidencias.
SELECT t.iCodTrabajador,v.incidencia
FROM dbo.Tra_M_Trabajadores t JOIN @Personal e ON e.iCodTrabajador=t.iCodTrabajador
CROSS APPLY (VALUES
 (CASE WHEN NULLIF(LTRIM(RTRIM(t.cUsuario)),N'') IS NULL THEN N'USUARIO VACIO' END),
 (CASE WHEN LEN(LTRIM(RTRIM(t.cUsuario)))>20 THEN N'USUARIO SUPERA 20 CARACTERES' END),
 (CASE WHEN NULLIF(LTRIM(RTRIM(t.cNumDocIdentidad)),N'') IS NULL
         OR LEN(LTRIM(RTRIM(t.cNumDocIdentidad)))<>8
         OR LTRIM(RTRIM(t.cNumDocIdentidad)) COLLATE Latin1_General_100_BIN2 LIKE N'%[^0-9]%'
       THEN N'IDENTIDAD NO COMPATIBLE CON DNI DE 8 DIGITOS' END),
 (CASE WHEN NULLIF(LTRIM(RTRIM(t.cNombresTrabajador)),N'') IS NULL
         OR NULLIF(LTRIM(RTRIM(t.cApellidosTrabajador)),N'') IS NULL THEN N'NOMBRES INCOMPLETOS' END),
 (CASE WHEN LEN(t.cNombresTrabajador)>80 THEN N'NOMBRES SUPERAN 80 CARACTERES' END),
 (CASE WHEN LEN(t.cMailTrabajador)>50 THEN N'CORREO SUPERA 50 CARACTERES' END),
 (CASE WHEN NOT EXISTS(SELECT 1 FROM dbo.Tra_M_Perfil_Ususario a
        JOIN dbo.Tra_M_Perfil p ON p.iCodPerfil=a.iCodPerfil
        WHERE a.iCodTrabajador=t.iCodTrabajador AND a.iCodOficina=@Oficina)
       THEN N'SIN ASIGNACION DE PERFIL VALIDA EN EL AREA' END),
 (CASE WHEN LOWER(LTRIM(RTRIM(t.cUsuario))) IN (N'admin',N'mesa_demo',N'area_demo',N'jefe_demo')
       THEN N'USUARIO COINCIDE CON CUENTA DE LABORATORIO' END)
) v(incidencia) WHERE v.incidencia IS NOT NULL ORDER BY t.iCodTrabajador,v.incidencia;

-- 5. Duplicados exactos de asignaciones; no eliminar asignaciones múltiples legítimas.
SELECT a.iCodTrabajador,a.iCodOficina,a.iCodPerfil,COUNT(*) AS repeticiones
FROM dbo.Tra_M_Perfil_Ususario a JOIN @Personal e ON e.iCodTrabajador=a.iCodTrabajador
WHERE a.iCodOficina=@Oficina
GROUP BY a.iCodTrabajador,a.iCodOficina,a.iCodPerfil HAVING COUNT(*)>1;

-- 6. Colisiones de usuario con cualquier otro trabajador del mismo filtro de estado.
SELECT t.iCodTrabajador,COUNT(*) AS personas_con_el_mismo_usuario
FROM dbo.Tra_M_Trabajadores t JOIN @Personal e ON e.iCodTrabajador=t.iCodTrabajador
JOIN dbo.Tra_M_Trabajadores x ON LOWER(LTRIM(RTRIM(x.cUsuario)))=LOWER(LTRIM(RTRIM(t.cUsuario)))
WHERE (@SoloEstado1=0 OR x.nFlgEstado=1) AND NULLIF(LTRIM(RTRIM(t.cUsuario)),N'') IS NOT NULL
GROUP BY t.iCodTrabajador HAVING COUNT(*)>1;

-- 7. Incidencias de la oficina; revisar antecesores en la exportación.
SELECT v.incidencia FROM dbo.Tra_M_Oficinas o
CROSS APPLY (VALUES
 (CASE WHEN ISNULL(o.iFlgEstado,-1)<>1 THEN N'OFICINA FUERA DEL ESTADO 1' END),
 (CASE WHEN NULLIF(LTRIM(RTRIM(o.cNomOficina)),'') IS NULL THEN N'OFICINA SIN NOMBRE' END),
 (CASE WHEN LEN(o.cNomOficina)>200 THEN N'NOMBRE DE OFICINA SUPERA 200 CARACTERES' END),
 (CASE WHEN LEN(o.cSiglaOficina)>50 THEN N'SIGLA SUPERA 50 CARACTERES' END),
 (CASE WHEN NOT EXISTS(SELECT 1 FROM dbo.Tra_M_Ubicacion_Oficina u WHERE u.iCodUbicacion=o.iCodUbicacion)
       THEN N'SEDE INEXISTENTE O NO INDICADA' END),
 (CASE WHEN o.idPadre<>0 AND o.idPadre IS NOT NULL AND NOT EXISTS
       (SELECT 1 FROM dbo.Tra_M_Oficinas p WHERE p.iCodOficina=o.idPadre) THEN N'PADRE INEXISTENTE' END)
) v(incidencia) WHERE o.iCodOficina=@Oficina AND v.incidencia IS NOT NULL;

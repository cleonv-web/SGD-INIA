/* Extracción para revisión y preparación; NO es un script de carga PostgreSQL.
   Ejecutar después de Revisar-Area-SQLServer.sql. Cambiar @Oficina.
   Cambiar únicamente @Oficina. La selección final se realiza en el CSV preparado:
   incluir_sgd=1 para cargar, incluir_sgd=0 para excluir.
   Estado 1 NO demuestra vínculo laboral vigente. Revisar cesados y cuentas genéricas.
   Exportar cada resultado por separado como CSV UTF-8 con encabezados.
   Guardarlos en datos (excluido de Git); no exportar claves ni hashes.
   Sólo una oficina: no incorpora automáticamente sus subáreas ni sus usuarios.
   Sus antecesores se incluyen únicamente para reconstruir el organigrama. */
USE [bd_tramite_dev];
SET NOCOUNT ON;
DECLARE @Oficina int = NULL; -- Sustituir por el código del área revisada.
DECLARE @SoloEstado1 bit = 1; -- Confirmar significado del estado en el origen.
IF @Oficina IS NULL
    THROW 50001,'Indicar @Oficina antes de exportar. Consultar Revisar-Area-SQLServer.sql.',1;
IF NOT EXISTS(SELECT 1 FROM dbo.Tra_M_Oficinas WHERE iCodOficina=@Oficina)
    THROW 50002,'El codigo de oficina no existe.',1;

DECLARE @Oficinas TABLE(iCodOficina int PRIMARY KEY);
DECLARE @Cadena TABLE(iCodOficina int,idPadre int,ciclo int,profundidad int);
;WITH h AS
(
 SELECT o.iCodOficina,o.idPadre,CAST('/'+CONVERT(varchar(12),o.iCodOficina)+'/' AS varchar(max)) AS ruta,
        CAST(0 AS int) AS ciclo,CAST(0 AS int) AS profundidad
 FROM dbo.Tra_M_Oficinas o WHERE o.iCodOficina=@Oficina
 UNION ALL
 SELECT p.iCodOficina,p.idPadre,CAST(h.ruta+CONVERT(varchar(12),p.iCodOficina)+'/' AS varchar(max)),
        CASE WHEN CHARINDEX('/'+CONVERT(varchar(12),p.iCodOficina)+'/',h.ruta)>0 THEN 1 ELSE 0 END,
        h.profundidad+1
 FROM h JOIN dbo.Tra_M_Oficinas p ON p.iCodOficina=h.idPadre
 WHERE h.ciclo=0 AND h.profundidad<100
)
INSERT INTO @Cadena SELECT iCodOficina,idPadre,ciclo,profundidad FROM h OPTION(MAXRECURSION 101);
IF EXISTS(SELECT 1 FROM @Cadena WHERE ciclo=1)
    THROW 50003,'La jerarquia tiene un ciclo. Resolver antes de exportar.',1;
IF EXISTS(SELECT 1 FROM @Cadena WHERE profundidad=100)
    THROW 50004,'Jerarquia demasiado profunda; revisar antes de exportar.',1;
IF EXISTS(SELECT 1 FROM @Cadena h WHERE h.idPadre IS NOT NULL AND h.idPadre<>0
          AND NOT EXISTS(SELECT 1 FROM dbo.Tra_M_Oficinas p WHERE p.iCodOficina=h.idPadre))
    THROW 50005,'Un padre de la jerarquia no existe. Resolver antes de exportar.',1;
INSERT INTO @Oficinas SELECT DISTINCT iCodOficina FROM @Cadena;

DECLARE @Personal TABLE(iCodTrabajador int PRIMARY KEY);
INSERT INTO @Personal
SELECT t.iCodTrabajador FROM dbo.Tra_M_Trabajadores t
WHERE (@SoloEstado1=0 OR t.nFlgEstado=1)
 AND (t.iCodOficina=@Oficina OR EXISTS(SELECT 1 FROM dbo.Tra_M_Perfil_Ususario a
      WHERE a.iCodTrabajador=t.iCodTrabajador AND a.iCodOficina=@Oficina));

-- Resultado 1: guardar como ubicaciones-<codigo>.csv.
SELECT u.iCodUbicacion,u.cNomUbicacion,u.cNomDistrito,u.nFlagEstado
FROM dbo.Tra_M_Ubicacion_Oficina u
WHERE EXISTS(SELECT 1 FROM dbo.Tra_M_Oficinas o JOIN @Oficinas a ON a.iCodOficina=o.iCodOficina
             WHERE o.iCodUbicacion=u.iCodUbicacion) ORDER BY u.iCodUbicacion;

-- Resultado 2: oficinas-<codigo>.csv. Antecesores NO habilitan sus empleados.
SELECT o.iCodOficina,o.cNomOficina,o.cSiglaOficina,o.iCodUbicacion,o.idPadre,
       o.iFlgEstado,o.iCodTipoOficina,o.iCodCategoriaOficina,o.iFlgRof,o.esArea,
       CASE WHEN o.iCodOficina=@Oficina THEN 1 ELSE 0 END AS es_area_piloto
FROM dbo.Tra_M_Oficinas o JOIN @Oficinas a ON a.iCodOficina=o.iCodOficina ORDER BY o.iCodOficina;

-- Resultado 3: trabajadores-<codigo>.csv. Conservar apellidos completos para revisión.
-- iCodOficina es la oficina principal del ORIGEN; no copiarla sin comprobar alcance.
SELECT t.iCodTrabajador,t.iCodOficina,t.iCodPerfil,t.cNombresTrabajador,t.cApellidosTrabajador,
       t.cTipoDocIdentidad,t.cNumDocIdentidad,t.cMailTrabajador,t.cCargo,t.cUsuario,
       t.nFlgEstado,t.ES_EXTERNO,t.CODIGO_SEDE,t.encargado,t.nFirmaDigital,
       @Oficina AS oficina_piloto_seleccionada
FROM dbo.Tra_M_Trabajadores t JOIN @Personal e ON e.iCodTrabajador=t.iCodTrabajador
ORDER BY t.iCodTrabajador;

-- Resultado 4: perfiles-<codigo>.csv. Sólo catálogo utilizado por los candidatos.
SELECT p.iCodPerfil,RTRIM(p.cDescPerfil) AS cDescPerfil,p.cDescripcion
FROM dbo.Tra_M_Perfil p
WHERE EXISTS(SELECT 1 FROM dbo.Tra_M_Perfil_Ususario a JOIN @Personal e ON e.iCodTrabajador=a.iCodTrabajador
             WHERE a.iCodOficina=@Oficina AND a.iCodPerfil=p.iCodPerfil)
 OR EXISTS(SELECT 1 FROM dbo.Tra_M_Trabajadores t JOIN @Personal e ON e.iCodTrabajador=t.iCodTrabajador
           WHERE t.iCodPerfil=p.iCodPerfil)
ORDER BY p.iCodPerfil;

-- Resultado 5: asignaciones-<codigo>.csv. Duplicados se conservan para revisión.
-- No incluye permisos de otras oficinas del mismo trabajador.
SELECT a.iCodPerfilUsuario,a.iCodTrabajador,a.iCodOficina,a.iCodPerfil
FROM dbo.Tra_M_Perfil_Ususario a JOIN @Personal e ON e.iCodTrabajador=a.iCodTrabajador
WHERE a.iCodOficina=@Oficina ORDER BY a.iCodTrabajador,a.iCodPerfil,a.iCodPerfilUsuario;
-- Una exportación terminada NO significa que la información está lista para cargar.

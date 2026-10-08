/* Ejecutar en SQL Server (SSMS o conexión SQL Server de VS Code).
   Sólo consulta metadatos; no consulta registros de trabajadores.
   Cambiar USE si la base origen tiene otro nombre. */
USE [bd_tramite_dev];
SET NOCOUNT ON;

SELECT s.name AS esquema,t.name AS tabla,c.column_id,c.name AS columna,
       ty.name AS tipo,
       CASE WHEN ty.name IN ('nvarchar','nchar') AND c.max_length>0
            THEN c.max_length/2 ELSE c.max_length END AS longitud,
       c.precision,c.scale,c.is_nullable,c.is_identity,dc.definition AS valor_predeterminado
FROM sys.tables t
JOIN sys.schemas s ON s.schema_id=t.schema_id
JOIN sys.columns c ON c.object_id=t.object_id
JOIN sys.types ty ON ty.user_type_id=c.user_type_id
LEFT JOIN sys.default_constraints dc ON dc.object_id=c.default_object_id
WHERE s.name='dbo' AND t.name IN
 ('Tra_M_Trabajadores','Tra_M_Oficinas','Tra_M_Perfil',
  'Tra_M_Ubicacion_Oficina','Tra_M_Perfil_Ususario')
ORDER BY t.name,c.column_id;

SELECT OBJECT_SCHEMA_NAME(i.object_id) AS esquema,OBJECT_NAME(i.object_id) AS tabla,
       i.name AS indice,i.is_primary_key,i.is_unique,c.name AS columna,ic.key_ordinal
FROM sys.indexes i
JOIN sys.index_columns ic ON ic.object_id=i.object_id AND ic.index_id=i.index_id
JOIN sys.columns c ON c.object_id=ic.object_id AND c.column_id=ic.column_id
WHERE OBJECT_SCHEMA_NAME(i.object_id)='dbo' AND OBJECT_NAME(i.object_id) IN
 ('Tra_M_Trabajadores','Tra_M_Oficinas','Tra_M_Perfil',
  'Tra_M_Ubicacion_Oficina','Tra_M_Perfil_Ususario')
 AND i.index_id>0 AND ic.key_ordinal>0
ORDER BY tabla,indice,ic.key_ordinal;

SELECT fk.name AS relacion,
       OBJECT_SCHEMA_NAME(fk.parent_object_id) AS esquema_origen,
       OBJECT_NAME(fk.parent_object_id) AS tabla_origen,pc.name AS campo_origen,
       OBJECT_SCHEMA_NAME(fk.referenced_object_id) AS esquema_destino,
       OBJECT_NAME(fk.referenced_object_id) AS tabla_destino,rc.name AS campo_destino,
       fk.is_disabled,fk.is_not_trusted
FROM sys.foreign_keys fk
JOIN sys.foreign_key_columns fkc ON fkc.constraint_object_id=fk.object_id
JOIN sys.columns pc ON pc.object_id=fkc.parent_object_id AND pc.column_id=fkc.parent_column_id
JOIN sys.columns rc ON rc.object_id=fkc.referenced_object_id AND rc.column_id=fkc.referenced_column_id
WHERE (OBJECT_SCHEMA_NAME(fk.parent_object_id)='dbo' AND OBJECT_NAME(fk.parent_object_id) IN
 ('Tra_M_Trabajadores','Tra_M_Oficinas','Tra_M_Perfil','Tra_M_Ubicacion_Oficina','Tra_M_Perfil_Ususario'))
 OR (OBJECT_SCHEMA_NAME(fk.referenced_object_id)='dbo' AND OBJECT_NAME(fk.referenced_object_id) IN
 ('Tra_M_Trabajadores','Tra_M_Oficinas','Tra_M_Perfil','Tra_M_Ubicacion_Oficina','Tra_M_Perfil_Ususario'))
ORDER BY tabla_origen,relacion,fkc.constraint_column_id;
-- La ausencia de FK declarada no demuestra que no exista una relación funcional.

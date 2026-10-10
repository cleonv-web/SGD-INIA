-- PENDIENTE DE AUTORIZACION: Codex no ha ejecutado estas consultas.
-- Solo configuracion, estadisticas agregadas e indices; no lee PDFs ni personas.
-- EXPLAIN sin ANALYZE describe el plan y no ejecuta la consulta del documento.
BEGIN READ ONLY;
SET LOCAL statement_timeout = '15s';
SET LOCAL lock_timeout = '3s';
SELECT current_database() AS base, current_setting('server_version') AS version;
SELECT name,setting,unit FROM pg_settings
 WHERE name IN ('shared_buffers','work_mem','maintenance_work_mem',
                'max_connections','effective_cache_size','wal_compression',
                'track_io_timing','synchronous_commit','max_wal_size')
 ORDER BY name;
SELECT numbackends,xact_commit,xact_rollback,blks_read,blks_hit,
       temp_files,temp_bytes,deadlocks,blk_read_time,blk_write_time
 FROM pg_stat_database WHERE datname=current_database();
SELECT state,wait_event_type,wait_event,count(*) AS conexiones
 FROM pg_stat_activity WHERE datname=current_database()
 GROUP BY state,wait_event_type,wait_event ORDER BY conexiones DESC;
SELECT relname,n_live_tup,n_dead_tup,seq_scan,seq_tup_read,idx_scan,
       last_autovacuum,last_autoanalyze,
       pg_size_pretty(pg_total_relation_size(relid)) AS tamano_total
 FROM pg_stat_user_tables WHERE schemaname='idosgd'
   AND relname IN ('tdtv_archivo_doc','tdtr_plantilla_docx','tdtv_remitos','tdtv_anexos')
 ORDER BY relname;
SELECT tablename,indexname,indexdef FROM pg_indexes WHERE schemaname='idosgd'
 AND tablename IN ('tdtv_archivo_doc','tdtr_plantilla_docx','tdtv_remitos','tdtv_anexos')
 ORDER BY tablename,indexname;
EXPLAIN SELECT bl_doc FROM idosgd.tdtv_archivo_doc
 WHERE nu_ann='2026' AND nu_emi='QA';
COMMIT;

-- Tablas virtuales del SELECT de prueba; no crea ni modifica tablas reales.
WITH tdtv_remitos(nu_ann,nu_emi,nu_ann_exp,nu_sec_exp,co_dep_emi,es_doc_emi,es_eli,fe_emi) AS (
    VALUES ('2026','ROOT','2026','EXP','REG','5','0',timestamp '2026-10-11 19:28:54'),
           ('2026','A','2026','EXP','REG','0','0',timestamp '2026-10-12 09:00:00'),
           ('2026','B','2026','EXP','REG','0','0',timestamp '2026-10-12 11:00:00'),
           ('2026','DRAFT','2026','EXP','REG','5','0',timestamp '2026-10-13 11:00:00'),
           ('2026','PENDING','2026','EXP','REG','7','0',timestamp '2026-10-13 12:00:00'),
           ('2026','CANCELED','2026','EXP','REG','9','0',timestamp '2026-10-13 13:00:00'),
           ('2026','DELETED','2026','EXP','REG','0','1',timestamp '2026-10-13 14:00:00'),
           ('2025','OTHER-YEAR','2025','EXP','REG','0','0',timestamp '2026-10-13 15:00:00')
), tdtc_expediente(nu_ann_exp,nu_sec_exp,co_dep_exp,fe_exp) AS (
    VALUES ('2026','EXP','REG',timestamp '2026-10-11 19:28:54')
), rhtm_dependencia(co_dependencia,de_dependencia) AS (
    VALUES ('REG','MESA DE PARTES'),('A','OFICINA A'),('B','OFICINA B'),('INVALID','OFICINA QUE NO DEBE MOSTRARSE')
), tdtr_estados(de_tab,co_est,de_est) AS (
    VALUES ('TDTV_REMITOS','5','EN PROYECTO'),('TDTV_DESTINOS','1','RECIBIDO'),('TDTV_DESTINOS','3','ARCHIVADO')
), tdtv_destinos(nu_ann,nu_emi,nu_des,co_dep_des,es_doc_rec,de_ane,es_eli,fe_use_mod,fe_use_cre) AS (
    VALUES ('2026','A',1,'A','1','En atención','0',timestamp '2026-10-12 09:00:00',timestamp '2026-10-12 09:00:00'),
           ('2026','B',1,'B','3','Atención concluida','0',NULL::timestamp,timestamp '2026-10-12 11:00:00'),
           ('2026','B',2,'INVALID','9','Destino anulado','0',timestamp '2026-10-13 09:00:00',timestamp '2026-10-13 09:00:00'),
           ('2026','B',3,'INVALID','1','Destino eliminado','1',timestamp '2026-10-13 10:00:00',timestamp '2026-10-13 10:00:00'),
           ('2026','DRAFT',1,'INVALID','1','Borrador','0',timestamp '2026-10-13 11:00:00',timestamp '2026-10-13 11:00:00'),
           ('2026','PENDING',1,'INVALID','1','Pendiente','0',timestamp '2026-10-13 12:00:00',timestamp '2026-10-13 12:00:00'),
           ('2026','CANCELED',1,'INVALID','1','Documento anulado','0',timestamp '2026-10-13 13:00:00',timestamp '2026-10-13 13:00:00'),
           ('2026','DELETED',1,'INVALID','1','Documento eliminado','0',timestamp '2026-10-13 14:00:00',timestamp '2026-10-13 14:00:00'),
           ('2025','OTHER-YEAR',1,'INVALID','1','Otro año','0',timestamp '2026-10-13 15:00:00',timestamp '2026-10-13 15:00:00')
)

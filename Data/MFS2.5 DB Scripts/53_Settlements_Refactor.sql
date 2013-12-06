use mfino;

update service_settlement_cfg set SchedulerStatus = 0;

delete from settlement_schedule_log;
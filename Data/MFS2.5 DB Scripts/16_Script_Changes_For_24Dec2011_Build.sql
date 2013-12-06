-- Script to add service.partner.id to the System Parameters.
use mfino;

insert into system_parameters values(14, 1, now(), 'System', now(), 'system', 'service.partner.id', '1', 'Service Partner Id');
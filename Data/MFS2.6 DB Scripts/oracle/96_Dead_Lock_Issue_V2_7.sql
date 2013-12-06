
alter table chargetxn_transfer_map drop constraint FK_TxnTransferMap_SCTL;

drop index FK_TxnTransferMap_SCTL;

commit;
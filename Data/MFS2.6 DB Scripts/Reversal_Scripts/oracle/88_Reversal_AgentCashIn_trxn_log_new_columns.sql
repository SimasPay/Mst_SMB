ALTER TABLE agent_cashin_txn_log DROP (
AgentCashInTrxnStatus,AgentCashInTrxnStatusReason,SctlId
);

commit;

ALTER TABLE agent_cashin_txn_log ADD (
AgentCashInTrxnStatus int(11),
AgentCashInTrxnStatusReason varchar(255),
SctlId bigint(20)
);
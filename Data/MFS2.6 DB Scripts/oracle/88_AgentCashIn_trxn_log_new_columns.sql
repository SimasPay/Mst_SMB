ALTER TABLE agent_cashin_txn_log ADD (
AgentCashInTrxnStatus NUMBER(10,0),
AgentCashInTrxnStatusReason varchar2(255),
SctlId NUMBER(19,0)
);

commit;
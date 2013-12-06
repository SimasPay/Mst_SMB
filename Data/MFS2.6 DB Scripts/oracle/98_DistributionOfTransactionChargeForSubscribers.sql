
ALTER TABLE sctl_settlement_map MODIFY PartnerID NULL;

ALTER TABLE txn_amount_dstrb_log MODIFY PartnerID NULL;

ALTER TABLE txn_amount_dstrb_log ADD SubscriberID NUMBER(19,0);

ALTER TABLE txn_amount_dstrb_log
ADD CONSTRAINT FK_TxnAmtDistLog_Subscriber FOREIGN KEY
(
  SubscriberID
)
REFERENCES subscriber
(
  ID
)
ENABLE;


ALTER TABLE transaction_charge ADD IsChrgDstrbApplicableToSrcSub NUMBER(3,0) DEFAULT '0';

ALTER TABLE transaction_charge ADD IsChrgDstrbApplicableToDestSub NUMBER(3,0) DEFAULT '0';

commit;

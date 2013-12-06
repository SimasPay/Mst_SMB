use mfino;

ALTER TABLE subscriber_mdn ADD 
      CONSTRAINT FK_subscriber_mdn
      FOREIGN    KEY (SubscriberID)
      REFERENCES subscriber(ID);
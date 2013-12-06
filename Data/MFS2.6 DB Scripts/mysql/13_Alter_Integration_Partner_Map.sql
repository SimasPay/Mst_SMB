

ALTER TABLE integration_partner_map ADD MFSBillerID bigint(20) NULL;

ALTER TABLE integration_partner_map CHANGE PartnerID PartnerID bigint(20) DEFAULT NULL;

ALTER TABLE integration_partner_map ADD CONSTRAINT FK_INTEGRATION_PARTNER_MAP_BI FOREIGN KEY (MFSBillerID) REFERENCES mfs_biller (ID);
	  
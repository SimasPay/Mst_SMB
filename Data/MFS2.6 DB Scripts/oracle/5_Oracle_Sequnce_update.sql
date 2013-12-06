PROMPT Drop Existing Sequences ........................
DROP SEQUENCE activities_log_ID_SEQ;
DROP SEQUENCE address_ID_SEQ;
DROP SEQUENCE agent_cashin_txn_log_ID_SEQ;
DROP SEQUENCE airtime_purchase_ID_SEQ;
DROP SEQUENCE auth_person_details_ID_SEQ;
DROP SEQUENCE bank_ID_SEQ;
DROP SEQUENCE bank_admin_ID_SEQ;
DROP SEQUENCE bill_payment_txn_ID_SEQ;
DROP SEQUENCE bill_payments_ID_SEQ;
DROP SEQUENCE biller_ID_SEQ;
DROP SEQUENCE brand_ID_SEQ;
DROP SEQUENCE bulk_bank_account_ID_SEQ;
DROP SEQUENCE bulk_lop_ID_SEQ;
DROP SEQUENCE bulk_upload_ID_SEQ;
DROP SEQUENCE bulk_upload_entry_ID_SEQ;
DROP SEQUENCE bulk_upload_file_ID_SEQ;
DROP SEQUENCE card_info_ID_SEQ;
DROP SEQUENCE channel_code_ID_SEQ;
DROP SEQUENCE channel_session_mgmt_ID_SEQ;
DROP SEQUENCE charge_definition_ID_SEQ;
DROP SEQUENCE charge_pricing_ID_SEQ;
DROP SEQUENCE charge_type_ID_SEQ;
DROP SEQUENCE chargetxn_transfer_map_ID_SEQ;
DROP SEQUENCE company_ID_SEQ;
DROP SEQUENCE creditcard_destinations_ID_SEQ;
DROP SEQUENCE credit_card_transaction_ID_SEQ;
DROP SEQUENCE db_param_ID_SEQ;
DROP SEQUENCE denomination_ID_SEQ;
DROP SEQUENCE distribution_chain_lvl_ID_SEQ;
DROP SEQUENCE distribution_chain_temp_ID_SEQ;
DROP SEQUENCE enum_text_ID_SEQ;
DROP SEQUENCE interbank_codes_ID_SEQ;
DROP SEQUENCE interbank_transfers_ID_SEQ;
DROP SEQUENCE kyc_fields_ID_SEQ;
DROP SEQUENCE kyc_level_ID_SEQ;
DROP SEQUENCE ledger_ID_SEQ;
DROP SEQUENCE letter_of_purchase_ID_SEQ;
DROP SEQUENCE lop_history_ID_SEQ;
DROP SEQUENCE mdn_range_ID_SEQ;
DROP SEQUENCE merchant_code_ID_SEQ;
DROP SEQUENCE merchant_prefix_code_ID_SEQ;
DROP SEQUENCE mfino_service_provider_ID_SEQ;
DROP SEQUENCE mfino_user_ID_SEQ;
DROP SEQUENCE mfs_biller_ID_SEQ;
DROP SEQUENCE mfsbiller_partner_map_ID_SEQ;
DROP SEQUENCE mobile_network_operator_ID_SEQ;
DROP SEQUENCE notification_ID_SEQ;
DROP SEQUENCE offline_report_ID_SEQ;
DROP SEQUENCE offline_report_company_ID_SEQ;
DROP SEQUENCE offline_report_receiver_ID_SEQ;
DROP SEQUENCE partner_ID_SEQ;
DROP SEQUENCE partner_services_ID_SEQ;
DROP SEQUENCE pending_txns_entry_ID_SEQ;
DROP SEQUENCE pending_txns_file_ID_SEQ;
DROP SEQUENCE permission_item_ID_SEQ;
DROP SEQUENCE person_2_person_ID_SEQ;
DROP SEQUENCE pocket_ID_SEQ;
DROP SEQUENCE pocket_template_ID_SEQ;
DROP SEQUENCE product_indicator_ID_SEQ;
DROP SEQUENCE region_ID_SEQ;
DROP SEQUENCE report_parameters_ID_SEQ;
DROP SEQUENCE role_permission_ID_SEQ;
DROP SEQUENCE sap_groupid_ID_SEQ;
DROP SEQUENCE service_ID_SEQ;
DROP SEQUENCE service_audit_ID_SEQ;
DROP SEQUENCE service_charge_txn_log_ID_SEQ;
DROP SEQUENCE service_settlement_cfg_ID_SEQ;
DROP SEQUENCE service_transaction_ID_SEQ;
DROP SEQUENCE settlement_schedule_log_ID_SEQ;
DROP SEQUENCE settlement_template_ID_SEQ;
DROP SEQUENCE settlement_txn_log_ID_SEQ;
DROP SEQUENCE share_partner_ID_SEQ;
DROP SEQUENCE sms_code_ID_SEQ;
DROP SEQUENCE sms_partner_ID_SEQ;
DROP SEQUENCE sms_transaction_log_ID_SEQ;
DROP SEQUENCE smsc_configuration_ID_SEQ;
DROP SEQUENCE subscriber_ID_SEQ;
DROP SEQUENCE subscriber_addi_info_ID_SEQ;
DROP SEQUENCE subscriber_mdn_ID_SEQ;
DROP SEQUENCE system_parameters_ID_SEQ;
DROP SEQUENCE transaction_charge_ID_SEQ;
DROP SEQUENCE transaction_charge_log_ID_SEQ;
DROP SEQUENCE transaction_log_ID_SEQ;
DROP SEQUENCE transaction_rule_ID_SEQ;
DROP SEQUENCE transaction_type_ID_SEQ;
DROP SEQUENCE txn_amount_dstrb_log_ID_SEQ;
DROP SEQUENCE unregistered_txn_info_ID_SEQ;
DROP SEQUENCE visafone_txn_generator_id_SEQ;
DROP SEQUENCE subscriber_groups_ID_SEQ;
DROP SEQUENCE groups_ID_SEQ;
DROP SEQUENCE pocket_template_config_ID_SEQ;

PROMPT Create Sequences..............

DECLARE 
  command1 varchar(255);
  part1 varchar(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  select 'CREATE SEQUENCE activities_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from activities_log;
  execute immediate command1; 
  
  select 'CREATE SEQUENCE address_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from address;
  execute immediate command1;

  select 'CREATE SEQUENCE agent_cashin_txn_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from agent_cashin_txn_log;
  execute immediate command1;

  select 'CREATE SEQUENCE airtime_purchase_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from airtime_purchase;
  execute immediate command1;

  select 'CREATE SEQUENCE auth_person_details_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from auth_person_details;
  execute immediate command1;

  select 'CREATE SEQUENCE bank_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bank;
  execute immediate command1; 

  select 'CREATE SEQUENCE bank_admin_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bank_admin;
  execute immediate command1; 

  select 'CREATE SEQUENCE bill_payment_txn_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bill_payment_txn;
  execute immediate command1; 

  select 'CREATE SEQUENCE bill_payments_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bill_payments;
  execute immediate command1; 

  select 'CREATE SEQUENCE biller_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from biller;
  execute immediate command1;

  select 'CREATE SEQUENCE brand_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from brand;
  execute immediate command1;

  select 'CREATE SEQUENCE bulk_bank_account_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bulk_bank_account;
  execute immediate command1;
  
  select 'CREATE SEQUENCE bulk_lop_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bulk_lop;
  execute immediate command1;

  select 'CREATE SEQUENCE bulk_upload_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bulk_upload;
  execute immediate command1;

  select 'CREATE SEQUENCE bulk_upload_entry_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bulk_upload_entry;
  execute immediate command1;

  select 'CREATE SEQUENCE bulk_upload_file_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from bulk_upload_file;
  execute immediate command1;

  select 'CREATE SEQUENCE card_info_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from card_info;
  execute immediate command1;

  select 'CREATE SEQUENCE channel_code_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from channel_code;
  execute immediate command1;

  select 'CREATE SEQUENCE channel_session_mgmt_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from channel_session_mgmt;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE charge_definition_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from charge_definition;
  execute immediate command1;

  select 'CREATE SEQUENCE charge_pricing_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from charge_pricing;
  execute immediate command1;

  select 'CREATE SEQUENCE charge_type_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from charge_type;
  execute immediate command1;

  select 'CREATE SEQUENCE chargetxn_transfer_map_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from chargetxn_transfer_map;
  execute immediate command1;

  select 'CREATE SEQUENCE company_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from company;
  execute immediate command1; 

  select 'CREATE SEQUENCE creditcard_destinations_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from creditcard_destinations;
  execute immediate command1;

  select 'CREATE SEQUENCE credit_card_transaction_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from credit_card_transaction;
  execute immediate command1; 

  select 'CREATE SEQUENCE db_param_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from db_param;
  execute immediate command1;

  select 'CREATE SEQUENCE denomination_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from denomination;
  execute immediate command1;

  select 'CREATE SEQUENCE distribution_chain_lvl_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from distribution_chain_lvl;
  execute immediate command1;

  select 'CREATE SEQUENCE distribution_chain_temp_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from distribution_chain_temp;
  execute immediate command1;

  select 'CREATE SEQUENCE enum_text_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from enum_text;
  execute immediate command1; 

  select 'CREATE SEQUENCE interbank_codes_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from interbank_codes;
  execute immediate command1;

  select 'CREATE SEQUENCE interbank_transfers_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from interbank_transfers;
  execute immediate command1;

  select 'CREATE SEQUENCE kyc_fields_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from kyc_fields;
  execute immediate command1;

  select 'CREATE SEQUENCE kyc_level_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from kyc_level;
  execute immediate command1;

  select 'CREATE SEQUENCE ledger_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from ledger;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE letter_of_purchase_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from letter_of_purchase;
  execute immediate command1;

  select 'CREATE SEQUENCE lop_history_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from lop_history;
  execute immediate command1;

  select 'CREATE SEQUENCE mdn_range_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mdn_range;
  execute immediate command1;

  select 'CREATE SEQUENCE merchant_code_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from merchant_code;
  execute immediate command1;

  select 'CREATE SEQUENCE merchant_prefix_code_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from merchant_prefix_code;
  execute immediate command1;

  select 'CREATE SEQUENCE mfino_service_provider_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mfino_service_provider;
  execute immediate command1;

  select 'CREATE SEQUENCE mfino_user_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mfino_user;
  execute immediate command1;

  select 'CREATE SEQUENCE mfs_biller_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mfs_biller;
  execute immediate command1;

  select 'CREATE SEQUENCE mfsbiller_partner_map_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mfsbiller_partner_map;
  execute immediate command1; 

  select 'CREATE SEQUENCE mobile_network_operator_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from mobile_network_operator;
  execute immediate command1; 

  select 'CREATE SEQUENCE notification_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from notification;
  execute immediate command1;

  select 'CREATE SEQUENCE offline_report_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from offline_report;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE offline_report_company_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from offline_report_company;
  execute immediate command1;
  
  select 'CREATE SEQUENCE offline_report_receiver_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from offline_report_receiver;
  execute immediate command1;
  
  select 'CREATE SEQUENCE partner_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from partner;
  execute immediate command1;
    
  select 'CREATE SEQUENCE partner_services_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from partner_services;
  execute immediate command1;
  
  select 'CREATE SEQUENCE pending_txns_entry_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from pending_txns_entry;
  execute immediate command1;
  
  select 'CREATE SEQUENCE pending_txns_file_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from pending_txns_file;
  execute immediate command1; 

  select 'CREATE SEQUENCE permission_item_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from permission_item;
  execute immediate command1; 

  select 'CREATE SEQUENCE person_2_person_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from person_2_person;
  execute immediate command1; 

  select 'CREATE SEQUENCE pocket_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from pocket;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE pocket_template_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from pocket_template;
  execute immediate command1;   

  select 'CREATE SEQUENCE product_indicator_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from product_indicator;
  execute immediate command1;
  
  select 'CREATE SEQUENCE region_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from region;
  execute immediate command1;
  
  select 'CREATE SEQUENCE report_parameters_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from report_parameters;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE role_permission_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from role_permission;
  execute immediate command1; 
  
  select 'CREATE SEQUENCE sap_groupid_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from sap_groupid;
  execute immediate command1;

  select 'CREATE SEQUENCE service_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from service;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE service_audit_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from service_audit;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE service_charge_txn_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from service_charge_txn_log;
  execute immediate command1; 
  
  select 'CREATE SEQUENCE service_settlement_cfg_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from service_settlement_cfg;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE service_transaction_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from service_transaction;
  execute immediate command1; 

  select 'CREATE SEQUENCE settlement_schedule_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from settlement_schedule_log;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE settlement_template_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from settlement_template;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE settlement_txn_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from settlement_txn_log;
  execute immediate command1; 

  select 'CREATE SEQUENCE share_partner_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from share_partner;
  execute immediate command1; 

  select 'CREATE SEQUENCE sms_code_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from sms_code;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE sms_partner_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from sms_partner;
  execute immediate command1; 
  
  select 'CREATE SEQUENCE sms_transaction_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from sms_transaction_log;
  execute immediate command1;  

  select 'CREATE SEQUENCE smsc_configuration_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from smsc_configuration;
  execute immediate command1;

  select 'CREATE SEQUENCE subscriber_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from subscriber;
  execute immediate command1; 

  select 'CREATE SEQUENCE subscriber_addi_info_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from subscriber_addi_info;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE subscriber_mdn_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from subscriber_mdn;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE system_parameters_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from system_parameters;
  execute immediate command1; 

  select 'CREATE SEQUENCE transaction_charge_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from transaction_charge;
  execute immediate command1;  
  
  select 'CREATE SEQUENCE transaction_charge_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from transaction_charge_log;
  execute immediate command1;  

  select 'CREATE SEQUENCE transaction_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from transaction_log;
  execute immediate command1; 

  select 'CREATE SEQUENCE transaction_rule_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from transaction_rule;
  execute immediate command1;

  select 'CREATE SEQUENCE transaction_type_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from transaction_type;
  execute immediate command1;   
  
  select 'CREATE SEQUENCE txn_amount_dstrb_log_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from txn_amount_dstrb_log;
  execute immediate command1; 

  select 'CREATE SEQUENCE unregistered_txn_info_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from unregistered_txn_info;
  execute immediate command1;

  select 'CREATE SEQUENCE visafone_txn_generator_id_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from visafone_txn_generator;
  execute immediate command1; 

  select 'CREATE SEQUENCE subscriber_groups_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from subscriber_groups;
  execute immediate command1; 

  select 'CREATE SEQUENCE groups_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from groups;
  execute immediate command1; 

  select 'CREATE SEQUENCE pocket_template_config_ID_SEQ start with ' ||  (nvl(max(id), 0)+1) || part1 into command1 from pocket_template_config;
  execute immediate command1;  
  
END;

/

commit;
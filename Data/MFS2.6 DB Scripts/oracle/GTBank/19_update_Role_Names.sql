UPDATE role SET DisplayText = 'Master_Admin' WHERE EnumValue = 'Master_Admin';
UPDATE role SET DisplayText = 'Access_and_Authorisation_User' WHERE EnumValue = 'System_Admin';
UPDATE role SET DisplayText = 'BACK_OFFICE_AGENT' WHERE EnumValue = 'Gallery_Admin';
UPDATE role SET DisplayText = 'COMPLIANCE' WHERE EnumValue = 'Gallery_Manager';
UPDATE role SET DisplayText = 'HEAD_OFFICE_AUDIT' WHERE EnumValue = 'Merchant_Support';
UPDATE role SET DisplayText = 'Bulk_Upload_Support' WHERE EnumValue = 'Bulk_Upload_Support';
UPDATE role SET DisplayText = 'E-SETTLEMENT' WHERE EnumValue = 'Sales_Admin';
UPDATE role SET DisplayText = 'AUDIT_AND_INVESTIGATION' WHERE EnumValue = 'Finance_Treasury';
UPDATE role SET DisplayText = 'MOBILE-MONEY_TEAM_CUSTOMER SERVICE' WHERE EnumValue = 'Finance_Admin';
UPDATE role SET DisplayText = 'CALL_CENTER' WHERE EnumValue = 'Customer_Care';
UPDATE role SET DisplayText = 'CALL_CENTER_SUPERVISOR' WHERE EnumValue = 'Customer_Care_Manager';
UPDATE role SET DisplayText = 'MOBILE-MONEY_SUPERVISOR' WHERE EnumValue = 'Reviewer';
UPDATE role SET DisplayText = 'MOBILE-MONEY_TEAM_MARKETING' WHERE EnumValue = 'Operation_Support';
UPDATE role SET DisplayText = 'FINCON' WHERE EnumValue = 'Finance_Support';
UPDATE role SET DisplayText = 'MOBILE-MONEY_TEAM_OPERATIONS' WHERE EnumValue = 'Omnibus_Support';
UPDATE role SET DisplayText = 'IT_Security_IS_Audit' WHERE EnumValue = 'Finance_Discount';
UPDATE role SET DisplayText = 'Tech_Audit_Super_User' WHERE EnumValue = 'Credit_Card_Reviewer';
UPDATE role SET DisplayText = 'BranchTeller' WHERE EnumValue = 'Bank_Customer_Care';
UPDATE role SET DisplayText = 'Partner' WHERE EnumValue = 'Service_Partner';
UPDATE role SET DisplayText = 'Agent' WHERE EnumValue = 'Business_Partner';
UPDATE role SET DisplayText = 'BankTeller' WHERE EnumValue = 'BankTeller';
UPDATE role SET DisplayText = 'BACK_OFFICE_AGENT_SUPERVISOR' WHERE EnumValue = 'Approver';
UPDATE role SET DisplayText = 'Corporate_User' WHERE EnumValue = 'Corporate_User';

-- Delete role 14,18,21 are not used in GT.
delete from role where enumcode = 21 and enumvalue = 'Bank_Customer_Care_Manager';
delete from role where enumcode = 18 and enumvalue = 'Subscriber';
delete from role where enumcode = 14 and enumvalue = 'Merchant';

commit;
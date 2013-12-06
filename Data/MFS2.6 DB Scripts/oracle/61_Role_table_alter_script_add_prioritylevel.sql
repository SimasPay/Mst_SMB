ALTER TABLE role ADD PriorityLevel Number(3,0) DEFAULT NULL;

  
UPDATE role SET prioritylevel = 1 WHERE enumvalue = 'Master_Admin';
UPDATE role SET prioritylevel = 1 WHERE enumvalue = 'System_Admin';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Gallery_Admin';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Gallery_Manager';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Merchant_Support';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Bulk_Upload_Support';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Sales_Admin';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Finance_Treasury';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Finance_Admin';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Customer_Care';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Customer_Care_Manager';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Reviewer';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Operation_Support';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Merchant';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Finance_Support';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Omnibus_Support';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Finance_Discount';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Subscriber';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Credit_Card_Reviewer';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Bank_Customer_Care';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Bank_Customer_Care_Manager';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Service_Partner';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Business_Partner';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'BankTeller';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Approver';
UPDATE role SET prioritylevel = 2 WHERE enumvalue = 'Corporate_User';

commit;
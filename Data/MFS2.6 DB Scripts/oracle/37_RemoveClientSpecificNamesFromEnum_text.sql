 
 UPDATE enum_text SET EnumValue='BalanceInquiry_Savings', DisplayText='BalanceInquiry_Savings' where TagID=5227 and EnumCode='311000';
  
 UPDATE enum_text SET EnumValue='BalanceInquiry_Check', DisplayText='BalanceInquiry_Check' where TagID=5227 and EnumCode='312000';
 
 UPDATE enum_text SET EnumValue='TransferInquiry_CheckToCheck', DisplayText='TransferInquiry_CheckToCheck' where TagID=5227 and EnumCode='302020';
 
 UPDATE enum_text SET EnumValue='TransferInquiry_SavingsToSavings', DisplayText='TransferInquiry_SavingsToSavings' where TagID=5227 and EnumCode='301010';
 
 UPDATE enum_text SET EnumValue='Transfer_CheckToCheck', DisplayText='Transfer_CheckToCheck' where TagID=5227 and EnumCode='402020';
 
 UPDATE enum_text SET EnumValue='Transfer_SavingsToSavings', DisplayText='Transfer_SavingsToSavings' where TagID=5227 and EnumCode='401010';
 
 UPDATE enum_text SET TagName='ISO8583_CurrencyCode' where TagID=6312;

 commit;
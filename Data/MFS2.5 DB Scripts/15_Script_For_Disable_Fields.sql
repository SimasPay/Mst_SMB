-- disable credit check, reference number fields in subscriber registration in admin application
Delete from kyc_fields where kycfieldsname in ('subsrefaccount','creditcheck');
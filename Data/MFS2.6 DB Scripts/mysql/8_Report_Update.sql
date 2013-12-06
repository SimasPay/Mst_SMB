

UPDATE offline_report SET Name='MFSSummaryReport' WHERE Name='eaZymoneysummaryReport';

UPDATE offline_report SET Name='MFSAccountDeactiveReport' WHERE Name='ZMMAccountDeactiveReport';

UPDATE offline_report SET ReportClass='com.mfino.report.zenith.financial.IncomeReport' WHERE Name='MFSIncomeReport';

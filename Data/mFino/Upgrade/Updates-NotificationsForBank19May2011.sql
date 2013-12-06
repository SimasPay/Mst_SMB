use mfino;

DROP TABLE IF EXISTS `bank`;
CREATE TABLE `bank` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL,
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `Name` varchar(255) DEFAULT NULL,
  `Description` varchar(255) DEFAULT NULL,
  `Status` int(11) DEFAULT NULL,
  `StatusTime` datetime DEFAULT NULL,
  `Version` int(11) NOT NULL DEFAULT '0',
  `BankCode` int(11) DEFAULT NULL,
  `Header` varchar(255) DEFAULT NULL,
  `ContactNumber` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


INSERT INTO `bank` (`ID`,`LastUpdateTime`,`UpdatedBy`,`CreateTime`,`CreatedBy`,`Name`,`Description`,`Status`,`StatusTime`,`Version`,`BankCode`,`Header`,`ContactNumber`) VALUES
 (1,now(),'user',now(),'user','BSM','Bank Sinarmas',0,now(),1,153,'Bank Sinarmas','881'),
 (2,now(),'user',now(),'user','BPRKS','BPRKS',0,now(),1,152,'mBPRKS','022-99999');   

DROP TABLE IF EXISTS `mfino`.`bank_admin`;
CREATE TABLE  `mfino`.`bank_admin` (
  `ID` bigint(20) NOT NULL AUTO_INCREMENT,
  `Version` int(11) NOT NULL,
  `LastUpdateTime` datetime NOT NULL,
  `UpdatedBy` varchar(255) NOT NULL DEFAULT ' ',
  `CreateTime` datetime NOT NULL,
  `CreatedBy` varchar(255) NOT NULL,
  `UserID` bigint(20) NOT NULL,
  `BankID` bigint(20) NOT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_BankAdmin_Bank` (`BankID`),
  KEY `FK_BankAdmin_User` (`UserID`),
  CONSTRAINT `FK_BankAdmin_Bank` FOREIGN KEY (`BankID`) REFERENCES `bank` (`ID`),
  CONSTRAINT `FK_BankAdmin_User` FOREIGN KEY (`UserID`) REFERENCES `user` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

UPDATE notification SET Text='$(BankName) Your Smart Dompet Balance on  $(CurrentDateTime) is $(BankAccountCurrency) $(BankAccountBalanceValue).' WHERE Code=4 AND  Language=0;

UPDATE notification SET Text='$(BankName) Saldo Smart Dompet Anda pada $(CurrentDateTime) adalah $(BankAccountCurrency) $(BankAccountBalanceValue).' WHERE Code=4 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Your number is not registered in Dompet Service. Please do M-Commerce Activation to get this service. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=8 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Nomor anda tidak terdaftar dalam layanan Dompet. Untuk mendapatkan layanan ini, lakukanlah Aktivasi M-Commerce. Info hub $(ContactCenterNo). REF:$(TransactionID)' WHERE Code=8 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=9 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=9 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Your Dompet service is not active. To activate, go to Activate M-Commerce menu. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=30 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Layanan Dompet anda tidak aktif. Untuk mengaktifkan layanan M-Commerce, pilih menu Aktifkan M-Commerce. Info, hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=30 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=31 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=31 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=36 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=36 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=41 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=41 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=42 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=42 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=57 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=57 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Account is already activated. Info, call $(ContactCenterNo)' WHERE Code=58 AND  Language=0;
UPDATE notification SET Text='$(BankName)  Rekening anda sudah diaktifkan. Info hub $(ContactCenterNo).' WHERE Code=58 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your Request has been processed. Please wait. Thank you' WHERE Code=61 AND  Language=0;
UPDATE notification SET Text='$(BankName) Permintaan anda sedang diproses. Silahkan tunggu. Terima kasih.' WHERE Code=61 AND  Language=1;

UPDATE notification SET Text='$(BankName)  Welcome to M-Commerce Service. You can use your PIN for Smart Dompet and share load service. For info call $(ContactCenterNo)' WHERE Code=63 AND  Language=0;
UPDATE notification SET Text='$(BankName) Selamat datang di layanan M-Commerce. Anda bisa menggunakan layanan SMART Dompet dan Kirim Pulsa dengan PIN Anda. Untuk info hub $(ContactCenterNo)' WHERE Code=63 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=64 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=64 AND  Language=1;

UPDATE notification SET Text='$(BankName) You have successfully changed your Dompet PIN on $(TransactionDateTime).' WHERE Code=65 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah berhasil melakukan perubahan Dompet PIN pada $(TransactionDateTime).' WHERE Code=65 AND  Language=1;

UPDATE notification SET Text='$(BankName) $(BankAccountTransactionDate) $(BankAccountTransactionType) $(BankAccountCurrency) $(BankAccountTransactionAmount) ' WHERE Code=67 AND  Language=0;
UPDATE notification SET Text='$(BankName) $(BankAccountTransactionDate) $(BankAccountTransactionType) $(BankAccountCurrency) $(BankAccountTransactionAmount) ' WHERE Code=67 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=68 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=68 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=71 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=71 AND  Language=1;

UPDATE notification SET Text='$(BankName) Thank you for topping up $(Amount) on $(TransactionDateTime) to $(ReceiverMDN). REF: $(TransactionID)' WHERE Code=77 AND  Language=0;
UPDATE notification SET Text='$(BankName) Terima kasih atas pengisian pulsa sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF: $(TransactionID)' WHERE Code=77 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=80 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=80 AND  Language=1;

UPDATE notification SET Text='$(BankName) Thank you for transferring $(Currency) $(Amount) to $(ReceiverMDN) on $(TransactionDateTime). REF: $(TransactionID)' WHERE Code=81 AND  Language=0;
UPDATE notification SET Text='$(BankName) Terima kasih atas pengiriman  sebesar $(Currency) $(Amount) ke $(ReceiverMDN) pada tanggal $(TransactionDateTime). REF: $(TransactionID)' WHERE Code=81 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=82 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=82 AND  Language=1;

UPDATE notification SET Text='$(BankName)  Sorry, Smart-Dompet Activation failed. Your request is rejected from bank. For more info, please contact Bank Sinar Mas Customer service on nearest location.' WHERE Code=83 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, Aktivasi Smart Dompet gagal. Permintaan anda ditolak dari bank. Untuk informasi lanjut, hubungi Customer Service Bank Sinar Mas di cabang terdekat.' WHERE Code=83 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, your request to change Dompet PIN on $(TransactionDateTime) failed.  Info, call $(ContactCenterNo).' WHERE Code=84 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, permintaan Anda untuk mengubah Dompet PIN pada $(TransactionDateTime) tidak berhasil. Info hub $(ContactCenterNo).' WHERE Code=84 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=85 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=85 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, your request to check balance on $(CurrentDateTime) failed. Pls try again . For info, call $(ContactCenterNo).' WHERE Code=86 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, permintaan Anda untuk cek saldo pada $(CurrentDateTime) tidak berhasil. Silakan ulangi kembali.  Untuk info hub $(ContactCenterNo).' WHERE Code=86 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=87 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=87 AND  Language=1;

UPDATE notification SET Text='$(BankName) Your transaction is having a problem. To check the status of transaction, contact $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=88 AND  Language=0;
UPDATE notification SET Text='$(BankName) Transaksi yang anda lakukan mengalami masalah. Untuk memastikan status transaksi, hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=88 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=89 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses. Untuk info hub $(ContactCenterNo). REF: $(TransactionID))' WHERE Code=89 AND  Language=1;

UPDATE notification SET Text='$(BankName) You have received Airtime Topup of $(Amount) on $(TransactionDateTime). REF:$(TransactionID)' WHERE Code=124 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima pengisian pulsa sebesar  $(Amount) pada $(TransactionDateTime) berhasil. REF:$(TransactionID)' WHERE Code=124 AND  Language=1;

UPDATE notification SET Text='$(BankName) Thank you for paying $(Currency) $(Amount)  to SMART Mobile No $(PostpaidMDN) on $(TransactionDateTime) through $(BankName) .REF:$(TransactionID)' WHERE Code=125 AND  Language=0;
UPDATE notification SET Text='$(BankName) Pembayaran $(Currency) $(Amount) untuk nomor $(PostpaidMDN) telah kami terima pada $(TransactionDateTime) dari $(BankName). Terima kasih. REF:$(TransactionID)' WHERE Code=125 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, you are not allowed to do Change ATM PIN via handphone. Please change your PIN in the nearest Bank Sinarmas ATM. Info, call $(ContactCenterNo).' WHERE Code=137 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, Anda tidak diperbolehkan mengubah PIN melalui handphone. Silakan ubah PIN anda di ATM Bank Sinarmas terdekat. Untuk info hub $(ContactCenterNo).' WHERE Code=137 AND  Language=1;

UPDATE notification SET Text='$(BankName) You have cancelled your fund transfer request. Thank you. ' WHERE Code=145 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah membatalkan transaksi pengiriman dana anda. Terima kasih' WHERE Code=145 AND  Language=1;

UPDATE notification SET Text='$(BankName) You have received $(Currency) $(Amount) from $(SenderMDN) on $(TransactionDateTime). Msg: $(OptionalTextMessage). REF: $(TransactionID)' WHERE Code=223 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima sebesar $(Currency) $(Amount) dari $(SenderMDN) pada tanggal $(TransactionDateTime). Pesan : $(OptionalTextMessage). REF: $(TransactionID)' WHERE Code=223 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Bank could not process your request. Please retry or call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=232 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Bank tidak dapat memproses permintaan anda. Silakan ulangi kembali. atau hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=232 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry transaction on $(TransactionDateTime) failed due to invalid amount. Please retry with correct amount. Info, call $(ContactCenterNo). REF: $(TransactionID). ' WHERE Code=233 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena jumlah yang dimasukkan salah. Silakan ulangi kembali dengan jumlah yang benar. Info hub $(ContactCenterNo). REF: $(TransactionID). ' WHERE Code=233 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry transaction on $(TransactionDateTime) failed. This service is currently not available. To check available services, call $(ContactCenterNo).  REF: $(TransactionID). ' WHERE Code=234 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Layanan ini sekarang belum tersedia. Untuk mengetahui layanan yang tersedia, hub $(ContactCenterNo). REF: $(TransactionID). ' WHERE Code=234 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on  $(TransactionDateTime) failed due to insufficient balance. Info, call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=235 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena saldo  tidak cukup untuk melakukan transaksi ini. Info, hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=235 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Checking Account does not exist. Please retry with different account. Info, call $(ContactCenterNo). REF: $(TransactionID' WHERE Code=236 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime)  gagal karena rekening giro tidak terdaftar. Silakan ulangi kembali dengan rekening lain. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=236 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Saving Account does not exist. Please retry with different account or call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=237 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime)  gagal karena rekening tabungan tidak terdaftar. Silakan ulangi kembali dengan rekening lain. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=237 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Card already expired. To renew card, contact your bank card issuer. REF: $(TransactionID)' WHERE Code=238 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime)  gagal. Masa berlaku kartu telah habis. Untuk memperbarui, silakan hubungi bank penerbit kartu Anda. REF: $(TransactionID)' WHERE Code=238 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. This transaction is restricted.  To access, contact your bank card issuer. Info, call $(ContactCenterNo). REF: $(TransactionID) ' WHERE Code=239 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Transaksi ini tidak diperbolehkan. Utk akses transaksi, hub bank penerbit kartu anda. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=239 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on  $(TransactionDateTime) failed due to card restriction. To reactivate, contact your bank card issuer.  REF: $(TransactionID)  ' WHERE Code=240 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal krn kartu terblokir. Untuk buka blokir, silakan hubungi bank penerbit kartu Anda. REF: $(TransactionID)' WHERE Code=240 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error occurred while bank is checking your PIN. Please retry or call $(ContactCenterNo). REF: $(TransactionID) ' WHERE Code=241 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena gangguan identifikasi PIN pada bank. Silakan ulangi kembali atau hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=241 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. No bank confirmation was received. Please retry. Info, call $(ContactCenterNo).REF: $(TransactionID)' WHERE Code=242 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi  pada $(TransactionDateTime) gagal. Tidak ada konfirmasi dari Bank. Silakan ulangi kembali. Info, hub $(ContactCenterNo). REF: $(TransactionID).' WHERE Code=242 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Your M-Banking service is temporarily blocked due to too many PIN retries. To unlock, contact your Bank. REF: $(TransactionID)' WHERE Code=243 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada  $(TransactionDateTime) gagal. Layanan M-Banking Anda terblokir karena melewati batas maksimum salah PIN. Untuk buka blokir, hub Bank Anda. REF: $(TransactionID)' WHERE Code=243 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Account does not exist. Please retry with different account or call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=244 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena rekening tabungan tidak terdaftar. Silakan ulangi kembali dengan rekening lain. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=244 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Bank's system may be under maintenance. Please retry or call $(ContactCenterNo). REF: $(TransactionID)" WHERE Code=245 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Sistem Bank dalam pemeliharaan. Silakan ulangi kembali. atau hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=245 AND  Language=1;

UPDATE notification SET Text='$(BankName) Sorry, transaction on $(TransactionDateTime) failed due to possible duplicate transmission. Please retry or call $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=246 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena kemungkinan transaksi ganda. Silakan coba lagi atau hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=246 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Bank's system may be under maintenance. Please retry or call $(ContactCenterNo). REF: $(TransactionID)" WHERE Code=247 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Sistem Bank dalam pemeliharaan. Silakan ulangi kembali atau hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=247 AND  Language=1;

UPDATE notification SET Text="$(BankName) You have received airtime topup of  $(Amount) from $(SenderMDN) on $(TransactionDateTime). REF: $(TransactionID)" WHERE Code=248 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima pengisian pulsa sebesar $(Amount) dari $(SenderMDN) pada tanggal $(TransactionDateTime). REF: $(TransactionID)' WHERE Code=248 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on  $(TransactionDateTime) failed due to wrong PIN. Please retry using correct PIN. Info, call $(ContactCenterNo). REF: $(TransactionID)" WHERE Code=262 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal karena PIN salah. Silakan ulangi lagi dgn menggunakan PIN yang benar. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=262 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed. An error in Bank occurred while processing your request. Info, call $(ContactCenterNo). REF: $(TransactionID)" WHERE Code=263 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Permintaan anda tidak dapat diproses oleh Bank. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=263 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Your transaction amount is more than maximum amount of bank transaction limit. Please retry on next day. Info, call $(ContactCenterNo).REF: $(TransactionID)" WHERE Code=269 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Nilai transaksi anda lebih dari batas maksimum per transaksi dari bank. Silakan mencoba kembali pada hari berikutnya. Info hub $(ContactCenterNo).REF: $(TransactionID)' WHERE Code=269 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime)  failed. Maximum number of transactions at the Bank is exceeded. Please retry on next day. Info, call $(ContactCenterNo).REF: $(TransactionID)" WHERE Code=270 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Jumlah maksimum transaksi yang diperbolehkan oleh bank sudah terlewati. Silakan coba di hari berikutnya. Info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=270 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed.Your account is not active. Info, call $(ContactCenterNo) or Bank.  REF: $(TransactionID)" WHERE Code=271 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Rekening anda tidak aktif. Untuk info hub $(ContactCenterNo) atau Bank anda. REF: $(TransactionID)' WHERE Code=271 AND  Language=1;

UPDATE notification SET Text="$(BankName) Thank you for E-money reload $(Amount) on $(TransactionDateTime) to $(ReceiverMDN).  REF: $(TransactionID)" WHERE Code=305 AND  Language=0;
UPDATE notification SET Text='$(BankName) Terima kasih atas pengisian ulang E-Money sebesar $(Amount) pada tanggal $(TransactionDateTime) ke $(ReceiverMDN). REF:$(TransactionID)' WHERE Code=305 AND  Language=1;

UPDATE notification SET Text="$(BankName) You have received $(Currency) $(Amount) on $(TransactionDateTime). REF: $(TransactionID)" WHERE Code=306 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima pengisian ulang E-Money sebesar $(Currency) $(Amount) pada $(TransactionDateTime).  REF: $(TransactionID)' WHERE Code=306 AND  Language=1;

UPDATE notification SET Text="$(BankName) Thank you for transferring $(Currency) $(Amount) to $(ReceiverMDN) on $(TransactionDateTime). REF: $(TransactionID)" WHERE Code=307 AND  Language=0;
UPDATE notification SET Text='$(BankName) Terima kasih atas pengiriman  sebesar $(Currency) $(Amount) ke $(ReceiverMDN) pada tanggal $(TransactionDateTime). REF: $(TransactionID)' WHERE Code=307 AND  Language=1;

UPDATE notification SET Text="$(BankName) You have received $(Currency) $(Amount) from $(SenderMDN) on $(TransactionDateTime). Msg: $(OptionalTextMessage). REF: $(TransactionID)" WHERE Code=308 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima sebesar $(Currency) $(Amount) dari $(SenderMDN) pada tanggal $(TransactionDateTime). Pesan : $(OptionalTextMessage). REF: $(TransactionID)' WHERE Code=308 AND  Language=1;

UPDATE notification SET Text="$(BankName) Thank you for transferring $(Currency) $(Amount) to $(ReceiverMDN) on $(TransactionDateTime). REF: $(TransactionID)" WHERE Code=309 AND  Language=0;
UPDATE notification SET Text='$(BankName) Terima kasih atas pengiriman  sebesar $(Currency) $(Amount) ke $(ReceiverMDN) pada tanggal $(TransactionDateTime). REF: $(TransactionID)' WHERE Code=309 AND  Language=1;

UPDATE notification SET Text="$(BankName) You have received $(Currency) $(Amount) from $(SenderMDN) on $(TransactionDateTime). Msg: $(OptionalTextMessage). REF: $(TransactionID)" WHERE Code=310 AND  Language=0;
UPDATE notification SET Text='$(BankName) Anda telah menerima sebesar $(Currency) $(Amount) dari $(SenderMDN) pada tanggal $(TransactionDateTime). Pesan : $(OptionalTextMessage). REF: $(TransactionID)' WHERE Code=310 AND  Language=1;

UPDATE notification SET Text="$(BankName) Sorry, transaction on $(TransactionDateTime) failed. Our system is under maintenance. Info, call $(ContactCenterNo). REF: $(TransactionID)" WHERE Code=318 AND  Language=0;
UPDATE notification SET Text='$(BankName) Maaf, transaksi pada $(TransactionDateTime) gagal. Sistem kami sedang dalam pemeliharaan. Untuk info hub $(ContactCenterNo). REF: $(TransactionID)' WHERE Code=318 AND  Language=1;



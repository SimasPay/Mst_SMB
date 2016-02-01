
ALTER TABLE PARTNER ADD (
BANKBRANCHCODE NUMBER(10),
BRANCHSEQUENCE NUMBER(10), 
ACCOUNTNUMBEROFBANKSINARMAS VARCHAR2(255), 
COMPANYEMAILID VARCHAR2(255));


ALTER TABLE SUBSCRIBER_ADDI_INFO ADD (
AGREEMENTNUMBER VARCHAR2(255),
AGENTCOMPANYNAME VARCHAR2(255),
LATITUDE VARCHAR2(255),
LONGITUDE VARCHAR2(255),
USERBANKBRANCH VARCHAR2(255),
ELECTONICDEVICEUSED NUMBER(10),
BANKACCOUNTSTATUS NUMBER(10),
AGREMENTDATE TIMESTAMP,
IMPLEMENTATINDATE TIMESTAMP);


delete from enum_text where TAGID=8302;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentType','8302','1','PersonalAgent','PersonalAgent');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentType','8302','2','Corporate','Corporate');

delete from enum_text where TAGID=8303;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','1','A','A');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','2','B','B');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','3','C','C');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','4','D','D');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','5','E','E');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','6','F','F');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','7','G','G');

delete from enum_text where TAGID=8304;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','160010','Industri Pengolahan Tembakau','Industri Pengolahan Tembakau'); 
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','181000','Industri Pakaian Jadi dan perlengkapannya','Industri Pakaian Jadi dan perlengkapannya');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','192000','Industri Alas Kaki','Industri Alas Kaki');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','512200','Perdagangan Besar Makanan, Minuman dan Tembakau','Perdagangan Besar Makanan, Minuman dan Tembakau');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','513900','Perdagangan Besar Barang-barang Keperluan Rumah Tangga lainnya','Perdagangan Besar Barang-barang Keperluan Rumah Tangga lainnya');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','521100','Perdagangan Eceran Berbagai Macam Barang yang Didominasi Makanan, Minuman dan Tembakau','Perdagangan Eceran Berbagai Macam Barang yang Didominasi Makanan, Minuman dan Tembakau');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','521900','Perdagangan Eceran Berbagai Macam Barang yang Didominasi Oleh Barang Bukan Makanan, Minuman dan Tembakau','Perdagangan Eceran Berbagai Macam Barang yang Didominasi Oleh Barang Bukan Makanan, Minuman dan Tembakau');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','522100','Perdagangan Eceran Komoditi Makanan dari Hasil Pertanian','Perdagangan Eceran Komoditi Makanan dari Hasil Pertanian');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','522200','Perdagangan Eceran Komuditi makanan,Minuman dan Tembakau hasil industri pengolahan','Perdagangan Eceran Komuditi makanan,Minuman dan Tembakau hasil industri pengolahan');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','523200','Perdagangan Eceran Tekstil, Pakaian Jadi, Alas Kaki, dan Barang Keperluan Pribadi','Perdagangan Eceran Tekstil, Pakaian Jadi, Alas Kaki, dan Barang Keperluan Pribadi');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','523300','Perdagangan Eceran Perlengkapan Rumah Tangga dan Perlengkapan Dapur','Perdagangan Eceran Perlengkapan Rumah Tangga dan Perlengkapan Dapur');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','523400','Perdagangan Eceran Bahan Konstruksi','Perdagangan Eceran Bahan Konstruksi');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','523600','Perdagangan Eceran Kertas, Barang-barangdari Kertas, Alat Tulis, Barang Cetakan, Alat Olahraga, Alat Musik, Alat Fotografi. Komputer','Perdagangan Eceran Kertas, Barang-barangdari Kertas, Alat Tulis, Barang Cetakan, Alat Olahraga, Alat Musik, Alat Fotografi. Komputer');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','524000','Perdagangan Eceran Barang Bekas','Perdagangan Eceran Barang Bekas');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','525100','Perdagangan Eceran Kaki Lima Komoditi dari Hasil Pertanian','Perdagangan Eceran Kaki Lima Komoditi dari Hasil Pertanian');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','525200','Perdagangan Eceran Kaki Lima Komoditi Makanan, Minuman Hasil Industri Pengolahan','Perdagangan Eceran Kaki Lima Komoditi Makanan, Minuman Hasil Industri Pengolahan');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','525400','Perdagangan Eceran Tekstil, Pakaian Jadi, Alas Kaki, dan Barang Keperluan Pribadi','Perdagangan Eceran Tekstil, Pakaian Jadi, Alas Kaki, dan Barang Keperluan Pribadi');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','525500','Perdagangan Eceran Kaki Lima Perlengkapan, Rumah Tangga dan Perlengkapan Dapur','Perdagangan Eceran Kaki Lima Perlengkapan, Rumah Tangga dan Perlengkapan Dapur');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','552100','Restoran / Rumah Makan','Restoran / Rumah Makan');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','552009','Penyediaan Makan Minum Lainnya','Penyediaan Makan Minum Lainnya');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','729000','Kegiatan Lain yang Berkaitan Dengan Komputer','Kegiatan Lain yang Berkaitan Dengan Komputer');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypeofBusinessAgent','8304','743000','Jasa Periklanan','Jasa Periklanan');

delete from enum_text where TAGID=8305;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','1','EDC','EDC');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','2','Telephone Selular','Telephone Selular');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','3','Computer','Computer');

delete from enum_text where TAGID=8389;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','1','Agen baru','Agen baru');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','2','Perubahan klasifikasi Agen','Perubahan klasifikasi Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','3','Perubahan jenis usaha Agen','Perubahan jenis usaha Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','4','Perubahan lokasi Agen','Perubahan lokasi Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','5','Penghentian Agen karena pelanggaran','Penghentian Agen karena pelanggaran');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','6','Penghentian Agen karena habis jangka waktu perjanjian kerjasama dan tanpa perpanjangan kerjasama','Penghentian Agen karena habis jangka waktu perjanjian kerjasama dan tanpa perpanjangan kerjasama');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','7','Perubahan dan/atau penambahan perangkat elektronik','Perubahan dan/atau penambahan perangkat elektronik');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BankAccountStatus','8389','8','Agen pasif (Agen tidak aktif melayani transaksi selama lebih dari 90 hari)','Agen pasif (Agen tidak aktif melayani transaksi selama lebih dari 90 hari)');

commit;

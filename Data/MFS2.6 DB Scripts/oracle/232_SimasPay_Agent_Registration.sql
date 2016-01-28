
ALTER TABLE PARTNER ADD (
BRANCHCODE NUMBER(10), 
BRANCHSEQUENCE NUMBER(10), 
ACCOUNTNUMBEROFBANKSINARMAS VARCHAR2(255), 
BRANCHOFBANKSINARMAS VARCHAR2(255), 
MERCHANTADDRESSID2 VARCHAR2(255)
COMPANYEMAILID VARCHAR2(255));


ALTER TABLE SUBSCRIBER_ADDI_INFO ADD (
ELECTONICDEVIEUSED VARCHAR2(255), 
AGENTDESCRIPTION VARCHAR2(255),
KTPID VARCHAR2(255),
AGREEMENTNUMBER VARCHAR2(255),
AGREEMENTDATE VARCHAR2(255),
IMPLEMENTATIONDATE VARCHAR2(255),
AGENTCOMPANYNAME VARCHAR2(255),
LATITUDELONGITUDE VARCHAR2(255));
PLACEOFBIRTH VARCHAR2(255),
DATEOFBIRTH VARCHAR2(255),
USERBANKBRANCH VARCHAR2(255),
BANKACCOUNTSTATUS VARCHAR2(255));


delete from enum_text where TAGID=8202;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentType','8302','1','PersonalAgent','PersonalAgent');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentType','8302','2','Corporate','Corporate');

delete from enum_text where TAGID=8203;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','1','A','A');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','2','B','B');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','3','C','C');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','4','D','D');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','5','E','E');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','6','F','F');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ClassificationAgent','8303','7','G','G');

delete from enum_text where TAGID=8204;
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

delete from enum_text where TAGID=8205;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','1','EDC','EDC');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','2','Telephone Selular','Telephone Selular');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','ElectonicDevieused','8305','3','Computer','Computer');

delete from enum_text where TAGID=8206;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','1','Agen baru','Agen baru');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','2','Perubahan klasifikasi Agen','Perubahan klasifikasi Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','3','Perubahan jenis usaha Agen','Perubahan jenis usaha Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','4','Perubahan lokasi Agen','Perubahan lokasi Agen');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','5','Penghentian Agen karena pelanggaran','Penghentian Agen karena pelanggaran');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','6','Penghentian Agen karena habis jangka waktu perjanjian kerjasama dan tanpa perpanjangan kerjasama','Penghentian Agen karena habis jangka waktu perjanjian kerjasama dan tanpa perpanjangan kerjasama');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','7','Perubahan dan/atau penambahan perangkat elektronik','Perubahan dan/atau penambahan perangkat elektronik');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','AgentDescription','8306','8','Agen pasif (Agen tidak aktif melayani transaksi selama lebih dari 90 hari)','Agen pasif (Agen tidak aktif melayani transaksi selama lebih dari 90 hari)');

delete from enum_text where TAGID=8207;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','KCKCPKKBankSinarmas','8307','002','KCU Thamrin','KCU Thamrin');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','KCKCPKKBankSinarmas','8307','003','KC Zainul Arifin','KC Zainul Arifin');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','KCKCPKKBankSinarmas','8307','004','KC Hasyim Ashari','KC Hasyim Ashari');

delete from enum_text where TAGID=8208;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BranchCode','8233','002','002','002');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BranchCode','8233','003','003','003');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','BranchCode','8233','004','004','004');

delete from enum_text where TAGID=8222;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','DomicileAddress','8322','1','In Accordance Identity','In Accordance Identity');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','DomicileAddress','8322','2','Different Accordance Identity','Different Accordance Identity');

delete from enum_text where TAGID=8232;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Jobs','8332','1','Govermant Employees','Govermant Employees');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Jobs','8332','2','Professional','Professional');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Jobs','8332','3','Entrepreneurial','Entrepreneurial');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Jobs','8332','4','Private employees','Private employees');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Jobs','8332','5','Other','Other');

delete from enum_text where TAGID=8263;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypesofbusinessEntity','8363','1','PT(Company Limited)','PT(Company Limited)');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypesofbusinessEntity','8363','2','UD(Trading Businesses)','UD(Trading Businesses)');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypesofbusinessEntity','8363','3','PD(Local Company)','PD(Local Company)');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypesofbusinessEntity','8363','4','KUD(Village Unit Cooperatives)','KUD(Village Unit Cooperatives)');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','TypesofbusinessEntity','8363','5','Other','Other');

delete from enum_text where TAGID=8266;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','StatusofBusinessSites','8366','1','Ones own','Ones own');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','StatusofBusinessSites','8366','2','Other','Other');

delete from enum_text where TAGID=8269;
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','LegalRelationship','8369','1','Authority','Authority');
INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','LegalRelationship','8369','2','Other','Other');

INSERT INTO role (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, EnumCode, EnumValue, DisplayText,PRIORITYLEVEL, IsSystemUser) VALUES (23,1,sysdate,'System',sysdate,'system','23','Business_Partner','Agent',2,1);

INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','10209');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','10211');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','10228');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','10235');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','10401');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','12003');
INSERT INTO role_permission (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, Role, Permission) VALUES ('1',sysdate,'system',sysdate,'system','23','12206');
insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'System', 25, 12206);

INSERT INTO enum_text (VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES ('1',sysdate,'system',sysdate,'system','0','Permission','5493','12206','BusinessPartnerSp_View','BusinessPartnerSp_View');

INSERT INTO permission_group (ID, Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,PermissionGroupName) VALUES(27, '1', sysdate, 'system', sysdate, 'system', 'Agentsp');
INSERT INTO permission_item (Version, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy,Permission,ItemType,ItemID,FieldID,Action,PERMISSIONGROUPID,DESCRIPTION) VALUES('1', sysdate, 'system', sysdate, 'system', 12206,1,'businessPartner','default','default',27,'View Agentsp Tab');
insert into role_permission (Version,LastUpdateTime,UpdatedBy,CreateTime,CreatedBy,Role,Permission) values (1, sysdate, 'System', sysdate, 'System', 25, 12206);

commit;

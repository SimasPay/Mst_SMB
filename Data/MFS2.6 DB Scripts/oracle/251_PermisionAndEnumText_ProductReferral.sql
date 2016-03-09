delete from enum_text where TAGID=8421;
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Kartu Kredit','Kartu Kredit','Kartu Kredit');
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Kredit Kepemilikan Mobil','Kredit Kepemilikan Mobil','Kredit Kepemilikan Mobil');
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Pinjaman Modal Kerja','Pinjaman Modal Kerja','Pinjaman Modal Kerja');
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Kredit Investasi','Kredit Investasi','Kredit Investasi');
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Kredit Usaha Mikro','Kredit Usaha Mikro','Kredit Usaha Mikro');
INSERT INTO enum_text (ID,VERSION, LastUpdateTime, UpdatedBy, CreateTime, CreatedBy, LANGUAGE, TagName, TagID, EnumCode, EnumValue, DisplayText) VALUES (ENUM_TEXT_ID_SEQ.NEXTVAL,'1',sysdate,'system',sysdate,'system','0','ProductDesired','8421','Lainnya','Lainnya','Lainnya');



INSERT INTO PERMISSION_ITEM  (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE,ITEMID,FIELDID,ACTION,PERMISSIONGROUPID,DESCRIPTION) VALUES (PERMISSION_ITEM_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system',23101,1,'ProductReferrals','default','default',null,null);
INSERT INTO PERMISSION_ITEM  (ID,VERSION, LASTUPDATETIME, UPDATEDBY, CREATETIME, CREATEDBY, PERMISSION, ITEMTYPE,ITEMID,FIELDID,ACTION,PERMISSIONGROUPID,DESCRIPTION) VALUES (PERMISSION_ITEM_ID_SEQ.NEXTVAL,1,SYSDATE,'system',SYSDATE,'system',23102,1,'ProductReferrals.view','default','default',null,null);
commit;



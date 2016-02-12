CREATE   TABLE PROVINCE
(ID NUMBER(19,0) NOT NULL ENABLE, 
 VERSION NUMBER(10,0) NOT NULL ENABLE, 
 LASTUPDATETIME TIMESTAMP (0) NOT NULL ENABLE, 
 UPDATEDBY VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL ENABLE, 
 CREATETIME TIMESTAMP (0) NOT NULL ENABLE, 
 CREATEDBY VARCHAR2(255 CHAR) NOT NULL ENABLE, 
 DISPLAYTEXT VARCHAR2(200 CHAR),
 PROVINCEID VARCHAR2(20 CHAR) NOT NULL ENABLE, 
  CONSTRAINT PRIMARY_PROVINCE PRIMARY KEY (ID)
   );
   
  DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE province_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM PROVINCE;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/

CREATE TABLE "PROVINCE_REGION"("ID" NUMBER(19,0) NOT NULL ENABLE, 
 "VERSION" NUMBER(10,0) NOT NULL ENABLE, 
 "LASTUPDATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "UPDATEDBY" VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL ENABLE, 
 "CREATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "CREATEDBY" VARCHAR2(255 CHAR) NOT NULL ENABLE,
 "REGIONID" VARCHAR2(20 CHAR) NOT NULL ENABLE,
 "DISPLAYTEXT" VARCHAR2(200 CHAR),
 "BIREGIONID" VARCHAR2(20 CHAR) NOT NULL ENABLE,
 "IDPROVINCE" NUMBER(19,0) NOT NULL ENABLE, 
 CONSTRAINT "ID_PROVINCE" FOREIGN KEY ("IDPROVINCE")
REFERENCES "PROVINCE" ("ID") ENABLE,  
CONSTRAINT "PRIMARY_PROVINCE_REGION" PRIMARY KEY("ID"));

DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE province_region_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM PROVINCE_REGION;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/

CREATE TABLE "DISTRICT" ("ID" NUMBER(19,0) NOT NULL ENABLE, 
 "VERSION" NUMBER(10,0) NOT NULL ENABLE, 
 "LASTUPDATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "UPDATEDBY" VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL ENABLE, 
 "CREATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "CREATEDBY" VARCHAR2(255 CHAR) NOT NULL ENABLE,
 "DISTRICTID" VARCHAR2(20 CHAR) NOT NULL ENABLE, 
 "DISPLAYTEXT" VARCHAR2(200 CHAR), 
 "IDREGION" NUMBER(19,0) NOT NULL ENABLE, 
  CONSTRAINT "ID_REGION"  FOREIGN KEY("IDREGION") REFERENCES PROVINCE_REGION("ID"),
  CONSTRAINT "PRIMARY_DISTRICT" PRIMARY KEY ("ID")
   );
   
DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE district_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM DISTRICT;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/


CREATE TABLE "VILLAGE" ("ID" NUMBER(19,0) NOT NULL ENABLE, 
 "VERSION" NUMBER(10,0) NOT NULL ENABLE, 
 "LASTUPDATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "UPDATEDBY" VARCHAR2(255 CHAR) DEFAULT ' ' NOT NULL ENABLE, 
 "CREATETIME" TIMESTAMP (0) NOT NULL ENABLE, 
 "CREATEDBY" VARCHAR2(255 CHAR) NOT NULL ENABLE,
 "VILLAGEID" VARCHAR2(20 CHAR) NOT NULL ENABLE, 
 "DISPLAYTEXT" VARCHAR2(200 CHAR), 
 "IDDISTRICT" NUMBER(19,0) NOT NULL ENABLE, 
  CONSTRAINT "ID_DISTRICT"  FOREIGN KEY("IDDISTRICT") REFERENCES DISTRICT("ID"),
  CONSTRAINT "PRIMARY_VILLAGE" PRIMARY KEY ("ID")
   );
   
   
DECLARE 
  COMMAND1 VARCHAR(255);
  PART1 VARCHAR(100) := ' MINVALUE 1 MAXVALUE 999999999999999999999999 INCREMENT BY 1  NOCYCLE';
BEGIN
  SELECT 'CREATE SEQUENCE village_ID_SEQ start with ' ||  (NVL(MAX(ID), 0)+1) || PART1 INTO COMMAND1 FROM VILLAGE;
   EXECUTE IMMEDIATE COMMAND1; 
END;
/

commit;
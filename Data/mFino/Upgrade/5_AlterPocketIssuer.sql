use mfino;

ALTER TABLE pocket_issuer 
ADD COLUMN CreatedBy varchar(255) null;

ALTER TABLE pocket_issuer 
ADD COLUMN CreateTime DATETIME null;

ALTER TABLE subscriber ADD COLUMN `Nickname` varchar(255);
ALTER TABLE subscriber ADD COLUMN `IsEmailVerified` tinyint(4) DEFAULT 1;


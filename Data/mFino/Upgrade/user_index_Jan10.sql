use mfino;
ALTER TABLE `user` ADD INDEX `Index_Create_Time` USING BTREE(`CreateTime`),
 ADD INDEX `Index_Last_Update_Time` USING BTREE(`LastUpdateTime`),
 ADD INDEX `Index_Activation_Time` USING BTREE(`UserActivationTime`),
 ADD INDEX `Index_Confirmation_Time` USING BTREE(`ConfirmationTime`),
 ADD INDEX `Index_Role` USING BTREE(`Role`);

USE mfino;

INSERT IGNORE INTO `user` (`Username`,`Password`) VALUES 
('mfino','810ab25e9074aa4359fba932d54d6fb62de81ae4');

INSERT IGNORE INTO `user_authority` (`UserID`,`Authority`) VALUES 
(LAST_INSERT_ID(),'ROLE_ADMIN');


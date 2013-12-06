

create table integration_partner_map (
    ID bigint(20) NOT NULL AUTO_INCREMENT,
    Version int(11) NOT NULL,
    LastUpdateTime datetime NOT NULL,
    UpdatedBy varchar(255) NOT NULL DEFAULT ' ',
    CreateTime datetime NOT NULL,
    CreatedBy varchar(255) NOT NULL,
    InstitutionID varchar(255) NOT NULL,
    PartnerID bigint(20) NOT NULL,       
    PRIMARY KEY(ID),    
    constraint FK_INTEGRATION_PARTNER_MAP_PartnerID foreign key (PartnerID)references partner (ID)    
    );
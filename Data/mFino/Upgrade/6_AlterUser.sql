use mfino;

ALTER TABLE user
ADD ( FirstName varchar(255) null,
      LastName  varchar(255) null,
      Email varchar(255) null,
      Language int(11) not null,
      Timezone varchar(255) null,
      Restrictions int(11) not null,
      Status int(11) not null,
      StatusTime datetime not null,
      CreateTime datetime not null,
      CreatedBy varchar(255) not null,
      LastUpdateTime datetime not null,
      UpdatedBy varchar(255) not null,      
      FailedLoginCount int(11) not null,
      FirstTimeLogin tinyint(4) null,
      LastLoginTime datetime null
)

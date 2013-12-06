

ALTER TABLE groups CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE pocket_template_config CHARACTER SET utf8 COLLATE utf8_general_ci;
ALTER TABLE subscriber_groups CHARACTER SET utf8 COLLATE utf8_general_ci;

ALTER TABLE ptc_group_mapping MODIFY COLUMN groupID int(10) UNSIGNED;
ALTER TABLE ptc_group_mapping MODIFY COLUMN ptcID BIGINT(20) UNSIGNED;

alter table ptc_group_mapping add constraint fk_ptcmap_groupid foreign key(groupID) references groups(ID);
alter table ptc_group_mapping add constraint fk_ptcmap_ptcid foreign key(ptcID) references pocket_template_config(ID);

update pocket_template_config set isdefault = 1; 
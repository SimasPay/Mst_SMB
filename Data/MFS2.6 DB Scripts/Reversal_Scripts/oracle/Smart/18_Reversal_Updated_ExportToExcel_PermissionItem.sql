DELETE FROM permission_item where Permission in (10242,12115,10422,10243);

DELETE FROM role_permission where Permission in (10242,12115,10422,10243);

DELETE FROM role_permission where Permission='10801' and Role='9';

commit;
Delete from role_permission where permission in (21901);
Delete from role_permission where permission in (21902);
Delete from role_permission where permission in (21903);

Delete from permission_item where permission in (21901);
Delete from permission_item where permission in (21902);
Delete from permission_item where permission in (21903);

commit;
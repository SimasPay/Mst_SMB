Delete from system_parameters where ParameterName = 'max.amt.for.agent.funding';

DELETE FROM enum_text where TAGID=8026;

Delete from role_permission where permission='21001';
Delete from permission_item where permission = 21001;

Delete from role_permission where permission='21002';
Delete from permission_item where permission = 21002;

commit;
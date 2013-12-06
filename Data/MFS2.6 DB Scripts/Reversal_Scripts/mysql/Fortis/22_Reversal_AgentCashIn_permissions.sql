use mfino;

Delete from system_parameters where ParameterName = 'max.amt.for.agent.funding';

DELETE FROM enum_text where TAGID=8026;

Delete from permission_item where Permission=21001;
Delete from role_permission where Permission='21001';

Delete from permission_item where Permission=21002;
Delete from role_permission where Permission='21002';
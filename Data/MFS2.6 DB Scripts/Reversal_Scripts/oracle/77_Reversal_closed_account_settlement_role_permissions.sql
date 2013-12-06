use mfino;

Delete FROM role_permission where Permission = '10240' and Role = '1';
Delete FROM role_permission where Permission = '10241' and Role = '25';
Delete FROM permission_item where Permission = '10240';
Delete FROM permission_item where Permission = '10241';


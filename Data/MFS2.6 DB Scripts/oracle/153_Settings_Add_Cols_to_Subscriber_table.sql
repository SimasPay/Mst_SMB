
ALTER TABLE subscriber ADD Nickname varchar2(255);
ALTER TABLE subscriber ADD IsEmailVerified number(3,0) DEFAULT 1;

commit;
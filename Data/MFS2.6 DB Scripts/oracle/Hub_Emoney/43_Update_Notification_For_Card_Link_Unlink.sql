UPDATE notification SET Text='Link NFC card $(CardPan) with Mobile number $(SenderMDN) and CardAlias $(CardAlias)was successful.' WHERE code=836;
UPDATE notification SET Text='Link NFC card $(CardPan) with Mobile number $(SenderMDN) and CardAlias $(CardAlias) was failed.' WHERE code=837;
UPDATE notification SET Text='Unlink NFC card $(CardPan) with Mobile number $(SenderMDN) and CardAlias $(CardAlias) is successful.' WHERE code=838;
UPDATE notification SET Text='Unlink NFC card $(CardPan) with Mobile number $(SenderMDN) and CardAlias $(CardAlias) is failed.' WHERE code=839;

commit;
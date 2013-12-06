
DELETE FROM favorite_category;

-- Delete notifications related to Favorites
Delete from notification where code in (2087, 2088, 2089, 2090, 2091, 2092, 2093);

-- Delete txn type realted to Favorites
DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='AddFavorite');
DELETE FROM transaction_type where TRANSACTIONNAME = 'AddFavorite';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='EditFavorite');
DELETE FROM transaction_type where TRANSACTIONNAME = 'EditFavorite';

DELETE FROM service_transaction where serviceid=(select id from service where servicename='Account') and transactiontypeid=(select id from transaction_type where transactionname='DeleteFavorite');
DELETE FROM transaction_type where TRANSACTIONNAME = 'DeleteFavorite';


-- Delete system param : "max.favorites.per.category"
Delete from system_parameters where ParameterName = 'max.favorites.per.category';

commit;
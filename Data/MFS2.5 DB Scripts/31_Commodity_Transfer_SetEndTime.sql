use mfino;

UPDATE commodity_transfer SET EndTime = LastUpdateTime WHERE EndTime IS NULL;


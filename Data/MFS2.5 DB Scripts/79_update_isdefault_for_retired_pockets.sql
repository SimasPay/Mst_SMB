use mfino;

update pocket set isdefault = 0 where status in (2, 3);

update enum_text set DisplayText='Retired' where TagID='6062' and EnumCode='2';
update enum_text set DisplayText='To Grave' where TagID='6062' and EnumCode='3';
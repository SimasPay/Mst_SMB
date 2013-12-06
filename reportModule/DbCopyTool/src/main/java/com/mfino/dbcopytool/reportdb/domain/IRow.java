package com.mfino.dbcopytool.reportdb.domain;

public interface IRow {
	public void initialiseRow(Object[] m);

	public void transfromRow();

	public void insertRow();

	public void printRow();

}

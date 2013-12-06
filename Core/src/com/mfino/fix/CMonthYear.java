package com.mfino.fix;

/**
 * Summary description for CMonthYear.
 */
public class CMonthYear {

	public CMonthYear() {
		m_Year = 0;
		m_Month = 0;
	}

	public CMonthYear(CMonthYear NewValue) {
		m_Year = NewValue.m_Year;
		m_Month = NewValue.m_Month;
	}
	int m_Year;
	int m_Month;
}

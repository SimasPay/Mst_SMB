package com.mfino.dao.query;

public class MFAAuthenticationQuery extends BaseQuery{
	private Long sctlId;
	private Integer mfaMode;
	private String mfaValue;
	public Long getSctlId() {
		return sctlId;
	}
	public void setSctlId(Long sctlId) {
		this.sctlId = sctlId;
	}
	public Integer getMfaMode() {
		return mfaMode;
	}
	public void setMfaMode(Integer mfaMode) {
		this.mfaMode = mfaMode;
	}
	public String getMfaValue() {
		return mfaValue;
	}
	public void setMfaValue(String mfaValue) {
		this.mfaValue = mfaValue;
	}
}

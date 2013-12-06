package com.mfino.dao.query;

/**
 * 
 * @author Amar
 *
 */
public class MdnOtpQuery extends BaseQuery{

	private Long mdn;
    private Integer otpStatus;
    
	public Long getMdn() {
		return mdn;
	}
	
	public void setMdn(Long mdn) {
		this.mdn = mdn;
	}
	
	public Integer getOtpStatus() {
		return otpStatus;
	}
	
	public void setOtpStatus(Integer otpStatus) {
		this.otpStatus = otpStatus;
	}    
    
}

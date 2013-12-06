package com.mfino.dao.query;


public class NotificationLogQuery extends BaseQuery{

	private Long sctlID;
    private Integer code;
    private Integer notificationMethod;
    private String sourceAddress;
    private Integer NotificationReceiverType;
    private Boolean isSensitiveData;
    
    public Long getSctlID() {
        return sctlID;
    }

    public void setSctlID(Long sctlID) {
        this.sctlID = sctlID;
    }
    
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

	public Integer getNotificationMethod() {
		return notificationMethod;
	}

	public void setNotificationMethod(Integer notificationMethod) {
		this.notificationMethod = notificationMethod;
	}

	public String getSourceAddress() {
		return sourceAddress;
	}

	public void setSourceAddress(String sourceAddress) {
		this.sourceAddress = sourceAddress;
	}

	public Integer getNotificationReceiverType() {
		return NotificationReceiverType;
	}

	public void setNotificationReceiverType(Integer notificationReceiverType) {
		NotificationReceiverType = notificationReceiverType;
	}

	public Boolean isSensitiveData() {
		return isSensitiveData;
	}

	public void setSensitiveData(Boolean isSensitiveData) {
		this.isSensitiveData = isSensitiveData;
	}
    
}

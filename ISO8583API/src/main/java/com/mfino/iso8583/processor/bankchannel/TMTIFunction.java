package com.mfino.iso8583.processor.bankchannel;

public enum TMTIFunction {
	MTIRequest(0), 
	MTIRequestResponse(1),
	MTIAdvice(2), 
	MTIAdviceResponse(3), 
	MTINotification(4), 
	MTIResponseAcknowledgment(8),
	MTINegativeAcknowledgment(9);
	private int	value;
	private TMTIFunction(int value) {
		this.value = value;
	}
	public int getValue() {
		return this.value;
	}
}

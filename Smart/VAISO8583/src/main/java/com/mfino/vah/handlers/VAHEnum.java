package com.mfino.vah.handlers;

import java.util.concurrent.atomic.AtomicBoolean;

public enum VAHEnum {
	Signon;
	private AtomicBoolean	signonstatus;
	
	private VAHEnum(){
		this.signonstatus = new AtomicBoolean();
	}

	boolean getStatus() {
		return signonstatus.get();
	}

	void setStatus(boolean status) {
		this.signonstatus.set(status);
	}

}

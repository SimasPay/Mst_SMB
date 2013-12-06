package com.mfino.fep.handlers;

import java.util.concurrent.atomic.AtomicBoolean;

public enum FEPEnum {
	Signon;
	private AtomicBoolean	signonstatus;
	
	private FEPEnum(){
		this.signonstatus = new AtomicBoolean();
	}

	boolean getStatus() {
		return signonstatus.get();
	}

	void setStatus(boolean status) {
		this.signonstatus.set(status);
	}

}

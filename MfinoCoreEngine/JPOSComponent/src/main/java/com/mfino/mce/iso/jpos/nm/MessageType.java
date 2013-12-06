package com.mfino.mce.iso.jpos.nm;

public enum MessageType {
	
	Signon(1),Echo(2),KeyExchange(3),Signoff(4);
	
	int id;
	public int getId() {
		return id;
	}
	private MessageType(int id){
		this.id = id;
	}

}

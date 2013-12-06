package com.mfino.mce.component.iso;

import com.mfino.mce.component.iso.common.Envelope;;

public interface ClientHandlerListener {

	void messageReceived(Envelope message);
}

package com.mfino.tools.adjustments;

import org.apache.camel.Exchange;

import com.mfino.mce.core.MCEMessage;

public interface SctlStatusBean {

	public MCEMessage updateToPending(Exchange ex);

	public MCEMessage updateToSuccessful(Exchange ex);

	public MCEMessage updateToFailed(Exchange ex);
}

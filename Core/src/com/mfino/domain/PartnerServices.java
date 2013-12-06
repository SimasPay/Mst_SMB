package com.mfino.domain;

import com.mfino.fix.CmFinoFIX;

public class PartnerServices extends CmFinoFIX.CRPartnerServices{

	@Override
	public String toString() {
		Long partnerId = getPartner()!=null ? getPartner().getID() : null;
		Long serviceId = getService()!=null ? getService().getID() : null;
		String dctName = getDistributionChainTemplate()!=null ? getDistributionChainTemplate().getName() : null;
		return "PartnerService-{partnerid="+partnerId + ":serviceid="+serviceId+":DCT="+dctName+"}";
	}
}

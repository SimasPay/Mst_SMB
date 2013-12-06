package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ActorChannelMappingDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMappingValidator;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.ActorChannelMappingValidatorProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
@Service("ActorChannelMappingValidatorProcessorImpl")
public class ActorChannelMappingValidatorProcessorImpl extends BaseFixProcessor implements ActorChannelMappingValidatorProcessor{

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {
		CMJSActorChannelMappingValidator realMsg = (CMJSActorChannelMappingValidator) msg;
		ActorChannelMappingDAO acmDao = DAOFactory.getInstance().getActorChannelMappingDao();
		ActorChannelMappingQuery query = new ActorChannelMappingQuery();
		CMJSError error = new CMJSError();
		if (realMsg.getSubscriberType() != null) {
			query.setSubscriberType(realMsg.getSubscriberType());
		}
		if (realMsg.getBusinessPartnerType() != null) {
			query.setPartnerType(realMsg.getBusinessPartnerType());
		}
		if(realMsg.getServiceID() != null) {
			query.setServiceID(realMsg.getServiceID());
		}
		if (realMsg.getTransactionTypeID() != null) {
			query.setTransactionTypeID(realMsg.getTransactionTypeID());
		}
		if(realMsg.getChannelCodeID() != null) {
			query.setChannelCodeID(realMsg.getChannelCodeID());
		}
		if(realMsg.getKYCLevel() != null) {
			query.setKycLevel(realMsg.getKYCLevel());
		}
		if(realMsg.getGroupID() != null) {
			query.setGroup(Long.valueOf(realMsg.getGroupID()));
		}
		List<ActorChannelMapping> list = acmDao.get(query);
		if(list.size() != 0 && !list.get(0).getID().equals(realMsg.getID())) {
			error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
			error.setErrorDescription(MessageText._("Actor channel mapping with the same criteria already exists in DB, please enter different criteria."));
		} else {
			error.setErrorCode(CmFinoFIX.ErrorCode_NoError);
			error.setErrorDescription(MessageText._(""));
    	}
		return error;
	}
}

package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ActorChannelMappingDAO;
import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.KYCLevelDAO;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.ActorChannelMappingQuery;
import com.mfino.domain.ActorChannelMapping;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSActorChannelMapping;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.ActorChannelMappingProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 * @author Srikanth
 *
 */
@org.springframework.stereotype.Service("ActorChannelMappingProcessorImpl")
public class ActorChannelMappingProcessorImpl extends BaseFixProcessor implements ActorChannelMappingProcessor{
	
	@Autowired
	@Qualifier("EnumTextServiceImpl")
	private EnumTextService enumTextService;
	
	private void updateEntity(ActorChannelMapping acm, CMJSActorChannelMapping.CGEntries e) {
		ServiceDAO sDAO = DAOFactory.getInstance().getServiceDAO();
		TransactionTypeDAO ttDAO = DAOFactory.getInstance().getTransactionTypeDAO();
		ChannelCodeDAO ccDAO = DAOFactory.getInstance().getChannelCodeDao();
		KYCLevelDAO kycDAO = DAOFactory.getInstance().getKycLevelDAO();
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		
		if(e.getSubscriberType() != null) {
			acm.setSubscribertype(e.getSubscriberType());
		}
		if(e.getBusinessPartnerType() != null) {
			acm.setBusinesspartnertype(e.getBusinessPartnerType().longValue());
		} else if(e.isRemoteModifiedBusinessPartnerType()) {
			acm.setBusinesspartnertype(null);
		}
		if (e.getServiceID() != null) {
			acm.setService(sDAO.getById(e.getServiceID()));
		}
		if (e.getTransactionTypeID() != null) {
			acm.setTransactionType(ttDAO.getById(e.getTransactionTypeID()));
		}
		if (e.getChannelCodeID() != null) {
			acm.setChannelCode(ccDAO.getById(e.getChannelCodeID()));
		}
		if (e.getKYCLevel() != null) {
			acm.setKycLevel(kycDAO.getById(e.getKYCLevel()));
		} else if(e.isRemoteModifiedKYCLevel()) {
			acm.setKycLevel(null);
		}
		if(e.getGroupID() != null) {
			acm.setGroups(groupDao.getById(Long.valueOf(e.getGroupID())));
		}
		if(e.getIsAllowed() != null) {
			acm.setIsallowed((short) (e.getIsAllowed()?1:0));
		}
	}
	
	private void updateMessage(ActorChannelMapping acm, CMJSActorChannelMapping.CGEntries e) {
		e.setID(acm.getId().longValue());
		if((Long)acm.getSubscribertype() != null) {
			e.setSubscriberType(((Long)acm.getSubscribertype()).intValue());
			e.setSubscriberTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_SubscriberType, CmFinoFIX.Language_English, acm.getSubscribertype()));
		}
		if(acm.getBusinesspartnertype() != null) {
			e.setBusinessPartnerType(acm.getBusinesspartnertype().intValue());
			e.setBusinessPartnerTypeText(enumTextService.getEnumTextValue(CmFinoFIX.TagID_BusinessPartnerType, CmFinoFIX.Language_English, acm.getBusinesspartnertype()));
		}
		if(((Short)acm.getIsallowed()) != null) {
			e.setIsAllowed(acm.getIsallowed() != 0);
		}
		if (acm.getService() != null) {
			e.setServiceID(acm.getService().getId().longValue());
			e.setServiceName(acm.getService().getDisplayname());
		}
		if (acm.getTransactionType() != null) {
			e.setTransactionTypeID(acm.getTransactionType().getId().longValue());
			e.setTransactionName(acm.getTransactionType().getDisplayname());
		}
		if (acm.getChannelCode() != null) {
			e.setChannelCodeID(acm.getChannelCode().getId().longValue());
			e.setChannelName(acm.getChannelCode().getChannelname());
		}
		if (acm.getKycLevel() != null) {
			e.setKYCLevel(acm.getKycLevel().getId().longValue());
			e.setKYCLevelText(acm.getKycLevel().getKyclevelname());
		}
		if (acm.getGroups() != null) {
			e.setGroupID(acm.getGroups().getId().toString());
			e.setGroupName(acm.getGroups().getGroupname());
		}
		e.setRecordVersion(((Long)acm.getVersion()).intValue());
		e.setCreatedBy(acm.getCreatedby());
		e.setCreateTime(acm.getCreatetime());
		e.setUpdatedBy(acm.getUpdatedby());
		e.setLastUpdateTime(acm.getLastupdatetime());
	}

	@Override
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception {
		CMJSActorChannelMapping realMsg = (CMJSActorChannelMapping) msg;
		ActorChannelMappingDAO dao = DAOFactory.getInstance().getActorChannelMappingDao();
		
		if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
			ActorChannelMappingQuery query = new ActorChannelMappingQuery();
			int i=0;
			
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
			if (realMsg.getstart() != null) {
				query.setStart(realMsg.getstart());
			}
			if (realMsg.getlimit() != null) {
				query.setLimit(realMsg.getlimit());
			}
			
			List<ActorChannelMapping> lst = dao.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				realMsg.allocateEntries(lst.size());
				for (ActorChannelMapping tr: lst){
					CMJSActorChannelMapping.CGEntries e = new CMJSActorChannelMapping.CGEntries();
					updateMessage(tr, e);
					realMsg.getEntries()[i] = e;
        			i++;
        		}
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(query.getTotal());
		} else if (CmFinoFIX.JSaction_Insert.equals(realMsg.getaction())) {
			CMJSActorChannelMapping.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSActorChannelMapping.CGEntries e: entries) {
				if(e!= null) {
					ActorChannelMapping tr = new ActorChannelMapping();
					updateEntity(tr, e);
	        		try {
						dao.save(tr);
					} catch (ConstraintViolationException ce) {
						return generateError(ce);
					}
					updateMessage(tr, e);
				}				
			}
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
			CMJSActorChannelMapping.CGEntries[] entries = realMsg.getEntries();			
			for (CMJSActorChannelMapping.CGEntries e: entries) {				
				ActorChannelMapping tr = dao.getById(e.getID());
        		if (!(e.getRecordVersion().equals(tr.getVersion()))) {
        			handleStaleDataException();
        		}
        		updateEntity(tr, e);        		
        		try {
					dao.save(tr);
				} catch (ConstraintViolationException ce) {
					return generateError(ce);
				}
        		updateMessage(tr, e);
        	}        	
        	realMsg.setsuccess(CmFinoFIX.Boolean_True);
        	realMsg.settotal(entries.length);			
		} else if (CmFinoFIX.JSaction_Delete.equals(realMsg.getaction())) {
			
		}
		return realMsg;
	}
	
	/**
	 * @return
	 */
	private CFIXMsg generateError(ConstraintViolationException cvError) {
		CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
		String message = MessageText._("Actor channel mapping with the same criteria already exists in DB, please enter different criteria.");
		errorMsg.setErrorDescription(message);
		errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);		
		log.warn(message, cvError);
		return errorMsg;
	}	
}

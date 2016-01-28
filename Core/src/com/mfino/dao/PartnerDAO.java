package com.mfino.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.query.PartnerQuery;
import com.mfino.dao.query.PocketQuery;
import com.mfino.domain.EnumText;
import com.mfino.domain.Partner;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.TransactionRule;
import com.mfino.fix.CmFinoFIX;

public class PartnerDAO extends BaseDAO<Partner> {

	private static Logger log = LoggerFactory.getLogger(PartnerDAO.class);

	public List<Partner> get(PartnerQuery query) {
		log.info("PartnerDao::get() method BEGIN");

		Criteria criteria = createCriteria();

		if (query.getTransactionRuleId() != null) {
			TransactionRuleDAO trDAO = DAOFactory.getInstance().getTransactionRuleDAO();
			TransactionRule tr = trDAO.getById(query.getTransactionRuleId());
			criteria.createAlias(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID,"ps");
			criteria.add(Restrictions.eq("ps." + CmFinoFIX.CRPartnerServices.FieldName_PartnerByServiceProviderID, tr.getPartnerByServiceProviderID()));
			criteria.add(Restrictions.eq("ps." + CmFinoFIX.CRPartnerServices.FieldName_Service,	tr.getService()));
		}
		if (StringUtils.isNotBlank(query.getPartnerTypeSearchString())) {
			String[] strArray = query.getPartnerTypeSearchString().split(",");
			Integer[] intArray = new Integer[strArray.length];
			int i = 0;
			for (String s : strArray) {
				intArray[i] = new Integer(s);
				i++;
			}
			criteria.add(Restrictions.in(CmFinoFIX.CRPartner.FieldName_BusinessPartnerType,	intArray));
		}
		if (StringUtils.isNotBlank(query.getTradeName())) {
			if (Boolean.TRUE == query.isPartnerCodeLike()) {
				addLikeStartRestriction(criteria, CmFinoFIX.CRPartner.FieldName_TradeName, query.getTradeName());
			} else {
				criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_TradeName, query.getTradeName()).ignoreCase());
			}
		}
		if (StringUtils.isNotBlank(query.getAuthorizedEmail())) {
			criteria.add(Restrictions.eq(
					CmFinoFIX.CRPartner.FieldName_AuthorizedEmail,
					query.getAuthorizedEmail()).ignoreCase());
		}
		if (StringUtils.isNotBlank(query.getCardPAN())) {
			PocketDAO pocketDAO = DAOFactory.getInstance().getPocketDAO();
			PocketQuery pocketQuery = new PocketQuery();
			pocketQuery.setCardPan(query.getCardPAN());
			List<Pocket> pockets = pocketDAO.get(pocketQuery);
			if (CollectionUtils.isNotEmpty(pockets)) {
				Pocket pocket = pockets.get(0);
				SubscriberMDN subscriberMDN = pocket.getSubscriberMDNByMDNID();
				criteria.add(Restrictions.eq(
						CmFinoFIX.CRPartner.FieldName_Subscriber,
						subscriberMDN.getSubscriber()));
			} else {
				criteria.add(Restrictions.eq(
						CmFinoFIX.CRPartner.FieldName_Subscriber, null));
			}
		}
		if (StringUtils.isNotBlank(query.getPartnerCode())) {
			if (Boolean.TRUE == query.isPartnerCodeLike()) {
				addLikeStartRestriction(criteria, CmFinoFIX.CRPartner.FieldName_PartnerCode, query.getPartnerCode());
            } else {
            	criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_PartnerCode,query.getPartnerCode()).ignoreCase());
            }			
		}
		if (null != query.getPartnerType()) {
			if (null != query.getPartnerType()) {
				List<Integer> partnerTypes = new ArrayList<Integer>();
				if (query.getPartnerType().equals(
						CmFinoFIX.TagID_BusinessPartnerTypeAgent)
						|| query.getPartnerType().equals(
								CmFinoFIX.TagID_BusinessPartnerTypePartner)) {
					DetachedCriteria partner = DetachedCriteria.forClass(EnumText.class).
												setProjection(Property.forName(CmFinoFIX.CREnumText.FieldName_EnumCode))
												.add(Restrictions.eq(CmFinoFIX.CREnumText.FieldName_TagID, query.getPartnerType()));
//					HashMap<String, String> results = enumTextService
//							.getEnumTextSet(query.getPartnerType(), null);
//					if (!results.isEmpty()) {
//						Iterator<String> iterator = results.keySet().iterator();
//						while (iterator.hasNext()) {
//							partnerTypes.add(Integer.valueOf(iterator.next()));
//						}
//					}
//					criteria.add(Restrictions.in(
//							CmFinoFIX.CRPartner.FieldName_BusinessPartnerType,
//							partnerTypes));
					criteria.add(Property.forName(CmFinoFIX.CRPartner.FieldName_BusinessPartnerType).in(partner));
				} else {
					criteria.add(Restrictions.eq(
							CmFinoFIX.CRPartner.FieldName_BusinessPartnerType,
							query.getPartnerType()));
				}
			}
		}
		if (null != query.getNotPartnerType()) {
			criteria.add(Restrictions.ne(
					CmFinoFIX.CRPartner.FieldName_BusinessPartnerType,
					query.getNotPartnerType()));
		}
		if (query.getUpgradeStateSearch() != null) {
			criteria.createAlias(CmFinoFIX.CRPartner.FieldName_Subscriber,
					"sub");
			criteria.add(Restrictions.eq("sub."
					+ CmFinoFIX.CRSubscriber.FieldName_UpgradeState,
					query.getUpgradeStateSearch()));
		}

		// if (null != query.getServiceProviderId() && null !=
		// query.getServiceId()) {
		// ServiceProviderServiceDAO spsDAO = new ServiceProviderServiceDAO();
		// List<ServiceProviderServices> lst =
		// spsDAO.getServiceProviderServices(query.getServiceProviderId(),
		// query.getServiceId());
		// if (CollectionUtils.isNotEmpty(lst)) {
		// criteria.createCriteria(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID).
		// add(Restrictions.eq(CmFinoFIX.CRPartnerServices.FieldName_ServiceProviderServices,
		// lst.get(0))).
		// add(Restrictions.eq(CmFinoFIX.CRPartnerServices.FieldName_PartnerServiceStatus,
		// CmFinoFIX.PartnerServiceStatus_Active));
		// }
		// }
		if (query.getServiceId() != null) {
			ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
			criteria.createCriteria(
					CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID)
					.add(Restrictions.eq(
							CmFinoFIX.CRPartnerServices.FieldName_Service,
							serviceDAO.getById(query.getServiceId())));
		}

        if((null != query.getDistributionChainTemplateId()) && (null != query.getParentId())){
        	criteria.createAlias(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID, "partnerService");
        	criteria.createAlias("partnerService."+CmFinoFIX.CRPartnerServices.FieldName_DistributionChainTemplate, "psDct");
        	criteria.add(Restrictions.eq("psDct."+CmFinoFIX.CRDistributionChainTemplate.FieldName_RecordID, query.getDistributionChainTemplateId()));
        	criteria.createAlias("partnerService."+CmFinoFIX.CRPartnerServices.FieldName_PartnerByParentID, "psParent");
        	criteria.add(Restrictions.eq("psParent."+CmFinoFIX.CRPartner.FieldName_RecordID, query.getParentId()));
        }
        else if(null != query.getDistributionChainTemplateId()){
        	criteria.createAlias(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID, "partnerService");
//        	criteria.createAlias("partnerService."+CmFinoFIX.CRPartnerServices.FieldName_PartnerByParentID, "parent");
        	criteria.createAlias("partnerService."+CmFinoFIX.CRPartnerServices.FieldName_DistributionChainTemplate, "psDct");
        	criteria.add(Restrictions.eq("psDct."+CmFinoFIX.CRDistributionChainTemplate.FieldName_RecordID, query.getDistributionChainTemplateId()));
        	
        	if(query.isFirstLevelPartnerSearch()){
        		criteria.add(Restrictions.isNull("partnerService."+CmFinoFIX.CRPartnerServices.FieldName_PartnerByParentID));
        	}
        	
//        	Criteria partnerServiceCriteria = criteria.createCriteria(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID);
//        	Criteria distributionChainTemplateCriteria = partnerServiceCriteria.createCriteria(CmFinoFIX.CRPartnerServices.FieldName_DistributionChainTemplate);
//        	distributionChainTemplateCriteria.add(Restrictions.eq(CmFinoFIX.CRDistributionChainTemplate.FieldName_RecordID, query.getDistributionChainTemplateId()));
        }
        else if(null != query.getParentId()){
        	criteria.createAlias(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID, "partnerService1");
        	criteria.createAlias("partnerService1."+CmFinoFIX.CRPartnerServices.FieldName_PartnerByParentID, "psParent");
        	criteria.add(Restrictions.eq("psParent."+CmFinoFIX.CRPartner.FieldName_RecordID, query.getParentId()));
        	
//        	Criteria partnerServiceCriteria = criteria.createCriteria(CmFinoFIX.CRPartner.FieldName_PartnerServicesFromPartnerID);
//        	Criteria partnerServiceParentCriteria = partnerServiceCriteria.createCriteria(CmFinoFIX.CRPartnerServices.FieldName_PartnerByParentID);
//        	partnerServiceParentCriteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_RecordID, query.getParentId()));
        }
        
        if(null != query.getPartnerId()){
        	criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_RecordID, query.getPartnerId()));
        }
		
		processBaseQuery(query, criteria);

		// Paging
		processPaging(query, criteria);

		// applying Order
		criteria.addOrder(Order.desc(CmFinoFIX.CRPartner.FieldName_RecordID));
		applyOrder(query, criteria);
		@SuppressWarnings("unchecked")
		List<Partner> results = criteria.list();

		return results;
	}

	@SuppressWarnings("unchecked")
	public Partner getPartnerByTradeName(String tradeName) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_TradeName,
				tradeName).ignoreCase());
		List<Partner> results = criteria.list();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	public Partner getPartnerByPartnerCode(String code) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_PartnerCode,
				code).ignoreCase());
		List<Partner> results = criteria.list();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Partner getServiceProvider() {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(
				CmFinoFIX.CRPartner.FieldName_BusinessPartnerType,
				CmFinoFIX.BusinessPartnerType_ServicePartner));
		List<Partner> results = criteria.list();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

	@SuppressWarnings("unchecked")
	public Partner getPartnerBySubscriber(Subscriber sub) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_Subscriber,
				sub));
		List<Partner> results = criteria.list();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	public Partner getBranchSequence(PartnerQuery query) {
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_BusinessPartnerType, query.getBusinessPartnerType()));
		criteria.add(Restrictions.eq(CmFinoFIX.CRPartner.FieldName_BranchCode, query.getBranchCode()));
		criteria.addOrder(Order.desc("ID"));
		criteria.setMaxResults(1);
		List<Partner> results = criteria.list();
		if (results.size() > 0) {
			return results.get(0);
		} else {
			return null;
		}
	}

}

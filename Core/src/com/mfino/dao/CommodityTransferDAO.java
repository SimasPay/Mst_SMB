/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.type.StandardBasicTypes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.constants.DAOConstants;
import com.mfino.dao.query.CommodityTransferQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.Company;
import com.mfino.domain.PendingCommodityTransfer;
import com.mfino.domain.Pocket;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.TransactionLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.ConfigurationUtil;

/**
 *
 * @author sandeepjs
 */
public class CommodityTransferDAO extends BaseDAO<CommodityTransfer> {

    private static final String SubscriberMDNBySourceMDNID = "subscriberMdn";
    private static final String PocketBySourcePocketID = "pocket";
    private CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
    
    private Logger log = LoggerFactory.getLogger(getClass());
    
    public void applyQuery(CommodityTransferQuery query, Criteria criteria) throws Exception
    {
    	applyQuery(query,criteria,true);
    }
    
    public void applyQuery(CommodityTransferQuery query, Criteria criteria, boolean useSrcDestPocketCriteria ) throws Exception {

        // Adding Restrictions
        if (query.getTransferStatus() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferStatus, query.getTransferStatus()));
        }
        if (query.getTransferFailureReason() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferFailureReason, query.getTransferFailureReason()));
        }
        if (query.getStartDate() != null) {
            criteria.add(Restrictions.gt(CommodityTransfer.FieldName_StartTime, query.getStartDate()));
        }
        if (query.getEndDate() != null) {
            criteria.add(Restrictions.lt(CommodityTransfer.FieldName_EndTime, query.getEndDate()));
        }

        if (query.getStartTimeGE() != null) {
            criteria.add(Restrictions.ge(CommodityTransfer.FieldName_StartTime, query.getStartTimeGE()));
        }
        if (query.getStartTimeLT() != null) {
            criteria.add(Restrictions.lt(CommodityTransfer.FieldName_StartTime, query.getStartTimeLT()));
        }

        if (query.getEndTimeLT() != null) {
            criteria.add(Restrictions.lt(CommodityTransfer.FieldName_EndTime, query.getEndTimeLT()));
        }
        if (query.getCreateTimeSearchLE() != null) {
            criteria.add(Restrictions.le(CommodityTransfer.FieldName_CreateTime, query.getCreateTimeSearchLE()));
            criteria.add(Restrictions.gt(CommodityTransfer.FieldName_CreateTime, query.getCreateTimeSearchGT()));
        }
        if (query.getTransactionAmount() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferAmount, query.getTransactionAmount()));
        }
        if (query.getTransactionID() != null) {
//            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransactionID, query.getTransactionID()));
        	criteria.createAlias(CommodityTransfer.FieldName_TransactionsLogByTransactionID, "tl");
        	criteria.add(Restrictions.eq("tl."+TransactionLog.FieldName_RecordID, query.getTransactionID()));
        }
        if (query.getSubscriberMDN() != null) {
            criteria.add(Restrictions.disjunction().add(
                    Restrictions.eq(SubscriberMDNBySourceMDNID , query.getSubscriberMDN())).add(
                    Restrictions.eq(CommodityTransfer.FieldName_DestMDNID, query.getSubscriberMDN().getId())));
        }

        // adding restrictions for checking if the record is a bank channel record.
        if (query.isIsBankChannel() != null && query.isIsBankChannel()) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_BankChannel));
        }

        // query by UICategory
        if (query.getUiCategory() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransactionUICategory, query.getUiCategory()));
        }
        

        if (query.isDompetTxn()) {
            List<Integer> uiCategories = new ArrayList<Integer>();
            uiCategories.add(CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer);
            uiCategories.add(CmFinoFIX.TransactionUICategory_Dompet_Self_Topup);
            uiCategories.add(CmFinoFIX.TransactionUICategory_Dompet_Topup_Another);
            criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, uiCategories));
        }

        if (query.hasExternalCall()) {
            criteria.add(Restrictions.disjunction().add(Restrictions.isNotNull(CommodityTransfer.FieldName_BankCodeForRouting)).add(Restrictions.isNotNull(CommodityTransfer.FieldName_BankResponseCode)).add(Restrictions.isNotNull(CommodityTransfer.FieldName_OperatorResponseCode)));
        }

        if (query.getMsgType() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransactionUICategory, query.getMsgType()));
        }

//        criteria.createAlias(SubscriberMDNBySourceMDNID, SubscriberMDNBySourceMDNID + DAOConstants.ALIAS_SUFFIX);

        if (query.getSourceDestnMDN() != null) {
            criteria.add(Restrictions.disjunction().add(
                    Restrictions.eq(CommodityTransfer.FieldName_SourceMDN, query.getSourceDestnMDN())).add(
                    Restrictions.eq(CommodityTransfer.FieldName_DestMDN, query.getSourceDestnMDN())));
        }
        if (query.getSourceDestMDNAndID() != null) {
            boolean isBadRequest = false;
            if (!(query.getSourceDestMDNAndID() instanceof Object[])) {
                isBadRequest = true;
            }
            Object[] mdnAndID = (Object[]) query.getSourceDestMDNAndID();
            if (mdnAndID.length != 2 || !(mdnAndID[0] instanceof String) || !(mdnAndID[1] instanceof Long)) {
                isBadRequest = true;
            }
            if (isBadRequest) {
                throw new Exception("Bad query parameter for CommodityTransferDAO - SourceDestMDNAndID");
            }
            SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
            SubscriberMdn subscMDN = subscriberMDNDAO.getById((Long)mdnAndID[1]);
            criteria.add(Restrictions.disjunction().add(Restrictions.eq(CommodityTransfer.FieldName_DestMDN, mdnAndID[0])).add(Restrictions.eq(SubscriberMDNBySourceMDNID , subscMDN)).add(Restrictions.eq(CommodityTransfer.FieldName_DestMDNID, mdnAndID[1])));
        }
        if (query.getSourceMDN() != null) {
            if (!query.getSourceMDN().equals("")) {
//                addLikeStartRestriction(criteria, CommodityTransfer.FieldName_SourceMDN, query.getSourceMDN());
                criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceMDN, query.getSourceMDN()));
            }
        }
        
        if (query.getSourceSubscMDN() != null) {
        	criteria.add(Restrictions.eq(SubscriberMDNBySourceMDNID , query.getSourceSubscMDN()));
        }
        
        if (query.getExactSourceMDN() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceMDN, query.getExactSourceMDN()));
        }

        if (null != query.getExactBankCode()) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_ISO8583_AcquiringInstIdCode, query.getExactBankCode()));
        }
        if (null != query.getBankRoutingCode()) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_BankCodeForRouting, query.getBankRoutingCode()));
        }

        if (query.getDestinationMDN() != null) {
            if (!query.getDestinationMDN().equals("")) {
                addLikeStartRestriction(criteria, CommodityTransfer.FieldName_DestMDN, query.getDestinationMDN());
            }
        }
        if (query.getSourceReferenceID() != null) {
            if (!query.getSourceReferenceID().equals("")) {
                criteria.add(Restrictions.disjunction().add(
                        Restrictions.ilike(CommodityTransfer.FieldName_SourceReferenceID, query.getSourceReferenceID())).add(
                        Restrictions.ilike(CommodityTransfer.FieldName_ISO8583_SystemTraceAuditNumber, query.getSourceReferenceID())));
            }
        }

        if (query.getDestinationRefID() != null) {
            if (!query.getDestinationRefID().equals("")) {
                criteria.add(Restrictions.disjunction().add(
                        Restrictions.ilike(CommodityTransfer.FieldName_OperatorAuthorizationCode, query.getDestinationRefID())).add(
                        Restrictions.ilike(CommodityTransfer.FieldName_BankAuthorizationCode, query.getDestinationRefID())).add(
                        Restrictions.ilike(CommodityTransfer.FieldName_BankReversalAuthorizationCode, query.getDestinationRefID())));
            }
        }
        if (query.getSourcePocket() != null) {
            //criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourcePocketID, query.getsourcePocketID()));
//            criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
            criteria.add(Restrictions.eq(PocketBySourcePocketID, query.getSourcePocket()));
        }
        if (query.getDestinationPocketID() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, query.getDestinationPocketID()));
        }
        // Mapping Source application(Access Method) to SourceApplication..
        if (query.getSourceApplicationSearch() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, query.getSourceApplicationSearch()));
        }
//        if (query.getSourceApplicationSearch() != null && 0 != query.getSourceApplicationSearch()) {
//            @SuppressWarnings("unchecked")
//            List<Object> property = new ArrayList();
//
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Web) == CmFinoFIX.SourceApplicationSearch_Web) {
//                //Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_Web);
//                property.add(CmFinoFIX.SourceApplication_Web);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_BackEnd) == CmFinoFIX.SourceApplicationSearch_BackEnd) {
//                //criteria.add(Restrictions.disjunction().add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_BackEnd)));
//                property.add(CmFinoFIX.SourceApplication_BackEnd);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Phone) == CmFinoFIX.SourceApplicationSearch_Phone) {
//                //criteria.add(Restrictions.disjunction().add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_Phone)));
//                property.add(CmFinoFIX.SourceApplication_Phone);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebService) == CmFinoFIX.SourceApplicationSearch_WebService) {
//                //criteria.add(Restrictions.disjunction().add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, CmFinoFIX.SourceApplication_WebService)));
//                property.add(CmFinoFIX.SourceApplication_WebService);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_SMS) == CmFinoFIX.SourceApplicationSearch_SMS) {
//
//                property.add(CmFinoFIX.SourceApplication_SMS);
//            }
//            if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebAPI) == CmFinoFIX.SourceApplicationSearch_WebAPI) {
//
//                property.add(CmFinoFIX.SourceApplication_WebAPI);
//            }
//            criteria.add(Restrictions.in(CommodityTransfer.FieldName_SourceApplication, property));
//        }
        if(useSrcDestPocketCriteria)
        {
	        if (query.getSourceDestnPocket() != null) {
	//            criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
	            criteria.add(Restrictions.disjunction().add(
	                    Restrictions.eq(PocketBySourcePocketID,query.getSourceDestnPocket())).add(
	                    Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, 
	                    		query.getSourceDestnPocket().getId())));
	        }
        }
        if (query.getBulkuploadID() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_BulkUploadID, query.getBulkuploadID()));
        }

        if (null != query.hasCSRAction()) {
            if (query.hasCSRAction()) {
                criteria.add(Restrictions.isNotNull(CommodityTransfer.FieldName_CSRAction));
            } else {
                criteria.add(Restrictions.isNull(CommodityTransfer.FieldName_CSRAction));
            }
        }
        if (query.getBulkUploadLineNumber() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_BulkUploadLineNumber, query.getBulkUploadLineNumber()));
        }

        if (query.getCommodity() != null) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_Commodity, query.getCommodity()));
        }

        // Add the Only E-Money Criteria
        if (query.isOnlyEmoneyTxns() && !query.isOnlyBankTxns()) {
            Set<Integer> collection = new HashSet<Integer>();
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashIn);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashOut);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Self_Topup);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Topup_Another);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Empty_SVA);

            criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, collection));
        }

        // Add the Only Bank Criteria
        if (query.isOnlyBankTxns() && !query.isOnlyEmoneyTxns()) {
            Set<Integer> collection = new HashSet<Integer>();
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Money_Transfer);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Self_Topup);
            collection.add(CmFinoFIX.TransactionUICategory_Dompet_Topup_Another);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_Purchase);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashIn);
            collection.add(CmFinoFIX.TransactionUICategory_EMoney_CashOut);

            criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, collection));
        }
        
        if (query.getMessageTypes() != null) {
            criteria.add(Restrictions.in(CommodityTransfer.FieldName_MsgType, query.getMessageTypes()));
        }
        
        // Mysql is picking the company index, so not adding company to the query in few cases where source subscriber is known
        
        if ( query.getCompany() != null && isCompanyRequired(query)) {
            criteria.add(Restrictions.eq(CommodityTransfer.FieldName_Company, query.getCompany()));
        }
        if(StringUtils.isNotBlank(query.getBankRRN())){
        	 criteria.add(Restrictions.eq(CommodityTransfer.FieldName_BankRetrievalReferenceNumber, query.getBankRRN()).ignoreCase());
        }
        if(StringUtils.isNotBlank(query.getSourceMessage())){
       	 criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceMessage, query.getSourceMessage()).ignoreCase());
       }
        
    }

    
    /**
	 * @param query 
     * @return
	 */
	private boolean isCompanyRequired(CommodityTransferQuery query) {
		if (query.getSourceSubscMDN() == null
				&& query.getSubscriberMDN() == null
				&& query.getSourceDestMDNAndID() == null 
				&& query.getSourceDestnPocket() == null
				&& query.getSourcePocket() == null)
			return true;
		return false;
	}

	@SuppressWarnings("unchecked")
	private List<CommodityTransfer> getTransferList(CommodityTransferQuery query) {
		List<CommodityTransfer> result = new ArrayList<CommodityTransfer>();
		
		Criteria criteria = createCriteria();
		criteria.createAlias(CommodityTransfer.FieldName_PocketBySourcePocketID, "sourcePocket");
		Criterion sourcePocket = Restrictions.eq("sourcePocket." + Pocket.FieldName_RecordID, query.getSourceDestnPocket().getId());
		Criterion destPocket = Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, query.getSourceDestnPocket().getId());
		
		criteria.add(Restrictions.or(sourcePocket, destPocket));
		
		boolean requiresSuccfullTxns = ConfigurationUtil.getRequiresSuccessfullTransactionsInEmoneyHistory();
		if(requiresSuccfullTxns){
			criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferStatus, CmFinoFIX.TransferState_Complete));
		}
		
		if (query.getStartTimeGE() != null) {
			criteria.add(Restrictions.ge(CommodityTransfer.FieldName_StartTime, query.getStartTimeGE()));
		}
		
		if (query.getStartTimeLT() != null) {
			criteria.add(Restrictions.lt(CommodityTransfer.FieldName_StartTime, query.getStartTimeLT()));
		}
		
		if (query.getTransferStatus() != null) {
			criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferStatus, query.getTransferStatus()));
		}
		
		if (query.getId() != null) {
			criteria.add(Restrictions.eq(CommodityTransfer.FieldName_RecordID, query.getId()));
		}
		
		if (query.getMsgType() != null) {
			criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransactionUICategory, query.getMsgType()));
		}
		
		if (query.getSourceApplicationSearch() != null) {
			criteria.add(Restrictions.eq(CommodityTransfer.FieldName_SourceApplication, query.getSourceApplicationSearch()));
		}
		
		criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
		processPaging(query, criteria);
		
		log.info("Total Number of completed transaction for given query is -->" + query.getTotal());
		result = criteria.list();
		
		return result;
	}
	
	private List<CommodityTransfer> getTransferList(CommodityTransferQuery query, String sqlQueryStr ) {
    	StringBuilder sb = new StringBuilder("select ");
    	sb.append(CommodityTransfer.FieldName_RecordID);
    	sb.append(sqlQueryStr);
    	sb.append(" union all ");
    	sb.append("select ");
    	sb.append(CommodityTransfer.FieldName_RecordID);
    	sb.append(sqlQueryStr.replace(CommodityTransfer.FieldName_DestPocketID, CommodityTransfer.FieldName_SourcePocketID));
    	sb.append(" order by 1 desc limit ");
    	sb.append(query.getStart());
		sb.append(" , ");
		sb.append(query.getLimit());
		SQLQuery sqlQuery = getSession().createSQLQuery(sb.toString());
		sqlQuery.setLong("pocketId", query.getSourceDestnPocket().getId().longValue());
		if(query.getStartTimeGE() != null) {
			sqlQuery.setTimestamp("startTimeGE", query.getStartTimeGE());
		}
		if(query.getStartTimeLT() != null) {
			sqlQuery.setTimestamp("startTimeLT", query.getStartTimeLT());
		}
		if(query.getTransferStatus() != null) {
			sqlQuery.setInteger("transferStatus", query.getTransferStatus());
		}
		if(query.getId() != null) {
			sqlQuery.setLong("referenceId", query.getId());
		}
		if(query.getMsgType() != null) {
			sqlQuery.setInteger("msgType", query.getMsgType());
		}
                
//		if(query.getSourceApplicationSearch() != null  && query.getSourceApplicationSearch() != 0) {
//			List<Integer> srcAppList = new ArrayList<Integer>();
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Web) == CmFinoFIX.SourceApplicationSearch_Web) {
//				srcAppList.add(CmFinoFIX.SourceApplication_Web);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_BackEnd) == CmFinoFIX.SourceApplicationSearch_BackEnd) {
//				srcAppList.add(CmFinoFIX.SourceApplication_BackEnd);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Phone) == CmFinoFIX.SourceApplicationSearch_Phone) {
//				srcAppList.add(CmFinoFIX.SourceApplication_Phone);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebService) == CmFinoFIX.SourceApplicationSearch_WebService) {
//				srcAppList.add(CmFinoFIX.SourceApplication_WebService);
//			}
//                        if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_SMS) == CmFinoFIX.SourceApplicationSearch_SMS) {
//				srcAppList.add(CmFinoFIX.SourceApplication_SMS);
//			}
//			sqlQuery.setParameterList("srcApp", srcAppList);
//		}
		sqlQuery.addScalar("id", StandardBasicTypes.LONG);
                @SuppressWarnings("unchecked")
		List<Long> results = sqlQuery.list();
		if(results != null && results.size() >0){
                    Criteria criteria = createCriteria();
                    criteria.add(Restrictions.in(CommodityTransfer.FieldName_RecordID, results));
		    criteria.addOrder(Order.desc(CommodityTransfer.FieldName_EndTime));
                    @SuppressWarnings("unchecked")
                    List<CommodityTransfer> commoditTransferResults = criteria.list();
                    return commoditTransferResults;
                }else {
                    return new ArrayList<CommodityTransfer>();
                }
	}
    private Long getRowCount(CommodityTransferQuery query, String sqlQueryStr , boolean src) {
    	StringBuilder sb = new StringBuilder("select count(1) as count ");
    	sb.append(sqlQueryStr);
    	String queryStr = sb.toString();
    	if (!src) {
    		queryStr = queryStr.replace(CommodityTransfer.FieldName_DestPocketID, CommodityTransfer.FieldName_SourcePocketID);
    	}
		SQLQuery sqlQuery = getSession().createSQLQuery(queryStr);
		sqlQuery.setLong("pocketId", query.getSourceDestnPocket().getId().longValue());
		if(query.getStartTimeGE() != null) {
			sqlQuery.setTimestamp("startTimeGE", query.getStartTimeGE());
		}
		if(query.getStartTimeLT() != null) {
			sqlQuery.setTimestamp("startTimeLT", query.getStartTimeLT());
		}
		if(query.getTransferStatus() != null) {
			sqlQuery.setInteger("transferStatus", query.getTransferStatus());
		}
		if(query.getId() != null) {
			sqlQuery.setLong("referenceId", query.getId());
		}
		if(query.getMsgType() != null) {
			sqlQuery.setInteger("msgType", query.getMsgType());
		}
//		if(query.getSourceApplicationSearch() != null  && query.getSourceApplicationSearch() != 0) {
//			List<Integer> srcAppList = new ArrayList<Integer>();
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Web) == CmFinoFIX.SourceApplicationSearch_Web) {
//				srcAppList.add(CmFinoFIX.SourceApplication_Web);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_BackEnd) == CmFinoFIX.SourceApplicationSearch_BackEnd) {
//				srcAppList.add(CmFinoFIX.SourceApplication_BackEnd);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_Phone) == CmFinoFIX.SourceApplicationSearch_Phone) {
//				srcAppList.add(CmFinoFIX.SourceApplication_Phone);
//			}
//			if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_WebService) == CmFinoFIX.SourceApplicationSearch_WebService) {
//				srcAppList.add(CmFinoFIX.SourceApplication_WebService);
//			}
//                        if ((query.getSourceApplicationSearch() & CmFinoFIX.SourceApplicationSearch_SMS) == CmFinoFIX.SourceApplicationSearch_SMS) {
//				srcAppList.add(CmFinoFIX.SourceApplication_SMS);
//			}
//			sqlQuery.setParameterList("srcApp", srcAppList);
//		}
		sqlQuery.addScalar("count", StandardBasicTypes.LONG);
                @SuppressWarnings("unchecked")
		List<Long> countList = sqlQuery.list();
		Long srcRowCnt = countList.get(0);
    	return srcRowCnt;
    }
    
    public List<CommodityTransfer> getTxnHistory(CommodityTransferQuery query)
    {
    	if (query.getSourceDestnPocket() != null 
    			&& query.getLimit() <= ConfigurationUtil.getExcelRowLimit() && (query.getSubTotalBy() == null)) {

    		String selectString = "select distinct ct, ctmap.sctlid from CommodityTransfer ct, ChargetxnTransferMap ctmap, ServiceChargeTxnLog sctl ";
    		//String orderString = " order by sctl.ID desc, ct.ID desc ";
    		String orderString = " order by ctmap.sctlid desc, ct.id desc ";
    		String queryString = " where ( ct.pocket = :sourcePocket" 
    				+ " or ct.destpocketid = :destPocketID )"
    				+ " and ctmap.commoditytransferid = ct.id "
    				+ " and sctl.id = ctmap.sctlid "  ;

    		boolean requiresSuccfullTxns = ConfigurationUtil.getRequiresSuccessfullTransactionsInEmoneyHistory();
    		if(requiresSuccfullTxns){
    			queryString = queryString +" and sctl.status in ( :transferStatus )";
    		}

    		if (query.getStartTimeGE() != null) {
    			queryString = queryString +" and sctl.createtime >= :startTimeGE" ;
    		}

    		if (query.getStartTimeLT() != null) {
    			queryString = queryString +" and sctl.createtime < :startTimeLT";
    		}

    		Query newQuery = getSession().createQuery(selectString + queryString + orderString); 
    		if (query.getStart() != null && query.getLimit() != null) 
    		{
    			newQuery.setMaxResults(query.getLimit());
    			newQuery.setFirstResult(query.getStart());
    		}
    		if (query.getSourceDestnPocket() != null)
    		{
    			newQuery.setParameter("sourcePocket" , query.getSourceDestnPocket());
    			newQuery.setParameter("destPocketID" , query.getSourceDestnPocket().getId());
    		}
    		if(requiresSuccfullTxns){
    			List<Integer> statusList = getListOfSuccessTransferStatus();
    			newQuery.setParameterList("transferStatus",  statusList);
    		}    		

    		if (query.getStartTimeGE() != null) {
    			newQuery.setParameter("startTimeGE", query.getStartTimeGE());
    		}

    		if (query.getStartTimeLT() != null) {
    			newQuery.setParameter("startTimeLT", query.getStartTimeLT());
    		}

    		List list = newQuery.list();

    		List<CommodityTransfer> ctList = new ArrayList<CommodityTransfer>();
    		log.info("The size of the ctList is: "+list.size());
    		for(int i=0;i<list.size();i++) 
    		{
    			Object[] data = (Object[])list.get(i);
    			CommodityTransfer ct = (CommodityTransfer)data[0];			
    			long sctlId = (Long)data[1];
    			ct.setSctlId(sctlId);
    			ctList.add(ct);
    		}
    		return ctList;    		
    	}
    	return null;
    }
    
    
    public Long getTxnCount(CommodityTransferQuery query)
    {
    	String selectString = "select distinct count(*) from CommodityTransfer ct, ChargetxnTransferMap ctmap, ServiceChargeTxnLog sctl ";
    	String orderString = " order by sctl.id desc, ct.id desc ";
    	String queryString = " where ( ct.pocket = :sourcePocket" 
    			+ " or ct.destpocketid = :destPocketID )"
    			+ " and ctmap.commoditytransferid = ct.id "
    			+ " and sctl.id = ctmap.sctlid "  ;

    	boolean requiresSuccfullTxns = ConfigurationUtil.getRequiresSuccessfullTransactionsInEmoneyHistory();
    	if(requiresSuccfullTxns){
    		queryString = queryString +" and sctl.status in ( :transferStatus )";
    	}

    	if (query.getStartTimeGE() != null) {
    		queryString = queryString +" and sctl.createtime >= :startTimeGE" ;
    	}

    	if (query.getStartTimeLT() != null) {
    		queryString = queryString +" and sctl.createtime < :startTimeLT";
    	}

    	Query newQuery = getSession().createQuery(selectString + queryString + orderString); 
    	if (query.getSourceDestnPocket() != null)
    	{
    		newQuery.setParameter("sourcePocket" , query.getSourceDestnPocket());
    		newQuery.setParameter("destPocketID" , query.getSourceDestnPocket().getId());
    	}
    	if(requiresSuccfullTxns){
    		List<Integer> statusList = getListOfSuccessTransferStatus();
    		newQuery.setParameterList("transferStatus",  statusList);
    	}    		

    	if (query.getStartTimeGE() != null) {
    		newQuery.setParameter("startTimeGE", query.getStartTimeGE());
    	}

    	if (query.getStartTimeLT() != null) {
    		newQuery.setParameter("startTimeLT", query.getStartTimeLT());
    	}

    	return (Long) newQuery.list().get(0);
    }
    
    private List<Integer> getListOfSuccessTransferStatus()
    {
    	List<Integer> statusList = Arrays.asList( 
				CmFinoFIX.SCTLStatus_Confirmed,	
				CmFinoFIX.SCTLStatus_Distribution_Started,
				CmFinoFIX.SCTLStatus_Distribution_Completed,	
				CmFinoFIX.SCTLStatus_Distribution_Failed,
				CmFinoFIX.SCTLStatus_Reverse_Requested,
				CmFinoFIX.SCTLStatus_Reverse_Initiated,
				CmFinoFIX.SCTLStatus_Reverse_Approved,
				CmFinoFIX.SCTLStatus_Reverse_Rejected,
				CmFinoFIX.SCTLStatus_Reverse_Start,
				CmFinoFIX.SCTLStatus_Reverse_Processing,
				CmFinoFIX.SCTLStatus_Reverse_Success,
				CmFinoFIX.SCTLStatus_Reversed,
				CmFinoFIX.SCTLStatus_Reverse_Failed
		);
    	return statusList;
    }
    

    public List<CommodityTransfer> get(CommodityTransferQuery query) throws Exception {
    	if (query.getSourceDestnPocket() != null 
    			&& query.getLimit() != ConfigurationUtil.getExcelRowLimit() && (query.getSubTotalBy() == null)) {
    		//Do Sql processing and return
//    		StringBuilder srcQueryStrBuilder = new StringBuilder();
//    		srcQueryStrBuilder = new StringBuilder();
//    		srcQueryStrBuilder.append(" from commodity_transfer where ");
//    		srcQueryStrBuilder.append(CommodityTransfer.FieldName_DestPocketID);
//    		srcQueryStrBuilder.append(" = :pocketId ");
//    		if(query.getStartTimeGE() != null) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_StartTime);
//    			srcQueryStrBuilder.append(" >= :startTimeGE");
//    		}
//    		if(query.getStartTimeLT() != null) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_StartTime);
//    			srcQueryStrBuilder.append(" < :startTimeLT");
//    		}
//    		if(query.getTransferStatus() != null) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_TransferStatus);
//    			srcQueryStrBuilder.append(" = :transferStatus");
//    		}
//    		if(query.getId() != null) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_RecordID);
//    			srcQueryStrBuilder.append(" = :referenceId");
//    		}
//    		if(query.getMsgType() != null) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_TransactionUICategory);
//    			srcQueryStrBuilder.append(" = :msgType");
//    		}
//    		if(query.getSourceApplicationSearch() != null && query.getSourceApplicationSearch() != 0) {
//    			srcQueryStrBuilder.append(" and ");
//    			srcQueryStrBuilder.append(CommodityTransfer.FieldName_SourceApplication);
//    			srcQueryStrBuilder.append(" in (:srcApp)");
//    		}
//    		long destRowCnt = getRowCount(query,srcQueryStrBuilder.toString(),false);
//    		long srcRowCnt = getRowCount(query,srcQueryStrBuilder.toString(),true);
//    		log.info("Total row count = " + (srcRowCnt + destRowCnt));
//    		query.setTotal((int)(srcRowCnt + destRowCnt));
//    	    return getTransferList(query, srcQueryStrBuilder.toString());
    		return getTransferList(query);
    	}
		
       	// control will be here when it is download.
    	// no need to run separate queries when source dest pocket  is null
    	// a query division done below is required in case source and dest pocket 
    	// are there.
    	if (query.getSourceDestnPocket() != null &&
    			query.getLimit()!=null && query.getLimit() == ConfigurationUtil.getExcelRowLimit())
    	{
	    	/**
	    	 * code for getting the results with only source pocket id set
	    	 * NOTE: when you change this code make sure the below code for dest pocket id
	    	 * is changed accordingly
	    	 */
	        Criteria sourcePocketCriteria = createCriteria();
	        
	        applyQuery(query, sourcePocketCriteria,false);
	        
	        applySourcePocketCriteria(query,sourcePocketCriteria);
	
	        processBaseQuery(query, sourcePocketCriteria);
	        // Paging
	       // processPaging(query, sourcePocketCriteria);
	
	        //applying Order
	        sourcePocketCriteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
	        applyOrder(query, sourcePocketCriteria);
	        
	        /**
	         * code for getting the results with only dest pocket id set
	         */
	        Criteria destPocketCriteria = createCriteria();
	        
	        applyQuery(query, destPocketCriteria,false);
	        
	        applyDestPocketCriteria(query,destPocketCriteria);
	        
	        // TODO:possible optimization
	        //size of result of querying from source dest pocket is greater than the limit 
	        // we want
	        // hence we need to get the results which are greater than the limit record 
	        // from source destination pocket and use it as extra criteria on dest pocket query
	        
	        processBaseQuery(query, destPocketCriteria);
	        // Paging
	       // processPaging(query, destPocketCriteria);
	
	        //applying Order
//	        destPocketCriteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
//	        applyOrder(query, destPocketCriteria);

	        /**
	         * Get the results from source pocket and dest pocket and merge and sort and get the 
	         * max limit rows
	         */
	        @SuppressWarnings("unchecked")
	        List<CommodityTransfer> resultFromSourcePokcet = sourcePocketCriteria.list();
	        
	        
	        @SuppressWarnings("unchecked")
	        List<CommodityTransfer> resultFromDestPocket = destPocketCriteria.list();
	        
	        //now merge these results
	        List<CommodityTransfer> results = mergeResults(resultFromSourcePokcet,resultFromDestPocket,query.getLimit());
	        query.setTotal(results.size());
	        return results;
    	}
    	//for the case of viewing the results from UI
    	//  --- HACK HACK HACK ---
    	// This is not the optimal solution code wise, work on removing this code 
    	// or use separate method for download stuff.
    	else
    	{
	        Criteria criteria = createCriteria();
	
	        applyQuery(query, criteria);
	
	        processBaseQuery(query, criteria);
	        // Paging
	        processPaging(query, criteria);
	
        	criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
	        applyOrder(query, criteria);
	        @SuppressWarnings("unchecked")
	        List<CommodityTransfer> results = criteria.list();
	        return results;
    	}
    }


    private List<CommodityTransfer> mergeResults(List<CommodityTransfer> results1,
			List<CommodityTransfer> results2, int maxCount)
	{
		List<CommodityTransfer> mergedResult = new ArrayList<CommodityTransfer>();
		mergedResult.addAll(results1);
		mergedResult.addAll(results2);
        Collections.sort(mergedResult, new Comparator<CommodityTransfer>() {
        	@Override
			public int compare(CommodityTransfer ct1, CommodityTransfer ct2)
        	{
				return (BigDecimal.valueOf(ct2.getId()).subtract(BigDecimal.valueOf(ct1.getId()))).intValue();
			}
        });
        List<CommodityTransfer> sortedMergedResult;
        if(mergedResult != null && mergedResult.size() >= maxCount)
        {
        	sortedMergedResult = mergedResult.subList(0, maxCount);
        }else {
        	sortedMergedResult = mergedResult;
        }
        return sortedMergedResult;
	}


	private void applySourcePocketCriteria(CommodityTransferQuery query, Criteria criteria)
    {
    	 if (query.getSourceDestnPocket() != null) {
//           criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
           criteria.add( Restrictions.eq(PocketBySourcePocketID,query.getSourceDestnPocket()));
       }
    }

    private void applyDestPocketCriteria(CommodityTransferQuery query, Criteria criteria)
    {
    	 if (query.getSourceDestnPocket() != null) {
//           criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
           criteria.add(Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, query.getSourceDestnPocket().getId()));
       }
    }

    public List<Object[]> groupBy(CommodityTransferQuery query) throws Exception {
        Criteria criteria = createCriteria();

        applyQuery(query, criteria);
        processBaseQuery(query, criteria);

        ProjectionList projList = Projections.projectionList();

        if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Access_Method) {
            projList.add(Projections.groupProperty(CommodityTransfer.FieldName_SourceApplication));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Commodity_Type) {
            projList.add(Projections.groupProperty(CommodityTransfer.FieldName_Commodity));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Transaction_Status) {
            projList.add(Projections.groupProperty(CommodityTransfer.FieldName_TransferStatus));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Transaction_Type) {
            projList.add(Projections.groupProperty(CommodityTransfer.FieldName_TransactionUICategory));
        } else if (query.getSubTotalBy() == CmFinoFIX.SubtotalBy_Buy_Sell) {
            if (query.getSourcePocket() != null) {
                //projList.add(Projections.groupProperty(PendingCommodityTransfer.FieldName_SourcePocketID));
                //criteria.createAlias(POCEKTBYSOURCEPOCKETID, POCEKTBYSOURCEPOCKETID+DAOConstants.ALIAS_SUFFIX);
                projList.add(Projections.groupProperty(PocketBySourcePocketID));
            } else {
                projList.add(Projections.groupProperty(PendingCommodityTransfer.FieldName_DestPocketID));
            }
        }
        projList.add(Projections.sum(CommodityTransfer.FieldName_TransferAmount));

        criteria.setProjection(projList);
        @SuppressWarnings("unchecked")
        List<Object[]> results = criteria.list();

        return results;
    }

    public List<Integer> getAllDistinctBankCodes(Date startGE, Date startLT) {
        String sqlQuery = "SELECT distinct " + CommodityTransfer.FieldName_ISO8583_AcquiringInstIdCode + " FROM CommodityTransfer where starttime >= :startGE and starttime < :startLT";

        Query queryObj = getQuery(sqlQuery);
        queryObj.setTimestamp("startGE", startGE);
        queryObj.setTimestamp("startLT", startLT);
        @SuppressWarnings("unchecked")
        List<Integer> results = (List<Integer>) queryObj.list();
        return results;
    }

    @Deprecated
    public BigDecimal getPocketBalanceForRetiredMerchantAsOf(Pocket pocket, Date date) {
        // The best indicator would be the source pocket balance captured in the immediate next transfer after this time.
        // But we might not have any, so make do with the immediate transfer preceding this time.
        Criteria criteria = createCriteria();
        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
        criteria.add(Restrictions.eq(PendingCommodityTransfer.FieldName_PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "id",
                pocket.getId()));
        criteria.add(Restrictions.le(CommodityTransfer.FieldName_StartTime, date));
        criteria.addOrder(Order.desc(CommodityTransfer.FieldName_StartTime));

        criteria.setMaxResults(1);
        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();

        if (null == results || 0 == results.size()) {
            return null;
        }

        CommodityTransfer ct = results.get(0);
        BigDecimal transferAmount = new BigDecimal(0);
        if (CmFinoFIX.TransferStatus_Completed.equals(ct.getTransferstatus())) {
            transferAmount = ct.getAmount();
        }

        BigDecimal balance = new BigDecimal(0);
  	  //Before Correcting errors reported by Findbugs:
  		//if (ct.getPocketBySourcePocketID().getID() == pocket.getID()) {
  	
  	  //After Correcting the errors reported by Findbugs
        if (ct.getPocket().getId() != null && ct.getPocket().getId().equals(pocket.getId())) {
        	BigDecimal sourcePocketBalance = ct.getSourcepocketbalance();
            balance = sourcePocketBalance.subtract(transferAmount);
        } else {
        	BigDecimal destPocketBalance = ct.getDestpocketbalance();
            balance = destPocketBalance.add(transferAmount);
        }

        return balance;
    }

    public CommodityTransfer getLastTransferBefore(Pocket pocket, Date date) {
        // The best indicator would be the source pocket balance captured in the immediate next transfer after this time.
        // But we might not have any, so make do with the immediate transfer preceding this time.
        //Date lastTransactionDate = pocket.getLastTransactionTime();
        Date toUse = date;
//      if(lastTransactionDate.before(date)) {
//        toUse = lastTransactionDate;
//      }

        CommodityTransfer ctSource = getLastTransferAsSourceBefore(pocket, toUse);
        CommodityTransfer ctDest = getLastTransferAsDestBefore(pocket, toUse);

        if (null == ctSource) {
            return ctDest;
        } else if (null == ctDest) {
            return ctSource;
        } else if (ctSource.getId().compareTo(ctDest.getId()) > 0) {
            return ctSource;
        } else {
            return ctDest;
        }
    }

    public CommodityTransfer getFirstTransferAfter(Pocket pocket, Date date) {
        
    	CommodityTransfer ctSource = getFirstTransferAfter(pocket, date, true);
    	CommodityTransfer ctDest = getFirstTransferAfter(pocket, date, false);
      //Long id = null;
      
      if (null == ctSource) {
        return ctDest;
      } else if (null == ctDest) {
        return ctSource;
      } else if (ctSource.getId().compareTo(ctDest.getId()) < 0) {
        return ctSource;
      } else {
        return ctDest;
      }
      
      /*if(id == null)
    	  return null;
      else
    	  return getById(id);*/
    }
    
    @SuppressWarnings("unchecked")
	private CommodityTransfer getFirstTransferAfter(Pocket pocket, Date date, boolean isSource) {
    	String pocketField =  CommodityTransfer.FieldName_DestPocketID;
    	if(isSource) {
    		pocketField = CommodityTransfer.FieldName_SourcePocketID;
    	}
    	
    	/**
    	 * There is a potential bug in the MySQL planning engine which takes a long time to return when the
    	 * WHERE clause doesn't match any records. In this case it is using ID as the index and using where on 
    	 * all the records (> 4mil) which takes a long time. This is part of the MySQL Order By optimization.
    	 * To make sure that this 'optimization' does not kill us, we add an additional dummy field to the 
    	 * order by clause which will prevent this. 
    	 */
    	
    	
    	String hql = "from CommodityTransfer where " +	pocketField + " = :pocketID and " +
    			CommodityTransfer.FieldName_StartTime + " >= :startTime " + 
    			"order by " + CommodityTransfer.FieldName_StartTime + ", ID ";
        Query queryObj = getQuery(hql);
        queryObj.setLong("pocketID", pocket.getId().longValue());
        queryObj.setTimestamp("startTime", date);
        queryObj.setMaxResults(1);
        
        List<CommodityTransfer> results = queryObj.list();
        if (null == results || 0 == results.size()) {
            return null;
        }
        return results.get(0);        
    } 
    
    
    private CommodityTransfer getLastTransferAsSourceBefore(Pocket pocket, Date date) {
        Criteria criteria = createCriteria();
        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
        Criterion sourcePocketIDCriterion = Restrictions.eq(CommodityTransfer.FieldName_PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "id",
                pocket.getId());

        criteria.add(sourcePocketIDCriterion);
        criteria.add(Restrictions.le(CommodityTransfer.FieldName_StartTime, date));
        criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
        //criteria.addOrder(Order.desc(CommodityTransfer.FieldName_StartTime));

        criteria.setMaxResults(1);

        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();

        if (null == results || 0 == results.size()) {
            return null;
        }

        CommodityTransfer ct = results.get(0);
        return ct;
    }

    private CommodityTransfer getLastTransferAsDestBefore(Pocket pocket, Date date) {
        Criteria criteria = createCriteria();
        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);

        Criterion destPocketIDCriterion = Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, pocket.getId());
        criteria.add(destPocketIDCriterion);
        criteria.add(Restrictions.le(CommodityTransfer.FieldName_StartTime, date));
        //criteria.addOrder(Order.desc(CommodityTransfer.FieldName_StartTime));
        criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));

        criteria.setMaxResults(1);

        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();

        if (null == results || 0 == results.size()) {
            return null;
        }

        CommodityTransfer ct = results.get(0);
        return ct;
    }

    public List<CommodityTransfer> getResolvedAsFailedTxnsBetween(Pocket pocket, Date start, Date end) {
        Criteria criteria = createCriteria();

        criteria.createAlias(PocketBySourcePocketID, PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX);
        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_PocketBySourcePocketID + DAOConstants.ALIAS_SUFFIX + DAOConstants.ALIAS_COLNAME_SEPARATOR + "id",
                pocket.getId()));
        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_TransferStatus, CmFinoFIX.TransferStatus_Failed));
        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_CSRAction, CmFinoFIX.CSRAction_Cancel));
        criteria.add(Restrictions.ge(CommodityTransfer.FieldName_CSRActionTime, start));
        criteria.add(Restrictions.lt(CommodityTransfer.FieldName_CSRActionTime, end));

        // criteria.addOrder(Order.desc(CommodityTransfer.FieldName_CSRActionTime));


        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();

        return results;
    }

    public Integer getAirtimeTxnsCount(Date end) {
        Criteria criteria = createCriteria();
        Integer[] uiCategories = new Integer[]{
            CmFinoFIX.TransactionUICategory_BulkTopup,
            CmFinoFIX.TransactionUICategory_BulkTransfer,
            CmFinoFIX.TransactionUICategory_MA_Topup,
            CmFinoFIX.TransactionUICategory_MA_Transfer,
            CmFinoFIX.TransactionUICategory_Empty_SVA,
            CmFinoFIX.TransactionUICategory_Distribute_LOP
        };

        criteria.add(Restrictions.le(CommodityTransfer.FieldName_StartTime, end));
        criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, uiCategories));
        criteria.setProjection(Projections.rowCount());
        Integer count = (Integer) criteria.uniqueResult();
        return count;
    }

    public List<CommodityTransfer> getAllAirtimeTxnsOrderedByStartBefore(Date end, int start, int maxResults) {
        Criteria criteria = createCriteria();
        Integer[] uiCategories = new Integer[]{
            CmFinoFIX.TransactionUICategory_BulkTopup,
            CmFinoFIX.TransactionUICategory_BulkTransfer,
            CmFinoFIX.TransactionUICategory_MA_Topup,
            CmFinoFIX.TransactionUICategory_MA_Transfer,
            CmFinoFIX.TransactionUICategory_Empty_SVA,
            CmFinoFIX.TransactionUICategory_Distribute_LOP
        };

        criteria.add(Restrictions.le(CommodityTransfer.FieldName_StartTime, end));
        criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, uiCategories));
        criteria.addOrder(Order.desc(CommodityTransfer.FieldName_StartTime));
        criteria.setFirstResult(start);
        criteria.setMaxResults(maxResults);

        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();
        return results;
    }

    public List<CommodityTransfer> getAllRAFTxns(int start, int maxResults) {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_CSRAction, CmFinoFIX.CSRAction_Cancel));

        criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
        criteria.setFirstResult(start);
        criteria.setMaxResults(maxResults);

        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();
        return results;
    }

    public int getAllRAFTxnCount() {
        Criteria criteria = createCriteria();

        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_CSRAction, CmFinoFIX.CSRAction_Cancel));

        criteria.setProjection(Projections.rowCount());
        Integer count = (Integer) criteria.uniqueResult();
        return count;

    }
    
    public List<CommodityTransfer> getAllRAFTxnsAfter(int start, int maxResults, Date date, Long companyId){
      Criteria criteria = createCriteria();
      
      criteria.add(Restrictions.eq(CommodityTransfer.FieldName_CSRAction, CmFinoFIX.CSRAction_Cancel));
      criteria.add(Restrictions.ge(CommodityTransfer.FieldName_CSRActionTime, date));
      if(companyId != null) {
        Company company = companyDao.getById(companyId);
        criteria.add(Restrictions.eq(CommodityTransfer.FieldName_Company, company));
      }
      
      criteria.addOrder(Order.desc(CommodityTransfer.FieldName_RecordID));
      criteria.setFirstResult(start);
      criteria.setMaxResults(maxResults);

      @SuppressWarnings("unchecked")
      List<CommodityTransfer> results = criteria.list();
      return results;
     }

    public List<CommodityTransfer> getAllRAFTxnsAfter(int start, int maxResults, Date date, Long pocketID, Long companyId){
        String hql = "from CommodityTransfer where csraction=:csraction and csractiontime >= :csractiontime and sourcepocketid = :sourcepocketid " +
        		"and company= :companyId order by id desc";
        Query queryObj = getQuery(hql);
    	queryObj.setInteger("csraction", CmFinoFIX.CSRAction_Cancel);
    	queryObj.setTimestamp("csractiontime", date);
        queryObj.setFirstResult(start);
        queryObj.setMaxResults(maxResults);
        queryObj.setLong("sourcepocketid", pocketID);
        queryObj.setLong("companyId", companyId);
        
        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = queryObj.list();
        return results;
    }
    
    public int getCountOfAllRAFTxnAfter(Date date, Long companyId){
      Criteria criteria = createCriteria();
      
      criteria.add(Restrictions.eq(CommodityTransfer.FieldName_CSRAction, CmFinoFIX.CSRAction_Cancel));      
      criteria.add(Restrictions.ge(CommodityTransfer.FieldName_CSRActionTime, date));
      if(companyId != null) {
          Company company = companyDao.getById(companyId);
          criteria.add(Restrictions.eq(CommodityTransfer.FieldName_Company, company));
      }
      criteria.setProjection(Projections.rowCount());
      Integer count = (Integer) criteria.uniqueResult();
      return count;
      
     }
    
    public int markSourceMDNRX(Long mdnID, String mdnRX){
    	String sqlQuery = "update commodity_transfer set sourcemdn = :mdnRX where sourcemdnid = :mdnID";
    	Query queryObj = getSQLQuery(sqlQuery);
    	queryObj.setLong("mdnID", mdnID);
    	queryObj.setString("mdnRX", mdnRX);
    	return queryObj.executeUpdate();
}
    
    public int markDestMDNRX(String mdn, String mdnRX){
    	String sqlQuery = "update commodity_transfer set destmdn = :mdnRX where destmdn = :mdn";
    	Query queryObj = getSQLQuery(sqlQuery);
    	queryObj.setString("mdn", mdn);
    	queryObj.setString("mdnRX", mdnRX);
    	return queryObj.executeUpdate();
    }

    public Date getTopUpTime(long mdnId){
    	String sqlQuery = "select id,StartTime from commodity_transfer where SourceMDNID = :mdnId and UICategory = :uiCategory ORDER BY ID DESC";
    	Query queryObj = getSQLQuery(sqlQuery);
    	queryObj.setLong("mdnId", mdnId);
    	queryObj.setInteger("uiCategory", CmFinoFIX.TransactionUICategory_MA_Topup);
    	queryObj.setMaxResults(1);
        @SuppressWarnings("unchecked")
    	List<Object[]> list = queryObj.list();
    	if (list.size() > 0) {
    		Object[] objects = list.get(0);
    		Date date = (Date) objects[1];
    		return date;
    	}
    	return null;
    }
    
    public CommodityTransfer getEmptySVATxn(long mdnId){
    	return getEmptySVATxn(mdnId, CmFinoFIX.TransactionUICategory_Empty_SVA); 
    }
    
    public CommodityTransfer getEMoneyEmptySVATxn(long mdnId){
    	return getEmptySVATxn(mdnId, CmFinoFIX.TransactionUICategory_EMoney_Empty_SVA);
    }
    
    private CommodityTransfer getEmptySVATxn(long mdnId, Integer uiCategory){    	   	
    	//HQL does not support Limit clause!
    	String hql = "from CommodityTransfer where SourceMDNID = :mdnId and UICategory = :uiCategory ORDER BY ID DESC";
    	Query queryObj = getQuery(hql);
    	queryObj.setLong("mdnId", mdnId);
    	queryObj.setInteger("uiCategory", uiCategory);
    	queryObj.setMaxResults(1);
        @SuppressWarnings("unchecked")
    	List<CommodityTransfer> list = queryObj.list();
    	if (list.size() > 0) {    		
    		return list.get(0);
    	}
    	return null;
    }
    public CommodityTransfer Copy(PendingCommodityTransfer pendingCommodityTransfer)
    {
        Long pId = pendingCommodityTransfer.getId().longValue();
        String properties = GetProperties();
        Query queryObj = getQuery("insert into commodity_transfer (" + properties +") values (select ("+ properties +") from pending_commodity_transfer where id =" +pId +")");
        queryObj.executeUpdate();
          Criteria criteria = createCriteria();
          criteria.add(Restrictions.eq("ID", pId));
        
        @SuppressWarnings("unchecked")
        List<CommodityTransfer> results = criteria.list();
        return results.get(0);
    }
    private String GetProperties()
    {
        StringBuffer str = new StringBuffer();
        ClassMetadata classMetadata = ((AbstractEntityPersister)getSession().getSessionFactory().getClassMetadata(CommodityTransfer.class));
        String[] propertyNames = classMetadata.getPropertyNames();
        int i=0;
        for(i=0;i<propertyNames.length-1;i++)
        {
            str.append(propertyNames[i]);
            str.append(",");
        }
        str.append(propertyNames[i]);
        return str.toString();
    }
    /**
     * 
     * @param startDate
     * @param endDate
     * @param company
     * @return List 
     * all the completed transactions based on the inputs startdate, enddate and company.  
     */
    public List<CommodityTransfer> getAllCompleteCCTransactions(Date startDate,Date endDate,Company company){
    Criteria criteria = createCriteria();
    List<Integer> collection = new ArrayList<Integer>();
    collection.add(CmFinoFIX.TransactionUICategory_CC_Payment);
    collection.add(CmFinoFIX.TransactionUICategory_CC_Topup);
    criteria.add(Restrictions.in(CommodityTransfer.FieldName_TransactionUICategory, collection));
    if(startDate!=null){
    criteria.add(Restrictions.ge(CommodityTransfer.FieldName_CreateTime, startDate));
    }
    if(endDate!=null){
    criteria.add(Restrictions.lt(CommodityTransfer.FieldName_CreateTime, endDate));
    }
    if(company!=null){
    	criteria.add(Restrictions.eq(CommodityTransfer.FieldName_Company, company));
    }
    @SuppressWarnings("unchecked")
    List<CommodityTransfer> results = criteria.list();
    return results;
    }

	public List<CommodityTransfer> getByNotificationCode(Date startDate, Date endDate,Integer notificationcode) {
		 Criteria criteria = createCriteria();
		 if(startDate!=null){
			    criteria.add(Restrictions.ge(CommodityTransfer.FieldName_CreateTime, startDate));
			    }
			    if(endDate!=null){
			    criteria.add(Restrictions.lt(CommodityTransfer.FieldName_CreateTime, endDate));
			    }
			    if(notificationcode!=null){
			    	criteria.add(Restrictions.eq(CommodityTransfer.FieldName_NotificationCode, notificationcode));
			    }
			    @SuppressWarnings("unchecked")
			    List<CommodityTransfer> results = criteria.list();
			    return results;
	}

	@SuppressWarnings("unchecked")
	public List<CommodityTransfer> getSelfB2E2BTransactions(Date startDate, Date endDate) {		
		List<Integer> uiCateCategory =new ArrayList<Integer>();
		uiCateCategory.add(CmFinoFIX.TransactionUICategory_Dompet_EMoney_Trf);
		uiCateCategory.add(CmFinoFIX.TransactionUICategory_EMoney_Dompet_Trf);
		
		String hql ="select ct from CommodityTransfer as ct where ct.CreateTime between :start and :end and ct.TransferStatus = :status and ct.SourceMDN= ct.DestMDN and ct.UICategory in (:uiCategories)";
		 
        Query queryObj = getQuery(hql);
        queryObj.setDate("start", startDate);
        queryObj.setDate("end", endDate);
        queryObj.setInteger("status", CmFinoFIX.TransferStatus_Completed);
        queryObj.setParameterList("uiCategories", uiCateCategory);        
        List<CommodityTransfer> results = queryObj.list();	    
        return results;
	}
	
	/**
	 * Returns the List of Commodity transfer ids contains the given pocket id either as Source or destination.
	 * @param pocketId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<Long> getCommodityTransferIdsBySourceAndDestPocketId(Long pocketId) throws Exception{
		List<Long> lstCtIds = null;
		PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();
		Pocket pocket = pocketDao.getById(pocketId);
		
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.disjunction().add(
	                    Restrictions.eq(PocketBySourcePocketID, pocket)).add(
	                    Restrictions.eq(CommodityTransfer.FieldName_DestPocketID, pocket.getId())));
		
		List<CommodityTransfer> lstCommodityTransfers = criteria.list();
		
		if (CollectionUtils.isNotEmpty(lstCommodityTransfers)) {
			lstCtIds = new ArrayList<Long>();
			for (CommodityTransfer ct: lstCommodityTransfers) {
				lstCtIds.add(ct.getId().longValue());
			}
		}
		return lstCtIds; 
	}
	
}
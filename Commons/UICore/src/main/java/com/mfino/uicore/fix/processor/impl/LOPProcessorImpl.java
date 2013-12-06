/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.LOPDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.query.LOPQuery;
import com.mfino.domain.CommodityTransfer;
import com.mfino.domain.LOP;
import com.mfino.domain.Merchant;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSLOP;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.LOPService;
import com.mfino.service.UserService;
import com.mfino.service.impl.UserServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.LOPProcessor;

@Service("LOPProcessorImpl")
public class LOPProcessorImpl extends BaseFixProcessor implements LOPProcessor{

	@Autowired
	@Qualifier("LOPServiceImpl")
	private LOPService lopService;

	@Autowired
	@Qualifier("UserServiceImpl")
	private UserService userService;
	
    private void updateEntity(LOP lop, CmFinoFIX.CMJSLOP.CGEntries e) {

        if (e.getStatus() != null) {
            lop.setStatus(e.getStatus());
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String userName = (auth != null) ? auth.getName() : " ";
            if (e.getStatus().equals(CmFinoFIX.LOPStatus_Approved)) {
                e.setApprovedBy(userName);
                e.setApprovalTime(new Timestamp(new Date()));
            } else if (e.getStatus().equals(CmFinoFIX.LOPStatus_Rejected)) {
                e.setRejectedBy(userName);
                 e.setRejectTime(new Timestamp(new Date()));
            }
        }
        if (e.getGiroRefID() != null) {
            lop.setGiroRefID(e.getGiroRefID());
        }
        if (e.getActualAmountPaid() != null) {
            lop.setActualAmountPaid(e.getActualAmountPaid());
        }
        if (e.getAmountDistributed() != null) {
            lop.setAmountDistributed(e.getAmountDistributed());
        }
        if (e.getApprovalTime() != null) {
            lop.setApprovalTime(e.getApprovalTime());
        }
        if (e.getApprovedBy() != null) {
            lop.setApprovedBy(e.getApprovedBy());
        }
        if (e.getTransferDate() != null) {
            lop.setTransferDate(e.getTransferDate());
        }
        if (e.getDistributeTime() != null) {
            lop.setDistributeTime(e.getDistributeTime());
        }
        if (e.getDistributedBy() != null) {
            lop.setDistributedBy(e.getDistributedBy());
        }
        if (e.getCreateTime() != null) {
            lop.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            lop.setLastUpdateTime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            lop.setUpdatedBy(e.getUpdatedBy());
        }
        if (e.getCreatedBy() != null) {
            lop.setCreatedBy(e.getCreatedBy());
        }
        if (e.getComment() != null) {
            lop.setLOPComment(e.getComment());
        }
        if (e.getDistributorName() != null) {
            lop.getMerchantBySubscriberID().setTradeName(e.getDistributorName());
        }
        if(e.getCommission() != null) {
        	lop.setCommission(e.getCommission());
        }
    }

    private void updateMessage(LOP lop, CMJSLOP.CGEntries entry) {

        entry.setID(lop.getID());
        Set<CommodityTransfer> results = lop.getCommodityTransferFromLOPID();
        if (results.size() > 0) {
            CommodityTransfer ct = (CommodityTransfer) results.toArray()[0];
            entry.setTransactionID(ct.getID());
        }
        if (lop.getStatus() != null) {
            entry.setStatus(lop.getStatus());
        }
        if (lop.getGiroRefID() != null) {
            entry.setGiroRefID(lop.getGiroRefID());
        }
        if (lop.getActualAmountPaid() != null) {
            entry.setActualAmountPaid(lop.getActualAmountPaid());
        }
        if (lop.getAmountDistributed() != null) {
            entry.setAmountDistributed(lop.getAmountDistributed());
        }
        if (lop.getApprovalTime() != null) {
            entry.setApprovalTime(lop.getApprovalTime());
        }
        if (lop.getApprovedBy() != null) {
            entry.setApprovedBy(lop.getApprovedBy());
        }
        if (lop.getTransferDate() != null) {

//            DateFormat df = new SimpleDateFormat("yyyyMM-dd");
//
//            try {
//                Date tdate = df.parse(lop.getTransferDate());
//                entry.setTransferDate(df.format(tdate));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            entry.setTransferDate(lop.getTransferDate());
        }
        if (lop.getDistributeTime() != null) {
            entry.setDistributeTime(lop.getDistributeTime());
        }
        if (lop.getDistributedBy() != null) {
            entry.setDistributedBy(lop.getDistributedBy());
        }
        if (lop.getCreateTime() != null) {
            entry.setCreateTime(lop.getCreateTime());
        }
        if (lop.getCreatedBy() != null) {
            entry.setCreatedBy(lop.getCreatedBy());
        }
        if (lop.getLastUpdateTime() != null) {
            entry.setLastUpdateTime(lop.getLastUpdateTime());
        }
        if (lop.getUpdatedBy() != null) {
            entry.setUpdatedBy(lop.getUpdatedBy());
        }
        if (lop.getVersion() != null) {
            entry.setRecordVersion(lop.getVersion());
        }
        if (lop.getLOPComment() != null) {
            entry.setComment(lop.getLOPComment());
        }
		if (lop.getCommission() != null) {
			entry.setCommission(lop.getCommission());
		} else if (lop.getDistributionChainLevelByDCTLevelID() != null && 
				lop.getDistributionChainLevelByDCTLevelID().getCommission() != null) {
				entry.setCommission(lop.getDistributionChainLevelByDCTLevelID().getCommission());
		} else if (lop.getActualAmountPaid() != null && lop.getAmountDistributed() != null) {
			log.info("commision is null for this lop:"+lop.getID());
			BigDecimal amountPaid = lop.getActualAmountPaid();
			BigDecimal amountDistributed = lop.getAmountDistributed();
			BigDecimal commission = ZERO;
//			commission = ((amountDistributed - amountPaid) * 100.0) / (amountDistributed * 1.0);
			commission = (amountDistributed.subtract(amountPaid).multiply(HUNDREAD)).divide(amountDistributed);			
//			entry.setCommission(Math.round(commission) * 1.0);
			entry.setCommission(commission);
			
		}

        if (lop.getSubscriberMDNByMDNID() != null) {
            if (lop.getSubscriberMDNByMDNID().getID() != null) {
                entry.setMDNID(lop.getSubscriberMDNByMDNID().getID());
            }
            if (lop.getSubscriberMDNByMDNID().getMDN() != null) {
                entry.setMDN(lop.getSubscriberMDNByMDNID().getMDN());
            }
        }
        if (lop.getMerchantBySubscriberID().getSubscriber() != null) {
            if (lop.getMerchantBySubscriberID().getSubscriber().getUser() != null) {
                entry.setUsername(lop.getMerchantBySubscriberID().getSubscriber().getUser().getUsername());
            }
        }
        if (lop.getMerchantBySubscriberID().getSubscriber() != null) {
            entry.setSubscriberID(lop.getMerchantBySubscriberID().getID());
        }
    }
    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSLOP realMsg = (CMJSLOP) msg;

        LOPDAO dao = DAOFactory.getInstance().getLopDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSLOP.CGEntries[] entries = realMsg.getEntries();

            for (CMJSLOP.CGEntries e : entries) {
                LOP lop = dao.getById(e.getID());

                // Here check for the LOP Expiration and if
                // applicable set it.
                lopService.checkAndSetExpiredStatus(lop);
                
                // Check for Stale Data
                if (!e.getRecordVersion().equals(lop.getVersion())) {
                    handleStaleDataException();
                }

                //Do not allow LOP to be approved if the creator merchant is not active.
                if (CmFinoFIX.LOPStatus_Approved.equals(e.getStatus())) {
                    //Merchant m = MerchantService.getMerchantFromMDN(e.getMDN());
                    // We got the LOP object from the id somewhere above.
                    // We can use the LOP object to get the Merchant instead of using the entries.
                    // Since this is an update only remote modified fields will be available in the 
                    // entries. So the entries.getmdn will be null as it is not modified neither sent
                    // explicitly.
                    Merchant m = lop.getMerchantBySubscriberID();
                    if (!CmFinoFIX.SubscriberStatus_Active.equals(m.getStatus())) {
                        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                        errorMsg.setErrorDescription(MessageText._("Merchant is not active. You can not approve the LOP"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        // The return statement was missing previously.
                        return errorMsg;
                    }
                } else if (CmFinoFIX.LOPStatus_Rejected.equals(e.getStatus())) {
                    // If we reach here then its LOP Reject.
                    // we need to revert the Amount Pending for the week
                    // for this merchant.
                    lopService.resetCurrentWeeklyAmount(lop);
                }

                updateEntity(lop, e);
                dao.save(lop);
                updateMessage(lop, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            LOPQuery query = new LOPQuery();
            if (userService.getUserCompany() != null) {
                query.setCompany(userService.getUserCompany());
            }
            if (realMsg.getStartDateSearch() != null) {
                query.setStartDate(realMsg.getStartDateSearch());
            }
            if (realMsg.getEndDateSearch() != null) {
                query.setEndDate(realMsg.getEndDateSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            if (StringUtils.isEmpty(realMsg.getUsernameSearch()) == false) {
                query.setUserName(realMsg.getUsernameSearch());
            }
            if(realMsg.getMerchantIDSearch()!=null)
            {
                SubscriberDAO subsDao = DAOFactory.getInstance().getSubscriberDAO();
                Subscriber sub = subsDao.getById(realMsg.getMerchantIDSearch());
                SubscriberMDN subMdn = (SubscriberMDN)sub.getSubscriberMDNFromSubscriberID().toArray()[0];
                query.setMdnid(subMdn.getID());
            }
            String status = realMsg.getLOPStatusSearch();
            if (status != null && status.trim().length() > 0) {
                query.setLopstatus(status);
            }

            String dname = realMsg.getDistributorNameSearch();
            if (dname != null && dname.trim().length() > 0) {
                query.setDistributornameLike(dname);
            }

            if (realMsg.getDCTNameSearch() != null && realMsg.getDCTNameSearch().length() > 0) {
                query.setDctName(realMsg.getDCTNameSearch());
            }
            if (realMsg.getLOPViewSearch() != null) {
                if (CmFinoFIX.LOPViews_NormalLOP.equals(realMsg.getLOPViewSearch())) {
                    query.setCommissionChanged(Boolean.FALSE);
                } else if (CmFinoFIX.LOPViews_DiscountLOP.equals(realMsg.getLOPViewSearch())) {
                    query.setCommissionChanged(Boolean.TRUE);
                }
            }
            query.setId(realMsg.getIDSearch());

            List<LOP> results = dao.get(query);
            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                LOP s = results.get(i);

                // Here check for the LOP Expiration and if
                // applicable set it.
                lopService.checkAndSetExpiredStatus(s, dao);

                CMJSLOP.CGEntries entry =
                        new CMJSLOP.CGEntries();

                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSLOP.CGEntries[] entries = realMsg.getEntries();

            for (CMJSLOP.CGEntries e : entries) {
                LOP l = new LOP();
                updateEntity(l, e);
                dao.save(l);
                updateMessage(l, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }
}
   
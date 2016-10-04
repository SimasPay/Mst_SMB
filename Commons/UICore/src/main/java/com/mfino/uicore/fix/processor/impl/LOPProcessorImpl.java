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
import com.mfino.domain.SubscriberMdn;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSLOP;
import com.mfino.hibernate.Timestamp;
import com.mfino.i18n.MessageText;
import com.mfino.service.LOPService;
import com.mfino.service.UserService;
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
            lop.setGirorefid(e.getGiroRefID());
        }
        if (e.getActualAmountPaid() != null) {
            lop.setActualamountpaid(e.getActualAmountPaid());
        }
        if (e.getAmountDistributed() != null) {
            lop.setAmountdistributed(e.getAmountDistributed());
        }
        if (e.getApprovalTime() != null) {
            lop.setApprovaltime(e.getApprovalTime());
        }
        if (e.getApprovedBy() != null) {
            lop.setApprovedby(e.getApprovedBy());
        }
        if (e.getTransferDate() != null) {
            lop.setTransferdate(e.getTransferDate());
        }
        if (e.getDistributeTime() != null) {
            lop.setDistributetime(e.getDistributeTime());
        }
        if (e.getDistributedBy() != null) {
            lop.setDistributedby(e.getDistributedBy());
        }
        if (e.getCreateTime() != null) {
            lop.setCreatetime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            lop.setLastupdatetime(e.getLastUpdateTime());
        }
        if (e.getUpdatedBy() != null) {
            lop.setUpdatedby(e.getUpdatedBy());
        }
        if (e.getCreatedBy() != null) {
            lop.setCreatedby(e.getCreatedBy());
        }
        if (e.getComment() != null) {
            lop.setLopcomment(e.getComment());
        }
        if (e.getDistributorName() != null) {
            lop.getMerchant().setTradename(e.getDistributorName());
        }
        if(e.getCommission() != null) {
        	lop.setCommission(e.getCommission());
        }
    }

    private void updateMessage(LOP lop, CMJSLOP.CGEntries entry) {

        entry.setID(lop.getId().longValue());
        Set<CommodityTransfer> results = lop.getCommodityTransfers();
        if (results.size() > 0) {
            CommodityTransfer ct = (CommodityTransfer) results.toArray()[0];
            entry.setTransactionID(ct.getId().longValue());
        }
        if (lop.getStatus() != null) {
            entry.setStatus(lop.getStatus());
        }
        if (lop.getGirorefid() != null) {
            entry.setGiroRefID(lop.getGirorefid());
        }
        if (lop.getActualamountpaid() != null) {
            entry.setActualAmountPaid(lop.getActualamountpaid());
        }
        if (lop.getAmountdistributed() != null) {
            entry.setAmountDistributed(lop.getAmountdistributed());
        }
        if (lop.getApprovaltime() != null) {
            entry.setApprovalTime(lop.getApprovaltime());
        }
        if (lop.getApprovedby() != null) {
            entry.setApprovedBy(lop.getApprovedby());
        }
        if (lop.getTransferdate() != null) {

//            DateFormat df = new SimpleDateFormat("yyyyMM-dd");
//
//            try {
//                Date tdate = df.parse(lop.getTransferDate());
//                entry.setTransferDate(df.format(tdate));
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
            entry.setTransferDate(lop.getTransferdate());
        }
        if (lop.getDistributetime() != null) {
            entry.setDistributeTime(lop.getDistributetime());
        }
        if (lop.getDistributedby() != null) {
            entry.setDistributedBy(lop.getDistributedby());
        }
        if (lop.getCreatetime() != null) {
            entry.setCreateTime(lop.getCreatetime());
        }
        if (lop.getCreatedby() != null) {
            entry.setCreatedBy(lop.getCreatedby());
        }
        if (lop.getLastupdatetime() != null) {
            entry.setLastUpdateTime(lop.getLastupdatetime());
        }
        if (lop.getUpdatedby() != null) {
            entry.setUpdatedBy(lop.getUpdatedby());
        }
        if ((Long)lop.getVersion() != null) {
            entry.setRecordVersion(((Long)lop.getVersion()).intValue());
        }
        if (lop.getLopcomment() != null) {
            entry.setComment(lop.getLopcomment());
        }
		if (lop.getCommission() != null) {
			entry.setCommission(lop.getCommission());
		} else if (lop.getDistributionChainLvl() != null && 
				lop.getDistributionChainLvl().getCommission() != null) {
				entry.setCommission(lop.getDistributionChainLvl().getCommission());
		} else if (lop.getActualamountpaid() != null && lop.getAmountdistributed() != null) {
			log.info("commision is null for this lop:"+lop.getId());
			BigDecimal amountPaid = lop.getActualamountpaid();
			BigDecimal amountDistributed = lop.getAmountdistributed();
			BigDecimal commission = ZERO;
//			commission = ((amountDistributed - amountPaid) * 100.0) / (amountDistributed * 1.0);
			commission = (amountDistributed.subtract(amountPaid).multiply(HUNDREAD)).divide(amountDistributed);			
//			entry.setCommission(Math.round(commission) * 1.0);
			entry.setCommission(commission);
			
		}

        if (lop.getSubscriberMdn() != null) {
            if (lop.getSubscriberMdn().getId() != null) {
                entry.setMDNID(lop.getSubscriberMdn().getId().longValue());
            }
            if (lop.getSubscriberMdn().getMdn() != null) {
                entry.setMDN(lop.getSubscriberMdn().getMdn());
            }
        }
        if (lop.getMerchant().getSubscriber() != null) {
            if (lop.getMerchant().getSubscriber().getMfinoUserBySubscriberuserid() != null) {
                entry.setUsername(lop.getMerchant().getSubscriber().getMfinoUserBySubscriberuserid().getUsername());
            }
        }
        if (lop.getMerchant().getSubscriber() != null) {
            entry.setSubscriberID(lop.getMerchant().getId().longValue());
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
                    Merchant m = lop.getMerchant();
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
                SubscriberMdn subMdn = (SubscriberMdn)sub.getSubscriberMdns().toArray()[0];
                query.setMdnid(subMdn.getId().longValue());
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
   
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.AddressDAO;
import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.CardInfoQuery;
import com.mfino.dao.query.SubscriberMdnQuery;
import com.mfino.domain.Address;
import com.mfino.domain.CardInfo;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.domain.User;
import com.mfino.exceptions.AddressLine1RequiredException;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSCardInfo;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.EnumTextService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.web.WebContextError;

/**
 *
 * @author ADMIN
 */
public class CardInfoProcessor extends BaseFixProcessor {

    private AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
    private Logger log = LoggerFactory.getLogger(this.getClass());

    public CFIXMsg process(CFIXMsg msg) throws Exception {

        CMJSCardInfo realMsg = (CMJSCardInfo) msg;

        CardInfoDAO cardInfoDao = DAOFactory.getInstance().getCardInfoDAO();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSCardInfo.CGEntries[] entries = realMsg.getEntries();

            for (CMJSCardInfo.CGEntries entry : entries) {
                CardInfo cardInfo = cardInfoDao.getById(entry.getID());
                if (!entry.getRecordVersion().equals(cardInfo.getVersion())) {
                    handleStaleDataException();
                }
                if (cardInfo != null) {
                    // no need of this check
					cardUpdateEntity(cardInfo, entry, true);
                    cardInfoDao.save(cardInfo);
                    updateMessage(cardInfo, entry); //to show the udpated details to the user.
                } else {
                    CMJSError err = new CMJSError();
                    err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    err.setErrorDescription(MessageText._("Invalid details"));
                    return err;
                }
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            CardInfoQuery query = new CardInfoQuery();
            if (null != realMsg.getIDSearch()) {
                query.setId(realMsg.getIDSearch());
            }
            if (null != realMsg.getSubscriberUserIDSearch()) {
                UserDAO dao = DAOFactory.getInstance().getUserDAO();
				User user = dao.getById(realMsg.getSubscriberUserIDSearch()); 
				// SubscriberUserID is same as userid
                Set<Subscriber> sub = user.getSubscriberFromSubscriberUserID();
                if (!sub.isEmpty()) {
                    for (Subscriber record : sub) {
                        query.setSubscriber(record);
                    }
                }else{
                	SubscriberMDNDAO subscriberMDNDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
                	SubscriberMdnQuery subquery = new SubscriberMdnQuery();
                	int index=-1;
                	 if(user.getStatus().equals(CmFinoFIX.UserStatus_Expired)){
                		index=user.getUsername().indexOf(UserDAO.EXPIRY_TAG);
                	}           		
            		if(index!=-1){    
            		String	usermdn=user.getUsername().substring(0,index);
            		subquery.setMdn(usermdn);
            		subquery.setCreateTimeLT(realMsg.getCreationDateStartTime());
            		subquery.setLastUpdateTimeGE(realMsg.getCreationDateEndTime());
            		List<SubscriberMDN> subscriberMDNs = subscriberMDNDAO.get(subquery);
            		if (!subscriberMDNs.isEmpty()) {
                        for (SubscriberMDN record : subscriberMDNs) {
                        	if( record !=null && record.getSubscriber()!=null && isSameMDN(record.getMDN(),usermdn)){
                            query.setSubscriber(record.getSubscriber());
                            break;
                        	}     	
                        }
                      }else {
                  		return realMsg;
          			}
            		}else {
                		return realMsg;
        			}            		
                }
            }
            
            query.setCreateTimeGE(realMsg.getCreationDateStartTime());
            query.setCreateTimeLT(realMsg.getCreationDateEndTime());
//            query.setShowBothConfirmAndActiveCards(Boolean.TRUE);
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());
            
            List<CardInfo> results = cardInfoDao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                CardInfo cardInfo = results.get(i);
                CMJSCardInfo.CGEntries entry = new CMJSCardInfo.CGEntries();

                updateMessage(cardInfo, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());

		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg
				.getaction())) {
            CMJSCardInfo.CGEntries[] entries = realMsg.getEntries();

            for (CMJSCardInfo.CGEntries e : entries) {

                CardInfo firstCardInfo = new CardInfo();
                firstCardInfo.setCardStatus(CmFinoFIX.UserStatus_Registered);
				cardUpdateEntity(firstCardInfo, e, false);
                cardInfoDao.save(firstCardInfo);

                updateMessage(firstCardInfo, e);
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }

        return realMsg;
    }

    private boolean isSameMDN(String mdn, String usermdn) {
		if(mdn.equals(usermdn)){
			return true;
		}else if(mdn.contains("R") && usermdn.equals(mdn.substring(0, mdn.indexOf("R")))){
			return true;			
		}
		return false;
	}
    
    public void updateMessage(CardInfo cardInfo, CMJSCardInfo.CGEntries e) {
        e.setID(cardInfo.getID());
        e.setCardAddressID(cardInfo.getAddress().getID());
        if (cardInfo.getAddress().getCity() != null) {
            e.setCardCity(cardInfo.getAddress().getCity());
        }
        if (cardInfo.getAddress().getCountry() != null) {
            e.setCardCountry(cardInfo.getAddress().getCountry());
        }
        if (cardInfo.getCardF6() != null) {
            e.setCardF6(cardInfo.getCardF6());
        }
        if (cardInfo.getIssuerName() != null) {
            e.setCardIssuerName(cardInfo.getIssuerName());
        }
		if (cardInfo.getCardL4() != null) {
			e.setCardL4(cardInfo.getCardL4());
        }
       
        if (cardInfo.getNameOnCard() != null) {
            e.setCardNameOnCard(cardInfo.getNameOnCard());
        }
        if (cardInfo.getSubscriber() != null) {
            e.setSubscriberID(cardInfo.getSubscriber().getID());
        }
        if(cardInfo.getPocket()!=null)
        	e.setCardPocketID(cardInfo.getPocket().getID());
        
        if (cardInfo.getAddress().getLine1() != null) {
            e.setCardLine1(cardInfo.getAddress().getLine1());
        }
        if (cardInfo.getAddress().getLine2() != null) {
            e.setCardLine2(cardInfo.getAddress().getLine2());
        }
        if (cardInfo.getAddress().getRegionName() != null) {
            e.setCardRegionName(cardInfo.getAddress().getRegionName());
        }
        if (cardInfo.getAddress().getCity() != null) {
            e.setCardCity(cardInfo.getAddress().getCity());
        }
        if (cardInfo.getAddress().getState() != null) {
            e.setCardState(cardInfo.getAddress().getState());
        }
        if (cardInfo.getAddress().getCountry() != null) {
            e.setCardCountry(cardInfo.getAddress().getCountry());
        }
        if (cardInfo.getAddress().getZipCode() != null) {
            e.setCardZipCode(cardInfo.getAddress().getZipCode());
        }
        if (cardInfo.getAddressByBillingAddressID().getLine1() != null) {
            e.setCardBillingLine1(cardInfo.getAddressByBillingAddressID().getLine1());
        }
        if (cardInfo.getAddressByBillingAddressID().getLine2() != null) {
            e.setCardBillingLine2(cardInfo.getAddressByBillingAddressID().getLine2());
        }
        if (cardInfo.getAddressByBillingAddressID().getRegionName() != null) {
            e.setCardBillingRegionName(cardInfo.getAddressByBillingAddressID().getRegionName());
        }
        if (cardInfo.getAddressByBillingAddressID().getCity() != null) {
            e.setCardBillingCity(cardInfo.getAddressByBillingAddressID().getCity());
        }
        if (cardInfo.getAddressByBillingAddressID().getState() != null) {
            e.setCardBillingState(cardInfo.getAddressByBillingAddressID().getState());
        }
        if (cardInfo.getAddressByBillingAddressID().getCountry() != null) {
            e.setCardBillingCountry(cardInfo.getAddressByBillingAddressID().getCountry());
        }
        if (cardInfo.getAddressByBillingAddressID().getZipCode() != null) {
            e.setCardBillingZipCode(cardInfo.getAddressByBillingAddressID().getZipCode());
        }
        if(cardInfo.getCardStatus()!=null)
        	e.setCardStatusText(EnumTextService.getEnumTextValue(CmFinoFIX.TagID_CardStatus, CmFinoFIX.Language_English, cardInfo.getCardStatus()));
        	
        e.setRecordVersion(cardInfo.getVersion());
        e.setLastUpdateTime(cardInfo.getLastUpdateTime());
        e.setUpdatedBy(cardInfo.getUpdatedBy());
        e.setCreatedBy(cardInfo.getCreatedBy());
        e.setCreateTime(cardInfo.getCreateTime());
    }

	private void cardUpdateEntity(CardInfo cardInfo,CMJSCardInfo.CGEntries entry, boolean isUpdate) throws Exception {

        SubscriberDAO subsDao = DAOFactory.getInstance().getSubscriberDAO();
        if (entry.getSubscriberID() != null) {
			cardInfo.setSubscriber(subsDao.getById(entry.getSubscriberID()));
        }
        PocketDAO pocketDao = DAOFactory.getInstance().getPocketDAO();

        if (entry.getCardPocketID() != null) {
			cardInfo.setPocket(pocketDao.getById(entry.getCardPocketID()));
        }
        if (entry.getCardF6() != null) {
			if (isUpdate) {
				if (cardInfo.getCardF6() != null) {
					cardInfo.setOldCardF6(cardInfo.getCardF6());
        }
			}else {
					cardInfo.setOldCardF6(entry.getCardF6());
        }
			cardInfo.setCardF6(entry.getCardF6());
		}
		if (entry.getCardL4() != null) {
			if (isUpdate) {
				if (cardInfo.getCardL4() != null) {
					cardInfo.setOldCardL4(cardInfo.getCardL4());
				} 
			}else {
					cardInfo.setOldCardL4(entry.getCardL4());
			}
			cardInfo.setCardL4(entry.getCardL4());
		}
        if (StringUtils.isNotBlank(entry.getCardIssuerName())) {
			if (isUpdate) {
				if (cardInfo.getIssuerName() != null) {
					cardInfo.setOldIssuerName(cardInfo.getIssuerName());
        }
			}else {
					cardInfo.setOldIssuerName(entry.getCardIssuerName());
				}
		cardInfo.setIssuerName(entry.getCardIssuerName());
		}
        if (StringUtils.isNotBlank(entry.getCardNameOnCard())) {
			if (isUpdate) {
				if (cardInfo.getNameOnCard() != null) {
					cardInfo.setOldNameOnCard(cardInfo.getNameOnCard());
        }
			}else {
					cardInfo.setOldNameOnCard(entry.getCardNameOnCard());
				}
					cardInfo.setNameOnCard(entry.getCardNameOnCard());
		}

		Address addr = cardInfo.getAddress();
		Address	oldaddr = cardInfo.getAddressByOldAddressID();
            if (addr == null) {
                addr = new Address();
			oldaddr = new Address(); 
			cardInfo.setAddress(addr);
			cardInfo.setAddressByOldAddressID(oldaddr);
            }

            if (StringUtils.isNotBlank(entry.getCardLine1())) {
			if (isUpdate) {
				if (addr.getLine1() != null) {
					oldaddr.setLine1(addr.getLine1());
				} 
			}
			else {
					oldaddr.setLine1(entry.getCardLine1());
				}
                addr.setLine1(entry.getCardLine1());
            }

           
			if (isUpdate) {
					oldaddr.setLine2(addr.getLine2());
				}else {
					oldaddr.setLine2(entry.getCardLine2());
			}
                addr.setLine2(entry.getCardLine2());
            

            if (StringUtils.isNotBlank(entry.getCardCity())) {
			if (isUpdate) {
				if (addr.getCity() != null) {
					oldaddr.setCity(addr.getCity());
				}
			}else {
					oldaddr.setCity(entry.getCardCity());
			}
                addr.setCity(entry.getCardCity());
            }
            
            if (StringUtils.isNotBlank(entry.getCardRegionName())) {
			if (isUpdate) {
				if (addr.getRegionName() != null) {
					oldaddr.setRegionName(addr.getRegionName());
				}
			}else {
					oldaddr.setRegionName(entry.getCardRegionName());
			}
                addr.setRegionName(entry.getCardRegionName());
            }

            if (StringUtils.isNotBlank(entry.getCardState())) {
			if (isUpdate) {
				if (addr.getState() != null) {
					oldaddr.setState(addr.getState());
				}
			}else {
					oldaddr.setState(entry.getCardState());
			}
                addr.setState(entry.getCardState());
            }

            if (StringUtils.isNotBlank(entry.getCardCountry())) {
			if (isUpdate) {
				if (addr.getCountry() != null) {
					oldaddr.setCountry(addr.getCountry());
				}
			}else {
					oldaddr.setCountry(entry.getCardCountry());
			}
                addr.setCountry(entry.getCardCountry());
            }
            if (StringUtils.isNotBlank(entry.getCardZipCode())) {
			if (isUpdate) {
				if (addr.getZipCode() != null) {
					oldaddr.setZipCode(addr.getZipCode());
				}
			}else {
					oldaddr.setZipCode(entry.getCardZipCode());
			}
                addr.setZipCode(entry.getCardZipCode());
            }
		Address billingaddr = cardInfo.getAddressByBillingAddressID();
		Address oldbillingaddr = cardInfo.getAddressByOldBillingAddressID();
		if (billingaddr == null) {
			billingaddr = new Address();
			oldbillingaddr = new Address();
			cardInfo.setAddressByBillingAddressID(billingaddr);
			cardInfo.setAddressByOldBillingAddressID(oldbillingaddr);
		}
		if (StringUtils.isNotBlank(entry.getCardBillingLine1())) {
			if (isUpdate) {
				if (billingaddr.getLine1() != null) {
					oldbillingaddr.setLine1(billingaddr.getLine1());
				}
			}else {
				oldbillingaddr.setLine1(entry.getCardBillingLine1());
			}
			billingaddr.setLine1(entry.getCardBillingLine1());
		}

		if (StringUtils.isNotBlank(entry.getCardBillingLine2())) {
			if (isUpdate) {
					oldbillingaddr.setLine2(billingaddr.getLine2());
				}else {
				oldbillingaddr.setLine2(entry.getCardBillingLine2());
			}
			billingaddr.setLine2(entry.getCardBillingLine2());
		}

		if (StringUtils.isNotBlank(entry.getCardBillingCity())) {
			if (isUpdate) {
				if (billingaddr.getCity()!= null) {
					oldbillingaddr.setCity(billingaddr.getCity());
				}
			}else {
				oldbillingaddr.setCity(entry.getCardBillingCity());
			}
			billingaddr.setCity(entry.getCardBillingCity());
		}

		if (StringUtils.isNotBlank(entry.getCardBillingRegionName())) {
			if (isUpdate) {
				if (billingaddr.getRegionName() != null) {
					oldbillingaddr.setRegionName(billingaddr.getRegionName());
				}
			}else {
				oldbillingaddr.setRegionName(entry.getCardBillingRegionName());
			}
			billingaddr.setRegionName(entry.getCardBillingRegionName());
		}

		if (StringUtils.isNotBlank(entry.getCardBillingState())) {
			if (isUpdate) {
				if (billingaddr.getState()!= null) {
					oldbillingaddr.setState(billingaddr.getState());
				}
			}else {
				oldbillingaddr.setState(entry.getCardBillingState());
			}
			billingaddr.setState(entry.getCardBillingState());
		}

		if (StringUtils.isNotBlank(entry.getCardBillingCountry())) {
			if (isUpdate) {
				if (billingaddr.getCountry() != null) {
					oldbillingaddr.setCountry(billingaddr.getCountry());
				}
			}else {
				oldbillingaddr.setCountry(entry.getCardBillingCountry());
			}
			billingaddr.setCountry(entry.getCardBillingCountry());
		}
		if (StringUtils.isNotBlank(entry.getCardBillingZipCode())) {
			if (isUpdate) {
				if (billingaddr.getZipCode() != null) {
					oldbillingaddr.setZipCode(billingaddr.getZipCode());
				}
			}else {
				oldbillingaddr.setZipCode(entry.getCardBillingZipCode());
			}
			billingaddr.setZipCode(entry.getCardBillingZipCode());
		}
		if(isUpdate){
			cardInfo.setisConformationRequired(true);
		}

            try {
                addressDAO.save(addr);
			addressDAO.save(billingaddr);
			addressDAO.save(oldaddr);
			addressDAO.save(oldbillingaddr);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
                handleAddressException();
            }
        }

    private void handleAddressException() throws AddressLine1RequiredException {
        CmFinoFIX.CMJSError error = new CmFinoFIX.CMJSError();
        error.setErrorCode(CmFinoFIX.ErrorCode_Generic);
		error.setErrorDescription(MessageText
				._("Address ,BillingAddress Required"));
        WebContextError.addError(error);
		throw new AddressLine1RequiredException(MessageText
				._(" Address,BillingAddress Required "));
    }

	// private void logCardAddress(CMJSCardInfo.CGEntries entry) {
	// log.info("Card Address :");
	// log.info("CardLine1 = " + entry.getCardLine1());
	// log.info("CardLine2 = " + entry.getCardLine2());
	// log.info("CardState = " + entry.getCardState());
	// log.info("CardCity = " + entry.getCardCity());
	// log.info("CardCountry = " + entry.getCardCountry());
	// log.info("CardZipCode = " + entry.getCardZipCode());
	// log.info("Billing Address:");
	// log.info("BillingCardLine1 = " + entry.getCardBillingLine1());
	// log.info("BillingCardLine2 = " + entry.getCardBillingLine2());
	// log.info("BillingCardState = " + entry.getCardBillingState());
	// log.info("BillingCardCity = " + entry.getCardBillingCity());
	// log.info("BillingCardCountry = " + entry.getCardBillingCountry());
	// log.info("CardZipCode = " + entry.getCardBillingZipCode());
	// }
    }

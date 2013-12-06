/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CardInfoDAO;
import com.mfino.dao.CompanyDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.UserDAO;
import com.mfino.dao.query.CardInfoQuery;
import com.mfino.domain.CardInfo;
import com.mfino.domain.Company;
import com.mfino.domain.CreditCardDestinations;
import com.mfino.domain.Subscriber;
import com.mfino.fix.CmFinoFIX;
import com.mfino.service.EnumTextService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.MfinoUtil;

/**
 *
 * @author ADMIN
 */
public class CreditCardRegistrationReport extends OfflineReportBase {

    private static final int NUM_COLUMNS = 25+ConfigurationUtil.getCCDestinationLimit();
    private static String destinationHeader=" DestinationMDN1";
    static{
    	for(int i=2;i<ConfigurationUtil.getCCDestinationLimit();i++)
    		destinationHeader=destinationHeader+", DestinationMDN"+i;
    }
    private static final String HEADER_ROW = "#, Customer MDN, Customer FullName, Customer ID Number, Customer Email, Date of Birth, " +destinationHeader+
            ", HomePhone, WorkPhone, First 6 Digits, Last 4 Digits, Issuer Bank Name, Name on Card, Address Line1, Address Line2,Address City, " +
            " Address State, Address Zipcode, Billing Address Line1, Billing Address Line2, Billing Address City, " +
            "Billing Address State, Billing Address Zipcode, Status, Registration Date, Confirmation Date, Activation Date, Rejection Date, " +
            "Expiry Date, LastUpdateDate ";

    @Override
    public String getReportName() {
        return "CreditCardRegistrationReport";
    }
    @Override
    public File run(Date start, Date end)
    {
        return run(start, end, null);
    }
    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = null;

        CardInfoDAO cardDAO = DAOFactory.getInstance().getCardInfoDAO();

        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int seq = 1;

            CardInfoQuery query = new CardInfoQuery();
            query.setCreateTimeGE(start);
            query.setCreateTimeLT(end);
            query.setShowBothRegisteredAndActiveCards(Boolean.TRUE);

            if (companyID != null) {
                CompanyDAO companyDao = DAOFactory.getInstance().getCompanyDAO();
                Company company = companyDao.getById(companyID);
                if (company != null) {
                    query.setCompany(company);
                }
                reportFile = getReportFilePath(company);
            }
            else {
                reportFile = getReportFilePath();
            }
            List<CardInfo> results = cardDAO.get(query);

            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            writer.println(HEADER_ROW);

            for (CardInfo cardInfo : results) {
                DateFormat df = getDateFormat();
                DateFormat dobdf = new SimpleDateFormat("dd/MM/yyyy");
            	

                String firstName = StringUtils.EMPTY;
                String lastName = StringUtils.EMPTY;
                Long idNumber = null;
                String email = StringUtils.EMPTY;
                Date dob = null;
                String destinations = "";
                String mdn = StringUtils.EMPTY;
                String first6 = null;
                String last3 = null;
                String bankName = StringUtils.EMPTY;
                String cardName = StringUtils.EMPTY;
                String line1 = StringUtils.EMPTY;
                String line2 = StringUtils.EMPTY;
                String city = StringUtils.EMPTY;
                String region = StringUtils.EMPTY;
                String state = StringUtils.EMPTY;
                String zipcode = StringUtils.EMPTY;
                String billline1 = StringUtils.EMPTY;
                String billline2 = StringUtils.EMPTY;
                String billcity = StringUtils.EMPTY;
                String billregion = StringUtils.EMPTY;
                String billstate = StringUtils.EMPTY;
                String billzipcode = StringUtils.EMPTY;
                Integer status = null;
                Date registrationDate = null;
                Date confirmationDate = null;
                Date activationDate = null;
                Date rejectionDate = null;
                Date expiryDate = null;
                Date lastUpdateTime = null;
                String homePhone = null;
                String workPhone = null;
                if (cardInfo.getSubscriber() != null && cardInfo.getSubscriber().getUserBySubscriberUserID() != null) {
                    firstName = cardInfo.getSubscriber().getUserBySubscriberUserID().getFirstName();
                    lastName = cardInfo.getSubscriber().getUserBySubscriberUserID().getLastName();
                    idNumber = cardInfo.getSubscriber().getUserBySubscriberUserID().getID();
                    email = cardInfo.getSubscriber().getUserBySubscriberUserID().getEmail();
                    dob = cardInfo.getSubscriber().getUserBySubscriberUserID().getDateOfBirth();
                    homePhone = cardInfo.getSubscriber().getUserBySubscriberUserID().getHomePhone();
                    workPhone = cardInfo.getSubscriber().getUserBySubscriberUserID().getWorkPhone();
                    status = cardInfo.getCardStatus();

                    registrationDate = cardInfo.getSubscriber().getUserBySubscriberUserID().getCreateTime();
                    confirmationDate = cardInfo.getSubscriber().getUserBySubscriberUserID().getConfirmationTime();
                    activationDate = cardInfo.getSubscriber().getUserBySubscriberUserID().getUserActivationTime();
                    rejectionDate = cardInfo.getSubscriber().getUserBySubscriberUserID().getRejectionTime();
                    expiryDate = cardInfo.getSubscriber().getUserBySubscriberUserID().getExpirationTime();
                    lastUpdateTime = cardInfo.getSubscriber().getUserBySubscriberUserID().getLastUpdateTime();
                    Subscriber sub = cardInfo.getSubscriber();
                    Set<CreditCardDestinations> creditCardDestinations = sub.getCreditCardDestinationsFromSubscriberID();
                   int i=1;
                    for(CreditCardDestinations credDestinations : creditCardDestinations){
                    	if(credDestinations.getCreateTime().getTime()>start.getTime()&&credDestinations.getCreateTime().getTime()<end.getTime()){
                    		if(i<ConfigurationUtil.getCCDestinationLimit()-1)
                    		destinations = destinations+credDestinations.getDestMDN()+",";
                    		else{
                    			destinations = destinations+credDestinations.getDestMDN();
                    		}
                    		i++;
                    	}
                    }
                    for(;i<ConfigurationUtil.getCCDestinationLimit();i++){
                    	if(i<ConfigurationUtil.getCCDestinationLimit()-1)
                    		destinations = destinations+" ,";
                    		else{
                    			destinations = destinations+" ";
                    		}
                    }
                }
                if (cardInfo.getPocket() != null && cardInfo.getPocket().getSubscriberMDNByMDNID() != null) {
                    mdn = cardInfo.getPocket().getSubscriberMDNByMDNID().getMDN();
                 }else{
                	 mdn = cardInfo.getSubscriber().getUserBySubscriberUserID().getUsername();
                	 int index = mdn.indexOf(UserDAO.EXPIRY_TAG);
                	 if(index!=-1)
                		 mdn=mdn.substring(0,index);
                	 
                 }
                first6 = cardInfo.getCardF6();
                last3 = cardInfo.getCardL4();
                bankName = cardInfo.getIssuerName();
                cardName = cardInfo.getNameOnCard();
                if (cardInfo.getAddress() != null) {
                    line1 = cardInfo.getAddress().getLine1();
                    line2 = cardInfo.getAddress().getLine2();
                    city = cardInfo.getAddress().getCity();
                    region = cardInfo.getAddress().getRegionName();
                    state = cardInfo.getAddress().getState();
                    zipcode = cardInfo.getAddress().getZipCode();
                }
                if (cardInfo.getAddressByBillingAddressID() != null) {
                    billline1 = cardInfo.getAddressByBillingAddressID().getLine1();
                    billline2 = cardInfo.getAddressByBillingAddressID().getLine2();
                    billcity = cardInfo.getAddressByBillingAddressID().getCity();
                    billregion = cardInfo.getAddressByBillingAddressID().getRegionName();
                    billstate = cardInfo.getAddressByBillingAddressID().getState();
                    billzipcode = cardInfo.getAddressByBillingAddressID().getZipCode();
                }
                
                writer.println(String.format(getFormatString(NUM_COLUMNS),
                        seq,
                        mdn,
                        MfinoUtil.replaceCommasWithSemicolons(firstName)+"  "+MfinoUtil.replaceCommasWithSemicolons(lastName),
                        (idNumber != null) ? idNumber : StringUtils.EMPTY,
                        email,
                        (dob != null) ? dobdf.format(dob) : StringUtils.EMPTY,
                        destinations,
                        homePhone,
                        workPhone,
                        (first6 != null) ? first6 : StringUtils.EMPTY,
                        (last3 != null) ? last3 : StringUtils.EMPTY,
                        bankName,
                        cardName,
                        MfinoUtil.replaceCommasWithSemicolons(line1),
                        MfinoUtil.replaceCommasWithSemicolons(line2),
                        MfinoUtil.replaceCommasWithSemicolons(city),
//                        MfinoUtil.replaceCommasWithSemicolons(region),
                        MfinoUtil.replaceCommasWithSemicolons(state),
                        zipcode,
                        MfinoUtil.replaceCommasWithSemicolons(billline1),
                        MfinoUtil.replaceCommasWithSemicolons(billline2),
                        MfinoUtil.replaceCommasWithSemicolons(billcity),
//                        MfinoUtil.replaceCommasWithSemicolons(billregion),
                        MfinoUtil.replaceCommasWithSemicolons(billstate),
                        billzipcode,
                        (status != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_UserStatus, null, status) : StringUtils.EMPTY,
                        (registrationDate != null) ? df.format(registrationDate) : StringUtils.EMPTY,
                        (confirmationDate != null) ? df.format(confirmationDate) : StringUtils.EMPTY,
                        (activationDate != null) ? df.format(activationDate) : StringUtils.EMPTY,
                        (rejectionDate != null) ? df.format(rejectionDate) : StringUtils.EMPTY,
                        (expiryDate != null) ? df.format(expiryDate) : StringUtils.EMPTY,
                        (lastUpdateTime != null) ? df.format(lastUpdateTime) : StringUtils.EMPTY));
                seq++;
            }
            writer.close();
        } catch (Throwable t) {
            log.error("Error in CreditCard Registration Report", t);
        }
        finally{
            HibernateUtil.getCurrentSession().getTransaction().rollback();
        }
        return reportFile;
    }
}

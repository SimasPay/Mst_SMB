/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.dao;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;

import com.mfino.constants.GeneralConstants;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMdn;
import com.mfino.domain.User;
import com.mfino.fix.CmFinoFIX;

/**
 *
 * @author sandeepjs
 */
public class MerchantDAO extends BaseDAO<Merchant> {

    public static final String SUBSCRIBER_TABLE_NAME = "Subscriber";
    public static final String SubscriberMdn_ASSOC_NAME = "SubscriberMdnFromSubscriberID";
    public static final String FIX_MESSAGE_CLASS_NAME = "com.mfino.fix.CmFinoFIX$CMJSMerchant";

    public List<Merchant> get(MerchantQuery query) {
        return getByHQL(query);
    }

    public List<Merchant> getByHQL(MerchantQuery query) {

        String selectClause = "SELECT merchant ";
        String selectCountClause = "SELECT count(*) ";

        String fromClause = "FROM Merchant as merchant ";
        String joinSubscriberClause = "JOIN merchant.Subscriber as subscriber";
        String joinSubscriberMdnClause = "JOIN merchant.Subscriber.SubscriberMdnFromSubscriberID as SubscriberMdn";
        String joinUserClause = "JOIN merchant.Subscriber.User as user";
        String joinMSPClause = "JOIN merchant.Subscriber.User.MfinoServiceProviderByMSPID as MSP";
        //String orderClause = " order by subscriber.LastName asc ";

        ArrayList<String> whereClauses = new ArrayList<String>();
        ArrayList<String> joinClauses = new ArrayList<String>();

        if (query.getId() != null) {
            String idClause = "merchant." + Merchant.FieldName_RecordID + " = " + query.getId();
            whereClauses.add(idClause);
        }

//        // Search by Mechant Status
//        if (query.getStatus() != null) {
//            String statsClause = "merchant." + Merchant.FieldName_SubscriberStatus + " = " + query.getStatus();
//            whereClauses.add(statsClause);
//        }

        if (query.getParentID() != null) {
            String parentIDClause = "merchant." + Merchant.FieldName_MerchantByParentID + " = " + query.getParentID();
            whereClauses.add(parentIDClause);
        }

        if (query.getExactGroupID() != null) {
            String groupIDClause = "merchant." + Merchant.FieldName_GroupID + " = '" + query.getExactGroupID() + "'";
            whereClauses.add(groupIDClause);
        }

        // Search by self and parent
        if (query.getParentAndSelfID() != null) {
            String parentAndSelfClause = "((" + "merchant." + Merchant.FieldName_RecordID + " = " + query.getParentAndSelfID() + ") OR (" + "merchant." + Merchant.FieldName_MerchantByParentID + " = " + query.getParentAndSelfID() + "))";
            whereClauses.add(parentAndSelfClause);
        }


        // Search by start registration date.
        if (query.getStartRegistrationDate() != null) {
            String startRegTimeClause = "merchant." + Merchant.FieldName_CreateTime + " > :startdate";
            whereClauses.add(startRegTimeClause);
        }

        // Search by end registration date.
        if (query.getEndRegistrationDate() != null) {
            String endRegTimeClause = "merchant." + Merchant.FieldName_CreateTime + " < :enddate";
            whereClauses.add(endRegTimeClause);
        }

        if (query.getLastUpdateTimeGE() != null) {
            String lastUpdateTimeGEClause = "merchant." + Merchant.FieldName_LastUpdateTime + " >= :lastUpdateTimeGE";
            whereClauses.add(lastUpdateTimeGEClause);
        }


        if (query.getLastUpdateTimeLT() != null) {
            String lastUpdateTimeLTClause = "merchant." + Merchant.FieldName_LastUpdateTime + " < :lastUpdateTimeLT";
            whereClauses.add(lastUpdateTimeLTClause);
        }

        if (query.getStatusTimeGE() != null) {
            String statusTimeGEClause = "merchant." + Merchant.FieldName_StatusTime + " >= :statusTimeGE";
            whereClauses.add(statusTimeGEClause);
        }


        if (query.getStatusTimeLT() != null) {
            String statusTimeLTClause = "merchant." + Merchant.FieldName_StatusTime + " < :statusTimeLT";
            whereClauses.add(statusTimeLTClause);
        }

        if (query.getMerchantStatusIn() != null) {
//                boolean isBadRequest = false;
//
//                Integer[] statuses = query.getMerchantStatusIn();
//                if (statuses.length != 2) {
//                    isBadRequest = true;
//                }
////                if (isBadRequest) {
////                    throw new Exception("Bad query parameter for CommodityTransferDAO - SourceDestMDNAndID");
////                }

            String stClause = "merchant." + Subscriber.FieldName_SubscriberStatus + " in (:statusList)";
            whereClauses.add(stClause);
        }

        // Search by columns in Subscriber Table.
        if (query.getFirstName() != null || query.getLastName() != null || query.getMerchantStatus() != null || query.getMerchantRestrictions() != null || query.getCompany() != null) {

            joinClauses.add(joinSubscriberClause);

            // Search by first name.
            if (query.getFirstName() != null && query.getFirstName().length() > 0) {
                //String fnClause = "subscriber." + Subscriber.FieldName_FirstName + " like '%" + firstName + "%'";
                String fnClause = "subscriber." + Subscriber.FieldName_FirstName + " like :firstName";
                whereClauses.add(fnClause);
            }

            // Search by last name.
            if (query.getLastName() != null && query.getLastName().length() > 0) {
                //      String lnClause = "subscriber." + Subscriber.FieldName_LastName + " like '%" + query.getLastName() + "%'";
                String lnClause = "subscriber." + Subscriber.FieldName_LastName + " like :lastName";
                whereClauses.add(lnClause);
            }
            if (query.getCompany() != null) {
                String cpClause = "subscriber." + Subscriber.FieldName_Company + "=" + query.getCompany().getId();
                whereClauses.add(cpClause);
            }
            // Search by status.
            if (query.getMerchantStatus() != null) {
                String stClause = "merchant." + Subscriber.FieldName_SubscriberStatus + "=" + query.getMerchantStatus();
                whereClauses.add(stClause);
            }



            // Search by restrictions.
            if (query.getMerchantRestrictions() != null) {

                int rest = query.getMerchantRestrictions();
                if (rest > 0) {
                    String restClause = "bitwise_and(subscriber." + Subscriber.FieldName_SubscriberRestrictions + "," + query.getMerchantRestrictions() + ") > 0";
                    whereClauses.add(restClause);

                } else {
                    //When user restrictions equal
                    String restClause = "subscriber." + Subscriber.FieldName_SubscriberRestrictions + "=" + CmFinoFIX.SubscriberRestrictions_None;
                    whereClauses.add(restClause);
                }
            }
        }

        // Search by MDN.
        if (query.getMdn() != null && query.getMdn().length() > 0) {
            joinClauses.add(joinSubscriberMdnClause);
            String usClause = "SubscriberMdn." + SubscriberMdn.FieldName_MDN + " like '" + query.getMdn() + "%'";
            whereClauses.add(usClause);
        }

        if (query.getExactMDN() != null) {
            joinClauses.add(joinSubscriberMdnClause);
            String usClause = "SubscriberMdn." + SubscriberMdn.FieldName_MDN + "='" + query.getExactMDN() + "'";
            whereClauses.add(usClause);
        }

        // Search by UserName.
        if (query.getUserName() != null) {
            joinClauses.add(joinUserClause);
            joinClauses.add(joinMSPClause);
            //    String usClause = "user." + User.FieldName_Username + " like '%" + userName + "%'";
            String usClause = "user." + User.FieldName_Username + " like :userName and MSP." + MfinoServiceProvider.FieldName_RecordID + " = :mspid";
            whereClauses.add(usClause);
        }

        if (query.getExactUser() != null) {
            joinClauses.add(joinUserClause);
            joinClauses.add(joinMSPClause);
            // String usClause = "user." + User.FieldName_Username + "='" + query.getExactUser() + "'";
            String usClause = "user." + User.FieldName_Username + "= :exactUserName and MSP." + MfinoServiceProvider.FieldName_RecordID + " = :mspid";
            whereClauses.add(usClause);
        }

        if (!joinClauses.contains(joinSubscriberClause)) {
            joinClauses.add(joinSubscriberClause);
        }

//        if (!StringUtils.isEmpty(query.getFirstName())) {
//             orderClause+=", subscriber.FirstName asc ";
//        } else if (!StringUtils.isEmpty(query.getMdn())) {
//            orderClause+=", SubscriberMdn." + SubscriberMdn.FieldName_MDN;
//        } else if(!StringUtils.isEmpty(query.getUserName())) {
//            orderClause+=", user." + User.FieldName_Username ;
//        }

        // Here by default we have the Last Name Search.
        // But when we have the MDN given then order by MDN
        // then by First Name
        // then by Last Name
        // then by User Name
        // then if nthing is present then by Last Name.
        String orderClause = null;
        if (!StringUtils.isEmpty(query.getMdn())) {
            orderClause = "order by SubscriberMdn." + SubscriberMdn.FieldName_MDN + " asc ";
        } else if (!StringUtils.isEmpty(query.getFirstName())) {
            orderClause = "order by subscriber.FirstName asc ";
        } else if (!StringUtils.isEmpty(query.getLastName())) {
            orderClause = "order by subscriber.LastName asc ";
        } else if (!StringUtils.isEmpty(query.getUserName())) {
            orderClause = "order by user." + User.FieldName_Username + " asc ";
        } else if (query.isIDOrdered()) {
            orderClause = "order by merchant." + Merchant.FieldName_RecordID + " asc ";
        }
        // This is causing the system to be extremely slow so taking it out as it doesn't provide much value
//        else {
//            orderClause = " order by subscriber.LastName asc ";
//        }


        final String HQLQueryString = createHQLQueryString(fromClause, joinClauses, whereClauses, orderClause);

        String queryStringHQL = selectClause + HQLQueryString;
        String queryStringCountHQL = selectCountClause + HQLQueryString;

        Query queryCountObj = getQuery(queryStringCountHQL);
        // this is to handle the like results
        if (query.getFirstName() != null) {
            String firstName = query.getFirstName();
            queryCountObj.setString("firstName", firstName + "%");
        }
        if (query.getLastName() != null) {
            String lastName = query.getLastName();
            queryCountObj.setString("lastName", lastName + "%");
        }
        if (query.getUserName() != null) {
            String userName = query.getUserName();
            queryCountObj.setString("userName", userName + "%");
            queryCountObj.setBigInteger("mspid", new BigInteger("1"));
        }
        if (query.getExactUser() != null) {
            queryCountObj.setParameter("exactUserName", query.getExactUser());
            queryCountObj.setBigInteger("mspid", new BigInteger("1"));
        }
        if (query.getStartRegistrationDate() != null) {
            queryCountObj.setTimestamp("startdate", query.getStartRegistrationDate());
        }

        if (query.getEndRegistrationDate() != null) {
            //This is to make today's records return to the user.
            // Date endDatePlus1 = DateUtil.addDays(query.getEndRegistrationDate(), 1);
            queryCountObj.setTimestamp("enddate", query.getEndRegistrationDate());
        }


        if (query.getLastUpdateTimeGE() != null) {
            queryCountObj.setTimestamp("lastUpdateTimeGE", query.getLastUpdateTimeGE());
        }
        if (query.getLastUpdateTimeLT() != null) {
            queryCountObj.setTimestamp("lastUpdateTimeLT", query.getLastUpdateTimeLT());
        }

        if (query.getStatusTimeGE() != null) {
            queryCountObj.setTimestamp("statusTimeGE", query.getStatusTimeGE());
        }

        if (query.getStatusTimeLT() != null) {
            queryCountObj.setTimestamp("statusTimeLT", query.getStatusTimeLT());
        }

        if (query.getMerchantStatusIn() != null) {
            queryCountObj.setParameterList("statusList", query.getMerchantStatusIn());
        }

        Long count = (Long) queryCountObj.list().get(0);
        query.setTotal(count.intValue());

        Query queryObj = getQuery(queryStringHQL);

        if (query.getFirstName() != null) {
            String firstName = query.getFirstName();
            queryObj.setString("firstName", firstName + "%");
        }
        if (query.getLastName() != null) {
            String lastName = query.getLastName();
            queryObj.setString("lastName", lastName + "%");
        }
        if (query.getUserName() != null) {
            String userName = query.getUserName();
            queryObj.setString("userName", userName + "%");
            queryObj.setBigInteger("mspid", new BigInteger("1"));
        }
        if (query.getExactUser() != null) {
            queryObj.setParameter("exactUserName", query.getExactUser());
            queryObj.setBigInteger("mspid", new BigInteger("1"));
        }
        if (query.getStartRegistrationDate() != null) {
            queryObj.setTimestamp("startdate", query.getStartRegistrationDate());
        }

        if (query.getEndRegistrationDate() != null) {
            queryObj.setTimestamp("enddate", query.getEndRegistrationDate());
        }

        if (query.getLastUpdateTimeGE() != null) {
            queryObj.setTimestamp("lastUpdateTimeGE", query.getLastUpdateTimeGE());
        }

        if (query.getLastUpdateTimeLT() != null) {
            queryObj.setTimestamp("lastUpdateTimeLT", query.getLastUpdateTimeLT());
        }

        if (query.getStatusTimeGE() != null) {
            queryObj.setTimestamp("statusTimeGE", query.getStatusTimeGE());
        }

        if (query.getStatusTimeLT() != null) {
            queryObj.setTimestamp("statusTimeLT", query.getStatusTimeLT());
        }


        if (query.getMerchantStatusIn() != null) {
            queryObj.setParameterList("statusList", query.getMerchantStatusIn());
        }

        // Paging is below.
        if (query.getLimit() != null && query.getStart() != null) {
            queryObj.setMaxResults(query.getLimit());
            queryObj.setFirstResult(query.getStart());
        }
        @SuppressWarnings("unchecked")
        List<Merchant> results = queryObj.list();

        return results;
    }

    public HashMap getLevelandDCTIDFromParentID(Long merchantParentId) {

        long DCTID = -1;
        int level = 1;
        HashMap hm = new HashMap();
        Merchant merchant = getById(merchantParentId);

        while (merchant != null && merchant.getId().compareTo(new BigDecimal(0)) != 0 && merchant.getDistributionchaintemplateid() == null) {
            Merchant tempMerchant = merchant.getMerchant();
            if (tempMerchant == null) {
                break;
            } else {
                merchant = tempMerchant;
            }
            level++;
        }
        if (merchant != null && merchant.getId().compareTo(new BigDecimal(0)) != 0 && merchant.getDistributionchaintemplateid() != null) {
            DCTID = merchant.getDistributionchaintemplateid().longValue();
        } else {
            DCTID = -1;
            level = -1;
        }
        @SuppressWarnings("unchecked")
        Object justObject1 = hm.put("DCTID", DCTID);
        @SuppressWarnings("unchecked")
        Object justObject2 = hm.put("level", level);

        return hm;
    }

    private String createHQLQueryString(String fromClause, ArrayList<String> joinClauses, ArrayList<String> whereClauses, String orderClause) {

        String queryString = fromClause + GeneralConstants.SINGLE_SPACE;

        String joinString = GeneralConstants.EMPTY_STRING;
        for (int i = 0; i < joinClauses.size(); i++) {
            joinString = joinString + joinClauses.get(i) + GeneralConstants.SINGLE_SPACE;
        }

        String whrString = GeneralConstants.EMPTY_STRING;

        if (whereClauses.size() > 0) {
            whrString = " WHERE ";
            for (int i = 0; i < whereClauses.size(); i++) {

                if (i < whereClauses.size() - 1) {
                    whrString = whrString + whereClauses.get(i) + GeneralConstants.SINGLE_SPACE + " AND ";
                } else {
                    whrString = whrString + whereClauses.get(i) + GeneralConstants.SINGLE_SPACE;
                }
            }
        }

        queryString = queryString + joinString + whrString + (null != orderClause ? orderClause : "");
        return queryString;
    }

    public List<Merchant> getAllRecordsWhoHasNullOrZeroParentId(Company company) {
        String selectClause = "SELECT merchant ";
        String fromClause = "FROM Merchant as merchant, Subscriber as subscriber";

        ArrayList<String> whereClauses = new ArrayList<String>();
        ArrayList<String> joinClauses = new ArrayList<String>();

        String idClause = "subscriber." + Subscriber.FieldName_RecordID + "=" + "merchant." + Merchant.FieldName_RecordID;
        whereClauses.add(idClause);
        String companyClause = "subscriber." + Subscriber.FieldName_Company + " = :company";
        whereClauses.add(companyClause);
        String parentIdClause = "merchant.MerchantByParentID IS NULL OR merchant.MerchantByParentID=0";
        whereClauses.add(parentIdClause);

        final String HQLQueryString = createHQLQueryString(fromClause, joinClauses, whereClauses, "");

        String queryStringHQL = selectClause + HQLQueryString;

        Query queryObj = getQuery(queryStringHQL);
        queryObj.setEntity("company", company);

        @SuppressWarnings("unchecked")
        List<Merchant> results = (List<Merchant>) queryObj.list();
        return results;
    }

    public Integer getActiveMerchantCount(Date end, Long companyId) {
        String queryString = "Select count(1) from subscriber as s join merchant as m on s.id=m.id where s.companyId=:companyId and m.status != " +
                ":mstatus and m.createTime < :date";

        Query queryObj = getSQLQuery(queryString);

        queryObj.setLong("companyId", companyId);
        queryObj.setInteger("mstatus", CmFinoFIX.SubscriberStatus_Retired);
        queryObj.setTimestamp("date", end);

        @SuppressWarnings("unchecked")
        List<Object[]> list = queryObj.list();
        if(list.size() > 0) {
          Object obj = list.get(0);
          BigInteger count = (BigInteger) obj;
          return count.intValue();
        }
        return 0;
    }
}

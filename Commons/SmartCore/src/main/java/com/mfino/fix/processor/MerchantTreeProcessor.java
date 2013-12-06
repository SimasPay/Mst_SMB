/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix.processor;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSMerchantTree;
import com.mfino.service.MerchantTreeService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.service.UserService;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class MerchantTreeProcessor extends BaseFixProcessor {

    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSMerchantTree realMsg = (CMJSMerchantTree) msg;

        Long merchantId = realMsg.getIDSearch();

        if (null == realMsg.getTreeSearch()) {
            throw new Exception("The Tree Search Criteria is not found in the Message.");
        }

        Long nodeId = realMsg.getNodeIDSearch();
        Company company = UserService.getUserCompany();
        MerchantTreeService merchantTreeService = new MerchantTreeService();
        List<Merchant> merchantList = null;
        if (CmFinoFIX.TreeSearch_AllParents.equals(realMsg.getTreeSearch())) {
            // IF we reach here then do the tree search.
            merchantList = merchantTreeService.getAllParents(merchantId, nodeId, company);
        } else if (CmFinoFIX.TreeSearch_ImmediateChildren.equals(realMsg.getTreeSearch())) {
            merchantList = merchantTreeService.getAllChildren(merchantId, company);
        }

        // Here put all the Entries to the Message.
        realMsg.allocateEntries(merchantList.size());
        for (int i = 0; i < merchantList.size(); i++) {
            Merchant s = (Merchant) merchantList.get(i);
            CMJSMerchantTree.CGEntries entry = updateMessage(s);
            if (null == entry) {
                continue;
            }
            realMsg.getEntries()[i] = entry;
        }

        realMsg.setsuccess(CmFinoFIX.Boolean_True);
        return realMsg;
    }

    private CMJSMerchantTree.CGEntries updateMessage(Merchant merchant) {
        // 1. If the Merchant Id is not found then return null.
        // 2. If the Subscriber for this Merchant is not found then there is no
        //    point returning this merchant. Just LOG the Error and return null.
        // 3. If both the First Name and Last Name are null then
        //    We just get the Username and show it in brackets ().
        //    If the UserName is also null then we just put N/A.

        if (null == merchant.getID()) {
            // If we reach here then we dont have the Merchant ID.
            // We need the ID as this respresents the Node Id.
            log.error("Got a merchant without merchant id <" + merchant.toString() + ">");
            return null;
        }

        if (null == merchant.getSubscriber()) {
            log.error("Got a merchant with ID <" + merchant.getID() +
                    "> without subscriber entity.");
            return null;
        }

        CMJSMerchantTree.CGEntries entry =
                new CMJSMerchantTree.CGEntries();

        entry.setID(merchant.getID());

       // String firstName = merchant.getSubscriber().getFirstName();
       // String lastName = merchant.getSubscriber().getLastName();
        String userName = "N/A";

        if (null != merchant.getSubscriber().getUser()) {
            if (StringUtils.isNotBlank(merchant.getSubscriber().getUser().getUsername())) {
                userName = merchant.getSubscriber().getUser().getUsername();
            }
        }

//        entry.setText(firstName + " " + lastName + "  (" + userName + ")");
        //Smart want only user name to match what's in Trivnet
        entry.setText(userName);

        if (null != merchant.getStatus()) {
            entry.setSubscriberStatus(merchant.getStatus());
        }

        if (null != merchant.getSubscriber().getRestrictions()) {
            entry.setSubscriberRestrictions(merchant.getSubscriber().getRestrictions());
        }

        return entry;
    }
}

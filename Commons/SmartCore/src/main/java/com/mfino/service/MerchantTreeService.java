/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MerchantDAO;
import com.mfino.dao.query.MerchantQuery;
import com.mfino.domain.Company;
import com.mfino.domain.Merchant;
import com.mfino.exceptions.TreeCycleFoundException;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class MerchantTreeService extends BaseService<Merchant> {
	private Logger log = LoggerFactory.getLogger(this.getClass());
    public MerchantTreeService() {
        super();
    }

    public List<Merchant> getAllParents(Long merchantId, Long nodeId,Company company) throws TreeCycleFoundException {
        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();
        Long idToUse = merchantId;

        List<Merchant> merchants = new ArrayList<Merchant>();
        while (true) {
            MerchantQuery merchantQuery = new MerchantQuery();
            merchantQuery.setId(idToUse);
            merchantQuery.setCompany(company);

            List<Merchant> merchantsList = merchantDAO.getByHQL(merchantQuery);
            if (null == merchantsList || 0 == merchantsList.size()) {
                break;
            }

            Merchant eachMerchant = merchantsList.get(0);

            if (null == eachMerchant) {
                break;
            }
            // Check if this merchant is already in the list.
            if(merchants.contains(eachMerchant)) {
                String errorMessage = "The merchant " + eachMerchant.toString() + " is already present.";
                errorMessage += "Found Tree Cycle which may run into an infinite loop.";
                log.error(errorMessage);
                throw new TreeCycleFoundException("Invalid Data to display.");
            }

            merchants.add(eachMerchant);

            Long parentId = null;
            if(eachMerchant.getMerchantByParentID() != null){
                parentId = eachMerchant.getMerchantByParentID().getID();
            }

            if (null == parentId) {
                break;
            }

            if (nodeId.longValue() == parentId.longValue()) {
                break;
            }

            idToUse = parentId;
        }

        return merchants;
    }

    public List<Merchant> getAllChildren(Long merchantId, Company company) {
        MerchantDAO merchantDAO = DAOFactory.getInstance().getMerchantDAO();

        if (null == merchantId || 0 == merchantId) {
            return merchantDAO.getAllRecordsWhoHasNullOrZeroParentId(company);
        }

        MerchantQuery merchantQuery = new MerchantQuery();
        merchantQuery.setParentID(merchantId);
        merchantQuery.setCompany(company);

        List<Merchant> resultantMerchants = merchantDAO.getByHQL(merchantQuery);
        return resultantMerchants;
    }
}

/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSBillerDAO;
import com.mfino.dao.MFSBillerPartnerDAO;
import com.mfino.dao.query.MFSBillerPartnerQuery;
import com.mfino.dao.query.MFSBillerQuery;
import com.mfino.domain.MFSBiller;
import com.mfino.domain.MFSBillerPartner;
import com.mfino.domain.Partner;
import com.mfino.service.BillerService;

/**
 * Service which provides helper methods for creating billers or identifying the associated partner for billers.
 * 
 * @author Chaitanya
 *
 */
@Service("BillerServiceImpl")
public class BillerServiceImpl implements BillerService {
	private static Logger	        log	             = LoggerFactory.getLogger(BillerServiceImpl.class);

	/**
	 * Returns the partner from the billerCode 
	 * @param billerCode
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public Partner getPartner(String billerCode){
		Partner partner = null;
		//29Dec2011 - Assuming only there would be one partner associated with a biller code
		//There could be multiple biller codes associated with a single partner but not vice-versa
		//for now. The latter scenario need to be handled later.
		if(billerCode==null){
			log.error("The biller code is null");
			return partner;
		}
		MFSBillerDAO billerDAO = DAOFactory.getInstance().getMFSBillerDAO();
		MFSBillerQuery billerQuery = new MFSBillerQuery();
		billerQuery.setBillerCode(billerCode);
		List<MFSBiller> billersList = billerDAO.get(billerQuery);
		MFSBiller biller = null;
		if(billersList.size()==1){
			biller = billersList.get(0);
			log.info("Got a single biller with the billerCode: "+billerCode);

		}
		if(biller!=null){
			log.info("getting the partner from billerID: "+biller.getID());
			MFSBillerPartnerDAO billerPartnerDAO = DAOFactory.getInstance().getMFSBillerPartnerDAO();
			MFSBillerPartnerQuery billerPartnerQuery = new MFSBillerPartnerQuery();
			billerPartnerQuery.setMfsBillerId(biller.getID());
			List<MFSBillerPartner> billerPartners = billerPartnerDAO.get(billerPartnerQuery);
			if(billerPartners.size()==1){
				MFSBillerPartner billerPartner = billerPartners.get(0);
				partner = billerPartner.getPartner();
				log.info("got the partner with id: "+partner.getID()+"as the biller partner");
			}
		}
		return partner;
	}
	
}

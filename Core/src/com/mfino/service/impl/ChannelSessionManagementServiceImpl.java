package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelSessionManagementDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.ChannelSessionMgmt;
import com.mfino.service.ChannelSessionManagementService;
@Service("ChannelSessionManagementServiceImpl")
public class ChannelSessionManagementServiceImpl implements ChannelSessionManagementService{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * Gets the channelSessionManagement with the MDNID
	 * @param mdnID
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public ChannelSessionMgmt getChannelSessionManagemebtByMDNID(Long mdnID){
		log.info("ChannelSessionManagement : get"+mdnID);

		if(mdnID == null) return null;
		
		ChannelSessionManagementDAO csmDAO = DAOFactory.getInstance().getChannelSessionManagementDAO();
		
		return (csmDAO.getChannelSessionManagemebtByMDNID(mdnID)) ;
	}
	
	/**
	 * Saves the ChannelSessionManagement record to dataabase
	 * @param csm
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public void saveCSM(ChannelSessionMgmt csm){
		ChannelSessionManagementDAO csmDAO = DAOFactory.getInstance().getChannelSessionManagementDAO();
		csmDAO.save(csm);
	}
}

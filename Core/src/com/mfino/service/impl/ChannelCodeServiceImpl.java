package com.mfino.service.impl;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MfinoServiceProviderDAO;
import com.mfino.domain.ChannelCode;
import com.mfino.domain.MfinoServiceProvider;
import com.mfino.service.ChannelCodeService;
@Service("ChannelCodeServiceImpl")
public class ChannelCodeServiceImpl implements ChannelCodeService{
	private static Logger log = LoggerFactory.getLogger(ChannelCodeServiceImpl.class); 
	
	/**
	 * Calls the getByChannelSourceApplication method in the channelCodeDAO to return the channel code based
	 * on the sourceApplication
	 * @param channelSourceApplication
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ChannelCode getChannelCodebySourceApplication(Integer channelSourceApplication){
		ChannelCode channelCode = null;
		if(channelSourceApplication!=null){
			log.info("Getting the channel code record for sourceApplication: "+channelSourceApplication);
			ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao(); 
			channelCode = channelCodeDAO.getByChannelSourceApplication(channelSourceApplication);
		}
		return channelCode;		
	}
	/**
	 * Gets the channelCode from the getChannelCodebySourceApplication method and returns the channelName
	 * @param channelSourceApplication
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public String getChannelNameBySourceApplication(Integer channelSourceApplication){
		String channelName = null;
		ChannelCode channelCode = getChannelCodebySourceApplication(channelSourceApplication); 
		if(channelCode!=null){
			log.info("getting the channel name from channelCode: "+channelCode.getId());
			channelName = channelCode.getChannelname();
		}else{
			log.error("Channel code with sourceApplication : "+channelSourceApplication+" is null");
		}
		return channelName;
	}
	
	/**
	 * Gets the channelCode Object by the given channel code string
	 * @param channelCodeStr
	 * @return
	 */
	@Transactional(readOnly = false, propagation = Propagation.REQUIRED)
	public ChannelCode getChannelCodeByChannelCode(String channelCodeStr){
		ChannelCode channelCode = null;
		if(StringUtils.isNotBlank(channelCodeStr)){
			log.info("Getting the channel code record for channelCode: "+channelCodeStr);
			ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao(); 
			channelCode = channelCodeDAO.getByChannelCode(channelCodeStr);
		}
		
		return channelCode;
	}
	
	/**
	 * Gets the channelCode Object by the given channel code ID
	 * @param channelID
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public ChannelCode getChannelCodeByChannelId(Long channelID){
		ChannelCode channelCode = null;
		if(channelID!=null){
			log.info("Getting the channel code record for channelID: "+channelID);
			ChannelCodeDAO channelCodeDAO = DAOFactory.getInstance().getChannelCodeDao(); 
			channelCode = channelCodeDAO.getById(channelID);
		}
		
		return channelCode;
	}
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public MfinoServiceProvider getMFSPbyID(int id){
	
		MfinoServiceProviderDAO mspDao = DAOFactory.getInstance().getMfinoServiceProviderDAO();
		return mspDao.getById(id);
	}
}

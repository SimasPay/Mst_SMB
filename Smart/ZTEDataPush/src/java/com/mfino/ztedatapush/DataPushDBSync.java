package com.mfino.ztedatapush;

import java.util.Iterator;
import java.util.Set;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketDAO;
import com.mfino.dao.SubscriberDAO;
import com.mfino.dao.SubscriberMDNDAO;
import com.mfino.domain.Pocket;
import com.mfino.domain.Subscriber;
import com.mfino.domain.SubscriberMDN;
import com.mfino.fix.CmFinoFIX;

public class DataPushDBSync {
	
	 private Logger log = LoggerFactory.getLogger(this.getClass());
	 private Session session = null;
	
	public String retireSubscriber(String msisdn){
		log.info(String.format("DataPushDBSync::retireSubscriber() function called"));
		try{
			SubscriberMDNDAO smDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMDN sm = smDAO.getByMDN(msisdn);
			if(sm != null){
				log.info(String.format("DataPushDBSync::retireSubscriber(): SubscriberMDN Domain Object(ID:%d) exists corresponding to Msisdn(%s)",sm.getID(),msisdn));
				if(isSubscriberElgibleTobeRetired(sm)){
					log.info(String.format("DataPushDBSync::retireSubscriber(): Retiring SubscriberMDN Domain Object(ID:%d)",sm.getID()));
					retireSubscriberPockets(sm);
					Subscriber sub = sm.getSubscriber();
					int smStatus = sm.getStatus();
					sm.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					sm.setIsForceCloseRequested(new Boolean(false));
					int subStatus = sm.getStatus();
					sub.setStatus(CmFinoFIX.SubscriberStatus_PendingRetirement);
					smDAO.save(sm);
					SubscriberDAO subDAO = new SubscriberDAO();
					subDAO.save(sub);
					log.info(String.format("DataPushDBSync::retireSubscriber(): Changed the status of SubscriberMDN Domain Object(ID:%d) to Retired (%d -> %d)",sm.getID(),smStatus,sm.getStatus()));
					log.info(String.format("DataPushDBSync::retireSubscriber(): Changed the status of Subscriber Domain Object(ID:%d) to Retired (%d -> %d)",sub.getID(),subStatus,sub.getStatus()));
					return  "Successfully retired the Subscriber with msisdn:"+msisdn;
				}else{
					log.info(String.format("DataPushDBSync::retireSubscriber(): SubscriberMDN Domain Object(ID:%d) is not eligible to be Retired corresponding to MDN(%s)",sm.getID(),msisdn));
					return "Failed to retire subscriber with msisdn:"+msisdn+" due to some internal conditions";
				}
			}else{
				log.info(String.format("DataPushDBSync::retireSubscriber(): SubscriberMDN Domain Object doesn't exists corresponding to Msisdn(%s)",msisdn));
				return "Failed to retire Subscriber, as the subscriber doesnt exist corresponding to the msisdn:"+msisdn;
			}
		}catch(Exception e){
			log.error(String.format("Error Occured while retiring a subscriber with msisdn(%s)",msisdn),e);
			return "Failed to retire subscriber with msisdn:"+msisdn+" due to some internal error";
		}
	}
	
	
	private void retireSubscriberPockets(SubscriberMDN sm) {
		log.info(String.format("DataPushDBSync::retireSubscriberPockets() function called"));
		Set<Pocket> pokects = sm.getPocketFromMDNID();
		Iterator<Pocket> iterator = pokects.iterator();
		PocketDAO pocDAO = DAOFactory.getInstance().getPocketDAO();;
		while (iterator.hasNext()) {
			Pocket pocket = iterator.next();
			if (!pocket.getStatus().equals(CmFinoFIX.PocketStatus_Retired)) {
				pocket.setStatus(CmFinoFIX.PocketStatus_PendingRetirement);
				pocket.setIsDefault(false);
				pocDAO.save(pocket);
				log.info(String.format("DataPushDBSync::retireSubscriberPockets(): Changing the Status of Pocket(ID:%d) of Subscriber(MDN:%s) to Pending Retired",pocket.getID(),sm.getMDN()));
			}
		}
		log.info(String.format("DataPushDBSync::retireSubscriberPockets() function finished"));
	}
	
	private boolean isSubscriberElgibleTobeRetired(SubscriberMDN sm){
		//code should be written to check whether the subscriber is eligible to be retired or not
		return true;
	}
	
	public String suspendSubscriber(String msisdn){
		log.info(String.format("DataPushDBSync::suspendSubscriber() function called"));
		try{
			SubscriberMDNDAO smDAO = DAOFactory.getInstance().getSubscriberMdnDAO();
			SubscriberMDN sm = smDAO.getByMDN(msisdn);
			if(sm != null){
				log.info(String.format("DataPushDBSync::suspendSubscriber(): SubscriberMDN Domain Object(ID:%d) exists corresponding to Msisdn(%s)",sm.getID(),msisdn));
				if(isSubscriberElgibleTobeSuspended(sm)){
					log.info(String.format("DataPushDBSync::suspendSubscriber(): Retiring SubscriberMDN Domain Object(ID:%d)",sm.getID()));
					Subscriber sub = sm.getSubscriber();
					int smStatus = sm.getStatus();
					sm.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					sm.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					sm.setIsForceCloseRequested(new Boolean(false));
					int subStatus = sm.getStatus();
					sub.setStatus(CmFinoFIX.SubscriberStatus_Suspend);
					sub.setRestrictions(CmFinoFIX.SubscriberRestrictions_Suspended);
					smDAO.save(sm);
					SubscriberDAO subDAO = new SubscriberDAO();
					subDAO.save(sub);
					log.info(String.format("DataPushDBSync::suspendSubscriber(): Changed the status of SubscriberMDN Domain Object(ID:%d) to Suspended (%d -> %d)",sm.getID(),smStatus,sm.getStatus()));
					log.info(String.format("DataPushDBSync::suspendSubscriber(): Changed the status of Subscriber Domain Object(ID:%d) to Suspended (%d -> %d)",sub.getID(),subStatus,sub.getStatus()));
					return "Successfully suspended the Subscriber with msisdn:"+msisdn;
				}else{
					log.info(String.format("DataPushDBSync::suspendSubscriber(): SubscriberMDN Domain Object(ID:%d) is not eligible to be Suspended corresponding to MDN(%s)",sm.getID(),msisdn));
					return "Failed to suspend subscriber with msisdn:"+msisdn+" due to some internal conditions";
				}
			}else{
				log.info(String.format("DataPushDBSync::suspendSubscriber(): SubscriberMDN Domain Object doesn't exists corresponding to Msisdn(%s)",msisdn));
				return "Failed to suspend Subscriber, as the subscriber doesnt exist, corresponding to the msisdn:"+msisdn;
			}
		}catch(Exception e){
			log.error(String.format("Error Occured while suspending a subscriber with msisdn(%s)",msisdn),e);
			return "Failed to suspend subscriber with msisdn:"+msisdn+" due to some internal error";
		}
	}
	
	private boolean isSubscriberElgibleTobeSuspended(SubscriberMDN sm){
		//code should be written to check whether the subscriber is eligible to be suspended or not
		return true;
	}
	
}

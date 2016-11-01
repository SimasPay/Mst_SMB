package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.VillageDAO;
import com.mfino.dao.query.VillageQuery;
import com.mfino.domain.Village;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSVillage;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.VillageProcessor;

@Service("VillageProcessorImpl")
public class VillageProcessorImpl extends BaseFixProcessor implements VillageProcessor{	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		log.info("Entered into VillageProcessorImpl Process Method ");
		CMJSVillage realMsg = (CMJSVillage) msg;
		VillageDAO districtDAO = DAOFactory.getInstance().getVillageDAO();
		VillageQuery query = new VillageQuery();

		log.info("IdDistrict received is: "+realMsg.getIdDistrict());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("VillageProcessorImpl :: In select action Block");

			//query.setStart(realMsg.getstart());
			//query.setLimit(realMsg.getlimit());
			query.setIdDistrict(realMsg.getIdDistrict());
			
			List<Village> results = districtDAO.get(query);
			log.info("Village resluts size is: "+results.size());
			realMsg.allocateEntries(results.size());
	
			for (int i = 0; i < results.size(); i++) {
				Village village = results.get(i);
				CMJSVillage.CGEntries entry = new CMJSVillage.CGEntries();
				updateMessage(village, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}
		return realMsg;
	}

private void updateMessage(Village r, CMJSVillage.CGEntries e) {
	e.setID(r.getId());
	e.setDisplayText(r.getDisplaytext());
	e.setIdDistrict(r.getDistrict().getId());
}}

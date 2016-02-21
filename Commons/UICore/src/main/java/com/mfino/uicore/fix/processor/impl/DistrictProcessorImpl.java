package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistrictDAO;
import com.mfino.dao.query.DistrictQuery;
import com.mfino.domain.District;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDistrict;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DistrictProcessor;

@Service("DistrictProcessorImpl")
public class DistrictProcessorImpl extends BaseFixProcessor implements DistrictProcessor{	
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		log.info("Entered into DistrictProcessorImpl Process Method ");
		CMJSDistrict realMsg = (CMJSDistrict) msg;
		DistrictDAO districtDAO = DAOFactory.getInstance().getDistrictDAO();
		DistrictQuery query = new DistrictQuery();

		log.info("IdRegion received is: "+realMsg.getIdRegion());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("ProvinceProcessorImpl :: In select action Block");
	
			//query.setStart(realMsg.getstart());
			//query.setLimit(realMsg.getlimit());
			query.setIdRegion(realMsg.getIdRegion());
			
			List<District> results = districtDAO.get(query);
			log.info("District resluts size is: "+results.size());
			realMsg.allocateEntries(results.size());
	
			for (int i = 0; i < results.size(); i++) {
				District district = results.get(i);
				CMJSDistrict.CGEntries entry = new CMJSDistrict.CGEntries();
				updateMessage(district, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}
		return realMsg;
	}

private void updateMessage(District r, CMJSDistrict.CGEntries e) {
	e.setID(r.getID());
	e.setDisplayText(r.getDisplayText());
	e.setIdRegion(r.getIdRegion());
}}

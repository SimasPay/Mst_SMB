package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ProvinceRegionDAO;
import com.mfino.dao.query.ProvinceRegionQuery;
import com.mfino.domain.ProvinceRegion;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSProvinceRegion;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ProvinceRegionProcessor;

@Service("ProvinceRegionProcessorImpl")
public class ProvinceRegionProcessorImpl extends BaseFixProcessor implements ProvinceRegionProcessor{
	private Logger log = LoggerFactory.getLogger(this.getClass());

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		log.info("Entered into ProvinceProcessorImpl Process Method ");
		CMJSProvinceRegion realMsg = (CMJSProvinceRegion) msg;
		ProvinceRegionDAO provinceRegionDAO = DAOFactory.getInstance().getProvinceRegionDAO();
		ProvinceRegionQuery query = new ProvinceRegionQuery();
		
		log.info("IdProvince received is: "+realMsg.getIdProvince());
		
		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("ProvinceProcessorImpl :: In select action Block");

			//query.setStart(realMsg.getstart());
			//query.setLimit(realMsg.getlimit());
			query.setIdProvince(realMsg.getIdProvince());
			
			List<ProvinceRegion> results = provinceRegionDAO.get(query);
			log.info("provinceRegion resluts size is: "+results.size());
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				ProvinceRegion provinceRegion = results.get(i);
				CMJSProvinceRegion.CGEntries entry = new CMJSProvinceRegion.CGEntries();
				updateMessage(provinceRegion, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}
		return realMsg;
	}
	
	private void updateMessage(ProvinceRegion r, CMJSProvinceRegion.CGEntries e) {
		e.setID(r.getID());
		e.setDisplayText(r.getDisplayText());
		e.setIdProvince(r.getIdProvince());
	}
}

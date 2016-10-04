package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ProvinceDAO;
import com.mfino.dao.query.ProvinceQuery;
import com.mfino.domain.Province;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSProvince;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ProvinceProcessor;

@Service("ProvinceProcessorImpl")
public class ProvinceProcessorImpl extends BaseFixProcessor implements ProvinceProcessor{
	private Logger log = LoggerFactory.getLogger(this.getClass());
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED, rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) throws Exception  {
		log.info("Entered into ProvinceProcessorImpl Process Method ");
		CMJSProvince realMsg = (CMJSProvince) msg;
		ProvinceDAO provinceDAO = DAOFactory.getInstance().getProvinceDAO();
		ProvinceQuery query = new ProvinceQuery();

		if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
			log.info("ProvinceProcessorImpl :: In select action block");

			//query.setStart(realMsg.getstart());
			//query.setLimit(realMsg.getlimit());
			
			List<Province> results = provinceDAO.get(query);
			log.info("Province resluts size is: "+results.size());
			realMsg.allocateEntries(results.size());

			for (int i = 0; i < results.size(); i++) {
				Province province = results.get(i);
				CMJSProvince.CGEntries entry = new CMJSProvince.CGEntries();
				updateMessage(province, entry);
				realMsg.getEntries()[i] = entry;
			}
			realMsg.setsuccess(CmFinoFIX.Boolean_True);
			realMsg.settotal(query.getTotal());
			
		} else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
		} else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
		}
		return realMsg;
	}
	
	private void updateMessage(Province r, CMJSProvince.CGEntries e) {
		e.setID(r.getId().longValue());
		e.setDisplayText(r.getDisplaytext());
	}
}

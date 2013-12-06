package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PartnerDAO;
import com.mfino.dao.query.PartnerQuery;
import com.mfino.domain.Partner;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.fix.CmFinoFIX.CMJSTradeNameCheck;
import com.mfino.i18n.MessageText;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.TradeNameCheckProcessor;

@Service("TradeNameCheckProcessorImpl")
public class TradeNameCheckProcessorImpl extends BaseFixProcessor implements TradeNameCheckProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public CFIXMsg process(CFIXMsg msg) {

		CMJSTradeNameCheck realMsg = (CMJSTradeNameCheck) msg;
		PartnerDAO partnerDao = DAOFactory.getInstance().getPartnerDAO();
		PartnerQuery query = new PartnerQuery();
		query.setTradeName(realMsg.getTradeName());

		List<Partner> results = partnerDao.get(query);

		// TODO : Send possible username that would be available in the DB

		CMJSError err = new CMJSError();

		if (results.size() > 0) {
			if (realMsg.getCheckIfExists()) {
				err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				err.setErrorDescription(MessageText._("Trade Name Already Exists in DB, Please Ener a Different TradeName"));
			} else {
				err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				err.setErrorDescription(MessageText._("Partner with TradeName "+realMsg.getTradeName()+" Exist"));
			}
		} else {
			if (realMsg.getCheckIfExists()) {
				err.setErrorCode(CmFinoFIX.ErrorCode_NoError);
				err.setErrorDescription(MessageText._("TradeName Available"));
			} else {
				err.setErrorCode(CmFinoFIX.ErrorCode_Generic);
				err.setErrorDescription(MessageText._("Partner with TradeName "+realMsg.getTradeName()+" doesn't exists"));
			}
		}

		return err;
	}
}

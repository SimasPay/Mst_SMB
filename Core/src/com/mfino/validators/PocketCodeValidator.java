package com.mfino.validators;


import java.util.List;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.PocketTemplateDAO;
import com.mfino.dao.query.PocketTemplateQuery;
import com.mfino.domain.PocketTemplate;
import com.mfino.fix.CmFinoFIX;

public class PocketCodeValidator implements IValidator {
	private String	       PocketCode;
	private PocketTemplate	pocketTemplate;

	public PocketCodeValidator(String pc) {
		this.PocketCode = pc;
	}

	@Override
	public Integer validate() {
		PocketTemplateQuery ptq = new PocketTemplateQuery();
		ptq.setPocketCode(PocketCode);
		PocketTemplateDAO ptDAO = DAOFactory.getInstance().getPocketTemplateDao();
		List<PocketTemplate> ptList = ptDAO.get(ptq);
		if (ptList.size() == 1) {
			setPocketTemplate(ptList.get(0));
			return CmFinoFIX.ResponseCode_Success;
		}
		else
			return CmFinoFIX.ResponseCode_Failure;
	}

	private void setPocketTemplate(PocketTemplate pocketTemplate) {
		this.pocketTemplate = pocketTemplate;
	}

	public PocketTemplate getPocketTemplate() {
		return pocketTemplate;
	}
}

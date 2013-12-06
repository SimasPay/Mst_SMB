package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSDenominationsDAO;
import com.mfino.dao.query.MFSDenominationsQuery;
import com.mfino.domain.MFSDenominations;
import com.mfino.service.MFSDenominationsService;

@Service("MFSDenominationsServiceImpl")
public class MFSDenominationsServiceImpl implements MFSDenominationsService{
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
	public List<MFSDenominations> get(MFSDenominationsQuery mdquery) {
		MFSDenominationsDAO mfsdDAO = DAOFactory.getInstance().getMfsDenominationsDAO();
		return mfsdDAO.get(mdquery);
	}
}

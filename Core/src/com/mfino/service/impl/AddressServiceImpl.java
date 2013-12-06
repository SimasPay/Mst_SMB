package com.mfino.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.AddressDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.Address;
import com.mfino.service.AddressService;

@Service("AddressServiceImpl")
public class AddressServiceImpl implements AddressService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(
			Address address) {
		AddressDAO addressDAO = DAOFactory.getInstance().getAddressDAO();
		addressDAO.save(address);
	}

}

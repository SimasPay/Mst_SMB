/**
 * 
 */
package com.mfino.service.impl;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.ServiceDAO;
import com.mfino.dao.query.ServiceQuery;
import com.mfino.domain.Service;
import com.mfino.service.MfinoService;

/**
 * @author Shashank
 *
 */
@org.springframework.stereotype.Service("MfinoServiceImpl")
public class MfinoServiceImpl implements MfinoService {
	
	
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public Service getByServiceID(Long id){
		Service service = DAOFactory.getInstance().getServiceDAO().getById(id);
		return service;
		
	}
	/**
	 * Returns the Service by Name
	 * @param serviceName
	 * @return
	 */
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public Service getServiceByName(String serviceName) {
		ServiceDAO serviceDAO = DAOFactory.getInstance().getServiceDAO();
		Service s=null;
		if (StringUtils.isNotBlank(serviceName)) {
			ServiceQuery query = new ServiceQuery();
			query.setServiceName(serviceName);
			List<Service> lst = serviceDAO.get(query);
			if (CollectionUtils.isNotEmpty(lst)) {
				s = lst.get(0);
			}			
		}
		return s;
	}
}

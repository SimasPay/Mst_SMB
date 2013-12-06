package com.mfino.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.GroupDao;
import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Group;
import com.mfino.service.GroupService;

@Service("GroupServiceImpl")
public class GroupServiceImpl implements GroupService {
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public List<Group> get(GroupQuery query){
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		return groupDao.get(query);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public void save(Group group){
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		groupDao.save(group);
	}
	@Transactional(readOnly=false, propagation = Propagation.REQUIRED)
	public Group getById(Long groupID) {
		GroupDao groupDao = DAOFactory.getInstance().getGroupDao();
		return groupDao.getById(groupID);
	}
}

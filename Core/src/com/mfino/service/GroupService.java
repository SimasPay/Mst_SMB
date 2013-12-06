package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Group;

public interface GroupService {
	public List<Group> get(GroupQuery query);
	public void save(Group group);
	public Group getById(Long groupID);
}

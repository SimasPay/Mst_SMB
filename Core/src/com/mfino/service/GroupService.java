package com.mfino.service;

import java.util.List;

import com.mfino.dao.query.GroupQuery;
import com.mfino.domain.Groups;

public interface GroupService {
	public List<Groups> get(GroupQuery query);
	public void save(Groups group);
	public Groups getById(Long groupID);
}

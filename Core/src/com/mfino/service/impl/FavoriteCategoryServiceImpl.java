package com.mfino.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.FavoriteCategoryDAO;
import com.mfino.domain.FavoriteCategory;
import com.mfino.service.FavoriteCategoryService;

/**
 *
 * @author Srikanth
 */
@Service("FavoriteCategoryServiceImpl")
public class FavoriteCategoryServiceImpl implements FavoriteCategoryService{

	public Logger log = LoggerFactory.getLogger(this.getClass());
	public static DAOFactory daoFactory = DAOFactory.getInstance();
	public static FavoriteCategoryDAO favoriteCategoryDAO = daoFactory.getFavoriteCategoryDAO();
	
	@Override
	public FavoriteCategory getByID(Long favoriteCategoryID) {
		return favoriteCategoryDAO.getById(favoriteCategoryID);		
	}	
}

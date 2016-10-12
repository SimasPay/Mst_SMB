package com.mfino.dao;

import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javassist.util.proxy.ProxyObject;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.proxy.HibernateProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import com.mfino.constants.QueryConstants;
import com.mfino.dao.query.BaseQuery;
import com.mfino.fix.CmFinoFIX;
import com.mfino.hibernate.Timestamp;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.CoreServiceFactory;
import com.mfino.service.HibernateService;
import com.mysql.jdbc.Driver;

/**
 *
 * @author sandeepjs
 */


public class BaseDAO<T> {

    private Class<T> _class = null;
    private Session _session = null;
    private HibernateSessionHolder hibernateSessionHolder;
    LocalSessionFactoryBean b;
    Driver d;
    HibernateProxy h;
    ProxyObject p;    
    
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    
    public HibernateSessionHolder getHibernateSessionHolder() {
    	
    	if(hibernateSessionHolder == null){
    		HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
    		HibernateSessionHolder sessionHolder = hibernateService.getHibernateSessionHolder();
    		this.hibernateSessionHolder = sessionHolder; 
    	}
    	
		return hibernateSessionHolder;
	}

	public void setHibernateSessionHolder(
			HibernateSessionHolder hibernateSessionHolder) {
		this.hibernateSessionHolder = hibernateSessionHolder;
	}

	@SuppressWarnings("unchecked")
    protected BaseDAO() 
    {
    	Class thisClass = getClass();
        // Since we KNOW this must be a ParameterizedType, we can cast
        //ParameterizedType pType = (ParameterizedType)thisClass.getGenericSuperclass();
        //Type type = pType.getActualTypeArguments()[0];
        this._class = (Class<T>) getClass(thisClass.getGenericSuperclass());
        
        //this._class = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }
        
    protected Class<?> getClass(Type type)
    {
    	if (type instanceof Class) 
    	{
    	      return (Class) type;
    	}
	    else if (type instanceof ParameterizedType) 
	    {
	      return getClass(((ParameterizedType) type).getActualTypeArguments()[0]);
	    }
	    else if (type instanceof GenericArrayType) 
	    {
	      Type componentType = ((GenericArrayType) type).getGenericComponentType();
	      Class<?> componentClass = getClass(componentType);
	      if (componentClass != null ) 
	      {
	        return Array.newInstance(componentClass, 0).getClass();
	      }
	      else 
	      {
	        return null;
	      }
	    }
	    else 
	    {
	      return null;
	    }
    }

    public static void addOrder(String order, String colName, Criteria criteria) {
        Order orderObj = null;
        if (order.equals(QueryConstants.ASC_STRING)) {
            orderObj = Order.asc(colName);
        } else {
            orderObj = Order.desc(colName);
        }
        if (orderObj != null) {
            criteria.addOrder(orderObj);
        }
    }

    public static String getOrderForColumn(BaseQuery query, String colName) {
        String order = (String) query.getOrderMap().get(colName);
        return order;
    }

    public Class<T> getPersistentClass() {
        return _class;
    }

    protected Session getSession() 
    {
    	if(getHibernateSessionHolder() !=null && getHibernateSessionHolder().getSession()!=null)
    	{
    		Session session =  hibernateSessionHolder.getSession();
    		return session;
    	}
    	
    	HibernateService hibernateService = CoreServiceFactory.getInstance().getHibernateService();
    	if(null != hibernateService.getSessionFactory()){
    		return hibernateService.getSessionFactory().getCurrentSession();
    	}
    	
    	return _session; 	
    }

    protected Criteria createCriteria() {
        return getSession().createCriteria(getPersistentClass());
    }

    public void setSession(Session session) {
        this._session = session;
    }

    public void save(Collection<T> s) {
        for (T data : s) {
            save(data);
        }
    }

    public void save(T s) {
        this.saveWithoutFlush(s);
        //getSession().flush();
    }

    public void saveWithoutFlush(T s) {
        CmFinoFIX.CRBase r = (CmFinoFIX.CRBase) s;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userName = (auth != null) ? auth.getName() : " ";
        if (r.getID() == null) {
        	if(r.getCreatedBy()==null){
            r.setCreatedBy(userName);
        	}
            r.setCreateTime(new Timestamp());
        }

        //update information related to every action here
        r.setLastUpdateTime(new Timestamp());

        // For all the background processes the username is null.
        // For EG: the MDN Retire Tool will have no username.
        // Under all these conditions we dont wanna store null
        // instead we store it as system.
        
        if(StringUtils.isBlank(userName)) {
            String prevUpdatedBy = r.getUpdatedBy();            
            if(StringUtils.isBlank(prevUpdatedBy) || prevUpdatedBy.equals("System")) {
              r.setUpdatedBy("System");              
            } else if (!(prevUpdatedBy.contains("(System)"))) {
              r.setUpdatedBy(prevUpdatedBy + "(System)");              
            }            
        } else {
            r.setUpdatedBy(userName);
        }

        getSession().saveOrUpdate(s);
    }

    @SuppressWarnings("unchecked")
    public List<T> getAll() {
        Criteria crit = getSession().createCriteria(_class);
        List<T> result = (List<T>) crit.list();
        return result;
    }

    /**
     * Phasing Out Pessimistic locking
     * @param id
     * @param lockMode
     * @return
     */
    @SuppressWarnings("unchecked")
    @Deprecated
    public T getById(BigDecimal id, LockMode lockMode) {
        T entity;
        entity = (T) getSession().get(getPersistentClass(), id, lockMode);
        return entity;
    }
    
    @SuppressWarnings("unchecked")
    public T getById(BigDecimal id) {
        return getById(id, LockMode.NONE);
    }

    public void processPaging(BaseQuery query, Criteria criteria) {
        if (query.getStart() != null && query.getLimit() != null) {
            criteria.setProjection(Projections.rowCount());
            Integer count = (Integer) criteria.uniqueResult();
            query.setTotal(count);
            criteria.setProjection(null);
            criteria.setResultTransformer(Criteria.ROOT_ENTITY);

            criteria.setFirstResult(query.getStart());
            criteria.setMaxResults(query.getLimit());
        }
    }

    public void processBaseQuery(BaseQuery query, Criteria criteria) {
        if (query.getId() != null) {
            criteria.add(Restrictions.eq(CmFinoFIX.CRBase.FieldName_RecordID, query.getId()));
        }
        
        if(query.getLastUpdateTimeGE() != null) {
        	criteria.add(Restrictions.ge(CmFinoFIX.CRBase.FieldName_LastUpdateTime, query.getLastUpdateTimeGE()));
        }
        
        if(query.getLastUpdateTimeLT() != null) {
        	criteria.add(Restrictions.lt(CmFinoFIX.CRBase.FieldName_LastUpdateTime, query.getLastUpdateTimeLT()));
        }

        if(query.getCreateTimeGE() != null){
            criteria.add(Restrictions.ge(CmFinoFIX.CRBase.FieldName_CreateTime, query.getCreateTimeGE()));
        }

        if(query.getCreateTimeLT() != null){
            criteria.add(Restrictions.lt(CmFinoFIX.CRBase.FieldName_CreateTime, query.getCreateTimeLT()));
        }
    }

    @SuppressWarnings("unchecked")
    public void deleteById(long id) {
        T entity = (T) getSession().load(_class, id);
        delete(entity);
    }

    public void delete(T entity) {
        if (entity != null) {
            getSession().delete(entity);
        }
        //getSession().flush();
    }
    
    public void delete(Collection<T> entities){
    	for(T entity : entities){
    		if(entity != null){
    			getSession().delete(entity);
    		}
    	}
    	//getSession().flush();
    }

    protected static void applyOrder(BaseQuery query, Criteria criteria) {
        Set colNameSet = query.getOrderMap().keySet();
        Iterator keyIterator = colNameSet.iterator();

        while (keyIterator.hasNext()) {
            String colName = (String) keyIterator.next();
            String order = getOrderForColumn(query, colName);

            addOrder(order, colName, criteria);
        }
    }

    protected static SimpleExpression getLikeStartRestriction(String colName, String matchStr) {
        return Restrictions.like(colName, matchStr, MatchMode.START).ignoreCase();
    }

    protected static SimpleExpression getLikeEndRestriction(String colName, String matchStr) {
        return Restrictions.like(colName, matchStr, MatchMode.END).ignoreCase();
    }

    protected static SimpleExpression getLikeAnywhereRestriction(String colName, String matchStr) {
        return Restrictions.like(colName, matchStr, MatchMode.ANYWHERE).ignoreCase();
    }

    protected static void addLikeStartRestriction(Criteria criteria, String colName, String matchStr) {
        criteria.add(getLikeStartRestriction(colName, matchStr));
    }

    protected static void addLikeAnywhereRestriction(Criteria criteria, String colName, String matchStr) {
        criteria.add(getLikeAnywhereRestriction(colName, matchStr));
    }

    protected static void addLikeEndRestriction(Criteria criteria, String colName, String matchStr) {
        criteria.add(getLikeEndRestriction(colName, matchStr));
    }

    protected static void processColumn(BaseQuery query, String colName, String colNameWithAlias) {

        LinkedHashMap<String, String> orderMap = (LinkedHashMap<String, String>) query.getOrderMap();
        LinkedHashMap<String, String> newOrderMap = new LinkedHashMap<String, String>();

        if (orderMap.containsKey(colName)) {
            for (Iterator it = orderMap.entrySet().iterator(); it.hasNext();) {
                Entry entry = (Entry) it.next();
                if (entry.getKey().equals(colName)) {
                    newOrderMap.put(colNameWithAlias, (String) entry.getValue());
                } else {
                    newOrderMap.put((String) entry.getKey(), (String) entry.getValue());
                }
            }
        }
        orderMap.clear();
        orderMap.putAll(newOrderMap);
    }

    protected Query getQuery(String queryStr) {
        return getSession().createQuery(queryStr);
    }
    protected Query getSQLQuery(String queryStr) {
        return getSession().createSQLQuery(queryStr);
}

}


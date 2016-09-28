package com.mfino.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.mfino.dao.query.BookingDatedBalanceQuery;
import com.mfino.domain.BookingDatedBalance;
import com.mfino.domain.Pocket;
import com.mfino.fix.CmFinoFIX;
import com.mfino.util.DateUtil;

public class BookingDatedBalanceDAO extends BaseDAO<BookingDatedBalance> {
	
		
	public BookingDatedBalance getExactDatedEntry(Pocket pocket, Date date) {
		if (pocket == null || date == null)
			return null;

		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRBookingDatedBalance.FieldName_PocketID, pocket.getId()));
		criteria.add(Restrictions.eq(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate, date));

		@SuppressWarnings("unchecked")
		List<BookingDatedBalance> list = criteria.list();
		return list.isEmpty() ? null : list.get(0);
	}

	public BookingDatedBalance getPreDatedEntry(Pocket pocket, Date date) {
		if (pocket == null || date == null)
			return null;

		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRBookingDatedBalance.FieldName_PocketID, pocket.getId()));
		criteria.add(Restrictions.lt(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate, date));
		criteria.addOrder(Order.desc(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate));
		
		@SuppressWarnings("unchecked")
		List<BookingDatedBalance> list = criteria.list();
		return list.isEmpty() ? null : list.get(0);
	}
	
	public int deleteBookingDatedEntries(Date date){
		String deleteHql = "delete from BookingDatedBalance b where b.BookingDate >= :aDate";
		int deletedRecordsCount = getSession().createQuery(deleteHql).setDate("aDate", date).executeUpdate();
		log.info("BookingDatedBalanceDao deleteBookingDatedEntries date="+DateUtil.getFormattedDate()+", deletedRecordCount="+deletedRecordsCount);
		getSession().flush();
		getSession().clear();
		return deletedRecordsCount;
	}
	
	public List<BookingDatedBalance> get(BookingDatedBalanceQuery query) {
		Criteria criteria = createCriteria();
		
		processPaging(query, criteria);
		processBaseQuery(query, criteria);
		
		@SuppressWarnings("unchecked")
		List<BookingDatedBalance> list = criteria.list();			
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public List<BookingDatedBalance> getDailyBalanceForPocket(Long pocketId, Date startDate, Date endDate) {
		List<BookingDatedBalance> result = null;
		Criteria criteria = createCriteria();
		criteria.add(Restrictions.eq(CmFinoFIX.CRBookingDatedBalance.FieldName_PocketID, pocketId));
		criteria.add(Restrictions.ge(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate, startDate));
		criteria.add(Restrictions.le(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate, endDate));
		criteria.addOrder(Order.asc(CmFinoFIX.CRBookingDatedBalance.FieldName_BookingDate));
		result = criteria.list();
		return result;
	}
}

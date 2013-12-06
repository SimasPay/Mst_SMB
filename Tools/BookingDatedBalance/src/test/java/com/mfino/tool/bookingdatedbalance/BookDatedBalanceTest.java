package com.mfino.tool.bookingdatedbalance;

import java.io.File;
import java.util.Date;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.junit.Test;

import com.mfino.dao.BookingDatedBalanceDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.MFSLedgerDAO;
import com.mfino.domain.LedgerBalance;
import com.mfino.hibernate.session.HibernateSessionHolder;
import com.mfino.service.impl.BalancesCalculatorServiceImpl;
import com.mfino.util.DateUtil;

/**
 * 
 */

/**
 * @author Upendar
 * 
 */
public class BookDatedBalanceTest   {
	Transaction transaction;
	Session session;
	public static final String dateFormat = "dd/MM/yyyy";


//	/**
//	 * @throws java.lang.Exception
//	 */
//	@Before
//	public void setUp() throws Exception {
//	}
//
//	/**
//	 * @throws java.lang.Exception
//	 */
//	@After
//	public void tearDown() throws Exception {
//	}

	@Test
	public void test() {

		try {
			Configuration cfg = new Configuration();
			cfg.configure(new File(
					"D:/MFinoTxnManagement/MAIN_TrxnMngmt/Core/settings/developer/hibernate.cfg.xml"));
			SessionFactory sf;
			sf = cfg.buildSessionFactory();
			 session=sf.openSession();
				HibernateSessionHolder holder = new HibernateSessionHolder();
				holder.setSession(session);

				DAOFactory.getInstance().setHibernateSessionHolder(holder);

			 transaction=session.beginTransaction();
			try{

			
				Date  date=DateUtil.getDate("01/03/2013",dateFormat);

				calculate(date);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}catch (Exception e) {
			e.printStackTrace();	
			}finally{
				transaction.commit();
				session.close();
			}
		


	}
	private void calculate(Date startDate) {
		Date currentDate = new Date(System.currentTimeMillis());
		Date endDate = DateUtil.addDays(currentDate, -1);
		
		System.out.println("BalancesCalculatorTool startDate="+DateUtil.getFormattedDate(startDate, dateFormat)+", endDate="+endDate+", currentDate="+currentDate);
		
		BalancesCalculatorServiceImpl bookingBalanceBuilder = new BalancesCalculatorServiceImpl();
		
		try{
			
			System.out.println("Deleting all Booing Dated Value entries on or before "+DateUtil.getFormattedDate(startDate));
			BookingDatedBalanceDAO bdvDao = DAOFactory.getInstance().getBookingDatedBalanceDao();
			int count = bdvDao.deleteBookingDatedEntries(startDate);
			
			System.out.println("BalancesCalculatorTool :: calculate BDV delete count="+count);
			
			do{
				bookingBalanceBuilder.buildBookingBalances(startDate);
				startDate = DateUtil.addDays(startDate, 1);
			}
			while(startDate.before(endDate));
		}catch (Exception e) {
			System.out.println("BalancesCalculatorTool :: calculate "+ e);
			e.printStackTrace();
		}
	}
}

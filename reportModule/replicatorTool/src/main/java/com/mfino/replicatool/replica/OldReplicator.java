package com.mfino.replicatool.replica;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import com.mfino.hibernate.Timestamp;
import com.mfino.replicatool.domain.decrypted.CopyScheduleInfo;
import com.mfino.replicatool.persistence.CopyScheduleInfoDAO;
import com.mfino.replicatool.persistence.MfinoDbHibernateUtil;
import com.mfino.replicatool.persistence.RepilcaDbHibernateUtil;

public class OldReplicator {
	
	private static Logger log = LoggerFactory.getLogger(OldReplicator.class);
	
	public static void main(String[] args){
		//init();
		log.info("Replicator Tool Started");
		new OldReplicator().callAllMethods();
		log.info("Replicator Tool Completed");
	}
	
	public static String getDateString(Date d){
		return "'"+d.toString()+"'";
	}
	
	
	public void copyCompany(){
		log.info("Started Copying com.mfino.domain.Company Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Company");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Company where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Company obj = (com.mfino.domain.Company) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Company existobj = (com.mfino.domain.Company) replicaSession.get("com.mfino.domain.Company", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Company Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Company Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyActivitiesLog(){
		log.info("Started Copying com.mfino.domain.ActivitiesLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ActivitiesLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ActivitiesLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ActivitiesLog obj = (com.mfino.domain.ActivitiesLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ActivitiesLog existobj = (com.mfino.domain.ActivitiesLog) replicaSession.get("com.mfino.domain.ActivitiesLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ActivitiesLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ActivitiesLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyAddress(){
		log.info("Started Copying com.mfino.domain.Address Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Address");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Address where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Address obj = (com.mfino.domain.Address) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Address existobj = (com.mfino.domain.Address) replicaSession.get("com.mfino.domain.Address", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Address Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Address Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyAirtimePurchase(){
		log.info("Started Copying com.mfino.domain.AirtimePurchase Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.AirtimePurchase");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.AirtimePurchase where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.AirtimePurchase obj = (com.mfino.domain.AirtimePurchase) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.AirtimePurchase existobj = (com.mfino.domain.AirtimePurchase) replicaSession.get("com.mfino.domain.AirtimePurchase", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.AirtimePurchase Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.AirtimePurchase Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copymFinoServiceProvider(){
		log.info("Started Copying com.mfino.domain.mFinoServiceProvider Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.mFinoServiceProvider");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.mFinoServiceProvider where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.mFinoServiceProvider obj = (com.mfino.domain.mFinoServiceProvider) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.mFinoServiceProvider existobj = (com.mfino.domain.mFinoServiceProvider) replicaSession.get("com.mfino.domain.mFinoServiceProvider", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.mFinoServiceProvider Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.mFinoServiceProvider Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyAuthorizingPerson(){
		log.info("Started Copying com.mfino.domain.AuthorizingPerson Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.AuthorizingPerson");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.AuthorizingPerson where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.AuthorizingPerson obj = (com.mfino.domain.AuthorizingPerson) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.AuthorizingPerson existobj = (com.mfino.domain.AuthorizingPerson) replicaSession.get("com.mfino.domain.AuthorizingPerson", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.AuthorizingPerson Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.AuthorizingPerson Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBank(){
		log.info("Started Copying com.mfino.domain.Bank Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Bank");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Bank where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Bank obj = (com.mfino.domain.Bank) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Bank existobj = (com.mfino.domain.Bank) replicaSession.get("com.mfino.domain.Bank", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Bank Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Bank Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyUser(){
		log.info("Started Copying com.mfino.domain.User Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.User");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.User where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.User obj = (com.mfino.domain.User) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.User existobj = (com.mfino.domain.User) replicaSession.get("com.mfino.domain.User", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.User Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.User Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBankAdmin(){
		log.info("Started Copying com.mfino.domain.BankAdmin Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BankAdmin");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BankAdmin where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BankAdmin obj = (com.mfino.domain.BankAdmin) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BankAdmin existobj = (com.mfino.domain.BankAdmin) replicaSession.get("com.mfino.domain.BankAdmin", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BankAdmin Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BankAdmin Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBiller(){
		log.info("Started Copying com.mfino.domain.Biller Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Biller");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Biller where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Biller obj = (com.mfino.domain.Biller) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Biller existobj = (com.mfino.domain.Biller) replicaSession.get("com.mfino.domain.Biller", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Biller Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Biller Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBillPayments(){
		log.info("Started Copying com.mfino.domain.BillPayments Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BillPayments");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BillPayments where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BillPayments obj = (com.mfino.domain.BillPayments) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BillPayments existobj = (com.mfino.domain.BillPayments) replicaSession.get("com.mfino.domain.BillPayments", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BillPayments Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BillPayments Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPocketTemplate(){
		log.info("Started Copying com.mfino.domain.PocketTemplate Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PocketTemplate");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PocketTemplate where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PocketTemplate obj = (com.mfino.domain.PocketTemplate) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PocketTemplate existobj = (com.mfino.domain.PocketTemplate) replicaSession.get("com.mfino.domain.PocketTemplate", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PocketTemplate Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PocketTemplate Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyKYCLevel(){
		log.info("Started Copying com.mfino.domain.KYCLevel Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.KYCLevel");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.KYCLevel where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.KYCLevel obj = (com.mfino.domain.KYCLevel) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.KYCLevel existobj = (com.mfino.domain.KYCLevel) replicaSession.get("com.mfino.domain.KYCLevel", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.KYCLevel Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.KYCLevel Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySubscriber(){
		log.info("Started Copying com.mfino.domain.Subscriber Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Subscriber");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Subscriber where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Subscriber obj = (com.mfino.domain.Subscriber) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Subscriber existobj = (com.mfino.domain.Subscriber) replicaSession.get("com.mfino.domain.Subscriber", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Subscriber Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Subscriber Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBillPaymentTransaction(){
		log.info("Started Copying com.mfino.domain.BillPaymentTransaction Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BillPaymentTransaction");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BillPaymentTransaction where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BillPaymentTransaction obj = (com.mfino.domain.BillPaymentTransaction) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BillPaymentTransaction existobj = (com.mfino.domain.BillPaymentTransaction) replicaSession.get("com.mfino.domain.BillPaymentTransaction", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BillPaymentTransaction Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BillPaymentTransaction Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBrand(){
		log.info("Started Copying com.mfino.domain.Brand Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Brand");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Brand where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Brand obj = (com.mfino.domain.Brand) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Brand existobj = (com.mfino.domain.Brand) replicaSession.get("com.mfino.domain.Brand", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Brand Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Brand Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBulkBankAccount(){
		log.info("Started Copying com.mfino.domain.BulkBankAccount Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BulkBankAccount");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BulkBankAccount where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BulkBankAccount obj = (com.mfino.domain.BulkBankAccount) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BulkBankAccount existobj = (com.mfino.domain.BulkBankAccount) replicaSession.get("com.mfino.domain.BulkBankAccount", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BulkBankAccount Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BulkBankAccount Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyRegion(){
		log.info("Started Copying com.mfino.domain.Region Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Region");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Region where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Region obj = (com.mfino.domain.Region) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Region existobj = (com.mfino.domain.Region) replicaSession.get("com.mfino.domain.Region", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Region Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Region Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMerchant(){
		log.info("Started Copying com.mfino.domain.Merchant Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Merchant");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Merchant where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Merchant obj = (com.mfino.domain.Merchant) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Merchant existobj = (com.mfino.domain.Merchant) replicaSession.get("com.mfino.domain.Merchant", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Merchant Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Merchant Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySubscriberMDN(){
		log.info("Started Copying com.mfino.domain.SubscriberMDN Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SubscriberMDN");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SubscriberMDN where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SubscriberMDN obj = (com.mfino.domain.SubscriberMDN) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SubscriberMDN existobj = (com.mfino.domain.SubscriberMDN) replicaSession.get("com.mfino.domain.SubscriberMDN", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SubscriberMDN Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SubscriberMDN Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyService(){
		log.info("Started Copying com.mfino.domain.Service Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Service");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Service where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Service obj = (com.mfino.domain.Service) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Service existobj = (com.mfino.domain.Service) replicaSession.get("com.mfino.domain.Service", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Service Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Service Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyDistributionChainTemplate(){
		log.info("Started Copying com.mfino.domain.DistributionChainTemplate Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.DistributionChainTemplate");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.DistributionChainTemplate where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.DistributionChainTemplate obj = (com.mfino.domain.DistributionChainTemplate) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.DistributionChainTemplate existobj = (com.mfino.domain.DistributionChainTemplate) replicaSession.get("com.mfino.domain.DistributionChainTemplate", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.DistributionChainTemplate Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.DistributionChainTemplate Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionType(){
		log.info("Started Copying com.mfino.domain.TransactionType Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionType");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionType where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionType obj = (com.mfino.domain.TransactionType) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionType existobj = (com.mfino.domain.TransactionType) replicaSession.get("com.mfino.domain.TransactionType", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionType Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionType Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyDistributionChainLevel(){
		log.info("Started Copying com.mfino.domain.DistributionChainLevel Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.DistributionChainLevel");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.DistributionChainLevel where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.DistributionChainLevel obj = (com.mfino.domain.DistributionChainLevel) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.DistributionChainLevel existobj = (com.mfino.domain.DistributionChainLevel) replicaSession.get("com.mfino.domain.DistributionChainLevel", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.DistributionChainLevel Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.DistributionChainLevel Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBulkLOP(){
		log.info("Started Copying com.mfino.domain.BulkLOP Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BulkLOP");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BulkLOP where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BulkLOP obj = (com.mfino.domain.BulkLOP) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BulkLOP existobj = (com.mfino.domain.BulkLOP) replicaSession.get("com.mfino.domain.BulkLOP", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BulkLOP Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BulkLOP Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPocket(){
		log.info("Started Copying com.mfino.domain.Pocket Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Pocket");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Pocket where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Pocket obj = (com.mfino.domain.Pocket) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Pocket existobj = (com.mfino.domain.Pocket) replicaSession.get("com.mfino.domain.Pocket", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Pocket Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Pocket Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBulkUpload(){
		log.info("Started Copying com.mfino.domain.BulkUpload Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BulkUpload");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BulkUpload where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BulkUpload obj = (com.mfino.domain.BulkUpload) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BulkUpload existobj = (com.mfino.domain.BulkUpload) replicaSession.get("com.mfino.domain.BulkUpload", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BulkUpload Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BulkUpload Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBulkUploadEntry(){
		log.info("Started Copying com.mfino.domain.BulkUploadEntry Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BulkUploadEntry");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BulkUploadEntry where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BulkUploadEntry obj = (com.mfino.domain.BulkUploadEntry) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BulkUploadEntry existobj = (com.mfino.domain.BulkUploadEntry) replicaSession.get("com.mfino.domain.BulkUploadEntry", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BulkUploadEntry Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BulkUploadEntry Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyBulkUploadFile(){
		log.info("Started Copying com.mfino.domain.BulkUploadFile Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.BulkUploadFile");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.BulkUploadFile where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.BulkUploadFile obj = (com.mfino.domain.BulkUploadFile) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.BulkUploadFile existobj = (com.mfino.domain.BulkUploadFile) replicaSession.get("com.mfino.domain.BulkUploadFile", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.BulkUploadFile Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.BulkUploadFile Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyCardInfo(){
		log.info("Started Copying com.mfino.domain.CardInfo Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.CardInfo");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.CardInfo where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.CardInfo obj = (com.mfino.domain.CardInfo) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.CardInfo existobj = (com.mfino.domain.CardInfo) replicaSession.get("com.mfino.domain.CardInfo", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.CardInfo Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.CardInfo Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChannelCode(){
		log.info("Started Copying com.mfino.domain.ChannelCode Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChannelCode");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChannelCode where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChannelCode obj = (com.mfino.domain.ChannelCode) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChannelCode existobj = (com.mfino.domain.ChannelCode) replicaSession.get("com.mfino.domain.ChannelCode", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChannelCode Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChannelCode Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChannelSessionManagement(){
		log.info("Started Copying com.mfino.domain.ChannelSessionManagement Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChannelSessionManagement");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChannelSessionManagement where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChannelSessionManagement obj = (com.mfino.domain.ChannelSessionManagement) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChannelSessionManagement existobj = (com.mfino.domain.ChannelSessionManagement) replicaSession.get("com.mfino.domain.ChannelSessionManagement", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChannelSessionManagement Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChannelSessionManagement Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChargeType(){
		log.info("Started Copying com.mfino.domain.ChargeType Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChargeType");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChargeType where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChargeType obj = (com.mfino.domain.ChargeType) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChargeType existobj = (com.mfino.domain.ChargeType) replicaSession.get("com.mfino.domain.ChargeType", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChargeType Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChargeType Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPartner(){
		log.info("Started Copying com.mfino.domain.Partner Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Partner");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Partner where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Partner obj = (com.mfino.domain.Partner) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Partner existobj = (com.mfino.domain.Partner) replicaSession.get("com.mfino.domain.Partner", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Partner Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Partner Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChargeDefinition(){
		log.info("Started Copying com.mfino.domain.ChargeDefinition Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChargeDefinition");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChargeDefinition where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChargeDefinition obj = (com.mfino.domain.ChargeDefinition) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChargeDefinition existobj = (com.mfino.domain.ChargeDefinition) replicaSession.get("com.mfino.domain.ChargeDefinition", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChargeDefinition Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChargeDefinition Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChargePricing(){
		log.info("Started Copying com.mfino.domain.ChargePricing Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChargePricing");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChargePricing where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChargePricing obj = (com.mfino.domain.ChargePricing) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChargePricing existobj = (com.mfino.domain.ChargePricing) replicaSession.get("com.mfino.domain.ChargePricing", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChargePricing Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChargePricing Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyChargeTxnCommodityTransferMap(){
		log.info("Started Copying com.mfino.domain.ChargeTxnCommodityTransferMap Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ChargeTxnCommodityTransferMap");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ChargeTxnCommodityTransferMap where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ChargeTxnCommodityTransferMap obj = (com.mfino.domain.ChargeTxnCommodityTransferMap) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ChargeTxnCommodityTransferMap existobj = (com.mfino.domain.ChargeTxnCommodityTransferMap) replicaSession.get("com.mfino.domain.ChargeTxnCommodityTransferMap", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ChargeTxnCommodityTransferMap Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ChargeTxnCommodityTransferMap Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionsLog(){
		log.info("Started Copying com.mfino.domain.TransactionsLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionsLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionsLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionsLog obj = (com.mfino.domain.TransactionsLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionsLog existobj = (com.mfino.domain.TransactionsLog) replicaSession.get("com.mfino.domain.TransactionsLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionsLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionsLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyLOP(){
		log.info("Started Copying com.mfino.domain.LOP Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.LOP");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.LOP where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.LOP obj = (com.mfino.domain.LOP) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.LOP existobj = (com.mfino.domain.LOP) replicaSession.get("com.mfino.domain.LOP", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.LOP Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.LOP Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyCreditCardTransaction(){
		log.info("Started Copying com.mfino.domain.CreditCardTransaction Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.CreditCardTransaction");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.CreditCardTransaction where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.CreditCardTransaction obj = (com.mfino.domain.CreditCardTransaction) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.CreditCardTransaction existobj = (com.mfino.domain.CreditCardTransaction) replicaSession.get("com.mfino.domain.CreditCardTransaction", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.CreditCardTransaction Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.CreditCardTransaction Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyCommodityTransfer(){
		log.info("Started Copying com.mfino.domain.CommodityTransfer Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.CommodityTransfer");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.CommodityTransfer where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.CommodityTransfer obj = (com.mfino.domain.CommodityTransfer) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.CommodityTransfer existobj = (com.mfino.domain.CommodityTransfer) replicaSession.get("com.mfino.domain.CommodityTransfer", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.CommodityTransfer Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.CommodityTransfer Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyCreditCardDestinations(){
		log.info("Started Copying com.mfino.domain.CreditCardDestinations Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.CreditCardDestinations");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.CreditCardDestinations where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.CreditCardDestinations obj = (com.mfino.domain.CreditCardDestinations) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.CreditCardDestinations existobj = (com.mfino.domain.CreditCardDestinations) replicaSession.get("com.mfino.domain.CreditCardDestinations", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.CreditCardDestinations Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.CreditCardDestinations Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyDbParam(){
		log.info("Started Copying com.mfino.domain.DbParam Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.DbParam");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.DbParam where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.DbParam obj = (com.mfino.domain.DbParam) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.DbParam existobj = (com.mfino.domain.DbParam) replicaSession.get("com.mfino.domain.DbParam", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.DbParam Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.DbParam Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyDCTRestrictions(){
		log.info("Started Copying com.mfino.domain.DCTRestrictions Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.DCTRestrictions");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.DCTRestrictions where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.DCTRestrictions obj = (com.mfino.domain.DCTRestrictions) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.DCTRestrictions existobj = (com.mfino.domain.DCTRestrictions) replicaSession.get("com.mfino.domain.DCTRestrictions", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.DCTRestrictions Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.DCTRestrictions Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyDenomination(){
		log.info("Started Copying com.mfino.domain.Denomination Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Denomination");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Denomination where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Denomination obj = (com.mfino.domain.Denomination) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Denomination existobj = (com.mfino.domain.Denomination) replicaSession.get("com.mfino.domain.Denomination", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Denomination Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Denomination Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyEnumText(){
		log.info("Started Copying com.mfino.domain.EnumText Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.EnumText");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.EnumText where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.EnumText obj = (com.mfino.domain.EnumText) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.EnumText existobj = (com.mfino.domain.EnumText) replicaSession.get("com.mfino.domain.EnumText", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.EnumText Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.EnumText Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyGroup(){
		log.info("Started Copying com.mfino.domain.Group Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Group");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Group where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Group obj = (com.mfino.domain.Group) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Group existobj = (com.mfino.domain.Group) replicaSession.get("com.mfino.domain.Group", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Group Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Group Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMFSBiller(){
		log.info("Started Copying com.mfino.domain.MFSBiller Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MFSBiller");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MFSBiller where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MFSBiller obj = (com.mfino.domain.MFSBiller) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MFSBiller existobj = (com.mfino.domain.MFSBiller) replicaSession.get("com.mfino.domain.MFSBiller", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MFSBiller Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MFSBiller Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyIntegrationPartnerMapping(){
		log.info("Started Copying com.mfino.domain.IntegrationPartnerMapping Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.IntegrationPartnerMapping");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.IntegrationPartnerMapping where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.IntegrationPartnerMapping obj = (com.mfino.domain.IntegrationPartnerMapping) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.IntegrationPartnerMapping existobj = (com.mfino.domain.IntegrationPartnerMapping) replicaSession.get("com.mfino.domain.IntegrationPartnerMapping", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.IntegrationPartnerMapping Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.IntegrationPartnerMapping Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyInterBankCode(){
		log.info("Started Copying com.mfino.domain.InterBankCode Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.InterBankCode");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.InterBankCode where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.InterBankCode obj = (com.mfino.domain.InterBankCode) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.InterBankCode existobj = (com.mfino.domain.InterBankCode) replicaSession.get("com.mfino.domain.InterBankCode", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.InterBankCode Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.InterBankCode Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyInterbankTransfer(){
		log.info("Started Copying com.mfino.domain.InterbankTransfer Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.InterbankTransfer");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.InterbankTransfer where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.InterbankTransfer obj = (com.mfino.domain.InterbankTransfer) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.InterbankTransfer existobj = (com.mfino.domain.InterbankTransfer) replicaSession.get("com.mfino.domain.InterbankTransfer", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.InterbankTransfer Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.InterbankTransfer Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyKYCFields(){
		log.info("Started Copying com.mfino.domain.KYCFields Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.KYCFields");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.KYCFields where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.KYCFields obj = (com.mfino.domain.KYCFields) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.KYCFields existobj = (com.mfino.domain.KYCFields) replicaSession.get("com.mfino.domain.KYCFields", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.KYCFields Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.KYCFields Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyLedger(){
		log.info("Started Copying com.mfino.domain.Ledger Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Ledger");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Ledger where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Ledger obj = (com.mfino.domain.Ledger) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Ledger existobj = (com.mfino.domain.Ledger) replicaSession.get("com.mfino.domain.Ledger", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Ledger Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Ledger Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyLOPHistory(){
		log.info("Started Copying com.mfino.domain.LOPHistory Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.LOPHistory");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.LOPHistory where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.LOPHistory obj = (com.mfino.domain.LOPHistory) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.LOPHistory existobj = (com.mfino.domain.LOPHistory) replicaSession.get("com.mfino.domain.LOPHistory", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.LOPHistory Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.LOPHistory Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMDNRange(){
		log.info("Started Copying com.mfino.domain.MDNRange Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MDNRange");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MDNRange where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MDNRange obj = (com.mfino.domain.MDNRange) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MDNRange existobj = (com.mfino.domain.MDNRange) replicaSession.get("com.mfino.domain.MDNRange", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MDNRange Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MDNRange Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMerchantCode(){
		log.info("Started Copying com.mfino.domain.MerchantCode Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MerchantCode");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MerchantCode where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MerchantCode obj = (com.mfino.domain.MerchantCode) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MerchantCode existobj = (com.mfino.domain.MerchantCode) replicaSession.get("com.mfino.domain.MerchantCode", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MerchantCode Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MerchantCode Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMerchantPrefixCode(){
		log.info("Started Copying com.mfino.domain.MerchantPrefixCode Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MerchantPrefixCode");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MerchantPrefixCode where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MerchantPrefixCode obj = (com.mfino.domain.MerchantPrefixCode) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MerchantPrefixCode existobj = (com.mfino.domain.MerchantPrefixCode) replicaSession.get("com.mfino.domain.MerchantPrefixCode", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MerchantPrefixCode Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MerchantPrefixCode Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMFSBillerPartner(){
		log.info("Started Copying com.mfino.domain.MFSBillerPartner Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MFSBillerPartner");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MFSBillerPartner where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MFSBillerPartner obj = (com.mfino.domain.MFSBillerPartner) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MFSBillerPartner existobj = (com.mfino.domain.MFSBillerPartner) replicaSession.get("com.mfino.domain.MFSBillerPartner", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MFSBillerPartner Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MFSBillerPartner Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMFSDenominations(){
		log.info("Started Copying com.mfino.domain.MFSDenominations Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MFSDenominations");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MFSDenominations where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MFSDenominations obj = (com.mfino.domain.MFSDenominations) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MFSDenominations existobj = (com.mfino.domain.MFSDenominations) replicaSession.get("com.mfino.domain.MFSDenominations", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MFSDenominations Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MFSDenominations Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyMobileNetworkOperator(){
		log.info("Started Copying com.mfino.domain.MobileNetworkOperator Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.MobileNetworkOperator");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.MobileNetworkOperator where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.MobileNetworkOperator obj = (com.mfino.domain.MobileNetworkOperator) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.MobileNetworkOperator existobj = (com.mfino.domain.MobileNetworkOperator) replicaSession.get("com.mfino.domain.MobileNetworkOperator", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.MobileNetworkOperator Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.MobileNetworkOperator Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyNotification(){
		log.info("Started Copying com.mfino.domain.Notification Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Notification");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Notification where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Notification obj = (com.mfino.domain.Notification) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Notification existobj = (com.mfino.domain.Notification) replicaSession.get("com.mfino.domain.Notification", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Notification Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Notification Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyOfflineReport(){
		log.info("Started Copying com.mfino.domain.OfflineReport Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.OfflineReport");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.OfflineReport where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.OfflineReport obj = (com.mfino.domain.OfflineReport) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.OfflineReport existobj = (com.mfino.domain.OfflineReport) replicaSession.get("com.mfino.domain.OfflineReport", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.OfflineReport Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.OfflineReport Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyOfflineReportForCompany(){
		log.info("Started Copying com.mfino.domain.OfflineReportForCompany Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.OfflineReportForCompany");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.OfflineReportForCompany where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.OfflineReportForCompany obj = (com.mfino.domain.OfflineReportForCompany) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.OfflineReportForCompany existobj = (com.mfino.domain.OfflineReportForCompany) replicaSession.get("com.mfino.domain.OfflineReportForCompany", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.OfflineReportForCompany Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.OfflineReportForCompany Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyOfflineReportReceiver(){
		log.info("Started Copying com.mfino.domain.OfflineReportReceiver Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.OfflineReportReceiver");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.OfflineReportReceiver where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.OfflineReportReceiver obj = (com.mfino.domain.OfflineReportReceiver) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.OfflineReportReceiver existobj = (com.mfino.domain.OfflineReportReceiver) replicaSession.get("com.mfino.domain.OfflineReportReceiver", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.OfflineReportReceiver Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.OfflineReportReceiver Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPartnerRestrictions(){
		log.info("Started Copying com.mfino.domain.PartnerRestrictions Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PartnerRestrictions");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PartnerRestrictions where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PartnerRestrictions obj = (com.mfino.domain.PartnerRestrictions) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PartnerRestrictions existobj = (com.mfino.domain.PartnerRestrictions) replicaSession.get("com.mfino.domain.PartnerRestrictions", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PartnerRestrictions Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PartnerRestrictions Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPartnerServices(){
		log.info("Started Copying com.mfino.domain.PartnerServices Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PartnerServices");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PartnerServices where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PartnerServices obj = (com.mfino.domain.PartnerServices) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PartnerServices existobj = (com.mfino.domain.PartnerServices) replicaSession.get("com.mfino.domain.PartnerServices", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PartnerServices Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PartnerServices Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPendingCommodityTransfer(){
		log.info("Started Copying com.mfino.domain.PendingCommodityTransfer Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PendingCommodityTransfer");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PendingCommodityTransfer where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PendingCommodityTransfer obj = (com.mfino.domain.PendingCommodityTransfer) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PendingCommodityTransfer existobj = (com.mfino.domain.PendingCommodityTransfer) replicaSession.get("com.mfino.domain.PendingCommodityTransfer", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PendingCommodityTransfer Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PendingCommodityTransfer Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPendingTransactionsEntry(){
		log.info("Started Copying com.mfino.domain.PendingTransactionsEntry Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PendingTransactionsEntry");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PendingTransactionsEntry where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PendingTransactionsEntry obj = (com.mfino.domain.PendingTransactionsEntry) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PendingTransactionsEntry existobj = (com.mfino.domain.PendingTransactionsEntry) replicaSession.get("com.mfino.domain.PendingTransactionsEntry", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PendingTransactionsEntry Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PendingTransactionsEntry Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPendingTransactionsFile(){
		log.info("Started Copying com.mfino.domain.PendingTransactionsFile Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PendingTransactionsFile");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PendingTransactionsFile where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PendingTransactionsFile obj = (com.mfino.domain.PendingTransactionsFile) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PendingTransactionsFile existobj = (com.mfino.domain.PendingTransactionsFile) replicaSession.get("com.mfino.domain.PendingTransactionsFile", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PendingTransactionsFile Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PendingTransactionsFile Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPermissionItems(){
		log.info("Started Copying com.mfino.domain.PermissionItems Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PermissionItems");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PermissionItems where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PermissionItems obj = (com.mfino.domain.PermissionItems) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PermissionItems existobj = (com.mfino.domain.PermissionItems) replicaSession.get("com.mfino.domain.PermissionItems", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PermissionItems Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PermissionItems Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPerson2Person(){
		log.info("Started Copying com.mfino.domain.Person2Person Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.Person2Person");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.Person2Person where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.Person2Person obj = (com.mfino.domain.Person2Person) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.Person2Person existobj = (com.mfino.domain.Person2Person) replicaSession.get("com.mfino.domain.Person2Person", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.Person2Person Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.Person2Person Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyPocketTemplateConfig(){
		log.info("Started Copying com.mfino.domain.PocketTemplateConfig Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.PocketTemplateConfig");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.PocketTemplateConfig where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.PocketTemplateConfig obj = (com.mfino.domain.PocketTemplateConfig) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.PocketTemplateConfig existobj = (com.mfino.domain.PocketTemplateConfig) replicaSession.get("com.mfino.domain.PocketTemplateConfig", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.PocketTemplateConfig Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.PocketTemplateConfig Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyProductIndicator(){
		log.info("Started Copying com.mfino.domain.ProductIndicator Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ProductIndicator");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ProductIndicator where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ProductIndicator obj = (com.mfino.domain.ProductIndicator) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ProductIndicator existobj = (com.mfino.domain.ProductIndicator) replicaSession.get("com.mfino.domain.ProductIndicator", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ProductIndicator Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ProductIndicator Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyReportParameters(){
		log.info("Started Copying com.mfino.domain.ReportParameters Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ReportParameters");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ReportParameters where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ReportParameters obj = (com.mfino.domain.ReportParameters) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ReportParameters existobj = (com.mfino.domain.ReportParameters) replicaSession.get("com.mfino.domain.ReportParameters", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ReportParameters Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ReportParameters Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyRolePermission(){
		log.info("Started Copying com.mfino.domain.RolePermission Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.RolePermission");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.RolePermission where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.RolePermission obj = (com.mfino.domain.RolePermission) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.RolePermission existobj = (com.mfino.domain.RolePermission) replicaSession.get("com.mfino.domain.RolePermission", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.RolePermission Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.RolePermission Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySAPGroupID(){
		log.info("Started Copying com.mfino.domain.SAPGroupID Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SAPGroupID");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SAPGroupID where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SAPGroupID obj = (com.mfino.domain.SAPGroupID) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SAPGroupID existobj = (com.mfino.domain.SAPGroupID) replicaSession.get("com.mfino.domain.SAPGroupID", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SAPGroupID Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SAPGroupID Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyServiceAudit(){
		log.info("Started Copying com.mfino.domain.ServiceAudit Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ServiceAudit");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ServiceAudit where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ServiceAudit obj = (com.mfino.domain.ServiceAudit) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ServiceAudit existobj = (com.mfino.domain.ServiceAudit) replicaSession.get("com.mfino.domain.ServiceAudit", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ServiceAudit Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ServiceAudit Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyServiceChargeTransactionLog(){
		log.info("Started Copying com.mfino.domain.ServiceChargeTransactionLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ServiceChargeTransactionLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ServiceChargeTransactionLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ServiceChargeTransactionLog obj = (com.mfino.domain.ServiceChargeTransactionLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ServiceChargeTransactionLog existobj = (com.mfino.domain.ServiceChargeTransactionLog) replicaSession.get("com.mfino.domain.ServiceChargeTransactionLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ServiceChargeTransactionLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ServiceChargeTransactionLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySettlementTemplate(){
		log.info("Started Copying com.mfino.domain.SettlementTemplate Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SettlementTemplate");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SettlementTemplate where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SettlementTemplate obj = (com.mfino.domain.SettlementTemplate) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SettlementTemplate existobj = (com.mfino.domain.SettlementTemplate) replicaSession.get("com.mfino.domain.SettlementTemplate", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SettlementTemplate Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SettlementTemplate Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyServiceSettlementConfig(){
		log.info("Started Copying com.mfino.domain.ServiceSettlementConfig Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ServiceSettlementConfig");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ServiceSettlementConfig where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ServiceSettlementConfig obj = (com.mfino.domain.ServiceSettlementConfig) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ServiceSettlementConfig existobj = (com.mfino.domain.ServiceSettlementConfig) replicaSession.get("com.mfino.domain.ServiceSettlementConfig", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ServiceSettlementConfig Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ServiceSettlementConfig Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyServiceTransaction(){
		log.info("Started Copying com.mfino.domain.ServiceTransaction Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.ServiceTransaction");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.ServiceTransaction where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.ServiceTransaction obj = (com.mfino.domain.ServiceTransaction) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.ServiceTransaction existobj = (com.mfino.domain.ServiceTransaction) replicaSession.get("com.mfino.domain.ServiceTransaction", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.ServiceTransaction Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.ServiceTransaction Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySettlementSchedulerLogs(){
		log.info("Started Copying com.mfino.domain.SettlementSchedulerLogs Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SettlementSchedulerLogs");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SettlementSchedulerLogs where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SettlementSchedulerLogs obj = (com.mfino.domain.SettlementSchedulerLogs) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SettlementSchedulerLogs existobj = (com.mfino.domain.SettlementSchedulerLogs) replicaSession.get("com.mfino.domain.SettlementSchedulerLogs", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SettlementSchedulerLogs Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SettlementSchedulerLogs Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySettlementTransactionLogs(){
		log.info("Started Copying com.mfino.domain.SettlementTransactionLogs Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SettlementTransactionLogs");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SettlementTransactionLogs where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SettlementTransactionLogs obj = (com.mfino.domain.SettlementTransactionLogs) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SettlementTransactionLogs existobj = (com.mfino.domain.SettlementTransactionLogs) replicaSession.get("com.mfino.domain.SettlementTransactionLogs", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SettlementTransactionLogs Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SettlementTransactionLogs Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionRule(){
		log.info("Started Copying com.mfino.domain.TransactionRule Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionRule");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionRule where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionRule obj = (com.mfino.domain.TransactionRule) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionRule existobj = (com.mfino.domain.TransactionRule) replicaSession.get("com.mfino.domain.TransactionRule", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionRule Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionRule Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionCharge(){
		log.info("Started Copying com.mfino.domain.TransactionCharge Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionCharge");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionCharge where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionCharge obj = (com.mfino.domain.TransactionCharge) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionCharge existobj = (com.mfino.domain.TransactionCharge) replicaSession.get("com.mfino.domain.TransactionCharge", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionCharge Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionCharge Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySharePartner(){
		log.info("Started Copying com.mfino.domain.SharePartner Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SharePartner");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SharePartner where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SharePartner obj = (com.mfino.domain.SharePartner) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SharePartner existobj = (com.mfino.domain.SharePartner) replicaSession.get("com.mfino.domain.SharePartner", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SharePartner Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SharePartner Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySMSPartner(){
		log.info("Started Copying com.mfino.domain.SMSPartner Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SMSPartner");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SMSPartner where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SMSPartner obj = (com.mfino.domain.SMSPartner) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SMSPartner existobj = (com.mfino.domain.SMSPartner) replicaSession.get("com.mfino.domain.SMSPartner", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SMSPartner Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SMSPartner Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySMSC(){
		log.info("Started Copying com.mfino.domain.SMSC Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SMSC");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SMSC where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SMSC obj = (com.mfino.domain.SMSC) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SMSC existobj = (com.mfino.domain.SMSC) replicaSession.get("com.mfino.domain.SMSC", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SMSC Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SMSC Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySMSCode(){
		log.info("Started Copying com.mfino.domain.SMSCode Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SMSCode");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SMSCode where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SMSCode obj = (com.mfino.domain.SMSCode) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SMSCode existobj = (com.mfino.domain.SMSCode) replicaSession.get("com.mfino.domain.SMSCode", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SMSCode Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SMSCode Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySMSTransactionsLog(){
		log.info("Started Copying com.mfino.domain.SMSTransactionsLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SMSTransactionsLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SMSTransactionsLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SMSTransactionsLog obj = (com.mfino.domain.SMSTransactionsLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SMSTransactionsLog existobj = (com.mfino.domain.SMSTransactionsLog) replicaSession.get("com.mfino.domain.SMSTransactionsLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SMSTransactionsLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SMSTransactionsLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySubscriberGroup(){
		log.info("Started Copying com.mfino.domain.SubscriberGroup Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SubscriberGroup");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SubscriberGroup where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SubscriberGroup obj = (com.mfino.domain.SubscriberGroup) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SubscriberGroup existobj = (com.mfino.domain.SubscriberGroup) replicaSession.get("com.mfino.domain.SubscriberGroup", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SubscriberGroup Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SubscriberGroup Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySubscribersAdditionalFields(){
		log.info("Started Copying com.mfino.domain.SubscribersAdditionalFields Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SubscribersAdditionalFields");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SubscribersAdditionalFields where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SubscribersAdditionalFields obj = (com.mfino.domain.SubscribersAdditionalFields) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SubscribersAdditionalFields existobj = (com.mfino.domain.SubscribersAdditionalFields) replicaSession.get("com.mfino.domain.SubscribersAdditionalFields", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SubscribersAdditionalFields Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SubscribersAdditionalFields Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copySystemParameters(){
		log.info("Started Copying com.mfino.domain.SystemParameters Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.SystemParameters");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.SystemParameters where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.SystemParameters obj = (com.mfino.domain.SystemParameters) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.SystemParameters existobj = (com.mfino.domain.SystemParameters) replicaSession.get("com.mfino.domain.SystemParameters", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.SystemParameters Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.SystemParameters Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionAmountDistributionLog(){
		log.info("Started Copying com.mfino.domain.TransactionAmountDistributionLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionAmountDistributionLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionAmountDistributionLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionAmountDistributionLog obj = (com.mfino.domain.TransactionAmountDistributionLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionAmountDistributionLog existobj = (com.mfino.domain.TransactionAmountDistributionLog) replicaSession.get("com.mfino.domain.TransactionAmountDistributionLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionAmountDistributionLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionAmountDistributionLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyTransactionChargeLog(){
		log.info("Started Copying com.mfino.domain.TransactionChargeLog Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.TransactionChargeLog");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.TransactionChargeLog where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.TransactionChargeLog obj = (com.mfino.domain.TransactionChargeLog) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.TransactionChargeLog existobj = (com.mfino.domain.TransactionChargeLog) replicaSession.get("com.mfino.domain.TransactionChargeLog", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.TransactionChargeLog Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.TransactionChargeLog Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyUnRegisteredTxnInfo(){
		log.info("Started Copying com.mfino.domain.UnRegisteredTxnInfo Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.UnRegisteredTxnInfo");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.UnRegisteredTxnInfo where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.UnRegisteredTxnInfo obj = (com.mfino.domain.UnRegisteredTxnInfo) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.UnRegisteredTxnInfo existobj = (com.mfino.domain.UnRegisteredTxnInfo) replicaSession.get("com.mfino.domain.UnRegisteredTxnInfo", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.UnRegisteredTxnInfo Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.UnRegisteredTxnInfo Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void copyVisafoneTxnGenerator(){
		log.info("Started Copying com.mfino.domain.VisafoneTxnGenerator Domain Object");
		Date curUpdateTime = new Timestamp();
		CopyScheduleInfoDAO dao = new CopyScheduleInfoDAO();
		CopyScheduleInfo csi = dao.getClassInfo("com.mfino.domain.VisafoneTxnGenerator");
		String lastUpdateTime =  getDateString(csi.getLastCopyTime());
		String currentUpdateTime =  getDateString(curUpdateTime);

		Session mfinoSession = MfinoDbHibernateUtil.getSessionFactory().openSession(); 
		Query queryResult = mfinoSession.createQuery("from com.mfino.domain.VisafoneTxnGenerator where LastUpdateTime between "+lastUpdateTime+" and "+currentUpdateTime);
		ScrollableResults sr = queryResult.scroll();
		int readCount = 0;
		Session replicaSession = RepilcaDbHibernateUtil.getSessionFactory().openSession();
		while(sr.next()){ 
			com.mfino.domain.VisafoneTxnGenerator obj = (com.mfino.domain.VisafoneTxnGenerator) sr.get(0);
			mfinoSession.evict(obj);
			com.mfino.domain.VisafoneTxnGenerator existobj = (com.mfino.domain.VisafoneTxnGenerator) replicaSession.get("com.mfino.domain.VisafoneTxnGenerator", obj.getID());
			if(existobj != null){
				existobj.copy( obj);
				replicaSession.beginTransaction();
				replicaSession.saveOrUpdate(existobj);
				replicaSession.getTransaction().commit();
			}else{
				try{
					replicaSession.beginTransaction();
					replicaSession.save(obj);
					replicaSession.getTransaction().commit();
				}catch(Exception e){
					log.error("Error Inserting com.mfino.domain.VisafoneTxnGenerator Domain Object with ID: "+obj.getID(),e);
				}
			}
			readCount++;
			if(readCount % 100 == 0) {
				readCount = 0;
				mfinoSession.flush();
				mfinoSession.clear();
			}
		}
		replicaSession.close();
		mfinoSession.close();
		log.info("Completed Copying com.mfino.domain.VisafoneTxnGenerator Domain Object");

		csi.setLastCopyTime(curUpdateTime);
		dao.save(csi);
	}

	public void callAllMethods(){
		copyCompany();
		copyActivitiesLog();
		copyAddress();
		copyAirtimePurchase();
		copymFinoServiceProvider();
		copyAuthorizingPerson();
		copyBank();
		copyUser();
		copyBankAdmin();
		copyBiller();
		copyBillPayments();
		copyPocketTemplate();
		copyKYCLevel();
		copySubscriber();
		copyBillPaymentTransaction();
		copyBrand();
		copyBulkBankAccount();
		copyRegion();
		copyMerchant();
		copySubscriberMDN();
		copyService();
		copyDistributionChainTemplate();
		copyTransactionType();
		copyDistributionChainLevel();
		copyBulkLOP();
		copyPocket();
		copyBulkUpload();
		copyBulkUploadEntry();
		copyBulkUploadFile();
		copyCardInfo();
		copyChannelCode();
		copyChannelSessionManagement();
		copyChargeType();
		copyPartner();
		copyChargeDefinition();
		copyChargePricing();
		copyChargeTxnCommodityTransferMap();
		copyTransactionsLog();
		copyLOP();
		copyCreditCardTransaction();
		copyCommodityTransfer();
		copyCreditCardDestinations();
		copyDbParam();
		copyDCTRestrictions();
		copyDenomination();
		copyEnumText();
		copyGroup();
		copyMFSBiller();
		copyIntegrationPartnerMapping();
		copyInterBankCode();
		copyInterbankTransfer();
		copyKYCFields();
		copyLedger();
		copyLOPHistory();
		copyMDNRange();
		copyMerchantCode();
		copyMerchantPrefixCode();
		copyMFSBillerPartner();
		copyMFSDenominations();
		copyMobileNetworkOperator();
		copyNotification();
		copyOfflineReport();
		copyOfflineReportForCompany();
		copyOfflineReportReceiver();
		copyPartnerRestrictions();
		copyPartnerServices();
		copyPendingCommodityTransfer();
		copyPendingTransactionsEntry();
		copyPendingTransactionsFile();
		copyPermissionItems();
		copyPerson2Person();
		copyPocketTemplateConfig();
		copyProductIndicator();
		copyReportParameters();
		copyRolePermission();
		copySAPGroupID();
		copyServiceAudit();
		copyServiceChargeTransactionLog();
		copySettlementTemplate();
		copyServiceSettlementConfig();
		copyServiceTransaction();
		copySettlementSchedulerLogs();
		copySettlementTransactionLogs();
		copyTransactionRule();
		copyTransactionCharge();
		copySharePartner();
		copySMSPartner();
		copySMSC();
		copySMSCode();
		copySMSTransactionsLog();
		copySubscriberGroup();
		copySubscribersAdditionalFields();
		copySystemParameters();
		copyTransactionAmountDistributionLog();
		copyTransactionChargeLog();
		copyUnRegisteredTxnInfo();
		copyVisafoneTxnGenerator();
	}




}
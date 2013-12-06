/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.tools.schemachangeupgradetool.dao;

import com.mfino.domain.DbParam;
import com.mfino.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author sandeepjs
 */
public class DbParamDAO {

    public DbParamDAO() {
    }

    public DbParam getDbParam(String name) {

        DbParam dbParam = null;
        Session session = null;
        Transaction transaction = null;

        try {

            session = HibernateUtil.getCurrentSession();
            transaction = session.getTransaction();

            transaction.begin();

            List list = session.createQuery("from DbParam as t where t.name='Version' ").list();

            if (list.size() > 0) {
                dbParam = (DbParam) list.get(0);
            }

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {

            if (session != null && session.isOpen()) {
                session.close();
                session = null;
            }

            if (transaction != null) {
                transaction = null;
            }
        }

        return dbParam;
    }

    public void saveDbParam(DbParam dbParam) {

        Session session = null;
        Transaction transaction = null;

        try {

            session = HibernateUtil.getCurrentSession();
            transaction = session.getTransaction();

            transaction.begin();

            session.saveOrUpdate(dbParam);

            transaction.commit();

        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {

            if (session != null && session.isOpen()) {
                session.close();
                session = null;
            }

            if (transaction != null) {
                transaction = null;
            }
        }

    }
}

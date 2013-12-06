/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.tools.schemachangeupgradetool.database.executors;


import com.mfino.util.HibernateUtil;
import java.util.List;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author sandeepjs
 */
public class HibernateExecutor extends AbstractExecutor {

    @Override
    public boolean executeUpdate() {

        boolean retBooleanVal = true;
        Session session = null;
        Transaction transaction = null;

        try {

            session = HibernateUtil.getCurrentSession();

            transaction = session.getTransaction();

            transaction.begin();

            for (int i = from; i < to; i++) {
                session.createSQLQuery(sqlStatements[i]).executeUpdate();
            }

            transaction.commit();


        } catch (Exception e) {
            retBooleanVal = false;
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

        return retBooleanVal;
    }

    @Override
    public List execute() {

        List retListVal = null;
        Session session = null;
        Transaction transaction = null;

        try {

            session = HibernateUtil.getCurrentSession();
            transaction = session.getTransaction();

            transaction.begin();

            for (int i = from; i < to; i++) {
                retListVal = session.createSQLQuery(sqlStatements[i]).list();
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

        return retListVal;
    }

    @Override
    public void setData(String[] sqlStatements, int from, int to) {
        this.sqlStatements = sqlStatements;
        this.from = from;
        this.to = to;

    }

}

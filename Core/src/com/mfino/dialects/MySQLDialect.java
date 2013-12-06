/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dialects;

import org.hibernate.Hibernate;

/**
 *
 * @author sandeepjs
 */
public class MySQLDialect extends org.hibernate.dialect.MySQLInnoDBDialect {


   public MySQLDialect() {
       super();
        registerFunction("bitwise_and", new MySQLBitwiseAndSQLFunction("bitwise_and", Hibernate.INTEGER));
     
   }

}


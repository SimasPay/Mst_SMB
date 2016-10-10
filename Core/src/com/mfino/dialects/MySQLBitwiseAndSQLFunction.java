/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dialects;

import java.util.List;

import org.hibernate.QueryException;
import org.hibernate.dialect.function.SQLFunction;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.type.Type;

import com.mfino.i18n.MessageText;



/**
 *
 * @author sandeepjs
 */
public class MySQLBitwiseAndSQLFunction extends StandardSQLFunction implements SQLFunction {

   public MySQLBitwiseAndSQLFunction(String name) {
       super(name);
   }

   public MySQLBitwiseAndSQLFunction(String name, Type typeValue) {
       super(name, typeValue);
   }

   public String render(List args, SessionFactoryImplementor factory) throws QueryException {
       if (args.size() != 2){
           throw new IllegalArgumentException(MessageText._("the function must be passed 2 arguments"));
       }
       StringBuffer buffer = new StringBuffer(args.get(0).toString());
       buffer.append(" & ").append(args.get(1));
       return buffer.toString();
   }

}


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import com.mfino.domain.Subscriber;

/**
 *
 * @author Maruthi
 */
public class CreditCardDestinationQuery extends BaseQuery  {
   private Subscriber subscriber;
   private Integer Mdnstatus;


public void setSubscriber(Subscriber subscriber) {
	this.subscriber = subscriber;
}

public Subscriber getSubscriber() {
	return subscriber;
}

public void setMdnstatus(Integer mdnstatus) {
	Mdnstatus = mdnstatus;
}
   
public Integer getMdnstatus() {
	return Mdnstatus;
}


   


   
}

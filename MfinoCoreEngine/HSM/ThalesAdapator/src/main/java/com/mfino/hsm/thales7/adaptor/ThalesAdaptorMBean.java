package com.mfino.hsm.thales7.adaptor;

import org.jdom.Element;

/**
 * MBean interface that need to be implemented by the adaptor
 * @author POCHADRI
 *
 */

public interface ThalesAdaptorMBean extends org.jpos.q2.QBeanSupportMBean  
{
   public Element getPersist ();
   public void setTickInterval(long tickInterval) ;
   public long getTickInterval() ;



  void setReconnectDelay(long delay) ;

  long getReconnectDelay() ;

  void setInQueue(java.lang.String in) ;

  java.lang.String getInQueue() ;

  void setOutQueue(java.lang.String out) ;

  java.lang.String getOutQueue() ;

  void setHost(java.lang.String host) ;

  java.lang.String getHost() ;

  void setPort(int port) ;

  int getPort() ;

  void setSocketFactory(java.lang.String sFac) ;

  java.lang.String getSocketFactory() ;

  public boolean isConnected();
  public void resetCounters ();
  public String getCountersAsString ();
  public int getTXCounter();
  public int getRXCounter();
  public int getConnectsCounter();
  public long getLastTxnTimestampInMillis();
  public long getIdleTimeInMillis();
}

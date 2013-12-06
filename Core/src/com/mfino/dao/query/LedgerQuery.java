/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.dao.query;

import java.util.List;

/**
 *
 * @author Maruthi
 */
@Deprecated
public class LedgerQuery extends BaseQuery  {
  private Long srcPocketID;
  private Long destPocketID;
  private Long sourceDestnPocketID;
  private String sourcenDestMDN;
  private List<Long> transferIDs;
  
public Long getSrcPocketID() {
	return srcPocketID;
}
public void setSrcPocketID(Long srcPocketID) {
	this.srcPocketID = srcPocketID;
}
public Long getDestPocketID() {
	return destPocketID;
}
public void setDestPocketID(Long destPocketID) {
	this.destPocketID = destPocketID;
}
public Long getSourceDestnPocketID() {
	return sourceDestnPocketID;
}
public void setSourceDestnPocketID(Long sourceDestnPocketID) {
	this.sourceDestnPocketID = sourceDestnPocketID;
}
public void setSourcenDestMDN(String sourcenDestMDN) {
	this.sourcenDestMDN = sourcenDestMDN;
}
public String getSourcenDestMDN() {
	return sourcenDestMDN;
}
public List<Long> getTransferIDs() {
	return transferIDs;
}
public void setTransferIDs(List<Long> transferIDs) {
	this.transferIDs = transferIDs;
}
}

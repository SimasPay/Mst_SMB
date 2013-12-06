/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.handset.agent.services.listeners;

import com.mfino.handset.agent.datacontainers.ResponseDataContainer;

/**
 *
 * @author karthik
 */
public interface IResponseListener {
    
    public void responseReceived(ResponseDataContainer responseData);
    
}

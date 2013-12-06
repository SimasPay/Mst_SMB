/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.fix;

/**
 *
 * @author moshiko
 */
public interface IFIXMessageCreator {

	public CFIXMsg Create(Integer MsgType);
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.BankDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.domain.Bank;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSBank;
import com.mfino.uicore.fix.processor.BankProcessor;
import com.mfino.uicore.fix.processor.BaseFixProcessor;

/**
 *
 * @author sunil
 */
@Service("BankProcessorImpl")
public class BankProcessorImpl extends BaseFixProcessor implements BankProcessor{

	@Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSBank realMsg = (CMJSBank) msg;

        BankDAO bankdao = DAOFactory.getInstance().getBankDao();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBank.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBank.CGEntries e : entries) {
                Bank bank = bankdao.getById(e.getID());

                // Check for Stale Data
               if(!e.getRecordVersion().equals(bank.getVersion()))
               {
                    handleStaleDataException();
               }

                updateEntity(bank, e);
                bankdao.save(bank);
                updateMessage(bank, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            List<Bank> results = bankdao.getAll();
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                Bank bank = results.get(i);
                CMJSBank.CGEntries entry =
                        new CMJSBank.CGEntries();
                updateMessage(bank, entry);
                realMsg.getEntries()[i] = entry;
            }
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSBank.CGEntries[] entries = realMsg.getEntries();

            for (CMJSBank.CGEntries e : entries) {
                Bank bank = new Bank();
                updateEntity(bank, e);
                bankdao.save(bank);
                updateMessage(bank, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
        }
        return realMsg;
    }

    public void updateEntity(Bank bank, CMJSBank.CGEntries e) {

        if (e.getID() != null) {
            bank.setID(e.getID());
        }
        if (e.getBankName() != null) {
            bank.setName(e.getBankName());
        }
        if (e.getDescription() != null) {
            bank.setDescription(e.getDescription());
        }

        if (e.getCreateTime() != null) {
            bank.setCreateTime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            bank.setLastUpdateTime(e.getLastUpdateTime());
        }
        if(e.getBankCode()!=null){
        	bank.setBankCode(e.getBankCode());
        }

        if (e.getStatusTime() != null) {
            bank.setStatusTime((e.getStatusTime()));
        }
    }

    public void updateMessage(Bank bank, CMJSBank.CGEntries e) {

        if (bank.getID() != null) {
            e.setID(bank.getID());
        }
        if (bank.getName() != null) {
            e.setBankName(bank.getName());
        }
        if (bank.getDescription() != null) {
            e.setDescription(bank.getDescription());
        }

        if (bank.getCreateTime() != null) {
            e.setCreateTime(bank.getCreateTime());
        }
        if (bank.getLastUpdateTime() != null) {
            e.setLastUpdateTime(bank.getLastUpdateTime());
        }

        if (bank.getStatusTime() != null) {
            e.setStatusTime(bank.getStatusTime());
        }
        if(bank.getBankCode()!=null){
        	e.setBankCode(bank.getBankCode());
        }

        if(bank.getVersion() != null)
        {
            e.setRecordVersion(bank.getVersion());
        }
    }
}

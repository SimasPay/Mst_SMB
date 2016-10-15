/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.math.BigDecimal;
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
            bank.setId(e.getID());
        }
        if (e.getBankName() != null) {
            bank.setName(e.getBankName());
        }
        if (e.getDescription() != null) {
            bank.setDescription(e.getDescription());
        }

        if (e.getCreateTime() != null) {
            bank.setCreatetime(e.getCreateTime());
        }
        if (e.getLastUpdateTime() != null) {
            bank.setLastupdatetime(e.getLastUpdateTime());
        }
        if(e.getBankCode()!=null){
        	bank.setBankcode(e.getBankCode().longValue());
        }

        if (e.getStatusTime() != null) {
            bank.setStatustime((e.getStatusTime()));
        }
    }

    public void updateMessage(Bank bank, CMJSBank.CGEntries e) {

        if (bank.getId() != null) {
            e.setID(bank.getId().longValue());
        }
        if (bank.getName() != null) {
            e.setBankName(bank.getName());
        }
        if (bank.getDescription() != null) {
            e.setDescription(bank.getDescription());
        }

        if (bank.getCreatetime() != null) {
            e.setCreateTime(bank.getCreatetime());
        }
        if (bank.getLastupdatetime() != null) {
            e.setLastUpdateTime(bank.getLastupdatetime());
        }

        if (bank.getStatustime() != null) {
            e.setStatusTime(bank.getStatustime());
        }
        if(bank.getBankcode()!=null){
        	e.setBankCode(bank.getBankcode().intValue());
        }

        if((Long)bank.getVersion() != null)
        {
            e.setRecordVersion(((Long)bank.getVersion()).intValue());
        }
    }
}

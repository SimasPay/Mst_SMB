/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.DAOFactory;
import com.mfino.dao.DistributionChainLevelDAO;
import com.mfino.dao.DistributionChainTemplateDAO;
import com.mfino.dao.TransactionTypeDAO;
import com.mfino.dao.query.DistributionChainLevelQuery;
import com.mfino.domain.DistributionChainLevel;
import com.mfino.domain.DistributionChainTemplate;
import com.mfino.domain.TransactionType;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSDistributionChainLevel;
import com.mfino.i18n.MessageText;
import com.mfino.service.impl.DCTRestrictionsServiceImpl;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.DistributionChainLevelProcessor;

/**
 *
 * @author xchen
 */
@Service("DistributionChainLevelProcessorImpl")
public class DistributionChainLevelProcessorImpl extends BaseFixProcessor implements DistributionChainLevelProcessor{

    private DistributionChainLevelDAO levelDAO = DAOFactory.getInstance().getDistributionChainLevelDAO();
    private DistributionChainTemplateDAO templateDAO = DAOFactory.getInstance().getDistributionChainTemplateDAO();

    private void updateMessage(DistributionChainLevel e,
            CMJSDistributionChainLevel.CGEntries m) {
        m.setID(e.getId().longValue());
        m.setDistributionChainTemplateID(e.getDistributionChainTemp().getId().longValue());
        m.setDistributionLevel(((Long)e.getDistributionlevel()).intValue());

        int perm = ((Long)e.getPermissions()).intValue();
        m.setDirectDistributeAllowed((perm & CmFinoFIX.DistributionPermissions_DirectDistribute) > 0);
        m.setIndirectDistributeAllowed((perm & CmFinoFIX.DistributionPermissions_IndirectDistribute) > 0);
        m.setDirectTransferAllowed((perm & CmFinoFIX.DistributionPermissions_DirectTransfer) > 0);
        m.setIndirectTransferAllowed((perm & CmFinoFIX.DistributionPermissions_IndirectTransfer) > 0);
        m.setRechargeAllowed((perm & CmFinoFIX.DistributionPermissions_Recharge) > 0);
        m.setGenerateLOPAllowed((perm & CmFinoFIX.DistributionPermissions_LOP) > 0);
        m.setLOPDistributeAllowed((perm & CmFinoFIX.DistributionPermissions_LOPDistribute) > 0);

        m.setLOPCommission(e.getCommission());
        m.setLOPMaxCommission(e.getMaxcommission());
        m.setLOPMinCommission(e.getMincommission());
        m.setMaxWeeklyPurchaseAmount(e.getMaxweeklylopamount());
        m.setMaxAmountPerTransaction(e.getMaxlopamount());
        if (null != ((Long)e.getVersion())) {
            m.setRecordVersion(((Long)e.getVersion()).intValue());
        }
        if(null != e.getTransactiontypeid()){
        	TransactionTypeDAO transactionTypeDAO = DAOFactory.getInstance().getTransactionTypeDAO();
        	TransactionType transactionType = transactionTypeDAO.getById(e.getTransactiontypeid());
        	m.setTransactionName(transactionType.getTransactionname());
        	m.setTransactionID(e.getTransactiontypeid());
        }
    }

    private void updateEntity(DistributionChainLevel e,
            CMJSDistributionChainLevel.CGEntries m) {
    	
    	TransactionTypeDAO transactionTypeDao = DAOFactory.getInstance().getTransactionTypeDAO();
    	
        if (m.getDistributionChainTemplateID() != null) {
            DistributionChainTemplate template = templateDAO.getById(m.getDistributionChainTemplateID());
            e.setDistributionChainTemp(template);
        }
        if (m.getDistributionLevel() != null) {
            e.setDistributionlevel(m.getDistributionLevel());
        }

        if (m.getPermissions() != null) {
            e.setPermissions(m.getPermissions());
        }

        if (m.getLOPCommission() != null) {
            e.setCommission(m.getLOPCommission());
        }
        if(m.getLOPMaxCommission() != null){
            e.setMaxcommission(m.getLOPMaxCommission());
        }
        if(m.getLOPMinCommission() != null){
            e.setMincommission(m.getLOPMinCommission());
        }
        if (m.getMaxWeeklyPurchaseAmount() != null) {
            e.setMaxweeklylopamount(m.getMaxWeeklyPurchaseAmount());
        }
        if (m.getMaxAmountPerTransaction() != null) {
            e.setMaxlopamount(m.getMaxAmountPerTransaction());
        }
        if(null != m.getTransactionID()){
        	TransactionType transactionType = transactionTypeDao.getById(m.getTransactionID());
        	e.setTransactiontypeid(transactionType.getId().longValue());
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSDistributionChainLevel realMsg = (CMJSDistributionChainLevel) msg;

        if (CmFinoFIX.JSaction_Update.equals(realMsg.getaction())) {
            CMJSDistributionChainLevel.CGEntries[] entries = realMsg.getEntries();

            for (CMJSDistributionChainLevel.CGEntries e : entries) {
                DistributionChainLevel s = levelDAO.getById(e.getID());

                // Check for Stale Data
                if (!e.getRecordVersion().equals(s.getVersion())) {
                    handleStaleDataException();
                }

                updateEntity(s, e);
                levelDAO.save(s);
                updateMessage(s, e);
            }

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Select.equals(realMsg.getaction())) {
            DistributionChainLevelQuery query = new DistributionChainLevelQuery();
            query.setId(realMsg.getIDSearch());
            query.setDistributionChainTemplateID(realMsg.getDistributionChainTemplateID());
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            // Ordering the results by level in ascending order.
            //Level:asc
            query.setSortString("DistributionLevel:asc");
            List<DistributionChainLevel> results = levelDAO.get(query);
            realMsg.allocateEntries(results.size());
            for (int i = 0; i < results.size(); i++) {
                DistributionChainLevel s = results.get(i);
                CMJSDistributionChainLevel.CGEntries entry =
                        new CMJSDistributionChainLevel.CGEntries();
                updateMessage(s, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
        	DCTRestrictionsServiceImpl dctRestrictionsService = new DCTRestrictionsServiceImpl();
            CMJSDistributionChainLevel.CGEntries[] entries = realMsg.getEntries();

            for (CMJSDistributionChainLevel.CGEntries e : entries) {
                DistributionChainLevel s = new DistributionChainLevel();
                updateEntity(s, e);
                levelDAO.save(s);
                updateMessage(s, e);
                dctRestrictionsService.createDefautlRestrictions(s);
            }
            
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSDistributionChainLevel.CGEntries[] entries = realMsg.getEntries();
            DistributionChainTemplate template = null;

            DCTRestrictionsServiceImpl dctRestrictionsService = new DCTRestrictionsServiceImpl();
            List<DistributionChainLevel> deletedLevels = new ArrayList<DistributionChainLevel>();

            for (CMJSDistributionChainLevel.CGEntries e : entries) {
                DistributionChainLevel aLevel = levelDAO.getById(e.getID());
                
                if (null == template) {
                    template = aLevel.getDistributionChainTemp();
                }
                try {
                    levelDAO.deleteById(e.getID());
                    deletedLevels.add(aLevel);
                } catch (ConstraintViolationException ex) {
                    CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
                    String message = MessageText._("Could not delete this level." );
                    errorMsg.setErrorDescription(message);
                    errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                    log.warn(message, ex);
                    deletedLevels.remove(aLevel);
                    return errorMsg;
                }
            }
            
            dctRestrictionsService.deleteRestrictions(deletedLevels);
            //adjustLevels(template, deletedLevels);

            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }

    private void adjustLevels(DistributionChainTemplate template, List<Integer> deletedLevels) {
        if (template == null) {
            throw new IllegalArgumentException(MessageText._("Invalid DistributionChainTemplate"));
        }

        Set<DistributionChainLevel> levels = template.getDistributionChainLvls();
        @SuppressWarnings("unchecked")
        SortedSet levelSet = new TreeSet(new LevelComparator());
        @SuppressWarnings("unchecked")
        boolean justBoolean = levelSet.addAll(levels);
        //Collections.sort(levels, new LevelComparator());
        @SuppressWarnings("unchecked")
        Iterator<DistributionChainLevel> iter = levelSet.iterator();

        while (iter.hasNext()) {
            int toDecrement = 0;
            DistributionChainLevel aLevel = iter.next();
            int currentLevel = ((Long)aLevel.getDistributionlevel()).intValue();

            if (deletedLevels.contains(aLevel.getDistributionlevel())) {
                continue;
            }

            for (Integer deletedLevel : deletedLevels) {
                if (currentLevel > deletedLevel) {
                    toDecrement++;
                }
            }
            aLevel.setDistributionlevel(currentLevel - toDecrement);
            log.info(currentLevel + " --> " + (currentLevel - toDecrement));

            if (toDecrement > 0) {
                levelDAO.save(aLevel);
            }
        }
    }

    private class LevelComparator implements Comparator<Object> {

        public int compare(Object o1, Object o2) {
            if (o1 instanceof DistributionChainLevel && o2 instanceof DistributionChainLevel) {
                DistributionChainLevel level1 = (DistributionChainLevel) o1;
                DistributionChainLevel level2 = (DistributionChainLevel) o2;
                Long distributionlevel1 = level1.getDistributionlevel();
                Long distributionlevel2 = level2.getDistributionlevel();
                return distributionlevel1.compareTo(distributionlevel2);
            }

            return 0;
        }
    }
}

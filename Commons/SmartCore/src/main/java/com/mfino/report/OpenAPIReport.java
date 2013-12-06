/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.report;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mfino.dao.CommodityTransferDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.PendingCommodityTransferDAO;
import com.mfino.dao.TransactionsLogDAO;
import com.mfino.domain.ActivitiesLog;
import com.mfino.domain.TransactionsLog;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CRCommodityTransfer;
import com.mfino.service.ActivitiesLogService;
import com.mfino.service.EnumTextService;
import com.mfino.service.TransactionsLogService;
import com.mfino.util.ConfigurationUtil;
import com.mfino.util.HibernateUtil;
import com.mfino.util.OfflineReportUtil;

/**
 *
 * @author Siddhartha Chinthapally
 */
public class OpenAPIReport extends OfflineReportBase {


    private CommodityTransferDAO ctDAO = DAOFactory.getInstance().getCommodityTransferDAO();
    private PendingCommodityTransferDAO pctDAO = DAOFactory.getInstance().getPendingCommodityTransferDAO();
    private static final int NUM_COLUMNS = 7;
    private static final String HEADER_ROW = "#,External Element,Reference Number,Function called to External Element," +
            "Time request sent to external element,Time response received from external element,Return Code";

    @Override
    public String getReportName() {
        return "OpenAPIReport";
    }

    @Override
    public File run(Date start, Date end) {
        return run(start,end,null);
    }

    @Override
    public File run(Date start, Date end, Long companyID) {
        File reportFile = getReportFilePath();
        
        TransactionsLogDAO tlDAO = DAOFactory.getInstance().getTransactionsLogDAO();

        try {
            HibernateUtil.getCurrentSession().beginTransaction();
            int firstResult = 0;
            int count = tlDAO.getAllTxnCountBetween(start, end);
            int batchSize = ConfigurationUtil.getOpenAPIReportBatchSize();
            int seq = 1;
            PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(reportFile)));
            writer.println(HEADER_ROW);

            // Get all TxnsLog records with Null Parent Transactions ID.
            while(firstResult < count) {
              List<TransactionsLog> allTxns = TransactionsLogService.getAllTxnsBetween(start, end, firstResult, batchSize);
              seq = reportForBatch(allTxns, writer, seq);
              firstResult += allTxns.size();
              
              HibernateUtil.getCurrentSession().clear();
              allTxns.clear();
              System.gc();
            }
            writer.close();
                        
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            return reportFile;
        } catch (Throwable t) {
            HibernateUtil.getCurrentSession().getTransaction().rollback();
            log.error("Error in OpenAPIReport", t);
        }
        return reportFile;
    }


    private boolean containsType(Integer msgCode, List<TransactionsLog> transactions) {

        if(msgCode == null)
            return false;

        for(TransactionsLog txn : transactions){
            if(msgCode.equals(txn.getMessageCode()))
                return true;
        }
        return false;
    }
    
    private int reportForBatch(List<TransactionsLog> allTxns, PrintWriter writer, int seq) {
      DateFormat df = getDateFormat();
      String formatStr = getFormatString(NUM_COLUMNS);
      List<TransactionsLog> parentTxns = new LinkedList<TransactionsLog>();
      Map <Long, ArrayList<TransactionsLog>> parentIdVsChildTxns = new HashMap<Long, ArrayList<TransactionsLog>>();
      for(TransactionsLog aTxn: allTxns) {
        Long parentTxnID = aTxn.getParentTransactionID();
        if(null == parentTxnID) {
          parentTxns.add(aTxn);                
        } else {                
          ArrayList<TransactionsLog> childTxns = parentIdVsChildTxns.get(parentTxnID);
          if(childTxns == null) {
            childTxns = new ArrayList<TransactionsLog>();
          }
          childTxns.add(aTxn);
          parentIdVsChildTxns.put(parentTxnID, childTxns);
        }
      }

      for (TransactionsLog parentTxn : parentTxns) {                
        Long parentTxnId = parentTxn.getID();                
        ActivitiesLog activitiesLog = ActivitiesLogService.getRecordForThisParentTxnId(parentTxnId);

        if (null == activitiesLog) {
          //DefaultLogger.warn("Potentially spurious parent Txn encountered. ID = " + parentTxnId);
          continue;
        }

        List<TransactionsLog> childTxns = parentIdVsChildTxns.get(parentTxnId);// TransactionsLogService.getTxnsByParentTxnId(parentTxnId);

        List<TransactionsLog> completeRequestList = new ArrayList<TransactionsLog>();
        if(null != childTxns)
          completeRequestList.addAll(childTxns);
        completeRequestList.add(0, parentTxn);

        for (TransactionsLog txn : completeRequestList) {
          String externalElement = null;
          boolean isOperatorCall = false;
          Integer messageCode = txn.getMessageCode();
          if (OfflineReportUtil.isCallToBank(messageCode)) {                        
            externalElement = EnumTextService.getEnumTextValue( CmFinoFIX.TagID_BankCodeForRouting, null,
                activitiesLog.getISO8583_AcquiringInstIdCode());                    
          } else if (OfflineReportUtil.isCallToOperator(messageCode)) {                        
            // TODO ::  we need to find a better way. The Operrator code is hidden inside the msgdata!
            isOperatorCall = true;
            externalElement = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorCodeForRouting, null, CmFinoFIX.OperatorCodeForRouting_CBOSS);
          } else {
            continue;
          }

          Integer responseType = OfflineReportUtil.getResponseMsgType(messageCode);
          Integer reversalType = OfflineReportUtil.getReversalMsgType(messageCode);
          Date responseTime = null;

          // Now iterate and get see if the response record is there.
          // For Bank Reversal Txns the response belongs to the last Bank reversal Txn only.
          TransactionsLog response = null;
          for (TransactionsLog potentialResponse : childTxns) {
            if(potentialResponse.getID().equals(txn.getID()))
              continue;
            if (potentialResponse.getMessageCode().equals(responseType)) {
              response = potentialResponse;
              responseTime = potentialResponse.getTransactionTime();
            } else if (potentialResponse.getMessageCode().equals(messageCode)) {
              // If you encounter the same msg twice in the child Txn list then it is most likely a duplicate reversal.
              // Which means that this reversal hasn't got a reply.
              // Don't search for a response for it
              break;
            }
          }

          // No response yet and there is no reversal sent. This means the request has been rejected!
          // Check for generic response
          if(response == null && reversalType != null && !containsType(reversalType, childTxns)){
            responseType = CmFinoFIX.MsgType_BankResponse;
            for (TransactionsLog potentialResponse : childTxns) {
              if(potentialResponse.getID().equals(txn.getID()))
                continue;
              if (potentialResponse.getMessageCode().equals(responseType)) {
                response = potentialResponse;
                responseTime = potentialResponse.getTransactionTime();
              }
            }
          }

          childTxns.remove(txn);
          if(response != null)
            childTxns.remove(response);

          Date requestTime = txn.getTransactionTime();
          Long transferID = activitiesLog.getTransferID();

          CRCommodityTransfer ct = null;

          if (transferID != null) {
            ct = ctDAO.getById(transferID);
            if (ct == null) {
              ct = pctDAO.getById(transferID);
            }
          }

          if(StringUtils.isEmpty(externalElement))
            externalElement = (ct != null) ? EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, ct.getBankCode()) : null;

          if(StringUtils.isEmpty(externalElement)) {
            //FIXME: If we reach here then the bank code is hidden in the msg data.
            // For now I am using Sinarmas as default.
//            externalElement = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, CmFinoFIX.BankCodeForRouting_Sinarmas);
              externalElement = "153";   // hard coding Sinarmas bank as 153
          }

          String functionToExternalCall = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_MsgType, null, txn.getMessageCode());
          if(isOperatorCall && ct != null) {
            functionToExternalCall = EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory());
          }

          if (StringUtils.isEmpty(functionToExternalCall)) {
            functionToExternalCall = OfflineReportUtil.getUICategory(messageCode,
                activitiesLog.getSourceMDN(), null, activitiesLog.getServletPath(), null, null);
          }

          Integer notificationCode = activitiesLog.getNotificationCode();

          writer.println(String.format(formatStr,
              seq,
              externalElement,
              transferID,
              functionToExternalCall,
              df.format(requestTime),
              responseTime != null ? df.format(responseTime) : "",
              notificationCode));
          seq++;
        } // end of for
      }
      return seq;
    }
}

//@Override
//public File run(Date start, Date end) {
//  File reportFile = getReportFilePath();
//  DateFormat df = getDateFormat();
//  String formatStr = getFormatString(NUM_COLUMNS);
//  CommodityTransferDAO ctDAO = new CommodityTransferDAO();
//
//  try {
//      HibernateUtil.getCurrentSession().beginTransaction();
//      CommodityTransferQuery query = new CommodityTransferQuery();
//
//      //query.setTransferStatus(CmFinoFIX.TransferStatus_Completed);
//      query.setHasExternalCall(true);
//      query.setCreateTimeGE(start);
//      query.setCreateTimeLT(end);
//      List<CommodityTransfer> results = ctDAO.get(query);
//      PrintWriter writer = new PrintWriter(new FileWriter(reportFile));
//      int seq = 1;
//
//      writer.println(HEADER_ROW);
//      for (CommodityTransfer ct : results) {
//          boolean opCallFollowsBankCall = false;
//
//          if (isBankCall(ct)) {
//              opCallFollowsBankCall = true;
//              writer.println(String.format(formatStr,
//                      seq,
//                      EnumTextService.getEnumTextValue(CmFinoFIX.TagID_BankCodeForRouting, null, ct.getBankCode()),
//                      ct.getID(),
//                      EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),
//                      df.format(ct.getStartTime()),
//                      ct.getBankResponseTime() !=null
//                          ? df.format(ct.getBankResponseTime())
//                          :"",
//                      ct.getNotificationCode()
//                      )
//              );
//              seq++;
//          }
//
//          if (isOperatorCall(ct)) {
//              writer.println(String.format(formatStr,
//                      seq,
//                      EnumTextService.getEnumTextValue(CmFinoFIX.TagID_OperatorCodeForRouting, null, ct.getOperatorCode()),
//                      ct.getID(),
//                      EnumTextService.getEnumTextValue(CmFinoFIX.TagID_TransactionUICategory, null, ct.getUICategory()),
//                      (opCallFollowsBankCall) ? df.format(ct.getBankResponseTime()) : df.format(ct.getStartTime()),
//                      df.format(ct.getOperatorResponseTime()),
//                      ct.getNotificationCode()
//                      )
//              );
//              seq++;
//          }
//      }
//      writer.close();
//      HibernateUtil.getCurrentSession().getTransaction().commit();
//      return reportFile;
//  } catch (Throwable t) {
//      HibernateUtil.getCurrentSession().getTransaction().rollback();
//      DefaultLogger.error("Error in OpenAPIReport", t);
//  }
//  return reportFile;
//
//}
//
//private boolean isOperatorCall(CommodityTransfer ct) {
//  if (ct.getOperatorResponseCode() != null || ct.getOperatorResponseTime() != null) {
//      return true;
//  }
//  return false;
//}
//
//private boolean isBankCall(CommodityTransfer ct) {
//  if (ct.getBankCode() != null || ct.getBankResponseCode() != null || ct.getBankResponseTime() != null) {
//      return true;
//  }
//  return false;
//}

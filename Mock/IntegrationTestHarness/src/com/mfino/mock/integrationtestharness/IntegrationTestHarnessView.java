/*
 * IntegrationTestHarnessView.java
 */

package com.mfino.mock.integrationtestharness;

import com.mfino.mock.integrationtestharness.commons.ITHConstants;
import com.mfino.mock.integrationtestharness.commons.ITHLogger;
import com.mfino.mock.testharnessbli.TestHarnessBLI;
import com.mfino.mock.testharnessbliimpl.Analyzer;
import com.mfino.mock.testharnessbliimpl.TestHarnessBLIFactoryIMPL;
import com.mfino.mock.testharnessbliimpl.TestHarnessBLIFactoryIMPL.HarnessType;
import com.mfino.mock.testharnessbliimpl.TestHarnessValueObject;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
//import java.util.logging.Level;
//import java.util.logging.Logger;
import java.util.Iterator;
import java.util.Vector;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import org.slf4j.Logger;

/**
 * The application's main frame.
 */
public class IntegrationTestHarnessView extends FrameView {

    public IntegrationTestHarnessView(SingleFrameApplication app) {
        super(app);

        initComponents();
        IntTestHarnessTabbedPane.setSelectedComponent(apiChooserPanel);

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        
        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    //progressBar.setVisible(true);
                    //progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
//                    progressBar.setVisible(false);
//                    progressBar.setValue("");
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
//                    progressBar.setVisible(true);
//                    progressBar.setIndeterminate(false);
//                    progressBar.setValue(value);
                }
            }
        });
    }
    @Action
    public void showAPITabbedPane() {
        serviceName="";
        String action= buttonGroup1.getSelection().getActionCommand();
        if((action).equalsIgnoreCase(ITHConstants.SUBSRICPTION_ACTIVATION)){
            IntTestHarnessTabbedPane.setSelectedComponent(SubcriptionActivationPane);
            serviceName=ITHConstants.SUBSRICPTION_ACTIVATION;
        }else if((action).equalsIgnoreCase(ITHConstants.CHANGE_PIN)){
            IntTestHarnessTabbedPane.setSelectedComponent(changePinPane);
            serviceName=ITHConstants.CHANGE_PIN;
        }else if((action).equalsIgnoreCase(ITHConstants.RESET_PIN)){
            IntTestHarnessTabbedPane.setSelectedComponent(resetPinPane);
            serviceName=ITHConstants.RESET_PIN;
        }else if((action).equalsIgnoreCase(ITHConstants.GET_TRANSACTIONS)){
            IntTestHarnessTabbedPane.setSelectedComponent(getTransactionsPane);
            serviceName=ITHConstants.GET_TRANSACTIONS;
        }else if((action).equalsIgnoreCase(ITHConstants.MOBILE_AGENT_RECHARGE)){
            IntTestHarnessTabbedPane.setSelectedComponent(mobileAgentRechargePane);
            serviceName=ITHConstants.MOBILE_AGENT_RECHARGE;
        }else if((action).equalsIgnoreCase(ITHConstants.MCASH_TOPUP)){
            IntTestHarnessTabbedPane.setSelectedComponent(mCashTopupPane);
            serviceName=ITHConstants.MCASH_TOPUP;
        }else if((action).equalsIgnoreCase(ITHConstants.CHECK_BALANCE)){
            IntTestHarnessTabbedPane.setSelectedComponent(checkBalancePane);
            serviceName=ITHConstants.CHECK_BALANCE;
        }else if((action).equalsIgnoreCase(ITHConstants.CHANGE_MCASH_PIN)){
            IntTestHarnessTabbedPane.setSelectedComponent(changeMcashPinPane);
            serviceName=ITHConstants.CHANGE_MCASH_PIN;
        }else if((action).equalsIgnoreCase(ITHConstants.MERCHANT_MPIN_RESET)){
            IntTestHarnessTabbedPane.setSelectedComponent(merchantMpinResetPane);
            serviceName=ITHConstants.MERCHANT_MPIN_RESET;
        }else if((action).equalsIgnoreCase(ITHConstants.GET_MCASH_TRANSACTIONS)){
            IntTestHarnessTabbedPane.setSelectedComponent(getMcashTransactionsPane);
            serviceName=ITHConstants.GET_MCASH_TRANSACTIONS;
        }else if((action).equalsIgnoreCase(ITHConstants.MCASH_TO_MCASH)){
            IntTestHarnessTabbedPane.setSelectedComponent(mcashToMcashPane);
            serviceName=ITHConstants.MCASH_TO_MCASH;
        }else if((action).equalsIgnoreCase(ITHConstants.SHARE_LOAD)){
            IntTestHarnessTabbedPane.setSelectedComponent(shareLoadPane);
            serviceName=ITHConstants.SHARE_LOAD;
        }else if((action).equalsIgnoreCase(ITHConstants.MCASH_BALANCE_INQUIRY)){
            IntTestHarnessTabbedPane.setSelectedComponent(mcashBalanceInquiryPane);
            serviceName=ITHConstants.MCASH_BALANCE_INQUIRY;
        }else if((action).equalsIgnoreCase(ITHConstants.MOBILE_AGENT_DISTRIBUTE)){
            IntTestHarnessTabbedPane.setSelectedComponent(mobileAgentDistributePane);
            serviceName=ITHConstants.MOBILE_AGENT_DISTRIBUTE;
        }
        
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = IntegrationTestHarnessApp.getApplication().getMainFrame();
            aboutBox = new IntegrationTestHarnessAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        IntegrationTestHarnessApp.getApplication().show(aboutBox);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        IntTestHarnessTabbedPane = new javax.swing.JTabbedPane();
        apiChooserPanel = new javax.swing.JPanel();
        subcriptionActivationRadio = new javax.swing.JRadioButton();
        changepinRadio = new javax.swing.JRadioButton();
        ResetPinRadio = new javax.swing.JRadioButton();
        getTransactionsRadio = new javax.swing.JRadioButton();
        mobileAgentRechargeRadio = new javax.swing.JRadioButton();
        mcashTopupRadio = new javax.swing.JRadioButton();
        changeMcashPinRadio = new javax.swing.JRadioButton();
        checkBalanceRadio = new javax.swing.JRadioButton();
        merchantMpinResetRadio = new javax.swing.JRadioButton();
        nextAPIButton = new javax.swing.JButton();
        mcashToMcashRadio = new javax.swing.JRadioButton();
        getMcashTransactionsRadio = new javax.swing.JRadioButton();
        shareLoadRadio = new javax.swing.JRadioButton();
        mcashBalanceInquiryRadio = new javax.swing.JRadioButton();
        mobileAgentDistributeRadio = new javax.swing.JRadioButton();
        frequencyTest = new javax.swing.JRadioButton();
        SubcriptionActivationPane = new javax.swing.JPanel();
        SMSPinLabel = new javax.swing.JLabel();
        SMSPinTextBox = new javax.swing.JTextField();
        SMSSourceMsisdnTextBox = new javax.swing.JTextField();
        SMSSecretAnswerTextBox = new javax.swing.JTextField();
        SMSContactNumberTextBox = new javax.swing.JTextField();
        SMSSourceMsisdnLabel = new javax.swing.JLabel();
        SMSSecretAnswerLabel = new javax.swing.JLabel();
        SMSContactNumberLabel = new javax.swing.JLabel();
        SubmitSubsriptionActivation = new javax.swing.JButton();
        frequencyPane = new javax.swing.JPanel();
        jLabel29 = new javax.swing.JLabel();
        requestPerMinTF = new javax.swing.JTextField();
        SubmitFrequencyTest = new javax.swing.JButton();
        CBSubcriptionActivation = new javax.swing.JCheckBox();
        CBChange_Pin = new javax.swing.JCheckBox();
        CBReset_Pin = new javax.swing.JCheckBox();
        CBGet_Transactions = new javax.swing.JCheckBox();
        CBMobile_Agent_Recharge = new javax.swing.JCheckBox();
        CBMcash_Topup = new javax.swing.JCheckBox();
        CBCheck_Balance = new javax.swing.JCheckBox();
        CBMerchant_Mpin_Reset = new javax.swing.JCheckBox();
        CBMcash_Mcash = new javax.swing.JCheckBox();
        CBGet_MCash_Transactions = new javax.swing.JCheckBox();
        CBShare_Load = new javax.swing.JCheckBox();
        CBMCash_Balance_Inquiry = new javax.swing.JCheckBox();
        CBMobile_Agent_Distribute = new javax.swing.JCheckBox();
        resetPinPane = new javax.swing.JPanel();
        SubmitMpinReset = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        mrSMSNewPinTF = new javax.swing.JTextField();
        mrSecretAnswerTF = new javax.swing.JTextField();
        mrSourceMsisdnTF = new javax.swing.JTextField();
        checkBalancePane = new javax.swing.JPanel();
        SubmitCheckInventoryPane = new javax.swing.JButton();
        SMSSourceMsisdnLabel1 = new javax.swing.JLabel();
        ciSMSPin = new javax.swing.JTextField();
        ciSourceMsisdn = new javax.swing.JTextField();
        SMSPinLabel1 = new javax.swing.JLabel();
        merchantMpinResetPane = new javax.swing.JPanel();
        SubmitMerchantMpinReset = new javax.swing.JButton();
        mmrContactNumber = new javax.swing.JTextField();
        mmrSecretAnswer = new javax.swing.JTextField();
        SMSSecretAnswerLabel3 = new javax.swing.JLabel();
        mmrNewPin = new javax.swing.JTextField();
        SMSContactNumberLabel3 = new javax.swing.JLabel();
        SMSPinLabel3 = new javax.swing.JLabel();
        getTransactionsPane = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        l3tSMSPinTF = new javax.swing.JTextField();
        SubmitLast3Transactions = new javax.swing.JButton();
        l3tSMSSourceMsisdnTF1 = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        mobileAgentRechargePane = new javax.swing.JPanel();
        topupSMSSourceMsisdnTF = new javax.swing.JTextField();
        SubmitTopup = new javax.swing.JButton();
        SMSContactNumberLabel4 = new javax.swing.JLabel();
        topupSMSDestMsisdnTF = new javax.swing.JTextField();
        SMSPinLabel4 = new javax.swing.JLabel();
        SMSSourceMsisdnLabel4 = new javax.swing.JLabel();
        topupSMSBucketTypeTF = new javax.swing.JTextField();
        SMSSecretAnswerLabel4 = new javax.swing.JLabel();
        topupSMSPinTF = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        topupSMSRechargeAmountTF = new javax.swing.JTextField();
        changeMcashPinPane = new javax.swing.JPanel();
        SubmitChangeMerchantPin = new javax.swing.JButton();
        SMSSourceMsisdnLabel2 = new javax.swing.JLabel();
        cmpSourceMsisdn = new javax.swing.JTextField();
        SMSSecretAnswerLabel2 = new javax.swing.JLabel();
        cmpOldPin = new javax.swing.JTextField();
        cmpNewPin = new javax.swing.JTextField();
        SMSPinLabel2 = new javax.swing.JLabel();
        changePinPane = new javax.swing.JPanel();
        SubmitChangeMpin = new javax.swing.JButton();
        SMSOldPinlabel = new javax.swing.JLabel();
        SMSNewPinLabel = new javax.swing.JLabel();
        cmpSMSSourceMsisdnLabel = new javax.swing.JLabel();
        cmSMSOldPin = new javax.swing.JTextField();
        cmSMSNewPin = new javax.swing.JTextField();
        cmSMSSourceMsisdn = new javax.swing.JTextField();
        mCashTopupPane = new javax.swing.JPanel();
        SubmitTopupMerchant = new javax.swing.JButton();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        tmSMSMcashPin = new javax.swing.JTextField();
        tmSourceMsisdn = new javax.swing.JTextField();
        tmDistributeAmount = new javax.swing.JTextField();
        tmDestMsisdn = new javax.swing.JTextField();
        tmSMSAmount = new javax.swing.JTextField();
        jLabel5 = new javax.swing.JLabel();
        getMcashTransactionsPane = new javax.swing.JPanel();
        submitMcashTransactions = new javax.swing.JButton();
        jLabel23 = new javax.swing.JLabel();
        gmtSMS_mCashPin = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        gmtSMS_sourceMsisdn = new javax.swing.JTextField();
        mcashToMcashPane = new javax.swing.JPanel();
        mCashToMcashButton = new javax.swing.JButton();
        jLabel16 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        m2mSMS_mCashPin = new javax.swing.JTextField();
        m2mSMS_SecretAnswer = new javax.swing.JTextField();
        m2mSMS_sourceMsisdn = new javax.swing.JTextField();
        m2mSMS_destMsisdn = new javax.swing.JTextField();
        m2mSMS_mCashMessage = new javax.swing.JTextField();
        shareLoadPane = new javax.swing.JPanel();
        submitShareLoadButton = new javax.swing.JButton();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        slSMSNewPin = new javax.swing.JTextField();
        slSMSRechargeAmount = new javax.swing.JTextField();
        slSourceMsisdn = new javax.swing.JTextField();
        slSMSDestMsisdn = new javax.swing.JTextField();
        mcashBalanceInquiryPane = new javax.swing.JPanel();
        mCashBalaneInquiryButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        mbiSMS_mCashPin = new javax.swing.JTextField();
        mbiSMS_sourceMsisdn = new javax.swing.JTextField();
        mobileAgentDistributePane = new javax.swing.JPanel();
        mobileAgentDistributeButton = new javax.swing.JButton();
        jLabel25 = new javax.swing.JLabel();
        jLabel26 = new javax.swing.JLabel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        madSMS_Pin = new javax.swing.JTextField();
        madSMS_sourceMsisdn = new javax.swing.JTextField();
        madSMS_destMsisdn = new javax.swing.JTextField();
        madSMS_distributeAmount = new javax.swing.JTextField();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        buttonGroup1 = new javax.swing.ButtonGroup();

        mainPanel.setName("mainPanel"); // NOI18N

        IntTestHarnessTabbedPane.setName("apiChooser"); // NOI18N

        apiChooserPanel.setName("apiChooserPanel"); // NOI18N

        buttonGroup1.add(subcriptionActivationRadio);
        subcriptionActivationRadio.setSelected(true);
        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.mfino.mock.integrationtestharness.IntegrationTestHarnessApp.class).getContext().getResourceMap(IntegrationTestHarnessView.class);
        subcriptionActivationRadio.setText(resourceMap.getString("subsricption_activation.text")); // NOI18N
        subcriptionActivationRadio.setName("subsricption_activation"); // NOI18N

        buttonGroup1.add(changepinRadio);
        changepinRadio.setText(resourceMap.getString("change_mpin.text")); // NOI18N
        changepinRadio.setName("change_mpin"); // NOI18N

        buttonGroup1.add(ResetPinRadio);
        ResetPinRadio.setText(resourceMap.getString("mpin_reset.text")); // NOI18N
        ResetPinRadio.setName("mpin_reset"); // NOI18N

        buttonGroup1.add(getTransactionsRadio);
        getTransactionsRadio.setText(resourceMap.getString("last_3_transaction.text")); // NOI18N
        getTransactionsRadio.setName("last_3_transaction"); // NOI18N

        buttonGroup1.add(mobileAgentRechargeRadio);
        mobileAgentRechargeRadio.setText(resourceMap.getString("topup.text")); // NOI18N
        mobileAgentRechargeRadio.setName("topup"); // NOI18N

        buttonGroup1.add(mcashTopupRadio);
        mcashTopupRadio.setText(resourceMap.getString("mcash_topup.text")); // NOI18N
        mcashTopupRadio.setName("mcash_topup"); // NOI18N

        buttonGroup1.add(changeMcashPinRadio);
        changeMcashPinRadio.setText(resourceMap.getString("change_mcash_Pin.text")); // NOI18N
        changeMcashPinRadio.setName("change_mcash_Pin"); // NOI18N

        buttonGroup1.add(checkBalanceRadio);
        checkBalanceRadio.setText(resourceMap.getString("check_balance.text")); // NOI18N
        checkBalanceRadio.setName("check_balance"); // NOI18N

        buttonGroup1.add(merchantMpinResetRadio);
        merchantMpinResetRadio.setText(resourceMap.getString("merchant_mpin_reset.text")); // NOI18N
        merchantMpinResetRadio.setName("merchant_mpin_reset"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(com.mfino.mock.integrationtestharness.IntegrationTestHarnessApp.class).getContext().getActionMap(IntegrationTestHarnessView.class, this);
        nextAPIButton.setAction(actionMap.get("showAPITabbedPane")); // NOI18N
        nextAPIButton.setText(resourceMap.getString("Next.text")); // NOI18N
        nextAPIButton.setName("Next"); // NOI18N
        nextAPIButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextAPIButtonActionPerformed(evt);
            }
        });
        nextAPIButton.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                nextAPIButtonKeyPressed(evt);
            }
        });

        buttonGroup1.add(mcashToMcashRadio);
        mcashToMcashRadio.setText(resourceMap.getString("mcash_to_mcash.text")); // NOI18N
        mcashToMcashRadio.setName("mcash_to_mcash"); // NOI18N

        buttonGroup1.add(getMcashTransactionsRadio);
        getMcashTransactionsRadio.setText(resourceMap.getString("get_mcash_transactions.text")); // NOI18N
        getMcashTransactionsRadio.setName("get_mcash_transactions"); // NOI18N

        buttonGroup1.add(shareLoadRadio);
        shareLoadRadio.setText(resourceMap.getString("share_load.text")); // NOI18N
        shareLoadRadio.setName("share_load"); // NOI18N

        buttonGroup1.add(mcashBalanceInquiryRadio);
        mcashBalanceInquiryRadio.setText(resourceMap.getString("mcash_balance_inquiry.text")); // NOI18N
        mcashBalanceInquiryRadio.setName("mcash_balance_inquiry"); // NOI18N

        buttonGroup1.add(mobileAgentDistributeRadio);
        mobileAgentDistributeRadio.setText(resourceMap.getString("mobile_agent_distribute.text")); // NOI18N
        mobileAgentDistributeRadio.setName("mobile_agent_distribute"); // NOI18N

        buttonGroup1.add(frequencyTest);
        frequencyTest.setText(resourceMap.getString("frequencyTest.text")); // NOI18N
        frequencyTest.setName("frequencyTest"); // NOI18N

        javax.swing.GroupLayout apiChooserPanelLayout = new javax.swing.GroupLayout(apiChooserPanel);
        apiChooserPanel.setLayout(apiChooserPanelLayout);
        apiChooserPanelLayout.setHorizontalGroup(
            apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apiChooserPanelLayout.createSequentialGroup()
                .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(apiChooserPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ResetPinRadio)
                            .addComponent(subcriptionActivationRadio)
                            .addComponent(merchantMpinResetRadio)
                            .addComponent(checkBalanceRadio)
                            .addComponent(mobileAgentRechargeRadio)
                            .addComponent(getMcashTransactionsRadio)
                            .addComponent(mcashToMcashRadio))
                        .addGap(18, 18, 18))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, apiChooserPanelLayout.createSequentialGroup()
                        .addContainerGap(138, Short.MAX_VALUE)
                        .addComponent(frequencyTest)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(getTransactionsRadio)
                    .addComponent(mcashBalanceInquiryRadio)
                    .addComponent(shareLoadRadio)
                    .addComponent(changeMcashPinRadio)
                    .addComponent(mcashTopupRadio)
                    .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(nextAPIButton)
                        .addComponent(mobileAgentDistributeRadio))
                    .addComponent(changepinRadio))
                .addContainerGap(52, Short.MAX_VALUE))
        );
        apiChooserPanelLayout.setVerticalGroup(
            apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(apiChooserPanelLayout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(apiChooserPanelLayout.createSequentialGroup()
                        .addComponent(changepinRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getTransactionsRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mcashTopupRadio)
                            .addGroup(apiChooserPanelLayout.createSequentialGroup()
                                .addGap(23, 23, 23)
                                .addComponent(changeMcashPinRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(shareLoadRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(mcashBalanceInquiryRadio)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(mobileAgentDistributeRadio))))
                    .addGroup(apiChooserPanelLayout.createSequentialGroup()
                        .addComponent(subcriptionActivationRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(ResetPinRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mobileAgentRechargeRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(checkBalanceRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(merchantMpinResetRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(getMcashTransactionsRadio)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mcashToMcashRadio)))
                .addGap(18, 18, 18)
                .addGroup(apiChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(nextAPIButton)
                    .addComponent(frequencyTest))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        subcriptionActivationRadio.getAccessibleContext().setAccessibleName(resourceMap.getString("subcriptionActivationRadio.AccessibleContext.accessibleName")); // NOI18N
        subcriptionActivationRadio.setActionCommand(ITHConstants.SUBSRICPTION_ACTIVATION);
        changepinRadio.setActionCommand(ITHConstants.CHANGE_PIN);
        ResetPinRadio.setActionCommand(ITHConstants.RESET_PIN);
        getTransactionsRadio.setActionCommand(ITHConstants.GET_TRANSACTIONS);
        mobileAgentRechargeRadio.setActionCommand(ITHConstants.MOBILE_AGENT_RECHARGE);
        mcashTopupRadio.setActionCommand(ITHConstants.MCASH_TOPUP);
        changeMcashPinRadio.setActionCommand(ITHConstants.CHANGE_MCASH_PIN);
        checkBalanceRadio.setActionCommand(ITHConstants.CHECK_BALANCE);
        merchantMpinResetRadio.setActionCommand(ITHConstants.MERCHANT_MPIN_RESET);
        mcashToMcashRadio.setActionCommand(ITHConstants.MCASH_TO_MCASH);
        getMcashTransactionsRadio.setActionCommand(ITHConstants.GET_MCASH_TRANSACTIONS);
        shareLoadRadio.setActionCommand(ITHConstants.SHARE_LOAD);
        mcashBalanceInquiryRadio.setActionCommand(ITHConstants.MCASH_BALANCE_INQUIRY);
        mobileAgentDistributeRadio.setActionCommand(ITHConstants.MOBILE_AGENT_DISTRIBUTE);
        frequencyTest.setActionCommand(ITHConstants.FREQUENCY_TEST);

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("apiChooserPanel.TabConstraints.tabTitle"), apiChooserPanel); // NOI18N

        SubcriptionActivationPane.setName("SubcriptionActivationPane"); // NOI18N

        SMSPinLabel.setText(resourceMap.getString("SMSPinLabel.text")); // NOI18N
        SMSPinLabel.setName("SMSPinLabel"); // NOI18N

        SMSPinTextBox.setText(resourceMap.getString("SMSPinTextBox.text")); // NOI18N
        SMSPinTextBox.setName("SMSPinTextBox"); // NOI18N

        SMSSourceMsisdnTextBox.setText(resourceMap.getString("SMSSourceMsisdnTextBox.text")); // NOI18N
        SMSSourceMsisdnTextBox.setName("SMSSourceMsisdnTextBox"); // NOI18N

        SMSSecretAnswerTextBox.setText(resourceMap.getString("SMSSecretAnswerTextBox.text")); // NOI18N
        SMSSecretAnswerTextBox.setName("SMSSecretAnswerTextBox"); // NOI18N

        SMSContactNumberTextBox.setText(resourceMap.getString("SMSContactNumberTextBox.text")); // NOI18N
        SMSContactNumberTextBox.setName("SMSContactNumberTextBox"); // NOI18N

        SMSSourceMsisdnLabel.setText(resourceMap.getString("SMSSourceMsisdnLabel.text")); // NOI18N
        SMSSourceMsisdnLabel.setName("SMSSourceMsisdnLabel"); // NOI18N

        SMSSecretAnswerLabel.setText(resourceMap.getString("SMSSecretAnswerLabel.text")); // NOI18N
        SMSSecretAnswerLabel.setName("SMSSecretAnswerLabel"); // NOI18N

        SMSContactNumberLabel.setText(resourceMap.getString("SMSContactNumberLabel.text")); // NOI18N
        SMSContactNumberLabel.setName("SMSContactNumberLabel"); // NOI18N

        SubmitSubsriptionActivation.setText(resourceMap.getString("SubmitSubsriptionActivation.text")); // NOI18N
        SubmitSubsriptionActivation.setName("SubmitSubsriptionActivation"); // NOI18N
        SubmitSubsriptionActivation.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitSubsriptionActivationActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout SubcriptionActivationPaneLayout = new javax.swing.GroupLayout(SubcriptionActivationPane);
        SubcriptionActivationPane.setLayout(SubcriptionActivationPaneLayout);
        SubcriptionActivationPaneLayout.setHorizontalGroup(
            SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubcriptionActivationPaneLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitSubsriptionActivation, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(SubcriptionActivationPaneLayout.createSequentialGroup()
                        .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SMSPinLabel)
                            .addComponent(SMSSecretAnswerLabel)
                            .addComponent(SMSContactNumberLabel)
                            .addComponent(SMSSourceMsisdnLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(SMSSourceMsisdnTextBox)
                            .addComponent(SMSPinTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 267, Short.MAX_VALUE)
                            .addComponent(SMSSecretAnswerTextBox)
                            .addComponent(SMSContactNumberTextBox, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))))
                .addContainerGap(46, Short.MAX_VALUE))
        );
        SubcriptionActivationPaneLayout.setVerticalGroup(
            SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(SubcriptionActivationPaneLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSPinLabel)
                    .addComponent(SMSPinTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSourceMsisdnTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSSourceMsisdnLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSecretAnswerTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSSecretAnswerLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(SubcriptionActivationPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSContactNumberTextBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSContactNumberLabel))
                .addGap(26, 26, 26)
                .addComponent(SubmitSubsriptionActivation, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(77, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("SubcriptionActivationPane.TabConstraints.tabTitle"), SubcriptionActivationPane); // NOI18N

        frequencyPane.setName("frequencyPane"); // NOI18N

        jLabel29.setText(resourceMap.getString("jLabel29.text")); // NOI18N
        jLabel29.setName("jLabel29"); // NOI18N

        requestPerMinTF.setText(resourceMap.getString("requestPerMinTF.text")); // NOI18N
        requestPerMinTF.setName("requestPerMinTF"); // NOI18N

        SubmitFrequencyTest.setText(resourceMap.getString("Submit.text")); // NOI18N
        SubmitFrequencyTest.setName("Submit"); // NOI18N
        SubmitFrequencyTest.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitFrequencyTestActionPerformed(evt);
            }
        });

        CBSubcriptionActivation.setText(resourceMap.getString("CBSubcriptionActivation.text")); // NOI18N
        CBSubcriptionActivation.setName("CBSubcriptionActivation"); // NOI18N

        CBChange_Pin.setText(resourceMap.getString("Change_Pin.text")); // NOI18N
        CBChange_Pin.setName("Change_Pin"); // NOI18N

        CBReset_Pin.setText(resourceMap.getString("Reset_Pin.text")); // NOI18N
        CBReset_Pin.setName("Reset_Pin"); // NOI18N

        CBGet_Transactions.setText(resourceMap.getString("Get_Transactions.text")); // NOI18N
        CBGet_Transactions.setName("Get_Transactions"); // NOI18N

        CBMobile_Agent_Recharge.setText(resourceMap.getString("Mobile_Agent_Recharge.text")); // NOI18N
        CBMobile_Agent_Recharge.setName("Mobile_Agent_Recharge"); // NOI18N

        CBMcash_Topup.setText(resourceMap.getString("Mcash_Topup.text")); // NOI18N
        CBMcash_Topup.setName("Mcash_Topup"); // NOI18N

        CBCheck_Balance.setText(resourceMap.getString("Check_Balance.text")); // NOI18N
        CBCheck_Balance.setName("Check_Balance"); // NOI18N

        CBMerchant_Mpin_Reset.setText(resourceMap.getString("Merchant_Mpin_Reset.text")); // NOI18N
        CBMerchant_Mpin_Reset.setName("Merchant_Mpin_Reset"); // NOI18N

        CBMcash_Mcash.setText(resourceMap.getString("Mcash_Mcash.text")); // NOI18N
        CBMcash_Mcash.setName("Mcash_Mcash"); // NOI18N

        CBGet_MCash_Transactions.setText(resourceMap.getString("Get_MCash_Transactions.text")); // NOI18N
        CBGet_MCash_Transactions.setName("Get_MCash_Transactions"); // NOI18N

        CBShare_Load.setText(resourceMap.getString("Share_Load.text")); // NOI18N
        CBShare_Load.setName("Share_Load"); // NOI18N

        CBMCash_Balance_Inquiry.setText(resourceMap.getString("MCash_Balance_Inquiry.text")); // NOI18N
        CBMCash_Balance_Inquiry.setName("MCash_Balance_Inquiry"); // NOI18N

        CBMobile_Agent_Distribute.setText(resourceMap.getString("Mobile_Agent_Distribute.text")); // NOI18N
        CBMobile_Agent_Distribute.setName("Mobile_Agent_Distribute"); // NOI18N

        javax.swing.GroupLayout frequencyPaneLayout = new javax.swing.GroupLayout(frequencyPane);
        frequencyPane.setLayout(frequencyPaneLayout);
        frequencyPaneLayout.setHorizontalGroup(
            frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frequencyPaneLayout.createSequentialGroup()
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, frequencyPaneLayout.createSequentialGroup()
                        .addGap(45, 45, 45)
                        .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(CBChange_Pin)
                            .addComponent(CBSubcriptionActivation)
                            .addComponent(CBReset_Pin)
                            .addComponent(CBGet_Transactions)
                            .addComponent(CBMobile_Agent_Recharge)
                            .addComponent(CBMcash_Topup)
                            .addComponent(CBMobile_Agent_Distribute))
                        .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(frequencyPaneLayout.createSequentialGroup()
                                .addGap(45, 45, 45)
                                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(CBMerchant_Mpin_Reset)
                                    .addComponent(CBCheck_Balance)
                                    .addComponent(CBMcash_Mcash)
                                    .addComponent(CBGet_MCash_Transactions)
                                    .addComponent(CBShare_Load)
                                    .addComponent(CBMCash_Balance_Inquiry))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 25, Short.MAX_VALUE))
                            .addGroup(frequencyPaneLayout.createSequentialGroup()
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(SubmitFrequencyTest))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, frequencyPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel29)
                        .addGap(18, 18, 18)
                        .addComponent(requestPerMinTF, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(37, Short.MAX_VALUE))
        );
        frequencyPaneLayout.setVerticalGroup(
            frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(frequencyPaneLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel29)
                    .addComponent(requestPerMinTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBSubcriptionActivation)
                    .addComponent(CBCheck_Balance))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBChange_Pin)
                    .addComponent(CBMerchant_Mpin_Reset))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBReset_Pin)
                    .addComponent(CBMcash_Mcash))
                .addGap(3, 3, 3)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBGet_Transactions)
                    .addComponent(CBGet_MCash_Transactions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBMobile_Agent_Recharge)
                    .addComponent(CBShare_Load))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(CBMcash_Topup)
                    .addComponent(CBMCash_Balance_Inquiry))
                .addGap(2, 2, 2)
                .addGroup(frequencyPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(frequencyPaneLayout.createSequentialGroup()
                        .addComponent(CBMobile_Agent_Distribute)
                        .addGap(21, 21, 21))
                    .addGroup(frequencyPaneLayout.createSequentialGroup()
                        .addComponent(SubmitFrequencyTest)
                        .addContainerGap())))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("frequencyPane.TabConstraints.tabTitle"), frequencyPane); // NOI18N

        resetPinPane.setName("reset_pin"); // NOI18N

        SubmitMpinReset.setText(resourceMap.getString("SubmitMpinReset.text")); // NOI18N
        SubmitMpinReset.setName("SubmitMpinReset"); // NOI18N
        SubmitMpinReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitMpinResetActionPerformed(evt);
            }
        });

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N

        mrSMSNewPinTF.setText(resourceMap.getString("mrSMSNewPinTF.text")); // NOI18N
        mrSMSNewPinTF.setName("mrSMSNewPinTF"); // NOI18N

        mrSecretAnswerTF.setText(resourceMap.getString("mrSecretAnswerTF.text")); // NOI18N
        mrSecretAnswerTF.setName("mrSecretAnswerTF"); // NOI18N

        mrSourceMsisdnTF.setText(resourceMap.getString("mrSourceMsisdnTF.text")); // NOI18N
        mrSourceMsisdnTF.setName("mrSourceMsisdnTF"); // NOI18N

        javax.swing.GroupLayout resetPinPaneLayout = new javax.swing.GroupLayout(resetPinPane);
        resetPinPane.setLayout(resetPinPaneLayout);
        resetPinPaneLayout.setHorizontalGroup(
            resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, resetPinPaneLayout.createSequentialGroup()
                .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, resetPinPaneLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel3))
                        .addGap(19, 19, 19)
                        .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mrSecretAnswerTF, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(mrSourceMsisdnTF, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                            .addComponent(mrSMSNewPinTF, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(resetPinPaneLayout.createSequentialGroup()
                        .addContainerGap(321, Short.MAX_VALUE)
                        .addComponent(SubmitMpinReset)))
                .addGap(304, 304, 304))
        );
        resetPinPaneLayout.setVerticalGroup(
            resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(resetPinPaneLayout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(mrSMSNewPinTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(mrSecretAnswerTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(resetPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(mrSourceMsisdnTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(47, 47, 47)
                .addComponent(SubmitMpinReset)
                .addContainerGap(68, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("reset_pin.TabConstraints.tabTitle"), resetPinPane); // NOI18N

        checkBalancePane.setName("check_balance"); // NOI18N

        SubmitCheckInventoryPane.setText(resourceMap.getString("SubmitCheckInventoryPane.text")); // NOI18N
        SubmitCheckInventoryPane.setName("SubmitCheckInventoryPane"); // NOI18N
        SubmitCheckInventoryPane.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitCheckInventoryPaneActionPerformed(evt);
            }
        });

        SMSSourceMsisdnLabel1.setText(resourceMap.getString("SMSSourceMsisdnLabel1.text")); // NOI18N
        SMSSourceMsisdnLabel1.setName("SMSSourceMsisdnLabel1"); // NOI18N

        ciSMSPin.setName("ciSMSPin"); // NOI18N

        ciSourceMsisdn.setName("ciSourceMsisdn"); // NOI18N

        SMSPinLabel1.setText(resourceMap.getString("SMSPinLabel1.text")); // NOI18N
        SMSPinLabel1.setName("SMSPinLabel1"); // NOI18N

        javax.swing.GroupLayout checkBalancePaneLayout = new javax.swing.GroupLayout(checkBalancePane);
        checkBalancePane.setLayout(checkBalancePaneLayout);
        checkBalancePaneLayout.setHorizontalGroup(
            checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkBalancePaneLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addGroup(checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitCheckInventoryPane)
                    .addGroup(checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(checkBalancePaneLayout.createSequentialGroup()
                            .addComponent(SMSSourceMsisdnLabel1)
                            .addGap(18, 18, 18)
                            .addComponent(ciSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(checkBalancePaneLayout.createSequentialGroup()
                            .addComponent(SMSPinLabel1)
                            .addGap(63, 63, 63)
                            .addComponent(ciSMSPin))))
                .addContainerGap(72, Short.MAX_VALUE))
        );
        checkBalancePaneLayout.setVerticalGroup(
            checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(checkBalancePaneLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSPinLabel1)
                    .addComponent(ciSMSPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(checkBalancePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SMSSourceMsisdnLabel1)
                    .addComponent(ciSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(SubmitCheckInventoryPane)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("check_balance.TabConstraints.tabTitle"), checkBalancePane); // NOI18N

        merchantMpinResetPane.setName("merchant_mpin_reset"); // NOI18N

        SubmitMerchantMpinReset.setText(resourceMap.getString("SubmitMerchantMpinReset.text")); // NOI18N
        SubmitMerchantMpinReset.setName("SubmitMerchantMpinReset"); // NOI18N
        SubmitMerchantMpinReset.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitMerchantMpinResetActionPerformed(evt);
            }
        });

        mmrContactNumber.setName("mmrContactNumber"); // NOI18N

        mmrSecretAnswer.setName("mmrSecretAnswer"); // NOI18N

        SMSSecretAnswerLabel3.setText(resourceMap.getString("SMSSecretAnswerLabel3.text")); // NOI18N
        SMSSecretAnswerLabel3.setName("SMSSecretAnswerLabel3"); // NOI18N

        mmrNewPin.setName("mmrNewPin"); // NOI18N

        SMSContactNumberLabel3.setText(resourceMap.getString("SMSContactNumberLabel3.text")); // NOI18N
        SMSContactNumberLabel3.setName("SMSContactNumberLabel3"); // NOI18N

        SMSPinLabel3.setText(resourceMap.getString("SMSPinLabel3.text")); // NOI18N
        SMSPinLabel3.setName("SMSPinLabel3"); // NOI18N

        javax.swing.GroupLayout merchantMpinResetPaneLayout = new javax.swing.GroupLayout(merchantMpinResetPane);
        merchantMpinResetPane.setLayout(merchantMpinResetPaneLayout);
        merchantMpinResetPaneLayout.setHorizontalGroup(
            merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(merchantMpinResetPaneLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitMerchantMpinReset)
                    .addGroup(merchantMpinResetPaneLayout.createSequentialGroup()
                        .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(merchantMpinResetPaneLayout.createSequentialGroup()
                                .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(SMSPinLabel3)
                                    .addComponent(SMSSecretAnswerLabel3))
                                .addGap(11, 11, 11))
                            .addGroup(merchantMpinResetPaneLayout.createSequentialGroup()
                                .addComponent(SMSContactNumberLabel3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mmrContactNumber)
                            .addComponent(mmrSecretAnswer)
                            .addComponent(mmrNewPin, javax.swing.GroupLayout.DEFAULT_SIZE, 259, Short.MAX_VALUE))))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        merchantMpinResetPaneLayout.setVerticalGroup(
            merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(merchantMpinResetPaneLayout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSPinLabel3)
                    .addComponent(mmrNewPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mmrSecretAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSSecretAnswerLabel3))
                .addGap(19, 19, 19)
                .addGroup(merchantMpinResetPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(mmrContactNumber, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSContactNumberLabel3))
                .addGap(18, 18, 18)
                .addComponent(SubmitMerchantMpinReset)
                .addContainerGap(85, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("merchant_mpin_reset.TabConstraints.tabTitle"), merchantMpinResetPane); // NOI18N

        getTransactionsPane.setName("get_Transactions"); // NOI18N

        jLabel10.setText(resourceMap.getString("jLabel10.text")); // NOI18N
        jLabel10.setName("jLabel10"); // NOI18N

        l3tSMSPinTF.setName("l3tSMSPinTF"); // NOI18N

        SubmitLast3Transactions.setText(resourceMap.getString("SubmitLast3Transactions.text")); // NOI18N
        SubmitLast3Transactions.setName("SubmitLast3Transactions"); // NOI18N
        SubmitLast3Transactions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitLast3TransactionsActionPerformed(evt);
            }
        });

        l3tSMSSourceMsisdnTF1.setName("l3tSMSSourceMsisdnTF1"); // NOI18N

        jLabel11.setText(resourceMap.getString("jLabel11.text")); // NOI18N
        jLabel11.setName("jLabel11"); // NOI18N

        javax.swing.GroupLayout getTransactionsPaneLayout = new javax.swing.GroupLayout(getTransactionsPane);
        getTransactionsPane.setLayout(getTransactionsPaneLayout);
        getTransactionsPaneLayout.setHorizontalGroup(
            getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getTransactionsPaneLayout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitLast3Transactions)
                    .addGroup(getTransactionsPaneLayout.createSequentialGroup()
                        .addGroup(getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(16, 16, 16)
                        .addGroup(getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(l3tSMSSourceMsisdnTF1)
                            .addComponent(l3tSMSPinTF, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(53, Short.MAX_VALUE))
        );
        getTransactionsPaneLayout.setVerticalGroup(
            getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, getTransactionsPaneLayout.createSequentialGroup()
                .addContainerGap(85, Short.MAX_VALUE)
                .addGroup(getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(l3tSMSPinTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11))
                .addGap(34, 34, 34)
                .addGroup(getTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(l3tSMSSourceMsisdnTF1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(40, 40, 40)
                .addComponent(SubmitLast3Transactions)
                .addGap(44, 44, 44))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("get_Transactions.TabConstraints.tabTitle"), getTransactionsPane); // NOI18N

        mobileAgentRechargePane.setName("mobile_Agent_Recharge"); // NOI18N

        topupSMSSourceMsisdnTF.setName("topupSMSSourceMsisdnTF"); // NOI18N

        SubmitTopup.setText(resourceMap.getString("SubmitTopup.text")); // NOI18N
        SubmitTopup.setName("SubmitTopup"); // NOI18N
        SubmitTopup.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitTopupActionPerformed(evt);
            }
        });

        SMSContactNumberLabel4.setText(resourceMap.getString("SMSContactNumberLabel4.text")); // NOI18N
        SMSContactNumberLabel4.setName("SMSContactNumberLabel4"); // NOI18N

        topupSMSDestMsisdnTF.setName("topupSMSDestMsisdnTF"); // NOI18N

        SMSPinLabel4.setText(resourceMap.getString("SMSPinLabel4.text")); // NOI18N
        SMSPinLabel4.setName("SMSPinLabel4"); // NOI18N

        SMSSourceMsisdnLabel4.setText(resourceMap.getString("SMSSourceMsisdnLabel4.text")); // NOI18N
        SMSSourceMsisdnLabel4.setName("SMSSourceMsisdnLabel4"); // NOI18N

        topupSMSBucketTypeTF.setName("topupSMSBucketTypeTF"); // NOI18N

        SMSSecretAnswerLabel4.setText(resourceMap.getString("SMSSecretAnswerLabel4.text")); // NOI18N
        SMSSecretAnswerLabel4.setName("SMSSecretAnswerLabel4"); // NOI18N

        topupSMSPinTF.setName("topupSMSPinTF"); // NOI18N

        jLabel4.setText(resourceMap.getString("jLabel4.text")); // NOI18N
        jLabel4.setName("jLabel4"); // NOI18N

        topupSMSRechargeAmountTF.setText(resourceMap.getString("topupSMSRechargeAmountTF.text")); // NOI18N
        topupSMSRechargeAmountTF.setName("topupSMSRechargeAmountTF"); // NOI18N

        javax.swing.GroupLayout mobileAgentRechargePaneLayout = new javax.swing.GroupLayout(mobileAgentRechargePane);
        mobileAgentRechargePane.setLayout(mobileAgentRechargePaneLayout);
        mobileAgentRechargePaneLayout.setHorizontalGroup(
            mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mobileAgentRechargePaneLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitTopup)
                    .addGroup(mobileAgentRechargePaneLayout.createSequentialGroup()
                        .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SMSSourceMsisdnLabel4)
                            .addComponent(SMSPinLabel4)
                            .addComponent(SMSSecretAnswerLabel4)
                            .addComponent(SMSContactNumberLabel4)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(topupSMSRechargeAmountTF)
                            .addComponent(topupSMSDestMsisdnTF)
                            .addComponent(topupSMSBucketTypeTF)
                            .addComponent(topupSMSPinTF)
                            .addComponent(topupSMSSourceMsisdnTF, javax.swing.GroupLayout.PREFERRED_SIZE, 267, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        mobileAgentRechargePaneLayout.setVerticalGroup(
            mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mobileAgentRechargePaneLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSPinLabel4)
                    .addComponent(topupSMSPinTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSourceMsisdnLabel4)
                    .addComponent(topupSMSSourceMsisdnTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSContactNumberLabel4)
                    .addComponent(topupSMSBucketTypeTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSecretAnswerLabel4)
                    .addComponent(topupSMSDestMsisdnTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mobileAgentRechargePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(topupSMSRechargeAmountTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(SubmitTopup)
                .addContainerGap(60, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("mobile_Agent_Recharge.TabConstraints.tabTitle"), mobileAgentRechargePane); // NOI18N

        changeMcashPinPane.setName("change_mcash_Pin"); // NOI18N

        SubmitChangeMerchantPin.setText(resourceMap.getString("SubmitChangeMerchantPin.text")); // NOI18N
        SubmitChangeMerchantPin.setName("SubmitChangeMerchantPin"); // NOI18N
        SubmitChangeMerchantPin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitChangeMerchantPinActionPerformed(evt);
            }
        });

        SMSSourceMsisdnLabel2.setText(resourceMap.getString("SMSSourceMsisdnLabel2.text")); // NOI18N
        SMSSourceMsisdnLabel2.setName("SMSSourceMsisdnLabel2"); // NOI18N

        cmpSourceMsisdn.setName("cmpSourceMsisdn"); // NOI18N

        SMSSecretAnswerLabel2.setText(resourceMap.getString("SMSSecretAnswerLabel2.text")); // NOI18N
        SMSSecretAnswerLabel2.setName("SMSSecretAnswerLabel2"); // NOI18N

        cmpOldPin.setName("cmpOldPin"); // NOI18N

        cmpNewPin.setName("cmpNewPin"); // NOI18N

        SMSPinLabel2.setText(resourceMap.getString("SMSPinLabel2.text")); // NOI18N
        SMSPinLabel2.setName("SMSPinLabel2"); // NOI18N

        javax.swing.GroupLayout changeMcashPinPaneLayout = new javax.swing.GroupLayout(changeMcashPinPane);
        changeMcashPinPane.setLayout(changeMcashPinPaneLayout);
        changeMcashPinPaneLayout.setHorizontalGroup(
            changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changeMcashPinPaneLayout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(SubmitChangeMerchantPin)
                    .addGroup(changeMcashPinPaneLayout.createSequentialGroup()
                        .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(SMSSecretAnswerLabel2)
                            .addComponent(SMSPinLabel2)
                            .addComponent(SMSSourceMsisdnLabel2))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cmpOldPin)
                            .addComponent(cmpSourceMsisdn)
                            .addComponent(cmpNewPin, javax.swing.GroupLayout.DEFAULT_SIZE, 281, Short.MAX_VALUE))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        changeMcashPinPaneLayout.setVerticalGroup(
            changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changeMcashPinPaneLayout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmpOldPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSPinLabel2))
                .addGap(28, 28, 28)
                .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSourceMsisdnLabel2)
                    .addComponent(cmpNewPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(34, 34, 34)
                .addGroup(changeMcashPinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(SMSSecretAnswerLabel2)
                    .addComponent(cmpSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(SubmitChangeMerchantPin)
                .addContainerGap(65, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("change_mcash_Pin.TabConstraints.tabTitle"), changeMcashPinPane); // NOI18N

        changePinPane.setName("changePinPane"); // NOI18N

        SubmitChangeMpin.setAction(actionMap.get("invokeSubcriptionActivation")); // NOI18N
        SubmitChangeMpin.setText(resourceMap.getString("Submit.text")); // NOI18N
        SubmitChangeMpin.setName("Submit"); // NOI18N
        SubmitChangeMpin.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitChangeMpinActionPerformed(evt);
            }
        });

        SMSOldPinlabel.setText(resourceMap.getString("SMSOldPinlabel.text")); // NOI18N
        SMSOldPinlabel.setName("SMSOldPinlabel"); // NOI18N

        SMSNewPinLabel.setText(resourceMap.getString("SMSNewPinLabel.text")); // NOI18N
        SMSNewPinLabel.setName("SMSNewPinLabel"); // NOI18N

        cmpSMSSourceMsisdnLabel.setText(resourceMap.getString("cmpSMSSourceMsisdnLabel.text")); // NOI18N
        cmpSMSSourceMsisdnLabel.setName("cmpSMSSourceMsisdnLabel"); // NOI18N

        cmSMSOldPin.setText(resourceMap.getString("cmSMSOldPin.text")); // NOI18N
        cmSMSOldPin.setName("cmSMSOldPin"); // NOI18N

        cmSMSNewPin.setText(resourceMap.getString("cmSMSNewPin.text")); // NOI18N
        cmSMSNewPin.setName("cmSMSNewPin"); // NOI18N

        cmSMSSourceMsisdn.setText(resourceMap.getString("cmSMSSourceMsisdn.text")); // NOI18N
        cmSMSSourceMsisdn.setName("cmSMSSourceMsisdn"); // NOI18N

        javax.swing.GroupLayout changePinPaneLayout = new javax.swing.GroupLayout(changePinPane);
        changePinPane.setLayout(changePinPaneLayout);
        changePinPaneLayout.setHorizontalGroup(
            changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, changePinPaneLayout.createSequentialGroup()
                .addGroup(changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(changePinPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(SubmitChangeMpin, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, changePinPaneLayout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cmpSMSSourceMsisdnLabel)
                            .addComponent(SMSOldPinlabel)
                            .addComponent(SMSNewPinLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(changePinPaneLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(cmSMSSourceMsisdn, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE))
                            .addComponent(cmSMSOldPin, javax.swing.GroupLayout.PREFERRED_SIZE, 262, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cmSMSNewPin, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE))))
                .addGap(309, 309, 309))
        );
        changePinPaneLayout.setVerticalGroup(
            changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(changePinPaneLayout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addGroup(changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cmSMSOldPin, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(SMSOldPinlabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(changePinPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(changePinPaneLayout.createSequentialGroup()
                        .addComponent(cmSMSNewPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cmSMSSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(changePinPaneLayout.createSequentialGroup()
                        .addComponent(SMSNewPinLabel)
                        .addGap(18, 18, 18)
                        .addComponent(cmpSMSSourceMsisdnLabel)))
                .addGap(30, 30, 30)
                .addComponent(SubmitChangeMpin, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(112, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("changePinPane.TabConstraints.tabTitle"), changePinPane); // NOI18N

        mCashTopupPane.setName("mCash_Topup"); // NOI18N

        SubmitTopupMerchant.setText(resourceMap.getString("SubmitTopupMerchant.text")); // NOI18N
        SubmitTopupMerchant.setName("SubmitTopupMerchant"); // NOI18N
        SubmitTopupMerchant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                SubmitTopupMerchantActionPerformed(evt);
            }
        });

        jLabel6.setText(resourceMap.getString("jLabel6.text")); // NOI18N
        jLabel6.setName("jLabel6"); // NOI18N

        jLabel7.setText(resourceMap.getString("jLabel7.text")); // NOI18N
        jLabel7.setName("jLabel7"); // NOI18N

        jLabel8.setText(resourceMap.getString("jLabel8.text")); // NOI18N
        jLabel8.setName("jLabel8"); // NOI18N

        jLabel9.setText(resourceMap.getString("jLabel9.text")); // NOI18N
        jLabel9.setName("jLabel9"); // NOI18N

        tmSMSMcashPin.setText(resourceMap.getString("tmSMSMcashPin.text")); // NOI18N
        tmSMSMcashPin.setName("tmSMSMcashPin"); // NOI18N

        tmSourceMsisdn.setText(resourceMap.getString("tmSourceMsisdn.text")); // NOI18N
        tmSourceMsisdn.setName("tmSourceMsisdn"); // NOI18N

        tmDistributeAmount.setText(resourceMap.getString("tmDistributeAmount.text")); // NOI18N
        tmDistributeAmount.setName("tmDistributeAmount"); // NOI18N

        tmDestMsisdn.setText(resourceMap.getString("tmDestMsisdn.text")); // NOI18N
        tmDestMsisdn.setName("tmDestMsisdn"); // NOI18N

        tmSMSAmount.setText(resourceMap.getString("tmSMSAmount.text")); // NOI18N
        tmSMSAmount.setName("tmSMSAmount"); // NOI18N

        jLabel5.setText(resourceMap.getString("jLabel5.text")); // NOI18N
        jLabel5.setName("jLabel5"); // NOI18N

        javax.swing.GroupLayout mCashTopupPaneLayout = new javax.swing.GroupLayout(mCashTopupPane);
        mCashTopupPane.setLayout(mCashTopupPaneLayout);
        mCashTopupPaneLayout.setHorizontalGroup(
            mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mCashTopupPaneLayout.createSequentialGroup()
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mCashTopupPaneLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(SubmitTopupMerchant))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mCashTopupPaneLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel6)
                            .addComponent(jLabel5)
                            .addComponent(jLabel9)
                            .addComponent(jLabel7)
                            .addComponent(jLabel8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(tmSMSAmount)
                            .addComponent(tmDestMsisdn)
                            .addComponent(tmDistributeAmount)
                            .addComponent(tmSourceMsisdn)
                            .addComponent(tmSMSMcashPin, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))))
                .addGap(305, 305, 305))
        );
        mCashTopupPaneLayout.setVerticalGroup(
            mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mCashTopupPaneLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6)
                    .addComponent(tmSMSMcashPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tmSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tmDistributeAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(tmDestMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9))
                .addGroup(mCashTopupPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mCashTopupPaneLayout.createSequentialGroup()
                        .addGap(10, 10, 10)
                        .addComponent(tmSMSAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(31, 31, 31)
                        .addComponent(SubmitTopupMerchant))
                    .addGroup(mCashTopupPaneLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel5)))
                .addContainerGap(53, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("mCash_Topup.TabConstraints.tabTitle"), mCashTopupPane); // NOI18N

        getMcashTransactionsPane.setName("get_mcash_transactions"); // NOI18N

        submitMcashTransactions.setText(resourceMap.getString("submitMcashTransactions.text")); // NOI18N
        submitMcashTransactions.setName("submitMcashTransactions"); // NOI18N

        jLabel23.setText(resourceMap.getString("jLabel23.text")); // NOI18N
        jLabel23.setName("jLabel23"); // NOI18N

        gmtSMS_mCashPin.setText(resourceMap.getString("gmtSMS_mCashPin.text")); // NOI18N
        gmtSMS_mCashPin.setName("gmtSMS_mCashPin"); // NOI18N

        jLabel24.setText(resourceMap.getString("jLabel24.text")); // NOI18N
        jLabel24.setName("jLabel24"); // NOI18N

        gmtSMS_sourceMsisdn.setText(resourceMap.getString("gmtSMS_sourceMsisdn.text")); // NOI18N
        gmtSMS_sourceMsisdn.setName("gmtSMS_sourceMsisdn"); // NOI18N

        javax.swing.GroupLayout getMcashTransactionsPaneLayout = new javax.swing.GroupLayout(getMcashTransactionsPane);
        getMcashTransactionsPane.setLayout(getMcashTransactionsPaneLayout);
        getMcashTransactionsPaneLayout.setHorizontalGroup(
            getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getMcashTransactionsPaneLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addGroup(getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(submitMcashTransactions)
                    .addGroup(getMcashTransactionsPaneLayout.createSequentialGroup()
                        .addGroup(getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel24)
                            .addComponent(jLabel23))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(gmtSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(gmtSMS_mCashPin, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(55, Short.MAX_VALUE))
        );
        getMcashTransactionsPaneLayout.setVerticalGroup(
            getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(getMcashTransactionsPaneLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addGroup(getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel23)
                    .addComponent(gmtSMS_mCashPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(36, 36, 36)
                .addGroup(getMcashTransactionsPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel24)
                    .addComponent(gmtSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addComponent(submitMcashTransactions)
                .addContainerGap(88, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("get_mcash_transactions.TabConstraints.tabTitle"), getMcashTransactionsPane); // NOI18N

        mcashToMcashPane.setName("mcash_to_mcash"); // NOI18N

        mCashToMcashButton.setText(resourceMap.getString("mCashToMcashButton.text")); // NOI18N
        mCashToMcashButton.setName("mCashToMcashButton"); // NOI18N
        mCashToMcashButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCashToMcashButtonActionPerformed(evt);
            }
        });

        jLabel16.setText(resourceMap.getString("jLabel16.text")); // NOI18N
        jLabel16.setName("jLabel16"); // NOI18N

        jLabel17.setText(resourceMap.getString("jLabel17.text")); // NOI18N
        jLabel17.setName("jLabel17"); // NOI18N

        jLabel18.setText(resourceMap.getString("jLabel18.text")); // NOI18N
        jLabel18.setName("jLabel18"); // NOI18N

        jLabel19.setText(resourceMap.getString("jLabel19.text")); // NOI18N
        jLabel19.setName("jLabel19"); // NOI18N

        jLabel20.setText(resourceMap.getString("jLabel20.text")); // NOI18N
        jLabel20.setName("jLabel20"); // NOI18N

        m2mSMS_mCashPin.setText(resourceMap.getString("m2mSMS_mCashPin.text")); // NOI18N
        m2mSMS_mCashPin.setName("m2mSMS_mCashPin"); // NOI18N

        m2mSMS_SecretAnswer.setText(resourceMap.getString("m2mSMS_SecretAnswer.text")); // NOI18N
        m2mSMS_SecretAnswer.setName("m2mSMS_SecretAnswer"); // NOI18N

        m2mSMS_sourceMsisdn.setText(resourceMap.getString("m2mSMS_sourceMsisdn.text")); // NOI18N
        m2mSMS_sourceMsisdn.setName("m2mSMS_sourceMsisdn"); // NOI18N

        m2mSMS_destMsisdn.setText(resourceMap.getString("m2mSMS_destMsisdn.text")); // NOI18N
        m2mSMS_destMsisdn.setName("m2mSMS_destMsisdn"); // NOI18N

        m2mSMS_mCashMessage.setText(resourceMap.getString("m2mSMS_mCashMessage.text")); // NOI18N
        m2mSMS_mCashMessage.setName("m2mSMS_mCashMessage"); // NOI18N

        javax.swing.GroupLayout mcashToMcashPaneLayout = new javax.swing.GroupLayout(mcashToMcashPane);
        mcashToMcashPane.setLayout(mcashToMcashPaneLayout);
        mcashToMcashPaneLayout.setHorizontalGroup(
            mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mcashToMcashPaneLayout.createSequentialGroup()
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mcashToMcashPaneLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel17)
                            .addComponent(jLabel16)
                            .addComponent(jLabel20)
                            .addComponent(jLabel18)
                            .addComponent(jLabel19))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(m2mSMS_destMsisdn, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(m2mSMS_mCashMessage, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(m2mSMS_mCashPin, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(m2mSMS_SecretAnswer, javax.swing.GroupLayout.DEFAULT_SIZE, 241, Short.MAX_VALUE)
                            .addComponent(m2mSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED))
                    .addGroup(mcashToMcashPaneLayout.createSequentialGroup()
                        .addContainerGap(300, Short.MAX_VALUE)
                        .addComponent(mCashToMcashButton)))
                .addGap(325, 325, 325))
        );
        mcashToMcashPaneLayout.setVerticalGroup(
            mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mcashToMcashPaneLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16)
                    .addComponent(m2mSMS_mCashPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel17)
                    .addComponent(m2mSMS_SecretAnswer, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel18)
                    .addComponent(m2mSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(m2mSMS_destMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(mcashToMcashPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel20)
                    .addComponent(m2mSMS_mCashMessage, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(27, 27, 27)
                .addComponent(mCashToMcashButton)
                .addContainerGap(30, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("mcash_to_mcash.TabConstraints.tabTitle"), mcashToMcashPane); // NOI18N

        shareLoadPane.setName("share_load"); // NOI18N

        submitShareLoadButton.setText(resourceMap.getString("Submit.text")); // NOI18N
        submitShareLoadButton.setName("Submit"); // NOI18N
        submitShareLoadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                submitShareLoadButtonActionPerformed(evt);
            }
        });

        jLabel12.setText(resourceMap.getString("jLabel12.text")); // NOI18N
        jLabel12.setName("jLabel12"); // NOI18N

        jLabel13.setText(resourceMap.getString("jLabel13.text")); // NOI18N
        jLabel13.setName("jLabel13"); // NOI18N

        jLabel14.setText(resourceMap.getString("jLabel14.text")); // NOI18N
        jLabel14.setName("jLabel14"); // NOI18N

        jLabel15.setText(resourceMap.getString("jLabel15.text")); // NOI18N
        jLabel15.setName("jLabel15"); // NOI18N

        slSMSNewPin.setText(resourceMap.getString("slSMSNewPin.text")); // NOI18N
        slSMSNewPin.setName("slSMSNewPin"); // NOI18N

        slSMSRechargeAmount.setText(resourceMap.getString("slSMSRechargeAmount.text")); // NOI18N
        slSMSRechargeAmount.setName("slSMSRechargeAmount"); // NOI18N

        slSourceMsisdn.setText(resourceMap.getString("slSourceMsisdn.text")); // NOI18N
        slSourceMsisdn.setName("slSourceMsisdn"); // NOI18N

        slSMSDestMsisdn.setText(resourceMap.getString("slSMSDestMsisdn.text")); // NOI18N
        slSMSDestMsisdn.setName("slSMSDestMsisdn"); // NOI18N

        javax.swing.GroupLayout shareLoadPaneLayout = new javax.swing.GroupLayout(shareLoadPane);
        shareLoadPane.setLayout(shareLoadPaneLayout);
        shareLoadPaneLayout.setHorizontalGroup(
            shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shareLoadPaneLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(submitShareLoadButton)
                    .addGroup(shareLoadPaneLayout.createSequentialGroup()
                        .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel13)
                            .addComponent(jLabel12)
                            .addComponent(jLabel14)
                            .addComponent(jLabel15))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slSMSDestMsisdn, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .addComponent(slSourceMsisdn, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .addComponent(slSMSRechargeAmount, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE)
                            .addComponent(slSMSNewPin, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(299, 299, 299))
        );
        shareLoadPaneLayout.setVerticalGroup(
            shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(shareLoadPaneLayout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel12)
                    .addComponent(slSMSNewPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(slSMSRechargeAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(slSourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(shareLoadPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15)
                    .addComponent(slSMSDestMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32)
                .addComponent(submitShareLoadButton)
                .addContainerGap(53, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("share_load.TabConstraints.tabTitle"), shareLoadPane); // NOI18N

        mcashBalanceInquiryPane.setName("mcash_balance_inquiry"); // NOI18N

        mCashBalaneInquiryButton.setText(resourceMap.getString("mCashBalaneInquiryButton.text")); // NOI18N
        mCashBalaneInquiryButton.setName("mCashBalaneInquiryButton"); // NOI18N
        mCashBalaneInquiryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mCashBalaneInquiryButtonActionPerformed(evt);
            }
        });

        jLabel21.setText(resourceMap.getString("jLabel21.text")); // NOI18N
        jLabel21.setName("jLabel21"); // NOI18N

        jLabel22.setText(resourceMap.getString("jLabel22.text")); // NOI18N
        jLabel22.setName("jLabel22"); // NOI18N

        mbiSMS_mCashPin.setText(resourceMap.getString("mbiSMS_mCashPin.text")); // NOI18N
        mbiSMS_mCashPin.setName("mbiSMS_mCashPin"); // NOI18N

        mbiSMS_sourceMsisdn.setText(resourceMap.getString("mbiSMS_sourceMsisdn.text")); // NOI18N
        mbiSMS_sourceMsisdn.setName("mbiSMS_sourceMsisdn"); // NOI18N

        javax.swing.GroupLayout mcashBalanceInquiryPaneLayout = new javax.swing.GroupLayout(mcashBalanceInquiryPane);
        mcashBalanceInquiryPane.setLayout(mcashBalanceInquiryPaneLayout);
        mcashBalanceInquiryPaneLayout.setHorizontalGroup(
            mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mcashBalanceInquiryPaneLayout.createSequentialGroup()
                .addGap(302, 302, 302)
                .addGroup(mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mCashBalaneInquiryButton)
                    .addGroup(mcashBalanceInquiryPaneLayout.createSequentialGroup()
                        .addGroup(mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel21))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(mbiSMS_sourceMsisdn)
                            .addComponent(mbiSMS_mCashPin, javax.swing.GroupLayout.DEFAULT_SIZE, 255, Short.MAX_VALUE))))
                .addGap(39, 39, 39))
        );
        mcashBalanceInquiryPaneLayout.setVerticalGroup(
            mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mcashBalanceInquiryPaneLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel21)
                    .addComponent(mbiSMS_mCashPin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(55, 55, 55)
                .addGroup(mcashBalanceInquiryPaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel22)
                    .addComponent(mbiSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(30, 30, 30)
                .addComponent(mCashBalaneInquiryButton)
                .addContainerGap(74, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("mcash_balance_inquiry.TabConstraints.tabTitle"), mcashBalanceInquiryPane); // NOI18N

        mobileAgentDistributePane.setName("mobile_agent_distribute"); // NOI18N

        mobileAgentDistributeButton.setText(resourceMap.getString("mobileAgentDistributeButton.text")); // NOI18N
        mobileAgentDistributeButton.setName("mobileAgentDistributeButton"); // NOI18N

        jLabel25.setText(resourceMap.getString("jLabel25.text")); // NOI18N
        jLabel25.setName("jLabel25"); // NOI18N

        jLabel26.setText(resourceMap.getString("jLabel26.text")); // NOI18N
        jLabel26.setName("jLabel26"); // NOI18N

        jLabel27.setText(resourceMap.getString("jLabel27.text")); // NOI18N
        jLabel27.setName("jLabel27"); // NOI18N

        jLabel28.setText(resourceMap.getString("jLabel28.text")); // NOI18N
        jLabel28.setName("jLabel28"); // NOI18N

        madSMS_Pin.setText(resourceMap.getString("madSMS_Pin.text")); // NOI18N
        madSMS_Pin.setName("madSMS_Pin"); // NOI18N

        madSMS_sourceMsisdn.setText(resourceMap.getString("madSMS_sourceMsisdn.text")); // NOI18N
        madSMS_sourceMsisdn.setName("madSMS_sourceMsisdn"); // NOI18N

        madSMS_destMsisdn.setText(resourceMap.getString("madSMS_destMsisdn.text")); // NOI18N
        madSMS_destMsisdn.setName("madSMS_destMsisdn"); // NOI18N

        madSMS_distributeAmount.setText(resourceMap.getString("madSMS_distributeAmount.text")); // NOI18N
        madSMS_distributeAmount.setName("madSMS_distributeAmount"); // NOI18N

        javax.swing.GroupLayout mobileAgentDistributePaneLayout = new javax.swing.GroupLayout(mobileAgentDistributePane);
        mobileAgentDistributePane.setLayout(mobileAgentDistributePaneLayout);
        mobileAgentDistributePaneLayout.setHorizontalGroup(
            mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mobileAgentDistributePaneLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(mobileAgentDistributeButton)
                    .addGroup(mobileAgentDistributePaneLayout.createSequentialGroup()
                        .addGroup(mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel28)
                            .addComponent(jLabel25)
                            .addComponent(jLabel26)
                            .addComponent(jLabel27))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(madSMS_distributeAmount)
                            .addComponent(madSMS_destMsisdn)
                            .addComponent(madSMS_sourceMsisdn)
                            .addComponent(madSMS_Pin, javax.swing.GroupLayout.DEFAULT_SIZE, 239, Short.MAX_VALUE))))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        mobileAgentDistributePaneLayout.setVerticalGroup(
            mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mobileAgentDistributePaneLayout.createSequentialGroup()
                .addGap(22, 22, 22)
                .addGroup(mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(mobileAgentDistributePaneLayout.createSequentialGroup()
                        .addComponent(jLabel25)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel26)
                        .addGap(18, 18, 18)
                        .addComponent(jLabel27))
                    .addGroup(mobileAgentDistributePaneLayout.createSequentialGroup()
                        .addComponent(madSMS_Pin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(madSMS_sourceMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(madSMS_destMsisdn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(mobileAgentDistributePaneLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel28)
                    .addComponent(madSMS_distributeAmount, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(mobileAgentDistributeButton)
                .addContainerGap(69, Short.MAX_VALUE))
        );

        IntTestHarnessTabbedPane.addTab(resourceMap.getString("mobile_agent_distribute.TabConstraints.tabTitle"), mobileAgentDistributePane); // NOI18N

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IntTestHarnessTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 431, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(IntTestHarnessTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 342, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 451, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 431, Short.MAX_VALUE)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void nextAPIButtonKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_nextAPIButtonKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_nextAPIButtonKeyPressed

    private void nextAPIButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextAPIButtonActionPerformed
        // TODO add your handling code here:
         
    }//GEN-LAST:event_nextAPIButtonActionPerformed

    private void SubmitSubsriptionActivationActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitSubsriptionActivationActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.SUBSRICPTION_ACTIVATION);
    }//GEN-LAST:event_SubmitSubsriptionActivationActionPerformed

    private void SubmitMpinResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitMpinResetActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.RESET_PIN);
    }//GEN-LAST:event_SubmitMpinResetActionPerformed

    private void SubmitCheckInventoryPaneActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitCheckInventoryPaneActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.CHECK_BALANCE);
    }//GEN-LAST:event_SubmitCheckInventoryPaneActionPerformed

    private void SubmitMerchantMpinResetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitMerchantMpinResetActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.MERCHANT_MPIN_RESET);
    }//GEN-LAST:event_SubmitMerchantMpinResetActionPerformed

    private void SubmitLast3TransactionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitLast3TransactionsActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.GET_TRANSACTIONS);
    }//GEN-LAST:event_SubmitLast3TransactionsActionPerformed

    private void SubmitTopupActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitTopupActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.MOBILE_AGENT_RECHARGE);
    }//GEN-LAST:event_SubmitTopupActionPerformed

    private void SubmitChangeMerchantPinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitChangeMerchantPinActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.CHANGE_MCASH_PIN);
    }//GEN-LAST:event_SubmitChangeMerchantPinActionPerformed

    private void SubmitChangeMpinActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitChangeMpinActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.CHANGE_PIN);
    }//GEN-LAST:event_SubmitChangeMpinActionPerformed

    private void SubmitTopupMerchantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitTopupMerchantActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.MCASH_TOPUP);
    }//GEN-LAST:event_SubmitTopupMerchantActionPerformed

    private void submitShareLoadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_submitShareLoadButtonActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.SHARE_LOAD);
    }//GEN-LAST:event_submitShareLoadButtonActionPerformed

    private void mCashBalaneInquiryButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCashBalaneInquiryButtonActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.MCASH_BALANCE_INQUIRY);
    }//GEN-LAST:event_mCashBalaneInquiryButtonActionPerformed

    private void mCashToMcashButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mCashToMcashButtonActionPerformed
        // TODO add your handling code here:
        invokeTestHarnessAPI(ITHConstants.MCASH_TO_MCASH);
    }//GEN-LAST:event_mCashToMcashButtonActionPerformed

    private void SubmitFrequencyTestActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_SubmitFrequencyTestActionPerformed
        // TODO add your handling code here:

       
        invokeTestHarnessAPI(ITHConstants.FREQUENCY_TEST);
    }//GEN-LAST:event_SubmitFrequencyTestActionPerformed

    @Action
    public void invokeTestHarnessAPI(String serviceName) {
        TestHarnessBLI invokeSubActivation=TestHarnessBLIFactoryIMPL.createTestHarnessBLI(HarnessType.Gemalto);
        TestHarnessValueObject inputvo=new TestHarnessValueObject();
        
        if(serviceName.equalsIgnoreCase(ITHConstants.SUBSRICPTION_ACTIVATION)){

            if((SMSPinTextBox.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((SMSPinTextBox.getText()));
            inputvo.setSMS_sourceMsisdn(SMSSourceMsisdnTextBox.getText());
            inputvo.setSMS_secretAnswer(SMSSecretAnswerTextBox.getText());
            if((SMSContactNumberTextBox.getText()).equals(""))
                inputvo.setSMS_contactNumber("");
            else
                inputvo.setSMS_contactNumber((SMSContactNumberTextBox.getText()));
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.subsricption_activation(inputvo);

        }else if(serviceName.equalsIgnoreCase(ITHConstants.CHANGE_PIN)){
            if((cmSMSNewPin.getText()).equals(""))
                inputvo.setSMS_newPin("");
            else
                inputvo.setSMS_newPin((cmSMSNewPin.getText()));
            if((cmSMSOldPin.getText()).equals(""))
                inputvo.setSMS_oldPin("");
            else
                inputvo.setSMS_oldPin((cmSMSOldPin.getText()));
            inputvo.setSMS_sourceMsisdn(cmSMSSourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.change_pin(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.RESET_PIN)){
            if((mrSMSNewPinTF.getText()).equals(""))
                inputvo.setSMS_newPin("");
            else
                inputvo.setSMS_newPin((mrSMSNewPinTF.getText()));
            inputvo.setSMS_secretAnswer(mrSecretAnswerTF.getText());
            inputvo.setSMS_sourceMsisdn(mrSourceMsisdnTF.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.GET_TRANSACTIONS)){
            if((l3tSMSPinTF.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((l3tSMSPinTF.getText()));
            inputvo.setSMS_sourceMsisdn(l3tSMSSourceMsisdnTF1.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.get_transactions(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MOBILE_AGENT_RECHARGE)){
            if((topupSMSPinTF.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((topupSMSPinTF.getText()));
            inputvo.setSMS_sourceMsisdn(topupSMSSourceMsisdnTF.getText());
            inputvo.setSMS_bucketType(topupSMSBucketTypeTF.getText());
            inputvo.setSMS_rechargeAmount(topupSMSRechargeAmountTF.getText());
            inputvo.setSMS_destMsisdn(topupSMSDestMsisdnTF.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.mobile_Agent_Recharge(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MCASH_TOPUP)){
             if((tmSMSMcashPin.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((tmSMSMcashPin.getText()));
            inputvo.setSMS_distributeAmount(tmDistributeAmount.getText());
            inputvo.setSMS_sourceMsisdn(tmSourceMsisdn.getText());
            inputvo.setSMS_destMsisdn(tmDestMsisdn.getText());
            if((tmSMSAmount.getText()).equals(""))
                inputvo.setSMS_Amount("");
            else
                inputvo.setSMS_Amount((tmSMSAmount.getText()));
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.mcash_topup(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.CHECK_BALANCE)){
            //String SMS_destMsisdn,String SMS_distributeAmount, String SMS_pin,String SMS_sourceMsisdn){
            if((ciSMSPin.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((ciSMSPin.getText()));
            inputvo.setSMS_sourceMsisdn(ciSourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.check_balance(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.CHANGE_MCASH_PIN)){
             if((cmpNewPin.getText()).equals(""))
                inputvo.setSMS_newPin("");
            else
                inputvo.setSMS_newPin((cmpNewPin.getText()));
            if((cmpOldPin.getText()).equals(""))
                inputvo.setSMS_oldPin("");
            else
                inputvo.setSMS_oldPin((cmpOldPin.getText()));
            inputvo.setSMS_sourceMsisdn(cmpSourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.change_mcash_Pin(inputvo);
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MERCHANT_MPIN_RESET)){
            
            if((mmrNewPin.getText()).equals(""))
                inputvo.setSMS_newPin("");
            else
                inputvo.setSMS_newPin((mmrNewPin.getText()));
            if ((mmrSecretAnswer.getText()).equals("")) {
                inputvo.setSMS_secretAnswer("");
            } else {
                inputvo.setSMS_secretAnswer(mmrSecretAnswer.getText());
            }
            if ((mmrContactNumber.getText()).equals("")) {
                inputvo.setSMS_sourceMsisdn("");
            } else {
                inputvo.setSMS_sourceMsisdn((mmrContactNumber.getText()));
            }
            
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.merchant_mpin_reset(inputvo);
        } else if(serviceName.equalsIgnoreCase(ITHConstants.SHARE_LOAD)){
            
            if((slSMSNewPin.getText()).equals(""))
                inputvo.setSMS_newPin("");
            else
                inputvo.setSMS_newPin((slSMSNewPin.getText()));

            inputvo.setSMS_rechargeAmount(slSMSRechargeAmount.getText());
            inputvo.setSMS_destMsisdn(slSMSDestMsisdn.getText());
            inputvo.setSMS_sourceMsisdn(slSourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);

        }else if(serviceName.equalsIgnoreCase(ITHConstants.GET_MCASH_TRANSACTIONS)){
            
            if((gmtSMS_mCashPin.getText()).equals(""))
                inputvo.setSMS_mCashPin("");
            else
                inputvo.setSMS_mCashPin((gmtSMS_mCashPin.getText()));

            inputvo.setSMS_sourceMsisdn(gmtSMS_sourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);
            
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MCASH_BALANCE_INQUIRY)){
            
            if((mbiSMS_mCashPin.getText()).equals(""))
                inputvo.setSMS_mCashPin("");
            else
                inputvo.setSMS_mCashPin((mbiSMS_mCashPin.getText()));
            
            inputvo.setSMS_sourceMsisdn(mbiSMS_sourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);
            
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MCASH_TO_MCASH)){

            if((m2mSMS_mCashPin.getText()).equals(""))
                inputvo.setSMS_mCashPin("");
            else
                inputvo.setSMS_mCashPin((m2mSMS_mCashPin.getText()));
            inputvo.setSMS_secretAnswer(m2mSMS_SecretAnswer.getText());
            inputvo.setSMS_destMsisdn(m2mSMS_destMsisdn.getText());
            inputvo.setSMS_sourceMsisdn(m2mSMS_sourceMsisdn.getText());
            inputvo.setSMS_mCashMessage(m2mSMS_mCashMessage.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);
            
        }else if(serviceName.equalsIgnoreCase(ITHConstants.MOBILE_AGENT_DISTRIBUTE)){

            if((madSMS_Pin.getText()).equals(""))
                inputvo.setSMS_pin("");
            else
                inputvo.setSMS_pin((madSMS_Pin.getText()));
            inputvo.setSMS_distributeAmount(madSMS_distributeAmount.getText());
            inputvo.setSMS_destMsisdn(madSMS_destMsisdn.getText());
            inputvo.setSMS_sourceMsisdn(madSMS_sourceMsisdn.getText());
            inputvo.setSMS_serviceName(serviceName);
            invokeSubActivation.reset_pin(inputvo);
        }
        else if(serviceName.equalsIgnoreCase(ITHConstants.FREQUENCY_TEST)){

            int selectionList=0;
            if(CBChange_Pin.isSelected())
            {
                selectionList|=ITHConstants.CHANGE_PIN_INT;
            }
            if(CBCheck_Balance.isSelected())
            {
                selectionList|=ITHConstants.CHECK_BALANCE_INT;
                
            }
            if(CBGet_MCash_Transactions.isSelected())
            {
                selectionList|=ITHConstants.GET_MCASH_TRANSACTIONS_INT;
            }
            if(CBGet_Transactions.isSelected())
            {
                selectionList|=ITHConstants.GET_TRANSACTIONS_INT;
            }
            if(CBMCash_Balance_Inquiry.isSelected())
            {
                selectionList|=ITHConstants.MCASH_BALANCE_INQUIRY_INT;
            }
            if(CBMcash_Mcash.isSelected())
            {
                selectionList|=ITHConstants.MCASH_TO_MCASH_INT;
            }
            if(CBMcash_Topup.isSelected())
            {
                selectionList|=ITHConstants.MCASH_TOPUP_INT;
            }
            if(CBMerchant_Mpin_Reset.isSelected())
            {
                selectionList|=ITHConstants.MERCHANT_MPIN_RESET_INT;
            }
            if(CBMobile_Agent_Distribute.isSelected())
            {
                selectionList|=ITHConstants.MOBILE_AGENT_DISTRIBUTE_INT;
            }
            if(CBMobile_Agent_Recharge.isSelected())
            {
                selectionList|=ITHConstants.MOBILE_AGENT_RECHARGE_INT;
            }
            if(CBShare_Load.isSelected())
            {
                selectionList|=ITHConstants.SHARE_LOAD_INT;
            }
            if(CBSubcriptionActivation.isSelected())
            {
                selectionList|=ITHConstants.SUBSRICPTION_ACTIVATION_INT;
            }

            inputvo.setService_Name(selectionList);
            int totalNoOfRequests=0;
            if((requestPerMinTF.getText()).equals("")){
                totalNoOfRequests=0;
                return;
            }
            else{
                totalNoOfRequests=Integer.parseInt(requestPerMinTF.getText());
            }

            invokeSubActivation.frequency_test(selectionList, totalNoOfRequests);
            Analyzer az=new Analyzer();
            inputvo.setHtmlResponse(az.analyze());
            
        }
//        if (SubActvtnCnfrmDlg == null) {
//            JFrame mainFrame = IntegrationTestHarnessApp.getApplication().getMainFrame();
//            SubActvtnCnfrmDlg = new SubcriptionActivationConfirmation(mainFrame, true,outputvo);
//            SubActvtnCnfrmDlg.setLocationRelativeTo(mainFrame);
//        }
//        IntegrationTestHarnessApp.getApplication().show(SubActvtnCnfrmDlg);
        if (ConfirmResponse == null) {
            JFrame mainFrame = IntegrationTestHarnessApp.getApplication().getMainFrame();
            ConfirmResponse = new ConfirmResponse(mainFrame, true,inputvo);
            ConfirmResponse.setLocationRelativeTo(mainFrame);
        }
        IntegrationTestHarnessApp.getApplication().show(ConfirmResponse);
        
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox CBChange_Pin;
    private javax.swing.JCheckBox CBCheck_Balance;
    private javax.swing.JCheckBox CBGet_MCash_Transactions;
    private javax.swing.JCheckBox CBGet_Transactions;
    private javax.swing.JCheckBox CBMCash_Balance_Inquiry;
    private javax.swing.JCheckBox CBMcash_Mcash;
    private javax.swing.JCheckBox CBMcash_Topup;
    private javax.swing.JCheckBox CBMerchant_Mpin_Reset;
    private javax.swing.JCheckBox CBMobile_Agent_Distribute;
    private javax.swing.JCheckBox CBMobile_Agent_Recharge;
    private javax.swing.JCheckBox CBReset_Pin;
    private javax.swing.JCheckBox CBShare_Load;
    private javax.swing.JCheckBox CBSubcriptionActivation;
    private javax.swing.JTabbedPane IntTestHarnessTabbedPane;
    private javax.swing.JRadioButton ResetPinRadio;
    private javax.swing.JLabel SMSContactNumberLabel;
    private javax.swing.JLabel SMSContactNumberLabel3;
    private javax.swing.JLabel SMSContactNumberLabel4;
    private javax.swing.JTextField SMSContactNumberTextBox;
    private javax.swing.JLabel SMSNewPinLabel;
    private javax.swing.JLabel SMSOldPinlabel;
    private javax.swing.JLabel SMSPinLabel;
    private javax.swing.JLabel SMSPinLabel1;
    private javax.swing.JLabel SMSPinLabel2;
    private javax.swing.JLabel SMSPinLabel3;
    private javax.swing.JLabel SMSPinLabel4;
    private javax.swing.JTextField SMSPinTextBox;
    private javax.swing.JLabel SMSSecretAnswerLabel;
    private javax.swing.JLabel SMSSecretAnswerLabel2;
    private javax.swing.JLabel SMSSecretAnswerLabel3;
    private javax.swing.JLabel SMSSecretAnswerLabel4;
    private javax.swing.JTextField SMSSecretAnswerTextBox;
    private javax.swing.JLabel SMSSourceMsisdnLabel;
    private javax.swing.JLabel SMSSourceMsisdnLabel1;
    private javax.swing.JLabel SMSSourceMsisdnLabel2;
    private javax.swing.JLabel SMSSourceMsisdnLabel4;
    private javax.swing.JTextField SMSSourceMsisdnTextBox;
    private javax.swing.JPanel SubcriptionActivationPane;
    private javax.swing.JButton SubmitChangeMerchantPin;
    private javax.swing.JButton SubmitChangeMpin;
    private javax.swing.JButton SubmitCheckInventoryPane;
    private javax.swing.JButton SubmitFrequencyTest;
    private javax.swing.JButton SubmitLast3Transactions;
    private javax.swing.JButton SubmitMerchantMpinReset;
    private javax.swing.JButton SubmitMpinReset;
    private javax.swing.JButton SubmitSubsriptionActivation;
    private javax.swing.JButton SubmitTopup;
    private javax.swing.JButton SubmitTopupMerchant;
    private javax.swing.JPanel apiChooserPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel changeMcashPinPane;
    private javax.swing.JRadioButton changeMcashPinRadio;
    private javax.swing.JPanel changePinPane;
    private javax.swing.JRadioButton changepinRadio;
    private javax.swing.JPanel checkBalancePane;
    private javax.swing.JRadioButton checkBalanceRadio;
    private javax.swing.JTextField ciSMSPin;
    private javax.swing.JTextField ciSourceMsisdn;
    private javax.swing.JTextField cmSMSNewPin;
    private javax.swing.JTextField cmSMSOldPin;
    private javax.swing.JTextField cmSMSSourceMsisdn;
    private javax.swing.JTextField cmpNewPin;
    private javax.swing.JTextField cmpOldPin;
    private javax.swing.JLabel cmpSMSSourceMsisdnLabel;
    private javax.swing.JTextField cmpSourceMsisdn;
    private javax.swing.JPanel frequencyPane;
    private javax.swing.JRadioButton frequencyTest;
    private javax.swing.JPanel getMcashTransactionsPane;
    private javax.swing.JRadioButton getMcashTransactionsRadio;
    private javax.swing.JPanel getTransactionsPane;
    private javax.swing.JRadioButton getTransactionsRadio;
    private javax.swing.JTextField gmtSMS_mCashPin;
    private javax.swing.JTextField gmtSMS_sourceMsisdn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JTextField l3tSMSPinTF;
    private javax.swing.JTextField l3tSMSSourceMsisdnTF1;
    private javax.swing.JTextField m2mSMS_SecretAnswer;
    private javax.swing.JTextField m2mSMS_destMsisdn;
    private javax.swing.JTextField m2mSMS_mCashMessage;
    private javax.swing.JTextField m2mSMS_mCashPin;
    private javax.swing.JTextField m2mSMS_sourceMsisdn;
    private javax.swing.JButton mCashBalaneInquiryButton;
    private javax.swing.JButton mCashToMcashButton;
    private javax.swing.JPanel mCashTopupPane;
    private javax.swing.JTextField madSMS_Pin;
    private javax.swing.JTextField madSMS_destMsisdn;
    private javax.swing.JTextField madSMS_distributeAmount;
    private javax.swing.JTextField madSMS_sourceMsisdn;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JTextField mbiSMS_mCashPin;
    private javax.swing.JTextField mbiSMS_sourceMsisdn;
    private javax.swing.JPanel mcashBalanceInquiryPane;
    private javax.swing.JRadioButton mcashBalanceInquiryRadio;
    private javax.swing.JPanel mcashToMcashPane;
    private javax.swing.JRadioButton mcashToMcashRadio;
    private javax.swing.JRadioButton mcashTopupRadio;
    private javax.swing.JPanel merchantMpinResetPane;
    private javax.swing.JRadioButton merchantMpinResetRadio;
    private javax.swing.JTextField mmrContactNumber;
    private javax.swing.JTextField mmrNewPin;
    private javax.swing.JTextField mmrSecretAnswer;
    private javax.swing.JButton mobileAgentDistributeButton;
    private javax.swing.JPanel mobileAgentDistributePane;
    private javax.swing.JRadioButton mobileAgentDistributeRadio;
    private javax.swing.JPanel mobileAgentRechargePane;
    private javax.swing.JRadioButton mobileAgentRechargeRadio;
    private javax.swing.JTextField mrSMSNewPinTF;
    private javax.swing.JTextField mrSecretAnswerTF;
    private javax.swing.JTextField mrSourceMsisdnTF;
    private javax.swing.JButton nextAPIButton;
    private javax.swing.JTextField requestPerMinTF;
    private javax.swing.JPanel resetPinPane;
    private javax.swing.JPanel shareLoadPane;
    private javax.swing.JRadioButton shareLoadRadio;
    private javax.swing.JTextField slSMSDestMsisdn;
    private javax.swing.JTextField slSMSNewPin;
    private javax.swing.JTextField slSMSRechargeAmount;
    private javax.swing.JTextField slSourceMsisdn;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    private javax.swing.JRadioButton subcriptionActivationRadio;
    private javax.swing.JButton submitMcashTransactions;
    private javax.swing.JButton submitShareLoadButton;
    private javax.swing.JTextField tmDestMsisdn;
    private javax.swing.JTextField tmDistributeAmount;
    private javax.swing.JTextField tmSMSAmount;
    private javax.swing.JTextField tmSMSMcashPin;
    private javax.swing.JTextField tmSourceMsisdn;
    private javax.swing.JTextField topupSMSBucketTypeTF;
    private javax.swing.JTextField topupSMSDestMsisdnTF;
    private javax.swing.JTextField topupSMSPinTF;
    private javax.swing.JTextField topupSMSRechargeAmountTF;
    private javax.swing.JTextField topupSMSSourceMsisdnTF;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
    private JDialog SubActvtnCnfrmDlg;
    private JDialog ConfirmResponse;
    public static String serviceName;
}

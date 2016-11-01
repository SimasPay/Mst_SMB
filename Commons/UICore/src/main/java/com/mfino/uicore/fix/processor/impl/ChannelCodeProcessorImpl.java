/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.fix.processor.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mfino.dao.ChannelCodeDAO;
import com.mfino.dao.DAOFactory;
import com.mfino.dao.query.ChannelCodeQuery;
import com.mfino.domain.ChannelCode;
import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSChannelCode;
import com.mfino.fix.CmFinoFIX.CMJSError;
import com.mfino.i18n.MessageText;
import com.mfino.service.AuthorizationService;
import com.mfino.service.ChannelCodeService;
import com.mfino.uicore.fix.processor.BaseFixProcessor;
import com.mfino.uicore.fix.processor.ChannelCodeProcessor;

/**
 *
 * @author ADMIN
 */
@Service("ChannelCodeProcessorImpl")
public class ChannelCodeProcessorImpl extends BaseFixProcessor implements ChannelCodeProcessor{
	
	@Autowired
	@Qualifier("ChannelCodeServiceImpl")
	private ChannelCodeService channelCodeService;
	
	@Autowired
	@Qualifier("AuthorizationServiceImpl")
	private AuthorizationService authorizationService;

    public CMJSError handleChannelCodes(ConstraintViolationException error) {
        CmFinoFIX.CMJSError errorMsg = new CmFinoFIX.CMJSError();
        String message = MessageText._(error.getCause().getMessage());
        errorMsg.setErrorDescription(message);
        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
        log.warn(message, error);
        return errorMsg;
    }

    private void updateEntity(ChannelCode channelCode, CmFinoFIX.CMJSChannelCode.CGEntries e) {

        if (e.getChannelCode() != null) {
            channelCode.setChannelcode(e.getChannelCode());
        }

        if (e.getChannelName() != null) {
            channelCode.setChannelname(e.getChannelName());
        }
        if (e.getDescription() != null) {
            channelCode.setDescription(e.getDescription());
        }
        if (e.getChannelSourceApplication() != null) {
            channelCode.setChannelsourceapplication(e.getChannelSourceApplication());
        }
    }

    private void updateMessage(ChannelCode channelCode, CMJSChannelCode.CGEntries entry) {

        entry.setID(channelCode.getId().longValue());

        if (channelCode.getDescription() != null) {
            entry.setDescription(channelCode.getDescription());
        }
        if (channelCode.getChannelcode() != null) {
            entry.setChannelCode(channelCode.getChannelcode());
        }
        if (channelCode.getChannelname() != null) {
            entry.setChannelName(channelCode.getChannelname());
        }
        if ((channelCode.getChannelsourceapplication()) != null) {
            entry.setChannelSourceApplication((channelCode.getChannelsourceapplication()).intValue());
            entry.setChannelSourceApplicationText(channelCodeService.getChannelNameBySourceApplication(
            				(channelCode.getChannelsourceapplication()).intValue()));
        }
        if (channelCode.getCreatetime() != null) {
            entry.setCreateTime(channelCode.getCreatetime());
        }
        if (channelCode.getCreatedby() != null) {
            entry.setCreatedBy(channelCode.getCreatedby());
        }
        if (channelCode.getLastupdatetime() != null) {
            entry.setLastUpdateTime(channelCode.getLastupdatetime());
        }
        if (channelCode.getUpdatedby() != null) {
            entry.setUpdatedBy(channelCode.getUpdatedby());
        }
        if (channelCode.getVersion() != null) {
            entry.setRecordVersion(channelCode.getVersion());
        }
    }

    @Transactional(readOnly=false, propagation = Propagation.REQUIRED,rollbackFor=Throwable.class)
    public CFIXMsg process(CFIXMsg msg) throws Exception {
        CMJSChannelCode realMsg = (CMJSChannelCode) msg;

        ChannelCodeDAO dao = DAOFactory.getInstance().getChannelCodeDao();

        if (CmFinoFIX.JSaction_Update.equalsIgnoreCase(realMsg.getaction())) {
            CMJSChannelCode.CGEntries[] entries = realMsg.getEntries();
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Channel_Codes_Edit)) {
                for (CMJSChannelCode.CGEntries e : entries) {
                    ChannelCode channelCode = dao.getById(e.getID());

                    // Check for Stale Data
                    if (!e.getRecordVersion().equals(channelCode.getVersion())) {
                        handleStaleDataException();
                    }
                    //restrict the user if channelcode is less than 10 (7 are used 3 are kept for reserved)
                    if (Integer.parseInt(channelCode.getChannelcode()) > 10) {
                        updateEntity(channelCode, e);
                        try {
                            dao.save(channelCode);
                        } catch (ConstraintViolationException error) {
                            return handleChannelCodes(error);
                        }
                    } else {
                        CMJSError errorMsg = new CMJSError();
                        errorMsg.setErrorDescription(MessageText._("You cannot edit the system defined channelcodes"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                    updateMessage(channelCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to edit Product Indicator Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSChannelCode.CGEntries.FieldName_ChannelCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);

        } else if (CmFinoFIX.JSaction_Select.equalsIgnoreCase(realMsg.getaction())) {
            ChannelCodeQuery query = new ChannelCodeQuery();

            // getQuery is for access method search in transaction search form..
            if (StringUtils.isNotBlank(realMsg.getquery())) {
                query.setChannelNameLike(realMsg.getquery());
            }
            if (StringUtils.isNotBlank(realMsg.getChannelCodeSearch())) {
                query.setChannelCode(realMsg.getChannelCodeSearch());
            }
            if (StringUtils.isNotBlank(realMsg.getChannelNameSearch())) {
                query.setChannelName(realMsg.getChannelNameSearch());
            }
            if (realMsg.getSourceApplicationSearch() != null) {
                query.setSourceApplication(realMsg.getSourceApplicationSearch());
            }
            query.setStart(realMsg.getstart());
            query.setLimit(realMsg.getlimit());

            List<ChannelCode> results = dao.get(query);

            realMsg.allocateEntries(results.size());

            for (int i = 0; i < results.size(); i++) {
                ChannelCode channelCode = results.get(i);

                CMJSChannelCode.CGEntries entry = new CMJSChannelCode.CGEntries();
                updateMessage(channelCode, entry);
                realMsg.getEntries()[i] = entry;
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(query.getTotal());
        } else if (CmFinoFIX.JSaction_Insert.equalsIgnoreCase(realMsg.getaction())) {
            CMJSChannelCode.CGEntries[] entries = realMsg.getEntries();
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Channel_Codes_Add)) {
                for (CMJSChannelCode.CGEntries e : entries) {
                    ChannelCode channelCode = new ChannelCode();
                    //get the maximum channelsource
                    Integer chanelSourceValue = dao.getMaximumChannelSourceValue();
                    if (chanelSourceValue != null) {
                        chanelSourceValue += 1;
                    } else {
                        chanelSourceValue = 0;
                    }
                    e.setChannelSourceApplication(chanelSourceValue);
                    updateEntity(channelCode, e);
                    try {
                        dao.save(channelCode);
                    } catch (ConstraintViolationException error) {
                        return handleChannelCodes(error);
                    }
                    updateMessage(channelCode, e);
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to add new Channel Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSChannelCode.CGEntries.FieldName_ChannelCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        } else if (CmFinoFIX.JSaction_Delete.equalsIgnoreCase(realMsg.getaction())) {
            CMJSChannelCode.CGEntries[] entries = realMsg.getEntries();
            if (authorizationService.isAuthorized(CmFinoFIX.Permission_Channel_Codes_Delete)) {
                for (CMJSChannelCode.CGEntries e : entries) {
                    //restrict the user if channelcode is less than 10 (7 are used 3 are kept for reserved).
                    ChannelCode code = dao.getById(e.getID());
                    if (Integer.parseInt(code.getChannelcode()) > 10) {
                        dao.deleteById(e.getID());
                    } else {
                        CMJSError errorMsg = new CMJSError();
                        errorMsg.setErrorDescription(MessageText._("You cannot delete the system defined channelcodes"));
                        errorMsg.setErrorCode(CmFinoFIX.ErrorCode_Generic);
                        return errorMsg;
                    }
                }
            } else {
                return getErrorMessage(MessageText._("Not authorized to delete Product Indicator Code"),
                        CmFinoFIX.ErrorCode_Generic,
                        CmFinoFIX.CMJSChannelCode.CGEntries.FieldName_ChannelCode,
                        MessageText._("Not allowed"));
            }
            realMsg.setsuccess(CmFinoFIX.Boolean_True);
            realMsg.settotal(entries.length);
        }
        return realMsg;
    }
}

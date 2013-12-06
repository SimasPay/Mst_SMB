package com.mfino.fix2notification.processor;

import java.util.List;

import com.mfino.fix.CFIXMsg;
import com.mfino.mailer.NotificationWrapper;

public interface IFix2NotifListProcessor {

    public List<NotificationWrapper> process(CFIXMsg msg) throws Exception;
}

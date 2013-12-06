package com.mfino.fix2notification.processor;

import com.mfino.fix.CFIXMsg;
import com.mfino.mailer.NotificationWrapper;

public interface IFix2NotifProcessor {
    public NotificationWrapper process(CFIXMsg msg) throws Exception;
}

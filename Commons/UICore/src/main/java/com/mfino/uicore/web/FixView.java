/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mfino.uicore.web;

import java.util.Map;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

import com.mfino.fix.CFIXMsg;
import com.mfino.fix.CMultiXBuffer;

/**
 *
 * @author xchen
 */
public class FixView implements View {

    private byte[] rawMessage = null;
    private CFIXMsg msg = null;

    public FixView(byte[] rawMsg) {
        rawMessage = rawMsg;
    }

    public FixView(CFIXMsg msg) {
        this.msg = msg;
    }

    public String getContentType() {
        return "application/fix";
    }

    public void render(Map model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        ServletOutputStream out = response.getOutputStream();
        if (rawMessage != null) {
            out.write(rawMessage);
        } else if (msg != null) {
            CMultiXBuffer Buf = new CMultiXBuffer();
            msg.toFIX(Buf);
            out.write(Buf.DataPtr(), 0, Buf.Length());
        }
    }
}

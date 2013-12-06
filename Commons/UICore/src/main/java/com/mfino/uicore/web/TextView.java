/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.uicore.web;

import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.View;

/**
 *
 * @author Venkata Krishna Teja D
 */
public class TextView implements View {
   
    private String data;

    public TextView(String data){
        super();
        this.data = data;
    }

    public String getContentType() {
        return "text/html";
    }

    public void render(Map model, HttpServletRequest request,
            HttpServletResponse response) throws Exception {
      response.setContentType("text/html");

        PrintWriter out = response.getWriter();
        if(null == this.data) {
            out.write(model.toString());
        } else {
            out.write(this.data);
        }
        
    }
}

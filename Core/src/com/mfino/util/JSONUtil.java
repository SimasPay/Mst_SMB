package com.mfino.util;


import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.json.*;

public class JSONUtil {

    private static final Logger log = LoggerFactory.getLogger(JSONUtil.class);

    public static JSONObject fromMap(Map<String, Collection<Object>> model) {
        JSONObject jsonObject = new JSONObject();

        try {
            Iterator<String> ite = model.keySet().iterator();
            while (ite.hasNext()) {
                String key = (String) ite.next();
                jsonObject.put(key, model.get(key));
            }
        } catch (Exception e) {
            log.error("call fromMap failed ", e);
        }

        return jsonObject;
    }
}

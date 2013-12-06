package com.mfino.uicore.web;

import java.util.ArrayList;

import com.mfino.fix.CmFinoFIX;
import com.mfino.fix.CmFinoFIX.CMJSError;

/**
 *
 * @author xchen
 */
public class WebContextError {
    private static ThreadLocal<ArrayList<CmFinoFIX.CMJSError>> cache =
            new ThreadLocal<ArrayList<CmFinoFIX.CMJSError>>() {
        @Override
        protected  ArrayList<CMJSError> initialValue() {
            return new ArrayList<CMJSError>();
        }
    };

    public static void clear() {
        cache.get().clear();
    }

    public static void addError(CmFinoFIX.CMJSError error){
        cache.get().add(error);
    }

    public static boolean isEmpty(){
        return cache.get().isEmpty();
    }

    public static ArrayList<CmFinoFIX.CMJSError> getAllErrors(){
        return cache.get();
    }

    public static CmFinoFIX.CMJSError getError(){
        ArrayList<CmFinoFIX.CMJSError> errors = cache.get();
        if(errors.isEmpty()){
            return null;
        }else{
            return errors.get(0);
        }
    }
}

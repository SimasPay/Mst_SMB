/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.database.executors;

import java.util.List;

/**
 *
 * @author sandeepjs
 */

public interface Executor {
    public abstract boolean executeUpdate();
    public abstract List execute();
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.mfino.tools.schemachangeupgradetool.database.executors;

/**
 *
 * @author sandeepjs
 */
public abstract class AbstractExecutor implements Executor {

    protected String[] sqlStatements;
    protected int from;
    protected int to;

    public abstract void setData(String[] sqlStatements, int from, int to);
}

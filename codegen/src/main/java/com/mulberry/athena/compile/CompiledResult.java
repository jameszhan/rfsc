/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.compile;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 10:33 PM
 */
public class CompiledResult {

    private final boolean success;
    private final String errorMessage;
    private final Class<?> clazz;

    public CompiledResult(String errorMessage) {
        this.success = false;
        this.errorMessage = errorMessage;
        this.clazz = null;
    }

    public CompiledResult(Class<?> clazz) {
        this.success = true;
        this.errorMessage = null;
        this.clazz = clazz;
    }

    public Class<?> getClazz() {
        if (success) {
            return clazz;
        } else {
            return null;
        }
    }

    public boolean isSuccess() {
        return success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}

/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.alibaba.rfsc.hadoop;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 4/12/14
 *         Time: 1:54 AM
 */
public class WordCount {

    public static void main(String[] args) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, 4, 1);
        calendar.add(Calendar.DATE, 10000);
        System.out.println(calendar.getTime());
    }
}

/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.dfa.translates;

import org.junit.Assert;
import org.junit.Test;

import java.io.*;
import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 6/14/14
 *         Time: 11:31 PM
 */
public class DfaTranslateTable {

    /**
     * Translates characters to character classes
     */
    private static final String ZZ_CMAP_PACKED = "\11\0\1\10\1\7\1\0\1\10\1\6\22\0\1\10\3\0\1\3\5\0\1\2\4\0\1\1\113\0\1\4\1\0\1\5\uff82\0";

    /**
     * Translates a state to a row index in the translates table
     */
    private static final String ZZ_ROWMAP_PACKED = "\0\0\0\11\0\22\0\33\0\44\0\55\0\11\0\66\0\77\0\110\0\121\0\55";

    /**
     * The translates table of the DFA
     */
    private static final String ZZ_TRANS_PACKED = "\1\2\1\3\1\2\1\4\2\2\1\5\1\6\1\7\1\2\1\0\1\2\1\0\2\2\2\0\1\2\2\0"+
                    "\1\10\12\0\1\11\13\0\1\6\12\0\2\10\1\12\6\10\5\13\1\0\3\13\1\10\1\6\1\12\6\10\5\13\1\14\3\13";

    /**
     * Translates DFA states to action switch labels.
     */
    private static final String ZZ_ACTION_PACKED = "\1\0\3\1\3\2\4\0\1\3";

    @Test
    public void unpackCmap() throws Exception{
        int[] cmap = unpack(ZZ_CMAP_PACKED);
        FileOutputStream fos = new FileOutputStream("/tmp/test.txt");
        Writer out = new OutputStreamWriter(fos, "UTF-8");
        try {
            for (int i = 0; i < cmap.length; i++) {
                out.write(String.format("%c : %d\n", (char)i, cmap[i]));
            }
        } finally {
            out.close();
        }
    }

    @Test
    public void unpackAction() {
        int[] actions = unpack(ZZ_ACTION_PACKED);
        Assert.assertArrayEquals(new int[]{0, 1, 1, 1, 2, 2, 2, 0, 0, 0, 0, 3}, actions);
    }

    @Test
    public void unpackTrans() {
        int[] trans = unpack(ZZ_TRANS_PACKED);
        Assert.assertEquals(90, trans.length);
        System.out.println(Arrays.toString(trans));
    }

    /*
    private static int[] zzUnpackAction() {
        int [] result = new int[12];
        int offset = 0;
        zzUnpackAction(ZZ_ACTION_PACKED, offset, result);
        return result;
    }

    private static int zzUnpackAction(String packed, int offset, int[] result) {
        int i = 0;       // index in packed string
        int j = offset;  // index in unpacked array

        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }
    */

    public static int[] unpack(String packed) {
        int total = 0, i = 0;
        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int _ = packed.charAt(i++);
            total += count;
        }
        int[] result = new int[total];
        unpack(packed, 0, result);
        return result;
    }

    public static int unpack(String packed, int offset, int[] result) {
        int i = 0;       // index in packed string
        int j = offset;  // index in unpacked array

        int l = packed.length();
        while (i < l) {
            int count = packed.charAt(i++);
            int value = packed.charAt(i++);
            do {
                result[j++] = value;
            } while (--count > 0);
        }
        return j;
    }

}

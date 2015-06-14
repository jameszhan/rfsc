package com.mulberry.kickoff.encoding;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 1/12/15
 *         Time: 10:11 AM
 */
public class CharsetTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(CharsetTests.class);
    private static final Charset ISO8859_1 = Charset.forName("iso8859-1");
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    private static final byte[] BYTES = new byte[256];
    static {
        for (int i = 0; i < BYTES.length; i++) {
            BYTES[i] = (byte)i;
        }
    }

    @BeforeClass
    public static void env(){
        LOGGER.debug("available charsets: ");
        SortedMap<String, Charset> charsets = Charset.availableCharsets();
        for (String key : charsets.keySet()) {
            LOGGER.debug("{} = {}", key, charsets.get(key));
        }
        LOGGER.debug("Default charset is {}.", Charset.defaultCharset());
    }

    @Test
    public void iso8859_1() {
        String decodeString = new String(BYTES, ISO8859_1);
        byte[] encodeArray = decodeString.getBytes(ISO8859_1);
        Assert.assertArrayEquals(BYTES, encodeArray);
    }

    @Test
    public void findPrefectCharset() {
        List<Charset> prefectCharsets = new ArrayList<>();
        for (Charset charset : Charset.availableCharsets().values()) {
            try {
                String decodeString = new String(BYTES, charset);
                byte[] encodeArray = decodeString.getBytes(charset);
                if (encodeArray.length == BYTES.length) {
                    if (isMatch(BYTES, encodeArray, charset)) {
                        LOGGER.info("Found prefect charset: {}.", charset);
                        prefectCharsets.add(charset);
                    }
                } else {
                    LOGGER.info("Charset {} length not match, {} vs. {}.", charset, encodeArray.length, BYTES.length);
                }
            } catch (Exception e) {
                LOGGER.warn("{} is not available.", charset, e);
            }
        }
        System.out.println(prefectCharsets);
    }

    @Test
    public void asciiCompatible() {
        List<Charset> alienCharsets = new ArrayList<>();
        for (Charset charset : Charset.availableCharsets().values()) {
            try {
                String decodeString = new String(BYTES, 0, 128, charset);
                byte[] encodeArray = decodeString.getBytes(charset);
                if (encodeArray.length == 128) {
                    if (!doCompare(BYTES, encodeArray, charset)) {
                        LOGGER.warn("Found alien charset: {}.", charset);
                        alienCharsets.add(charset);
                    }
                } else {
                    LOGGER.info("Charset {} length not match, {} vs. {}.", charset, encodeArray.length, BYTES.length);
                    alienCharsets.add(charset);
                }
            } catch (Exception e) {
                LOGGER.warn("{} is not available.", charset, e);
            }
        }
        System.out.println(alienCharsets);
    }

    private boolean isMatch(byte[] expected, byte[] actual, Charset charset) {
        if (expected == null && actual == null) {
            return true;
        } else if (expected == null || actual == null || expected.length != actual.length) {
            return false;
        } else {
            return doCompare(expected, actual, charset);
        }
    }

    private boolean doCompare(byte[] expected, byte[] actual, Charset charset) {
        boolean ret = true;
        for (int i = 0; i < actual.length; i++) {
            if (expected[i] != actual[i]) {
                LOGGER.info("charset {} array[{}] not match: {} vs. {}.", charset, i, expected[i], actual[i]);
                ret = false;
                break;
            }
        }
        return ret;
    }

}

package com.mulberry.startup;

import com.lowagie.text.pdf.PdfReader;

import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 1/9/15
 *         Time: 12:34 PM
 */
public class PdfToTxt {


   // private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("onlisp.pdf");
        PdfReader reader = new PdfReader(in);
    }

}

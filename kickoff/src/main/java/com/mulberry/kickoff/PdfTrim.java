package com.mulberry.kickoff;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.RectangleReadOnly;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfImportedPage;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

/**
 * Created with IntelliJ IDEA.
 *
 * @author zizhi.zhzzh
 *         Date: 1/9/15
 *         Time: 4:33 PM
 */
public class PdfTrim {

    private static final Logger LOGGER = LoggerFactory.getLogger(PdfTrim.class);

    public static void main(String[] args) throws Exception {
        InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("onlisp.pdf");
        PdfReader reader = new PdfReader(in);

        Document document = new Document(new RectangleReadOnly(460, 720));

        File file = File.createTempFile("itext-", ".pdf");
        LOGGER.debug("output file is {}.", file.getCanonicalPath());
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();

        PdfContentByte canvas = writer.getDirectContent();
        PdfImportedPage page;

        for (int i = 1; i <= reader.getNumberOfPages(); i++) {
            Rectangle currentSize = reader.getPageSize(i);
            print(currentSize);
            document.newPage();
            page = writer.getImportedPage(reader, i);
            if (i % 2 == 0) {
                canvas.addTemplate(page, 1, 0, 0, 1, -55, -45);
            } else {
                canvas.addTemplate(page, 1, 0, 0, 1, -83, -45);
            }
        }
        document.close();
    }

    private static void print(Rectangle rectangle) {
        LOGGER.info("[{}, {}, {}, {}] border: [{}, {}, {}, {}]", rectangle.getTop(), rectangle.getRight(),
                rectangle.getBottom(), rectangle.getLeft(), rectangle.getBackgroundColor(),
                rectangle.getBorderWidthRight(), rectangle.getBorderWidthBottom(), rectangle.getBorderWidthLeft(),
                rectangle.getBackgroundColor());
    }


}

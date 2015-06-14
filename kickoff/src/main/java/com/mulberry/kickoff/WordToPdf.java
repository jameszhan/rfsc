package com.mulberry.kickoff;

import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

import java.io.File;

/**
* Created with IntelliJ IDEA.
*
* @author zizhi.zhzzh
*         Date: 1/12/15
*         Time: 2:42 PM
*/
public class WordToPdf {

    private static final String OPEN_OFFICE_HOME = "/Applications/Toolkit/OpenOffice.app/Contents/MacOS/";


    public static void toPDF(File sourceFile , File targetFile) throws Exception {
    //  String command = "soffice.bin  -headless -accept=\"socket,host=127.0.0.1,port=8200;urp;\"";
    //    Process process = Runtime.getRuntime().exec(OPEN_OFFICE_HOME + command);

        OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8200);
        connection.connect();

        // convert
        DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
        converter.convert(sourceFile, targetFile);

        // close the connection
        connection.disconnect();
        // close OpenOffice process
        //process.destroy();
    }

    public static void main(String[] args) throws Exception {
        toPDF(new File("/Users/james/Downloads/tmp.doc"), new File("/tmp/tmp.pdf"));
    }
}

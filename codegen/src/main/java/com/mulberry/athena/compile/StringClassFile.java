
package com.mulberry.athena.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 7:44 PM
 */
public class StringClassFile extends SimpleJavaFileObject {

    private ByteArrayOutputStream baos;

    protected StringClassFile(String name) {
        super(URI.create("string:///" + name.replaceAll("\\.", "/") + Kind.CLASS.extension), Kind.CLASS);
        this.baos = new ByteArrayOutputStream();
    }

    public OutputStream openOutputStream() throws IOException {
        return baos;
    }

    public byte[] getClassAsBytes(){
        return baos.toByteArray();
    }
}

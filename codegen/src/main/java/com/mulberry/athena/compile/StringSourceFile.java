
package com.mulberry.athena.compile;

import javax.tools.SimpleJavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 7:36 PM
 */
public class StringSourceFile extends SimpleJavaFileObject {
    private String contents = null;

    public StringSourceFile(String className, String contents) {
        super(URI.create("string:///" + className.replace('.', '/') + Kind.SOURCE.extension), Kind.SOURCE);
        this.contents = contents;
    }

    public CharSequence getCharContent(boolean ignoreEncodingErrors) throws IOException {
        return contents;
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return new ByteArrayInputStream(contents.getBytes());
    }


}
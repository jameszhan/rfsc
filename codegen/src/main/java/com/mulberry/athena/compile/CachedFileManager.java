/*
 * Copyright 1999-2004 Alibaba.com All right reserved. This software is the confidential and proprietary information of
 * Alibaba.com ("Confidential Information"). You shall not disclose such Confidential Information and shall use it only
 * in accordance with the terms of the license agreement you entered into with Alibaba.com.
 */
package com.mulberry.athena.compile;

import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import java.io.IOException;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 7:34 PM
 */
public class CachedFileManager extends ForwardingJavaFileManager<StandardJavaFileManager> {

    private Map<String, StringClassFile> classFileCache;

    protected CachedFileManager(StandardJavaFileManager fileManager, Map<String, StringClassFile> cache) {
        super(fileManager);
        this.classFileCache = cache;
    }

    public JavaFileObject getJavaFileForOutput(Location location, String className, JavaFileObject.Kind kind, FileObject sibling) throws IOException {
        if (classFileCache.containsKey(className)) {
            return classFileCache.get(className);
        } else {
            StringClassFile file = new StringClassFile(className);
            this.classFileCache.put(className, file);
            return file;
        }
    }

    public void close() throws IOException {
        super.close();
        this.classFileCache = null;
    }

}

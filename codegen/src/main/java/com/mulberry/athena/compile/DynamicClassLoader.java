package com.mulberry.athena.compile;

import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 *
 * @author James Zhan
 *         Date: 6/17/14
 *         Time: 3:12 PM
 */
public class DynamicClassLoader extends URLClassLoader {

    static ConcurrentHashMap<String, Reference<Class>> classCache = new ConcurrentHashMap<String, Reference<Class>>();

    static final URL[] EMPTY_URLS = new URL[]{};

    static final ReferenceQueue rq = new ReferenceQueue();

    public DynamicClassLoader() {
        super(EMPTY_URLS, (Thread.currentThread().getContextClassLoader() == null ||
                Thread.currentThread().getContextClassLoader() == ClassLoader.getSystemClassLoader()) ?
                DynamicClassLoader.class.getClassLoader() : Thread.currentThread().getContextClassLoader());
    }

    public DynamicClassLoader(ClassLoader parent) {
        super(EMPTY_URLS, parent);
    }

    public Class defineClass(String name, byte[] bytes) {
        clearCache(rq, classCache);
        Class c = defineClass(name, bytes, 0, bytes.length);
        classCache.put(name, new SoftReference(c, rq));
        return c;
    }

    protected Class<?> findClass(String name) throws ClassNotFoundException {
        Reference<Class> cr = classCache.get(name);
        if (cr != null) {
            Class c = cr.get();
            if (c != null) {
                return c;
            } else {
                classCache.remove(name, cr);
            }
        }
        return super.findClass(name);
    }

    static public <K, V> void clearCache(ReferenceQueue rq, ConcurrentHashMap<K, Reference<V>> cache) {
        //cleanup any dead entries
        if (rq.poll() != null) {
            while (rq.poll() != null);
            for (Map.Entry<K, Reference<V>> e : cache.entrySet()) {
                Reference<V> val = e.getValue();
                if (val != null && val.get() == null) {
                    cache.remove(e.getKey(), val);
                }
            }
        }
    }



}

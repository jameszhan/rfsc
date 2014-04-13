package com.alibaba.rfsc.loader;

import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.codehaus.plexus.classworlds.realm.DuplicateRealmException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class Module {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private final ClassWorld classWorld;
    private final String realmId;
    private final String parentRealmId;
    private final Collection<URL> urls = new ArrayList<>();
    private final Collection<Map.Entry<String, String>> dependencies = new ArrayList<>();

    public Module(ClassWorld classWorld, String realmId, String parentRealmId) {
        this.classWorld = classWorld;
        this.realmId = realmId;
        this.parentRealmId = parentRealmId;
    }

    public Module addDependency(String realmId, String... packages) {
        if (packages != null && packages.length > 0) {
            for (String packageName : packages) {
                dependencies.add(new AbstractMap.SimpleEntry<>(realmId, packageName));
            }
        } else {
            dependencies.add(new AbstractMap.SimpleEntry<>(realmId, (String)null));
        }
        return this;
    }

    public Module addURL(URL url) {
        urls.add(url);
        return this;
    }

    public ClassRealm load() {
        final ClassRealm classRealm = findClassRealm(realmId);
        for (URL url : urls) {
            classRealm.addURL(url);
        }
        loadDependencies(classRealm);
        return classRealm;
    }

    private void loadDependencies(ClassRealm classRealm){
        classRealm.setParentRealm(findClassRealm(parentRealmId));
        for (Map.Entry<String, String> dep : dependencies) {
            ClassRealm realm = findClassRealm(dep.getKey());
            classRealm.importFrom(realm, dep.getValue());
        }
    }

    private ClassRealm findClassRealm(String name) {
        ClassRealm classRealm = classWorld.getClassRealm(name);
        if (classRealm == null) {
            try {
                classRealm = classWorld.newRealm(name);
            } catch (DuplicateRealmException e) {
                logger.warn("ClassRealm {} has already defined.", name, e);
                classRealm = classWorld.getClassRealm(name);
            }
        }
        return classRealm;
    }

    public String getRealmId() {
        return realmId;
    }
}
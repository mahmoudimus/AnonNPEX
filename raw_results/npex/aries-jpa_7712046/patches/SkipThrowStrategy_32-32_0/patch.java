/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.aries.jpa.blueprint.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class AnnotationScanner {

public java.util.List<java.lang.reflect.AccessibleObject> getJpaAnnotatedMembers(java.lang.Class<?> c, java.lang.Class<? extends java.lang.annotation.Annotation> annotation) {
    final java.util.List<java.lang.reflect.AccessibleObject> jpaAnnotated = new java.util.ArrayList<java.lang.reflect.AccessibleObject>();
    /* NPEX_PATCH_BEGINS */
    if (c == null) {
        throw new java.lang.IllegalArgumentException();
    }
    for (java.lang.Class<?> cl = c; cl != java.lang.Object.class; cl = cl.getSuperclass()) {
        org.apache.aries.jpa.blueprint.impl.AnnotationScanner.parseClass(annotation, jpaAnnotated, cl);
    }
    return jpaAnnotated;
}

    private static void parseClass(Class<? extends Annotation> annotation, final List<AccessibleObject> jpaAnnotated, Class<?> cl) {
        for (Field field : cl.getDeclaredFields()) {
            if (field.isAnnotationPresent(annotation)) {
                field.setAccessible(true);
                jpaAnnotated.add(field);
            }
        }

        for (Method method : cl.getDeclaredMethods()) {
            if ((method.isAnnotationPresent(annotation)) && method.getName().startsWith("set") && method.getParameterTypes().length == 1) {
                jpaAnnotated.add(method);
            }
        }
    }

    public static String getName(AccessibleObject member) {
        if (member instanceof Field) {
            return ((Field)member).getName();
        } else if (member instanceof Method) {
            Method method = (Method)member;
            String name = method.getName();
            if (!name.startsWith("set")) {
                return null;
            }
            return lowerCamelCase(name.substring(3));
        }
        throw new IllegalArgumentException();
    }

    private static String lowerCamelCase(String name) {
        return name.substring(0, 1).toLowerCase() + name.substring(1);
    }
    
    public static Class<?> getType(AccessibleObject member) {
        if (member instanceof Field) {
            return ((Field)member).getType();
        } else if (member instanceof Method) {
            return ((Method)member).getParameterTypes()[0];
        }
        throw new IllegalArgumentException();
    }
}

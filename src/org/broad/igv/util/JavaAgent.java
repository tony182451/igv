/*
 * Copyright (c) 2007-2013 The Broad Institute, Inc.
 * SOFTWARE COPYRIGHT NOTICE
 * This software and its documentation are the copyright of the Broad Institute, Inc. All rights are reserved.
 *
 * This software is supplied without any warranty or guaranteed support whatsoever. The Broad Institute is not responsible for its use, misuse, or functionality.
 *
 * This software is licensed under the terms of the GNU Lesser General Public License (LGPL),
 * Version 2.1 which is available at http://www.opensource.org/licenses/lgpl-2.1.php.
 */

package org.broad.igv.util;

import com.google.common.primitives.Primitives;

import java.lang.instrument.Instrumentation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Do not add any more dependencies to this class than necessary.
 * Packaged into JavaAgent.jar, for memory profiling
 * @author jacob
 * @date 2013-Oct-04
 */
public class JavaAgent {


    /**
     * Size, in BITS, of each primitive
     */
    private static Map<Class, Long> primitiveMemMap = new HashMap<Class, Long>(9);
    static{
        primitiveMemMap.put(Byte.TYPE, 8l);
        primitiveMemMap.put(Short.TYPE, 16l);
        primitiveMemMap.put(Integer.TYPE, 32l);
        primitiveMemMap.put(Long.TYPE, 64l);
        primitiveMemMap.put(Float.TYPE, 32l);
        primitiveMemMap.put(Double.TYPE, 64l);
        //May be an overestimate
        primitiveMemMap.put(Boolean.TYPE, 1l);
        primitiveMemMap.put(Character.TYPE, 16l);
        primitiveMemMap.put(Void.TYPE, 0l);
        assert primitiveMemMap.size() == Primitives.allPrimitiveTypes().size();
    }

    private static Instrumentation instrumentation;


    public static void premain(String args, Instrumentation inst) {
        instrumentation = inst;
    }

    /**
     * Must invoke with instrumentation on command line
     * Note: Not recursive
     *
     * @param o
     * @return
     */
    public static long getObjectSize(Object o) {
        if (instrumentation == null) {
            throw new IllegalStateException("No instrumentation available. Need to launch width -javaagent:path/to/RuntimeUtils.jar");
        }

        //Maybe not right
        if(o == null) return 4;

        return instrumentation.getObjectSize(o);
    }

    public static long getObjectSizeRecursive(Object o, Set<Object> completedObjs){
        try{
            if(o != null && completedObjs.contains(o)) return 0;
        }catch(Exception e){
            System.out.println("Error on " + o.getClass() + " skipping");
            return 0;
        }

        long fullSize = getObjectSize(o);

        if(o == null) return fullSize;

        completedObjs.add(o);

        //May be off a bit for wrapper classes,
        //but we let that go for now
        if(isPrimitiveOrWrapper(o)){
            return fullSize;
        }

        if(o.getClass().isArray()){
            for(int ii=0; ii < Array.getLength(o); ii++){
                Object el = Array.get(o, ii);
                fullSize += getObjectSizeRecursive(el, completedObjs);
            }
        }

        Set<Field> fields = new HashSet<Field>();
        getAllFields(o.getClass(), fields);

        for(Field field: fields){
            if(Modifier.isStatic(field.getModifiers())){
                continue;
            }

            field.setAccessible(true);

            try {

                Object fieldValue = field.get(o);

                //Field.get boxes primitives as wrapper classes,
                //which would artificially inflate the total
                Class fieldType = field.getType();
                if(fieldType.isPrimitive()){
                    fullSize += primitiveMemMap.get(fieldType);
                }else{
                    fullSize += getObjectSizeRecursive(fieldValue, completedObjs);
                }
                completedObjs.add(fieldValue);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch(NullPointerException e){
                System.out.println("Error on " + o.getClass() + " skipping");
            }
        }
        return fullSize;
    }

    static boolean isPrimitiveOrWrapper(Object o){
        return o.getClass().isPrimitive() || Primitives.isWrapperType(o.getClass());
    }



    /**
     * Get all fields an object contains, including public, private, protected,
     * and inherited
     * @param clazz
     * @param fields
     */
    static void getAllFields(Class clazz, Set<Field> fields){
        fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
        Class supClass = clazz.getSuperclass();
        if(supClass != null){
            getAllFields(supClass, fields);
        }
    }

}

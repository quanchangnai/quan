package quan.test;

import quan.common.util.ClassUtils;

import java.util.*;

/**
 * Created by quanchangnai on 2019/7/10.
 */
public class Test {

    public static void main(String[] args) {
        Set<Class<?>> classes = ClassUtils.loadClasses("com");
        System.err.println("classes.size():" + classes.size());
        for (Class<?> clazz : classes) {
//            System.err.println("clazz.getName():" + clazz.getName());
        }


        Map<Integer, List<String>> hashClasses = new HashMap<>();

        for (Class<?> clazz : classes) {
            int hashCode = clazz.getName().hashCode();
//            hashCode = hashCode & 0x7FFFFFFF;
            hashCode = hashCode % Short.MAX_VALUE;

            List<String> list = hashClasses.getOrDefault(hashCode, new ArrayList<>());
            list.add(clazz.getName());
            hashClasses.put(hashCode, list);
        }

        int conflictNum = 0;
        for (Integer hash : hashClasses.keySet()) {
            List<String> list = hashClasses.get(hash);
            if (list.size() < 2) {
                continue;
            }
            conflictNum++;

            System.err.println("hash:" + hash + ",list:" + list.size());
        }

        System.err.println("conflictNum:" + conflictNum);
    }


}

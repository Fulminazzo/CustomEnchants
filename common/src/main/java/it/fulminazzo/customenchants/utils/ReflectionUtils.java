package it.fulminazzo.customenchants.utils;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;
import java.util.stream.Collectors;

@SuppressWarnings("unchecked")
public class ReflectionUtils {

    public static @NotNull Set<Class<?>> findClassesInPackage(@Nullable String packageName)  {
        return findClassesInPackage(packageName, ReflectionUtils.class);
    }

    @NotNull
    public static Set<Class<?>> findClassesInPackage(@Nullable String packageName, @NotNull Class<?> callingClass)  {
        TreeSet<Class<?>> classes = new TreeSet<>(Comparator.comparing(Class::getCanonicalName));
        if (packageName == null || packageName.trim().isEmpty()) return classes;
        if (packageName.endsWith(File.separator)) packageName = packageName.substring(0, packageName.length() - 1);
        if (packageName.endsWith(".")) packageName = packageName.substring(0, packageName.length() - 1);
        String path = packageName.replaceAll("\\.", File.separator);
        String currentJar = getJarName(callingClass);
        try {
            if (currentJar.endsWith(".jar")) {
                // JAR File
                FileInputStream fileInputStream = new FileInputStream(currentJar);
                JarInputStream inputStream = new JarInputStream(fileInputStream);
                JarEntry entry;
                while ((entry = inputStream.getNextJarEntry()) != null) {
                    String className = entry.getName();
                    if (!className.startsWith(path)) continue;
                    if (className.equalsIgnoreCase(path + File.separator)) continue;
                    className = className.replace("/", ".");
                    if (!className.endsWith(".class")) classes.addAll(findClassesInPackage(className));
                    else {
                        className = className.substring(0, className.length() - ".class".length());
                        Class<?> clazz = Class.forName(className);
                        if (clazz.getCanonicalName() != null) classes.add(clazz);
                    }
                }
            } else {
                // File System
                File directory = new File(currentJar, path);
                if (!directory.isDirectory()) return classes;
                File[] files = directory.listFiles();
                if (files == null) return classes;
                for (File file : files) {
                    String className = file.getName();
                    if (!className.endsWith(".class"))
                        classes.addAll(findClassesInPackage(packageName + "." + className));
                    else {
                        className = className.substring(0, className.length() - ".class".length());
                        classes.add(Class.forName(packageName + "." + className));
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return new LinkedHashSet<>(classes);
    }

    public static @NotNull List<Object> getFieldsAndMethodsResults(@NotNull Class<?> clazz, Object object, Class<?>... classes) {
        List<Object> objects = getFields(clazz, object).stream().distinct()
                .filter(o -> o == null || Arrays.asList(classes).contains(o.getClass()))
                .collect(Collectors.toList());
        getMethods(clazz).stream()
                .filter(m -> !m.getDeclaringClass().equals(Object.class))
                .filter(m -> !Modifier.isStatic(m.getModifiers()))
                .filter(m -> m.getParameterCount() == 0)
                .filter(m -> Arrays.asList(classes).contains(m.getReturnType()))
                .peek(m -> m.setAccessible(true))
                .map(m -> {
                    try {
                        return m.invoke(object);
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                })
                .forEach(objects::add);
        return objects;
    }

    public static <T> T getField(@NotNull Class<?> clazz, String name, Object object) {
        try {
            return (T) getField(clazz, name).get(object);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static @Nullable Field getField(@NotNull Class<?> clazz, String name) {
        return getFields(clazz).stream()
                .filter(f -> f.getName().equals(name))
                .peek(f -> f.setAccessible(true))
                .findFirst().orElse(null);
    }

    public static <T> @NotNull List<T> getFields(@NotNull Class<?> clazz, @NotNull Class<?> type, Object object) {
        return getFields(clazz, type).stream()
                .map(f -> {
                    try {
                        return (T) f.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public static <T> @NotNull List<T> getFields(@NotNull Class<?> clazz, Object object) {
        return getFields(clazz).stream()
                .filter(f -> !Modifier.isStatic(f.getModifiers()))
                .map(f -> {
                    try {
                        f.setAccessible(true);
                        return (T) f.get(object);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }

    public static @NotNull List<Field> getFields(@NotNull Class<?> clazz, @NotNull Class<?> type) {
        return getFields(clazz).stream()
                .filter(f -> type.isAssignableFrom(f.getType()))
                .peek(f -> f.setAccessible(true))
                .collect(Collectors.toList());
    }

    public static @NotNull List<Field> getFields(@NotNull Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null) {
            fields.addAll(Arrays.asList(clazz.getFields()));
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    public static @NotNull List<Method> getMethods(@NotNull Class<?> clazz) {
        List<Method> methods = new ArrayList<>();
        while (clazz != null) {
            methods.addAll(Arrays.asList(clazz.getMethods()));
            methods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
            clazz = clazz.getSuperclass();
        }
        return methods;
    }

    @NotNull
    public static String getJarName(@NotNull Class<?> jarClass) {
        try {
            return jarClass.getProtectionDomain().getCodeSource().getLocation().toURI().getPath();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}

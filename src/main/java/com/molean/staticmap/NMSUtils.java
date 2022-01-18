package com.molean.staticmap;

import org.bukkit.Bukkit;

public class NMSUtils {
    private static final String NAME = Bukkit.getServer().getClass().getPackage().getName();
    private static final String VERSION = NAME.substring(NAME.lastIndexOf('.') + 1);

    public static String getNMSVersion() {
        return VERSION;
    }

    public static Class<?> getNMSClass(String name) throws ClassNotFoundException {
        return Class.forName("net.minecraft."+ name);
    }

    public static Class<?> getBukkitClass(String name) throws ClassNotFoundException {
        return Class.forName("org.bukkit.craftbukkit." + VERSION + "." + name);
    }

}

package com.molean.staticmap;


import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NMSTagUtils {
    private static Method asNMSCopyMethod;
    private static Method asBukkitCopyMethod;
    private static Method getTagMethod;
    private static Method setTagMethod;
    private static Method setStringMethod;
    private static Method setBytesMethod;
    private static Method getStringMethod;
    private static Method getBytesMethod;
    private static Method hasKey;

    static {
        try {
            Class<?> craftItemClass = NMSUtils.getBukkitClass("inventory.CraftItemStack");
            Class<?> nbtTagCompoundClass = NMSUtils.getNMSClass("nbt.NBTTagCompound");
            Class<?> nmsItemClass = NMSUtils.getNMSClass("world.item.ItemStack");
            asNMSCopyMethod = craftItemClass.getDeclaredMethod("asNMSCopy", ItemStack.class);
            asBukkitCopyMethod = nmsItemClass.getDeclaredMethod("asBukkitCopy");
            getTagMethod = nmsItemClass.getDeclaredMethod("t");
            setTagMethod = nmsItemClass.getDeclaredMethod("a", nbtTagCompoundClass);
            setStringMethod = nbtTagCompoundClass.getDeclaredMethod("a", String.class, String.class);
            setBytesMethod = nbtTagCompoundClass.getDeclaredMethod("a", String.class, byte[].class);
            getStringMethod = nbtTagCompoundClass.getDeclaredMethod("l", String.class);
            getBytesMethod = nbtTagCompoundClass.getDeclaredMethod("m", String.class);
            hasKey = nbtTagCompoundClass.getDeclaredMethod("e", String.class);
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public static ItemStack set(ItemStack item, String key, String value) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            setStringMethod.invoke(compound, key, value);
            setTagMethod.invoke(nmsItem, compound);
            return (ItemStack) asBukkitCopyMethod.invoke(nmsItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static ItemStack set(ItemStack item, String key, byte[] value) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            setBytesMethod.invoke(compound, key, value);
            setTagMethod.invoke(nmsItem, compound);
            return (ItemStack) asBukkitCopyMethod.invoke(nmsItem);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return item;
    }

    public static String get(ItemStack item, String key) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            if ((Boolean) hasKey.invoke(compound, key)) {
                return (String) getStringMethod.invoke(compound, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] getAsBytes(ItemStack item, String key) {
        try {
            Object nmsItem = asNMSCopyMethod.invoke(null, item);
            Object compound = getTagMethod.invoke(nmsItem);
            if ((Boolean) hasKey.invoke(compound, key)) {
                return (byte[]) getBytesMethod.invoke(compound, key);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}

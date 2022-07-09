package com.molean.staticmap;

import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataHolder;
import org.bukkit.persistence.PersistentDataType;

import java.util.Locale;
import java.util.UUID;

public class PDHSimplified {

    private PersistentDataHolder persistentDataHolder;

    private final static String NAMESPACE = "staticmap";


    private PDHSimplified(PersistentDataHolder persistentDataHolder) {
        this.persistentDataHolder = persistentDataHolder;
    }

    public static PDHSimplified of(PersistentDataHolder persistentDataHolder) {
        return new PDHSimplified(persistentDataHolder);
    }

    public boolean has(String key) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        return persistentDataContainer.has(new NamespacedKey(NAMESPACE, key.toLowerCase(Locale.ROOT)));
    }


    public void setBytes(String key, byte[] bytes) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        persistentDataContainer.set(new NamespacedKey(NAMESPACE, key.toLowerCase(Locale.ROOT)), PersistentDataType.BYTE_ARRAY, bytes);
    }

    public void setString(String key, String string) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        persistentDataContainer.set(new NamespacedKey(NAMESPACE, key.toLowerCase(Locale.ROOT)),
                PersistentDataType.STRING, string);

    }

    public void setUUID(String key, UUID uuid) {
        setString(key.toLowerCase(Locale.ROOT), uuid.toString());

    }

    public UUID getUUID(String key) {
        String asString = getAsString(key.toLowerCase(Locale.ROOT));
        if (asString != null) {
            return UUID.fromString(asString);
        }
        return null;
    }

    public String getAsString(String key) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        return persistentDataContainer.get(new NamespacedKey(NAMESPACE, key.toLowerCase(Locale.ROOT)), PersistentDataType.STRING);
    }

    public byte[] getAsBytes(String key) {
        PersistentDataContainer persistentDataContainer = persistentDataHolder.getPersistentDataContainer();
        return persistentDataContainer.get(new NamespacedKey(NAMESPACE, key.toLowerCase(Locale.ROOT)), PersistentDataType.BYTE_ARRAY);
    }

}

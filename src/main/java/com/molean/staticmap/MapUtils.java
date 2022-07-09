package com.molean.staticmap;


import net.minecraft.world.level.saveddata.maps.MapItemSavedData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MapUtils {

    abstract static class MyMapRenderer extends MapRenderer {
        public int hashCode = 0;
    }

    public static void updateStaticMap(byte[] bytes, MapMeta mapMeta) {
        if (bytes == null) {
            return;
        }
        try {
            MapRenderer mapRenderer = Objects.requireNonNull(mapMeta.getMapView()).getRenderers().get(0);
            if (mapRenderer instanceof MyMapRenderer) {
                if (((MyMapRenderer) mapRenderer).hashCode == Arrays.hashCode(bytes)) {
                    return;
                }
            }
        } catch (Exception ignored) {

        }

        MapView mapView = Bukkit.createMap(Bukkit.getWorlds().get(0));
        while (!mapView.getRenderers().isEmpty()) {
            mapView.removeRenderer(mapView.getRenderers().get(0));
        }
        mapView.addRenderer(new MyMapRenderer() {
            @Override
            public void render(@NotNull MapView mapView, @NotNull MapCanvas mapCanvas, @NotNull Player player) {
                for (int i = 0; i < 128 * 128 && i < bytes.length; i++) {
                    int x = i % 128;
                    int y = i / 128;
                    byte color = bytes[i];
                    mapCanvas.setPixel(x, y, color);
                }
                hashCode = bytes.hashCode();
            }
        });
        mapMeta.setMapView(mapView);
    }


    public static byte[] getColors(MapMeta mapMeta) {
        MapView mapView = mapMeta.getMapView();

        StringBuilder colors = new StringBuilder();
        if (mapView == null) {
            return null;
        }
        List<MapRenderer> renderers = mapView.getRenderers();
        if (renderers.isEmpty()) {
            return null;
        }
        MapRenderer renderer = renderers.get(0);

        try {
            Field worldMapField = renderer.getClass().getDeclaredField("worldMap");
            worldMapField.setAccessible(true);
            MapItemSavedData worldMap = (MapItemSavedData) worldMapField.get(renderer);
            return worldMap.colors;

        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}

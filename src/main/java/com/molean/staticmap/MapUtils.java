package com.molean.staticmap;


import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.*;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

public class MapUtils {

    abstract static class MyMapRenderer extends MapRenderer {
        public int hashCode = 0;
    }

    public static void updateStaticMap(String colors, MapMeta mapMeta) {
        if (colors == null || colors.length() < 128 * 128 * 2) {
            return;
        }
        try {
            MapRenderer mapRenderer = Objects.requireNonNull(mapMeta.getMapView()).getRenderers().get(0);
            if(mapRenderer instanceof  MyMapRenderer){
                if (((MyMapRenderer) mapRenderer).hashCode==colors.hashCode()) {
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
                for (int i = 0; i < 128 * 128; i++) {
                    int x = i % 128;
                    int y = i / 128;
                    byte color = (byte) Integer.parseInt(colors.substring(i * 2, i * 2 + 2), 16);
                    mapCanvas.setPixel(x, y, color);
                }
                hashCode = colors.hashCode();
            }
        });
        mapMeta.setMapView(mapView);
    }


    public static String setPixel(String origin, int x, int y, int color) {
        if (origin == null || origin.length() < 128 * 128 * 2) {
            return origin;
        }
        if (x > 128 || x < 0 || y > 128 || y < 0 || color > 0xFF || color < 0) {
            return origin;
        }
        int p = (y * 128 + x) * 2;
        return origin.substring(0, p) + String.format("%02x", color) + origin.substring(p + 2);
    }

    public static String fillRectangle(String origin, int x1, int y1, int x2, int y2, int color) {
        String colors = origin;
        for (int i = x1; i < x2; i++) {
            for (int j = y1; j < y2; j++) {
                colors = setPixel(colors, i, j, color);
            }
        }
        return colors;

    }

    public static String drawText(String origin, int x, int y, MapFont font, String text) {
        String colors = origin;
        int xStart = x;
        byte color = 44;
        if (!font.isValid(text)) {
            return origin;
        }
        int i = 0;
        while (i < text.length()) {
            char ch = text.charAt(i);
            if (ch == '\n') {
                x = xStart;
                y += font.getHeight() + 1;
            } else if (ch == 167) {
                int j = text.indexOf(59, i);
                if (j < 0) {
                    break;
                }

                try {
                    color = Byte.parseByte(text.substring(i + 1, j));
                    i = j;
                } catch (NumberFormatException var12) {
                    return origin;
                }
            } else {
                MapFont.CharacterSprite sprite = font.getChar(text.charAt(i));
                assert sprite != null;
                for (int r = 0; r < font.getHeight(); ++r) {
                    for (int c = 0; c < sprite.getWidth(); ++c) {
                        if (sprite.get(r, c)) {
                            colors = setPixel(colors, x + c, y + r, unsignedByteToInt(color));
                        }
                    }
                }
                x += sprite.getWidth() + 1;
            }
            ++i;
        }
        return colors;

    }

    public static int unsignedByteToInt(byte b) {
        return Integer.parseInt(String.format("%x", b), 16);
    }

    public static String drawImage(String origin, int x, int y, BufferedImage image) {
        String colors = origin;
        if (image == null) {
            return origin;
        }
        for (int x2 = 0; x2 < image.getWidth(null); ++x2) {
            for (int y2 = 0; y2 < image.getHeight(null); ++y2) {
                Color color = new Color(image.getRGB(x2, y2));
                @SuppressWarnings("deprecation")
                byte b = MapPalette.matchColor(color);
                colors = setPixel(colors, x + x2, y + y2, unsignedByteToInt(b));
            }
        }
        return colors;
    }

    public static String getColors(MapMeta mapMeta) {
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
            Object worldMap = worldMapField.get(renderer);
            Field worldMapColorsField = worldMap.getClass().getDeclaredField("g");
            worldMapColorsField.setAccessible(true);
            byte[] bytes = (byte[]) worldMapColorsField.get(worldMap);
            for (int y = 0; y < 128; y++) {
                for (int x = 0; x < 128; x++) {
                    Byte color = bytes[y * 128 + x];
                    colors.append(String.format("%02x", color));
                }
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
        return colors.toString();
    }

}

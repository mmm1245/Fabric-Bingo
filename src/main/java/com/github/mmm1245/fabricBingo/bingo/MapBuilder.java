package com.github.mmm1245.fabricBingo.bingo;

import net.minecraft.block.MaterialColor;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapState;
import net.minecraft.server.world.ServerWorld;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.IOException;
import java.util.Arrays;
import java.util.Objects;

public class MapBuilder {

    private static final double shadeCoeffs[] = { 0.71, 0.86, 1.0, 0.53 };
    public static BufferedImage createGrid(BingoItem[] items, boolean[] checked) {
        if(checked.length != 9) throw new IllegalArgumentException("check array length must be 9");
        if(items.length != 9) throw new IllegalArgumentException("items array length must be 9");
        BufferedImage bufferedImage = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = bufferedImage.getGraphics();
        graphics.setColor(new Color(153, 102, 51));
        graphics.fillRect(0, 0, 128, 128);
        graphics.setColor(Color.BLACK);
        graphics.drawLine(128/3, 0, 128/3, 128);
        graphics.drawLine(128/3*2, 0, 128/3*2, 128);

        graphics.drawLine(0, 128/3, 128, 128/3);
        graphics.drawLine(0, 128/3*2, 128, 128/3*2);

        for(int i = 0;i < 9;i++){
            boolean checkCell = checked[i];
            int x = (i%3)*128/3;
            int y = (i/3)*128/3;
            try {
                graphics.drawImage(getItemTex(items[i].texture), x, y, 42, 42, null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            if(checkCell){
                try {
                    graphics.drawImage(getItemTex("done"), x, y, 42, 42, null);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        return bufferedImage;
    }
    public static ItemStack create(ServerWorld world, BufferedImage image){
        ItemStack stack = FilledMapItem.createMap(world , 0, 0, (byte) 3, false, false);
        MapState state = FilledMapItem.getMapState(stack, world);
        state.locked = true;
        Image resizedImage = image.getScaledInstance(128, 128, Image.SCALE_DEFAULT);
        BufferedImage resized = convertToBufferedImage(resizedImage);
        int width = resized.getWidth();
        int height = resized.getHeight();
        int[][] pixels = convertPixelArray(resized);
        MaterialColor[] mapColors = MaterialColor.COLORS;
        Color imageColor;
        mapColors = Arrays.stream(mapColors).filter(Objects::nonNull).toArray(MaterialColor[]::new);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                imageColor = new Color(pixels[j][i], true);
                state.colors[i + j * width] = (byte) nearestColor(mapColors, imageColor);
            }
        }
        return stack;
    }
    public static BufferedImage getItemTex(String tex) throws IOException {
        return ImageIO.read(MapBuilder.class.getResourceAsStream("/items/" + tex + ".png"));
    }

    private static int nearestColor(MaterialColor[] colors, Color imageColor) {
        double[] imageVec = { (double) imageColor.getRed() / 255.0, (double) imageColor.getGreen() / 255.0,
                (double) imageColor.getBlue() / 255.0 };
        int best_color = 0;
        double lowest_distance = 10000;
        for (int k = 0; k < colors.length; k++) {
            Color mcColor = new Color(colors[k].color);
            double[] mcColorVec = { (double) mcColor.getRed() / 255.0, (double) mcColor.getGreen() / 255.0,
                    (double) mcColor.getBlue() / 255.0 };
            for (int shadeInd = 0; shadeInd < shadeCoeffs.length; shadeInd++) {
                double distance = distance(imageVec, applyShade(mcColorVec, shadeInd));
                if (distance < lowest_distance) {
                    lowest_distance = distance;
                    // todo: handle shading with alpha values other than 255
                    if (k == 0 && imageColor.getAlpha() == 255) {
                        best_color = 119;
                    } else {
                        best_color = k * shadeCoeffs.length + shadeInd;
                    }
                }
            }
        }
        return best_color;
    }

    private static double distance(double[] vectorA, double[] vectorB) {
        return Math.sqrt(Math.pow(vectorA[0] - vectorB[0], 2) + Math.pow(vectorA[1] - vectorB[1], 2)
                + Math.pow(vectorA[2] - vectorB[2], 2));
    }

    private static int[][] convertPixelArray(BufferedImage image) {

        final byte[] pixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        final int width = image.getWidth();
        final int height = image.getHeight();

        int[][] result = new int[height][width];
        final int pixelLength = 4;
        for (int pixel = 0, row = 0, col = 0; pixel + 3 < pixels.length; pixel += pixelLength) {
            int argb = 0;
            argb += (((int) pixels[pixel] & 0xff) << 24); // alpha
            argb += ((int) pixels[pixel + 1] & 0xff); // blue
            argb += (((int) pixels[pixel + 2] & 0xff) << 8); // green
            argb += (((int) pixels[pixel + 3] & 0xff) << 16); // red
            result[row][col] = argb;
            col++;
            if (col == width) {
                col = 0;
                row++;
            }
        }

        return result;
    }

    private static double[] applyShade(double[] color, int ind) {
        double coeff = shadeCoeffs[ind];
        return new double[] { color[0] * coeff, color[1] * coeff, color[2] * coeff };
    }

    private static BufferedImage convertToBufferedImage(Image image) {
        BufferedImage newImage = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g = newImage.createGraphics();
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return newImage;
    }
}

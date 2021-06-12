package ru.hiddenalt.mtbe.utils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;

public class BufferedImageManipulator {

    protected BufferedImage bufferedImage;

    public BufferedImageManipulator(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage getBufferedImage() {
        return bufferedImage;
    }

    public void setBufferedImage(BufferedImage bufferedImage) {
        this.bufferedImage = bufferedImage;
    }

    public BufferedImage bakeCopy(){
        BufferedImage tmp = new BufferedImage(
                this.bufferedImage.getWidth(),
                this.bufferedImage.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        Graphics g = tmp.getGraphics();
        g.drawImage(this.bufferedImage, 0, 0, null);
        g.dispose();

        return tmp;
    }

    public BufferedImageManipulator makeNegative(){
        BufferedImage tmp = bakeCopy();
        int w = tmp.getWidth();
        int h = tmp.getHeight();

        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int pixel = tmp.getRGB(x,y);
                Color col = new Color(pixel, true);

                int a = col.getAlpha();
                if(a == 0) continue;

                int r = col.getRed();
                int g = col.getGreen();
                int b = col.getBlue();

                col = new Color(
                    255 - r,
                    255 - g,
                    255 - b,
                    a
                );
                tmp.setRGB(x, y, col.getRGB());
            }
        }
        this.bufferedImage = tmp;
        return this;
    }

    public BufferedImageManipulator makeSharpen(){
        Kernel kernel = new Kernel(3,3, new float[]{
                0.f, -1.f, 0.f,
                -1.f, 5.0f, -1.f,
                0.f, -1.f, 0.f});
        ConvolveOp cop = new ConvolveOp(kernel,
                ConvolveOp.EDGE_NO_OP,
                null);
        this.bufferedImage = cop.filter(this.bufferedImage, null);
        return this;
    }

    public BufferedImageManipulator makeGrayscale(){
        BufferedImage tmp = bakeCopy();
        int w = tmp.getWidth();
        int h = tmp.getHeight();

        for (int x = 0; x < w; x++){
            for (int y = 0; y < h; y++){
                int pixel = tmp.getRGB(x,y);
                Color col = new Color(pixel, true);

                int a = col.getAlpha();
                if(a == 0) continue;

                int r = col.getRed();
                int g = col.getGreen();
                int b = col.getBlue();

                int avg = (r+g+b)/3;

                tmp.setRGB(x, y, (a<<24) | (avg<<16) | (avg<<8) | avg);
            }
        }
        this.bufferedImage = tmp;
        return this;
    }


}

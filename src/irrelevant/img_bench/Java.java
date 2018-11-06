package irrelevant.img_bench;

import java.awt.Frame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

public class Java extends Frame {
    public int width = 640;
    public int height = 480;
    public BufferedImage image;
    public int[] pixels;
    public int[] cs = {16711680, 167123};

    public static void main(String[] args) {
        new Java();
    }

    public Java() {
        image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
        setSize(width, height);
        setBackground(Color.black);
        setFocusableWindowState(false);
        setVisible(true);

        for (int ti = 0; ti < 200; ti++){
            long t = System.currentTimeMillis();
            for (int c = 0; c < 2; c++) {
                for (int i = 0 ; i < width; i++) {
                    for (int j = 0; j < height; j++) {
                        pixels[i + j * width] = cs[c];
                    }
                }
                repaint();
            }
            System.out.println(System.currentTimeMillis() - t);
            try {
                Thread.sleep(500);
            } catch (Exception e) {};
        }
    }
    public void update(Graphics g) {
        paint(g);
    }
    public void paint(Graphics g) {
        g.drawImage(image, 0, 0, null);
    }
}
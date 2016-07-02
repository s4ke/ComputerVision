package de.ubt.ai3.computervision;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.DirectColorModel;
import java.awt.image.PixelGrabber;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class TraitDetermination {

    public static double firstInvariantMoment(int[][] imArray) {
        double firstInvariantMoment = 0;
        double[][] tmpImArray = new double[imArray.length][imArray[0].length];
        // setting the grey values from int to double values ranging from 0 = black to 1 = white
        for (int i = 0; i < imArray.length; i++) {
            for (int j = 0; j < imArray[0].length; j++) {
                tmpImArray[i][j] = imArray[i][j] / 255.0d;
            }
        }
        // compute gray centre
        double m00 = 0;
        double m10 = 0;
        double m01 = 0;
        for (int y = 0; y < imArray.length; y++) {
            for (int x = 0; x < imArray[0].length; x++) {
                m00 += tmpImArray[y][x] * 1 * 1;
                m10 += tmpImArray[y][x] * x * 1;
                m01 += tmpImArray[y][x] * 1 * y;
            }
        }
        double sx = m10 / m00;
        double sy = m01 / m00;


        // compute central moments
        double centralMoment00 = 0;
        double centralMoment20 = 0;
        double centralMoment02 = 0;
        for (int y = 0; y < imArray.length; y++) {
            for (int x = 0; x < imArray[0].length; x++) {
                centralMoment00 += tmpImArray[y][x] * 1 * 1;
                centralMoment20 += tmpImArray[y][x] * ((x - sx) * (x - sx)) * 1;
                centralMoment02 += tmpImArray[y][x] * 1 * ((y - sy) * (y - sy));
            }
        }

        // compute normalized central moments
        double normCentralMoment20 = 0;
        double normCentralMoment02 = 0;
        // gamma = 2 for both cases
        normCentralMoment02 = (centralMoment02 / Math.pow(centralMoment00, 2));
        normCentralMoment20 = (centralMoment20 / Math.pow(centralMoment00, 2));

        // compute first invariant moment
        firstInvariantMoment = normCentralMoment20 + normCentralMoment02;

        return firstInvariantMoment;
    }

    public static double objectSize(int[][] imArray) {
        double objectSize = 0;
        for (int i = 0; i < imArray.length; i++) {
            for (int j = 0; j < imArray[0].length; j++) {
                if (imArray[i][j] != 0) {
                    objectSize++;
                }
            }
        }
        return objectSize;
    }


    public static void main(String[] args) throws Exception {

        // Load Image from file into 2D-array
        // First index is row, second index is column of image
        // Array contains grayscale values in range 0-255
        for (int i = 1; i <= 4; ++i) {
            String fileName = "test" + i + ".png";
            int[][] imArray = loadFromFile(fileName);

            // image dimensions can be obtained with
            int height = imArray.length;
            int width = imArray[0].length;

            // Do your image processing here
            // for example setting left top pixel to white:
            //imArray[0][0] = 255;

            //#######################################################
            // computation of both traits for the four given examples
            //#######################################################

            double firstInvariantMoment = firstInvariantMoment(imArray);
            double objectSize = objectSize(imArray);

            System.out.println(fileName + ":");
            System.out.println("first invariant moment: " + firstInvariantMoment);
            System.out.println("object size: " + objectSize);
        }

        // Save array back to file
        //saveToFile(imArray, "out.png", "png");
    }


    /**
     * Save image to file
     *
     * @param pixels   2D-grayscale image from 0-255
     * @param filename e.g. "test.png"
     * @param type     e.g. "bmp", "png", ...
     */
    public static void saveToFile(int[][] pixels, String filename, String type) throws IOException {
        // Convert to RGB
        int height = pixels.length;
        int width = pixels[0].length;
        int[] rgb = new int[width * height];
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                rgb[y * width + x] = (pixels[y][x] << 16) + (pixels[y][x] << 8) + pixels[y][x];

        // save to file
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        image.setRGB(0, 0, width, height, rgb, 0, width);
        File file = new File(filename);
        ImageIO.write(image, type, file);
    }

    /**
     * Read image from file. Returns an 2D-array of int containing grayscale values
     * (0-255).
     */
    public static int[][] loadFromFile(String filename) throws IOException, InterruptedException {
        // read from file
        File file = new File(filename);
        Image img = ImageIO.read(file);

        int width = img.getWidth(null);
        int height = img.getHeight(null);
        if (width * height <= 0)
            throw new IOException("Image has no data!");

        int[] array = new int[width * height];
        PixelGrabber grabber = new PixelGrabber(img, 0, 0, width, height,
                array, 0, width);
        grabber.grabPixels();

        //make grayscale
        int[][] grayscale = new int[height][width];
        DirectColorModel colorModel = (DirectColorModel) ColorModel
                .getRGBdefault();
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++) {
                grayscale[y][x] = (colorModel.getRed(array[y * width + x])
                        + colorModel.getGreen(array[y * width + x]) + colorModel
                        .getBlue(array[y * width + x])) / 3;
            }

        return grayscale;
    }
}


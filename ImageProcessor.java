import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.BufferedImageOp;
import java.awt.image.Kernel;
import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;

public class ImageProcessor {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter the path of the image: ");
        String imagePath = scanner.nextLine();
        System.out.print("Enter the option (1 to 5): ");
        int option = scanner.nextInt();

        try {
            BufferedImage originalImage = ImageIO.read(new File(imagePath));
            String fileExtension = imagePath.substring(imagePath.lastIndexOf('.') + 1);

            if (!fileExtension.equalsIgnoreCase("jpg")) {
                throw new IOException("Invalid file format. Only JPG images are supported.");
            }

            BufferedImage processedImage = null;
            switch (option) {
                case 1:
                    processedImage = convertToGrayscale(originalImage);
                    break;
                case 2:
                    processedImage = applyBlur(originalImage);
                    break;
                case 3:
                    System.out.print("Enter the number of pixels to crop: ");
                    int cropSize = scanner.nextInt();
                    processedImage = cropImage(originalImage, cropSize);
                    break;
                case 4:
                    processedImage = applyNegative(originalImage);
                    break;
                case 5:
                    processedImage = removeRedAndGreen(originalImage);
                    break;
                default:
                    throw new IllegalArgumentException("Invalid image option.");
            }

            if (processedImage != null) {
                String processedImagePath = imagePath.substring(0, imagePath.lastIndexOf('.')) + "_processed.png";
                ImageIO.write(processedImage, "png", new File(processedImagePath));
                System.out.println("Processed image saved successfully.");
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static BufferedImage convertToGrayscale(BufferedImage image) {
        BufferedImage grayscaleImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_BYTE_GRAY);
        Graphics2D graphics = grayscaleImage.createGraphics();
        graphics.drawImage(image, 0, 0, null);
        graphics.dispose();
        return grayscaleImage;
    }

    private static BufferedImage applyBlur(BufferedImage image) {
        float blurRadius = 10.0f;
        BufferedImage blurredImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        int size = (int) (blurRadius * 3);
        float[] kernelData = new float[size * size];
        for (int i = 0; i < kernelData.length; i++) {
            kernelData[i] = 1.0f / (size * size);
        }
        Kernel kernel = new Kernel(size, size, kernelData);
        BufferedImageOp op = new ConvolveOp(kernel);
        blurredImage = op.filter(image, null);

        return blurredImage;
    }

    private static BufferedImage cropImage(BufferedImage image, int cropSize) {
        int width = image.getWidth();
        int height = image.getHeight();
        int croppedWidth = Math.min(cropSize, width);
        int croppedHeight = Math.min(cropSize, height);

        BufferedImage croppedImage = new BufferedImage(croppedWidth, croppedHeight, image.getType());
        Graphics2D graphics = croppedImage.createGraphics();
        graphics.drawImage(image, 0, 0, croppedWidth, croppedHeight, 0, 0, croppedWidth, croppedHeight, null);
        graphics.dispose();
        return croppedImage;
    }

    private static BufferedImage applyNegative(BufferedImage image) {
        BufferedImage negativeImage = new BufferedImage(image.getWidth(), image.getHeight(),
                BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                Color color = new Color(rgb, true);
                int red = 255 - color.getRed();
                int green = 255 - color.getGreen();
                int blue = 255 - color.getBlue();
                Color negativeColor = new Color(red, green, blue);
                negativeImage.setRGB(x, y, negativeColor.getRGB());
            }
        }
        return negativeImage;
    }

    private static BufferedImage removeRedAndGreen(BufferedImage image) {
        BufferedImage blueImage = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {
                int rgb = image.getRGB(x, y);
                int blue = rgb & 0xFF;
                int blueRGB = blue | (blue << 8) | (blue << 16) | (0xFF << 24);
                blueImage.setRGB(x, y, blueRGB);
            }
        }
        return blueImage;
    }
}

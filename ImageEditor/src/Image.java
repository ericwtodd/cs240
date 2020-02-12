import java.io.*;
import java.util.Scanner;


public class Image {
    int height;
    int width;
    int maxColor;
    String ID;
    Pixel[][] pixels;

    /*
    * fromFile takes a file and creates a 2-D array of pixels
    */
    static Image fromFile(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        scanner.useDelimiter("((#[^\\n]*\\n)|(\\s+))+");
        String imageID = scanner.next();

        //Get the Width and Height of the Image from the file
        int width = scanner.nextInt();
        int height = scanner.nextInt();
        int maxColor = scanner.nextInt();

        //Once past the header formatting,
        //Populate the Image Pixels from the file
        Image image = new Image();
        image.ID = imageID;
        image.height = height;
        image.width = width;
        image.maxColor = maxColor;

        //And Make Pixels a 2-D Array with "height" rows and "width" columns
        image.pixels = new Pixel[height][width];

        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                image.pixels[i][j] = new Pixel(scanner.nextInt(),scanner.nextInt(),scanner.nextInt());
            }
        }
        return image;
    }

    File toFile(File outputFile) throws FileNotFoundException {
        try {
            PrintWriter out = new PrintWriter(outputFile, "UTF-8");
            //Add the Header to the file: P3, width, height, max color value
            out.write( ID + "\n");
            out.write(String.valueOf(width) + " " + String.valueOf(height) + "\n");
            out.write(String.valueOf(maxColor) + "\n");
            //export the pixel color values to the outImage
            for (int i = 0; i < this.height; i++) {
                for (int j = 0; j < this.width; j++) {
                out.write(String.valueOf(this.pixels[i][j].getRed()) + "\n");
                out.write(String.valueOf(this.pixels[i][j].getGreen()) + "\n");
                out.write(String.valueOf(this.pixels[i][j].getBlue()) + "\n");
                }
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            System.out.println("Error: File Format");
        }
        return outputFile;
    }

    Image invertImage()
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++)
            {
                this.pixels[i][j].setRed(255 - this.pixels[i][j].getRed());
                this.pixels[i][j].setGreen(255 - this.pixels[i][j].getGreen());
                this.pixels[i][j].setBlue(255 - this.pixels[i][j].getBlue());
            }
        }
        return this;
    }

    Image grayscaleImage()
    {
        for (int i = 0; i < height; i++ )
        {
            for (int j = 0; j < width; j++)
            {
                int grayValue = (this.pixels[i][j].getRed() + this.pixels[i][j].getGreen() + this.pixels[i][j].getBlue())/3;
                this.pixels[i][j].setAll(grayValue);
            }
        }
        return this;
    }

    Image embossImage()
    {
        for (int i = this.height - 1; i >= 0; i-- )
        {
            for (int j = this.width - 1; j >= 0; j--)
            {
                int embossValue;
                if (i == 0 || j  == 0) //If it's on the edge, it's value should be 128.
                {
                    embossValue = 128;
                }
                else
                {
                    int redDiff = pixels[i][j].getRed() - pixels[i-1][j-1].getRed();
                    int greenDiff = pixels[i][j].getGreen() - pixels[i-1][j-1].getGreen();
                    int blueDiff = pixels[i][j].getBlue() - pixels[i-1][j-1].getBlue();
                    int maxDifference =
                            (Math.abs(redDiff) >= Math.max(Math.abs(greenDiff), Math.abs(blueDiff))) ? redDiff :
                            (Math.abs(greenDiff) >= Math.abs(blueDiff) ? greenDiff:blueDiff);
                    embossValue = 128 + maxDifference;
                    if(embossValue < 0){embossValue = 0;}
                    else if (embossValue > 255){embossValue = 255;}
                }
                this.pixels[i][j].setAll(embossValue);
            }
        }
        return this;
    }

    Image blurImage(int n)
    {
        for (int i = 0; i < height; i++)
        {
            for (int j = 0; j < width; j++) //For each pixel
            {
                int avgRed = 0;
                int avgGreen = 0;
                int avgBlue = 0;
                int rightLimit = Math.min(j + n, width);
                for (int k = j; k < rightLimit ; k++)
                {
                    avgRed += this.pixels[i][k].getRed();
                    avgGreen += this.pixels[i][k].getGreen();
                    avgBlue += this.pixels[i][k].getBlue();
                }
                if (n == 1)
                {
                    avgRed = this.pixels[i][j].getRed();
                    avgGreen = this.pixels[i][j].getGreen();
                    avgBlue = this.pixels[i][j].getBlue();
                }
                else
                {
                    avgRed = avgRed / (rightLimit - j);
                    avgGreen = avgGreen / (rightLimit - j);
                    avgBlue = avgBlue / (rightLimit - j);
                }
                this.pixels[i][j].setRed(avgRed);
                this.pixels[i][j].setGreen(avgGreen);
                this.pixels[i][j].setBlue(avgBlue);
            }
        }
        return this;
    }
}
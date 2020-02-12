import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.attribute.UserDefinedFileAttributeView;


public class ImageEditor {

    private static final String USAGE = "USAGE: java ImageEditor in-file out-file"
            + " (grayscale|invert|emboss|motionblur motion-blur-length)";

    public static void main(String[] args) {
        Image image = null;
        if (args.length < 3 || args.length > 4) {
            System.out.println(USAGE);
            return;
        }
        else {
            File file = new File(args[0]);
            File outputFile = new File(args[1]);
            try {
                image = Image.fromFile(file);
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                System.out.println(USAGE);
                return;
            }

            switch (args.length)
            {
                case (3):
                {
                    switch(args[2]) {
                        case ("invert"): {
                            image = image.invertImage();
                            break;
                        }
                        case ("grayscale"): {
                            image = image.grayscaleImage();
                            break;
                        }
                        case ("emboss"): {
                            image = image.embossImage();
                            break;
                        }
                        case ("nothing"):
                        {
                            image = image;
                            break;
                        }
                        default:
                        {
                            System.out.println(USAGE);
                            return;
                        }
                    }
                    break;
                }
                case (4):
                {
                    switch (args[2])
                    {
                        case ("motionblur"): {
                            if (Integer.parseInt(args[3]) <= 0)
                            {
                                System.out.println(USAGE);
                                return;
                            }
                            image = image.blurImage(Integer.parseInt(args[3]));
                            break;
                        }
                        default:
                        {
                            System.out.println(USAGE);
                            return;
                        }
                    }
                    break;
                }
                default:
                {
                    System.out.println(USAGE);
                    return;
                }
            }
            try {
                image.toFile(outputFile);
            } catch (FileNotFoundException e) {
                System.out.println("File Not Found.");
                System.out.println(USAGE);
                return;
            }
        }
    }
}

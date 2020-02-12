public class Pixel {
    private int red;
    private int green;
    private int blue;

    public Pixel(int r, int g, int b)
    {
        red = r;
        green = g;
        blue = b;
    }

    public int getRed(){
        return red;
    }
    public int getGreen(){
        return green;
    }
    public int getBlue()
    {
        return blue;
    }

    public void setRed(int newRed)
    {
        red = newRed;
    }
    public void setGreen(int newGreen)
    {
        green = newGreen;
    }
    public void setBlue(int newBlue)
    {
        blue = newBlue;
    }

    public void setAll(int newValue)
    {
        red = newValue;
        green = newValue;
        blue = newValue;
    }
}

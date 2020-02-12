package edu.byu.erictodd.familymapclient.model;


import android.graphics.Color;

import java.util.ArrayList;
import java.util.List;

import edu.byu.erictodd.familymapclient.R;

/**
 * A class that keeps track of the app's settings preferences, including the whether or not life story lines,
 * family tree lines, and spouse lines are enabled, as well as each of those individual colors.
 * It also stores which map type is selected, and a list of available colors for the lines.
 */
public class Settings {

    /**
     * Default constructor that initializes all the settings default preferences.
     * The lines are default to off, and the line colors are default to green, blue, and red.
     * The default map type is "normal"
     */
    public Settings() {
        //Default Settings:
        mLifeStoryLinesColor = "Green";
        mFamilyTreeLinesColor = "Blue";
        mSpouseLinesColor = "Red";

        mLifeStoryLinesEnabled = false;
        mFamilyTreeLinesEnabled = false;
        mSpouseLinesEnabled = false;

        mMapType = "Normal";

        availableColors = new ArrayList<>();
        availableColors.add("Azure");
        availableColors.add("Blue");
        availableColors.add("Cyan");
        availableColors.add("Green");
        availableColors.add("Magenta");
        availableColors.add("Orange");
        availableColors.add("Red");
        availableColors.add("Rose");
        availableColors.add("Violet");
        availableColors.add("Yellow");
    }

    /**
     * The color of the life story lines, stored as a string
     */
    private String mLifeStoryLinesColor;
    /**
     * A flag determining whether or not life story lines are enabled
     */
    private boolean mLifeStoryLinesEnabled;

    /**
     * The color of the family tree lines, stored as a string
     */
    private String mFamilyTreeLinesColor;
    /**
     * A flag determining whether or not the family trees lines are enabled
     */
    private boolean mFamilyTreeLinesEnabled;

    /**
     * The colors of the spouse lines, stored as a string
     */
    private String mSpouseLinesColor;
    /**
     * A flag determining whether or not the spouse lines are enabled.
     */
    private boolean mSpouseLinesEnabled;

    /**
     * The type of the map that is to be displayed, stored as a string
     */
    private String mMapType;

    /**
     * A static list of the available colors to choose from for the lines.
     */
    private static List<String> availableColors;

    public String getLifeStoryLinesColor() {
        return mLifeStoryLinesColor;
    }

    public void setLifeStoryLinesColor(String lifeStoryLinesColor) {
        mLifeStoryLinesColor = lifeStoryLinesColor;
    }

    public boolean isLifeStoryLinesEnabled() {
        return mLifeStoryLinesEnabled;
    }

    public void setLifeStoryLinesEnabled(boolean lifeStoryLinesEnabled) {
        mLifeStoryLinesEnabled = lifeStoryLinesEnabled;
    }

    public String getFamilyTreeLinesColor() {
        return mFamilyTreeLinesColor;
    }

    public void setFamilyTreeLinesColor(String familyTreeLinesColor) {
        mFamilyTreeLinesColor = familyTreeLinesColor;
    }

    public boolean isFamilyTreeLinesEnabled() {
        return mFamilyTreeLinesEnabled;
    }

    public void setFamilyTreeLinesEnabled(boolean familyTreeLinesEnabled) {
        mFamilyTreeLinesEnabled = familyTreeLinesEnabled;
    }

    public String getSpouseLinesColor() {
        return mSpouseLinesColor;
    }

    public void setSpouseLinesColor(String spouseLinesColor) {
        mSpouseLinesColor = spouseLinesColor;
    }

    public boolean isSpouseLinesEnabled() {
        return mSpouseLinesEnabled;
    }

    public void setSpouseLinesEnabled(boolean spouseLinesEnabled) {
        mSpouseLinesEnabled = spouseLinesEnabled;
    }

    public String getMapType() {
        return mMapType;
    }

    public void setMapType(String mapType) {
        mMapType = mapType;
    }

    /**
     * Returns the corresponding index in the availableColors Array for a given color
     *
     * @param color given color
     * @return index of the color in the array
     */
    public int getColorIndex(String color) {
        return availableColors.indexOf(color);
    }

    /**
     * Returns the hue of the color of the line passed in
     *
     * @param color the line color passed in
     * @return the hue of the line to be displayed
     */
    public int colorStringToHue(String color) {
        if (color.equals("Red")) {
            return Color.RED;
        } else if (color.equals("Orange")) {
            return Color.rgb(255, 102, 0);
        } else if (color.equals("Yellow")) {
            return Color.YELLOW;
        } else if (color.equals("Green")) {
            return Color.GREEN;
        } else if (color.equals("Cyan")) {
            return Color.CYAN;
        } else if (color.equals("Azure")) {
            return Color.rgb(153, 255, 255);
        } else if (color.equals("Blue")) {
            return Color.BLUE;
        } else if (color.equals("Violet")) {
            return Color.rgb(127, 0, 255);
        } else if (color.equals("Magenta")) {
            return Color.MAGENTA;
        } else {
            return Color.rgb(255, 51, 153);
        }
    }

    /**
     * Returns the corresponding index in the mapTypes Array found in the string resources file
     * for a given MapType
     *
     * @param mapType given MapType
     * @return index of mapType in the array
     */
    public int getMapTypeIndex(String mapType) {
        if (mapType.equals("Normal")) {
            return 0;
        } else if (mapType.equals("Hybrid")) {
            return 1;
        } else if (mapType.equals("Satellite")) {
            return 2;
        } else if (mapType.equals("Terrain")) {
            return 3;
        } else {
            return 0;
        }
    }
}

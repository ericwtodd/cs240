package server;

import api.model.Location;
import com.google.gson.Gson;

import java.io.*;
import java.util.ArrayList;

/**
 * Generates random names and locations for Person and Event objects
 */
public class Generator {

    /**
     * Default Constructor - used to create an empty Generator object
     */
    public Generator() {
    }

    /**
     * Default Constructor reads in all the names and locations from the JSON files and converts them
     * to ArrayLists to be used in Generation of Persons and Events
     */
    public Generator(boolean build) {
        if (build) {
            try {
                Gson gson = new Gson();
                File locationsFile = new File("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\json\\locations.json");
                File femaleNamesFile = new File("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\json\\femaleNames.json");
                File maleNamesFile = new File("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\json\\maleNames.json");
                File lastNamesFile = new File("C:\\Users\\Eric Todd\\IdeaProjects\\FamilyMapServer\\json\\lastNames.json");

                Reader locationsReader = new InputStreamReader(new FileInputStream(locationsFile));
                Generator generator1 = gson.fromJson(locationsReader, Generator.class);
                this.setLocations(generator1.getLocations());

                Reader femaleReader = new InputStreamReader(new FileInputStream(femaleNamesFile));
                Generator generator2 = gson.fromJson(femaleReader, this.getClass());
                this.setFemaleNames(generator2.getFemaleNames());

                Reader maleReader = new InputStreamReader(new FileInputStream(maleNamesFile));
                Generator generator3 = gson.fromJson(maleReader, this.getClass());
                this.setMaleNames(generator3.getMaleNames());

                Reader lastNamesReader = new InputStreamReader(new FileInputStream(lastNamesFile));
                Generator generator4 = gson.fromJson(lastNamesReader, this.getClass());
                this.setLastNames(generator4.getLastNames());

                locationsReader.close();
                femaleReader.close();
                maleReader.close();
                lastNamesReader.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * A list of male first names
     */
    private ArrayList<String> maleNames;
    /**
     * A list of female first names
     */
    private ArrayList<String> femaleNames;
    /**
     * A list of last names
     */
    private ArrayList<String> lastNames;
    /**
     * A list of Location objects
     */
    private ArrayList<Location> locations;

    public ArrayList<String> getMaleNames() {
        return maleNames;
    }

    public void setMaleNames(ArrayList<String> maleNames) {
        this.maleNames = maleNames;
    }

    public ArrayList<String> getFemaleNames() {
        return femaleNames;
    }

    public void setFemaleNames(ArrayList<String> femaleNames) {
        this.femaleNames = femaleNames;
    }

    public ArrayList<String> getLastNames() {
        return lastNames;
    }

    public void setLastNames(ArrayList<String> lastNames) {
        this.lastNames = lastNames;
    }

    public ArrayList<Location> getLocations() {
        return locations;
    }

    public void setLocations(ArrayList<Location> locations) {
        this.locations = locations;
    }

    /**
     * Returns a random male name from the maleNames array
     *
     * @return a male name from the array
     */
    public String generateMaleName() {
        int upper = maleNames.size();
        int lower = 0;
        int randomNameIndex = (int) (Math.random() * (upper - lower)) + lower;
        return maleNames.get(randomNameIndex);
    }

    /**
     * Returns a random female name from the femaleNames array
     *
     * @return a female name from the array
     */
    public String generateFemaleName() {
        int upper = femaleNames.size();
        int lower = 0;
        int randomNameIndex = (int) (Math.random() * (upper - lower)) + lower;
        return femaleNames.get(randomNameIndex);
    }

    /**
     * Returns a random last name from the lastNames array
     *
     * @return a last name from the array
     */
    public String generateLastName() {
        int upper = lastNames.size();
        int lower = 0;
        int randomNameIndex = (int) (Math.random() * (upper - lower)) + lower;
        return lastNames.get(randomNameIndex);
    }

    /**
     * Returns a random location from the locations array
     *
     * @return a location from the array
     */
    public Location generateLocation() {
        int upper = locations.size();
        int lower = 0;
        int randomNameIndex = (int) (Math.random() * (upper - lower)) + lower;
        return locations.get(randomNameIndex);
    }
}

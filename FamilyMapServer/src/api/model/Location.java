package api.model;

/**
 * Simple data object used to populate random events for a Person
 */
public class Location {

    /**
     * Country in which event occurred
     */
    private String country;
    /**
     * City in which event occurred
     */
    private String city;
    /**
     * Latitude of event's location
     */
    private String latitude;
    /**
     * Longitude of event's location
     */
    private String longitude;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }
}

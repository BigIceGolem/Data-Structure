
package Delivery;


public class Rider {
    private String riderId;
    private String name;
    private double distance;
    private double eta;
    private boolean isAvailable;
    
    Rider(String riderId,String name){
        this.riderId=riderId;
        this.name= name;
        this.isAvailable = true;
    }

    /**
     * @return the riderId
     */
    public String getRiderId() {
        return riderId;
    }

    /**
     * @param riderId the riderId to set
     */
    public void setRiderId(String riderId) {
        this.riderId = riderId;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

    /**
     * @param distance the distance to set
     */
    public void setDistance(double distance) {
        this.distance = distance;
    }

    /**
     * @return the eta
     */
    public double getEta() {
        return eta;
    }

    /**
     * @param eta the eta to set
     */
    public void setEta(double eta) {
        this.eta = eta;
    }

    /**
     * @return the isAvailable
     */
    public boolean isIsAvailable() {
        return isAvailable;
    }

    /**
     * @param isAvailable the isAvailable to set
     */
    public void setIsAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }
    
            
}

package Delivery;

public class Rider {
    private String id;
    private String name;
    private double eta;

    public Rider(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getName() { return name; }
    public double getEta() { return eta; }
    public void setEta(double eta) { this.eta = eta; }
}
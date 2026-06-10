package Delivery;

import java.util.Comparator;
import java.util.PriorityQueue;

public class DeliverySystem {

    PriorityQueue<Rider> riderheap = new PriorityQueue<>(Comparator.comparingDouble(r -> r.getEta()));

    public void addRider(Rider r) {
        riderheap.add(r);
    }

    public Rider assignRider() {
        if (riderheap.isEmpty()){
        }
        return riderheap.poll();
    }

    public void loadDummyRiders() {
        Rider r1 = new Rider("001", "ali");
        Rider r2 = new Rider("002", "abu");
        Rider r3 = new Rider("003", "ahmad");
        Rider r4 = new Rider("004", "sarah");
        Rider r5 = new Rider("005", "abi");

        r1.setEta(10);
        r2.setEta(92);
        r3.setEta(24);
        r4.setEta(5);
        r5.setEta(43);

        addRider(r1);
        addRider(r2);
        addRider(r3);
        addRider(r4);
        addRider(r5);

       
    }

    DeliverySystem() {
        loadDummyRiders();
    }

    
  /* just to test out
    public static void main(String[] args) {
    
        DeliverySystem ds = new DeliverySystem();

        System.out.println(ds.assignRider().getName());
    }*/ 
}
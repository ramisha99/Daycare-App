package edu.lawrence.daycare.data;

public class Provider {
    public Provider() {}

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name;}
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
    public String getMinAge() { return minAge; }
    public void setMinAge(String minAge) { this.minAge = minAge; }
    public String getMaxAge() { return maxAge; }
    public void setMaxAge(String maxAge) { this.maxAge = maxAge; }
    
    private int id;
    private String name;
    private String address;
    private String city;
    private int capacity;
    private String minAge;
    private String maxAge; 
}

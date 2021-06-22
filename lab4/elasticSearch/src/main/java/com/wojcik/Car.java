package com.wojcik;
import lombok.Data;

@Data
public class Car {


    private Long id;


    private String brand;

    public Car() {
    }

    public Car(String brand) {
        this.brand = brand;
    }

    public Long getId() {
        return id;
    }

    public String getBrand() {
        return brand;
    }


    Workstation workstation = new Workstation();

    public void addWorkstation(Workstation workstation){
        this.workstation = workstation;

    }

    public Car(Long id, String brand) {
        this.id = id;
        this.brand = brand;
    }

    public void deleteWorkstation(Long id){
        if(this.workstation.getId() == id){
            this.workstation = null;
        }
        else {
            System.out.println("This car isn`t on that station");
        }
    }

    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", brand='" + brand + '\'' +
                '}';
    }
}

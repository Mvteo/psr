package com.wojcik;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

import java.util.HashSet;
import java.util.Set;


@Data
public class Workstation {


    private Long id;

    private String name;


    public Workstation(){

    }

    public Workstation(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Workstation(String name) {
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private Set<Employee> employees = new HashSet<>();

    public Set<Employee> getEmployees() {
        return employees;
    }

    public void addWorker(Employee employee){employees.add(employee);}

    @Override
    public String toString() {
        return "Workstation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}

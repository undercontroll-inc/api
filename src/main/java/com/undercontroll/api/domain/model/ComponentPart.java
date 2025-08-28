package com.undercontroll.api.domain.model;


import jakarta.persistence.*;

@Entity
@Table(name = "components")
public class ComponentPart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String description;

    private String brand;

    private Double price;

    private String supplier;

    private String category;

    public ComponentPart(String name, String description, String brand, Double price, String supplier, String category) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.supplier = supplier;
        this.category = category;
    }

    public ComponentPart() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}

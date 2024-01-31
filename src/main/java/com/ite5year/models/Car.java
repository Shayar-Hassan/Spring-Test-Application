package com.ite5year.models;



import com.ite5year.optimisticlock.VersionedEntity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;


@Entity
@Table(name = "car")
public class Car implements VersionedEntity, Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private double price;
    private int seatsNumber;
    private Date dateOfSale;
    private double priceOfSale;
    private String payerName;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private ApplicationUser owner;

    @Version
    private Long version;
    public Car() {
    }

    public ApplicationUser getOwner() {
        return owner;
    }

    public void setOwner(ApplicationUser owner) {
        this.owner = owner;
    }

    public Car(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public Car(String name, double price, int seatsNumber, Date dateOfSale, double priceOfSale) {
        this.name = name;
        this.price = price;
        this.seatsNumber = seatsNumber;
        this.dateOfSale = dateOfSale;
        this.priceOfSale = priceOfSale;
    }

    public Car(Long id, String name, double price, int seatsNumber, Date dateOfSale, double priceOfSale) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.seatsNumber = seatsNumber;
        this.dateOfSale = dateOfSale;
        this.priceOfSale = priceOfSale;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getSeatsNumber() {
        return seatsNumber;
    }

    public void setSeatsNumber(int seatsNumber) {
        this.seatsNumber = seatsNumber;
    }

    public Date getDateOfSale() {
        return dateOfSale;
    }

    public void setDateOfSale(Date dateOfSale) {
        this.dateOfSale = dateOfSale;
    }

    public double getPriceOfSale() {
        return priceOfSale;
    }

    public void setPriceOfSale(double priceOfSale) {
        this.priceOfSale = priceOfSale;
    }

    public String getPayerName() {
        return payerName;
    }

    public void setPayerName(String payerName) {
        this.payerName = payerName;
    }

    @Override
    public Long getVersion() {
        return version;
    }

    @Override
    public void setVersion(Long version) {
        this.version = version;
    }

    @Override
    public String getTableName() {
        return "car";
    }


    @Override
    public String toString() {
        return "Car{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", price=" + price +
                ", seatsNumber=" + seatsNumber +
                ", dateOfSale=" + dateOfSale +
                ", priceOfSale=" + priceOfSale +
                ", payerName='" + payerName + '\'' +
                ", version=" + version +
                '}';
    }
}

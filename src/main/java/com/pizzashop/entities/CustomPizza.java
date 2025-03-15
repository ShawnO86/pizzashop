package com.pizzashop.entities;

import jakarta.persistence.*;

@Entity
@Table(name = "custompizza")
public class CustomPizza {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // ToDo: create crust and size enums???

}

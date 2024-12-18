package com.example.Caltizm.DTO;

import lombok.Getter;

@Getter
public class BrandDTO implements Comparable<BrandDTO>{


    String name;
    String logo_url;
    String description;
    boolean is_deleted = false;


    public BrandDTO(String name, String logo_url, String description) {
        this.name = name;
        this.logo_url = logo_url;
        this.description = description;
    }

    @Override
    public int compareTo(BrandDTO other) {
        return this.name.compareTo(other.name);
    }

    @Override
    public String toString() {
        return "BrandDTO{" +
                "name='" + name + '\'' +
                ", logo_url='" + logo_url + '\'' +
                ", description='" + description + '\'' +
                ", is_deleted=" + is_deleted +
                '}';
    }
}

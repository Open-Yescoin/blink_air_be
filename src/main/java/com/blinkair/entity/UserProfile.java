package com.blinkair.entity;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Immutable;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;

@Data
@Entity
@Immutable
@Table(name = "user_profile")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;

    @Column(name = "name")
    private String name;

    @Column(name = "gender")
    private Short gender;

    @Column(name = "birthday")
    private LocalDate birthday;

    @Column(name = "bio")
    private String bio;

    @Column(name = "city")
    private String city;

    @Column(name = "country_code")
    private String countryCode;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "contact_info")
    private String contactInfo;

    public Integer getAge() {
        if (birthday == null) return null;
        return Period.between(birthday, LocalDate.now()).getYears();
    }

    public String getGenderStr() {
        if (gender == null) return null;
        return switch (gender.intValue()) {
            case 1 -> "MALE";
            case 2 -> "FEMALE";
            default -> "OTHER";
        };
    }
}

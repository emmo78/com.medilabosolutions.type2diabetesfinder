package com.medilabosolutions.type2diabetesfinder.patientservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import java.time.LocalDate;

/**
 * Entity class representing a Patient in the system.
 * This class is mapped to the "patient" table in the database.
 * <p>
 * Annotations:
 * - @Entity: Specifies that this class is an entity.
 * - @Table: Specifies the name of the database table.
 * - @DynamicInsert: Generates dynamic SQL for insert operations.
 * - @DynamicUpdate: Generates dynamic SQL for update operations.
 * - @Getter: Lombok annotation to generate getter methods.
 * - @Setter: Lombok annotation to generate setter methods.
 * - @Builder: Lombok annotation to implement the builder pattern for the class.
 * - @AllArgsConstructor: Lombok annotation to generate a constructor with all fields.
 * - @NoArgsConstructor: Lombok annotation to generate a no-args constructor.
 * - @ToString: Lombok annotation to generate a toString method including only explicitly included fields.
 */
@Entity
@Table(name = "patient")
@DynamicInsert
@DynamicUpdate
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
public class Patient {

    private static final long serialVersionUID = 1L;

    //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    @ToString.Include
    private Integer id;

    @Column(name = "first_name")
    @NotBlank(message = "Firstname is mandatory")
    @Size(max = 35, message = "Firstname must be maximum of 35 characters")
    @ToString.Include
    private String firstName;

    @Column(name = "last_name")
    @NotBlank(message = "Lastname is mandatory")
    @Size(max = 35, message = "Lastname must be maximum of 35 characters")
    @ToString.Include
    private String lastName;

    @Column(name = "birth_date")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Birthdate is mandatory")
    @Past(message = "Birthdate must be in the past")
    @ToString.Include
    private LocalDate birthDate;

    @Column(name = "genre")
    @NotBlank(message = "Lastname is mandatory")
    @Size(max = 1, message = "Genre must be maximum of 1 character")
    @ToString.Include
    private String genre;

    @Column(name = "postal_address")
    @Size(max = 90, message = "Address must be maximum of 90 characters")
    private String address;

    @Column(name = "phone_number")
    @Size(max = 16, message = "Phone number must be maximum of 16 characters")
    private String phoneNumber;

}

package com.medilabosolutions.type2diabetesfinder.frontservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Entity class representing a Patient in the system.
 * This class is mapped to the "patient" table in the database.*
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(onlyExplicitlyIncluded = true, includeFieldNames = true)
public class Patient {

    //Integer.MAX_VALUE = 2 147 483 647 = 2^31-1
    @ToString.Include
    private Integer id;

    @ToString.Include
    private String firstName;

    @ToString.Include
    private String lastName;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @ToString.Include
    private LocalDate birthDate;

    @ToString.Include
    private String genre;

    private String address;

    private String phoneNumber;

    private List<Note> medicalNotes;
}

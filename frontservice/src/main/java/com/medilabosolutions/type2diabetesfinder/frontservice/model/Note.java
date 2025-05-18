package com.medilabosolutions.type2diabetesfinder.frontservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.data.annotation.Id;

import java.time.LocalDateTime;

/**
 * Entity class representing a Note in the system.
 * This class is mapped to the "notes" collection in MongoDB.
 * <p>
 * Annotations:
 * - @Document: Specifies that this class is a MongoDB document.
 * - @Getter: Lombok annotation to generate getter methods.
 * - @Setter: Lombok annotation to generate setter methods.
 * - @Builder: Lombok annotation to implement the builder pattern for the class.
 * - @AllArgsConstructor: Lombok annotation to generate a constructor with all fields.
 * - @NoArgsConstructor: Lombok annotation to generate a no-args constructor.
 * - @ToString: Lombok annotation to generate a toString method including only explicitly included fields.
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = true)
public class Note {

    @Id
    private String id;

    private Integer patientId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;

    private String content;
}
package com.medilabosolutions.type2diabetesfinder.noteservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document(collection = "notes")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(includeFieldNames = true)
public class Note {

    @Id
    private String id;

    @Indexed(unique = true)
    @NotNull(message = "Patient ID is mandatory")
    private Integer patientId;

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    @NotNull(message = "Date is mandatory")
    private LocalDateTime dateTime;

    @NotBlank(message = "Content is mandatory")
    private String content;
}
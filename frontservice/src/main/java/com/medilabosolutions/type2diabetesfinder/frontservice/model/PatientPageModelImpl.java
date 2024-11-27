package com.medilabosolutions.type2diabetesfinder.frontservice.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.web.PagedModel;

import java.util.List;

public class PatientPageModelImpl<T> extends PagedModel<T> {

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public PatientPageModelImpl (@JsonProperty("content") List<T> content) {
            super(new PageImpl<>(content));
        }
}


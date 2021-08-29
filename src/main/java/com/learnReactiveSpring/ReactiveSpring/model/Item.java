package com.learnReactiveSpring.ReactiveSpring.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Item {

    @Id
    @JsonProperty
    private String itemId;

    @JsonProperty
    private String description;

    @JsonProperty
    private Double price;
}

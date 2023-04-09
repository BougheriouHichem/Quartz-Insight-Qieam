package com.apigames.quartzinsight.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@EqualsAndHashCode
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Games")
public class Games {
    @Id
    @Column(name = "game_id", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "title",unique = true)
    private String title;

    @Column(name = "url", unique = true)
    private String url;

    @Column(name = "availability")
    @JsonIgnoreProperties(value = {"availability"})
    private Boolean availability = true;
}

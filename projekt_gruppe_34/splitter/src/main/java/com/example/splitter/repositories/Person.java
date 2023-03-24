package com.example.splitter.repositories;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("PERSON")
record Person(@Column("PERSON_ID") @Id Integer personId,
              @Column("GIT_HUB_NAME") String gitHubName) {

}

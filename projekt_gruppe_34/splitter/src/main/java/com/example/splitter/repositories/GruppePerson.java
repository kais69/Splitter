package com.example.splitter.repositories;

import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("GRUPPE_PERSON")
record GruppePerson(
        @Column("PERSON") Integer personenIds) {

}
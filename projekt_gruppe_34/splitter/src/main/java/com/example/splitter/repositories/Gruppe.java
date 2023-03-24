package com.example.splitter.repositories;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("GRUPPE")
record Gruppe(@Column("GRUPPE_ID") @Id Integer gruppeId, @Column("NAME") String name,

              @MappedCollection(idColumn = "GRUPPE", keyColumn = "PERSON")
              Set<GruppePerson> personenIds,

              @Column("GRUPPE") Set<Transaktion> transaktionen, @Column("GESCHLOSSEN") boolean geschlossen) {
}

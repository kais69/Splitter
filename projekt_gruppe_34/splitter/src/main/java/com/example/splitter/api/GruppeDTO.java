package com.example.splitter.api;

import java.util.List;
import java.util.Set;

public record GruppeDTO(String gruppe, String name, Set<String> personen, boolean geschlossen,
                        List<TransaktionDTO> ausgaben) {
}

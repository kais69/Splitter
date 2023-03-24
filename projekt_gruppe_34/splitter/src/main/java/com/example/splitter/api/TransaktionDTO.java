package com.example.splitter.api;

import java.util.Set;

public record TransaktionDTO(String grund, String glaeubiger, double cent, Set<String> schuldner) {
}

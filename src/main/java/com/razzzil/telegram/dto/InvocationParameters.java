package com.razzzil.telegram.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
public class InvocationParameters {
    private List<String> variables;
    private String text;
}

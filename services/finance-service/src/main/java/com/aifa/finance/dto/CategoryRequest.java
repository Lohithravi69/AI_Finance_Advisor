package com.aifa.finance.dto;

import java.io.Serializable;

public record CategoryRequest(
    String name,
    String description,
    String icon,
    String color,
    Double monthlyBudget,
    Boolean isPredefined
) implements Serializable {}

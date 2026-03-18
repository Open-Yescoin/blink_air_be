package com.blinkair.dto;

import com.blinkair.entity.enums.LookingFor;
import lombok.Data;

@Data
public class MatchRequest {
    private LookingFor lookingFor = LookingFor.ANY;
}

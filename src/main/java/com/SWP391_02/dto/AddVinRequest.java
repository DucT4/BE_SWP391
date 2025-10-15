package com.SWP391_02.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import java.util.List;

@Data
public class AddVinRequest {
    @Schema(example = "[\"VF8ABC1234567890\", \"VF3XYZ9876543210\"]")
    @NotEmpty
    private List<String> vins;
}

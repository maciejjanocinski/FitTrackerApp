package app.dto;

import lombok.Data;

import java.util.Map;
@Data
public class UpdateProfileInfoDto {
    private Map<String, String> updates;
}

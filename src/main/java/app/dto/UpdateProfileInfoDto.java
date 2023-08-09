package app.dto;

import lombok.Data;

import java.util.Map;

public record UpdateProfileInfoDto(Map<String, String> updates) {
}

package app.user;

import lombok.Data;

import java.util.Map;

record UpdateProfileInfoDto(Map<String, String> updates) {
}

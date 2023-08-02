package app.dto;

import app.utils.passwordValidation.ValidPassword;

public record DeleteProfileDto(@ValidPassword String password) {
}

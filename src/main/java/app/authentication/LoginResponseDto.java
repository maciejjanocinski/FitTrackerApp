package app.authentication;

import app.user.User;

record LoginResponseDto(User userEntity, String jwt) {
}

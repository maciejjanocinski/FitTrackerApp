package app.user;

import app.util.passwordValidation.ValidPassword;

record DeleteUserDto(@ValidPassword String password) {
}

package com.joelforjava.ripplr

trait DomainDataFactory {

    User validUser() {
        new User(username: 'valid_user_name', passwordHash: 'passwordHashedL8r!')
    }

    User validUserWithUsername(String username) {
        new User(username: username, passwordHash: 'passw0rd1')
    }

    User validUserAndProfile() {
        new User(username: 'valid_user_with_profile', passwordHash: 'password!', profile: new Profile(fullName:"Mocked Name", email:"mock@mocking.com"))
    }
}
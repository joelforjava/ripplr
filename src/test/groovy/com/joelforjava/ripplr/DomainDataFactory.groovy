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

    User validUserWithUsernameAndProfile(String username) {
        new User(username: username, passwordHash: 'password!', profile: new Profile(fullName:"Mocked Name", email:"mock@mocking.com"))
    }

    Profile validProfile() {
        validProfileWithUser(new User(username: 'valid_user_name', passwordHash: 'passwordHashedL8r!'))
    }

    Profile validProfileWithUser(User user) {
        new Profile(fullName:"Mocked Name", email:"mock@mocking.com", user: user)
    }
}
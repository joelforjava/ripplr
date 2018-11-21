package com.joelforjava.ripplr.page

import geb.Page

class LoginPage extends Page {

    static url = '/login/auth'

    static at = { title == 'Login' }

    static content = {
        usernameField { $('#username', 0) }
        passwordField { $('#password', 0) }
        submitField { $('#submit', 0) }
    }

    void login(String username, String password) {
        usernameField << username
        passwordField << password
        submitField.click()
    }
}

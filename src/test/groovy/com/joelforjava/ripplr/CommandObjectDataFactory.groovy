package com.joelforjava.ripplr

import grails.compiler.GrailsCompileStatic

@GrailsCompileStatic
trait CommandObjectDataFactory {

    UserRegisterCommand validUserRegisterCommandObject() {
        def urc = new UserRegisterCommand()
        urc.username = "sterling"
        urc.password = "duchess"
        urc.passwordVerify = "duchess"
        urc.profile = validProfileRegisterCommand()
        urc
    }

    UserUpdateCommand validUserUpdateCommandObject() {
        def uuc = new UserUpdateCommand()
        uuc.username = "sterling"
        uuc.password = "duchess"
        uuc.passwordVerify = "duchess"
        uuc.profile = validProfileRegisterCommand()
        uuc
    }

    ProfileRegisterCommand validProfileRegisterCommand() {
        def prc = new ProfileRegisterCommand()
        prc.fullName = 'Sterling Archer'
        prc.email = 'sterling@isis.com'
        prc
    }

    UserUpdateCommand emptyUserUpdateCommandObject() {
        new UserUpdateCommand()
    }

    RippleSaveCommand emptyRippleSaveCommand() {
        new RippleSaveCommand()
    }

    RippleSaveCommand validRippleSaveCommand() {
        new RippleSaveCommand(content: 'Valid Content', fromPage: 'timeline')

    }
}
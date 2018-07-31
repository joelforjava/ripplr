package com.joelforjava.ripplr

import spock.lang.Specification
import spock.lang.Unroll

class UserRegistrationCommandSpec extends Specification {

    @Unroll
    def "Registering with command object for #username validates correctly"() {

        given: "A mocked register command object"
        def urc = new UserRegisterCommand()

        and: "a set of initial values"
        urc.username = username
        urc.password = password
        urc.passwordVerify = passwordVerify
        urc.profile = new ProfileCommand()
        urc.profile.fullName = fullName
        urc.profile.email = email
        // Other items are optional

        when: "the validator is invoked"
        def isValid = urc.validate()

        then: "the appropriate fields are flagged as errors"
        isValid == anticipatedValid
        urc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        username   | password   | passwordVerify    | fullName		| email					| anticipatedValid  | fieldInError      | errorCode
        "kirk_ham" | "password" | "IDontMatch"      | "Kirk Hammett"| "kirk@metallica.com"	| false             | "passwordVerify"  | "validator.invalid"
        "james_het"| "password" | "password"        | "Jim Hetfield"| "james@metallica.com" | true				| null              | null
        "dave_must"| "guitars"  | "guitars"         | "Dave"        | "dave@megadeth.com"   | true				| null              | null
        "dr"       | "password" | "password"        | "Doc"			| "doc@derp.com"		| false             | "username"        | "size.toosmall"
        "jeeves"   | "password" | "password"        | ""			| "jeeves@metallica.com"| false				| "profile"		    | "validator.invalid"
        "kirk_ham" | "password" | "password"		| "Kirk Hammett"| ""					| false				| "profile"			| "validator.invalid"
        "james_het"| "guest12"	| "guest12"			| "Jim"			| "NotAnEmailAddress"	| false				| "profile"	        | "validator.invalid"
    }

    @Unroll
    def "Updating with command object for #username validates correctly"() {

        given: "A mocked update command object"
        def uuc = new UserUpdateCommand()

        and: "a set of initial values"
        uuc.username = username
        uuc.password = password
        uuc.passwordVerify = passwordVerify
        uuc.profile = new ProfileCommand()
        uuc.profile.fullName = fullName
        uuc.profile.email = email
        uuc.usernameDirty = false
        uuc.passwordDirty = false
        // Other items are optional

        when: "the validator is invoked"
        def isValid = uuc.validate()

        then: "the appropriate fields are flagged as errors"
        println uuc.errors
        isValid == anticipatedValid
        uuc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        username   | password   | passwordVerify    | fullName      | email                 | anticipatedValid  | fieldInError      | errorCode
        "kirk_ham" | "password" | "IDontMatch"      | "Kirk Hammett"| "kirk@metallica.com"  | false             | "passwordVerify"  | "validator.invalid"
        "james_het"| "password" | "password"        | "Jim Hetfield"| "james@metallica.com" | true              | null              | null
        "dave_must"| "guitars"  | "guitars"         | "Dave"        | "dave@megadeth.com"   | true              | null              | null
        "dr"       | "password" | "password"        | "Doc"         | "doc@derp.com"        | false             | "username"        | "size.toosmall"
        "jeeves"   | "password" | "password"        | ""            | "jeeves@metallica.com"| false             | "profile"         | "validator.invalid"
        "kirk_ham" | "password" | "password"        | "Kirk Hammett"| ""                    | false             | "profile"         | "validator.invalid"
        "j4mes_h3t"| "guest12"  | "guest12"         | "Jim"         | "NotAnEmailAddress"   | false             | "profile"         | "validator.invalid"
    }

    @Unroll
    def "Common Profile command object for #fullName validates correctly"() {

        given: "A mocked register command object"
        def pc = new ProfileCommand()

        and: "a set of initial values"
        pc.fullName = fullName
        pc.email = email
        // Other items are optional

        when: "the validator is invoked"
        def isValid = pc.validate()

        then: "the appropriate fields are flagged as errors"
        isValid == anticipatedValid
        pc.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        fullName		| email					| anticipatedValid  | fieldInError      | errorCode
        "Jim Hetfield"  | "james@metallica.com" | true				| null              | null
        "Dave"          | "dave@megadeth.com"   | true				| null              | null
        ""			    | "jeeves@metallica.com"| false				| "fullName"		| "blank"
        "Kirk Hammett"  | ""					| false				| "email"			| "blank"
        "Jim"			| "NotAnEmailAddress"	| false				| "email"	        | "email.invalid"
    }

}

package com.joelforjava.ripplr

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.*

import spock.lang.*
import geb.spock.*

/**
 * See http://www.gebish.org/manual/current/ for more instructions
 */
@Integration
@Rollback
class UserRegistrationSpec extends GebSpec {

    def setup() {
    }

    def cleanup() {
    }

    @Ignore
    void "test something"() {
        when:"The home page is visited"
            go '/'

        then:"The title is correct"
        	title == "Ripplr &#9729; Welcome to Ripplr"
    }
}

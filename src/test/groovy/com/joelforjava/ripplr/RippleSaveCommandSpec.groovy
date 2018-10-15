package com.joelforjava.ripplr

import grails.testing.gorm.DataTest
import spock.lang.Specification
import spock.lang.Unroll

class RippleSaveCommandSpec extends Specification {
    @Unroll("RippleSaveCommand for '#msgContent' validates to #expValid")
    void 'RippleSaveCommand object validates as expected'() {
        given: 'A new command object'
        def cmd = new RippleSaveCommand()

        and: 'A set of initial values'
        cmd.with {
            content = msgContent
            fromPage = frmPage
        }

        when: 'We invoke validation'
        def isValid = cmd.validate()

        then: 'The appropriate fields are flagged with the expected errors'
        isValid == expValid
        cmd.errors.allErrors.size() == numErrors
        cmd.errors.getFieldError(fieldInError)?.code == errorCode

        where:
        msgContent      | frmPage    || expValid | numErrors | fieldInError | errorCode
        'Test content'  | 'timeline' || true     | 0         | null         | null
        ''              | 'global'   || false    | 1         | 'content'    | 'blank'
        null            | 'timeline' || false    | 1         | 'content'    | 'nullable'
        'Test content'  | null       || false    | 1         | 'fromPage'   | 'nullable'
        'Test content'  | 'show'     || false    | 1         | 'fromPage'   | 'not.inList'
    }
}

package com.joelforjava.ripplr

import org.grails.plugins.testing.GrailsMockMultipartFile
import spock.lang.Specification

class ImageRegisterCommandSpec extends Specification {

    void 'ImageRegisterCommand object validates as expected'() {
        given: 'A new ImageRegisterCommand object'
        def iuc = new ImageRegisterCommand()
        iuc.with {
            photo = photoMock
            description = desc
        }

        when: 'We invoke validation'
        def isValid = iuc.validate()

        then: 'The appropriate fields are flagged as errors'
        isValid == expValid
        iuc.errors.allErrors.size() == numErrors
        iuc.errors.getFieldError(fieldInError)?.code == errorCode

        where:

        photoMock                                                 | desc          || expValid | numErrors | fieldInError | errorCode
        populateMultipartFile('filename.jpg', [1,0,1] as byte[])  | 'Main Photo'  || true     | 0         | null         | null
        populateMultipartFile('filename.jpeg', [1,0,1] as byte[]) | 'Main Photo'  || true     | 0         | null         | null
        populateMultipartFile('filename.png', [1,0,1] as byte[])  | 'Main Photo'  || true     | 0         | null         | null
        populateMultipartFile('filename.jpg', [1,0,1] as byte[])  | null          || true     | 0         | null         | null
        null                                                      | 'No Image'    || true     | 0         | null         | null
        populateMultipartFile('filename.gif', [1,0,1] as byte[])  | 'Funny GIF'   || false    | 1         | 'photo'      | 'validator.invalid'
        populateMultipartFile('filename.txt', [1,0,1] as byte[])  | 'Text File'   || false    | 1         | 'photo'      | 'validator.invalid'
        populateMultipartFile('filename.tiff', [1,0,1] as byte[]) | 'TIFF file'   || false    | 1         | 'photo'      | 'validator.invalid'
        populateMultipartFile('filename.jpg', [] as byte[])       | 'Empty Bytes' || false    | 1         | 'photo'      | 'validator.invalid'

    }

    def populateMultipartFile(String filename, byte[] content) {
        new GrailsMockMultipartFile(filename, filename, 'content-type', content)
    }

}

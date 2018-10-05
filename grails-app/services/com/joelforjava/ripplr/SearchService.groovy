package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional
import grails.web.servlet.mvc.GrailsParameterMap
import groovy.json.JsonOutput

@Transactional
class SearchService {

    def searchRipples(String query, params) {
        def searchResult = Ripple.search(query, params)

        if (!searchResult) {
            log.debug "no results found"
        } else {
            log.debug "Results found: ${searchResult.total}"
//            log.debug "${JsonOutput.toJson(searchResult)}"
        }

        searchResult
    }
}

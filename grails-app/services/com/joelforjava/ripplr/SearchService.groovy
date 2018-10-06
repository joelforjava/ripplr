package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional

@Transactional
class SearchService {

    def searchRipples(String query, params) {
        def searchResult = Ripple.search(query, params)

        log.debug "${searchResult.total} Results found"

        searchResult
    }
}

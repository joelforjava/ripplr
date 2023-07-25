package com.joelforjava.ripplr

import grails.converters.JSON
import org.springframework.http.HttpStatus

class SearchController {

    SearchService searchService

    def index() { }
	
    def results() {
        def query = params.q
        log.debug "Query received: $query"
        if (!query) {
            return [:]
        }

        def highlighter = {
            field 'content'
            preTags '<strong>'
            postTags '</strong>'
        }

        try {
            def searchResult = searchService.searchRipples(query, [highlight:  highlighter])
            if (!searchResult) {
                log.debug "no results found"
                return
            }

            /* For some reason, the highlighted List<Map> is a list of empty maps */
            def highlighted = searchResult.highlight
            log.debug "Here are the highlighted: ${highlighted}"
            searchResult?.searchResults?.eachWithIndex { hit, index ->
                def fragments = highlighted[index].content?.fragments
                log.debug fragments?.size() ? "${fragments[0]}" : ''
            }
            /* */

            request.withFormat {
                form multipartForm {
                    respond(searchResult, [model: [searchResult:searchResult, highlights: searchResult?.highlight, q: query]])
                }
                '*' { render searchResult as JSON }
            }
        } catch (e) {
            log.error "An error! ${e}"
            internalError()
        }
    }

    protected void internalError() {
        request.withFormat {
            form multipartForm {
                flash.message = message(code: 'default.search.error', args: [message(code: 'query.label', default: 'Query'), params.q])
                redirect(uri: '/search', method: 'GET')
            }
            '*' { render status: HttpStatus.INTERNAL_SERVER_ERROR }
        }
    }
}

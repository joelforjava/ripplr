package com.joelforjava.ripplr

class SearchController {

    //def index() { }
	
	def search() {
		def query = params.q
		log.debug "Query received: $query"
		if (!query) {
			return [:]
		}
		try {
			params.highlight = {
				field 'content'
				field 'user.username'
				preTags '<strong>'
				postTags '</strong>'
			}
			def result = Ripple.search(query, params)
			if (!result) {
				log.debug "no results found"
			} else {
				log.debug "Results found: $result"
				/* */
				def highlighted = result.highlight
				log.debug "Here are the highlighted: ${highlighted}"
				result?.searchResults?.eachWithIndex { hit, index ->
					def fragments = highlighted[index].content?.fragments
					println fragments?.size() ? fragments[0] : ''
				}
				/* */
			}
			return [searchResult : result, highlights: result?.highlight]
		} catch (e) {
			return [searchError : true]
		}
	}
}

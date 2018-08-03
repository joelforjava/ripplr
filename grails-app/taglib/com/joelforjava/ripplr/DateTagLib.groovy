package com.joelforjava.ripplr

/**
 * Custom Date Taglib used to display dates in various forms.
 */
class DateTagLib {
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static namespace = 'rip'

    def timeAgo = { attrs ->
    	def date = attrs.date
    	def elapsedDate = getTimeAgo(date)
    	out << elapsedDate
    }

    protected String getTimeAgo(Date date) {
        def now = new Date()
        def diff = Math.abs now.time - date.time

        final long SECOND = 1000
        final long MINUTE = SECOND * 60
        final long HOUR = MINUTE * 60
        final long DAY = HOUR * 24

    	def timeAgo = ''
    	long calc = 0
    	calc = Math.floor diff/DAY

    	if (calc) {
    		timeAgo += calc + ' day' + (calc > 1 ? 's ' : ' ')
    		diff %= DAY
    	}
    	calc = Math.floor diff/HOUR

    	if (calc) {
    		timeAgo += calc + ' hour' + (calc > 1 ? 's ' : ' ')
    		diff %= HOUR
    	}
    	calc = Math.floor diff/MINUTE

    	if (calc) {
    		timeAgo += calc + ' minute' + (calc > 1 ? 's ' : ' ')
    		diff %= MINUTE
    	}
    	if (!timeAgo) {
    		timeAgo = 'Right Now'
    	} else {
    		timeAgo += (date.time > now.time) ? 'from now' : 'ago'
    	}

    	return timeAgo
    }
}

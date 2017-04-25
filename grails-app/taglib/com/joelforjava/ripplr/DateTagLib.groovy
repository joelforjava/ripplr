package com.joelforjava.ripplr

class DateTagLib {
    //static defaultEncodeAs = [taglib:'html']
    //static encodeAsForTags = [tagName: [taglib:'html'], otherTagName: [taglib:'none']]

    static namespace = "rip"
    
    def timeAgo = { attrs ->
    	def date = attrs.date
    	def elapsedDate = getTimeAgo(date)
    	out << elapsedDate
    }

    protected String getTimeAgo(Date date) {
    	def now = new Date()
    	def diff = Math.abs now.time - date.time

    	final long second = 1000
    	final long minute = second * 60
    	final long hour = minute * 60
    	final long day = hour * 24

    	def timeAgo = ""
    	long calc = 0
    	calc = Math.floor diff/day

    	if (calc) {
    		timeAgo += calc + " day" + (calc > 1 ? "s " : " ")
    		diff %= day
    	}
    	calc = Math.floor diff/hour

    	if (calc) {
    		timeAgo += calc + " hour" + (calc > 1 ? "s " : " ")
    		diff %= hour
    	}
    	calc = Math.floor diff/minute

    	if (calc) {
    		timeAgo += calc + " minute" + (calc > 1 ? "s " : " ")
    		diff %= minute
    	}
    	if (!timeAgo) {
    		timeAgo = "Right Now"
    	} else {
    		timeAgo += (date.time > now.time) ? "from now" : "ago"
    	}

    	return timeAgo
    }
}

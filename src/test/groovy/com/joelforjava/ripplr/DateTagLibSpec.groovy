package com.joelforjava.ripplr

import grails.test.mixin.TestFor
import groovy.time.TimeCategory
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.mop.Use

/**
 * See the API for {@link grails.test.mixin.web.GroovyPageUnitTestMixin} for usage instructions
 */
@Use(TimeCategory)
@TestFor(DateTagLib)
class DateTagLibSpec extends Specification {


	@Unroll
    def "Conversion of #testName matches #expectedTimeAgo"() {

    	expect:
    	applyTemplate('<rip:timeAgo date="${date}" />', [date: testDate]) == expectedTimeAgo

    	where:
    	testName		       | testDate						 | expectedTimeAgo
    	"Current Time"         | new Date()						 | "Right Now"
    	"Now - 1 day"          | new Date().minus(1) 			 | "1 day ago"
    	"Now - 2 days"         | new Date().minus(2) 		  	 | "2 days ago"
    	"1 minute ago"         | 1.minute.ago          			 | "1 minute ago"
    	"2 minutes ago"        | 2.minutes.ago         			 | "2 minutes ago"
    	"1 hour ago"    	   | 1.hour.ago            			 | "1 hour ago"
    	"2 hours ago"          | 2.hours.ago           			 | "2 hours ago"
    	"10 hours ago"  	   | 10.hours.ago          			 | "10 hours ago"
    	"1 second ago"  	   | 1.second.ago          			 | "Right Now"
    	"2 seconds ago"        | 2.seconds.ago         			 | "Right Now"
    	"59 seconds ago"	   | 59.seconds.ago 				 | "1 minute ago"
    	"1 hour 4 minutes ago" | new Date() - 1.hour - 4.minutes | "1 hour 4 minutes ago"
    	"2 days 3 hours ago"   | new Date() - 2.days - 3.hours	 | "2 days 3 hours ago" // Saying '2.days.ago' starts your time at 00:00
    																					// which will render your test dependent on current time
    }
}

package ripplr.grails3

import com.joelforjava.ripplr.*

class BootStrap {

    def init = { servletContext ->
    	environments {
    		development {
    			if (!User.count()) createSampleData()
    		}
    	}
    }
    def destroy = {
    }

    def createSampleData() {
    	println "Creating Ripplr Sample Data"
    	def jezza = new User(username: 'jezza', passwordHash: 'topgear', profile: new Profile(fullName: "Jeremy Clarkson", email: "jezza@amazon.co.uk"))
    	jezza.addToRipples(new Ripple(content:"Bacon ipsum dolor amet duis ribeye drumstick mollit turkey meatball excepteur t-bone ut short loin short ribs turducken"))
    	jezza.addToRipples(new Ripple(content:"Hamburger sunt exercitation pancetta sausage consequat shankle tongue jerky veniam bresaola ribeye"))
    	jezza.addToRipples(new Ripple(content:"Tenderloin leberkas chuck, swine tail ham hamburger brisket excepteur ea ex non fugiat"))
    	jezza.addToRipples(new Ripple(content:"Turducken excepteur hamburger nostrud sed"))
    	jezza.addToRipples(new Ripple(content:"Chuck in ut meatball ground round cow fatback"))
    	jezza.save()

    	println "User jezza created with ${jezza.ripples.size()} Ripples"

    	def jamesmay = createUser('jamesmay','topgear')
    	def profile = new Profile(fullName: 'James May', email: 'jamesmay@amazon.co.uk')
    	jamesmay.profile = profile
    	jamesmay.addToRipples(new Ripple(content:"Et eu cupidatat pork chop"))
    	jamesmay.addToRipples(new Ripple(content:"Veniam ut cow"))
    	jamesmay.addToRipples(new Ripple(content:"prosciutto voluptate bresaola magna exercitation meatball dolore sunt"))
    	jamesmay.addToRipples(new Ripple(content:"drumstick proident elit minim tri-tip hamburger laborum do sausage est non"))
    	jamesmay.addToRipples(new Ripple(content:"Pastrami porchetta bresaola cupim brisket fatback shankle ea aliquip andouille"))
    	jamesmay.save()

    	println "User jamesmay created with ${jamesmay.ripples.size()} Ripples"

    	def hammond = createUser('hammond','topgear')
    	profile = new Profile(fullName: 'Richard Hammond', email: 'hammond@amazon.co.uk')
    	hammond.profile = profile
    	hammond.addToRipples(new Ripple(content:"Et sed excepteur brisket venison biltong adipisicing"))
    	hammond.addToRipples(new Ripple(content:"Aliqua jerky leberkas boudin dolor meatloaf turkey tenderloin ut tri-tip irure dolore jowl mollit"))
    	hammond.addToRipples(new Ripple(content:"Turkey shankle laboris minim"))
    	hammond.addToRipples(new Ripple(content:"Pork belly tail porchetta cillum enim ribeye cupidatat voluptate"))
    	hammond.addToRipples(new Ripple(content:"Minim anim ball tip ribeye in voluptate exercitation meatloaf enim"))
    	hammond.save()

    	println "User hammond created with ${hammond.ripples.size()} Ripples"

    	println "Number of users created: ${User.count()}"

    }

	User createUser(String username, String passwordHash, boolean accountLocked = false, boolean accountExpired = false,
					boolean passwordExpired = false) {

		def user = new User(username: username, passwordHash: passwordHash,
				accountLocked: accountLocked, accountExpired: accountExpired,
				passwordExpired: passwordExpired)
		user.save()
		user
	}

}

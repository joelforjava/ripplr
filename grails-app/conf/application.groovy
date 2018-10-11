

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.securityConfigType = 'InterceptUrlMap'
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.joelforjava.ripplr.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.joelforjava.ripplr.UserRole'
grails.plugin.springsecurity.userLookup.passwordPropertyName = 'passwordHash'
grails.plugin.springsecurity.authority.className = 'com.joelforjava.ripplr.Role'
grails.plugin.springsecurity.interceptUrlMap = [
	[pattern: '/',               access: ['permitAll']],
	[pattern: '/global',  		 access: ['permitAll']],
	[pattern: '/ripple/global',  access: ['permitAll']],
	[pattern: '/user/regist*',   access: ['permitAll']],
	[pattern: '/user/save',      access: ['permitAll']],
	[pattern: '/api/ripples',  	 access: ['permitAll']], // yep. dangerous stuff here.
	[pattern: '/error',          access: ['permitAll']],
	[pattern: '/index',          access: ['permitAll']],
	[pattern: '/index.gsp',      access: ['permitAll']],
	[pattern: '/shutdown',       access: ['permitAll']],
	[pattern: '/assets/**',      access: ['permitAll']],
	[pattern: '/**/js/**',       access: ['permitAll']],
	[pattern: '/**/css/**',      access: ['permitAll']],
	[pattern: '/**/images/**',   access: ['permitAll']],
	[pattern: '/**/favicon.ico', access: ['permitAll']],
    [pattern: '/dbconsole/**',          access: ['permitAll']],
    [pattern: '/login',          access: ['permitAll']],
    [pattern: '/login/**',       access: ['permitAll']],
    [pattern: '/logout',         access: ['permitAll']],
    [pattern: '/logout/**',      access: ['permitAll']],
    [pattern: '/**',             access: ['isAuthenticated()']]
]

grails.plugin.springsecurity.filterChain.chainMap = [
	[pattern: '/assets/**',      filters: 'none'],
	[pattern: '/**/js/**',       filters: 'none'],
	[pattern: '/**/css/**',      filters: 'none'],
	[pattern: '/**/images/**',   filters: 'none'],
	[pattern: '/**/favicon.ico', filters: 'none'],
	[pattern: '/**',             filters: 'JOINED_FILTERS']
]

//elasticSearch.client.mode='local'


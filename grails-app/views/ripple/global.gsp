<html>
	<head>
		<title><g:message code="global.timeline.page.title.label" default="Global Ripplr Timeline"/></title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">			
		</style>
		<g:javascript>
		</g:javascript>
	</head>
	<body>
		<div class="border-bottom mb-4">
			<h1><g:message code="global.timeline.headline.label" default="Global Timeline"/></h1>
		</div>
		<g:if test="${flash.message}">
			<div class="flash alert alert-success alert-dismissible">
				<button type="button" class="close" data-dismiss="alert" aria-label="${message(code: 'close.label', default: 'Close')}"><span ara-hidden="true">&times;</span></button>
				${flash.message}
			</div>
		</g:if>
		<div class="row">
			<sec:ifLoggedIn>
				<div class="col-sm-12 col-md-4" id="newPost">
					<g:render template="addTopicForm" model="[fromPage: 'global']"/>
				</div>
			</sec:ifLoggedIn>
			<sec:ifNotLoggedIn>
				<div class="col-sm-12 col-md-4" id="didYouKnow">
					<h1><g:message code="global.timeline.ripplr.subheadline.label" default="What is Ripplr?"/></h1>
					<p><g:message code="global.timeline.ripplr.brief.description.label"
								  default="Ripplr is a site to create ripples of discussion among friends, colleagues, and other interesting people."/></p>
					<p>
                        <a class="btn btn-primary btn-lg"
                           href="${createLink(controller: 'user', action: 'register')}" role="button">
                            <g:message code="join.ripplr.button.label" default="Join Ripplr"/>
                        </a>
                    </p>
				</div>
			</sec:ifNotLoggedIn>
			<div class="col-sm-8 col-md-5" id="topicArea">
				<div class="spinner" style="display: none">
				  <div class="rect1"></div>
				  <div class="rect2"></div>
				  <div class="rect3"></div>
				  <div class="rect4"></div>
				  <div class="rect5"></div>
				</div>
				<div id="allTopics">
					<g:render template="topicEntry" collection="${ripples}" var="ripple" />
					<nav aria-label="${message(code: 'ripple.list.aria.navigation.label', default: 'Ripple Navigation')}">
						<span class="pagination">
							<g:paginate class="btn" action="global" total="${rippleCount}" max="5" />
						</span>
					</nav>
				</div>
			</div>
			<div class="col-sm-4 col-md-3" id="featuredUsers">
				<div class="card">
					<div class="card-header bg-info">
						<h4 class="card-title text-white bg-info">
                            <g:message code="global.timeline.latest.users.label" default="Latest Users"/>
                        </h4>
					</div>
					<div class="card-body border border-info">
						<g:each var="latestUser" in="${latestUsers}">
							<div class="media mb-4">
								<div class="mr-3">
									<a href="${createLink(controller: 'user', action: 'profile', id: latestUser.username)}">
										<asset:image src="person.jpeg" width="64" height="64" />
									</a>
								</div>
								<div class="media-body">
									<h5 class="media-heading">${latestUser.username}</h5>
								</div>
							</div>
						</g:each>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
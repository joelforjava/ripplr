<html>
	<head>
		<title>Global Ripplr Timeline</title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">			
		</style>
		<g:javascript>
		</g:javascript>
	</head>
	<body>
		<div class="page-header">
			<h1>Global Timeline</h1>
		</div>
		<g:if test="${flash.message}">
			<div class="flash alert alert-success alert-dismissible">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span ara-hidden="true">&times;</span></button>
				${flash.message}
			</div>
		</g:if>
		<div class="row">
			<sec:ifLoggedIn>
				<div class="col-sm-12 col-md-4" id="newPost">
					<g:render template="addTopicForm" />
				</div>
			</sec:ifLoggedIn>
			<sec:ifNotLoggedIn>
				<div class="col-sm-12 col-md-4" id="didYouKnow">
					<h1>What is Ripplr?</h1>
					<p>Ripplr is a site to create ripples of discussion among friends, colleagues, and other interesting people.</p>
					<p><a class="btn btn-primary btn-lg" href="${createLink(controller: 'user', action: 'registration')}" role="button">Join Ripplr</a></p>
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
					<nav aria-label="Ripple Navigation">
						<span class="pagination">
							<g:paginate class="btn" action="global" total="${rippleCount}" max="5" />
						</span>
					</nav>
				</div>
			</div>
			<div class="col-sm-4 col-md-3" id="featuredUsers">
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Latest Users</h3>
					</div>
					<div class="panel-body">
						<g:each var="latestUser" in="${latestUsers}">
							<div class="media">
								<div class="media-left">
									<a href="${createLink(controller: 'user', action: 'profile', id: latestUser.username)}">
										<asset:image src="person.jpeg" width="64" height="64" />
									</a>
								</div>
								<div class="media-body">
									<h4 class="media-heading">${latestUser.username}</h4>
								</div>
							</div>
						</g:each>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
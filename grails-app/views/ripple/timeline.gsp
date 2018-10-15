<html>
	<head>
		<g:set var="displayName" value="${ user.profile ? user.profile.fullName : user.username }"/>
		<title><g:message code="timeline.page.title.label" args="${[displayName]}"/></title>
		<meta name="layout" content="main" />
		<style type="text/css" media="screen">			
		</style>
		<g:javascript>
		</g:javascript>
	</head>
	<body>
		<div class="border-bottom mb-4">
			<h1><g:message code="timeline.page.headline.label" args="${[displayName]}"/></h1>
		</div>
		<g:if test="${flash.message}">
			<div class="flash alert alert-success alert-dismissible">
				<button type="button" class="close" data-dismiss="alert" aria-label="${message(code: 'close.label', default: 'Close')}"><span ara-hidden="true">&times;</span></button>
				${flash.message}
			</div>
		</g:if>
		<div class="row">
			<div class="col-md-8 offset-md-2" id="newPost">
				<g:render template="addTopicForm" model="[fromPage: 'timeline']" />
			</div>
		</div>
		<div class="row">
			<div class="col-md-8 offset-md-2" id="topicArea">
				<div class="spinner" style="display: none">
				  <div class="rect1"></div>
				  <div class="rect2"></div>
				  <div class="rect3"></div>
				  <div class="rect4"></div>
				  <div class="rect5"></div>
				</div>
				<div id="allTopics">
					<g:render template="topicEntry" collection="${user.ripples}" var="ripple" />
				</div>
			</div>
		</div>
	</body>
</html>
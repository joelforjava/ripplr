<%@page expressionCodec="none" %> <!-- I don't like this... doesn't seem to work otherwise-->
<html>
	<head>
		<title>Find a Topic</title>
		<meta name="layout" content="main" />
	</head>
	<body>
		<div class="border-bottom mb-4">
			<h1>Search</h1>
		</div>
		<g:if test="${flash.message}">
			<div class="flash alert alert-success alert-dismissible">
				<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span ara-hidden="true">&times;</span></button>
				${flash.message}
			</div>
		</g:if>
		<div class="row">
			<div class="col-md-12 mb-4">
				<g:form class="form-inline">
                    <g:textField class="form-control mb-2 mr-sm-2" id="searchBox" name="q" />
					<g:submitButton class="btn btn-primary mb-2" name="search" value="Search" />
				</g:form>
			</div>
			<g:if test="${searchResult?.searchResults}">
				<div class="col-md-12">
					<h3>${searchResult.total} results found</h3>
					<g:each status="i" var="result" in="${searchResult.searchResults}">
						<div class="searchTopic card mb-4">
							<div class="searchContent card-body">${result.content}</div>
							<g:if test="${highlights && highlights[i]}">
								<div class="searchContent card-body">${highlights[i]?.content?.fragments[0]}</div>
							</g:if>
							<div class="searchFrom card-footer">From: <g:link controller="users" action="${result.user?.username}">${result.user?.username}</g:link></div>
						</div>
					</g:each>
				</div>
			</g:if>
			<g:if test="${searchResult?.total == 0}">
				<div class="col-md-8">
					<div class="well">
						No results returned.
					</div>
				</div>
			</g:if>
		</div>
	</body>
</html>
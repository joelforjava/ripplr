<%@page expressionCodec="none" %> <!-- I don't like this... doesn't seem to work otherwise-->
<html>
    <head>
        <title>Search Results</title>
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
        <div class="row justify-content-center">
            <g:render template="searchForm" />
        </div>
        <div class="row mt-4">
            <g:if test="${searchResult?.searchResults}">
                <div class="col-md-12">
                    <h3>${searchResult.total} results found for: ${q}</h3>
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
                <div class="col-md-12">
                    <h3>
                        No results returned.
                    </h3>
                </div>
            </g:if>
        </div>
    </body>
</html>
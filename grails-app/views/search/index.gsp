<%@page expressionCodec="none" %> <!-- I don't like this... doesn't seem to work otherwise-->
<html>
    <head>
        <title>Search Ripples</title>
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
        <div class="container">
            <div class="row justify-content-center">
                <g:render template="searchForm" />
            </div>
        </div>
    </body>
</html>
<html>
    <head>
        <title><g:message code="message.label" default="Message"/></title>
        <meta name="layout" content="main">
        <asset:javascript src="messages.js"/>
    </head>
    <body>
        <div class="border-bottom mb-4">
            <h1>${message.subject}</h1>
        </div>
        <div class="container">
            <div class="row">
                <div class="col">
                    <div class="card">
                        <div class="card-body">
                            <div class="media">
                                <asset:image src="person.jpeg" width="64" height="64" class="mr-3"
                                             alt="${message(code:'mainPhoto.description.label', args:[message.sender.username])}"/>
                                <div class="media-body">
                                    <h6 class="mt-0">${message.sender.username}</h6>
                                    TODO - User details might go here?
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col">
                    <div class="card border-primary">
                        <div class="card-body">
                            ${message.content}
                        </div>
                        <div class="card-footer">
                            <div class="float-right">
                                <a href="${g.createLink(action: 'index')}" class="btn btn-secondary">
                                    <g:message code="message.return.to.index.label" default="Go Back to Messages"/>
                                </a>
                                <a href="#" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageBox">
                                    <g:message code="message.reply.label" default="Reply"/>
                                </a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <sec:ifLoggedIn>
            <g:render template="sendMessageModal" model='[subject: "RE: ${message.subject}", recipient: "${message.sender.username}"]'/>
        </sec:ifLoggedIn>
    </body>
</html>
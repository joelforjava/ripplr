<!DOCTYPE html>
<html>
    <head>
        <title><g:message code="messages.your.messages.label" default="Your Messages"/></title>
        <meta name="layout" content="main"/>
    </head>
    <body>
        <h2><g:message code="messages.your.messages.label" default="Your Messages"/></h2>
        <div class="container">
            <div class="row">
                <div class="col-sm-12">
                    <div class="card">
                        <div class="card-body">
                            <table class="table table-striped table-hover table-sm">
                                <thead class="bg-primary">
                                    <tr>
                                        <th scope="col"><g:message code="message.sender.label" default="Sender"/></th>
                                        <th scope="col"><g:message code="message.subject.label" default="Subject"/></th>
                                        <th scope="col"><g:message code="message.dateReceived.label" default="Date"/></th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <g:each in="${messageList}" var="message">
                                        <tr>
                                            <td>
                                                <div class="media">
                                                    <a href="${g.createLink(controller: 'user', action: 'profile', id: message.sender.username)}">
                                                        <asset:image class="mr-3" src="person.jpeg" width="64" height="64"/>
                                                    </a>
                                                    <div class="media-body">
                                                        <a href="${g.createLink(controller: 'user', action: 'profile', id: message.sender.username)}">
                                                            <h5 class="mt-3">${message.sender.username}</h5>
                                                        </a>
                                                    </div>
                                                </div>
                                            </td>
                                            %{--<td>${message.sender.username}</td>--}%
                                            <td><div class="mt-3"><a href="${g.createLink(action: 'show', id: message.id)}">${message.subject}</a></div></td>
                                            <td><div class="mt-3"><g:formatDate date="${message.dateSent}" type="datetime" style="MEDIUM"/></div></td>
                                        </tr>
                                    </g:each>
                                </tbody>
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </body>
</html>
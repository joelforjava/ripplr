<div>
	<%-- Not the biggest fan of the surrounding div, but this isn't exactly world-class design at this point --%>
    <div class="topicEntry card mb-4">
        <div class="topicText card-body">
            <div class="media">
                <div class="mr-3">
                    <a href="${g.createLink(controller: 'user', action: 'profile', id: ripple.user.username)}">
                        <asset:image src="person.jpeg" width="64" height="64"/>
                    </a>
                </div>

                <div class="media-body">
                    <h4 class="media-heading">${ripple.user.username}</h4>
                    ${ripple.content}
                </div>
            </div>
            <sec:ifLoggedIn>
                <g:if test="${ripple.responses}">
                    <button type="button" class="float-left btn btn-link btn-sm show-responses-button"
                            data-responses-to-button="${ripple.id}">See Replies</button>
                </g:if>
                <button type="button" class="float-right btn btn-link btn-sm reply-button"
                        data-in-reply-to-button="${ripple.id}">Reply</button>
            </sec:ifLoggedIn>
        </div>

        <div class="topicDate card-footer">
            <rip:timeAgo date="${ripple.dateCreated}"/>
        </div>
    </div>
    <sec:ifLoggedIn>
        <g:if test="${ripple.responses}">
            <div class="rippleResponses" data-responses-to-id="${ripple.id}" style="display:none">
                <ul class="list-unstyled">
                    <g:each in="${ripple.responses}" var="response">
                        <li class="media">
                            <a href="${g.createLink(controller: 'user', action: 'profile', id: response.ripple.user.username)}">
                                <asset:image src="person.jpeg" width="64" height="64"/>
                            </a>

                            <div class="media-body">
                                <h4 class="media-heading">${response.ripple.user.username}</h4>
                                ${response.ripple.content}
                            </div>
                        </li>
                    </g:each>
                </ul>
            </div>
        </g:if>
        <div class="topicReply border border-primary mb-4" data-in-reply-to-id="${ripple.id}" style="display: none">
            <g:form action="save" name="replyToRipple${ripple.id}Form">
                <g:textArea class="form-control topicContent" name="content" rows="3" cols="50"
                            value="@${ripple.user.username}"/>
                <input type="hidden" name="inResponseTo" value="${ripple.id}"/>
                <button class="send-reply-button btn btn-primary btn-lg btn-block">
                    Send Reply
                </button>
            </g:form>
        </div>
    </sec:ifLoggedIn>
</div>

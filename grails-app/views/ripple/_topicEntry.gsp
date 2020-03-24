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
	</div>
	<sec:ifLoggedIn>
		<div class="topicDate card-footer"><rip:timeAgo date="${ripple.dateCreated}" /><button type="button" class="float-right btn btn-link btn-sm reply-button" data-in-reply-to-button="${ripple.id}">Reply</button></div>
	</sec:ifLoggedIn>
</div>
<sec:ifLoggedIn>
	<div class="topicReply border border-primary mb-4" data-in-reply-to-id="${ripple.id}" style="display: none">
%{--		<div class="topicText input-group">--}%
%{--			<textarea class="form-control" aria-label="Send Reply">@${ripple.user.username}</textarea>--}%
%{--			<div class="input-group-append">--}%
%{--				<span class="input-group-text">Send</span>--}%
%{--			</div>--}%
%{--		</div>--}%
		<g:form action="save" name="replyToRipple${ripple.id}Form">
			<g:textArea class="form-control topicContent" name="content" rows="3" cols="50" value="@${ripple.user.username}"/>
			<input type="hidden" name="inResponseTo"  value="${ripple.id}"/>
			<button class="send-reply-button btn btn-primary btn-lg btn-block">
				Send Reply
			</button>
		</g:form>
	</div>
</sec:ifLoggedIn>

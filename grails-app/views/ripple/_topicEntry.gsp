<div class="topicEntry panel panel-default">
	<div class="topicText panel-body">
		<div class="media">
			<div class="media-left">
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
	<div class="topicDate panel-footer"><rip:timeAgo date="${ripple.dateCreated}" /></div>
</div>
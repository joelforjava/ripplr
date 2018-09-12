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
	<div class="topicDate card-footer"><rip:timeAgo date="${ripple.dateCreated}" /></div>
</div>
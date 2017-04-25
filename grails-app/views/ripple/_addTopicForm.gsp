<div id="newTopicForm">
	<g:form action="addAjax" id="${user.username}" name="addTopicForm">
		<g:textArea class="form-control" id="topicContent" name="content" rows="3" cols="50" placeholder="What ripple would you like to create, ${user.profile.fullName}?"/>
		<button id="addTopicBtn" class="btn btn-primary btn-lg btn-block">Create Ripple</button>
	</g:form>
</div>

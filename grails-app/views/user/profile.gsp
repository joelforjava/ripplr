<html>
	<head>
        <meta name="layout" content="main" />
        <g:set var="fullName" value="${profile.fullName}"/>
        <g:set var="username" value="${profile.user.username}"/>
        <g:set var="entityName" value="${message(code: 'profile.label', default: 'Profile')}"/>
        <g:set var="forLabel" value="${message(code: 'for.label', default: 'for')}"/>
		<title><g:message code="profile.title.label" args="${[fullName]}"/></title>
		<style>
			.mainPhoto {
				border: 1px dotted gray;
				background: lightyellow;
				padding: 1em;
				font-size: 1em;
			}
		</style>
		<g:javascript>
			$(document).ready(function() {
				$("#message, #error").hide();
				$("#followingButton.nofollow").click(function() {
					$("#followingButton").html('<div class="loader">Loading...</div>')
					jQuery.ajax({
						type:'POST', 
						data: {'usernameToFollow': '${username}'},
						url:'/user/follow',
						success: function(data, status) { 
							displaySuccess(data);
							$("#followingButton").removeClass("btn-info nofollow")
												 .addClass("btn-default following disabled")
												 .text("Following")
												 .blur(); 
						},
						error: function(XMLHttpRequest,textStatus,errorThrown) {
							 displayError(XMLHttpRequest.responseText); 
						}
					});
					return false;
				});
				$("#blockButton").click(function() {
					jQuery.ajax({
						type:'POST',
						data:{'usernameToBlock': '${username}'},
						url:'/user/block',
						success: function(data,textStatus) {
							displaySuccess(data);

							$("#optionsMenu").blur();
						},
						error: function(XMLHttpRequest,textStatus,errorThrown) {
							displayError(XMLHttpRequest.responseText);
						}
					});
					return false;
				});
				$("#unfollowButton").click(function() {
					$("#followingButton").html('<div class="loader">Loading...</div>')
					$(".dropdown-menu").blur();
					jQuery.ajax({
						type:'POST',
						data:{'usernameToUnfollow': '${username}'},
						url:'/user/unfollow',
						success: function(data,textStatus) {
							displaySuccess(data);
							$("#unfollowButton, #optionsMenu").blur();
							$("#followingButton").addClass("btn-info nofollow")
												 .removeClass("btn-default following disabled")
												 .html('<span class="glyphicon glyphicon-plus"></span> Follow Me!')
												 .blur();
							$("#unfollowButton").hide(); 
						},
						error: function(XMLHttpRequest,textStatus,errorThrown) {
							displayError(XMLHttpRequest.responseText);
						}
					});
					return false;
				});

				function displaySuccess(message) {
					$("#message").html(message)
								 .show("fast")
								 .fadeOut(10000);
				}

				function displayError(message) {
					$("#error").html(message)
							   .show("fast")
							   .fadeOut(10000);
				}

				var sendMessageButton = $('#doMessageSend');
				var sendMessage = function() {
					sendMessageButton.text('Sending...').addClass('disabled');
					var form = $('#sendMessageForm');
					$.ajax({
						url: form.attr('action'),
						method: 'POST',
						data: form.serialize(),
						dataType: 'JSON',
						success: function(json, textStatus, jqXHR) {
						    console.log(json);
							if (json) {    // There is no 'success' indicator currently
								form[0].reset();
								sendMessageButton.text('Send').removeClass('disabled');
								location.reload(true);
							} else {
								$("#sendMessageAlert").text(jqXHR.responseText).removeClass('hidden alert-info').addClass('alert-danger');
							}
						},
						error: function(jqXHR, textStatus, errorThrown) {
							if (jqXHR.status === 401 && jqXHR.getResponseHeader('Location')) {
								$("#sendMessageAlert").text('Error!').removeClass('hidden alert-info').addClass('alert-danger');
							} else {
								sendMessageButton.text('Error!!').removeClass('disabled btn-success').addClass('btn-error');
							}
						},
						complete: function(jqXHR, textStatus) {
							sendMessageButton.text('Send').removeClass('disabled');
						}
					})

				};
				sendMessageButton.click(sendMessage);
			});
		</g:javascript>
	</head>
	<body>
		<div class="row">
			<div class="left-side col-md-3 col-sm-4">
				<div class="card border-secondary">
					<div class="card-body">
						<div class="card">
							<div class="card-body">
								<g:if test='${profile.mainPhoto}'>
									<img src="${createLink(controller: 'image', action: 'renderMainPhoto', id: username)}" />
								</g:if>
								<g:else>
									<asset:image src="person.jpeg" width="150" height="150" class="img-responsive"/>
								</g:else>
							</div>
							<div class="card-footer">
								<p>${entityName} ${forLabel} <strong>${fullName}</strong></p>
								<g:if test="${profile.about}">
									<p><g:message code="profile.about.label" args="${[profile.about]}"/></p>
								</g:if>
							</div>
						</div>
						<sec:ifLoggedIn>
							<g:if test="${currentLoggedInUser != profile.user}">
								<div class="btn-group mt-4">
									<g:if test="${loggedInIsFollowing}">
										<button id="followingButton" class="btn btn-default following" role="button">
                                            <g:message code="profile.button.following.label" default="Following"/>
										</button>
									</g:if>
									<g:else>
										<button id="followingButton" class="btn btn-info nofollow" role="button">
											<span class="glyphicon glyphicon-plus"></span> <g:message code="profile.button.add.follow.label" default="Follow Me!"/>
										</button>
									</g:else>
									<button id="optionsMenu" type="button" class="btn btn-outline-dark dropdown-toggle"
                                            data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
										<span class="glyphicon glyphicon-cog"></span>
									</button>
									<div class="dropdown-menu" aria-labelledby="optionsMenu" role="menu">
                                        <a class="dropdown-item" href="#">
                                            <g:message code="profile.user.menu.start.ripple.with.label"
                                                       args="${[username]}"
                                                       default="Start Ripple with ${username}"/>
                                        </a>
                                        <a class="dropdown-item" href="#" data-toggle="modal" data-target="#sendMessageBox">
                                            <g:message code="profile.user.menu.message.label"
                                                       args="${[username]}"
                                                       default="Message ${username}"/>
                                        </a>
                                        <g:if test="${currentLoggedInUser.following.contains(profile.user)}">
                                            <a class="dropdown-item" href="#" id="unfollowButton">
                                                <g:message code="profile.user.menu.unfollow.label"
                                                           args="${[username]}"
                                                           default="Unfollow ${username}"/>
                                            </a>
                                        </g:if>
                                        <a class="dropdown-item" href="#" id="blockButton">
                                            <g:message code="profile.user.menu.block.user.label" default="Block User"/>
                                        </a>
									</div>
								</div>
							</g:if>
						</sec:ifLoggedIn>
					</div>
				</div>
			</div>
			<div class="col-md-6 col-sm-8 mb-4">
				<div class="alert alert-success" id="message"></div>
				<div class="alert alert-danger" id="error"></div>
				<h3><g:message code="profile.latest.ripples.label" args="${[fullName]}"/></h3>
				<g:render template="/ripple/topicEntry" collection="${profile.user.ripples}" var="ripple" />
			</div>
			<div class="col-md-3 col-sm-8">
				<g:if test="${profile.user.following}">
					<div class="card mb-4">
						<div class="card-header bg-info">
							<h5 class="card-title text-white">
                                <g:message code="profile.follows.label" default="Follows"/>
                            </h5>
						</div>
							<div class="card-body">
								<g:render template="profileEntry" collection="${profile.user.following}" var="user" />
							</div>
					</div>
				</g:if>
				<g:if test="${followedBy}">
					<div class="card mb-4">
						<div class="card-header bg-info">
							<h5 class="card-title text-white">
                                <g:message code="profile.followed.by.label" default="Followed By"/>
                            </h5>
						</div>
							<div class="card-body">
								<g:render template="profileEntry" collection="${followedBy}" var="user" />
							</div>
					</div>
				</g:if>
				<div class="card mb-4">
					<div class="card-header bg-info">
						<h5 class="card-title text-white">
                            <g:message code="profile.users.you.may.know.label" default="Users You May Know"/>
                        </h5>
					</div>
					<div class="card-body">
						<p>No one!</p>
					</div>
				</div>
                <div class="card mb-4">
                    <div class="card-header bg-info">
                        <h5 class="card-title text-white">
                            <g:message code="profile.user.tags.label"
                                       args="${[username]}"
                                       default="${username}'s Tags"/>
                        </h5>
                    </div>
                    <div class="card-body">
                        <g:each in="${profile.user.tags}" var="tag">
                            <p>${tag}</p>
                        </g:each>
                    </div>
                </div>
			</div>
		</div>
	    <sec:ifLoggedIn>
            <g:render template="/message/sendMessageModal" model='[recipient: "${username}"]'/>
        </sec:ifLoggedIn>
	</body>
</html>
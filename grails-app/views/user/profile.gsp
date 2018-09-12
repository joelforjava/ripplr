<html>
	<head>
		<title>Ripplr &#9729; Profile for ${profile.fullName}</title>
		<meta name="layout" content="main" />
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
						data: {'usernameToFollow': '${profile.user.username}'},
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
						data:{'usernameToBlock': '${profile.user.username}'},
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
						data:{'usernameToUnfollow': '${profile.user.username}'},
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
				};
				function displayError(message) {
					$("#error").html(message)
							   .show("fast")
							   .fadeOut(10000);
				};
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
									<img src="${createLink(controller: 'image', action: 'renderMainPhoto', id: profile.user.username)}" />
								</g:if>
								<g:else>
									<asset:image src="person.jpeg" width="150" height="150" class="img-responsive"/>
								</g:else>
							</div>
							<div class="card-footer">
								<p>Profile for <strong>${profile.fullName}</strong></p>
								<g:if test="${profile.about}">
									<p>About: ${profile.about}</p>
								</g:if>
							</div>
						</div>
						<sec:ifLoggedIn>
							<g:if test="${currentLoggedInUser != profile.user}">
								<div class="btn-group mt-4">
									<g:if test="${loggedInIsFollowing}">
										<button id="followingButton" class="btn btn-default following" role="button">
											Following
										</button>
									</g:if>
									<g:else>
										<button id="followingButton" class="btn btn-info nofollow" role="button">
											<span class="glyphicon glyphicon-plus"></span> Follow Me!
										</button>
									</g:else>
									<button id="optionsMenu" type="button" class="btn btn-outline-dark dropdown-toggle" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
										<span class="glyphicon glyphicon-cog"></span>
									</button>
									<div class="dropdown-menu" aria-labelledby="optionsMenu" role="menu">
                                        <a class="dropdown-item" href="#">
                                            Start Ripple with ${profile.user.username}
                                        </a>
                                        <g:if test="${currentLoggedInUser.following.contains(profile.user)}">
                                            <a class="dropdown-item" href="#" id="unfollowButton">
                                                Unfollow ${profile.user.username}
                                            </a>
                                        </g:if>
                                        <a class="dropdown-item" href="#" id="blockButton">
                                            Block User
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
				<h3>Latest Ripples for ${profile.fullName}</h3>
				<g:render template="/ripple/topicEntry" collection="${profile.user.ripples}" var="ripple" />
			</div>
			<div class="col-md-3 col-sm-8">
				<g:if test="${profile.user.following}">
					<div class="card mb-4">
						<div class="card-header bg-info">
							<h5 class="card-title text-white">Follows</h5>
						</div>
							<div class="card-body">
								<g:render template="profileEntry" collection="${profile.user.following}" var="user" />
							</div>
					</div>
				</g:if>
				<g:if test="${followedBy}">
					<div class="card mb-4">
						<div class="card-header bg-info">
							<h5 class="card-title text-white">Followed By</h5>
						</div>
							<div class="card-body">
								<g:render template="profileEntry" collection="${followedBy}" var="user" />
							</div>
					</div>
				</g:if>
				<div class="card mb-4">
					<div class="card-header bg-info">
						<h5 class="card-title text-white">Users You May Know</h5>
					</div>
					<div class="card-body">
						<p>No one!</p>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
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
						url:'/user/ajaxFollow',
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
						url:'/user/ajaxBlock',
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
						url:'/user/ajaxUnfollow',
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
				<div class="panel panel-default">
					<div class="panel-body">
						<div class="panel panel-info">
							<div class="panel-body">
								<g:if test='${profile.mainPhoto}'>
									<img src="${createLink(controller: 'image', action: 'renderMainPhoto', id: profile.user.username)}" />
								</g:if>
								<g:else>
									<img src="http://lorempixel.com/150/150" />
								</g:else>
							</div>
							<div class="panel-footer">
								<p>Profile for <strong>${profile.fullName}</strong></p>
								<g:if test="${profile.about}">
									<p>About: ${profile.about}</p>
								</g:if>
							</div>
						</div>
						<sec:ifLoggedIn>
							<g:if test="${currentLoggedInUser != profile.user}">
								<div class="btn-group">
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
									<button id="optionsMenu" type="button" class="btn btn-default dropdown-toggle" data-toggle="dropdown" aria-expanded="false">
										<span class="glyphicon glyphicon-cog"></span>
									</button>
									<ul class="dropdown-menu" role="menu">
										<li>
											<a href="#">
												Start Ripple with ${profile.user.username}
											</a>
										</li>
											<g:if test="${currentLoggedInUser.following.contains(profile.user)}">
												<li>
													<a href="#" id="unfollowButton">
														Unfollow ${profile.user.username}
													</a>
												</li>
											</g:if>
										<li>
											<a href="#" id="blockButton">
												Block User
											</a>
										</li>
									</ul>
								</div>
							</g:if>
						</sec:ifLoggedIn>
					</div>
				</div>
			</div>
			<div class="col-md-6 col-sm-8">
				<div class="alert alert-success" id="message"></div>
				<div class="alert alert-danger" id="error"></div>
				<h3>Latest Ripples for ${profile.fullName}</h3>
				<g:render template="/ripple/topicEntry" collection="${profile.user.ripples}" var="ripple" />
			</div>
			<div class="col-md-3 col-sm-8">
				<g:if test="${profile.user.following}">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Follows</h3>
						</div>
							<div class="panel-body">
								<g:render template="profileEntry" collection="${profile.user.following}" var="user" />
							</div>
					</div>
				</g:if>
				<g:if test="${followedBy}">
					<div class="panel panel-info">
						<div class="panel-heading">
							<h3 class="panel-title">Followed By</h3>
						</div>
							<div class="panel-body">
								<g:render template="profileEntry" collection="${followedBy}" var="user" />
							</div>
					</div>
				</g:if>
				<div class="panel panel-info">
					<div class="panel-heading">
						<h3 class="panel-title">Other Users You May Know</h3>
					</div>
					<div class="panel-body">
						<p>No one!</p>
					</div>
				</div>
			</div>
		</div>
	</body>
</html>
<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title>Ripplr &#9729; Welcome to Ripplr</title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
		<link href="https://fonts.googleapis.com/css?family=Montserrat" rel="stylesheet">
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
		<g:javascript>
			$(document).ready(function() {
				$('a[data-toggle="tab"]').on('shown.bs.tab', function(e) {
					var data= $(e.target).data('caption');
					var modal=$('#signInBox');
					modal.find('.modal-title').text(data);
				});

				var loginButton = $('#doLogin');
				var authAjax = function() {
					loginButton.text('Signing in...').addClass('disabled');
					var form = $('#signInForm');
					$.ajax({
						url: form.attr('action'),
						method: 'POST',
						data: form.serialize(),
						dataType: 'JSON',
						success: function(json, textStatus, jqXHR) {
							if (json.success) {
								form[0].reset();
								loginButton.text('Sign In').removeClass('disabled');
								location.reload(true);
							} else if (json.error) {
								$("#signInAlert").text(jqXHR.responseText).removeClass('hidden alert-info').addClass('alert-danger');
							}
						},
						error: function(jqXHR, textStatus, errorThrown) {
							if (jqXHR.status === 401 && jqXHR.getResponseHeader('Location')) {
								$("#signInAlert").text('Error!').removeClass('hidden alert-info').addClass('alert-danger');
							} else {
								loginButton.text('Error!!').removeClass('disabled btn-success').addClass('btn-error');
							}
						}, 
						complete: function(jqXHR, textStatus) {
							loginButton.text('Sign In').removeClass('disabled');
						}
					})
				};
				loginButton.click(authAjax);
				$('input').focus(function() {
					var $signInAlert = $("#signInAlert");
					if (!$signInAlert.hasClass('hidden')) {
						$signInAlert.addClass('hidden alert-info').removeClass('alert-danger');
					}
				});

				var logout = function(evt) {
					evt.preventDefault();
					$.ajax({
						url: $('#logout').attr('href'),
						method: 'POST',
						success: function(data, textStatus, jqXHR) {
							window.Location = '/'
							location.reload(true);
						},
						error: function(jqXHR, textStatus, errorThrown) {
							console.log('Logout error, textStatus: ' + textStatus + ', errorThrown: ' + errorThrown);
						}
					});
				};
				$('#logout').click(logout);
			});
		</g:javascript>
	</head>
	<body>
		<div class="container">
			<nav class="navbar navbar-inverse">
				<div class="navbar-header">
		          <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
		            <span class="sr-only">Toggle navigation</span>
		            <span class="icon-bar"></span>
		            <span class="icon-bar"></span>
		            <span class="icon-bar"></span>
		          </button>
		          <a class="navbar-brand" href="/">Ripplr</a>
		        </div>
				<div id="navbar" class="navbar-collapse collapse">
		          <ul class="nav navbar-nav">
		          	<!-- need to add code to set active -->
		            <li class="active"><a href="#"><span class="glyphicon glyphicon-home"></span> Home</a></li>
		            <sec:ifLoggedIn>
		            	<li><a href="${createLink(controller: 'ripple', action: 'dashboard')}">Your Dashboard</a></li>
		            </sec:ifLoggedIn>
		            <!-- move these to the footer?
		            <li><a href="#about">About</a></li>
		            <li><a href="#contact">Contact</a></li>
		            -->
		            <sec:ifLoggedIn>
			            <li class="dropdown">
			              <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false"><span class="glyphicon glyphicon-user"></span> <sec:username/>'s Profile <span class="caret"></span></a>
			              <ul class="dropdown-menu" role="menu">
			                <li><g:link controller="user" action="update">Settings</g:link></li>
							<li><g:link controller='logout'>Logout</g:link></li>
			                <li><a href="#">Something else here</a></li>
			                <li class="divider"></li>
			                <li class="dropdown-header">Nav header</li>
			                <li><a href="#">Separated link</a></li>
			                <li><a href="#">One more separated link</a></li>
			              </ul>
			            </li>
		            </sec:ifLoggedIn>
		            <sec:ifLoggedIn>
						<li><g:link elementId="logout" controller='logout'><span class="glyphicon glyphicon-log-out"></span> Logout</g:link></li>
		            </sec:ifLoggedIn>
		            <sec:ifNotLoggedIn>
 						<li>
 							<a class="pull-right" data-toggle="modal" data-target="#signInBox"><span class="glyphicon glyphicon-log-in"></span> Login</a>
 						</li>
 		            </sec:ifNotLoggedIn>
		          </ul>
		        </div>
	   		</nav>
			<g:layoutBody/>
			<div class="footer" role="contentinfo"></div>
            <sec:ifNotLoggedIn>
				<div class="modal fade" id="signInBox" tabindex="-1">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header">
								<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
								<h4 class="modal-title">Sign In to Ripplr</h4>
							</div>
							<div class="modal-body">
								<div>
									<ul class="nav nav-pills">
										<li class="active"><a href="#signInTab" data-toggle="tab" data-caption="Sign In to Ripplr">Sign In</a></li>
										<li><a href="#signUpTab" data-toggle="tab" data-caption="Sign Up for Ripplr">Sign Up</a></li>
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="signInTab">
											<div style="margin-top: 20px;" id="signInAlert" class="hidden alert alert-info"></div>
											<form style="padding-top: 5px;" id="signInForm" name="signInForm" action="${request.contextPath}/login/authenticate" method="POST">
												<div class="form-group">
													<input type="text" class="form-control" name="username" id="username" placeholder="User Name"/>
												</div>
												<div class="form-group">
													<input type="password" class="form-control" name="password" id="password" placeholder="Password"/>
												</div>
												<button type="button" class="btn btn-success btn-block" id="doLogin">Sign In</button>
											</form>
										</div>
										<div class="tab-pane" id="signUpTab">
											<form style="padding-top: 5px;" action="#">
												<div class="form-group">
													<input type="text" class="form-control" id="yourName" placeholder="Your Full Name"/>
												</div>
												<!-- etc. look at existing form -->
												<button type="button" class="btn btn-success btn-block" id="doLogin">Sign Up</button>
											</form>
										</div>
									</div>
								</div>
							</div>
							<div class="modal-footer">
								<button type="button" class="btn btn-warning" data-dismiss="modal">Cancel</button>
							</div>
						</div>
					</div>
				</div>
            </sec:ifNotLoggedIn>
		</div>
	</body>
</html>

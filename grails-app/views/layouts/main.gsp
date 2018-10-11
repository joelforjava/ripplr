<!DOCTYPE html>
<!--[if lt IE 7 ]> <html lang="en" class="no-js ie6"> <![endif]-->
<!--[if IE 7 ]>    <html lang="en" class="no-js ie7"> <![endif]-->
<!--[if IE 8 ]>    <html lang="en" class="no-js ie8"> <![endif]-->
<!--[if IE 9 ]>    <html lang="en" class="no-js ie9"> <![endif]-->
<!--[if (gt IE 9)|!(IE)]><!--> <html lang="en" class="no-js"><!--<![endif]-->
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
		<title><g:layoutTitle default="Ripplr &#9729; Welcome to Ripplr"/></title>
		<meta name="viewport" content="width=device-width, initial-scale=1.0">
		<link rel="shortcut icon" href="${assetPath(src: 'favicon.ico')}" type="image/x-icon">
		<link rel="apple-touch-icon" href="${assetPath(src: 'apple-touch-icon.png')}">
		<link rel="apple-touch-icon" sizes="114x114" href="${assetPath(src: 'apple-touch-icon-retina.png')}">
		<link href="https://fonts.googleapis.com/css?family=Montserrat" rel="stylesheet">
  		<asset:stylesheet src="application.css"/>
		<asset:javascript src="application.js"/>
		<g:layoutHead/>
	</head>
	<body>
		<div class="container">
			<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top">
				<div class="navbar-header">
					<a class="navbar-brand" href="/">Ripplr</a>
					<button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbar" aria-controls="navbar" aria-expanded="false" aria-label="Toggle Navigation">
						<span class="navbar-toggler-icon"></span>
					</button>
		        </div>
				<div id="navbar" class="navbar-collapse collapse">
		          <ul class="navbar-nav ml-auto">
		          	<!-- need to add code to set active -->
		            <li class="active nav-item text-white"><a class="nav-link" href="/"><span class="glyphicon glyphicon-home"></span> Home</a></li>
		            <sec:ifLoggedIn>
						<li class="nav-item text-white-50"><g:link class="nav-link" controller='search'><span class="glyphicon glyphicon-search"></span> Search</g:link></li>
		            	<li class="nav-item text-white-50"><a class="nav-link" href="${createLink(controller: 'ripple', action: 'dashboard')}">Your Dashboard</a></li>
		            </sec:ifLoggedIn>
		            <!-- move these to the footer?
		            <li><a href="#about">About</a></li>
		            <li><a href="#contact">Contact</a></li>
		            -->
		            <sec:ifLoggedIn>
			            <li class="nav-item dropdown">
			              <a href="#" id="dropdown-user-menu" class="nav-link dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false"><span class="glyphicon glyphicon-user"></span> <sec:username/>'s Profile <span class="caret"></span></a>
			              <div class="dropdown-menu" aria-labelledby="dropdown-user-menu" role="menu">
							  <g:link class="dropdown-item" controller="message" action="index">Messages</g:link>
			                <g:link class="dropdown-item" controller="user" action="update">Settings</g:link>
							<g:link class="dropdown-item" controller='logout'>Logout</g:link>
			                <a class="dropdown-item" href="#">Something else here</a>
			                <div class="dropdown-divider"></div>
			                <div class="dropdown-header">Nav header</div>
			                <a class="dropdown-item" href="#">Separated link</a>
			                <a class="dropdown-item" href="#">One more separated link</a>
			              </div>
			            </li>
		            </sec:ifLoggedIn>
		            <sec:ifLoggedIn>
						<li class="nav-item text-white-50"><g:link class="nav-link" elementId="logout" controller='logout'><span class="glyphicon glyphicon-log-out"></span> Logout</g:link></li>
		            </sec:ifLoggedIn>
		            <sec:ifNotLoggedIn>
 						<li class="nav-item text-white-50">
 							<a class="nav-link pull-right" data-toggle="modal" data-target="#signInBox"><span class="glyphicon glyphicon-log-in"></span> Login</a>
 						</li>
 		            </sec:ifNotLoggedIn>
		          </ul>
		        </div>
	   		</nav>
			<g:layoutBody/>
			<div class="footer" role="contentinfo"></div>
            <sec:ifNotLoggedIn>
				<div class="modal fade" id="signInBox" tabindex="-1" role="dialog">
					<div class="modal-dialog" role="document">
						<div class="modal-content">
							<div class="modal-header">
								<h5 class="modal-title">Sign In to Ripplr</h5>
								<button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
							</div>
							<div class="modal-body">
								<div>
									<ul class="nav nav-pills">
										<li class="nav-item"><a class="nav-link active" href="#signInTab" data-toggle="tab" data-caption="Sign In to Ripplr">Sign In</a></li>
										<li class="nav-item"><a class="nav-link" href="#signUpTab" data-toggle="tab" data-caption="Sign Up for Ripplr">Sign Up</a></li>
									</ul>
									<div class="tab-content">
										<div class="tab-pane active" id="signInTab">
											<div style="margin-top: 20px;" id="signInAlert" class="d-none alert alert-info"></div>
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
												<button type="button" class="btn btn-success btn-block" id="doSignup">Sign Up</button>
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

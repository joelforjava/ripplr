<!DOCTYPE html>
<html>
	<head>
		<meta name="layout" content="basic"/>
		<title>Welcome to Grails</title>
		<style type="text/css" media="screen">
			body {
				padding-top: 50px;
				padding-bottom: 20px;
			}
			body > .container {
				padding-bottom: 60px;
			}
		</style>
	</head>
	<body>
		<nav class="navbar navbar-expand-md navbar-dark bg-dark fixed-top" role="navigation">
		  <div class="container">
            <a class="navbar-brand" href="#">Ripplr</a>
            <button type="button" class="navbar-toggler" data-toggle="collapse" data-target="#navbar" aria-controls="navbar" aria-expanded="false" aria-label="Toggle Navigation">
                <span class="navbar-toggler-icon"></span>
            </button>
		    <div id="navbar" class="navbar-collapse collapse">
                <sec:ifNotLoggedIn>
			      <form action="${request.contextPath}/login/authenticate" method="POST" id="loginForm" autocomplete="off" class="form-inline my-2 my-lg-0">
			        <div class="form-group">
			          <input type="text" placeholder="User Name" name="username" id="username" class="form-control" style="background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3QsPDhss3LcOZQAAAU5JREFUOMvdkzFLA0EQhd/bO7iIYmklaCUopLAQA6KNaawt9BeIgnUwLHPJRchfEBR7CyGWgiDY2SlIQBT/gDaCoGDudiy8SLwkBiwz1c7y+GZ25i0wnFEqlSZFZKGdi8iiiOR7aU32QkR2c7ncPcljAARAkgckb8IwrGf1fg/oJ8lRAHkR2VDVmOQ8AKjqY1bMHgCGYXhFchnAg6omJGcBXEZRtNoXYK2dMsaMt1qtD9/3p40x5yS9tHICYF1Vn0mOxXH8Uq/Xb389wff9PQDbQRB0t/QNOiPZ1h4B2MoO0fxnYz8dOOcOVbWhqq8kJzzPa3RAXZIkawCenHMjJN/+GiIqlcoFgKKq3pEMAMwAuCa5VK1W3SAfbAIopum+cy5KzwXn3M5AI6XVYlVt1mq1U8/zTlS1CeC9j2+6o1wuz1lrVzpWXLDWTg3pz/0CQnd2Jos49xUAAAAASUVORK5CYII=); background-attachment: scroll; background-position: 100% 50%; background-repeat: no-repeat no-repeat;">
			        </div>
			        <div class="form-group">
			          <input type="password" placeholder="Password" name="password" id="password" class="form-control" style="background-image: url(data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAABAAAAASCAYAAABSO15qAAAABmJLR0QA/wD/AP+gvaeTAAAACXBIWXMAAAsTAAALEwEAmpwYAAAAB3RJTUUH3QsPDhss3LcOZQAAAU5JREFUOMvdkzFLA0EQhd/bO7iIYmklaCUopLAQA6KNaawt9BeIgnUwLHPJRchfEBR7CyGWgiDY2SlIQBT/gDaCoGDudiy8SLwkBiwz1c7y+GZ25i0wnFEqlSZFZKGdi8iiiOR7aU32QkR2c7ncPcljAARAkgckb8IwrGf1fg/oJ8lRAHkR2VDVmOQ8AKjqY1bMHgCGYXhFchnAg6omJGcBXEZRtNoXYK2dMsaMt1qtD9/3p40x5yS9tHICYF1Vn0mOxXH8Uq/Xb389wff9PQDbQRB0t/QNOiPZ1h4B2MoO0fxnYz8dOOcOVbWhqq8kJzzPa3RAXZIkawCenHMjJN/+GiIqlcoFgKKq3pEMAMwAuCa5VK1W3SAfbAIopum+cy5KzwXn3M5AI6XVYlVt1mq1U8/zTlS1CeC9j2+6o1wuz1lrVzpWXLDWTg3pz/0CQnd2Jos49xUAAAAASUVORK5CYII=); background-attachment: scroll; background-position: 100% 50%; background-repeat: no-repeat no-repeat;">
			        </div>
			        <button type="submit" class="btn btn-success my-2 my-sm-0">Sign in</button>
			      </form>
		      	</sec:ifNotLoggedIn>
		    </div>
		  </div>
		</nav>
   		<div class="jumbotron">
			<div class="container">
				<sec:ifNotLoggedIn>
					<h1>What is Ripplr?</h1>
					<p>Ripplr is a site to create ripples of discussion among friends, colleagues, and other interesting people.</p>
					<p><a class="btn btn-primary btn-lg" href="${createLink(controller: 'user', action: 'register')}" role="button">Join Ripplr</a></p>
				</sec:ifNotLoggedIn>
				<sec:ifLoggedIn>
					<p>Welcome back, <sec:username />!</p>
				</sec:ifLoggedIn>
			</div>
		</div>
		<div class="container">
			<div class="row">
				<div id="status" class="col-md-3" role="complementary">
					<div class="card">
						<div class="card-header">
							<h3 class="card-title">Application Status</h3>
						</div>
						<div class="card-body">
							<ul>
								<li>App version: <g:meta name="app.version"/></li>
								<li>Grails version: <g:meta name="app.grails.version"/></li>
								<li>Groovy version: ${GroovySystem.getVersion()}</li>
								<li>JVM version: ${System.getProperty('java.version')}</li>
								<li>Reloading active: ${grails.util.Environment.reloadingAgentEnabled}</li>
								<li>Controllers: ${grailsApplication.controllerClasses.size()}</li>
								<li>Domains: ${grailsApplication.domainClasses.size()}</li>
								<li>Services: ${grailsApplication.serviceClasses.size()}</li>
								<li>Tag Libraries: ${grailsApplication.tagLibClasses.size()}</li>
							</ul>
						</div>
					</div>
				</div>
				<div class="col-md-3">
					<div class="card">
						<div class="card-header">
							<h3 class="card-title">Installed Plugins</h3>
						</div>
						<div class="card-body">
							<ul>
								<g:each var="plugin" in="${applicationContext.getBean('pluginManager').allPlugins}">
									<li>${plugin.name} - ${plugin.version}</li>
								</g:each>
							</ul>
						</div>
					</div>
				</div>
				<div id="page-body" class="col-md-6" role="main">
					<div class="card">
						<div class="card-header">
							<h3 class="card-title">Welcome to Grails</h3>
						</div>
						<div class="card-body">
							<p>Congratulations, you have successfully started your first Grails application! At the moment
							   this is the default page, feel free to modify it to either redirect to a controller or display whatever
							   content you may choose. Below is a list of controllers that are currently deployed in this application,
							   click on each to execute its default action:</p>

							<div id="controller-list" role="navigation">
								<h2>Available Controllers:</h2>
								<ul>
									<g:each var="c" in="${grailsApplication.controllerClasses.sort { it.fullName } }">
										<li class="controller"><g:link controller="${c.logicalPropertyName}">${c.fullName}</g:link></li>
									</g:each>
								</ul>
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
		<footer class="footer">
			<div class="container">
				<p class="text-muted">Footer to go here</p>
			</div>
		</footer>
	</body>
</html>

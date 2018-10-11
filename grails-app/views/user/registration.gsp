<html>
	<head>
		<title><g:message code="register.form.title.label" default="New User Registration"/></title>
		<meta name="layout" content="main">
	</head>
	<body>
		<div class="border-bottom mb-4">
			<h1><g:message code="register.form.headline.label" default="New User Registration"/></h1>
		</div>
		<g:hasErrors>
			<div class="errors alert alert-danger">
				<g:renderErrors bean="${user}" as="list" />
			</div>
		</g:hasErrors>
		<g:if test="${flash.message}">
			<div class="flash">${flash.message}</div>
		</g:if>
		<div class="col-md-2">
		</div>
		<div class="col-md-6">
			<g:uploadForm useToken="true" action="register">
				<fieldset class="form">
					<div class="form-group">
						<label for="username"><g:message code="register.form.username.label" default="User Name"/></label>
						<g:textField class="form-control" name="username" value="${user?.username}" />
					</div>
					<div class="form-group">
						<label for="password"><g:message code="register.form.password.label" default="Password"/></label>
						<g:passwordField class="form-control" name="password" />
					</div>
					<div class="form-group">
						<label for="passwordVerify"><g:message code="register.form.password.verify.label" default="Password (verify)"/></label>
						<g:passwordField class="form-control" name="passwordVerify" />
					</div>
					<div class="form-group">
						<label for="profile.fullName"><g:message code="register.form.fullname.label" default="Full Name"/></label>
						<g:textField class="form-control" name="profile.fullName" value="${user?.profile?.fullName}" />
					</div>
					<div class="form-group">
						<label for="profile.about"><g:message code="register.form.about.label" default="About Yourself"/></label>
						<g:textArea class="form-control" name="profile.about" value="${user?.profile?.about}" />
					</div>
					<div class="form-group">
						<label for="profile.email"><g:message code="register.form.email.label" default="Email"/></label>
						<g:textField class="form-control" name="profile.email" value="${user?.profile?.email}" />
						<g:hasErrors bean="${user}" field="profile.email">
							<g:eachError bean="${user}" field="profile.email">
								<p style="color: red;"><g:message error="${it}" /></p>
							</g:eachError>
						</g:hasErrors>
					</div>
					<div class="form-group">
						<label for="profile.country"><g:message code="register.form.country.label" default="Country"/></label>
						<g:countrySelect class="form-control"
										 name="profile.country"
                                         noSelection="['': 'Choose your country...']"
                                         value="${user?.profile?.country}"/>
					</div>
					<div class="form-group">
						<label for="profile.timezone"><g:message code="register.form.timezone.label" default="Time Zone"/></label>
						<g:timeZoneSelect class="form-control" name="profile.timezone" value="${user?.profile?.timezone ? TimeZone.getTimeZone(user?.profile?.timezone as String) : ''}"/>
					</div>
                    <div class="form-group">
                        <div class="custom-file">
                            <label class="custom-file-label" for="profile.mainPhoto.photo"><g:message code="register.form.mainPhoto.label" default="Main Photo"/></label>
                            <input type="file" class="custom-file-input" name="profile.mainPhoto.photo" value="${user?.profile?.mainPhoto?.photo}"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <div class="custom-file">
                            <label class="custom-file-label" for="profile.coverPhoto.photo"><g:message code="register.form.coverPhoto.label" default="Cover Photo"/></label>
                            <input type="file" class="custom-file-input" name="profile.coverPhoto.photo" value="${user?.profile?.coverPhoto?.photo}" />
                        </div>
                    </div>
					<div class="form-group">
						<label for="profile.facebookProfile"><g:message code="register.form.facebook.profile.label" default="Facebook Profile"/></label>
						<div class="input-group">
                            <div class="input-group-prepend">
                                <div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
                            </div>
							<g:textField class="form-control" name="profile.facebookProfile" value="${user?.profile?.facebookProfile}"/>
						</div>
					</div>
					<div class="form-group">
						<label for="profile.twitterProfile"><g:message code="register.form.twitter.profile.label" default="Twitter Profile"/></label>
						<div class="input-group">
                            <div class="input-group-prepend">
                                <div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
                            </div>
							<g:textField class="form-control" name="profile.twitterProfile" value="${user?.profile?.twitterProfile}"/>
						</div>
					</div>

				</fieldset>
				<fieldset class="buttons">
					<g:submitButton class="btn btn-primary" name="register" value="${message(code: 'register.form.register.button.label', default: 'Register Now!')}" />
				</fieldset>
			</g:uploadForm>
		</div>
	</body>
</html>
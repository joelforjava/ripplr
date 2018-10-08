<fieldset class="form">
	<div class="form-group">
		<label for="username"><g:message code="update.form.username.label" default="User Name"/></label>
		<g:textField class="form-control" name="username" value="${user?.username}" />
	</div>
	<div class="form-group">
		<label for="profile.fullName"><g:message code="update.form.fullname.label" default="Full Name"/></label>
		<g:textField class="form-control" name="profile.fullName" value="${user?.profile?.fullName}" />
	</div>
	<div class="form-group">
		<label for="profile.about"><g:message code="update.form.about.label" default="About Yourself"/></label>
		<g:textArea class="form-control" name="profile.about" value="${user?.profile?.about}" />
	</div>
	<div class="form-group">
		<label for="profile.email"><g:message code="update.form.email.label" default="Email"/></label>
		<g:textField class="form-control" name="profile.email" value="${user?.profile?.email}" />
		<g:hasErrors bean="${user?.profile}" field="email">
			<g:eachError bean="${user?.profile}" field="email">
				<p style="color: red;"><g:message error="${it}" /></p>
			</g:eachError>
		</g:hasErrors>
	</div>
	<div class="form-group">
		<label for="profile.country"><g:message code="register.form.country.label" default="Country"/></label>
		<g:countrySelect class="form-control"
						 name="profile.country"
                         noSelection="['':'Choose your country...']"
                         value="${user?.profile?.country}"/>
	</div>
	<div class="form-group">
		<label for="profile.timezone"><g:message code="update.form.timezone.label" default="Time Zone"/></label>
		<g:timeZoneSelect class="form-control" name="profile.timezone" value="${user?.profile?.timezone ? TimeZone.getTimeZone(user?.profile?.timezone) : null}"/>
	</div>
	<div class="form-group">
		<div class="custom-file">
			<label class="custom-file-label" for="profile.mainPhoto.photo"><g:message code="update.form.mainPhoto.label" default="Main Photo"/></label>
			<input type="file" class="custom-file-input" name="profile.mainPhoto.photo" />
		</div>
	</div>
	<div class="form-group">
		<div class="custom-file">
			<label class="custom-file-label" for="profile.coverPhoto.photo"><g:message code="update.form.coverPhoto.label" default="Cover Photo"/></label>
			<input type="file" class="custom-file-input" name="profile.coverPhoto.photo" />
		</div>
	</div>
	<div class="form-group">
		<label for="profile.facebookProfile"><g:message code="update.form.facebook.profile.label" default="Facebook Profile"/></label>
		<div class="input-group">
			<div class="input-group-prepend">
				<div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
			</div>
			<g:textField class="form-control" name="profile.facebookProfile" value="${user?.profile?.facebookProfile}"/>
		</div>
	</div>
	<div class="form-group">
		<label for="profile.twitterProfile"><g:message code="update.form.twitter.profile.label" default="Twitter Profile"/></label>
		<div class="input-group">
			<div class="input-group-prepend">
				<div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
			</div>
			<g:textField class="form-control" name="profile.twitterProfile" value="${user?.profile?.twitterProfile}"/>
		</div>
	</div>

</fieldset>

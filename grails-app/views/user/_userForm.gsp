<fieldset class="form">
	<div class="form-group">
		<label for="username">User Name</label>
		<g:textField class="form-control" name="username" value="${user?.username}" />
	</div>
	<div class="form-group">
		<label for="password">Password</label>
		<g:passwordField class="form-control" name="password" />
	</div>
	<div class="form-group">
		<label for="passwordVerify">Password (verify)</label>
		<g:passwordField class="form-control" name="passwordVerify" />
	</div>
	<div class="form-group">
		<label for="profile.fullName">Full Name</label>
		<g:textField class="form-control" name="profile.fullName" value="${user?.profile?.fullName}" />
	</div>
	<div class="form-group">
		<label for="profile.about">About Yourself</label>
		<g:textArea class="form-control" name="profile.about" value="${user?.profile?.about}" />
	</div>
	<div class="form-group">
		<label for="profile.email">Email</label>
		<g:textField class="form-control" name="profile.email" value="${user?.profile?.email}" />
		<g:hasErrors bean="${user?.profile}" field="email">
			<g:eachError bean="${user?.profile}" field="email">
				<p style="color: red;"><g:message error="${it}" /></p>
			</g:eachError>
		</g:hasErrors>
	</div>
	<div class="form-group">
		<label for="profile.country">Country</label>
		<g:countrySelect class="form-control"
						 name="profile.country"
                         noSelection="['':'Choose your country...']"
                         value="${user?.profile?.country}"/>
	</div>
	<div class="form-group">
		<label for="profile.timezone">Time Zone</label>
		<g:timeZoneSelect class="form-control" name="profile.timezone" value="${user?.profile?.timezone ? TimeZone.getTimeZone(user?.profile?.timezone) : null}"/>
	</div>
	<div class="form-group">
		<div class="custom-file">
			<label class="custom-file-label" for="profile.mainPhoto.photo">Main Photo</label>
			<input type="file" class="custom-file-input" name="profile.mainPhoto.photo" />
		</div>
	</div>
	<div class="form-group">
		<div class="custom-file">
			<label class="custom-file-label" for="profile.coverPhoto.photo">Cover Photo</label>
			<input type="file" class="custom-file-input" name="profile.coverPhoto.photo" />
		</div>
	</div>
	<div class="form-group">
		<label for="profile.facebookProfile">Facebook Profile</label>
		<div class="input-group">
			<div class="input-group-prepend">
				<div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
			</div>
			<g:textField class="form-control" name="profile.facebookProfile" value="${user?.profile?.facebookProfile}"/>
		</div>
	</div>
	<div class="form-group">
		<label for="profile.twitterProfile">Twitter Profile</label>
		<div class="input-group">
			<div class="input-group-prepend">
				<div class="input-group-text"><span class="glyphicon glyphicon-thumbs-up"></span></div>
			</div>
			<g:textField class="form-control" name="profile.twitterProfile" value="${user?.profile?.twitterProfile}"/>
		</div>
	</div>

</fieldset>

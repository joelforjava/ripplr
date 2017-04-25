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
		<label for="fullName">Full Name</label>
		<g:textField class="form-control" name="profile.fullName" value="${user?.profile?.fullName}" />
	</div>
	<div class="form-group">
		<label for="about">About Yourself</label>
		<g:textArea class="form-control" name="profile.about" value="${user?.profile?.about}" />
	</div>
	<div class="form-group">
		<label for="email">Email</label>
		<g:textField class="form-control" name="profile.email" value="${user?.profile?.email}" />
		<g:hasErrors bean="${user?.profile}" field="email">
			<g:eachError bean="${user?.profile}" field="email">
				<p style="color: red;"><g:message error="${it}" /></p>
			</g:eachError>
		</g:hasErrors>
	</div>
	<div class="form-group">
		<label for="country">Country</label>
		<g:countrySelect class="form-control" name="profile.country" noSelection="['':'Choose your country...']"/>
	</div>
	<div class="form-group">
		<label for="timezone">Time Zone</label>
		<g:timeZoneSelect class="form-control" name="profile.timezone" />
	</div>
	<div class="form-group">
		<label for="mainPhoto">Main Photo</label>
		<input type="file" class="form-control" name="profile.mainPhoto" />
	</div>
	<div class="form-group">
		<label for="coverPhoto">Cover Photo</label>
		<input type="file" class="form-control" name="profile.coverPhoto" />
	</div>
	<div class="form-group">
		<label for="facebookProfile">Facebook Profile</label>
		<div class="input-group">
			<span class="input-group-addon" id="fb-addon"><span class="glyphicon glyphicon-thumbs-up"></span></span>					
			<g:textField class="form-control" name="profile.facebookProfile" value="${user?.profile?.facebookProfile}" aria-describedby="fb-addon"/>
		</div>
	</div>
	<div class="form-group">
		<label for="twitterProfile">Twitter Profile</label>
		<div class="input-group">
			<span class="input-group-addon" id="tw-addon"><span class="glyphicon glyphicon-thumbs-up"></span></span>					
			<g:textField class="form-control" name="profile.twitterProfile" value="${user?.profile?.twitterProfile}" aria-describedby="tw-addon" />
		</div>
	</div>

</fieldset>

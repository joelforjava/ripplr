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

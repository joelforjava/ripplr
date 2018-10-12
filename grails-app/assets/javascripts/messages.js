// TODO - refactor all JS!
(function(handleMessages) {
    handleMessages(window.jQuery, window, document);
}(function($, window, document) {
    $(function() {
        var $sendMessageButton = $('#doMessageSend');
        var sendMessage = function() {
            $sendMessageButton.text('Sending...').addClass('disabled');
            var form = $('#sendMessageForm');
            $.ajax({
                url: form.attr('action'),
                method: 'POST',
                data: form.serialize(),
                dataType: 'JSON',
                success: function(json, textStatus, jqXHR) {
                    console.log(json);
                    if (json) {    // There is no 'success' indicator currently
                        form[0].reset();
                        $sendMessageButton.text('Send').removeClass('disabled');
                        location.replace('/message/index'); // Hacky, yes, but will refactor
                    } else {
                        $("#sendMessageAlert").text(jqXHR.responseText).removeClass('hidden alert-info').addClass('alert-danger');
                    }
                },
                error: function(jqXHR, textStatus, errorThrown) {
                    if (jqXHR.status === 401 && jqXHR.getResponseHeader('Location')) {
                        $("#sendMessageAlert").text('Error!').removeClass('hidden alert-info').addClass('alert-danger');
                    } else {
                        $sendMessageButton.text('Error!!').removeClass('disabled btn-success').addClass('btn-error');
                    }
                },
                complete: function(jqXHR, textStatus) {
                    $sendMessageButton.text('Send').removeClass('disabled');
                }
            });

        };
        $sendMessageButton.click(sendMessage);
    });
}));
<html>
    <head>
        <title>Message</title>
        <meta name="layout" content="main">
        <g:javascript>
            // TODO - refactor all JS!
            $(document).ready(function() {
                var sendMessageButton = $('#doMessageSend');
                var sendMessage = function() {
                    sendMessageButton.text('Sending...').addClass('disabled');
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
                                sendMessageButton.text('Send').removeClass('disabled');
                                location.replace('/message/index'); // Hacky, yes, but will refactor
                            } else {
                                $("#sendMessageAlert").text(jqXHR.responseText).removeClass('hidden alert-info').addClass('alert-danger');
                            }
                        },
                        error: function(jqXHR, textStatus, errorThrown) {
                            if (jqXHR.status === 401 && jqXHR.getResponseHeader('Location')) {
                                $("#sendMessageAlert").text('Error!').removeClass('hidden alert-info').addClass('alert-danger');
                            } else {
                                sendMessageButton.text('Error!!').removeClass('disabled btn-success').addClass('btn-error');
                            }
                        },
                        complete: function(jqXHR, textStatus) {
                            sendMessageButton.text('Send').removeClass('disabled');
                        }
                    })

                };
                sendMessageButton.click(sendMessage);
            });
        </g:javascript>
    </head>
    <body>
        <div class="border-bottom mb-4">
            <h1>${message.subject}</h1>
        </div>
        <div class="container">
            <div class="row">
                <div class="col">
                    <div class="card">
                        <div class="card-body">
                            <div class="media">
                                <asset:image src="person.jpeg" width="64" height="64" class="mr-3" alt="Profile picture for User ${message.sender.username}"/>
                                <div class="media-body">
                                    <h6 class="mt-0">${message.sender.username}</h6>
                                    TODO - User details might go here?
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
            <div class="row mt-4">
                <div class="col">
                    <div class="card border-primary">
                        <div class="card-body">
                            ${message.content}
                        </div>
                        <div class="card-footer">
                            <div class="float-right">
                                <a href="${g.createLink(action: 'index')}" class="btn btn-secondary">Go Back to Messages</a>
                                <a href="#" class="btn btn-primary" data-toggle="modal" data-target="#sendMessageBox">Reply</a>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        <sec:ifLoggedIn>
            <div class="modal fade" id="sendMessageBox" tabindex="-1" role="dialog">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header bg-primary text-white">
                            <h5 class="modal-title">Send Message</h5>
                            <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
                        </div>
                        <div class="modal-body">
                            <div style="margin-top: 20px;" id="sendMessageAlert" class="d-none alert alert-info"></div>
                            <g:form useToken="false" name="sendMessageForm" controller="message" action="save" method="POST">
                                <div class="form-group">
                                    <input type="text" class="form-control" name="subject" id="subject" value="RE: ${message.subject}"/>
                                </div>
                                <div class="form-group">
                                    <g:textArea class="form-control" name="content"/>
                                </div>
                                <input type="hidden" name="recipientUsername" id="recipientUsername" value="${message.sender.username}"/>
                                <button type="button" class="btn btn-success btn-block" id="doMessageSend">Send</button>
                            </g:form>
                        </div>
                        <div class="modal-footer">
                            <button type="button" class="btn btn-warning" data-dismiss="modal">Cancel</button>
                        </div>
                    </div>
                </div>
            </div>
        </sec:ifLoggedIn>
    </body>
</html>
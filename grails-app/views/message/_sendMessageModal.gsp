<div class="modal fade" id="sendMessageBox" tabindex="-1" role="dialog">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-primary text-white">
                <h5 class="modal-title"><g:message code="message.send.header.label" default="Send Message"/></h5>
                <button type="button" class="close" data-dismiss="modal"><span>&times;</span></button>
            </div>
            <div class="modal-body">
                <div style="margin-top: 20px;" id="sendMessageAlert" class="d-none alert alert-info"></div>
                <g:form useToken="false" name="sendMessageForm" controller="message" action="save" method="POST">
                    <div class="form-group">
                        <input type="text" class="form-control" name="subject" id="subject" placeholder="Subject" value="${subject ?: ''}"/>
                    </div>
                    <div class="form-group">
                        <g:textArea class="form-control" name="content"/>
                    </div>
                    <input type="hidden" name="recipientUsername" id="recipientUsername" value="${recipient}"/>
                    <button type="button" class="btn btn-success btn-block" id="doMessageSend"><g:message code="message.button.send.label" default="Send"/></button>
                </g:form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-warning" data-dismiss="modal"><g:message code="message.button.cancel.label" default="Cancel"/></button>
            </div>
        </div>
    </div>
</div>

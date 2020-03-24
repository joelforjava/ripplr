(function($, window, document) {
    $(function() {
        const $addTopicBtn = $("#addTopicBtn");
        const $topicContent = $("#topicContent");
        $addTopicBtn.click(function() {
            showSpinner(true);
            $topicContent.fadeTo("fast", 0.4);
            $(this).attr('disabled', 'disabled');
            $.ajax({
                type:'POST',
                data:$(this).parents('form:first').serialize(),
                url:'/ripple/save',
                success: function(data,textStatus){
                    if (textStatus === 'success') {
                        $('#allTopics').html(data);
                        clearTopic(data);
                    }
                },
                error: function(XMLHttpRequest,textStatus,errorThrown){
                },
                complete: function(XMLHttpRequest,textStatus){
                    showSpinner(false);
                    $topicContent.fadeTo("fast", 1.0);
                    $addTopicBtn.removeAttr('disabled');
                    $addTopicBtn.blur();
                }
            });
            return false;
        });
        const $replyButtons = $(".reply-button");
        $replyButtons.each(function(index) {
            const inReplyTo = $(this).data('in-reply-to-button');
            console.log("This button is to reply to Ripple number: " + inReplyTo);
            $(this).click(function() {
                const $inReplyToSection = $(`*[data-in-reply-to-id="${inReplyTo}"]`);
                $inReplyToSection.slideToggle();
                $inReplyToSection.find('button').click(function() {
                    console.debug($(this).parents('form:first'));
                    $.ajax({
                        type:'POST',
                        data:$(this).parents('form:first').serialize(),
                        url:'/response/save',
                        success: function(data,textStatus){
                            if (textStatus === 'success') {
                                $('#allTopics').html(data);
                                clearTopic(data);
                            }
                        },
                        error: function(XMLHttpRequest,textStatus,errorThrown){
                            console.log(textStatus);
                            console.log(errorThrown);
                        },
                        complete: function(XMLHttpRequest,textStatus){
                            $inReplyToSection.slideToggle();
                        }
                    });
                    return false;
                });
            });
        });
    });

    function clearTopic(e) {
        $('#topicContent').val('');
    }
    function showSpinner(visible) {
        if (visible) {
            console.log("Showing stuff...");
            //$("#newPost textArea, #newPost .btn").attr('disabled', 'disabled');
            $('.spinner').slideDown();
        } else {
            console.log("Hiding stuff...");
            //$("#newPost textArea, #newPost .btn").attr('disabled', '');
            $('.spinner').slideUp();
        }
    }

}(window.jQuery, window, document));;

(function($, window, document) {

    $(function() {
        var $followingButton = $("#followingButton");
        var $unfollowButton = $("#unfollowButton");
        var $blockButton = $("#blockButton");
        var $username = $("#profileUsername").val();
        $("#message, #error").hide();
        $("#followingButton.nofollow").click(function() {
            $followingButton.html('<div class="loader">Loading...</div>');
            $.ajax({
                type:'POST',
                data: {'usernameToFollow': $username},
                url:'/user/follow',
                success: function(data, status) {
                    displaySuccess(data);
                    $followingButton.removeClass("btn-info nofollow")
                        .addClass("btn-default following disabled")
                        .text("Following")
                        .blur();
                },
                error: function(XMLHttpRequest,textStatus,errorThrown) {
                    displayError(XMLHttpRequest.responseText);
                }
            });
            return false;
        });
        $blockButton.click(function() {
            blockUser($username).done(function(data) {
                displaySuccess(data);
                $("#optionsMenu").blur();
            }).catch(function (err) {
                displayError(err);
            });
        });
        $unfollowButton.click(function() {
            $followingButton.html('<div class="loader">Loading...</div>');
            $(".dropdown-menu").blur();
            $.ajax({
                type:'POST',
                data:{'usernameToUnfollow': $username},
                url:'/user/unfollow',
                success: function(data,textStatus) {
                    displaySuccess(data);
                    $("#unfollowButton, #optionsMenu").blur();
                    $followingButton.addClass("btn-info nofollow")
                        .removeClass("btn-default following disabled")
                        .html('<span class="glyphicon glyphicon-plus"></span> Follow Me!')
                        .blur();
                    $unfollowButton.hide();
                },
                error: function(XMLHttpRequest,textStatus,errorThrown) {
                    displayError(XMLHttpRequest.responseText);
                }
            });
            return false;
        });

        function blockUser(username) {
            var data = { usernameToBlock : username };
            return $.ajax({
                type: 'POST',
                data: data,
                url: '/user/block'
            });
        }

        function displaySuccess(message) {
            $("#message").html(message)
                .show("fast")
                .fadeOut(10000);
        }

        function displayError(message) {
            $("#error").html(message)
                .show("fast")
                .fadeOut(10000);
        }
    });

}(window.jQuery, window, document));

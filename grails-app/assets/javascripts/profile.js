$(document).ready(function() {
    var $followingButton = $("#followingButton");
    var $unfollowButton = $("#unfollowButton");
    var $username = $("#profileUsername").val();
    $("#message, #error").hide();
    $("#followingButton.nofollow").click(function() {
        $followingButton.html('<div class="loader">Loading...</div>');
        jQuery.ajax({
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
    $("#blockButton").click(function() {
        jQuery.ajax({
            type:'POST',
            data:{'usernameToBlock': $username},
            url:'/user/block',
            success: function(data,textStatus) {
                displaySuccess(data);

                $("#optionsMenu").blur();
            },
            error: function(XMLHttpRequest,textStatus,errorThrown) {
                displayError(XMLHttpRequest.responseText);
            }
        });
        return false;
    });
    $unfollowButton.click(function() {
        $followingButton.html('<div class="loader">Loading...</div>');
        $(".dropdown-menu").blur();
        jQuery.ajax({
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

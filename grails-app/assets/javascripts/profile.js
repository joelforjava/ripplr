(function($, window, document) {

    $(function() {
        var $followingButton = $("#followingButton");
        var $unfollowButton = $("#unfollowButton");
        var $blockButton = $("#blockButton");
        var $username = $("#profileUsername").val();

        $("#message, #error").hide();

        if ($followingButton.hasClass("nofollow")) {
            configureFollowButton();
        }

        $blockButton.click(function() {
            blockUser($username).done(function(data) {
                displaySuccess(data);
                hideDisplayedMenu();
            }).catch(function (err) {
                displayError(err);
            });
        });

        $unfollowButton.click(function() {
            $followingButton.html('<div class="loader">Loading...</div>');
            unfollowUser($username).done(function(data) {
                displaySuccess(data);
                reenableFollowButton();
                configureFollowButton();
                hideUnfollowButton();
                hideDisplayedMenu();
            }).catch(function (err) {
                displayError(err);
            });
        });

        function configureFollowButton() {
            $followingButton.click(function() {
                followUser($username).done(function(data) {
                    displaySuccess(data);
                    disableFollowButton();
                }).catch(function (err) {
                    displayError(err);
                });
            });
        }

        function disableFollowButton() {
            $followingButton.removeClass("btn-info nofollow")
                .addClass("btn-default following disabled")
                .text("Following")
                .off('click')
                .blur();
        }

        function reenableFollowButton() {
            $followingButton.addClass("btn-info nofollow")
                .removeClass("btn-default following disabled")
                .html('<span class="glyphicon glyphicon-plus"></span> Follow Me!')
                .blur();
        }

        function hideUnfollowButton() {
            $unfollowButton.blur();
            $unfollowButton.hide();
        }

        function hideDisplayedMenu() {
            $(".dropdown-menu.show").dropdown('toggle');
        }

        function followUser(username) {
            var data = { usernameToFollow : username };
            return $.ajax({
                type: 'POST',
                data: data,
                url: '/user/follow'
            });
        }

        function unfollowUser(username) {
            var data = { usernameToUnfollow : username };
            return $.ajax({
                type: 'POST',
                data: data,
                url: '/user/unfollow'
            });
        }

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

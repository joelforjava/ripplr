package com.joelforjava.ripplr

import grails.gorm.transactions.Transactional


class UserException extends RuntimeException {
	String message
	User user
}


@Transactional
class UserService {

    /**
     * Retrieve a user via unique ID.
     *
     * @param id - the user ID as assigned by the database.
     * @return the retrieved user, if found. Otherwise, null.
     */
    User findUser(Long id) {
        User.read(id)
    }

    /**
     * Retrieve a user with a given unique username.
     *
     * @param username - the username of given user.
     * @return the retrieved user.
     * @throws UserException if user is not found
     */
    User findUser(String username) {
        User.findByUsername(username)
    }

    /**
     * Retrieve the latest users that have joined.
     *
     * @param numLatest - the number of requested users
     * @return a list of at most the numLatest users.
     * @throws UserException if no users are found
     */
    List retrieveLatestUsers(int numLatest = 5) {
        int numToReturn = 5
        if (numLatest > 0) {
            numToReturn = numLatest
        }

        User.list(max: numToReturn, sort: 'dateCreated', order: 'desc')
    }

    User create(UserRegisterCommand command, boolean flush = false) {
        def user = command as User
        if (!user?.save(flush: flush)) {
            log.error("Could not save user: ${user?.errors?.toString()}")
            return user
        }
        user
    }

    User update(UserUpdateCommand cmd) {
        def user = findUser cmd.username
        if (cmd.passwordDirty) {
            return this.saveUser(user.id, cmd.username, cmd.password)
        } else if (cmd.usernameDirty) {
            // TODO - if the username is dirty, then the 'findUser' call above will always return null!
            return this.updateUsername(user.id, cmd.username)
        }
        user
    }

    protected User saveUser(Long userId, String username, String passwordHash, boolean accountLocked = false,
                  boolean accountExpired = false, boolean passwordExpired = false) {

        def user = findUser userId

        if (!user) {
            return user
        }

        user.passwordHash = passwordHash
        user.accountLocked = accountLocked
        user.accountExpired = accountExpired
        user.passwordExpired = passwordExpired
        user.username = username // this will be turned off at the UI level, for now
        user.save()

        user
    }

    protected User updateUsername(Long userId, String username) {
        def user = findUser userId

        if (!user) {
            return user
        }

        user.username = username
        user.save()

        user

    }

    int onlineUserCount() {
        return User.count()
    }

    boolean addToFollowing(String username, String nameToFollow) {
        def user = User.findByUsername(username)
        def userToFollow = User.findByUsername nameToFollow

        if (user && userToFollow && (userToFollow.username != user.username)) {
            log.debug "userToFollow valid and username is not current user's username. attempting to add"
            user.addToFollowing userToFollow
            def success = user.save()
            def following = user.following
            log.debug "${user.username} is following $following"
            return success
        }
        throw new UserException(message: 'Either requested user to follow is invalid or that user is equal to the user requesting to add', user: user)
    }

    boolean removeFromFollowing(String username, String nameToUnfollow) {
        def user = User.findByUsername username
        def userToRemove = User.findByUsername nameToUnfollow

        if (user && userToRemove && (userToRemove.username != user.username)) {
            user.removeFromFollowing userToRemove
            def success = user.save()
            def following = user.following
            log.debug "${user.username} is no longer following $following"
            return success
        }
        throw new UserException(message: 'Either requested user to remove from following is invalid or user is attempting to unfollow himself', user: user)
    }

    boolean addToBlocking(String username, String nameToBlock) {
        def user = User.findByUsername username
        def userToBlock = User.findByUsername nameToBlock

        // Do we add logic to remove nameToBlock from user.following?
        // Or, is that handled separately? -- probably separately. Don't want one method doing too much

        if (user && userToBlock && (userToBlock.username != user.username)) {
            user.addToBlocking userToBlock
            def success = user.save()
            return success
        }
        throw new UserException(message: 'Either requested user to block is invalid or that user is equal to the user requesting to block', user: user)
    }

//    boolean blockUser(String username, String usernameToBlock) {
//        removeFromFollowing(username, usernameToBlock) && removeFromFollowing(usernameToBlock, username) && addToBlocking(username, usernameToBlock)
//    }

    def getFollowersForUser(String username) {
        def user = findUser username
        if (user) {
            def query = User.where {
                following*.username == username
            }
            query.list()
        } else {
            []
        }
    }

    def getBlockersForUser(String username) {
        def user = findUser username
        if (user) {
            def query = User.where {
                blocking*.username == username
            }
            query.list()
        } else {
            []
        }
    }

}

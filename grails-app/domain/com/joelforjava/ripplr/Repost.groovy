package com.joelforjava.ripplr

class Repost {

    // TODO - implement this. This is the 'repost' instance from which this Repost was spawned
    //      - e.g., A posts 'original', B reposts A. If C reposts from B, the B would be rippledFrom
    //        and A would be original. If C reposts from A, then A is both rippledFrom and original
    // Ripple rippledFrom

    static belongsTo = [ original: Ripple, user: User ]

    static constraints = {
    }
}

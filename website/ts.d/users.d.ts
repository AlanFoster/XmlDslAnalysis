/**
 * Represents a user within the database
 */
interface IUser {
    /**
     * A unique id associated with the user
     */
    identity: string

    /**
     * Whether or not the user has been verified
     */
    verified: boolean

    /**
     * Determines whether or not the user should be granted admin
     * rights within the system
     */
    isAdmin: boolean

    /**
     * Contains additional misc details related to this user
     */
    profile?: any
}

/**
 * Represents a feature definition
 */
interface IFeature {
    /**
     * The title of the feature
     */
    title: string

    /**
     * The creation date of the feature since epoch.
     * This will be used as a crude mechanism for sorting features,
     * as sequential IDs are not scalable across multi-node environments
     */
    date: number

    /**
     * The images associated with the feature
     */
    images: Image[]

    /**
     * The list of tags assocaited with the feature
     */
    tags: string[]
}

/**
 * Represents the details of an associated image
 */
interface Image {
    /**
     * Remembers the title of the image
     */
    title?: string

    /**
     * Represents the data associated with the Image,
     * IE This may be data:... or http:...
     */
    location: string

    /**
     * A description associated with the image
     */
    description?: string
}
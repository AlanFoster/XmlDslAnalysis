/**
 * Represents a feature definition
 */
interface IFeature {
    title: string
    images: Image[]
    tags: string[]
}

interface ITag {
    id: number
    text: string
}

/**
 * Represents the details of an associated image
 */
interface Image {
    title: string
    location: string
    description?: string
}
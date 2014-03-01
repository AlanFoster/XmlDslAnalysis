/**
 * Represents the base interface of a generic Data Access Object.
 */
export interface IRepository<T> {
    /**
     * Adds an element to the repository
     * @param elem The element of type Td
     */
    insert(elem:T)

    /**
     * Attempts to update the element within the repository
     * @param elem The element to update
     */
    update(elem:T)

    /**
     * Attempts to remove the element from within the repository
     * @param elem The elemnet to remove
     */
    remove(elem: T)

    /**
     * Attempts to find the element which matches the given id
     * within the repository
     *
     * @param id The unique id of the element to be found
     */
    find(id:Number): T

    /**
     * Returns all records within the current database.
     */
    all(): T[]
}

/**
 * Represents a generic feature repository which provides additional
 * methods to the base IRepository implementation
 */
export interface IFeatureRepository extends IRepository<IFeature> {
    /**
     * Gets all unique tags associated with features within
     * this repository
     */
    getTags(): string[]
}

export class MongoDbRepository<T> implements IRepository<T> {
    /**
     * Represents access to a MongoDb collection
     * No static typing available.
     */
    private db: any;

    /**
     * Represents the collection name that this repository has access to
     */
    private collectionName: String;

    /**
     * Creates a new instance of a MongoDb Repository with the
     * given db access
     * @param db
     * @param collectionName
     */
    constructor(db, collectionName){
        this.db = db;
        this.collectionName = collectionName;
    }

    /**
     * {@inheritdoc}
     */
    insert(elem:T) {
        this.collection().insert(elem)
    }

    /**
     * {@inheritdoc}
     */
    update(elem:T) {
        // noop
    }

    /**
     * {@inheritdoc}
     */
    remove(elem:T) {
        this.collection().remove({ _id: (<any> elem).id });
    }

    /**
     * {@inheritdoc}
     */
    find(id:Number):T {
        return this.collection().find({ _id: id })
    }

    /**
     * {@inheritdoc}
     */
    all():T[] {
        return this.collection().find();
    }

    /**
     * Provides access to the collection associated with this repository
     * @returns The collection associated with this repository
     */
    // TypeScript does not support the notation of 'protected'
    collection(): any {
        return this.db.get(this.collectionName);
    }
}

/**
 * Provides a concrete implementation of the IFeatureRepository interface.
 */
export class MongoDbFeatureRepository extends MongoDbRepository<IFeature> implements IFeatureRepository {
    /**
     * {@inheritdoc}
     */
    getTags():string[] {
        //this.collection().
        return undefined;
    }
}

/**
 * Provides a concrete implementation of the IRepository interface for users
 */
export class MongodbUserRepository extends MongoDbRepository<IUser> {
    /**
     * Creates a new instance of a MongoDb Repository with the
     * given db access
     * @param db
     */
    constructor(db: any) {
        super(db, "users")
    }
}

/**
 * Provides a concrete implementation of the IRepository interface for features
 */
export class MongodbFeatureRepository extends MongoDbRepository<IFeature> {
    /**
     * Creates a new instance of a MongoDb Repository with the
     * given db access
     * @param db
     */
        constructor(db: any) {
        super(db, "features")
    }
}
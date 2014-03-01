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
    find(id:Number): IPromise<T>

    /**
     * Returns all records within the current database as a promise.
     */
    all(): IPromise<T[]>
}

export interface IPromise<T> {
    success(element: T)
    error(error: Error)
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
    getTags(): IPromise<string[]>
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
    collectionName: String;

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
    find(id:Number):IPromise<T> {
        return this.collection().find({ _id: id })
    }

    /**
     * {@inheritdoc}
     */
    all():IPromise<T[]> {
        //return this.collection().find({});
        return this.db.get("features").find()
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

var Promise = require("monk").Promise;

/**
 * Provides a concrete implementation of the IFeatureRepository interface.
 */
export class MongoDbFeatureRepository extends MongoDbRepository<IFeature> implements IFeatureRepository {
    /**
     * Creates a new instance of a MongoDb Repository with the
     * given db access
     * @param db
     */
        constructor(db: any) {
        super(db, "features")
    }

    /**
     * {@inheritdoc}
     */
    getTags():IPromise<string[]> {
        // Monk doesn't support a wrapper for 'distinct' - this implements it instead
        var collection = this.collection();
        var promise = new Promise(collection, "distinct");

        /**
         * Similarly accessible via
         *         this.collection().driver.db
         *               .collection(this.collectionName)
         */
        this.collection()
            .col
            .distinct("tags", {}, promise.fulfill)

        return promise;
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
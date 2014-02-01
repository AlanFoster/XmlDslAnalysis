/**
 * Provides a shim for JavaScript's arrays
 */

/**
 * The Array interface, where T is the type of object encapsulated within the array
 */
interface Array<T> {
    /**
     * Returns a distinct array of values
     */
    distinct(): Array<T>

    /**
     * Finds the first value T within an array of values which meets the given predicate
     */
    find(f: (T) => boolean): T
}

/**
 * Returns a distinct set of values
 * @returns {Array}
 */
Array.prototype.distinct = Array.prototype.distinct || function() {
    var distinctList = [];
    // Naive implementation - IE we don't mergesort to remove duplicates etc
    for(var i = 0, length = this.length; i < length; i++) {
        var elem = this[i];
        if(distinctList.indexOf(elem) === -1) {
            distinctList.push(elem)
        }
    }
    return distinctList;
};

/**
 * Finds the first value T within an array of values which meets the given predicate
 * @returns {T}
 */
Array.prototype.find = Array.prototype.find || function<T>(pred: (T) => boolean) {
    for(var i = 0, length = this.length; i < length; i++) {
        var elem = <T> this[i];
        if(pred(elem)) {
            return elem;
        }
    }
    return undefined;
};
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
}

/**
 * Returns a distinct set of values
 * @returns {Array}
 */
Array.prototype.distinct = function() {
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
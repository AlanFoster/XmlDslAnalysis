package foo.factory;

import foo.models.PersonModel;

/**
 * Represents a person factory
 */
public class PersonFactory {
    /**
     * Contains the type information within the result type of the
     * method signature.
     * @return A person instance
     */
    public PersonModel createPerson() {
        return new PersonModel();
    }
}

package foo.features;

import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import foo.language.psi.impl.ElementSplitter$;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import java.util.List;

import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

/**
 * Defines the step definitions for the PakageSplitter feature
 */
public class PackageSplitterStepDefinitions {
    /**
     * The input string name
     */
    private String inputName;
    /**
     * The actual result
     */
    private List<SplitPackageTuple> result;

    @Given("^the following fully qualified class name '(.*)'$")
    public void the_following_fully_qualified_class_name_input_string_(String inputName) throws Throwable {
        this.inputName = inputName;
    }

    @When("^I calculate the reference boundaries$")
    public void I_calculate_the_reference_boundaries() throws Throwable {
        result = PackageSplitterHelper$.MODULE$.convertTupleToSplitPackageTuple(
                ElementSplitter$.MODULE$.split(inputName)
        );
    }

    @Then("^the boundaries should be$")
    public void the_boundaries_should_be(List<SplitPackageTuple> expectedTupleList) throws Throwable {
        assertReflectionEquals("The tuples should be as expected",
                expectedTupleList, result, ReflectionComparatorMode.LENIENT_ORDER);
    }
}

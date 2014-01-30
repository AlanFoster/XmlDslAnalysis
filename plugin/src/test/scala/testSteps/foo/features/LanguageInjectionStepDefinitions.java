package foo.features;

import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import foo.language.CamelSimpleLanguageInjector;
import org.junit.Assert;

/**
 * Represents the test step definitions for the language injection functionality
 */
public class LanguageInjectionStepDefinitions {
    /**
     * Represents the current xml being tested
     */
    private String exampleXml;

    /**
     * Represents the child's text value which will be injected
     */
    private String childText;

    /**
     * The calculated injection range within the element, relative to the XmlText injection host
     */
    private int injectionRange;

    @Given("^the following xml reference '(.*?)'$")
    public void the_following_xml_reference_example_xml_(String exampleXml) throws Throwable {
        this.exampleXml = exampleXml;
    }

    @And("^a child with the following text '(.*?)'$")
    public void a_child_with_the_following_text(String childText) throws Throwable {
        this.childText = childText;
    }

    @When("^I calculate the injection range$")
    public void I_calculate_the_injection_range() throws Throwable {
        injectionRange = new CamelSimpleLanguageInjector().calculateInjectionLength(exampleXml, childText);
    }

    @Then("^the answer should be '(.*?)'$")
    public void the_answer_should_be_example_range_(int expected) throws Throwable {
        Assert.assertEquals("the expected injection range should equal the expected", expected, injectionRange);
    }



}

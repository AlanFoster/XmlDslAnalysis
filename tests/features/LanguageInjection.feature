Feature: Language Injection

  Scenario Outline: A well formed simple expression is used within the DOM
    Given the following xml reference '<example_xml>'
    And a child with the following text '<example_childText>'
    When I calculate the injection range
    Then the answer should be '<example_range>'

  Examples:
    | example_xml                   | example_childText | example_range |
    | <simple>${body}</simple>      | ${body}           | 7             |
    | <simple></simple>             |                   | 0             |
    | <simple>${body} > 10</simple> | ${body} > 10      | 12            |


  Scenario Outline: Malformed XML text should successfully be handled
    Given the following xml reference '<example_xml>'
    And a child with the following text '<example_childText>'
    When I calculate the injection range
    Then the answer should be '<example_range>'

  Examples:
    | example_xml                             | example_childText | example_range |
    | <simple>${body} < 10 </simple>          |                   | 8             |
    | <simple>${body} <><><><<<><>< </simple> |                   | 8             |
    | <simple>></simple>                      |                   | 1             |

  Scenario Outline: Malformed XML which injects the wrong element should be handled with 0 text range
    Given the following xml reference '<example_xml>'
    And a child with the following text '<example_childText>'
    When I calculate the injection range
    Then the answer should be '<example_range>'

  Examples:
    | example_xml                             | example_childText | example_range |
    | <simple>${body} < 10 </simple>          | 10                | 0             |
    | <simple>${body} <><><><<<><>< </simple> |                   | 0             |


  Scenario Outline: Entity Encoding should be taken into consideration
    Given the following xml reference '<example_xml>'
    And a child with the following text '<example_childText>'
    When I calculate the injection range
    Then the answer should be '<example_range>'

  Examples:
    | example_xml                                      | example_childText            | example_range |
    | <simple>${body} &lt; 10</simple>                 | ${body} < 10                 | 15            |
    | <simple>${body} &lt;= 10</simple>                | ${body} <= 10                | 16            |
    | <simple>${body} &lt; 10 && ${body} < 5</simple>  | ${body} < 10 && ${body} < 5  | 27            |
    | <simple>>${body} &lt; 10 && ${body} < 5</simple> | >${body} < 10 && ${body} < 5 | 28            |

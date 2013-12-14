Feature: Splitting a package fully qualified class name

  Scenario: Successfully splitting a single package reference
    Given the following fully qualified class name 'java'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token | Start | End |
      | java  | 0     | 4   |

  Scenario: Successfully splitting a fully qualified class name
    Given the following fully qualified class name 'java.lang.String'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token  | Start | End |
      | java   | 0     | 4   |
      | lang   | 5     | 9   |
      | String | 10    | 16  |

  Scenario: Successfully extracting a variable with a trailing dot
    Given the following fully qualified class name 'java.'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token | Start | End |
      | java  | 0     | 4   |

  Scenario: Ignoring blank names
    Given the following fully qualified class name 'java...lang'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token | Start | End |
      | java  | 0     | 4   |
      | lang  | 7     | 11  |

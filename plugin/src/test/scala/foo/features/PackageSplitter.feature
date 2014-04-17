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

  Scenario: Elvis operator support
    Given the following fully qualified class name 'java?.lang?.String'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token  | Start | End  |
      | java   | 0     | 4    |
      | lang   | 6     | 10   |
      | String | 12    | 18   |

  Scenario: Array Access
    Given the following fully qualified class name 'body.array[0].getArray[0]'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token    | Start | End  |
      | body     | 0     | 4    |
      | array    | 5     | 10   |
      | getArray | 14    | 22   |

  Scenario: Array Access Trailing brace
    Given the following fully qualified class name 'body.array['
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token    | Start | End  |
      | body     | 0     | 4    |
      | array    | 5     | 10   |

  Scenario: Array Access Trailing brace with intermediate code
    Given the following fully qualified class name 'body.array[0.getArray[0]'
    When I calculate the reference boundaries
    Then the boundaries should be
      | Token    | Start | End  |
      | body     | 0     | 4    |
      | array    | 5     | 10   |
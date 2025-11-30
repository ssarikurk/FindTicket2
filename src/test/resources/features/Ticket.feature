@webApp
Feature: Skyscanner Feature

  Scenario: Skyscanner Ticket Search Tests
    When Scenario Started "Skyscanner Ticket Search"
#    Given Navigate to "skyscanner"
    #And pass human check if exists
#    Then search for flights from "Esenboğa" to "Dusseldorf"
#    And select departure date as "23.05.2026"
    And select from "DUS" to "ESB" departure date as "23.05.2026"
    Then collect flight list

  Scenario Outline: Skyscanner Ticket Search with Data Table
    When Scenario Started "Skyscanner Ticket Search with Data Table"
    And select from "<From>" to "<To>" departure date as "<FlightDate>"
    Then collect flight list
    Examples:
      | From | To  | FlightDate |
      | DUS  | ESB | 23.05.2026 |
      | ESB  | FRA | 23.05.2026 |
      | ESB  | DUS | 23.05.2026 |
      | ESB  | CGN | 23.05.2026 |


  Scenario: Ticket Search Tests with Flight list
    When Scenario Started "Ticket Search Tests with Flight list"
    Then read search data from csv "src/test/resources/files/flightInfoList.csv"
    And search for each flight in flight list


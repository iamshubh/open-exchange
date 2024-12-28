This application fetches data from the open exchange rates service.

High level architecture:

1. app: Main entry point to the application
2. core: The innermost business logic. It contains code to be used by all the feature and app module
3. feature: contains high level implementation for the open exchange feature

App refreshes open exchange conversion rate data after 30 mins.


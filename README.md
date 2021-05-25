# Transact Country Flag app
## Design Decisions
* MVVM is used for managing the UI architecture
* The Repository pattern is used for managing data and separating the data layer from the Application layer
* Use case objects are used to mediate between the repository layer and the view layer
* Dependency injection is used to manage dependencies and to help make the code testable

## UI Design
The UI has been split into 2 Fragments accessible from two tabs Search and Saved. This is is because these two requirements seemed to imply this
 *  As a user I should be able to search the Saved Country list using country name or country code from the saved list.
 *  As a user, if I enter a valid country code, I should be able to save the country code into a list of country codes  if the flag preview is available.
 
 I took this to mean that the user would need to manual save the FlagData so we save individual items on the "Search" fragment but can review the saved items on the "Saved" fragment.
 
 ## Saved Fragment
 * The "Saved Fragment" listens for updates to the database and updates the data as soon as the number of saved database items changes.
 * The Saved Fragment can then give a clear picture of what has been successfully saved to the db
 * The Saved fragment can filter the saved images for an individual item by country code or country name

## Search Fragment
* The Search Fragment allows the users to query the remote url https://www.countryflags.io/  for individual country flags. The flat flag style is the only one currently supported
* The Search fragment only allows queries by country code as per requirement:
 "As a user, I should able to launch the app and enter a 2 letter country code to retrieve the relevant country flag (64px size)"
* The Search fragment satifies the requirement: "As a user, if I enter an invalid or unavailable country code,  I should get an error dialog" by using a Toast alert as opposed to using a modal dialog.
* Searching for a new item clears the list on the Search Fragment
* Flag Images are saved to the local cache when querying for flags.

 ## Database Design:
 * The database uses a simple two table design
 * The tables are Country and Flag.
 * The Country table is prepopulated with the ISO 3166-1 alpha-2 country codes(PK) and equivalent countries. This allows us to do local validations. It was also intenfed to use these to propose codes and languages in the search text views but I didn't get to implementing that
 * The flag table has two fields, country_code(PK) and image_path (the location of locally cached flags). 
 


## Technology choices
* Image Handling: Coil is used for image loading as it's a native kotlin library that takes advantage of extension functions. The performance is equivalent to Picasso or Glide
* Network Requests: Network requests are handled by retrofit including requests for images. This is to standardize the app on one technique and to allow us to test the implementation.
* Room is used for database handling as it simplifies database interactions and allows for clean code.
* Hilt is used for dependency injection. This is an opinionated wrapper around dagger that reduces the amount of boilerplate associated with setting up DI.
* View binding is used in the app for binding to individual views. This eliminates the need for calls to findViewById
* The Jetpack support for MVVM,LiveData,Kotlin Coroutines and Flow are used to support a relatively modern architecture
* Kotlin coroutines and Flow are used instead of Rx as they are native platform implementations now and achieve parity with the most common Rx use cases
* MockK is used to 


## Issues
* Not all classes are tested. The viewmodels and the FetchFlagUseCase have tests. The remaining classes don't have tests due to time constraints
* The Saved Fragment does not have validation on the country code or country (again due to time constraints). The counties dependency was included as an argument to the SavedFlagViewModel with a view towards implementing this
* Table Design. The Flag table does not include the country name field - This was done to basically demonstrate the limitations of Room and to show how one would map values in the Flag table via the country_code field. In retrospect this was not really necessary and is not particularly efficient
* The mechanism on the SavedFragment to listen for updates is a bit hacky. It listens to LiveData backed by a Kotlin flow. The flow listens only for a change in the count of saved flags. A better implementation would have been to use that actual flow to update the data on the Saved Fragment in combination with a Flags table that included all necessary fields - that would have been more efficient and cut down on some code
* The app only supports portrait mode currently (again due to time constraints) 
* The UI design could so with some work as it does not look very pretty - it really is more of a testbed for the requirements.
* Dimensions and styles have not been extracted to separate files. With more time, I would have cleanded that up.

## Video
![App Video](project_demo.mp4)

 


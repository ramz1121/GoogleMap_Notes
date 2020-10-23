# Google Map Notes 

A simple application used to add notes to users current location. It also provides an option for them to search the notes
and users.

* MVVM architecture is used along with the repository layer
* Base classes(Base Activity,Base ViewModel, Base Adapter, BaseItemViewHolder, BaseItemViewModel) layer added to make the code more concise.
* Firebase Authentication and Database is used for user authentication and storage.
* Google Maps is used for showing the user location and marker with notes.
* RxJava is used for asynchronous call, provided with Observables, Schedulers.
* Coroutines for suspended execution.
* Dagger 2 is used for dependency injection.
* Shared preferences are used to store user data locally.
* Mockito and Junit are used for Unit testing.

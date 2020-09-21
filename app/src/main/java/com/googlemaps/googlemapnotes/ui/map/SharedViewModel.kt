package com.googlemaps.googlemapnotes.ui.map

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.googlemaps.googlemapnotes.data.local.prefs.UserPreferences
import com.googlemaps.googlemapnotes.data.model.LocationModel
import com.googlemaps.googlemapnotes.data.model.Notes
import com.googlemaps.googlemapnotes.data.repository.FirebaseNotesRepository
import com.googlemaps.googlemapnotes.data.repository.FirebaseRepository
import com.googlemaps.googlemapnotes.ui.base.BaseViewModel
import com.googlemaps.googlemapnotes.utils.common.Event
import com.googlemaps.googlemapnotes.utils.network.NetworkHelper
import com.googlemaps.googlemapnotes.utils.network.NetworkResult
import com.googlemaps.googlemapnotes.utils.rx.SchedulerProvider
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.CompositeDisposable
import kotlinx.coroutines.*
import java.lang.IllegalArgumentException
import java.util.concurrent.Executors
import kotlin.coroutines.coroutineContext


class SharedViewModel(
    schedulerProvider: SchedulerProvider,
    compositeDisposable: CompositeDisposable,
    networkHelper: NetworkHelper,
    private val repository: FirebaseRepository,
    private val notesRepository: FirebaseNotesRepository,
    private val userPreferences: UserPreferences
) : BaseViewModel(schedulerProvider, compositeDisposable, networkHelper) {
    val currentLocation = MutableLiveData<String>()
    val locationLatLong = MutableLiveData<LocationModel>()

    fun data(item: String) {
        currentLocation.value = item
    }

    override fun onCreate() {

    }

    val notesField: MutableLiveData<String> = MutableLiveData()
    val latLongField: MutableLiveData<LocationModel> = MutableLiveData()
    val noteSaving: MutableLiveData<Boolean> = MutableLiveData()
    val getNoteSaved: MutableLiveData<Notes> = MutableLiveData()
    val getNoteList: MutableLiveData<MutableList<Notes>> = MutableLiveData()
    val clearNoteText: MutableLiveData<Event<String>> = MutableLiveData()
    val userError: MutableLiveData<String> = MutableLiveData()

    val statusMessage: LiveData<Event<String>>
        get() = clearNoteText

    fun onNotesChange(notes: String) = notesField.postValue(notes)
    fun onLatlongChange(locationModel: LocationModel) = latLongField.postValue(locationModel)

    fun addNote() {
        val note = notesField.value
        val latlong = latLongField.value
        val userID = userPreferences.getUserId()
        if (checkInternetConnectionWithMessage() && note != null && latlong != null && userPreferences.getUserId() != null) {
            noteSaving.postValue(true)

            compositeDisposable.addAll(
                notesRepository.addNotes(
                    Notes(
                        latlong.latitude,
                        latlong.longitude,
                        note,
                        userPreferences.getUserId()
                    )
                )
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {
                            clearNoteText.postValue(Event("Note Saved Successfully"))
                            noteSaving.postValue(false)
                            getNoteSaved.postValue(
                                Notes(
                                    latlong.latitude,
                                    latlong.longitude,
                                    note,
                                    userID
                                )
                            )
                        },
                        {
                            noteSaving.postValue(false)
                            handleNetworkError(it)
                        }

                    )

            )

        }
    }

    fun signout() {
        if (checkInternetConnectionWithMessage()) {
            compositeDisposable.addAll(
                repository.logout()
                    .subscribeOn(schedulerProvider.io())
                    .subscribe(
                        {

                        }, {
                            handleNetworkError(it)
                        }
                    )
            )

        }
    }

    /****************************************************Coroutines*******************************************************/
    /****It can execute a few lines of functionA and then execute a few lines of functionB and then again a few lines of functionA and so on.
    This will be helpful when a thread is sitting idle not doing anything, in that case, it can execute a few lines of another function.
    This way, it can take the full advantage of thread. Ultimately the cooperation helps in multitasking.

    So, we can say that Coroutines and the threads both are multitasking. But the difference is that threads are managed by the OS and coroutines by the users

    Dispatchers: Dispatchers help coroutines in deciding the thread on which the work has to be done.
    There are majorly three types of Dispatchers which are as IO, Default, and Main.
    IO dispatcher is used to do the network and disk-related work.
    Default is used to do the CPU intensive work.
    Main is the UI thread of Android.
    In order to use these, we need to wrap the work under the async function. Async function looks like below.
    suspend fun async() // implementation removed for brevity
    suspend: Suspend function is a function that could be started, paused, and resume.

    launch: fire and forget
    async: perform a task and return a result
    .await : wait for results
    withContext :another way of writing the async where we do not have to write await() --it runs in series instead of parallel.
    Coroutine job:is created with launch coroutine builder

    Scopes in Kotlin Coroutines
    Scopes in Kotlin Coroutines are very useful because we need to cancel the background task as soon as the activity is destroyed

    Example:
    GlobalScope.launch(Dispatchers.Main) {
    val userOne = async(Dispatchers.IO) { fetchFirstUser() }
    val userTwo = async(Dispatchers.IO) { fetchSecondUser() }
    showUsers(userOne.await(), userTwo.await()) // back on UI thread

    open class AppExecutors(
    val ioContext: CoroutineContext = Dispatchers.Default,
    val networkContext: CoroutineContext = Executors.newFixedThreadPool(NETWORK_THREAD_POOL).asCoroutineDispatcher(),
    val uiContext: CoroutineContext = Dispatchers.Main
    )
    }*/

    fun getNoteList(defaultUserName: String) {
        val myScope = CoroutineScope(Dispatchers.Main)
        if (checkInternetConnection())
            myScope.launch(Dispatchers.Main) {
                var notesList: MutableList<Notes> = mutableListOf()
                val notes =
                    notesRepository.getNotes { replaceNoteIdToName(it, defaultUserName) }
                when (notes) {
                    is NetworkResult.Error -> {
                        // userError.postValue("no items found")
                    }
                    is NetworkResult.Success -> {
                        notes.data.forEach {
                            notesList.add(it)
                            getNoteList.postValue(notesList)

                        }
                    }
                }
            }
    }

    fun replaceNoteIdToName(note: Notes, defaultUserName: String): Job = runBlocking {
        launch {
            val userName = repository.getUserReadableName(note.user!!)
            if (userName is NetworkResult.Success) {
                note.user = userName.data
            } else {
                note.user = defaultUserName
            }
        }
    }
/*RxSearchView.queryTextChanges(searchView)
        .debounce(1, TimeUnit.SECONDS)
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe(new Consumer<CharSequence>() {
            @Override
            public void accept(@NonNull CharSequence charSequence) throws Exception {
                    // do something
            }
        });*/

    /*internal class DebouncingQueryTextListener(
    private val onDebouncingQueryTextChange: (String?) -> Unit
) : SearchView.OnQueryTextListener {
    var debouncePeriod: Long = 500

    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.Main)

    private var searchJob: Job? = null

    override fun onQueryTextSubmit(query: String?): Boolean {
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        searchJob?.cancel()
        searchJob = coroutineScope.launch {
            newText?.let {
                delay(debouncePeriod)
                onDebouncingQueryTextChange(newText)
            }
        }
        return false
    }
}*/

fun searchNotes(text: String, categoryPosition: Int, defaultUserName: String) {
        val notesSearchCategory = 0
        val usersSearchCategory = 1
        val myScope = CoroutineScope(Dispatchers.Main)
        var notesList: MutableList<Notes> = mutableListOf()
        if (checkInternetConnection())
            when (categoryPosition) {
                notesSearchCategory -> {
                    myScope.launch(Dispatchers.Main) {
                        val notes = notesRepository.searchNotesByNote(text) {
                            replaceNoteIdToName(
                                it,
                                defaultUserName
                            )
                        }
                        when (notes) {
                            is NetworkResult.Error -> {
                                userError.postValue("no items found")
                            }
                            is NetworkResult.Success -> {
                                notes.data.forEach {
                                    notesList.add(it)
                                    getNoteList.postValue(notesList)
                                }
                            }
                        }
                    }
                }
                usersSearchCategory -> {
                    myScope.launch(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
                        withContext(Executors.newFixedThreadPool(3).asCoroutineDispatcher()) {
                            val userId = repository.getUserIdFromActualName(text)
                            if (userId is NetworkResult.Success) {
                                val notes = notesRepository.searchNotesByUser(userId.data, text)
                                if (notes is NetworkResult.Success) {
                                    myScope.launch(Dispatchers.Main) {
                                        notes.data.forEach {

                                            notesList.add(it)
                                            getNoteList.postValue(notesList)
                                        }
                                    }
                                } else {
                                    myScope.launch(Dispatchers.Main) {
                                        userError.postValue("no items found")
                                    }
                                }
                            } else {
                                myScope.launch(Dispatchers.Main) {
                                    userError.postValue("no items found")
                                }
                            }
                        }
                    }
                }
                else -> throw IllegalArgumentException("Error")
            }
    }


}
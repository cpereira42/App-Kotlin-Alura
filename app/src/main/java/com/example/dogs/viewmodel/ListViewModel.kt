package com.example.dogs.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogDatabase
import com.example.dogs.model.DogsApiService
import com.example.dogs.util.NotificationHelper
import com.example.dogs.util.SharedPreferencesHelper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.launch
import java.lang.NumberFormatException

class ListViewModel(application: Application) : BaseViewModel(application) {

    private var prefHelper = SharedPreferencesHelper(getApplication())
    private var refreshTime = 5 * 60 * 1000 * 1000 * 1000L // 5 minutos em nanoseconds

    private val dogService = DogsApiService()
    private val disposable = CompositeDisposable()

    val dogs = MutableLiveData<List<DogBreed>>() // carrega a lista Dogbreed
    val dogsLoadError = MutableLiveData<Boolean>() // apenas para mostrar se houve erro
    val loading = MutableLiveData<Boolean>() // apenas para mostrar que está pronto

    fun refresh(){
        checckCacheDuration() // aula 59
        val updateTime : Long? = prefHelper.getUpdateTime()
        if (updateTime != null && updateTime != 0L && System.nanoTime() - updateTime < refreshTime){
            fetchFromDatabase()
        } else {
            fetchFromRemote()
        }


        /*val dog1 = DogBreed("1","Corgus","15 years", "breedGroup","bredfor", "temperament","")
        val dog2 = DogBreed("2","Labrador","10 years", "breedGroup","bredfor", "temperament","")
        val dog3 = DogBreed("3","Rotweiller","20 years", "breedGroup","bredfor", "temperament","")
        val dogList : ArrayList<DogBreed> = arrayListOf<DogBreed>(dog1, dog2, dog3)

        dogs.value =  dogList
        dogsLoadError.value = false // estamos forçando para false, pois a informação já está correta
        loading.value = false // estamos forçando para false, pois a informação já carregada*/

    }
    // aula 59
    private fun checckCacheDuration (){
        val cachePreference = prefHelper.getCacheDuration()
        try{
            val cachePreferenceInt = cachePreference?.toInt() ?: 5 * 60
            refreshTime= cachePreferenceInt.times(1000 * 1000 * 1000L)
        } catch (e:NumberFormatException){
            e.printStackTrace()
        }

    }

    fun refreshBypassCache(){
        fetchFromRemote()
    }

    private fun fetchFromDatabase(){
        loading.value = true
        // lauch para ocorrer em subrotina ( coroutine)
       launch {
            val dogs : List<DogBreed> = DogDatabase(getApplication()).dogDao().getAllDogs()
            dogsRetrieved((dogs))
            Toast.makeText(getApplication(), "Dogs retrieved from database",Toast.LENGTH_SHORT).show()
        }
    }

    private fun fetchFromRemote() {
        loading.value = true
        disposable.add(
            dogService.getDogs()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<List<DogBreed>>(){
                    override fun onSuccess(dogList: List<DogBreed>) {
                        storeDogsLocally(dogList)
                        Toast.makeText(getApplication(), "Dogs retrieved from endpoint",Toast.LENGTH_SHORT).show()
                        NotificationHelper(getApplication()).createNotification()
                    }

                    override fun onError(e: Throwable) {
                        dogsLoadError.value = true
                        loading.value = false
                        e.printStackTrace()
                    }

                })
        )

    }

    private fun dogsRetrieved(dogList: List<DogBreed>) {
        dogs.value = dogList
        dogsLoadError.value = false
        loading.value = false
    }

    private fun storeDogsLocally(list : List<DogBreed>){
        launch {
            val dao =  DogDatabase(getApplication()).dogDao()
            dao.deleteAllDogs() // apaga tudo do banco de dados para depois ree inscrever tudo
            val result = dao.insertAll(*list.toTypedArray()) // divide a list em elementos
            var i = 0
            while (i < list.size) {
                list[i].uuid = result[i].toInt()
                i++
            }
            dogsRetrieved(list)
        }
        prefHelper.saveUpdateTime(System.nanoTime() )
    }


    override fun onCleared() {
        super.onCleared()
        disposable.clear() // limpa para não ter problemas com memory leaks
    }




}
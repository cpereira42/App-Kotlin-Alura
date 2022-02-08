package com.example.dogs.viewmodel

import android.app.Application
import android.content.Context
import android.widget.Toast
import android.widget.Toast.makeText
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dogs.model.DogBreed
import com.example.dogs.model.DogDatabase
import kotlinx.coroutines.launch

class DetailViewModel (application: Application) : BaseViewModel(application){

    val dogLiveData = MutableLiveData<DogBreed>() // carrega a lista Dogbreed

    fun fetch(uuid : Int) {

        //val dog = DogBreed("1","Corgus","15 years", "breedGroup","bredfor", "temperament","")
        launch {
            val dog : DogBreed = DogDatabase(getApplication()).dogDao().getDog(uuid)
            dogLiveData.value = dog
        }
    }
}
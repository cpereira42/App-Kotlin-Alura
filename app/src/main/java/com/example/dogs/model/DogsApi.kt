package com.example.dogs.model

import io.reactivex.Single
import retrofit2.http.GET

interface DogsApi {

    @GET("DevTides/DogsApi/master/dogs.json")
    fun getDogs():Single<List<DogBreed>>
    //pode usar mais de um get?
    // pode usar o metodo post aqui também

}
package com.example.dogs.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface DogDao {
    @Insert // demostra que irá ter um insert no data base
    fun insertAll(vararg dogs: DogBreed) : List<Long> // o que vai inserir e vai retornar uma List Long

    @Query("SELECT * FROM dogbreed") // dogbreed é o nome da classe da nossa Model.kt, pois não definimos um nome da base
    fun getAllDogs(): List<DogBreed>

    @Query("SELECT * FROM dogbreed WHERE uuid =:dogId") // faz a busca de um Uid expecífico
    fun getDog(dogId: Int) : DogBreed //  dogId é int pois no Model.kt, a chave primaria (uuid) é Int

    @Query("DELETE FROM dogbreed") // deleta toda a base de dados
    fun deleteAllDogs()


}
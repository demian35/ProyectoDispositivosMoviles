package com.example.proyectodispositivosmoviles

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Producto::class], version = 1)
abstract class AppBasedeDatos : RoomDatabase() {

    abstract fun  productos() : ProductosDao

    companion object{
        @Volatile
        private var INSTANCE: AppBasedeDatos? = null

        fun getDataBase(context:Context) : AppBasedeDatos{
            val tempInstance = INSTANCE

            if(tempInstance!=null){
                return  tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppBasedeDatos::class.java,
                    "app_basededatos"
                ).build()
                INSTANCE=instance
                return instance
            }
        }
    }
}
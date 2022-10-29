package com.projects.android.ricettario.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.projects.android.ricettario.model.Ricetta
import com.projects.android.ricettario.model.RicettaFTS

@Database(entities = [Ricetta::class, RicettaFTS::class], version = 1)
@TypeConverters(Converters::class)
abstract class RicettarioDatabase : RoomDatabase() {

	abstract fun ricettarioDao(): RicettarioDao
}
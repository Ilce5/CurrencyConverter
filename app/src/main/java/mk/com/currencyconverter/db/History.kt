package mk.com.currencyconverter.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "history_table")
data class History(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "input") val input: String,
    @ColumnInfo(name = "result") val result: String,
    @ColumnInfo(name = "currency") val currency: String
)
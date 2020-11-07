package org.jdfossapps.android.shopwithmom.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import android.os.Parcelable
import android.os.Parcel

import java.util.Date

import androidx.room.TypeConverters

// Parcel extensions
fun Parcel.writeDate(date: Date?) {
    writeLong(date?.time ?: -1)
}

fun Parcel.readDate(): Date? {
    val long = readLong()
    return if (long != -1L) Date(long) else null
}
// End Parcel extensions

@Entity(tableName = "item_table")
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "name") var name: String,
    @ColumnInfo(name = "description") var description: String,
    @ColumnInfo(name = "unit_description") var unit_description: String,
    @ColumnInfo(name = "unit") var unit: Double,
    @ColumnInfo(name = "price") var price: Double,
    @ColumnInfo(name = "price_per_unit") var price_per_unit: Double,
    @ColumnInfo(name = "quantity") var quantity: Double,
    @ColumnInfo(name = "item_total") var item_total: Double,
    @ColumnInfo(name = "created_at") val created_at: Date?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readDate()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)             
        parcel.writeString(name)
        parcel.writeString(description)
        parcel.writeString(unit_description)
        parcel.writeDouble(unit)
        parcel.writeDouble(price)
        parcel.writeDouble(price_per_unit)
        parcel.writeDouble(quantity)
        parcel.writeDouble(item_total)
        parcel.writeDate(created_at)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Item> {
        override fun createFromParcel(parcel: Parcel): Item {
            return Item(parcel)
        }

        override fun newArray(size: Int): Array<Item?> {
            return arrayOfNulls(size)
        }
    }

}

package org.jdfossapps.android.shopwithmom.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

import android.os.Parcelable
import android.os.Parcel

@Entity(tableName = "compare_item_table")
data class Compare(
        @PrimaryKey(autoGenerate = true)
        @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "unit") var unit: Double,
        @ColumnInfo(name = "price") var price: Double,
        @ColumnInfo(name = "price_per_unit") var price_per_unit: Double
) : Parcelable {

        constructor(parcel: Parcel) : this(
                parcel.readInt(),
                parcel.readDouble(),
                parcel.readDouble(),
                parcel.readDouble()
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
                parcel.writeInt(id)             
                parcel.writeDouble(unit)
                parcel.writeDouble(price)
                parcel.writeDouble(price_per_unit)
        }

        override fun describeContents(): Int {
                return 0
            }
        
        companion object CREATOR : Parcelable.Creator<Compare> {
                override fun createFromParcel(parcel: Parcel): Compare {
                        return Compare(parcel)
                }

                override fun newArray(size: Int): Array<Compare?> {
                        return arrayOfNulls(size)
                }
        }
        

}
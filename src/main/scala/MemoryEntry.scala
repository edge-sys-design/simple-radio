package com.edgesysdesign.simpleradio

import _root_.android.content.{ContentValues, Context}
import _root_.android.database.Cursor
import _root_.android.database.sqlite.{SQLiteDatabase, SQLiteOpenHelper}
import _root_.android.view.{LayoutInflater, View, ViewGroup}
import _root_.android.widget.{BaseAdapter, LinearLayout, TextView}

import com.edgesysdesign.frequency.FrequencyImplicits._

import scala.collection.mutable.ArrayBuffer

/** A memory entry that can quickly switch the radio to predefined settings.
  *
  * @param id The unique ID for the memory entry.
  * @param label A name for the entry.
  * @param frequency The frequency, in hertz.
  * @param plTone The subaudible tone to send under the signal, if any.
  * @param shift One of Some("+"), Some("-"), or None.
  * @param offset How much to shift by, in kilohertz.
  */
case class MemoryEntry(
  val id: Long,
  val label: String,
  val frequency: Long,
  val mode: String,
  val plTone: Option[Float],
  val shift: Option[String],
  val offset: Option[Float]) {
  override val toString = s"$label (${frequency.toLong.Hz.MHz} MHz)"
}

object MemoryEntry {
  val DatabaseVersion = 1

  /** Construct a [[MemoryInstance]] object from a cursor. */
  def fromCursor(cursor: Cursor) = MemoryEntry(
    cursor.getLong(0), // id
    cursor.getString(1), // label
    cursor.getLong(2), // frequency
    cursor.getString(3), // mode
    Option(cursor.getFloat(4)),  // plTone
    Option(cursor.getString(5)), // shift
    Option(cursor.getFloat(6))) // offset
}

class MemoryEntryHelper(context: Context)
    extends SQLiteOpenHelper(context, "simpleradio", null, MemoryEntry.DatabaseVersion) {

  /** Create the table on a fresh install of the application.
    *
    * @param db The database to act on.
    */
  override def onCreate(db: SQLiteDatabase) {
    db.execSQL("""
      |CREATE TABLE memory_entries(
      |  _id integer primary key,
      |  label varchar(255) not null,
      |  frequency integer not null,
      |  mode varchar(10) not null,
      |  pl_tone float,
      |  shift varchar(1),
      |  offset float);
      |""".stripMargin)
  }

  /** Called for schema updates on application upgrades.
    *
    * We do a bit of a trick here so that we get something similar to
    * "migrations."
    *
    * @param db The database to act on.
    * @param oldVersion The current version of the schema.
    * @param newVersion The version of the schema we're upgrading to.
    */
  override def onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
    oldVersion to newVersion foreach { version =>
      version match {
        // Start at 2 since onCreate gives us version 1.
        case 2 => /* {
          db.execSQL(...)
          db.execSQL(...)
        }
        case 3 => ...
        */
      }
    }
  }
}

class MemoryEntryAdapter(val memories: ArrayBuffer[MemoryEntry], context: Context)
  extends BaseAdapter {
  def getCount = memories.length
  def getItem(position: Int) = memories(position)
  def getItemId(position: Int) = memories(position).id
  def getView(position: Int, convertView: View, parent: ViewGroup): View = {

    val memory = getItem(position)

    val layout = LayoutInflater.from(context)
      .inflate(R.layout.memory_entry, parent, false)
      .asInstanceOf[LinearLayout]

    // TODO: Find out how to treat this as typed instead of casting to get rid
    // of most of this ugliness.
    layout
      .findViewById(R.id.memory_frequency)
      .asInstanceOf[TextView]
      .setText(memory.frequency.toLong.Hz.MHz)
    layout
  }
}

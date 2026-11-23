package com.takto.launcher.core

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

/**
 * Information Architecture: Workspace -> Module -> Record hierarchy
 * Supports progressive complexity: Basic, Power, Builder tiers
 */

@Entity(tableName = "workspaces")
data class WorkspaceEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String,
    val iconName: String,
    val complexityTier: String // "BASIC", "POWER", "BUILDER"
)

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey val id: String,
    val workspaceId: String,
    val name: String,
    val viewType: String, // "LIST", "KANBAN", "CALENDAR", "TILES"
    val orderIndex: Int
)

@Entity(tableName = "records")
data class RecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val moduleId: String,
    val recordType: String, // "JOURNAL", "TASK", "EXPENSE", "NOTE"
    val content: String,
    val markdownBullet: String,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val syncedWithLogseq: Boolean = true
)

@Dao
interface RecordDao {
    @Query("SELECT * FROM records ORDER BY createdAt DESC")
    fun getAllRecords(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE recordType = 'TASK' AND isCompleted = 0 ORDER BY createdAt DESC")
    fun getActiveTasks(): Flow<List<RecordEntity>>

    @Query("SELECT * FROM records WHERE recordType = 'JOURNAL' ORDER BY createdAt DESC LIMIT 50")
    fun getInternalThreadEntries(): Flow<List<RecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecord(record: RecordEntity): Long

    @Query("UPDATE records SET isCompleted = :completed WHERE id = :id")
    suspend fun updateTaskStatus(id: Long, completed: Boolean)
}

@Database(entities = [WorkspaceEntity::class, ModuleEntity::class, RecordEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recordDao(): RecordDao
}

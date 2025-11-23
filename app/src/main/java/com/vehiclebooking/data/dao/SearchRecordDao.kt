package com.vehiclebooking.data.dao

import androidx.room.*
import com.vehiclebooking.data.model.SearchRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking

@Dao
interface SearchRecordDao {
    @Query("SELECT * FROM search_records ORDER BY timestamp DESC")
    fun getAllSearchRecords(): Flow<List<SearchRecordEntity>>

    @Query("SELECT * FROM search_records WHERE phoneNumber = :phoneNumber AND timestamp = :timestamp LIMIT 1")
    suspend fun getSearchRecord(phoneNumber: String, timestamp: String): SearchRecordEntity?

    @Query("SELECT * FROM search_records WHERE status = :status ORDER BY timestamp DESC")
    fun getSearchRecordsByStatus(status: String): Flow<List<SearchRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchRecord(record: SearchRecordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchRecords(records: List<SearchRecordEntity>)

    @Update
    suspend fun updateSearchRecord(record: SearchRecordEntity)

    @Delete
    suspend fun deleteSearchRecord(record: SearchRecordEntity)

    @Query("DELETE FROM search_records WHERE phoneNumber = :phoneNumber AND timestamp = :timestamp")
    suspend fun deleteSearchRecord(phoneNumber: String, timestamp: String)

    @Query("DELETE FROM search_records")
    suspend fun deleteAllSearchRecords()

    @Query("SELECT COUNT(*) FROM search_records")
    fun getSearchRecordCount(): Flow<Int>
    
    // Blocking wrappers for Java compatibility
    fun getAllSearchRecordsBlocking(): List<SearchRecordEntity> = runBlocking {
        getAllSearchRecords().first()
    }
    
    fun getSearchRecordBlocking(phoneNumber: String, timestamp: String): SearchRecordEntity? = runBlocking {
        getSearchRecord(phoneNumber, timestamp)
    }
    
    fun getSearchRecordsByStatusBlocking(status: String): List<SearchRecordEntity> = runBlocking {
        getSearchRecordsByStatus(status).first()
    }
    
    fun insertSearchRecordBlocking(record: SearchRecordEntity) = runBlocking {
        insertSearchRecord(record)
    }
    
    fun updateSearchRecordBlocking(record: SearchRecordEntity) = runBlocking {
        updateSearchRecord(record)
    }
    
    fun deleteAllSearchRecordsBlocking() = runBlocking {
        deleteAllSearchRecords()
    }
}

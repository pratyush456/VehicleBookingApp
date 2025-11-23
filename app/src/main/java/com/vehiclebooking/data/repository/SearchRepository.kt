package com.vehiclebooking.data.repository

import android.content.Context
import com.vehiclebooking.VehicleSearchActivity
import com.vehiclebooking.data.AppDatabase
import com.vehiclebooking.data.dao.SearchRecordDao
import com.vehiclebooking.data.model.SearchRecordEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchRepository(context: Context) {
    private val searchRecordDao: SearchRecordDao = AppDatabase.getDatabase(context).searchRecordDao()

    // Flow-based reactive queries
    val allSearchRecords: Flow<List<VehicleSearchActivity.SearchRecord>> =
        searchRecordDao.getAllSearchRecords()
            .map { entities -> entities.map { it.toSearchRecord() } }

    fun getSearchRecordsByStatus(status: String): Flow<List<VehicleSearchActivity.SearchRecord>> =
        searchRecordDao.getSearchRecordsByStatus(status)
            .map { entities -> entities.map { it.toSearchRecord() } }

    val searchRecordCount: Flow<Int> = searchRecordDao.getSearchRecordCount()

    // Suspend functions for write operations
    suspend fun getSearchRecord(phoneNumber: String, timestamp: String): VehicleSearchActivity.SearchRecord? {
        return searchRecordDao.getSearchRecord(phoneNumber, timestamp)?.toSearchRecord()
    }

    suspend fun insertSearchRecord(record: VehicleSearchActivity.SearchRecord) {
        searchRecordDao.insertSearchRecord(SearchRecordEntity(record))
    }

    suspend fun insertSearchRecords(records: List<VehicleSearchActivity.SearchRecord>) {
        searchRecordDao.insertSearchRecords(records.map { SearchRecordEntity(it) })
    }

    suspend fun updateSearchRecord(record: SearchRecordEntity) {
        searchRecordDao.updateSearchRecord(record)
    }

    suspend fun updateSearchStatus(phoneNumber: String, timestamp: String, newStatus: String) {
        val record = searchRecordDao.getSearchRecord(phoneNumber, timestamp)
        record?.let {
            it.status = newStatus
            searchRecordDao.updateSearchRecord(it)
        }
    }

    suspend fun updateAdminNotes(phoneNumber: String, timestamp: String, notes: String) {
        val record = searchRecordDao.getSearchRecord(phoneNumber, timestamp)
        record?.let {
            it.adminNotes = notes
            searchRecordDao.updateSearchRecord(it)
        }
    }

    suspend fun deleteSearchRecord(phoneNumber: String, timestamp: String) {
        searchRecordDao.deleteSearchRecord(phoneNumber, timestamp)
    }

    suspend fun deleteAllSearchRecords() {
        searchRecordDao.deleteAllSearchRecords()
    }
}

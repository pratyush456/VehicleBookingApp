package com.vehiclebooking.data.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import com.vehiclebooking.data.model.SearchRecordEntity;
import java.util.List;

@Dao
public interface SearchRecordDao {
    @Query("SELECT * FROM search_records")
    List<SearchRecordEntity> getAllSearchRecords();

    @Query("SELECT * FROM search_records WHERE phoneNumber = :phoneNumber AND timestamp = :timestamp LIMIT 1")
    SearchRecordEntity getSearchRecord(String phoneNumber, String timestamp);

    @Query("SELECT * FROM search_records WHERE status = :status")
    List<SearchRecordEntity> getSearchRecordsByStatus(String status);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchRecord(SearchRecordEntity record);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSearchRecords(List<SearchRecordEntity> records);

    @Update
    void updateSearchRecord(SearchRecordEntity record);

    @Delete
    void deleteSearchRecord(SearchRecordEntity record);

    @Query("DELETE FROM search_records WHERE phoneNumber = :phoneNumber AND timestamp = :timestamp")
    void deleteSearchRecord(String phoneNumber, String timestamp);

    @Query("DELETE FROM search_records")
    void deleteAllSearchRecords();
}

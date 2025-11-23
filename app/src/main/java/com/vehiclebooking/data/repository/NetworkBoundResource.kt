package com.vehiclebooking.data.repository

import kotlinx.coroutines.flow.*

/**
 * A generic class that implements the offline-first pattern.
 * It loads data from the local database first, then fetches from network if needed.
 * 
 * @param ResultType Type for the Resource data (domain model)
 * @param RequestType Type for the API response (DTO)
 */
abstract class NetworkBoundResource<ResultType, RequestType> {

    fun asFlow(): Flow<Resource<ResultType>> = flow {
        // Emit Loading state
        emit(Resource.Loading())

        // Load data from local database
        val dbData = loadFromDb().first()
        
        // Emit Loading with cached data
        emit(Resource.Loading(dbData))

        // Check if we should fetch from network
        if (shouldFetch(dbData)) {
            try {
                // Fetch from network
                val apiResponse = fetchFromNetwork()
                
                // Save network result to database
                saveNetworkResult(apiResponse)
                
                // Load fresh data from database and emit
                val freshData = loadFromDb().first()
                emit(Resource.Success(freshData))
                
            } catch (throwable: Throwable) {
                // Emit error with cached data
                val errorMessage = throwable.message ?: "Unknown error occurred"
                emit(Resource.Error(errorMessage, dbData))
            }
        } else {
            // Just emit the cached data as success
            emit(Resource.Success(dbData))
        }
    }

    /**
     * Load data from local database
     */
    protected abstract suspend fun loadFromDb(): Flow<ResultType>

    /**
     * Fetch data from network
     */
    protected abstract suspend fun fetchFromNetwork(): RequestType

    /**
     * Save network result to database
     */
    protected abstract suspend fun saveNetworkResult(data: RequestType)

    /**
     * Determine whether to fetch from network
     * @param data Current data from database
     * @return true if should fetch from network, false otherwise
     */
    protected abstract fun shouldFetch(data: ResultType?): Boolean
}

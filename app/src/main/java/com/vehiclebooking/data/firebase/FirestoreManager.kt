package com.vehiclebooking.data.firebase

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.vehiclebooking.data.model.dto.BookingDto
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Firebase Firestore manager for real-time sync
 * Handles uploading and listening to booking changes
 */
class FirestoreManager {
    
    private val db: FirebaseFirestore = Firebase.firestore
    private val bookingsCollection = db.collection("bookings")
    
    /**
     * Upload a booking to Firestore
     */
    suspend fun uploadBooking(booking: BookingDto) {
        try {
            bookingsCollection
                .document(booking.bookingId)
                .set(booking)
                .await()
        } catch (e: Exception) {
            // Log error but don't crash
            e.printStackTrace()
        }
    }
    
    /**
     * Upload multiple bookings to Firestore
     */
    suspend fun uploadBookings(bookings: List<BookingDto>) {
        try {
            val batch = db.batch()
            bookings.forEach { booking ->
                val docRef = bookingsCollection.document(booking.bookingId)
                batch.set(docRef, booking)
            }
            batch.commit().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * Listen to real-time changes in Firestore
     * Returns a Flow that emits whenever bookings change
     */
    fun listenToBookingChanges(): Flow<List<BookingDto>> = callbackFlow {
        val listener = bookingsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                // Handle error
                close(error)
                return@addSnapshotListener
            }
            
            if (snapshot != null) {
                val bookings = snapshot.documents.mapNotNull { doc ->
                    try {
                        doc.toObject(BookingDto::class.java)
                    } catch (e: Exception) {
                        null
                    }
                }
                trySend(bookings)
            }
        }
        
        awaitClose { listener.remove() }
    }
    
    /**
     * Fetch all bookings from Firestore (one-time)
     */
    suspend fun fetchAllBookings(): List<BookingDto> {
        return try {
            val snapshot = bookingsCollection.get().await()
            snapshot.documents.mapNotNull { doc ->
                try {
                    doc.toObject(BookingDto::class.java)
                } catch (e: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
    
    /**
     * Delete a booking from Firestore
     */
    suspend fun deleteBooking(bookingId: String) {
        try {
            bookingsCollection.document(bookingId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

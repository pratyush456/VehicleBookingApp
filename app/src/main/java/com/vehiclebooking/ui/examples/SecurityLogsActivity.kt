package com.vehiclebooking.ui.examples

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.vehiclebooking.R
import com.vehiclebooking.security.SecurityLogger

/**
 * Example Activity to view security logs
 * Shows failed login attempts and other security events
 * Should only be accessible to admin users
 */
class SecurityLogsActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var btnClearLogs: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_security_logs_example)

        recyclerView = findViewById(R.id.rv_security_logs)
        btnClearLogs = findViewById(R.id.btn_clear_logs)

        setupRecyclerView()
        loadLogs()

        btnClearLogs.setOnClickListener {
            SecurityLogger.clearLogs(this)
            loadLogs()
        }
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun loadLogs() {
        val logs = SecurityLogger.getSecurityLogs(this, maxLines = 200)
        
        // Create adapter with logs
        // recyclerView.adapter = SecurityLogsAdapter(logs)
        
        // For now, just log to console
        logs.forEach { log ->
            println(log)
        }
    }
}

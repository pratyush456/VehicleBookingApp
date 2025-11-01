package com.vehiclebooking;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import java.util.*;

/**
 * Custom pie chart for displaying booking status distribution
 */
public class StatusPieChart extends View {
    
    private Paint paint;
    private RectF rectF;
    private Map<BookingStatus, Integer> data;
    private List<Integer> colors;
    private int total = 0;
    
    // Chart styling
    private static final float STROKE_WIDTH = 8f;
    private static final float START_ANGLE = -90f; // Start from top
    
    public StatusPieChart(Context context) {
        super(context);
        init();
    }
    
    public StatusPieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public StatusPieChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        
        rectF = new RectF();
        data = new HashMap<>();
        
        // Initialize colors for each status
        colors = new ArrayList<>();
        colors.add(ContextCompat.getColor(getContext(), R.color.status_pending));
        colors.add(ContextCompat.getColor(getContext(), R.color.status_confirmed));
        colors.add(ContextCompat.getColor(getContext(), R.color.status_in_progress));
        colors.add(ContextCompat.getColor(getContext(), R.color.status_completed));
        colors.add(ContextCompat.getColor(getContext(), R.color.status_cancelled));
    }
    
    public void setData(Map<BookingStatus, Integer> statusData) {
        this.data = new HashMap<>(statusData);
        this.total = 0;
        for (int count : data.values()) {
            total += count;
        }
        invalidate(); // Redraw the chart
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        if (data.isEmpty() || total == 0) {
            drawEmptyState(canvas);
            return;
        }
        
        // Calculate chart bounds
        int width = getWidth();
        int height = getHeight();
        int size = Math.min(width, height);
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = (size - 40) / 2; // Leave some padding
        
        rectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        
        // Draw pie slices
        float currentAngle = START_ANGLE;
        BookingStatus[] statuses = {
            BookingStatus.PENDING,
            BookingStatus.CONFIRMED,
            BookingStatus.IN_PROGRESS,
            BookingStatus.COMPLETED,
            BookingStatus.CANCELLED
        };
        
        for (int i = 0; i < statuses.length; i++) {
            BookingStatus status = statuses[i];
            Integer count = data.get(status);
            if (count != null && count > 0) {
                float sweepAngle = (count * 360f) / total;
                
                paint.setColor(colors.get(i));
                canvas.drawArc(rectF, currentAngle, sweepAngle, true, paint);
                
                currentAngle += sweepAngle;
            }
        }
        
        // Draw center circle for donut effect
        paint.setColor(ContextCompat.getColor(getContext(), R.color.surface_color));
        float centerRadius = radius * 0.5f;
        canvas.drawCircle(centerX, centerY, centerRadius, paint);
        
        // Draw total count in center
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
        paint.setTextSize(radius * 0.25f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        
        String totalText = String.valueOf(total);
        float textY = centerY - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText(totalText, centerX, textY, paint);
        
        // Draw "Total" label
        paint.setTextSize(radius * 0.15f);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
        canvas.drawText("Total", centerX, textY + radius * 0.3f, paint);
    }
    
    private void drawEmptyState(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        int radius = Math.min(width, height) / 3;
        
        // Draw empty circle
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(STROKE_WIDTH);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        canvas.drawCircle(centerX, centerY, radius, paint);
        
        // Draw "No Data" text
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(radius * 0.3f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        
        float textY = centerY - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText("No Data", centerX, textY, paint);
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int width = View.MeasureSpec.getSize(widthMeasureSpec);
        int height = View.MeasureSpec.getSize(heightMeasureSpec);
        
        // Make it square based on smaller dimension
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }
}
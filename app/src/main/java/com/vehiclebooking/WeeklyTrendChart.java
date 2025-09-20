package com.vehiclebooking;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import androidx.core.content.ContextCompat;
import java.util.*;

/**
 * Custom bar chart for displaying weekly booking trends
 */
public class WeeklyTrendChart extends View {
    
    private Paint paint;
    private Map<String, Integer> weeklyData;
    private List<String> daysOfWeek;
    private int maxValue = 0;
    
    // Chart styling
    private static final float BAR_WIDTH_RATIO = 0.7f;
    private static final float BOTTOM_MARGIN = 60f;
    private static final float TOP_MARGIN = 40f;
    private static final float SIDE_MARGIN = 20f;
    
    public WeeklyTrendChart(Context context) {
        super(context);
        init();
    }
    
    public WeeklyTrendChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public WeeklyTrendChart(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        weeklyData = new LinkedHashMap<>();
        
        // Initialize days of week
        daysOfWeek = Arrays.asList("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday");
        
        // Initialize with empty data
        for (String day : daysOfWeek) {
            weeklyData.put(day, 0);
        }
    }
    
    public void setData(Map<String, Integer> dailyData) {
        weeklyData.clear();
        maxValue = 0;
        
        // Map incoming data to our day format and find max
        for (String day : daysOfWeek) {
            Integer dayCount = dailyData.get(day);
            int count = (dayCount == null ? 0 : dayCount);
            weeklyData.put(day, count);
            maxValue = Math.max(maxValue, count);
        }
        
        // Ensure maxValue is at least 1 for scaling
        if (maxValue == 0) maxValue = 1;
        
        invalidate(); // Redraw the chart
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        if (width <= 0 || height <= 0) return;
        
        // Calculate chart area
        float chartWidth = width - (2 * SIDE_MARGIN);
        float chartHeight = height - TOP_MARGIN - BOTTOM_MARGIN;
        
        if (weeklyData.isEmpty()) {
            drawEmptyState(canvas, width, height);
            return;
        }
        
        // Calculate bar dimensions
        float barSpacing = chartWidth / daysOfWeek.size();
        float barWidth = barSpacing * BAR_WIDTH_RATIO;
        
        // Draw bars
        int index = 0;
        for (Map.Entry<String, Integer> entry : weeklyData.entrySet()) {
            String day = entry.getKey();
            int count = entry.getValue();
            
            // Calculate bar position and height
            float x = SIDE_MARGIN + (index * barSpacing) + (barSpacing - barWidth) / 2;
            float barHeight = maxValue > 0 ? (count * chartHeight) / maxValue : 0;
            float y = TOP_MARGIN + chartHeight - barHeight;
            
            // Draw bar
            paint.setStyle(Paint.Style.FILL);
            if (count > 0) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.primary));
            } else {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
            }
            
            RectF barRect = new RectF(x, y, x + barWidth, TOP_MARGIN + chartHeight);
            canvas.drawRoundRect(barRect, 8f, 8f, paint);
            
            // Draw count on top of bar (if value > 0)
            if (count > 0) {
                paint.setColor(ContextCompat.getColor(getContext(), R.color.text_primary));
                paint.setTextSize(24f);
                paint.setTextAlign(Paint.Align.CENTER);
                paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
                
                float textX = x + barWidth / 2;
                float textY = y - 8f;
                canvas.drawText(String.valueOf(count), textX, textY, paint);
            }
            
            // Draw day label
            paint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
            paint.setTextSize(20f);
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.NORMAL));
            
            float labelX = x + barWidth / 2;
            float labelY = height - 15f;
            
            // Abbreviate day names for better fit
            String abbreviatedDay = day.length() > 3 ? day.substring(0, 3) : day;
            canvas.drawText(abbreviatedDay, labelX, labelY, paint);
            
            index++;
        }
        
        // Draw baseline
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        paint.setStrokeWidth(2f);
        canvas.drawLine(SIDE_MARGIN, TOP_MARGIN + chartHeight, 
                       width - SIDE_MARGIN, TOP_MARGIN + chartHeight, paint);
    }
    
    private void drawEmptyState(Canvas canvas, int width, int height) {
        paint.setStyle(Paint.Style.FILL);
        paint.setTextSize(32f);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(ContextCompat.getColor(getContext(), R.color.text_secondary));
        
        float textY = height / 2f - ((paint.descent() + paint.ascent()) / 2);
        canvas.drawText("No weekly data available", width / 2f, textY, paint);
        
        // Draw placeholder bars
        paint.setColor(ContextCompat.getColor(getContext(), R.color.divider));
        float chartWidth = width - (2 * SIDE_MARGIN);
        float chartHeight = height - TOP_MARGIN - BOTTOM_MARGIN;
        float barSpacing = chartWidth / 7;
        float barWidth = barSpacing * 0.5f;
        
        for (int i = 0; i < 7; i++) {
            float x = SIDE_MARGIN + (i * barSpacing) + (barSpacing - barWidth) / 2;
            float y = TOP_MARGIN + chartHeight * 0.8f;
            
            RectF barRect = new RectF(x, y, x + barWidth, TOP_MARGIN + chartHeight);
            canvas.drawRoundRect(barRect, 8f, 8f, paint);
        }
    }
    
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        
        int desiredHeight = 180; // Fixed height for consistency
        int height = resolveSize(desiredHeight, heightMeasureSpec);
        setMeasuredDimension(getMeasuredWidth(), height);
    }
}
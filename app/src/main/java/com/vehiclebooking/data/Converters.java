package com.vehiclebooking.data;

import androidx.room.TypeConverter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vehiclebooking.BookingStatus;
import com.vehiclebooking.StatusChange;
import com.vehiclebooking.UserRole;
import com.vehiclebooking.utils.GsonProvider;
import org.threeten.bp.LocalDate;
import org.threeten.bp.format.DateTimeFormatter;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;

public class Converters {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    @TypeConverter
    public static LocalDate toLocalDate(String value) {
        return value == null ? null : LocalDate.parse(value, DATE_FORMATTER);
    }

    @TypeConverter
    public static String fromLocalDate(LocalDate date) {
        return date == null ? null : date.format(DATE_FORMATTER);
    }

    @TypeConverter
    public static UserRole toUserRole(String value) {
        return value == null ? null : UserRole.valueOf(value);
    }

    @TypeConverter
    public static String fromUserRole(UserRole role) {
        return role == null ? null : role.name();
    }

    @TypeConverter
    public static BookingStatus toBookingStatus(String value) {
        return value == null ? null : BookingStatus.valueOf(value);
    }

    @TypeConverter
    public static String fromBookingStatus(BookingStatus status) {
        return status == null ? null : status.name();
    }

    @TypeConverter
    public static List<StatusChange> toStatusChangeList(String value) {
        if (value == null) {
            return Collections.emptyList();
        }
        Type listType = new TypeToken<List<StatusChange>>() {}.getType();
        return GsonProvider.getGson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStatusChangeList(List<StatusChange> list) {
        return GsonProvider.getGson().toJson(list);
    }
}

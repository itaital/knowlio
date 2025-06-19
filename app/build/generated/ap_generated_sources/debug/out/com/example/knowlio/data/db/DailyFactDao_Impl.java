package com.example.knowlio.data.db;

import android.database.Cursor;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@SuppressWarnings({"unchecked", "deprecation"})
public final class DailyFactDao_Impl implements DailyFactDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DailyFactEntity> __insertionAdapterOfDailyFactEntity;

  public DailyFactDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDailyFactEntity = new EntityInsertionAdapter<DailyFactEntity>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR REPLACE INTO `DailyFactEntity` (`id`,`date`,`en`,`he`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          final DailyFactEntity entity) {
        statement.bindLong(1, entity.id);
        if (entity.date == null) {
          statement.bindNull(2);
        } else {
          statement.bindString(2, entity.date);
        }
        if (entity.en == null) {
          statement.bindNull(3);
        } else {
          statement.bindString(3, entity.en);
        }
        if (entity.he == null) {
          statement.bindNull(4);
        } else {
          statement.bindString(4, entity.he);
        }
      }
    };
  }

  @Override
  public void insert(final DailyFactEntity... facts) {
    __db.assertNotSuspendingTransaction();
    __db.beginTransaction();
    try {
      __insertionAdapterOfDailyFactEntity.insert(facts);
      __db.setTransactionSuccessful();
    } finally {
      __db.endTransaction();
    }
  }

  @Override
  public LiveData<List<DailyFactEntity>> getAll() {
    final String _sql = "SELECT * FROM DailyFactEntity ORDER BY date DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"DailyFactEntity"}, false, new Callable<List<DailyFactEntity>>() {
      @Override
      @Nullable
      public List<DailyFactEntity> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfEn = CursorUtil.getColumnIndexOrThrow(_cursor, "en");
          final int _cursorIndexOfHe = CursorUtil.getColumnIndexOrThrow(_cursor, "he");
          final List<DailyFactEntity> _result = new ArrayList<DailyFactEntity>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DailyFactEntity _item;
            _item = new DailyFactEntity();
            _item.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _item.date = null;
            } else {
              _item.date = _cursor.getString(_cursorIndexOfDate);
            }
            if (_cursor.isNull(_cursorIndexOfEn)) {
              _item.en = null;
            } else {
              _item.en = _cursor.getString(_cursorIndexOfEn);
            }
            if (_cursor.isNull(_cursorIndexOfHe)) {
              _item.he = null;
            } else {
              _item.he = _cursor.getString(_cursorIndexOfHe);
            }
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public LiveData<DailyFactEntity> getLatest() {
    final String _sql = "SELECT * FROM DailyFactEntity ORDER BY date DESC LIMIT 1";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return __db.getInvalidationTracker().createLiveData(new String[] {"DailyFactEntity"}, false, new Callable<DailyFactEntity>() {
      @Override
      @Nullable
      public DailyFactEntity call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfDate = CursorUtil.getColumnIndexOrThrow(_cursor, "date");
          final int _cursorIndexOfEn = CursorUtil.getColumnIndexOrThrow(_cursor, "en");
          final int _cursorIndexOfHe = CursorUtil.getColumnIndexOrThrow(_cursor, "he");
          final DailyFactEntity _result;
          if (_cursor.moveToFirst()) {
            _result = new DailyFactEntity();
            _result.id = _cursor.getInt(_cursorIndexOfId);
            if (_cursor.isNull(_cursorIndexOfDate)) {
              _result.date = null;
            } else {
              _result.date = _cursor.getString(_cursorIndexOfDate);
            }
            if (_cursor.isNull(_cursorIndexOfEn)) {
              _result.en = null;
            } else {
              _result.en = _cursor.getString(_cursorIndexOfEn);
            }
            if (_cursor.isNull(_cursorIndexOfHe)) {
              _result.he = null;
            } else {
              _result.he = _cursor.getString(_cursorIndexOfHe);
            }
          } else {
            _result = null;
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}

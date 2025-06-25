package com.example.knowlio.data.db;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings({"unchecked", "deprecation"})
public final class KnowlioDb_Impl extends KnowlioDb {
  private volatile DailyFactDao _dailyFactDao;

  private volatile DailyBundleDao _dailyBundleDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `DailyFactEntity` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `date` TEXT, `en` TEXT, `he` TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS `DailyBundleEntity` (`date` TEXT NOT NULL, `json` TEXT, PRIMARY KEY(`date`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '905de6620271d343839855dd7b21a6a9')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `DailyFactEntity`");
        db.execSQL("DROP TABLE IF EXISTS `DailyBundleEntity`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDailyFactEntity = new HashMap<String, TableInfo.Column>(4);
        _columnsDailyFactEntity.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFactEntity.put("date", new TableInfo.Column("date", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFactEntity.put("en", new TableInfo.Column("en", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyFactEntity.put("he", new TableInfo.Column("he", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyFactEntity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyFactEntity = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyFactEntity = new TableInfo("DailyFactEntity", _columnsDailyFactEntity, _foreignKeysDailyFactEntity, _indicesDailyFactEntity);
        final TableInfo _existingDailyFactEntity = TableInfo.read(db, "DailyFactEntity");
        if (!_infoDailyFactEntity.equals(_existingDailyFactEntity)) {
          return new RoomOpenHelper.ValidationResult(false, "DailyFactEntity(com.example.knowlio.data.db.DailyFactEntity).\n"
                  + " Expected:\n" + _infoDailyFactEntity + "\n"
                  + " Found:\n" + _existingDailyFactEntity);
        }
        final HashMap<String, TableInfo.Column> _columnsDailyBundleEntity = new HashMap<String, TableInfo.Column>(2);
        _columnsDailyBundleEntity.put("date", new TableInfo.Column("date", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDailyBundleEntity.put("json", new TableInfo.Column("json", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDailyBundleEntity = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDailyBundleEntity = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDailyBundleEntity = new TableInfo("DailyBundleEntity", _columnsDailyBundleEntity, _foreignKeysDailyBundleEntity, _indicesDailyBundleEntity);
        final TableInfo _existingDailyBundleEntity = TableInfo.read(db, "DailyBundleEntity");
        if (!_infoDailyBundleEntity.equals(_existingDailyBundleEntity)) {
          return new RoomOpenHelper.ValidationResult(false, "DailyBundleEntity(com.example.knowlio.data.db.DailyBundleEntity).\n"
                  + " Expected:\n" + _infoDailyBundleEntity + "\n"
                  + " Found:\n" + _existingDailyBundleEntity);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "905de6620271d343839855dd7b21a6a9", "b4b38ec45c83f3e84f03807a1efafcca");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "DailyFactEntity","DailyBundleEntity");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `DailyFactEntity`");
      _db.execSQL("DELETE FROM `DailyBundleEntity`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DailyFactDao.class, DailyFactDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(DailyBundleDao.class, DailyBundleDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DailyFactDao dailyFactDao() {
    if (_dailyFactDao != null) {
      return _dailyFactDao;
    } else {
      synchronized(this) {
        if(_dailyFactDao == null) {
          _dailyFactDao = new DailyFactDao_Impl(this);
        }
        return _dailyFactDao;
      }
    }
  }

  @Override
  public DailyBundleDao dailyBundleDao() {
    if (_dailyBundleDao != null) {
      return _dailyBundleDao;
    } else {
      synchronized(this) {
        if(_dailyBundleDao == null) {
          _dailyBundleDao = new DailyBundleDao_Impl(this);
        }
        return _dailyBundleDao;
      }
    }
  }
}

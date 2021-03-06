package com.sudoajay.historycachecleaner.activity.app.database

import androidx.paging.DataSource
import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery


@Dao
interface AppDao {

    @Query("Select * From AppTable Where System_App = :isSystemApp  or User_App = :isUserApp Order By Name asc ")
    fun getSortByAlpha(isSystemApp: Int, isUserApp: Int): DataSource.Factory<Int, App>

    @Query("Select * From AppTable Where System_App = :isSystemApp  or User_App = :isUserApp Order By Date Desc ")
    fun getSortByDate(isSystemApp: Int, isUserApp: Int): DataSource.Factory<Int, App>

    @Query("Select * From AppTable Where System_App = :isSystemApp  or User_App = :isUserApp Order By Cache_Size Desc ")
    fun getSortBySize(isSystemApp: Int, isUserApp: Int): DataSource.Factory<Int, App>

    @Query("SELECT * FROM AppTable WHERE Name LIKE :search ")
    fun searchItem(search: String?): DataSource.Factory<Int, App>

    @Query("SELECT * FROM AppTable WHERE id= :id")
    suspend fun getAppFromId(id:Int): App

    @Query("Select * FROM AppTable Where Selected =1 ")
    suspend fun getSelectedApp(): MutableList<App>

    @RawQuery
    suspend fun getIdViaQuery(query: SupportSQLiteQuery): List<Int>


    @Query("Select Package_Name FROM AppTable Where Selected =:selected")
    suspend fun getPackageFromSelected(selected: Boolean): MutableList<String>

    @Query("UPDATE AppTable SET Selected = :value WHERE id = :id")
    suspend fun updateSelectedById(value: Boolean, id: Int)

    @Query("Select Count(*) FROM AppTable ")
    suspend fun getCount(): Int


    @Query("SELECT id FROM AppTable WHERE Installed = '0' ")
    suspend fun getUninstallList(): List<Int>

    @Query("SELECT Package_Name FROM AppTable ")
    suspend fun getPackageList(): List<String>


    @Query("UPDATE AppTable SET Installed = '0' ")
    suspend fun setDefaultValueInstall()

    @Query("UPDATE AppTable SET Installed = 1 and Cache_Size = -1 where Package_Name = :packageName")
    suspend fun updateCacheSizeAndInstalledByPackage(packageName: String )

    @Query("UPDATE AppTable SET Cache_Size = :cacheSize where Package_Name = :packageName")
    suspend fun updateCacheSizeByPackage(packageName: String, cacheSize : Long )


    @Query("UPDATE AppTable SET Selected = :selected  Where Package_Name = :packageName")
    suspend fun updateSelectedApp(selected: Boolean, packageName: String)

    @Query("UPDATE AppTable SET Selected = 1  Where Package_Name = :packageName")
    suspend fun listRefresh( packageName: String)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(app: App)



    @Query("DELETE FROM AppTable")
    suspend fun deleteAll()

    @Query("DELETE FROM AppTable Where id = :ID")
    fun deleteRow(ID: Int)

    @Query("DELETE FROM AppTable Where Package_Name = :packageName")
    fun deleteRowFromPackage(packageName: String)



}
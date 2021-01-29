package com.sudoajay.historycachecleaner.activity.app

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.util.Log
import com.sudoajay.historycachecleaner.activity.app.database.App
import com.sudoajay.historycachecleaner.activity.app.database.AppRepository
import com.sudoajay.historycachecleaner.helper.FileHelper
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class LoadApps(private val context: Context, private  val appRepository: AppRepository) {
    private lateinit var packageManager: PackageManager
    private var TAG = "LoadAppsTagg"
    private lateinit var fileHelper :FileHelper
    suspend fun searchInstalledApps( ){
        appDatabaseConfiguration(getInstalledApplication(context))
    }

    private fun getInstalledApplication(context: Context): List<ApplicationInfo> {
        packageManager = context.packageManager
        return packageManager.getInstalledApplications(0)
    }

    private suspend fun appDatabaseConfiguration(
        installedApplicationsInfo: List<ApplicationInfo>
    ) {
        fileHelper = FileHelper(context)

//        Here we first get all package list
        val packageList = appRepository.getPackageList()
        //        Here we Just add new Install App Into Data base

        for (applicationInfo in installedApplicationsInfo) {
            val packageName = getApplicationPackageName(applicationInfo)
            val cacheSize = fileHelper.fileLength(packageName)
            if (packageName !in packageList) {
                if (cacheSize > 8000L) {
                    createApp(applicationInfo, cacheSize)
                    Log.e(TAG, "Not Avaliable in Data base $packageName")
                }
            } else {
                if (cacheSize > 8000L) {
                    Log.e(TAG, "package - $packageName cache size - $cacheSize")
                    appRepository.updateCacheSizeByPackage(packageName, cacheSize)
                } else {
                    Log.e(TAG, "package - $packageName Remove element")
                    appRepository.deleteRowFromPackage(packageName)
                }

                Log.e(TAG, "Avaliable in Data base $packageName")
            }
        }
        Log.e(TAG, " Done appDatabaseConfiguration")
    }

    private suspend fun createApp(applicationInfo: ApplicationInfo, cacheSize:Long) {

        val packageName = getApplicationPackageName(applicationInfo)

            val label = getApplicationLabel(applicationInfo)
            val sourceDir = getApplicationSourceDir(applicationInfo)
            val icon = getApplicationsIcon(applicationInfo)
            val installedDate = getInstalledDate(packageName)
            val systemApp = isSystemApps(applicationInfo)
            appRepository.insert(
                App(
                    null,
                    label,
                    sourceDir,
                    packageName,
                    icon,
                    installedDate,
                    cacheSize,
                    systemApp,
                    !systemApp,
                    isSelected = true,
                    isInstalled = true
                )
            )

    }


    private fun isSystemApps(applicationInfo: ApplicationInfo): Boolean =
        applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 1


    private fun getApplicationLabel(applicationInfo: ApplicationInfo): String =
        packageManager.getApplicationLabel(applicationInfo) as String


    private fun getApplicationSourceDir(applicationInfo: ApplicationInfo): String =
        applicationInfo.sourceDir


    private fun getApplicationPackageName(applicationInfo: ApplicationInfo): String =
        applicationInfo.packageName


    private fun getApplicationsIcon(applicationInfo: ApplicationInfo): String {
        return try {
            applicationInfo.processName
        } catch (e: PackageManager.NameNotFoundException) {
            "defaultApplicationIcon"
        }
    }

    private fun getInstalledDate(packageName: String): String {
        val installDate: Long? = try {
            packageManager.getPackageInfo(packageName, 0).firstInstallTime
        } catch (e: PackageManager.NameNotFoundException) {
            Calendar.getInstance().timeInMillis
        }
        return convertDateToStringFormat(Date(installDate!!))
    }

    private fun convertDateToStringFormat(date: Date): String {
        val pattern = "yyyy-MM-dd HH:mm:ss"

        val df: DateFormat = SimpleDateFormat(pattern, Locale.getDefault())

        return df.format(date)

    }

}
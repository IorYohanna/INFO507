
package fr.univsmb.fridgemate.notifications

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.*
import fr.univsmb.fridgemate.MainActivity
import fr.univsmb.fridgemate.R
import fr.univsmb.fridgemate.local.ProduitDatabase
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

class ExpirationWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val context = applicationContext

            val manager = context.getSystemService(
                NotificationManager::class.java
            )

            manager.createNotificationChannel(
                NotificationChannel(
                    CHANNEL_ID,
                    "Dates d'expiration",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description =
                        "Rappels des aliments arrivant à expiration"
                }
            )
            if (
                Build.VERSION.SDK_INT >= 33 &&
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return Result.success()
            }

            val dao = ProduitDatabase.get(context).produitDao()
            val today = LocalDate.now()
            val products = dao.getProduitsANotifier(
                today.plusDays(7).toString()
            )

            products.forEach { product ->
                val expiration = runCatching {
                    LocalDate.parse(product.date_expiration)
                }.getOrNull() ?: return@forEach

                val days = ChronoUnit.DAYS.between(
                    today,
                    expiration
                )

                if (days < 0) return@forEach

                val message = when (days) {
                    0L -> "Expire aujourd'hui"
                    1L -> "Expire demain"
                    else -> "Expire dans $days jours"
                }

                val intent = Intent(
                    context,
                    MainActivity::class.java
                ).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                            Intent.FLAG_ACTIVITY_CLEAR_TOP
                }

                val pendingIntent = PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or
                            PendingIntent.FLAG_IMMUTABLE
                )

                val notification = NotificationCompat.Builder(
                    context,
                    CHANNEL_ID
                )
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentTitle(
                        "À consommer bientôt : ${product.nom}"
                    )
                    .setContentText(
                        "${product.nombre} produit(s) · $message"
                    )
                    .setStyle(
                        NotificationCompat.BigTextStyle().bigText(
                            "${product.nom} : $message. Pense à le consommer pour éviter le gaspillage."
                        )
                    )
                    .setPriority(
                        NotificationCompat.PRIORITY_DEFAULT
                    )
                    .setAutoCancel(true)
                    .setContentIntent(pendingIntent)
                    .build()

                NotificationManagerCompat.from(context)
                    .notify(
                        product.code.hashCode(),
                        notification
                    )

                dao.marquerNotifie(product.code)
            }

            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }

    companion object {
        private const val CHANNEL_ID =
            "fridgemate_expiration"

        private const val UNIQUE_JOB =
            "fridgemate_daily_expiration_check"

        fun schedule(context: Context) {
            WorkManager.getInstance(context)
                .enqueueUniquePeriodicWork(
                    UNIQUE_JOB,
                    ExistingPeriodicWorkPolicy.KEEP,
                    PeriodicWorkRequestBuilder<ExpirationWorker>(
                        1,
                        TimeUnit.DAYS
                    ).build()
                )
        }

        fun checkNow(context: Context) {
            WorkManager.getInstance(context)
                .enqueue(
                    OneTimeWorkRequestBuilder<ExpirationWorker>()
                        .build()
                )
        }
    }
}

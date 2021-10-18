package com.gc.myapplication.manager

class AppWorkManager {

    companion object {
        const val LOCATION_WORKER_TAG = "location_worker"
    }

//    fun startGetLocationService(context: Context) {
//        Intent(context, UserLocationService::class.java).also {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                context.startForegroundService(it)
//            } else {
//                context.startService(it)
//            }
//        }
//    }
//
//    fun enqueueGetLocationWork() {
//
//        val constraints = Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build()
//
//        val getLocationRequest = PeriodicWorkRequestBuilder<GetLocationWorker>(PeriodicWorkRequest.MIN_PERIODIC_INTERVAL_MILLIS, TimeUnit.MILLISECONDS)
//                .setConstraints(constraints)
//                .addTag(LOCATION_WORKER_TAG)
//                .build()
//
//        WorkManager.getInstance().enqueue(getLocationRequest)
//    }
//
//    fun cancelGetLocationWork() {
//        WorkManager.getInstance().cancelAllWorkByTag(LOCATION_WORKER_TAG)
//    }
//
//    class GetLocationWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
//
//        private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
//
//        override fun doWork(): Result {
//            return if (ContextCompat.checkSelfPermission(applicationContext,
//                            Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                Timber.d("requestLocationUpdates")
//                try {
//                    val location = Single.create<Location> { emitter ->
//                        fusedLocationClient.lastLocation.addOnCompleteListener {
//                            if (it.isSuccessful) {
//                                if (it.result != null) {
//                                    emitter.onSuccess(it.result!!)
//                                } else {
//                                    emitter.onError(Throwable("No location data exception"))
//                                }
//                            } else {
//                                emitter.onError(Throwable(""))
//                            }
//                        }
//                    }.blockingGet()
//                    Timber.d("Location: $location")
//                    return Result.success()
//                } catch (e: Exception) {
//                    Result.failure()
//                }
//            } else {
//                Result.failure()
//            }
//        }
//
//    }

}
package com.commit451.driveappfolderviewer

import com.google.api.services.drive.Drive
import com.google.api.services.drive.DriveRequest
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.InputStream

internal fun <T> DriveRequest<T>.asSingle(): Single<T> {
    return Single.defer {
        Single.just(this.execute())
    }
}

internal fun Drive.Files.Get.asInputStream(): Single<InputStream> {
    return Single.defer {
        Single.just(this.executeMediaAsInputStream())
    }
}

internal fun DriveRequest<Void>.asCompletable(): Completable {
    return Completable.defer {
        this.execute()
        Completable.complete()
    }
}

internal fun<T> Single<T>.with(): Single<T> {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}

internal fun Completable.with(): Completable {
    return subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
}
package com.sgc.speedometer.ui.base

import androidx.lifecycle.ViewModel
import com.sgc.speedometer.data.DataManager
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel(val dataManager: DataManager) : ViewModel() {
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        mCompositeDisposable.dispose()
        super.onCleared()
    }

}

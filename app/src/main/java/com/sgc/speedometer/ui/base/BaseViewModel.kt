package com.sgc.speedometer.ui.base

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.ViewModel
import com.sgc.speedometer.data.DataManager
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel(val dataManager: DataManager) : ViewModel() {
    val isLoading = ObservableBoolean()
    private val mCompositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onCleared() {
        mCompositeDisposable.dispose()
        super.onCleared()
    }

    val compositeDisposable: CompositeDisposable
        get() = mCompositeDisposable

}

package com.sgc.speedometer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory
import java.util.function.Supplier
import javax.inject.Singleton

@Singleton
class ViewModelProviderFactory<T : ViewModel?>(
    private val viewModelClass: Class<T>,
    private val viewModelSupplier: Supplier<T>
) :
    NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(viewModelClass)){
            return viewModelSupplier.get() as T
        }else {
            throw  IllegalArgumentException("Unknown Class name "+viewModelClass.name)
        }
    }

}
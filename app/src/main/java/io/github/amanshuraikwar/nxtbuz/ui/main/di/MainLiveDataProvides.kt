package io.github.amanshuraikwar.nxtbuz.ui.main.di

import androidx.lifecycle.MutableLiveData
import dagger.Module
import dagger.Provides
import io.github.amanshuraikwar.multiitemadapter.RecyclerViewListItem
import io.github.amanshuraikwar.nxtbuz.di.ActivityScoped
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.Loading
import io.github.amanshuraikwar.nxtbuz.ui.main.fragment.model.Alert
import javax.inject.Named

@Module
internal class MainLiveDataProvides {

    @Provides
    @Named("onBackPressed")
    @ActivityScoped
    fun a(): MutableLiveData<Unit> {
        return MutableLiveData<Unit>()
    }

    @Provides
    @Named("listItems")
    @ActivityScoped
    fun b(): MutableLiveData<MutableList<RecyclerViewListItem>> {
        return MutableLiveData<MutableList<RecyclerViewListItem>>()
    }

    @Provides
    @Named("loading")
    @ActivityScoped
    fun c(): MutableLiveData<Loading> {
        return MutableLiveData<Loading>()
    }

    @Provides
    @Named("starredListItems")
    @ActivityScoped
    fun d(): MutableLiveData<MutableList<RecyclerViewListItem>> {
        return MutableLiveData<MutableList<RecyclerViewListItem>>()
    }

    @Provides
    @Named("collapseBottomSheet")
    @ActivityScoped
    fun e(): MutableLiveData<Unit> {
        return MutableLiveData<Unit>()
    }

    @Provides
    @Named("error")
    @ActivityScoped
    fun f(): MutableLiveData<Alert> {
        return MutableLiveData<Alert>()
    }

    @Provides
    @Named("startStarredBusArrivalActivity")
    @ActivityScoped
    fun g(): MutableLiveData<Unit> {
        return MutableLiveData<Unit>()
    }
}
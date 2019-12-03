package io.github.amanshuraikwar.howmuch.di

import io.github.amanshuraikwar.howmuch.ui.launcher.LaunchModule
import dagger.Module
import dagger.android.ContributesAndroidInjector
import io.github.amanshuraikwar.howmuch.data.user.di.GoogleSignInProvides
import io.github.amanshuraikwar.howmuch.ui.launcher.LauncherActivity
import io.github.amanshuraikwar.howmuch.ui.onboarding.OnboardingActivity
import io.github.amanshuraikwar.howmuch.ui.onboarding.OnboardingModule

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
@Suppress("UNUSED")
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = [LaunchModule::class])
    internal abstract fun a(): LauncherActivity

    @ActivityScoped
    @ContributesAndroidInjector(modules = [OnboardingModule::class, GoogleSignInProvides::class])
    internal abstract fun b(): OnboardingActivity

}

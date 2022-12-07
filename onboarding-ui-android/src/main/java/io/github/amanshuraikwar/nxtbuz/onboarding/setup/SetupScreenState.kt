package io.github.amanshuraikwar.nxtbuz.onboarding.setup

internal data class SetupScreenState(
    val versionName: String,
    val setupProgressState: SetupProgressState
)

internal sealed class SetupProgressState {
    object Starting : SetupProgressState()
    object SetupComplete : SetupProgressState()
    data class Error(val message: String) : SetupProgressState()
    data class InProgress(val progress: Int) : SetupProgressState()
}
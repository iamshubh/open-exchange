import kotlinx.serialization.Serializable

interface Routes {

    @Serializable
    object Splash: Routes

    @Serializable
    object Main: Routes
}
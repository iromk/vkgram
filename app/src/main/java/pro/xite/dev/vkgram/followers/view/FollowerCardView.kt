package pro.xite.dev.vkgram.followers.view

/**
 * Created by Roman Syrchin on 7/3/18.
 */
interface FollowerCardView {

    fun setName(name: String)
    fun setAvatar(url: String)
    fun setAvatarStub(sex: Int)
    fun setPosition(position: String)
    fun setCity(city: String)
}


package pro.xite.dev.vkgram.followers.presenter

import com.arellomobile.mvp.InjectViewState
import com.arellomobile.mvp.MvpPresenter
import com.vk.sdk.api.model.VKUsersArray
import io.reactivex.Scheduler
import pro.xite.dev.vkgram.followers.model.FollowersRepo
import pro.xite.dev.vkgram.followers.view.FollowerCardView
import pro.xite.dev.vkgram.followers.view.FollowersView
import pro.xite.dev.vkgram.main.Application
import timber.log.Timber
import javax.inject.Inject

/**
 * Created by Roman Syrchin on 7/3/18.
 */
@InjectViewState
class FollowersPresenter : MvpPresenter<FollowersView>() {


    init {
        Timber.v("init FollowersPresenter")
    }

    @Inject lateinit var mainThreadScheduler: Scheduler

    @Inject lateinit var repo : FollowersRepo

    private var followers : VKUsersArray = VKUsersArray()

    //    constructor(mainThreadScheduler: Scheduler, vkDataSource: VkApiDataSource) : super() {
//        Timber.v("InJECTED!!!!!!")
//        this.mainThreadScheduler = mainThreadScheduler
//        this.followers = VKUsersArray()
//        this.repo = FollowersRepo(vkDataSource)
//    }

    fun showCard(card: FollowerCardView, position: Int) {
//        if(followers.size == 0) {
//            repo.paolosFollowers.observeOn(mainThreadScheduler)
//                .subscribe {
//                    followers.addAll(it)
//                viewState.updated() }
//        } else {
            val follower = followers[position]
            card.setName(follower.first_name + " " + follower.last_name)
            if(follower.photo_100.length > 50) card.setAvatar(follower.photo_100)
            else card.setAvatarStub(follower.sex)
            if(follower.city != null) card.setCity(follower.city.title)
            card.setPosition(position.toString())
//        }
    }

    val itemCount : Int
    get() = followers.size

    override fun onFirstViewAttach() {
        Timber.v("FollowersPresenter.onFirstViewAttach")
        Application.getAppComponent().inject(this)
        repo.paolosFollowers.observeOn(mainThreadScheduler)
                .subscribe {
                    followers.addAll(it)
                    viewState.updated() }
        super.onFirstViewAttach()
    }

}

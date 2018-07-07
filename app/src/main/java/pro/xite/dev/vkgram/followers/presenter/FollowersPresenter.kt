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


    @Inject
    lateinit var mainThreadScheduler: Scheduler

//    @Inject
    lateinit var repo : FollowersRepo

    private lateinit var followers : VKUsersArray

    init {
        Application.getAppComponent().inject(this)
        followers = VKUsersArray()
    }

    //    constructor(mainThreadScheduler: Scheduler, vkDataSource: VkApiDataSource) : super() {
//        Timber.v("InJECTED!!!!!!")
//        this.mainThreadScheduler = mainThreadScheduler
//        this.followers = VKUsersArray()
//        this.repo = FollowersRepo(vkDataSource)
//    }

    fun showCard(card: FollowerCardView, position: Int) {
        if(followers.size == 0) {
            repo.paolosFollowers.observeOn(mainThreadScheduler)
                .subscribe { followers.addAll(it)
                viewState.updated()}
        } else {
//            card.setName(followers[position].first_name + followers[position].last_name)
        }
    }

    val itemCount : Int
    get() = followers.size + 1

    override fun onFirstViewAttach() {
        Timber.v("FollowersPresenter.onFirstViewAttach")
        super.onFirstViewAttach()
    }

}

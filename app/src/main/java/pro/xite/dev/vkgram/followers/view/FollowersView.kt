package pro.xite.dev.vkgram.followers.view

import com.arellomobile.mvp.MvpView
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

/**
 * Created by Roman Syrchin on 7/3/18.
 */
interface FollowersView : MvpView {

    @StateStrategyType(SkipStrategy::class)
    fun updated()

}

package pro.xite.dev.vkgram.di;

import javax.inject.Singleton;

import dagger.Component;
import pro.xite.dev.vkgram.di.modules.SchedulerModule;
import pro.xite.dev.vkgram.followers.presenter.FollowersPresenter;
import pro.xite.dev.vkgram.main.presenter.MainViewPresenter;

/**
 * Created by Roman Syrchin on 7/4/18.
 */
@Singleton
@Component(modules = {SchedulerModule.class})
public interface AppComponent
{
    void inject(FollowersPresenter presenter);
    void inject(MainViewPresenter presenter);
}

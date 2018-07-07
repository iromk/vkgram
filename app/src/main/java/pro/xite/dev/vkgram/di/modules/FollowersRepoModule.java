package pro.xite.dev.vkgram.di.modules;

import android.arch.lifecycle.ViewModelProviders;

import dagger.Module;
import dagger.Provides;
import pro.xite.dev.vkgram.followers.model.FollowersRepo;
import pro.xite.dev.vkgram.main.model.VkApiDataSource;
import pro.xite.dev.vkgram.main.model.VkApiViewModel;
import pro.xite.dev.vkgram.main.view.MainActivity;

/**
 * Created by Roman Syrchin on 7/4/18.
 */
@Module
public class FollowersRepoModule {

    @Provides
    public FollowersRepo provideFollowersRepo(VkApiDataSource vkApiDataSource) {
        return new FollowersRepo(vkApiDataSource);
    }

    @Provides
    public VkApiDataSource provideVkDataSource() {
        return ViewModelProviders.of(new MainActivity()).get(VkApiViewModel.class);
    }

}

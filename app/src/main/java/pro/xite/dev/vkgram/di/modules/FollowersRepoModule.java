package pro.xite.dev.vkgram.di.modules;

import android.arch.lifecycle.ViewModelProviders;

import dagger.Module;
import dagger.Provides;
import pro.xite.dev.vkgram.followers.model.FollowersRepo;
import pro.xite.dev.vkgram.main.model.VkApiDataSource;
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService;
import pro.xite.dev.vkgram.main.model.vkapi.VkApiViewModel;
import pro.xite.dev.vkgram.main.view.MainActivity;

/**
 * Created by Roman Syrchin on 7/4/18.
 */
@Module(includes = { VkApiModule.class })
public class FollowersRepoModule {

    @Provides
    public FollowersRepo provideFollowersRepo(VkApiService api) {//VkApiDataSource vkApiDataSource) {
        return new FollowersRepo(api);
    }

    @Provides
    public VkApiDataSource provideVkDataSource() {
        return ViewModelProviders.of(new MainActivity()).get(VkApiViewModel.class);
    }

}

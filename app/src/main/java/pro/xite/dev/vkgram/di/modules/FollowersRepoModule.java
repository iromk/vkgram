package pro.xite.dev.vkgram.di.modules;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import pro.xite.dev.vkgram.di.anno.LocalDataSource;
import pro.xite.dev.vkgram.di.anno.RemoteDataSource;
import pro.xite.dev.vkgram.followers.model.FollowersDataSource;
import pro.xite.dev.vkgram.followers.model.FollowersRepo;
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService;

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
    @RemoteDataSource
    public FollowersDataSource provideFollowersRemoteDataSource(FollowersRepo followersRepo) {
        return followersRepo;
    }

    @Provides
    @LocalDataSource
    public FollowersDataSource provideFollowersRealmDataSource(@NonNull FollowersRepo followersRepo) {
        return followersRepo;
    }

}

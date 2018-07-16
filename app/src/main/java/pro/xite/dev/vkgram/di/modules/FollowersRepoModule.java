package pro.xite.dev.vkgram.di.modules;

import android.support.annotation.NonNull;

import dagger.Module;
import dagger.Provides;
import pro.xite.dev.vkgram.di.anno.CacheableDataSource;
import pro.xite.dev.vkgram.di.anno.LocalDataSource;
import pro.xite.dev.vkgram.di.anno.RemoteDataSource;
import pro.xite.dev.vkgram.followers.model.FollowersDataSource;
import pro.xite.dev.vkgram.followers.model.FollowersRepo;
import pro.xite.dev.vkgram.followers.model.FollowersVkRepo;
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService;

/**
 * Created by Roman Syrchin on 7/4/18.
 */
@Module(includes = { VkApiModule.class })
public class FollowersRepoModule {

    @Provides
    public FollowersVkRepo provideFollowersVkRepo(VkApiService api) {
        return new FollowersVkRepo(api);
    }

    @Provides
    @RemoteDataSource
    public FollowersDataSource provideFollowersRemoteDataSource(FollowersVkRepo followersVkRepo) {
        return followersVkRepo;
    }

    @Provides
    @LocalDataSource
    public FollowersDataSource provideFollowersRealmDataSource(@NonNull FollowersVkRepo followersVkRepo) {
        return followersVkRepo;
    }

    @Provides
    @CacheableDataSource
    public FollowersDataSource provideFollowersCacheableRepo(
                    @RemoteDataSource FollowersDataSource remote,
                    @LocalDataSource FollowersDataSource local) {
        return new FollowersRepo(remote, local);
    }

}

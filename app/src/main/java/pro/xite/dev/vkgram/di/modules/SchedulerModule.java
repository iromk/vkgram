package pro.xite.dev.vkgram.di.modules;

import dagger.Module;
import dagger.Provides;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Roman Syrchin on 7/4/18.
 */
@Module
public class SchedulerModule {

    @Provides
    public Scheduler provideMainThreadScheduler() {
        return AndroidSchedulers.mainThread();
    }

}

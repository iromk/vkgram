package pro.xite.dev.vkgram.di.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import pro.xite.dev.vkgram.di.anno.VkApiBaseUrl;
import pro.xite.dev.vkgram.di.anno.VkApiVersion;
import pro.xite.dev.vkgram.main.model.converter.VkApiModelConverterFactory;
import pro.xite.dev.vkgram.main.model.vkapi.VkApiService;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Roman Syrchin on 7/7/18.
 */
@Module
public class VkApiModule {

    @Provides @Singleton
    public VkApiService provideVkFollowersApi(Retrofit retrofit) {
        return retrofit.create(VkApiService.class);
    }

    @Provides
    public Retrofit retrofit(@VkApiBaseUrl String baseUrl,
                             OkHttpClient client,
                             VkApiModelConverterFactory vkApiModelConverterFactory,
//                             GsonConverterFactory gsonConverterFactory,
                             RxJava2CallAdapterFactory rxJava2CallAdapterFactory)
    {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(vkApiModelConverterFactory)
//                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    @Provides @VkApiBaseUrl
    public String provideVkBaseApiUrl() {
        return "https://api.vk.com/";
    }

    @Provides
    public OkHttpClient provideOkHttpClient(HttpLoggingInterceptor loggingInterceptor,
                                            VkBaseParamsInterceptor vkBaseParamsInterceptor) {
        return new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(vkBaseParamsInterceptor)
                .build();
    }

    @Provides HttpLoggingInterceptor provideHttpLoggingInterceptor() {
        final HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return loggingInterceptor;
    }

    interface VkBaseParamsInterceptor extends Interceptor {}

    @Provides
    public VkBaseParamsInterceptor provideVkBaseParamsInterceptor(VKAccessToken vkAccessToken,
                                                                  @VkApiVersion String vkApiVersion) {
        return chain -> {
            final Request originalRequest = chain.request();
            final HttpUrl newHttpUrl = originalRequest
                    .url().newBuilder()
                    .addQueryParameter(VKApiConst.ACCESS_TOKEN, vkAccessToken.accessToken)
                    .addQueryParameter(VKApiConst.VERSION, vkApiVersion)
                    .build();
            Request.Builder requestBuilder = originalRequest.newBuilder().url(newHttpUrl);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
    }

    @Provides VKAccessToken provideVkCurrentAccessToken() {
        return VKAccessToken.currentToken();
    }

    @Provides
    @VkApiVersion
    String provideVkApiVersion() { return VKSdk.getApiVersion(); }

    @Provides
    public VkApiModelConverterFactory provideVkApiModelConverterFactory() {
        return VkApiModelConverterFactory.create();
    }

    @Provides
    public GsonConverterFactory provideGsonConverterFactory(Gson gson) {
        return GsonConverterFactory.create(gson);
    }

    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .setFieldNamingStrategy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    @Provides
    public RxJava2CallAdapterFactory provideRxJava2CallAdapterFactory() {
        return RxJava2CallAdapterFactory.create();
    }
}

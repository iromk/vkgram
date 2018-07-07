package pro.xite.dev.vkgram.di.modules;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.model.VKUsersArray;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import pro.xite.dev.vkgram.main.model.VkApiService;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

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
    public Retrofit retrofit(@Named("VkApiBaseUrl") String baseUrl,
                             OkHttpClient client,
                             GsonConverterFactory gsonConverterFactory,
                             RxJava2CallAdapterFactory rxJava2CallAdapterFactory)
    {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addCallAdapterFactory(rxJava2CallAdapterFactory)
                .addConverterFactory(new MyCnvFac())
//                .addConverterFactory(gsonConverterFactory)
                .build();
    }

    class MyCnvFac extends Converter.Factory {
        @Nullable
        @Override
        public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            Timber.v("MyCnvFac extends Converter.Factory");
            return new MyCnv<>();
//            return super.responseBodyConverter(type, annotations, retrofit);
        }

        @Nullable
        @Override
        public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
            return super.requestBodyConverter(type, parameterAnnotations, methodAnnotations, retrofit);
        }

        @Nullable
        @Override
        public Converter<?, String> stringConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
            return super.stringConverter(type, annotations, retrofit);
        }
    }

    class MyCnv<T> implements Converter<ResponseBody, T> {

        @Override
        public T convert(ResponseBody value) throws IOException {
            String v = value.string();
            Timber.v(v);
            VKUsersArray a = new VKUsersArray();
            try {
                a = (VKUsersArray) a.parse(new JSONObject(v));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return (T) a;
        }
    }

    @Provides @Named("VkApiBaseUrl")
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
                    .addQueryParameter(VKApiConst.FIELDS, "id,first_name,last_name,sex,bdate,city,photo_100")
                    .addQueryParameter(VKApiConst.COUNT, "3")
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

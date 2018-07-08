package pro.xite.dev.vkgram.main.model.converter;

import com.vk.sdk.api.model.VKApiModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.UnknownFormatConversionException;

import javax.annotation.Nullable;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;
import timber.log.Timber;

/**
 * Created by Roman Syrchin on 7/8/18.
 */
public class VkApiModelConverterFactory extends Converter.Factory {

    @Nullable
    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        Timber.v("VkApiModelConverterFactory extends Converter.Factory");
        VKApiModel apiModel = VKApiModelTypeFactory.create(type);
        if(apiModel == null)
            throw new UnknownFormatConversionException("VKApiModelTypeFactory cannot provide certain model for converter");
        return new VkApiModelResponseBodyConverter<>(apiModel);
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

    public static VkApiModelConverterFactory create() {
        return new VkApiModelConverterFactory();
    }
}

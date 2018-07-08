package pro.xite.dev.vkgram.main.model.converter;

import android.support.annotation.NonNull;

import com.vk.sdk.api.model.VKApiModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;
import timber.log.Timber;

/**
 * Created by Roman Syrchin on 7/8/18.
 */
public class VkApiModelResponseBodyConverter<T extends VKApiModel> implements Converter<ResponseBody, T> {

    private T vkApiModel;

    public VkApiModelResponseBodyConverter(T vkApiModel) {
        this.vkApiModel = vkApiModel;
    }

    @Override
    public T convert(@NonNull ResponseBody value) throws IOException {
        final String v = value.string();
        Timber.v(v);
        try {
            vkApiModel.parse(new JSONObject(v));
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            value.close();
        }
        return vkApiModel;
    }
}
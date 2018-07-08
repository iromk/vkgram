package pro.xite.dev.vkgram.main.model.converter;

import com.vk.sdk.api.model.VKApiModel;
import com.vk.sdk.api.model.VKUsersArray;

import java.lang.reflect.Type;

/**
 * Created by Roman Syrchin on 7/8/18.
 */
class VKApiModelTypeFactory {


    public static VKApiModel create(Type type) {
        if(type == VKUsersArray.class)
            return new VKUsersArray();
        return null;
    }
}

package pro.xite.dev.vkgram;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;

import static java.util.Objects.requireNonNull;

public class StateKeeper {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, StateKeeper.class.getSimpleName());

    public static void bundle(Object object, Bundle bundle) {
        Class<?> objectClass = requireNonNull(object).getClass();
        for (Field field: objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(KeepState.class)) {
                final String key = field.getAnnotation(KeepState.class).value();
                Log.i(TAG, String.format("saving field: @(%s) %s %s",
                        key,
                        field.getType().getSimpleName(),
                        field.getName()));
                try {
                    final Object objectItem = field.get(object);
                    if(objectItem == null) return;

                    if(Parcelable.class.isAssignableFrom(field.getType())) {
                        Log.i(TAG, "bundled as Parcelable object");
                        bundle.putParcelable(key, (Parcelable) objectItem);
                        continue;
                    }
                    if(field.getGenericType() == int.class) {
                        Log.i(TAG, "bundle as int value");
                        bundle.putInt(key, field.getInt(object));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void unbundle(Object object, Bundle bundle) {

        if(bundle == null || object == null) return;

        Class<?> objectClass = requireNonNull(object).getClass();

        for (Field field: objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(KeepState.class)) {
                final String key = field.getAnnotation(KeepState.class).value();
                Log.i(TAG, String.format("recovering field: @(%s) %s %s",
                        key,
                        field.getType().getSimpleName(),
                        field.getName()));
                Object objectItem = bundle.get(key);
                if(objectItem != null) {
                    try {
                        field.set(object, objectItem);
                        Log.i(TAG, String.format("unbundled: %s", objectItem.getClass()));
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "unbundle error:+ e.printStackTrace()");
                    }
                }
            }
        }
        for(String key : bundle.keySet()) {
            Log.d(TAG, String.format("unbundle: key [%s]", key));

        }

    }
}

package pro.xite.dev.vkgram.statekeeper;

import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

import java.lang.reflect.Field;

import pro.xite.dev.vkgram.Application;

import static java.util.Objects.requireNonNull;

public class StateKeeper {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, StateKeeper.class.getSimpleName());

    public static void bundle(Object object, Bundle bundle) {
        Class<?> objectClass = requireNonNull(object).getClass();
        for (Field field: objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(KeepState.class)) {
                final String key = getKey(field);

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
                    if(field.getGenericType() == boolean.class) {
                        Log.i(TAG, "bundle as boolean value");
                        bundle.putBoolean(key, field.getBoolean(object));
                    }
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getKey(Field field) {
        String key = field.getAnnotation(KeepState.class).value();
        if(key.equals(KeepState.DUMMY_KEY))
            key = String.format("%s_%s",field.getType(), field.getName());
        return key;
    }

    public static void unbundle(Bundle what, Object target) {

        if(what == null || target == null) return;

        Class<?> objectClass = requireNonNull(target).getClass();

        for (Field field: objectClass.getDeclaredFields()) {
            field.setAccessible(true);
            if (field.isAnnotationPresent(KeepState.class)) {
                final String key = field.getAnnotation(KeepState.class).value();
                Log.i(TAG, String.format("recovering field: @(%s) %s %s",
                        key,
                        field.getType().getSimpleName(),
                        field.getName()));
                Object objectItem = what.get(key);
                if(objectItem != null) {
                    try {
                        field.set(target, objectItem);
                        Log.i(TAG, String.format("unbundled: %s", objectItem.getClass()));
                    } catch (IllegalAccessException e) {
                        Log.e(TAG, "unbundle error:+ e.printStackTrace()");
                    }
                }
            }
        }
        for(String key : what.keySet()) {
            Log.d(TAG, String.format("unbundle: key [%s]", key));

        }

    }
}

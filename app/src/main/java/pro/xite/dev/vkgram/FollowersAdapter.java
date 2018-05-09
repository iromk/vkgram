package pro.xite.dev.vkgram;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v4.util.LruCache;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

public class FollowersAdapter extends RecyclerView.Adapter {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName());

    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;

    private VKUsersArray vkFollowers;

    FollowersAdapter(VKUsersArray vkUsersArray) {
        vkFollowers = vkUsersArray;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder: ");
        CardView cvFollower = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_follower, parent, false);
        initImageLoader(parent.getContext());
        return new ViewHolder(cvFollower);
    }

    private void initImageLoader(Context context) {
        mRequestQueue = Volley.newRequestQueue(context.getApplicationContext());
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder: ");
        final VKApiUserFull vkFollower = vkFollowers.get(position);
        final CardView cvFollower = ((ViewHolder) holder).cvFollower;
        final TextView tvUsername = cvFollower.findViewById(R.id.card_follower_user_name);
        final TextView tvCity = cvFollower.findViewById(R.id.card_follower_city);
        final TextView tvPosition = cvFollower.findViewById(R.id.card_position);
//        final ImageView ivAvatar = cvFollower.findViewById(R.id.card_avatar);
        final NetworkImageView nivAvatar = (NetworkImageView) cvFollower.findViewById(R.id.card_avatar);

        tvPosition.setText(String.valueOf(position));
        if(vkFollower != null) {
            Log.w(TAG, String.format("onBindViewHolder: %s %s [%s]",
                    vkFollower.first_name, vkFollower.last_name, vkFollower.photo_200));
            if(vkFollower.photo_200.length() > 50) {
                Log.d(TAG, String.format("vkFollower.photo_200.length() > 50 for %s %s ", vkFollower.first_name, vkFollower.last_name));
                nivAvatar.setImageUrl(vkFollower.photo_200, mImageLoader);
            }
            else {
                Log.d(TAG, String.format("vkFollower.photo_200.length() <= 50 for %s %s ", vkFollower.first_name, vkFollower.last_name));
                switch (vkFollower.sex) {
                    case VKApiUserFull.Sex.MALE:
                        Log.d(TAG, String.format("vkFollower MALE for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        nivAvatar.setDefaultImageResId(R.drawable.icons8_adam_sandler_filled_100);
                        break;
                    case VKApiUserFull.Sex.FEMALE:
                        Log.d(TAG, String.format("vkFollower FEMALE for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        nivAvatar.setDefaultImageResId(R.drawable.icons8_kim_kardashian_filled_100);
                        break;
                    default:
                        Log.d(TAG, String.format("vkFollower UNKNOWN for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        nivAvatar.setDefaultImageResId(R.drawable.ic_launcher_foreground);
                }
                nivAvatar.setImageUrl(null, mImageLoader);
            }
            tvUsername.setText(
                String.format("%s %s",
                    vkFollower.first_name,
                    vkFollower.last_name));
            if(vkFollower.city != null)
                tvCity.setText(vkFollower.city.title);
        }

    }

    @Override
    public int getItemCount() {
        Log.d(TAG, "getItemCount: ");
        return vkFollowers.getCount();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = String.format("%s/%s.%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName(), ViewHolder.class.getSimpleName());

        CardView cvFollower;

        ViewHolder(CardView cardView) {
            super(cardView);
            Log.d(TAG, "ViewHolder: ");
            cvFollower = cardView;
        }
    }

}

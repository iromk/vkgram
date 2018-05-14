package pro.xite.dev.vkgram;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.vk.sdk.api.model.VKApiUserFull;
import com.vk.sdk.api.model.VKUsersArray;

public class FollowersAdapter extends RecyclerView.Adapter {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName());

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
        return new BetterViewHolder(cvFollower);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final VKApiUserFull vkFollower = vkFollowers.get(position);
        final BetterViewHolder vh = (BetterViewHolder) holder;

        vh.tvPosition.setText(String.valueOf(position));

        if (vkFollower != null) {

            Log.w(TAG, String.format("onBindViewHolder: %s %s [%s]",
                    vkFollower.first_name, vkFollower.last_name, vkFollower.photo_200));

            if (vkFollower.photo_200.length() > 50) {
//                Log.d(TAG, String.format("vkFollower.photo_200.length() > 50 for %s %s ", vkFollower.first_name, vkFollower.last_name));
                vh.nivAvatar.setImageUrl(vkFollower.photo_200, Application.getImageLoader());
            } else {
//                Log.d(TAG, String.format("vkFollower.photo_200.length() <= 50 for %s %s ", vkFollower.first_name, vkFollower.last_name));
                switch (vkFollower.sex) {
                    case VKApiUserFull.Sex.MALE:
//                        Log.d(TAG, String.format("vkFollower MALE for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        vh.nivAvatar.setDefaultImageResId(R.drawable.icons8_adam_sandler_filled_100);
                        break;
                    case VKApiUserFull.Sex.FEMALE:
//                        Log.d(TAG, String.format("vkFollower FEMALE for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        vh.nivAvatar.setDefaultImageResId(R.drawable.icons8_kim_kardashian_filled_100);
                        break;
                    default:
//                        Log.d(TAG, String.format("vkFollower UNKNOWN for %s %s ", vkFollower.first_name, vkFollower.last_name));
                        vh.nivAvatar.setDefaultImageResId(R.drawable.ic_launcher_foreground);
                }
                vh.nivAvatar.setImageUrl(null, Application.getImageLoader());
            }

            vh.tvUsername.setText(
                    String.format("%s %s",
                            vkFollower.first_name,
                            vkFollower.last_name));
            if (vkFollower.city != null)
                vh.tvCity.setText(vkFollower.city.title);


        }
    }

    @Override
    public int getItemCount() {
//        Log.d(TAG, "getItemCount: ");
        return vkFollowers.getCount();
    }

    private int viewByIdCount = 0;

    public class BetterViewHolder extends RecyclerView.ViewHolder {

        private final String TAG = String.format("%s/%s.%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName(), BetterViewHolder.class.getSimpleName());
        final TextView         tvUsername;
        final TextView         tvCity;
        final TextView         tvPosition;
        final NetworkImageView nivAvatar;
        final CardView         cvFollower;

        BetterViewHolder(CardView cardView) {
            super(cardView);
            Log.d(TAG, String.format("BetterViewHolder: findViewById calls: %d", ++viewByIdCount));
            cvFollower = cardView;
            tvUsername = cvFollower.findViewById(R.id.card_follower_user_name);
            tvCity     = cvFollower.findViewById(R.id.card_follower_city);
            tvPosition = cvFollower.findViewById(R.id.card_position);
            nivAvatar  = cvFollower.findViewById(R.id.card_avatar);

        }
    }

}

package pro.xite.dev.vkgram;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.model.VKApiUser;
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
        return new ViewHolder(cvFollower);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Log.d(TAG, "onCreateViewHolder: ");
        final VKApiUserFull vkFollower = vkFollowers.get(position);
        final CardView cvFollower = ((ViewHolder) holder).cvFollower;
        final TextView tvUsername = cvFollower.findViewById(R.id.card_follower_user_name);
        final TextView tvCity = cvFollower.findViewById(R.id.card_follower_city);
        final TextView tvPosition = cvFollower.findViewById(R.id.card_position);
        final ImageView ivAvatar = cvFollower.findViewById(R.id.card_avatar);
        tvPosition.setText(String.valueOf(position));
        if(vkFollower != null) {
            if(vkFollower.sex == VKApiUserFull.Sex.MALE)
                ivAvatar.setImageDrawable(cvFollower.getResources().getDrawable(R.drawable.icons8_adam_sandler_filled_100));
            if(vkFollower.sex == VKApiUserFull.Sex.FEMALE)
                ivAvatar.setImageDrawable(cvFollower.getResources().getDrawable(R.drawable.icons8_kim_kardashian_filled_100));
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
        Log.d(TAG, "onCreateViewHolder: ");
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

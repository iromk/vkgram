package pro.xite.dev.vkgram;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vk.sdk.api.VKApi;
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
        CardView cvFollower = ((ViewHolder) holder).cvFollower;
        TextView tvUsername = cvFollower.findViewById(R.id.card_follower_user_name);
        TextView tvCity = cvFollower.findViewById(R.id.card_follower_city);
        final VKApiUserFull follower = vkFollowers.get(position);
        if(follower != null) {
            tvUsername.setText(
                String.format("%s %s",
                    follower.first_name,
                    follower.last_name));
            if(follower.city != null)
                tvCity.setText(follower.city.title);
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

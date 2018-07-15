package pro.xite.dev.vkgram.followers.ui;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.vk.sdk.api.model.VKApiUserFull;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.xite.dev.vkgram.R;
import pro.xite.dev.vkgram.followers.model.VkLoaderService;
import pro.xite.dev.vkgram.followers.presenter.FollowersPresenter;
import pro.xite.dev.vkgram.followers.view.FollowerCardView;
import pro.xite.dev.vkgram.followers.view.FollowersView;
import pro.xite.dev.vkgram.main.Application;
import timber.log.Timber;

public class FollowersAdapter extends MvpBaseAdapter implements FollowersView {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName());

    @InjectPresenter
    public FollowersPresenter p;

    public FollowersAdapter(MvpDelegate<?> parentDelegate) {
        super(parentDelegate, String.valueOf(1));
        Timber.v("FollowersAdapter constructor");
        VkLoaderService.startActionFoo(Application.getInstance().getApplicationContext(), "some", p);
    }

    @NonNull
    @Override
    public BetterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        Timber.tag(TAG).d("onCreateViewHolder: ");
        CardView cvFollower = (CardView) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_follower, parent, false);
        return new BetterViewHolder(cvFollower);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        p.showCard((BetterViewHolder)holder, position);
    }

    @Override
    public int getItemCount() {
        return p.getItemCount();
    }

    private int viewByIdCount = 0;

    @Override
    public void updated() {
        Timber.v("updated notifyDataSetChanged()");
        notifyDataSetChanged();
    }

    public class BetterViewHolder extends RecyclerView.ViewHolder implements FollowerCardView {

        private final String TAG = String.format("%s/%s.%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName(), BetterViewHolder.class.getSimpleName());
        @BindView(R.id.card_follower_user_name) TextView tvUsername;
        @BindView(R.id.card_follower_city) TextView tvCity;
        @BindView(R.id.card_position) TextView tvPosition;
        @BindView(R.id.card_avatar) NetworkImageView nivAvatar;

        BetterViewHolder(CardView cardView) {
            super(cardView);
            Timber.tag(TAG).d("BetterViewHolder: findViewById calls: %d", ++viewByIdCount);
            ButterKnife.bind(this, cardView);
        }

        @Override
        public void setName(@NotNull String name) {
            tvUsername.setText(name);
        }

        @Override
        public void setAvatar(@Nullable String url) {
            nivAvatar.setImageDrawable(null);
            nivAvatar.setImageUrl(url, Application.getImageLoader());
        }

        @Override
        public void setPosition(String position) {
            tvPosition.setText(position);
        }

        @Override
        public void setCity(@NotNull String city) {
            tvCity.setText(city);
        }

        @Override
        public void setAvatarStub(int sex) {
            switch (sex) {
                case VKApiUserFull.Sex.MALE:
                    nivAvatar.setDefaultImageResId(R.drawable.icons8_adam_sandler_filled_100);
                    break;
                case VKApiUserFull.Sex.FEMALE:
                    nivAvatar.setDefaultImageResId(R.drawable.icons8_kim_kardashian_filled_100);
                    break;
                default:
                    nivAvatar.setDefaultImageResId(R.drawable.ic_launcher_foreground);
            }
            nivAvatar.setImageDrawable(null);
        }
    }

}

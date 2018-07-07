package pro.xite.dev.vkgram.followers.ui;

import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.jetbrains.annotations.NotNull;

import butterknife.BindView;
import butterknife.ButterKnife;
import pro.xite.dev.vkgram.R;
import pro.xite.dev.vkgram.followers.presenter.FollowersPresenter;
import pro.xite.dev.vkgram.followers.view.FollowerCardView;
import pro.xite.dev.vkgram.followers.view.FollowersView;
import pro.xite.dev.vkgram.main.Application;
import timber.log.Timber;

public class FollowersAdapter extends MvpBaseAdapter implements FollowersView {

    private static final String TAG = String.format("%s/%s", Application.APP_TAG, FollowersAdapter.class.getSimpleName());

//    private VKUsersArray vkFollowers;

    @InjectPresenter
    public FollowersPresenter p;

    public FollowersAdapter(MvpDelegate<?> parentDelegate) {//}, String childId) {
        super(parentDelegate, String.valueOf(1));
    }

//    public FollowersAdapter(MvpDelegate mvpDelegate,Scheduler s, VkApiViewModel v) {//, String childId) {
//        super(mvpDelegate, String.valueOf(1));
//        p.setMainThreadScheduler(s);
//        p.setRepo(v);
//        p.load();
//        super(parentDelegate, FollowersPresenter.class.getClass().toString());
//    }

//    FollowersAdapter(VKUsersArray vkUsersArray) {
//        vkFollowers = vkUsersArray;
//    }

//    FollowersAdapter(FollowersPresenter followersPresenter) {
//        p = followersPresenter;
//    }

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

//        final VKApiUserFull vkFollower = vkFollowers.get(position);
//        final BetterViewHolder vh = (BetterViewHolder)holder;

//        vh.tvPosition.setText(String.valueOf(position));
//        vh.tvUsername.setText("wtf!!!!!!");
/*
        if (vkFollower != null) {

            Timber.tag(TAG).w("onBindViewHolder: %s %s [%s]",
                    vkFollower.first_name, vkFollower.last_name, vkFollower.photo_200);

            if (vkFollower.photo_200.length() > 50) {
                vh.nivAvatar.setImageUrl(vkFollower.photo_200, Application.getImageLoader());
            } else {
                switch (vkFollower.sex) {
                    case VKApiUserFull.Sex.MALE:
                        vh.nivAvatar.setDefaultImageResId(R.drawable.icons8_adam_sandler_filled_100);
                        break;
                    case VKApiUserFull.Sex.FEMALE:
                        vh.nivAvatar.setDefaultImageResId(R.drawable.icons8_kim_kardashian_filled_100);
                        break;
                    default:
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
*/
    }

    @Override
    public int getItemCount() {
        return p.getItemCount();
//        return vkFollowers.getCount();
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
//        final CardView cvFollower;

        BetterViewHolder(CardView cardView) {
            super(cardView);
            Timber.tag(TAG).d("BetterViewHolder: findViewById calls: %d", ++viewByIdCount);
            ButterKnife.bind(this, cardView);
//            tvUsername = cardView.findViewById(R.id.card_follower_user_name);
//            cvFollower = cardView;

        }

        @Override
        public void setName(@NotNull String name) {
            tvUsername.setText(name);
            tvCity.setText("naddddme");
            tvPosition.setText("11");
//            nivAvatar.setDefaultImageResId(R.drawable.icons8_kim_kardashian_filled_100);
            nivAvatar.setImageResource(R.drawable.icons8_kim_kardashian_filled_100);
        }
    }

}

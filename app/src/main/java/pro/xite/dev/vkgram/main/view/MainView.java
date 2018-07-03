package pro.xite.dev.vkgram.main.view;

import com.arellomobile.mvp.MvpView;

/**
 * Created by Roman Syrchin on 6/20/18.
 */
public interface MainView extends MvpView {

    void invokeVkLoginActivity();
    void setActiveUser();
    void setLoggedUserName(CharSequence firstName, CharSequence lastName);
    void setLoggedUserAvatar(String url);
    void setUiStateLoggedIn();
    void setUiStateLoggedOut();
    void setTheme(int theme);
}

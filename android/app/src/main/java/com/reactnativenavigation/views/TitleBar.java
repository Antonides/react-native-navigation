package com.reactnativenavigation.views;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v7.widget.ActionMenuView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.graphics.Typeface;
import android.widget.TextView;

import com.reactnativenavigation.params.BaseTitleBarButtonParams;
import com.reactnativenavigation.params.StyleParams;
import com.reactnativenavigation.params.TitleBarButtonParams;
import com.reactnativenavigation.params.TitleBarLeftButtonParams;
import com.reactnativenavigation.utils.ViewUtils;

import java.util.List;

public class TitleBar extends Toolbar {

    private LeftButton leftButton;
    private ActionMenuView actionMenuView;
    private Context context;
    
    public TitleBar(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public void onViewAdded(View child) {
        super.onViewAdded(child);
        if (child instanceof ActionMenuView) {
            actionMenuView = (ActionMenuView) child;
        }
    }

    public void setRightButtons(List<TitleBarButtonParams> rightButtons, String navigatorEventId) {
        Menu menu = getMenu();
        menu.clear();
        if (rightButtons == null) {
            return;
        }
        addButtonsToTitleBar(rightButtons, navigatorEventId, menu);
    }

    public void setLeftButton(TitleBarLeftButtonParams leftButtonParams,
                              LeftButtonOnClickListener leftButtonOnClickListener,
                              String navigatorEventId,
                              boolean overrideBackPressInJs) {
        if (shouldSetLeftButton(leftButtonParams)) {
            createAndSetLeftButton(leftButtonParams, leftButtonOnClickListener, navigatorEventId, overrideBackPressInJs);
        } else if (hasLeftButton()) {
            updateLeftButton(leftButtonParams);
        }
    }

    public void setStyle(StyleParams params) {
        setVisibility(params.titleBarHidden ? GONE : VISIBLE);
        setTitleTextColor(params);
        setTitleTextFont(params);
        setSubtitleTextColor(params);
        colorOverflowButton(params);
        setTranslucent(params);
    }

    private void colorOverflowButton(StyleParams params) {
        Drawable overflowIcon = actionMenuView.getOverflowIcon();
        if (shouldColorOverflowButton(params, overflowIcon)) {
            ViewUtils.tintDrawable(overflowIcon, params.titleBarButtonColor.getColor(), true);
        }
    }

    private void setTranslucent(StyleParams params) {
        if (params.topBarTranslucent) {
            setBackground(new TranslucentTitleBarBackground());
        }
    }

    private boolean shouldColorOverflowButton(StyleParams params, Drawable overflowIcon) {
        return overflowIcon != null && params.titleBarButtonColor.hasColor();
    }

    protected void setTitleTextColor(StyleParams params) {
        if (params.titleBarTitleColor.hasColor()) {
            setTitleTextColor(params.titleBarTitleColor.getColor());
        }
    }

    protected void setTitleTextFont(StyleParams params) {
        if (params.titleBarTitleFont != null && !params.titleBarTitleFont.isEmpty()) {
            Typeface titleFont = null;

            try {
                titleFont = Typeface.createFromAsset(this.context.getAssets(), "fonts/"+ params.titleBarTitleFont +".ttf");
            } catch(RuntimeException e)
            {}

            if (titleFont == null) {
                try {
                    titleFont = Typeface.createFromAsset(this.context.getAssets(), "fonts/"+ params.titleBarTitleFont +".otf");
                } catch(RuntimeException e)
                {}
            }

            if (titleFont != null) {
                for (int i = 0; i < this.getChildCount(); i++){
                    View view = this.getChildAt(i);
                    if (view instanceof TextView){
                        TextView tv = (TextView) view;
                        tv.setTypeface(titleFont);
                    }
                }
            }
        }
    }

    protected void setSubtitleTextColor(StyleParams params) {
        if (params.titleBarSubtitleColor.hasColor()) {
            setSubtitleTextColor(params.titleBarSubtitleColor.getColor());
        }
    }

    private void addButtonsToTitleBar(List<TitleBarButtonParams> rightButtons, String navigatorEventId, Menu menu) {
        for (int i = 0; i < rightButtons.size(); i++) {
            final TitleBarButton button = ButtonFactory.create(menu, this, rightButtons.get(i), navigatorEventId);
            addButtonInReverseOrder(rightButtons, i, button);
        }
    }

    protected void addButtonInReverseOrder(List<? extends BaseTitleBarButtonParams> buttons, int i, TitleBarButton button) {
        final int index = buttons.size() - i - 1;
        button.addToMenu(index);
    }

    private boolean hasLeftButton() {
        return leftButton != null;
    }

    private void updateLeftButton(TitleBarLeftButtonParams leftButtonParams) {
        leftButton.setIconState(leftButtonParams);
    }

    private boolean shouldSetLeftButton(TitleBarLeftButtonParams leftButtonParams) {
        return leftButton == null && leftButtonParams != null;
    }

    private void createAndSetLeftButton(TitleBarLeftButtonParams leftButtonParams,
                                        LeftButtonOnClickListener leftButtonOnClickListener,
                                        String navigatorEventId,
                                        boolean overrideBackPressInJs) {
        leftButton = new LeftButton(getContext(), leftButtonParams, leftButtonOnClickListener, navigatorEventId,
                overrideBackPressInJs);
        setNavigationOnClickListener(leftButton);
        setNavigationIcon(leftButton);
    }

    public void hide() {
        hide(null);
    }

    public void hide(@Nullable final Runnable onHidden) {
        animate()
                .alpha(0)
                .setDuration(200)
                .setInterpolator(new AccelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onHidden != null) {
                            onHidden.run();
                        }
                    }
                });
    }

    public void show() {
        this.show(null);
    }

    public void show(final @Nullable Runnable onDisplayed) {
        setAlpha(0);
        animate()
                .alpha(1)
                .setDuration(200)
                .setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (onDisplayed != null) {
                            onDisplayed.run();
                        }
                    }
                });
    }
}

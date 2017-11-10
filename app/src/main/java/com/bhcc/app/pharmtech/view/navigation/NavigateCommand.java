package com.bhcc.app.pharmtech.view.navigation;

import com.bhcc.app.pharmtech.R;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

public abstract class NavigateCommand implements Command {

    protected Fragment fragment;

    protected FragmentManager manager;

    protected boolean add;

    protected boolean backstack;

    protected int container = R.id.fragment_container;

    public NavigateCommand() {

    }

    public NavigateCommand(Fragment fragment) {
        this.fragment = fragment;
    }

    public NavigateCommand init(FragmentActivity fragmentActivity) {
        this.manager = fragmentActivity.getSupportFragmentManager();
        return this;
    }

    public NavigateCommand init(FragmentManager fragmentManager) {
        this.manager = fragmentManager;
        return this;
    }

    /**
     * if this method isn't called the Fragment Transaction will be a replace
     */
    protected final NavigateCommand add() {
        this.add = true;
        return this;
    }

    public final NavigateCommand container(int container) {
        this.container = container;
        return this;
    }

    public final NavigateCommand addToBackstack(boolean backstack) {
        this.backstack = backstack;
        return this;
    }

    @Override
    public void execute() {
        final FragmentTransaction fragmentTransaction = manager.beginTransaction();
        final String tag = fragment.getClass().getSimpleName();
        if (add) {
            fragmentTransaction.add(container, fragment, tag);
        } else {
            fragmentTransaction.replace(container, fragment, tag);
        }
        if (backstack) {
            fragmentTransaction.addToBackStack(tag);
        }
        fragmentTransaction.commitAllowingStateLoss();
    }
}
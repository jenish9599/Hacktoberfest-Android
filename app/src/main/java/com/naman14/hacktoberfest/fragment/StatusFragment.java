package com.naman14.hacktoberfest.fragment;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.naman14.hacktoberfest.R;
import com.naman14.hacktoberfest.adapters.PRAdapter;
import com.naman14.hacktoberfest.network.entity.Issue;
import com.naman14.hacktoberfest.network.repository.GithubRepository;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by naman on 4/10/17.
 */

public class StatusFragment extends BaseFragment {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;

    @BindView(R.id.iv_user_image)
    ImageView ivUserImage;

    @BindView(R.id.et_username)
    EditText etUsername;

    @BindView(R.id.iv_check)
    ImageView ivCheck;

    @BindView(R.id.scrollView)
    NestedScrollView scrollView;

    @BindView(R.id.statusView)
    View statusView;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private PRAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_status, container, false);

        ButterKnife.bind(this, rootView);

        setToolbar(rootView, "Hacktoberfest");

        setupRecyclerview();
        Picasso.with(getActivity()).load("https://avatars3.githubusercontent.com/u/8599099?v=4").into(ivUserImage);


        return rootView;
    }


    @OnClick(R.id.iv_check)
    public void checkClicked() {
        List<Issue> issues = new ArrayList<>();
        for (int i=0; i<6; i++) {
            Issue issue = new Issue();
            issue.setNumber(319);
            issue.setTitle("Fixes 395 and other improvements");
            issue.setHtml_url("https://github.com/openMF/community-app/pull/19");
            issue.setRepository_url("https://api.github.com/repos/openMF/community-app");

            issues.add(issue);
        }

        adapter.setData(issues);

//        checkPRStatus();
        scrollToView(scrollView, etUsername);
    }

    private void setupRecyclerview() {

        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getActivity(), 2);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new PRAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

    }

    private void checkPRStatus() {
        progressBar.setVisibility(View.VISIBLE);
        GithubRepository.getInstance().findValidPRs(etUsername.getText().toString())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Subscriber<Object>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressBar.setVisibility(View.GONE);
                        Snackbar.make(scrollView, "Error fetching details", Snackbar.LENGTH_LONG).show();
                    }

                    @Override
                    public void onNext(Object response) {
                        progressBar.setVisibility(View.GONE);
                        showStatus(response);

                    }
                });
    }

    private void showStatus(Object response) {
        statusView.setVisibility(View.VISIBLE);
    }


    private void scrollToView(final NestedScrollView scrollViewParent, final View view) {
        // Get deepChild Offset
        Point childOffset = new Point();
        getDeepChildOffset(scrollViewParent, view.getParent(), view, childOffset);
        // Scroll to child.
        scrollViewParent.smoothScrollTo(0, childOffset.y - 50);
    }

    private void getDeepChildOffset(final ViewGroup mainParent, final ViewParent parent, final View child, final Point accumulatedOffset) {
        ViewGroup parentGroup = (ViewGroup) parent;
        accumulatedOffset.x += child.getLeft();
        accumulatedOffset.y += child.getTop();
        if (parentGroup.equals(mainParent)) {
            return;
        }
        getDeepChildOffset(mainParent, parentGroup.getParent(), parentGroup, accumulatedOffset);
    }
}
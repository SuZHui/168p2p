package com.magic.szh.cnf_168p2p.content.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.listener.OnItemClickListener;
import com.magic.szh.cnf_168p2p.R;
import com.magic.szh.cnf_168p2p.api.response.ResponseHomeBanner;
import com.magic.szh.cnf_168p2p.api.url.Api;
import com.magic.szh.cnf_168p2p.base.MagicFragment;
import com.magic.szh.net.RestClient;
import com.magic.szh.util.log.MagicLogger;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * project: CNF_168p2p
 * package: com.magic.szh.cnf_168p2p.content.home
 * file: HomeFragment
 * author: admin
 * date: 2018/2/11
 * description: 主页fragment
 */

public class HomeFragment extends MagicFragment implements OnItemClickListener {
    ConvenientBanner<ResponseHomeBanner.BannerItem> mConvenientBanner;
    private static final ArrayList<ResponseHomeBanner.BannerItem> BANNERS = new ArrayList<>();

    @BindView(R.id.root_layout)
    LinearLayout mContentLayout;

    @Override
    public Object setLayout() {
        return R.layout.fragment_home;
    }

    @Override
    public void onBindView(@Nullable Bundle savedInstanceState, View rootView) {
        initBanner();
    }

    /**
     * 初始化banner滚动图
     */
    private void initBanner () {
        mConvenientBanner = new ConvenientBanner<>(getContext());
        // 转换banner高度单位dp -> px
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 204, getActivity().getResources().getDisplayMetrics());
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
        mConvenientBanner.setLayoutParams(params);
        mConvenientBanner
                .setPages(new BannerHolderCreator(), BANNERS)
                .setPageIndicator(new int[]{R.drawable.dot_normal, R.drawable.dot_focus})
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.CENTER_HORIZONTAL)
                .startTurning(3000)
                .setOnItemClickListener(this)
                .setCanLoop(true);
        mContentLayout.addView(mConvenientBanner, 0);
        RestClient.builder()
                .url(Api.GET_MAIN_HOME_BANNER)
                .success(response -> {
                    Log.e("HOME-BANNER", response);
                    ResponseHomeBanner responseHomeBanner = ResponseHomeBanner.getInstance(response);
                    if (responseHomeBanner.getCode() == 200) {
                        BANNERS.clear();
                        BANNERS.addAll(responseHomeBanner.list);
                        mConvenientBanner.notifyDataSetChanged();
                    }
                })
                .failure((Throwable t) -> {
                    Log.e("HOME-BANNER", t.getMessage());
                })
                .error((code, msg) -> {
                    MagicLogger.e("HOME-BANNER"+code, msg);
                })
                .build()
                .get();
    }

    @Override
    public void onItemClick(int position) {
        Toast
            .makeText(getActivity(), BANNERS.get(position).name, Toast.LENGTH_SHORT)
            .show();
    }
}

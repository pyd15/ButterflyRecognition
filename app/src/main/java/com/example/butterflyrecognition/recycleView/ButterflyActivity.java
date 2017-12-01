package com.example.butterflyrecognition.recycleView;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.HttpAction;
import com.example.butterflyrecognition.db.InfoDetail;
import com.example.butterflyrecognition.recycleView.indexBar.ButterflyInfo_copy;
import com.example.butterflyrecognition.recycleView.search.HeaderAdapter;
import com.example.butterflyrecognition.recycleView.search.RecyclerOnItemClickListener;
import com.example.butterflyrecognition.recycleView.search.SearchButterflyInfoAdapter;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ButterflyActivity extends AppCompatActivity implements RecyclerOnItemClickListener.OnItemClickListener{

    @BindView(R.id.toolbar_recycler_view)
    Toolbar toolbar;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.search_view)
    MaterialSearchView searchView;
    @BindView(R.id.swipsh_refresh)
    SwipeRefreshLayout swipeRefreshLayout;

    private SearchButterflyInfoAdapter butterflyAdapter;
//    ButterflyAdapter butterflyAdapter;

    private SuspensionDecoration butterflyDecoration;
    /**
     * 右侧边栏导航区域
     */
    private IndexBar mIndexBar;

    /**
     * 显示指示器DialogText
     */
    private TextView mTvSideBarHint;

    /**
     * 显示进度条
     */
    private ProgressDialog progressDialog;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    List<InfoDetail> butterflyInfoList = new ArrayList<>();
    List<ButterflyInfo_copy> butterflyInfo_copyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_view);

        ButterKnife.bind(this);
        initView();

        intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkChangeReciver = new NetworkChangeReciver();
        registerReceiver(networkChangeReciver, intentFilter);

    }

    private void initView() {
        initToolBar();
        initRecyclerView();
        initSearchView();
        initSwipeRefreshLayout();
    }

    /**
     * init Toolbar
     */
    private void initToolBar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void  initButterfly() {
        List<InfoDetail> list = DataSupport.findAll(InfoDetail.class);
        butterflyInfoList.clear();
        butterflyInfo_copyList.clear();
        ButterflyInfo_copy butterflyInfo_copy;
        for (InfoDetail butterflyInfo : list) {
            butterflyInfo_copy = new ButterflyInfo_copy();
            butterflyInfo_copy.setId(butterflyInfo.getId());
            butterflyInfo_copy.setName(butterflyInfo.getName());
            butterflyInfo_copy.setLatinName(butterflyInfo.getLatinName());
            butterflyInfo_copy.setFeature(butterflyInfo.getFeature());
            butterflyInfo_copy.setType(butterflyInfo.getType());
            butterflyInfo_copy.setRare(butterflyInfo.getRare());
            butterflyInfo_copy.setProtect(butterflyInfo.getProtect());
            butterflyInfo_copy.setImagePath(butterflyInfo.getImagePath());
            butterflyInfo_copy.setImageUrl(butterflyInfo.getImageUrl());
            butterflyInfo_copy.setUniqueToChina(butterflyInfo.getUniqueToChina());
            //            Log.d("ButterflyInfo_copy", "id is " + butterflyInfo_copy.getId());
            //            Log.d("ButterflyInfo_copy", "name is " + butterflyInfo_copy.getName());
            //            Log.d("ButterflyInfo_copy", "ename is " + butterflyInfo_copy.getLatinName());
            //            Log.d("ButterflyInfo_copy", "desc is " + butterflyInfo_copy.getFeature());
            //            Log.d("ButterflyInfo_copy", "type is " + butterflyInfo_copy.getType());
            //            Log.d("ButterflyInfo_copy", "image is " + butterflyInfo_copy.getImagePath());
            butterflyInfoList.add(butterflyInfo);
            butterflyInfo_copyList.add(butterflyInfo_copy);
        }
    }

    /**
     * init RecyclerView
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    private void initRecyclerView() {
        initButterfly();

        LinearLayoutManager layoutManager=new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        butterflyAdapter = new SearchButterflyInfoAdapter(butterflyInfo_copyList);
        recyclerView.setAdapter(butterflyAdapter);
        HeaderAdapter adapter = new HeaderAdapter(butterflyAdapter);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(butterflyDecoration = new SuspensionDecoration(this, butterflyInfo_copyList));
        recyclerView.addItemDecoration(new DividerItemDecoration(this,LinearLayoutManager.VERTICAL));

        //使用indexBar
        mTvSideBarHint = (TextView) findViewById(R.id.tvSideBarHint);//HintTextView
        mIndexBar = (IndexBar) findViewById(R.id.indexBar);//IndexBar

        //indexbar初始化
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(layoutManager);//设置RecyclerView的LayoutManager
        mIndexBar.setmSourceDatas(butterflyInfo_copyList)//设置数据
                .invalidate();
        butterflyAdapter.setDatas(butterflyInfo_copyList);
        if (butterflyInfo_copyList.size() < 5) {
            mIndexBar.setVisibility(View.INVISIBLE);
        }

        butterflyDecoration.setmDatas(butterflyInfo_copyList);
    }

    /**
     * init SearchView
     */
    private void initSearchView() {

        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                final List<ButterflyInfo_copy> filteredModelList = filter(butterflyInfo_copyList, newText);
                //reset
                butterflyAdapter.setFilter(filteredModelList);
                butterflyAdapter.animateTo(filteredModelList);
                recyclerView.scrollToPosition(0);
                return true;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {
                butterflyAdapter.setFilter(butterflyInfo_copyList);
            }
        });
    }

    /**
     * init SwiperRefreshLayout
     */
    private void initSwipeRefreshLayout() {
        swipeRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            swipeRefreshLayout.setElevation((float) 0.0);
        }
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (ContextCompat.checkSelfPermission(ButterflyActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ButterflyActivity.this,new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
                }
                ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                if (networkInfo != null && networkInfo.isAvailable()) {
                    refresh();
                } else {
                    Toast.makeText(ButterflyActivity.this, "无法刷新！", Toast.LENGTH_SHORT).show();
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });
    }

    private void refresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        HttpAction.sendRequestWithOkHttp();
                        //                        initButterfly();
                        butterflyAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(ButterflyActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciver);
    }


    /**
     * 筛选逻辑
     * @param butterflyinfos
     * @param query
     * @return
     */
    private List<ButterflyInfo_copy> filter(List<ButterflyInfo_copy> butterflyinfos, String query) {
        query = query.toLowerCase();

        final List<ButterflyInfo_copy> filteredModelList = new ArrayList<>();
        for (ButterflyInfo_copy butterflyinfo : butterflyinfos) {
            final String name = butterflyinfo.getName();
            final String latinName = butterflyinfo.getLatinName();
            if (name.contains(query) ||  latinName.contains(query) ) {
                filteredModelList.add(butterflyinfo);
            }
        }
        return filteredModelList;
    }

    /**
     * 搜索按钮
     *
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_search, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 返回按钮处理
     */
    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
        } else {
            finish();
            //实现淡入淡出的切换效果
            overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
            super.onBackPressed();
        }
    }

    /**
     * 筛选传递
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MaterialSearchView.REQUEST_VOICE && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                String searchWrd = matches.get(0);
                if (!TextUtils.isEmpty(searchWrd)) {
                    searchView.setQuery(searchWrd, false);
                }
            }
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    class NetworkChangeReciver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
            } else {
                Toast.makeText(context, "无法连接至服务器，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(ButterflyActivity.this);
            progressDialog.setMessage("加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        InfoDetail butterflyInfo = butterflyInfoList.get(position);
        Intent intent = new Intent(this,InfoActivity.class);
        intent.putExtra("butterflyNo", butterflyInfo.getId());
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}

//        /**
//         * 默认情况下, search widget是"iconified“的，只是用一个图标 来表示它(一个放大镜),
//         * 当用户按下它的时候才显示search box . 你可以调用setIconifiedByDefault(false)让search
//         * box默认都被显示。 你也可以调用setIconified()让它以iconified“的形式显示。
//         */
//        mSearchView.setIconifiedByDefault(false);
//        /**
//         * 默认情况下是没提交搜索的按钮，所以用户必须在键盘上按下"enter"键来提交搜索.你可以同过setSubmitButtonEnabled(
//         * true)来添加一个提交按钮（"submit" button)
//         * 设置true后，右边会出现一个箭头按钮。如果用户没有输入，就不会触发提交（submit）事件
//         */
//        mSearchView.setSubmitButtonEnabled(true);
//        /**
//         * 初始是否已经是展开的状态
//         * 写上此句后searchView初始展开的，也就是是可以点击输入的状态，如果不写，那么就需要点击下放大镜，才能展开出现输入框
//         */
////        mSearchView.onActionViewExpanded();
//        mSearchView.onActionViewCollapsed();
//        // 设置search view的背景色
//        mSearchView.setBackgroundColor(0x22ff00ff);
//        /**
//         * 默认情况下, search widget是"iconified“的，只是用一个图标 来表示它(一个放大镜),
//         * 当用户按下它的时候才显示search box . 你可以调用setIconifiedByDefault(false)让search
//         * box默认都被显示。 你也可以调用setIconified()让它以iconified“的形式显示。
//         */
//        mSearchView.setIconifiedByDefault(true);

//        mSearchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//
//                return true;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                final List<ButterflyInfo> filteredModelList = filter(butterflyInfoList, newText);
//
//                //reset
////                butterflyAdapter.setFilter(filteredModelList);
////                mAdapter.animateTo(filteredModelList);
////                recyclerView.scrollToPosition(0);
//                return true;
//            }
//        });

//        mSearchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//                mAdapter.setFilter(mButterflyInfoList);
//            }
//        });
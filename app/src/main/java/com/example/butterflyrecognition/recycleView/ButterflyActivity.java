package com.example.butterflyrecognition.recycleView;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.Util.HttpAction;
import com.example.butterflyrecognition.Util.HttpUtil;
import com.example.butterflyrecognition.Util.SQLUtil;
import com.example.butterflyrecognition.db.InfoDetail;
import com.example.butterflyrecognition.recycleView.indexBar.ButterflyInfo_copy;
import com.example.butterflyrecognition.recycleView.search.HeaderAdapter;
import com.example.butterflyrecognition.recycleView.search.RecyclerOnItemClickListener;
import com.example.butterflyrecognition.recycleView.search.SearchButterflyInfoAdapter;
import com.mcxtzhang.indexlib.IndexBar.widget.IndexBar;
import com.mcxtzhang.indexlib.suspension.SuspensionDecoration;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ButterflyActivity extends AppCompatActivity implements RecyclerOnItemClickListener.OnItemClickListener {

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
     * 顶部首字母
     */
    private HeaderAdapter adapter;

    /**
     * 显示进度条
     */
    private ProgressDialog progressDialog;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private IntentFilter intentFilter;
    private NetworkChangeReciver networkChangeReciver;

    private static boolean readFlag = false;
    String address = "http://40.125.207.182:8080/getInfo.do";

    List<InfoDetail> butterflyInfoList = new ArrayList<>();
    List<ButterflyInfo_copy> butterflyInfo_copyList = new ArrayList<>();

    private boolean netFlag = true;
    public static String[] images = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycle_view);

        ButterKnife.bind(this);

        //初始化资源文件
        SQLUtil.createDatabase(getApplicationContext());
        String jsonData = readAssetsTxt(getApplicationContext(), "data");
        Log.i("data", jsonData);
        sp = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE);
        editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
        editor.putString("old_data", jsonData);
        editor.apply();
        //        assets=getAssets();
        try {
            //            images = assets.list("");//获取asset目录下所有文件
            images = getAssets().list("btf");
            for (String imageName :
                    images) {
                Log.d("image", imageName);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //        String oldData=sp.getString("old_data","1");
        //        boolean flag = sp.getBoolean("info_changed", false);
        //        Log.d("response_sp_old_data", oldData);
        //        Log.d("response_sp_info", String.valueOf(flag));
        //        if (!flag) {
        //            try {
        //                queryFromServer();
        //            } catch (InterruptedException e) {
        //                e.printStackTrace();
        //            }
        //        }
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


    private void initButterfly() {
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

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        recyclerView.setLayoutManager(layoutManager);
        butterflyAdapter = new SearchButterflyInfoAdapter(butterflyInfo_copyList);
        recyclerView.setAdapter(butterflyAdapter);
        adapter = new HeaderAdapter(butterflyAdapter);
        recyclerView.setAdapter(adapter);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(butterflyDecoration = new SuspensionDecoration(this, butterflyInfo_copyList));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

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
            mIndexBar.setVisibility(View.INVISIBLE);//若列表数目小于5则不显示索引栏
        }

        butterflyDecoration.setmDatas(butterflyInfo_copyList);
    }

    /**
     * init SearchView
     */
    private void initSearchView() {
        searchView.setHint("请输入蝴蝶的中文名或拉丁名");
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
     * init IndexBar
     */
    private void initIndexBar() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mIndexBar.setmPressedShowTextView(mTvSideBarHint)//设置HintTextView
                .setNeedRealIndex(true)//设置需要真实的索引
                .setmLayoutManager(layoutManager);//设置RecyclerView的LayoutManager
        mIndexBar.setmSourceDatas(butterflyInfo_copyList)//设置数据
                .invalidate();
        butterflyAdapter.setDatas(butterflyInfo_copyList);
        if (butterflyInfo_copyList.size() < 5) {
            mIndexBar.setVisibility(View.INVISIBLE);//若列表数目小于5则不显示索引栏
        }

        butterflyDecoration.setmDatas(butterflyInfo_copyList);

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
                ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
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

    /**
     * 读取assets下的txt文件，返回utf-8 String
     *
     * @param context
     * @param fileName 不包括后缀
     * @return
     */
    public static String readAssetsTxt(Context context, String fileName) {
        try {
            //Return an AssetManager instance for your application's package
            InputStream is = context.getAssets().open(fileName + ".txt");
            int size = is.available();
            // Read the entire asset into a local byte buffer.
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            // Convert the buffer into a string.
            String text = new String(buffer, "utf-8");
            // Finally stick the string into the text view.
            return text;
        } catch (IOException e) {
            // Should never happen!
            //            throw new RuntimeException(e);
            e.printStackTrace();
        }
        return "读取错误，请检查文件名";
    }

    private boolean queryFromServer() throws InterruptedException {
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responeData = response.body().string();
                editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
                editor.putString("old_data", responeData);
                try {
                    readFlag = HttpAction.parseJSONWithGSON(responeData + "^+");
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                if (readFlag) {
                    Log.d("name_read_Flag", String.valueOf(readFlag));
                    editor.putBoolean("info_changed", true);
                } else {
                    editor.putBoolean("info_changed", false);
                }
                editor.apply();
                ButterflyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        //                        initButterfly();
                        butterflyAdapter.notifyDataSetChanged();
                        //                        butterflyDecoration.notifyAll();
                        initView();
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                ButterflyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ButterflyActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        //此处先使readFlag为true 日后还要根据服务器数据库的改动记录进行本地数据库的修改修改
        return readFlag = true;
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

    private void refresh() {
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responeData = response.body().string();
                sp = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE);
                String oldData = sp.getString("old_data", null);
                Log.d("response_old", oldData);
                Log.d("respone_new", responeData);
                if (!responeData.equals(oldData)) {

                    String handlerespone = handleResponse(oldData, responeData);
                    Log.d("response_handle", handlerespone);

                    boolean result = false;
                    try {
                        result = HttpAction.parseJSONWithGSON(handlerespone);
                        Log.d("loading", String.valueOf(result));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    if (result) {
                        ButterflyActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                editor = getSharedPreferences("com_example_butterfly_recognition_data", MODE_PRIVATE).edit();
                                editor.remove("old_data");
                                editor.putString("old_data", responeData);
                                editor.apply();
                                //                                initButterfly();
                                //                                butterflyAdapter.setDatas(butterflyInfo_copyList);
                                //                                butterflyAdapter.notifyDataSetChanged();
                                //                                adapter.notifyDataSetChanged();
                                initRecyclerView();
                                //                                initView();
                                mIndexBar.setmSourceDatas(butterflyInfo_copyList)//设置数据
                                        .invalidate();
                                butterflyAdapter.setDatas(butterflyInfo_copyList);
                                butterflyDecoration.setmDatas(butterflyInfo_copyList);
                                //                                initIndexBar();
                                //                                butterflyAdapter.notifyItemRangeInserted(0, butterflyInfo_copyList.size()-1);
                                swipeRefreshLayout.setRefreshing(false);
                                Toast.makeText(ButterflyActivity.this, "更新成功！", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                } else {
                    ButterflyActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //                            butterflyAdapter.notifyDataSetChanged();
                            swipeRefreshLayout.setRefreshing(false);
                            Toast.makeText(ButterflyActivity.this, "没有新数据！", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                ButterflyActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        swipeRefreshLayout.setRefreshing(false);
                        Toast.makeText(ButterflyActivity.this, "Fail!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private String handleResponse(String oldData, String newData) {
        String head = newData.substring(0, 35);
        String oldInfo = oldData.substring(35, oldData.length() - 2);
        String newInfo = newData.substring(35, newData.length() - 2);
        String[] oldlist = oldInfo.split("(?<=\\}),(?=\\{)");
        String[] newlist = newInfo.split("(?<=\\}),(?=\\{)");
        //        Pattern pattern = Pattern.compile("\\{\\}");
        //        Pattern p= Pattern.compile("ab.*c");
        //        Matcher m=p.matcher(str);
        //        while(m.find()){
        //            System.out.println(m.group());
        //        }
        String result = "";
        String finalResult = "";
        Log.d("response_result", String.valueOf(oldlist.length));
        Log.d("response_result", String.valueOf(newlist.length));
        if (oldlist.length == newlist.length) {
            for (int i = 0; i < newlist.length; i++) {
                if (oldlist[i].equals(newlist[i])) {
                    continue;
                }
                result = result + "," + newlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^=";
        } else if (oldlist.length < newlist.length) {
            for (int i = oldlist.length; i < newlist.length; i++) {
                result = result + "," + newlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^+";
        } else {
            for (int i = newlist.length; i < oldlist.length; i++) {
                result = result + "," + oldlist[i];
            }
            finalResult = head + result.substring(1, result.length()) + "]}" + "^-";
        }
        Log.d("response_result", finalResult);
        return finalResult;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(networkChangeReciver);
    }


    /**
     * 筛选逻辑
     *
     * @param butterflyinfos
     * @param query
     * @return
     */
    private List<ButterflyInfo_copy> filter(List<ButterflyInfo_copy> butterflyinfos, String query) {
        //        Log.e("test","调用filter方法");
        //        Log.e("test","butterflyinfos个数:"+butterflyinfos.size());
        //        query = query.toLowerCase();

        final List<ButterflyInfo_copy> filteredModelList = new ArrayList<>();
        for (ButterflyInfo_copy butterflyinfo : butterflyinfos) {
            final String name = butterflyinfo.getName();
            final String latinName = butterflyinfo.getLatinName();
            if (name.contains(query) || latinName.contains(query)) {
                //                Log.e("test_latinName", latinName);
                filteredModelList.add(butterflyinfo);
            }
        }
        //        Log.e("test","filteredModelList个数:"+filteredModelList.size());
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
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isAvailable()) {
            } else {
                Toast.makeText(context, "无法连接至服务器，请稍后再试", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        InfoDetail butterflyInfo = butterflyInfoList.get(position);
        Intent intent = new Intent(this, InfoActivity.class);
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
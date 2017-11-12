package com.example.butterflyrecognition.recycleView;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.butterflyrecognition.MainActivity;
import com.example.butterflyrecognition.R;
import com.example.butterflyrecognition.db.ButterflyInfo;

import org.litepal.crud.DataSupport;

/**
 * Created by Dr.P on 2017/10/10.
 */

public class InfoActivity extends AppCompatActivity {

    ImageView butterflypicture;
    TextView nametext;
    TextView latinNametext;
    TextView typetext;
    TextView featuretext;;
    TextView areatext;
    TextView protecttext;
    TextView raretext;
    TextView uniqueToChinatext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.info_layout);

        int id=getIntent().getIntExtra("butterflyNo",1);
        ButterflyInfo butterflyInfo = DataSupport.find(ButterflyInfo.class, id);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_info);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.image_collapsing_toolbar);
        FloatingActionButton fab_fruit_content = (FloatingActionButton) findViewById(R.id.fab_fruit_content);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        collapsingToolbarLayout.setTitle(butterflyInfo.getName());
        //        Glide.with(this).load(fruitImageId).into(fruitImageView);

        butterflypicture = (ImageView) findViewById(R.id.buttefly_big_image);
        nametext = (TextView) findViewById(R.id.name);
        latinNametext = (TextView) findViewById(R.id.latinName);
        typetext = (TextView) findViewById(R.id.type);
        featuretext = (TextView) findViewById(R.id.feature);
        areatext = (TextView) findViewById(R.id.area);
        protecttext = (TextView) findViewById(R.id.protect);
        raretext = (TextView) findViewById(R.id.rare);
        uniqueToChinatext = (TextView) findViewById(R.id.uniqueToChina);


        nametext.setText(butterflyInfo.getName());
        latinNametext.setText(butterflyInfo.getLatinName());
        typetext.setText(butterflyInfo.getType());
        featuretext.setText(butterflyInfo.getFeature());
        areatext.setText(butterflyInfo.getArea());
        protecttext.setText(butterflyInfo.getProtect());
        raretext.setText(butterflyInfo.getRare());
        uniqueToChinatext.setText(butterflyInfo.getUniqueToChina());
        //        butterflypicture.setImageURI(Uri.parse(butterflyInfo.getImagePath()));
        Glide.with(this).load(butterflyInfo.getImagePath()).into(butterflypicture);

        fab_fruit_content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InfoActivity.this, MainActivity.class);
                startActivity(intent);
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
        });
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

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
        super.onBackPressed();
    }
}

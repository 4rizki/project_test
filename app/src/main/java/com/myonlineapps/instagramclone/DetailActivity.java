package com.myonlineapps.instagramclone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;

public class DetailActivity extends AppCompatActivity {

    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);

        Intent intent = getIntent();
        String username = intent.getStringExtra("username");
        Toast.makeText(this, "username: "+username, Toast.LENGTH_SHORT).show();

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("UserImage");
        query.whereEqualTo("username",username);
        query.orderByDescending("createdAt");

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> objects, ParseException e) {
                if(e==null){
                    for(ParseObject obj : objects){
                        ParseFile file = (ParseFile) obj.get("image");
                        file.getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] data, ParseException e) {
                                if(e==null && data!=null){
                                    Bitmap image = BitmapFactory.decodeByteArray(data,0,data.length);

                                    ImageView imageView = new ImageView(getApplicationContext());

                                    imageView.setLayoutParams(new ViewGroup.LayoutParams(400, 400));

                                    imageView.setImageBitmap(image);

                                    linearLayout.addView(imageView);
                                }
                            }
                        });

                    }
                }
            }
        });

//        int imageCount=0;
//        try {
//            imageCount = query.count();
//            Toast.makeText(this, "count: "+imageCount, Toast.LENGTH_SHORT).show();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        for (int i=0;i<imageCount;++){
//
//        }

    }
}

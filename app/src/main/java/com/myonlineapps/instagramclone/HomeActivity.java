package com.myonlineapps.instagramclone;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    TextView textView;
    ListView listView;
    List<String> listUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        textView = (TextView) findViewById(R.id.textView);
        //Intent intent = getIntent();

//        if (intent.hasExtra("name")){
//            textView.setText("Hi "+ intent.getStringExtra("name")+", Welcome");
//        }

        ParseUser user = ParseUser.getCurrentUser();
        textView.setText("Welcome "+user.getUsername());

        listView = (ListView) findViewById(R.id.listView);

        //utk nangkap klik di item nya
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), DetailActivity.class);
                intent.putExtra("username",listUsername.get(i) );
                startActivity(intent);
            }
        });

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.orderByAscending("username");
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> objects, ParseException e) {
                if (e==null){
                    listUsername = new ArrayList();
                    for(ParseUser user: objects){
                        listUsername.add(user.getUsername());
                    }


                    ArrayAdapter adapter = new ArrayAdapter(
                            getApplicationContext(),
                            android.R.layout.simple_list_item_1,listUsername);
                    listView.setAdapter(adapter);
                }
                else{
                    Toast.makeText(HomeActivity.this, "e: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.upload){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else{
                    //Baca foto
                    GetPhoto();
                }
            }
            else{
                //Baca foto
                GetPhoto();
            }
        }
        else if (item.getItemId() == R.id.menuItem){
            Toast.makeText(this, "Menu item", Toast.LENGTH_SHORT).show();
        }
        else if (item.getItemId() == R.id.logout){
            Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();

            ParseUser.logOut();
            //Jika user klik logout, disamain dengan user klik back
            super.onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == 1){
            if(permissions.length>0){
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    //Baca foto
                    GetPhoto();
                }
            }
        }
    }
    public void GetPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==2 && resultCode==RESULT_OK && data!=null){
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                //convert bitmap menjadi byte (not Parse specific)
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG,100,stream);
                byte[] byteArray = stream.toByteArray();

                //specific Parse: mengubah byte menjadi file
                ParseFile file = new ParseFile("image.png", byteArray);

                ParseObject object = new ParseObject("UserImage");
                object.put("username", ParseUser.getCurrentUser().getUsername());
                object.put("image", file);

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e==null){
                            Toast.makeText(HomeActivity.this, "Berhasil", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(HomeActivity.this, "Gagal", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    //utk me override action pada saat tombol back ditekan (tomobl back di hp)
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        Toast.makeText(this, "Ga bisa yee", Toast.LENGTH_SHORT).show();
        // atau bikin pop up nanya are you sure?
    }
}

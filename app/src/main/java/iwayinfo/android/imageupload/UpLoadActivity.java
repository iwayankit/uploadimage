package iwayinfo.android.imageupload;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/**
 * Created by IWAY on 21-04-2017.
 */

public class UpLoadActivity extends AppCompatActivity implements View.OnClickListener{

    private Button upload_btn, select_btn;
    private EditText desc_et, name_et;
    private String Url = "http://iway.netai.net/uploadimage.php";
    private TextView imagepath,goback;
    private Bitmap bitmap;
    private int PICK_IMAGE_REQUEST = 1;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        upload_btn = (Button)findViewById(R.id.upload_btn);
        select_btn = (Button)findViewById(R.id.select_btn);
        desc_et = (EditText)findViewById(R.id.upload_et);
        name_et = (EditText)findViewById(R.id.name_et);
        imagepath = (TextView) findViewById(R.id.imagepath);
        goback = (TextView)findViewById(R.id.goback);
        goback.setOnClickListener(this);
        upload_btn.setOnClickListener(this);
        select_btn.setOnClickListener(this);

    }

    private String getDateandTime() {

        Calendar c = Calendar.getInstance();
        //System.out.println("Current time =&gt; "+c.getTime());
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());
        Log.e("uploadimage Activity", "time date "+formattedDate);
        return formattedDate;
    }

    public String image_to_string(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    private void uploadImage(){
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(this,"Uploading...","Please wait...",false,false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        Log.e("UploadActivity","onResponce"+s);
                        Toast.makeText(UpLoadActivity.this, ""+s, Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();
                        Log.e("UploadActivity","onError"+volleyError);
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = image_to_string(bitmap);
                String name = name_et.getText().toString().trim();
                String desc = desc_et.getText().toString().trim();

                Map<String,String> params = new Hashtable<String, String>();

                //Adding parameters
                params.put("image", image);
                params.put("name", name);
                params.put("desc",desc);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imagepath.setText(filePath.toString());
                imagepath.setVisibility(View.VISIBLE);
                //Setting the Bitmap to ImageView
                //imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onClick(View view) {
        if(view == upload_btn){
            uploadImage();
        } else if(view == select_btn){

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        } else if(view==goback){
            Intent intent = new Intent(UpLoadActivity.this, MainActivity.class);
            startActivity(intent);
        }

    }
    @Override
    public void onBackPressed() {

        Intent intent = new Intent(UpLoadActivity.this, MainActivity.class);
        startActivity(intent);
    }
}

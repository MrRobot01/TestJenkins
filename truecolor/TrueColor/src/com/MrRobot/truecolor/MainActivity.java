package com.MrRobot.truecolor;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.text.DateFormat.Field;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ShareActionProvider;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity {

	private static final int REQUEST_IMAGE_CAPTURE = 1;
	private static final int REQUEST_SELECT_PICTURE = 2;
	private String color;
	ImageButton capture;
	ImageButton load;
	TextView showColorName;
	TextView showColor;
	ImageView image;
	Bitmap bitmap;
	Bitmap photo;
	MenuItem item;
	ShareActionProvider shareActionProvider;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED); 
    
        capture = (ImageButton)findViewById(R.id.imageButton1);
        load = (ImageButton)findViewById(R.id.SaveButton);
        showColorName = (TextView)findViewById(R.id.showColorName);
        showColor = (TextView)findViewById(R.id.textView1);
        image = (ImageView)findViewById(R.id.imageView1);
          
       
        load.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
			
				/* clear cache before action */
				image.invalidate();
				image.setImageDrawable(null);
				image.refreshDrawableState();
				image.destroyDrawingCache();
				bitmap = null;
				
				Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				startActivityForResult(intent,REQUEST_SELECT_PICTURE);
				
				
			}
		});
         
image.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent motionEvent) {
					
				if(motionEvent.getAction() == MotionEvent.ACTION_DOWN || motionEvent.getAction() == MotionEvent.ACTION_MOVE){
			
					image.setDrawingCacheEnabled(true);
					image.buildDrawingCache(true);
					bitmap = image.getDrawingCache();

					int pixel = bitmap.getPixel((int)motionEvent.getX(),(int)motionEvent.getY());

					int r = Color.red(pixel);
					int g = Color.green(pixel);
					int b = Color.blue(pixel);
					
					float[] hsv = new float[3];
					
					Colors c = new Colors();
					color = c.getColor(r, g, b, hsv);
				           
					showColor.setBackgroundColor(Color.rgb(r,g,b));
					showColorName.setText(color);
		
				}
				
				return false;
				}
		});


        //if(!hasCamera()){
       // 	capture.setEnabled(false);
       // }
        
    }

    @Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//Share Button
		getMenuInflater().inflate(R.menu.main, menu);
		item = menu.findItem(R.id.Share);
		shareActionProvider = (ShareActionProvider)item.getActionProvider();
        setIntent("192.168.0.115:9090/truecolor/");

		return  true;
		
	}
	
    /*check if the device has camera*/
    private boolean hasCamera(){
    	
    	return getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY);
    	
    }
    
    public void launchCamera(View view){
    	/*Clear cache before action */
    	image.invalidate();
    	image.setImageDrawable(null);
    	image.refreshDrawableState();
    	image.destroyDrawingCache();
    	bitmap = null;
    	
    	Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
    	startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);
    	
    }
    
    
    
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		super.onActivityResult(requestCode, resultCode, data);
		
		bitmap = null;
		
		if(requestCode == REQUEST_IMAGE_CAPTURE){
			Bundle extras = data.getExtras();
			photo = (Bitmap)extras.get("data");
			image.setImageBitmap(photo);
			
		}
		
		if(requestCode==REQUEST_SELECT_PICTURE){
			Uri selectedImage = data.getData();
			String[] filepathColum = {MediaStore.Images.Media.DATA};
			Cursor cursor = getContentResolver().query(selectedImage, filepathColum, null, null, null);
			cursor.moveToFirst();
			int columIndex = cursor.getColumnIndex(filepathColum[0]);
			String picturePath = cursor.getString(columIndex);
			cursor.close();
			image.setImageBitmap(BitmapFactory.decodeFile(picturePath));
		}
		
	}

	private void setIntent(String text){
		//Post from share button
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_TEXT, text);
		shareActionProvider.setShareIntent(intent);
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
    	switch(item.getItemId()){
    	
    	case R.id.About:
    		Toast.makeText(getApplicationContext(), "About", Toast.LENGTH_SHORT).show();
    		String message  = "App name: True Color \nCreated By: Mr.Robot \nHackathon Serres 2016 TEICM ";
			AlertDialog.Builder ABuilder =  new AlertDialog.Builder(MainActivity.this);
			ABuilder.setMessage(message)
					.setCancelable(false)
				    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
							
							
						}
					});
			AlertDialog alert = ABuilder.create();
			alert.setTitle("About");
			alert.show();
    		break;
    	case R.id.Help:
    		Toast.makeText(getApplicationContext(), "Help", Toast.LENGTH_SHORT).show();
    		String message1= "option 1: Capture image and tap the screen to get the color" +
    				"\noption 2: load picture from file" +
    				"\noption 3: Share the app";
    		AlertDialog.Builder BBuilder =  new AlertDialog.Builder(MainActivity.this);
			BBuilder.setMessage(message1)
					.setCancelable(false)
				    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							
							dialog.cancel();
							
							
						}
					});
			AlertDialog alert1 = BBuilder.create();
			alert1.setTitle("About");
			alert1.show();
    	
    		break;
    	case R.id.Save:
    		Toast.makeText(getApplicationContext(), "Save", Toast.LENGTH_SHORT).show();
    		Toast.makeText(getApplicationContext(), "Save Successful", Toast.LENGTH_SHORT).show();
			Bitmap bit = Bitmap.createBitmap(image.getDrawingCache());
			
		try {

			bit.compress(CompressFormat.PNG, 100, new FileOutputStream(Environment.getExternalStorageDirectory()+"/TRUE_COLOR.png"));
		} catch (FileNotFoundException e) {
			
			e.printStackTrace();
		}
			
			break;
	}
			return super.onOptionsItemSelected(item);

 
    
    		
    	
        
       
    }
}

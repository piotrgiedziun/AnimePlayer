package eu.isweb.animeplayer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;

public class FlashPlayerManager {
	Context context;
	
	public FlashPlayerManager(Context c) {
		this.context = c;
	}
	
	private boolean isInstalled() {
		try{
		     context.getPackageManager().getApplicationInfo("com.adobe.flashplayer", 0 );
		     return true;
		} catch( PackageManager.NameNotFoundException e ){
			 return false;
		}
	}
	
	public void install() {
		if(!isInstalled()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(context);
			builder.setMessage("This software require flashplayer. Do you want to install it now?")
				   .setTitle("Missing FlashPlayer")
			       .setCancelable(false)
			       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
			           @Override
					public void onClick(DialogInterface dialog, int id) {
			        	   copyFlashPlayer();
			           }
			       })
			       .setNegativeButton("No. I will use limted version", new DialogInterface.OnClickListener() {
			           @Override
					public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			           }
			       });
			AlertDialog alert = builder.create();
			alert.show();
		}
	}
	
	public void copyFlashPlayer() {
		 File file=new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "com.adobe.flashplayer-2.apk");
	
		try {
			int length = 0;
			file.createNewFile();
			InputStream inputStream = context.getAssets().open(
					"com.adobe.flashplayer-2.apk");
			FileOutputStream fOutputStream = new FileOutputStream(file);
			byte[] buffer = new byte[inputStream.available()];
			while ((length = inputStream.read(buffer)) > 0) {
				fOutputStream.write(buffer, 0, length);
			}
			fOutputStream.flush();
			fOutputStream.close();
			inputStream.close();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	
		Intent intent = new Intent(Intent.ACTION_VIEW);
	    intent.setDataAndType(Uri.fromFile(new File(Environment.getExternalStorageDirectory() + java.io.File.separator + "com.adobe.flashplayer-2.apk")), "application/vnd.android.package-archive");
	    context.startActivity(intent);
	 
	}
	
	
}

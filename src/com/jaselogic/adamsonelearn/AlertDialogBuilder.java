package com.jaselogic.adamsonelearn;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogBuilder {
	public static class NeutralDialog {
		
		public NeutralDialog(String title, String message, Context context) {	
			AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
			mBuilder.setTitle(title);
			mBuilder.setMessage(message);
			mBuilder.setCancelable(true);
			mBuilder.setNeutralButton(android.R.string.ok, 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dialog.cancel();
						}
					});
			
			mBuilder.create().show();
		}
	}
}

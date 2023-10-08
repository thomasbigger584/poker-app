package com.twb.pokerapp.ui.dialog;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ProgressBar;

import androidx.appcompat.app.AlertDialog;

import com.twb.pokerapp.R;

public class DialogHelper {

    private DialogHelper() {

    }

    public static AlertDialog createLoadingSpinner(Activity activity) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
        View alertView = activity.getLayoutInflater().inflate(R.layout.loading_dialog_spinner, null);

        ProgressBar progressBar = alertView.findViewById(R.id.loadingSpinner);
        progressBar.setIndeterminate(true);
        dialogBuilder.setView(alertView);
        dialogBuilder.setCancelable(false);

        AlertDialog alertDialog = dialogBuilder.create();
        Window window = alertDialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        return alertDialog;
    }

    public static void show(AlertDialog alertDialog) {
        if (!alertDialog.isShowing()) {
            alertDialog.show();
        }
    }

    public static void dismiss(AlertDialog alertDialog) {
        if (alertDialog.isShowing()) {
            alertDialog.dismiss();
        }
    }

    public static AlertDialog createMessageDialog(Activity activity) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("This is a message dialog");
        alertDialogBuilder.setPositiveButton("OK", (dialog, which) -> dialog.dismiss());
        return alertDialogBuilder.create();
    }
}

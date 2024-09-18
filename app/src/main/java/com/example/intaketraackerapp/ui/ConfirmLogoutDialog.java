package com.example.intaketraackerapp.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.intaketraackerapp.R;
import com.example.intaketraackerapp.ui.login.LoginActivity;

public class ConfirmLogoutDialog extends DialogFragment {

    public interface ConfirmLogoutDialogListener {
        void onConfirmLogout();
    }

    private ConfirmLogoutDialogListener listener;

    public ConfirmLogoutDialog(ConfirmLogoutDialogListener listener) {
        this.listener = listener;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_confirm_logout, null);

        builder.setView(view)
                .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        listener.onConfirmLogout();
                        Toast.makeText(getActivity(), "Session closed successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ConfirmLogoutDialog.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}

package com.ualr.firetask.tasks;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ualr.firetask.R;

public class ConfirmDeleteDialog extends DialogFragment {
    private ConfirmDeleteDialogListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if (context instanceof ConfirmDeleteDialogListener) {
            mListener = (ConfirmDeleteDialogListener) context;
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.confirm_delete);
        builder.setNegativeButton(R.string.cancel_delete_btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dismiss();
            }
        });
        builder.setPositiveButton(R.string.delete_btn_label, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mListener.onDeleteConfirm();
            }
        });

        return builder.create();
    }

    public interface ConfirmDeleteDialogListener {
        void onDeleteConfirm();
    }
}

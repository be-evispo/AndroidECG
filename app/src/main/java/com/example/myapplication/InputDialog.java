package com.example.myapplication;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDialogFragment;

public class InputDialog extends AppCompatDialogFragment {
    private EditText edit_IPaddress;
    private InputDialogListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.input_ipaddr, null);

        builder.setView(view)
                .setTitle("IP Address")
                .setNegativeButton("Cencel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String IPaddr = edit_IPaddress.getText().toString();
                        listener.applyText(IPaddr);
                    }
                });
        edit_IPaddress = view.findViewById(R.id.edit_IPaddr);

        return builder.create();

    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (InputDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "Must Input IP address");
        }
    }

    public interface InputDialogListener{
        String applyText (String IPaddr);
    }
}

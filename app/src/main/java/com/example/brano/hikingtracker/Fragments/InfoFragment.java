package com.example.brano.hikingtracker.Fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.brano.hikingtracker.R;
import com.example.brano.hikingtracker.Session;


/**
 * A simple {@link Fragment} subclass.
 */
public class InfoFragment extends Fragment {

    private String name;
    private String password;
    private int id;

    EditText edttName;
    EditText edtttPassword;
    Button btnLogin;

    Session session;

    public InfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info, container, false);

        btnLogin = view.findViewById(R.id.buttonLogin);
        edttName = view.findViewById(R.id.editTextName);
        edtttPassword = view.findViewById(R.id.editTextPassword);

        if (!Session.isSessionStarted()) {
            Session.createSession(getContext());
        }
        session = Session.getInstance();

        if (session.getLoginStatus() == false) {
            btnLogin.setText("Login");
            edttName.setEnabled(true);
            edtttPassword.setEnabled(true);
        } else {
            btnLogin.setText("Logout");
            edttName.setText(session.getUserName());
            edttName.setEnabled(false);
            edtttPassword.setEnabled(false);
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (session.getLoginStatus() == false) {

                    if (edttName.getText().toString().length() < 0) {
                        Toast.makeText(getContext(), "Enter user name", Toast.LENGTH_LONG).show();
                        return;
                    }
                    session.userLogin(1, edttName.getText().toString());
                    btnLogin.setText("Logout");
                    edttName.setText(session.getUserName());
                    edttName.setEnabled(false);
                    edtttPassword.setEnabled(false);
                    Toast.makeText(getContext(), "Logged in", Toast.LENGTH_LONG).show();
                } else {
                    session.userLogout();
                    btnLogin.setText("Login");
                    edttName.setEnabled(true);
                    edtttPassword.setEnabled(true);
                    Toast.makeText(getContext(), "Logged out", Toast.LENGTH_LONG).show();
                }


            }
        });

        return view;
    }

}

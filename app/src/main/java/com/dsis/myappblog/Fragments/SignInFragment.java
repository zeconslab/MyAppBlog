package com.dsis.myappblog.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Explode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dsis.myappblog.AuthActivity;
import com.dsis.myappblog.Constants;
import com.dsis.myappblog.DashboardActivity;
import com.dsis.myappblog.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class SignInFragment extends Fragment {

    private View view;
    private TextInputLayout layoutEmail, layoutPassword;
    private TextInputEditText txtEmail, txtPassword;
    private TextView txtSignUp;
    private ProgressBar progressBar;
    private Button btnSignIn;


    public SignInFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        init();
        return view;
    }

    private void init(){
        layoutEmail = view.findViewById(R.id.txtLayoutEmailSignIn);
        layoutPassword = view.findViewById(R.id.txtLayoutPasswordSignIn);

        txtEmail = view.findViewById(R.id.txtEmailSignIn);
        txtPassword = view.findViewById(R.id.txtPasswordSignIn);

        btnSignIn = view.findViewById(R.id.btnSignIn);
        txtSignUp = view.findViewById(R.id.txtSignUp);

        progressBar = view.findViewById(R.id.progressBarSignIn);
        progressBar.setVisibility(view.INVISIBLE);
        progressBar.bringToFront();


        txtSignUp.setOnClickListener(v->{
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frameAuthContainer, new
                    SignUpFragment()).commit();
        });

        btnSignIn.setOnClickListener(v->{
            if (validate()){
                progressBar.setVisibility(view.VISIBLE);
                btnSignIn.setEnabled(false);
                login();
            }
        });

        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (!txtEmail.getText().toString().isEmpty()) {
                    layoutEmail.setErrorEnabled(false);
                    if (isEmail(txtEmail.getText().toString())) {
                        layoutEmail.setErrorEnabled(false);
                    }
                    else {
                        layoutEmail.setErrorEnabled(true);
                        layoutEmail.setError(getText(R.string.email_request));
                    }
                }
                else {
                    layoutEmail.setErrorEnabled(true);
                    layoutEmail.setError(getText(R.string.email_required));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        txtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (txtPassword.getText().toString().length() > 7) {
                    layoutPassword.setErrorEnabled(false);
                }
                else {
                    layoutPassword.setErrorEnabled(true);
                    layoutPassword.setError(getText(R.string.password_length));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

    }

    private boolean validate() {
        if (txtEmail.getText().toString().isEmpty()) {
            layoutEmail.setErrorEnabled(true);
            layoutEmail.setError(getText(R.string.email_required));
            return false;
        }
        if (txtPassword.getText().toString().length() == 0 ) {
            layoutPassword.setErrorEnabled(true);
            layoutPassword.setError(getText(R.string.password_required));
            return false;
        }
        return true;
    }

    public boolean isEmail(String email) {
        return Pattern.compile("^((?!\\.)[\\w_.]*[^.])(@\\w+)(\\.\\w+(\\.\\w+)?[^.\\W])").matcher(email).matches();
    }

    private void login() {
        StringRequest request = new StringRequest(Request.Method.POST, Constants.LOGIN, response -> {
            // Conxion establecida
            try {
                JSONObject object = new JSONObject(response);
                if (object.getBoolean("status")) {
                    //Encapsulamos el objeto usuario
                    JSONObject user = object.getJSONObject("user");

                    //se crean la preferencia donde se almacenaran los datos
                    SharedPreferences userPref = getActivity().getApplicationContext().getSharedPreferences("user", getContext().MODE_PRIVATE);
                    SharedPreferences.Editor editor = userPref.edit();

                    editor.putString("token", object.getString("access_token"));
                    editor.putString("name", user.getString("name"));
                    editor.putString("lastname", user.getString("lastname"));
                    editor.putString("photo", user.getString("photo"));
                    editor.putBoolean("isLoggedIn", true);
                    editor.apply();

                    // exito
                    Toast.makeText(getContext(), getText(R.string.successLogin), Toast.LENGTH_LONG).show();
                    btnSignIn.setEnabled(true);
                    progressBar.setVisibility(view.INVISIBLE);
                    startActivity(new Intent(((AuthActivity)getContext()), DashboardActivity.class));
                    ((AuthActivity) getContext()).finish();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }, error -> {
            //En caso de falla de conexion
            error.printStackTrace();
            Toast.makeText(getContext(), getText(R.string.failLogin) + "\nVerifique sus datos", Toast.LENGTH_SHORT).show();
            btnSignIn.setEnabled(true);
            progressBar.setVisibility(view.INVISIBLE);
        }){
            // Agregar parametros
            @Nullable
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> map = new HashMap<>();
                map.put("email", txtEmail.getText().toString().trim());
                map.put("password", txtPassword.getText().toString().trim());
                return map;
            }
        };
        RequestQueue queue = Volley.newRequestQueue(getContext());
        queue.add(request);
    }
}
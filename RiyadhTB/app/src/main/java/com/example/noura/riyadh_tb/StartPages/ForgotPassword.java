package com.example.noura.riyadh_tb.StartPages;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.noura.riyadh_tb.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class ForgotPassword extends AppCompatActivity {

    private Button sendPasswordButton;
    private EditText EmailEditText;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgot_password);
        mAuth=FirebaseAuth.getInstance();


        sendPasswordButton=(Button)findViewById(R.id.forgetPasswordButton);
        EmailEditText=(EditText)findViewById(R.id.Email_editText);

        sendPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String UserEmail=EmailEditText.getText().toString();

                if(TextUtils.isEmpty(UserEmail)){
                    Toast.makeText(ForgotPassword.this,"Plasse write a valid Email Address first... ",Toast.LENGTH_SHORT).show();

                }else{
                    mAuth.sendPasswordResetEmail(UserEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if(task.isSuccessful())
                            {
                                Toast.makeText(ForgotPassword.this,"plasse check your email to reset the password",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPassword.this,Login.class));
                            }
                            else {
                                String message=task.getException().getMessage();
                                Toast.makeText(ForgotPassword.this,"Error Occured: "+message,Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });


    }
}


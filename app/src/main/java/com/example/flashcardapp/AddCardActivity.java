package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class AddCardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_card);

        findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener(){
            // clicking cancel button brings user back to main page
            @Override
            public void onClick(View v){
                finish();
            }});

        // code for clicking on save button
        // sends EditView question and EditView answer information back to the main page
        findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // get text from user's inputted question and answer
                EditText new_question = findViewById(R.id.add_question);
                EditText new_answer = findViewById(R.id.add_answer);
                // convert question/answer's text value
                String question_string = new_question.getText().toString();
                String answer_string = new_answer.getText().toString();

                Intent data = new Intent(AddCardActivity.this, MainActivity.class);
                // input new question/answer string with respective keys
                data.putExtra("new_question", question_string);  // key = "new_question", value = question_string (text value)
                data.putExtra("new_answer", answer_string);
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
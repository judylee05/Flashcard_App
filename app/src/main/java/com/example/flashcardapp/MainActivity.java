package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView questionTextView = findViewById(R.id.flashcard_question);
        TextView answerTextView = findViewById(R.id.flashcard_answer);


        questionTextView.setOnClickListener(new View.OnClickListener() {
            // when user clicks on the question, will show the answer
            @Override
            public void onClick(View view) {
                questionTextView.setVisibility(View.INVISIBLE);
                answerTextView.setVisibility(View.VISIBLE);
            }
        });

        answerTextView.setOnClickListener(new View.OnClickListener(){
            // when user clicks on the answer, will show the question
            @Override
            public void onClick(View view){
                questionTextView.setVisibility(View.VISIBLE);
                answerTextView.setVisibility(View.INVISIBLE);
            }
        });

        findViewById(R.id.addCardButton).setOnClickListener(new View.OnClickListener() {
            // when user clicks on the "add" button at the bottom right corner, will go to add_card screen
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddCardActivity.class);
                MainActivity.this.startActivityForResult(intent, 100);
            }
        });
    }

    // when user sends back a new question/answer from AddCardActivity and pressed the save button
    // changes the MainActivity page's current question/answer to the new one
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if data == null, means that user went from MainActivity -> AddCardActivity -> clicked cancel button -> going back to MainActivity with no data
        // data is only sent if the "save" button is pressed, so must run the following code only if the "save" button is pressed and not the cancel
        if (requestCode == 100 && data != null) { // this 100 needs to match the 100 we used when we called startActivityForResult!
            String new_question = data.getExtras().getString("new_question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String new_answer = data.getExtras().getString("new_answer");

            TextView current_question = findViewById(R.id.flashcard_question);
            TextView current_answer = findViewById(R.id.flashcard_answer);

            current_question.setText(new_question);
            current_answer.setText(new_answer);
        }


    }
}
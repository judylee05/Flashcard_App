package com.example.flashcardapp;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // initialize the database code
    FlashcardDatabase flashcardDatabase;

    // declare variable to hold list of Flashcards
    List<Flashcard> allFlashcards;

    // initialize flashcard index
        // will increase by +1 when next button is pressed
        // will be reset once user reaches end of card
    // change to index = 0 if not implementing feature where last card added is seen at app launch
    int current_card_index = -1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        flashcardDatabase = new FlashcardDatabase(this);
        allFlashcards = flashcardDatabase.getAllCards();

        if (allFlashcards != null && allFlashcards.size() > 0) {
            // when app is initialized, will show the most recently added question in the main page
            ((TextView) findViewById(R.id.flashcard_question)).setText(allFlashcards.get(allFlashcards.size()-1).getQuestion());
            ((TextView) findViewById(R.id.flashcard_answer)).setText(allFlashcards.get(allFlashcards.size()-1).getAnswer());
        }

        TextView questionTextView = findViewById(R.id.flashcard_question);
        TextView answerTextView = findViewById(R.id.flashcard_answer);

        questionTextView.setOnClickListener(new View.OnClickListener() {
            // when user clicks on the question, will show the answer
            @Override
            public void onClick(View view) {
                // get the center for the clipping circle
                int cx = answerTextView.getWidth() / 2;
                int cy = answerTextView.getHeight() / 2;

                // get the final radius for the clipping circle
                float finalRadius = (float) Math.hypot(cx, cy);

                // create the animator for this view (the start radius is zero)
                Animator anim = ViewAnimationUtils.createCircularReveal(answerTextView, cx, cy, 0f, finalRadius);

                questionTextView.setVisibility(View.INVISIBLE);
                answerTextView.setVisibility(View.VISIBLE);

                anim.setDuration(1500);
                anim.start();
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
                overridePendingTransition(R.anim.right_in, R.anim.left_out);
            }
        });

        findViewById(R.id.nextCardButton).setOnClickListener(new View.OnClickListener() {
            // when user clicks on the "next" button, will show the next card (i.e. card that was added before the current one on main page)
            // will show the question part always first - if user clicks "next" button while on the answer portion of the current card,
            // the next button will show next question portion of the card
            @Override
            public void onClick(View view) {
                // don't allow user to go to next card if there's no cards in the database
                if (allFlashcards.size() == 0) {
                    return;
                }

                // increase current_card_index += 1 for the next card in database
                current_card_index ++;

                // if user reached last card, will prevent getting IndexOutofBoundsError by not doing anything with next button
                if (current_card_index >= allFlashcards.size()){
                    // set index back to 0
                    current_card_index = 0;
                }

                // will show next card only if user has not already reached the last card
                allFlashcards = flashcardDatabase.getAllCards();
                Flashcard current_flashcard = allFlashcards.get(current_card_index);

                questionTextView.setText(current_flashcard.getQuestion());
                answerTextView.setText(current_flashcard.getAnswer());

                // ensure that user sees the question side of the next card
                questionTextView.setVisibility(View.VISIBLE);
                answerTextView.setVisibility(View.INVISIBLE);
                System.out.println(50);

                // setup animations for when next card is shown
                final Animation leftOutAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.left_out);
                final Animation rightInAnim = AnimationUtils.loadAnimation(view.getContext(), R.anim.right_in);

                leftOutAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        leftOutAnim.setDuration(1500);
                        leftOutAnim.start();
                        System.out.println(101);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        // this method is called when the animation is finished playing
                        rightInAnim.setDuration(1500);
                        findViewById(R.id.flashcard_question).startAnimation(rightInAnim);
                        System.out.println(201);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                        // we don't need to worry about this method
                    }
                });
                // once user successfully clicks on "next" button, play animation chain
                    // starts with leftOutAnimation and then once that ends, starts rightInAnimation
                findViewById(R.id.flashcard_question).startAnimation(leftOutAnim);

            }
        });
    }

    // when user sends back a new question/answer from AddCardActivity and pressed the save button
    // changes the MainActivity page's current question/answer to the new one
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if data == null, means that user went from MainActivity -> AddCardActivity -> clicked cancel button -> going back to MainActivity with no data
        // data is only sent if the "save" button is pressed, so must run the following code only if the "save" button is pressed and not the cancel
        super.onActivityResult(requestCode,resultCode,data);
        if (requestCode == 100 && data != null) { // this 100 needs to match the 100 we used when we called startActivityForResult!
            String new_question = data.getExtras().getString("new_question"); // 'string1' needs to match the key we used when we put the string in the Intent
            String new_answer = data.getExtras().getString("new_answer");

            TextView current_question = findViewById(R.id.flashcard_question);
            TextView current_answer = findViewById(R.id.flashcard_answer);

            current_question.setText(new_question);
            current_answer.setText(new_answer);

            // insert new card data into the database
            flashcardDatabase.insertCard(new Flashcard(new_question, new_answer));
            // save all cards from database into the local list (holding all flashcard data)
            allFlashcards = flashcardDatabase.getAllCards();
        }


    }
}
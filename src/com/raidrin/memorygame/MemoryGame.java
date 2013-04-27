package com.raidrin.memorygame;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 
 * FileName: LotteryNumberPicker.java
 *  
 * @author Aldrin Jerome Almacin 
 * Date: October 13, 2012 
 * Description: A game that tests the memory of the user by matching the cards.
 * 				The game can be played as many times as wanted by the user. 
 * 
 */
public class MemoryGame extends Activity {
	// Constants used by the Program, static is used to avoid
		// making a copy of the variable in each instance (Just good programming practice).
	private static final int MAX_TIME = 30;				// The maximum time that the game will run
	private static final int COLUMN_COUNT = 4;			// Count of columns the game has.
	private static final int NUM_CARD_TYPE = 13;		// The count of the type of cards. 1 - 13
//	private static final int TOTAL_CARD_NUM = 52;		// The total distinct card numbers the application can choose from
	private static final int DUPLICATE_COUNT = 2;		// The number of card duplicates.
	private static final int NUM_OF_DISTINCT_CARDS = 8;	// The number of distinct cards that the application will use in each game.
	
	private static final String LOG_NAME = "AndroidType";
	
	private int score;			// A variable that stores all the score of the user
	private int secondsLeft;	// Seconds left in the game thats currently on
	private boolean firstCard;	// States whether the card thats currently selected is the first card
	private boolean cardsOpen;	// States whether both cards are still open

	private TextView scoreTextView;			// TextView that stores the current score of the user.
	private TextView messageTextView;		// TextView that shows a message to the user about its card interactions.
	private TextView timeLeftValueTextView;	// TextView that stores how much time is left in the game. 
	private LinearLayout cardsLinearLayout;	// The LinearLayout that holds all the card rows in the game.
	
	private Card firstCardSelected;	// A reference to the first card that's selected is stored here.

	private Handler handler;					// The handler that is used by the application to delay call or call anonymous runnable classes
	private CountdownThread countdownThread;	// An inner class created to make a CountDown.
	private ArrayList<String> cardFileNames;	// The filenames of the cards that were currently in use in the game.
	private ArrayList<String> gameCardNames;	// A list containing all the filenames that were all distinct.
	
	
	/**
	 * When the application starts/created, onCreate method is executed.
	 * Therefore, all initializations are done in this method.
	 * 
	 * @param savedInstanceState The saved state of the application
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) 
	{
		// In order for this override to be valid. A call to the super method must be done.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main); // Set the content view to main which is the app's main layout.
        
        // Make a reference from the resources to this java program and cast them to their appropriate View types
		cardsLinearLayout = (LinearLayout) findViewById(R.id.cardsLinearLayout);
		timeLeftValueTextView = (TextView) findViewById(R.id.timeLeftValueTextView);
		scoreTextView = (TextView)findViewById(R.id.scoreValueTextView);
		messageTextView = (TextView) findViewById(R.id.messageTextView);

		// Instantiate Objects that will be needed by the application
		handler = new Handler();				// instantiate handler
		gameCardNames = new ArrayList<String>();// instantiate gameCardNames
        cardFileNames = new ArrayList<String>();// instantiate cardFileNames
		countdownThread = new CountdownThread();// instantiate the countdownThread
        
        firstCard = true;			// set the firstCard to true to make sure that when the game starts, firstCard will be the firstCard
        cardsOpen = false;			// set the cardsOpen to false because when the game starts, both cards are not open
        firstCardSelected = null;	// set the firstCardSelected to null because there is no card selected yet.
		score = 0;					// score is set to 0 because the user doesn't have a score yet

		// Call the showAlertDialog method with the appropriate string values used at the game start
		showAlertDialog(getString(R.string.start), 
				getString(R.string.start_msg), 
				getString(R.string.start),alertDialogInitialOnClickListener);
		
		// make sure countdownThread is the only Thread that runs the processes inside synchronized
		synchronized (countdownThread) 
		{
			countdownThread.start();			// Start the Thread.
			countdownThread.setWaiting(true);	// Pause the Thread
		}	// end of timerThread's synchronize
		
    } // End of onCreate method

	/**
	 * Sets the filenames of the images as a collection of strings.
	 * Makes the access of image filenames easier. 
	 */
	private void setAllCardFilenames() 
	{
		cardFileNames.clear();	// make sure that the cardFileNames collection is always empty before
									// setting the cardFileNames
		// The suit array represents the suits in a playing card deck
		// c = clover, s = spade, h = heart, d = diamond
        char[] suit = {'c','s','h','d'};
        
        // build card's filenames by concatenating their card number and their suit
        	// and add .png at the bottom. (this is how the files in the assets folder are named)
        	// then add each of the generated String in the cardFileNames ArrayList
        for(int i=1;i<=NUM_CARD_TYPE;i++)
        	for(int a=0;a<suit.length;a++)
        		cardFileNames.add(i + Character.toString(suit[a]) + ".png");
	} // End of setAllCardFilenames method

	/**
	 * Sets the gameCardFilenames randomly from the cardFileNames collection.
	 * Duplicates the cards too and add them to the gameCardNames collection.
	 */
	private void setGameCardFilenames() {
		gameCardNames.clear();	// clear the collection to make sure that the collection is initially empty
									// before adding card names on it
		Random rand = new Random();	// declare and initialize the Random class that generates a random number
										// Used to get a random card from the deck
		// take a distinct card the same times as the value of NUM_OF_DISTINCT_CARDS
		for(int i = 0;i<NUM_OF_DISTINCT_CARDS;i++)
		{
			// Grab a cardFileName from the cardFilenames collection randomly
			String cardFilename = cardFileNames.remove(rand.nextInt(cardFileNames.size()));
			// Create duplicates by the value of the DUPLICATE_COUNT then add it in the gameCardNames collection
			for(int a=0;a<DUPLICATE_COUNT;a++)
				gameCardNames.add(cardFilename);
		} // End of NUM_OF_DISTINCT_CARDS for loop
	} // End of setGameCardFilenames method

	/**
	 * Randomly shuffles the cardFileNames collection.
	 */
	private void shuffleCardFilenames() {
		Random rand = new Random();	// declare and initialize the Random class that generates a random number
		final int SHUFFLE_COUNT = 100;	// The times that the order of the gameCardNames is changed
		// To shuffle the gameCardNames collection, randomly remove a card from the collection and 
			// add it to the beginning. Shuffle it as many times as the value of SHUFFLE_COUNT
		for(int i = 0;i<SHUFFLE_COUNT ;i++)
			gameCardNames.add(gameCardNames.remove(rand.nextInt(gameCardNames.size())));
	} // End of shuffleCardFilenames method

	/**
	 * Create the cards and add them on the cardsLinearLayout.
	 */
	private void createCards() {
		// For each LinearLayout child of cardsLinearLayout, add a Card object which is a subclass of ImageButton
		for(int i=0;i<cardsLinearLayout.getChildCount();i++)
		{
			// Get the cardsLinearLayout with the current index and save it as a tempLinearLayout variable
			LinearLayout tempLinearLayout = ((LinearLayout)cardsLinearLayout.getChildAt(i));
			// If tempLinearLayout is existing, continue the adding of Cards.
			if(tempLinearLayout != null)
				// Add cards to the tempLinearLayout with the amount of columns it has
				for(int a=0;a<COLUMN_COUNT;a++)
					// Add the cards as a view. The context will be this activity.
					tempLinearLayout.addView(new Card(this));
		} // End of cardsLinearLayout for loop
	} // End of createCards methods

	/**
	 * 	Set the card's in the cardsLinearLayout's properties and listeners.
	 */
	private void setImageButtons() 
	{
		// Initialize the gameCardNamesIndex. each time a card gets its filename,
			// the gameCardNamesIndex will increment by 1
		int gameCardNamesIndex = 0;
		// For each child of the cardsLinearLayout, get a reference of it
			// and save it on a tempLinearLayout
		for(int i = 0; i < cardsLinearLayout.getChildCount();i++)
		{
			LinearLayout tempLinearLayout = (LinearLayout)cardsLinearLayout.getChildAt(i);
			// go through each child of the tempLinearLayout and use its initial methods
			for(int a=0;a<tempLinearLayout.getChildCount();a++)
			{
				Card tempCard = (Card)tempLinearLayout.getChildAt(a);
				if(tempCard != null)
				{
					tempCard.initialState();
					tempCard.setFileName(gameCardNames.get(gameCardNamesIndex));
					tempCard.setOnClickListener(cardClickedListener);
					gameCardNamesIndex++;	// Increment the index
				} // End of tempCard If
			} // End of tempLinearLayout for loop
		} // End of cardsLinearLayout for loop
	} // End of setImageButtons method
	
	/**
	 * The click listener that listens to when a card is clicked.
	 * Also used to show and hide cards, make card glow and unglow, 
	 * and checking if two cards match.
	 * If matched, the two cards will be invisible and the score will be
	 * updated.
	 */
	private OnClickListener cardClickedListener = new OnClickListener() 
	{
		/**
		 * Overridden onClick method of the OnClickListener anonymous class.
		 */
		@Override
		public void onClick(View v) 
		{
			// Check if the cards are currently open.
			// If cards are still open, the cards shouldn't be allowed
				// to open. 
			// Otherwise, it will show three cards open at the same time.
			if(!cardsOpen)
			{
				// The view that got clicked is casted to Card and saved in a variable
				final Card tempCard = (Card)v;
				// Everytime a card is clicked, the card's front will be shown
				// The only time it doesn't show up is when two cards are already open
				tempCard.showFront();
				// If the card that's clicked is the first card
				if(firstCard)
				{
					// Set its selected state to true
					firstCardSelected = tempCard;
					// Save a reference to the object that just got clicked to the firstCardSelected field
					tempCard.setSelected(true);
					// Set the firstCard to false because the next card will be the second one
					firstCard = false;
					// Show a message to the user that say's "Pick another card"
					messageTextView.setText(getText(R.string.pick_another_card));
				}
				else
				{
					// If it is not the first card then, it is the second card
					
					// compare the file name of the two cards and save the boolean result to cardMatched
					// cardMatched is declared as final because it will be used on the showCardBackRunnable
					final boolean cardMatched = firstCardSelected.getFileName().equals(tempCard.getFileName());
					// Set the right message to be shown in the messageTextView
						// If the card that is currently selected is already selected before, show a "Pick a card, any card"
						// If not, show either a right or wrong message depending on whether the cards matched
					messageTextView.setText((tempCard.getSelected())?getString(R.string.pick_a_card):(cardMatched)?getText(R.string.right):getText(R.string.wrong));

					// Set the cardsOpen to true to prevent the user from clicking a third part
						// until both cards are closed
					cardsOpen = true;
					// Create an anonymous inner class of the Runnable class
					// This will be called by the handler.
					Runnable showCardBackRunnable = new Runnable() 
					{
						// run gets called when we call the postDelayed method
						@Override
						public void run() 
						{
							// If the tempCard is not the one that is recently selected and both cards matched
								// then add score and make both cards invisible,
							if(!tempCard.getSelected() && cardMatched)
							{
								// Add a score to the score
								addScore();
								// Set cards invisible
								firstCardSelected.setVisibility(View.INVISIBLE);
								tempCard.setVisibility(View.INVISIBLE);
							}
							// If the firstCard object is not the one that just clicked.
								// then make the firstCard unselected and show it's back
							if(firstCardSelected != tempCard)
							{
								firstCardSelected.showBack();
								firstCardSelected.setSelected(false);								
							} // End of firstCardSelected != tempCard If
							// Always show the back of the card and set it to not selected
								// After showing the value of it to the screen
							tempCard.showBack();
							tempCard.setSelected(false);
							// Set the cardsOpen to false to allow the user to select a new match
							cardsOpen = false;
							// Set the firstCard to true because the second card is just selected now so it
								// will be the second card soon.
							firstCard = true;
						} // End of run method
					}; // End of runnable inner class
					// Delay the call to the showCardBackRunnable by 300 milliseconds
					// The reason is to show the user what the second card is.
					handler.postDelayed(showCardBackRunnable, 300);
				} // End of firstCard If
			} // End of cardsOpen If		
		} // End of onClick method
	}; // End of cardClickedListener anonymous inner class

	/**
	 * Add 1 to the score property and then show the score to the user.
	 */
	private void addScore() {
		// increment the score by 1
		score++;
		// show the score
		showScore();
	} // End of addScore method

	/**
	 * Shows the score to the user
	 */
	private void showScore() {
		// Set the text in the scoreTextView as the parsed integer score field
		scoreTextView.setText(Integer.toString(score));
	} // End of showScore method

	/**
	 */
	private void updateTime() {
		// Run the timerUpdateRunnable
		handler.post(timerUpdateRunnable);
	}
	/**
	 * Update the time, reduce the secondsLeft, pause the game and show the
	 * dialog box with the score and a button to play again
	 */
	private Runnable timerUpdateRunnable = new Runnable() 
	{
		// Method run is run when timerUpdateRunnable is called by the handler 
		@Override
		public void run() {	
			// Make sure countdownThread only does these processes
			synchronized (countdownThread) 
			{
				// Decrement the seconds left
				--secondsLeft;
				// If the time runs out or the max score is taken
				if(secondsLeft == 0 || score == NUM_OF_DISTINCT_CARDS)
				{
					// Pause the countdownThread
					countdownThread.setWaiting(true);
					// Set the secondsLeft to 30 again
					secondsLeft = MAX_TIME;
					// Call the showAlertDialog method that shows an AlertDialog
						// Send the texts to be shown as arguments
					showAlertDialog(
							getString(R.string.play_again),
							getString(R.string.score_text) +" "+ Integer.toString(score),
							getString(R.string.play_again),alertDialogResetOnClickListener);
				} // End of secondsLeft == 0 || score == NUM_OF_DISTINCT_CARDS If
				// Show the remaining time
				showTimeRemaining();
			} // End of countdownThread synchronized
		} // End of run method
	}; // End of timerUpdateRunnable anonymous inner class

	/**
	 * Show the time remaining to the user
	 */
	private void showTimeRemaining() 
	{
		// Set the text in the timeLeftValueTextView as the parsed integer secondsLeft field		
		timeLeftValueTextView.setText(Integer.toString(secondsLeft));
	} // End of showTimeRemaining method

	/**
	 * Initialize the game properties.
	 * Gets called at the beginning of the game.
	 */
	private void initialize() {
		// Make sure countdownThread only does these processes
		synchronized (countdownThread) 
		{
			secondsLeft = MAX_TIME;				// set the seconds left to the maximum time at the start of each game
			showTimeRemaining();				// show the time remaining to the user
		} // End of countdownThread synchronized
		// Call the methods that creates and sets the cards
		setAllCardFilenames();
        setGameCardFilenames();
        shuffleCardFilenames();
        createCards();
        setImageButtons();

		resumecountdownThread();
	} // End of initialize method

	/**
	 * Resets the properties to be ready for the next game.
	 */
	private void reset() 
	{		
		// Call the methods that only sets the cards
		setAllCardFilenames();
        setGameCardFilenames();
        shuffleCardFilenames();
        setImageButtons();
        
        // set the state of the properties needed in the game
        	// the same as when they were initialized 
        firstCard = true;
        cardsOpen = false;
        firstCardSelected = null;
        // Set the score to 0 and show it to the user
		score = 0;
		showScore();
		messageTextView.setText(getText(R.string.pick_a_card));

		resumecountdownThread();
	} // End of reset method
	
	/**
	 * Resumes the countdownThread.
	 */
	private void resumecountdownThread() {
		// Make sure countdownThread only does these processes
		synchronized (countdownThread) 
		{
			// Set the waiting property of the countdownThread to false.
			countdownThread.setWaiting(false);
			// And tell the thread to stop the wait.
			countdownThread.notify();
		} // End of countdownThread synchronized
	} // End of resumecountdownThread method

	/**
	 * The AlertDialog that will be shown on the screen is created and shown.
	 * 
	 * @param title The title of the AlertDialog box
	 * @param message The message in the AlertDialog box
	 * @param buttonText The text inside the OK button
	 * @param buttonOkListener the listener for the OK button
	 */
	private void showAlertDialog(String title, String message, String buttonText, AlertDialog.OnClickListener buttonOkListener) 
	{
		// Get the AlertDialog Builder class and save it in a variable.
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
		// Set the title, message, OK button, and the cancel listener of the AlertDialog
		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		
		// Positive button is using the buttonOKListener which is passed as a parameter
			// as its event listener.
		alertDialogBuilder.setPositiveButton(buttonText, buttonOkListener);
		
		// set the cancel listener of the AlertDialog
		// Cancel is when the user pressed the back key in his/her phone.
		alertDialogBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() 
		{
			// The onCancel method is called when the back key is pressed.
			@Override
			public void onCancel(DialogInterface dialog) {
				// The current activity will be exited.
				finish();
			} // End of onCancel method
		}); // End of OnCancelListener
		// Create the AlertDialog and show it on the screen
		alertDialogBuilder.create().show();
	} // End of showAlertDialog

	/**
	 * Anonymous class used by the AlertDialog when the OK button is clicked
	 */
	private AlertDialog.OnClickListener alertDialogInitialOnClickListener = new AlertDialog.OnClickListener() 
	{		
		// When the button is clicked, call the initialize method and resume the thread.
		public void onClick(DialogInterface dialog, int which) 
		{
			initialize();
		} // End of onClick method
	}; // End of alertDialogInitialOnClickListener anonymous inner class

	/**
	 * Anonymous class used by the AlertDialog when the OK button is clicked
	 */
	private AlertDialog.OnClickListener alertDialogResetOnClickListener = new AlertDialog.OnClickListener() 
	{
		// When the button is clicked, call the reset method and resume the thread.
		public void onClick(DialogInterface dialog, int which) 
		{
			reset();
		} // End of onClick method
	}; // End of alertDialogResetOnClickListener anonymous inner class
	
	/**
	 * Private class that extends Thread which is the class
	 *  that runs processes that belongs to its own. This thread though
	 *  is used as a count down by using the Thread.sleep method of the Thread class.
	 * @author Aldrin Jerome Almacin
	 *
	 */
	private class CountdownThread extends Thread
	{
		private boolean waiting; // States whether the thread is on pause

		/**
		 * Constructor of the CountdownThread object
		 * waiting is set to false initially causing the Thread.sleep to run when
		 * the thread is started.
		 */
		public CountdownThread()
		{
			waiting = false;
		} // End of Constructor
		
		/**
		 * Getter of the waiting field
		 * @return the wait state of the Thread
		 */
		public boolean getWaiting() 
		{
			return waiting;
		} // End of the getWaiting getter

		/**
		 * Setter of the waiting field
		 * @param waiting the new state of the wait
		 */
		public void setWaiting(boolean waiting) 
		{
			this.waiting = waiting;
		} // End of the getWaiting setter
		
		/**
		 * This method will be run when the Thread is started.
		 */
		public void run()
		{
			// This loop will be run forever by this Thread.
			// It doesn't need to stop because it can only
				// pause and run again
			while(true)
			{
				// If the thread is not paused then
					// Update the time and sleep for a second then 
					// update again.
				// Repeat the same process until the thread gets paused.
				// In which the waiting will be set to true.
				if(!getWaiting())
				{
					try 
					{
						updateTime();
						Thread.sleep(1000);
					} 
					catch (InterruptedException e) 
					{
						Log.e(LOG_NAME, "An error occured while trying to make the thread sleep."+e.getMessage());
						e.printStackTrace();
					}
					catch (Exception e)
					{
						Log.e(LOG_NAME, "An error occured while trying to make the thread sleep."+e.getMessage());
						e.printStackTrace();
					} // End of try - catch
				}
				else
				{
					try 
					{
						synchronized (this) 
						{
							this.wait();
						} // End of synchronized
					} catch (InterruptedException e) 
					{
						e.printStackTrace();
					} // End of try - catch
				} // End of waiting if else
			} // End of while true loop
		} // End of run method
	} // End of CountdownThread private class
} // End of MemoryGame class



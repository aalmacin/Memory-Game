package com.raidrin.memorygame;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

/**
 * Card class that extends the ImageButton class.
 * Each Card object has its own front and back Drawable
 * and also has a glow and default Drawable.
 * It also saves its selected state.
 * @author Aldrin Jerome Almacin
 *
 */
public class Card extends ImageButton 
{
	public static final String CARD_BACK_FILENAME = "cardback.png";		// The filename off all CardObject's back
	private static final String GLOW_BACKGROUND_FILENAME = "glow.png";	// The filename off all CardObject's glow
	
	private String filename;		// The filename of the current front Drawable
	private Drawable backDrawable;	// The back image is saved in this property
	private Drawable frontDrawable; // The front image is saved in this property
	private Context context;		// The context in which the Card is created
	private boolean selected;		// States whether the Card is selected
	private Drawable glowDrawable;	// The glow image is saved in this property
	private Drawable normalDrawable;// The default background is saved in this property
	
	/**
	 * The constructor of the Card class
	 * @param context The context in which this Card is created
	 */
	public Card(Context context) 
	{
		// Call to the super constructor. 
		// This card is still an ImageButton but with some addition to it.
		super(context);
		// Save the param context as a property
		this.context = context;
		// Call the initialize method to initialize the Card's properties
		initialize();
	} // End of Constructor

	/**
	 * Getter of the selected property
	 * @return selected The state of the card's selected property.
	 */
	public boolean getSelected()
	{
		return selected;
	} // End of selected getter

	/**
	 * Setter of the selected property
	 * @param selectedState The new state of the card's selected property.
	 */
	public void setSelected(boolean selectedState)
	{
		this.selected = selectedState;
	} // End of selected setter

	/**
	 * Getter of the filename property
	 * @return the current filename's String value
	 */
	public String getFileName()
	{
		return filename;
	}

	/**
	 * Setter of the filename property
	 * The drawable for the Card object is also built.
	 * @param filename the new filename's String value
	 */
	public void setFileName(String filename)
	{
		this.filename = filename;
		// Create the Drawable for this Card object
		frontDrawable = createDrawable(filename);
	} // End of setFileName
	
	/**
	 * Initializes the properties of the Card object
	 */
	private void initialize() 
	{
		backDrawable = createDrawable(Card.CARD_BACK_FILENAME); //	Call the createDrawable method that creates a drawable
		frontDrawable = null;	// The frontDrawable is null because no filename is provided yet
		normalDrawable = this.getBackground();	// Get the current background when created and save it in a normalDrawable
		glowDrawable = createDrawable(GLOW_BACKGROUND_FILENAME);	// create the Drawable for the glow image
		selected = false;	// The card is not selected as default
	} // End of initialize
	
	/**
	 * The card is sent back to its initial state.
	 */
	public void initialState()
	{
		// Make the card not selected, show the Back, and make it visible
		selected = false;
		this.showBack();
		this.setVisibility(View.VISIBLE);
	} // End of initialState method

	/**
	 * Creates a Drawable object by providing a filename.
	 * @param cardFileName the filename of the image needed to be converted as a Drawable
	 * @return the Drawable that's created
	 */
	private Drawable createDrawable(String cardFileName) 
	{
    	// Get the context's assets from the assets folder and add it on the AssetManager
    	AssetManager assets = context.getAssets();
    	InputStream stream;	// Declare the stream on which the file will be saved to.
    	Drawable createdDrawable = null;
    	try
    	{
    		stream = assets.open(cardFileName);	// Open the file with the given file name and save it to stream
    		createdDrawable = Drawable.createFromStream(stream, cardFileName);	// Create the drawable from the input stream
    	}
		catch (IOException e) {
			Log.e("AndroidType","Error in opening the file input."+e.getMessage());
		}
		catch (Exception e) {
			Log.e("AndroidType","An error occured while opening the file."+e.getMessage());
		} // End of Try - Catch
		return createdDrawable; // Return the Drawable object
	} // End of createDrawable method
	
	/**
	 * Shows the back of the card and the default background
	 */
	public void showBack()
	{
		this.setImageDrawable(backDrawable);
		this.setBackgroundDrawable(normalDrawable);
	} // End of showBack
	
	/**
	 * Shows the front of the card and the glow background
	 */
	public void showFront()
	{
		// If frontDrawable already has an image then show the image
		if(frontDrawable != null)
		{
			this.setImageDrawable(frontDrawable);
			this.setBackgroundDrawable(glowDrawable);
		} // End if
	} // End of showFront method
}

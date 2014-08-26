package appzappy.nirail.ui.widgets;


import appzappy.nirail.R;
import appzappy.nirail.events.Event;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.ImageButton;

/**
 * Sample usage<br /> <br />
 * 
 * &lt;AppZappy.NIRailAndBus.UI.widgets.ToggleImageButton <br />
 * android:id="@+id/sample ID" <br />
 * android:layout_width="wrap_content" <br />
 * android:layout_height="wrap_content" <br />
 * android:onClick="onToggleClick" <br />
 * app:initialState="true" <br />
 * app:onImage="@drawable/onImage" <br />
 * app:offImage="@drawable/offImage" /&gt; <br /> <br />
 * 
 * Include this in a parent element<br /> 
 * xmlns:app="http://schemas.android.com/apk/res/AppZappy.NIRailAndBus"
 *
 */
public class ToggleImageButton extends ImageButton
{
	public static final int NULL_REFERENCE = 0;

	private int off = NULL_REFERENCE;
	private int on = NULL_REFERENCE;

	public ToggleImageButton(Context context)
	{
		this(context, null);
	}

	public ToggleImageButton(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		if (attrs != null) // default initialisations
		{
			TypedArray typed_array = context.obtainStyledAttributes(attrs, R.styleable.ToggleImageButton);

			int onResource = typed_array.getResourceId(R.styleable.ToggleImageButton_onImage, NULL_REFERENCE);
			int offResource = typed_array.getResourceId(R.styleable.ToggleImageButton_offImage, NULL_REFERENCE);
			boolean initialState = typed_array.getBoolean(R.styleable.ToggleImageButton_initialState, false);
			
			if (onResource != NULL_REFERENCE)
				setOnResource(onResource);
			if (offResource != NULL_REFERENCE)
				setOffResource(offResource);
			
			_setState(initialState);
			

			typed_array.recycle();			
		}
	}


	/**
	 * Set "On" image
	 * 
	 * @param onResource The image resource
	 */
	public void setOnResource(int onResource)
	{
		this.on = onResource;
	}

	/**
	 * Set the "Off" resource
	 * 
	 * @param offResource The image resource
	 */
	public void setOffResource(int offResource)
	{
		this.off = offResource;
	}

	private boolean _activeState = false;

	/**
	 * Get the state of this toggle image button
	 * 
	 * @return
	 */
	public boolean isActive()
	{
		return _activeState;
	}

	private void _setState(boolean state)
	{
		boolean oldState = _activeState;
		
		setStateNoEvent(state);
		
		if (oldState != state)
			onStateChanged.triggerEvent(this);
	}
	
	/**
	 * Set the state of this toggle image button
	 * 
	 * @param state
	 */
	public void setState(boolean state)
	{
		_setState(state);
	}
	
	/**
	 * Toggle the state of this button
	 * @return The new state
	 */
	public boolean toggleState()
	{
		setStateNoEvent(!_activeState);
		
		onStateChanged.triggerEvent(this);
		
		return this._activeState;
	}
	
	/**
	 * Set the state, but dont trigger the state changed event
	 * @param state
	 */
	public void setStateNoEvent(boolean state)
	{
		this._activeState = state;
		if (state)
		{
			this.setImageResource(on);
		}
		else
		{
			this.setImageResource(off);
		}
	}
	
	/**
	 * The state of this button has changed
	 */
	public Event onStateChanged = new Event();
}

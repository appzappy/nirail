package appzappy.nirail.mode;

import appzappy.nirail.data.enums.TransportType;
import appzappy.nirail.userdata.Settings;

public class ProgramMode
{
	private static IProgramMode train = new TrainMode(UIInterfaceFactory.getInterface());
	
	private static IProgramMode metro = new MetroMode(UIInterfaceFactory.getInterface());

	public static void toggleMode()
	{
		TransportType type = Settings.getMode();
		switch(type)
		{
			case Train:
				Settings.setMode(TransportType.Metro);
				break;
			case Metro:
				Settings.setMode(TransportType.Train);
				break;
			default:
				throw new UnsupportedOperationException("TransportType: " + type.toString());
				
		}
	}
	
	public static void setMode(TransportType type)
	{
		Settings.setMode(type);
	}

	public static IProgramMode singleton()
	{
		TransportType type = Settings.getMode();
		switch(type)
		{
			case Train:
				return train;
			case Metro:
				return metro;
			default:
				throw new UnsupportedOperationException("TransportType: " + type.toString());
		}
	}
}

package AppZappy.NIRailAndBus.notifications;

import java.util.ArrayList;
import java.util.List;

public class TrainReminderService {

	private List<TrainReminder> _reminders;
	
	public TrainReminderService()
	{
		setReminders(new ArrayList<TrainReminder>());
	}

	private void setReminders(List<TrainReminder> reminders) {
		this._reminders = reminders;
	}

	public List<TrainReminder> getReminders() {
		return _reminders;
	}
	
	public void addReminder(int time, int before, String station, String destination, int route, int start, int end)
	{
		getReminders().add(new TrainReminder());
	}
	
	@Override
	public String toString()
	{
		return "TrainReminderService: Reminder Count: " + _reminders.size();
	}
}

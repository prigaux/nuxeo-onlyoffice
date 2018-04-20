package fr.edu.lyon.nuxeo.onlyoffice.test;

import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventBundle;
import org.nuxeo.ecm.core.event.EventProducer;

import fr.edu.lyon.nuxeo.onlyoffice.service.OnlyofficeServiceImplTest;

public class MockEventProducer implements EventProducer
{

	@Override
	public void fireEvent(Event event)
	{
		OnlyofficeServiceImplTest.eventFired=event.getName();
	}

	@Override
	public void fireEventBundle(EventBundle event)
	{

	}

}

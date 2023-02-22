package mk.ukim.finki.wp.jan2021.events.service.impl;

import mk.ukim.finki.wp.jan2021.events.model.Event;
import mk.ukim.finki.wp.jan2021.events.model.EventLocation;
import mk.ukim.finki.wp.jan2021.events.model.EventType;
import mk.ukim.finki.wp.jan2021.events.model.exceptions.InvalidEventIdException;
import mk.ukim.finki.wp.jan2021.events.model.exceptions.InvalidEventLocationIdException;
import mk.ukim.finki.wp.jan2021.events.repository.EventLocationRepository;
import mk.ukim.finki.wp.jan2021.events.repository.EventRepository;
import mk.ukim.finki.wp.jan2021.events.service.EventService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final EventLocationRepository eventLocationRepository;

    public EventServiceImpl(EventRepository eventRepository, EventLocationRepository eventLocationRepository) {
        this.eventRepository = eventRepository;
        this.eventLocationRepository = eventLocationRepository;
    }

    @Override
    public List<Event> listAllEvents() {
        return this.eventRepository.findAll();
    }

    @Override
    public Event findById(Long id) {
        return this.eventRepository.findById(id)
                .orElseThrow(() -> new InvalidEventIdException());
    }

    @Override
    public Event create(String name, String description, Double price, EventType type, Long location) {
        EventLocation eventLocation = this.eventLocationRepository.findById(location)
                .orElseThrow(() -> new InvalidEventLocationIdException());
        Event event = new Event(name,description,price,type,eventLocation);
        return this.eventRepository.save(event);
    }

    @Override
    public Event update(Long id, String name, String description, Double price, EventType type, Long location) {
        Event event = this.eventRepository.findById(id)
                .orElseThrow(() -> new InvalidEventIdException());
        EventLocation eventLocation = this.eventLocationRepository.findById(location)
                .orElseThrow(() -> new InvalidEventLocationIdException());
        event.setName(name);
        event.setDescription(description);
        event.setPrice(price);
        event.setType(type);
        event.setLocation(eventLocation);
        return this.eventRepository.save(event);
    }

    @Override
    public Event delete(Long id) {
        Event event = this.eventRepository.findById(id).orElseThrow(() -> new InvalidEventIdException());
        this.eventRepository.delete(event);
        return event;
    }

    @Override
    public Event like(Long id) {
      Event event = this.eventRepository.findById(id)
              .orElseThrow(() -> new InvalidEventIdException());
      int likes = event.getLikes();
      event.setLikes(likes+1);
      this.eventRepository.save(event);
      return event;
    }

    @Override
    public List<Event> listEventsWithPriceLessThanAndType(Double price, EventType type) {
        if(price!=null && type!=null){

            return this.eventRepository.findAllByPriceLessThanAndType(price,type);
        }else if(price!=null){
            return this.eventRepository.findAllByPriceLessThan(price);
        } else if (type!=null) {
            return this.eventRepository.findAllByType(type);
        }
        return this.eventRepository.findAll();
    }
}

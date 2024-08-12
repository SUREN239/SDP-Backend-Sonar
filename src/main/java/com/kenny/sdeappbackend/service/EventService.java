package com.kenny.sdeappbackend.service;

import com.kenny.sdeappbackend.enums.Role;
import com.kenny.sdeappbackend.model.Event;
import com.kenny.sdeappbackend.model.User;
import com.kenny.sdeappbackend.repo.EventRepo;
import com.kenny.sdeappbackend.repo.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepo eventRepo;

    @Autowired
    private UserRepo userRepo;

    public List<Event> getAllEvents() {
        return eventRepo.findAll();
    }

    public Event createEvent(Event event) {
        LocalDateTime now = LocalDateTime.now();
        event.setStart(now);

        LocalDateTime end = event.getDeadline().withHour(17).withMinute(0).withSecond(0).withNano(0);
        event.setEnd(end);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        event.setCreatedBy(currentUsername);

        event.setPublic(true);


        List<Long> assigneeIds = event.getAssignees().stream()
                .map(User::getId)
                .toList(); // Extract user IDs

        List<User> assignees = userRepo.findByIdIn(assigneeIds);
        assignees = assignees.stream()
                .filter(user -> user.getRole() == Role.USER)
                .toList();


        for (User assignee : assignees) {
            assignee.setEvent(event);
        }
        event.setAssignees(assignees);

        return eventRepo.save(event);
    }


    public void deleteEvent(Long id) {
        // Check if event exists before attempting to delete
        if (eventRepo.existsById(id)) {
            eventRepo.deleteById(id);
        } else {
            throw new RuntimeException("Event not found with id: " + id);
        }
    }
}

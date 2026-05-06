package com.example.tourmanagement.service;

import com.example.tourmanagement.model.Tour;
import com.example.tourmanagement.model.TourAssignment;

import java.util.List;

public interface TourAssignmentService {
    List<TourAssignment> getAssignmentsByTour(Tour tour);
    List<TourAssignment> saveAssignments(List<TourAssignment> assignments);
    void cancelAssignment(TourAssignment assignment);
}

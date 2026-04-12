package com.example.tourmanagement.service;

import com.example.tourmanagement.dto.request.AssignmentRequestDTO;
import com.example.tourmanagement.dto.response.AssignmentDTO;

import java.util.List;

public interface TourAssignmentService {
    List<AssignmentDTO> getAssignmentsByTourId(Long tourId);
    List<AssignmentDTO> saveAssignments(AssignmentRequestDTO request);
    void cancelAssignment(Long assignmentId);
}

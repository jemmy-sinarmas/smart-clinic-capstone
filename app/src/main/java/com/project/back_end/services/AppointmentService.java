package com.project.back_end.services;

import com.example.model.Appointment;
import com.example.model.Doctor;
import com.example.repository.AppointmentRepository;
import com.example.repository.DoctorRepository;
import com.example.repository.PatientRepository;
import com.example.service.helper.TokenService;
import com.example.dto.AppointmentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final TokenService tokenService;

    @Autowired
    public AppointmentService(AppointmentRepository appointmentRepository,
                               DoctorRepository doctorRepository,
                               PatientRepository patientRepository,
                               TokenService tokenService) {
        this.appointmentRepository = appointmentRepository;
        this.doctorRepository = doctorRepository;
        this.patientRepository = patientRepository;
        this.tokenService = tokenService;
    }

    public int bookAppointment(Appointment appointment) {
        try {
            appointmentRepository.save(appointment);
            return 1;
        } catch (Exception e) {
            return 0;
        }
    }

    public ResponseEntity<Map<String, String>> updateAppointment(Appointment appointment) {
        Optional<Appointment> existing = appointmentRepository.findById(appointment.getId());
        Map<String, String> response = new HashMap<>();

        if (existing.isEmpty()) {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }

        appointmentRepository.save(appointment);
        response.put("message", "Appointment updated successfully");
        return ResponseEntity.ok(response);
    }

    public ResponseEntity<Map<String, String>> cancelAppointment(long id, String token) {
        Map<String, String> response = new HashMap<>();
        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isPresent()) {
            Appointment appointment = appointmentOpt.get();
            String emailFromToken = tokenService.extractEmail(token);
            if (!appointment.getPatient().getEmail().equals(emailFromToken)) {
                response.put("message", "Unauthorized");
                return ResponseEntity.status(401).body(response);
            }
            appointmentRepository.delete(appointment);
            response.put("message", "Appointment cancelled successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("message", "Appointment not found");
            return ResponseEntity.badRequest().body(response);
        }
    }

    public Map<String, Object> getAppointment(String pname, LocalDate date, String token) {
        Map<String, Object> result = new HashMap<>();
        String email = tokenService.extractEmail(token);
        Long doctorId = doctorRepository.findByEmail(email).getId();

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.plusDays(1).atStartOfDay();
        List<Appointment> appointments;

        if (pname == null || pname.isEmpty()) {
            appointments = appointmentRepository.findByDoctorIdAndAppointmentTimeBetween(doctorId, start, end);
        } else {
            appointments = appointmentRepository.findByDoctorIdAndPatient_NameContainingIgnoreCaseAndAppointmentTimeBetween(doctorId, pname, start, end);
        }

        List<AppointmentDTO> dtos = new ArrayList<>();
        for (Appointment a : appointments) {
            dtos.add(new AppointmentDTO(
                a.getId(), a.getDoctor().getId(), a.getDoctor().getName(),
                a.getPatient().getId(), a.getPatient().getName(),
                a.getPatient().getEmail(), a.getPatient().getPhone(),
                a.getPatient().getAddress(), a.getAppointmentTime(), a.getStatus()
            ));
        }

        result.put("appointments", dtos);
        return result;
    }
}

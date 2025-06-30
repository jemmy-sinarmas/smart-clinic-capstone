package com.project.back_end.controllers;


@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private Service service;

    @GetMapping("/{date}/{patientName}/{token}")
    public ResponseEntity<?> getAppointments(@PathVariable String date, @PathVariable String patientName, @PathVariable String token) {
        ResponseEntity<Map<String, String>> validation = service.validateToken(token, "doctor");
        if (validation.getStatusCode().isError()) return validation;
        return ResponseEntity.ok(appointmentService.getAppointment(patientName, LocalDate.parse(date), token));
    }

    @PostMapping("/{token}")
    public ResponseEntity<?> bookAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        if (service.validateToken(token, "patient").getStatusCode().isError())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));

        int isValid = service.validateAppointment(appointment);
        if (isValid != 1)
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Invalid appointment"));

        return appointmentService.bookAppointment(appointment) == 1 ?
            ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "Appointment booked")) :
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("message", "Booking failed"));
    }

    @PutMapping("/{token}")
    public ResponseEntity<?> updateAppointment(@PathVariable String token, @RequestBody Appointment appointment) {
        if (service.validateToken(token, "patient").getStatusCode().isError())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        return appointmentService.updateAppointment(appointment);
    }

    @DeleteMapping("/{id}/{token}")
    public ResponseEntity<?> cancelAppointment(@PathVariable long id, @PathVariable String token) {
        if (service.validateToken(token, "patient").getStatusCode().isError())
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", "Invalid token"));
        return appointmentService.cancelAppointment(id, token);
    }
}
